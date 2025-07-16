package com.example.SellPhone.Service;

import com.example.SellPhone.DTO.Request.Product.ProductCreationRequest;
import com.example.SellPhone.DTO.Request.Specification.SpecificationCreationRequest;
import com.example.SellPhone.Model.Category;
import com.example.SellPhone.Model.Product;
import com.example.SellPhone.Model.Specification;
import com.example.SellPhone.Model.SpecificationVariant;
import com.example.SellPhone.Repository.CategoryRepository;
import com.example.SellPhone.Repository.ProductRepository;
import com.example.SellPhone.Repository.SpecificationRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {

     ProductRepository productRepository;

     SpecificationRepository specificationRepository;

     CategoryRepository categoryRepository;

    // Tìm kiếm sản phẩm theo searchQuery
    public Page<Product> searchProduct(String searchQuery, Pageable pageable) {

        // Kiểm tra nếu searchQuery có phải là một số hợp lệ
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            try {
                // Kiểm tra nếu searchQuery có thể chuyển thành Long
                Long.parseLong(searchQuery);  // Nếu chuyển thành Long được thì đây là một số
                return productRepository.findByProductId(Long.parseLong(searchQuery), pageable);  // Tìm kiếm theo id
            } catch (NumberFormatException e) {
                // Nếu không thể chuyển đổi thành Long, tìm kiếm theo tên, status hoặc category
                return productRepository.findByNameContainingOrStatusContainingOrCategory_Name(
                        searchQuery, searchQuery, searchQuery, pageable);
            }
        } else {
            // Nếu searchQuery rỗng hoặc null, tìm kiếm theo các trường khác
            return productRepository.findByNameContainingOrStatusContainingOrCategory_Name(
                    searchQuery, searchQuery, searchQuery, pageable);
        }
    }

    // Hiển thị danh sách sản phẩm
    public Page<Product> getProduct(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    // Lấy sản phẩm theo ID
    public Product getProductById(Long productId) {
        return productRepository.findById(productId).orElseThrow(()-> new RuntimeException("Không tìm thấy sản phẩm"));
    }

    // Kiểm tra xem sản phẩm có tồn tại theo tên và màu sắc hay không
    public boolean doesProductExistByNameAndColor(String name, String color) {
        return productRepository.existsByNameAndColor(name, color);
    }

    // Luu sản phẩm mới
    public Product createProduct(ProductCreationRequest request) {

        // Lấy danh mục
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại"));

        SpecificationCreationRequest specReq = request.getSpecification();
        Specification specification = Specification.builder()
                .screenSize(specReq.getScreenSize())
                .rearCamera(specReq.getRearCamera())
                .frontCamera(specReq.getFrontCamera())
                .chipset(specReq.getChipset())
                .ram(specReq.getRam())
                .sim(specReq.getSim())
                .operatingSystem(specReq.getOperatingSystem())
                .cpu(specReq.getCpu())
                .charging(specReq.getCharging())
                .build();

        // Map các variant từ request
        List<SpecificationVariant> variants = request.getRomVariants().stream()
                .map(variantReq -> SpecificationVariant.builder()
                        .rom(variantReq.getRom())
                        .importPrice(variantReq.getImportPrice())
                        .sellingPrice(variantReq.getSellingPrice())
                        .quantity(variantReq.getQuantity())
                        .specification(specification) // liên kết ngược lại
                        .build())
                .toList();

        specification.setVariants(variants);

        // Kiểm tra xem có bất kỳ variant nào có số lượng lớn hơn 0 không
        // Để tạo trạng thái sản phẩm dựa trên số lượng
        boolean hasQuantity = variants.stream()
                .anyMatch(v -> v.getQuantity() != null && v.getQuantity() > 0);

        String imagePath = saveImageToDisk(request.getImageUrl());

        // Tạo sản phẩm
        Product product = Product.builder()
                .name(request.getName())
                .imageUrl(imagePath)
                .color(request.getColor())
                .category(category)
                .status(hasQuantity ? "Còn hàng" : "Hết hàng") // Trạng thái dựa trên số lượng
                .description(request.getDescription())
                .specification(specification)
                .build();

        return productRepository.save(product);
    }

    // Method giúp kiểm tra extension (đuôi file) là gì
    private String getExtensionFromContentType(String contentType) {
        if (contentType == null) return ".jpg"; // mặc định

        switch (contentType.toLowerCase()) {
            case "image/jpeg", "image/jpg" -> {
                return ".jpg";
            }
            case "image/png" -> {
                return ".png";
            }
            case "image/gif" -> {
                return ".gif";
            }
            case "image/webp" -> {
                return ".webp";
            }
            default -> {
                return ".jpg"; // mặc định
            }
        }

    }

    // Lưu hình ảnh vào ổ đĩa
    private String saveImageToDisk(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) return null;

        try {
            String originalFileName = imageFile.getOriginalFilename();
            String cleanFileName = (originalFileName != null && !originalFileName.isBlank())
                    ? originalFileName.replaceAll("\\s+", "_").replaceAll("[^a-zA-Z0-9._-]", "")
                    : UUID.randomUUID() + "_image" + getExtensionFromContentType(imageFile.getContentType());

            String fileName = UUID.randomUUID() + "_" + cleanFileName;
            String uploadDir = "uploads/products";
            Path uploadPath = Paths.get(uploadDir);

            if (Files.notExists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            imageFile.transferTo(filePath);

            return "/" + uploadDir + "/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu hình ảnh", e);
        }
    }

}
