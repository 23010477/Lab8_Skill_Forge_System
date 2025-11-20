package InstructorManagement;

import CourseManagement.Course;
import JsonDBManager.JsonDBManager;
import java.util.ArrayList;

public class InstructorManagement {
    private ArrayList<Instructor> instructors;
    private static final String INSTRUCTORS_FILE = "src/main/java/JsonDBManager/Users.json";

    public InstructorManagement() {
        this.instructors = new ArrayList<>();
        loadInstructors();
    }

    public void loadInstructors() {
        this.instructors = JsonDBManager.readInstructors(INSTRUCTORS_FILE);
    }

    public void saveInstructors() {
        JsonDBManager.writeInstructors(INSTRUCTORS_FILE, instructors);
    }

    public void addInstructor(Instructor instructor) {
        if (instructor == null) {
            System.out.println("Invalid instructor");
            return;
        }
        if (instructors.contains(instructor)) {
            System.out.println("Instructor already exists");
            return;
        }
        instructors.add(instructor);
        saveInstructors();
        System.out.println("Instructor added successfully");
    }

    public void removeInstructor(int instructorId) {
        if (instructors.removeIf(i -> i.getUserId() == instructorId)) {
            saveInstructors();
            System.out.println("Instructor removed successfully");
        } else {
            System.out.println("Instructor not found");
        }
    }

    public Instructor findInstructor(int instructorId) {
        for (Instructor instructor : instructors) {
            if (instructor.getUserId() == instructorId) {
                return instructor;
            }
        }
        return null;
    }

    public Instructor findInstructorByName(String name) {
        for (Instructor instructor : instructors) {
            if (instructor.getUserName() != null && instructor.getUserName().equals(name)) {
                return instructor;
            }
        }
        return null;
    }

    public Instructor findInstructorByEmail(String email) {
        for (Instructor instructor : instructors) {
            if (instructor.getEmail() != null && instructor.getEmail().equals(email)) {
                return instructor;
            }
        }
        return null;
    }

    public void assignCourseToInstructor(int instructorId, Course course) {
        Instructor instructor = findInstructor(instructorId);
        if (instructor == null) {
            System.out.println("Instructor not found");
            return;
        }
        if (course == null) {
            System.out.println("Invalid course");
            return;
        }
        instructor.addCourse(course);
        course.setInstructorId(String.valueOf(instructorId));
        saveInstructors();
        System.out.println("Course assigned to instructor successfully");
    }

    public void removeCourseFromInstructor(int instructorId, Course course) {
        Instructor instructor = findInstructor(instructorId);
        if (instructor == null) {
            System.out.println("Instructor not found");
            return;
        }
        if (course == null) {
            System.out.println("Invalid course");
            return;
        }
        instructor.removecreatedCourses(course);
        saveInstructors();
        System.out.println("Course removed from instructor successfully");
    }

    public ArrayList<Instructor> getAllInstructors() {
        return new ArrayList<>(instructors);
    }

    public void updateInstructor(Instructor updatedInstructor) {
        if (updatedInstructor == null) {
            System.out.println("Invalid instructor");
            return;
        }
        for (int i = 0; i < instructors.size(); i++) {
            if (instructors.get(i).getUserId() == updatedInstructor.getUserId()) {
                instructors.set(i, updatedInstructor);
                saveInstructors();
                System.out.println("Instructor updated successfully");
                return;
            }
        }
        System.out.println("Instructor not found");
    }
}

