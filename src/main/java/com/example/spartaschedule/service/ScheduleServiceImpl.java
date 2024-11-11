package com.example.spartaschedule.service;

import com.example.spartaschedule.dto.ScheduleRequestDto;
import com.example.spartaschedule.dto.ScheduleResponseDto;
import com.example.spartaschedule.entity.Schedule;
import com.example.spartaschedule.repository.ScheduleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    // 일정 생성
    @Override
    public ScheduleResponseDto saveSchedule(ScheduleRequestDto dto) {

        // 요청받은 데이터로 Schedule 객체 생성 ID 없음
        Schedule schedule = new Schedule(dto.getTitle(), dto.getContents(), dto.getWriter(), dto.getPwd());

        // DB 저장
        return scheduleRepository.saveSchedule(schedule);
    }

    // 전체 일정 조회
    @Override
    public List<ScheduleResponseDto> findAllSchedules(String writer, boolean modifiedAtFilter) {

        List<ScheduleResponseDto> allSchedules = scheduleRepository.findAllSchedules(writer, modifiedAtFilter);

//        // 수정일 기준 내림차순으로 정렬
//        allSchedules.sort(Comparator.comparing(ScheduleResponseDto::getModifiedAt).reversed());

        return allSchedules;
    }

    // 선택 일정 조회
    @Override
    public ScheduleResponseDto findScheduleById(long id) {

        Schedule schedule = scheduleRepository.findScheduleByIdOrElseThrow(id);

        return new ScheduleResponseDto(schedule);
    }

    // 선택 일정 수정
    @Transactional
    @Override
    public ScheduleResponseDto updateSchedule(Long id, String title, String contents, String writer, String pwd) {

        if (title == null || contents == null || writer == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title or contents or writer is null");
        }

        // 업데이트된 열 수
        int updateRow = scheduleRepository.updateSchedule(id, title, contents, writer, pwd);

        // 업데이트된 열 수가 0이면 해당 id의 schedule(행)이 존재하지 않는다
        if (updateRow == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no exist = " + id);
        }

        Schedule schedule = scheduleRepository.findScheduleByIdOrElseThrow(id);

        return new ScheduleResponseDto(schedule);
    }


    @Override
    public void deleteSchedule(long id, String pwd) {

        int deletedRow = scheduleRepository.deleteSchedule(id, pwd);

        if (deletedRow == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no exist = " + id);
        }
    }
}
