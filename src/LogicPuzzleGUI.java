import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LogicPuzzleGUI extends JFrame {

    // ================= BASIC DATA =================
    private String[] students = {"John", "Kate", "Liam", "Mia", "Noah"};
    private String[] tasks = {"Security", "Coding", "Cooking", "Cleaning", "Gardening"};
    private JLabel[][] cells = new JLabel[5][5];
    private JTextArea resultArea = new JTextArea();

    // ================= CONSTRUCTOR =================
    public LogicPuzzleGUI() {

        setTitle("Group Project CSC510 - Robot Logic Puzzle");
        setSize(900, 720);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        ImagePanel background = new ImagePanel("assets/background.png");
        setContentPane(background);

        JLabel title = new JLabel(
                "<html><center><b>Robot Logic Puzzle</b><br/>Discrete Structure – Propositional Logic</center></html>",
                SwingConstants.CENTER
        );
        title.setBounds(300, 10, 300, 40);
        background.add(title);

        ImagePanel hintPanel = new ImagePanel("assets/notebg.png");
        hintPanel.setBounds(20, 60, 300, 550);
        background.add(hintPanel);

        createGridContent(background);
        buttonControls(background);

        ImagePanel gridPanel = new ImagePanel("assets/columnbg.png");
        gridPanel.setBounds(370, 70, 530, 550);
        background.add(gridPanel);

        setVisible(true);
    }

    // ================= BUTTONS =================
    private void buttonControls(JPanel background) {

        JButton resetBtn = new JButton("Reset");
        resetBtn.setBounds(330, 595, 100, 60);
        resetBtn.addActionListener(e -> resetGame());
        background.add(resetBtn);

        JButton submitBtn = new JButton("Submit");
        submitBtn.setBounds(430, 595, 100, 60);
        submitBtn.addActionListener(e -> validateAnswer());
        background.add(submitBtn);

        resultArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(resultArea);
        scroll.setBounds(533, 595, 323, 60);
        background.add(scroll);
    }

    // ================= GRID =================
    private void createGridContent(JPanel parent) {

        for (int i = 0; i < students.length; i++) {

            ImagePanel avatar = new ImagePanel(getAvatarPath(i));
            avatar.setBounds(337, 120 + i * 95, 80, 80);
            parent.add(avatar);

            for (int j = 0; j < tasks.length; j++) {

                JLabel cell = new JLabel("", SwingConstants.CENTER);
                cells[i][j] = cell;

                cell.setBounds(330 + (j + 1) * 90, 120 + i * 95, 77, 75);
                cell.setFont(new Font("Arial", Font.BOLD, 32));
                cell.setOpaque(false);

                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        String cur = cell.getText();
                        if ("".equals(cur)) cell.setText("X");
                        else if ("X".equals(cur)) cell.setText("/");
                        else cell.setText("");
                    }
                });
                parent.add(cell);
            }
        }
    }

    // ================= AVATAR =================
    private String getAvatarPath(int index) {
        String[] paths = {
                "assets/avatar01-John.png",
                "assets/avatar03-Kate.png",
                "assets/avatar04-Liam.png",
                "assets/avatar05-Mia.png",
                "assets/avatar02-Noah.png"
        };
        return paths[index];
    }

    // ================= RESET =================
    private void resetGame() {
        resultArea.setText("");
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                cells[i][j].setText("");
                cells[i][j].setOpaque(false); // reset opaque
                cells[i][j].setBackground(null); // reset background colour
                cells[i][j].setBorder(null); // reset border
            }
        }
    }

    // ================= CHECK TRUE =================

    
    private boolean t(int r, int c) {
        return "/".equals(cells[r][c].getText());
    }

    private int countSelected(int row) {
        int count = 0;
        for (int j = 0; j < 5; j++)
            if (t(row, j)) count++;
        return count;
    }


    // ================= LOGIC ENGINE =================
    private boolean isValidRow(int r) {
        // Column Order:
        // 0 = Security, 1 = Coding, 2 = Cooking, 3 = Cleaning, 4 = Gardening

        // ===== LINKED CASES FOR JOHN & LIAM =====
        if (r == 0 || r == 2) {
             boolean caseA =
                // John: Coding + Cleaning
                t(0,1) && t(0,3) && !t(0,0) && !t(0,2) && !t(0,4) &&
                // Liam: Gardening + Coding
                t(2,4) && t(2,1) && !t(2,0) && !t(2,2) && !t(2,3);

            boolean caseB =
                // John: Coding + Gardening
                t(0,1) && t(0,4) && !t(0,0) && !t(0,2) && !t(0,3) &&
                // Liam: Gardening + Cleaning
                t(2,4) && t(2,3) && !t(2,0) && !t(2,1) && !t(2,2);

            return caseA || caseB;
        }

        // ===== KATE =====
        if (r == 1) {
            // Kate: Security + (Cooking or Cleaning)
            boolean k1 = t(1,0) && t(1,2) && !t(1,1) && !t(1,3) && !t(1,4);
            boolean k2 = t(1,0) && t(1,3) && !t(1,1) && !t(1,2) && !t(1,4);
            return k1 || k2;
        }

        // Mia
        if (r == 3) {
            return t(3,2) && t(3,3) && !t(3,0) && !t(3,1) && !t(3,4);
        }

        // Noah
        if (r == 4) {
            return t(4,0) && t(4,1) && !t(4,2) && !t(4,3) && !t(4,4);
        }

        return false;
    }

    // ================= VALIDATION & COLORING =================
    private void validateAnswer() {
        resultArea.setText("");
        boolean allCorrect = true;

        for (int i = 0; i < 5; i++) {

            // Only validate & colour when exactly 2 are selected
            if (countSelected(i) != 2) continue;

            boolean valid = isValidRow(i);
            allCorrect &= valid;

            for (int j = 0; j < 5; j++) {
                if (t(i,j)) {
                    cells[i][j].setOpaque(true);
                    cells[i][j].setBackground(
                        valid ? new Color(190,229,174)
                            : new Color(253,180,180)
                    );
                }
            }
        }

        if (allCorrect) 
            resultArea.append("✅ Logic constraints satisfied!");
        else 
            resultArea.append("❌ Some marks do not match logic.");
    }

    // ================= MAIN =================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(LogicPuzzleGUI::new);
    }
}
