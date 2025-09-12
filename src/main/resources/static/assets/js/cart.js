
// Giỏ hàng
document.addEventListener('DOMContentLoaded', async function() {
    console.log('DOM loaded - initializing cart page');

    const selectAllCheckbox = document.getElementById('selectAll');
    const itemCheckboxes = document.querySelectorAll('.item-checkbox');
    const checkoutBtn = document.getElementById('checkoutBtn');
    const subtotalElement = document.getElementById('subtotal');
    const totalElement = document.getElementById('total');
    const selectedCountElement = document.getElementById('selectedCount');
    const checkoutCountElement = document.getElementById('checkoutCount');

    if (!selectAllCheckbox || !checkoutBtn || !subtotalElement || !totalElement) {
        console.error('Required cart elements not found');
        return;
    }

    // Chọn nhiều items
    selectAllCheckbox.addEventListener('change', function() {
    itemCheckboxes.forEach(checkbox => {
        checkbox.checked = this.checked;
    });
    updateSummary();
    });

    // Chọn từng items
    itemCheckboxes.forEach(checkbox => {
    checkbox.addEventListener('change', function() {
        updateSelectAll();
        updateSummary();
    });
});

// Hiển thị thông báo lỗi
function showErrorNotification(message) {
    const errorNotification = document.getElementById('errorNotification');
    const errorMessageText = document.getElementById('errorMessageText');
    const errorProgressBar = document.getElementById('progress');

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

// Cập nhật số lượng
document.querySelectorAll('.qty-btn').forEach(btn => {
    btn.addEventListener('click', function () {
        const cartItem = this.closest('.cart-item');
        const cartItemId = cartItem.getAttribute('data-cart-item-id');
        const qtyInput = cartItem.querySelector('.qty-input');
        const checkbox = cartItem.querySelector('.item-checkbox');
        const currentQty = parseInt(qtyInput.value);
        const unitPrice = parseInt(checkbox.getAttribute('data-price'));
        const stock = parseInt(cartItem.getAttribute('data-stock'));

        const decreaseBtn = cartItem.querySelector('.qty-btn.decrease');
        const increaseBtn = cartItem.querySelector('.qty-btn.increase');

        let newQty = currentQty;

        // Không cho thao tác liên tục
        if (increaseBtn.disabled || decreaseBtn.disabled) return;

        // Tăng hoặc giảm số lượng
        if (this.classList.contains('increase')) {
            if (currentQty < stock) {
                newQty = currentQty + 1;
            } else {
                showErrorNotification("Không thể tăng quá số lượng còn lại");
                return;
            }
        } else if (this.classList.contains('decrease') && currentQty > 1) {
            newQty = currentQty - 1;
        } else {
            return;
        }

        // Disable thao tác
        increaseBtn.disabled = true;
        decreaseBtn.disabled = true;

        // Update UI tạm thời
        qtyInput.value = newQty;
        const itemTotal = cartItem.querySelector('.total-price');
        itemTotal.textContent = (unitPrice * newQty).toLocaleString('vi-VN') + '₫';
        updateSummary();

        // Gửi request cập nhật
        fetch('/user/cart/update-quantity', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: `cartItemId=${encodeURIComponent(cartItemId)}&quantity=${encodeURIComponent(newQty)}`
        })
        .then(response => {
            if (!response.ok) {
                if (response.status === 401) {
                    window.location.href = `/login?redirect=${encodeURIComponent(window.location.href)}`;
                    return;
                }
                return response.json().then(err => {
                    throw new Error(err.message || 'Cập nhật thất bại');
                });
            }
            return response.json();
        })
        .then(data => {
            if (!data.success) {
                showErrorNotification(data.message || 'Cập nhật thất bại');
                // Nếu thất bại → revert lại số lượng cũ?
            }
        })
        .catch(error => {
            showErrorNotification(error.message);
            console.error(error);
        })
        .finally(() => {
            // Mở lại thao tác
            increaseBtn.disabled = false;
            decreaseBtn.disabled = false;
        });
    });
});

// Hàm xóa một item trong giỏ hàng
document.querySelectorAll('.btn-action').forEach(btn => {
    if (btn.querySelector('.fa-trash')) {
        btn.addEventListener('click', function () {
            const modal = document.getElementById('deleteModal');
            const cartItem = this.closest('.cart-item');
            const cartItemId = cartItem.getAttribute('data-cart-item-id');

            modal.style.display = 'flex';

            const modalBody = modal.querySelector('.modal-body p');
            modalBody.textContent = 'Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng?';

            modal.querySelector('.btn-confirm').onclick = function () {
                fetch('/user/cart/delete-cart-item', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    body: `cartItemId=${encodeURIComponent(cartItemId)}`
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        cartItem.remove();
                        updateSummary();
                        updateSelectAll();
                        ComponentLoader.showNotification('Đã xóa sản phẩm thành công!', 'success');
                    } else {
                        ComponentLoader.showNotification(data.message || 'Xóa sản phẩm thất bại', 'error');
                    }
                })
                .catch(err => {
                    ComponentLoader.showNotification('Lỗi máy chủ!', 'error');
                    console.error(err);
                })
                .finally(() => {
                    modal.style.display = 'none';
                });
            };

            modal.querySelector('.btn-cancel').onclick = () => modal.style.display = 'none';
            modal.querySelector('.modal-close').onclick = () => modal.style.display = 'none';
        });
    }
});

