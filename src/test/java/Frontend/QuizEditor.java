package Frontend;

import CourseManagement.Lesson;
import CourseManagement.Questions;
import CourseManagement.Quizzes;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class QuizEditor extends JDialog {
    private Lesson lesson;
    private Quizzes quiz;
    private DefaultListModel<String> questionsListModel;
    private JList<String> questionsList;
    private JTextField questionField;
    private JTextField[] optionFields;
    private ButtonGroup correctOptionGroup;
    private JRadioButton[] correctOptionRadios;

    public QuizEditor(Frame owner, Lesson lesson) {
        super(owner, "Manage Quiz for: " + lesson.getTitle(), true);
        this.lesson = lesson;
        this.quiz = lesson.getQuiz();
        if (this.quiz == null) {
            this.quiz = new Quizzes();
            lesson.setQuiz(this.quiz);
        }

        initComponents();
        loadQuestions();
        setSize(600, 500);
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Left Panel: List of Questions
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Questions"));
        questionsListModel = new DefaultListModel<>();
        questionsList = new JList<>(questionsListModel);
        leftPanel.add(new JScrollPane(questionsList), BorderLayout.CENTER);

        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.addActionListener(e -> deleteQuestion());
        leftPanel.add(deleteButton, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);

        // Center Panel: Add New Question
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Add/Edit Question"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Question Text
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        centerPanel.add(new JLabel("Question:"), gbc);
        gbc.gridy = 1;
        questionField = new JTextField();
        centerPanel.add(questionField, gbc);

        // Options
        optionFields = new JTextField[4];
        correctOptionRadios = new JRadioButton[4];
        correctOptionGroup = new ButtonGroup();

        for (int i = 0; i < 4; i++) {
            gbc.gridy++;
            gbc.gridwidth = 1;
            gbc.weightx = 0.0;
            correctOptionRadios[i] = new JRadioButton();
            correctOptionGroup.add(correctOptionRadios[i]);
            centerPanel.add(correctOptionRadios[i], gbc);

            gbc.gridx = 1;
            gbc.weightx = 1.0;
            optionFields[i] = new JTextField();
            centerPanel.add(optionFields[i], gbc);
            gbc.gridx = 0; // Reset for next row
        }
        correctOptionRadios[0].setSelected(true); // Default select first

        // Add Button
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JButton addButton = new JButton("Add Question");
        addButton.addActionListener(e -> addQuestion());
        centerPanel.add(addButton, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel: Save/Close
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        bottomPanel.add(closeButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadQuestions() {
        questionsListModel.clear();
        if (quiz != null && quiz.getQuestions() != null) {
            for (Questions q : quiz.getQuestions()) {
                questionsListModel.addElement(q.getQuestion());
            }
        }
    }

    private void addQuestion() {
        String questionText = questionField.getText().trim();
        if (questionText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Question text cannot be empty.");
            return;
        }

        ArrayList<String> options = new ArrayList<>();
        int correctIndex = -1;
        boolean allOptionsFilled = true;

        for (int i = 0; i < 4; i++) {
            String opt = optionFields[i].getText().trim();
            if (opt.isEmpty()) {
                allOptionsFilled = false;
                break;
            }
            options.add(opt);
            if (correctOptionRadios[i].isSelected()) {
                correctIndex = i;
            }
        }

        if (!allOptionsFilled) {
            JOptionPane.showMessageDialog(this, "All 4 options must be filled.");
            return;
        }

        Questions newQuestion = new Questions(questionText, options, correctIndex);
        quiz.addQuestion(newQuestion);

        // Clear fields
        questionField.setText("");
        for (JTextField f : optionFields)
            f.setText("");
        correctOptionRadios[0].setSelected(true);

        loadQuestions();
    }

    private void deleteQuestion() {
        int selectedIndex = questionsList.getSelectedIndex();
        if (selectedIndex != -1) {
            quiz.getQuestions().remove(selectedIndex);
            loadQuestions();
        }
    }
}
