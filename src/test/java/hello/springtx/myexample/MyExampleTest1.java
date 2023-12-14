package hello.springtx.myexample;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class MyExampleTest1 {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    BookService bookService;

    @Autowired
    Librarian librarian;

    @BeforeEach
    void init() {
        Book book1 = Book.builder().name("1번책").available(false).build();
        Book book2 = Book.builder().name("2번책").available(false).build();
        bookRepository.save(book1);
        bookRepository.save(book2);
    }

    @AfterEach
    void reset() {
        List<Book> all = bookRepository.findAll();
        bookRepository.deleteAllInBatch(all);
    }

    @Test
    void 트랜잭션테스트_실패() {
        List<Book> allBookList = bookRepository.findAll();
        Assertions.assertThat(allBookList.get(0).getAvailable()).isFalse();

        //when
        librarian.arrangeBooks(bookService, bookRepository);

        //then
        List<Book> allBookList2 = bookRepository.findAll();
        Assertions.assertThat(allBookList2.get(0).getAvailable()).isFalse(); // True로 만들고 싶다
    }

    @Test
    @DisplayName("방법1: arrangeBooksWithSave() 메서드에서 bookRepository.save() 메서드를 한번 호출하여 변경 데이터를 커밋해버린다.")
    void 트랜잭션테스트_성공1() {
        List<Book> allBookList = bookRepository.findAll();
        Assertions.assertThat(allBookList.get(0).getAvailable()).isFalse();

        //when
        librarian.arrangeBooksWithSave(bookService, bookRepository);

        //then
        List<Book> allBookList2 = bookRepository.findAll();
        Assertions.assertThat(allBookList2.get(0).getAvailable()).isTrue();
    }

    @Test
    @DisplayName("방법2: arrangeBooksWithRequiresNew() 메서드에서 트랜잭션이 시작되고, 내부의 bookService.bookReturnWithRequiresNew() 에서 새로운 자식 트랜잭션을 시작한다.")
    void 트랜잭션테스트_성공2() {
        List<Book> allBookList = bookRepository.findAll();
        Assertions.assertThat(allBookList.get(0).getAvailable()).isFalse();

        //when
        librarian.arrangeBooksWithRequiresNew(bookService, bookRepository);

        //then
        List<Book> allBookList2 = bookRepository.findAll();
        Assertions.assertThat(allBookList2.get(0).getAvailable()).isTrue();
    }
}
