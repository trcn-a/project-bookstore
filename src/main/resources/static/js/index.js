    document.getElementById('filterForm').addEventListener('submit', function (e) {
    const minPriceInput = this.querySelector('[name="minPrice"]');
    const maxPriceInput = this.querySelector('[name="maxPrice"]');

    if (!minPriceInput.value) {
    minPriceInput.removeAttribute('name');
}
    if (!maxPriceInput.value) {
    maxPriceInput.removeAttribute('name');
}
});

    document.getElementById('sort').addEventListener('change', function () {
    const form = document.getElementById('filterForm');
    const minPriceInput = form.querySelector('[name="minPrice"]');
    const maxPriceInput = form.querySelector('[name="maxPrice"]');

    if (!minPriceInput.value) {
    minPriceInput.removeAttribute('name');
}
    if (!maxPriceInput.value) {
    maxPriceInput.removeAttribute('name');
}

    form.submit();
});

    function toggleFilter(titleEl) {
    const content = titleEl.nextElementSibling;
    const arrowIcon = titleEl.querySelector('.arrow i');

    if (content.style.display === 'none' || content.style.display === '') {
    content.style.display = 'block';
    arrowIcon.classList.remove('fa-chevron-right');
    arrowIcon.classList.add('fa-chevron-down');
    titleEl.classList.add('active');
} else {
    content.style.display = 'none';
    arrowIcon.classList.remove('fa-chevron-down');
    arrowIcon.classList.add('fa-chevron-right');
    titleEl.classList.remove('active');
}
}

    // При завантаженні сторінки сховаємо всі .filter-content і поставимо стрілку "▶",
    // але розкриємо ті, де є відмічені чекбокси
    document.addEventListener('DOMContentLoaded', () => {
    // Спочатку ховаємо всі
    document.querySelectorAll('.filter-content').forEach(content => {
        content.style.display = 'none';
    });
    document.querySelectorAll('.filter-title').forEach(titleEl => {
    titleEl.classList.remove('active');
    const arrowIcon = titleEl.querySelector('.arrow i');
    if (arrowIcon) {
    arrowIcon.classList.remove('fa-chevron-down');
    arrowIcon.classList.add('fa-chevron-right');
}
});

    // Відкриваємо фільтри, де є відмічені чекбокси
    document.querySelectorAll('.filter-group').forEach(group => {
    const checkedInputs = group.querySelectorAll('input[type="checkbox"]:checked');
    const minPriceInput = group.querySelector('input[name="minPrice"]');
    const maxPriceInput = group.querySelector('input[name="maxPrice"]');

    const hasCheckedCheckboxes = checkedInputs.length > 0;
    const hasPriceValue = (minPriceInput && minPriceInput.value) || (maxPriceInput && maxPriceInput.value);

    if (hasCheckedCheckboxes || hasPriceValue) {
    const titleEl = group.querySelector('.filter-title');
    const content = group.querySelector('.filter-content');
    const arrowIcon = titleEl.querySelector('.arrow i');
    if (content && titleEl && arrowIcon) {
    content.style.display = 'block';
    arrowIcon.classList.remove('fa-chevron-right');
    arrowIcon.classList.add('fa-chevron-down');
    titleEl.classList.add('active');
}
}
});

    // Пошук по опціях фільтрів
    document.querySelectorAll('.filter-search').forEach(input => {
    input.addEventListener('input', () => {
    const term = input.value.toLowerCase();
    const labels = input.closest('.filter-content').querySelectorAll('.filter-options label');

    labels.forEach(label => {
    const text = label.textContent.toLowerCase();
    label.style.display = text.includes(term) ? 'block' : 'none';
});
});
});
});

    function validatePrice() {
    const minPrice = parseFloat(document.querySelector('input[name="minPrice"]').value) || 0;
    const maxPrice = parseFloat(document.querySelector('input[name="maxPrice"]').value) || 0;

    if (minPrice !== 0 && maxPrice !== 0 && minPrice > maxPrice) {
    alert('Мінімальна ціна не може бути більшою за максимальну!');
    return false; // Не відправляти форму
}
    return true;
}

