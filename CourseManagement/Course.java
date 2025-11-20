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

    public Course(int courseId, String title, String description, String instructorId) {
        this.setCourseId(courseId);
        this.setTitle(title);
        this.setDescription(description);
        this.setInstructorId(instructorId);
        this.lessons = new ArrayList<>();
        this.students = new ArrayList<>();
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
        completedLessons.add(lesson);
    }
    
    public ArrayList<Student> getStudents() {
        return students;
    }
}