use schedule;

# schedule 테이블
CREATE TABLE schedule
(
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY COMMENT '일정 식별자',
    title       VARCHAR(100) NOT NULL COMMENT '제목',
    contents    TEXT COMMENT '내용',
    writer      VARCHAR(100) NOT NULL COMMENT '작성자명',
    pwd         VARCHAR(100)  COMMENT '비밀번호',
    create_at   DATETIME     NOT NULL COMMENT '작성일',
    modified_at DATETIME     NOT NULL COMMENT '수정일'
);

# 일정 생성을 하는 query를 작성
INSERT INTO schedule (title, contents, writer, pwd, create_at, modified_at) VALUES
    ('제목', '내용', '작성자', '0000', now(), now());

# 전체 일정을 조회하는 query를 작성
SELECT *
FROM schedule;

# 선택 일정을 조회하는 query를 작성
SELECT *
FROM schedule
WHERE id = 1;

# 선택한 일정을 수정하는 query를 작성
UPDATE schedule
SET title = '장보기'
WHERE id = 1;

# 선택한 일정을 삭제하는 query를 작성
DELETE FROM schedule WHERE id = 1;

