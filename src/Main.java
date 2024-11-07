import javax.swing.*;
import java.util.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List; // why does this have to be specially imported??

// make the length of guess = to target word
public class Main {
    private static final String fileName = "resources/wordlist.txt";
    private static final List<String> words = loadWordsFromFile(fileName);
    private static final String TARGET_WORD = getTargetWord(words).toUpperCase();
    private static final int length = TARGET_WORD.length();
    private static final int MAX_ATTEMPTS = 6;
    private int attemptCount = 0;
    private JPanel gridPanel;
    private JTextField inputField;
    private JButton submitButton;
    private JLabel displayLength;

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
        frame.setSize(600,500);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        displayLength = new JLabel("The length of the word is: " + length);
        gridPanel = new JPanel(new GridLayout(MAX_ATTEMPTS, length));
        inputField = new JTextField(length);
        submitButton = new JButton("Submit");

        ((AbstractDocument) inputField.getDocument()).setDocumentFilter(new AlphabeticDocumentFilter());

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String guess = inputField.getText().trim().toUpperCase();
                if (guess.length() != length) {
                    JOptionPane.showMessageDialog(frame, "Please enter a " + length + " letter word.");
                    return;
                }

                if (attemptCount < MAX_ATTEMPTS) {
                    addGuessToGrid(guess);
                    if (guess.equals(TARGET_WORD)) {
                        JOptionPane.showMessageDialog(frame, "Congratulations! You guessed the word!");
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
        inputPanel.add(displayLength);

        frame.add(gridPanel, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private void addGuessToGrid(String guess) {
        JPanel guessPanel = new JPanel(new GridLayout(1, 5));
        for (int i = 0; i < length; i++) {
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


// ONLY [A-Z]
// I do not know how this works
class AlphabeticDocumentFilter extends DocumentFilter {
    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (isAlphabetic(string)) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (isAlphabetic(text)) {
            super.replace(fb, offset, length, text, attrs);
        }
    }

    private boolean isAlphabetic(String text) {
        return text != null && text.matches("[a-zA-Z]+");
    }
}
