package com.nova.backend.alarm.service;

import com.nova.backend.alarm.dao.AlarmDAO;
import com.nova.backend.alarm.dto.AlarmResponseDTO;
import com.nova.backend.alarm.entity.PlantAlarmEntity;
import com.nova.backend.alarm.repository.PlantAlarmRepository;
import com.nova.backend.dashboard.entity.FarmEntity;
import com.nova.backend.dashboard.repository.FarmRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlarmServiceImpl implements AlarmService {
    private final AlarmDAO alarmDAO;
    private final FarmRepository farmRepository;
    private final ModelMapper modelMapper;

    // farm 조회 공통 메서드
    private FarmEntity getFarm(Long farmId) {
        return farmRepository.findById(farmId)
                .orElseThrow(() -> new IllegalArgumentException("팜을 찾을 수 없습니다."));
    }
    // 실시간 팝업 알림 (읽지 않은 알림)
    @Override
    public List<AlarmResponseDTO> getUnreadAlarms(Long farmId) {
        FarmEntity farm = getFarm(farmId);
        return alarmDAO
                .findUnreadByFarm(farm)
                .stream()
                .map(alarm -> modelMapper.map(alarm, AlarmResponseDTO.class))
                .collect(Collectors.toList());
    }
    // 최근 알람 10개
    @Override
    public List<AlarmResponseDTO> getRecentAlarms(Long farmId) {
        FarmEntity farm = getFarm(farmId);

        return alarmDAO
                .findRecentByFarm(farm)
                .stream()
                .map(alarm -> modelMapper.map(alarm, AlarmResponseDTO.class))
                .collect(Collectors.toList());
    }

    // 오늘 알람
    @Override
    public List<AlarmResponseDTO> getTodayAlarms(Long farmId) {
        FarmEntity farm = getFarm(farmId);

        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        return alarmDAO
                .findTodayByFarm(farm, start, end)
                .stream()
                .map(alarm -> modelMapper.map(alarm, AlarmResponseDTO.class))
                .collect(Collectors.toList());
    }
    // 전체 알람
    @Override
    public List<AlarmResponseDTO> getAllAlarms(Long farmId) {
        FarmEntity farm = getFarm(farmId);

        return alarmDAO
                .findAllByFarm(farm)
                .stream()
                .map(alarm -> modelMapper.map(alarm, AlarmResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void readAllAlarms(Long farmId) {
        FarmEntity farm = farmRepository.findById(farmId).orElse(null);
        if (farm == null) return;

        List<PlantAlarmEntity> unreadAlarms =
                alarmDAO.findUnreadByFarm(farm);

        for (PlantAlarmEntity alarm : unreadAlarms) {
            alarm.setRead(true);
        }
        // JPA dirty checking → 자동 UPDATE
    }

    @Override
    public void createSensorAlarm(FarmEntity farm, String title, String message) {
        PlantAlarmEntity alarm = PlantAlarmEntity.builder()
                .farm(farm)
                .alarmType("sensor")
                .title(title)
                .message(message)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        alarmDAO.save(alarm);
    }
}
