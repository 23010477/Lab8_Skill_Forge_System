package Frontend;

import CourseManagement.Course;
import CourseManagement.CourseManagementSystem;
import CourseManagement.CourseStatus;
import CourseManagement.Lesson;
import CourseManagement.Progress;
import Student.Certificate;
import Student.Student;
import Student.StudentManagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

// <editor-fold defaultstate="collapsed" desc="StudentDashboard Class">
/**
 * Student Dashboard for browsing courses, viewing lessons, taking quizzes, and tracking progress
 */
public class StudentDashboard extends JFrame {

    // <editor-fold defaultstate="collapsed" desc="Variables">
    private Student currentStudent;
    private StudentManagement studentManagement;
    private CourseManagementSystem courseManagement;

    // Tables
    private JTable availableCoursesTable, enrolledCoursesTable, lessonsTable, progressTable, certificatesTable;
    private DefaultTableModel availableCoursesModel, enrolledCoursesModel, lessonsModel, progressModel, certificatesModel;

    // Buttons
    private JButton enrollButton, unenrollButton, viewLessonButton, takeQuizButton;
    private JButton refreshButton, logoutButton, viewProgressButton, markCompleteButton;

    // Text areas
    private JTextArea lessonContentArea, courseDescriptionArea;

    // Tabbed pane
    private JTabbedPane tabbedPane;

    // Selected course and lesson
    private Course selectedCourse;
    private Lesson selectedLesson;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructor">
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

