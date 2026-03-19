package favorites.controller;

import favorites.service.FavoriteService;
import lombok.extern.slf4j.Slf4j;
import model.Favorite;
import model.Products;
import model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import utils.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/favorite")
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;

    @GetMapping("/{productid}")
    public Result FindFavorite(@PathVariable long productid, HttpServletRequest httprequest){
        String token = httprequest.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = JwtUtil.getUsernameJwt(token);
        Favorite favorite= favoriteService.FindFavorite(username,productid);
        log.info("1:{}",favorite);
        return Result.success(favorite);
    }
    @PostMapping("/{productid}")
    public Result AddFavorite(@PathVariable long productid, HttpServletRequest httprequest){
        String token = httprequest.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = JwtUtil.getUsernameJwt(token);
        favoriteService.AddFavorite(username,productid);
        return Result.success("成功添加收藏");
    }
    @DeleteMapping("/{productid}")
    public Result DeleteFavorite(@PathVariable long productid, HttpServletRequest httprequest){
        String token = httprequest.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = JwtUtil.getUsernameJwt(token);
        favoriteService.DeleteFavorite(username,productid);
        return Result.success("取消收藏");
    }
    @GetMapping()
    public Result FindAllFavorite(HttpServletRequest httprequest){
        String token = httprequest.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = JwtUtil.getUsernameJwt(token);
        List<Products> products = favoriteService.FindAllFavorite(username);
        return Result.success(products);
    }
}
