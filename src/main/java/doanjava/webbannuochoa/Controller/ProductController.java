package doanjava.webbannuochoa.Controller;

import doanjava.webbannuochoa.Service.BrandService;
import doanjava.webbannuochoa.Service.CategoryService;
import doanjava.webbannuochoa.Service.ProductService;
import doanjava.webbannuochoa.Service.ReviewService;
import doanjava.webbannuochoa.models.Brand;
import doanjava.webbannuochoa.models.Product;
import doanjava.webbannuochoa.models.Review;
import doanjava.webbannuochoa.models.User;
import doanjava.webbannuochoa.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static groovyjarjarantlr4.v4.gui.GraphicsSupport.saveImage;

@Controller
@RequestMapping("/products")
public class ProductController {

    private static final String UPLOADED_FOLDER ="src/main/resources/static/images/"; // Đường dẫn thư mục lưu ảnh ;

    private String uploadDir;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService; // Đảm bảo đã inject BrandService
    private final ReviewService reviewService;

    @Autowired
    public ProductController(ProductService productService, CategoryService categoryService, BrandService brandService, ReviewService reviewService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.brandService = brandService;
        this.reviewService = reviewService;
    }

    // Hiển thị danh sách sản phẩm theo category
    @GetMapping("/category/{categoryId}")
    public String showProductsByCategory(@PathVariable Long categoryId,
                                         @RequestParam(defaultValue = "0") int page,
                                         Model model) {
        Page<Product> productPage = productService.getProductsByCategoryIdPaged(categoryId, PageRequest.of(page, 8));
        model.addAttribute("productPage", productPage);

        return "/products/products-by-category";
    }

    // Hiển thị danh sách sản phẩm theo thương hiệu
    @GetMapping("/brand/{brandId}")
    public String showProductsByBrand(@PathVariable Long brandId,
                                      @RequestParam(defaultValue = "0") int page,
                                      Model model) {
        Page<Product> productPage = productService.getProductsByBrandIdPaged(brandId, PageRequest.of(page, 5));

        // Lấy thông tin thương hiệu từ BrandService (Optional<Brand>)
        Optional<Brand> optionalBrand = brandService.getBrandById(brandId);

        // Nếu tìm thấy thương hiệu, lấy tên của nó, nếu không thì sử dụng tên mặc định
        String brandName = optionalBrand.map(Brand::getName)
                .orElse("Thương hiệu không xác định");
        model.addAttribute("productPage", productPage);
        model.addAttribute("brandId", brandId); // Add brandId to model for pagination links
        model.addAttribute("brandName", brandName);  // Truyền tên thương hiệu vào model
        return "/products/products-by-brand";
    }

    // Hiển thị danh sách sản phẩm phân trang cho người dùng
    @GetMapping
    public String showProductList(Model model, @RequestParam(defaultValue = "0") int page) {
        // Lấy thông tin người dùng hiện tại
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Page<Product> productPage = productService.getAllProductsPaged(PageRequest.of(page, 8));
        model.addAttribute("productPage", productPage);

        // Lấy danh sách tất cả thương hiệu và danh mục để hiển thị trong form lọc
        model.addAttribute("allBrands", brandService.getAllBrands());
        model.addAttribute("allCategories", categoryService.getAllCategories());

        // Nếu người dùng chưa đăng nhập, hiển thị product list cho người dùng thông thường
        if (auth == null || auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ADMIN"))) {
            return "/products/products-list-user";
        } else {
            // Nếu người dùng là admin, có thể hiển thị view dành cho admin (có thể khác view người dùng)
            return "/products/products-list-user";
        }
    }

    // Hiển thị danh sách sản phẩm cho ADMIN
    @GetMapping("/products-list")
    public String showProductListAdmin(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "/products/products-list";
    }

