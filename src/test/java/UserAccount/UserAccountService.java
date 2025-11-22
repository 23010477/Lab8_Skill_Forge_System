package UserAccount;

import Student.Student;
import InstructorManagement.Instructor;
import InstructorManagement.InstructorManagement;

public class UserAccountService extends UserAccountManagement {
    
    private InstructorManagement instructorManagement;
    
    public UserAccountService() {
        super();
        this.instructorManagement = new InstructorManagement();
    }
    
    @Override
    public void signUp(String User, String email, String Pass, Role r) {
        // Call parent method which handles validation and saves to JSON
        // The parent method already saves Instructor to InstructorManagement
        super.signUp(User, email, Pass, r);
    }
    
    @Override
    public boolean Login(String User, String Pass) {
        if (User == null || User.trim().isEmpty()) {
            return false;
        }

        if (Pass == null || Pass.trim().isEmpty()) {
            return false;
        }

        String hashedPass = HashMe(Pass);
        if (hashedPass == null) {
            return false;
        }

        // Try to find as Student first
        Student student = studentManagement.findStudentByUsername(User);
        if (student != null && student.getHashedPass() != null && student.getHashedPass().equals(hashedPass)) {
            currentUser = student;
            return true;
        }

        // Try to find as Instructor
        Instructor instructor = instructorManagement.findInstructorByName(User);
        if (instructor != null && instructor.getHashedPass() != null && instructor.getHashedPass().equals(hashedPass)) {
            // For instructor login, we can't set currentUser (it's Student type)
            // But we can return true to indicate successful login
            return true;
        }

        return false;
    }
    
    public Instructor getCurrentInstructor(String username) {
        return instructorManagement.findInstructorByName(username);
    }
}

