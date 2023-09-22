package hello.springtx.propagation;

import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

@Slf4j
@SpringBootTest
public class BasicTxTest {

    /** [트랜잭션 전파와 롤백 정리] <br>
     * 1. 트랜잭션은 '논리' 트랜잭션과 '물리' 트랜잭션 두가지로 개념을 나눌 수 있다. <br>
     * +---1-1. '논리' 트랜잭션은 트랜잭션 매니저에서 getTransaction(autocommit false) 가 되고 .commit()이 호출되는 한 단위다. <br>
     * +---1-2. '물리' 트랜잭션은 실제로 트랜잭션이 시작된 후 커밋이 되어 DB에 반영이 되는 한 단위다. <br>
     * +---1-3. '물리' 트랜잭션이 커밋되려면 '모든' 논리 트랜잭션이 커밋되어야 한다. <br>
     * 2. 트랜잭션이 시작된 후에 커밋되기 전에 새로운 트랜잭션이 시작되면 Spring은 기존 트랜잭션에 새로운 트랜잭션을 참여시키는 것을 기본 설정(REQUIRED)으로 가져간다. <br>
     * +---2-1. 이 때 두 '논리' 트랜잭션이 '물리' 트랜잭션으로 묶이게 된다. <br>
     * 3. '물리' 트랜잭션을 '획특'하거나 '커밋'하는 '논리' 트랜잭션은 isNew = True인 트랜잭션, 즉, 새롭게 생긴 트랜잭션이다. <br>
     *  <br>
     * 위 원칙들에 따라 아래와 같은 동작이 진행된다. (단, 기본설정인 REQUIRED에서!!) <br>
     * => isNew = true 트랜잭션이 롤백이 되면 -> 롤백이 된다. <br>
     * => isNew = false 트랜잭션이 롤백이 되면 -> 트랜잭션에 'rollback-only' 마킹을 한다. => isNew = true 트랜잭션이 커밋을 호출할 때, 이 마킹이 되어 있으면 롤백이 된다. <br>
     * <br>
     * 기본설정이 아닌 REQUIRES_NEW에서는? <br>
     * -> 신규 트랜잭션이 생성된다. 따라서 데이터베이스 커넥션이 하나 더 생성해서 커넥션을 물고 있게 된다. 나머지는 위 원칙을 그대로 따른다. <br>
     * -> 커넥션 풀에 개수가 부족할 수 있는지 확인이 되어야 한다. <br>
     * 기타 다른 옵션들: SUPPORT, NOT_SUPPORT, MANDATORY, NEVER, NESTED 등이 있으나, 실제 실무에서는 거의 사용되지는 않을 것...
     */

    @Autowired
    PlatformTransactionManager txManager;

