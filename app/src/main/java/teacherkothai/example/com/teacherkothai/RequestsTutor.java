package teacherkothai.example.com.teacherkothai;



public class RequestsTutor {
    private String reqType;

    public RequestsTutor(String reqType) {
        this.reqType = reqType;
    }

    public RequestsTutor() {
    }

    public String getReqType() {
        return reqType;
    }

    public void setReqType(String reqType) {
        this.reqType = reqType;
    }
}