// Hàm xóa nhiều item trong giỏ hàng
const deleteSelectedBtn = document.getElementById('deleteSelected');
if (deleteSelectedBtn) {
    deleteSelectedBtn.addEventListener('click', function () {
        const checkedBoxes = document.querySelectorAll('.item-checkbox:checked');

        if (checkedBoxes.length === 0) {
            ComponentLoader.showNotification('Vui lòng chọn sản phẩm cần xóa!', 'warning');
            return;
        }

        const modal = document.getElementById('deleteModal');
        const modalBody = modal.querySelector('.modal-body p');

        modal.style.display = 'flex';
        modalBody.textContent = `Bạn có chắc chắn muốn xóa ${checkedBoxes.length} sản phẩm đã chọn khỏi giỏ hàng?`;

        modal.querySelector('.btn-confirm').onclick = function () {
            const cartItemIds = Array.from(checkedBoxes).map(checkbox => {
                const cartItem = checkbox.closest('.cart-item');
                return cartItem.getAttribute('data-cart-item-id');
            });

            const bodyData = new URLSearchParams();
            cartItemIds.forEach(id => bodyData.append('ids', id));

            fetch('/user/cart/delete-multiple', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: bodyData.toString()
            })
            .then(res => {
                if (res.ok) {
                    checkedBoxes.forEach(checkbox => {
                        const cartItem = checkbox.closest('.cart-item');
                        cartItem.remove();
                    });
                    updateSummary();
                    updateSelectAll();
                    ComponentLoader.showNotification('Đã xóa sản phẩm thành công!', 'success');
                } else {
                    ComponentLoader.showNotification('Xóa sản phẩm thất bại!', 'error');
                }
            })
            .catch(err => {
                ComponentLoader.showNotification('Lỗi máy chủ!', 'error');
                console.error(err);
            })
            .finally(() => {
                modal.style.display = 'none';
                modalBody.textContent = 'Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng?';
            });
        };

        modal.querySelector('.btn-cancel').onclick = function () {
            modal.style.display = 'none';
            modalBody.textContent = 'Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng?';
        };

        modal.querySelector('.modal-close').onclick = function () {
            modal.style.display = 'none';
            modalBody.textContent = 'Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng?';
        };
    });
}

// Update select all checkbox based on individual selections
function updateSelectAll() {
    const currentCheckboxes = document.querySelectorAll('.item-checkbox');
    const checkedBoxes = document.querySelectorAll('.item-checkbox:checked');

    if (currentCheckboxes.length === 0) {
        selectAllCheckbox.checked = false;
        selectAllCheckbox.indeterminate = false;
        return;
    }

    selectAllCheckbox.checked = checkedBoxes.length === currentCheckboxes.length;
    selectAllCheckbox.indeterminate = checkedBoxes.length > 0 && checkedBoxes.length < currentCheckboxes.length;
}

// Cập nhật tóm tắt đơn hàng và nút mua
function updateSummary() {
    const checkedBoxes = document.querySelectorAll('.item-checkbox:checked');
    let total = 0;
    let count = 0;

    checkedBoxes.forEach(checkbox => {
        const cartItem = checkbox.closest('.cart-item');
        const qty = parseInt(cartItem.querySelector('.qty-input').value);
        const price = parseInt(checkbox.getAttribute('data-price'));
        total += price * qty;
        count += qty;
    });

    if (subtotalElement) subtotalElement.textContent = total.toLocaleString('vi-VN') + '₫';
    if (totalElement) totalElement.textContent = total.toLocaleString('vi-VN') + '₫';
    if (selectedCountElement) selectedCountElement.textContent = count;
    if (checkoutCountElement) checkoutCountElement.textContent = count;

    // Enable/disable checkout button
    if (checkoutBtn) {
        checkoutBtn.disabled = checkedBoxes.length === 0;
    }

    // Update select all label
    const totalItems = document.querySelectorAll('.cart-item').length;
    const selectAllLabel = document.querySelector('label[for="selectAll"]');
    if (selectAllLabel) {
        selectAllLabel.textContent = `Chọn tất cả (${totalItems} sản phẩm)`;
    }
}

// Checkout functionality
if (checkoutBtn) {
checkoutBtn.addEventListener('click', function () {
    const checkedBoxes = document.querySelectorAll('.item-checkbox:checked');
    if (checkedBoxes.length === 0) {
        ComponentLoader.showNotification('Vui lòng chọn sản phẩm cần thanh toán!', 'warning');
        return;
    }

    // Tạo danh sách sản phẩm để thanh toán
    const selectedItems = Array.from(checkedBoxes).map(checkbox => {
        const cartItem = checkbox.closest('.cart-item');
        return {
            productId: parseInt(cartItem.getAttribute('data-product-id')),
            productName: cartItem.querySelector('.item-name').textContent.trim(),
            color: cartItem.querySelector('.item-color').textContent.trim(),
            rom: parseInt(cartItem.querySelector('.item-rom').textContent.replace('GB', '').replace('TB', '000')),
            price: parseInt(checkbox.getAttribute('data-price')),
            quantity: parseInt(cartItem.querySelector('.qty-input').value)
        };
    });

    // Gửi request đến server
    fetch('/user/checkout/from-cart', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(selectedItems)
    })
    .then(response => response.json())
    .then(data => {
        if (data.redirectUrl) {
            window.location.href = data.redirectUrl;
        } else {
            ComponentLoader.showNotification('Không thể chuyển sang trang thanh toán!', 'error');
        }
    })
    .catch(error => {
        console.error('Checkout error:', error);
        ComponentLoader.showNotification('Lỗi máy chủ!', 'error');
    });
});
}

// Đóng modal xóa item khi nhấn ra ngoài
const deleteModal = document.getElementById('deleteModal');
if (deleteModal) {
deleteModal.addEventListener('click', function(e) {
    if (e.target === this) {
        this.style.display = 'none';
    }
});
}

// Initialize summary
updateSummary();

console.log('Các hàm js giỏ hàng đã được khởi tạo thành công');
});