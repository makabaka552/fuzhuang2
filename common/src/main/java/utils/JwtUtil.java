package utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;
import io.jsonwebtoken.Jwts;

public class JwtUtil {

    private static final String KEY = "ptu";

    // 接收业务数据,生成token并返回
    private static String signKey = "itheima";
    private static Long expire = 43200000L;// 12小时：12*60*1000

    public static String generateJwt(Map<String, Object> claims) {
        String jwt = Jwts.builder()
                .setClaims(claims) // 自定义内容(载荷)
                .signWith(SignatureAlgorithm.HS256, signKey) // 签名算法
                .setExpiration(new Date(System.currentTimeMillis() + expire)) // 有效期
                .compact();
        return jwt;
    }

    public static Claims parseJWT(String jwt) {
        Claims claims = Jwts.parser()
                .setSigningKey(signKey) // 指定签名秘钥
                .parseClaimsJws(jwt) // 解析令牌
                .getBody();
        return claims;
    }

    public static String getUsernameJwt(String jwt) {
        Claims claims = JwtUtil.parseJWT(jwt);
        String username = (String) claims.get("username");
        return username;
    }

}
