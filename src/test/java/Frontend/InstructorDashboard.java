package Frontend;

import CourseManagement.Course;
import CourseManagement.CourseManagementSystem;
import CourseManagement.Lesson;
import CourseManagement.Progress;
import InstructorManagement.Instructor;
import InstructorManagement.InstructorManagement;
import Student.Student;
import Student.StudentManagement;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
// <editor-fold defaultstate="collapsed" desc="InstructorDashboard Class">
/**
 * Instructor dashboard for course and lesson management
 */
public class InstructorDashboard extends JFrame {
    
    // <editor-fold defaultstate="collapsed" desc="Variables">
    private InstructorManagement instructorManagement;
    private CourseManagementSystem courseManagement;
    private StudentManagement studentManagement;
    private Instructor currentInstructor;
    private JTable coursesTable, lessonsTable, studentsTable;
    private DefaultTableModel coursesModel, lessonsModel, studentsModel;
    private JTextField courseTitleField, courseDescriptionField, lessonTitleField, lessonContentField;
    private JTextArea courseDescriptionArea, lessonContentArea;
    private JButton createCourseButton, deleteCourseButton, addLessonButton, deleteLessonButton;
    private JButton viewStudentsButton, viewStudentProgressButton, refreshButton, logoutButton;
    private JTabbedPane tabbedPane;
    private Course selectedCourse;
    private JButton insightsButton;
    
    private int nextCourseId = 1;
    private int nextLessonId = 1;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Constructors">
    public InstructorDashboard() {
        initializeComponents();
        setupUI();
        loadData();
    }
    
