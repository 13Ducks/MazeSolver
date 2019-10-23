import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.*;

public class GUI extends JFrame {
    private final int squareWidth;

    // With no bounds, squares will just draw at 0,0 with whatever width and height
    private Squares squares = new Squares();

    public GUI(int squareNumX, int squareNumY, Maze maze) {
        super("Maze Solver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Square[][] mazeArr = maze.getMaze();

        // Need 250 in y direction for all buttons to fit on screen
        // Squares need to all fit in 500 y for GUI to fit on whole computer screen nicely
        squareWidth = 500/squareNumY;

        // Need 570 in x direction for all tex tto fit on screen
        int frameWidth = Math.max(squareNumX * squareWidth, 570);

        int squareHeight = squareWidth;
        int frameHeight = 750;

        // Size the GUI will open at
        getContentPane().setPreferredSize(new Dimension(frameWidth,frameHeight));

        // Initial maze state, shows all tiles
        for (int i = 0; i < squareNumY; i++) {
            for (int j = 0; j < squareNumX; j++) {
                squares.addSquare(j * squareWidth, i * squareWidth, squareWidth, squareHeight, chooseColor(mazeArr[i][j]));
            }
        }

        // JLabel is basically just a line of text
        JLabel labelMode = new JLabel("Heuristic mode for A*");
        labelMode.setBounds(20, squareNumY*squareWidth + 20, 300, 20);
        // Add adds the element to the JFrame that the class is extended from
        add(labelMode);

        // JComboBox is a drop down box with the values of a string array
        // By starting array at None, that becomes the default option since it defaults to top
        // Chooses which heuristic mode should be used in A*
        String[] modes = {"None", "Euclidean", "Manhattan", "Proximity sensor"};

        JComboBox<String> cbMode = new JComboBox<>(modes);
        cbMode.setBounds(10, squareNumY*squareWidth + 50,160,20);
        add(cbMode);

        /*
         Option to use distance to start
         A* algorithm is f(x) = g(x) + h(x) where g(x) is distance to start, h(x) is estimated distance to end
         I find it works much better most of the time without adding h(x) so I added an option for that
         */

        JLabel labelDist = new JLabel("Should A* use distance to the start as part of heuristic?");
        labelDist.setBounds(205, squareNumY*squareWidth + 20, 400, 20);
        add(labelDist);

        String[] modeDist = {"No", "Yes"};

        JComboBox<String> cbDist = new JComboBox<>(modeDist);
        cbDist.setBounds(200, squareNumY*squareWidth + 50,160,20);
        add(cbDist);

        JLabel labelAnimate = new JLabel("Animation Speed (milliseconds per square)");
        labelAnimate.setBounds(20, squareNumY*squareWidth + 100, 300, 20);
        add(labelAnimate);

        // Slider for tick speed from 0-3000, I think anything higher would be too much
        JSlider slider = new JSlider(0, 3000, 500);
        // Only ticks every 500 so they don't intersect and look clean
        slider.setMajorTickSpacing(500);
        // Show ticks and labels
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setBounds(0, squareNumY*squareWidth + 100, 500, 100);
        add(slider);

        JButton startButton = new JButton("Start");

        // ActionListener detects if the button is pressed
        // Lambda e -> removes need for separate ActionListener class
        startButton.addActionListener(e -> {
            // the existing Maze class took an int, so I kept that
            String hMode = (String) cbMode.getSelectedItem();
            // No is as index 0, Yes is at index 1 -> if the index is 1 addDistance should be true
            maze.solveMaze(hMode.toLowerCase().charAt(0), cbDist.getSelectedIndex() == 1);
            ArrayList<Square> seenOrder = maze.getSeenOrder();

            // Use this instead of Thread.sleep because Swing components are not Thread-safe
            Timer timer = new Timer(slider.getValue(), new ActionListener() {
                // Start at i=1 so the start is not included and painted over
                int i = 1;
                public void actionPerformed(ActionEvent e) {
                    // No way to stop timer so we just keep it running and do nothing with it if exceeds array
                    // Using Thread.sleep and then Timer.stop() would pause the event dispatch thread, stopping the timer from running
                    if (i < seenOrder.size()) {
                        Square currAnimate = seenOrder.get(i);
                        squares.changeSeenColor(currAnimate.getX()*squareWidth, currAnimate.getY()*squareWidth);
                        i++;

                        // Force the repaint so the changed colors show
                        getContentPane().repaint();
                    }
                }
            });
            timer.start();
        });

        startButton.setBounds(20,  squareNumY*squareWidth + 200, 50, 20);

        add(startButton);

        // Add squares last to give them priority when drawing
        add(squares);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private Color chooseColor (Square s) {
        if (s.isTeleport()) return Color.RED;
        if (s.isOpen()) return Color.WHITE;
        if (s.isStart()) return Color.BLUE;
        if (s.isEnd()) return Color.GREEN;
        return Color.BLACK;
    }
}

// Squares will be part of separate JPanel on the JFrame
class Squares extends JPanel {
    // HashMap lets us update the color of a rectangle
    private HashMap<Rectangle, Color> squares = new HashMap<>();

    public void addSquare(int x, int y, int width, int height, Color color) {
        Rectangle rect = new Rectangle(x, y, width, height);
        squares.put(rect, color);
    }

    public void changeSeenColor (int x, int y) {
        // For every rectangle that is being changed, find it and change color
        for (Rectangle key : squares.keySet()) {
            if (key.x == x && key.y == y) {
                squares.replace(key, Color.GRAY);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        for (Rectangle key : squares.keySet()) {
            g2.setColor(squares.get(key));
            g2.fill(key);
        }
    }
}