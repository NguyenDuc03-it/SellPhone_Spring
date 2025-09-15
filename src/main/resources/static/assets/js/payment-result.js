let seconds = 5;
const countdownElement = document.getElementById('countdown');

const countdownInterval = setInterval(() => {
    seconds--;
    if (countdownElement) {
        countdownElement.textContent = seconds;
    }

    if (seconds <= 0) {
        clearInterval(countdownInterval);
        window.location.href = "/user/profile?section=orders";
    }
}, 1000);