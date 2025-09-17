document.addEventListener('DOMContentLoaded', function () {
    const editBtn = document.getElementById('editBtn');
    const saveBtn = document.getElementById('saveBtn');
    const cancelBtn = document.getElementById('cancelBtn');

    editBtn.addEventListener("click", function () {
        // Enable all input fields
        document.querySelectorAll("#profileForm input, #profileForm select").forEach(input => {
          if (input.id !== 'staffId') {
              input.disabled = false;
          }
        });

        // Toggle buttons
        editBtn.style.display = "none";
        saveBtn.style.display = "inline-block";
        cancelBtn.style.display = "inline-block";
    });

    cancelBtn.addEventListener("click", function () {
        document.querySelectorAll("#profileForm input, #profileForm select").forEach(input => {
            input.disabled = true;
        });

        // Toggle buttons
        editBtn.style.display = "inline-block";
        saveBtn.style.display = "none";
        cancelBtn.style.display = "none";
    });

    document.getElementById('changePasswordForm').addEventListener('submit', function(event) {
        const newPassword = document.getElementById('newPassword').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        const currentPassword = document.getElementById('currentPassword').value;

        if (newPassword !== confirmPassword) {
            event.preventDefault(); // Ngăn form submit
            document.getElementById('passwordMatchMessage').style.display = 'block';
        }
        else {
            document.getElementById('passwordMatchMessage').style.display = 'none';
        }
    });
});

document.addEventListener('DOMContentLoaded', function () {
    // Lấy tham số section từ URL
    const urlParams = new URLSearchParams(window.location.search);
    const section = urlParams.get('section');

    if (section === 'password') {
        // Kích hoạt tab "Đổi mật khẩu"
        // Dùng jQuery Bootstrap Tab method
        $('#v-pills-password-tab').tab('show');
    } else {
        // Mặc định là tab Thông tin tài khoản
        $('#v-pills-info-tab').tab('show');
    }
});