    public InstructorDashboard(Instructor instructor) {
        this.currentInstructor = instructor;
        initializeComponents();
        setupUI();
        loadData();
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Initialization Methods">
    private void initializeComponents() {
        instructorManagement = new InstructorManagement();
        courseManagement = new CourseManagementSystem();
        studentManagement = new StudentManagement();
        
        // Initialize instructor (for demo)
        if (currentInstructor == null) {
            ArrayList<Instructor> instructors = instructorManagement.getAllInstructors();
            if (!instructors.isEmpty()) {
                currentInstructor = instructors.get(0);
            }
        }
        
        // Calculate next IDs
        calculateNextIds();
        
        // Initialize tables
        coursesModel = new DefaultTableModel(new Object[]{"Course ID", "Title", "Description", "Lessons", "Students"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        lessonsModel = new DefaultTableModel(new Object[]{"Lesson ID", "Title"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        studentsModel = new DefaultTableModel(new Object[]{"Student ID", "Username", "Email", "Progress %"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        coursesTable = new JTable(coursesModel);
        lessonsTable = new JTable(lessonsModel);
        studentsTable = new JTable(studentsModel);
        
        courseTitleField = new JTextField(20);
        courseDescriptionField = new JTextField(20);
        lessonTitleField = new JTextField(20);
        lessonContentField = new JTextField(20);
        
        courseDescriptionArea = new JTextArea(5, 30);
        courseDescriptionArea.setEditable(false);
        courseDescriptionArea.setWrapStyleWord(true);
        courseDescriptionArea.setLineWrap(true);
        
        lessonContentArea = new JTextArea(10, 40);
        lessonContentArea.setEditable(false);
        lessonContentArea.setWrapStyleWord(true);
        lessonContentArea.setLineWrap(true);
        
        createCourseButton = new JButton("Create Course");
        deleteCourseButton = new JButton("Delete Course");
        addLessonButton = new JButton("Add Lesson");
        deleteLessonButton = new JButton("Delete Lesson");
        viewStudentsButton = new JButton("View Students");
        viewStudentProgressButton = new JButton("View Student Progress");
        refreshButton = new JButton("Refresh");
        logoutButton = new JButton("Logout");
    }
    public class ChartFrame extends JFrame {

    public ChartFrame(String title, JPanel chartPanel) {
        setTitle(title);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        add(chartPanel, BorderLayout.CENTER);
    }
}
    public class InsightsPanel extends JPanel {

    public InsightsPanel(Map<String, Double> completionPercentages,
                         Map<String, Double> quizAverages) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createSectionLabel("Course Completion Percentages"));
        add(createListPanel(completionPercentages));

        add(Box.createRigidArea(new Dimension(0,20)));

        add(createSectionLabel("Quiz Averages Per Lesson"));
        add(createListPanel(quizAverages));
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JPanel createListPanel(Map<String, Double> map) {
        JPanel panel = new JPanel(new GridLayout(0,1,5,5));
        for (var entry : map.entrySet()) {
            panel.add(new JLabel(entry.getKey() + " → " + String.format("%.1f%%", entry.getValue())));
        }
        return panel;
    }
}
    private void calculateNextIds() {
        ArrayList<Course> allCourses = courseManagement.getAllCourses();
        for (Course c : allCourses) {
            if (c.getCourseId() >= nextCourseId) {
                nextCourseId = c.getCourseId() + 1;
            }
            for (Lesson l : c.getLessons()) {
                if (l.getLessonId() >= nextLessonId) {
                    nextLessonId = l.getLessonId() + 1;
                }
            }
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="UI Setup Methods">
    private void setupUI() {
        setTitle("Instructor Dashboard");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        tabbedPane = new JTabbedPane();
        JButton insightsButton = new JButton("View Insights");
insightsButton.addActionListener(e -> openInsights());

JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
bottomPanel.add(insightsButton); // <-- Add here
bottomPanel.add(refreshButton);
bottomPanel.add(logoutButton);
add(bottomPanel, BorderLayout.SOUTH);
        // Tab 1: My Courses
        JPanel myCoursesPanel = createMyCoursesPanel();
        tabbedPane.addTab("My Courses", myCoursesPanel);
        
        // Tab 2: Course Management
        JPanel courseManagementPanel = createCourseManagementPanel();
        tabbedPane.addTab("Course Management", courseManagementPanel);
        
        // Tab 3: Lesson Management
        JPanel lessonManagementPanel = createLessonManagementPanel();
        tabbedPane.addTab("Lesson Management", lessonManagementPanel);
        
        // Tab 4: Students & Progress
        JPanel studentsPanel = createStudentsPanel();
        tabbedPane.addTab("Students & Progress", studentsPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Bottom panel
     //   JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(refreshButton);
        bottomPanel.add(logoutButton);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        createCourseButton.addActionListener(e -> handleCreateCourse());
        deleteCourseButton.addActionListener(e -> handleDeleteCourse());
        addLessonButton.addActionListener(e -> handleAddLesson());
        deleteLessonButton.addActionListener(e -> handleDeleteLesson());
        viewStudentsButton.addActionListener(e -> handleViewStudents());
        viewStudentProgressButton.addActionListener(e -> handleViewStudentProgress());
        refreshButton.addActionListener(e -> loadData());
        logoutButton.addActionListener(e -> handleLogout());
        
        coursesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateSelectedCourse();
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
        
        JLabel titleLabel = new JLabel("My Created Courses");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(coursesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(viewStudentsButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createCourseManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Top: Course List
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("My Courses:"), BorderLayout.NORTH);
        topPanel.add(new JScrollPane(coursesTable), BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.CENTER);
        
        // Bottom: Create/Delete Course
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        bottomPanel.add(new JLabel("Course Title:"), gbc);
        gbc.gridx = 1;
        bottomPanel.add(courseTitleField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        bottomPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        bottomPanel.add(courseDescriptionField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(createCourseButton);
        buttonPanel.add(deleteCourseButton);
        bottomPanel.add(buttonPanel, gbc);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createLessonManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Top: Course Description
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Selected Course Description:"), BorderLayout.NORTH);
        topPanel.add(new JScrollPane(courseDescriptionArea), BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Center: Lessons List
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JLabel("Lessons:"), BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(lessonsTable), BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        // Bottom: Add/Delete Lesson and Lesson Content
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        // Lesson input fields
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Lesson Title:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(lessonTitleField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Lesson Content:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(lessonContentField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addLessonButton);
        buttonPanel.add(deleteLessonButton);
        inputPanel.add(buttonPanel, gbc);
        
        bottomPanel.add(inputPanel, BorderLayout.NORTH);
        
        // Lesson content display
        bottomPanel.add(new JLabel("Lesson Content:"), BorderLayout.CENTER);
        bottomPanel.add(new JScrollPane(lessonContentArea), BorderLayout.CENTER);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Enrolled Students");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(studentsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(viewStudentProgressButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Data Management Methods">
    private void loadData() {
        if (currentInstructor == null) {
            JOptionPane.showMessageDialog(this, "No instructor logged in", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Reload instructor data
        currentInstructor = instructorManagement.findInstructor(currentInstructor.getUserId());
        if (currentInstructor == null) return;
        
        // Load instructor's courses
        coursesModel.setRowCount(0);
        ArrayList<Course> instructorCourses = courseManagement.getCoursesByInstructor(String.valueOf(currentInstructor.getUserId()));
        
        for (Course course : instructorCourses) {
            coursesModel.addRow(new Object[]{
                course.getCourseId(),
                course.getTitle(),
                course.getDescription(),
                course.getLessons().size(),
                course.getStudents().size()
            });
        }
        ArrayList<Student> instructorStudents = currentInstructor.viewAllEnrolledInstructorStudents();
        for (Student student : instructorStudents) {
            coursesModel.addRow(new Object[]{
                student.getUserID(),
                student.getUsername(),
                student.getEmail(),
                student.getProgresses(),
            });
        }
        
        // Clear other tables if no course selected
        if (selectedCourse == null) {
            lessonsModel.setRowCount(0);
            studentsModel.setRowCount(0);
        }
    }
    
    private void updateSelectedCourse() {
        int selectedRow = coursesTable.getSelectedRow();
        if (selectedRow != -1) {
            int courseId = (Integer) coursesModel.getValueAt(selectedRow, 0);
            selectedCourse = courseManagement.findCourse(courseId);
            
            if (selectedCourse != null) {
                courseDescriptionArea.setText(selectedCourse.getDescription());
                
                // Load lessons
                lessonsModel.setRowCount(0);
                for (Lesson lesson : selectedCourse.getLessons()) {
                    lessonsModel.addRow(new Object[]{
                        lesson.getLessonId(),
                        lesson.getTitle()
                    });
                }
            }
        }
    }
    
    private void updateLessonContent() {
        int selectedRow = lessonsTable.getSelectedRow();
        if (selectedRow != -1 && selectedCourse != null) {
            int lessonId = (Integer) lessonsModel.getValueAt(selectedRow, 0);
            Lesson lesson = selectedCourse.findLesson(lessonId);
            if (lesson != null) {
                lessonContentArea.setText(lesson.getContent());
            }
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Event Handlers">
    private void handleCreateCourse() {
        String title = courseTitleField.getText().trim();
        String description = courseDescriptionField.getText().trim();
        
        if (title.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Course newCourse = new Course(nextCourseId++, title, description, String.valueOf(currentInstructor.getUserId()));
        courseManagement.addCourse(newCourse);
        currentInstructor.addCourse(newCourse);
        instructorManagement.saveInstructors();
        
        JOptionPane.showMessageDialog(this, "Course created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        
        courseTitleField.setText("");
        courseDescriptionField.setText("");
        loadData();
    }
    
    private void handleDeleteCourse() {
        int selectedRow = coursesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to delete", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int courseId = (Integer) coursesModel.getValueAt(selectedRow, 0);
        Course course = courseManagement.findCourse(courseId);
        
        if (course == null) return;
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete course: " + course.getTitle() + "?\nThis will remove all lessons and unenroll all students.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            courseManagement.removeCourse(courseId);
            currentInstructor.removecreatedCourses(course);
            instructorManagement.saveInstructors();
            
            JOptionPane.showMessageDialog(this, "Course deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            selectedCourse = null;
            loadData();
        }
    }
    
    private void handleAddLesson() {
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please select a course first", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String title = lessonTitleField.getText().trim();
        String content = lessonContentField.getText().trim();
        
        if (title.isEmpty() || content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Lesson newLesson = new Lesson(nextLessonId++, title, content);
        courseManagement.addLessonToCourse(selectedCourse.getCourseId(), newLesson);
        
        JOptionPane.showMessageDialog(this, "Lesson added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        
        lessonTitleField.setText("");
        lessonContentField.setText("");
        loadData();
        updateSelectedCourse();
    }
    
    private void handleDeleteLesson() {
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please select a course first", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int selectedRow = lessonsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a lesson to delete", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int lessonId = (Integer) lessonsModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this lesson?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            courseManagement.removeLessonFromCourse(selectedCourse.getCourseId(), lessonId);
            JOptionPane.showMessageDialog(this, "Lesson deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadData();
            updateSelectedCourse();
        }
    }
    
    private void handleViewStudents() {
        int selectedRow = coursesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to view students", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int courseId = (Integer) coursesModel.getValueAt(selectedRow, 0);
        Course course = courseManagement.findCourse(courseId);
        
        if (course == null) return;
        
        // Switch to students tab
        tabbedPane.setSelectedIndex(3);
        
        // Load students
        studentsModel.setRowCount(0);
        for (Student student : course.getStudents()) {
            double progress = getStudentProgress(student, course);
            studentsModel.addRow(new Object[]{
                student.getUserID(),
                student.getUsername(),
                student.getEmail(),
                String.format("%.1f%%", progress)
            });
        }
    }
    
    private double getStudentProgress(Student student, Course course) {
        for (Progress progress : student.getProgresses()) {
            if (progress.getCourse().equals(course)) {
                return progress.getPercentage();
            }
        }
        return 0.0;
    }
    
    private void handleViewStudentProgress() {
        int selectedRow = studentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to view progress", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int studentId = (Integer) studentsModel.getValueAt(selectedRow, 0);
        Student student = studentManagement.findStudent(studentId);
        
        if (student == null) {
            JOptionPane.showMessageDialog(this, "Student not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int courseRow = coursesTable.getSelectedRow();
        if (courseRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course first", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int courseId = (Integer) coursesModel.getValueAt(courseRow, 0);
        Course course = courseManagement.findCourse(courseId);
        
        if (course == null) return;
        
        Progress progress = getStudentProgressObject(student, course);
        if (progress == null) {
            JOptionPane.showMessageDialog(this, "No progress found for this student", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Student: ").append(student.getUsername()).append("\n");
        sb.append("Course: ").append(course.getTitle()).append("\n");
        sb.append("Progress: ").append(String.format("%.1f%%", progress.getPercentage())).append("\n\n");
        sb.append("Completed Lessons: ").append(progress.getCompletedLessons().size()).append(" / ").append(course.getLessons().size()).append("\n\n");
        sb.append("Completed Lessons:\n");
        for (Lesson lesson : progress.getCompletedLessons()) {
            sb.append("- ").append(lesson.getTitle()).append("\n");
        }
        
        JOptionPane.showMessageDialog(this, sb.toString(), "Student Progress", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private Progress getStudentProgressObject(Student student, Course course) {
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
   private void openInsights() {
    if (selectedCourse == null) {
        JOptionPane.showMessageDialog(this, "Please select a course first", "Warning", JOptionPane.WARNING_MESSAGE);
        return;
    }

    Analytics.AnalyticsManager analytics = new Analytics.AnalyticsManager();

    // Get student completion percentages
    var completion = analytics.calculateCompletionPercentages(selectedCourse);

    // Get quiz averages per lesson
    Map<String, Double> quizAverages = analytics.calculateQuizAverages(selectedCourse);

    // Pass data to the InsightsPanel
    InsightsPanel panel = new InsightsPanel(completion, quizAverages);

    // Open chart frame
    ChartFrame frame = new ChartFrame("Course Insights", panel);
    frame.setVisible(true);
}



    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Main Method">
    /**
     * @param args the command line arguments
     */
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> new InstructorDashboard().setVisible(true));
//    }
    // </editor-fold>
}
// </editor-fold>
