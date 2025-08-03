document.addEventListener("DOMContentLoaded", function () {
    fetch("/management/orders/pending-count")
        .then(response => response.json())
        .then(count => {
            const badge = document.getElementById("pendingOrdersBadge");
            if (badge) {
                if (count > 0) {
                    badge.textContent = count;
                    badge.style.display = "inline-block";
                } else {
                    badge.style.display = "none";
                }
            }
        });
});
