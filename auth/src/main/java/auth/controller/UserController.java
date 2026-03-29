package auth.controller;

import auth.service.CaptchaService;
import auth.service.SmsService;
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
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static utils.JwtUtil.generateJwt;



@Slf4j
@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        captchaService.generateCaptchaImage(request, response);
    }

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

    @PostMapping("/register")
    public Result register(@RequestBody Map<String, String> registerParams, HttpServletRequest request) {
        String username = registerParams.get("username");
        String password = registerParams.get("password");
        String phone = registerParams.get("phone");
        String captcha = registerParams.get("captcha");

        if (username == null || username.isEmpty()) {
            return Result.failure(ResultCodeEnum.FAIL, "用户名不能为空");
        }
        if (password == null || password.isEmpty()) {
            return Result.failure(ResultCodeEnum.FAIL, "密码不能为空");
        }
        if (phone == null || phone.isEmpty()) {
            return Result.failure(ResultCodeEnum.FAIL, "手机号不能为空");
        }
        if (!phone.matches("^1[3-9]\\d{9}$")) {
            return Result.failure(ResultCodeEnum.FAIL, "手机号格式不正确");
        }

        if (captcha != null && !captcha.isEmpty()) {
            boolean isValid = captchaService.validateCaptcha(request, captcha);
            if (!isValid) {
                return Result.failure(ResultCodeEnum.FAIL, "验证码错误或已过期");
            }
        }

        User dbuser = userService.findByUsername(username);
        if (dbuser != null) {
            return Result.failure(ResultCodeEnum.FAIL, "用户名已存在！");
        }

        User dbuserByPhone = userService.findByPhone(phone);
        if (dbuserByPhone != null) {
            return Result.failure(ResultCodeEnum.FAIL, "手机号已被注册！");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setPhone(phone);
        user.setRole("user");
        user.setPoints(0);

        userService.register(user);
        return Result.success("注册成功");
    }

    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> loginParams, HttpServletRequest request, HttpSession httpSession) {
        String username = loginParams.get("username");
        String password = loginParams.get("password");
        String captcha = loginParams.get("captcha");
        String loginType = loginParams.get("loginType");

        User e = null;
        if ("phone".equals(loginType)) {
            String smsCode = loginParams.get("verificationCode");
            if (smsCode == null || smsCode.isEmpty()) {
                return Result.failure(ResultCodeEnum.FAIL, "验证码不能为空");
            }
            String cachedCode = stringRedisTemplate.opsForValue().get("sms:" + username);
            if (cachedCode == null || !cachedCode.equals(smsCode)) {
                return Result.failure(ResultCodeEnum.FAIL, "短信验证码错误或已过期");
            }
            e = userService.findByPhone(username);
            if (e == null) {
                return Result.failure(ResultCodeEnum.FAIL, "该手机号未注册");
            }
        } else {
            boolean isValid = captchaService.validateCaptcha(request, captcha);
            if (!isValid) {
                return Result.failure(ResultCodeEnum.FAIL, "验证码错误或已过期");
            }
            e = userService.login(username, password);
        }

        if (e != null) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", e.getId());
            claims.put("username", e.getUsername());
            String uuid = UUID.randomUUID().toString();
            claims.put("uuid", uuid);
            String jwt = generateJwt(claims);
            if (stringRedisTemplate.hasKey(redisKeyUserName(e.getUsername()))) {
                stringRedisTemplate.delete(redisKeyUserName(e.getUsername()));
            }
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(redisKeyUserName(e.getUsername()), jwt, 1, TimeUnit.HOURS);
            httpSession.setAttribute("currentUser", e.getUsername());
            return Result.success(jwt);
        } else {
            return Result.failure(ResultCodeEnum.FAIL, "用户名或密码错误");
        }
    }

    @PostMapping("/send-code")
    public Result sendVerificationCode(@RequestBody Map<String, String> params, HttpServletRequest request) {
        String phone = params.get("phoneNumber");

        if (phone == null || phone.isEmpty()) {
            return Result.failure(ResultCodeEnum.FAIL, "手机号不能为空");
        }
        if (!phone.matches("^1[3-9]\\d{9}$")) {
            return Result.failure(ResultCodeEnum.FAIL, "手机号格式不正确");
        }

        String code = String.format("%06d", new Random().nextInt(1000000));
        stringRedisTemplate.opsForValue().set("sms:" + phone, code, 5, TimeUnit.MINUTES);

        boolean sendResult = smsService.sendVerificationCode(phone, code);
        if (sendResult) {
            log.info("验证码发送成功，手机号: {}", phone);
            return Result.success("验证码发送成功");
        } else {
            log.error("验证码发送失败，手机号: {}", phone);
            return Result.failure(ResultCodeEnum.FAIL, "验证码发送失败，请稍后重试");
        }
    }

    @PostMapping("/admin/login")
    public Result adminlogin(@RequestBody Map<String, String> loginParams, HttpServletRequest request) {
        String username = loginParams.get("username");
        String password = loginParams.get("password");
        String captcha = loginParams.get("captcha");

        boolean isValid = captchaService.validateCaptcha(request, captcha);
        if (!isValid) {
            return Result.failure(ResultCodeEnum.FAIL, "验证码错误或已过期");
        }

        User e = userService.adminlogin(username, password);

        if (e != null) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", e.getUsername());
            claims.put("username", e.getUsername());
            String uuid = UUID.randomUUID().toString();
            claims.put("uuid", uuid);
            String jwt = generateJwt(claims);
            if (stringRedisTemplate.hasKey(redisKeyAdminName(username))) {
                stringRedisTemplate.delete(redisKeyAdminName(username));
            }
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(redisKeyAdminName(username), jwt, 1, TimeUnit.HOURS);
            return Result.success(jwt);
        } else {
            return Result.failure(ResultCodeEnum.FAIL, "用户名或密码错误");
        }
    }

    @RequestMapping("/logout")
    public Result logout(@RequestHeader(value = "uid", required = false) String userId) {
        if (userId != null) {
            return Result.success();
        }
        return Result.failure(ResultCodeEnum.FAIL, "未知用户");
    }

    private String redisKeyUserName(String userName) {
        return "userName:" + userName;
    }

    private String redisKeyAdminName(String userName) {
        return "adminName:" + userName;
    }
}
