package com.example.SellPhone.Controller.Management;

import com.example.SellPhone.DTO.Request.User.UserCreationRequest;
import com.example.SellPhone.DTO.Request.User.UserUpdateRequest;
import com.example.SellPhone.Model.User;
import com.example.SellPhone.Service.CustomerService;
import com.example.SellPhone.Service.UserService;
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


// Trang quản lý khách hàng
@Controller
@RequestMapping("/management/customers")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    //Mở trang thêm khách hàng
    @GetMapping("/op_add")
    String opAddCustomer(Model model){
        model.addAttribute("customer", new User());
        return "DashBoard/themKhachHang";
    }

    //Mở trang cập nhật thông tin khách hàng
    @PostMapping("/op_update")
    String opUpdateCustomer(@RequestParam Long userId, Model model){
        User user = customerService.getCustomerById(userId);

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

        model.addAttribute("customer", user);
        return "DashBoard/suaKhachHang";
    }


    // Chức năng thêm khách hàng
    @PostMapping("/add")
    String addCustomer(@Valid @ModelAttribute("customer") UserCreationRequest request, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes){
        if (bindingResult.hasErrors()) {
            // Xử lý lỗi
            model.addAttribute("customer", request);
            return "DashBoard/themKhachHang"; // Trả về một thông báo lỗi
        }

        // Kiểm tra ngày sinh có phải trong tương lai không
        if (isDobInFuture(request.getDob())) {
            bindingResult.rejectValue("dob", "error.dob", "Ngày sinh không thể là ngày trong tương lai");
            model.addAttribute("customer", request);
            return "DashBoard/themKhachHang"; // Trả về thông báo lỗi nếu ngày sinh không hợp lệ
        }

        if(customerService.doesCustomerExistByEmail(request.getEmail())){
            bindingResult.rejectValue("email", "error.email", "Email đã tồn tại");
            model.addAttribute("customer", request);
            return "DashBoard/themKhachHang"; // Trả về thông báo lỗi nếu email đã tồn tại
        }

        if(customerService.doesCustomerExistByCCCD(request.getCCCD())){
            bindingResult.rejectValue("CCCD", "error.CCCD", "CCCD đã tồn tại");
            model.addAttribute("customer", request);
            return "DashBoard/themKhachHang"; // Trả về thông báo lỗi nếu CCCD đã tồn tại
        }

        User user = customerService.createCustomer(request);
        // Thêm thông báo thành công vào FlashAttributes
        redirectAttributes.addFlashAttribute("successMessage", "Thêm khách hàng thành công!");
        return "redirect:/management/customers"; // Chuyển hướng về trang danh sách khách hàng
    }

    // Chức năng sửa thông tin khách hàng
    @PostMapping("/update")
    String updateCustomer(@Valid @ModelAttribute("customer") UserUpdateRequest request, BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()) {
            // Xử lý lỗi
            model.addAttribute("customer", request);
            return "DashBoard/suaKhachHang"; // Trả về một thông báo lỗi
        }

        // Kiểm tra ngày sinh có phải trong tương lai không
        if (isDobInFuture(request.getDob())) {
            bindingResult.rejectValue("dob", "error.dob", "Ngày sinh không thể là ngày trong tương lai");
            model.addAttribute("customer", request);
            return "DashBoard/suaKhachHang"; // Trả về thông báo lỗi nếu ngày sinh không hợp lệ
        }



        if(customerService.doesCustomerExistByCCCD(request.getCCCD())){
            bindingResult.rejectValue("CCCD", "error.CCCD", "CCCD đã tồn tại");
            model.addAttribute("customer", request);
            return "DashBoard/suaKhachHang"; // Trả về thông báo lỗi nếu CCCD đã tồn tại
        }

        User user = customerService.updateCustomer(request.getUserId(), request);
        return "redirect:/management/customers"; // Chuyển hướng về trang danh sách khách hàng
    }


    // Hiển thị danh sách khách hàng và phân trang
    @GetMapping
    String customerManagement(Model model, @RequestParam(defaultValue = "0") int page){
        Pageable pageable = PageRequest.of(page, 10);  // 10 dòng trên mỗi trang

        Page<User> customers =customerService.getCustomer(pageable);
        model.addAttribute("customers", customers);
        return "DashBoard/quanLyKhachHang";
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
