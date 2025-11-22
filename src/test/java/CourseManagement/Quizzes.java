package CourseManagement;
import java.util.ArrayList;

public class Quizzes {
    private ArrayList<Questions> questions;

    public Quizzes(){
        this.questions = new ArrayList<>();
    }

    public ArrayList<Questions> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Questions> questions) {
        this.questions = questions;
    }



}
