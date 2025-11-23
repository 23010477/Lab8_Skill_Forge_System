//GEN-BEGIN:variables
package Frontend;
//GEN-END:variables

import CourseManagement.Course;
import CourseManagement.CourseManagementSystem;
import CourseManagement.CourseStatus;
import Frontend.LoginFrame;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import static java.awt.PageAttributes.MediaType.C;
import java.util.ArrayList;

public class AdminDashboard extends JFrame {

    private CourseManagementSystem courseManagement;
    private JTable coursesTable;
    private DefaultTableModel coursesModel;
    private JButton approveButton, rejectButton, refreshButton, logoutButton;

    public AdminDashboard() {
        courseManagement = new CourseManagementSystem();
        initializeComponents();
        setupUI();
        loadPendingCourses();
    }

    private void initializeComponents() {
        coursesModel = new DefaultTableModel(new Object[]{
                "Course ID", "Title", "Instructor ID", "Description", "Status"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        coursesTable = new JTable(coursesModel);

        approveButton = new JButton("Approve");
        rejectButton = new JButton("Reject");
        refreshButton = new JButton("Refresh");
        logoutButton = new JButton("Logout");

        approveButton.addActionListener(e -> handleApproveCourse());
        rejectButton.addActionListener(e -> handleRejectCourse());
        refreshButton.addActionListener(e -> loadPendingCourses());
        logoutButton.addActionListener(e -> handleLogout());
    }

    private void setupUI() {
        setTitle("Admin Dashboard - Course Approval");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        add(new JScrollPane(coursesTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(approveButton);
        bottomPanel.add(rejectButton);
        bottomPanel.add(refreshButton);
        bottomPanel.add(logoutButton);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadPendingCourses() {
        coursesModel.setRowCount(0);
        ArrayList<Course> allCourses = courseManagement.getAllCourses();

            Course c=null;
        for (Course course : allCourses) {
            if (c.getStatus() == null || c.getStatus() == CourseStatus.PENDING) {
                coursesModel.addRow(new Object[]{
                        course.getCourseId(),
                        course.getTitle(),
                        course.getInstructorId(),
                        course.getDescription(),
                        course.getStatus()
                });
            }
        }
    }

    private void handleApproveCourse() {
        int selectedRow = coursesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a course to approve", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int courseId = (Integer) coursesModel.getValueAt(selectedRow, 0);
        Course course = courseManagement.findCourse(courseId);
        if (course != null) {
            course.setStatus(CourseStatus.APPROVED);
            courseManagement.saveCourses(); // persist changes
            JOptionPane.showMessageDialog(this, "Course Approved!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadPendingCourses();
        }
    }

    private void handleRejectCourse() {
        int selectedRow = coursesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a course to reject", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int courseId = (Integer) coursesModel.getValueAt(selectedRow, 0);
        Course course = courseManagement.findCourse(courseId);
        if (course != null) {
            course.setStatus(CourseStatus.REJECTED);
            courseManagement.saveCourses(); // persist changes
            JOptionPane.showMessageDialog(this, "Course Rejected!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadPendingCourses();
        }
    }

    private void handleLogout() {
        dispose();
        new LoginFrame();
    }
}
    /**
     * @param args the command line arguments
     */
    

    // Variables declaration - do not modify                     
    // End of variables declaration                   

