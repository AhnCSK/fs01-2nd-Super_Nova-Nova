package com.nova.backend.sensor.service;

import com.nova.backend.alarm.service.AlarmService;
import com.nova.backend.dashboard.entity.FarmEntity;
import com.nova.backend.dashboard.entity.PresetStepEntity;
import com.nova.backend.dashboard.repository.FarmRepository;
import com.nova.backend.sensor.dao.SensorLogDAO;
import com.nova.backend.sensor.dto.SensorCurrentDTO;
import com.nova.backend.sensor.dto.SensorHistoryDTO;
import com.nova.backend.sensor.dto.SensorPointDTO;
import com.nova.backend.sensor.entity.SensorLogEntity;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SensorServiceImpl implements SensorService {
    private final SensorLogDAO sensorLogDAO;
    private final FarmRepository farmRepository;
    private final ModelMapper modelMapper;
    private final AlarmService alarmService;

    private void checkThreshold(SensorLogEntity log) {
        FarmEntity farm = log.getFarm();
        if (farm == null) return;

        PresetStepEntity step = farm.getPresetStep();
        if (step == null) return;

        if (log.getSoilMoisture() < step.getSoilMoistureMin()) {
            alarmService.createSensorAlarm(
                    farm,
                    "토양 수분 부족",
                    "토양 수분이 기준보다 낮습니다."
            );
        }

        if (log.getTemp() > step.getTemperatureMax()) {
            alarmService.createSensorAlarm(
                    farm,
                    "온도 초과",
                    "온도가 기준보다 높습니다."
            );
        }
    }

    @Override
    @Transactional
    public void saveSensorLog(SensorLogEntity sensorLog) {
        // 센서 로그값 저장
        sensorLogDAO.save(sensorLog);
        // 프리셋 기준 판단
        checkThreshold(sensorLog);
    }

    @Override
    public SensorCurrentDTO getCurrentSensor(Long farmId) {
        FarmEntity farm = farmRepository
                .findById(farmId).orElse(null);

        SensorLogEntity log = sensorLogDAO
                .findLatestByFarm(farm);

        // Entity → DTO
        return modelMapper.map(log, SensorCurrentDTO.class);
    }

    @Override
    public SensorHistoryDTO getSensorHistory(Long farmId) {
        FarmEntity farm = farmRepository.findById(farmId).orElse(null);
        if (farm == null) return null;

        List<SensorLogEntity> logs =
                sensorLogDAO.findRecentLogsByFarm(farm);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        return SensorHistoryDTO.builder()
                .temperature(toPoints(logs, formatter, "temperature"))
                .humidity(toPoints(logs, formatter, "humidity"))
                .soilMoisture(toPoints(logs, formatter, "soilMoisture"))
                .light(toPoints(logs, formatter, "light"))
                .co2(toPoints(logs, formatter, "co2"))
                .build();
    }

    // 🔽 그래프용 공통 변환 메소드
    private List<SensorPointDTO> toPoints(
            List<SensorLogEntity> logs,
            DateTimeFormatter formatter,
            String type
    ) {
        return logs.stream()
                .map(log -> SensorPointDTO.builder()
                        .time(log.getRecordTime().format(formatter))
                        .value(getValue(log, type))
                        .build()
                )
                .collect(Collectors.toList());
    }

    private float getValue(SensorLogEntity log, String type) {
        return switch (type) {
            case "temperature" -> log.getTemp();
            case "humidity" -> log.getHumidity();
            case "soilMoisture" -> log.getSoilMoisture();
            case "light" -> log.getLightPower();
            case "co2" -> log.getCo2();
            default -> 0f;
        };
    }
}