    @TestConfiguration
    static class Config {
        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }
    }

    @Test
    void commit() {
        log.info("트랜잭션 시작");
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션 커밋 시작");
        txManager.commit(status);
        log.info("트랜잭션 커밋 완료");
        /*
        2023-09-22 22:42:58.097  INFO 41408 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 트랜잭션 시작
        2023-09-22 22:42:58.099 DEBUG 41408 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
        2023-09-22 22:42:58.099 DEBUG 41408 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@65112445 wrapping conn0: url=jdbc:h2:mem:82af14a7-c950-4042-bfeb-7e1a097a7386 user=SA] for JDBC transaction
        2023-09-22 22:42:58.103 DEBUG 41408 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@65112445 wrapping conn0: url=jdbc:h2:mem:82af14a7-c950-4042-bfeb-7e1a097a7386 user=SA] to manual commit
        2023-09-22 22:42:58.104  INFO 41408 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 트랜잭션 커밋 시작
        2023-09-22 22:42:58.104 DEBUG 41408 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Initiating transaction commit
        2023-09-22 22:42:58.104 DEBUG 41408 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Committing JDBC transaction on Connection [HikariProxyConnection@65112445 wrapping conn0: url=jdbc:h2:mem:82af14a7-c950-4042-bfeb-7e1a097a7386 user=SA]
        2023-09-22 22:42:58.108 DEBUG 41408 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@65112445 wrapping conn0: url=jdbc:h2:mem:82af14a7-c950-4042-bfeb-7e1a097a7386 user=SA] after transaction
        2023-09-22 22:42:58.108  INFO 41408 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 트랜잭션 커밋 완료
         */
    }

    @Test
    void rollback() {
        log.info("트랜잭션 시작");
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션 롤백 시작");
        txManager.rollback(status);
        log.info("트랜잭션 롤백 완료");
        /*
        2023-09-22 22:45:08.057  INFO 39104 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 트랜잭션 시작
        2023-09-22 22:45:08.061 DEBUG 39104 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
        2023-09-22 22:45:08.061 DEBUG 39104 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@1013957837 wrapping conn0: url=jdbc:h2:mem:8fb39821-f3ab-4a64-9939-33f113736fd1 user=SA] for JDBC transaction
        2023-09-22 22:45:08.065 DEBUG 39104 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@1013957837 wrapping conn0: url=jdbc:h2:mem:8fb39821-f3ab-4a64-9939-33f113736fd1 user=SA] to manual commit
        2023-09-22 22:45:08.066  INFO 39104 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 트랜잭션 롤백 시작
        2023-09-22 22:45:08.066 DEBUG 39104 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Initiating transaction rollback
        2023-09-22 22:45:08.066 DEBUG 39104 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Rolling back JDBC transaction on Connection [HikariProxyConnection@1013957837 wrapping conn0: url=jdbc:h2:mem:8fb39821-f3ab-4a64-9939-33f113736fd1 user=SA]
        2023-09-22 22:45:08.069 DEBUG 39104 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@1013957837 wrapping conn0: url=jdbc:h2:mem:8fb39821-f3ab-4a64-9939-33f113736fd1 user=SA] after transaction
        2023-09-22 22:45:08.070  INFO 39104 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 트랜잭션 롤백 완료
         */
    }

    @Test
    void double_commit() {
        log.info("트랜잭션1 시작");
        TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션1 커밋 시작");
        txManager.commit(tx1);
        log.info("트랜잭션1 커밋 완료");

        log.info("트랜잭션2 시작");
        TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션2 커밋 시작");
        txManager.commit(tx2);
        log.info("트랜잭션2 커밋 완료");
        /*
        2023-09-22 22:47:43.849  INFO 32984 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 트랜잭션1 시작
        2023-09-22 22:47:43.851 DEBUG 32984 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
        2023-09-22 22:47:43.852 DEBUG 32984 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@187571699 wrapping conn0: url=jdbc:h2:mem:3c77ebac-ad43-4d3e-b308-ae0f3328ca96 user=SA] for JDBC transaction
        2023-09-22 22:47:43.854 DEBUG 32984 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@187571699 wrapping conn0: url=jdbc:h2:mem:3c77ebac-ad43-4d3e-b308-ae0f3328ca96 user=SA] to manual commit
        2023-09-22 22:47:43.855  INFO 32984 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 트랜잭션1 커밋 시작
        2023-09-22 22:47:43.855 DEBUG 32984 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Initiating transaction commit
        2023-09-22 22:47:43.855 DEBUG 32984 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Committing JDBC transaction on Connection [HikariProxyConnection@187571699 wrapping conn0: url=jdbc:h2:mem:3c77ebac-ad43-4d3e-b308-ae0f3328ca96 user=SA]
        2023-09-22 22:47:43.858 DEBUG 32984 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@187571699 wrapping conn0: url=jdbc:h2:mem:3c77ebac-ad43-4d3e-b308-ae0f3328ca96 user=SA] after transaction
        2023-09-22 22:47:43.858  INFO 32984 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 트랜잭션1 커밋 완료
        2023-09-22 22:47:43.858  INFO 32984 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 트랜잭션2 시작
        2023-09-22 22:47:43.859 DEBUG 32984 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
        2023-09-22 22:47:43.860 DEBUG 32984 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@2022543673 wrapping conn0: url=jdbc:h2:mem:3c77ebac-ad43-4d3e-b308-ae0f3328ca96 user=SA] for JDBC transaction
        2023-09-22 22:47:43.860 DEBUG 32984 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@2022543673 wrapping conn0: url=jdbc:h2:mem:3c77ebac-ad43-4d3e-b308-ae0f3328ca96 user=SA] to manual commit
        2023-09-22 22:47:43.860  INFO 32984 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 트랜잭션2 커밋 시작
        2023-09-22 22:47:43.860 DEBUG 32984 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Initiating transaction commit
        2023-09-22 22:47:43.860 DEBUG 32984 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Committing JDBC transaction on Connection [HikariProxyConnection@2022543673 wrapping conn0: url=jdbc:h2:mem:3c77ebac-ad43-4d3e-b308-ae0f3328ca96 user=SA]
        2023-09-22 22:47:43.860 DEBUG 32984 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@2022543673 wrapping conn0: url=jdbc:h2:mem:3c77ebac-ad43-4d3e-b308-ae0f3328ca96 user=SA] after transaction
        2023-09-22 22:47:43.861  INFO 32984 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 트랜잭션2 커밋 완료
         */

        /*
        로그를 보면 두 Transaction 모두 wrapping conn0 이라고 하며 같은 커넥션을 사용한다.
        다만, 커넥션이 풀에서 가져올 때 conn0 로 래핑해서 프록시 커넥션을 가져다주는데,
        내부 실제 커넥션의 주소를 확인하면 커넥션 풀에서 획득한 커넥션을 구분할 수 있다.

        tx1: HikariProxyConnection@187571699 wrapping conn0
        tx2: HikariProxyConnection@2022543673 wrapping conn0

        즉, DB와 연결되는 물리 커넥션은 재사용되었지만, 각각 커넥션 풀에서 커넥션을 새로 조회한 것이라고 볼 수 있다.
         */
    }

    @Test
    void double_commit_rollback() {
        log.info("트랜잭션1 시작");
        TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션1 커밋 시작");
        txManager.commit(tx1);
        log.info("트랜잭션1 커밋 완료");

        log.info("트랜잭션2 시작");
        TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션2 롤백 시작");
        txManager.rollback(tx2);
        log.info("트랜잭션2 롤백 완료");
        /*
        2023-09-22 22:56:42.911  INFO 24596 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 트랜잭션1 시작
        2023-09-22 22:56:42.914 DEBUG 24596 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
        2023-09-22 22:56:42.914 DEBUG 24596 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@1444494906 wrapping conn0: url=jdbc:h2:mem:48c204e4-c038-424b-912f-e98eed4eb44f user=SA] for JDBC transaction
        2023-09-22 22:56:42.917 DEBUG 24596 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@1444494906 wrapping conn0: url=jdbc:h2:mem:48c204e4-c038-424b-912f-e98eed4eb44f user=SA] to manual commit
        2023-09-22 22:56:42.918  INFO 24596 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 트랜잭션1 커밋 시작
        2023-09-22 22:56:42.918 DEBUG 24596 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Initiating transaction commit
        2023-09-22 22:56:42.918 DEBUG 24596 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Committing JDBC transaction on Connection [HikariProxyConnection@1444494906 wrapping conn0: url=jdbc:h2:mem:48c204e4-c038-424b-912f-e98eed4eb44f user=SA]
        2023-09-22 22:56:42.922 DEBUG 24596 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@1444494906 wrapping conn0: url=jdbc:h2:mem:48c204e4-c038-424b-912f-e98eed4eb44f user=SA] after transaction
        2023-09-22 22:56:42.922  INFO 24596 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 트랜잭션1 커밋 완료
        2023-09-22 22:56:42.922  INFO 24596 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 트랜잭션2 시작
        2023-09-22 22:56:42.922 DEBUG 24596 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
        2023-09-22 22:56:42.923 DEBUG 24596 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@1406333164 wrapping conn0: url=jdbc:h2:mem:48c204e4-c038-424b-912f-e98eed4eb44f user=SA] for JDBC transaction
        2023-09-22 22:56:42.923 DEBUG 24596 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@1406333164 wrapping conn0: url=jdbc:h2:mem:48c204e4-c038-424b-912f-e98eed4eb44f user=SA] to manual commit
        2023-09-22 22:56:42.923  INFO 24596 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 트랜잭션2 롤백 시작
        2023-09-22 22:56:42.923 DEBUG 24596 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Initiating transaction rollback
        2023-09-22 22:56:42.923 DEBUG 24596 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Rolling back JDBC transaction on Connection [HikariProxyConnection@1406333164 wrapping conn0: url=jdbc:h2:mem:48c204e4-c038-424b-912f-e98eed4eb44f user=SA]
        2023-09-22 22:56:42.924 DEBUG 24596 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@1406333164 wrapping conn0: url=jdbc:h2:mem:48c204e4-c038-424b-912f-e98eed4eb44f user=SA] after transaction
        2023-09-22 22:56:42.924  INFO 24596 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 트랜잭션2 롤백 완료
         */
    }

    @Test
    void inner_commit() {
        log.info("외부 트랜잭션 시작");
//        2023-09-22 23:09:41.092  INFO 17096 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 외부 트랜잭션 시작
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
//        2023-09-22 23:09:41.093 DEBUG 17096 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
//        2023-09-22 23:09:41.094 DEBUG 17096 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@434495760 wrapping conn0: url=jdbc:h2:mem:a088083c-b58b-4d4e-8b7b-3b9cca2c7a7f user=SA] for JDBC transaction
//        2023-09-22 23:09:41.097 DEBUG 17096 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@434495760 wrapping conn0: url=jdbc:h2:mem:a088083c-b58b-4d4e-8b7b-3b9cca2c7a7f user=SA] to manual commit
        log.info("outer.isNewTransaction()={}", outer.isNewTransaction());
//        2023-09-22 23:09:41.097  INFO 17096 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : outer.isNewTransaction()=true <-- 신규 트랜잭션

        log.info("내부 트랜잭션 시작");
//        2023-09-22 23:09:41.097  INFO 17096 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 내부 트랜잭션 시작
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
//        2023-09-22 23:09:41.097 DEBUG 17096 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Participating in existing transaction <-- 기존 트랜잭션 참여
        log.info("inner.isNewTransaction()={}", inner.isNewTransaction());
//        2023-09-22 23:09:41.100  INFO 17096 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : inner.isNewTransaction()=false <-- 신규 트랜잭션 아님
        log.info("내부 트랜잭션 커밋");
//        2023-09-22 23:09:41.100  INFO 17096 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 내부 트랜잭션 커밋
        txManager.commit(inner);
//        아무것도 안함
        log.info("외부 트랜잭션 커밋");
//        2023-09-22 23:09:41.101  INFO 17096 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 외부 트랜잭션 커밋
        txManager.commit(outer);
//        2023-09-22 23:09:41.101 DEBUG 17096 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Initiating transaction commit
//        2023-09-22 23:09:41.101 DEBUG 17096 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Committing JDBC transaction on Connection [HikariProxyConnection@434495760 wrapping conn0: url=jdbc:h2:mem:a088083c-b58b-4d4e-8b7b-3b9cca2c7a7f user=SA]
//        2023-09-22 23:09:41.102 DEBUG 17096 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@434495760 wrapping conn0: url=jdbc:h2:mem:a088083c-b58b-4d4e-8b7b-3b9cca2c7a7f user=SA] after transaction
    }

    @Test
    void outer_rollback() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("내부 트랜잭션 시작");
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("내부 트랜잭션 커밋");
        txManager.commit(inner);

        log.info("외부 트랜잭션 롤백");
        txManager.rollback(outer);
        /*
        2023-09-22 23:33:17.632  INFO 13496 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 외부 트랜잭션 시작
        2023-09-22 23:33:17.633 DEBUG 13496 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
        2023-09-22 23:33:17.633 DEBUG 13496 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@1827910615 wrapping conn0: url=jdbc:h2:mem:82117e51-c4ea-435f-85a0-0ac514a1d55e user=SA] for JDBC transaction
        2023-09-22 23:33:17.636 DEBUG 13496 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@1827910615 wrapping conn0: url=jdbc:h2:mem:82117e51-c4ea-435f-85a0-0ac514a1d55e user=SA] to manual commit
        2023-09-22 23:33:17.636  INFO 13496 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 내부 트랜잭션 시작
        2023-09-22 23:33:17.636 DEBUG 13496 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Participating in existing transaction
        2023-09-22 23:33:17.637  INFO 13496 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 내부 트랜잭션 커밋
        2023-09-22 23:33:17.639  INFO 13496 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 외부 트랜잭션 롤백
        2023-09-22 23:33:17.640 DEBUG 13496 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Initiating transaction rollback
        2023-09-22 23:33:17.640 DEBUG 13496 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Rolling back JDBC transaction on Connection [HikariProxyConnection@1827910615 wrapping conn0: url=jdbc:h2:mem:82117e51-c4ea-435f-85a0-0ac514a1d55e user=SA]
        2023-09-22 23:33:17.640 DEBUG 13496 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@1827910615 wrapping conn0: url=jdbc:h2:mem:82117e51-c4ea-435f-85a0-0ac514a1d55e user=SA] after transaction
         */
    }

    @Test
    void innter_rollback() {
        log.info("외부 트랜잭션 시작");
//        2023-09-22 23:39:06.517  INFO 35868 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 외부 트랜잭션 시작
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
//        2023-09-22 23:39:06.519 DEBUG 35868 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
//        2023-09-22 23:39:06.519 DEBUG 35868 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@1438780049 wrapping conn0: url=jdbc:h2:mem:73a4d2af-5e4d-4289-9731-15eb808a6d40 user=SA] for JDBC transaction
//        2023-09-22 23:39:06.521 DEBUG 35868 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@1438780049 wrapping conn0: url=jdbc:h2:mem:73a4d2af-5e4d-4289-9731-15eb808a6d40 user=SA] to manual commit

        log.info("내부 트랜잭션 시작");
//        2023-09-22 23:39:06.522  INFO 35868 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 내부 트랜잭션 시작
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
//        2023-09-22 23:39:06.522 DEBUG 35868 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Participating in existing transaction
        log.info("내부 트랜잭션 롤백");
//        2023-09-22 23:39:06.522  INFO 35868 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 내부 트랜잭션 롤백
        txManager.rollback(inner);
//        2023-09-22 23:39:06.525 DEBUG 35868 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Participating transaction failed - marking existing transaction as rollback-only
//        2023-09-22 23:39:06.526 DEBUG 35868 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Setting JDBC transaction [HikariProxyConnection@1438780049 wrapping conn0: url=jdbc:h2:mem:73a4d2af-5e4d-4289-9731-15eb808a6d40 user=SA] rollback-only
        /*
        내부 트랜잭션이 롤백 되면 참여중인 트랜잭션에 'rollback-only'라고 마킹을 하게 됨
        나중에 이 트랜잭션이 commit을 호출하려고 할때, 'rollback-only' 마킹을 통해 commit이 아니라 rollback을 하게 함
         */

        log.info("외부 트랜잭션 커밋");
//        2023-09-22 23:39:06.526  INFO 35868 --- [    Test worker] hello.springtx.propagation.BasicTxTest   : 외부 트랜잭션 커밋
        Assertions.assertThatThrownBy(() -> txManager.commit(outer))
            .isInstanceOf(UnexpectedRollbackException.class);
//        2023-09-22 23:39:06.526 DEBUG 35868 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Global transaction is marked as rollback-only but transactional code requested commit
//        2023-09-22 23:39:06.526 DEBUG 35868 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Initiating transaction rollback
//        2023-09-22 23:39:06.526 DEBUG 35868 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Rolling back JDBC transaction on Connection [HikariProxyConnection@1438780049 wrapping conn0: url=jdbc:h2:mem:73a4d2af-5e4d-4289-9731-15eb808a6d40 user=SA]
//        2023-09-22 23:39:06.527 DEBUG 35868 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@1438780049 wrapping conn0: url=jdbc:h2:mem:73a4d2af-5e4d-4289-9731-15eb808a6d40 user=SA] after transaction
    }

    @Test
    void inner_rollback_requires_new() {
        log.info("외부 트랜잭션 시작");
//        2023-09-23 00:12:56.603  INFO 6176 --- [    Test worker] h.s.p.BasicTxTest_REQUIRES_NEW           : 외부 트랜잭션 시작
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
//        2023-09-23 00:12:56.605 DEBUG 6176 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
//        2023-09-23 00:12:56.606 DEBUG 6176 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@2094728618 wrapping conn0: url=jdbc:h2:mem:881b5a83-ecdf-4b62-871a-41187b021bec user=SA] for JDBC transaction
//        2023-09-23 00:12:56.608 DEBUG 6176 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@2094728618 wrapping conn0: url=jdbc:h2:mem:881b5a83-ecdf-4b62-871a-41187b021bec user=SA] to manual commit
        log.info("outer.isNewTransaction()={}", outer.isNewTransaction());
//        2023-09-23 00:12:56.608  INFO 6176 --- [    Test worker] h.s.p.BasicTxTest_REQUIRES_NEW           : outer.isNewTransaction()=true <-- 신규 트랜잭션 생성

        log.info("내부 트랜잭션 시작");
//        2023-09-23 00:12:56.608  INFO 6176 --- [    Test worker] h.s.p.BasicTxTest_REQUIRES_NEW           : 내부 트랜잭션 시작
        DefaultTransactionAttribute definition = new DefaultTransactionAttribute();
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW); // 기존에 진행되던 트랜잭션을 무시하고 새로운 트랜잭션을 생성함.
        TransactionStatus inner = txManager.getTransaction(definition);
