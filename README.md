﻿# 📝 화장품 메모장

백엔드 토이 프로젝트로 진행하는 프로젝트입니다. Spring Boot를 활용하여 "화장품 메모장" 웹 애플리케이션을 구현하였습니다.

----

## 👨‍🏫 프로젝트 소개

화장품에는 유통 기한이 존재한다. 하지만, 제품을 개봉한 날짜에 따라 사용 기한이 정해진다.


이 웹 애플리케이션은 언제, 무슨 화장품을 개봉하였는지 기록하고 알림을 받게 하여 화장품을 일정한 시기에 교체할 수 있도록 도와준다.

----

## 🖥 프로젝트 구성 및 기본 기능

개발 목표 (필수)

- 메모장
  - [x] 스킨케어 메모를 저장
  - [X] 스킨케어 메모를 삭제
  - [X] 스킨케어 메모를 수정
  - [X] 특정 회원의 스킨케어 메모를 모두 읽기
  - [ ] 메모의 정렬 기준을 선택
  - [X] 메모를 페이징
  - [ ] 휴지통 기능

- 알림 서비스 (현재는 Service Worker를 활용한 웹 푸시만 처리, Android 및 ios는 추후 지원 예정)
  - [x] FCM을 활용한 Web Push 구성
  - [x] 구성된 Web Push를 활용하여 일정 시간에 메모를 확인 후 Web Push
  - [x] 회원만 Web Push를 활용할 수 있고 여러 개의 디바이스에서 Web Push를 수신할 수 있음
  - [x] 화장품의 이름과 만료 날짜를 Web Push로 수신할 수 있음

- 화장품 정보 모음집

- 자신의 루틴을 공유할 수 있는 커뮤니티
  - [ ] 자신이 작성한 메모 또는 새로 작성할 메모를 공유


향후 계획된 모듈 (선택)

- 과거 작성 메모 보기

- 화장품 추천

- 가격 비교

- 화장품 조합 점수 계산

- 실시간 대화방

- 친구들과 나의 메모 공유하기
----

## 📊 개발 도메인

- 메모
  - [x] 사용 기한 마지막 날짜가 개봉 날짜 이전일 수 없다

----

## ⏲️ 개발 기간

- 2024.09.23(월) ~
----

## 🧑‍🤝‍🧑 개발자 소개

- **최민규** : 기획, 백엔드 개발
----