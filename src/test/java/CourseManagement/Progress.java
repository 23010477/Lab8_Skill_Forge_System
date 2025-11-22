package CourseManagement;

import Student.Student;

import java.util.ArrayList;
import java.util.HashMap;

public class Progress {
    private Student student;
    private Course course;
    private ArrayList<Lesson> completedLessons;
    private HashMap<Integer, ArrayList<Integer>> quizAttempts;


    public Progress(Student student, Course course) {
        this.setStudent(student);
        this.setCourse(course);
        this.completedLessons = new ArrayList<>();
        this.quizAttempts = new HashMap<>();
    }

    public void addAttempt(int lessonId, int stdScore ){
        quizAttempts.putIfAbsent(lessonId, new ArrayList<>());
        quizAttempts.get(lessonId).add(stdScore);





    }

    public ArrayList<Integer> getListOfAttempts(int lessonId){
        return quizAttempts.getOrDefault(lessonId,new ArrayList<>());
    }

    public void addCompletedLesson(Lesson lesson){
        if(lesson.getQuiz()==null){
            if (lesson != null && !completedLessons.contains(lesson)){
            completedLessons.add(lesson);
            }
        }else {
            ArrayList<Integer> attempts = getListOfAttempts(lesson.getLessonId());
            for(int attempt : attempts){
                if(attempt >= 50) {  // 23meloha percentages fl 7esbaaaa!!
                completedLessons.add(lesson);
                break;
                }

            }
        }
    }

    public Student getStudent() {
        return student;
    }
    public void setStudent(Student student) {
        this.student = student;
    }
    public ArrayList<Lesson> getCompletedLessons(){
        return completedLessons;
    }
    public Course getCourse() {
        return course;
    }
    public void setCourse(Course course) {
        this.course = course;
    }
    public double getPercentage() {
        if(course.getLessons().isEmpty()) return 0;
        return (double) (completedLessons.size() * 100) / course.getLessons().size();

    }

    public boolean courseCompletion(){
        return !course.getLessons().isEmpty() && course.getLessons().size() == completedLessons.size();
    }
    public void displayProgressTerminal() {
        System.out.println("Progress for " + student.getUsername() + " , Course " + course.getTitle());
        System.out.println("Completed " + completedLessons.size() + " / " + course.getLessons().size() + " lessons");
        System.out.printf("Percentage: %.2f%%\n", getPercentage());

    }


}
