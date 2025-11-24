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
    private JButton viewStudentsButton, viewStudentProgressButton, refreshButton, logoutButton, insightsButton;

    private JTabbedPane tabbedPane;
    private Course selectedCourse;

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
        loadCourses();
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
            if (!instructors.isEmpty()) currentInstructor = instructors.get(0);
        }

        // Calculate next IDs
        calculateNextIds();

        // Initialize tables
        coursesModel = new DefaultTableModel(
                new Object[] { "Course ID", "Title", "Description", "Lessons", "Students" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        lessonsModel = new DefaultTableModel(new Object[] { "Lesson ID", "Title" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentsModel = new DefaultTableModel(new Object[] { "Student ID", "Username", "Email", "Progress %" }, 0) {
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
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="UI Setup Methods">
    private void setupUI() {
        setTitle("Instructor Dashboard");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        tabbedPane = new JTabbedPane();

        // Tab 1: Course Management
        JPanel courseManagementPanel = createCourseManagementPanel();
        tabbedPane.addTab("Course Management", courseManagementPanel);

        // Tab 2: Lesson Management
        JPanel lessonManagementPanel = createLessonManagementPanel();
        tabbedPane.addTab("Lesson Management", lessonManagementPanel);

        // Tab 3: Students & Progress
        JPanel studentsPanel = createStudentsPanel();
        tabbedPane.addTab("Students & Progress", studentsPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(insightsButton);
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
        refreshButton.addActionListener(e -> loadCourses());
        logoutButton.addActionListener(e -> handleLogout());

        coursesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateSelectedCourse();
        });


        lessonsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateLessonContent();
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
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        bottomPanel.add(new JLabel("Course Title:"), gbc);
        gbc.gridx=1;
        bottomPanel.add(courseTitleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        bottomPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx=1;
        bottomPanel.add(courseDescriptionField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
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
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Lesson Title:"), gbc);
        gbc.gridx=1;
        inputPanel.add(lessonTitleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Lesson Content:"), gbc);
        gbc.gridx=1;
        inputPanel.add(lessonContentField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addLessonButton);
        buttonPanel.add(deleteLessonButton);

        JButton takeQuizButton = new JButton("Take Quiz");
        takeQuizButton.addActionListener(e -> handleTakeQuiz());
        buttonPanel.add(takeQuizButton);

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
        System.out.println("DEBUG: Reloading data for instructor ID: " + currentInstructor.getUserId());
        currentInstructor = instructorManagement.findInstructor(currentInstructor.getUserId());
        if (currentInstructor == null) {
            System.out.println("DEBUG: Could not find instructor in fresh management instance");
            return;
        }

        // Load instructor's courses
        coursesModel.setRowCount(0);
        System.out.println("DEBUG: Fetching courses for instructor ID: " + currentInstructor.getUserId());
        ArrayList<Course> instructorCourses = courseManagement
                .getCoursesByInstructor(String.valueOf(currentInstructor.getUserId()));
        System.out.println("DEBUG: Found " + instructorCourses.size() + " courses");

        for (Course course : instructorCourses) {
            coursesModel.addRow(new Object[] {
                    course.getCourseId(),
                    course.getTitle(),
                    course.getDescription(),
                    course.getLessons().size(),
                    course.getStudents().size()
            });
        }

        // Clear other tables if no course selected
        if (selectedCourse == null) {
            lessonsModel.setRowCount(0);
            studentsModel.setRowCount(0);
        }
    }

    private void loadCourses() {
        loadData();
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
                    lessonsModel.addRow(new Object[] {
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

        Course newCourse = new Course(nextCourseId++, title, description,
                String.valueOf(currentInstructor.getUserId()));
        courseManagement.addCourse(newCourse);
        currentInstructor.addCourse(newCourse);
        instructorManagement.saveInstructors();

        JOptionPane.showMessageDialog(this, "Course created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        courseTitleField.setText("");
        courseDescriptionField.setText("");
        loadCourses();
    }


    private void handleDeleteCourse() {
        int selectedRow = coursesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to delete", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int courseId = (Integer) coursesModel.getValueAt(selectedRow, 0);
        Course course = courseManagement.findCourse(courseId);

        if (course == null)
            return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete course: " + course.getTitle()
                        + "?\nThis will remove all lessons and unenroll all students.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            courseManagement.removeCourse(courseId);
            currentInstructor.removecreatedCourses(course);
            instructorManagement.saveInstructors();

            JOptionPane.showMessageDialog(this, "Course deleted successfully", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            selectedCourse = null;
            loadCourses();
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

       
        updateSelectedCourse();
    }


    private void handleDeleteLesson() {
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please select a course first", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedRow = lessonsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a lesson to delete", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int lessonId = (Integer) lessonsModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this lesson?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            courseManagement.removeLessonFromCourse(selectedCourse.getCourseId(), lessonId);
            JOptionPane.showMessageDialog(this, "Lesson deleted successfully", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            loadData();
            updateSelectedCourse();
        }
    }


    private void handleViewStudents() {
        int selectedRow = coursesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to view students", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int courseId = (Integer) coursesModel.getValueAt(selectedRow, 0);
        Course course = courseManagement.findCourse(courseId);

        if (course == null)
            return;

        // Switch to students tab
        tabbedPane.setSelectedIndex(3);

        // Load students
        studentsModel.setRowCount(0);
        for (Student student : course.getStudents()) {
            double progress = getStudentProgress(student, course);
            studentsModel.addRow(new Object[] {
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
            JOptionPane.showMessageDialog(this, "Please select a student to view progress", "Warning",
                    JOptionPane.WARNING_MESSAGE);
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

        if (course == null)
            return;

        Progress progress = getStudentProgressObject(student, course);
        if (progress == null) {
            JOptionPane.showMessageDialog(this, "No progress found for this student", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Student: ").append(student.getUsername()).append("\n");
        sb.append("Course: ").append(course.getTitle()).append("\n");
        sb.append("Progress: ").append(String.format("%.1f%%", progress.getPercentage())).append("\n\n");
        sb.append("Completed Lessons: ").append(progress.getCompletedLessons().size()).append(" / ")
                .append(course.getLessons().size()).append("\n\n");
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

    private void handleTakeQuiz() {
        JOptionPane.showMessageDialog(this, 
            "Instructors cannot take quizzes. Only students can take quizzes.", 
            "Information", 
            JOptionPane.INFORMATION_MESSAGE);
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
        SwingUtilities.invokeLater(() -> new InstructorDashboard().setVisible(true));
    }
    // </editor-fold>
}
// </editor-fold>
