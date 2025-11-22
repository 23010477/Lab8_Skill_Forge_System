
package InstructorManagement;
import CourseManagement.*;
import Student.*;
import java.util.ArrayList;
public class StudentPerformanceAnalytics {
  
    public void recordQuizResults(){
        
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
        
    }
    public void courseStatics(Course c){
        ArrayList<Student> listOfStudents=new ArrayList<>(c.getStudents());
        System.out.println("The course is: " +c.getTitle());
        System.out.println("The Students enrolled in the course:");
        for(Student s:listOfStudents){
            System.out.print(s);
        }
        System.out.println();
        
        
    }
    
}
