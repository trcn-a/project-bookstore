document.addEventListener("DOMContentLoaded", function () {
    let cityInput = document.getElementById("city");
    let citySuggestionsList = document.getElementById("citySuggestions");
    let postOfficeInput = document.getElementById("postOfficeNumber");
    let postOfficeSuggestionsList = document.getElementById("postOfficeSuggestions");

    let selectedCity = null; // Перемінна для зберігання вибраного міста

    // Відслідковуємо введення в полі "Місто"
    cityInput.addEventListener("input", function () {
        let city = this.value.trim();
        if (city.length >= 3) {
            fetchCities(city);
        } else {
            citySuggestionsList.style.display = 'none';
            // Якщо місто не вибрано, очищуємо відділення
            clearWarehouses();
        }
    });

    // Функція для запиту міст через API Нової Пошти
    function fetchCities(city) {
        fetch("/api/cities?query=" + encodeURIComponent(city))
            .then(response => response.json())
            .then(data => {
                citySuggestionsList.innerHTML = '';
                if (data.success && data.data && data.data.length > 0) {
                    data.data.forEach(cityItem => {
                        let listItem = document.createElement("li");
                        listItem.textContent = cityItem.Description;
                        listItem.addEventListener("click", function () {
                            cityInput.value = cityItem.Description;
                            selectedCity = cityItem.Description; // Зберігаємо вибране місто
                            citySuggestionsList.style.display = 'none';
                            fetchWarehouses(cityItem.Description);
                        });
                        citySuggestionsList.appendChild(listItem);
                    });
                    citySuggestionsList.style.display = 'block';
                } else {
                    citySuggestionsList.style.display = 'none';
                }
            })
            .catch(error => {
                console.error("Помилка при отриманні міст:", error);
                citySuggestionsList.style.display = 'none';
            });
    }

    // Відслідковуємо введення в полі "Номер відділення"
    postOfficeInput.addEventListener("input", function () {
        let warehouse = this.value.trim();
        if (warehouse.length >= 1) {
            filterWarehouses(warehouse);
        } else {
            // Якщо поле для міста не вибрано, очищуємо відділення
            if (!selectedCity) {
                clearWarehouses();
            } else {
                fetchWarehouses(cityInput.value);
            }
        }
    });

    // Функція для фільтрації відділень
    function filterWarehouses(warehouse) {
        let options = postOfficeSuggestionsList.getElementsByTagName("li");
        Array.from(options).forEach(option => {
            let text = option.textContent;
            if (text.includes(warehouse)) {
                option.style.display = '';
            } else {
                option.style.display = 'none';
            }
        });
    }

    // Функція для запиту відділень по вибраному місту
    function fetchWarehouses(city) {
        // Якщо місто не вибрано зі списку, очищуємо відділення
        if (!selectedCity) {
            clearWarehouses();
            return;
        }

        fetch("/api/warehouses?city=" + encodeURIComponent(city))
            .then(response => response.json())
            .then(data => {
                postOfficeSuggestionsList.innerHTML = '';
                if (data.success && data.data && data.data.length > 0) {
                    data.data.forEach(warehouse => {
                        let listItem = document.createElement("li");
                        listItem.textContent = `${warehouse.Description}`;
                        listItem.addEventListener("click", function () {
                            postOfficeInput.value = warehouse.Description;
                            postOfficeSuggestionsList.style.display = 'none';
                        });
                        postOfficeSuggestionsList.appendChild(listItem);
                    });
                    postOfficeSuggestionsList.style.display = 'block';
                } else {
                    postOfficeSuggestionsList.style.display = 'none';
                }
            })
            .catch(error => {
                console.error("Помилка при отриманні відділень:", error);
                postOfficeSuggestionsList.style.display = 'none';
            });
    }

    // Функція для очищення списку відділень
    function clearWarehouses() {
        postOfficeSuggestionsList.innerHTML = '';
        postOfficeSuggestionsList.style.display = 'none';
        postOfficeInput.value = ''; // Очищаємо значення в полі вводу для відділення
    }
});
