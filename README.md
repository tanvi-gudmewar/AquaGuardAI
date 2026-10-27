# AquaGuard AI — Full-Stack Starter Project

AquaGuard AI is a water-monitoring demo that uses simulated readings to demonstrate suspected leak detection, tank-fill predictions, consumption analytics, and estimated water savings.

## Technology
- Frontend: HTML, CSS, JavaScript, React.js (Vite)
- Backend: Java 21, Spring Boot 3.5, Spring Web, Spring Data JPA, Validation
- Database: MySQL
- AI logic: explainable Java rules/statistics (not a trained ML model)

> This is a functional starter implementation for local development. Simulated data is not evidence of a real physical leak. The included leak detector uses transparent rules and should be calibrated and validated with real sensor data before deployment.

## Project layout
```text
AquaGuard_AI_Project/
├── frontend/                 # React UI, HTML entry, CSS, JavaScript
├── backend/                  # Spring Boot REST API
├── database/schema.sql       # Optional manual database setup
├── docs/                     # Step-by-step setup guides
├── .gitignore
└── README.md
```

## Prerequisites
Install:
1. JDK 21
2. Maven 3.9+ (or use an IDE's Maven integration)
3. Node.js 20+ and npm
4. MySQL 8+
5. VS Code or IntelliJ IDEA, and MySQL Workbench
6. Optional: Postman

## Step 1 — Create the database
Open MySQL Workbench and connect to your local MySQL server. Open `database/schema.sql` and execute it. It creates the `aquaguard` database. The Spring Boot application can also create/update tables from JPA entities when configured.

## Step 2 — Configure the backend
From the project root:
```bash
cd backend
```
Set environment variables before starting the server.

**Windows PowerShell:**
```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/aquaguard"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="YOUR_MYSQL_PASSWORD"
```

**macOS/Linux:**
```bash
export DB_URL="jdbc:mysql://localhost:3306/aquaguard"
export DB_USERNAME="root"
export DB_PASSWORD="YOUR_MYSQL_PASSWORD"
```

Start the backend:
```bash
mvn spring-boot:run
```
API base URL: `http://localhost:8080`
Health check: `http://localhost:8080/api/health`
Dashboard API: `http://localhost:8080/api/dashboard`

## Step 3 — Configure and run the frontend
Open a second terminal:
```bash
cd frontend
npm install
npm run dev
```
Open the URL printed by Vite, usually `http://localhost:5173`.

The frontend calls `http://localhost:8080/api`. Override this with `VITE_API_BASE_URL` if needed. Copy `frontend/.env.example` to `frontend/.env` to configure it.

## Step 4 — Try the demo
1. Open the frontend.
2. Click **Start simulation** to generate simulated water readings.
3. Use **Inject leak** to create an abnormal-flow demo reading.
4. Review the dashboard metrics and recent readings.
5. Open the **Leak detection** section and mark an event as repaired.
6. Open **Tank prediction** and test different tank capacities and flow rates.

## API endpoints included
- `GET /api/health`
- `GET /api/dashboard`
- `GET /api/readings`
- `POST /api/readings`
- `POST /api/simulation/start`
- `POST /api/simulation/stop`
- `GET /api/simulation/status`
- `GET /api/leaks`
- `POST /api/leaks/detect`
- `PATCH /api/leaks/{id}/status`
- `POST /api/leaks/{id}/repair`
- `GET /api/tanks`
- `POST /api/tanks`
- `GET /api/tanks/{id}/prediction`
- `GET /api/analytics/consumption`
- `GET /api/analytics/savings`
- `GET /api/analytics/savings/export`

## Sample API request
```bash
curl -X POST http://localhost:8080/api/readings \
  -H "Content-Type: application/json" \
  -d '{"zone":"Kitchen","flowRateLpm":3.2,"tankLevelPercent":55,"source":"SIMULATED"}'
```

## Troubleshooting
- **Backend cannot connect to MySQL:** check MySQL is running, database exists, and `DB_USERNAME` / `DB_PASSWORD` are correct.
- **Port 8080 already in use:** stop the other app or change `server.port` in `backend/src/main/resources/application.yml`.
- **Port 5173 already in use:** Vite will print another local URL.
- **Frontend says API unavailable:** start Spring Boot first and check `http://localhost:8080/api/health`.
- **Maven not found:** install Maven or import `backend/pom.xml` into IntelliJ/Eclipse and run `AquaGuardApplication`.
- **No readings shown:** start simulation or submit a reading.
- **CORS error:** ensure frontend is served from `http://localhost:5173`; adjust `app.cors.allowed-origin` if your Vite URL differs.

## Important notes
- The simulation is deliberately simple and is intended for a hackathon/college demonstration.
- Rule-based detection is not a trained AI model. For credible ML evaluation, collect representative labeled data, train and test a model, and report metrics.
- Savings are estimates based on configured assumptions, not verified real-world conservation.
- Authentication, role-based access, production deployment, real sensor ingestion, and migration/versioning policy are future enhancements.
=======

