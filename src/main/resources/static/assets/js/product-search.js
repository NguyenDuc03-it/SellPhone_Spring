function getFilterValues(isModal = false) {
  let container = document;
  if (isModal) {
    container = document.getElementById('mobile-filters-content');
  } else {
    container = document.querySelector('.filters-sidebar');
  }
  const searchQuery = container.querySelector(".search-input").value;
  const selectedCategories = Array.from(container.querySelectorAll("input[name='category']:checked"))
                                   .map(input => input.value).join(",");
  const minPrice = container.querySelector(".min-price").value;
  const maxPrice = container.querySelector(".max-price").value;
  const selectedStorage = Array.from(container.querySelectorAll("input[name='storage']:checked"))
                                .map(input => input.value).join(",");
  return { searchQuery, selectedCategories, minPrice, maxPrice, selectedStorage };
}

// Lắng nghe sự kiện thay đổi bộ lọc
//document.getElementById("search-btn").addEventListener("click", function() {
//    const searchQuery = document.querySelector(".search-input").value;
//    const selectedCategories = Array.from(document.querySelectorAll("input[name='category']:checked"))
//                                     .map(input => input.value).join(",");
//    const minPrice = document.querySelector(".min-price").value;
//    const maxPrice = document.querySelector(".max-price").value;
//    const selectedStorage = Array.from(document.querySelectorAll("input[name='storage']:checked"))
//                                  .map(input => input.value).join(",");
//    const sort = document.getElementById("sort-select").value;
//
//    const url = `product-search/search?page=0&searchQuery=${encodeURIComponent(searchQuery)}&category=${encodeURIComponent(selectedCategories)}&minPrice=${minPrice}&maxPrice=${maxPrice}&storage=${encodeURIComponent(selectedStorage)}&sort=${sort}`;
//
//    fetch(url)
//        .then(response => {
//             // Kiểm tra xem response có phải là JSON không
//             const contentType = response.headers.get("content-type");
//             if (contentType && contentType.includes("application/json")) {
//                 return response.json();
//             } else {
//                 // Nếu không phải JSON, trả về response bình thường
//                 return response.text(); // Hoặc xử lý theo cách bạn muốn
//             }
//         })
//        .then(data => {
//            if (typeof data === 'object') {
//                // Xử lý dữ liệu JSON
//                updateProductList(data);
//            } else {
//                // Nếu dữ liệu không phải JSON, xử lý theo cách khác
//                console.log('Dữ liệu không phải JSON:', data);
//            }
//        })
//        .catch(error => {
//            console.error('Lỗi khi fetch dữ liệu:', error);
//        });
//});

// Cập nhật danh sách sản phẩm
function updateProductList(data) {
    const productsContainer = document.getElementById("products-container");
    productsContainer.innerHTML = ''; // Xóa danh sách sản phẩm hiện tại

    data.content.forEach(product => {
        const productCard = document.createElement("div");
        productCard.classList.add("product-card");
        productCard.innerHTML = `
            <a href="/product-detail/product/${product.productId}" class="product-link">
                <div class="badge">${product.isHot ? 'Hot' : ''}</div>
                <div class="product-thumb">
                    <img src="${product.imageUrl}" alt="${product.name}">
                </div>
                <div class="product-details">
                    <h3>${product.name}</h3>
                    <div class="product-specs">
                        <span>Chip: ${product.chipset}</span>
                        <span>Hệ điều hành: ${product.operatingSystem}</span>
                        <span>Màu: ${product.color}</span>
                    </div>
                    <div class="product-price">
                        <span class="price">${formatCurrency(product.sellingPrice)}</span>
                    </div>
                </div>
            </a>
        `;
        productsContainer.appendChild(productCard);
    });

    // Cập nhật phân trang nếu cần
    updatePagination(data);
}

function formatCurrency(amount) {
    return amount.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
}

let currentFilters = {
    searchQuery: "",
    category: "",
    minPrice: "",
    maxPrice: "",
    storage: "",
    sort: ""
};

// Cập nhật phân trang
function updatePagination(data) {
    const pagination = document.querySelector(".pagination");
    pagination.innerHTML = '';

    const currentPage = data.number;
    const totalPages = data.totalPages;

    // Nút "Trước"
    const prevItem = document.createElement("li");
    prevItem.classList.add("page-item");
    if (currentPage === 0) prevItem.classList.add("disabled");

    const prevLink = document.createElement("a");
    prevLink.classList.add("page-link");
    prevLink.href = "#";
    prevLink.textContent = "Trước";
    prevLink.addEventListener("click", function (e) {
        e.preventDefault();
        if (currentPage > 0) triggerSearch(currentPage - 1);
    });

    prevItem.appendChild(prevLink);
    pagination.appendChild(prevItem);

    // Các trang
    for (let i = 0; i < totalPages; i++) {
        const pageItem = document.createElement("li");
        pageItem.classList.add("page-item");
        if (i === currentPage) pageItem.classList.add("active");

        const pageLink = document.createElement("a");
        pageLink.classList.add("page-link");
        pageLink.href = "#";
        pageLink.textContent = i + 1;
        pageLink.addEventListener("click", function (e) {
            e.preventDefault();
            triggerSearch(i);
        });

        pageItem.appendChild(pageLink);
        pagination.appendChild(pageItem);
    }

    // Nút "Sau"
    const nextItem = document.createElement("li");
    nextItem.classList.add("page-item");
    if (currentPage === totalPages - 1) nextItem.classList.add("disabled");

    const nextLink = document.createElement("a");
    nextLink.classList.add("page-link");
    nextLink.href = "#";
    nextLink.textContent = "Sau";
    nextLink.addEventListener("click", function (e) {
        e.preventDefault();
        if (currentPage < totalPages - 1) triggerSearch(currentPage + 1);
    });

    nextItem.appendChild(nextLink);
    pagination.appendChild(nextItem);
}

