# ERP–Salesforce Integration 프로젝트

## 프로젝트 개요

ERP의 기준정보 및 영업 데이터를 Salesforce CRM과 연동하기 위한 **Backend Integration 시스템**입니다. 별도의 사용자 화면을 제공하는 서비스가 아니라, ERP와 Salesforce 사이에서 고객·품목·견적·수주·배송 데이터를 양방향으로 전달하고 동기화하는 Middleware 성격의 프로젝트입니다.

> 공개 저장소는 실제 기업 프로젝트에서 경험한 기술적 패턴을 바탕으로 새로 구현한 포트폴리오 버전이며, 회사 원본 소스·실데이터·내부 DB 정보·Salesforce 조직 정보 및 인증정보를 포함하지 않습니다.

## 주요 기술

`Java` · `Spring Framework` · `Spring MVC` · `MyBatis` · `Maven` · `WAR` · `MSSQL 계열 데이터 처리` · `Salesforce REST API` · `Bulk API` · `OAuth 2.0`

## 담당/구현 경험을 설명할 때 강조할 부분

### 1. ERP → Salesforce 데이터 동기화

ERP DB에서 고객, 품목, 수주 및 배송 데이터를 조회한 뒤 Salesforce 객체 형태로 변환하여 REST API 기반으로 Upsert하도록 구성했습니다. 외부 키를 이용해 재호출 시 중복 데이터가 생성되지 않도록 동기화 기준을 설계했습니다.

### 2. 대량 품목 데이터 Bulk API 처리

대량의 품목 마스터를 단건 REST 호출로 처리하지 않고 Job 기반 Bulk 처리 흐름으로 분리했습니다.

`ERP 조회 → CSV 생성 → Bulk Job 생성 → 데이터 Upload → Job Close → 상태 Polling → 성공/실패 결과 확인`

이를 통해 대량 데이터 연동에서 발생하는 처리시간과 API 호출량을 고려한 연동 방식을 경험했습니다.

### 3. Salesforce → ERP 견적/수주 연동

Salesforce에서 전달되는 견적과 수주 Master/Detail 데이터를 JSON으로 수신하여 ERP DB에 반영하는 역방향 연동을 구현했습니다. 견적과 수주 데이터가 하나의 업무 단위로 처리되도록 Transaction을 적용해 중간 실패 시 데이터 불일치를 방지했습니다.

### 4. Integration 계층 분리

공개 버전에서는 `CrmGateway` 인터페이스를 두어 업무 Service가 Salesforce HTTP 구현에 직접 의존하지 않도록 재설계했습니다. 기본 실행에서는 Mock Adapter를 사용하며, 실제 HTTP Adapter는 명시적인 Profile과 환경변수 설정이 있을 때만 활성화됩니다.

## 포트폴리오 한 줄 설명

> 기존 ERP 데이터베이스와 Salesforce CRM 사이에서 고객·품목·견적·수주·배송 데이터를 양방향으로 연동하고, 대량 품목 데이터에는 Salesforce Bulk API Job 방식을 적용한 Spring MVC 기반 Integration 프로젝트입니다.

## 면접에서 설명하기 좋은 포인트

- Spring Boot가 아닌 전통적인 Spring MVC + WAR 구조의 기업 시스템 경험
- MyBatis를 활용한 기존 ERP SQL/데이터 구조 연계
- REST API 단건 연동과 Bulk API 대량 연동의 차이
- OAuth Token 기반 외부 시스템 인증 흐름
- 외부 키 기반 Upsert와 재처리/중복 방지 전략
- Master/Detail 데이터의 Transaction 처리
- 외부 연동 실패를 추적하기 위한 Sync History 설계
- 회사 소스 공개 대신 경험을 일반화한 공개용 재구현을 선택한 이유
