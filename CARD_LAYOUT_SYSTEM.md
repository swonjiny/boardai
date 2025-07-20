# 카드 레이아웃 시스템 (Card Layout System)

이 문서는 카드 기반 UI 레이아웃 시스템의 구현에 대한 요약을 제공합니다.

## 개요

카드 레이아웃 시스템은 화면에 카드 형식의 컴포넌트를 배치하고 관리하는 기능을 제공합니다. 이 시스템은 다음과 같은 특징을 가집니다:

1. 좌측과 우측에 각각 2개씩, 총 4개의 카드를 배치할 수 있습니다.
2. 각 카드는 제목, 접기/펼치기 상태(가로/세로), 제목만 표시 상태, 확장 상태 등의 속성을 가집니다.
3. 화면 중앙 하단에 위치하는 중앙 메뉴가 있으며, 이 메뉴는 화면 표현에 최우선순위를 가질 수 있습니다.
4. 중앙 메뉴가 확장되면 좌우 카드는 접힘 상태로 변경되고, 중앙 메뉴는 전체 화면을 덮을 수 있습니다.

## 데이터 모델

### 1. 화면 레이아웃 (ScreenLayout)

화면 레이아웃은 전체 레이아웃 구성을 나타냅니다.

```java
public class ScreenLayout {
    private Long layoutId;
    private String name;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    
    // 데이터베이스에 저장되지 않는 필드
    private List<Card> cards;
    private CentralMenu centralMenu;
}
```

### 2. 카드 (Card)

카드는 화면에 표시되는 개별 카드 컴포넌트를 나타냅니다.

```java
public class Card {
    private Long cardId;
    private Long layoutId;
    private String position; // 'LEFT_1', 'LEFT_2', 'RIGHT_1', 'RIGHT_2'
    private String title;
    private Boolean horizontalCollapse;
    private Boolean verticalCollapse;
    private Boolean titleOnly;
    private Boolean expanded;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
```

### 3. 중앙 메뉴 (CentralMenu)

중앙 메뉴는 화면 중앙 하단에 위치하는 메뉴 컴포넌트를 나타냅니다.

```java
public class CentralMenu {
    private Long menuId;
    private Long layoutId;
    private Boolean priority;
    private Boolean expanded;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
```

## 데이터베이스 스키마

### MariaDB 스키마

```sql
-- 화면 레이아웃 테이블
CREATE TABLE screen_layout (
    layout_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 카드 테이블
CREATE TABLE card (
    card_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    layout_id BIGINT NOT NULL,
    position VARCHAR(10) NOT NULL, -- 'LEFT_1', 'LEFT_2', 'RIGHT_1', 'RIGHT_2'
    title VARCHAR(255) NOT NULL,
    horizontal_collapse BOOLEAN DEFAULT FALSE,
    vertical_collapse BOOLEAN DEFAULT FALSE,
    title_only BOOLEAN DEFAULT FALSE,
    expanded BOOLEAN DEFAULT FALSE,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (layout_id) REFERENCES screen_layout(layout_id) ON DELETE CASCADE
);

-- 중앙 메뉴 테이블
CREATE TABLE central_menu (
    menu_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    layout_id BIGINT NOT NULL,
    priority BOOLEAN DEFAULT FALSE,
    expanded BOOLEAN DEFAULT FALSE,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (layout_id) REFERENCES screen_layout(layout_id) ON DELETE CASCADE
);
```

### Oracle 스키마

Oracle 데이터베이스에서는 BOOLEAN 타입 대신 NUMBER(1)을 사용하고, 시퀀스와 트리거를 사용하여 자동 증가 기능을 구현합니다.

## API 엔드포인트

### 화면 레이아웃 API

- `POST /api/screen-layouts` - 새 화면 레이아웃 생성
- `GET /api/screen-layouts/{layoutId}` - ID로 화면 레이아웃 조회
- `GET /api/screen-layouts` - 모든 화면 레이아웃 조회
- `PUT /api/screen-layouts/{layoutId}` - 화면 레이아웃 수정
- `DELETE /api/screen-layouts/{layoutId}` - 화면 레이아웃 삭제

## 구현 계층

### 1. 모델 계층

- `ScreenLayout`: 화면 레이아웃 모델
- `Card`: 카드 모델
- `CentralMenu`: 중앙 메뉴 모델

### 2. 리포지토리 계층

- `ScreenLayoutRepository`: 화면 레이아웃 데이터 액세스
- `CardRepository`: 카드 데이터 액세스
- `CentralMenuRepository`: 중앙 메뉴 데이터 액세스

### 3. 서비스 계층

- `ScreenLayoutService`: 화면 레이아웃, 카드, 중앙 메뉴 관리 비즈니스 로직

### 4. 컨트롤러 계층

- `ScreenLayoutController`: 화면 레이아웃 API 엔드포인트

## 테스트 방법

이 시스템을 테스트하기 위해서는 다음과 같은 방법을 사용할 수 있습니다:

### 1. 단위 테스트

- 각 모델 클래스의 getter/setter 테스트
- 각 리포지토리 메서드 테스트
- 서비스 계층 메서드 테스트

### 2. 통합 테스트

- 컨트롤러 API 엔드포인트 테스트
- 데이터베이스 연동 테스트

### 3. API 테스트

Postman이나 curl을 사용하여 API 엔드포인트를 테스트할 수 있습니다:

#### 화면 레이아웃 생성 예제

```bash
curl -X POST http://localhost:8080/api/screen-layouts \
  -H "Content-Type: application/json" \
  -d '{
    "name": "기본 레이아웃",
    "cards": [
      {
        "position": "LEFT_1",
        "title": "왼쪽 상단 카드",
        "horizontalCollapse": false,
        "verticalCollapse": false,
        "titleOnly": false,
        "expanded": false
      },
      {
        "position": "LEFT_2",
        "title": "왼쪽 하단 카드",
        "horizontalCollapse": false,
        "verticalCollapse": false,
        "titleOnly": false,
        "expanded": false
      },
      {
        "position": "RIGHT_1",
        "title": "오른쪽 상단 카드",
        "horizontalCollapse": false,
        "verticalCollapse": false,
        "titleOnly": false,
        "expanded": false
      },
      {
        "position": "RIGHT_2",
        "title": "오른쪽 하단 카드",
        "horizontalCollapse": false,
        "verticalCollapse": false,
        "titleOnly": false,
        "expanded": false
      }
    ],
    "centralMenu": {
      "priority": false,
      "expanded": false
    }
  }'
```

## 결론

이 카드 레이아웃 시스템은 화면에 카드 형식의 컴포넌트를 배치하고 관리하는 기능을 제공합니다. 시스템은 MariaDB와 Oracle 데이터베이스를 모두 지원하며, RESTful API를 통해 화면 레이아웃을 관리할 수 있습니다. 이 시스템을 사용하여 다양한 화면 레이아웃을 구성하고 관리할 수 있습니다.
