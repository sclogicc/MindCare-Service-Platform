# Feedback Module

## This Round

This round completes the feedback evaluation module and closes the main business chain:

- appointment
- confirmation
- consultation record
- feedback evaluation
- report statistics

## Backend Additions

### New controller

- `FeedbackController`

### New pojo

- `Feedback`
- `FeedbackQueryParam`
- `FeedbackPageItem`

### New mapper

- `FeedbackMapper`
- `FeedbackMapper.xml`

### New service

- `FeedbackService`
- `FeedbackServiceImpl`

## Backend APIs

### Add feedback

- `POST /feedbacks`

### Page query

- `GET /feedbacks`

### Query detail by id

- `GET /feedbacks/{id}`

### Query detail by appointment id

- `GET /feedbacks/appointment/{appointmentId}`

### Delete feedback

- `DELETE /feedbacks/{id}`

## Key Business Rules

### Only completed appointments can be evaluated

The service checks:

- appointment exists
- appointment status must be `3`

### One appointment can only have one feedback

The service checks:

- `countByAppointmentId`

### Feedback user must match appointment user

This prevents cross-user feedback submission.

### Counselor id must match appointment counselor id

This prevents the feedback from being attached to the wrong counselor.

### Score range

Score must be:

- `1` to `5`

## Design Choice

The feedback page is appointment-driven instead of pure single-table CRUD.

That means the page can show:

- completed appointments that are still waiting for feedback
- completed appointments that already have feedback

This is closer to a real user workflow.

## Frontend Additions

### New API file

- `frontend/src/api/feedback.js`

### New page

- `/feedbacks`

### New interaction entry

For ordinary users:

- completed appointment row in `/appointments`
- click `去评价`
- jump to feedback page with appointment id

## Frontend Features

- page query
- filter by feedback status
- filter by score
- filter by anonymous flag
- filter by counselor for admin
- detail drawer
- dialog form for feedback submission
- delete feedback for admin

## Verification

The following checks passed:

- `mvn clean compile`
- `npm run build`

The following full chain was verified on temporary port `8081`:

1. user creates appointment
2. admin confirms appointment
3. admin fills consultation record and completes appointment
4. user submits feedback
5. feedback detail query returns the saved result

## Verified Example

One verified appointment chain produced:

- `appointmentId = 6`
- `feedbackId = 3`
- `score = 5`

This proves the feedback module is connected to the real business flow.

## Important Note

Because the full-chain verification created real demo data,
your current local database may differ from the original init script state.

If you want to reset demo data, rerun:

- `sql/init_test_data.sql`

## Next Recommended Step

At this point the main business chain is basically complete.

The next best improvement is no longer a new core module.
Instead, it should be one of these:

1. counselor management page
2. user/account management page
3. home/dashboard refinement
4. data reset / demo guide improvement

Given the current project state, the most practical next step is:

- improve demo readiness and polish remaining management pages
