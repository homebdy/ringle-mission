# 링글 과제 - 수강 신청 API

학생과 튜터의 1:1 수업에 수강신청하는 API

## 기능

- 튜터: 30분 단위의 수업이 가능할 경우 수업 open
- 학생: 수업이 가능한 시간대 중 하나를 선택하고 신청

## 사용 기술

Java 17, Spring Boot 3.5.0, MySQL, Docker, Swagger, JPA

## 실행 방법

### 스크립트 실행 방식
1. 스크립트 실행 권한 설정
    ```
    chmod +x run.sh
    ```
2. 스크립트 실행
    ```
    ./run.sh
    ```
   
### 애플리케이션 실행 방식
1. 도커 실행
    ```
    docker-compose up
    ```
2. 애플리케이션 실행


애플리케이션이 실행된 후, Swagger([링크](http://localhost:8080/api/swagger-ui/index.html#/)) 에서 API 문서 확인 가능

---

## 설계

### 도메인 설계

(이미지)

**Member**

- 시스템에 등록된 사용자
- 주요 기능
    - 사용자 역할 (`TUTOR`, `STUDENT`) 관리


**LessonSlot: 튜터의 수업 가능 시간**

- 역할: 튜터가 등록한 수업 가능 시간을 관리
- 주요 기능
    - 생성 시 입력 날짜가 설정한 단위(현재 30분)에 맞지 않거나 과거일 경우 예외 발생
    - 사용자가 입력한 경우 reserved 값 변경
    - soft delete 적용

**ScheduledLesson: 실제 예약된 수업**

- 역할: 학생이 튜터의 LessonSlot을 예약한 내역 관리
- 주요 기능
    - 학생의 예약 내역 저장
    - 이후 예약 승인, 거절, 취소 등 추가 가능 예상

### 설계 고민 사항

**1. 시간 단위 변경 가능성**

- 상황
    - 튜터는 30분 단위로 수업 생성 → DB 저장도 30분 단위로 저장
    - 수업 단위는 30분과 60분으로 고정되어 있지만, 이후 변경 가능성이 있다 판단
- 수업의 시간 단위를 관리하는 `LessonInterval`을 도입, 기본 시간 단위(30분)는 `LessonConstant`로 분리
- 수업 조회 시, 비즈니스 로직에서 저장된 수업 중 연속된 시간대가 존재하는지 확인하도록 구현
- 결과: 수업 시간의 단위가 변경되더라도 비즈니스 로직의 수정은 최소화 가능

**2. 동시성 이슈**

- 학생이 동일 시간대에 수업을 예약할 경우 동시성 문제 발생 가능 예상
- 하지만, 낮은 빈도로 발생할 것이라 예상 → 낙관적 락 적용
- 동시성 문제 발생 시, 늦게 접근한 사용자에게 예외 반환

**3. 역할 기반 유효성 검증**

- 요구사항 확인 결과, 튜터와 학생의 역할이 기능적으로 유사하고, 구분되는 점은 권한에 국한된다 생각
- 별도의 학생/튜터 엔티티를 분리하지 않고 Member 엔티티로 통합 관리
- 역할에 따라 유효성 검증

---

## API 목록 및 설명 - base: /api

모든 API는 Swagger UI([링크](http://localhost:8080/api/swagger-ui/index.html#/))에서 상세 요청/응답 내용을 확인할 수 있습니다.

### 사용자 관리

**POST /members**

- 신규 사용자 추가
- Request Body:
    - 학생 생성

    ```json
    {
      "name": "student",
      "role": "STUDENT"
    }
    ```

    - 튜터 생성

    ```json
    {
      "name": "tutor",
      "role": "TUTER"
    }
    ```


### 수업 조회

**GET /slots?date={}&lessonInterval={}**

- 기간 및 수업 길이로 가능한 수업 시간대 조회
- ex) /api/slots?date=2025-05-27&lessonInterval=MINUTES_30
- Query Type:

    ```
    {
      "name": string,
      "role": MINUTES_30 | MINUTES_60
    }
    ```


**GET /slots/tutors?startAt={}&lessonInterval={}**

- 특정 날짜, 시간대, 수업 길이로 가능한 튜터 조회
- ex) /api/slots/tutors?startAt=2025-05-27T00:00:00&lessonInterval=MINUTES_60
- Query Type:

    ```
    {
      "startAt": LocalDateTime,
      "lessonInterval": MINUTES_30 | MINUTES_60
    }
    ```


### 튜터: 수업 시간 관리

**POST /slots?memberId={}**

- 수업 가능한 시간대 추가
- ex) /api/slots?memberId={}
- Request Body

    ```json
    [
      {
        "startAt": "2025-05-27T00:00:00"
      },
      {
        "startAt": "2025-05-27T00:30:00"
      }
    ]
    ```


**DELETE /slots/{lessonSlotId}?memberId={}**

- 수업 가능한 시간대 삭제
- ex) /api/slots/1?memberId={}

### 학생: 수업 신청 내역 관리

**POST /scheduled-lessons**

- 새로운 수업 신청
- Request Body

    ```json
    {
      "memberId": 2,
      "startAt": "2025-05-27T01:00:00",
      "lessonInterval": "MINUTES_60",
      "tutorId": 1
    }
    ```


**GET /scheduled-lessons/members/{memberId}**

- 학생이 신청한 수업 내역 조회
- ex) /api/scheduled-lessons/members/{memberId}

---

## 테스트

- 단위 테스트 및 동시성 테스트 작성
- 주요 테스트 시나리오:
    - 시간대 생성/삭제 시 정상 처리 및 동시성 검증
    - 수업 신청 시 시간대 및 권한 검증
    - 조회 API의 필터링

---

## 향후 개선 사항

- 인증/인가 기능 추가
    - 현재: memberId를 쿼리 파라미터로 받음 → 보안 문제
    - 인증/인가 기능을 추가하여 사용자의 정보가 노출되지 않도록 변경 필요