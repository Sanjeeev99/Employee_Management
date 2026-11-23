# Employee Management - Spring Boot Java 8

Minimal monolithic Spring Boot CRUD project.

## Requirements
- Java 8
- Maven
- MySQL

## Database

Run:

CREATE DATABASE employee_db;

Then update MySQL username/password in:
src/main/resources/application.properties

## Run

mvn spring-boot:run

## APIs

POST   http://localhost:8080/employees
GET    http://localhost:8080/employees
GET    http://localhost:8080/employees/1
DELETE http://localhost:8080/employees/1

POST body:

{
  "name": "Sanjeev",
  "email": "sanjeev@gmail.com",
  "salary": 50000
}
