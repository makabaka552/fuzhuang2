package auth.controller;

import auth.service.CaptchaService;
import auth.service.UserService;
import lombok.extern.slf4j.Slf4j;
import model.Result;
import model.ResultCodeEnum;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static utils.JwtUtil.generateJwt;



@Slf4j
@RestController
// 接口前缀
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 生成验证码图片
     */
    @GetMapping("/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        captchaService.generateCaptchaImage(request, response);
    }

    /**
     * 验证验证码
     */
    @PostMapping("/verify-captcha")
    public Result verifyCaptcha(@RequestBody Map<String, String> params, HttpServletRequest request) {
        String userCaptcha = params.get("captcha");
        if (userCaptcha == null || userCaptcha.isEmpty()) {
            return Result.failure(ResultCodeEnum.FAIL, "验证码不能为空");
        }

        boolean isValid = captchaService.validateCaptcha(request, userCaptcha);
        if (isValid) {
            return Result.success("验证码验证成功");
        } else {
            return Result.failure(ResultCodeEnum.FAIL, "验证码错误或已过期");
        }
    }

    // 注册
    @PostMapping("/register")
    public Result register(@RequestBody Map<String, String> registerParams, HttpServletRequest request) {
        String username = registerParams.get("username");
        String password = registerParams.get("password");
        String captcha = registerParams.get("captcha");

        // 验证码校验
        if (captcha != null && !captcha.isEmpty()) {
            boolean isValid = captchaService.validateCaptcha(request, captcha);
            if (!isValid) {
                return Result.failure(ResultCodeEnum.FAIL, "验证码错误或已过期");
            }
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        User dbuser = userService.findByUsername(username);
        if (dbuser != null) {
            return Result.failure(ResultCodeEnum.FAIL, "用户名已存在！");
        }
        userService.register(user);
        return Result.success("注册成功");
    }

    // 登录
    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> loginParams, HttpServletRequest request, HttpSession httpSession) {
        String username = loginParams.get("username");
        String password = loginParams.get("password");
        String captcha = loginParams.get("captcha");

        // 验证码校验
        boolean isValid = captchaService.validateCaptcha(request, captcha);
        if (!isValid) {
            return Result.failure(ResultCodeEnum.FAIL, "验证码错误或已过期");
        }

        User e = userService.login(username, password);

        if (e != null) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("id",e.getUsername());
            claims.put("username", e.getUsername());
            String uuid = UUID.randomUUID().toString();
            claims.put("uuid",uuid);
            String jwt = generateJwt(claims);
            if(stringRedisTemplate.hasKey(redisKeyUserName(username)))
            {
                stringRedisTemplate.delete(redisKeyUserName(username));
            }
            //新的token存放到redis
            ValueOperations<String,String> operations = stringRedisTemplate.opsForValue();
            operations.set(redisKeyUserName(username),jwt,1, TimeUnit.HOURS);
            httpSession.setAttribute("currentUser", username);
            return Result.success(jwt);
        } else {
            return Result.failure(ResultCodeEnum.FAIL, "用户名或密码错误");
        }
    }

    @PostMapping("/admin/login")
    public Result adminlogin(@RequestBody Map<String, String> loginParams, HttpServletRequest request) {
        String username = loginParams.get("username");
        String password = loginParams.get("password");
        String captcha = loginParams.get("captcha");

        // 验证码校验
        boolean isValid = captchaService.validateCaptcha(request, captcha);
        if (!isValid) {
            return Result.failure(ResultCodeEnum.FAIL, "验证码错误或已过期");
        }

        User e = userService.adminlogin(username, password);

        if (e != null) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("id",e.getUsername());
            claims.put("username", e.getUsername());
            String uuid = UUID.randomUUID().toString();
            claims.put("uuid",uuid);
            String jwt = generateJwt(claims);
            if (stringRedisTemplate.hasKey(redisKeyAdminName(username))){
                stringRedisTemplate.delete(redisKeyAdminName(username));
            }
            ValueOperations<String,String> operations = stringRedisTemplate.opsForValue();
            operations.set(redisKeyAdminName(username),jwt,1,TimeUnit.HOURS);
            return Result.success(jwt);
        } else {
            return Result.failure(ResultCodeEnum.FAIL, "用户名或密码错误");
        }
    }

    // 退出登录
    @RequestMapping("/logout")
    public Result logout(@RequestHeader(value = "uid") String userId) {
        if (userId != null) {
            // stringRedisTemplate.delete(userId);
            return Result.success();
        }
        return Result.failure(ResultCodeEnum.FAIL, "未知用户");
    }
    private String redisKeyUserName(String userName){
        return "userName:"+userName;
    }
    private String redisKeyAdminName(String userName){
        return "adminName:"+userName;
    }
}
