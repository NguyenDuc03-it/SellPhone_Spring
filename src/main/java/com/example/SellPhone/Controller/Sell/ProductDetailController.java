package com.example.SellPhone.Controller.Sell;

import com.example.SellPhone.DTO.Respone.Product.ProductSummaryRespone;
import com.example.SellPhone.Entity.Product;
import com.example.SellPhone.Entity.SpecificationVariant;
import com.example.SellPhone.Service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/product-detail")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductDetailController {

    ProductService productService;

    // Hiển thị dữ liệu ngay khi load trang chi tiết sản phẩm
    @GetMapping("/product/{id}")
    public String getProductDetail(@PathVariable("id") Long id, Model model) {
        Product product = productService.getProductById(id);
        List<Product> similarProducts = productService.findByNameAndProductIdNot(product.getName(), id);

        List<SpecificationVariant> variants = product.getSpecification().getVariants();

        // Lấy ROM mặc định: ROM đầu tiên (theo thứ tự tăng dần)
        variants.sort(Comparator.comparingInt(SpecificationVariant::getRom)); // Sắp xếp rom theo thứ tự tăng dần
        int defaultRom = variants.getFirst().getRom(); // Lấy rom đầu tiên trong danh sách đã sắp xếp

        // Lấy số lượng còn lại theo ROM mặc định
        Optional<SpecificationVariant> defaultVariantOpt = variants.stream()
                .filter(v -> v.getRom() == defaultRom)
                .findFirst();
        int availableQuantity = defaultVariantOpt.map(SpecificationVariant::getQuantity).orElse(0);

        // Lấy giá bán của sản phẩm hiện tại theo ROM mặc định
        Long currentPrice = variants.stream()
                .filter(v -> v.getRom() == defaultRom)
                .map(SpecificationVariant::getSellingPrice)
                .findFirst()
                .orElse(0L);

        // Lấy giá bán theo ROM mặc định cho các sản phẩm tương tự
        Map<Long, Long> priceByProductId = new HashMap<>();
        for (Product p : similarProducts) {
            List<SpecificationVariant> pVariants = p.getSpecification().getVariants();

            // Lấy variant đầu tiên (ROM đầu tiên của màu đó)
            Optional<SpecificationVariant> firstVariant = pVariants.stream().findFirst();

            priceByProductId.put(
                    p.getProductId(),
                    firstVariant.map(SpecificationVariant::getSellingPrice).orElse(0L)
            );
        }

        // Lấy tất cả ROM để hiển thị radio button
        Set<Integer> roms = variants.stream()
                .map(SpecificationVariant::getRom)
                .collect(Collectors.toCollection(TreeSet::new));


        Set<Integer> allRoms = new TreeSet<>(roms);

        // Thêm rom từ các sản phẩm tương tự
        for (Product p : similarProducts) {
            allRoms.addAll(
                    p.getSpecification().getVariants().stream()
                            .map(SpecificationVariant::getRom)
                            .collect(Collectors.toSet())
            );
        }

        // Lấy sản phẩm gợi ý
        List<ProductSummaryRespone> suggestedProducts = productService.getSuggestedProducts(id, product.getName());

        model.addAttribute("product", product);
        model.addAttribute("variants", variants);
        model.addAttribute("roms", roms);
        model.addAttribute("allRoms", allRoms);
        model.addAttribute("currentPrice", currentPrice);
        model.addAttribute("similarProducts", similarProducts);
        model.addAttribute("priceByProductId", priceByProductId);
        model.addAttribute("defaultRom", defaultRom);
        model.addAttribute("availableQuantity", availableQuantity);
        model.addAttribute("suggestedProducts", suggestedProducts);

        return "Sell/product-detail";
    }

    // Xử lý thay đổi giá, rom của sản phẩm khi người dùng chọn màu hoặc rom
    @GetMapping("/product-price")
    @ResponseBody
    public Map<String, Object> getProductPrice(@RequestParam Long productId, @RequestParam(required = false) Integer rom) {
        Product product = productService.getProductById(productId);
        if (product == null) {
            return Collections.singletonMap("error", "Không tìm thấy sản phẩm");
        }

        List<SpecificationVariant> variants = product.getSpecification().getVariants();
        variants.sort(Comparator.comparingInt(SpecificationVariant::getRom));

        int selectedRom = rom != null ? rom : variants.getFirst().getRom();

        Optional<SpecificationVariant> variantOpt = variants.stream()
                .filter(v -> v.getRom() == selectedRom)
                .findFirst();

        if (variantOpt.isEmpty()) {
            return Collections.singletonMap("error", "Không tìm thấy biến thể thông số kỹ thuật");
        }
        SpecificationVariant variant = variantOpt.get();

        Map<String, Object> result = new HashMap<>();
        result.put("price", variant.getSellingPrice());
        result.put("imageUrl", product.getImageUrl());
        result.put("defaultRom", selectedRom);
        result.put("quantity", variant.getQuantity());

        // Trả về danh sách ROM của sản phẩm để frontend cập nhật lại radio button
        List<Integer> romList = variants.stream().map(SpecificationVariant::getRom).collect(Collectors.toList());
        result.put("roms", romList);

        return result;
    }



}
