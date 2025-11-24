package CourseManagement;

public class Lesson {
    private int lessonId;
    private String title;
    private String content;
    private Quizzes quiz;

    public Lesson(int lessonId, String title, String content) {
        this.setLessonId(lessonId);
        this.setTitle(title);
        this.setContent(content);
        this.quiz = null;
    }

    public int getLessonId() {
        return lessonId;
    }

    public void setLessonId(int lessonId) {
        this.lessonId = lessonId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Quizzes getQuiz() {
        return quiz;
    }

    public void setQuiz(Quizzes quiz) {
        this.quiz = quiz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Lesson lesson = (Lesson) o;
        return lessonId == lesson.lessonId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(lessonId);
    }
}
