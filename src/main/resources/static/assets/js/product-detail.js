// Component loader - tự động load CSS theo component
class ComponentLoader {
    static async loadComponent(componentName, containerId) {
        try {
            // Load CSS trước
            await this.loadCSS(`../${componentName}.css`);

            // Load HTML sau
            const response = await fetch(`../${componentName}.html`);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const htmlContent = await response.text();

            // Parse HTML và lấy nội dung body
            const parser = new DOMParser();
            const doc = parser.parseFromString(htmlContent, 'text/html');
            const bodyContent = doc.body.innerHTML;

            // Insert vào container
            const container = document.getElementById(containerId);
            if (container) {
                container.innerHTML = bodyContent;
            }

            // Initialize component sau khi load
            await this.initializeComponent(componentName);

            return true;
        } catch (error) {
            console.warn(`Không thể load ${componentName}:`, error);
            return false;
        }
    }

    static loadCSS(href) {
        return new Promise((resolve, reject) => {
            if (document.querySelector(`link[href="${href}"]`)) {
                resolve();
                return;
            }

            const link = document.createElement('link');
            link.rel = 'stylesheet';
            link.href = href;
            link.onload = () => resolve();
            link.onerror = () => resolve(); // Không reject để không block
            document.head.appendChild(link);
        });
    }

    static async initializeComponent(componentName) {
        if (componentName === 'header') {
            await this.initializeHeader();
        } else if (componentName === 'footer') {
            this.initializeFooter();
        }
    }

    static async initializeHeader() {
        await new Promise(resolve => setTimeout(resolve, 100));

        // Mobile menu toggle
        const menuToggle = document.querySelector('.menu-toggle');
        const navMenu = document.querySelector('.nav-menu');

        if (menuToggle && navMenu) {
            menuToggle.addEventListener('click', function() {
                navMenu.classList.toggle('active');
            });
        }

        // Search functionality
        const searchButton = document.querySelector('.search-bar button');
        const searchInput = document.querySelector('.search-bar input');

        if (searchButton && searchInput) {
            searchButton.addEventListener('click', function() {
                const searchTerm = searchInput.value.trim();
                if (searchTerm) {
                    console.log('Searching for:', searchTerm);
                    // window.location.href = `search.html?q=${encodeURIComponent(searchTerm)}`;
                }
            });

            searchInput.addEventListener('keypress', function(e) {
                if (e.key === 'Enter') {
                    searchButton.click();
                }
            });
        }

        // Initialize login functionality
        this.initializeLogin();
    }

    static initializeLogin() {
        this.restoreLoginState();

        const loginForm = document.getElementById('loginForm');
        if (loginForm) {
            loginForm.addEventListener('submit', function(e) {
                e.preventDefault();

                const email = document.getElementById('email').value;
                const password = document.getElementById('password').value;
                const loginError = document.getElementById('loginError');

                console.log('Login attempt:', { email, password });

                const sampleAccount = {
                    email: 'admin@tdmobile.com',
                    password: '123456',
                    name: 'Trung Đức'
                };

                if (email === sampleAccount.email && password === sampleAccount.password) {
                    console.log('Login successful!');

                    this.saveLoginState(sampleAccount);

                    if (loginError) loginError.classList.add('d-none');

                    const loginModal = document.getElementById('loginModal');
                    if (loginModal) {
                        const modal = bootstrap.Modal.getInstance(loginModal) || new bootstrap.Modal(loginModal);
                        modal.hide();
                    }

                    this.showUserInfo();

                    this.showNotification('Đăng nhập thành công!', 'success');

                    loginForm.reset();
                } else {
                    console.log('Login failed - wrong credentials');
                    if (loginError) {
                        loginError.classList.remove('d-none');
                    } else {
                        this.showNotification('Email hoặc mật khẩu không đúng!', 'danger');
                    }
                }
            }.bind(this));
        }
    }

    static saveLoginState(user) {
        localStorage.setItem('isLoggedIn', 'true');
        localStorage.setItem('currentUser', JSON.stringify(user));
    }