    // Hiển thị kết quả tìm kiếm sản phẩm
    @GetMapping("/search")
    public String searchProducts(@RequestParam String keyword, @RequestParam(defaultValue = "0") int page, Model model) {
        Page<Product> productPage = productService.searchProductsByName(keyword, PageRequest.of(page, 5));
        model.addAttribute("productPage", productPage);
        model.addAttribute("keyword", keyword); // Để hiển thị lại từ khóa tìm kiếm trên giao diện
        return "/products/search-results";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories()); // Load categories
        model.addAttribute("brands", brandService.getAllBrands()); // Load brands
        return "/products/add-product";
    }

    @PostMapping("/add")
    public String addProduct(@Valid @ModelAttribute("product") Product product, BindingResult result,
                             @RequestParam("image") MultipartFile imageFile, Model model) {

        // Kiểm tra lỗi validation
        if (result.hasErrors()) {
            return "products/add-product";
        }

        // Lưu hình ảnh vào thư mục
        if (!imageFile.isEmpty()) {
            try {
                String imageName = saveImage(imageFile);
                product.setImgUrl("/images/" + imageName);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Lưu sản phẩm vào cơ sở dữ liệu
        productService.addProduct(product);


        return "redirect:/products"; // Redirect đến trang danh sách sản phẩm
    }
    private String saveImage(MultipartFile image) throws IOException {
        File saveFile = new ClassPathResource("static/images").getFile();
        String fileName = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(image.getOriginalFilename());
        Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + fileName);
        Files.copy(image.getInputStream(), path);
        return fileName;
    }

    // For editing a product
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);

        model.addAttribute("categories", categoryService.getAllCategories()); //Load categories
        model.addAttribute("brands", brandService.getAllBrands());
        return "/products/update-product";
    }

    // Process the form for updating a product
    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id, @Valid @ModelAttribute("product") Product product,
                                BindingResult result, @RequestParam("image") MultipartFile imageFile, Model model) {
        if (result.hasErrors()) {
            product.setId(id);
            return "/products/update-product";
        }
        if (!imageFile.isEmpty()) {
            try {
                String imageName = saveImage(imageFile);
                product.setImgUrl("/images/" + imageName);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        productService.updateProduct(product);
        return "redirect:/products/products-list";
    }
    

    // Handle request to delete a product
    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/products/products-list";
    }

    // Hiển thị chi tiết sản phẩm với sản phẩm liên quan và đánh giá
    @GetMapping("/detail/{id}")
    public String showProductDetail(@PathVariable Long id, Model model) {
        // Lấy thông tin sản phẩm và các sản phẩm liên quan
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));

        // Lấy các sản phẩm liên quan (ví dụ, cùng thương hiệu)
        List<Product> relatedProducts = productService.getRelatedProductsByBrand(product.getBrand().getId(), id);

        // Đưa dữ liệu vào model
        model.addAttribute("product", product);
        model.addAttribute("relatedProducts", relatedProducts);
        model.addAttribute("newReview", new Review()); // Tạo form đánh giá trống

        return "/products/product-detail";
    }

    // Hiển thị danh sách đánh giá cho sản phẩm
    @GetMapping("/reviews/{id}")
    public String showProductReviews(@PathVariable Long id, Model model) {
        // Lấy thông tin sản phẩm
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));

        // Lấy các nhận xét của sản phẩm
        List<Review> reviews = reviewService.getReviewsByProductId(id);

        // Thêm vào model
        model.addAttribute("product", product);
        model.addAttribute("reviews", reviews);

        System.out.println("Product name: " + product.getName()); // Kiểm tra
        System.out.println("Review count: " + reviews.size());    // Kiểm tra

        return "/products/reviews"; // Đường dẫn phải khớp với vị trí file HTML của bạn
    }

    // Thêm đánh giá cho sản phẩm
    @PostMapping("/detail/{id}/review")
    public String addReview(@PathVariable Long id, @Valid Review newReview, BindingResult result, Model model) {
        if (result.hasErrors()) {
            // Nếu có lỗi, lấy lại sản phẩm và các thông tin khác để hiển thị lại trang
            Product product = productService.getProductById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
            List<Product> relatedProducts = productService.getRelatedProductsByBrand(product.getBrand().getId(), id);
            List<Review> reviews = reviewService.getReviewsByProductId(id);

            // Cập nhật lại model với sản phẩm, các nhận xét và sản phẩm liên quan
            model.addAttribute("product", product);
            model.addAttribute("relatedProducts", relatedProducts);
            model.addAttribute("reviews", reviews);
            model.addAttribute("newReview", newReview); // Giữ dữ liệu đánh giá nếu có lỗi

            return "/products/product-detail";
        }

        // Lấy thông tin người dùng đã đăng nhập
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = null;
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            user = ((CustomUserDetails) auth.getPrincipal()).getUser();
        } else {
            // Nếu người dùng chưa đăng nhập, chuyển hướng đến trang đăng nhập
            return "redirect:/login";
        }

        // Lấy sản phẩm cần thêm nhận xét
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));

        // Tạo đánh giá mới và lưu vào database
        Review review = new Review(product, user, newReview.getContent(), newReview.getRating());
        reviewService.saveReview(review);

        // Điều hướng lại về trang chi tiết sản phẩm
        return "redirect:/products/detail/" + id;
    }

    @GetMapping("/filter")
    public String filterProducts(@RequestParam(required = false) Double price,
                                 @RequestParam(required = false) String comparisonType,
                                 @RequestParam(required = false) List<String> brandNames,
                                 @RequestParam(required = false) List<String> categoryNames,
                                 Model model) {

        List<Product> filteredProducts = productService.filterProductsByCriteria(price, comparisonType, brandNames, categoryNames);
        model.addAttribute("filteredProducts", filteredProducts);

        // Thêm dữ liệu cho form lọc
        model.addAttribute("allBrands", brandService.getAllBrands());
        model.addAttribute("allCategories", categoryService.getAllCategories());

        return "/products/product-filtered";
    }




}
