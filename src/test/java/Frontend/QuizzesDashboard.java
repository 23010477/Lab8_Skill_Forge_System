package Frontend;

import CourseManagement.Lesson;
import CourseManagement.Questions;
import CourseManagement.Quizzes;
import CourseManagement.Progress;
import Student.Student;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Enumeration;

public class QuizzesDashboard extends javax.swing.JFrame {
private static final java.util.logging.Logger logger =
        java.util.logging.Logger.getLogger(QuizzesDashboard.class.getName());

    private Lesson lesson;
    private Student student;
    private Progress progress;

    private ArrayList<ButtonGroup> answerGroups = new ArrayList<>();
    private JButton submitButton;

    
    public QuizzesDashboard() {
        initComponents(); 
    }

    
    public QuizzesDashboard(Lesson lesson, Student student, Progress progress) {
        this.lesson = lesson;
        this.student = student;
        this.progress = progress;

        initComponents();   
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        buildQuizUI();      
    }

    
    private void buildQuizUI() {

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        
        setTitle("Quiz: " + lesson.getTitle());
        setSize(600, 700);
        setLocationRelativeTo(null);

        
        getContentPane().removeAll();

       
        JTextArea contentArea = new JTextArea(lesson.getContent());
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);

        mainPanel.add(new JLabel("Lesson Content:"));
        mainPanel.add(new JScrollPane(contentArea));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        Quizzes quiz = lesson.getQuiz();

        if (quiz != null && !quiz.getQuestions().isEmpty()) {

            mainPanel.add(new JLabel("Quiz:"));

            ArrayList<Questions> questions = quiz.getQuestions();

            for (int i = 0; i < questions.size(); i++) {
                Questions q = questions.get(i);

                mainPanel.add(new JLabel((i + 1) + ". " + q.getQuestion()));

                ButtonGroup group = new ButtonGroup();
                answerGroups.add(group);

                for (int j = 0; j < q.getMcqOptions().size(); j++) {
                    JRadioButton optionBtn = new JRadioButton(q.getMcqOptions().get(j));
                    optionBtn.setActionCommand(String.valueOf(j));
                    group.add(optionBtn);
                    mainPanel.add(optionBtn);
                }

                mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }

            submitButton = new JButton("Submit Quiz");
            submitButton.addActionListener(this::handleSubmit);
            mainPanel.add(submitButton);
        }

        add(new JScrollPane(mainPanel));
        revalidate();
        repaint();
    }

    private void handleSubmit(ActionEvent e) {

    Quizzes quiz = lesson.getQuiz();
    ArrayList<Questions> questions = quiz.getQuestions();
    int score = 0;

    for (int i = 0; i < questions.size(); i++) {
        Questions q = questions.get(i);
        ButtonGroup group = answerGroups.get(i);

        if (group.getSelection() != null) {
            int selected = Integer.parseInt(group.getSelection().getActionCommand());
            if (q.checkAns(selected)) {
                score += 100 / questions.size();
            }
        }
    }

    progress.addAttempt(lesson.getLessonId(), score);

    
    StringBuilder result = new StringBuilder();
    result.append("Your Score: ").append(score).append("%\n\n");

    for (int i = 0; i < questions.size(); i++) {
        Questions q = questions.get(i);
        result.append((i + 1)).append(". ").append(q.getQuestion())
                .append("\nCorrect: ")
                .append(q.getMcqOptions().get(q.getIndexOfCorrectOption()))
                .append("\n\n");
    }

    JOptionPane.showMessageDialog(this, result.toString(), "Quiz Results", JOptionPane.INFORMATION_MESSAGE);

   
    submitButton.setEnabled(false);
    for (ButtonGroup group : answerGroups) {
        Enumeration<AbstractButton> btns = group.getElements();
        while (btns.hasMoreElements()) {
            btns.nextElement().setEnabled(false);
        }
    }

  
    if (!progress.getCompletedLessons().contains(lesson)) {
        progress.addCompletedLesson(lesson);         
            
    }
}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new QuizzesDashboard().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
