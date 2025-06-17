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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    public Book saveBook(Long id, String title, Integer price, Integer pages, Integer discount, String isbn,
                         Integer stockQuantity, Integer publicationYear,
                         String coverType, String authorName, String genreName, String publisherName,
                         MultipartFile coverImageFile, String description) {

        // Перевірки
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Назва книги не може бути порожньою");
        }

        if (authorName == null || authorName.trim().isEmpty()) {
            throw new IllegalArgumentException("Автор не може бути порожнім");
        }

        if (genreName == null || genreName.trim().isEmpty()) {
            throw new IllegalArgumentException("Жанр не може бути порожнім");
        }

        if (publisherName == null || publisherName.trim().isEmpty()) {
            throw new IllegalArgumentException("Видавництво не може бути порожнім");
        }

        if (coverType == null || coverType.trim().isEmpty()) {
            throw new IllegalArgumentException("Тип обкладинки не може бути порожнім");
        }

        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN не може бути порожнім");
        }

        if (price == null || price < 1) {
            throw new IllegalArgumentException("Ціна повинна бути не менше 1");
        }

        if (pages == null || pages < 0) {
            throw new IllegalArgumentException("Кількість сторінок не може бути від’ємною");
        }

        if (discount == null || discount < 0 || discount > 100) {
            throw new IllegalArgumentException("Знижка повинна бути в межах від 0 до 100");
        }

        if (stockQuantity == null || stockQuantity < 0) {
            throw new IllegalArgumentException("Кількість на складі не може бути від’ємною");
        }

        if (publicationYear == null || publicationYear < 2000 || publicationYear > LocalDateTime.now().getYear()) {
            throw new IllegalArgumentException("Рік публікації повинен бути між 2000 і " + LocalDateTime.now().getYear() + ".");
        }

        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Опис не може бути порожнім");
        }

        Book book;
        if (id != null) {
            book = bookRepository.findById(id).orElseThrow();
        } else {
            book = new Book();
        }

        book.setTitle(title);
        book.setPrice(price);
        book.setPages(pages);
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

        Pageable pageable = PageRequest.of(page, size);

        if ("actualPrice".equals(sortBy)) {
            if (ascending) {
                return bookRepository.filterAndSortByActualPriceAsc(authors, genres, publishers,
                        minPrice, maxPrice, pageable);
            } else {
                return bookRepository.filterAndSortByActualPriceDesc(authors, genres, publishers,
                        minPrice, maxPrice, pageable);
            }
        } else {
            Sort sort = Sort.by(ascending ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
            pageable = PageRequest.of(page, size, sort);
            return bookRepository.filterBooks(authors, genres, publishers, minPrice, maxPrice, pageable);
        }

}}
