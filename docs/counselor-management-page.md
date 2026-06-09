# Counselor Management Page

## This Round

This round adds a practical counselor management page for the current project polish stage.

Current page path:

- `/counselors`

## Scope

This round intentionally keeps the scope moderate.

Included:

- page query
- condition query
- detail view
- edit counselor business information
- enable / disable counselor

Not included in this round:

- create account + create counselor transaction
- delete counselor
- linked sys_user profile editing

Reason:

- current goal is project polish and demo readiness
- the existing project already has a full business chain
- this scope is enough for a convincing admin page without reopening larger account-management work

## Backend Additions

### New pojo

- `CounselorQueryParam`
- `CounselorPageItem`
- `CounselorUpdateParam`
- `CounselorStatusUpdateParam`

### Extended mapper

- `CounselorMapper`
- `CounselorMapper.xml`

### Extended service

- `CounselorService`
- `CounselorServiceImpl`

### Extended controller

- `CounselorController`

## New or Extended APIs

### Page query

- `GET /counselors`

### Management detail

- `GET /counselors/manage/{id}`

### Update counselor

- `PUT /counselors`

### Update counselor status

- `PUT /counselors/status`

## Frontend Additions

### New page

- `frontend/src/views/counselor/CounselorListView.vue`

### Extended API file

- `frontend/src/api/counselor.js`

### New menu entry

- `咨询师管理`

## Frontend Features

- filter by name
- filter by specialty
- filter by status
- page query
- detail drawer
- edit dialog
- enable / disable action
- schedule preview in counselor detail

## Design Choice

The detail drawer uses:

- counselor basic information
- schedule list

This is valuable because it links the counselor management page back to the appointment scheduling context,
which makes the demo flow easier to explain.

## Verification

The following checks passed:

- `mvn clean compile`
- `npm run build`

The following API checks were verified on temporary port `8081`:

1. counselor page query
2. counselor management detail query
3. counselor status update

## Important Note

During verification, counselor `1` was temporarily switched to disabled and then restored to enabled.
The final status has already been restored.

## Next Recommended Step

At this point the project already has:

- login
- dashboard
- counselor management
- schedule management
- appointment management
- consultation record
- feedback
- report

So the next best step is no longer adding another major business page first.

The better continuation is:

1. polish dashboard and menu experience
2. improve demo data and reset script usage guide
3. prepare project explanation and resume wording
