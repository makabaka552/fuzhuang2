package cart.mapper;

import model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantMapper extends JpaRepository<ProductVariant, Integer> {
}