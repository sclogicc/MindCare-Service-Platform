# Consultation Record Module

## This Round

This round upgrades the consultation record capability from a transaction-only back-end service
to a complete front-end and back-end module.

## Main Goal

Complete the core business chain:

- appointment confirmed
- counselor fills consultation record
- system writes consultation record
- appointment status changes to completed

## Backend Additions

### New controller

- `ConsultationRecordController`

### New query objects

- `ConsultationRecordQueryParam`
- `ConsultationRecordPageItem`

### Extended mapper

- `ConsultationRecordMapper`
- `ConsultationRecordMapper.xml`

### Extended service

- `ConsultationRecordService`
- `ConsultationRecordServiceImpl`

## Backend APIs

### Page query

- `GET /consultationRecords`

Purpose:

- query appointment-driven consultation record list
- include both "confirmed but not yet recorded" and "completed with record"

### Detail query

- `GET /consultationRecords/appointment/{appointmentId}`

Purpose:

- query one appointment's consultation record detail

### Complete appointment with record

- `POST /consultationRecords/complete`

Purpose:

- write consultation record
- update appointment status to completed
- execute both actions in one transaction

## Key Design Choice

The module is appointment-driven instead of pure consultation-record CRUD.

Reason:

1. one appointment corresponds to at most one consultation record
2. the real user action is "complete an appointment by filling a record"
3. this design is easier to explain in an interview

## Frontend Additions

### New page

- `/consultation-records`

### New API files

- `frontend/src/api/consultationRecord.js`
- `frontend/src/api/upload.js`

### New page features

- page query
- filter by appointment status
- filter by record status
- filter by counselor
- detail drawer
- dialog form for filling consultation record
- optional attachment upload

## Important Interaction Change

The appointment list page no longer directly uses "complete appointment" as a plain status update action.

Now:

- confirmed appointment -> click `填写记录`
- jump to consultation record page
- fill record -> submit -> transaction completes both write + status update

This avoids bypassing the transactional business flow.

## Attachment Note

Attachment upload is optional.

If OSS is correctly configured:

- the record form can upload consultation attachments

If OSS is not configured:

- the record form can still be submitted without attachment

## Verification

The following checks passed:

- `mvn clean compile`
- `npm run build`

The following back-end flows were verified on temporary port `8081`:

1. `GET /consultationRecords`
2. `POST /consultationRecords/complete`
3. `GET /consultationRecords/appointment/{appointmentId}`

## Current Demo Advice

If you want to reset demo data to the initial state,
you can rerun:

- `sql/init_test_data.sql`

## Next Recommended Step

After consultation record module, the next best continuation is:

- feedback evaluation page

Reason:

- then the complete chain becomes:
  booking -> confirmation -> consultation record -> feedback -> report
