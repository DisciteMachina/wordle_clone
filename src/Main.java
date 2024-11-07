import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

// make the length of guess = to target word
public class Main {
    private static final String fileName = "resources/wordlist.txt";
    private static final List<String> words = loadWordsFromFile(fileName);
    private static final String TARGET_WORD = getTargetWord(words);
    private static final int MAX_ATTEMPTS = 6;
    private int attemptCount = 0;
    private JPanel gridPanel;
    private JTextField inputField;
    private JButton submitButton;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().createAndShowGUI());
        System.out.println(TARGET_WORD);
    }

    private static List<String> loadWordsFromFile(String fileName) {
        List<String> words = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                words.add(line.trim());
            }
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
        return words;
    }

    public static String getTargetWord(List<String> words) {
        Random random = new Random();
        return words.get(random.nextInt(words.size()));
    }

    public void createAndShowGUI() {
        JFrame frame  = new JFrame("Java Wordle");
        frame.setSize(400,600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        gridPanel = new JPanel(new GridLayout(MAX_ATTEMPTS, 5));
        inputField = new JTextField(5);
        submitButton = new JButton("Submit");

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String guess = inputField.getText().trim().toLowerCase();
                if (guess.length() != 5) {
                    JOptionPane.showMessageDialog(frame, "Please enter a 5 letter word.");
                    return;
                }

                if (attemptCount < MAX_ATTEMPTS) {
                    addGuessToGrid(guess);
                    if (guess.equals(TARGET_WORD)) {
                        JOptionPane.showMessageDialog(frame, "Congratulations! You guess the word!");
                        submitButton.setEnabled(false);
                    } else {
                        attemptCount ++;
                        if (attemptCount == MAX_ATTEMPTS) {
                            JOptionPane.showMessageDialog(frame, "Game over!");
                            submitButton.setEnabled(false);
                        }
                    }
                }

                inputField.setText("");
            }
        });
        JPanel inputPanel = new JPanel();
        inputPanel.add(inputField);
        inputPanel.add(submitButton);

        frame.add(gridPanel, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private void addGuessToGrid(String guess) {
        JPanel guessPanel = new JPanel(new GridLayout(1, 5));
        for (int i = 0; i < 5; i++) {
            JLabel letterLabel = new JLabel(String.valueOf(guess.charAt(i)), SwingConstants.CENTER);
            letterLabel.setOpaque(true);
            if (guess.charAt(i) == TARGET_WORD.charAt(i)) {
                letterLabel.setBackground(Color.GREEN); // Correct position
            } else if (TARGET_WORD.contains(String.valueOf(guess.charAt(i)))) {
                letterLabel.setBackground(Color.YELLOW); // Wrong position
            } else {
                letterLabel.setBackground(Color.GRAY); // Not in word
            }
            letterLabel.setFont(new Font("Arial", Font.BOLD, 24));
            letterLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            guessPanel.add(letterLabel);
        }
        gridPanel.add(guessPanel);
        gridPanel.revalidate();
        gridPanel.repaint();
    }
}
