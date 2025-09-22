document.getElementById('addCustomerForm').addEventListener('submit', function(event) {
    var password = document.getElementById('password').value;
    var confirmPassword = document.getElementById('confirmPassword').value;
    const confirmPasswordGroup = document.getElementById('confirmPasswordGroup');

    // Kiểm tra xem mật khẩu và xác nhận mật khẩu có khớp không
    if (password !== confirmPassword) {
        // Nếu không khớp, hiển thị thông báo lỗi và ngừng gửi form
        document.getElementById('passwordError').style.display = 'block';
        confirmPasswordGroup.style.marginBottom = '0';
        event.preventDefault(); // Ngừng gửi form
    } else {
        // Nếu khớp, ẩn thông báo lỗi
        document.getElementById('passwordError').style.display = 'none';
        confirmPasswordGroup.style.marginBottom = '';
    }
});