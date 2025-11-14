# Full-Stack Application (Spring Boot + Angular)

This repository contains a full-stack application with a **Spring Boot backend** and an **Angular frontend**. Both services can be launched together using Docker Compose or manually.

## Getting Started

### Prerequisites
- Docker  
- Docker Compose
- Node.js & Angular CLI (for manual frontend startup)
- Java & Maven/Gradle (for manual backend startup)

## Running the Application

### Option 1: Run using Docker

Start both the backend and frontend using:
```
docker compose up --build
```

### Option 2: Run manually (without Docker)

You can also start both applications separately using their standard commands.

#### Start the Angular frontend:
```
cd TriviaFrontend
ng serve
```
Instructions for installing Angular can be found in [TriviaFrontend/README.md](./TriviaFrontend/README.md)
#### Start the Spring Boot backend:
```
cd trivia_backend
./mvnw spring-boot:run
```

## Accessing the Applications

### Frontend (Angular)
The frontend will be available at:
```
http://localhost:4300
```

(or at `http://localhost:50000` if running manually)

### Backend Swagger UI (Spring Boot)

API documentation is available at:
```
http://localhost:8080/swagger-ui/index.html
```

## Project Structure
```
/trivia_backend # Spring Boot application
/TriviaFrontend # Angular application
docker-compose.yml
```

Both applications run inside Docker containers and communicate through the Docker network.

# Hosted
The frontend is currently being hosted at [Trivia](wooden-isabella-jessevgool-7179aac1.koyeb.app/)

Don't mind the url (I didn't choose it)