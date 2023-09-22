package hello.springtx.exception;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class RollbackTest {

    @Autowired
    RollbackService rollbackService;

    @Test
    void runtimeException() {
        Assertions.assertThatThrownBy(() -> rollbackService.runtimeException())
            .isInstanceOf(RuntimeException.class);
        /*
        2023-09-21 23:05:48.241 DEBUG 37308 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Creating new transaction with name [hello.springtx.exception.RollbackTest$RollbackService.runtimeException]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
        2023-09-21 23:05:48.328 DEBUG 37308 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(349708076<open>)] for JPA transaction
        2023-09-21 23:05:48.336 DEBUG 37308 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@1b465fa9]
        2023-09-21 23:05:48.336 TRACE 37308 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Getting transaction for [hello.springtx.exception.RollbackTest$RollbackService.runtimeException]
        2023-09-21 23:05:48.349  INFO 37308 --- [    Test worker] h.s.e.RollbackTest$RollbackService       : call runtimeException
        2023-09-21 23:05:48.349 TRACE 37308 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Completing transaction for [hello.springtx.exception.RollbackTest$RollbackService.runtimeException] after exception: java.lang.RuntimeException
        2023-09-21 23:05:48.350 DEBUG 37308 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction rollback
        2023-09-21 23:05:48.350 DEBUG 37308 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Rolling back JPA transaction on EntityManager [SessionImpl(349708076<open>)]
        2023-09-21 23:05:48.353 DEBUG 37308 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Closing JPA EntityManager [SessionImpl(349708076<open>)] after transaction
         */
    }

    @Test
    void checkedException() {
        Assertions.assertThatThrownBy(() -> rollbackService.checkedException())
            .isInstanceOf(MyException.class);
        /*
        2023-09-21 23:08:59.606 DEBUG 36928 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Creating new transaction with name [hello.springtx.exception.RollbackTest$RollbackService.checkedException]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
        2023-09-21 23:08:59.679 DEBUG 36928 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(644323208<open>)] for JPA transaction
        2023-09-21 23:08:59.686 DEBUG 36928 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@752494dd]
        2023-09-21 23:08:59.686 TRACE 36928 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Getting transaction for [hello.springtx.exception.RollbackTest$RollbackService.checkedException]
        2023-09-21 23:08:59.696  INFO 36928 --- [    Test worker] h.s.e.RollbackTest$RollbackService       : call checkedException
        2023-09-21 23:08:59.696 TRACE 36928 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Completing transaction for [hello.springtx.exception.RollbackTest$RollbackService.checkedException] after exception: hello.springtx.exception.RollbackTest$MyException
        2023-09-21 23:08:59.696 DEBUG 36928 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction commit
        2023-09-21 23:08:59.696 DEBUG 36928 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Committing JPA transaction on EntityManager [SessionImpl(644323208<open>)]
        2023-09-21 23:08:59.698 DEBUG 36928 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Closing JPA EntityManager [SessionImpl(644323208<open>)] after transaction
         */
    }

    @Test
    void rollbackFor() {
        Assertions.assertThatThrownBy(() -> rollbackService.rollbackFor())
            .isInstanceOf(MyException.class);
        /*
        2023-09-21 23:09:50.252 DEBUG 37172 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Creating new transaction with name [hello.springtx.exception.RollbackTest$RollbackService.rollbackFor]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT,-hello.springtx.exception.RollbackTest$MyException
        2023-09-21 23:09:50.319 DEBUG 37172 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(1538086235<open>)] for JPA transaction
        2023-09-21 23:09:50.325 DEBUG 37172 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@6c1bebca]
        2023-09-21 23:09:50.325 TRACE 37172 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Getting transaction for [hello.springtx.exception.RollbackTest$RollbackService.rollbackFor]
        2023-09-21 23:09:50.333  INFO 37172 --- [    Test worker] h.s.e.RollbackTest$RollbackService       : call checkedException
        2023-09-21 23:09:50.335 TRACE 37172 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Completing transaction for [hello.springtx.exception.RollbackTest$RollbackService.rollbackFor] after exception: hello.springtx.exception.RollbackTest$MyException
        2023-09-21 23:09:50.335 DEBUG 37172 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction rollback
        2023-09-21 23:09:50.337 DEBUG 37172 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Rolling back JPA transaction on EntityManager [SessionImpl(1538086235<open>)]
        2023-09-21 23:09:50.340 DEBUG 37172 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Closing JPA EntityManager [SessionImpl(1538086235<open>)] after transaction
         */
    }

    @TestConfiguration
    static class RollbackTestConfig {

        @Bean
        RollbackService rollbackService() {
            return new RollbackService();
        }
    }

    @Slf4j
    static class RollbackService {

        // 런타임 예외 발생 : 롤백
        @Transactional
        public void runtimeException() {
            log.info("call runtimeException");
            throw new RuntimeException();
        }

        // 체크 예외 발생 : 커밋
        @Transactional
        public void checkedException() throws MyException {
            log.info("call checkedException");
            throw new MyException();
        }

        // 체크 예외 rollbackFor 지정 : 롤백
        @Transactional(rollbackFor = MyException.class)
        public void rollbackFor() throws MyException {
            log.info("call checkedException");
            throw new MyException();
        }
    }

    static class MyException extends Exception {

    }
}
