package JsonDBManager;

import CourseManagement.Course;
import CourseManagement.Lesson;
import Student.Student;
import InstructorManagement.Instructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class JsonDBManager {

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
                        c.addLesson(new Lesson(lessonId, ltitle, lcontent));
                    }
                }

                if (o.has("students")) {
                    JSONArray studs = o.getJSONArray("students");
                    for (int j = 0; j < studs.length(); j++) {
                        // student may be object or primitive id
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
                lessons.put(lo);
            }
            o.put("lessons", lessons);

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

    public static ArrayList<Student> readStudents(String filePath) {
        ArrayList<Student> students = new ArrayList<>();
        try {
            String content = Files.readString(Path.of(filePath), StandardCharsets.UTF_8);
            if (content == null || content.isBlank())
                return students;
            JSONArray arr = new JSONArray(content);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                // Only read objects with type "student" or no type (for backward compatibility)
                String type = o.optString("type", "student");
                if (type.equals("student") || !o.has("type")) {
                    int userID = o.optInt("userID", 0);
                    String user = o.optString("username", null);
                    String email = o.optString("email", null);
                    String hashedPass = o.optString("hashedPass", null);
                    Student s = new Student(userID, user, email, hashedPass);
                    students.add(s);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return students;
    }

    public static void writeStudents(String filePath, ArrayList<Student> students) {
        // Read existing users to preserve instructors
        ArrayList<Instructor> existingInstructors = readInstructors(filePath);

        JSONArray arr = new JSONArray();

        // Write students with type field
        for (Student s : students) {
            JSONObject o = new JSONObject();
            o.put("type", "student");
            o.put("userID", s.getuserID());
            o.put("username", s.getusername());
            o.put("email", s.getEmail());
            o.put("hashedPass", s.getHashedPass());
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
                    int instructorId = o.optInt("instructorId", 0);
                    String name = o.optString("username", null);
                    String email = o.optString("email", null);
                    String hashedPass = o.optString("hashedPass", null);
                    Instructor instructor = new Instructor(instructorId, name, email, hashedPass);
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

        // Write students with type field
        for (Student s : existingStudents) {
            JSONObject o = new JSONObject();
            o.put("type", "student");
            o.put("userID", s.getuserID());
            o.put("username", s.getusername());
            o.put("email", s.getEmail());
            o.put("hashedPass", s.getHashedPass());
            arr.put(o);
        }

        // Write instructors with type field
        for (Instructor i : instructors) {
            JSONObject o = new JSONObject();
            o.put("type", "instructor");
            o.put("instructorId", i.getUserId());
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

}
