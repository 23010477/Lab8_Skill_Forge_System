package CourseManagement;

import JsonDBManager.JsonDBManager;
import java.util.ArrayList;

public class CourseManagementSystem {
    private ArrayList<Course> courses;
    private static final String COURSES_FILE = "src/main/java/JsonDBManager/Courses.json";

    public CourseManagementSystem() {
        this.courses = new ArrayList<>();
        loadCourses();
    }

    public void loadCourses() {
        this.courses = JsonDBManager.readCourses(COURSES_FILE);
    }

    public void saveCourses() {
        JsonDBManager.writeCourses(COURSES_FILE, courses);
    }

    public void addCourse(Course course) {
        if (course == null) {
            System.out.println("Invalid course");
            return;
        }
        if (courses.contains(course)) {
            System.out.println("Course already exists");
            return;
        }
        courses.add(course);
        saveCourses();
        System.out.println("Course added successfully");
    }

    public void removeCourse(int courseId) {
        if (courses.removeIf(c -> c.getCourseId() == courseId)) {
            saveCourses();
            System.out.println("Course removed successfully");
        } else {
            System.out.println("Course not found");
        }
    }

    public Course findCourse(int courseId) {
        for (Course course : courses) {
            if (course.getCourseId() == courseId) {
                return course;
            }
        }
        return null;
    }

    public Course findCourseByTitle(String title) {
        for (Course course : courses) {
            if (course.getTitle() != null && course.getTitle().equals(title)) {
                return course;
            }
        }
        return null;
    }

    public ArrayList<Course> getCoursesByInstructor(String instructorId) {
        ArrayList<Course> instructorCourses = new ArrayList<>();
        for (Course course : courses) {
            if (course.getInstructorId() != null && course.getInstructorId().equals(instructorId)) {
                instructorCourses.add(course);
            }
        }
        return instructorCourses;
    }

    public ArrayList<Course> getAllCourses() {
        return new ArrayList<>(courses);
    }

    public void updateCourse(Course updatedCourse) {
        if (updatedCourse == null) {
            System.out.println("Invalid course");
            return;
        }
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getCourseId() == updatedCourse.getCourseId()) {
                courses.set(i, updatedCourse);
                saveCourses();
                System.out.println("Course updated successfully");
                return;
            }
        }
        System.out.println("Course not found");
    }

    public void addLessonToCourse(int courseId, Lesson lesson) {
        Course course = findCourse(courseId);
        if (course == null) {
            System.out.println("Course not found");
            return;
        }
        if (lesson == null) {
            System.out.println("Invalid lesson");
            return;
        }
        course.addLesson(lesson);
        saveCourses();
        System.out.println("Lesson added to course successfully");
    }

    public void removeLessonFromCourse(int courseId, int lessonId) {
        Course course = findCourse(courseId);
        if (course == null) {
            System.out.println("Course not found");
            return;
        }
        course.removeLesson(lessonId);
        saveCourses();
        System.out.println("Lesson removed from course successfully");
    }
}