//        2023-09-23 00:12:56.608 DEBUG 6176 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Suspending current transaction, creating new transaction with name [null] <-- 기존 트랜잭션을 잠깐 미뤄두고, 신규 트랜잭션을 생성
//        2023-09-23 00:12:56.609 DEBUG 6176 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@1013957837 wrapping conn1: url=jdbc:h2:mem:881b5a83-ecdf-4b62-871a-41187b021bec user=SA] for JDBC transaction
//        2023-09-23 00:12:56.609 DEBUG 6176 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@1013957837 wrapping conn1: url=jdbc:h2:mem:881b5a83-ecdf-4b62-871a-41187b021bec user=SA] to manual commit
        log.info("inner.isNewTransaction()={}", inner.isNewTransaction());
//        2023-09-23 00:12:56.609  INFO 6176 --- [    Test worker] h.s.p.BasicTxTest_REQUIRES_NEW           : inner.isNewTransaction()=true <-- 신규 트랜잭션 생성

        log.info("내부 트랜잭션 롤백");
//        2023-09-23 00:12:56.609  INFO 6176 --- [    Test worker] h.s.p.BasicTxTest_REQUIRES_NEW           : 내부 트랜잭션 롤백
        txManager.rollback(inner);
//        2023-09-23 00:12:56.610 DEBUG 6176 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Initiating transaction rollback <-- 신규 트랜잭션이기 때문에 rollback이 진행
//        2023-09-23 00:12:56.610 DEBUG 6176 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Rolling back JDBC transaction on Connection [HikariProxyConnection@1013957837 wrapping conn1: url=jdbc:h2:mem:881b5a83-ecdf-4b62-871a-41187b021bec user=SA]
//        2023-09-23 00:12:56.611 DEBUG 6176 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@1013957837 wrapping conn1: url=jdbc:h2:mem:881b5a83-ecdf-4b62-871a-41187b021bec user=SA] after transaction
//        2023-09-23 00:12:56.611 DEBUG 6176 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Resuming suspended transaction after completion of inner transaction <-- 기존 미뤄뒀던 트랜잭션 재시작

        log.info("외부 트랜잭션 커밋");
//        2023-09-23 00:12:56.611  INFO 6176 --- [    Test worker] h.s.p.BasicTxTest_REQUIRES_NEW           : 외부 트랜잭션 커밋
        txManager.commit(outer);
//        2023-09-23 00:12:56.611 DEBUG 6176 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Initiating transaction commit
//        2023-09-23 00:12:56.611 DEBUG 6176 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Committing JDBC transaction on Connection [HikariProxyConnection@2094728618 wrapping conn0: url=jdbc:h2:mem:881b5a83-ecdf-4b62-871a-41187b021bec user=SA]
//        2023-09-23 00:12:56.611 DEBUG 6176 --- [    Test worker] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@2094728618 wrapping conn0: url=jdbc:h2:mem:881b5a83-ecdf-4b62-871a-41187b021bec user=SA] after transaction
    }
}
