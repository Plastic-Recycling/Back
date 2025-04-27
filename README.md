Plastic-Recycling의 백엔드 서버 리포지토리입니다.  
[https://plastic-recycle.pages.dev/](https://plastic-recycle.pages.dev/) (현재 프론트엔드만 배포중인 상태입니다)

### 주요 기능 및 특징
* 탄소 배출 감소량 및 재활용 절감 비용 계산
* 회원가입후 프로필 페이지를 통한 회원 별 재활용 데이터 관리
* 회원 별 데이터 정보 분석 및 결과 시각화
##
### 작업
#### 탄소 배출 감소량 및 재활용 절감 비용 계산 시스템 구현

* [BERKELEY LAB](https://energyanalysis.lbl.gov/publications/climate-impact-primary-plastic)의 연구 자료를 기반으로 플라스틱 유형별(PP,
  PE, PS) 탄소 배출량 데이터를 분석하고 재활용 시 예상되는 탄소 감축 효과를 정량화했습니다.
* [한국환경공단의 자원순환정보시스템 API](https://www.data.go.kr/data/3076421/fileData.do#/)를 활용하여 실시간 플라스틱 시세 정보를 연동하고, 월별 시세 변동에 따른
  재활용 가치를 자동으로 계산하는 시스템을 구현했습니다.
* 플라스틱 유형별 생산량 대비 탄소 배출량 비율을 계산하고, 재활용 시 약 75%의 탄소 감축 효과를 적용한 환경 영향 분석 알고리즘을 개발했습니다.
* 정밀한 수치 계산을 위해 BigDecimal 타입과 MathContext를 활용하여 반올림 오차를 최소화하고, 그램 단위의 미세한 플라스틱 무게에도 정확한 탄소 감축량과 경제적 가치를 산출할 수 있도록
  구현했습니다.
* API 호출 시 당월 데이터가 없는 경우를 대비해 이전 월 데이터를 활용하는 대체 로직을 구현하여 서비스의 연속성을 보장했습니다.

#### AI 기반 플라스틱 재활용 분석 시스템 구현
* Spring Boot 기반의 백엔드 서버와 Flask 기반의 이미지 분석 서버를 활용한 마이크로서비스 아키텍처를 구현했습니다.
* 사용자가 플라스틱 이미지를 업로드하면 Flask 서버로 전송하여 AI 모델을 통해 플라스틱 유형(PP, PE, PS)과 무게를 예측합니다.
* RestTemplate을 사용하여 Flask 서버와의 HTTP 통신을 구현하고 JSON 응답을 처리했습니다.
* 사용자별 재활용 통계(재활용 횟수, 무게, 예상 수익, 탄소 감축량)를 DB에 저장하고 프로필 기능을 통해 시각화했습니다.
* 인증된 사용자와 비인증 사용자 모두 플라스틱 분석 서비스를 이용할 수 있도록 구현하되, 인증된 사용자의 경우 개인 통계에 반영되도록 했습니다.

#### 예외 처리 및 시스템 안정성 구현
* Flask 서버 응답 처리 과정에서 발생할 수 있는 예외를 처리하여 이미지 분석 서비스의 안정성을 확보했습니다.
* 외부 API 호출 시 데이터 없음, 서버 오류 등의 상황에 대비한 예외 처리 로직을 구현했습니다.
* 사용자 인증 관련 예외(UsernameNotFoundException)를 처리하여 안전한 사용자 경험을 제공했습니다.
* ObjectMapper를 활용한 JSON 파싱 과정에서 발생할 수 있는 예외를 적절히 처리하여 데이터 정합성을 유지했습니다.
* 비즈니스 로직의 안정성을 위해 Optional 패턴과 null 체크를 통해 안정성을 확보했습니다.

#### 이메일 인증을 통한 회원가입 구현
* DB의 I/O 최소화를 위해 Caffeine Cache를 통해 유지시간이 짧은 회원가입용 캐시를 관리했습니다.
* MySQL DB를 통해 회원가입 데이터를 저장 및 관리했습니다.
* 이메일 정보를 입력하여 회원가입 시 인증용 캐시를 생성 후 캐시 정보를 포함한 인증 링크를 전송합니다.
* 인증 링크를 통하여 인증 시 캐시 정보와 일치 여부를 확인하고 새로운 캐시를 생성하며 이전 캐시는 삭제 후 회원가입을 위한 세부 정보 입력 폼으로 다이렉션 합니다.
* 회원가입이 완료된 후 회원정보를 DB에 저장하고 캐시를 삭제합니다.
* 회원가입 도중 폼에서 나간 경우 진행중이던 이메일로 로그인 혹은 회원가입을 재시도 시 이메일로 발송된 새로운 인증 코드를 통해 진행할 수 있습니다.

#### 회원가입 예외처리 구현
* 이메일 검증 과정에서 중복 이메일 확인을 위한 EmailDuplicateException을 구현하여 사용자에게 적절한 피드백을 제공합니다.
* 이메일 인증 단계에서 인증 토큰 검증을 위한 DifferentCachedInfoException을 구현하여 캐시된 정보와 요청 정보의 불일치를 처리합니다.
* 회원가입 완료 단계에서 최초 인증한 이메일과 다른 이메일을 사용하는 경우 NotInitialEmailException을 발생시켜 인증 과정의 무결성을 보장합니다.
* 진행 중인 회원가입 요청에 대해 중복 요청이 발생하는 경우 DuplicateRequestException을 통해 처리합니다.
* 전역 예외 핸들러(GlobalExceptionHandler)를 구현하여 모든 예외를 일관된 형식의 ErrorResponse로 변환하고 적절한 HTTP 상태 코드와 함께 응답합니다.
* 입력값 검증 실패 시 field별 상세 오류 정보를 제공하여 클라이언트 측에서 사용자 친화적인 피드백을 제공할 수 있도록 지원합니다.
##
### 사용 기술
<img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"/> <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"/>
<img src="https://img.shields.io/badge/amazonec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white"/>
<img src="https://img.shields.io/badge/amazonrds-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white"/>

### 담당

|                                             고진혁                                           |
|:--------------------------------------------------------------------------------------------:|
| <img src="https://avatars.githubusercontent.com/u/160887371?v=4" width="100" height="100" /> |
|                         [@JinhyeokKo](https://github.com/JinhyeokKo)                         |
|                                         Front,  Back                                         |