        // Reload student data to get latest progress
        currentStudent = studentManagement.findStudent(currentStudent.getUserID());
        if (currentStudent == null) {
            JOptionPane.showMessageDialog(this, "Student not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Initialize table models
        availableCoursesModel = new DefaultTableModel(
                new Object[]{"Course ID", "Title", "Description", "Instructor ID", "Lessons"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        enrolledCoursesModel = new DefaultTableModel(
                new Object[]{"Course ID", "Title", "Description", "Progress %", "Lessons"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        lessonsModel = new DefaultTableModel(
                new Object[]{"Lesson ID", "Title", "Status", "Quiz Available"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        progressModel = new DefaultTableModel(
                new Object[]{"Course ID", "Course Title", "Progress %", "Completed Lessons", "Total Lessons"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        certificatesModel = new DefaultTableModel(
                new Object[]{"Certificate ID", "Course Title", "Date Earned"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Initialize tables
        availableCoursesTable = new JTable(availableCoursesModel);
        enrolledCoursesTable = new JTable(enrolledCoursesModel);
        lessonsTable = new JTable(lessonsModel);
        progressTable = new JTable(progressModel);
        certificatesTable = new JTable(certificatesModel);

        // Initialize text areas
        lessonContentArea = new JTextArea(10, 40);
        lessonContentArea.setEditable(false);
        lessonContentArea.setWrapStyleWord(true);
        lessonContentArea.setLineWrap(true);

        courseDescriptionArea = new JTextArea(5, 40);
        courseDescriptionArea.setEditable(false);
        courseDescriptionArea.setWrapStyleWord(true);
        courseDescriptionArea.setLineWrap(true);

        // Initialize buttons
        enrollButton = new JButton("Enroll in Course");
        unenrollButton = new JButton("Unenroll from Course");
        viewLessonButton = new JButton("View Lesson");
        takeQuizButton = new JButton("Take Quiz");
        markCompleteButton = new JButton("Mark Lesson Complete");
        viewProgressButton = new JButton("View Detailed Progress");
        refreshButton = new JButton("Refresh");
        logoutButton = new JButton("Logout");

        // Add action listeners
        enrollButton.addActionListener(e -> handleEnroll());
        unenrollButton.addActionListener(e -> handleUnenroll());
        viewLessonButton.addActionListener(e -> handleViewLesson());
        takeQuizButton.addActionListener(e -> handleTakeQuiz());
        markCompleteButton.addActionListener(e -> handleMarkComplete());
        viewProgressButton.addActionListener(e -> handleViewDetailedProgress());
        refreshButton.addActionListener(e -> loadData());
        logoutButton.addActionListener(e -> handleLogout());

        // Table selection listeners
        availableCoursesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateSelectedAvailableCourse();
        });

        enrolledCoursesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateSelectedEnrolledCourse();
        });

        lessonsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateSelectedLesson();
        });
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="UI Setup Methods">
    private void setupUI() {
        setTitle("Student Dashboard - " + currentStudent.getUsername());
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tabbedPane = new JTabbedPane();

        // Tab 1: Browse Courses
        JPanel browsePanel = createBrowseCoursesPanel();
        tabbedPane.addTab("Browse Courses", browsePanel);

        // Tab 2: My Courses
        JPanel myCoursesPanel = createMyCoursesPanel();
        tabbedPane.addTab("My Courses", myCoursesPanel);

        // Tab 3: My Progress
        JPanel progressPanel = createProgressPanel();
        tabbedPane.addTab("My Progress", progressPanel);

        // Tab 4: Certificates
        JPanel certificatesPanel = createCertificatesPanel();
        tabbedPane.addTab("Certificates", certificatesPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(refreshButton);
        bottomPanel.add(logoutButton);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createBrowseCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Top: Available courses table
        JLabel titleLabel = new JLabel("Available Courses (Approved Only)");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Ensure the table model is refreshed before displaying
        if (availableCoursesTable.getModel() != availableCoursesModel) {
            availableCoursesTable.setModel(availableCoursesModel);
        }
        availableCoursesModel.setRowCount(0); // Clear rows
        // Load courses explicitly here in case data isn't loaded yet
        ArrayList<Course> allCourses = courseManagement.getAllCourses();
        for (Course course : allCourses) {
            if (course.getStatus() == CourseStatus.APPROVED) {
                Object[] row = new Object[]{
                    course.getCourseId(),
                    course.getTitle(),
                    course.getDescription()
                };
                availableCoursesModel.addRow(row);
            }
        }

        JScrollPane scrollPane = new JScrollPane(availableCoursesTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom: Enroll button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(enrollButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMyCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Top: Enrolled courses table
        JLabel titleLabel = new JLabel("My Enrolled Courses");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Split pane for courses and lessons
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        // Left: Enrolled courses
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new JScrollPane(enrolledCoursesTable), BorderLayout.CENTER);
        JPanel leftButtonPanel = new JPanel(new FlowLayout());
        leftButtonPanel.add(unenrollButton);
        leftPanel.add(leftButtonPanel, BorderLayout.SOUTH);
        splitPane.setLeftComponent(leftPanel);

        // Right: Lessons and content
        JPanel rightPanel = new JPanel(new BorderLayout());

        // Course description
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.add(new JLabel("Course Description:"), BorderLayout.NORTH);
        descPanel.add(new JScrollPane(courseDescriptionArea), BorderLayout.CENTER);
        rightPanel.add(descPanel, BorderLayout.NORTH);

        // Lessons table
        JPanel lessonsPanel = new JPanel(new BorderLayout());
        lessonsPanel.add(new JLabel("Lessons:"), BorderLayout.NORTH);
        lessonsPanel.add(new JScrollPane(lessonsTable), BorderLayout.CENTER);
        rightPanel.add(lessonsPanel, BorderLayout.CENTER);

        // Lesson content and buttons
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(new JLabel("Lesson Content:"), BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(lessonContentArea), BorderLayout.CENTER);

        JPanel lessonButtonPanel = new JPanel(new FlowLayout());
        lessonButtonPanel.add(viewLessonButton);
        lessonButtonPanel.add(takeQuizButton);
        lessonButtonPanel.add(markCompleteButton);
        contentPanel.add(lessonButtonPanel, BorderLayout.SOUTH);

        rightPanel.add(contentPanel, BorderLayout.SOUTH);

        splitPane.setRightComponent(rightPanel);
        splitPane.setDividerLocation(400);

        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createProgressPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("My Progress Overview");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(progressTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(viewProgressButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCertificatesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("My Certificates");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(certificatesTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Data Management Methods">
    private void loadData() {
        // Reload student to get latest data
        currentStudent = studentManagement.findStudent(currentStudent.getUserID());
        if (currentStudent == null) return;

        loadAvailableCourses();
        loadEnrolledCourses();
        loadLessons();
        loadProgress();
        loadCertificates();
    }

    private void loadAvailableCourses() {
        availableCoursesModel.setRowCount(0);
        ArrayList<Course> allCourses = courseManagement.getAllCourses();

        for (Course course : allCourses) {
            // Only show approved courses that student is not enrolled in
            if (course.getStatus() == CourseStatus.APPROVED &&
                    !currentStudent.getEnrolledCourses().contains(course)) {
                availableCoursesModel.addRow(new Object[]{
                        course.getCourseId(),
                        course.getTitle(),
                        course.getDescription(),
                        course.getInstructorId(),
                        course.getLessons().size()
                });
            }
        }
    }

    private void loadEnrolledCourses() {
        enrolledCoursesModel.setRowCount(0);

        for (Course course : currentStudent.getEnrolledCourses()) {
            Progress progress = getProgressForCourse(course);
            double progressPercent = progress != null ? progress.getPercentage() : 0.0;

            enrolledCoursesModel.addRow(new Object[]{
                    course.getCourseId(),
                    course.getTitle(),
                    course.getDescription(),
                    String.format("%.1f%%", progressPercent),
                    course.getLessons().size()
            });
        }
    }

    private void loadLessons() {
        lessonsModel.setRowCount(0);

        if (selectedCourse != null) {
            Progress progress = getProgressForCourse(selectedCourse);
            ArrayList<Lesson> completedLessons = progress != null ? progress.getCompletedLessons() : new ArrayList<>();

            for (Lesson lesson : selectedCourse.getLessons()) {
                boolean isCompleted = completedLessons.contains(lesson);
                boolean canAccess = progress == null || progress.canAccessLesson(lesson);
                String status = isCompleted ? "Completed" : (canAccess ? "Available" : "Locked");
                String quizAvailable = lesson.getQuiz() != null ? "Yes" : "No";

                lessonsModel.addRow(new Object[]{
                        lesson.getLessonId(),
                        lesson.getTitle(),
                        status,
                        quizAvailable
                });
            }
        }
    }

    private void loadProgress() {
        progressModel.setRowCount(0);

        for (Course course : currentStudent.getEnrolledCourses()) {
            Progress progress = getProgressForCourse(course);
            if (progress != null) {
                progressModel.addRow(new Object[]{
                        course.getCourseId(),
                        course.getTitle(),
                        String.format("%.1f%%", progress.getPercentage()),
                        progress.getCompletedLessons().size(),
                        course.getLessons().size()
                });
            }
        }
    }

    private void loadCertificates() {
        certificatesModel.setRowCount(0);

        for (Certificate cert : currentStudent.getCertificates()) {
            certificatesModel.addRow(new Object[]{
                    cert.getCertificateId(),
                    cert.getCourseTitle(),
                    cert.getDateEarned()
            });
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

    private void updateSelectedAvailableCourse() {
        int selectedRow = availableCoursesTable.getSelectedRow();
        if (selectedRow != -1) {
            int courseId = (Integer) availableCoursesModel.getValueAt(selectedRow, 0);
            selectedCourse = courseManagement.findCourse(courseId);
        }
    }

    private void updateSelectedEnrolledCourse() {
        int selectedRow = enrolledCoursesTable.getSelectedRow();
        if (selectedRow != -1) {
            int courseId = (Integer) enrolledCoursesModel.getValueAt(selectedRow, 0);
            selectedCourse = courseManagement.findCourse(courseId);

            if (selectedCourse != null) {
                courseDescriptionArea.setText(selectedCourse.getDescription());
                loadLessons();
            }
        }
    }

    private void updateSelectedLesson() {
        int selectedRow = lessonsTable.getSelectedRow();
        if (selectedRow != -1 && selectedCourse != null) {
            int lessonId = (Integer) lessonsModel.getValueAt(selectedRow, 0);
            selectedLesson = selectedCourse.findLesson(lessonId);

            if (selectedLesson != null) {
                lessonContentArea.setText(selectedLesson.getContent());

                // Check if lesson is accessible
                Progress progress = getProgressForCourse(selectedCourse);
                boolean canAccess = progress == null || progress.canAccessLesson(selectedLesson);
                viewLessonButton.setEnabled(canAccess);
                takeQuizButton.setEnabled(canAccess && selectedLesson.getQuiz() != null);
                markCompleteButton.setEnabled(canAccess && selectedLesson.getQuiz() == null);
            }
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Event Handlers">
    private void handleEnroll() {
        int selectedRow = availableCoursesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to enroll in", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int courseId = (Integer) availableCoursesModel.getValueAt(selectedRow, 0);
        Course course = courseManagement.findCourse(courseId);

        if (course == null) {
            JOptionPane.showMessageDialog(this, "Course not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (currentStudent.getEnrolledCourses().contains(course)) {
            JOptionPane.showMessageDialog(this, "You are already enrolled in this course", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Enroll student
        studentManagement.enrollStudentInCourse(currentStudent.getUserID(), course);
        courseManagement.saveCourses();

        // Reload student data
        currentStudent = studentManagement.findStudent(currentStudent.getUserID());

        JOptionPane.showMessageDialog(this, "Successfully enrolled in course: " + course.getTitle(), "Success",
                JOptionPane.INFORMATION_MESSAGE);

        loadData();
        tabbedPane.setSelectedIndex(1); // Switch to My Courses tab
    }

    private void handleUnenroll() {
        int selectedRow = enrolledCoursesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to unenroll from", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int courseId = (Integer) enrolledCoursesModel.getValueAt(selectedRow, 0);
        Course course = courseManagement.findCourse(courseId);

        if (course == null) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to unenroll from: " + course.getTitle() + "?",
                "Confirm Unenroll", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            studentManagement.unenrollStudentFromCourse(currentStudent.getUserID(), course);
            courseManagement.saveCourses();

            // Reload student data
            currentStudent = studentManagement.findStudent(currentStudent.getUserID());

            selectedCourse = null;
            selectedLesson = null;
            courseDescriptionArea.setText("");
            lessonContentArea.setText("");

            JOptionPane.showMessageDialog(this, "Successfully unenrolled from course", "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            loadData();
        }
    }

    private void handleViewLesson() {
        if (selectedLesson == null || selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please select a lesson", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Progress progress = getProgressForCourse(selectedCourse);
        if (progress != null && !progress.canAccessLesson(selectedLesson)) {
            JOptionPane.showMessageDialog(this,
                    "You must complete the previous lesson first", "Access Denied",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lesson content is already displayed in the text area
        // This could open a separate window if needed
        JOptionPane.showMessageDialog(this,
                "Lesson content is displayed in the content area below", "Lesson View",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleTakeQuiz() {
        if (selectedLesson == null || selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please select a lesson with a quiz", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedLesson.getQuiz() == null) {
            JOptionPane.showMessageDialog(this, "This lesson does not have a quiz", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Progress progress = getProgressForCourse(selectedCourse);
        if (progress == null) {
            JOptionPane.showMessageDialog(this, "Progress not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!progress.canAccessLesson(selectedLesson)) {
            JOptionPane.showMessageDialog(this,
                    "You must complete the previous lesson first", "Access Denied",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Open quiz dashboard
        new QuizzesDashboard(selectedLesson, currentStudent, progress).setVisible(true);

        // Reload data after quiz (user might close quiz window)
        SwingUtilities.invokeLater(() -> {
            currentStudent = studentManagement.findStudent(currentStudent.getUserID());
            loadData();
        });
    }

    private void handleMarkComplete() {
        if (selectedLesson == null || selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please select a lesson", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedLesson.getQuiz() != null) {
            JOptionPane.showMessageDialog(this,
                    "This lesson has a quiz. Please complete the quiz to mark it as complete.", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Progress progress = getProgressForCourse(selectedCourse);
        if (progress == null) {
            JOptionPane.showMessageDialog(this, "Progress not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!progress.canAccessLesson(selectedLesson)) {
            JOptionPane.showMessageDialog(this,
                    "You must complete the previous lesson first", "Access Denied",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Mark lesson as complete
        progress.addCompletedLesson(selectedLesson);
        studentManagement.saveStudents();

        // Check if course is completed and generate certificate
        if (progress.courseCompletion()) {
            // Check if certificate already exists
            boolean hasCertificate = false;
            for (Certificate cert : currentStudent.getCertificates()) {
                if (cert.getCourseId() == selectedCourse.getCourseId()) {
                    hasCertificate = true;
                    break;
                }
            }

            if (!hasCertificate) {
                Certificate certificate = new Certificate(
                        currentStudent.getUserID(),
                        selectedCourse.getCourseId(),
                        selectedCourse.getTitle());
                currentStudent.addCertificate(certificate);
                studentManagement.saveStudents();

                JOptionPane.showMessageDialog(this,
                        "Congratulations! You have completed the course and earned a certificate!",
                        "Course Completed", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        // Reload student data
        currentStudent = studentManagement.findStudent(currentStudent.getUserID());

        JOptionPane.showMessageDialog(this, "Lesson marked as complete", "Success",
                JOptionPane.INFORMATION_MESSAGE);

        loadData();
    }

    private void handleViewDetailedProgress() {
        int selectedRow = progressTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to view progress", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int courseId = (Integer) progressModel.getValueAt(selectedRow, 0);
        Course course = courseManagement.findCourse(courseId);
        Progress progress = getProgressForCourse(course);

        if (progress == null) {
            JOptionPane.showMessageDialog(this, "Progress not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Course: ").append(course.getTitle()).append("\n");
        sb.append("Progress: ").append(String.format("%.1f%%", progress.getPercentage())).append("\n");
        sb.append("Completed Lessons: ").append(progress.getCompletedLessons().size()).append(" / ")
                .append(course.getLessons().size()).append("\n\n");

        sb.append("Completed Lessons:\n");
        for (Lesson lesson : progress.getCompletedLessons()) {
            sb.append("✓ ").append(lesson.getTitle());
            if (lesson.getQuiz() != null) {
                ArrayList<Integer> attempts = progress.getListOfAttempts(lesson.getLessonId());
                if (!attempts.isEmpty()) {
                    int highest = progress.getHighestScore(lesson);
                    double average = progress.getAverageScore(lesson);
                    sb.append(" (Quiz: Highest: ").append(highest).append("%, Average: ")
                            .append(String.format("%.1f", average)).append("%)");
                }
            }
            sb.append("\n");
        }

        sb.append("\nRemaining Lessons:\n");
        for (Lesson lesson : course.getLessons()) {
            if (!progress.getCompletedLessons().contains(lesson)) {
                sb.append("○ ").append(lesson.getTitle());
                if (lesson.getQuiz() != null) {
                    ArrayList<Integer> attempts = progress.getListOfAttempts(lesson.getLessonId());
                    if (!attempts.isEmpty()) {
                        int highest = progress.getHighestScore(lesson);
                        sb.append(" (Quiz attempts: ").append(attempts.size())
                                .append(", Highest: ").append(highest).append("%)");
                    } else {
                        sb.append(" (Quiz not attempted)");
                    }
                }
                sb.append("\n");
            }
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Detailed Progress", JOptionPane.INFORMATION_MESSAGE);
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
        SwingUtilities.invokeLater(() -> {
            // For testing purposes only
            JOptionPane.showMessageDialog(null,
                    "Please login through the LoginFrame to access Student Dashboard",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
        });
    }
    // </editor-fold>
}
// </editor-fold>

