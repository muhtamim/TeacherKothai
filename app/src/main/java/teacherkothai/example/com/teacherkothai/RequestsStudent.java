package teacherkothai.example.com.teacherkothai;



public class RequestsStudent {
    private String reqType;

    public RequestsStudent(String reqType) {
        this.reqType = reqType;
    }

    public RequestsStudent() {

    }

    public String getReqType() {
        return reqType;
    }

    public void setReqType(String reqType) {
        this.reqType = reqType;
    }
}
