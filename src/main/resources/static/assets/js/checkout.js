document.addEventListener('DOMContentLoaded', function () {
    const paymentOptions = document.querySelectorAll('.payment-option');

    paymentOptions.forEach(option => {
      option.addEventListener('click', () => {
        // Bỏ class active ở tất cả option
        paymentOptions.forEach(opt => opt.classList.remove('active'));

        // Thêm class active vào option đang được click
        option.classList.add('active');

        // Đánh dấu radio tương ứng là checked
        const radio = option.querySelector('input[type="radio"]');
        if (radio) {
          radio.checked = true;
        }
      });
    });
});

document.getElementById('checkout-btn').addEventListener('click', async function () {
    // Lấy phương thức thanh toán đã chọn
    const selectedMethod = document.querySelector('input[name="payment"]:checked').value;

    // Địa chỉ người nhận
    const address = document.querySelector('#customer-address').value;

    try {
        const response = await fetch('/user/checkout/payment', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: new URLSearchParams({
                method: selectedMethod,
                address: address,
            })
        });

        if (!response.ok) throw new Error('Lỗi server');

        const data = await response.json();
        const redirectUrl = data.redirectUrl;
        window.location.href = redirectUrl;

    } catch (error) {
        alert('Có lỗi xảy ra khi xử lý đơn hàng. Vui lòng thử lại.');
        console.error(error);
    }
});