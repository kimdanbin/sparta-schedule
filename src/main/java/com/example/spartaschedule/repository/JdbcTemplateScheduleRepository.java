package com.example.spartaschedule.repository;

import com.example.spartaschedule.dto.ScheduleResponseDto;
import com.example.spartaschedule.entity.Schedule;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcTemplateScheduleRepository implements ScheduleRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateScheduleRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public ScheduleResponseDto saveSchedule(Schedule schedule) {
        // INSERT Query 를 직접 작성하지 않아도 된다.
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("schedule").usingGeneratedKeyColumns("id");

        // 서버 현재 시간
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestampNow = Timestamp.valueOf(now);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("title", schedule.getTitle());
        parameters.put("contents", schedule.getContents());
        parameters.put("writer", schedule.getWriter());
        parameters.put("pwd", schedule.getPwd());
        parameters.put("create_at", timestampNow);
        parameters.put("modified_at", timestampNow);

        // 저장 후 생성된 key값 Number 타입으로 변환하는 메서드
        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));

        return new ScheduleResponseDto(key.longValue(), schedule.getTitle(), schedule.getContents(), schedule.getWriter(), schedule.getPwd(), timestampNow, timestampNow);
    }

    // 전체 일정 조회
    @Override
    public List<ScheduleResponseDto> findAllSchedules() {
        return jdbcTemplate.query("select * from schedule", scheduleRowMapper());
    }

    // 선택 일정 조회
    @Override
    public Schedule findScheduleByIdOrElseThrow(Long id) {
        List<Schedule> result = jdbcTemplate.query("select * from schedule where id = ?", scheduleRowMapperV2(), id);
        return result.stream().findAny().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "does not exists id = " + id));
    }

    // 선택 일정 수정
    @Override
    public int updateSchedule(Long id, String title, String contents, String writer, String pwd) {

        // 서버 현재 시간
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestampNow = Timestamp.valueOf(now);

        // id값의 행 가져오기
        List<Schedule> schedule = jdbcTemplate.query("select * from schedule where id = ?", scheduleRowMapperV2(), id);

        // 만약 비어있다면 (없다면) 0리턴
        if (schedule.isEmpty()) {
            return 0;
        }

        String schedulePwd;
        try {
            schedulePwd = jdbcTemplate.queryForObject("select pwd from schedule where id = ?", String.class, id);

            // 비밀번호가 존재하는데 틀릴경우
            if (schedulePwd != null && !schedulePwd.equals(pwd)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "wrong password");
            }

        } catch (EmptyResultDataAccessException e) {

            return jdbcTemplate.update("update schedule set title = ?, contents = ?, writer = ?, modified_at = ? where id = ?", title, contents, writer, timestampNow, id);

        }

        return jdbcTemplate.update("update schedule set title = ?, contents = ?, writer = ?, modified_at = ? where id = ?", title, contents, writer, timestampNow, id);

    }

    // 선택 일정 삭제
    @Override
    public int deleteSchedule(Long id, String pwd) {

        try {
            String schedulePwd = jdbcTemplate.queryForObject("select pwd from schedule where id = ?", String.class, id);

            // 비밀번호가 존재하는데 틀릴경우
            if (schedulePwd != null && !schedulePwd.equals(pwd)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "wrong password");

            }

        } catch (EmptyResultDataAccessException e) {

            return jdbcTemplate.update("delete from schedule where id = ?", id);
        }

        return jdbcTemplate.update("delete from schedule where id = ?", id);
    }

    private RowMapper<ScheduleResponseDto> scheduleRowMapper() {

        return new RowMapper<ScheduleResponseDto>() {
            @Override
            public ScheduleResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new ScheduleResponseDto(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("contents"),
                        rs.getString("writer"),
                        rs.getString("pwd"),
                        rs.getTimestamp("create_at"),
                        rs.getTimestamp("modified_at")
                );
            }
        };
    }

    private RowMapper<Schedule> scheduleRowMapperV2() {
        return new RowMapper<Schedule>() {
            @Override
            public Schedule mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Schedule(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("contents"),
                        rs.getString("writer"),
                        rs.getString("pwd"),
                        rs.getTimestamp("create_at"),
                        rs.getTimestamp("modified_at")
                );
            }
        };
    }
}
