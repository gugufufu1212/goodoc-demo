# goodoc-demo

굿닥(Goodoc) 스타일 병원 예약 서비스의 백엔드 API 서버. 현재는 예약 기능 중심의 데모이며, 병원 검색·리뷰·결제 등으로 확장 예정.

## 기술 스택

- **Java 17**, **Spring Boot 3.2.3**
- **Spring Data JPA** + **H2** (개발용 인메모리 DB, 추후 MySQL/PostgreSQL로 교체 예정)
- **Bean Validation** (`jakarta.validation`)
- **Maven Wrapper** (`./mvnw`) — Maven 별도 설치 불필요

## 실행

```bash
./mvnw spring-boot:run
```

- 서버: `http://localhost:8080`
- H2 콘솔 (개발용): `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:appointmentdb`

## 패키지 구조

```
src/main/java/com/goodoc/demo/
├── controller/     # @RestController, 요청/응답 처리
├── service/        # 비즈니스 로직, @Transactional 관리
├── repository/     # Spring Data JPA 인터페이스
├── entity/         # JPA 엔티티
├── dto/            # Request / Response DTO
└── exception/      # GlobalExceptionHandler
```

## API 엔드포인트

| 메서드  | URL                          | 설명                        |
|--------|------------------------------|-----------------------------|
| POST   | `/api/appointments`          | 예약 생성                   |
| GET    | `/api/appointments`          | 전체 조회 (`?patientName=`) |
| GET    | `/api/appointments/{id}`     | 단건 조회                   |
| PATCH  | `/api/appointments/{id}/cancel` | 예약 취소                |

## 현재 도메인 모델

**Appointment**
- `patientName`, `doctorName`, `department`, `appointmentDate`
- `status`: `PENDING` → `CONFIRMED` → `CANCELLED`
- `createdAt`: `@PrePersist` 자동 설정

## 코드 컨벤션

- **레이어 분리 원칙**: Controller는 HTTP 처리만, 비즈니스 로직은 Service에
- **DTO 변환**: `AppointmentResponse.from(entity)` 정적 팩토리 패턴 사용
- **트랜잭션**: Service 클래스에 `@Transactional(readOnly = true)` 기본, 쓰기 메서드에만 `@Transactional` 개별 선언
- **에러 처리**: `GlobalExceptionHandler`에서 통합 처리
  - 404 → `IllegalArgumentException`
  - 409 → `IllegalStateException`
  - 400 → `MethodArgumentNotValidException` (Bean Validation)
- **Lombok 미사용**: Getter/Setter 직접 작성 (현재 방식 유지, 변경 시 합의 필요)

## 확장 계획

앞으로 굿닥 서비스처럼 아래 기능들을 추가할 예정:

- **병원/의사 도메인**: `Hospital`, `Doctor` 엔티티 분리, 연관관계 설정
- **환자 계정**: `Patient` 엔티티, Spring Security + JWT 인증
- **병원 검색**: 진료과·지역·평점 기반 필터링
- **리뷰**: 진료 완료 후 리뷰 작성
- **DB 교체**: H2 → MySQL (운영) / PostgreSQL, `application-prod.properties` 분리
- **테스트**: `@SpringBootTest` 통합 테스트, `@DataJpaTest` 레포지토리 테스트

## DB 교체 시 주의사항

현재 `spring.jpa.hibernate.ddl-auto=create-drop`은 개발 전용.
운영 DB로 전환 시 반드시 `validate` 또는 `none`으로 변경하고 Flyway/Liquibase 마이그레이션 도입.
