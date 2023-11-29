## 개요
사용자ID, 비밀번호 입력없이 사용자 계정을 식별하여 로그인 수행

## 기능
### 자동 로그인
- SSO 솔루션에서 제공하는 기능 등을 이용하여 정해진 세션 애트리뷰트를 설정하여 자동 로그인을 수행한다.
- 세션 생성 시 필요한 프로젝트명, 서버명, 포트는 별도의 프로퍼티 파일로 관리하지 않고 MSTR 관리자 페이지를 통하여 설정된 값이 저정되는 xml 파일을 조회하여 이용한다.
### 사용자정보 유지
- 세션 애트리뷰트를 통하여 사용자의 기본정보를 조회할 수 있어야 하며 유지되어야 할 값은 다음과 같다.
    1. 인증방법(표준인증, 트러스트인증)
    2. 사용자ID
    3. 비밀번호
    4. 사용자 소속 사용자그룹정보 (사용자그룹명 키, 오브젝트ID 값의 맵 객체)
    5. MSTR 세션문자열 
### 동시접속 발생 상황에서 MSTR 세션 생성 오류 방지
- 선행 MSTR 세션 생성 요청 미완료 상태에서 후행 MSTR 세션 생성 요청 시 정상적인 MSTR 세션 생성이 실패하는 현상이 발생한다. MSTR 세션 생성 경합 발생 방지를 위하여
MSTR 세션이 필요한 별도의 gateway 페이지를 제공하여 사전에 호출하여 MSTR 세션을 생성하도록 한다.
- gateway 페이지는 정해진 경로 및 jsp 파일명으로 작성되어 있어야 접근가능하며 gateway 페이지내에서 MSTR 세션을 이용한 커스텀 코드를 작성할 수 있다.
### 로그아웃
- MSTR 로그아웃 시 웹세션에 잔류하는 MSTR 접속정보를 초기화하여 MSTR 세션이 재생성되는 상황 
### 다중 프로젝트 지원
- 다수의 프로젝트에 대해 개별적으로 MSTR 세션 접속상태가 유지되어야 한다.
### 이중화 구성 MSTR 서버 지원
- 이중화 구성 환경에서 1대의 서비스 중단 시 다른 MSTR 서버에 MSTR 세션이 생성되어야 한다.

## 파라미터의 전달
- 웹세션 애트리뷰트에 VO 클래스로 설정

## 기능적용방법
- plugin

## 기능이용방법
- '파라미터의 전달' 후 리포트 실행, 데스크탑 페이지 등의 MSTR 페이지 호출
- '파라미터의 전달' 후 gateway 페이지 접속, gateway 페이지내에서 MSTR 페이지 호출, 메뉴 구성 등 내용 수행

## 참조
- [MicroStrategy Online Web SDK » Customizing MicroStrategy Web » Part II: Advanced Customization Topics » Customizing Authentication » Creating a Custom External Security Module (ESM)](https://lw.microstrategy.com/msdz/MSDL/GARelease_Current/docs/projects/WebSDK/Content/topics/esm/Creating_a_Custom_External_Security_Module_ESM.htm?Highlight=ESM)
