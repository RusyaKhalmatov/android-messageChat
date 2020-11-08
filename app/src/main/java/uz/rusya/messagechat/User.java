package uz.rusya.messagechat;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String username;
    private String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, String username) {
        this.username = username;
        this.email = email;

    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public Map<String,Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("email", email);
        result.put("username", username);

        return result;
    }
}
