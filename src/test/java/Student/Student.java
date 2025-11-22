package Student;

import CourseManagement.Course;
import CourseManagement.Progress;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;
import UserAccount.Role;

public class Student {

    private int userID;
    private String username;
    private String email;
    private String hashedPass;
    private ArrayList<Course> enrolledCourses;
    protected ArrayList<Progress> progresses;
    private ArrayList<Certificate> certificates;
    private final Role r = Role.STUDENT;

    public Student(int userID, String username, String email, String hashedPass) {
        this.userID = userID;
        this.username = username;
        this.email = email;
        this.hashedPass = hashedPass;
        this.enrolledCourses = new ArrayList<>();
        this.progresses = new ArrayList<>();
        this.certificates = new ArrayList<>();
    }

    public int getuserID() {
        return userID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUser() {
        return username;
    }

    public void setUser(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setuserID(int userID) {
        this.userID = userID;
    }

    public String getusername() {
        return username;
    }

    public void setusername(String username) {
        this.username = username;
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

    public ArrayList<Course> getEnrolledCourses() {
        return enrolledCourses;
    }

    public ArrayList<Progress> getProgresses() {
        return progresses;
    }

    public ArrayList<Certificate> getCertificates() {
        return certificates;
    }

    public void addCertificate(Certificate certificate) {
        if (certificate != null) {
            this.certificates.add(certificate);
        }
    }

    public void addCourse(Course course) {
        if (course != null && !enrolledCourses.contains(course)) {
            enrolledCourses.add(course);
            progresses.add(new Progress(this, course));
        }
    }

    public void removeCourse(Course course) {
        if (course != null && enrolledCourses.contains(course)) {
            enrolledCourses.remove(course);
            progresses.removeIf(p -> p.getCourse().equals(course));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Student student = (Student) o;
        return userID == student.userID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID);
    }
}
