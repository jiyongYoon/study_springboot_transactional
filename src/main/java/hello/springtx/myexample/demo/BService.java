package hello.springtx.myexample.demo;

import hello.springtx.myexample.Book;
import hello.springtx.myexample.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
@Slf4j
public class BService {

    private final BookRepository bookRepository;
    private final CService cService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void bMethod() {
        String currentTransactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("트랜잭션={}, bMethod(), Requires_New", currentTransactionName);


        Book bookB = Book.builder().name("B번책").build();
        bookRepository.save(bookB);
        try {
            cService.cMethod();
        } catch (Exception e) {
            log.info("bMethod 에서 catch");
            throw e;
        }
    }
}
