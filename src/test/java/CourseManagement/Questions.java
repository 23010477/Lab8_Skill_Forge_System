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


}

