package hello.springtx.propagation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

@Slf4j
@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    LogRepository logRepository;

    /**
     * memberService    @Transactional-OFF
     * memberRepository @Transactional-ON
     * logRepository    @Transactional-ON
     */
    @Test
    void outerTxOff_success() {
        // given
        String username = "outerTxOff_success";

        // when
        memberService.joinV1(username);

        // then: 모든 데이터가 정상 저장된다.
        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * memberService    @Transactional-OFF
     * memberRepository @Transactional-ON
     * logRepository    @Transactional-ON, Exception
     */
    @Test
    void outerTxOff_fail() {
        // given
        String username = "로그예외_outerTxOff_fail";

        // when
        assertThatThrownBy(() -> memberService.joinV1(username))
            .isInstanceOf(RuntimeException.class);

        // then: log 데이터는 롤백된다.
        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isEmpty());
    }

    /**
     * memberService    @Transactional-ON
     * memberRepository @Transactional-OFF
     * logRepository    @Transactional-OFF
     */
    @Test
    void singleTx() {
        // given
        String username = "singleTx";

        // when
        memberService.joinV1(username);

        // then: 모든 데이터가 정상 저장된다.
        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * memberService    @Transactional-ON
     * memberRepository @Transactional-ON
     * logRepository    @Transactional-ON
     */
    @Test
    void outerTxOn_success() {
        // given
        String username = "outerTxOn_success";

        // when
        memberService.joinV1(username);

        // then: 모든 데이터가 정상 저장된다.
        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * memberService    @Transactional-ON
     * memberRepository @Transactional-ON
     * logRepository    @Transactional-ON, Exception
     */
    @Test
    void outerTxOn_fail() {
        // given
        String username = "로그예외_outerTxOn_fail";

        // when
        assertThatThrownBy(() -> memberService.joinV1(username))
            .isInstanceOf(RuntimeException.class);

        // then: 모든 데이터가 롤백 된다.
        Assertions.assertTrue(memberRepository.find(username).isEmpty());
        Assertions.assertTrue(logRepository.find(username).isEmpty());
    }

    /**
     * memberService    @Transactional-ON
     * memberRepository @Transactional-ON
     * logRepository    @Transactional-ON, Exception
     */
    @Test
    void recoverException_fail() {
        // given
        String username = "로그예외_recoverException_fail";

        // when
        assertThatThrownBy(() -> memberService.joinV2(username))
            .isInstanceOf(UnexpectedRollbackException.class);
        // 트랜잭션 매니저 입장에서, 외부 트랜잭션은 예외가 없었기 때문에 commit을 호출한다.
        // 그러나 rollback-only 마크가 되어 있기 때문에 기대하지 않던 Rollback이 진행된다.

        // then: 모든 데이터가 롤백 된다. 로그 저장 트랜잭션 예외를 잡아서 처리했는데도 왜?
        // '물리' 트랜잭션이 커밋되려면 '모든' 논리 트랜잭션이 커밋되어야 한다는 원리에 입각하면 커밋이 안되는게 맞다!!
        Assertions.assertTrue(memberRepository.find(username).isEmpty());
        Assertions.assertTrue(logRepository.find(username).isEmpty());
    }

    /**
     * memberService    @Transactional-ON
     * memberRepository @Transactional-ON
     * logRepository    @Transactional-ON(REQUIRES_NEW), Exception
     * memberRepository.save()와 memberService.joinV3()는 예외가 없었기 때문에 해당 물리 트랜잭션은 commit이 잘 수행된다.
     * logRepository.saveRequiresNew()는 새로운 트랜잭션에서 예외가 발생했기 때문에 트랜잭션이 rollback이 된다.
     */
    @Test
    void recoverException_success() {
        // given
        String username = "로그예외_recoverException_success";

        // when
        memberService.joinV3(username);

        // then: member 저장, log 롤백
        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isEmpty());
        /*
        2023-09-23 22:59:07.623 DEBUG 28748 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Creating new transaction with name [hello.springtx.propagation.MemberService.joinV3]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
        2023-09-23 22:59:07.626 DEBUG 28748 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(1642133945<open>)] for JPA transaction
        2023-09-23 22:59:07.632 DEBUG 28748 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@795131d6]
        2023-09-23 22:59:07.632 TRACE 28748 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Getting transaction for [hello.springtx.propagation.MemberService.joinV3]
        2023-09-23 22:59:07.646  INFO 28748 --- [    Test worker] h.springtx.propagation.MemberService     : == memberRepository 호출 시작 ==
        2023-09-23 22:59:07.646 DEBUG 28748 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Found thread-bound EntityManager [SessionImpl(1642133945<open>)] for JPA transaction
        2023-09-23 22:59:07.646 DEBUG 28748 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Participating in existing transaction
        2023-09-23 22:59:07.647 TRACE 28748 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Getting transaction for [hello.springtx.propagation.MemberRepository.save]
        2023-09-23 22:59:07.652  INFO 28748 --- [    Test worker] h.springtx.propagation.MemberRepository  : member 저장
        2023-09-23 22:59:07.656 DEBUG 28748 --- [    Test worker] org.hibernate.SQL                        : call next value for hibernate_sequence
        2023-09-23 22:59:07.719 TRACE 28748 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Completing transaction for [hello.springtx.propagation.MemberRepository.save]
        2023-09-23 22:59:07.719  INFO 28748 --- [    Test worker] h.springtx.propagation.MemberService     : == memberRepository 호출 종료 ==
        2023-09-23 22:59:07.719  INFO 28748 --- [    Test worker] h.springtx.propagation.MemberService     : == logRepository 호출 시작 ==
        2023-09-23 22:59:07.719 DEBUG 28748 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Found thread-bound EntityManager [SessionImpl(1642133945<open>)] for JPA transaction
        2023-09-23 22:59:07.719 DEBUG 28748 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Suspending current transaction, creating new transaction with name [hello.springtx.propagation.LogRepository.saveRequiresNew] <-- 기존 트랜잭션 미룸
        2023-09-23 22:59:07.720 DEBUG 28748 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(574999722<open>)] for JPA transaction
        2023-09-23 22:59:07.720 DEBUG 28748 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@22b10124]
        2023-09-23 22:59:07.720 TRACE 28748 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Getting transaction for [hello.springtx.propagation.LogRepository.saveRequiresNew] <-- 신규 트랜잭션 시작 (REQUIRES_NEW)
        2023-09-23 22:59:07.727  INFO 28748 --- [    Test worker] h.springtx.propagation.LogRepository     : log 저장
        2023-09-23 22:59:07.727 DEBUG 28748 --- [    Test worker] org.hibernate.SQL                        : call next value for hibernate_sequence
        2023-09-23 22:59:07.729  INFO 28748 --- [    Test worker] h.springtx.propagation.LogRepository     : log 저장시 예외 발생
        2023-09-23 22:59:07.734 TRACE 28748 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Completing transaction for [hello.springtx.propagation.LogRepository.saveRequiresNew] after exception: java.lang.RuntimeException: 예외 발생
        2023-09-23 22:59:07.734 DEBUG 28748 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction rollback
        2023-09-23 22:59:07.734 DEBUG 28748 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Rolling back JPA transaction on EntityManager [SessionImpl(574999722<open>)] <-- 해당 트랜잭션은 롤백됨
        2023-09-23 22:59:07.736 DEBUG 28748 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Closing JPA EntityManager [SessionImpl(574999722<open>)] after transaction
        2023-09-23 22:59:07.737 DEBUG 28748 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Resuming suspended transaction after completion of inner transaction <-- 기존 트랜잭션 재시작
        2023-09-23 22:59:07.737  INFO 28748 --- [    Test worker] h.springtx.propagation.MemberService     : log 저장에 실패했습니다. logMessage=로그예외_recoverException_success
        2023-09-23 22:59:07.737  INFO 28748 --- [    Test worker] h.springtx.propagation.MemberService     : 정상 흐름 반환
        2023-09-23 22:59:07.737  INFO 28748 --- [    Test worker] h.springtx.propagation.MemberService     : == logRepository 호출 종료 ==
        2023-09-23 22:59:07.738 TRACE 28748 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Completing transaction for [hello.springtx.propagation.MemberService.joinV3]
        2023-09-23 22:59:07.738 DEBUG 28748 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction commit
        2023-09-23 22:59:07.738 DEBUG 28748 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Committing JPA transaction on EntityManager [SessionImpl(1642133945<open>)]
        2023-09-23 22:59:07.750 DEBUG 28748 --- [    Test worker] org.hibernate.SQL                        : insert into member (username, id) values (?, ?)
        2023-09-23 22:59:07.757 DEBUG 28748 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Closing JPA EntityManager [SessionImpl(1642133945<open>)] after transaction
        2023-09-23 22:59:07.948 DEBUG 28748 --- [    Test worker] org.hibernate.SQL                        : select member0_.id as id1_1_, member0_.username as username2_1_ from member member0_ where member0_.username=?
        2023-09-23 22:59:07.966 DEBUG 28748 --- [    Test worker] org.hibernate.SQL                        : select log0_.id as id1_0_, log0_.message as message2_0_ from log log0_ where log0_.message=?
         */
    }
}