package CourseManagement;

import java.util.ArrayList;

public class Questions {
    private String question;
    private ArrayList<String> mcqOptions;
    private int indexOfCorrectOption;




    public Questions(String question, ArrayList<String> mcqOptions, int indexOfCorrectOption){
        this.question = question;
        this.mcqOptions = mcqOptions;
        this.indexOfCorrectOption = indexOfCorrectOption;
    }

    public boolean checkAns(int indexOfStudentAnswer){
        if (indexOfStudentAnswer == indexOfCorrectOption)
            return true;
        return false;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<String> getMcqOptions() {
        return mcqOptions;
    }

    public void setIndexOfCorrectOption(int indexOfCorrectOption) {
        this.indexOfCorrectOption = indexOfCorrectOption;
    }

    public int getIndexOfCorrectOption() {
        return indexOfCorrectOption;
    }


}

