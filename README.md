# Personal Safety Check-in System

[![CI](https://github.com/Dean110/check-in-tracker/actions/workflows/ci.yml/badge.svg)](https://github.com/Dean110/check-in-tracker/actions/workflows/ci.yml)

A personal safety system with flexible check-in schedules, emergency contacts, and privacy controls. Built with Spring Boot 4, OAuth2, SMS/Email notifications.

## Features

- **Primary Users**: Set flexible check-in schedules with custom grace periods
- **Emergency Contacts**: Opt-in to receive alerts, manage preferences, privacy controls
- **Admins**: System management, metrics, global do-not-contact lists
- **DevOps**: Automated CI/CD with GitHub Actions

## Technology Stack

- Spring Boot 4
- Java 25
- JPA with H2 (dev) / PostgreSQL (prod)
- OAuth2 (Google/Apple)
- SMS/Email notifications
- Lombok for clean entities
- GitHub Actions CI/CD

## Getting Started

```bash
./gradlew build
./gradlew test
./gradlew bootRun
```

## Project Structure

- 4 User Personas: Primary Users, Emergency Contacts, Admins, DevOps
- 11 Implementation Tasks tracked in GitHub Project
- Privacy-first design with opt-in consent system
- Location support for check-ins (text or lat/lng)
- Grace period challenges with session invalidation
