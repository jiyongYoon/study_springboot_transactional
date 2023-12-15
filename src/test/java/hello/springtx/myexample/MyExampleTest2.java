package hello.springtx.myexample;

import hello.springtx.myexample.demo.AService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class MyExampleTest2 {

    @Autowired
    AService aService;

    @Autowired
    BookRepository bookRepository;

    @AfterEach
    void reset() {
        bookRepository.deleteAll();
    }

    @Test
    @DisplayName("1. a트랜잭션에서_Exception_Catch")
    void a트랜잭션에서_Exception_Catch() {
        try {
            aService.aMethod();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            List<Book> all = bookRepository.findAll();
            for (Book book : all) {
                log.info("책이름={}", book.getName());
            }

            int savedBookCnt = all.size();
            Assertions.assertThat(savedBookCnt).isEqualTo(1);
            if (savedBookCnt > 0) {
                Book savedBook1 = all.get(0);
                Assertions.assertThat(savedBook1.getName()).isEqualTo("A번책");
            }
        }
    }

    @Test
    @DisplayName("2. a트랜잭션에서_Exception_Catch안함")
    void a트랜잭션에서_Exception_Catch안함() {
        try {
            aService.aMethodWithoutTryCatch();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            List<Book> all = bookRepository.findAll();
            for (Book book : all) {
                log.info("책이름={}", book.getName());
            }

            int savedBookCnt = all.size();
            Assertions.assertThat(savedBookCnt).isEqualTo(0);
            if (savedBookCnt > 0) {
                Book savedBook = all.get(0);
                Assertions.assertThat(savedBook.getName()).isEqualTo("A번책");
            }
        }
    }

    @Test
    @DisplayName("3. a, b트랜잭션에서_Exception_Catch안함")
    void a_b트랜잭션에서_Exception_Catch안함() {
        try {
            aService.aMethodWithoutBMethodTryCatch();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            List<Book> all = bookRepository.findAll();
            for (Book book : all) {
                log.info("책이름={}", book.getName());
            }

            int savedBookCnt = all.size();
            Assertions.assertThat(savedBookCnt).isEqualTo(0);
            if (savedBookCnt > 0) {
                Book savedBook = all.get(0);
                Assertions.assertThat(savedBook.getName()).isEqualTo("A번책");
            }
        }
    }

    @Test
    @DisplayName("4. b트랜잭션에서_Exception_Catch안함")
    void b트랜잭션에서_Exception_Catch안함() {
        try {
            aService.aMethodWithAMethodTryCatchWithoutBMethodTryCatch();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            List<Book> all = bookRepository.findAll();
            for (Book book : all) {
                log.info("책이름={}", book.getName());
            }

            int savedBookCnt = all.size();
            Assertions.assertThat(savedBookCnt).isEqualTo(1);
            if (savedBookCnt > 0) {
                Book savedBook = all.get(0);
                Assertions.assertThat(savedBook.getName()).isEqualTo("A번책");
            }
        }
    }
}
