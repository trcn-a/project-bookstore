# Бізнес-логіка вебдодатку (Spring Boot + Thymeleaf)

---
## 1. Користувач системи

### Вхід у систему

- Користувач може увійти в обліковий запис, використовуючи:
    - email (як логін);
    - пароль.
- Під час входу:
    - система перевіряє, чи існує користувач з таким email;
    - перевіряє відповідність пароля (збереженого у зашифрованому вигляді, наприклад, BCrypt).
- Успішна автентифікація надає доступ до персонального кабінету та функціоналу авторизованого користувача (кошик, обране, замовлення, редагування профілю, залишення відгуків тощо).

---

### Реєстрація користувача

- Користувач створює обліковий запис, заповнюючи форму:
    - ім’я;
    - email (унікальний);
    - пароль + підтвердження пароля. Мінімум 8 символів. Має містити хоча б одну цифрру та спеціальний символ.

- Пароль хешується перед збереженням у базі.
- Email перевіряється на унікальність.
- Після реєстрації користувач автоматично входить у систему

---


### Редагування даних користувача

- Користувач може:
    - змінити ім’я;
    - змінити номер телефону;
    - змінити пароль;


#### Валідація:
- Ім’я — обов’язкове.
- При зміні пароля:
    - перевірка старого пароля;
    - новий пароль. Мінімум 8 символів. Має містити хоча б одну цифрру та спеціальний символ.
    - підтвердження нового пароля.

---

## Кошик

### Основна мета:
Надати користувачу можливість додавати книги до кошика, змінювати кількість.

### Функціонал:
- Кожен користувач має **свій кошик** (зв’язок з користувачем).
- Якщо користувач **не авторизований**, він не може додати товар до кошика.
- При натисканні кнопки **«Додати до кошика»**:
    - Якщо книги немає в кошику — додається нова позиція.
    - Якщо вже додана — користувача перенаправляє до сторінки кошика.
- Можна змінювати кількість товарів або видаляти позиції.
- Після оформлення замовлення кошик очищується.

### Валідація:
- Неможливо додати **кількість більшу за наявну на складі**.
- Неможливо додати **більше 10 одиниць однієї позиції**.

---

## Оформлення замовлення

### Основна мета:
Надати користувачу можливість підтвердити покупку книг із кошика.

### Функціонал:
- Користувач переходить до сторінки оформлення замовлення з активного кошика.
- Вказує:
    - контактну інформацію (ім’я, номер телефону);
    - адресу доставки;

- Система перевіряє наявність книг на складі ще раз.
- Створюється запис у таблиці `orders`:
    - з інформацією про користувача;
    - датою замовлення;
    - статусом
- Для кожної книги додається запис у `order_items` з кількістю та ціною.
- Після успішного оформлення:
    - користувач бачить сторінку про успішне замовлення;
    - кошик очищується;
    - кількість книг на складі зменшується.

### Валідація:
- Всі поля форми обов’язкові (ім’я, email, адреса).
- Неможливо оформити замовлення з порожнім кошиком.

---

##  Список обраного

### Основна мета:
Дозволити користувачам зберігати книги, які їм сподобалися, щоб повернутися до них пізніше.

### Функціонал:
- Кожен користувач має свій список обраного (`favorites`).
- При натисканні **«❤» (Кнопка "Обране")**:
    - Якщо книги **немає** в обраному — додається запис до таблиці `favorites`.
    - Якщо книга **вже є** в обраному — запис **видаляється** (тобто кнопка працює як перемикач).
- Користувач може переглядати список обраного.


---

## Відгуки про книги

### Основна мета:
Надати можливість користувачам залишати свої думки про книги.

### Функціонал:
- Лише **авторизовані користувачі** можуть залишати відгуки.
- Відгук прив’язаний до книги і користувача.
- Один користувач може залишити **один відгук на книгу**
- Користувач може видалити свій відгук.
- Відгук містить:
    - оцінку 1–5 зірок (обов'язково);
    - текст коментаря;
- На сторінці книги відображаються всі відгуки з іменем автора та датою публікації.
- За відгуками підраховується середня оцінка книги

### Валідація:
- Неможливо залишити відгук без оцінки.
- Якщо текстовий відгук наявний він має бути в межах 2-500 символів.
- Оцінка має бути в межах від 1 до 5.

---

