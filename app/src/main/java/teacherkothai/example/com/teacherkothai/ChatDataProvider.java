package teacherkothai.example.com.teacherkothai;


public class ChatDataProvider {
    private String time;
    private String message;
    private String type;

    public ChatDataProvider() {
    }

    public ChatDataProvider(String time, String message, String type) {
        this.time = time;
        this.message = message;
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
