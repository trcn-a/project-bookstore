# Лінтинг коду
------ -
## Checkstyle

Для перевірки коду в проєкті було обрано **Checkstyle** — популярний інструмент статичного аналізу коду Java, призначений
для забезпечення відповідності стилю програмування встановленим стандартам.

#### Переваги
- Спеціалізується на перевірці Java-коду, забезпечуючи високу ефективність у виявленні відхилень від стилю.


- Легко інтегрується в процес збірки Maven, що дозволяє автоматично перевіряти код на відповідність при кожному запуску збірки.


- Підтримує різні конфігурації: Google Java Style, Sun Style, кастомні XML-конфігурації. Можна створювати або адаптувати власні правила під стандарти команди або проєкту.


---

## Базові правила та їх детальний опис

Нижче наведено пояснення до кожного з активованих модулів у `checkstyle.xml`.


| **Правило**                       | **Опис**                                                                                                                                     |
|-----------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------|
| **LineLength**                    | **Налаштування**: `max = 120`. Обмежує довжину рядка до 120 символів.                                                                        |
| **Indentation**                   | Перевіряє правильність відступів у коді.                                                                                                     |
| **EmptyBlock**                    | Забороняє порожні блоки (наприклад, `if {}`), якщо в них немає коментаря.                                                                    |
| **NeedBraces**                    | Вимагає використовувати фігурні дужки `{}` у всіх умовах та циклах, навіть якщо блок складається з одного рядка.                             |
| **WhitespaceAfter**               | Контролює наявність пробілу **після** певних символів (наприклад, ком, ключових слів `if`, `for` тощо).                                      |
| **WhitespaceAround**              | Контролює наявність пробілів **навколо** арифметичних (`+`, `-`, `*`, `/`) і логічних операторів (`&&`, `==`).                               |                                  ||
| **AvoidStarImport**               | Забороняє імпорти виду `import java.util.*;`, оскільки вони ускладнюють читання та контроль імпортів. Краще імпортувати лише потрібні класи. |
| **LocalVariableName**             | Забезпечує відповідність локальних змінних стилю lowerCamelCase (наприклад, `int maxCount;`). Не допускає використання небажаних префіксів.  |
| **MemberName**                    | Перевіряє правильність іменування полів класу. Поля мають бути в стилі lowerCamelCase (`private String firstName;`).                         |
| **MethodName**                    | Імена методів мають бути в стилі lowerCamelCase (`getSortedBooks()`).                                                                        |
| **TypeName**                      | Назви класів, інтерфейсів, enum'ів мають бути в стилі UpperCamelCase (`class MainController`). Заборонені нижні підкреслення та абревіатури. |
| **UnusedImports**                 | Виявляє імпортовані, але не використані класи/пакети.                                                                                        |
| **RedundantImport**               | Виявляє дублікати імпортів одного й того ж класу.                                                                                            |
| **JavadocMethod**                 | Перевіряє Javadoc-коментарі для методів і конструкторів.                                                                                     |
| **JavadocType**                   | Перевіряє Javadoc-коментарі для класів, інтерфейсів, перерахувань.                                                                           |
| **JavadocStyle**                  | Перевіряє загальний стиль Javadoc-коментарів, щоб вони були правильно оформлені і відповідали стандартам.                                    |
| **MissingJavadocMethod**          | Виявляє методи та конструктори без Javadoc-коментарів.                                                                                       |
| **MissingJavadocType**            | Виявляє відсутність Javadoc-коментарів для класів, інтерфейсів і перерахувань.                                                               |
| **JavadocBlockTagLocation**       | Перевіряє правильне розміщення тегів в Javadoc (наприклад, `@param`, `@return`) на початку рядка                                             |



## Інструкція з запуску Checkstyle

- У файлі `pom.xml` знаходиться конфігурація плагіна `maven-checkstyle-plugin`.
- У корені проєкту є файл `checkstyle.xml` з визначеними правилами.

### Запуск перевірки
У терміналі, перебуваючи в кореневій директорії проєкту, виконайте команду:


```bash
./mvnw checkstyle:check
```
(`./mvnw` запускає Maven без
необхідності встановлення його глобально)

**Результат перевірки буде виведено безпосередньо в терміналі.**


### Генерація HTML-звіту

Для створення зручного звіту у форматі HTML виконайте:
```bash
./mvnw checkstyle:checkstyle
```
**Після виконання буде створено звіт: `target/site/checkstyle.html`**



Його можна відкрити у браузері для зручного перегляду помилок.

## Git Hooks



Для запуску лінтера (Checkstyle) перед кожним комітом потрібно налаштувати
**pre-commit хук**, який буде автоматично виконувати перевірку. Це дозволить перевірити код на відповідність стандартам стилю ще до того, як зміни будуть зафіксовані в репозиторії.

### Кроки налаштування:

1. У директорії `.git/hooks` створіть файл `pre-commit`.

2. Додайте до файлу наступний скрипт:

   ```bash
   #!/bin/sh
   echo "Running Checkstyle before commit..."
   
   ./mvnw checkstyle:check
   
   if [ $? -ne 0 ]; then
     echo "Checkstyle failed. Commit aborted."
     exit 1
   fi
   
   echo "Checkstyle passed. Proceeding with commit."
   exit 0
   ```

Тепер при спробі зробити коміт, якщо в коді будуть знайдені помилки лінтингу, коміт не буде виконано, і ви побачите таке повідомлення:

```bash
Running Checkstyle before commit...
...
Checkstyle failed. Commit aborted.
```

Якщо перевірка пройде успішно, ви побачите:


```bash
Running Checkstyle before commit...
...
Checkstyle passed. Proceeding with commit.
```
## Інтеграція з процесом збірки

В проєкті інтегровано лінтинг в процес збірки, що дозволяє автоматично перевіряти код на відповідність стандартам при кожному запуску збірки проєкту. 

### Налаштування в `pom.xml`:

У конфігурації плагіна `maven-checkstyle-plugin` використовується секція **`executions`** для визначення, на якому етапі збірки буде виконуватись перевірка лінтером.


```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.3.0</version>
    <configuration>
        <configLocation>checkstyle.xml</configLocation>
        <failOnViolation>true</failOnViolation>
    </configuration>
    <executions>
        <execution>
            <phase>validate</phase>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
У даному проєкті перевірка коду на стиль налаштована на етап **`validate`**, що означає, що лінтер запускається ще до початку компіляції і тестування коду.


#### В разі порушення стандартів збірка проекту буде зупинена. 

