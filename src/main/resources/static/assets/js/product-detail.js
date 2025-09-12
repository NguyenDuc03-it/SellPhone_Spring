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

function formatCurrency(value) {
    return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, '.');
}

function updatePrice() {
    const selectedColor = document.querySelector('.color-item.selected');
    const productId = selectedColor.getAttribute('data-product-id');

    const selectedRomRadio = document.querySelector('input[name="storage"]:checked');
    const rom = selectedRomRadio ? selectedRomRadio.value : null;

    if (!productId || !rom) return;

    fetch(`/product-detail/product-price?productId=${productId}&rom=${rom}`)
      .then(response => response.json())
      .then(data => {
        if (data.error) {
          console.error(data.error);
          return;
        }

        // Cập nhật giá
        const priceElement = document.getElementById('price');
        priceElement.textContent = formatCurrency(data.price) + 'đ';

        // Tìm phần tử .color-item đang được chọn
        const selectedColorItem = document.querySelector('.color-item.selected');

        if (selectedColorItem) {
            // Tìm phần tử con có class .color-price bên trong
            const selectedColorPrice = selectedColorItem.querySelector('.color-price');

            if (selectedColorPrice) {
                selectedColorPrice.textContent = formatCurrency(data.price) + 'đ';
            }
        }

        // Cập nhật ảnh nếu có
        if (data.imageUrl) {
          const mainImage = document.querySelector('.carousel-inner .carousel-item.active img');
          if (mainImage) {
            mainImage.src = data.imageUrl;
          }

        }

        const quantityEl = document.getElementById('availableQuantity');
        if (typeof data.quantity !== 'undefined') {
          updateMaxQuantity(data.quantity);
        }
      })
      .catch(err => console.error(err));
}

// Hàm xử lý khi chọn màu mới
function handleColorSelect(colorItem) {
// Bỏ class selected ở các màu khác
document.querySelectorAll('.color-item').forEach(el => el.classList.remove('selected'));
colorItem.classList.add('selected');

const productId = colorItem.getAttribute('data-product-id');

fetch(`/product-detail/product-price?productId=${productId}`)
  .then(response => response.json())
  .then(data => {
    if (data.error) {
      console.error(data.error);
      return;
    }

    // Cập nhật giá
    const priceElement = document.getElementById('price');
    priceElement.textContent = formatCurrency(data.price) + 'đ';

    // Cập nhật ảnh
    if (data.imageUrl) {
      const mainImage = document.querySelector('.carousel-inner .carousel-item.active img');
      if (mainImage) {
        mainImage.src = data.imageUrl;
      }
    }

    const carouselEl = document.getElementById('productImageCarousel');
    const carousel = bootstrap.Carousel.getInstance(carouselEl);
    if (carousel) {
      carousel.pause(); // Dừng auto-slide khi người dùng chọn màu
    }

    // Cập nhật danh sách ROM
    const storageContainer = document.querySelector('.storage-options');
    storageContainer.innerHTML = ''; // Xoá radio cũ

    data.roms.forEach(rom => {
      // Tạo input radio
      const input = document.createElement('input');
      input.type = 'radio';
      input.name = 'storage';
      input.id = 'storage-' + rom;
      input.value = rom;
      if (rom === data.defaultRom) input.checked = true;

      // Tạo label
      const label = document.createElement('label');
      label.className = 'storage-option';
      label.htmlFor = input.id;
      label.textContent = formatRom(rom);

      // Gắn sự kiện khi đổi ROM
      input.addEventListener('change', updatePrice);

      // Thêm vào giao diện
      storageContainer.appendChild(input);
      storageContainer.appendChild(label);
    });

    // Gọi cập nhật giá cho ROM mặc định
    updatePrice();

    const quantityEl = document.getElementById('availableQuantity');
      if (quantityEl && typeof data.quantity !== 'undefined') {
        quantityEl.textContent = data.quantity;
    }
  })
  .catch(err => console.error(err));
}

// Gắn sự kiện khi chọn màu sắc
document.querySelectorAll('.color-item').forEach(item => {
item.addEventListener('click', function () {
  handleColorSelect(this);
});
});

// Gắn sự kiện ban đầu cho các radio (trường hợp không chọn lại màu)
document.querySelectorAll('input[name="storage"]').forEach(radio => {
radio.addEventListener('change', updatePrice);
});


