
# Smart Health Tracker

A full-stack web app for tracking health parameters (BP, sugar, weight, heart rate) with AI advice, alerts, and exports. Built with Spring Boot backend and React frontend.

## Website

**Live URL**: https://smart-health-tracker-frontend1.vercel.app/login

**Application Screenshots** : https://github.com/kiranc2001/smart-health-tracker/tree/main/screenshots

**Backend Github Repo** : Current repo

**Frontend Github Repo**: https://github.com/kiranc2001/smart-health-tracker-frontend1

**For more detailed explaination**:

**Medium** : https://medium.com/@kirangowda0212/building-a-smart-health-tracker-a-full-stack-app-with-spring-boot-react-and-ai-integration-505927ae8cf1


## Overview
Users can register/login, log daily readings, view trends/charts, get AI suggestions (OpenAI), receive email alerts/weekly summaries, and export data (PDF/CSV). Secure, responsive, and mobile-friendly.

## Features
- **Auth**: Register, login, forgot/reset password (OTP email).
- **Records**: Add/update/delete, list/filter by date, history with Chart.js trends.
- **Alerts**: AI advice, mark read, generate.
- **Dashboard**: Latest stats, quick actions.
- **Profile**: View details.
- **Exports**: PDF/CSV downloads.
- **Emails**: OTP, alerts, weekly reports (scheduled).

## Tech Stack
- **Backend**: Spring Boot 3.5.7 (Java 21), JPA/Hibernate, PostgreSQL, OpenAI SDK, iText7, ModelMapper.
- **Frontend**: React 18 (Vite), Axios, React Router, Formik/Yup, Chart.js, Bootstrap 5, React Toastify.
- **Deploy**: Heroku (backend), Vercel (frontend).

## Local Setup
### Backend
1. Clone repo → IntelliJ → Open pom.xml.
2. Update `application.properties` (DB, email, OpenAI key).
3. Run `SmartHealthTrackerApplication` (port 9000).
4. Test: Postman POST /api/users/register.

### Frontend
1. `npm install` (deps from package.json).
2. `.env`: `VITE_API_BASE_URL=http://localhost:9000/api`.
3. `npm run dev` (port 5173).

## Deployment
- **Backend (Heroku)**: `heroku create app-name`, add Postgres (`heroku addons:create heroku-postgresql:essential-0`), set vars (`heroku config:set ...`), `git push heroku main`.
- **Frontend (Vercel)**: GitHub repo → vercel.com import → Add VITE_API_BASE_URL → Deploy.

## Testing
- Postman collection for APIs (register → add record → advice → export).

## Contact
Email: kirangowda0212@gmail.com

LinkedIn: https://www.linkedin.com/in/kiran-c-gowda-2507021b9/




