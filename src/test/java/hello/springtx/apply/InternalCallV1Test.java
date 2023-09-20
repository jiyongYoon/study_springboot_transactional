package hello.springtx.apply;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@SpringBootTest
public class InternalCallV1Test {

    @Autowired
    CallService callService; // 프록시가 주입이 되게 됨

    @Test
    void printProxy() {
        log.info("callService class={}", callService.getClass());
    }

    @Test
    void internalCall() {
        callService.internal(); // callService의 프록시 객체가 Transactional을 시작하기 위해 먼저 Get Transaction을 시작하고,
        // 나머지 로직은 실제 대상 객체에게 위임한다.
        /*
        2023-09-21 00:10:32.614 TRACE 30176 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Getting transaction for [hello.springtx.apply.InternalCallV1Test$CallService.internal]
        2023-09-21 00:10:32.628  INFO 30176 --- [    Test worker] h.s.a.InternalCallV1Test$CallService     : call internal
        2023-09-21 00:10:32.628  INFO 30176 --- [    Test worker] h.s.a.InternalCallV1Test$CallService     : tx activate=true
        2023-09-21 00:10:32.629  INFO 30176 --- [    Test worker] h.s.a.InternalCallV1Test$CallService     : tx readOnly=false
        2023-09-21 00:10:32.629 TRACE 30176 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Completing transaction for [hello.springtx.apply.InternalCallV1Test$CallService.internal]
         */

    }

    @Test
    void externalCall() {
        callService.external();
        /*
        2023-09-21 00:09:57.324  INFO 40628 --- [    Test worker] h.s.a.InternalCallV1Test$CallService     : call external
        2023-09-21 00:09:57.325  INFO 40628 --- [    Test worker] h.s.a.InternalCallV1Test$CallService     : tx activate=false
        2023-09-21 00:09:57.325  INFO 40628 --- [    Test worker] h.s.a.InternalCallV1Test$CallService     : tx readOnly=false
        2023-09-21 00:09:57.325  INFO 40628 --- [    Test worker] h.s.a.InternalCallV1Test$CallService     : call internal
        2023-09-21 00:09:57.325  INFO 40628 --- [    Test worker] h.s.a.InternalCallV1Test$CallService     : tx activate=false
        2023-09-21 00:09:57.325  INFO 40628 --- [    Test worker] h.s.a.InternalCallV1Test$CallService     : tx readOnly=false
        */
    }

    @TestConfiguration
    static class InternalCallV1TestConfig {

        @Bean
        CallService callService() {
            return new CallService();
        }
    }

    @Slf4j
    static class CallService { // 외부 클래스라고 생각하면 됨

        public void external() {
            log.info("call external");
            printTxInfo();
            internal();
            // 프록시 객체에서는 실제 InternalCallTest 객체의 external()이 호출되었고,
            // 이 internal() 메서드는 실제 대상 객체의 내부에서 호출되기 때문에 프록시 객체를 거치지 않게 된다.
            // Transactional이 적용되려면 프록시를 타야되는데?? -> 그래서 적용이 안되는거다.

        }

        @Transactional
        public void internal() {
            log.info("call internal");
            printTxInfo();
        }
        private void printTxInfo() {
            boolean txActivate = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx activate={}", txActivate);
            boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
            log.info("tx readOnly={}", readOnly);
        }
    }


}
