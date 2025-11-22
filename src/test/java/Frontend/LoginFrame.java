package Frontend;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import UserAccount.Role;
import UserAccount.UserAccountService;
import Student.Student;
import InstructorManagement.Instructor;

// <editor-fold defaultstate="collapsed" desc="LoginFrame Class">
/**
 * Login frame for user authentication
 */
public class LoginFrame extends JFrame {
    
    // <editor-fold defaultstate="collapsed" desc="Variables">
    private UserAccountService userAccountService;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<Role> roleComboBox;
    private JButton loginButton, signupButton;
    private JLabel titleLabel, usernameLabel, passwordLabel, roleLabel, messageLabel;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    public LoginFrame() {
        // Initialize user account service
        userAccountService = new UserAccountService();
        
        // Frame settings
        setTitle("Login");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);
        
        // Title
        titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 0, 255));
        titleLabel.setBounds(160, 20, 200, 30);
        add(titleLabel);
        
        // Username Label
        usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameLabel.setForeground(new Color(0, 0, 255));
        usernameLabel.setBounds(50, 80, 100, 25);
        add(usernameLabel);
        
        // Username Field
        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBounds(160, 80, 180, 30);
        add(usernameField);
        
        // Password Label
        passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordLabel.setForeground(new Color(0, 0, 255));
        passwordLabel.setBounds(50, 130, 100, 25);
        add(passwordLabel);
        
        // Password Field
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBounds(160, 130, 180, 30);
        add(passwordField);
        
        // Role Label
        roleLabel = new JLabel("Role:");
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        roleLabel.setForeground(new Color(0, 0, 255));
        roleLabel.setBounds(50, 180, 100, 25);
        add(roleLabel);
        
        // Role ComboBox
        roleComboBox = new JComboBox<>(Role.values());
        roleComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        roleComboBox.setBounds(160, 180, 180, 30);
        roleComboBox.setSelectedIndex(0); // Default to first role
        add(roleComboBox);
        
        // Login Button
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBackground(new Color(0, 0, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBounds(80, 230, 100, 35);
        loginButton.setFocusPainted(false);
        add(loginButton);
        
        // Signup Button
        signupButton = new JButton("Sign Up");
        signupButton.setFont(new Font("Arial", Font.BOLD, 16));
        signupButton.setBackground(new Color(0, 0, 255)); 
        signupButton.setForeground(Color.WHITE);
        signupButton.setBounds(200, 230, 100, 35);
        signupButton.setFocusPainted(false);
        add(signupButton);
        
        // Message Label
        messageLabel = new JLabel("");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        messageLabel.setForeground(Color.RED);
        messageLabel.setBounds(50, 270, 300, 25);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(messageLabel);
        
        // Login button action
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        
        // Signup button action
        // Signup button action
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SignupFrame();
                dispose();
            }
        });
        
        // Press Enter to login
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        
        setVisible(true);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Event Handlers">
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        Role selectedRole = (Role) roleComboBox.getSelectedItem();
        
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please fill all fields");
            messageLabel.setForeground(Color.RED);
            return;
        }
        
        // Use UserAccountService to login
        boolean loginSuccess = userAccountService.Login(username, password);
        
        if (loginSuccess) {
            // Verify role matches
            Student student = userAccountService.getCurrentUser();
            Instructor instructor = userAccountService.getCurrentInstructor(username);
            
            // Check if role matches
            boolean roleMatches = false;
            if (selectedRole == Role.STUDENT && student != null) {
                roleMatches = true;
            } else if (selectedRole == Role.INSTRUCTOR && instructor != null) {
                roleMatches = true;
            } else if (selectedRole == Role.ADMIN) {
                // Admin login - you can add admin check here
                roleMatches = true; // For now, allow if login successful
            }
            
            if (!roleMatches) {
                messageLabel.setText("Role mismatch. Please select correct role.");
                messageLabel.setForeground(Color.RED);
                return;
            }
            
            messageLabel.setText("Login successful!");
            messageLabel.setForeground(new Color(0, 153, 0)); // Green
            dispose(); // Close login frame
                    if (selectedRole == Role.STUDENT && student != null) {
                        new StudentDashboard(student).setVisible(true);
                    } else if (selectedRole == Role.INSTRUCTOR && instructor != null) {
                        new InstructorDashboard(instructor).setVisible(true);
                    } else if (selectedRole == Role.ADMIN) {
                        // TODO: Create AdminDashboard if needed
                        JOptionPane.showMessageDialog(null, "Admin Dashboard - Coming Soon", "Admin", JOptionPane.INFORMATION_MESSAGE);
                    }
            // // Open appropriate dashboard based on role
            // Timer timer = new Timer(1000, new ActionListener() {
            //     @Override
            //     public void actionPerformed(ActionEvent e) {
            //         dispose(); // Close login frame
            //         if (selectedRole == Role.STUDENT && student != null) {
            //             new StudentDashboard(student).setVisible(true);
            //         } else if (selectedRole == Role.INSTRUCTOR && instructor != null) {
            //             new InstructorDashboard(instructor).setVisible(true);
            //         } else if (selectedRole == Role.ADMIN) {
            //             // TODO: Create AdminDashboard if needed
            //             JOptionPane.showMessageDialog(null, "Admin Dashboard - Coming Soon", "Admin", JOptionPane.INFORMATION_MESSAGE);
            //         }
            //     }
            // });
            // timer.setRepeats(false);
            // timer.start();
        } else {
            messageLabel.setText("Invalid username or password");
            messageLabel.setForeground(Color.RED);
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Main Method">
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
    // </editor-fold>
}
// </editor-fold>