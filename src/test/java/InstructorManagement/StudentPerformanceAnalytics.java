
package InstructorManagement;
import CourseManagement.*;
import JsonDBManager.JsonDBManager;
import Student.*;
import java.util.ArrayList;
public class StudentPerformanceAnalytics {
  private static final String USERS_FILE = "src/test/java/JsonDBManager/users.json";
    public void recordQuizResults(){
        //will implement once quiz class is ready
    }
    public void lessonCompletion(Lesson completedLesson,Progress p){
       try{
           p.addCompletedLesson(completedLesson);
      
       }catch(Exception e){
           System.out.println("Lesson is null!");
       }
     
    }
    public void progressionData(Student student){
        ArrayList<Progress> progresses=new ArrayList(student.getProgresses());
        for(Progress p:progresses){
            System.out.println("Course is: "+ p.getCourse().getTitle());
            System.out.println("Lesson completed: "+p.getCompletedLessons().size());
            System.out.println("Progress Completed: "+ p.getPercentage());
            System.out.println("Course Completed? "+ p.courseCompletion());
        }

        
    }
    public void lessonStatics(){
       // will implement once quiz class is ready
    }
    public void courseStatics(Course c,StudentManagement studentManagement){
        ArrayList<Student> listOfStudents=new ArrayList<>(c.getStudents());
        System.out.println("The course is: " +c.getTitle());
        System.out.println("The Students enrolled in the course:");
        for(Student s:listOfStudents){
            System.out.print(s.getUsername());
            studentManagement.showStudentProgress(s,c.getCourseId());
        }
       
    }
    
}
