package Frontend;

import CourseManagement.Course;
import CourseManagement.CourseManagementSystem;
import CourseManagement.Lesson;
import CourseManagement.Progress;
import Student.Student;
import Student.Certificate;
import Student.StudentManagement;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

// <editor-fold defaultstate="collapsed" desc="StudentDashboard Class">
/**
 * Student dashboard for course management and progress tracking
 */
public class StudentDashboard extends JFrame {

    // <editor-fold defaultstate="collapsed" desc="Variables">
    private StudentManagement studentManagement;
    private CourseManagementSystem courseManagement;
    private Student currentStudent;
    private JTable enrolledCoursesTable, availableCoursesTable, lessonsTable, certificatesTable;
    private DefaultTableModel enrolledModel, availableModel, lessonsModel, certificatesModel;
    private JTextArea courseDescriptionArea, lessonContentArea;
    private JLabel progressLabel;
    private JButton enrollButton, unenrollButton, viewLessonsButton, completeLessonButton, refreshButton, logoutButton;
    private JTabbedPane tabbedPane;
    private Course currentViewedCourse; // Track the course currently being viewed in Course Details tab
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructors">
    public StudentDashboard() {
        initializeComponents();
        setupUI();
        loadData();
    }

    public StudentDashboard(Student student) {
        this.currentStudent = student;
        initializeComponents();
        setupUI();
        loadData();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Initialization Methods">
    private void initializeComponents() {
        studentManagement = new StudentManagement();
        courseManagement = new CourseManagementSystem();

        // Initialize student (for demo, you can get from login)
        if (currentStudent == null) {
            // Get first student as demo - in real app, get from login
            ArrayList<Student> students = studentManagement.getAllStudents();
            if (!students.isEmpty()) {
                currentStudent = students.get(0);
            }
        }

        // Initialize tables
        enrolledModel = new DefaultTableModel(new Object[] { "Course ID", "Title", "Description", "Progress %" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        availableModel = new DefaultTableModel(new Object[] { "Course ID", "Title", "Description", "Instructor" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        lessonsModel = new DefaultTableModel(new Object[] { "Lesson ID", "Title", "Status" }, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        certificatesModel = new DefaultTableModel(new Object[] { "Certificate ID", "Course", "Date Earned" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        enrolledCoursesTable = new JTable(enrolledModel);
        availableCoursesTable = new JTable(availableModel);
        lessonsTable = new JTable(lessonsModel);
        certificatesTable = new JTable(certificatesModel);

        courseDescriptionArea = new JTextArea(5, 30);
        courseDescriptionArea.setEditable(false);
        courseDescriptionArea.setWrapStyleWord(true);
        courseDescriptionArea.setLineWrap(true);

        lessonContentArea = new JTextArea(10, 40);
        lessonContentArea.setEditable(false);
        lessonContentArea.setWrapStyleWord(true);
        lessonContentArea.setLineWrap(true);

        progressLabel = new JLabel("Progress: 0%");

        enrollButton = new JButton("Enroll in Course");
        unenrollButton = new JButton("Unenroll from Course");
        viewLessonsButton = new JButton("View Lessons");
        completeLessonButton = new JButton("Mark Lesson Complete");
        refreshButton = new JButton("Refresh");
        logoutButton = new JButton("Logout");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="UI Setup Methods">
    private void setupUI() {
        setTitle("Student Dashboard");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tabbedPane = new JTabbedPane();

        // Tab 1: My Courses
        JPanel myCoursesPanel = createMyCoursesPanel();
        tabbedPane.addTab("My Courses", myCoursesPanel);

        // Tab 2: Browse Courses
        JPanel browsePanel = createBrowseCoursesPanel();
        tabbedPane.addTab("Browse Courses", browsePanel);

        // Tab 3: Course Details
        JPanel courseDetailsPanel = createCourseDetailsPanel();
        tabbedPane.addTab("Course Details", courseDetailsPanel);

        // Tab 4: Certificates
        JPanel certificatesPanel = createCertificatesPanel();
        tabbedPane.addTab("Certificates", certificatesPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Bottom panel with logout
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(refreshButton);
        bottomPanel.add(logoutButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Add action listeners
        enrollButton.addActionListener(e -> handleEnroll());
        unenrollButton.addActionListener(e -> handleUnenroll());
        viewLessonsButton.addActionListener(e -> handleViewLessons());
        completeLessonButton.addActionListener(e -> handleCompleteLesson());
        refreshButton.addActionListener(e -> loadData());
        logoutButton.addActionListener(e -> handleLogout());

        enrolledCoursesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateCourseDescription();
            }
        });

        availableCoursesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateAvailableCourseDescription();
            }
        });

        lessonsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateLessonContent();
            }
        });
    }

    private JPanel createMyCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("My Enrolled Courses");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(enrolledCoursesTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(viewLessonsButton);
        buttonPanel.add(unenrollButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createBrowseCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Available Courses");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(availableCoursesTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(enrollButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCertificatesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("My Earned Certificates");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(certificatesTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCourseDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Top: Course Description
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Course Description:"), BorderLayout.NORTH);
        topPanel.add(new JScrollPane(courseDescriptionArea), BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);

        // Center: Lessons
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JLabel("Lessons:"), BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(lessonsTable), BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.CENTER);

        // Bottom: Lesson Content and Progress
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(new JLabel("Lesson Content:"), BorderLayout.NORTH);
        bottomPanel.add(new JScrollPane(lessonContentArea), BorderLayout.CENTER);

        JPanel progressPanel = new JPanel(new FlowLayout());
        progressPanel.add(progressLabel);
        progressPanel.add(completeLessonButton);
        bottomPanel.add(progressPanel, BorderLayout.SOUTH);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Data Management Methods">
    private void loadData() {
        if (currentStudent == null) {
            JOptionPane.showMessageDialog(this, "No student logged in", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Reload student data
        currentStudent = studentManagement.findStudent(currentStudent.getUserID());
        if (currentStudent == null)
            return;

        // Load enrolled courses
        enrolledModel.setRowCount(0);
        for (Course course : currentStudent.getEnrolledCourses()) {
            double progress = getCourseProgress(course);
            enrolledModel.addRow(new Object[] {
                    course.getCourseId(),
                    course.getTitle(),
                    course.getDescription(),
                    String.format("%.1f%%", progress)
            });
        }

        // Load available courses (not enrolled)
        availableModel.setRowCount(0);
        ArrayList<Course> allCourses = courseManagement.getAllCourses();
        for (Course course : allCourses) {
            if (!currentStudent.getEnrolledCourses().contains(course)) {
                availableModel.addRow(new Object[] {
                        course.getCourseId(),
                        course.getTitle(),
                        course.getDescription(),
                        course.getInstructorId()
                });
            }
        }

        // Load certificates
        certificatesModel.setRowCount(0);
        for (Certificate cert : currentStudent.getCertificates()) {
            certificatesModel.addRow(new Object[] {
                    cert.getCertificateId(),
                    cert.getCourseTitle(),
                    cert.getDateEarned()
            });
        }
    }

    private double getCourseProgress(Course course) {
        for (Progress progress : currentStudent.getProgresses()) {
            if (progress.getCourse().equals(course)) {
                return progress.getPercentage();
            }
        }
        return 0.0;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Event Handlers">
    private void handleEnroll() {
        int selectedRow = availableCoursesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to enroll", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int courseId = (Integer) availableModel.getValueAt(selectedRow, 0);
        Course course = courseManagement.findCourse(courseId);

        if (course == null) {
            JOptionPane.showMessageDialog(this, "Course not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (currentStudent.getEnrolledCourses().contains(course)) {
            JOptionPane.showMessageDialog(this, "Already enrolled in this course", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        studentManagement.enrollStudentInCourse(currentStudent.getUserID(), course);
        JOptionPane.showMessageDialog(this, "Successfully enrolled in course: " + course.getTitle(), "Success",
                JOptionPane.INFORMATION_MESSAGE);
        loadData();
    }

    private void handleUnenroll() {
        int selectedRow = enrolledCoursesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to unenroll", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int courseId = (Integer) enrolledModel.getValueAt(selectedRow, 0);
        Course course = courseManagement.findCourse(courseId);

        if (course == null) {
            JOptionPane.showMessageDialog(this, "Course not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to unenroll from: " + course.getTitle() + "?",
                "Confirm Unenroll", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            studentManagement.unenrollStudentFromCourse(currentStudent.getUserID(), course);
            JOptionPane.showMessageDialog(this, "Successfully unenrolled from course", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            loadData();
            // Clear course details
            courseDescriptionArea.setText("");
            lessonsModel.setRowCount(0);
            lessonContentArea.setText("");
            progressLabel.setText("Progress: 0%");
        }
    }

    private void handleViewLessons() {
        int selectedRow = enrolledCoursesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to view lessons", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int courseId = (Integer) enrolledModel.getValueAt(selectedRow, 0);
        Course course = courseManagement.findCourse(courseId);

        if (course == null)
            return;

        // Store the currently viewed course
        currentViewedCourse = course;

        // Switch to course details tab
        tabbedPane.setSelectedIndex(2);

        // Load course details
        courseDescriptionArea.setText(course.getDescription());

        // Load lessons
        lessonsModel.setRowCount(0);
        Progress progress = getProgressForCourse(course);
        ArrayList<Lesson> completedLessons = progress != null ? progress.getCompletedLessons() : new ArrayList<>();

        for (Lesson lesson : course.getLessons()) {
            String status = completedLessons.contains(lesson) ? "Completed" : "Not Completed";
            lessonsModel.addRow(new Object[] {
                    lesson.getLessonId(),
                    lesson.getTitle(),
                    status
            });
        }

        // Update progress
        if (progress != null) {
            progressLabel.setText(String.format("Progress: %.1f%%", progress.getPercentage()));
        } else {
            progressLabel.setText("Progress: 0%");
        }
    }

    private Progress getProgressForCourse(Course course) {
        for (Progress progress : currentStudent.getProgresses()) {
            if (progress.getCourse().equals(course)) {
                return progress;
            }
        }
        return null;
    }

    private void updateCourseDescription() {
        int selectedRow = enrolledCoursesTable.getSelectedRow();
        if (selectedRow != -1) {
            int courseId = (Integer) enrolledModel.getValueAt(selectedRow, 0);
            Course course = courseManagement.findCourse(courseId);
            if (course != null) {
                courseDescriptionArea.setText(course.getDescription());
            }
        }
    }

    private void updateAvailableCourseDescription() {
        int selectedRow = availableCoursesTable.getSelectedRow();
        if (selectedRow != -1) {
            int courseId = (Integer) availableModel.getValueAt(selectedRow, 0);
            Course course = courseManagement.findCourse(courseId);
            if (course != null) {
                courseDescriptionArea.setText(course.getDescription());
            }
        }
    }

    private void updateLessonContent() {
        int selectedRow = lessonsTable.getSelectedRow();
        if (selectedRow != -1) {
            int lessonId = (Integer) lessonsModel.getValueAt(selectedRow, 0);

            // Use the currently viewed course instead of checking table selection
            if (currentViewedCourse == null)
                return;

            Lesson lesson = currentViewedCourse.findLesson(lessonId);
            if (lesson != null) {
                lessonContentArea.setText(lesson.getContent());
            }
        }
    }

    private void handleCompleteLesson() {
        int selectedRow = lessonsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a lesson to complete", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Use the currently viewed course instead of checking table selection
        if (currentViewedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please view a course's lessons first", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Course course = currentViewedCourse;

        int lessonId = (Integer) lessonsModel.getValueAt(selectedRow, 0);
        Lesson lesson = course.findLesson(lessonId);
        if (lesson == null)
            return;

        Progress progress = getProgressForCourse(course);
        if (progress == null) {
            JOptionPane.showMessageDialog(this, "Progress not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (progress.getCompletedLessons().contains(lesson)) {
            JOptionPane.showMessageDialog(this, "Lesson already completed", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        progress.addCompletedLesson(lesson);
        course.completeLesson(lesson, currentStudent);
        studentManagement.saveStudents();

        // Check for certificate
        if (progress.getPercentage() >= 100.0) {
            boolean hasCertificate = false;
            for (Certificate c : currentStudent.getCertificates()) {
                if (c.getCourseId() == course.getCourseId()) {
                    hasCertificate = true;
                    break;
                }
            }

            if (!hasCertificate) {
                Certificate newCert = new Certificate(currentStudent.getUserID(), course.getCourseId(),
                        course.getTitle());
                currentStudent.addCertificate(newCert);
                studentManagement.saveStudents();
                JOptionPane.showMessageDialog(this,
                        "Congratulations! You have completed the course and earned a certificate!",
                        "Certificate Earned", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        JOptionPane.showMessageDialog(this, "Lesson marked as complete!", "Success", JOptionPane.INFORMATION_MESSAGE);

        // Refresh lessons table
        handleViewLessons();
        loadData();
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame();
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Main Method">
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentDashboard().setVisible(true));
    }
    // </editor-fold>
}
// </editor-fold>
