package Frontend;

import CourseManagement.Course;
import CourseManagement.CourseManagementSystem;
import CourseManagement.Lesson;
import CourseManagement.Progress;
import Student.Student;
import Student.StudentManagement;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

// <editor-fold defaultstate="collapsed" desc="StudentDashboard Class">
/**
 * Student dashboard for course management, progress tracking, and quiz access
 */
public class StudentDashboard extends JFrame {
    
    // Variables
    private StudentManagement studentManagement;
    private CourseManagementSystem courseManagement;
    private Student currentStudent;
    private JTable enrolledCoursesTable, availableCoursesTable, lessonsTable;
    private DefaultTableModel enrolledModel, availableModel, lessonsModel;
    private JTextArea courseDescriptionArea, lessonContentArea;
    private JLabel progressLabel;
    private JButton enrollButton, unenrollButton, viewLessonsButton, completeLessonButton, refreshButton, logoutButton;
    private JButton takeQuizButton; // <-- new button
    private JTabbedPane tabbedPane;

    // Constructors
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

    // Initialization
    private void initializeComponents() {
        studentManagement = new StudentManagement();
        courseManagement = new CourseManagementSystem();
        
        if (currentStudent == null) {
            ArrayList<Student> students = studentManagement.getAllStudents();
            if (!students.isEmpty()) {
                currentStudent = students.get(0);
            }
        }
        
        enrolledModel = new DefaultTableModel(new Object[]{"Course ID", "Title", "Description", "Progress %"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        availableModel = new DefaultTableModel(new Object[]{"Course ID", "Title", "Description", "Instructor"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        lessonsModel = new DefaultTableModel(new Object[]{"Lesson ID", "Title", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        enrolledCoursesTable = new JTable(enrolledModel);
        availableCoursesTable = new JTable(availableModel);
        lessonsTable = new JTable(lessonsModel);
        
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
        
        takeQuizButton = new JButton("Take Quiz"); // <-- new button
    }

    // UI Setup
    private void setupUI() {
        setTitle("Student Dashboard");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("My Courses", createMyCoursesPanel());
        tabbedPane.addTab("Browse Courses", createBrowseCoursesPanel());
        tabbedPane.addTab("Course Details", createCourseDetailsPanel());
        
        add(tabbedPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(refreshButton);
        bottomPanel.add(logoutButton);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Action listeners
        enrollButton.addActionListener(e -> handleEnroll());
        unenrollButton.addActionListener(e -> handleUnenroll());
        viewLessonsButton.addActionListener(e -> handleViewLessons());
        completeLessonButton.addActionListener(e -> handleCompleteLesson());
        takeQuizButton.addActionListener(e -> handleTakeQuiz()); // <-- new listener
        refreshButton.addActionListener(e -> loadData());
        logoutButton.addActionListener(e -> handleLogout());
        
        enrolledCoursesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateCourseDescription();
        });
        
        availableCoursesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateAvailableCourseDescription();
        });
        
        lessonsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateLessonContent();
                enableQuizButton();
            }
        });
    }

