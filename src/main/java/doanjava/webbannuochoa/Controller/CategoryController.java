package doanjava.webbannuochoa.Controller;

import doanjava.webbannuochoa.Service.CategoryService;
import doanjava.webbannuochoa.models.Category;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
public class CategoryController {
    @Autowired
    private final CategoryService categoryService;

    @GetMapping("/categories/add")
    public String showAddForm(Model model) {
        model.addAttribute("category", new Category());
        return "/categories/add-category";
    }

    @PostMapping("/categories/add")
    public String addCategory(@Valid Category category, BindingResult result, @RequestParam("image") MultipartFile imageFile) {
        if (result.hasErrors()) {
            return "/categories/add-category";
        }

        // Kiểm tra và lưu hình ảnh
        if (!imageFile.isEmpty()) {
            try {
                // Lưu hình ảnh và lấy đường dẫn
                String imageName = saveImage(imageFile);
                category.setImgUrl("/images/" + imageName); // Lưu đường dẫn hình ảnh
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Lưu danh mục vào cơ sở dữ liệu
        categoryService.addCategory(category);

        return "redirect:/categories";
    }

    // Lưu hình ảnh vào thư mục
    private String saveImage(MultipartFile image) throws IOException {
        File saveFile = new ClassPathResource("static/images").getFile();
        String fileName = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(image.getOriginalFilename());
        Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + fileName);
        Files.copy(image.getInputStream(), path);
        return fileName;
    }


    // Hiển thị danh sách danh mục
    @GetMapping("/categories")
    public String listCategories(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "/categories/categories-list";
    }

    @GetMapping("/categories/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        // Lấy danh mục theo ID từ cơ sở dữ liệu
        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + id));
        model.addAttribute("category", category);
        return "/categories/update-category";
    }

    @PostMapping("/categories/update/{id}")
    public String updateCategory(@PathVariable("id") Long id, @Valid Category category,
                                 BindingResult result, @RequestParam("image") MultipartFile imageFile, Model model) {
        if (result.hasErrors()) {
            category.setId(id);  // Đảm bảo ID không bị mất khi có lỗi validation
            return "/categories/update-category";
        }

        // Kiểm tra xem người dùng có tải lên hình ảnh mới không
        if (!imageFile.isEmpty()) {
            try {
                // Lưu hình ảnh mới và lấy tên file
                String imageName = saveImage(imageFile);
                category.setImgUrl("/images/" + imageName); // Cập nhật đường dẫn hình ảnh mới
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Cập nhật danh mục trong cơ sở dữ liệu
        categoryService.updateCategory(category);
        return "redirect:/categories"; // Chuyển hướng đến trang danh sách các danh mục
    }

    // GET request for deleting category (soft delete)
    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        categoryService.deleteCategoryById(id);
        redirectAttributes.addFlashAttribute("message", "Đã ẩn danh mục!");

        return "redirect:/categories";
    }

    @GetMapping("/categories/restore/{id}")
    public String restoreCategory(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.restoreCategoryById(id);
            redirectAttributes.addFlashAttribute("message", "Khôi phục thành công!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("message", "Danh mục đã có sẵn.");
        }

        return "redirect:/categories";
    }
}
