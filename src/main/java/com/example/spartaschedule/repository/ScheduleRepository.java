package com.example.spartaschedule.repository;

import com.example.spartaschedule.dto.ScheduleResponseDto;
import com.example.spartaschedule.entity.Schedule;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository {

    ScheduleResponseDto saveSchedule(Schedule Schedule);

    List<ScheduleResponseDto> findAllSchedules();

    Schedule findScheduleByIdOrElseThrow(Long id);

    int updateSchedule(Long id, String title, String contents, String writer, String pwd);

    int deleteSchedule(Long id, String pwd);
}
