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
}
