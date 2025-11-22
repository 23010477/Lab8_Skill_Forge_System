package Student;

import CourseManagement.Course;
import JsonDBManager.JsonDBManager;
import java.util.ArrayList;
import CourseManagement.Progress;

public class StudentManagement {
    private ArrayList<Student> students;
    private static final String STUDENTS_FILE = "src/test/java/JsonDBManager/Users.json";

    public StudentManagement() {
        this.students = new ArrayList<>();
        loadStudents();
    }

    public void loadStudents() {
        this.students = JsonDBManager.readStudents(STUDENTS_FILE);
    }

    public void saveStudents() {
        JsonDBManager.writeStudents(STUDENTS_FILE, students);
    }

    public void addStudent(Student student) {
        if (student == null) {
            System.out.println("Invalid student");
            return;
        }
        if (students.contains(student)) {
            System.out.println("Student already exists");
            return;
        }
        students.add(student);
        saveStudents();
        System.out.println("Student added successfully");
    }

    public void removeStudent(int userId) {
        if (students.removeIf(s -> s.getUserID() == userId)) {
            saveStudents();
            System.out.println("Student removed successfully");
        } else {
            System.out.println("Student not found");
        }
    }

    public Student findStudent(int userId) {
        for (Student student : students) {
            if (student.getUserID() == userId) {
                return student;
            }
        }
        return null;
    }

    public Student findStudentByUsername(String username) {
        for (Student student : students) {
            if (student.getUser() != null && student.getUser().equals(username)) {
                return student;
            }
        }
        return null;
    }

    public Student findStudentByEmail(String email) {
        for (Student student : students) {
            if (student.getEmail() != null && student.getEmail().equals(email)) {
                return student;
            }
        }
        return null;
    }

    public void enrollStudentInCourse(int userId, Course course) {
        Student student = findStudent(userId);
        if (student == null) {
            System.out.println("Student not found");
            return;
        }
        if (course == null) {
            System.out.println("Invalid course");
            return;
        }
        student.addCourse(course);
        student.progresses.add(new Progress(student, course));
        course.getStudents().add(student);
        saveStudents();
        System.out.println("Student enrolled in course successfully");
    }

    public void unenrollStudentFromCourse(int userId, Course course) {
        Student student = findStudent(userId);
        if (student == null) {
            System.out.println("Student not found");
            return;
        }
        if (course == null) {
            System.out.println("Invalid course");
            return;
        }
        student.removeCourse(course);
        course.getStudents().remove(student);
        saveStudents();
        System.out.println("Student unenrolled from course successfully");
    }

    public ArrayList<Student> getAllStudents() {
        return new ArrayList<>(students);
    }

    public void updateStudent(Student updatedStudent) {
        if (updatedStudent == null) {
            System.out.println("Invalid student");
            return;
        }
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getUserID() == updatedStudent.getUserID()) {
                students.set(i, updatedStudent);
                saveStudents();
                System.out.println("Student updated successfully");
                return;
            }
        }
        System.out.println("Student not found");
    }

    public void showStudentProgress(Student student, int courseId) {
        for (Progress p : student.getProgresses()) {
            if (p.getCourse().getCourseId() == courseId) {
                p.displayProgressTerminal();
                break;
            }

        }
        System.out.println("No progress found.");
    }
}
