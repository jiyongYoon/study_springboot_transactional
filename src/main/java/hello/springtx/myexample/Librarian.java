package hello.springtx.myexample;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class Librarian {

    public void arrangeBooks(BookService bookService, BookRepository bookRepository) {
        Book book1 = bookRepository.findByName("1번책").orElseThrow();
        bookService.bookReturn(book1);
        book1.returnBook();
        System.out.println("반납: " + book1.getName() + " / " + book1.getAvailable());
    }

    public void arrangeBooksWithSave(BookService bookService, BookRepository bookRepository) {
        Book book1 = bookRepository.findByName("1번책").orElseThrow();
        bookService.bookReturn(book1);
        book1.returnBook();
        bookRepository.save(book1);
        System.out.println("반납: " + book1.getName() + " / " + book1.getAvailable());
    }

    @Transactional
    public void arrangeBooksWithRequiresNew(BookService bookService, BookRepository bookRepository) {
        Book book1 = bookRepository.findByName("1번책").orElseThrow();
        bookService.bookReturnWithRequiresNew(book1);
        System.out.println("반납: " + book1.getName() + " / " + book1.getAvailable());
    }

}
