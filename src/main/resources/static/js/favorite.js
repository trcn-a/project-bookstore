function handleFavoriteForm(form) {

    form.addEventListener('submit', async e => {
        e.preventDefault();
        console.log("Submitting form");

        const response = await fetch(form.action, {
            method: 'POST',
            headers: {'X-Requested-With': 'XMLHttpRequest'},
            body: new FormData(form)
        });

        const html = await response.text();
        console.log("HTML response:", html);

        const parser = new DOMParser();
        const doc = parser.parseFromString(html, 'text/html');
        const newForm = doc.querySelector('.favorite-form');

        if (!newForm) {
            console.error("Помилка: оновленої форми не знайдено!");
            return;
        }

        console.log("Замінюємо форму");
        form.replaceWith(newForm);
        handleFavoriteForm(newForm);
    });

}

document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.favorite-form').forEach(handleFavoriteForm);
}); 