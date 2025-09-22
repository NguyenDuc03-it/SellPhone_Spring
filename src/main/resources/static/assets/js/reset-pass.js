// Hiển thị/ẩn mật khẩu
function setupPasswordToggle(toggleId, passwordId) {
    const toggle = document.getElementById(toggleId);
    const password = document.getElementById(passwordId);

    toggle.addEventListener('click', function () {
        // Đổi kiểu input khi click vào icon mắt
        const type = password.getAttribute('type') === 'password' ? 'text' : 'password';
        password.setAttribute('type', type);

        // Toggle the icon
        this.classList.toggle('fa-eye');
        this.classList.toggle('fa-eye-slash');
    });
}

setupPasswordToggle('toggle_new_password', 'new_password');
setupPasswordToggle('toggle_confirm_password', 'confirm_password');

document.getElementById('reset-form').addEventListener('submit', function(event) {
    var password = document.getElementById('new_password').value;
    var confirmPassword = document.getElementById('confirm_password').value;
    const confirmPasswordGroup = document.getElementById('confirmPasswordGroup');

    // Kiểm tra xem mật khẩu và xác nhận mật khẩu có khớp không
    if (password !== confirmPassword) {
        // Nếu không khớp, hiển thị thông báo lỗi và ngừng gửi form
        document.getElementById('passwordError').style.display = 'block';
        confirmPasswordGroup.style.marginBottom = '10px';
        event.preventDefault(); // Ngừng gửi form
    } else {
        // Nếu khớp, ẩn thông báo lỗi
        document.getElementById('passwordError').style.display = 'none';
        confirmPasswordGroup.style.marginBottom = '';
    }
});