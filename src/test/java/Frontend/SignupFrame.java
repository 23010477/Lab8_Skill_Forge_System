package Frontend;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import UserAccount.Role;
import UserAccount.UserAccountService;

// <editor-fold defaultstate="collapsed" desc="SignupFrame Class">
/**
 * Signup frame for user registration
 */
public class SignupFrame extends JFrame {
    
    // <editor-fold defaultstate="collapsed" desc="Variables">
    private UserAccountService userAccountService;
    private JTextField usernameField, emailField;
    private JPasswordField passwordField;
    private JComboBox<Role> roleComboBox;
    private JButton signupButton, backButton;
    private JLabel titleLabel, usernameLabel, emailLabel, passwordLabel, roleLabel, messageLabel;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    public SignupFrame() {
        // Initialize user account service
        userAccountService = new UserAccountService();
        
        // Frame settings
        setTitle("Sign Up");
        setSize(400, 380);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);
        
        // Title
        titleLabel = new JLabel("Sign Up");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 0, 255));
        titleLabel.setBounds(150, 20, 200, 30);
        add(titleLabel);
        
        // Username Label
        usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameLabel.setForeground(new Color(0, 0, 255));
        usernameLabel.setBounds(50, 70, 100, 25);
        add(usernameLabel);
        
        // Username Field
        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBounds(160, 70, 180, 30);
        add(usernameField);
        
        // Email Label
        emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        emailLabel.setForeground(new Color(0, 0, 255));
        emailLabel.setBounds(50, 115, 100, 25);
        add(emailLabel);
        
        // Email Field
        emailField = new JTextField();
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setBounds(160, 115, 180, 30);
        add(emailField);
        
        // Password Label
        passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordLabel.setForeground(new Color(0, 0, 255));
        passwordLabel.setBounds(50, 160, 100, 25);
        add(passwordLabel);
        
        // Password Field
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBounds(160, 160, 180, 30);
        add(passwordField);
        
        // Role Label
        roleLabel = new JLabel("Role:");
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        roleLabel.setForeground(new Color(0, 0, 255));
        roleLabel.setBounds(50, 205, 100, 25);
        add(roleLabel);
        
        // Role ComboBox
        roleComboBox = new JComboBox<>(Role.values());
        roleComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        roleComboBox.setBounds(160, 205, 180, 30);
        roleComboBox.setSelectedIndex(0); // Default to first role
        add(roleComboBox);
        
        // Signup Button
        signupButton = new JButton("Sign Up");
        signupButton.setFont(new Font("Arial", Font.BOLD, 16));
        signupButton.setBackground(new Color(0, 0, 255));
        signupButton.setForeground(Color.WHITE);
        signupButton.setBounds(80, 260, 100, 35);
        signupButton.setFocusPainted(false);
        add(signupButton);
        
        // Back Button
        backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setBackground(new Color(0, 0, 255));
        backButton.setForeground(Color.WHITE);
        backButton.setBounds(200, 260, 100, 35);
        backButton.setFocusPainted(false);
        add(backButton);
        
        // Message Label
        messageLabel = new JLabel("");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        messageLabel.setForeground(Color.RED);
        messageLabel.setBounds(50, 310, 300, 25);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(messageLabel);
        
        // Signup button action
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSignup();
            }
        });
        
        // Back button action
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginFrame();
                dispose();
            }
        });
        
        // Press Enter to signup
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSignup();
            }
        });
        
        setVisible(true);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Event Handlers">
    private void handleSignup() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        Role selectedRole = (Role) roleComboBox.getSelectedItem();
        
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please fill all fields");
            messageLabel.setForeground(Color.RED);
        } else if (password.length() < 4) {
            messageLabel.setText("Password must be at least 4 characters");
            messageLabel.setForeground(Color.RED);
        } else {
            // Use UserAccountService to sign up (it will validate and save to JSON)
            // Temporarily disable JOptionPane messages by catching them
            try {
                userAccountService.signUp(username, email, password, selectedRole);
                
                // If we reach here, signup was successful
                messageLabel.setText("Account created successfully as " + selectedRole.name() + "!");
                messageLabel.setForeground(new Color(0, 153, 0)); // Green
                
                // Clear fields
                usernameField.setText("");
                emailField.setText("");
                passwordField.setText("");
                
                // Go back to login after 2 seconds
                Timer timer = new Timer(2000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        new LoginFrame();
                        dispose();
                    }
                });
                timer.setRepeats(false);
                timer.start();
            } catch (Exception e) {
                // Error messages are shown by UserAccountService via JOptionPane
                // But we can also show in message label
                messageLabel.setText("Registration failed. Please check the error message.");
                messageLabel.setForeground(Color.RED);
            }
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Main Method">
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SignupFrame());
    }
    // </editor-fold>
}
// </editor-fold>