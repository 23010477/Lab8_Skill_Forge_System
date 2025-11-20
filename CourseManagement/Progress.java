package CourseManagement;

import Student.Student;
import CourseManagement.Course;

public class Progress {
    private Student student;
    private Course course;
    private double percentage;

    public Progress(Student student, Course course) {
        this.setStudent(student);
        this.setCourse(course);
        this.percentage = 0;
    }
    public Student getStudent() {
        return student;
    }
    public void setStudent(Student student) {
        this.student = student;
    }
    public Course getCourse() {
        return course;
    }
    public void setCourse(Course course) {
        this.course = course;
    }
    public double getpercentage() {
        return percentage;
    }
    public void setpercentage(double percentage) {
        this.percentage = percentage;
    }
}
