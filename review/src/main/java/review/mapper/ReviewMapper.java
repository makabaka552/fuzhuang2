package review.mapper;


import model.Products;
import model.Review;
import model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewMapper extends JpaRepository<Review,Long> {
    List<Review> findAllByUser(User user);
    List<Review> findByproduct(Products product);

}
