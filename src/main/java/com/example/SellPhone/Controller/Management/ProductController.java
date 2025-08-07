package com.example.SellPhone.Controller.Management;

import com.example.SellPhone.DTO.Respone.Product.ProductSpecificationVariantResponse;
import com.example.SellPhone.DTO.Request.Product.ProductCreationRequest;
import com.example.SellPhone.DTO.Respone.Product.ProductSpecificationDTO;
import com.example.SellPhone.DTO.Request.Product.ProductUpdateRequest;
import com.example.SellPhone.DTO.Request.Specification.SpecificationCreationRequest;
import com.example.SellPhone.DTO.Request.SpecificationVariant.SpecificationVariantRequest;
import com.example.SellPhone.DTO.Respone.Product.ProductSpecificationResponse;
import com.example.SellPhone.DTO.Respone.Product.ProductInfoResponse;
import com.example.SellPhone.Entity.Product;
import com.example.SellPhone.Entity.Specification;
import com.example.SellPhone.Service.CategoryService;
import com.example.SellPhone.Service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/management/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    // Hiển thị danh sách sản phẩm và phân trang
    @GetMapping
    String productManagement(Model model, @RequestParam(required = false) String searchQuery, @RequestParam(defaultValue = "0") int page){
        Pageable pageable = PageRequest.of(page, 10);  // 10 dòng trên mỗi trang
        Page<Product> products;
        if (searchQuery != null && !searchQuery.isEmpty()) {
            // Nếu có tìm kiếm
            products = productService.searchProduct(searchQuery, pageable);
            model.addAttribute("searchQuery", searchQuery); // Lưu giá trị tìm kiếm để giữ lại khi chuyển trang
        } else {
            // Nếu không có tìm kiếm
            products = productService.getProduct(pageable);
        }

        // Truyền các URL vào model
        model.addAttribute("products", products);
        model.addAttribute("currentPage", "products");
        return "DashBoard/product-management";
    }

    //Mở trang thêm sản phẩm
    @GetMapping("/op_add")
    String opAddProduct(Model model){
        model.addAttribute("request", new ProductCreationRequest());
        model.addAttribute("categories", categoryService.findAll()); // Trả về List<Category>
        model.addAttribute("currentPage", "products");
        return  "DashBoard/add-product";
    }

    //Mở trang cập nhật thông tin sản phẩm
    @GetMapping("/op_update")
    String opUpdateProduct(@RequestParam Long productId, Model model){
        Product product = productService.getProductById(productId);

        if (product.getSpecification() == null) {
            product.setSpecification(new Specification());
        }
        if (product.getSpecification().getVariants() == null) {
            product.getSpecification().setVariants(new ArrayList<>());
        }
        ProductUpdateRequest request = convertToUpdateRequest(product);
        model.addAttribute("request", request);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("currentPage", "products");
        return "DashBoard/edit-product";
    }

    // Chức năng thêm sản phẩm
    @PostMapping("/add")
    String addProduct(@Valid @ModelAttribute("request") ProductCreationRequest request, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes){

        if (request.getImageUrl() == null || request.getImageUrl().isEmpty()) {
            bindingResult.rejectValue("imageUrl", "error.imageUrl", "Vui lòng chọn hình ảnh");
        }

        if (bindingResult.hasErrors()) {
            // Xử lý lỗi
            model.addAttribute("request", request);
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("currentPage", "products");
            return "DashBoard/add-product"; // Trả về một thông báo lỗi
        }

        // Normalize screenSize từ screenSizeInput
        if (request.getSpecification() != null) {
            request.getSpecification().normalizeScreenSize();
        }

        if(productService.doesProductExistByNameAndColor(request.getName(), request.getColor())){
            bindingResult.rejectValue("name", "error.name", "Sản phẩm với tên và màu này đã tồn tại");
            model.addAttribute("request", request);
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("currentPage", "products");
            return "DashBoard/add-product"; // Trả về thông báo lỗi nếu
        }

        if (!categoryService.existsById(request.getCategoryId())) {
            bindingResult.rejectValue("categoryId", "error.categoryId", "Danh mục không hợp lệ");
            model.addAttribute("request", request);
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("currentPage", "products");
            return "DashBoard/add-product";
        }

        if(request.getSpecification() == null) {
            bindingResult.rejectValue("specificationIds", "error.specificationIds", "Vui lòng chọn ít nhất một thông số kỹ thuật");
            model.addAttribute("request", request);
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("currentPage", "products");
            return "DashBoard/add-product";
        }

        try {
            productService.createProduct(request);
            // Thêm thông báo thành công vào FlashAttributes
            redirectAttributes.addFlashAttribute("successMessage", "Thêm sản phẩm thành công!");
            return "redirect:/management/products"; // Chuyển hướng về trang danh sách sản phẩm
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", "Lỗi khi lưu sản phẩm: " + ex.getMessage());
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("currentPage", "products");
            return "DashBoard/add-product";
        }
    }

    // Chức năng sửa thông tin sảm phẩm
    @PostMapping("/update")
    String updateProduct(@Valid @ModelAttribute("request") ProductUpdateRequest request, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes){
        if (bindingResult.hasErrors()) {
            // Giữ lại ảnh cũ
            Product product = productService.getProductById(request.getProductId());
            request.setExistingImageUrl(product.getImageUrl());

            // Xử lý lỗi
            model.addAttribute("request", request);
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("currentPage", "products");
            return "DashBoard/edit-product"; // Trả về một thông báo lỗi
        }

        if(request.getSpecification() == null) {
            Product product = productService.getProductById(request.getProductId());
            request.setExistingImageUrl(product.getImageUrl());
            bindingResult.rejectValue("specificationIds", "error.specificationIds", "Vui lòng chọn ít nhất một thông số kỹ thuật");
            model.addAttribute("request", request);
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("currentPage", "products");
            return "DashBoard/edit-product";
        }
        else request.getSpecification().normalizeScreenSize(); // Normalize screenSize từ screenSizeInput

        if(productService.doesProductExistByNameAndColorAndID(request.getName(), request.getColor(), request.getProductId())){
            Product product = productService.getProductById(request.getProductId());
            request.setExistingImageUrl(product.getImageUrl());
            bindingResult.rejectValue("name", "error.name", "Sản phẩm với tên và màu này đã tồn tại");
            model.addAttribute("request", request);
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("currentPage", "products");
            return "DashBoard/edit-product"; // Trả về thông báo lỗi nếu
        }

        if (!categoryService.existsById(request.getCategory().getCategoryId())) {
            Product product = productService.getProductById(request.getProductId());
            request.setExistingImageUrl(product.getImageUrl());
            bindingResult.rejectValue("categoryId", "error.categoryId", "Danh mục không hợp lệ");
            model.addAttribute("request", request);
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("currentPage", "products");
            return "DashBoard/edit-product";
        }

        try {
            productService.updateProduct(request);
            // Thêm thông báo thành công vào FlashAttributes
            redirectAttributes.addFlashAttribute("successMessage", "Sửa thông tin sản phẩm thành công!");
            return "redirect:/management/products"; // Chuyển hướng về trang danh sách sản phẩm
        } catch (RuntimeException ex) {
            Product product = productService.getProductById(request.getProductId());
            request.setExistingImageUrl(product.getImageUrl());
            if (ex.getMessage().contains("trùng")) {
                bindingResult.rejectValue("romVariants", "error.romVariants", ex.getMessage());
            } else {
                model.addAttribute("errorMessage", "Lỗi khi sửa sản phẩm!");
            }
            System.out.println("Lỗi khi sửa sản phẩm: " + ex.getMessage());
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("currentPage", "products");
            return "DashBoard/edit-product";
        }
    }

    // Chức năng xóa sản phẩm
    @PostMapping("/delete")
    String deleteProduct(@RequestParam Long productId, RedirectAttributes redirectAttributes){

        Product product = productService.getProductById(productId);
        if (product != null) {
            productService.deleteProduct(productId);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa sản phẩm thành công!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm để xóa!");
        }
        return "redirect:/management/products"; // Chuyển hướng về trang danh sách nhân viên
    }

    @PostMapping("/discontinue")
    String discontinueProduct(@RequestParam Long productId, RedirectAttributes redirectAttributes){
        Product product = productService.getProductById(productId);
        if(product != null){
            productService.discontinueProduct(product);
            redirectAttributes.addFlashAttribute("successMessage", "Chuyển trạng thái sản phẩm thành công!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm để xóa!");
        }
        return "redirect:/management/products"; // Chuyển hướng về trang danh sách nhân viên

    }

    // Lấy thông tin sản phẩm để hiển thị vào modal xem thông số kỹ thuật
    @GetMapping("/get-detail-product/{productId}")
    @ResponseBody
    public ResponseEntity<?> getProductDetails(@PathVariable Long productId) {
        Product product = productService.getProductById(productId);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        ProductSpecificationResponse dto = new ProductSpecificationResponse();
        dto.setName(product.getName());
        dto.setImageUrl(product.getImageUrl());
        dto.setDescription(product.getDescription());

        Specification spec = product.getSpecification(); // OneToOne
        if (spec == null) {
            return ResponseEntity.ok(dto);
        }

        ProductSpecificationDTO specDTO = new ProductSpecificationDTO();
        specDTO.setScreenSize(spec.getScreenSize());
        specDTO.setRearCamera(spec.getRearCamera());
        specDTO.setFrontCamera(spec.getFrontCamera());
        specDTO.setChipset(spec.getChipset());
        specDTO.setRam(spec.getRam());
        specDTO.setSim(spec.getSim());
        specDTO.setOperatingSystem(spec.getOperatingSystem());
        specDTO.setCpu(spec.getCpu());
        specDTO.setCharging(spec.getCharging());

        List<ProductSpecificationVariantResponse> variantDTOs = spec.getVariants().stream().map(variant -> ProductSpecificationVariantResponse.builder()
                .rom(variant.getRom())
                .importPrice(variant.getImportPrice())
                .sellingPrice(variant.getSellingPrice())
                .quantity(variant.getQuantity())
                .build()).toList();

        specDTO.setVariants(variantDTOs);
        dto.setSpecifications(List.of(specDTO));

        return ResponseEntity.ok(dto);
    }

    // Chuyển đổi dữ liệu Product sang ProductUpdateRequest
    private ProductUpdateRequest convertToUpdateRequest(Product product) {
        ProductUpdateRequest dto = new ProductUpdateRequest();
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setExistingImageUrl(product.getImageUrl());
        dto.setColor(product.getColor());
        dto.setDescription(product.getDescription());
        dto.setCategory(product.getCategory()); // nếu trong DTO là Category chứ không phải categoryId

        // Convert specification
        Specification spec = product.getSpecification();
        if (spec != null) {
            SpecificationCreationRequest specDto = new SpecificationCreationRequest();
            specDto.setScreenSize(spec.getScreenSize());
            specDto.setFrontCamera(spec.getFrontCamera());
            specDto.setRearCamera(spec.getRearCamera());
            specDto.setChipset(spec.getChipset());
            specDto.setCpu(spec.getCpu());
            specDto.setRam(spec.getRam());
            specDto.setSim(spec.getSim());
            specDto.setCharging(spec.getCharging());
            specDto.setOperatingSystem(spec.getOperatingSystem());
            specDto.setScreenSizeInput(String.valueOf(spec.getScreenSize()));

            dto.setSpecification(specDto);
        }

        // Convert romVariants (nếu tồn tại)
        if (spec != null && spec.getVariants() != null) {
            List<SpecificationVariantRequest> variantDTOs = spec.getVariants().stream().map(variant -> {
                SpecificationVariantRequest v = new SpecificationVariantRequest();
                v.setRom(variant.getRom());
                v.setImportPrice(variant.getImportPrice());
                v.setSellingPrice(variant.getSellingPrice());
                v.setQuantity(variant.getQuantity());
                return v;
            }).toList();

            dto.setRomVariants(variantDTOs);
        }

        return dto;
    }

    // Kiểm tra sản phẩm có tồn tại trong db không (để fill vào ô input trong chức năng thêm sản phẩm)
    @GetMapping("/check-name")
    public ResponseEntity<?> checkProductName(@RequestParam("name") String name) {
        Optional<Product> productOpt = productService.findByName(name);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            Specification spec = product.getSpecification();
            ProductInfoResponse dto = new ProductInfoResponse();

            dto.setScreenSizeInput(spec.getScreenSize());
            dto.setOperatingSystem(spec.getOperatingSystem());
            dto.setRearCamera(spec.getRearCamera());
            dto.setFrontCamera(spec.getFrontCamera());
            dto.setSim(spec.getSim());
            dto.setChipset(spec.getChipset());
            dto.setCpu(spec.getCpu());
            dto.setCharging(spec.getCharging());
            dto.setRam(spec.getRam());
            dto.setDescription(product.getDescription());
            dto.setCategoryId(product.getCategory() != null ? product.getCategory().getCategoryId() : null);
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sản phẩm chưa tồn tại");
        }
    }


}