    private JPanel createMyCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("My Enrolled Courses"), BorderLayout.NORTH);
        panel.add(new JScrollPane(enrolledCoursesTable), BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(viewLessonsButton);
        buttonPanel.add(unenrollButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createBrowseCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Available Courses"), BorderLayout.NORTH);
        panel.add(new JScrollPane(availableCoursesTable), BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(enrollButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createCourseDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Course Description:"), BorderLayout.NORTH);
        topPanel.add(new JScrollPane(courseDescriptionArea), BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JLabel("Lessons:"), BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(lessonsTable), BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(new JLabel("Lesson Content:"), BorderLayout.NORTH);
        bottomPanel.add(new JScrollPane(lessonContentArea), BorderLayout.CENTER);
        
        JPanel progressPanel = new JPanel(new FlowLayout());
        progressPanel.add(progressLabel);
        progressPanel.add(completeLessonButton);
        progressPanel.add(takeQuizButton); // <-- new button
        bottomPanel.add(progressPanel, BorderLayout.SOUTH);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    // Data Management
    private void loadData() {
        if (currentStudent == null) return;
        currentStudent = studentManagement.findStudent(currentStudent.getUserID());
        if (currentStudent == null) return;
        
        enrolledModel.setRowCount(0);
        for (Course course : currentStudent.getEnrolledCourses()) {
            double progress = getCourseProgress(course);
            enrolledModel.addRow(new Object[]{
                course.getCourseId(),
                course.getTitle(),
                course.getDescription(),
                String.format("%.1f%%", progress)
            });
        }
        
        availableModel.setRowCount(0);
        for (Course course : courseManagement.getAllCourses()) {
            if (!currentStudent.getEnrolledCourses().contains(course)) {
                availableModel.addRow(new Object[]{
                    course.getCourseId(),
                    course.getTitle(),
                    course.getDescription(),
                    course.getInstructorId()
                });
            }
        }
    }
    
    private double getCourseProgress(Course course) {
        for (Progress progress : currentStudent.getProgresses()) {
            if (progress.getCourse().equals(course)) return progress.getPercentage();
        }
        return 0.0;
    }

    // Event Handlers
    private void handleEnroll() { int selectedRow = availableCoursesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to enroll", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int courseId = (Integer) availableModel.getValueAt(selectedRow, 0);
        Course course = courseManagement.findCourse(courseId);
        
        if (course == null) {
            JOptionPane.showMessageDialog(this, "Course not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (currentStudent.getEnrolledCourses().contains(course)) {
            JOptionPane.showMessageDialog(this, "Already enrolled in this course", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        studentManagement.enrollStudentInCourse(currentStudent.getUserID(), course);
        JOptionPane.showMessageDialog(this, "Successfully enrolled in course: " + course.getTitle(), "Success", JOptionPane.INFORMATION_MESSAGE);
        loadData();}
    private void handleUnenroll() {  int selectedRow = enrolledCoursesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to unenroll", "Warning", JOptionPane.WARNING_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Successfully unenrolled from course", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadData();
            // Clear course details
            courseDescriptionArea.setText("");
            lessonsModel.setRowCount(0);
            lessonContentArea.setText("");
            progressLabel.setText("Progress: 0%");
        }}
    private void handleViewLessons() {   int selectedRow = enrolledCoursesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to view lessons", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int courseId = (Integer) enrolledModel.getValueAt(selectedRow, 0);
        Course course = courseManagement.findCourse(courseId);
        
        if (course == null) return;
        
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
            lessonsModel.addRow(new Object[]{
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
        return null;}
    private void handleCompleteLesson() {  int selectedRow = lessonsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a lesson to complete", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int courseRow = enrolledCoursesTable.getSelectedRow();
        if (courseRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course first", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int courseId = (Integer) enrolledModel.getValueAt(courseRow, 0);
        Course course = courseManagement.findCourse(courseId);
        if (course == null) return;
        
        int lessonId = (Integer) lessonsModel.getValueAt(selectedRow, 0);
        Lesson lesson = course.findLesson(lessonId);
        if (lesson == null) return;
        
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
        
        JOptionPane.showMessageDialog(this, "Lesson marked as complete!", "Success", JOptionPane.INFORMATION_MESSAGE);
        
        // Refresh lessons table
        handleViewLessons();}
    private void handleLogout() {  int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?",
            "Confirm Logout", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame();
        }}

    private void handleTakeQuiz() {
        int selectedRow = lessonsTable.getSelectedRow();
        if (selectedRow == -1) return;

        int courseRow = enrolledCoursesTable.getSelectedRow();
        if (courseRow == -1) return;

        int courseId = (Integer) enrolledModel.getValueAt(courseRow, 0);
        Course course = courseManagement.findCourse(courseId);
        if (course == null) return;

        int lessonId = (Integer) lessonsModel.getValueAt(selectedRow, 0);
        Lesson lesson = course.findLesson(lessonId);
        if (lesson == null) return;

        if (lesson.getQuiz() == null || lesson.getQuiz().getQuestions().isEmpty()) {
            JOptionPane.showMessageDialog(this, "This lesson has no quiz", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Progress progress = getProgressForCourse(course);
        if (progress == null) return;

        new QuizzesDashboard(lesson, currentStudent, progress).setVisible(true);
    }

    private void enableQuizButton() {
        int selectedRow = lessonsTable.getSelectedRow();
        if (selectedRow == -1) {
            takeQuizButton.setEnabled(false);
            return;
        }

        int courseRow = enrolledCoursesTable.getSelectedRow();
        if (courseRow == -1) {
            takeQuizButton.setEnabled(false);
            return;
        }

        int courseId = (Integer) enrolledModel.getValueAt(courseRow, 0);
        Course course = courseManagement.findCourse(courseId);
        if (course == null) {
            takeQuizButton.setEnabled(false);
            return;
        }

        int lessonId = (Integer) lessonsModel.getValueAt(selectedRow, 0);
        Lesson lesson = course.findLesson(lessonId);
        takeQuizButton.setEnabled(lesson != null && lesson.getQuiz() != null && !lesson.getQuiz().getQuestions().isEmpty());
    }

   /* private Progress getProgressForCourse(Course course) {
        for (Progress progress : currentStudent.getProgresses()) {
            if (progress.getCourse().equals(course)) return progress;
        }
        return null;
    }*/

    private void updateCourseDescription() {  int selectedRow = enrolledCoursesTable.getSelectedRow();
        if (selectedRow != -1) {
            int courseId = (Integer) enrolledModel.getValueAt(selectedRow, 0);
            Course course = courseManagement.findCourse(courseId);
            if (course != null) {
                courseDescriptionArea.setText(course.getDescription());
            }
        } }
    private void updateAvailableCourseDescription() {  int selectedRow = availableCoursesTable.getSelectedRow();
        if (selectedRow != -1) {
            int courseId = (Integer) availableModel.getValueAt(selectedRow, 0);
            Course course = courseManagement.findCourse(courseId);
            if (course != null) {
                courseDescriptionArea.setText(course.getDescription());
            }
        } }
    private void updateLessonContent() {  int selectedRow = lessonsTable.getSelectedRow();
        if (selectedRow != -1) {
            int lessonId = (Integer) lessonsModel.getValueAt(selectedRow, 0);
            
            // Find the course
            int courseRow = enrolledCoursesTable.getSelectedRow();
            if (courseRow == -1) return;
            
            int courseId = (Integer) enrolledModel.getValueAt(courseRow, 0);
            Course course = courseManagement.findCourse(courseId);
            if (course == null) return;
            
            Lesson lesson = course.findLesson(lessonId);
            if (lesson != null) {
                lessonContentArea.setText(lesson.getContent());
            }
        } }

    // Main
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentDashboard().setVisible(true));
    }
}
// </editor-fold>
