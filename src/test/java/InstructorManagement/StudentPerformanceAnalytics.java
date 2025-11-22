
package InstructorManagement;
import CourseManagement.*;
import Student.*;
import java.util.ArrayList;
public class StudentPerformanceAnalytics {
  
    public void recordQuizResults(){
        
    }
    public void lessonCompletion(Lesson completedLesson, Student student,Progress p){
      p.addCompletedLesson(completedLesson);
      student.setProgresses(p.getCompletedLessons());
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
    
}
