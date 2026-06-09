# UI Polish Round 7

## This Round

This round focuses on one practical front-end problem:

- some core business pages had inconsistent table width behavior
- horizontal scrollbars could appear at the bottom

## Main Changes

### Unified compact table strategy

Files:

- `frontend/src/views/appointment/AppointmentListView.vue`
- `frontend/src/views/feedback/FeedbackListView.vue`
- `frontend/src/views/consultation-record/ConsultationRecordListView.vue`

What changed:

- reduced column width pressure
- merged date and time into one compact appointment-time column
- removed fixed right-side action columns
- shortened some action button text
- kept details in drawer dialogs instead of forcing all fields into the main table

### Shared table container behavior

File:

- `frontend/src/styles.css`

What changed:

- made `table-card` clip overflow more predictably
- set table width to `100%`
- added a shared `standard-data-table` cell style

## Why This Works

The original issue was not only “page size inconsistency”.
The deeper cause was that several tables had:

1. too many wide columns
2. fixed action columns
3. duplicated time/date fields occupying extra horizontal space

This round reduces width pressure while keeping the same core business actions available.

## Verification

The following check passed:

- `npm run build`
