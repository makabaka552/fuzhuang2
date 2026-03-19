package products;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("model") // 扫描实体类所在的包
@EnableJpaRepositories("products.mapper") // 扫描Repository接口所在的包
public class ProductsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductsApplication.class,args);
    }
}
