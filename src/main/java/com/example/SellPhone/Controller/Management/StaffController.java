package com.example.SellPhone.Controller.Management;

import com.example.SellPhone.DTO.Request.User.UserCreationRequest;
import com.example.SellPhone.DTO.Request.User.UserUpdateRequest;
import com.example.SellPhone.Model.User;
import com.example.SellPhone.Service.StaffService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

// Trang quản lý nhân viên
@Controller
@RequestMapping("/management/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;


    //Mở trang thêm nhân viên
    @GetMapping("/op_add")
    String opAddStaff(Model model){
        model.addAttribute("staff", new User());
        return "DashBoard/themNhanVien";
    }

    //Mở trang cập nhật thông tin khách hàng
    @PostMapping("/op_update")
    String opUpdateStaff(@RequestParam Long userId, Model model){
        User user = staffService.getStaffById(userId);

        // Chuyển đổi ngày sinh từ định dạng dd/MM/yyyy sang yyyy-MM-dd
        String dob = user.getDob(); // Lấy ngày sinh dạng dd/MM/yyyy
        String formattedDob = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date = sdf.parse(dob);
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            formattedDob = outputFormat.format(date); // Chuyển đổi thành yyyy-MM-dd
        } catch (ParseException e) {
            e.printStackTrace(); // Xử lý lỗi nếu có
        }

        // Cập nhật ngày sinh với định dạng đúng
        user.setDob(formattedDob);

        model.addAttribute("staff", user);
        return "DashBoard/suaNhanVien";
    }

    // Chức năng thêm nhân viên
    @PostMapping("/add")
    String addStaff(@Valid @ModelAttribute("staff") UserCreationRequest request, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes){
        if (bindingResult.hasErrors()) {
            // Xử lý lỗi
            model.addAttribute("staff", request);
            return "DashBoard/themNhanVien"; // Trả về một thông báo lỗi
        }

        // Kiểm tra ngày sinh có phải trong tương lai không
        if (isDobInFuture(request.getDob())) {
            bindingResult.rejectValue("dob", "error.dob", "Ngày sinh không hợp lệ hoặc nằm trong tương lai");
            model.addAttribute("staff", request);
            return "DashBoard/themNhanVien"; // Trả về thông báo lỗi nếu ngày sinh không hợp lệ
        }

        if(staffService.doesCustomerExistByEmail(request.getEmail())){
            bindingResult.rejectValue("email", "error.email", "Email đã tồn tại");
            model.addAttribute("staff", request);
            return "DashBoard/themNhanVien"; // Trả về thông báo lỗi nếu email đã tồn tại
        }

        if(staffService.doesCustomerExistByCCCD(request.getCCCD())){
            bindingResult.rejectValue("CCCD", "error.CCCD", "CCCD đã tồn tại");
            model.addAttribute("staff", request);
            return "DashBoard/themNhanVien"; // Trả về thông báo lỗi nếu CCCD đã tồn tại
        }

        User user = staffService.createStaff(request);
        // Thêm thông báo thành công vào FlashAttributes
        redirectAttributes.addFlashAttribute("successMessage", "Thêm nhân viên thành công!");
        return "redirect:/management/staff"; // Chuyển hướng về trang danh sách nhân viên
    }

    // Chức năng sửa thông tin nhân viên
    @PostMapping("/update")
    String updateStaff(@Valid @ModelAttribute("staff") UserUpdateRequest request, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes){
        if (bindingResult.hasErrors()) {
            // Xử lý lỗi
            model.addAttribute("staff", request);
            return "DashBoard/suaNhanVien"; // Trả về một thông báo lỗi
        }

        // Kiểm tra ngày sinh có phải trong tương lai không
        if (isDobInFuture(request.getDob())) {
            bindingResult.rejectValue("dob", "error.dob", "Ngày sinh không thể là ngày trong tương lai");
            model.addAttribute("staff", request);
            return "DashBoard/suaNhanVien"; // Trả về thông báo lỗi nếu ngày sinh không hợp lệ
        }



        if(staffService.doesCustomerExistByCCCD(request.getCCCD()) && !request.getCCCD().equals(staffService.getStaffById(request.getUserId()).getCCCD())){
            bindingResult.rejectValue("CCCD", "error.CCCD", "CCCD đã tồn tại");
            model.addAttribute("staff", request);
            return "DashBoard/suaNhanVien"; // Trả về thông báo lỗi nếu CCCD đã tồn tại
        }

        User user = staffService.updateStaff(request.getUserId(), request);
        // Thêm thông báo thành công vào FlashAttributes
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin nhân viên thành công!");
        return "redirect:/management/staff"; // Chuyển hướng về trang danh sách nhân viên
    }

    // Chức năng xóa nhân viên
    @PostMapping("/delete")
    String deleteStaff(@RequestParam Long userId, RedirectAttributes redirectAttributes){

        User user = staffService.getStaffById(userId);
        if (user != null) {
            staffService.deleteStaff(userId);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa nhân viên thành công!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy nhân viên để xóa!");
        }
        return "redirect:/management/staff"; // Chuyển hướng về trang danh sách nhân viên
    }

    // Chức năng tìm kiếm nhân viên
    @GetMapping("/search")
    String searchStaff(Model model, @RequestParam String searchQuery, @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);  // 10 dòng trên mỗi trang

        // Tìm kiếm nhân viên theo các trường fullname, email, hoặc CCCD
        Page<User> staff = staffService.searchStaff(searchQuery, pageable);

        model.addAttribute("staff", staff);
        model.addAttribute("searchQuery", searchQuery); // Để giữ giá trị tìm kiếm trong form
        return "DashBoard/quanLyNhanVien"; // Trả về trang quản lý nhân viên
    }


    // Hiển thị danh sách nhân viên và phân trang
    @GetMapping
    String StaffManagement(Model model, @RequestParam(required = false) String searchQuery, @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);  // 10 dòng trên mỗi trang

        Page<User> staff;
        if (searchQuery != null && !searchQuery.isEmpty()) {
            // Nếu có tìm kiếm
            staff = staffService.searchStaff(searchQuery, pageable);
            model.addAttribute("searchQuery", searchQuery); // Lưu giá trị tìm kiếm để giữ lại khi chuyển trang
        } else {
            // Nếu không có tìm kiếm
            staff = staffService.getStaff(pageable);
        }
        model.addAttribute("staff", staff);
        return "DashBoard/quanLyNhanVien";
    }

    // Phương thức kiểm tra ngày sinh có phải là trong tương lai không
    private boolean isDobInFuture(String dob) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Giả sử định dạng là yyyy-MM-dd
        try {
            LocalDate dobDate = LocalDate.parse(dob, formatter);
            return dobDate.isAfter(LocalDate.now()); // Kiểm tra xem ngày sinh có phải sau ngày hiện tại không
        } catch (Exception e) {
            return false; // Nếu không thể parse ngày sinh, coi như không hợp lệ
        }
    }
}
