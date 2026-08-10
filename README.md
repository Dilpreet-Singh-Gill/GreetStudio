# GreetStudio 🎂

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-brightgreen?style=for-the-badge&logo=spring-boot)
![React](https://img.shields.io/badge/React-19-blue?style=for-the-badge&logo=react)
![Tailwind](https://img.shields.io/badge/Tailwind_CSS-4.0-06B6D4?style=for-the-badge&logo=tailwindcss)

**GreetStudio** is an Enterprise-Grade Automated Birthday Poster Generator built with Java Spring Boot and React. It allows organizations to manage employee birthdays, generate personalized posters via Java2D, augment them with AI-generated wishes via HuggingFace, and automatically dispatch them via an SMTP email scheduler using GitHub Actions.

---

## ✨ Features

- **Auth System:** Secure JWT-based authentication and route protection.
- **Bulk Upload:** Effortlessly import hundreds of employees using Excel `.xlsx` files.
- **Template Management:** Upload custom poster backgrounds to Cloudinary.
- **Image Processing (Java2D):** Automatically crops user profile photos into circles, scales them, and wraps text on top of templates.
- **AI Wishes (HuggingFace API):** Generates contextual birthday wishes.
- **Automated Scheduler:** A secure cron endpoint triggered daily by GitHub Actions.
- **Email Delivery:** Sends individual posters manually or consolidated daily digests automatically using Brevo/Gmail SMTP.
- **Premium UI:** Glassmorphism aesthetic, sleek animations using Framer Motion, and responsive design.

---

## 🛠️ Technology Stack

| Layer | Technology |
|---|---|
| **Backend** | Java 21, Spring Boot 3.2, Spring Security (JWT), Spring Data JPA |
| **Frontend** | React (Vite), Tailwind CSS 4, Framer Motion, Lucide Icons |
| **Database** | PostgreSQL (Neon serverless) |
| **Storage** | Cloudinary (Images & Templates) |
| **AI** | HuggingFace Inference API (`meta-llama/Llama-3.2-1B-Instruct`) |
| **Email** | Spring Boot Starter Mail (Brevo / Gmail) |
| **Automation** | GitHub Actions Cron Jobs |

---

## 🚀 Getting Started (Local Development)

### 1. Prerequisites
- Java 21+
- Node.js 18+
- PostgreSQL (Local or Neon)
- Cloudinary Account
- HuggingFace API Token
- Brevo/Gmail SMTP Credentials

### 2. Backend Setup
1. Clone the repository and navigate to the `backend` directory.
2. Ensure you have copied `.env.example` to `.env` in the root directory and populated all the required secrets.
3. Run the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```

### 3. Frontend Setup
1. Navigate to the `frontend` directory.
2. Install dependencies (Legacy peer deps required due to React 19 testing library issues):
   ```bash
   npm install --legacy-peer-deps
   ```
3. Start the Vite dev server:
   ```bash
   npm run dev
   ```

---

## 🧪 Testing

### Backend
Run the comprehensive JUnit 5 and Mockito test suite:
```bash
mvn test
```

### Frontend
Run the Vitest component tests:
```bash
npm test
```

---

## 📦 Deployment

This repository is configured for immediate Production Deployment on modern serverless platforms. 
See the generated `deployment_guide.md` in the artifacts for full instructions on deploying to **Render**, **Vercel**, and **Neon**.

---

*Architected with Clean Architecture principles and a strict separation of concerns.*
