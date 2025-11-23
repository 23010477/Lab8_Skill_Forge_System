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

public class InstructorDashboard extends JFrame {

    private InstructorManagement instructorManagement;
    private CourseManagementSystem courseManagement;
    private StudentManagement studentManagement;
    private Instructor currentInstructor;

    private JTable coursesTable, lessonsTable, studentsTable;
    private DefaultTableModel coursesModel, lessonsModel, studentsModel;

    private JTextField courseTitleField, courseDescriptionField, lessonTitleField, lessonContentField;
    private JTextArea courseDescriptionArea, lessonContentArea;

    private JButton createCourseButton, deleteCourseButton, addLessonButton, deleteLessonButton;
    private JButton viewStudentsButton, viewStudentProgressButton, refreshButton, logoutButton, insightsButton;

    private JTabbedPane tabbedPane;
    private Course selectedCourse;

    private int nextCourseId = 1;
    private int nextLessonId = 1;

    public InstructorDashboard(Instructor instructor) {
        this.currentInstructor = instructor;
        initializeComponents();
        setupUI();
        loadCourses();
    }

    private void initializeComponents() {
        instructorManagement = new InstructorManagement();
        courseManagement = new CourseManagementSystem();
        studentManagement = new StudentManagement();

        if (currentInstructor == null) {
            ArrayList<Instructor> instructors = instructorManagement.getAllInstructors();
            if (!instructors.isEmpty()) currentInstructor = instructors.get(0);
        }

        calculateNextIds();

        coursesModel = new DefaultTableModel(new Object[]{"Course ID", "Title", "Description", "Lessons", "Students"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        lessonsModel = new DefaultTableModel(new Object[]{"Lesson ID", "Title"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        studentsModel = new DefaultTableModel(new Object[]{"Student ID", "Username", "Email", "Progress %"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
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
        insightsButton = new JButton("View Insights");
    }

    private void calculateNextIds() {
        ArrayList<Course> allCourses = courseManagement.getAllCourses();
        for (Course c : allCourses) {
            if (c.getCourseId() >= nextCourseId) nextCourseId = c.getCourseId() + 1;
            for (Lesson l : c.getLessons()) {
                if (l.getLessonId() >= nextLessonId) nextLessonId = l.getLessonId() + 1;
            }
        }
    }

    private void setupUI() {
        setTitle("Instructor Dashboard");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tabbedPane = new JTabbedPane();

        tabbedPane.addTab("My Courses", createMyCoursesPanel());
        tabbedPane.addTab("Course Management", createCourseManagementPanel());
        tabbedPane.addTab("Lesson Management", createLessonManagementPanel());
        tabbedPane.addTab("Students & Progress", createStudentsPanel());

        add(tabbedPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(insightsButton);
        bottomPanel.add(refreshButton);
        bottomPanel.add(logoutButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Action listeners
        createCourseButton.addActionListener(e -> handleCreateCourse());
        deleteCourseButton.addActionListener(e -> handleDeleteCourse());
        addLessonButton.addActionListener(e -> handleAddLesson());
        deleteLessonButton.addActionListener(e -> handleDeleteLesson());
        viewStudentsButton.addActionListener(e -> handleViewStudents());
        viewStudentProgressButton.addActionListener(e -> handleViewStudentProgress());
        refreshButton.addActionListener(e -> loadCourses());
        logoutButton.addActionListener(e -> handleLogout());
        insightsButton.addActionListener(e -> openInsights());

        coursesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateSelectedCourse();
        });

        lessonsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateLessonContent();
        });
    }

    // ------------------- Panels -------------------
    private JPanel createMyCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("My Created Courses");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);

        panel.add(new JScrollPane(coursesTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(viewStudentsButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCourseManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        panel.add(new JScrollPane(coursesTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx=0; gbc.gridy=0;
        bottomPanel.add(new JLabel("Course Title:"), gbc);
        gbc.gridx=1;
        bottomPanel.add(courseTitleField, gbc);

        gbc.gridx=0; gbc.gridy=1;
        bottomPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx=1;
        bottomPanel.add(courseDescriptionField, gbc);

        gbc.gridx=0; gbc.gridy=2; gbc.gridwidth=2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(createCourseButton);
        buttonPanel.add(deleteCourseButton);
        bottomPanel.add(buttonPanel, gbc);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createLessonManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Course description
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Selected Course Description:"), BorderLayout.NORTH);
        topPanel.add(new JScrollPane(courseDescriptionArea), BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);

        // Lessons list
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JLabel("Lessons:"), BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(lessonsTable), BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.CENTER);

        // Bottom: add/delete lesson + take quiz + content
        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx=0; gbc.gridy=0;
        inputPanel.add(new JLabel("Lesson Title:"), gbc);
        gbc.gridx=1;
        inputPanel.add(lessonTitleField, gbc);

        gbc.gridx=0; gbc.gridy=1;
        inputPanel.add(new JLabel("Lesson Content:"), gbc);
        gbc.gridx=1;
        inputPanel.add(lessonContentField, gbc);

        gbc.gridx=0; gbc.gridy=2; gbc.gridwidth=2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addLessonButton);
        buttonPanel.add(deleteLessonButton);

        JButton takeQuizButton = new JButton("Take Quiz");
        takeQuizButton.addActionListener(e -> handleTakeQuiz());
        buttonPanel.add(takeQuizButton);

        inputPanel.add(buttonPanel, gbc);

        bottomPanel.add(inputPanel, BorderLayout.NORTH);

        JPanel lessonContentPanel = new JPanel(new BorderLayout());
        lessonContentPanel.add(new JLabel("Lesson Content:"), BorderLayout.NORTH);
        lessonContentPanel.add(new JScrollPane(lessonContentArea), BorderLayout.CENTER);

        bottomPanel.add(lessonContentPanel, BorderLayout.CENTER);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Enrolled Students");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);

        panel.add(new JScrollPane(studentsTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(viewStudentProgressButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ------------------- Data & Event Methods -------------------
    private void loadCourses() {
        if (currentInstructor == null) return;

        currentInstructor = instructorManagement.findInstructor(currentInstructor.getUserId());
        if (currentInstructor == null) return;

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

        lessonsModel.setRowCount(0);
        studentsModel.setRowCount(0);
    }

    private void updateSelectedCourse() {
        int selectedRow = coursesTable.getSelectedRow();
        if (selectedRow != -1) {
            int courseId = (Integer) coursesModel.getValueAt(selectedRow, 0);
            selectedCourse = courseManagement.findCourse(courseId);

            if (selectedCourse != null) {
                courseDescriptionArea.setText(selectedCourse.getDescription());

                lessonsModel.setRowCount(0);
                for (Lesson lesson : selectedCourse.getLessons()) {
                    lessonsModel.addRow(new Object[]{lesson.getLessonId(), lesson.getTitle()});
                }

                loadStudentsForCourse(selectedCourse);
            }
        }
    }

    private void loadStudentsForCourse(Course course) {
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
            if (progress.getCourse().equals(course)) return progress.getPercentage();
        }
        return 0.0;
    }

    private void updateLessonContent() {
        int selectedRow = lessonsTable.getSelectedRow();
        if (selectedRow != -1 && selectedCourse != null) {
            int lessonId = (Integer) lessonsModel.getValueAt(selectedRow, 0);
            Lesson lesson = selectedCourse.findLesson(lessonId);
            if (lesson != null) lessonContentArea.setText(lesson.getContent());
        }
    }

    private void handleTakeQuiz() {
        int lessonRow = lessonsTable.getSelectedRow();
        int studentRow = studentsTable.getSelectedRow();
        if (lessonRow == -1 || studentRow == -1 || selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Select a lesson and student first", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int lessonId = (Integer) lessonsModel.getValueAt(lessonRow, 0);
        Lesson lesson = selectedCourse.findLesson(lessonId);

        if (lesson.getQuiz() == null || lesson.getQuiz().getQuestions().isEmpty()) {
            JOptionPane.showMessageDialog(this, "This lesson has no quiz.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int studentId = (Integer) studentsModel.getValueAt(studentRow, 0);
        Student student = studentManagement.findStudent(studentId);
        if (student == null) return;

        Progress progress = getStudentProgressObject(student, selectedCourse);
        if (progress == null) {
            progress = new Progress(student, selectedCourse);
            student.getProgresses().add(progress);
        }

        new QuizzesDashboard(lesson, student, progress);
    }

    private Progress getStudentProgressObject(Student student, Course course) {
        for (Progress progress : student.getProgresses()) {
            if (progress.getCourse().equals(course)) return progress;
        }
        return null;
    }

    // ------------------- CRUD Methods -------------------
    private void handleCreateCourse() {
        String title = courseTitleField.getText().trim();
        String description = courseDescriptionField.getText().trim();
        if (title.isEmpty() || description.isEmpty()) return;

        Course newCourse = new Course(nextCourseId++, title, description, String.valueOf(currentInstructor.getUserId()));
        courseManagement.addCourse(newCourse);
        currentInstructor.addCourse(newCourse);
        instructorManagement.saveInstructors();

        courseTitleField.setText("");
        courseDescriptionField.setText("");
        loadCourses();
    }

    private void handleDeleteCourse() {
        int row = coursesTable.getSelectedRow();
        if (row == -1) return;

        int courseId = (Integer) coursesModel.getValueAt(row, 0);
        Course course = courseManagement.findCourse(courseId);
        if (course == null) return;

        int confirm = JOptionPane.showConfirmDialog(this, "Delete course: " + course.getTitle() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            courseManagement.removeCourse(courseId);
            currentInstructor.removecreatedCourses(course);
            instructorManagement.saveInstructors();
            selectedCourse = null;
            loadCourses();
        }
    }

    private void handleAddLesson() {
        if (selectedCourse == null) return;

        String title = lessonTitleField.getText().trim();
        String content = lessonContentField.getText().trim();
        if (title.isEmpty() || content.isEmpty()) return;

        Lesson newLesson = new Lesson(nextLessonId++, title, content);
        courseManagement.addLessonToCourse(selectedCourse.getCourseId(), newLesson);

        lessonTitleField.setText("");
        lessonContentField.setText("");

        // Refresh only lessons table
        updateSelectedCourse();
    }

    private void handleDeleteLesson() {
        if (selectedCourse == null) return;
        int row = lessonsTable.getSelectedRow();
        if (row == -1) return;

        int lessonId = (Integer) lessonsModel.getValueAt(row, 0);
        courseManagement.removeLessonFromCourse(selectedCourse.getCourseId(), lessonId);

        updateSelectedCourse();
    }

    private void handleViewStudents() {
        if (selectedCourse != null) {
            tabbedPane.setSelectedIndex(3);
            loadStudentsForCourse(selectedCourse);
        }
    }

    private void handleViewStudentProgress() {
        int row = studentsTable.getSelectedRow();
        if (row == -1 || selectedCourse == null) return;

        int studentId = (Integer) studentsModel.getValueAt(row, 0);
        Student student = studentManagement.findStudent(studentId);
        if (student == null) return;

        Progress progress = getStudentProgressObject(student, selectedCourse);
        if (progress == null) return;

        JOptionPane.showMessageDialog(this,
                student.getUsername() + " → Progress: " + String.format("%.1f%%", progress.getPercentage()));
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Logout?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) dispose();
    }

    private void openInsights() {
        if (selectedCourse == null) return;
        Analytics.AnalyticsManager analytics = new Analytics.AnalyticsManager();

        Map<String, Double> completion = analytics.calculateCompletionPercentages(selectedCourse);
        Map<String, Double> quizAverages = analytics.calculateQuizAverages(selectedCourse);

        InsightsPanel panel = new InsightsPanel(completion, quizAverages);
        ChartFrame frame = new ChartFrame("Course Insights", panel);
        frame.setVisible(true);
    }

    // ------------------- Inner Classes -------------------
    public class ChartFrame extends JFrame {
        public ChartFrame(String title, JPanel chartPanel) {
            setTitle(title);
            setSize(800,600);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            add(chartPanel, BorderLayout.CENTER);
        }
    }

    public class InsightsPanel extends JPanel {
        public InsightsPanel(Map<String, Double> completionPercentages, Map<String, Double> quizAverages) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

            add(createSectionLabel("Course Completion Percentages"));
            add(createListPanel(completionPercentages));

            add(Box.createRigidArea(new Dimension(0,20)));

            add(createSectionLabel("Quiz Averages Per Lesson"));
            add(createListPanel(quizAverages));
        }

        private JLabel createSectionLabel(String text) {
            JLabel label = new JLabel(text);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            return label;
        }

        private JPanel createListPanel(Map<String, Double> data) {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                panel.add(new JLabel(entry.getKey() + " → " + String.format("%.1f%%", entry.getValue())));
            }
            return panel;
        }
    }
}
