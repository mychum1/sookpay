
# 소개
내 방 친구들에게 돈을 뿌리고 받는 이벤트의 API 를 구현한다.

# 개발 환경 
1. 언어 : Java 11
2. 프레임워크 : Spring Boot 2
3. 데이터베이스 : H2
4. OS : MacOS
5. 빌드 : Gradle
6. 테스트 : Spring Boot test
7. 기타 : JPA 

# 기능
## 1. 돈 뿌리기
* 뿌리기 요청 건에 대한 고유 토큰 값을 발급한다.
* 분배할 금액을 정의한다.

## 2. 돈 받기
* 받기 요청한 사용자에게 분배한다.
* 이미 받은 사용자는 받을 수 없다.
* 뿌리기 요청한 사용자는 받을 수 없다.
* 동일한 대화방에 있는 사용자만 받을 수 있다.
* 뿌리기를 요청한 후 10분간만 받을 수 있다. 
    * 10분은 추후 변경할 수 있다.

## 3. 뿌린 정보 상세 보기
* 뿌리기 건의 현황을 조회한다.
* 뿌린 사람만 조회할 수 있다.
* 뿌리기를 요청한 후 7일 동안만 조회할 수 있다.
    * 7일은 추후 변경할 수 있다.

## 4. 매일 로깅 파일 축적
* /log 에 매일 축적되는 로그 파일을 확인해볼 수 있다.

# 실행 방법
1. 직접 build
```$xslt
$ git clone https://github.com/mychum1/sookpay.git
$ cd sookpay
$ ./gradlew build
$ java -jar build/libs/sookpay-0.0.1-SNAPSHOT.jar
```

2. build 된 jar 실행
```$xslt
$ git clone https://github.com/mychum1/sookpay.git
$ cd sookpay
$ java -jar sookpay-0.0.1-SNAPSHOT.jar
```
# 핵심 문제 해결 전략
## 1. 동시에 동일인이 뿌리기를 받을 경우 
## 2. 동시에 받기 요청을 할 경우 
## 3. 같은 방의 사용자라는 것을 확인할 방법
## 4. 중복 토큰이 발생할 경우


# 테스트케이스
## API 
### 1. 돈뿌리기 
#### 1. 성공 시나리오
#### 2. 실패 시나리오
1. 받기 인원을 0인으로 지정했을 경우
        
### 2. 돈받기
#### 1. 성공 시나리오
#### 2. 실패 시나리오
1. 같은 방에 있는 사용자가 아닐 경우
2. 유효시간이 지나 받기 요청을 했을 경우
3. 돈뿌린 요청자가 받기 요청을 했을 경우
4. 이미 한 번 받은 사용자가 재요청했을 경우
5. 이미 전부 받아서 못 받았을 경우

### 3. 뿌린돈 결과 조회
#### 1. 성공 시나리오
#### 2. 실패 시나리오
1. 조회할 돈뿌리기 정보가 없는 경우
2. 뿌리지 않은 사용자가 조회 요청을 했을 경우 
3. 조회기간 넘어서 조회했을 경우
        
# API 명세서
## POST /api/spray
### Request
돈 뿌리기 건을 생성한다. 

parameter name|value type|의미|required|default|조건
--------------|------------|-----|-----|------|---
personnel|Integer|받을 수 있는 요청자 수|TRUE|x|0이상
money|Long|뿌린 금액|TRUE|x|

header name|value type|의미|required|default
--------------|------------|-----|-----|------
X-USER-ID|String|요청자 아이디|TRUE|x
X-ROOM-ID|String|방 아이디|TRUE|x

### Response
field name|value type
----------|------------
code|Integer
msg|String
data|Spray Object

## GET /api/spray
뿌린 금액 받기 요청을 한다.
### Request
parameter name|value type|의미|required|default
--------------|-----------|---|-------|--------
token|String|뿌리기 요청의 토큰|TRUE|x

header name|value type|의미|required|default
--------------|------------|-----|-----|------
X-USER-ID|String|요청자 아이디|TRUE|x
X-ROOM-ID|String|방 아이디|TRUE|x

### Response
field name|value type
----------|-----------
code|Integer
msg|String
data|Receipt Object

## GET /api/spray/info
뿌린 요청의 현황을 조회한다.
### Request
parameter name|value type|의미|required|default
--------------|-----------|---|-------|--------
token|String|뿌리기 요청의 토큰|TRUE|x

header name|value type|의미|required|default
--------------|------------|-----|-----|------
X-USER-ID|String|요청자 아이디|TRUE|x
X-ROOM-ID|String|방 아이디|TRUE|x

### Response
field name|value type
----------|-----------
code|Integer
msg|String
data|SprayInfo Object

## Response Objects
### Spray

### Receipt

### SprayInfo

## Response Codes

code | 의미
-----|----
200 | success
500 | fail

## Response Msgs

msg | 의미
----|----
success | 성공 
No Authority|권한이 없는 경우
Time Over|시간 초과
No Spray Info|생성한 뿌리기 건이 없는 경우
No Room User|같은 방의 사용자가 아닌 경우
Already taken|이미 받았을 경우
Not Valid|여러 가지 사유로 유효하지 않을 경우
    
