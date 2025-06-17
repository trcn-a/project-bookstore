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

            if (!response.ok) {
                console.error('Помилка відповіді сервера:', response.status);
                return;
            }

            const html = await response.text();

            // Парсимо отриманий HTML
            const tempDiv = document.createElement('div');
            tempDiv.innerHTML = html;

            // Знаходимо оновлену кнопку
            const newButton = tempDiv.querySelector('.cart-button');
            if (!newButton) {
                console.error("Помилка: оновленої кнопки не знайдено!");
                return;
            }

            // Замінюємо стару кнопку на нову
            const oldButton = this.closest('.cart-button');
            if (oldButton) {
                oldButton.replaceWith(newButton);
            }

            // Якщо у новій кнопці є форма, додаємо обробник
            const newForm = newButton.querySelector('form.cart-form');
            if (newForm) {
                handleCartForm(newForm);
            }

            // Оновлюємо лічильник в хедері
            const newCartCountElem = tempDiv.querySelector('#cart-count');
            const headerCartCountElem = document.querySelector('#cart-count');

            if (newCartCountElem && headerCartCountElem) {
                headerCartCountElem.textContent = newCartCountElem.textContent;
            }

        } catch (error) {
            console.error('Помилка:', error);
        }
    });
}

document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.cart-button .cart-form').forEach(handleCartForm);
});
