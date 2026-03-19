package webSocket.service;


import webSocket.pojo.ChatMessage;

import java.util.List;

public interface WebSocketService {

    void saveMessage(String userName, String toName, String tempMessage, boolean isSystem);

    List<ChatMessage> getHistoryMessage(String userName);

}
