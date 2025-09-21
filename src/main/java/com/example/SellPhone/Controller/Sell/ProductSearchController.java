package com.example.SellPhone.Controller.Sell;

import com.example.SellPhone.DTO.Respone.Product.ProductSummaryRespone;
import com.example.SellPhone.Service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/product-search")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductSearchController {

    ProductService productService;

    @GetMapping
    public String productSearch(@RequestParam(value = "page", defaultValue = "0") int page, Model model) {
        Pageable firstPage = PageRequest.of(page, 8);
        Page<ProductSummaryRespone> firstProducts = productService.getProductSummary(firstPage);

        model.addAttribute("productSummary", firstProducts);
        return "Sell/product-search";
    }

    @GetMapping("/search")
    @ResponseBody
    public Page<ProductSummaryRespone> productSearch(@RequestParam(value = "page", defaultValue = "0") int page,
                                                     Model model,
                                                     @RequestParam(value = "searchQuery", required = false) String searchQuery,
                                                     @RequestParam(value = "category", required = false) String category,
                                                     @RequestParam(value = "minPrice", required = false) String minPrice,
                                                     @RequestParam(value = "maxPrice", required = false) String maxPrice,
                                                     @RequestParam(value = "storage", required = false) String storage,
                                                     @RequestParam(value = "sort", required = false) String sort) {

        Pageable pageable = PageRequest.of(page, 8, getSort(sort));

        List<String> categoryList = category != null && !category.isEmpty() ? Arrays.asList(category.split(",")) : null;
        List<String> storageList = storage != null && !storage.isEmpty() ? Arrays.asList(storage.split(",")) : null;

        Long minPriceVal = (minPrice == null || minPrice.isEmpty()) ? null : Long.valueOf(minPrice);
        Long maxPriceVal = (maxPrice == null || maxPrice.isEmpty()) ? null : Long.valueOf(maxPrice);

        if ("price-asc".equals(sort)) {
            return productService.searchProductsOrderByPriceAsc(searchQuery, categoryList, storageList, minPriceVal, maxPriceVal, PageRequest.of(page, 8));
        } else if ("price-desc".equals(sort)) {
            return productService.searchProductsOrderByPriceDesc(searchQuery, categoryList, storageList, minPriceVal, maxPriceVal, PageRequest.of(page, 8));
        } else
            return productService.searchProducts(searchQuery, categoryList, storageList, minPriceVal, maxPriceVal, pageable);
    }

    @GetMapping("/find")
    public String productSearch(@RequestParam(value = "page", defaultValue = "0") int page,
                                              Model model,
                                              @RequestParam(value = "searchQuery", required = false) String searchQuery,
                                              @RequestParam(value = "sort", required = false) String sort) {

        Pageable pageable = PageRequest.of(page, 8, getSort(sort));
        Page<ProductSummaryRespone> products = productService.searchProducts(searchQuery, null, null, null, null, pageable);

        model.addAttribute("productSummary", products);
        return "Sell/product-search";
    }

    private Sort getSort(String sort) {
        if (sort == null || sort.equals("default")) return Sort.by("name").ascending();
        return switch (sort) {
            case "name-asc" -> Sort.by("name").ascending();
            case "name-desc" -> Sort.by("name").descending();
            case "newest" -> Sort.by("productId").descending(); // mới nhất theo ID
            default -> Sort.unsorted();
        };
    }
}
