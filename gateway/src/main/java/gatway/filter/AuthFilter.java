package gatway.filter;

import gatway.config.IgnoreWhiteProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import utils.JwtUtil;


@Component
public class AuthFilter implements GlobalFilter, Ordered {
    @Autowired
    private IgnoreWhiteProperties ignoreWhite;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequest.Builder mutate = request.mutate();
        String url = request.getURI().getPath();
        // 跳过不需要验证的路径
        if (matches(url, ignoreWhite.getWhites()))
        {
            return chain.filter(exchange);
        }
        String token = request.getHeaders().getFirst("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if(token==null)
        {
            return unauthorizedResponse(exchange, HttpStatus.UNAUTHORIZED, "令牌不能为空");
        }
        try {
            //解析token
            Map<String,Object> claims = JwtUtil.parseJWT(token);
            String userId = claims.get("id").toString();
            String uuid = claims.get("uuid").toString();
            //从redis获取对应token
            ValueOperations<String,String> operations = stringRedisTemplate.opsForValue();
            String redisToken = operations.get(redisKeyUserName(userId));
            if (redisToken == null){
                redisToken = operations.get(redisKeyAdminName(userId));
            }
            if(redisToken == null){
                return unauthorizedResponse(exchange, HttpStatus.UNAUTHORIZED, "无效token");
            }
            //对比uuid是否相同
            Map<String,Object> redisClaims = JwtUtil.parseJWT(redisToken);
            String redisUUID = redisClaims.get("uuid").toString();
            if(!redisUUID.equals(uuid)){
                return unauthorizedResponse(exchange, HttpStatus.UNAUTHORIZED, "过期token");
            }
            // 设置用户信息到请求
            addHeader(mutate, "uid", userId);
            // 内部请求来源参数清除
            removeHeader(mutate, "from-source");
            // 构建新的请求
            return chain.filter(exchange.mutate().request(mutate.build()).build());
        }catch (TokenExpiredException e)
        {
            return unauthorizedResponse(exchange, HttpStatus.UNAUTHORIZED, "token过期");
        }catch (JWTVerificationException e) {
            // 处理其他JWT验证失败的情况，比如签名无效
            return unauthorizedResponse(exchange, HttpStatus.UNAUTHORIZED, "token验证失败");
        } catch (Exception e) {
            // 捕捉所有其他异常
            return unauthorizedResponse(exchange, HttpStatus.UNAUTHORIZED, "无效token");
        }

    }

    @Override
    public int getOrder() {
        return -100;
    }

    public  boolean matches(String str, List<String> strs)
    {
        if (str.isEmpty() || strs.isEmpty())
        {
            return false;
        }
        AntPathMatcher matcher = new AntPathMatcher();
        for (String pattern : strs)
        {
            if (matcher.match(pattern, str))
            {
                return true;
            }
        }
        return false;
    }
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, HttpStatus status, String errorMessage) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory().wrap(errorMessage.getBytes())));
    }

    private void addHeader(ServerHttpRequest.Builder mutate, String name, Object value)
    {
        if (value == null)
        {
            return;
        }
        String valueStr = value.toString();
        String valueEncode = null;
        try {
            valueEncode = URLEncoder.encode(valueStr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        mutate.header(name, valueEncode);

    }

    private void removeHeader(ServerHttpRequest.Builder mutate, String name)
    {
        mutate.headers(httpHeaders -> httpHeaders.remove(name)).build();
    }

    private String redisKeyUserName(String userName){
        return "userName:"+userName;
    }
    private String redisKeyAdminName(String userName){
        return "adminName:"+userName;
    }
}