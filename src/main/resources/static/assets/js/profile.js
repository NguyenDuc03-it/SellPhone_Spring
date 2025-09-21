let orderItems;
let hasLoadedOrders;

// Filter orders function
function filterOrders(status) {
    orderItems.forEach(item => {
        const itemStatus = item.getAttribute('data-status');
        if (status === 'all' || itemStatus === status) {
            item.style.display = 'block';
        } else {
            item.style.display = 'none';
        }
    });

    // Thêm hiệu ứng
    setTimeout(() => {
        orderItems.forEach(item => {
            if (item.style.display !== 'none') {
                item.style.opacity = '0';
                item.style.transform = 'translateY(20px)';

                setTimeout(() => {
                    item.style.transition = 'all 0.3s ease';
                    item.style.opacity = '1';
                    item.style.transform = 'translateY(0)';
                }, 50);
            }
        });
    }, 50);
}

// Hàm xử lý trang profile
document.addEventListener('DOMContentLoaded', function() {
    showLoading();
    console.log('DOM loaded - initializing profile page');

    // Menu navigation
    const menuItems = document.querySelectorAll('.menu-item');
    const contentSections = document.querySelectorAll('.content-section');

    hasLoadedOrders = false;
    // Handle menu item clicks
    menuItems.forEach(item => {
        item.addEventListener('click', async function(e) {
            e.preventDefault();

            // Skip if it's logout
            if (this.classList.contains('logout')) {
                handleLogout();
                return;
            }

            const targetSection = this.getAttribute('data-section');

            // Remove active class from all menu items
            menuItems.forEach(mi => mi.classList.remove('active'));

            // Add active class to clicked item
            this.classList.add('active');

            // Hide all content sections
            contentSections.forEach(section => section.classList.remove('active'));

            // Show target section
            const targetElement = document.getElementById(targetSection + '-section');
            if (targetElement) {
                targetElement.classList.add('active');
            }

            if (targetSection === 'orders' && !hasLoadedOrders) {
                    await loadOrderHistory(); // gọi hàm API
                    hasLoadedOrders = true;
            }
        });
    });

    // tự động mở section tương ứng từ URL nếu có tham số section start
    const urlParams = new URLSearchParams(window.location.search);
    const sectionFromURL = urlParams.get('section');

    if (sectionFromURL) {
        const targetMenuItem = document.querySelector(`.menu-item[data-section="${sectionFromURL}"]`);
        if (targetMenuItem) {
            // Delay một chút để chắc rằng DOM đã load xong
            setTimeout(() => {
                targetMenuItem.click();
            }, 0);
        }
    }
    // tự động mở section tương ứng từ URL nếu có tham số section end

    // Hàm xử lý tab orders
    const orderTabs = document.querySelectorAll('.tab-btn');
    orderItems = document.querySelectorAll('.order-item');

    orderTabs.forEach(tab => {
        tab.addEventListener('click', function() {
            const status = this.getAttribute('data-status');

            // Xoá class active khỏi tất cả tab
            orderTabs.forEach(t => t.classList.remove('active'));

            // Thêm lại class active cho tab được click (tab hiện tại)
            this.classList.add('active');

            // Filter orders
            filterOrders(status);
        });
    });



    // Xử lý nút sửa thông tin tài khoản
    const editBtn = document.getElementById("editBtn");
    const saveBtn = document.getElementById("saveBtn");
    const cancelBtn = document.getElementById("cancelBtn");

    editBtn.addEventListener("click", function () {
        // Enable all input fields
        document.querySelectorAll("#profileForm input, #profileForm select").forEach(input => {
            input.disabled = false;
        });

        // Toggle buttons
        editBtn.style.display = "none";
        saveBtn.style.display = "inline-block";
        cancelBtn.style.display = "inline-block";
    });

    cancelBtn.addEventListener("click", function () {
        // Reload lại trang để hủy chỉnh sửa
        window.location.href = '/user/profile';
    });

    // Handle order actions
    document.addEventListener('click', function(e) {
        const actionBtn = e.target.closest('.btn[data-action]');
        if (actionBtn && actionBtn.closest('.order-actions')) {
            const action = actionBtn.getAttribute('data-action');
            const orderItem = actionBtn.closest('.order-item');
            const orderId = orderItem.querySelector('.order-id').textContent.replace('#', '').trim();

            switch(action) {
                case 'cancel':
                    handleCancelOrder(orderId, orderItem);
                    break;
                case 'view-detail':
                    handleViewOrderDetails(orderId);
                    break;
            }
        }
    });

    function handleCancelOrder(orderId, orderElement) {
        const modal = document.getElementById('cancelOrderModal');
        const orderIdText = modal.querySelector('.order-id-text');
        const confirmBtn = modal.querySelector('.btn-confirm');
        const cancelBtn = modal.querySelector('.btn-cancel');
        const closeBtn = modal.querySelector('.modal-close');

        orderIdText.textContent = `#${orderId}`;
        modal.style.display = 'flex';

        document.getElementById('cancel-order-id-input').value = orderId;

        cancelBtn.onclick = () => modal.style.display = 'none';
        closeBtn.onclick = () => modal.style.display = 'none';
    }

    function handleViewOrderDetails(orderId) {
        console.log(`Viewing details for order ${orderId}`);
        window.location.href = `/user/profile/order-history/order-detail/${orderId}`;
        // Implement order details modal
    }

    function handleContactSeller(orderId) {
        showNotification(`Đang kết nối với người bán cho đơn hàng ${orderId}`, 'info');
        // Implement contact seller functionality
    }

    // Mobile menu toggle (if needed)
    const menuToggle = document.querySelector('.menu-toggle');
    if (menuToggle) {
        menuToggle.addEventListener('click', function() {
            const sidebar = document.querySelector('.profile-sidebar');
            sidebar.classList.toggle('mobile-active');
        });
    }

    // Initialize tab counts
    updateTabCounts();

    // Add smooth scrolling for mobile
    if (window.innerWidth <= 768) {
        menuItems.forEach(item => {
            item.addEventListener('click', function() {
                const content = document.querySelector('.profile-content');
                if (content) {
                    content.scrollIntoView({ behavior: 'smooth', block: 'start' });
                }
            });
        });
    }

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

    // Xử lý nút xác nhận hủy đơn hàng trong modal start
    // Thêm hiệu ứng Loading
    const cancelOrderForm = document.getElementById('cancelOrderForm');
    cancelOrderForm.addEventListener('submit', function(event) {
        const modal = document.getElementById('cancelOrderModal');
        event.preventDefault(); // Ngăn submit mặc định (để show loading trước)

        modal.style.display = 'none'; // Ẩn modal ngay

        showLoading(); // Hiển thị loading

        // Submit form theo cách truyền thống (này sẽ load lại trang theo redirect controller)
        cancelOrderForm.submit();
    });
    // Xử lý nút xác nhận hủy đơn hàng trong modal end

    hideLoading();
    console.log('Profile functionality initialized successfully');
});

