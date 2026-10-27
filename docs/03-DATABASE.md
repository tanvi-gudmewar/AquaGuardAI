# Database setup — MySQL Workbench

1. Start MySQL Server.
2. Open MySQL Workbench and connect to your local instance.
3. Open `database/schema.sql`, then click the lightning-bolt Execute button.
4. Confirm the `aquaguard` schema exists.
5. Configure the backend environment variables and start Spring Boot.
6. JPA creates/updates the starter tables because `spring.jpa.hibernate.ddl-auto=update` is enabled for local development.
7. Refresh the Schemas panel to inspect tables such as `water_readings`, `water_tanks`, `leak_events`, and `repair_records`.

For production, replace automatic schema updates with Flyway/Liquibase migrations and use a restricted database account.
