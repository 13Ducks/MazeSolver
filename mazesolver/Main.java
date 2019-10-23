import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter width:");
        int width = sc.nextInt();
        System.out.println("Enter height:");
        int height = sc.nextInt();
        System.out.println(width + " " + height);

        // Maze will be stored in 2D array of Squares, each representing one tile
        // [height][width] so is more intuitive: increases first horizontally
        Square[][] mazeArr = new Square[height][width];

        // Track start and end now so we do not have to loop through whole thing to find them later
        Square start = null, end = null;

        // Store teleporter locations if the maze has them
        ArrayList<Square> teleporters = new ArrayList<>();

        System.out.println("Now enter your maze:");
        int count = 0;
        // While loop instead of for loop so incorrect inputs can be entered without increasing the counter
        while (count < height) {
            String input = sc.next();
            if (validInput(input, width)) {
                // Need to make into char array so we can process each one individually and make into different squares
                char[] inputArr = input.toCharArray();
                for (int i = 0; i < width; i++) {
                    // Create new Square object for every tile with correct x,y positions
                    // x=0, y=0 is top left corner
                    Square tile = new Square(inputArr[i], i, count);

                    // Make sure maze only has one start and one end
                    if (tile.isStart()) {
                        if (start == null) {
                            start = tile;
                        } else {
                            throw new IllegalArgumentException("Valid mazes can only have one start!");
                        }
                    }

                    if (tile.isEnd()) {
                        if (end == null) {
                            end = tile;
                        } else {
                            throw new IllegalArgumentException("Valid mazes can only have one end!");
                        }
                    }

                    if (tile.isTeleport()) {
                        teleporters.add(tile);
                    }

                    mazeArr[count][i] = tile;
                }
                count++;
            } else {
                // Don't error if invalid line input given so allows so copypasting file contents which may have had other text or comments
                System.out.println("Valid mazes can must have " + width + " characters per line and only consist of # (Wall), . (Open), o (Start), * (End), @ (Teleporter)");
            }
        }

        // Create the maze object with all information needed to find solution
        Maze maze = new Maze(mazeArr, start, end, width, height, teleporters);
        maze.validMaze();

        // Create GUI with the maze, allows it to start the maze solve and draw it
        new GUI(width, height, maze);

        // Stop memory leak by closing scanners
        sc.close();
    }

    public static boolean validInput (String s, int width) {
        // Regex is checking if all characters are part of set [#.ox@]
        return s.length() == width && s.matches("^[#.o*@]+$");
    }
}