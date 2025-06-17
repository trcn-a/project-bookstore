function initBookForm() {
    console.log('Ініціалізація скриптів для фрагмента');

    $('.select2').select2({
        tags: true,
        placeholder: 'Оберіть або введіть значення',
        allowClear: true
    });

    function updateActualPrice() {
        const price = parseFloat($('#price').val());
        const discount = parseFloat($('#discount').val());

        if (!isNaN(price)) {
            const validDiscount = (!isNaN(discount) && discount > 0 && discount <= 100) ? discount : 0;
            const rawPrice = price * (1 - validDiscount / 100);
            const actualPrice = Math.ceil(rawPrice);
            $('#actualPrice').val(actualPrice);
        } else {
            $('#actualPrice').val('');
        }
    }

    updateActualPrice();
    $('#price, #discount').off('input').on('input', updateActualPrice);

    $('#coverImageFile').off('change').on('change', function(event) {
        console.log('Файл обрано');
        const file = event.target.files[0];
        if (file) {
            console.log('Файл:', file.name);
            const reader = new FileReader();
            reader.onload = function(e) {
                console.log('Файл прочитано, відображення прев’ю...');
                const preview = document.getElementById('imagePreview');
                preview.style.backgroundImage = `url(${e.target.result})`;
                const span = preview.querySelector('span');
                if (span) span.style.display = 'none';
            };
            reader.readAsDataURL(file);
        }
    });
}
