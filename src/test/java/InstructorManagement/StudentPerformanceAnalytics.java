
package InstructorManagement;
import CourseManagement.*;
import Student.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class StudentPerformanceAnalytics {

    public void recordQuizResults(Student student, Lesson lesson, Progress progress, int score){
    if (progress != null && lesson != null) {
        progress.addAttempt(lesson.getLessonId(), score); 
    }
}

    public void lessonCompletion(Lesson completedLesson,Progress p){
       try{
           p.addCompletedLesson(completedLesson);
      
       }catch(Exception e){
           System.out.println("Lesson is null!");
       }
     
    }
    public void progressionData(Student student){
        ArrayList<Progress> progresses=new ArrayList<>(student.getProgresses());
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
public Map<String, Double> calculateQuizAverages(Course course) {
    Map<String, Double> averages = new HashMap<>();

    for (Lesson lesson : course.getLessons()) {
        Quizzes quiz = lesson.getQuiz();
        if (quiz == null || quiz.getQuestions().isEmpty()) {
            averages.put(lesson.getTitle(), 0.0);
            continue;
        }

        double totalScore = 0;
        int totalAttempts = 0;

        for (Student student : course.getStudents()) {
            Progress progress = null;
            for (Progress p : student.getProgresses()) {
                if (p.getCourse().equals(course)) {
                    progress = p;
                    break;
                }
            }

            if (progress != null) {
                ArrayList<Integer> attempts = progress.getListOfAttempts(lesson.getLessonId());
                for (int score : attempts) {
                    totalScore += score;
                    totalAttempts++;
                }
            }
        }

        double average = totalAttempts > 0 ? (totalScore / totalAttempts) : 0.0;
        averages.put(lesson.getTitle(), average);
    }

    return averages;
}

}
