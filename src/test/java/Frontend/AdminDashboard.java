package Frontend;

import CourseManagement.Course;
import CourseManagement.CourseManagementSystem;
import CourseManagement.CourseStatus;
import CourseManagement.Lesson;
import CourseManagement.Progress;
import InstructorManagement.Instructor;
import InstructorManagement.InstructorManagement;
import Student.Student;
import Student.StudentManagement;
import Student.Certificate;
import Analytics.AnalyticsManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

// <editor-fold defaultstate="collapsed" desc="AdminDashboard Class">
/**
 * Admin Dashboard for managing courses, users, and viewing system analytics
 */
public class AdminDashboard extends JFrame {

    // <editor-fold defaultstate="collapsed" desc="Variables">
    private CourseManagementSystem courseManagement;
    private StudentManagement studentManagement;
    private InstructorManagement instructorManagement;
    private AnalyticsManager analyticsManager;

    // Tables
    private JTable coursesTable, studentsTable, instructorsTable, analyticsTable;
    private DefaultTableModel coursesModel, studentsModel, instructorsModel, analyticsModel;

    // Buttons
    private JButton approveButton, rejectButton, viewCourseDetailsButton;
    private JButton viewStudentDetailsButton, viewInstructorDetailsButton;
    private JButton refreshButton, logoutButton;
    private JButton viewAnalyticsButton, viewSystemStatsButton;

    // Tabbed pane
    private JTabbedPane tabbedPane;

