# 화면 레이아웃 API 수정 사항

## 문제 설명

화면 레이아웃 API에서 다음과 같은 문제가 발생했습니다:

1. `POST /api/screen-layouts` 엔드포인트를 통해 카드와 중앙 메뉴를 포함한 화면 레이아웃을 생성할 수 있습니다.
2. 그러나 `GET /api/screen-layouts` 엔드포인트를 통해 모든 화면 레이아웃을 조회할 때, 응답에 카드와 중앙 메뉴 정보가 포함되지 않았습니다 (null로 반환).

```json
[
    {
        "layoutId": 2,
        "name": "기본 레이아웃",
        "createdDate": "2025-07-20T22:08:41",
        "modifiedDate": "2025-07-20T22:08:41",
        "cards": null,
        "centralMenu": null
    },
    {
        "layoutId": 1,
        "name": "기본 레이아웃",
        "createdDate": "2025-07-20T22:02:10",
        "modifiedDate": "2025-07-20T22:08:16",
        "cards": null,
        "centralMenu": null
    }
]
```

## 원인 분석

문제의 원인은 `ScreenLayoutServiceImpl` 클래스의 `getAllScreenLayouts()` 메서드에 있었습니다. 이 메서드는 단순히 `screenLayoutRepository.findAll()`의 결과를 반환하고 있었으며, 각 레이아웃에 대한 카드와 중앙 메뉴 정보를 로드하지 않았습니다.

```java
@Override
@Transactional(readOnly = true)
public List<ScreenLayout> getAllScreenLayouts() {
    log.debug("Getting all screen layouts");
    return screenLayoutRepository.findAll();
}
```

반면, `getScreenLayoutById(Long layoutId)` 메서드는 카드와 중앙 메뉴 정보를 올바르게 로드하고 있었습니다:

```java
@Override
@Transactional(readOnly = true)
public ScreenLayout getScreenLayoutById(Long layoutId) {
    log.debug("Getting screen layout by ID: {}", layoutId);
    
    // Get the screen layout
    ScreenLayout screenLayout = screenLayoutRepository.findById(layoutId)
            .orElseThrow(() -> new RuntimeException("Screen layout not found with ID: " + layoutId));
    
    // Load cards
    List<Card> cards = cardRepository.findByLayoutId(layoutId);
    screenLayout.setCards(cards);
    
    // Load central menu
    centralMenuRepository.findByLayoutId(layoutId).ifPresent(screenLayout::setCentralMenu);
    
    return screenLayout;
}
```

또한, `ScreenLayoutController`의 API 문서에는 "모든 화면 레이아웃 조회 (카드와 중앙 메뉴 제외)"라고 명시되어 있어, 이 동작이 의도적인 것처럼 보였습니다.

## 수정 사항

### 1. ScreenLayoutServiceImpl 수정

`getAllScreenLayouts()` 메서드를 수정하여 각 레이아웃에 대한 카드와 중앙 메뉴 정보를 로드하도록 했습니다:

```java
@Override
@Transactional(readOnly = true)
public List<ScreenLayout> getAllScreenLayouts() {
    log.debug("Getting all screen layouts");
    List<ScreenLayout> screenLayouts = screenLayoutRepository.findAll();
    
    // Load cards and central menu for each layout
    screenLayouts.forEach(layout -> {
        // Load cards
        List<Card> cards = cardRepository.findByLayoutId(layout.getLayoutId());
        layout.setCards(cards);
        
        // Load central menu
        centralMenuRepository.findByLayoutId(layout.getLayoutId()).ifPresent(layout::setCentralMenu);
    });
    
    return screenLayouts;
}
```

### 2. ScreenLayoutController 문서 수정

API 문서를 수정하여 모든 화면 레이아웃 조회 시 카드와 중앙 메뉴가 포함됨을 명시했습니다:

```java
@Operation(summary = "모든 화면 레이아웃 조회", description = "카드와 중앙 메뉴를 포함한 모든 화면 레이아웃을 조회합니다")
```

## 기대 효과

이 수정 사항을 적용한 후에는 `GET /api/screen-layouts` 엔드포인트를 통해 모든 화면 레이아웃을 조회할 때, 각 레이아웃에 대한 카드와 중앙 메뉴 정보가 응답에 포함될 것입니다. 예상되는 응답 형식은 다음과 같습니다:

```json
[
    {
        "layoutId": 2,
        "name": "기본 레이아웃",
        "createdDate": "2025-07-20T22:08:41",
        "modifiedDate": "2025-07-20T22:08:41",
        "cards": [
            {
                "cardId": 5,
                "layoutId": 2,
                "position": "LEFT_1",
                "title": "왼쪽 상단 카드",
                "horizontalCollapse": false,
                "verticalCollapse": false,
                "titleOnly": false,
                "expanded": false,
                "createdDate": "2025-07-20T22:08:41",
                "modifiedDate": "2025-07-20T22:08:41"
            },
            // ... 다른 카드들
        ],
        "centralMenu": {
            "menuId": 2,
            "layoutId": 2,
            "priority": false,
            "expanded": false,
            "createdDate": "2025-07-20T22:08:41",
            "modifiedDate": "2025-07-20T22:08:41"
        }
    },
    // ... 다른 레이아웃들
]
```

이로써 화면 레이아웃 API가 일관되게 동작하며, 사용자는 모든 화면 레이아웃을 조회할 때도 카드와 중앙 메뉴 정보를 함께 받을 수 있게 됩니다.