// Xử lý cuộn ngang cho phần 'order-tabs' bằng chuột start
// Lấy phần tử có class là 'order-tabs'
const orderTabs = document.querySelector('.order-tabs');

// Biến để lưu trạng thái khi nhấn chuột
let isMouseDown = false;
let startX;
let scrollLeft;

// Thêm sự kiện mousedown để bắt đầu cuộn khi nhấn chuột
orderTabs.addEventListener('mousedown', (e) => {
    isMouseDown = true; // Đặt trạng thái là nhấn chuột
    startX = e.pageX - orderTabs.offsetLeft; // Lưu vị trí chuột khi nhấn
    scrollLeft = orderTabs.scrollLeft; // Lưu vị trí cuộn hiện tại
    orderTabs.style.cursor = 'grabbing'; // Thay đổi con trỏ chuột khi kéo
});

// Thêm sự kiện mousemove để thực hiện cuộn khi kéo chuột
orderTabs.addEventListener('mousemove', (e) => {
    if (!isMouseDown) return; // Nếu không nhấn chuột thì không cuộn

    const x = e.pageX - orderTabs.offsetLeft; // Vị trí chuột khi di chuyển
    const walk = (x - startX) * 1; // Tính toán khoảng cách di chuyển
    orderTabs.scrollLeft = scrollLeft - walk; // Cuộn theo hướng ngang
});

// Thêm sự kiện mouseup để kết thúc cuộn khi thả chuột
orderTabs.addEventListener('mouseup', () => {
    isMouseDown = false; // Đặt trạng thái là không còn nhấn chuột
    orderTabs.style.cursor = 'grab'; // Trả lại con trỏ chuột ban đầu
});

// Thêm sự kiện mouseleave để kết thúc cuộn nếu chuột rời khỏi phần tử
orderTabs.addEventListener('mouseleave', () => {
    isMouseDown = false;
    orderTabs.style.cursor = 'grab';
});
// Xử lý cuộn ngang cho phần 'order-tabs' bằng chuột end

const statusMap = {
    "Chờ thanh toán": "pending-payment",
    "Chờ xử lý": "pending",
    "Đang giao hàng": "shipping",
    "Đang xử lý": "processing",
    "Đã hoàn thành": "completed",
    "Đã hủy": "canceled"
};

function renderActionButtons(statusKey) {
    if (statusKey === 'completed' || statusKey === 'canceled') {
        return `
            <button class="btn btn-outline" data-action="view-detail">Xem chi tiết</button>
        `;
    } else {
        return `
            <button class="btn btn-outline" data-action="view-detail">Xem chi tiết</button>
            <button class="btn btn-danger" data-action="cancel">Hủy đơn hàng</button>
        `;
    }
}

