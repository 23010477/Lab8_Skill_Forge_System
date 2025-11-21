package CourseManagement;

import Student.Student;

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

    public void addCompletedLesson(Lesson lesson){
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
    public double getPercentage() {
        if(course.getLessons().isEmpty()) return 0;
        return (double) (completedLessons.size() * 100) / course.getLessons().size();

    }

    public boolean courseCompletion(){
        return !course.getLessons().isEmpty() && course.getLessons().size() == completedLessons.size();
    }

}
