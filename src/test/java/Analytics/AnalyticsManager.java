package Analytics;

import CourseManagement.*;
import Student.*;
import java.util.*;

public class AnalyticsManager {

    // Returns completion percentage per student
    public Map<String, Double> calculateCompletionPercentages(Course course) {
        Map<String, Double> completion = new HashMap<>();
        for (Student student : course.getStudents()) {
            for (Progress p : student.getProgresses()) {
                if (p.getCourse().equals(course)) {
                    completion.put(student.getUsername(), p.getPercentage());
                }
            }
        }
        return completion;
    }

    // Returns average score per lesson in the course
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
                if (progress == null) continue;

                ArrayList<Integer> attempts = progress.getListOfAttempts(lesson.getLessonId());
                for (int score : attempts) {
                    totalScore += score;
                    totalAttempts++;
                }
            }

            double average = totalAttempts > 0 ? totalScore / totalAttempts : 0.0;
            averages.put(lesson.getTitle(), average);
        }

        return averages;
    }
}
