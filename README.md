# 📱 Malgage API

> 음성 인식 기반 감정 가계부 애플리케이션 백엔드 서버

[![Java](https://img.shields.io/badge/Java-17-orange)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.x-brightgreen)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-supported-blue)](https://www.docker.com/)
[![AWS](https://img.shields.io/badge/AWS-EC2%20%7C%20RDS-orange)](https://aws.amazon.com/)

## 🎯 프로젝트 개요

Malgage API는 Flutter 모바일 앱의 백엔드 서버로, 사용자가 음성으로 입력한 가계부 데이터를 OpenAI API를 통해 감정과 카테고리로 자동 분류하여 개인화된 소비 패턴 분석을 제공합니다.

### 주요 특징
- 🎤 **음성 기반 가계부 작성** - STT를 통한 편리한 데이터 입력
- 🤖 **AI 감정 분석** - OpenAI GPT-3.5 Turbo를 활용한 자동 분류
- 📊 **실시간 통계 분석** - 복합적인 지출 패턴 분석 (일반/할부 통합)
- 🔐 **보안 강화** - JWT 이중 토큰 인증 및 자동 로그인
- ⚡ **고성능 쿼리** - QueryDSL 기반 최적화된 통계 조회

## 🛠️ 기술 스택

### Core Framework
- **Java 17** - 최신 LTS 버전
- **Spring Boot 3.2.x** - 백엔드 프레임워크
- **Spring Data JPA** - ORM 및 데이터 액세스
- **QueryDSL 5.0+** - 타입 안전 동적 쿼리
- **Spring Security** - 인증 및 보안

### Database
- **MySQL 8.0** - 메인 데이터베이스 (AWS RDS)
- **HikariCP** - 커넥션 풀 관리

### External Services
- **OpenAI GPT-3.5 Turbo** - 감정 분석 및 카테고리 자동 분류
- **Google OAuth 2.0** - 소셜 로그인

### Infrastructure
- **AWS EC2** - 애플리케이션 서버
- **AWS RDS** - MySQL 데이터베이스
- **Docker** - 컨테이너화
- **GitHub Actions** - CI/CD 자동 배포

## 🏗️ 아키텍처

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Flutter App   │    │  Malgage API    │    │   MySQL DB      │
│                 │    │   (Spring)      │    │   (AWS RDS)     │
│ • STT 음성인식   │◄──►│ • JWT 인증      │◄──►│ • 사용자 데이터  │
│ • 가계부 UI      │    │ • REST API      │    │ • 가계부 기록   │
│ • 통계 차트      │    │ • QueryDSL      │    │ • 통계 데이터   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                         ┌─────────────────┐
                         │   OpenAI API    │
                         │ (GPT-3.5 Turbo) │
                         │ • 감정 분석     │
                         │ • 카테고리 분류 │
                         └─────────────────┘
```

## 📞 연락처

**개발자**: Dahyun Song
**이메일**: sdh6411@gmail.com  
**프로젝트 링크**: https://github.com/sdh4716/malgage-api

---
