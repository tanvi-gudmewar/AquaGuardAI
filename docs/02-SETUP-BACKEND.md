# Backend setup — Java + Spring Boot

1. Install JDK 21 and Maven 3.9+.
2. Ensure `java -version` and `mvn -version` work.
3. Create the MySQL database using `database/schema.sql`.
4. Set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`.
5. From `backend/`, run `mvn spring-boot:run`.
6. Verify `http://localhost:8080/api/health`.

Spring Boot layers:
- `controller`: HTTP routes and JSON responses.
- `service`: business rules and calculations.
- `entity`: JPA database entities.
- `repository`: database access.
- `dto`: request/response data.
- `config`: CORS and configuration.
