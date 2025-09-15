// Hàm xử lý trang profile
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM loaded - initializing profile page');

    // Menu navigation
    const menuItems = document.querySelectorAll('.menu-item');
    const contentSections = document.querySelectorAll('.content-section');

    // Handle menu item clicks
    menuItems.forEach(item => {
        item.addEventListener('click', function(e) {
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
    const orderItems = document.querySelectorAll('.order-item');

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

    // Filter orders function
    function filterOrders(status) {
        orderItems.forEach(item => {
            if (status === 'all') {
                item.style.display = 'block';
            } else {
                const itemStatus = item.getAttribute('data-status');
                if (itemStatus === status) {
                    item.style.display = 'block';
                } else {
                    item.style.display = 'none';
                }
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

    // Xử lý nút sửa thông tin tài khoản
    const editProfileBtn = document.querySelector('.edit-profile-btn');
    if (editProfileBtn) {
        editProfileBtn.addEventListener('click', function() {
            // Toggle edit mode
            toggleEditMode.call(this);
        });
    }

    // Toggle edit mode function
    function toggleEditMode() {
        const infoItems = document.querySelectorAll('.info-item p');
        const isEditing = this.textContent.includes('Lưu');

        if (!isEditing) {
            // Switch to edit mode
            infoItems.forEach(p => {
                const currentText = p.textContent;
                const input = document.createElement('input');
                input.type = 'text';
                input.value = currentText;
                input.className = 'form-control';
                p.parentNode.replaceChild(input, p);
            });

            this.innerHTML = '<i class="fas fa-save"></i> Lưu';
            this.className = 'btn btn-success edit-profile-btn';
        } else {
            // Switch back to view mode
            const inputs = document.querySelectorAll('.info-item input');
            inputs.forEach(input => {
                const p = document.createElement('p');
                p.textContent = input.value;
                input.parentNode.replaceChild(p, input);
            });

            this.innerHTML = '<i class="fas fa-edit"></i> Chỉnh sửa';
            this.className = 'btn btn-primary edit-profile-btn';

            // Show success message
            showNotification('Thông tin đã được cập nhật thành công!', 'success');
        }
    }

    // Handle password change form
    const passwordForm = document.querySelector('.password-form');
    if (passwordForm) {
        const submitBtn = passwordForm.querySelector('.btn-primary');

        if (submitBtn) {
            submitBtn.addEventListener('click', function(e) {
                e.preventDefault();

                const passwordInputs = passwordForm.querySelectorAll('input[type="password"]');
                const currentPassword = passwordInputs[0]?.value || '';
                const newPassword = passwordInputs[1]?.value || '';
                const confirmPassword = passwordInputs[2]?.value || '';

                // Basic validation
                if (!currentPassword || !newPassword || !confirmPassword) {
                    showNotification('Vui lòng điền đầy đủ thông tin!', 'error');
                    return;
                }

                if (newPassword !== confirmPassword) {
                    showNotification('Mật khẩu xác nhận không khớp!', 'error');
                    return;
                }

                if (newPassword.length < 6) {
                    showNotification('Mật khẩu mới phải có ít nhất 6 ký tự!', 'error');
                    return;
                }

                // Simulate API call
                showLoading(submitBtn);

                setTimeout(() => {
                    hideLoading(submitBtn);
                    showNotification('Mật khẩu đã được thay đổi thành công!', 'success');

                    // Clear form
                    passwordForm.querySelectorAll('input').forEach(input => {
                        input.value = '';
                    });
                }, 1500);
            });
        }
    }

    // Handle order actions
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('btn') && e.target.closest('.order-actions')) {
            const action = e.target.textContent.trim();
            const orderId = e.target.closest('.order-item').querySelector('.order-id').textContent;

            switch(action) {
                case 'Theo dõi đơn hàng':
                    handleTrackOrder(orderId);
                    break;
                case 'Hủy đơn hàng':
                    handleCancelOrder(orderId, e.target.closest('.order-item'));
                    break;
                case 'Xem chi tiết':
                    handleViewOrderDetails(orderId);
                    break;
                case 'Liên hệ người bán':
                    handleContactSeller(orderId);
                    break;
            }
        }
    });

    // Order action handlers
    function handleTrackOrder(orderId) {
        showNotification(`Đang theo dõi đơn hàng ${orderId}`, 'info');
        // Implement tracking logic
    }

    function handleCancelOrder(orderId, orderElement) {
        if (confirm(`Bạn có chắc chắn muốn hủy đơn hàng ${orderId}?`)) {
            showLoading();

            setTimeout(() => {
                // Update order status
                const statusElement = orderElement.querySelector('.order-status');
                statusElement.className = 'order-status status-cancelled';
                statusElement.innerHTML = '<i class="fas fa-times-circle"></i> Đã hủy';

                // Update order data attribute
                orderElement.setAttribute('data-status', 'cancelled');

                // Remove cancel button
                const cancelBtn = orderElement.querySelector('.btn-danger');
                if (cancelBtn) {
                    cancelBtn.remove();
                }

                hideLoading();
                showNotification(`Đơn hàng ${orderId} đã được hủy thành công!`, 'success');

                // Update tab counts
                updateTabCounts();
            }, 1000);
        }
    }

    function handleViewOrderDetails(orderId) {
        showNotification(`Đang xem chi tiết đơn hàng ${orderId}`, 'info');
        // Implement order details modal
    }

    function handleContactSeller(orderId) {
        showNotification(`Đang kết nối với người bán cho đơn hàng ${orderId}`, 'info');
        // Implement contact seller functionality
    }

    // Update tab counts
    function updateTabCounts() {
        const tabs = document.querySelectorAll('.tab-btn');

        tabs.forEach(tab => {
            const status = tab.getAttribute('data-status');
            let count = 0;

            if (status === 'all') {
                count = orderItems.length;
            } else {
                count = document.querySelectorAll(`[data-status="${status}"]`).length;
            }

            const countElement = tab.querySelector('.count');
            if (countElement) {
                countElement.textContent = `(${count})`;
            }
        });
    }

    // Logout handler
    function handleLogout() {
        if (confirm('Bạn có chắc chắn muốn đăng xuất?')) {
            showLoading();

            setTimeout(() => {
                ComponentLoader.logout();
                showNotification('Đã đăng xuất thành công!', 'success');
                // Redirect to home page
                setTimeout(() => {
                    window.location.href = '../index.html';
                }, 1500);
            }, 1000);
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