async function loadOrderHistory() {
    const ordersListContainer = document.querySelector('.orders-list');
    showLoading();

    try {
        const response = await fetch('/user/profile/order-history');
        if (!response.ok) throw new Error("Lỗi khi tải dữ liệu đơn hàng");

        const orders = await response.json();

        // Clear cũ
        ordersListContainer.innerHTML = '';

        // Render từng đơn hàng
        orders.forEach(order => {
            const statusKey = statusMap[order.orderStatus] || "unknown";

            const orderElement = document.createElement('div');
            orderElement.className = 'order-item';
            orderElement.setAttribute('data-status', statusKey);

            orderElement.innerHTML = `
                <div class="order-header">
                    <div class="order-info">
                        <span class="order-id">#${order.orderId}</span>
                        <span class="order-date">${order.orderTime}</span>
                    </div>
                    <div class="order-status status-${statusKey}">
                        ${getStatusIcon(statusKey)} ${getStatusText(statusKey)}
                    </div>
                </div>
                <div class="order-products">
                    ${order.productInfos.map(p => `
                        <div class="product-item" onclick="window.location.href='/product-detail/product/${p.productId}'">
                            <img src="${p.imageUrl}" alt="Product">
                            <div class="product-details">
                                <h4>${p.name}</h4>
                                <p>${formatRom(p.rom)} - ${p.color}</p>
                                <span class="quantity">x${p.quantity}</span>
                            </div>
                            <div class="product-price">${formatCurrency(p.price)}</div>
                        </div>
                    `).join('')}
                </div>
                <div class="order-footer">
                    <div class="order-total">
                        <span>Tổng tiền: <strong>${formatCurrency(order.totalPrice)}</strong></span>
                    </div>
                    <div class="order-actions">
                        ${renderActionButtons(statusKey)}
                    </div>
                </div>
            `;

            ordersListContainer.appendChild(orderElement);
        });

        updateTabCounts(); // cập nhật bộ đếm đơn hàng theo trạng thái
    } catch (err) {
        ordersListContainer.innerHTML = `<p class="error-text">Không thể tải dữ liệu đơn hàng!</p>`;
        console.error(err);
    } finally {
        hideLoading();
    }
}

function formatCurrency(amount) {
    return amount.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
}

function getStatusText(status) {
    switch (status.toLowerCase()) {
        case 'pending-payment': return 'Chờ thanh toán';
        case 'shipping': return 'Đang giao hàng';
        case 'pending': return 'Chờ xử lý';
        case 'processing': return 'Đang xử lý';
        case 'completed': return 'Đã hoàn thành';
        case 'canceled': return 'Đã hủy';
        default: return 'Không rõ';
    }
}

function getStatusIcon(status) {
    switch (status.toLowerCase()) {
        case 'pending-payment': return '<i class="fas fa-clock"></i>';
        case 'shipping': return '<i class="fas fa-truck"></i>';
        case 'pending': return '<i class="fas fa-hourglass-half"></i>';
        case 'processing': return '<i class="fas fa-check-circle"></i>';
        case 'completed': return '<i class="fas fa-check-circle"></i>';
        case 'canceled': return '<i class="fas fa-times-circle"></i>';
        default: return '<i class="fas fa-question-circle"></i>';
    }
}

function showLoading(element) {
    if (element) {
        element.disabled = true;
        element.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang xử lý...';
    } else {
        const loader = document.createElement('div');
        loader.id = 'global-loader';
        loader.innerHTML = `
            <div class="loader-backdrop">
                <div class="loader-content">
                    <i class="fas fa-spinner fa-spin fa-2x"></i>
                    <p>Đang xử lý...</p>
                </div>
            </div>
        `;

        loader.style.cssText = `
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            z-index: 9999;
            display: flex;
            justify-content: center;
            align-items: center;
        `;

        const backdrop = loader.querySelector('.loader-backdrop');
        backdrop.style.cssText = `
            background-color: rgba(0,0,0,0.5);
            width: 100%;
            height: 100%;
            display: flex;
            justify-content: center;
            align-items: center;
        `;

        const content = loader.querySelector('.loader-content');
        content.style.cssText = `
            background: white;
            padding: 30px;
            border-radius: 8px;
            text-align: center;
            color: #2563eb;
        `;

        document.body.appendChild(loader);
    }
}
function hideLoading(element) {
    if (element) {
        element.disabled = false;
        // Restore original button text based on context
        if (element.closest('.password-form')) {
            element.innerHTML = 'Cập nhật mật khẩu';
        }
    } else {
        const loader = document.getElementById('global-loader');
        if (loader) {
            loader.remove();
        }
    }
}

function updateTabCounts() {
    const tabs = document.querySelectorAll('.tab-btn');

    orderItems = document.querySelectorAll('.order-item');

    tabs.forEach(tab => {
        const status = tab.getAttribute('data-status');
        let count = 0;

        if (status === 'all') {
            count = orderItems.length;
        } else {
            count = Array.from(orderItems).filter(item => item.getAttribute('data-status') === status).length;
        }

        const countElement = tab.querySelector('.count');
        if (countElement) {
            countElement.textContent = `(${count})`;
        }
    });
}

function formatRom(rom) {
    if (rom >= 1000) {
     return (rom / 1000).toFixed(rom % 1000 === 0 ? 0 : 1) + 'TB';
    } else {
     return rom + 'GB';
    }
}

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