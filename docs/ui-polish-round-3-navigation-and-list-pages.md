# UI Polish Round 3

## This Round

This round focuses on two visible problems:

1. sidebar active-state presentation was not stable enough
2. core list pages still looked like separate pages instead of one unified admin console

## Main Changes

### Sidebar stability improvement

File:

- `frontend/src/layout/MainLayout.vue`

What changed:

- added an internal scroll container for the navigation area
- changed active background rendering to a dedicated pseudo-layer
- ensured each menu item has a stable rounded background area
- reduced the chance of visual gaps near the lower part of the sidebar when switching pages
- kept the footer independent from the menu scrolling area

### Core list-page consistency

Files:

- `frontend/src/views/appointment/AppointmentListView.vue`
- `frontend/src/views/counselor/CounselorListView.vue`
- `frontend/src/views/schedule/ScheduleListView.vue`
- `frontend/src/styles.css`

What changed:

- unified filter-card header structure
- unified table header structure
- added summary chips above tables
- unified pagination area spacing
- normalized page copy so pages read more like one product

## Why This Matters

For a demo project, the first credibility issue is often not missing APIs, but visual inconsistency.

This round improves:

- navigation stability
- page-to-page consistency
- admin-console presentation quality

## Verification

The following check passed:

- `npm run build`

## Current Follow-Up Priority

The next practical polish step is:

1. unify consultation record and feedback pages to the same list-page standard
2. review report page spacing and chart card hierarchy
3. later optimize large front-end chunks if needed
