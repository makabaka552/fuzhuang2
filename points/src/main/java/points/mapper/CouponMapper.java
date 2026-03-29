package points.mapper;

import model.Coupon;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CouponMapper {
    @Select("SELECT * FROM coupon")
    List<Coupon> findAll();

    @Select("SELECT * FROM coupon WHERE is_active = 1 and user_id = null")
    List<Coupon> findActiveCoupons();

    @Select("SELECT * FROM coupon WHERE id = #{id}")
    Optional<Coupon> findById(Long id);

    @Select("SELECT * FROM coupon WHERE user_id = #{userId}")
    List<Coupon> findByUserId(Long userId);

    @Insert("INSERT INTO coupon(name, code, discount_amount, min_order_amount, start_date, end_date, status, user_id, required_points, description, type, stock, total_stock, is_active, create_time, update_time) " +
            "VALUES(#{name}, #{code}, #{discountAmount}, #{minOrderAmount}, #{startDate}, #{endDate}, #{status}, #{userId}, #{requiredPoints}, #{description}, #{type}, #{stock}, #{totalStock}, #{isActive}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void save(Coupon coupon);

    @Update("UPDATE coupon SET name=#{name}, code=#{code}, discount_amount=#{discountAmount}, min_order_amount=#{minOrderAmount}, " +
            "start_date=#{startDate}, end_date=#{endDate}, status=#{status}, user_id=#{userId}, " +
            "required_points=#{requiredPoints}, description=#{description}, type=#{type}, " +
            "stock=#{stock}, total_stock=#{totalStock}, is_active=#{isActive}, update_time=NOW() WHERE id=#{id}")
    void update(Coupon coupon);

    @Update("UPDATE coupon SET stock = stock - 1, update_time = NOW() WHERE id = #{id} AND stock > 0")
    int decreaseStock(Long id);

    @Update("UPDATE coupon SET stock = stock + 1, update_time = NOW() WHERE id = #{id}")
    void increaseStock(Long id);

    @Update("UPDATE coupon SET is_active = #{isActive}, update_time = NOW() WHERE id = #{id}")
    void updateActiveStatus(@Param("id") Long id, @Param("isActive") Integer isActive);

    @Update("UPDATE coupon SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Delete("DELETE FROM coupon WHERE id = #{id}")
    void deleteById(Long id);
}