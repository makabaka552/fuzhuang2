package webSocket.pojo;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webSocket.service.WebSocketService;
import webSocket.utils.MessageUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/webSocket/chat/{username}")
@Component
public class ChatEndPoint {
    // 使用静态变量保存Service
    private static WebSocketService webSocketService;

    // 通过Setter方法注入
    @Autowired
    public void setWebSocketService(WebSocketService webSocketService) {
        ChatEndPoint.webSocketService = webSocketService;
    }

    private static final Map<String,Session> onlineUsers = new ConcurrentHashMap<>();
    private String userName;
    /**
     * 建立WebSocket连接后被调用
     * @param session
     */

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String userName) throws IOException {
        //1.将session进行保存
        this.userName = userName;
        onlineUsers.put(userName,session);
        //获取历史消息
        List<ChatMessage> historyMessage = webSocketService.getHistoryMessage(userName);
        session.getBasicRemote().sendText(JSON.toJSONString(historyMessage));
        // 通知所有用户，当前用户上线了
        String message = MessageUtils.getMessage(true, null, getFriends());
        broadcastAllUsers(message);
    }


    private Set<String> getFriends() {
        return onlineUsers.keySet();
    }
    private void broadcastAllUsers(String message) {
        try {
            Set<Map.Entry<String, Session>> entries = onlineUsers.entrySet();

            for (Map.Entry<String, Session> entry : entries) {
                // 获取到所有用户对应的 session 对象
                Session session = entry.getValue();
                // 使用 getBasicRemote() 方法发送同步消息
                session.getBasicRemote().sendText(message);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    /**
     *浏览器发的消息到服务端，该方法会被调用
     * @param message
     */
    @OnMessage
    public void onMessage(String message) {
        try {
            // 将消息推送给指定的用户
            Message msg = JSON.parseObject(message, Message.class);
            // 获取消息接收方的用户名
            String toName = msg.getToName();
            String tempMessage = msg.getMessage();
            //数据持久化，保存到数据库
            webSocketService.saveMessage(this.userName, toName, tempMessage,false);
            // 获取消息接收方用户对象的 session 对象
            Session session = onlineUsers.get(toName);
            String messageToSend = MessageUtils.getMessage(false, this.userName, tempMessage);
            session.getBasicRemote().sendText(messageToSend);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     *断开时被调用
     * @param session
     */
    @OnClose
    public void onClose(Session session) throws IOException {
        // 1.从 onlineUsers 中删除当前用户的 session 对象，表示当前用户已下线
        if (this.userName != null) {
            Session remove = onlineUsers.remove(this.userName);
            if (remove != null) {
                remove.close();
            }
            session.close();
        }
        // 2.通知其他用户，当前用户已下线
        // 注意：不是发送类似于 xxx 已下线的消息，而是向在线用户重新发送一次当前在线的所有用户
        String message = MessageUtils.getMessage(true, null, getFriends());
        broadcastAllUsers(message);
    }

}
