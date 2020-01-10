package teacherkothai.example.com.teacherkothai;



public class CoursesDataProvider {

    private int val;
    private String subjectName;

    public CoursesDataProvider() {
    }

    public CoursesDataProvider(int val, String subjectName) {
        this.val = val;
        this.subjectName = subjectName;

    }

    public int getVal() {

        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}