    static restoreLoginState() {
        const savedLoginState = localStorage.getItem('isLoggedIn');
        const savedUser = localStorage.getItem('currentUser');

        if (savedLoginState === 'true' && savedUser) {
            try {
                const currentUser = JSON.parse(savedUser);
                setTimeout(() => {
                    this.showUserInfo(currentUser);
                }, 200);
            } catch (error) {
                console.error('Error parsing saved user:', error);
                localStorage.removeItem('isLoggedIn');
                localStorage.removeItem('currentUser');
            }
        }
    }

    static showUserInfo(user = null) {
        const loginSection = document.querySelector('.login-section');
        const userInfo = document.getElementById('userInfo');

        if (!user) {
            const savedUser = localStorage.getItem('currentUser');
            if (savedUser) {
                user = JSON.parse(savedUser);
            }
        }

        if (!loginSection || !userInfo || !user) {
            console.log('Missing elements or user data for showUserInfo');
            return;
        }

        loginSection.classList.add('logged-in');

        const userName = userInfo.querySelector('.user-name');
        const userAvatar = userInfo.querySelector('.user-avatar');

        if (userName && userAvatar) {
            userName.textContent = user.name;
            userAvatar.textContent = user.name.charAt(0).toUpperCase();
            console.log('Updated user info:', {
                name: user.name,
                avatar: user.name.charAt(0).toUpperCase()
            });
        }
    }

    static logout() {
        console.log('Logout called');

        localStorage.removeItem('isLoggedIn');
        localStorage.removeItem('currentUser');

        const loginSection = document.querySelector('.login-section');
        if (loginSection) {
            loginSection.classList.remove('logged-in');
            console.log('Removed logged-in class');
        }

        this.showNotification('Đã đăng xuất thành công!', 'info');
    }

    static showNotification(message, type = 'success') {
        const notification = document.createElement('div');
        notification.className = `alert alert-${type} position-fixed`;
        notification.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
        notification.innerHTML = `
            <div class="d-flex align-items-center">
                <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'danger' ? 'exclamation-circle' : 'info-circle'} me-2"></i>
                ${message}
                <button type="button" class="btn-close ms-auto" onclick="this.parentElement.parentElement.remove()"></button>
            </div>
        `;

        document.body.appendChild(notification);

        setTimeout(() => {
            if (notification.parentElement) {
                notification.remove();
            }
        }, 3000);
    }

    static initializeFooter() {
        console.log('Footer initialized');
    }
}

// Make logout function global
window.logout = function() {
    ComponentLoader.logout();
};

