
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
동일인이 다른 디바이스로 동시에 받기를 요청한 경우 update query를 요청하는 시점에 데이터베이스에서
 
    1. 뿌리기 건에 할당된 빈 영수증들 데이터들을 확인해서 다른 인스턴스와 요청 에서 선점했는지 확인한다.
    2. 이미 선점했다면 롤백하고, 선점하지 않았다면 선점한다.
    3. 나중에 들어온 요청자가 먼저 선점하는 경우를 방지하기 위해 정렬해서 모든 요청자가 순서대로 선점할 수 있게끔 한다.
     
## 2. 동시에 받기 요청을 할 경우 
여러 인스턴스에서 동시에 뿌리기 건의 정보를 가져와서 유효한지 판단하는 경우 update query를 요청하는 시점에 데이터베이스에서 다시 한번 status와 받은 사람 값을 확인한 후 수령한다.
 
## 3. 같은 방의 사용자라는 것을 확인할 방법
요청온 방 아이디와 토큰이 일치하다면 같은 방의 사용자만 알 수 있는 정보라고 생각하고 처리한다.

## 4. 중복 토큰이 발생할 경우
문자열 3자리를 구성하는 대문자 26개, 소문자 26개, 숫자 9개만으로도 경우의 수가 많지만 중복된 토큰이 발생할 수 있다고 가정한다.
뿌리기 건에 대해 유효하지 않게 되었을 때 계속해서 데이터베이스에 중복체크 요청을 할 수 없으므로 방 아이디에 토큰은 유효하다고 가정한다.
방 아이디와 토큰으로 뿌리기 요청은 유니크하고 데이터베이스 내에서는 관리용 아이디를 생성한다.

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
personnel|Integer|받을 수 있는 요청자 수|TRUE|x|1 이상
money|Long|뿌린 금액|TRUE|x|

header name|value type|의미|required|default
--------------|------------|-----|-----|------
X-USER-ID|String|요청자 아이디|TRUE|x
X-ROOM-ID|String|방 아이디|TRUE|x

### Response
field name|value type|의미 
----------|------------|---
code|Integer|응답 코드 
msg|String|응답 결과 
data|String| 토큰

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
field name|value type|의미
----------|-----------|----
code|Integer|응답 코드
msg|String|응답 결과
data|Long|받은 금액

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
field name|value type|의미 
----------|-----------|---
code|Integer|응답 코드 
msg|String|응답 결과 
data|SprayInfo Object| 받은 현황

## Response Objects
### Spray
name|type|의미
----|----|----
id|Integer|뿌리기 건의 아이디
token|String|뿌리기 건의 토큰
requester|String|뿌리기 건을 요청한 사용자
roomId|String|뿌리기 건을 요청한 방 아이디
amountOfMoney|Long|뿌린 금액
personnel|Integer|받을 인원
initDate|Long|뿌린 날

### Receipt
name|type|의미
----|----|----
receiptId|Integer|받은 건의 아이디
token|String|뿌리기 건의 토큰
roomId|String|뿌리기를 요청한 방 아이디
recipient|String|받은 사용자의 아이디
money|Long|받을 금액
initDate|Long|받은 날
status|Boolean|받은 상태
receiptOrder|Integet|받을 순서

### SprayInfo
name|type|의미
---|---|---
InitDate|Long|뿌린 날
amountOfMoney|Long|뿌린 금액
totalReceived|Long|받은 총 금액
receivedInfoList|ReceivedInfo[]|받은 정보

#### ReceivedInfo 
name|type|의미
receipient|String|받은 사람
receivedMondy|Long|받은 금액

## Response Msgs

code | msg | 의미
-----|----|----
200|success | 성공 
500|fail | 실패
501|not valid|여러 가지 사유로 유효하지 않을 경우
502|time over|시간 초과
503|already taken|이미 받았을 경우
504|all taken|이미 전부 받았을 경우
505|no authority|권한이 없는 경우




    
