package InstructorManagement;

import CourseManagement.Course;
import java.util.ArrayList;
import java.util.Objects;
import UserAccount.Role;
import CourseManagement.Lesson;
import Student.Student;

public class Instructor {

    private int userId;
    private String userName;
    private String email;
    private String hashedPass;
    private ArrayList<Course> createdCourses;
    private final Role role = Role.INSTRUCTOR;

    public Instructor(int userId, String userName, String email, String passwordHash) {
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

    public void setCourses(ArrayList<Course> createdCourses) {
        this.createdCourses = createdCourses;
    }

    public void addCourse(Course course) {
        if (createdCourses != null && !createdCourses.contains(course)) {
            createdCourses.add(course);
        }
    }

    public void removecreatedCourses(Course course) {
        createdCourses.remove(course);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Instructor that = (Instructor) o;
        return userId == that.userId;
    }

    public String getPasswordHash() {
        return hashedPass;
    }

    public void setPasswordHash(String passwordHash) {
        this.hashedPass = passwordHash;
    }

    public ArrayList getCreatedCourses() {
        return createdCourses;
    }

    public void setCreatedCourses(ArrayList createdCourses) {
        this.createdCourses = createdCourses;
    }

    public void editCourse(Course c, int courseId, String title, String description, String instructorId) {
        c.setCourseId(courseId);
        c.setTitle(title);
        c.setDescription(description);
        c.setInstructorId(instructorId);
    }

    public void deleteCourse(Course c) {
        this.createdCourses.remove(c);
    }

    public void editLesson(Lesson l, int lessonId, String title, String content) {
        l.setTitle(title);
        l.setContent(content);
        l.setLessonId(lessonId);

    }

    public void addLesson(int lessonId, String title, String content) {
        Lesson newLesson = new Lesson(lessonId, title, content);
    }

    public void deleteLesson(Course c, int lessonId) {
        c.removeLesson(lessonId);
    }

    public void viewStudent(Course c) {
        ArrayList<Student> students = new ArrayList<>();
        students.addAll(c.getStudents());
        for (Student s : students) {
            System.out.println(s);
        }
    }
}