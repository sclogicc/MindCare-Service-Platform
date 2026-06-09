# Appointment Create Flow

## Current Goal

This round adds a complete front-end and back-end flow for creating an appointment:

1. Query counselor options
2. Query one counselor's available schedules
3. Submit a new appointment

## New Backend APIs

### Query counselor options

- Method: `GET`
- Path: `/counselors/options`
- Purpose: provide counselor select options for the appointment form

### Query counselor detail with available schedules

- Method: `GET`
- Path: `/counselors/{id}`
- Purpose: return counselor detail and current available `scheduleList`

### Submit appointment

- Method: `POST`
- Path: `/appointments`
- Purpose: submit a new appointment

## Current Frontend Pages

- `/appointments`: appointment list page
- `/appointments/create`: appointment create page

## Current Frontend Flow

1. Open the appointment list page
2. Click `新增预约`
3. Choose a counselor
4. The page automatically requests counselor detail and available schedules
5. Choose one schedule
6. Fill contact phone and remark
7. Submit the appointment

## Important Business Rule

The back-end still performs the final conflict check when submitting:

- It checks whether the selected `schedule_id` is already occupied by a non-canceled appointment
- Even if the front-end page shows the schedule, the back-end is still the final source of truth

This design is more realistic and is suitable for interview explanation.

## Verified Result

The new code has been verified on a temporary back-end port `8081`:

- `/counselors/options` works
- `/counselors/1` works
- `POST /appointments` works

## Why You May Need To Restart The Backend

If your browser can open the new route but the new API returns an old error,
the most likely reason is that your local `8080` process is still running an older server instance.

In that case:

1. stop the old Spring Boot process on `8080`
2. restart the back-end project from the latest project directory
3. keep the front-end dev server running

## Recommended Demo Account

For booking demonstration, prefer:

- Username: `user01`
- Password: `123456`

Reason:

- this account better matches the business meaning of "ordinary user creates appointment"

## Recommended Startup Commands

### Backend

```powershell
cd D:\project\my-project\MindCare-Service-Platform
mvn org.springframework.boot:spring-boot-maven-plugin:3.2.10:run
```

### Frontend

```powershell
cd D:\project\my-project\MindCare-Service-Platform\frontend
npm run dev
```
