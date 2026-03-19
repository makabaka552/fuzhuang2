package cart.mapper;

import model.Products;

import org.apache.ibatis.annotations.Select;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductMapper extends JpaRepository<Products, Long> {


}
