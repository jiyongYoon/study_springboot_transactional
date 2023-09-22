package hello.springtx.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class OrderServiceTest {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    void complete() throws NotEnoughMoneyException {
        // given
        Order order = new Order();
        order.setUsername("정상");
        // when
        orderService.order(order);
        // then
        Order findOrder = orderRepository.findById(order.getId()).get();
        assertThat(findOrder.getPayStatus()).isEqualTo("완료");

        /*
        2023-09-21 23:29:13.745 DEBUG 38244 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Creating new transaction with name [hello.springtx.order.OrderService.order]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
        2023-09-21 23:29:13.747 DEBUG 38244 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(349399986<open>)] for JPA transaction
        2023-09-21 23:29:13.753 DEBUG 38244 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@7b7b1448]
        2023-09-21 23:29:13.754 TRACE 38244 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Getting transaction for [hello.springtx.order.OrderService.order]
        2023-09-21 23:29:13.771  INFO 38244 --- [    Test worker] hello.springtx.order.OrderService        : order 호출
        2023-09-21 23:29:13.776 DEBUG 38244 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Found thread-bound EntityManager [SessionImpl(349399986<open>)] for JPA transaction
        2023-09-21 23:29:13.776 DEBUG 38244 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Participating in existing transaction
        2023-09-21 23:29:13.776 TRACE 38244 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Getting transaction for [org.springframework.data.jpa.repository.support.SimpleJpaRepository.save]
        2023-09-21 23:29:13.789 DEBUG 38244 --- [    Test worker] org.hibernate.SQL                        : call next value for hibernate_sequence
        2023-09-21 23:29:13.840 TRACE 38244 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Completing transaction for [org.springframework.data.jpa.repository.support.SimpleJpaRepository.save]
        2023-09-21 23:29:13.841  INFO 38244 --- [    Test worker] hello.springtx.order.OrderService        : 결제 프로세스 진입
        2023-09-21 23:29:13.841  INFO 38244 --- [    Test worker] hello.springtx.order.OrderService        : 정상 승인
        2023-09-21 23:29:13.841  INFO 38244 --- [    Test worker] hello.springtx.order.OrderService        : 결제 프로세스 완료
        2023-09-21 23:29:13.841 TRACE 38244 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Completing transaction for [hello.springtx.order.OrderService.order]
        2023-09-21 23:29:13.841 DEBUG 38244 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction commit
        2023-09-21 23:29:13.842 DEBUG 38244 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Committing JPA transaction on EntityManager [SessionImpl(349399986<open>)]
        2023-09-21 23:29:13.856 DEBUG 38244 --- [    Test worker] org.hibernate.SQL                        : insert into orders (pay_status, username, id) values (?, ?, ?)
        2023-09-21 23:29:13.865 DEBUG 38244 --- [    Test worker] org.hibernate.SQL                        : update orders set pay_status=?, username=? where id=?
        2023-09-21 23:29:13.872 DEBUG 38244 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Closing JPA EntityManager [SessionImpl(349399986<open>)] after transaction
        2023-09-21 23:29:13.874 DEBUG 38244 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Creating new transaction with name [org.springframework.data.jpa.repository.support.SimpleJpaRepository.findById]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT,readOnly
        2023-09-21 23:29:13.874 DEBUG 38244 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(2062492091<open>)] for JPA transaction
        2023-09-21 23:29:13.874 DEBUG 38244 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@50d4775b]
        2023-09-21 23:29:13.875 TRACE 38244 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Getting transaction for [org.springframework.data.jpa.repository.support.SimpleJpaRepository.findById]
        2023-09-21 23:29:13.890 DEBUG 38244 --- [    Test worker] org.hibernate.SQL                        : select order0_.id as id1_0_0_, order0_.pay_status as pay_stat2_0_0_, order0_.username as username3_0_0_ from orders order0_ where order0_.id=?
        2023-09-21 23:29:13.908 TRACE 38244 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Completing transaction for [org.springframework.data.jpa.repository.support.SimpleJpaRepository.findById]
        2023-09-21 23:29:13.908 DEBUG 38244 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction commit
        2023-09-21 23:29:13.908 DEBUG 38244 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Committing JPA transaction on EntityManager [SessionImpl(2062492091<open>)]
        2023-09-21 23:29:13.908 DEBUG 38244 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Closing JPA EntityManager [SessionImpl(2062492091<open>)] after transaction
         */
    }

    @Test
    void runtimeException() throws NotEnoughMoneyException {
        // given
        Order order = new Order();
        order.setUsername("예외");
        // when
        assertThatThrownBy(() -> orderService.order(order))
            .isInstanceOf(RuntimeException.class);
        // then
        Optional<Order> optionalOrder = orderRepository.findById(order.getId());
        assertThat(optionalOrder).isEmpty();
        /*
        2023-09-21 23:38:41.705 DEBUG 34444 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Creating new transaction with name [hello.springtx.order.OrderService.order]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
        2023-09-21 23:38:41.706 DEBUG 34444 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(941194882<open>)] for JPA transaction
        2023-09-21 23:38:41.716 DEBUG 34444 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@70b1028d]
        2023-09-21 23:38:41.716 TRACE 34444 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Getting transaction for [hello.springtx.order.OrderService.order]
        2023-09-21 23:38:41.735  INFO 34444 --- [    Test worker] hello.springtx.order.OrderService        : order 호출
        2023-09-21 23:38:41.743 DEBUG 34444 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Found thread-bound EntityManager [SessionImpl(941194882<open>)] for JPA transaction
        2023-09-21 23:38:41.743 DEBUG 34444 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Participating in existing transaction
        2023-09-21 23:38:41.743 TRACE 34444 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Getting transaction for [org.springframework.data.jpa.repository.support.SimpleJpaRepository.save]
        2023-09-21 23:38:41.762 DEBUG 34444 --- [    Test worker] org.hibernate.SQL                        : call next value for hibernate_sequence
        2023-09-21 23:38:41.836 TRACE 34444 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Completing transaction for [org.springframework.data.jpa.repository.support.SimpleJpaRepository.save]
        2023-09-21 23:38:41.837  INFO 34444 --- [    Test worker] hello.springtx.order.OrderService        : 결제 프로세스 진입
        2023-09-21 23:38:41.837  INFO 34444 --- [    Test worker] hello.springtx.order.OrderService        : 시스템 예외 발생
        2023-09-21 23:38:41.837 TRACE 34444 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Completing transaction for [hello.springtx.order.OrderService.order] after exception: java.lang.RuntimeException: 시스템 예외
        2023-09-21 23:38:41.838 DEBUG 34444 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction rollback
        2023-09-21 23:38:41.839 DEBUG 34444 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Rolling back JPA transaction on EntityManager [SessionImpl(941194882<open>)]
        2023-09-21 23:38:41.846 DEBUG 34444 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Closing JPA EntityManager [SessionImpl(941194882<open>)] after transaction
        2023-09-21 23:38:41.868 DEBUG 34444 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Creating new transaction with name [org.springframework.data.jpa.repository.support.SimpleJpaRepository.findById]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT,readOnly
        2023-09-21 23:38:41.868 DEBUG 34444 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(1238173945<open>)] for JPA transaction
        2023-09-21 23:38:41.868 DEBUG 34444 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@43549c6c]
        2023-09-21 23:38:41.869 TRACE 34444 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Getting transaction for [org.springframework.data.jpa.repository.support.SimpleJpaRepository.findById]
        2023-09-21 23:38:41.883 DEBUG 34444 --- [    Test worker] org.hibernate.SQL                        : select order0_.id as id1_0_0_, order0_.pay_status as pay_stat2_0_0_, order0_.username as username3_0_0_ from orders order0_ where order0_.id=?
        2023-09-21 23:38:41.891 TRACE 34444 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Completing transaction for [org.springframework.data.jpa.repository.support.SimpleJpaRepository.findById]
        2023-09-21 23:38:41.892 DEBUG 34444 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction commit
        2023-09-21 23:38:41.892 DEBUG 34444 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Committing JPA transaction on EntityManager [SessionImpl(1238173945<open>)]
        2023-09-21 23:38:41.892 DEBUG 34444 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Closing JPA EntityManager [SessionImpl(1238173945<open>)] after transaction
         */
    }

    @Test
    void notEnoughMoneyException() {
        // given
        Order order = new Order();
        order.setUsername("잔고부족");
        // when
        try {
            orderService.order(order);
        } catch (NotEnoughMoneyException e) {
            log.info("고객에게 잔고 부족을 알리고 별도의 계좌로 입금하도록 안내");
        }

        // then
        Order findOrder = orderRepository.findById(order.getId()).get();
        assertThat(findOrder.getPayStatus()).isEqualTo("대기");
        /*
        2023-09-21 23:41:32.980 DEBUG 38028 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Creating new transaction with name [hello.springtx.order.OrderService.order]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
        2023-09-21 23:41:32.980 DEBUG 38028 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(1120514542<open>)] for JPA transaction
        2023-09-21 23:41:32.988 DEBUG 38028 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@6f6a2ac8]
        2023-09-21 23:41:32.989 TRACE 38028 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Getting transaction for [hello.springtx.order.OrderService.order]
        2023-09-21 23:41:33.005  INFO 38028 --- [    Test worker] hello.springtx.order.OrderService        : order 호출
        2023-09-21 23:41:33.010 DEBUG 38028 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Found thread-bound EntityManager [SessionImpl(1120514542<open>)] for JPA transaction
        2023-09-21 23:41:33.010 DEBUG 38028 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Participating in existing transaction
        2023-09-21 23:41:33.010 TRACE 38028 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Getting transaction for [org.springframework.data.jpa.repository.support.SimpleJpaRepository.save]
        2023-09-21 23:41:33.021 DEBUG 38028 --- [    Test worker] org.hibernate.SQL                        : call next value for hibernate_sequence
        2023-09-21 23:41:33.095 TRACE 38028 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Completing transaction for [org.springframework.data.jpa.repository.support.SimpleJpaRepository.save]
        2023-09-21 23:41:33.095  INFO 38028 --- [    Test worker] hello.springtx.order.OrderService        : 결제 프로세스 진입
        2023-09-21 23:41:33.096  INFO 38028 --- [    Test worker] hello.springtx.order.OrderService        : 잔고 부족 비즈니스 예외 발생
        2023-09-21 23:41:33.096 TRACE 38028 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Completing transaction for [hello.springtx.order.OrderService.order] after exception: hello.springtx.order.NotEnoughMoneyException: 잔고가 부족합니다.
        2023-09-21 23:41:33.096 DEBUG 38028 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction commit
        2023-09-21 23:41:33.097 DEBUG 38028 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Committing JPA transaction on EntityManager [SessionImpl(1120514542<open>)]
        2023-09-21 23:41:33.120 DEBUG 38028 --- [    Test worker] org.hibernate.SQL                        : insert into orders (pay_status, username, id) values (?, ?, ?)
        2023-09-21 23:41:33.129 DEBUG 38028 --- [    Test worker] org.hibernate.SQL                        : update orders set pay_status=?, username=? where id=?
        2023-09-21 23:41:33.139 DEBUG 38028 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Closing JPA EntityManager [SessionImpl(1120514542<open>)] after transaction
        2023-09-21 23:41:33.140  INFO 38028 --- [    Test worker] hello.springtx.order.OrderServiceTest    : 고객에게 잔고 부족을 알리고 별도의 계좌로 입금하도록 안내
        2023-09-21 23:41:33.142 DEBUG 38028 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Creating new transaction with name [org.springframework.data.jpa.repository.support.SimpleJpaRepository.findById]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT,readOnly
        2023-09-21 23:41:33.143 DEBUG 38028 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Opened new EntityManager [SessionImpl(2106418971<open>)] for JPA transaction
        2023-09-21 23:41:33.143 DEBUG 38028 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Exposing JPA transaction as JDBC [org.springframework.orm.jpa.vendor.HibernateJpaDialect$HibernateConnectionHandle@28b68067]
        2023-09-21 23:41:33.143 TRACE 38028 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Getting transaction for [org.springframework.data.jpa.repository.support.SimpleJpaRepository.findById]
        2023-09-21 23:41:33.163 DEBUG 38028 --- [    Test worker] org.hibernate.SQL                        : select order0_.id as id1_0_0_, order0_.pay_status as pay_stat2_0_0_, order0_.username as username3_0_0_ from orders order0_ where order0_.id=?
        2023-09-21 23:41:33.179 TRACE 38028 --- [    Test worker] o.s.t.i.TransactionInterceptor           : Completing transaction for [org.springframework.data.jpa.repository.support.SimpleJpaRepository.findById]
        2023-09-21 23:41:33.179 DEBUG 38028 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Initiating transaction commit
        2023-09-21 23:41:33.179 DEBUG 38028 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Committing JPA transaction on EntityManager [SessionImpl(2106418971<open>)]
        2023-09-21 23:41:33.180 DEBUG 38028 --- [    Test worker] o.s.orm.jpa.JpaTransactionManager        : Closing JPA EntityManager [SessionImpl(2106418971<open>)] after transaction
         */
    }
}