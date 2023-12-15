package hello.springtx.myexample.demo;

import hello.springtx.myexample.Book;
import hello.springtx.myexample.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
@Slf4j
public class AService {

    private final BookRepository bookRepository;

    private final BService bService;

    @Transactional
    public void aMethod() {
        String currentTransactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("트랜잭션={}, aMethod()", currentTransactionName);


        Book bookA = Book.builder().name("A번책").build();
        bookRepository.save(bookA);
        try {
            bService.bMethod();
        } catch (Exception e) {
            log.info("AService에서 catch");
        }
    }

}
