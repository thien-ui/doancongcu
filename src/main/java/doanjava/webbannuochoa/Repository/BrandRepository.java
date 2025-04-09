package doanjava.webbannuochoa.Repository;

import doanjava.webbannuochoa.models.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    List<Brand> findAllByIsDeletedFalse(); // Tìm danh mục chưa bị xóa
}
