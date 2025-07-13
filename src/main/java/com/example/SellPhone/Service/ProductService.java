package com.example.SellPhone.Service;

import com.example.SellPhone.DTO.Request.Product.ProductCreationRequest;
import com.example.SellPhone.DTO.Request.Specification.SpecificationCreationRequest;
import com.example.SellPhone.Model.Category;
import com.example.SellPhone.Model.Product;
import com.example.SellPhone.Model.Specification;
import com.example.SellPhone.Repository.CategoryRepository;
import com.example.SellPhone.Repository.ProductRepository;
import com.example.SellPhone.Repository.SpecificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.UUID;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SpecificationRepository specificationRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // Tìm kiếm sản phẩm theo searchQuery
    public Page<Product> searchProduct(String searchQuery, Pageable pageable) {
        // Kiểm tra xem searchQuery có phải là một số hợp lệ không
        boolean isNumeric = false;

        // Kiểm tra nếu searchQuery có phải là một số hợp lệ
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            // Kiểm tra nếu nó là một số nguyên dài (Long)
            try {
                Long.parseLong(searchQuery);  // Nếu chuyển thành Long được thì đây là một số
                isNumeric = true;
            } catch (NumberFormatException e) {
                // Nếu không thể chuyển đổi thành Long thì không phải số
                isNumeric = false;
            }
        }

        // Nếu searchQuery là số, tìm kiếm theo id
        if (isNumeric) {
            return productRepository.findByProductId(Long.parseLong(searchQuery), pageable);
        } else {
            // Nếu không phải là số, tìm kiếm chỉ theo các trường khác
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
//    public boolean doesProductExistByNameAndColor(String name, String color) {
//        return productRepository.existsByNameAndColor(name, color);
//    }

//    // Luu sản phẩm mới
//    public Product createProduct(ProductCreationRequest request) {
//
//        // Lấy danh mục
//        Category category = categoryRepository.findById(request.getCategoryId())
//                .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại"));
//
//        SpecificationCreationRequest specReq = request.getSpecification();
//        Specification specification = Specification.builder()
//                .screenSize(specReq.getScreenSize())
//                .rearCamera(specReq.getRearCamera())
//                .frontCamera(specReq.getFrontCamera())
//                .chipset(specReq.getChipset())
//                .ram(specReq.getRam())
//                .rom(specReq.getRom())
//                .sim(specReq.getSim())
//                .operatingSystem(specReq.getOperatingSystem())
//                .cpu(specReq.getCpu())
//                .charging(specReq.getCharging())
//                .build();
//
//        String imagePath = null;
//
//        MultipartFile imageFile = request.getImageUrl();
//
//        if (imageFile != null && !imageFile.isEmpty()) {
//            // Tạo tên file duy nhất
//            String originalFileName = imageFile.getOriginalFilename();
//            String fileName;
//            if (originalFileName != null && !originalFileName.trim().isEmpty()) {
//                // Loại bỏ dấu cách và ký tự đặc biệt
//                String cleanFileName = originalFileName.replaceAll("\\s+", "_")
//                        .replaceAll("[^a-zA-Z0-9._-]", "");
//                fileName = UUID.randomUUID() + "_" + cleanFileName;
//            } else {
//                // Nếu originalFileName là null hoặc rỗng, xác định extension từ ContentType
//                String extension = getExtensionFromContentType(imageFile.getContentType());
//                fileName = UUID.randomUUID() + "_image" + extension;
//            }
//
//            // Đường dẫn thư mục lưu ảnh
//            String uploadDir = "uploads/products"; // hoặc Paths.get("src/main/resources/static/uploads/images").toString();
//
//            Path uploadPath = Paths.get(uploadDir);
//
//            try {
//                // Tạo thư mục nếu chưa tồn tại
//                if (Files.notExists(uploadPath)) {
//                    Files.createDirectories(uploadPath);
//                }
//
//                // Đường dẫn đầy đủ của file
//                Path filePath = uploadPath.resolve(fileName);
//                String savedPath = Paths.get(uploadDir, fileName).toString();
//                // Lưu file vào ổ đĩa
//                imageFile.transferTo(Paths.get(savedPath));
//
//                // Đường dẫn để lưu vào DB (tuỳ thuộc vào cách bạn truy cập ảnh từ front-end)
//                imagePath = "/" + uploadDir + "/" + fileName;
//
//            } catch (IOException e) {
//                throw new RuntimeException("Lỗi khi lưu hình ảnh", e);
//            }
//        }
//
//        Specification savedSpec = specificationRepository.save(specification);
//
//        // Tạo sản phẩm
//        Product product = Product.builder()
//                .name(request.getName())
//                .imageUrl(imagePath)
//                .category(category)
//                .color(request.getColor())
//                .importPrice(request.getImportPrice())
//                .sellingPrice(request.getSellingPrice())
//                .quantity(request.getQuantity())
//                .status(request.getStatus())
//                .description(request.getDescription())
//                .specifications(Collections.singletonList(savedSpec)) // Thêm specification vào sản phẩm
//                .build();
//
//        return productRepository.save(product);
//    }

    // Method giúp kiểm tra extension (đuôi file) là gì
    private String getExtensionFromContentType(String contentType) {
        if (contentType == null) return ".jpg"; // mặc định

        switch (contentType.toLowerCase()) {
            case "image/jpeg":
            case "image/jpg":
                return ".jpg";
            case "image/png":
                return ".png";
            case "image/gif":
                return ".gif";
            case "image/webp":
                return ".webp";
            default:
                return ".jpg"; // mặc định
        }
    }
}
