package favorites.service;

import model.Favorite;
import model.Products;

import java.util.List;

public interface FavoriteService {
    Favorite FindFavorite(String username, long productid);


    void AddFavorite(String username, long productid);

    void DeleteFavorite(String username, long productid);

    List<Products> FindAllFavorite(String username);
}