    // Selected items
    private Course selectedCourse;
    private Student selectedStudent;
    private Instructor selectedInstructor;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructor">
    public AdminDashboard() {
        initializeComponents();
        setupUI();
        loadData();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Initialization Methods">
    private void initializeComponents() {
        courseManagement = new CourseManagementSystem();
        studentManagement = new StudentManagement();
        instructorManagement = new InstructorManagement();
        analyticsManager = new AnalyticsManager();

        // Initialize table models
        coursesModel = new DefaultTableModel(
                new Object[]{"Course ID", "Title", "Instructor ID", "Status", "Lessons", "Students"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentsModel = new DefaultTableModel(
                new Object[]{"Student ID", "Username", "Email", "Enrolled Courses", "Certificates"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        instructorsModel = new DefaultTableModel(
                new Object[]{"Instructor ID", "Username", "Email", "Created Courses"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        analyticsModel = new DefaultTableModel(
                new Object[]{"Metric", "Value"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Initialize tables
        coursesTable = new JTable(coursesModel);
        studentsTable = new JTable(studentsModel);
        instructorsTable = new JTable(instructorsModel);
        analyticsTable = new JTable(analyticsModel);

        // Initialize buttons
        approveButton = new JButton("Approve Course");
        rejectButton = new JButton("Reject Course");
        viewCourseDetailsButton = new JButton("View Course Details");
        viewStudentDetailsButton = new JButton("View Student Details");
        viewInstructorDetailsButton = new JButton("View Instructor Details");
        viewAnalyticsButton = new JButton("View Course Analytics");
        viewSystemStatsButton = new JButton("Refresh Statistics");
        refreshButton = new JButton("Refresh");
        logoutButton = new JButton("Logout");

        // Add action listeners
        approveButton.addActionListener(e -> handleApproveCourse());
        rejectButton.addActionListener(e -> handleRejectCourse());
        viewCourseDetailsButton.addActionListener(e -> handleViewCourseDetails());
        viewStudentDetailsButton.addActionListener(e -> handleViewStudentDetails());
        viewInstructorDetailsButton.addActionListener(e -> handleViewInstructorDetails());
        viewAnalyticsButton.addActionListener(e -> handleViewCourseAnalytics());
        viewSystemStatsButton.addActionListener(e -> loadSystemStatistics());
        refreshButton.addActionListener(e -> loadData());
        logoutButton.addActionListener(e -> handleLogout());

        // Table selection listeners
        coursesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateSelectedCourse();
        });

        studentsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateSelectedStudent();
        });

        instructorsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateSelectedInstructor();
        });
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="UI Setup Methods">
    private void setupUI() {
        setTitle("Admin Dashboard - System Management");
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tabbedPane = new JTabbedPane();

        // Tab 1: System Overview
        JPanel overviewPanel = createSystemOverviewPanel();
        tabbedPane.addTab("System Overview", overviewPanel);

        // Tab 2: Course Management
        JPanel coursePanel = createCourseManagementPanel();
        tabbedPane.addTab("Course Management", coursePanel);

        // Tab 3: Student Management
        JPanel studentPanel = createStudentManagementPanel();
        tabbedPane.addTab("Student Management", studentPanel);

        // Tab 4: Instructor Management
        JPanel instructorPanel = createInstructorManagementPanel();
        tabbedPane.addTab("Instructor Management", instructorPanel);

        // Tab 5: Analytics
        JPanel analyticsPanel = createAnalyticsPanel();
        tabbedPane.addTab("Analytics & Reports", analyticsPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(refreshButton);
        bottomPanel.add(logoutButton);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createSystemOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("System Overview");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Statistics panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create stat cards
        statsPanel.add(createStatCard("Total Courses", String.valueOf(courseManagement.getAllCourses().size())));
        statsPanel.add(createStatCard("Total Students", String.valueOf(studentManagement.getAllStudents().size())));
        statsPanel.add(createStatCard("Total Instructors", String.valueOf(instructorManagement.getAllInstructors().size())));
        
        int approvedCourses = 0;
        int pendingCourses = 0;
        for (Course c : courseManagement.getAllCourses()) {
            if (c.getStatus() == CourseStatus.APPROVED) approvedCourses++;
            else if (c.getStatus() == CourseStatus.PENDING) pendingCourses++;
        }
        
        statsPanel.add(createStatCard("Approved Courses", String.valueOf(approvedCourses)));
        statsPanel.add(createStatCard("Pending Courses", String.valueOf(pendingCourses)));
        
        int totalCertificates = 0;
        for (Student s : studentManagement.getAllStudents()) {
            totalCertificates += s.getCertificates().size();
        }
        statsPanel.add(createStatCard("Total Certificates", String.valueOf(totalCertificates)));

        panel.add(statsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(viewSystemStatsButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStatCard(String title, String value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        card.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        titleLabel.setForeground(Color.GRAY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(new Color(0, 100, 200));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createCourseManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Course Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> statusFilter = new JComboBox<>(new String[]{"All", "Pending", "Approved", "Rejected"});
        statusFilter.addActionListener(e -> {
            String filter = (String) statusFilter.getSelectedItem();
            loadCourses(filter);
        });
        filterPanel.add(new JLabel("Filter by Status:"));
        filterPanel.add(statusFilter);
        panel.add(filterPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(coursesTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(viewCourseDetailsButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStudentManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Student Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(studentsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(viewStudentDetailsButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createInstructorManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Instructor Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(instructorsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(viewInstructorDetailsButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createAnalyticsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("System Analytics & Reports");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(analyticsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(viewAnalyticsButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Data Management Methods">
    private void loadData() {
        loadCourses("All");
        loadStudents();
        loadInstructors();
        loadSystemStatistics();
    }

    private void loadCourses(String filter) {
        coursesModel.setRowCount(0);
        ArrayList<Course> allCourses = courseManagement.getAllCourses();

        for (Course course : allCourses) {
            boolean shouldShow = false;
            if (filter.equals("All")) {
                shouldShow = true;
            } else if (filter.equals("Pending") && course.getStatus() == CourseStatus.PENDING) {
                shouldShow = true;
            } else if (filter.equals("Approved") && course.getStatus() == CourseStatus.APPROVED) {
                shouldShow = true;
            } else if (filter.equals("Rejected") && course.getStatus() == CourseStatus.REJECTED) {
                shouldShow = true;
            }

            if (shouldShow) {
                coursesModel.addRow(new Object[]{
                        course.getCourseId(),
                        course.getTitle(),
                        course.getInstructorId(),
                        course.getStatus() != null ? course.getStatus().toString() : "PENDING",
                        course.getLessons().size(),
                        course.getStudents().size()
                });
            }
        }
    }

    private void loadStudents() {
        studentsModel.setRowCount(0);
        ArrayList<Student> allStudents = studentManagement.getAllStudents();

        for (Student student : allStudents) {
            studentsModel.addRow(new Object[]{
                    student.getUserID(),
                    student.getUsername(),
                    student.getEmail(),
                    student.getEnrolledCourses().size(),
                    student.getCertificates().size()
            });
        }
    }

    private void loadInstructors() {
        instructorsModel.setRowCount(0);
        ArrayList<Instructor> allInstructors = instructorManagement.getAllInstructors();

        for (Instructor instructor : allInstructors) {
            instructorsModel.addRow(new Object[]{
                    instructor.getUserId(),
                    instructor.getUserName(),
                    instructor.getEmail(),
                    instructor.getCourses().size()
            });
        }
    }

    private void loadSystemStatistics() {
        analyticsModel.setRowCount(0);

        ArrayList<Course> allCourses = courseManagement.getAllCourses();
        ArrayList<Student> allStudents = studentManagement.getAllStudents();
        ArrayList<Instructor> allInstructors = instructorManagement.getAllInstructors();

        int totalEnrollments = 0;
        int totalCompletedCourses = 0;
        int totalLessons = 0;
        int totalQuizzes = 0;

        for (Course course : allCourses) {
            totalEnrollments += course.getStudents().size();
            totalLessons += course.getLessons().size();
            for (Lesson lesson : course.getLessons()) {
                if (lesson.getQuiz() != null) {
                    totalQuizzes++;
                }
            }
        }

        for (Student student : allStudents) {
            for (Progress progress : student.getProgresses()) {
                if (progress.courseCompletion()) {
                    totalCompletedCourses++;
                }
            }
        }

        analyticsModel.addRow(new Object[]{"Total Courses", allCourses.size()});
        analyticsModel.addRow(new Object[]{"Total Students", allStudents.size()});
        analyticsModel.addRow(new Object[]{"Total Instructors", allInstructors.size()});
        analyticsModel.addRow(new Object[]{"Total Enrollments", totalEnrollments});
        analyticsModel.addRow(new Object[]{"Total Lessons", totalLessons});
        analyticsModel.addRow(new Object[]{"Total Quizzes", totalQuizzes});
        analyticsModel.addRow(new Object[]{"Completed Courses", totalCompletedCourses});
        
        int approvedCount = 0, pendingCount = 0, rejectedCount = 0;
        for (Course c : allCourses) {
            if (c.getStatus() == CourseStatus.APPROVED) approvedCount++;
            else if (c.getStatus() == CourseStatus.PENDING) pendingCount++;
            else if (c.getStatus() == CourseStatus.REJECTED) rejectedCount++;
        }
        
        analyticsModel.addRow(new Object[]{"Approved Courses", approvedCount});
        analyticsModel.addRow(new Object[]{"Pending Courses", pendingCount});
        analyticsModel.addRow(new Object[]{"Rejected Courses", rejectedCount});
        
        int totalCertificates = 0;
        for (Student s : allStudents) {
            totalCertificates += s.getCertificates().size();
        }
        analyticsModel.addRow(new Object[]{"Total Certificates Issued", totalCertificates});
    }

    private void updateSelectedCourse() {
        int selectedRow = coursesTable.getSelectedRow();
        if (selectedRow != -1) {
            int courseId = (Integer) coursesModel.getValueAt(selectedRow, 0);
            selectedCourse = courseManagement.findCourse(courseId);
            
            // Enable/disable buttons based on course status
            if (selectedCourse != null) {
                boolean isPending = selectedCourse.getStatus() == CourseStatus.PENDING;
                approveButton.setEnabled(isPending);
                rejectButton.setEnabled(isPending);
            }
        }
    }

    private void updateSelectedStudent() {
        int selectedRow = studentsTable.getSelectedRow();
        if (selectedRow != -1) {
            int studentId = (Integer) studentsModel.getValueAt(selectedRow, 0);
            selectedStudent = studentManagement.findStudent(studentId);
        }
    }

    private void updateSelectedInstructor() {
        int selectedRow = instructorsTable.getSelectedRow();
        if (selectedRow != -1) {
            int instructorId = (Integer) instructorsModel.getValueAt(selectedRow, 0);
            selectedInstructor = instructorManagement.findInstructor(instructorId);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Event Handlers">
    private void handleApproveCourse() {
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please select a course to approve", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedCourse.getStatus() != CourseStatus.PENDING) {
            JOptionPane.showMessageDialog(this, "Only pending courses can be approved", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to approve: " + selectedCourse.getTitle() + "?",
                "Confirm Approval", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            selectedCourse.setStatus(CourseStatus.APPROVED);
            courseManagement.saveCourses();
            JOptionPane.showMessageDialog(this, "Course approved successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            loadData();
        }
    }

    private void handleRejectCourse() {
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please select a course to reject", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedCourse.getStatus() != CourseStatus.PENDING) {
            JOptionPane.showMessageDialog(this, "Only pending courses can be rejected", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to reject: " + selectedCourse.getTitle() + "?",
                "Confirm Rejection", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            selectedCourse.setStatus(CourseStatus.REJECTED);
            courseManagement.saveCourses();
            JOptionPane.showMessageDialog(this, "Course rejected", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            loadData();
        }
    }

    private void handleViewCourseDetails() {
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please select a course to view details", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Course Details\n");
        sb.append("==============\n\n");
        sb.append("Course ID: ").append(selectedCourse.getCourseId()).append("\n");
        sb.append("Title: ").append(selectedCourse.getTitle()).append("\n");
        sb.append("Description: ").append(selectedCourse.getDescription()).append("\n");
        sb.append("Instructor ID: ").append(selectedCourse.getInstructorId()).append("\n");
        sb.append("Status: ").append(selectedCourse.getStatus()).append("\n");
        sb.append("Number of Lessons: ").append(selectedCourse.getLessons().size()).append("\n");
        sb.append("Number of Students: ").append(selectedCourse.getStudents().size()).append("\n\n");

        sb.append("Lessons:\n");
        for (Lesson lesson : selectedCourse.getLessons()) {
            sb.append("- ").append(lesson.getTitle());
            if (lesson.getQuiz() != null) {
                sb.append(" (Has Quiz)");
            }
            sb.append("\n");
        }

        sb.append("\nEnrolled Students:\n");
        for (Student student : selectedCourse.getStudents()) {
            Progress progress = getStudentProgress(student, selectedCourse);
            sb.append("- ").append(student.getUsername())
                    .append(" (Progress: ").append(String.format("%.1f%%", progress != null ? progress.getPercentage() : 0.0))
                    .append(")\n");
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 500));

        JOptionPane.showMessageDialog(this, scrollPane, "Course Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleViewStudentDetails() {
        if (selectedStudent == null) {
            JOptionPane.showMessageDialog(this, "Please select a student to view details", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Student Details\n");
        sb.append("==============\n\n");
        sb.append("Student ID: ").append(selectedStudent.getUserID()).append("\n");
        sb.append("Username: ").append(selectedStudent.getUsername()).append("\n");
        sb.append("Email: ").append(selectedStudent.getEmail()).append("\n");
        sb.append("Enrolled Courses: ").append(selectedStudent.getEnrolledCourses().size()).append("\n");
        sb.append("Certificates Earned: ").append(selectedStudent.getCertificates().size()).append("\n\n");

        sb.append("Enrolled Courses:\n");
        for (Course course : selectedStudent.getEnrolledCourses()) {
            Progress progress = getStudentProgress(selectedStudent, course);
            sb.append("- ").append(course.getTitle())
                    .append(" (Progress: ").append(String.format("%.1f%%", progress != null ? progress.getPercentage() : 0.0))
                    .append(")\n");
        }

        sb.append("\nCertificates:\n");
        for (Certificate cert : selectedStudent.getCertificates()) {
            sb.append("- ").append(cert.getCourseTitle())
                    .append(" (Earned: ").append(cert.getDateEarned()).append(")\n");
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 500));

        JOptionPane.showMessageDialog(this, scrollPane, "Student Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleViewInstructorDetails() {
        if (selectedInstructor == null) {
            JOptionPane.showMessageDialog(this, "Please select an instructor to view details", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Instructor Details\n");
        sb.append("==================\n\n");
        sb.append("Instructor ID: ").append(selectedInstructor.getUserId()).append("\n");
        sb.append("Username: ").append(selectedInstructor.getUserName()).append("\n");
        sb.append("Email: ").append(selectedInstructor.getEmail()).append("\n");
        sb.append("Created Courses: ").append(selectedInstructor.getCourses().size()).append("\n\n");

        sb.append("Created Courses:\n");
        for (Course course : selectedInstructor.getCourses()) {
            sb.append("- ").append(course.getTitle())
                    .append(" (Status: ").append(course.getStatus())
                    .append(", Students: ").append(course.getStudents().size())
                    .append(", Lessons: ").append(course.getLessons().size())
                    .append(")\n");
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Instructor Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleViewCourseAnalytics() {
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please select a course to view analytics", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Course Analytics: ").append(selectedCourse.getTitle()).append("\n");
        sb.append("=====================================\n\n");

        // Completion percentages
        Map<String, Double> completionPercentages = analyticsManager.calculateCompletionPercentages(selectedCourse);
        sb.append("Student Completion Percentages:\n");
        for (Map.Entry<String, Double> entry : completionPercentages.entrySet()) {
            sb.append("- ").append(entry.getKey())
                    .append(": ").append(String.format("%.1f%%", entry.getValue())).append("\n");
        }

        // Quiz averages
        Map<String, Double> quizAverages = analyticsManager.calculateQuizAverages(selectedCourse);
        sb.append("\nQuiz Average Scores:\n");
        for (Map.Entry<String, Double> entry : quizAverages.entrySet()) {
            if (entry.getValue() > 0) {
                sb.append("- ").append(entry.getKey())
                        .append(": ").append(String.format("%.1f%%", entry.getValue())).append("\n");
            }
        }

        sb.append("\nCourse Statistics:\n");
        sb.append("- Total Students: ").append(selectedCourse.getStudents().size()).append("\n");
        sb.append("- Total Lessons: ").append(selectedCourse.getLessons().size()).append("\n");
        
        int completedCount = 0;
        for (Student student : selectedCourse.getStudents()) {
            Progress progress = getStudentProgress(student, selectedCourse);
            if (progress != null && progress.courseCompletion()) {
                completedCount++;
            }
        }
        sb.append("- Students Completed: ").append(completedCount).append("\n");
        sb.append("- Completion Rate: ").append(selectedCourse.getStudents().isEmpty() ? 0.0 :
                String.format("%.1f%%", (double) completedCount * 100 / selectedCourse.getStudents().size())).append("\n");

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 500));

        JOptionPane.showMessageDialog(this, scrollPane, "Course Analytics", JOptionPane.INFORMATION_MESSAGE);
    }

    private Progress getStudentProgress(Student student, Course course) {
        for (Progress progress : student.getProgresses()) {
            if (progress.getCourse().equals(course)) {
                return progress;
            }
        }
        return null;
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
        SwingUtilities.invokeLater(() -> new AdminDashboard().setVisible(true));
    }
    // </editor-fold>
}
// </editor-fold>
