# API testing

Use a browser for GET requests or Postman/curl for all methods.

## Health
`GET http://localhost:8080/api/health`

Expected shape:
```json
{"status":"UP","application":"AquaGuard AI"}
```

## Submit a reading
`POST http://localhost:8080/api/readings`
Header: `Content-Type: application/json`
Body:
```json
{
  "zone": "Kitchen",
  "flowRateLpm": 3.2,
  "tankLevelPercent": 55,
  "source": "SIMULATED"
}
```

## Start / stop simulation
- `POST http://localhost:8080/api/simulation/start`
- `POST http://localhost:8080/api/simulation/stop`
- `GET http://localhost:8080/api/simulation/status`

## Mark a leak repaired
1. Call `GET /api/leaks` to get an event ID.
2. Call `POST /api/leaks/{id}/repair` with body:
```json
{"postRepairFlowLpm":0.4}
```

IDs are database-generated. Do not assume an ID before retrieving it.
