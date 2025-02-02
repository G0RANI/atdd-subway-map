# 🚀 3단계 - 지하철 구간 관리

# 미션 소개

- 시나리오 형태로 작성된 인수 조건이 아닌 줄글 형태의 요구사항에서 인수 조건을 도출하여 기능을 구현하는 미션입니다.
- 아래의 `요구사항 설명`에서 제공되는 요구사항을 기반으로 **지하철 구간 관리 기능**을 구현하세요.

# 요구사항

## 기능 요구사항

- 요구사항을 정의한 **인수 조건**을 도출하세요.
- 인수 조건을 검증하는 **인수 테스트**를 작성하세요.
- 예외 케이스에 대한 검증도 포함하세요.

## 프로그래밍 요구사항

- 인수 테스트 주도 개발 프로세스

  에 맞춰서 기능을 구현하세요.

  - `요구사항 설명`을 참고하여 인수 조건을 정의
  - 인수 조건을 검증하는 인수 테스트 작성
  - 인수 테스트를 충족하는 기능 구현

- 인수 조건은 인수 테스트 메서드 상단에 주석으로 작성하세요.

  - 뼈대 코드의 인수 테스트를 참고

- 인수 테스트의 결과가 다른 인수 테스트에 영향을 끼치지 않도록 인수 테스트를 서로 격리 시키세요.

- 인수 테스트의 재사용성과 가독성, 그리고 빠른 테스트 의도 파악을 위해 인수 테스트를 리팩터링 하세요.

# 요구사항 설명

## 구간 등록 기능

- 지하철 노선에 구간을 등록하는 기능을 구현
- 새로운 구간의 상행역은 해당 노선에 등록되어있는 하행 종점역이어야 한다.
- 이미 해당 노선에 등록되어있는 역은 새로운 구간의 하행역이 될 수 없다.
- 새로운 구간 등록시 위 조건에 부합하지 않는 경우 에러 처리한다.

### 구간 등록 request

```http
POST /lines/1/sections HTTP/1.1
accept: */*
content-type: application/json; charset=UTF-8
host: localhost:52165

{
    "downStationId": "4",
    "upStationId": "2",
    "distance": 10
}
```

![img](https://nextstep-storage.s3.ap-northeast-2.amazonaws.com/832a8b49635c40b58f16fae1726909f6)

## 구간 제거 기능

- 지하철 노선에 구간을 제거하는 기능 구현
- 지하철 노선에 등록된 역(하행 종점역)만 제거할 수 있다. 즉, 마지막 구간만 제거할 수 있다.
- 지하철 노선에 상행 종점역과 하행 종점역만 있는 경우(구간이 1개인 경우) 역을 삭제할 수 없다.
- 새로운 구간 제거시 위 조건에 부합하지 않는 경우 에러 처리한다.

### 지하철 구간 삭제 request

```http
DELETE /lines/1/sections?stationId=2 HTTP/1.1
accept: */*
host: localhost:52165
```

## 구간 관리 기능의 예외 케이스를 고려하기

- 구간 등록과 제거 기능의 예외케이스들에 대한 시나리오를 정의
- 인수 테스트를 작성하고 이를 만족시키는 기능을 구현

## TODO

- [x] 구간 등록
  - [x] GIVEN  지하철 노선을 생성하고</br>
    WHEN  지하철 노선에 구간을 추가하면</br>
    THEN   수정된 구간을 조회 할 수 있다
  - [x] GIVEN  지하철 노선을 생성하고</br>
    WHEN  새로운 구간의 상행역이 기존의 하행역과 일치 하지 않는다면</br>
    THEN  에러 처리와 함께 '마지막 구간과 추가될 구간의 시작은 같아야 합니다.' 라는 메세지가 출력된다.
  - [x] GIVEN  지하철 노선을 생성하고</br>
    WHEN  새로운 구간이 이미 해당 노선에 등록되어있는 역이면</br>
    THEN  에러처리와 함께 '이미 구간에 포함 되어 있는 역 입니다.' 라는 메세지가 출력된다.
- [x] 구간 제거
  - [x] GIVEN  지하철 노선을 생성하고 노선을 수정 후</br>
    WHEN  지하철 마지막 구간을 제거하면</br>
    THEN  마지막 구간이 제거된다
  - [x] GIVEN  지하철 노선을 생성하고 노선을 수정 후</br>
    WHEN  마지막 구간이 아닌 지하철 구간을 제거하면</br>
    THEN  에러 처리와 함께 '마지막 구간의 역이 아닙니다.' 라는 메세지가 출력된다.
  - [x] GIVEN  지하철 노선을 시작과 끝만 생성하고</br>
    WHEN  지하철 마지막 구간을 제거하면</br>
    THEN  에러 처리와 함께 '구간이 하나 일 때는 삭제를 할 수 없습니다' 라는 메세지가 출력된다.
