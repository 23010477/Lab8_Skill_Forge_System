package CourseManagement;

import Student.Student;
import CourseManagement.Course;

import java.util.ArrayList;

public class Progress {
    private Student student;
    private Course course;
    private ArrayList<Lesson> completedLessons;


    public Progress(Student student, Course course) {
        this.setStudent(student);
        this.setCourse(course);
        this.completedLessons = new ArrayList<>();
    }

    public void AddCompletedLesson(Lesson lesson){
        if (lesson != null && !completedLessons.contains(lesson)){
            completedLessons.add(lesson);
        }
    }

    public Student getStudent() {
        return student;
    }
    public ArrayList<Lesson> getCompletedLessons(){
        return completedLessons;
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
        if(course.getLessons().isEmpty()) return 0;
        return (double) (completedLessons.size() * 100) / course.getLessons().size();

    }

}
