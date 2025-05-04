function handleCartForm(form) {
    form.addEventListener('submit', async function(e) {
        e.preventDefault();

        const formData = new FormData(this);
        const url = this.action;

        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: { 'X-Requested-With': 'XMLHttpRequest' },
                body: formData
            });

            const html = await response.text();

            // Парсимо отриманий HTML
            const tempDiv = document.createElement('div');
            tempDiv.innerHTML = html;
            const newButton = tempDiv.querySelector('.cart-button');

            if (!newButton) {
                console.error("Помилка: оновленої кнопки не знайдено!");
                return;
            }

            // Замінюємо весь блок cart-button
            this.closest('.cart-button').replaceWith(newButton);
            const newForm = newButton.querySelector('.cart-form');
            if (newForm) {
                handleCartForm(newForm);
            }

            // Додаємо обробник до нової форми, якщо вона є


        } catch (error) {
            console.error('Помилка:', error);
        }
    });
}

document.addEventListener('DOMContentLoaded', function() {
    // Шукаємо форми з класом cart-form всередині елементів з класом cart-button
    document.querySelectorAll('.cart-button .cart-form').forEach(handleCartForm);
});

