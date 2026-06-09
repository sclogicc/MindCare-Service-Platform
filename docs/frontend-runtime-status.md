# Frontend Runtime Status

## Current Ports

- Frontend dev server: `http://localhost:5173`
- Backend API server: `http://localhost:8080`

## Why Two Ports Are Used

This project uses a front-end and back-end separation architecture:

- `5173` is the Vite development server port. It is responsible for rendering the Vue pages in the browser.
- `8080` is the Spring Boot server port. It is responsible for providing login, appointment, and report APIs.

So when you open `http://localhost:5173`, you are visiting the Vue application.
The Vue application then uses Axios to request the back-end interfaces on `http://localhost:8080`.

## Current Frontend Request Configuration

File: `frontend/src/utils/request.js`

- Axios `baseURL` is configured as `VITE_BASE_API || 'http://localhost:8080'`
- JWT token is automatically attached in the `token` request header
- `401` responses are handled uniformly by clearing the login state and redirecting to `/login`

## Current Available Pages

- `/login`: login page
- `/dashboard`: home page
- `/appointments`: appointment management page
- `/reports`: report statistics page

## Verified Status

The following items have been verified locally:

- Back-end project can start successfully with Spring Boot
- Front-end project can build successfully with Vite
- Login API is available
- Appointment list API is available
- Report statistics API is available
- Appointment page source code has been connected to the real back-end interfaces
- Report page source code has been connected to the real back-end interfaces

## Demo Account

- Username: `admin`
- Password: `123456`

## Suggested Startup Commands

### Start Backend

```powershell
cd D:\project\my-project\MindCare-Service-Platform
mvn org.springframework.boot:spring-boot-maven-plugin:3.2.10:run
```

### Start Frontend

```powershell
cd D:\project\my-project\MindCare-Service-Platform\frontend
npm run dev
```

## Recommended Browser Verification Path

1. Open `http://localhost:5173`
2. Log in with `admin / 123456`
3. Visit the appointment management page and check whether the list data is displayed
4. Visit the report page and check whether the charts are displayed

## Next Recommended Work

The current project already has a visible running result.
The next priority should be one of the following:

1. Complete the appointment creation page and connect the counselor/time-slot selection flow
2. Continue implementing counselor management and schedule management pages
3. Improve dashboard data cards and make the home page use real statistics data
