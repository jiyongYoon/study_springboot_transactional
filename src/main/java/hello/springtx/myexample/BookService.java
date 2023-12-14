package hello.springtx.myexample;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookService {

    @Transactional
    public void bookReturn(Book book) {
        log.info("get books from return box! name={}", book.getName());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void bookReturnWithRequiresNew(Book book) {
        log.info("get books from return box! name={}", book.getName());
        book.returnBook();
    }
}
