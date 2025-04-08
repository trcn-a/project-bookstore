function handleFavoriteForm(form) {
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
            const newForm = tempDiv.querySelector('.favorite-form');

            if (!newForm) {
                console.error("Помилка: оновленої форми не знайдено!");
                return;
            }

            form.replaceWith(newForm); // Замінюємо форму повністю
            handleFavoriteForm(newForm); // Додаємо обробник одразу після заміни

        } catch (error) {
            console.error('Помилка:', error);
        }
    });
}

document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.favorite-form').forEach(handleFavoriteForm);
}); 