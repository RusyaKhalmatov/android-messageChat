package uz.rusya.messagechat;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Message {

    public String authorID;
    public String messageID;
    public String chatID;
    public String content;
    public String dateCreated;


    public Message() {
    }

    public Message(String authorID, String messageID,
                   String chatID, String content, String dateCreated) {
        this.authorID = authorID;
        this.messageID = messageID;
        this.chatID = chatID;
        this.content = content;
        this.dateCreated = dateCreated;
    }

    public Map<String,Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("authorID", authorID);
        result.put("messageID", messageID);
        result.put("chatID",chatID);
        result.put("content",content);
        result.put("dateCreated",dateCreated);

        return result;
    }

    public String getAuthorID() {
        return authorID;
    }

    public String getMessageID() {
        return messageID;
    }

    public String getChatID() {
        return chatID;
    }

    public String getContent() {
        return content;
    }

    public String getDateCreated() {
        return dateCreated;
    }
}
