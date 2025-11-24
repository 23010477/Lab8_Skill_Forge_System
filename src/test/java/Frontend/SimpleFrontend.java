package Frontend;

import Student.Student;
import Student.StudentManagement;
import CourseManagement.Course;
import CourseManagement.CourseManagementSystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.security.MessageDigest;
import java.util.List;

// <editor-fold defaultstate="collapsed" desc="SimpleFrontend Class">
/**
 * Simple frontend for testing student and course management
 */
public class SimpleFrontend {
    // <editor-fold defaultstate="collapsed" desc="Variables">
    private JFrame frame;
    private JList<Student> studentJList;
    private DefaultListModel<Student> studentListModel;
    private JList<Course> courseJList;
    private DefaultListModel<Course> courseListModel;

    private final StudentManagement studentManagement;
    private final CourseManagementSystem courseManagementSystem;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    public SimpleFrontend() {
        studentManagement = new StudentManagement();
        courseManagementSystem = new CourseManagementSystem();
        initialize();
        loadData();
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Initialization Methods">
    private void initialize() {
        frame = new JFrame("Simple Skill Forge Frontend");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        
        JPanel left = new JPanel(new BorderLayout(6, 6));
        left.add(new JLabel("Students"), BorderLayout.NORTH);
        studentListModel = new DefaultListModel<>();
        studentJList = new JList<>(studentListModel);
        studentJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentJList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            String text = value.getUser() + " (ID: " + value.getUserID() + ")";
            JLabel lbl = new JLabel(text);
            if (isSelected) lbl.setBackground(list.getSelectionBackground());
            return lbl;
        });
        left.add(new JScrollPane(studentJList), BorderLayout.CENTER);

        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addStudentBtn = new JButton("Add Student");
        addStudentBtn.addActionListener(this::onAddStudent);
        JButton refreshStudentsBtn = new JButton("Refresh");
        refreshStudentsBtn.addActionListener(e -> loadStudents());
        leftButtons.add(addStudentBtn);
        leftButtons.add(refreshStudentsBtn);
        left.add(leftButtons, BorderLayout.SOUTH);

      
        JPanel right = new JPanel(new BorderLayout(6, 6));
        right.add(new JLabel("Courses"), BorderLayout.NORTH);
        courseListModel = new DefaultListModel<>();
        courseJList = new JList<>(courseListModel);
        courseJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        courseJList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            String text = value.getTitle() + " (ID: " + value.getCourseId() + ")";
            JLabel lbl = new JLabel(text);
            if (isSelected) lbl.setBackground(list.getSelectionBackground());
            return lbl;
        });
        right.add(new JScrollPane(courseJList), BorderLayout.CENTER);

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton enrollBtn = new JButton("Enroll Selected Student");
        enrollBtn.addActionListener(e -> onEnrollStudent());
        JButton refreshCoursesBtn = new JButton("Refresh");
        refreshCoursesBtn.addActionListener(e -> loadCourses());
        rightButtons.add(enrollBtn);
        rightButtons.add(refreshCoursesBtn);
        right.add(rightButtons, BorderLayout.SOUTH);

        split.setLeftComponent(left);
        split.setRightComponent(right);
        split.setDividerLocation(380);

        root.add(split, BorderLayout.CENTER);

        frame.setContentPane(root);
        frame.setVisible(true);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Data Management Methods">
    private void loadData() {
        loadStudents();
        loadCourses();
    }

    private void loadStudents() {
        studentListModel.clear();
        List<Student> students = studentManagement.getAllStudents();
        for (Student s : students) studentListModel.addElement(s);
    }

    private void loadCourses() {
        courseListModel.clear();
        for (Course c : courseManagementSystem.getAllCourses()) courseListModel.addElement(c);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Event Handlers">
    private void onAddStudent(ActionEvent e) {
        JTextField username = new JTextField();
        JTextField email = new JTextField();
        JPasswordField password = new JPasswordField();
        Object[] inputs = {
                "Username:", username,
                "Email:", email,
                "Password:", password
        };
        int result = JOptionPane.showConfirmDialog(frame, inputs, "Add Student", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String u = username.getText().trim();
            String em = email.getText().trim();
            String pw = new String(password.getPassword());
            if (u.isEmpty() || em.isEmpty() || pw.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int newId = generateNewStudentId();
            String hashed = hashPassword(pw);
            Student s = new Student(newId, u, em, hashed);
            studentManagement.addStudent(s);
            loadStudents();
        }
    }

    private int generateNewStudentId() {
        int max = 0;
        for (Student s : studentManagement.getAllStudents()) {
            if (s.getUserID() > max) max = s.getUserID();
        }
        return max + 1;
    }

    private void onEnrollStudent() {
        Student s = studentJList.getSelectedValue();
        Course c = courseJList.getSelectedValue();
        if (s == null || c == null) {
            JOptionPane.showMessageDialog(frame, "Select a student and a course first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        studentManagement.enrollStudentInCourse(s.getUserID(), c);
        JOptionPane.showMessageDialog(frame, "Student enrolled in course.");
        loadStudents();
        loadCourses();
    }

    private String hashPassword(String pw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] h = md.digest(pw.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : h) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) sb.append('0');
                sb.append(hex);
            }
            return sb.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return pw; 
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Main Method">
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SimpleFrontend::new);
    }
    // </editor-fold>
}
// </editor-fold>
