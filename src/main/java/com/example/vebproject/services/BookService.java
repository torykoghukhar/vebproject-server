package com.example.vebproject.services;
import com.example.vebproject.DTO.BookDTO;
import com.example.vebproject.DTO.BookFilterDTO;
import com.example.vebproject.models.Book;
import com.example.vebproject.models.User;
import com.example.vebproject.repository.BookRepository;
import com.example.vebproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookService(BookRepository bookRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public List<Book> getPublicBooks() {
        return bookRepository.findAll().stream()
                .filter(book -> !book.isSold())
                .collect(Collectors.toList());
    }

    public List<Book> getAllBooks(String username) {
        return bookRepository.findAll().stream()
                .filter(book -> !book.isSold() &&
                        (book.getPublisher() != null ||
                                (book.getUser() != null && !username.equals(book.getUser().getName()))))
                .collect(Collectors.toList());
    }

    public BookDTO getBookById(Long id) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book == null) {
            return null;
        }
        String user = (book.getUser() != null && book.getUser().getName() != null) ? book.getUser().getName() : "unknown";
        String publisher = (book.getPublisher() != null && book.getPublisher().getName() != null) ? book.getPublisher().getName() : "unknown";
        return new BookDTO(
                book.getId(),
                book.getDescription(),
                book.getTitle(),
                book.getAuthor(),
                book.getGenre(),
                book.getPrice(),
                book.getPictureUrl(),
                publisher,
                user,
                book.getCondition()
        );
    }

    public List<String> getAllAuthors() {
        return bookRepository.findAllDistinctAuthors();
    }

    public List<String> getAllGenres() {
        return bookRepository.findAllDistinctGenres();
    }

    public List<Book> applyFilters(BookFilterDTO filters) {
        List<Book> allBooks = bookRepository.findAll();
        return allBooks.stream()
                .filter(book -> {
                    if (filters.getPublisher() != null && !filters.getPublisher().isEmpty()) {
                        if (book.getPublisher() != null) {
                            if (!book.getPublisher().getName().equalsIgnoreCase(filters.getPublisher())) {
                                return false;
                            }
                        }
                        else if (book.getUser() != null) {
                            if (!book.getUser().getName().equalsIgnoreCase(filters.getPublisher())) {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }

                    if (filters.getCondition() != null && !filters.getCondition().isEmpty() &&
                            !book.getCondition().equalsIgnoreCase(filters.getCondition())) {
                        return false;
                    }

                    if (filters.getMinPrice() != null && book.getPrice().compareTo(BigDecimal.valueOf(filters.getMinPrice())) < 0) {
                        return false;
                    }

                    if (filters.getMaxPrice() != null && book.getPrice().compareTo(BigDecimal.valueOf(filters.getMaxPrice())) > 0) {
                        return false;
                    }

                    if (filters.getGenre() != null && !filters.getGenre().isEmpty()) {
                        if (book.getGenre() == null || !book.getGenre().equalsIgnoreCase(filters.getGenre())) {
                            return false;
                        }
                    }

                    if (filters.getAuthor() != null && !filters.getAuthor().isEmpty()) {
                        if (book.getAuthor() == null || !book.getAuthor().equalsIgnoreCase(filters.getAuthor())) {
                            return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    public List<Book> getBooksByUser(String username) {
        return userRepository.findByName(username).map(User::getBooks).orElse(null);
    }

    public Long addBook(BookDTO bookDTO, String username) {
        User user = userRepository.findByName(username).orElse(null);
        Book book = new Book(user, false, "USED", bookDTO.getPictureUrl(), bookDTO.getPrice(), bookDTO.getDescription(), bookDTO.getGenre(), bookDTO.getAuthor(), bookDTO.getTitle());
        book = bookRepository.save(book);
        return book.getId();
    }

    public boolean deleteBookById(Long id) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book != null) {
            bookRepository.delete(book);
            return true;
        }
        return false;
    }

    public BookDTO updatebook(Long id, BookDTO bookDTO){
        Book book = bookRepository.findById(id).orElse(null);
        if (book != null) {
            book.setDescription(bookDTO.getDescription());
            book.setTitle(bookDTO.getTitle());
            book.setAuthor(bookDTO.getAuthor());
            book.setPrice(bookDTO.getPrice());
            book.setGenre(bookDTO.getGenre());
            book.setCondition(bookDTO.getCondition());
            bookRepository.save(book);
            return bookDTO;
        }
        return null;
    }

    public String uploadBookPhoto(Long bookId, MultipartFile file) throws IOException {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        String fileName = book.getTitle() + "_" + file.getOriginalFilename();
        String uploadDir = "/uploads/book-photos/";
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        String photoUrl = "/uploads/book-photos/" + fileName;
        book.setPictureUrl(photoUrl);
        bookRepository.save(book);

        return photoUrl;
    }
}