// Main initialization
document.addEventListener('DOMContentLoaded', async function() {
    console.log('DOM loaded - initializing product page');

    try {
        // Load header and footer
        console.log('Loading header...');
        await ComponentLoader.loadComponent('header', 'header-placeholder');
        console.log('Header loaded successfully');

        console.log('Loading footer...');
        await ComponentLoader.loadComponent('footer', 'footer-placeholder');
        console.log('Footer loaded successfully');
    } catch (error) {
        console.error('Error loading components:', error);
    }

    // Product images functionality
    let currentImageIndex = 0;
    const productImages = [
        'assets/images/products/iphone-14-pro-max-main-0',
        'assets/images/products/ip14',
        'assets/images/products/ip14x',
        'assets/images/products/ip14xx',
        'assets/images/products/ip14',
    ];

    function changeMainImage(src, index) {
        document.getElementById('mainProductImage').src = src;
        currentImageIndex = index;
        updateImageCounter();

        document.querySelectorAll('.thumbnail-img').forEach(img => {
            img.classList.remove('active');
        });
        event.target.classList.add('active');
    }

    function previousImage() {
        currentImageIndex = currentImageIndex > 0 ? currentImageIndex - 1 : productImages.length - 1;
        updateMainImage();
    }

    function nextImage() {
        currentImageIndex = currentImageIndex < productImages.length - 1 ? currentImageIndex + 1 : 0;
        updateMainImage();
    }

    function updateMainImage() {
        const mainImg = document.getElementById('mainProductImage');
        const thumbnails = document.querySelectorAll('.thumbnail-img');

        mainImg.src = productImages[currentImageIndex];

        thumbnails.forEach((thumb, index) => {
            if (index === currentImageIndex) {
                thumb.classList.add('active');
            } else {
                thumb.classList.remove('active');
            }
        });

        updateImageCounter();
    }

    function updateImageCounter() {
        document.getElementById('currentImageIndex').textContent = currentImageIndex + 1;
        document.getElementById('totalImages').textContent = productImages.length;
    }

    document.addEventListener('keydown', function(e) {
        if (e.key === 'ArrowLeft') {
            previousImage();
        } else if (e.key === 'ArrowRight') {
            nextImage();
        }
    });

    // Quantity controls

        const quantityInput = document.getElementById('quantity');
        const btnIncrease = document.getElementById('btnIncrease');
        const btnDecrease = document.getElementById('btnDecrease');

        btnIncrease.addEventListener('click', function () {
            let currentValue = parseInt(quantityInput.value) || 1;
            if (currentValue < 10) {
                quantityInput.value = currentValue + 1;
            }
        });

        btnDecrease.addEventListener('click', function () {
            let currentValue = parseInt(quantityInput.value) || 1;
            if (currentValue > 1) {
                quantityInput.value = currentValue - 1;
            }
        });


    function formatPrice(price) {
        return new Intl.NumberFormat('vi-VN').format(price);
    }

    // Price update based on storage selection
    const storageOptions = document.querySelectorAll('input[name="storage"]');
    const currentPriceElement = document.querySelector('.current-price');
    const originalPriceElement = document.querySelector('.original-price');

    const basePrices = {
        '128': { current: 24990000, original: 27990000 },
        '256': { current: 27990000, original: 30990000 },
        '512': { current: 32990000, original: 35990000 },
        '1024': { current: 37990000, original: 40990000 }
    };

    storageOptions.forEach(option => {
        option.addEventListener('change', function() {
            const storage = this.value;
            const prices = basePrices[storage];

            if (prices) {
                currentPriceElement.textContent = new Intl.NumberFormat('vi-VN').format(prices.current) + '₫';
                originalPriceElement.textContent = new Intl.NumberFormat('vi-VN').format(prices.original) + '₫';

                const addToCartBtn = document.querySelector('.add-to-cart-btn');
                if (addToCartBtn) {
                    addToCartBtn.setAttribute('data-product-price', prices.current);
                }
            }
        });
    });

    console.log('Product page initialized successfully');
});

// Function to select color and update price
function selectColor(element) {
    document.querySelectorAll('.color-item').forEach(item => {
        item.classList.remove('selected');
    });
    element.classList.add('selected');
    const price = element.getAttribute('data-price');
    document.querySelector('.current-price').textContent = price + '₫';
}

// Hàm quản lý carousel và thumbnails
const thumbnails = document.querySelectorAll('.thumbnail-img');
const carousel = document.querySelector('#productImageCarousel');

// Hàm đi đến ảnh chỉ định
function gotoSlide(index) {
    const carouselInstance = bootstrap.Carousel.getInstance(carousel) || new bootstrap.Carousel(carousel);
    carouselInstance.to(index);
    updateActiveThumbnail(index);
}

// Hàm cập nhật viền active cho thumbnail
function updateActiveThumbnail(activeIndex) {
    thumbnails.forEach((thumb, idx) => {
        if (idx === activeIndex) {
            thumb.classList.add('active');
        } else {
            thumb.classList.remove('active');
        }
    });
}

// Lắng nghe sự kiện khi chuyển slide (bằng nút hoặc tự động)
carousel.addEventListener('slid.bs.carousel', function (event) {
    updateActiveThumbnail(event.to);
});

// Gọi hàm khởi tạo ban đầu
updateActiveThumbnail(0);

function gotoSlide(index) {
    const carousel = bootstrap.Carousel.getOrCreateInstance(document.querySelector('#productImageCarousel'));
    carousel.to(index);
}