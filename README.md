# Airdrop REST Server

이 프로젝트는 특정 채팅방에서의 뿌리기 기능을 지원한다.

## 기본 설계

### 데이터베이스

이 프로젝트는 아래 두개의 테이블을 사용한다.

* airdrop_event
* airdrop_recipient

airdrop_event는 뿌리기 이벤트에 대한 정보를 저장하고, airdrop_recipient를 각 사용자가 받아갈 금액들이 저장된다. 따라서 airdrop_event와 airdrop_recipient는 1:N의 관계이며, airdrop_recipient가 foreign key를 갖고 있다.

> 단일 테이블을 사용할 수도 있으나, 공간 낭비를 줄이기 위해 두개의 테이블을 정의하였다.

### 뿌리기

뿌리기는 airdrop_event에 공통적인 정보를 저장하고, airdrop_recipient에 요청한 인원수만큼 금액을 분배하여 저장한다. 각 금액은 랜덤방식으로 결정된다.

### 받기

여러 인스턴스에서 접근 가능해야 하기 때문에 airdrop_recipient에 대한 SELECT...FOR UPDATE 구문을 사용하여 Row Lock으로 정합성을 보장한다.

> (Oracle이 아닌) 보통의 데이터베이스들은 시퀀스 객체나 SKIP LOCKED 구문을 지원하지 않기 때문에 동시성이 떨어져 한번에 하나의 사용자만 받기를 수행할 수 있다.

### 조회하기

두 테이블을 조회하여 정보를 취합하고 사용자에게 반환한다.

## 빌드

```bash
make build
```

## 실행

1. 데이터베이스 설치

이 프로젝트를 실행시키기 위해선 먼저 MySQL 데이터베이스가 설치되어 있어야 한다. 설치가 완료되면 아래 명령어를 수행하여 스키마를 생성한다.

```bash
sudo mysql -u root -p
```
```sql
source ./scripts/ddl.sql
```

스키마를 생성한 다음엔 테스트 계정으로 접속하여 스키마가 제대로 생성되었는지 확인한다.

```bash
mysql -u test -p
```
```sql
use test_db
show tables;
```

2. 설정 편집

src/main/resources 디렉토리에 있는 application.properties 파일을 열어 데이터베이스 접속 정보를 수정한다.

3. 서버 실행

```bash
make run
```

## 배포

아래 두가지 형식으로 배포할 수 있다.

### JAR

```bash
make dist
```

위 명령어를 수행하면 airdrop-jar-$(VERSION).tar 압축파일이 생성된다. 이 파일을 적당한 위치에 풀고, config/application.properties를 적절히 수정한 후 run_jar.sh을 수행시키면 된다.

### Docker

```bash
make docker-dist
```

위 명령어를 수행하면 airdrop-docker-$(VERSION).tar 압축파일이 생성된다. 이 파일을 적당한 위치에 풀고, config/application.properties를 적절히 수정한 후 run_docker.sh을 수행시키면 된다.