document.addEventListener('DOMContentLoaded', function () {
    // Tự động khởi tạo carousel khi trang load
    const carouselEl = document.getElementById('productImageCarousel');
    if (carouselEl) {
      new bootstrap.Carousel(carouselEl, {
        interval: 3000, // Tự động chuyển ảnh mỗi 3s
        ride: 'carousel'
      });
    }
    // Gọi hàm cập nhật số lượng và xem xét disable nút khi trang load
    const quantityValue = parseInt(document.getElementById("availableQuantity").textContent || "0", 10);
    updateMaxQuantity(quantityValue);

    // Thêm sản phẩm vào giỏ hàng và hiển thị thông báo thành công start
    const addToCartBtn = document.querySelector(".add-to-cart-btn");

    function showSuccessNotification(message) {
    const successNotification = document.getElementById('successNotification');
    const successMessageText = document.getElementById('successMessageText');
    const successProgressBar = document.getElementById('progress');

    successMessageText.textContent = message;
    successNotification.style.display = 'block';

    let successProgress = 100;
    successProgressBar.style.width = '100%';

    // Animation progress bar giảm dần
    const successInterval = setInterval(() => {
      if (successProgress > 0) {
        successProgress -= 1;
        successProgressBar.style.width = successProgress + '%';
      } else {
        clearInterval(successInterval);
        setTimeout(() => {
          successNotification.style.display = 'none';
        }, 500);
      }
    }, 30);

    // Tự ẩn sau 5 giây
    setTimeout(() => {
      if (successProgress > 0) {
        clearInterval(successInterval);
        successProgressBar.style.width = '0%';
        successNotification.style.display = 'none';
      }
    }, 5000);
    }

    addToCartBtn.addEventListener("click", function () {

        const selectedColorItem = document.querySelector(".color-item.selected");

        const productId = selectedColorItem?.dataset.productId;
        const quantity = document.getElementById("quantity").value;
        const romRadio = document.querySelector("input[name='storage']:checked");
        const rom = romRadio?.value;

        fetch("/user/cart/add", {
          method: "POST",
          headers: {
            "Content-Type": "application/json"
          },
          body: JSON.stringify({
            productId: productId,
            quantity: quantity,
            rom : rom
          })
        })
        .then(response => {
          if (!response.ok) {
            if (response.status === 401) {
              // Người dùng chưa đăng nhập -> redirect tới trang login với redirect param
              const currentUrl = window.location.pathname + window.location.search;
              window.location.href = `/login?redirect=${encodeURIComponent(currentUrl)}`;
            } else {
              throw new Error("Lỗi khi thêm vào giỏ hàng");
            }
          }
          return response.json();
        })
        .then(data => {
          // Cập nhật số lượng ở header
          const cartCount = document.querySelector(".cart-count");
          cartCount.textContent = data.totalItems;

          // Thông báo
          showSuccessNotification("Đã thêm sản phẩm vào giỏ hàng!");
        })
        .catch(error => {
          console.error(error);
          const currentUrl = window.location.pathname + window.location.search;
          window.location.href = `/login?redirect=${encodeURIComponent(currentUrl)}`;
        });
    });
    // Thêm sản phẩm vào giỏ hàng và hiển thị thông báo thành công end
});

function formatRom(rom) {
    if (rom >= 1000) {
     return (rom / 1000).toFixed(rom % 1000 === 0 ? 0 : 1) + 'TB';
    } else {
     return rom + 'GB';
    }
}

// Quantity controls
const quantityInput = document.getElementById('quantity');
const btnIncrease = document.getElementById('btnIncrease');
const btnDecrease = document.getElementById('btnDecrease');

function updateMaxQuantity(newMax) {
    quantityInput.setAttribute('max', newMax.toString());

    const availableQuantitySpan = document.getElementById("availableQuantity");
    if (availableQuantitySpan) {
        availableQuantitySpan.textContent = newMax;
    }

    // Nếu người dùng chọn số vượt quá max mới thì reset
    if (parseInt(quantityInput.value) > newMax) {
        quantityInput.value = newMax;
    }

    // Bật/tắt nút tùy theo số lượng
    const addToCartBtn = document.querySelector('.add-to-cart-btn');
    const buyNowBtn = document.querySelector('.buy-now');

    const shouldDisable = newMax <= 0;

    if (addToCartBtn) addToCartBtn.disabled = shouldDisable;
    if (buyNowBtn) buyNowBtn.disabled = shouldDisable;
}

btnIncrease.addEventListener('click', function () {
    let currentValue = parseInt(quantityInput.value) || 1;
    let maxValue = parseInt(quantityInput.max) || 10;  // Lấy max từ thuộc tính max của input
    if (currentValue < maxValue) {
        quantityInput.value = currentValue + 1;
    }
});

btnDecrease.addEventListener('click', function () {
    let currentValue = parseInt(quantityInput.value) || 1;
    let minValue = parseInt(quantityInput.min) || 1;
    if (currentValue > minValue) {
        quantityInput.value = currentValue - 1;
    }
});

// Xử lý khi người dùng nhấn nút 'Mua ngay'
document.addEventListener("DOMContentLoaded", function () {
    const buyNowBtn = document.getElementById("buyNowBtn");

    buyNowBtn.addEventListener("click", function () {
      // Lấy dữ liệu từ giao diện
      const selectedColorItem = document.querySelector(".color-item.selected");
      const selectedColor = selectedColorItem?.dataset.color;
      const productId = selectedColorItem?.dataset.productId;
      const rawPrice = selectedColorItem?.dataset.price ?? "";
      const price = parseInt(rawPrice.replace(/[^\d]/g, ''), 10);

      const romRadio = document.querySelector("input[name='storage']:checked");
      const rom = romRadio?.value;

      const quantity = document.getElementById("quantity").value;

      const productName = document.querySelector(".product-title").textContent.trim();

      // Tạo dữ liệu gửi đi
      const checkoutData = {
        productId: productId,
        productName: productName,
        color: selectedColor,
        rom: rom,
        price: price,
        quantity: quantity
      };

      // Gửi POST đến /checkout
      fetch("/user/checkout", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Accept": "application/json"
        },
        body: JSON.stringify(checkoutData)
      })
        .then(response => {
          if (response.status === 401) {
            // Chưa đăng nhập -> redirect đến trang đăng nhập
            const currentUrl = window.location.pathname + window.location.search;
            window.location.href = `/login?redirect=${encodeURIComponent(currentUrl)}`;
            return;
          }

          if (!response.ok) {
            throw new Error("Đã có lỗi xảy ra khi chuyển đến trang thanh toán.");
          }

          // Nếu thành công: chuyển hướng sang trang thanh toán
          return response.json();
        })
        .then(data => {
          if (data && data.redirectUrl) {
            window.location.href = data.redirectUrl;
          }
        })
        .catch(error => {
          console.error(error);
          const currentUrl = window.location.pathname + window.location.search;
          window.location.href = `/login?redirect=${encodeURIComponent(currentUrl)}`;
        });
    });
});