function triggerSearch(page = 0, isModal = false) {
    const filters = getFilterValues(isModal);
    const sort = document.getElementById("sort-select").value || "";

    currentFilters = {
        searchQuery: filters.searchQuery,
        category: filters.selectedCategories,
        minPrice: filters.minPrice,
        maxPrice: filters.maxPrice,
        storage: filters.selectedStorage,
        sort
    };

    const url = `product-search/search?page=${page}` +
            `&searchQuery=${encodeURIComponent(filters.searchQuery)}` +
            `&category=${encodeURIComponent(filters.selectedCategories)}` +
            `&minPrice=${filters.minPrice}` +
            `&maxPrice=${filters.maxPrice}` +
            `&storage=${encodeURIComponent(filters.selectedStorage)}` +
            `&sort=${encodeURIComponent(sort)}`;

    fetch(url)
        .then(response => {
            const contentType = response.headers.get("content-type");
            if (contentType && contentType.includes("application/json")) {
                return response.json();
            } else {
                return response.text();
            }
        })
        .then(data => {
            if (typeof data === 'object') {
                document.getElementById('current-page').textContent = "Tìm kiếm sản phẩm";
                document.getElementById('search-info-title').textContent = "Kết quả tìm kiếm";
                document.getElementById('product-count').textContent = data.totalElements;
                updateProductList(data);
            } else {
                console.log('Dữ liệu không phải JSON:', data);
            }
        })
        .catch(error => {
            console.error('Lỗi khi fetch dữ liệu:', error);
        });
}

// Gọi tìm kiếm khi thay đổi bất kỳ filter nào
const filterSelectors = [
    "input[name='category']",
    "input[name='storage']",
    "#min-price",
    "#max-price",
    "#sort-select"
];
filterSelectors.forEach(selector => {
    document.querySelectorAll(selector).forEach(el => {
        el.addEventListener("change", () => triggerSearch());
    });
});

// Gọi tìm kiếm khi click vào các nút preset giá
const pricePresetButtons = document.querySelectorAll(".price-preset");
pricePresetButtons.forEach(btn => {
    btn.addEventListener("click", function() {
        pricePresetButtons.forEach(button => button.classList.remove("active"));
        btn.classList.add("active");

        document.querySelector(".min-price").value = btn.getAttribute("data-min") || "";
        document.querySelector(".max-price").value = btn.getAttribute("data-max") || "";
        triggerSearch(0, false);
    });
});

// Gọi tìm kiếm khi nhấn nút search
if (document.getElementById("search-btn")) {
    // search-btn nằm trong sidebar
    document.getElementById("search-btn").addEventListener("click", function() {
        triggerSearch(0, false);
    });
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', async function() {
    console.log('DOM loaded, khởi tạo trang tìm kiếm sản phẩm...');

    // Code xử lý khi người dùng tìm kiếm sản phẩm từ input trên header start
    const pathname = window.location.pathname;
    const urlParams = new URLSearchParams(window.location.search);
    const searchQuery = urlParams.get('searchQuery');
    if (pathname.includes('/find')) {
        document.getElementById('current-page').textContent = "Tìm kiếm sản phẩm";
        document.getElementById('search-info-title').textContent = "Kết quả tìm kiếm";

        const searchInput = document.getElementById('search-input');
        const headerSearchQuery = document.getElementById('header-search-uery');
        if (searchInput) {
            searchInput.value = searchQuery;
            headerSearchQuery.value = searchQuery;
        }
    }
    // Code xử lý khi người dùng tìm kiếm sản phẩm từ input trên header end

    const cleanFilter = document.getElementById('clear-filters');
    cleanFilter.addEventListener('click', function() {
        window.location.href = '/product-search';
    });


    // Code xử lý copy bộ lọc vào modal khi người dùng sử dụng mobile
    const mobileFiltersModal = document.getElementById('mobileFiltersModal');
    const mobileFiltersContent = document.getElementById('mobile-filters-content');
    const filtersSidebar = document.querySelector('.filters-sidebar');
    if (mobileFiltersModal && mobileFiltersContent && filtersSidebar) {
        mobileFiltersModal.addEventListener('show.bs.modal', function () {
            mobileFiltersContent.innerHTML = filtersSidebar.innerHTML;
        });
    }

    console.log('Trang tìm kiếm sản phẩm đã được khởi tạo thành công');
});