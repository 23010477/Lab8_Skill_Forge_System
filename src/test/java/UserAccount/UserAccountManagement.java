
package UserAccount;

import Student.Student;
import Student.StudentManagement;
import java.util.regex.Pattern;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.swing.JOptionPane;
import UserAccount.Role;
import java.util.ArrayList;
import InstructorManagement.Instructor;
import InstructorManagement.InstructorManagement;

public abstract class UserAccountManagement {

    protected StudentManagement studentManagement;
    protected Student currentUser;

    private ArrayList<Student> Students;
    private ArrayList<Instructor> Instructors;

    int userID = 0;

    public UserAccountManagement() {
        this.studentManagement = new StudentManagement();
        this.Students = new ArrayList<>();
        this.Instructors = new ArrayList<>();
    }

    public void signUp(String User, String email, String Pass, Role r) throws  IllegalArgumentException {
        boolean emailValid = false, userValid = false, passValid = false;

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@"
                + "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern p = Pattern.compile(emailRegex);
        if (email != null && p.matcher(email).matches()) {
            emailValid = true;
        } else {

            JOptionPane.showMessageDialog(null, "Invalid email format", "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException();

        }

        if (User == null || User.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter a username", "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException();

        } else if (!User.matches("[a-zA-Z]+")) {
            JOptionPane.showMessageDialog(null, "Username must contain only letters", "Error",
                    JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException();
        } else {
            userValid = true;
        }

        if (Pass == null || Pass.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter a password", "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException();
        } else {
            passValid = true;
        }

        if (emailValid && userValid && passValid) {
            // Check if username already exists
            if (studentManagement.findStudentByUsername(User) != null) {
                JOptionPane.showMessageDialog(null, "Username already exists", "Error", JOptionPane.ERROR_MESSAGE);
                throw new IllegalArgumentException();
            }

            // Check if email already exists
            for (Student s : studentManagement.getAllStudents()) {
                if (s.getEmail() != null && s.getEmail().equals(email)) {
                    JOptionPane.showMessageDialog(null, "Email already exists", "Error", JOptionPane.ERROR_MESSAGE);
                    throw new IllegalArgumentException();
                }
            }

            // Generate new userID
            int newUserId = generateNewUserId();

            String hashedPass = HashMe(Pass);

            if (hashedPass == null) {
                JOptionPane.showMessageDialog(null, "Error hashing password", "Error", JOptionPane.ERROR_MESSAGE);
                throw new IllegalArgumentException();
            }

            if (r == Role.INSTRUCTOR) {
                Instructor i = new Instructor(newUserId, User, email, hashedPass);
                Instructors.add(i);
                // Save instructor to InstructorManagement
                InstructorManagement instructorManagement = new InstructorManagement();
                instructorManagement.addInstructor(i);
            } else {
                Student s = new Student(newUserId, User, email, hashedPass);
                Students.add(s);
                studentManagement.addStudent(s);
            }
            JOptionPane.showMessageDialog(null, "Account created successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private int generateNewUserId() {
        int maxId = 0;
        // Check students
        for (Student student : studentManagement.getAllStudents()) {
            if (student.getUserID() > maxId) {
                maxId = student.getUserID();
            }
        }
        // Check instructors
        InstructorManagement instructorManagement = new InstructorManagement();
        for (Instructor instructor : instructorManagement.getAllInstructors()) {
            if (instructor.getUserId() > maxId) {
                maxId = instructor.getUserId();
            }
        }
        return maxId + 1;
    }

    public String HashMe(String Password) {
        try {
            MessageDigest hObj = MessageDigest.getInstance("SHA-256");
            byte[] passwordHash = hObj.digest(Password.getBytes()); // bytala3 byte arr 256bits 8er mafhoom f lazem
                                                                    // n7a2welo

            StringBuilder hStr = new StringBuilder();
            for (byte i : passwordHash) {
                String h = Integer.toHexString(0xff & i);
                if (h.length() == 1) {
                    hStr.append('0');
                }
                hStr.append(h);

            }
            return hStr.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean Login(String User, String Pass) {
        if (User == null || User.trim().isEmpty()) {
            return false;
        }

        if (Pass == null || Pass.trim().isEmpty()) {
            return false;
        }

        Student student = studentManagement.findStudentByUsername(User);
        if (student == null) {
            return false;
        }

        String hashedPass = HashMe(Pass);
        if (hashedPass == null) {
            return false;
        }

        if (student.getHashedPass() != null && student.getHashedPass().equals(hashedPass)) {
            currentUser = student;
            return true;
        } else {
            return false;
        }
    }

    public void logout() {
        if (currentUser != null) {
            currentUser = null;
            JOptionPane.showMessageDialog(null, "Logged out successfully", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "No user is currently logged in", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public Student getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

}
