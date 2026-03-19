package webSocket.mapper;

import org.apache.ibatis.annotations.Mapper;
import webSocket.pojo.ChatMessage;

import java.util.List;

@Mapper
public interface WebSocketMapper {
     void saveMessage(String userName, String toName, String tempMessage, boolean isSystem);
     List<ChatMessage> getHistoryMessage(String userName) ;
}
