<<<<<<< HEAD
# gym-and-fitness-app
=======
# Gymbrut Android App (Java)

Gymbrut is a Java-based Android fitness tracker and gym operations app designed around your provided UI direction.

## Included mobile features
- Splash, onboarding, login, and registration flow
- Home dashboard with membership status, quick QR check-in, workout summary, and weekly chart
- Membership screen with active tier details, renewal CTA, and payment history
- Payment flow simulation with package selection, method selection, and proof-of-payment status
- Workout modules screen with curated programs and guided steps
- Progress dashboard with line chart, completion ring chart, and manual exercise log
- Profile/account screen with session handling

## Suggested admin backend scope
The Android app is structured to connect to a REST backend with these modules:
- Auth: login, register, refresh token, logout
- Members: profile, package status, renewal history
- Payments: create order, upload proof, verify payment, list transactions
- Workouts: retrieve modules by goal, trainer-curated plans, today target
- Attendance: QR validation, visit logs
- Reports: revenue, active members, expired members, visit analytics

## Recommended API endpoints
- POST /api/auth/register
- POST /api/auth/login
- GET /api/members/me
- GET /api/members/me/membership
- GET /api/payments/history
- POST /api/payments/renew
- POST /api/payments/{id}/proof
- GET /api/workouts/modules?goal=hypertrophy
- GET /api/progress/weekly
- POST /api/progress/logs
- POST /api/attendance/checkin
- GET /api/admin/reports/summary

## NetBeans notes
Apache NetBeans can open Gradle projects, so this repository is prepared as a standard Gradle Android project. In practice, Android Studio provides a smoother emulator and layout workflow, but this project can still be edited from NetBeans if your Android SDK and Gradle tooling are configured.

## Important implementation note
This project currently uses mock data in `MockRepository` so you can run the flow and then replace each repository method with Retrofit/API calls and Room persistence.
>>>>>>> 78943da (test)
