document.addEventListener('DOMContentLoaded', function () {
    const searchInput = document.getElementById('search-input');
    const searchResults = document.getElementById('search-results');
    const searchModal = document.getElementById('search-modal');
    const closeModal = document.getElementById('close-search-modal');
    let searchTimeout;

    function showModal() {
        searchModal.classList.add('show');
        searchModal.style.display = 'block';
        setTimeout(() => {
            searchModal.style.opacity = '1';
            searchModal.style.transform = 'translateY(0)';
        }, 10);
    }

    function hideModal() {
        searchModal.style.opacity = '0';
        searchModal.style.transform = 'translateY(-20px)';
        setTimeout(() => {
            searchModal.style.display = 'none';
        }, 200);
    }

    searchInput.addEventListener('input', function () {
        clearTimeout(searchTimeout);
        const query = this.value.trim();

        if (query.length < 2) {
            hideModal();
            return;
        }

        showModal();
        searchResults.innerHTML = '<div class="loading">Пошук...</div>';

        searchTimeout = setTimeout(() => {
            fetch(`/search/suggestions?query=${encodeURIComponent(query)}`)
                .then(response => response.text())
                .then(html => {
                    searchResults.innerHTML = html;
                })
                .catch(error => {
                    console.error('Помилка при пошуку:', error);
                    searchResults.innerHTML = '<div class="error">Помилка при пошуку</div>';
                });
        }, 300);
    });

    document.addEventListener('click', function (e) {
        if (!searchModal.contains(e.target) && e.target !== searchInput) {
            hideModal();
        }
    });

    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') {
            hideModal();
        }
    });

    searchResults.addEventListener('click', function (e) {
        e.stopPropagation();
    });

    if (closeModal) {
        closeModal.addEventListener('click', hideModal);
    }
});
