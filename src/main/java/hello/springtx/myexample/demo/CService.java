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
public class CService {

    private final BookRepository bookRepository;

    @Transactional
    public void cMethod() {
        String currentTransactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("트랜잭션={}, cMethod(), throw new RuntimeException()", currentTransactionName);


        Book bookC = Book.builder().name("C번책").build();
        bookRepository.save(bookC);

        throw new RuntimeException();
    }
}
