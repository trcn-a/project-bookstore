package org.example.bookstore.service;

import org.example.bookstore.entity.Book;
import org.example.bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

        // Якщо сортуємо за актуальною ціною (з урахуванням знижки)
        if ("actualPrice".equals(sortBy)) {
            return bookRepository.findAllSortedByActualPrice(PageRequest.of(page, size));
        }

        // Якщо сортуємо за іншими полями, наприклад, title
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
    public Page<Book> filterBooks(List<String> authors, List<String> genres, List<String> publishers,
                                  Integer minPrice, Integer maxPrice, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.filterBooks(authors, genres, publishers, minPrice, maxPrice, pageable);
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
                         Integer stockQuantity, Integer publicationYear,
                         String coverType, String authorName, String genreName, String publisherName, MultipartFile coverImageFile, String description) {
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
        book.setCoverType(coverType);
        book.setDescription(description);

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

    public Page<Book> filterAndSortBooks(List<String> authors, List<String> genres, List<String> publishers,
                                         Integer minPrice, Integer maxPrice,
                                         String sortBy, boolean ascending,
                                         int page, int size) {

        Sort sort;

        if ("actualPrice".equals(sortBy)) {
            // Сортування по актуальній ціні потрібно обробити окремо, бо вона розраховується
            return bookRepository.filterAndSortByActualPrice(authors, genres, publishers,
                    minPrice, maxPrice,
                    PageRequest.of(page, size));
        } else {
            sort = Sort.by(ascending ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        return bookRepository.filterBooks(authors, genres, publishers, minPrice, maxPrice, pageable);
    }

}
