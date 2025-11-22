package UserAccount;

import CourseManagement.Course;
import java.util.ArrayList;


public class AdminRole {
     private int userId;
    private String userName;
    private String email;
    private String hashedPass;
    private ArrayList<Course> createdCourses;
    private final Role role = Role.INSTRUCTOR;
    
    public AdminRole(int userId, String userName,String email,String passwordHash ){
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.hashedPass = passwordHash;
        this.createdCourses = new ArrayList<>();
    }
        public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashedPass() {
        return hashedPass;
    }

    public void setHashedPass(String hashedPass) {
        this.hashedPass = hashedPass;
    }

    public ArrayList<Course> getCourses() {
        return createdCourses;
    }
    

}
