package review.mapper;

import model.Products;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductMapper extends JpaRepository<Products, Long> {


}
