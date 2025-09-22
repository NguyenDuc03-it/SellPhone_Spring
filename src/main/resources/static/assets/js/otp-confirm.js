const inputs = document.querySelectorAll(".otp-inputs input");
const resendBtn = document.getElementById('resendBtn');
const countdownSpan = document.getElementById('countdown');
let countdown;
let timer = 60;

// Hàm bắt đầu đếm ngược
function startCountdown() {
    resendBtn.classList.add('disabled');
    countdown = setInterval(() => {
        timer--;
        countdownSpan.textContent = timer;
        if (timer <= 0) {
            clearInterval(countdown);
            resendBtn.classList.remove('disabled');
            resendBtn.innerHTML = "Gửi lại";
            timer = 60; // Reset thời gian đếm
        }
    }, 1000);
}

// Bắt đầu đếm ngược khi trang được tải xong
window.addEventListener('load', startCountdown);

// Phần xử lý khi nhấn Gửi lại
resendBtn.addEventListener('click', function (e) {
    e.preventDefault();
    if (this.classList.contains('disabled')) return;

    this.classList.add('disabled');
    this.innerHTML = `<i class="fas fa-spinner fa-spin"></i> Đang gửi...`;

    // Gửi yêu cầu POST bằng fetch
    fetch("/forgot-password/send-mail/resend-otp", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        }
    })
    .then(response => {
        if (response.ok) {
            resendBtn.innerHTML = `Gửi lại (<span id="countdown">${timer}</span>s)`;
            startCountdown();
        } else {
            showErrorNotification("Không thể gửi lại OTP. Vui lòng thử lại sau.");
        }
    })
    .catch(error => {
        console.error("Lỗi khi gửi lại OTP:", error);
        showErrorNotification("Đã có lỗi xảy ra. Vui lòng thử lại sau.");
    });
});

inputs.forEach((input, index) => {
    input.addEventListener("keyup", (e) => {
        const currentInput = input,
              nextInput = input.nextElementSibling,
              prevInput = input.previousElementSibling;

        if (currentInput.value.length > 1) {
            currentInput.value = "";
            return;
        }

        // Nếu ô input kế bị disable và ô hiện tại có giá trị thì tắt disable ô input kế và chuyển focus sang ô đó
        if (nextInput && nextInput.hasAttribute("disabled") && currentInput.value !== "") {
            nextInput.removeAttribute("disabled");
            nextInput.focus();
        }

        // Trường hợp nút backspace hoặc nút delete được nhấn
        if (e.key === "Backspace" || e.key === "Delete") {
            // Nếu ô input hiện tại trống và trước đó có ô input thì chuyển focus sang ô đó
            if(prevInput) {
                currentInput.setAttribute("disabled", true);
                currentInput.value = "";
                prevInput.focus();
            }
        }

        checkAllInputsFilled();
    });
});

const submitBtn = document.getElementById("submitBtn");
function checkAllInputsFilled() {
    let allFilled = true;
    inputs.forEach(input => {
        if (input.value.trim() === "") {
            allFilled = false;
        }
    });
    submitBtn.disabled = !allFilled;
}

// Xử lý hợp nhất 6 số otp thành 1 dãy số để gửi xuống BE
document.getElementById('otp-form').addEventListener('submit', function(event) {
    event.preventDefault();  // Ngừng gửi form để xử lý trước

    // Lấy giá trị của tất cả các ô input trong form OTP
    let otp = '';
    const inputs = document.querySelectorAll('input[name="otp"]');
    inputs.forEach(input => {
        otp += input.value;  // Kết hợp giá trị của tất cả các ô nhập OTP
    });

    console.log("OTP nhập vào: " + otp);

    // Xóa các input cũ
    inputs.forEach(input => input.remove());

    // Thêm mã OTP vào một trường ẩn
    const otpField = document.createElement("input");
    otpField.type = "hidden";
    otpField.name = "otp";  // Tên trường nhận OTP
    otpField.value = otp;

     // Loading trên nút
    const submitBtn = document.getElementById('submitBtn');
    submitBtn.disabled = true;
    submitBtn.innerHTML = `<span class="fas fa-spinner fa-spin"></span> Đang xác nhận...`;

    // Thêm trường ẩn vào form
    this.appendChild(otpField);

    // Gửi form
    this.submit();
});

// Hiển thị thông báo lỗi
function showErrorNotification(message) {
    const errorNotification = document.getElementById('errorNotificationn');
    const errorMessageText = document.getElementById('errorMessageText');
    const errorProgressBar = document.getElementById('progresss');

    errorMessageText.textContent = message;
    errorNotification.style.display = 'block';

    let errorProgress = 100;
    errorProgressBar.style.width = '100%';

    const errorInterval = setInterval(() => {
        if (errorProgress > 0) {
            errorProgress -= 1;
            errorProgressBar.style.width = errorProgress + '%';
        } else {
            clearInterval(errorInterval);
            setTimeout(() => {
                errorNotification.style.display = 'none';
            }, 500);
        }
    }, 30);

    // Tự ẩn sau 5 giây
    setTimeout(() => {
        if (errorProgress > 0) {
            clearInterval(errorInterval);
            errorProgressBar.style.width = '0%';
            errorNotification.style.display = 'none';
        }
    }, 5000);
}