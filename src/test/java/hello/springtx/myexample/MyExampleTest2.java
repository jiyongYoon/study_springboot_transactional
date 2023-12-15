package hello.springtx.myexample;

import hello.springtx.myexample.demo.AService;
import hello.springtx.myexample.demo.BService;
import hello.springtx.myexample.demo.CService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class MyExampleTest2 {

    @Autowired
    AService aService;

    @Autowired
    BService bService;

    @Autowired
    CService cService;

    @Autowired
    BookRepository bookRepository;

    @Test
    void test() {
        aService.aMethod();

        List<Book> all = bookRepository.findAll();
        for (Book book : all) {
            log.info("책이름={}", book.getName());
        }
    }
}
