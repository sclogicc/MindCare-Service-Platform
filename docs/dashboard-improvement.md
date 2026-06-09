# Dashboard Improvement

## This Round

The dashboard page has been upgraded from placeholder content to a real business home page.

Current page path:

- `/dashboard`

## What Has Been Added

### Real statistic cards

The home page now shows:

- total appointments
- pending appointments
- completed appointments
- confirmed appointments

These values are no longer hard-coded.
They are calculated from the existing appointment paging API totals.

### Recent appointment table

The home page now shows the latest appointments for the current scope.

### Role-aware scope

Current logic:

- admin: view global data
- ordinary user: view personal appointment data by `userId`
- counselor: view personal appointment data by matched `counselorId`

To support counselor matching, the counselor option object now includes:

- `userId`

### Business insight area

The right panel now shows:

- pending appointment count
- yearly appointment total
- top counselor by workload
- monthly appointment preview

## Reused Existing APIs

This round intentionally avoided creating a new complex dashboard back-end module.
It reuses the current APIs instead.

Used APIs:

- `GET /appointments`
- `GET /counselors/options`
- `GET /reports/counselorWorkload`
- `GET /reports/monthlyAppointments`

## Why This Design Is Appropriate

For the current project stage, this approach is better because:

1. it keeps complexity low
2. it fits the current teaching-style Spring Boot + Vue project
3. it improves visible results quickly
4. it keeps the code easy to explain in an interview

## Verification

The following checks passed:

- `mvn clean compile`
- `npm run build`

## Next Recommended Step

After the dashboard, the next practical improvement should be one of these:

1. implement counselor management page
2. implement schedule management page
3. implement consultation record page and connect upload

Given the current project state, the best next step is:

- schedule management page

Reason:

- it directly supports the appointment flow
- it makes the counselor detail and booking chain more complete
- it is easy to demonstrate in the browser
