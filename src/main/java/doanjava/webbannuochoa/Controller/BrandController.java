package doanjava.webbannuochoa.Controller;

import doanjava.webbannuochoa.Service.BrandService;
import doanjava.webbannuochoa.models.Brand;
import doanjava.webbannuochoa.models.Category;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class BrandController {

    @Autowired
    private final BrandService brandService;

    @GetMapping("/brands/add")
    public String showAddForm(Model model) {
        model.addAttribute("brand", new Brand());
        return "/brands/add-brand";
    }

    @PostMapping("/brands/add")
    public String addBrand(@Valid @ModelAttribute("brand") Brand brand, BindingResult result,
                           @RequestParam("image") MultipartFile imageFile) {

        // Kiểm tra lỗi validation
        if (result.hasErrors()) {
            return "brands/add-brand"; // Nếu có lỗi, trả về form
        }

        // Lưu hình ảnh nếu có
        if (!imageFile.isEmpty()) {
            try {
                String imageName = saveImage(imageFile); // Lưu hình ảnh và lấy tên file
                brand.setImgUrl("/images/" + imageName); // Gán đường dẫn hình ảnh cho brand
            } catch (IOException e) {
                e.printStackTrace(); // Nếu có lỗi khi lưu hình ảnh, ghi log lỗi
            }
        }

        // Lưu thương hiệu vào cơ sở dữ liệu
        brandService.addBrand(brand);

        return "redirect:/brands"; // Chuyển hướng đến trang danh sách thương hiệu sau khi thêm thành công
    }

    private String saveImage(MultipartFile image) throws IOException {
        File saveFile = new ClassPathResource("static/images").getFile();
        String fileName = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(image.getOriginalFilename());
        Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + fileName);
        Files.copy(image.getInputStream(), path);
        return fileName;
    }

    @GetMapping("/brands")
    public String listBrands(Model model) {
        List<Brand> brands = brandService.getAllBrands();
        model.addAttribute("brands", brands);
        return "/brands/brand-list";
    }

    @GetMapping("/brands/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Brand brand = brandService.getBrandById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid brand Id:" + id));
        model.addAttribute("brand", brand);
        return "/brands/update-brand";
    }

    @PostMapping("/brands/update/{id}")
    public String updateBrand(@PathVariable("id") Long id, @Valid Brand brand, BindingResult result,
                              @RequestParam(value = "image", required = false) MultipartFile imageFile, Model model) {

        // Kiểm tra lỗi validation
        if (result.hasErrors()) {
            brand.setId(id);
            return "/brands/update-brand";
        }

        // Xử lý hình ảnh nếu có
        if (!imageFile.isEmpty()) {
            try {
                // Lưu hình ảnh và lấy tên file
                String imageName = saveImage(imageFile);
                brand.setImgUrl("/images/" + imageName); // Cập nhật đường dẫn hình ảnh
            } catch (IOException e) {
                e.printStackTrace(); // Nếu có lỗi khi lưu hình ảnh
            }
        }

        // Cập nhật thông tin thương hiệu vào cơ sở dữ liệu
        brandService.updateBrand(brand);

        model.addAttribute("brands", brandService.getAllBrands());
        return "redirect:/brands"; // Chuyển hướng đến danh sách thương hiệu
    }


    @GetMapping("/brands/delete/{id}")
    public String deleteBrand(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        brandService.deleteBrandById(id);
        redirectAttributes.addFlashAttribute("message", "Đã ẩn thương hiệu!");
        return "redirect:/brands";
    }

    @GetMapping("/brands/restore/{id}")
    public String restoreBrand(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            brandService.restoreBrandById(id);
            redirectAttributes.addFlashAttribute("message", "Khôi phục thành công!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("message", "Thương hiệu đã có sẵn.");
        }
        return "redirect:/brands";
    }
}
