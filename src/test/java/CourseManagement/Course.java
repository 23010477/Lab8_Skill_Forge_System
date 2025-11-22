package CourseManagement;

import Student.Student;
import java.util.ArrayList;

public class Course {
    private int courseId;
    private String title;
    private String description;
    private String instructorId;
    private ArrayList<Lesson> lessons;
    private ArrayList<Lesson> completedLessons;
    private ArrayList<Student> students;
    private CourseStatus status = CourseStatus.PENDING;

    public Course(int courseId, String title, String description, String instructorId) {
        this.setCourseId(courseId);
        this.setTitle(title);
        this.setDescription(description);
        this.setInstructorId(instructorId);
        this.lessons = new ArrayList<>();
        this.completedLessons = new ArrayList<>();
        this.students = new ArrayList<>();
        this.status = CourseStatus.PENDING;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

    public ArrayList<Lesson> getLessons() {
        return lessons;
    }

    public void addLesson(Lesson lesson) {
        if (lesson != null && !lessons.contains(lesson)) {
            this.lessons.add(lesson);
        }
    }

    public void removeLesson(Lesson lesson) {
        lessons.remove(lesson);
    }

    public void removeLesson(int lessonId) {
        lessons.removeIf(l -> l.getLessonId() == lessonId);
    }

    public Lesson findLesson(int lessonId) {
        for (Lesson lesson : lessons) {
            if (lesson.getLessonId() == lessonId) {
                return lesson;
            }
        }
        return null;
    }
    
    public void updateLesson(int lessonId, String newTitle, String newContent) {
        Lesson lesson = findLesson(lessonId);
        if (lesson != null) {
            lesson.setTitle(newTitle);
            lesson.setContent(newContent);
        }
    }

    public void completeLesson(Lesson lesson, Student student) {
        Progress stdProgress = null;
        for( Progress p : student.getProgresses()){
            if(p.getCourse().equals(this)){
                stdProgress = p;
                break;
            }
        }
        if (stdProgress != null ){
            stdProgress.addCompletedLesson(lesson);
        }
    }

    public Lesson findLessonById(int lessonId){
        for (Lesson l : lessons){
            if (l.getLessonId() == lessonId)
                return l;
        }
        return null;
    }
    
    public ArrayList<Student> getStudents() {
        return students;
    }
}