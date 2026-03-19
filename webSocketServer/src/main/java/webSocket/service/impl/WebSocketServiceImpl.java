package webSocket.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webSocket.mapper.WebSocketMapper;
import webSocket.pojo.ChatMessage;
import webSocket.service.WebSocketService;

import java.util.ArrayList;
import java.util.List;

@Service
public class WebSocketServiceImpl implements WebSocketService {
    @Autowired
    private WebSocketMapper webSocketMapper;
    @Override
    public void saveMessage(String userName, String toName, String tempMessage, boolean isSystem) {
        webSocketMapper.saveMessage(userName,toName,tempMessage,isSystem);
    }


    @Override
    public List<ChatMessage> getHistoryMessage(String userName) {
        List<ChatMessage> messages = webSocketMapper.getHistoryMessage(userName);
        return messages != null ? messages : new ArrayList<>();
    }



}
