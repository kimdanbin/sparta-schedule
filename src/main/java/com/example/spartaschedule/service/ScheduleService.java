package com.example.spartaschedule.service;

import com.example.spartaschedule.dto.ScheduleRequestDto;
import com.example.spartaschedule.dto.ScheduleResponseDto;

import java.util.List;

public interface ScheduleService {
    ScheduleResponseDto saveSchedule(ScheduleRequestDto dto);

    List<ScheduleResponseDto> findAllSchedules(String writer, boolean modifiedAtFilter);

    ScheduleResponseDto findScheduleById(long id);

    ScheduleResponseDto updateSchedule(Long id, String title, String contents, String writer, String pwd);

    void deleteSchedule(long id, String pwd);
}
