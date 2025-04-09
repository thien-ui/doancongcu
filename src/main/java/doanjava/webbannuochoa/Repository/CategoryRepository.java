package doanjava.webbannuochoa.Repository;

import doanjava.webbannuochoa.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByIsDeletedFalse(); // Tìm danh mục chưa bị xóa

}
