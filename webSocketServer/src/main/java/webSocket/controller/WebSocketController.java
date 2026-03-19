package webSocket.controller;

import model.Result;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;


@RestController
@RequestMapping("/user")
public class WebSocketController {
    /**
     * 获取用户名
     *
     * @param httpSession HttpSession
     * @return String
     */
    @GetMapping("/getUsername")
    public Result<String> getUsername(HttpSession httpSession) {
        return Result.success((String) httpSession.getAttribute("currentUser"));
    }
}

