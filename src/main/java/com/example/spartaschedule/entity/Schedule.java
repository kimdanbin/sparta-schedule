package com.example.spartaschedule.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
public class Schedule {
    private Long id;
    private String title;
    private String contents;
    private String writer;
    private String pwd;
    private Timestamp createdAt;
    private Timestamp modifiedAt;

    public Schedule(String title, String contents, String writer, String pwd) {
        this.title = title;
        this.contents = contents;
        this.writer = writer;
        this.pwd = pwd;
    }

    public void update(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public void updateTitle(String title) {
        this.title = title;
    }
}
