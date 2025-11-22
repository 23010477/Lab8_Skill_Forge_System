package UserAccount;

import java.util.List;
import CourseManagement.Course;
import java.time.Instant;
import java.util.ArrayList;
import CourseManagement.CourseStatus;
import JsonDBManager.JsonDBManager;



public class AdminRole {
     private int userId;
    private String userName;
    private String email;
    private String hashedPass;
    private ArrayList<Course> createdCourses;
    private List<Course> courses = new ArrayList<>();

    
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
    
public Course getCourseById(int courseId) {
    for (Course c : createdCourses) {
        if (c.getCourseId() == courseId) { 
            return c;
        }
    }
    return null;
}

  public void approveCourse(int courseId) {
    Course c = getCourseById(courseId);
    if (c != null && c.getStatus() == CourseStatus.PENDING) {
        c.setstatus(CourseStatus.APPROVED);
        System.out.println("Course approved: " + c.getTitle());
        
                JsonDBManager.saveCourses(createdCourses);

    } else {
        System.out.println("Course not found or not pending.");
    }
}
  public void rejectCourse(int courseId) {
    Course c = getCourseById(courseId);
    if (c != null && c.getStatus() == CourseStatus.PENDING) {
        c.setstatus(CourseStatus.REJECTED);
        System.out.println("Course rejected: " + c.getTitle());
        
                JsonDBManager.saveCourses(createdCourses);

    }
  }

}
