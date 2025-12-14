package com.nova.backend.user.service;

import com.nova.backend.farm.dao.FarmDAO;
import com.nova.backend.farm.dto.FarmResponseDTO;
import com.nova.backend.nova.dao.NovaDAO;
import com.nova.backend.nova.dto.NovaRequestDTO;
import com.nova.backend.nova.dto.NovaResponseDTO;
import com.nova.backend.nova.entity.NovaEntity;
import com.nova.backend.timelapse.dao.TimelapseDAO;
import com.nova.backend.timelapse.dto.TimelapseResponseDTO;
import com.nova.backend.timelapse.dto.TimelapseVideoResponseDTO;
import com.nova.backend.timelapse.entity.TimelapseEntity;
import com.nova.backend.timelapse.entity.TimelapseVideoEntity;
import com.nova.backend.user.dao.UsersDAO;
import com.nova.backend.user.dto.*;
import com.nova.backend.user.entity.UsersEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService {
    private final UsersDAO usersDAO;
    private final NovaDAO novaDAO;
    private final FarmDAO farmDAO;
    private final TimelapseDAO timelapseDAO;
    private final ModelMapper modelMapper;

    @Override
    public MyPageResponseDTO findByUserId(Long userId) {

        // 1) 유저 조회
        UsersEntity user = usersDAO.findByUserId(userId);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다. userId=" + userId);
        }

        // 2) user → DTO (여긴 문제 없음)
        UsersResponseDTO usersResponseDTO =
                modelMapper.map(user, UsersResponseDTO.class);

        // 3) nova → DTO (충돌 나는 부분만 수동 처리)
        List<NovaResponseDTO> novaResponseDTOList =
                novaDAO.getNovaEntity(user).stream()
                        .map(nova -> {
                            NovaResponseDTO dto = new NovaResponseDTO();

                            dto.setNovaId(nova.getNovaId());
                            dto.setNovaSerialNumber(nova.getNovaSerialNumber());
                            dto.setStatus(nova.getStatus());

                            // 🔥 핵심: 어떤 userId를 쓸지 명확히 지정
                            dto.setUserId(nova.getUser().getUserId());
                            // 또는
                            // dto.setUserId(String.valueOf(nova.getUser().getUserId()));

                            return dto;
                        })
                        .collect(Collectors.toList());

        return new MyPageResponseDTO(usersResponseDTO, novaResponseDTOList);
    }

    @Override
    public void updateMyPage(MyPageRequestDTO myPageRequestDTO) {
        UsersRequestDTO usersRequestDTO = myPageRequestDTO.getUsersRequestDTO();
        UsersEntity usersEntity = usersDAO.findByUserId(usersRequestDTO.getUserId());
        if (usersEntity == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        modelMapper.map(usersRequestDTO, usersEntity);
        usersDAO.update(usersEntity);

        List<NovaRequestDTO> novaRequestDTOList = myPageRequestDTO.getNovaRequestDTOList();
        System.out.println("novaRequestDTOList 생성");
        List<NovaEntity> novaEntityUpdateList = novaRequestDTOList.stream()
                .filter(nova -> nova.getStatus().equals("update"))
                .peek(nova -> nova.setStatus("default"))
                .map(nova -> modelMapper.map(nova, NovaEntity.class))
                .collect(Collectors.toList());
        System.out.println("novaRequestDTOList: " + novaRequestDTOList);
        List<NovaEntity> novaEntityDeleteList = novaRequestDTOList.stream()
                .filter(nova -> nova.getStatus().equals("delete"))
                .map(nova -> modelMapper.map(nova, NovaEntity.class))
                .collect(Collectors.toList());
        System.out.println("novaRequestDTOList: " + novaRequestDTOList);
        List<NovaEntity> novaEntityCreateList = novaRequestDTOList.stream()
                .filter(nova -> nova.getStatus().equals("create"))
                .map(nova -> {
//                    NovaEntity entity = new NovaEntity(
//                            nova.getUserId(),
//                            nova.getNovaSerialNumber(),
//                            "default"
//                    );
//                    return entity;
                    //노바에 user fk설정으로 변경햇습니당
                    NovaEntity entity = new NovaEntity();
                    entity.setUser(usersEntity);   // (userId ❌, UsersEntity ⭕)
                    entity.setNovaSerialNumber(nova.getNovaSerialNumber());
                    entity.setStatus("default");
                    return entity;
                })
                .collect(Collectors.toList());
        System.out.println("novaEntityCreateList: " + novaEntityCreateList);
        novaDAO.update(novaEntityUpdateList);
        novaDAO.delete(novaEntityDeleteList);
        novaDAO.create(novaEntityCreateList);
    }

    @Override
    public List<FarmResponseDTO> getTimeLapseDTOList(int userId) {






//        List<Farm> farmEntityList = farmDAO.findListByNovaId(userId);

//        Map<Integer, List<NovaResponseDTO>> farmsByNova =
//                farmDAO.findListByNovaId(userId).stream()
//                        .map(farm -> modelMapper.map(farm, NovaResponseDTO.class))
//                        .collect(Collectors.groupingBy(NovaResponseDTO::getNovaId));





//        return farmEntityList.stream().map(entity -> modelMapper.map(entity, FarmResponseDTO.class)).collect(Collectors.toList());
        return null;
    }

    public List<TimelapseResponseDTO> getByFarm(int farmId) {

        List<TimelapseEntity> timelapseList =
                timelapseDAO.findWithVideosByFarmId(farmId);

        return timelapseList.stream()
                .map(timelapse -> {
                    TimelapseResponseDTO dto =
                            modelMapper.map(timelapse, TimelapseResponseDTO.class);

                    List<TimelapseVideoResponseDTO> videos =
                            timelapse.getVideoList().stream()
                                    .map(video -> modelMapper.map(video, TimelapseVideoResponseDTO.class))
                                    .toList();

                    dto.setVideoList(videos); // DTO에 List 추가
                    return dto;
                })
                .toList();
    }
}
