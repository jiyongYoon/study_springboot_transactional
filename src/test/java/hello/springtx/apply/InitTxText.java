package hello.springtx.apply;

import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@SpringBootTest
public class InitTxText {

    @Autowired
    Hello hello;

    @Test
    void go() {
        // 초기화 코드는 스프링이 초기화 시점에 호출함.
    }

    @TestConfiguration
    static class InitTxTestConfig {
        @Bean
        Hello hello() {
            return new Hello();
        }
    }

    @Slf4j
    static class Hello {

        // AOP 적용보다 PostConstruct가 순서가 먼저다
        @PostConstruct
        @Transactional
        public void initV1() {
            log.info("Hello init @PostConstruct");
            boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("@PostConstruct tx activate={}", isActive);
        }

        // 스프링 컨테이너가 다 뜬 후에 생기는 Event
        @EventListener(ApplicationReadyEvent.class)
        @Transactional
        public void initV2() {
            log.info("Hello init @EventListener");
            boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("@EventListener tx activate={}", isActive);
        }
    }

}
