# Schedule Management Module

## This Round

This round adds a complete schedule management module for the project.

Main purpose:

- maintain counselor available schedules
- support the appointment creation flow with cleaner schedule data
- provide a visible back-office management page for demo and interview explanation

## Backend Scope

New controller:

- `ScheduleController`

New service:

- `ScheduleService`
- `ScheduleServiceImpl`

New mapper:

- `ScheduleMapper`
- `ScheduleMapper.xml`

New pojo:

- `ScheduleQueryParam`
- `SchedulePageItem`
- `ScheduleStatusUpdateParam`

## Backend APIs

### Page query

- `GET /schedules`

Supports:

- `page`
- `pageSize`
- `counselorId`
- `status`
- `scheduleDate`

### Detail query

- `GET /schedules/{id}`

### Add schedule

- `POST /schedules`

### Update schedule

- `PUT /schedules`

### Update status

- `PUT /schedules/status`

### Delete schedule

- `DELETE /schedules/{id}`

## Key Business Rules

### Schedule overlap validation

When adding or editing a schedule:

- the same counselor on the same date cannot have overlapping time ranges

Validation rule:

- `existing.start_time < current.end_time`
- `existing.end_time > current.start_time`

This is stricter than only checking equal start/end times.
It is more suitable for a real schedule management scenario.

### Past date restriction

The module does not allow creating schedules for past dates.

### Delete restriction

If a schedule has already been referenced by any appointment record,
the system blocks physical deletion to avoid breaking historical data.

## Frontend Scope

New page:

- `/schedules`

New API module:

- `frontend/src/api/schedule.js`

Main features:

- page query
- filter by counselor
- filter by status
- filter by date
- add schedule
- edit schedule
- enable/disable schedule
- delete schedule

## Frontend Interaction Style

The page uses a classic admin style:

- filter form on top
- table list in the middle
- dialog form for add/edit
- action links for enable, disable, delete

## Verified Result

The following checks were completed successfully on temporary port `8081`:

1. schedule page query works
2. schedule creation works
3. schedule status update works
4. schedule deletion works
5. overlap validation works

## Commands Verified

### Backend compile

```powershell
cd D:\project\my-project\MindCare-Service-Platform
mvn clean compile
```

### Frontend build

```powershell
cd D:\project\my-project\MindCare-Service-Platform\frontend
npm run build
```

## Next Recommended Step

After schedule management, the next best continuation is:

1. counselor management page
2. consultation record page

Given the current project state, the more practical next step is:

- consultation record page

Reason:

- appointment -> record -> feedback becomes a more complete business chain
- it also helps connect the upload module to a real form
