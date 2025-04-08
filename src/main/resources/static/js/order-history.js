function showOrderDetails(orderId) {
    // Приховуємо всі деталі замовлень
    document.querySelectorAll('[id^="order-"]').forEach(el => el.style.display = 'none');

    // Показуємо деталі вибраного замовлення
    document.getElementById('order-' + orderId).style.display = 'block';

    // Підсвічуємо вибраний рядок в таблиці
    document.querySelectorAll('tr[data-order-id]').forEach(el => {
        el.style.backgroundColor = el.getAttribute('data-order-id') === orderId ? '#f0f0f0' : '';
    });
}

document.addEventListener('DOMContentLoaded', function() {
    const firstOrderId = document.querySelector('tr[data-order-id]')?.getAttribute('data-order-id');
    if (firstOrderId) {
        showOrderDetails(firstOrderId);
    }
}); 