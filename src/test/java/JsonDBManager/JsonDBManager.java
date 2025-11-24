package JsonDBManager;

import CourseManagement.Course;
import CourseManagement.Lesson;
import Student.Student;
import Student.Certificate;
import InstructorManagement.Instructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsonDBManager {
    private static final String COURSE_FILE = "courses.json";

    public JsonDBManager() {
    }

    public static ArrayList<Course> readCourses(String filePath) {
        ArrayList<Course> courses = new ArrayList<>();
        try {
            String content = Files.readString(Path.of(filePath), StandardCharsets.UTF_8);
            if (content == null || content.isBlank())
                return courses;
            JSONArray arr = new JSONArray(content);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                int courseId = o.optInt("courseId", 0);
                String title = o.optString("title", "");
                String description = o.optString("description", "");
                String instructorId = o.optString("instructorId", "");
                Course c = new Course(courseId, title, description, instructorId);

                if (o.has("lessons")) {
                    JSONArray lessons = o.getJSONArray("lessons");
                    for (int j = 0; j < lessons.length(); j++) {
                        JSONObject lo = lessons.getJSONObject(j);
                        int lessonId = lo.optInt("lessonId", 0);
                        String ltitle = lo.optString("title", "");
                        String lcontent = lo.optString("content", "");
                        Lesson lesson = new Lesson(lessonId, ltitle, lcontent);
                        
                        // Load quiz if present
                        if (lo.has("quiz")) {
                            JSONObject quizObj = lo.getJSONObject("quiz");
                            if (quizObj.has("questions")) {
                                CourseManagement.Quizzes quiz = new CourseManagement.Quizzes();
                                JSONArray questions = quizObj.getJSONArray("questions");
                                for (int k = 0; k < questions.length(); k++) {
                                    JSONObject qObj = questions.getJSONObject(k);
                                    String questionText = qObj.optString("question", "");
                                    int correctIndex = qObj.optInt("indexOfCorrectOption", 0);
                                    JSONArray options = qObj.getJSONArray("mcqOptions");
                                    ArrayList<String> mcqOptions = new ArrayList<>();
                                    for (int m = 0; m < options.length(); m++) {
                                        mcqOptions.add(options.getString(m));
                                    }
                                    CourseManagement.Questions question = new CourseManagement.Questions(questionText, mcqOptions, correctIndex);
                                    quiz.addQuestion(question);
                                }
                                lesson.setQuiz(quiz);
                            }
                        }
                        c.addLesson(lesson);
                    }
                }
                
                // Load course status
                if (o.has("status")) {
                    String statusStr = o.optString("status", "PENDING");
                    try {
                        c.setStatus(CourseManagement.CourseStatus.valueOf(statusStr));
                    } catch (IllegalArgumentException e) {
                        c.setStatus(CourseManagement.CourseStatus.PENDING);
                    }
                }

                if (o.has("students")) {
                    JSONArray studs = o.getJSONArray("students");
                    for (int j = 0; j < studs.length(); j++) {

                        if (studs.isNull(j))
                            continue;
                        Object so = studs.get(j);
                        if (so instanceof JSONObject) {
                            JSONObject sObj = (JSONObject) so;
                            int userID = sObj.optInt("userID", -1);
                            String user = sObj.optString("username", null);
                            String email = sObj.optString("email", null);
                            String hashedPass = sObj.optString("hashedPass", null);
                            Student s = new Student(userID, user, email, hashedPass);
                            c.getStudents().add(s);
                        } else if (so instanceof Number) {
                            int userID = ((Number) so).intValue();
                            Student s = new Student(userID, null, null, null);
                            c.getStudents().add(s);
                        } else if (so instanceof String) {
                            try {
                                int userID = Integer.parseInt((String) so);
                                Student s = new Student(userID, null, null, null);
                                c.getStudents().add(s);
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }
                }

                courses.add(c);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public static void writeCourses(String filePath, ArrayList<Course> courses) {
        JSONArray arr = new JSONArray();
        for (Course c : courses) {
            JSONObject o = new JSONObject();
            o.put("courseId", c.getCourseId());
            o.put("title", c.getTitle());
            o.put("description", c.getDescription());
            o.put("instructorId", c.getInstructorId());

            JSONArray lessons = new JSONArray();
            for (Lesson l : c.getLessons()) {
                JSONObject lo = new JSONObject();
                lo.put("lessonId", l.getLessonId());
                lo.put("title", l.getTitle());
                lo.put("content", l.getContent());
                
                // Save quiz if present
                if (l.getQuiz() != null && l.getQuiz().getQuestions() != null && !l.getQuiz().getQuestions().isEmpty()) {
                    JSONObject quizObj = new JSONObject();
                    JSONArray questions = new JSONArray();
                    for (CourseManagement.Questions q : l.getQuiz().getQuestions()) {
                        JSONObject qObj = new JSONObject();
                        qObj.put("question", q.getQuestion());
                        qObj.put("indexOfCorrectOption", q.getIndexOfCorrectOption());
                        JSONArray mcqOptions = new JSONArray();
                        for (String option : q.getMcqOptions()) {
                            mcqOptions.put(option);
                        }
                        qObj.put("mcqOptions", mcqOptions);
                        questions.put(qObj);
                    }
                    quizObj.put("questions", questions);
                    lo.put("quiz", quizObj);
                }
                
                lessons.put(lo);
            }
            o.put("lessons", lessons);
            
            // Save course status
            o.put("status", c.getStatus().toString());

            JSONArray studs = new JSONArray();
            for (Student s : c.getStudents()) {
                JSONObject so = new JSONObject();
                so.put("userID", s.getuserID());
                so.put("username", s.getusername());
                so.put("email", s.getEmail());
                so.put("hashedPass", s.getHashedPass());
                studs.put(so);
            }
            o.put("students", studs);

            arr.put(o);
        }

        try {
            Files.writeString(Path.of(filePath), arr.toString(4), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveCourses(List<Course> courses) {
        JSONArray array = new JSONArray();
        for (Course c : courses) {
            JSONObject obj = new JSONObject();
            obj.put("courseId", c.getCourseId());
            obj.put("title", c.getTitle());
            obj.put("status", c.getStatus().toString());
            array.put(obj);
        }

        try {
            Files.write(Paths.get(COURSE_FILE), array.toString(4).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Student> readStudents(String filePath) {
        ArrayList<Student> students = new ArrayList<>();
        try {
            String content = Files.readString(Path.of(filePath), StandardCharsets.UTF_8);
            if (content == null || content.isBlank())
                return students;
            JSONArray arr = new JSONArray(content);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);

                String type = o.optString("type", "student");
                if (type.equals("student") || !o.has("type")) {
                    int userID = o.optInt("userID", 0);
                    String user = o.optString("username", null);
                    String email = o.optString("email", null);
                    String hashedPass = o.optString("hashedPass", null);
                    Student s = new Student(userID, user, email, hashedPass);

                    // Read enrolled courses - load full course data from Courses.json
                    if (o.has("enrolledCourses")) {
                        JSONArray enrolledCourses = o.getJSONArray("enrolledCourses");
                        // Load all courses to get full data
                        ArrayList<Course> allCourses = readCourses("src/test/java/JsonDBManager/Courses.json");
                        for (int j = 0; j < enrolledCourses.length(); j++) {
                            JSONObject courseObj = enrolledCourses.getJSONObject(j);
                            int courseId = courseObj.optInt("courseId", 0);
                            
                            // Find the full course data from all courses
                            Course fullCourse = null;
                            for (Course c : allCourses) {
                                if (c.getCourseId() == courseId) {
                                    fullCourse = c;
                                    break;
                                }
                            }
                            
                            // If full course found, use it; otherwise create basic course
                            if (fullCourse != null) {
                                s.getEnrolledCourses().add(fullCourse);
                            } else {
                                // Fallback: create course with basic info
                                String title = courseObj.optString("title", "");
                                String description = courseObj.optString("description", "");
                                String instructorId = courseObj.optString("instructorId", "");
                                Course course = new Course(courseId, title, description, instructorId);
                                s.getEnrolledCourses().add(course);
                            }
                        }
                    }

                    // Read progresses
                    if (o.has("progresses")) {
                        JSONArray progresses = o.getJSONArray("progresses");
                        for (int j = 0; j < progresses.length(); j++) {
                            JSONObject progressObj = progresses.getJSONObject(j);
                            int courseId = progressObj.optInt("courseId", 0);

                            // Find the corresponding course
                            Course course = null;
                            for (Course c : s.getEnrolledCourses()) {
                                if (c.getCourseId() == courseId) {
                                    course = c;
                                    break;
                                }
                            }

                            if (course != null) {
                                CourseManagement.Progress progress = new CourseManagement.Progress(s, course);

                                // Read completed lessons
                                if (progressObj.has("completedLessons")) {
                                    JSONArray completedLessons = progressObj.getJSONArray("completedLessons");
                                    for (int k = 0; k < completedLessons.length(); k++) {
                                        int lessonId = completedLessons.getInt(k);
                                        Lesson lesson = course.findLessonById(lessonId);
                                        if (lesson != null) {
                                            progress.getCompletedLessons().add(lesson);
                                        }
                                    }
                                }
                                
                                // Read quiz attempts
                                if (progressObj.has("quizAttempts")) {
                                    JSONObject attemptsObj = progressObj.getJSONObject("quizAttempts");
                                    // Use reflection to directly set quizAttempts to avoid duplicate lesson completions
                                    try {
                                        java.lang.reflect.Field field = CourseManagement.Progress.class.getDeclaredField("quizAttempts");
                                        field.setAccessible(true);
                                        @SuppressWarnings("unchecked")
                                        java.util.HashMap<Integer, ArrayList<Integer>> quizAttempts = 
                                            (java.util.HashMap<Integer, ArrayList<Integer>>) field.get(progress);
                                        
                                        for (String key : attemptsObj.keySet()) {
                                            try {
                                                int lessonId = Integer.parseInt(key);
                                                JSONArray attempts = attemptsObj.getJSONArray(key);
                                                ArrayList<Integer> attemptList = new ArrayList<>();
                                                for (int k = 0; k < attempts.length(); k++) {
                                                    attemptList.add(attempts.getInt(k));
                                                }
                                                quizAttempts.put(lessonId, attemptList);
                                                
                                                // Now properly mark lesson as complete if score >= 50
                                                Lesson lesson = course.findLessonById(lessonId);
                                                if (lesson != null) {
                                                    for (Integer score : attemptList) {
                                                        if (score >= 50) {
                                                            // Lesson passed, add to completed if not already there
                                                            if (!progress.getCompletedLessons().contains(lesson)) {
                                                                progress.getCompletedLessons().add(lesson);
                                                            }
                                                            break; // Only need one passing score
                                                        }
                                                    }
                                                }
                                            } catch (NumberFormatException e) {
                                                // Skip invalid keys
                                            }
                                        }
                                    } catch (Exception e) {
                                        // If reflection fails, fall back to addAttempt method
                                        e.printStackTrace();
                                        for (String key : attemptsObj.keySet()) {
                                            try {
                                                int lessonId = Integer.parseInt(key);
                                                JSONArray attempts = attemptsObj.getJSONArray(key);
                                                for (int k = 0; k < attempts.length(); k++) {
                                                    progress.addAttempt(lessonId, attempts.getInt(k));
                                                }
                                            } catch (NumberFormatException ex) {
                                                // Skip invalid keys
                                            }
                                        }
                                    }
                                }

                                s.getProgresses().add(progress);
                            }
                        }
                    }

                    if (o.has("certificates")) {
                        JSONArray certs = o.getJSONArray("certificates");
                        for (int j = 0; j < certs.length(); j++) {
                            JSONObject cObj = certs.getJSONObject(j);
                            String certId = cObj.optString("certificateId", "");
                            int sId = cObj.optInt("studentId", 0);
                            int cId = cObj.optInt("courseId", 0);
                            String cTitle = cObj.optString("courseTitle", "");
                            String date = cObj.optString("dateEarned", "");
                            s.addCertificate(new Certificate(certId, sId, cId, cTitle, date));
                        }
                    }
                    students.add(s);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return students;
    }

    public static void writeStudents(String filePath, ArrayList<Student> students) {
        ArrayList<Instructor> existingInstructors = readInstructors(filePath);

        JSONArray arr = new JSONArray();

        // Write students with type field, enrolled courses, and progresses
        for (Student s : students) {
            JSONObject o = new JSONObject();
            o.put("type", "student");
            o.put("userID", s.getuserID());
            o.put("username", s.getusername());
            o.put("email", s.getEmail());
            o.put("hashedPass", s.getHashedPass());

            // Add enrolled courses
            JSONArray enrolledCourses = new JSONArray();
            for (Course c : s.getEnrolledCourses()) {
                JSONObject courseObj = new JSONObject();
                courseObj.put("courseId", c.getCourseId());
                courseObj.put("title", c.getTitle());
                courseObj.put("description", c.getDescription());
                courseObj.put("instructorId", c.getInstructorId());
                enrolledCourses.put(courseObj);
            }
            o.put("enrolledCourses", enrolledCourses);

            // Add progresses
            JSONArray progresses = new JSONArray();
            for (CourseManagement.Progress p : s.getProgresses()) {
                JSONObject progressObj = new JSONObject();
                progressObj.put("courseId", p.getCourse().getCourseId());
                progressObj.put("percentage", p.getPercentage());
                progressObj.put("completed", p.courseCompletion());

                // Add completed lessons
                JSONArray completedLessons = new JSONArray();
                for (Lesson l : p.getCompletedLessons()) {
                    completedLessons.put(l.getLessonId());
                }
                progressObj.put("completedLessons", completedLessons);
                
                // Add quiz attempts - need to access the quizAttempts HashMap
                // Using reflection to access private field
                try {
                    java.lang.reflect.Field field = CourseManagement.Progress.class.getDeclaredField("quizAttempts");
                    field.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    java.util.HashMap<Integer, ArrayList<Integer>> quizAttempts = 
                        (java.util.HashMap<Integer, ArrayList<Integer>>) field.get(p);
                    if (quizAttempts != null && !quizAttempts.isEmpty()) {
                        JSONObject attemptsObj = new JSONObject();
                        for (java.util.Map.Entry<Integer, ArrayList<Integer>> entry : quizAttempts.entrySet()) {
                            JSONArray attempts = new JSONArray();
                            for (Integer score : entry.getValue()) {
                                attempts.put(score);
                            }
                            attemptsObj.put(String.valueOf(entry.getKey()), attempts);
                        }
                        progressObj.put("quizAttempts", attemptsObj);
                    }
                } catch (Exception e) {
                    // If reflection fails, skip quiz attempts
                    e.printStackTrace();
                }

                progresses.put(progressObj);
            }
            o.put("progresses", progresses);

            // Add certificates
            JSONArray certificates = new JSONArray();
            for (Certificate cert : s.getCertificates()) {
                JSONObject certObj = new JSONObject();
                certObj.put("certificateId", cert.getCertificateId());
                certObj.put("studentId", cert.getStudentId());
                certObj.put("courseId", cert.getCourseId());
                certObj.put("courseTitle", cert.getCourseTitle());
                certObj.put("dateEarned", cert.getDateEarned());
                certificates.put(certObj);
            }
            o.put("certificates", certificates);

            arr.put(o);
        }

        // Write instructors with type field
        for (Instructor i : existingInstructors) {
            JSONObject o = new JSONObject();
            o.put("type", "instructor");
            o.put("userId", i.getUserId());
            o.put("username", i.getUserName());
            o.put("email", i.getEmail());
            o.put("hashedPass", i.getHashedPass());
            arr.put(o);
        }

        try {
            Files.writeString(Path.of(filePath), arr.toString(4), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Instructor> readInstructors(String filePath) {
        ArrayList<Instructor> instructors = new ArrayList<>();
        try {
            String content = Files.readString(Path.of(filePath), StandardCharsets.UTF_8);
            if (content == null || content.isBlank())
                return instructors;
            JSONArray arr = new JSONArray(content);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                // Only read objects with type "instructor"
                String type = o.optString("type", "");
                if (type.equals("instructor")) {
                    int instructorId = o.optInt("userId", o.optInt("instructorId", 0));
                    String name = o.optString("username", null);
                    System.out.println("DEBUG: Read instructor " + name + " with ID: " + instructorId);
                    String email = o.optString("email", null);
                    String hashedPass = o.optString("hashedPass", null);
                    Instructor instructor = new Instructor(instructorId, name, email, hashedPass);

                    // Read created courses
                    if (o.has("createdCourses")) {
                        JSONArray createdCourses = o.getJSONArray("createdCourses");
                        for (int j = 0; j < createdCourses.length(); j++) {
                            JSONObject courseObj = createdCourses.getJSONObject(j);
                            int courseId = courseObj.optInt("courseId", 0);
                            String title = courseObj.optString("title", "");
                            String description = courseObj.optString("description", "");
                            String instrId = courseObj.optString("instructorId", "");
                            Course course = new Course(courseId, title, description, instrId);
                            instructor.getCourses().add(course);
                        }
                    }

                    instructors.add(instructor);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return instructors;
    }

    public static void writeInstructors(String filePath, ArrayList<Instructor> instructors) {
        // Read existing users to preserve students
        ArrayList<Student> existingStudents = readStudents(filePath);

        JSONArray arr = new JSONArray();

        // Write students with type field, enrolled courses, and progresses
        for (Student s : existingStudents) {
            JSONObject o = new JSONObject();
            o.put("type", "student");
            o.put("userID", s.getuserID());
            o.put("username", s.getusername());
            o.put("email", s.getEmail());
            o.put("hashedPass", s.getHashedPass());

            // Add enrolled courses
            JSONArray enrolledCourses = new JSONArray();
            for (Course c : s.getEnrolledCourses()) {
                JSONObject courseObj = new JSONObject();
                courseObj.put("courseId", c.getCourseId());
                courseObj.put("title", c.getTitle());
                courseObj.put("description", c.getDescription());
                courseObj.put("instructorId", c.getInstructorId());
                enrolledCourses.put(courseObj);
            }
            o.put("enrolledCourses", enrolledCourses);

            // Add progresses
            JSONArray progresses = new JSONArray();
            for (CourseManagement.Progress p : s.getProgresses()) {
                JSONObject progressObj = new JSONObject();
                progressObj.put("courseId", p.getCourse().getCourseId());
                progressObj.put("percentage", p.getPercentage());
                progressObj.put("completed", p.courseCompletion());

                // Add completed lessons
                JSONArray completedLessons = new JSONArray();
                for (Lesson l : p.getCompletedLessons()) {
                    completedLessons.put(l.getLessonId());
                }
                progressObj.put("completedLessons", completedLessons);
                
                // Add quiz attempts - need to access the quizAttempts HashMap
                // Using reflection to access private field
                try {
                    java.lang.reflect.Field field = CourseManagement.Progress.class.getDeclaredField("quizAttempts");
                    field.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    java.util.HashMap<Integer, ArrayList<Integer>> quizAttempts = 
                        (java.util.HashMap<Integer, ArrayList<Integer>>) field.get(p);
                    if (quizAttempts != null && !quizAttempts.isEmpty()) {
                        JSONObject attemptsObj = new JSONObject();
                        for (java.util.Map.Entry<Integer, ArrayList<Integer>> entry : quizAttempts.entrySet()) {
                            JSONArray attempts = new JSONArray();
                            for (Integer score : entry.getValue()) {
                                attempts.put(score);
                            }
                            attemptsObj.put(String.valueOf(entry.getKey()), attempts);
                        }
                        progressObj.put("quizAttempts", attemptsObj);
                    }
                } catch (Exception e) {
                    // If reflection fails, skip quiz attempts
                    e.printStackTrace();
                }

                progresses.put(progressObj);
            }
            o.put("progresses", progresses);

            // Add certificates
            JSONArray certificates = new JSONArray();
            for (Certificate cert : s.getCertificates()) {
                JSONObject certObj = new JSONObject();
                certObj.put("certificateId", cert.getCertificateId());
                certObj.put("studentId", cert.getStudentId());
                certObj.put("courseId", cert.getCourseId());
                certObj.put("courseTitle", cert.getCourseTitle());
                certObj.put("dateEarned", cert.getDateEarned());
                certificates.put(certObj);
            }
            o.put("certificates", certificates);

            arr.put(o);
        }

        // Write instructors with type field and created courses
        for (Instructor i : instructors) {
            JSONObject o = new JSONObject();
            o.put("type", "instructor");
            o.put("userId", i.getUserId());
            o.put("username", i.getUserName());
            o.put("email", i.getEmail());
            o.put("hashedPass", i.getHashedPass());

            // Add created courses
            JSONArray createdCourses = new JSONArray();
            for (Course c : i.getCourses()) {
                JSONObject courseObj = new JSONObject();
                courseObj.put("courseId", c.getCourseId());
                courseObj.put("title", c.getTitle());
                courseObj.put("description", c.getDescription());
                courseObj.put("instructorId", c.getInstructorId());
                createdCourses.put(courseObj);
            }
            o.put("createdCourses", createdCourses);

            arr.put(o);
        }

        try {
            Files.writeString(Path.of(filePath), arr.toString(4), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
