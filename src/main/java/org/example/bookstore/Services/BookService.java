package org.example.bookstore.Services;

import org.example.bookstore.Entities.Book;
import org.example.bookstore.Repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.util.List;

/**
 * Сервісний клас для управління книгами в системі.
 * Включає методи для пошуку, фільтрації, сортування,
 * а також отримання книг за ID або автором.
 */
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final GenreService genreService;
    private final PublisherService publisherService;
    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    /**
     * Конструктор для ініціалізації сервісу з репозиторієм книг та іншими сервісами.
     *
     * @param bookRepository Репозиторій для доступу до даних книг.
     * @param authorService Сервіс для роботи з авторами.
     * @param genreService Сервіс для роботи з жанрами.
     * @param publisherService Сервіс для роботи з видавцями.
     */
    @Autowired
    public BookService(BookRepository bookRepository, AuthorService authorService, GenreService genreService, PublisherService publisherService) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
        this.genreService = genreService;
        this.publisherService = publisherService;
    }
    /**
     * Повертає певну сторінку книг з репозиторію, з заданою кількістю елементів на сторінці.
     *
     * @param page Номер сторінки.
     * @param size Кількість книг на сторінці.
     * @return Сторінка книг з репозиторією.
     */
    public Page<Book> getBooks(int page, int size) {
        logger.info("Request to get books. Page: {}, Size: {}", page, size);
        return bookRepository.findAll(PageRequest.of(page, size));
    }

    /**
     * Виконує пошук книг за вказаним текстовим запитом.
     *
     * @param searchQuery Пошуковий запит для знаходження книг.
     * @return Список книг, що відповідають пошуковому запиту.
     */
    public List<Book> searchBooks(String searchQuery) {
        logger.info("Search request for books with text: {}", searchQuery);
        List<Book> books = bookRepository.searchBooks(searchQuery);
        logger.info("Found books: {}", books.size());
        return books;
    }

    /**
     * Сортує книги за вказаним полем в порядку за зростанням або спаданням.
     *
     * @param sortBy Поле для сортування книг.
     * @param ascending Якщо true — сортує книги за зростанням, якщо false — за спаданням.
     * @param page Номер сторінки.
     * @param size Кількість книг на сторінці.
     * @return Сторінка книг, відсортованих за вказаним полем.
     */
    public Page<Book> getSortedBooks(String sortBy, boolean ascending, int page, int size) {
        logger.info("Request to sort books by field: {}. Order: {}. Page: {}, Size: {}",
                sortBy, ascending ? "ascending" : "descending", page, size);
        Sort.Direction direction = ascending ? Sort.Direction.ASC : Sort.Direction.DESC;
        return bookRepository.findAll(PageRequest.of(page, size, Sort.by(direction, sortBy)));
    }

    /**
     * Знаходить та повертає книгу за її унікальним ідентифікатором.
     *
     * @param id Ідентифікатор книги, яку потрібно знайти.
     * @return Книга з відповідним ідентифікатором.
     * @throws RuntimeException Якщо книга з таким ідентифікатором не знайдена.
     */
    public Book getBookById(Long id) {
        logger.info("Request to get book with ID: {}", id);
        return bookRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Book with ID={} not found", id);
                    return new RuntimeException("Book not found with id: " + id);
                });
    }

    /**
     * Фільтрує книги за авторами, жанрами, видавцями та ціною.
     *
     * @param authors Список авторів для фільтрації.
     * @param genres Список жанрів для фільтрації.
     * @param publishers Список видавців для фільтрації.
     * @param minPrice Мінімальна ціна для фільтрації.
     * @param maxPrice Максимальна ціна для фільтрації.
     * @return Список книг, що відповідають вказаним критеріям.
     * @throws IllegalArgumentException Якщо мінімальна ціна більша за максимальну.
     */
    public List<Book> filterBooks(List<String> authors, List<String> genres, List<String> publishers,
                                  Integer minPrice, Integer maxPrice) {
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            logger.error("Filtering error: min price is greater than max price");
            throw new IllegalArgumentException("Min price cannot be greater than max price.");
        }
        logger.info("Request to filter books by criteria - authors: {}, genres: {}, publishers: {}, price: {}-{}",
                authors, genres, publishers, minPrice, maxPrice);
        return bookRepository.filterBooks(authors, genres, publishers, minPrice, maxPrice);
    }

    /**
     * Знаходить всі книги, написані певним автором за його ідентифікатором.
     *
     * @param authorId Ідентифікатор автора, книги якого необхідно знайти.
     * @return Список книг, написаних автором з вказаним ідентифікатором.
     */
    public List<Book> getBooksByAuthor(Long authorId) {
        logger.info("Request to get books by author with ID: {}", authorId);
        return bookRepository.findByAuthorId(authorId);
    }



    public List<Book> findAll() {
       return bookRepository.findAll();
    }

    public Book saveBook(Long id, String title, Integer price, Integer discount, String isbn,
                         Integer stockQuantity, Integer publicationYear, String bookFormat,
                         String coverType, String authorName, String genreName, String publisherName, MultipartFile coverImageFile) {
        Book book;

        if (id != null) {
            book = bookRepository.findById(id).orElseThrow();
        } else {
            book = new Book();
        }

        book.setTitle(title);
        book.setPrice(price);
        book.setDiscount(discount);
        book.setIsbn(isbn);
        book.setStockQuantity(stockQuantity);
        book.setPublicationYear(publicationYear);
        book.setBookFormat(bookFormat);
        book.setCoverType(coverType);

        book.setAuthor(authorService.createIfNotExists(authorName));
        book.setGenre(genreService.createIfNotExists(genreName));
        book.setPublisher(publisherService.createIfNotExists(publisherName));

        if (!coverImageFile.isEmpty()) {
            try {
                String filename = "book_cover" + new SecureRandom().nextInt(999999) + coverImageFile.getOriginalFilename() ;
                Path uploadPath = Paths.get("img/");

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Path filePath = uploadPath.resolve(filename);
                Files.copy(coverImageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                book.setCoverImage(filename);
            } catch (IOException e) {
                throw new RuntimeException("Помилка збереження обкладинки", e);
            }
        }

        return bookRepository.save(book);
    }

}
