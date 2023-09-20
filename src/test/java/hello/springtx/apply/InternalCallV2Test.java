package hello.springtx.apply;


import lombok.RequiredArgsConstructor;
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
public class InternalCallV2Test {

    @Autowired
    CallService callService;

    @Test
    void printProxy() {
        log.info("callService class={}", callService.getClass());
    }

    @Test
    void externalCallV2() {
        callService.external();
        /*
        2023-09-21 00:22:06.691  INFO 17084 --- [    Test worker] h.s.a.InternalCallV2Test$CallService     : call external
        2023-09-21 00:22:06.692  INFO 17084 --- [    Test worker] h.s.a.InternalCallV2Test$CallService     : tx activate=false
        2023-09-21 00:22:06.692  INFO 17084 --- [    Test worker] h.s.a.InternalCallV2Test$CallService     : tx readOnly=false
        2023-09-21 00:22:06.810 TRACE 17084 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Getting transaction for [hello.springtx.apply.InternalCallV2Test$InternalService.internal]
        2023-09-21 00:22:06.826  INFO 17084 --- [    Test worker] h.s.a.InternalCallV2Test$InternalService : call internal
        2023-09-21 00:22:06.827  INFO 17084 --- [    Test worker] h.s.a.InternalCallV2Test$InternalService : tx activate=true
        2023-09-21 00:22:06.828  INFO 17084 --- [    Test worker] h.s.a.InternalCallV2Test$InternalService : tx readOnly=false
        2023-09-21 00:22:06.828 TRACE 17084 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Completing transaction for [hello.springtx.apply.InternalCallV2Test$InternalService.internal]
         */
    }

    @TestConfiguration
    static class InternalCallV1TestConfig {

        @Bean
        CallService callService() {
            return new CallService(internalService());
        }

        @Bean
        InternalService internalService() {
            return new InternalService();
        }
    }

    @Slf4j
    @RequiredArgsConstructor
    static class CallService {

        private final InternalService internalService;

        public void external() {
            log.info("call external");
            printTxInfo();
            internalService.internal();
        }

        private void printTxInfo() {
            boolean txActivate = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx activate={}", txActivate);
            boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
            log.info("tx readOnly={}", readOnly);
        }
    }

    @Slf4j
    static class InternalService {
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
