
package InstructorManagement;
import CourseManagement.*;
import Student.*;
import java.util.ArrayList;
public class StudentPerformanceAnalytics {

    public void recordQuizResults(Student student,Lesson lesson,Progress progress){
       int lessonId=lesson.getLessonId(); //we have lesson id
        Quizzes quiz=lesson.getQuiz();   //we have the quiz of the lesson
          progress.getListOfAttempts(lessonId);
        
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
    public void lessonStatics(Lesson lesson,Progress p){
       Quizzes quiz=lesson.getQuiz();
       ArrayList<Questions> question=new ArrayList<>(quiz.getQuestions());
       System.out.println("The lesson's Questions are: ");
       for(Questions q:question){
           System.out.println(q.getQuestion());
       }
       int noOfAttemps=p.getListOfAttempts(lesson.getLessonId()).size();
       System.out.println("The number of the attemps are: "+ noOfAttemps);
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
