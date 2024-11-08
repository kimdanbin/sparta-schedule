package com.example.spartaschedule.dto;

import lombok.Getter;

@Getter
public class ScheduleRequestDto {
    private String title;
    private String contents;
    private String writer;
    private String pwd;
}
