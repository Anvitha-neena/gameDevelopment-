import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;

public class SudokuGame extends JFrame {

    private static final int SIZE = 9; // Size of Sudoku grid
    private JTextField[][] grid;
    private JButton playButton, resetButton, solutionButton;
    private Timer timer;
    private int seconds;
    private JLabel timerLabel;
    private JPanel controlPanel;
    private JButton closeButton, minimizeButton, maximizeButton;

    public SudokuGame() {
        setTitle("Sudoku Game");
        setLayout(new BorderLayout());
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setBackground(Color.LIGHT_GRAY);

        minimizeButton = new JButton("-");
        minimizeButton.addActionListener(e -> setState(JFrame.ICONIFIED)); 

        maximizeButton = new JButton("+");
        maximizeButton.addActionListener(e -> setExtendedState(JFrame.MAXIMIZED_BOTH));

        closeButton = new JButton("X");
        closeButton.addActionListener(e -> System.exit(0)); 

        controlPanel.add(minimizeButton);
        controlPanel.add(maximizeButton);
        controlPanel.add(closeButton);

        add(controlPanel, BorderLayout.NORTH);

        grid = new JTextField[SIZE][SIZE];
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(SIZE, SIZE));

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                grid[row][col] = new JTextField();
                grid[row][col].setHorizontalAlignment(JTextField.CENTER);
                grid[row][col].setFont(new Font("Arial", Font.PLAIN, 25)); 
                grid[row][col].setBorder(createCellBorder(row, col)); 
                grid[row][col].setBackground(Color.WHITE); 
                grid[row][col].setEditable(false);
                grid[row][col].addKeyListener(new KeyAdapter() { 
                    @Override
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();
                        if (!Character.isDigit(c) || c == '0') {
                            e.consume();
                        }
                    }
                });
                gridPanel.add(grid[row][col]);
            }
        }

        playButton = new JButton("Play");
        playButton.addActionListener(e -> startGame());

        resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> resetGame());

        solutionButton = new JButton("Solution");
        solutionButton.addActionListener(e -> solveSudoku());

        timerLabel = new JLabel("Time: 00:00");
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        JPanel controlButtonsPanel = new JPanel();
        controlButtonsPanel.setLayout(new FlowLayout());
        controlButtonsPanel.add(playButton);
        controlButtonsPanel.add(resetButton);
        controlButtonsPanel.add(solutionButton);
        controlButtonsPanel.add(timerLabel);

        add(gridPanel, BorderLayout.CENTER);
        add(controlButtonsPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void startGame() {
        startTimer();
        generateSudokuPuzzle();
    }

    private void startTimer() {
        seconds = 0;

        if (timer != null) {
            timer.stop();
        }

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seconds++;
                int minutes = seconds / 60;
                int remainingSeconds = seconds % 60;
                timerLabel.setText(String.format("Time: %02d:%02d", minutes, remainingSeconds));
            }
        });
        timer.start();
    }

    private void generateSudokuPuzzle() {
        int[][] puzzle = generateSudokuBoard();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                String text = puzzle[row][col] == 0 ? "" : String.valueOf(puzzle[row][col]);
                grid[row][col].setText(text);
             
                grid[row][col].setEditable(puzzle[row][col] == 0);

                if (puzzle[row][col] == 0) {
                    grid[row][col].setBackground(Color.WHITE);  
                } else {
                    grid[row][col].setBackground(Color.LIGHT_GRAY);  
                }
            }
        }
    }

    private int[][] generateSudokuBoard() {
      
        return new int[][] {
            {5, 3, 0, 0, 7, 0, 0, 0, 0},
            {6, 0, 0, 1, 9, 5, 0, 0, 0},
            {0, 9, 8, 0, 0, 0, 0, 6, 0},
            {8, 0, 0, 0, 6, 0, 0, 0, 3},
            {4, 0, 0, 8, 0, 3, 0, 0, 1},
            {7, 0, 0, 0, 2, 0, 0, 0, 6},
            {0, 6, 0, 0, 0, 0, 2, 8, 0},
            {0, 0, 0, 4, 1, 9, 0, 0, 5},
            {0, 0, 0, 0, 8, 0, 0, 7, 9}
        };
    }

    private void solveSudoku() {
        int[][] puzzle = generateSudokuBoard();
        if (solveSudokuRecursive(puzzle)) {
         
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    grid[row][col].setText(puzzle[row][col] == 0 ? "" : String.valueOf(puzzle[row][col]));
                    grid[row][col].setEditable(false); 
                }
            }
        }
    }

    private boolean solveSudokuRecursive(int[][] board) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == 0) {
                    for (int num = 1; num <= 9; num++) {
                        if (isValid(board, row, col, num)) {
                            board[row][col] = num;
                            if (solveSudokuRecursive(board)) {
                                return true;
                            }
                            board[row][col] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true; 
    }

    private boolean isValid(int[][] board, int row, int col, int num) {
       
        for (int i = 0; i < SIZE; i++) {
            if (board[row][i] == num) {
                return false;
            }
        }

       
        for (int i = 0; i < SIZE; i++) {
            if (board[i][col] == num) {
                return false;
            }
        }

        int startRow = row / 3 * 3;
        int startCol = col / 3 * 3;
        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (board[i][j] == num) {
                    return false;
                }
            }
        }

        return true;
    }

    private void resetGame() {
        if (timer != null) {
            timer.stop();
        }
        timerLabel.setText("Time: 00:00");

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                grid[row][col].setText("");
                grid[row][col].setEditable(true); 
            }
        }
    }

    private Border createCellBorder(int row, int col) {
        Color borderColor = Color.DARK_GRAY; 

        if ((row % 3 == 0) && (col % 3 == 0)) {
            return BorderFactory.createMatteBorder(3, 3, 1, 1, borderColor); 
        } else if ((row % 3 == 2) && (col % 3 == 2)) {
            return BorderFactory.createMatteBorder(1, 1, 3, 3, borderColor); 
        } else if (row % 3 == 0) {
            return BorderFactory.createMatteBorder(3, 1, 1, 1, borderColor);
        } else if (col % 3 == 0) {
            return BorderFactory.createMatteBorder(1, 3, 1, 1, borderColor); 
        } else {
            return BorderFactory.createMatteBorder(1, 1, 1, 1, borderColor); 
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SudokuGame());
    }
}
