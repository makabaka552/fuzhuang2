package user.controller;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import model.PageBeam;
import model.Result;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import user.service.UserService;
import utils.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public Result GetUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Claims claims = JwtUtil.parseJWT(token);
        String username = (String) claims.get("username");
        log.info(username);
        User user = userService.FindUser(username);
        return Result.success(user);
    }

    @GetMapping("/points")
    public Result<Integer> getUserPoints(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Claims claims = JwtUtil.parseJWT(token);
        String username = (String) claims.get("username");
        User user = userService.FindUser(username);
        return Result.success(user.getPoints());
    }

    @GetMapping("/admin/list")
    public Result GetUserList(@RequestParam(defaultValue = "1") Integer page,
                              @RequestParam(defaultValue = "10") Integer pageSize
    ){
        PageBeam pageBeam = userService.GetUserList(page, pageSize);
        return Result.success(pageBeam);
    }
    
    @PostMapping("/admin/create")
    public Result createUser(@RequestBody User user){
        userService.createUser(user);
        return Result.success("创建用户成功");
    }
    
    @PutMapping("/admin/{userId}/update")
    public Result updateUser(@RequestBody User user,@PathVariable Integer userId){
        userService.updateUser(user,userId);
        return Result.success("更新用户成功");
    }
    
    @DeleteMapping("/admin/{userid}/delete")
    public Result deleteUser(@PathVariable Integer userid){
        userService.deleteUser(userid);
        return Result.success("删除用户成功");
    }

}

