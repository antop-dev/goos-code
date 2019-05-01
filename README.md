[![Issues](https://img.shields.io/github/issues/antop-dev/goos-code.svg)](https://github.com/antop-dev/goos-code/issueshttps://github.com/antop-dev/goos-code/issues)
[![Forks](https://img.shields.io/github/forks/antop-dev/goos-code.svg)](https://github.com/antop-dev/goos-code/network/members)
[![Stars](https://img.shields.io/github/stars/antop-dev/goos-code.svg)](https://github.com/antop-dev/goos-code/stargazers)
[![License](https://img.shields.io/github/license/antop-dev/goos-code.svg)](https://github.com/antop-dev/goos-code/blob/master/LICENSE)
[![Build Status](https://travis-ci.com/antop-dev/goos-code.svg?branch=master)](https://travis-ci.com/antop-dev/goos-code)
[![Coverage Status](https://coveralls.io/repos/github/antop-dev/goos-code/badge.svg?branch=master)](https://coveralls.io/github/antop-dev/goos-code?branch=master)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=antop-dev_goos-code&metric=alert_status)](https://sonarcloud.io/dashboard?id=antop-dev_goos-code)

# goods-code

![](http://www.insightbook.co.kr/wp-content/uploads/2013/06/thumbnail-234x300.jpg)

[테스트 주도 개발로 배우는 객체 지향 설계와 실천](http://www.insightbook.co.kr/book/programming-insight/%ED%85%8C%EC%8A%A4%ED%8A%B8-%EC%A3%BC%EB%8F%84-%EA%B0%9C%EB%B0%9C%EB%A1%9C-%EB%B0%B0%EC%9A%B0%EB%8A%94-%EA%B0%9D%EC%B2%B4-%EC%A7%80%ED%96%A5-%EC%84%A4%EA%B3%84%EC%99%80-%EC%8B%A4%EC%B2%9C) 소스 코드

## Preparation

### 테스트를 위한 계정 생성

아이디|비밀번호
------|--------
auction-item-54321|auction
auction-item-65432|auction
sniper|sniper

Users/Groups

![Imgur](https://i.imgur.com/cyOmaI8.png)

### 동시 접속을 막기 위한 설정

```
p131. 사우스비 온라인처럼 서버는 다수의 열린 연결을 거부하도록 설정돼 있으므로...
```

Server &gt; Server Setting &gt; Resource Policy

![Imgur](https://i.imgur.com/AGS3Wsf.png)

## Issues

```
couldn't setup local SOCKS5 proxy on port 7777: Address already in use: JVM_Bind
```

[openfire - couldn't setup local SOCKS5 proxy on port 7777: Address already in use: JVM_Bind - Stack Overflow](https://stackoverflow.com/questions/10517518/couldnt-setup-local-socks5-proxy-on-port-7777-address-already-in-use-jvm-bind)

내 개발 PC에서 Openfire 서버와 Smack 클라이언트를 같이 사용할 경우 파일 전송용 포트(7777)를 같이 사용하면서 나는 에러이다.

Server &gt; Server Settings &gt; FIle Transfer Settings 에서 포트를 변경하거나 비활성화 한다.

![Imgur](https://i.imgur.com/6vNpAb8.png)

## Reference

[JUnit 5 Tutorial: Running Unit Tests With Maven](https://www.petrikainulainen.net/programming/testing/junit-5-tutorial-running-unit-tests-with-maven/)

[Coveralls.io configuration for maven projects](https://github.com/asciidoctor/asciidoctor/wiki/Coveralls.io-configuration-for-maven-projects)

[Intro to JaCoCo | Baeldung](https://www.baeldung.com/jacoco)

[coveralls-maven-plugin](https://github.com/trautonen/coveralls-maven-plugin)

[Configuring Slack notifications](https://docs.travis-ci.com/user/notifications/#configuring-slack-notifications)

[The Travis Client](https://github.com/travis-ci/travis.rb)

[mockito](https://github.com/mockito/mockito)

[jMock versus Mockito](https://zsoltfabok.com/blog/2010/08/jmock-versus-mockito/)

[Mockito vs EasyMock vs JMockit](https://www.baeldung.com/mockito-vs-easymock-vs-jmockit)

[Skipping a CI Build for non-code changes](https://reflectoring.io/skip-ci-build/)