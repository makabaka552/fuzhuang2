package favorites.service.impl;

import favorites.mapper.FavoriteMapper;
import favorites.mapper.ProductMapper;
import favorites.mapper.UserMapper;
import favorites.service.FavoriteService;
import lombok.extern.slf4j.Slf4j;
import model.Favorite;
import model.Products;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FavoriteServerImpl implements FavoriteService {
    @Autowired
    private FavoriteMapper favoriteMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ProductMapper productMapper;
    @Override
    public Favorite FindFavorite(String username, long productid) {
        User user = userMapper.findByUsername(username);
        Products product = productMapper.findById(productid).orElseThrow(() -> new RuntimeException("商品不存在"));
        Favorite favorite = favoriteMapper.findByUserAndProduct(user,product);
        if (favorite == null){
            Favorite e =new Favorite();
            e.setIsFavorite(false);
            return e;
        }
        return favorite;
    }

    @Override
    public void AddFavorite(String username, long productid) {
        User user = userMapper.findByUsername(username);
        Products product = productMapper.findById(productid).orElseThrow(() -> new RuntimeException("商品不存在"));
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);
        favorite.setIsFavorite(true);
        favoriteMapper.save(favorite);
    }

    @Override
    public void DeleteFavorite(String username, long productid) {
        User user = userMapper.findByUsername(username);
        Products product = productMapper.findById(productid).orElseThrow(() -> new RuntimeException("商品不存在"));
        Favorite favorite = favoriteMapper.findByUserAndProduct(user,product);
        favoriteMapper.delete(favorite);
    }

    @Override
    public List<Products> FindAllFavorite(String username) {
        User user = userMapper.findByUsername(username);
        List<Favorite> favorites = favoriteMapper.findByUser(user);
        List<Products> products = favorites.stream().map(Favorite::getProduct).collect(Collectors.toList());
        return products;
    }


}
