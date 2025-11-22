package Student;

import java.util.UUID;
import java.time.LocalDate;

public class Certificate {
    private String certificateId;
    private int studentId;
    private int courseId;
    private String courseTitle;
    private String dateEarned;

    public Certificate(int studentId, int courseId, String courseTitle) {
        this.certificateId = UUID.randomUUID().toString();
        this.studentId = studentId;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.dateEarned = LocalDate.now().toString();
    }

    // Constructor for loading from JSON
    public Certificate(String certificateId, int studentId, int courseId, String courseTitle, String dateEarned) {
        this.certificateId = certificateId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.dateEarned = dateEarned;
    }

    public String getCertificateId() {
        return certificateId;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public String getDateEarned() {
        return dateEarned;
    }
}
