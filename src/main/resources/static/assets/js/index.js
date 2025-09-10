document.addEventListener("DOMContentLoaded", function () {
  const loadMoreBtn = document.querySelector(".btn-load-more");
  const productGrid = document.querySelector(".product-grid");

  let page = 1; // Trang 0 đã load ở server
  const size = 6;

  loadMoreBtn.addEventListener("click", function () {
    fetch(`/more-products?page=${page}&size=${size}`)
      .then(res => res.json())
      .then(data => {
        const products = data.content;
        if (products.length === 0) {
          loadMoreBtn.disabled = true;
          loadMoreBtn.innerHTML = "Đã hiển thị tất cả sản phẩm";
          return;
        }

        products.forEach(product => {
          const card = document.createElement("div");
          card.className = "product-card";
          card.innerHTML = `
            <div class="badge">Hot</div>
            <div class="product-thumb">
              <img src="${product.imageUrl}" alt="${product.name}">
            </div>
            <div class="product-details">
              <h3>${product.name}</h3>
              <div class="product-specs">
                <span>Chipset: <span>${product.chipset}</span></span>
                <span>Hệ điều hành: <span>${product.operatingSystem}</span></span>
                <span>Màu: <span>${product.color}</span></span>
              </div>
              <div class="product-price">
                <span class="price">${new Intl.NumberFormat('vi-VN').format(product.sellingPrice)}đ</span>
              </div>
              <div class="product-actions">
                <button class="btn btn-primary add-to-cart-btn"
                        data-product-id="${product.productId}"
                        data-product-name="${product.name}"
                        data-product-price="${product.sellingPrice}">
                  <i class="fas fa-shopping-cart"></i> Thêm vào giỏ hàng
                </button>
                <button class="btn btn-outline"><i class="fas fa-heart"></i></button>
              </div>
            </div>
          `;
          productGrid.appendChild(card);
        });

        if (!data.last) {
          page++;
        } else {
          loadMoreBtn.remove();
          const message = document.createElement("p");
          message.textContent = "Đã hiển thị tất cả sản phẩm!";
          message.className = "text-muted text-center mt-3";
          productGrid.parentElement.appendChild(message);
        }
      })
      .catch(err => {
        console.error("Lỗi khi tải thêm sản phẩm:", err);
      });
  });
});
