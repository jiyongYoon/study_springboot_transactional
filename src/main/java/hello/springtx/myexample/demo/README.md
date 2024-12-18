# Transactional과 Requires_New 테스트


> **정리** 
> 
> 1. `물리 트랜잭션`이 커밋되기 위해서는 내부의 '모든' `논리 트랜잭션`이 커밋되어야 한다.
> 2. `Commit / Rollback`의 작업은 트랜잭션을 `생성`한 곳에서만 할 수 있다. (즉, 물리 트랜잭션이 생성된 곳에서만)
> 3. 기존 tx에 참여한 tx은 `Commit / Rollback` 대신 기존 tx에 `rollbackOnly` 마크를 한다. 
> 4. commit을 시도하는 tx가 `rollbackOnly` 마크가 되어 있다면 `UnexpectedRollbackException`을 던진다.
---

기존에 실무에서 발생한 내용으로 어떻게 Spring Transaction을 적용할지 고민하면서 테스트 했던 내용이 있었다.
[(기존에 했던 테스트 보기)](/hello/springtx/myexample/README.md)

집에서 테스트 후 실무에 적용하려고 했는데, 실제 코드에서는 새로운 문제를 맞게 되었다.

## 문제
문제 상황을 도식화 하였다.
![image](https://github.com/jiyongYoon/study_springboot_transactional/assets/98104603/cc213e81-7f98-4806-9064-6f3b789abd7e)
`Transaction A` 의 데이터는 `Transaction B`와 물리적으로 끊어져 있으니 `Transaction B`와는 상관없이 `Commit` 되길 기대했으나, 결과는 실패했다.

## 일단 해결

일단 원인은 애석하게도, `call B`를 `try - catch`로 감싸주지 않았기 때문이었다. 기존에 테스트할때는 그 부분을 감싸주어 문제없이 통과가 되었었다.
그러나 실무에서는, 조금 더 논리적으로 코드를 구성하다보니 그림과 같은 상황이 발생하였다.

대체 왜 그런것일까?

## 근본 원인

일단, 트랜잭션에서의 대원칙을 되짚고 넘어가자.

> 1. `물리 트랜잭션`이 커밋되기 위해서는 내부의 '모든' `논리 트랜잭션`이 커밋되어야 한다.
> 2. `Commit / Rollback`의 작업은 트랜잭션을 `생성`한 곳에서만 할 수 있다. (즉, 물리 트랜잭션이 생성된 곳에서만)

여기서 말하는 `물리 트랜잭션`은 커넥션과 같은 의미라고 생각하면 되고, `논리 트랜잭션`은 새로 생성되는 모든 트랜잭션을 말한다.

스프링의 propagation 옵션 중에서
1. `REQUIRED`은 기존 트랜잭션이 없으면 `물리 트랜잭션`이 생성되며, 있으면 기존 `물리 트랜잭션`에 참여 하고 새로운 `논리 트랜잭션`만 생긴다.
2. `REQUIRES_NEW` 옵션은 새로운 `물리 트랜잭션`이 생성된다.

그러면, 문제 상황에서는 `Transaction B`는 별개의 물리 트랜잭션인데 왜 `Transaction A`가 커밋되지 않은걸까?

좀 전에 해결책에서 `call B`를 `try - catch`로 감싸주면 해결된다고 했다. 그 말은, `call B`에서 어떤 Exception이 발생해서 A에 영향을 준다는 뜻이다.
C 에서 발생한 Exception은 B에서 처리되었으니 원인은 아니다.

### 원인은 `UnexpectedRollbackException`이었다.

이 Exception은 `rollbackOnly`로 마크되어있는 트랜잭션을 커밋하려고 시도할 때 발생한다. 위 그림에서는 `Transaction B`가 `C Exception`에 의해 `rollbackOnly` 마크가 되었는데, B에서 예외를 처리해버렸기 때문에 Commit을 시도하게 됐고, 그래서 발생한 Exception이 A에 영향을 주었던 것이다.

트랜잭션 코드를 디버깅한 내용은 [링크](https://www.notion.so/jyyoon0615/Transaction-ce77419aec19410291260921dbc0c7ec?pvs=4)를 클릭하면 확인해볼 수 있다.

테스트 코드(test.java.hello.springtx.myexample.MyExampleTest2)와 함께 도식화 한 그림을 확인하면 이해가 더 쉬울 것이다.

![image](https://github.com/jiyongYoon/study_springboot_transactional/assets/98104603/7b998f7a-c221-4ea7-a910-52265e9f4ae5)


---
### 참고자료
[Spring, REQUIRED, REQUIRES_NEW 옵션과 Try-Catch](https://kth990303.tistory.com/388) <br>
[응? 이게 왜 롤백되는거지? | 우아한형제들 기술블로그](https://techblog.woowahan.com/2606/) <br>
[Springboot - Transaction](https://blog.breakingthat.com/2018/04/03/springboot-transaction-%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98/) <br>
