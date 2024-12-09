package com.example.vebproject.controllers;
import com.example.vebproject.DTO.BookDTO;
import com.example.vebproject.DTO.BookFilterDTO;
import com.example.vebproject.DTO.OrderDTO;
import com.example.vebproject.models.Book;
import com.example.vebproject.models.User;
import com.example.vebproject.services.BookService;
import com.example.vebproject.services.OrderService;
import com.example.vebproject.services.UserService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;

@RestController
public class BookController {
    private final BookService bookService;
    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    public BookController(BookService bookService, UserService userService, OrderService orderService) {
        this.bookService = bookService;
        this.userService = userService;
        this.orderService = orderService;
    }
    @GetMapping("/getallbooks")
    public ResponseEntity<?> getAllBooks(Principal principal) {
        List<Book> books;
        if (principal == null) {
            books = bookService.getPublicBooks();
        } else {
            books = bookService.getAllBooks(principal.getName());
        }

        if (books.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(books);
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<?> getBookById(@PathVariable Long id) {
        BookDTO book = bookService.getBookById(id);
        if (book != null) {
            return ResponseEntity.ok(book);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Book with ID " + id + " not found.");
        }
    }
    @PostMapping("/applyfilters")
    public ResponseEntity<?> applyFilters(@RequestBody BookFilterDTO filters) {
        List<Book> filteredBooks = bookService.applyFilters(filters);
        if (filteredBooks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(filteredBooks);
    }

    @GetMapping("/getallgenres")
    public ResponseEntity<?> getAllGenres() {
        List<String> genres = bookService.getAllGenres();
        if (genres.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(genres);
    }

    @GetMapping("/getallauthors")
    public ResponseEntity<?> getAllAuthors() {
        List<String> authors = bookService.getAllAuthors();
        if (authors.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(authors);
    }

    @GetMapping("/booksuser")
    public ResponseEntity<?> getBooksByUser(Principal principal) {
        if (principal==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Book> books = bookService.getBooksByUser(principal.getName());
        if (books.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(books);
    }

    @PostMapping("/booksuser")
    public ResponseEntity<?> addBook(@RequestBody BookDTO bookDTO, Principal principal) {
        if (principal==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long bookId = bookService.addBook(bookDTO, principal.getName());
        if (bookId != null){
            return ResponseEntity.ok(bookId);
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @DeleteMapping("/booksuser/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        if(bookService.deleteBookById(id)){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @PostMapping("/buybooks")
    public ResponseEntity<?> buyBook(@RequestBody OrderDTO orderDTO, Principal principal) {
        if (principal==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (orderDTO ==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        User user = userService.getuser(principal.getName());
        if (orderService.createOrder(orderDTO, user)){
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @PutMapping("/updatebook/{id}")
    public ResponseEntity<?> updateBook(@RequestBody BookDTO bookDTO, @PathVariable Long id) {
        if (bookDTO==null || id==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        BookDTO result = bookService.updatebook(id, bookDTO);
        if (result==null){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/books/upload-photo/{id}")
    public ResponseEntity<String> uploadBookPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            String photoUrl = bookService.uploadBookPhoto(id, file);
            return ResponseEntity.ok(photoUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload photo");
        }
    }

    @GetMapping("/uploads/book-photos/{filename}")
    public ResponseEntity<org.springframework.core.io.Resource> getBookPhoto(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("/uploads/book-photos").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
