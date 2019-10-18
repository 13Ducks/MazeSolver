package mazesolver;

import java.util.*;

public class MazeSolver {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        System.out.println("Enter width:");
        int width = sc.nextInt();
        System.out.println("Enter height:");
        int height = sc.nextInt();
        System.out.println(width + " " + height);

        // Maze will be stored in 2D array of Squares, each representing one tile
        // [height][width] so is more intuitive: increases first horizontally
        Square[][] maze = new Square[height][width];

        // Track start and end now so we do not have to loop through whole thing to find them later
        Square start = null, end = null;

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

                    maze[count][i] = tile;
                }
                count++;
            } else {
                // Don't error if invalid line input given so allows so copypasting file contents which may have had other text or comments
                System.out.println("Valid mazes can must have " + width + " characters per line and only consist of # (Wall), . (Open), o (Start), * (End)");
            }
        }
        
        displayMaze(maze);
        
        // Make sure maze has both a start and end
        if (start == null || end == null) throw new IllegalArgumentException("Valid mazes must have a start and an end!");
        solveMaze(maze, start, end, width, height);

        sc.close();
    }

    public static boolean validInput (String s, int width) {
        // Regex is checking if all characters are part of set [#.ox]
        return s.length() == width && s.matches("^[#.o*]+$");
    }

    public static void displayMaze (Square[][] maze) {
        for (Square[] row : maze) {
            for (Square item : row) {
                // toString defined for Square in Square class so no explicit calls needed
                System.out.print(item);
            }
            System.out.println();
        }
    }

    public static void solveMaze (Square[][] maze, Square start, Square end, int width, int height) {
        // Use a Queue so can do BFS rather than DFS - doing BFS allows for not having to keep track of distances
        // since every tile is reached in the minimum distance from start
        Queue<Square> queue = new LinkedList<>();
        queue.add(start);

        // HashSet since inserting into it is O(1) rather than ArrayList.contains() being O(n)
        HashSet<Square> seen = new HashSet<>();
        seen.add(start);

        boolean pathFound = false;

        // If queue is empty and path is not found, that means there is no possible path to end
        while (!queue.isEmpty()) {
            // Get first element of queue and remove it
            Square curr = queue.poll();
            int currX = curr.getX();
            int currY = curr.getY();

            /* Checks to make sure that will not cause ArrayIndexOutOfBounds exception, then get cells on one side of current one
               Make sure never have seen cell before to stop infinite loops and then add to queue and add to set of seen squares
               Set each squares adj to be which square led to it
            */

            if (currX > 0) {
                Square next = maze[currY][currX-1];
                if (next.isEnd()) {
                    next.setAdj(curr);
                    pathFound = true;
                    break;
                }
                if (next.isOpen() && seen.add(next)) {
                    next.setAdj(curr);
                    queue.add(next);
                }
            }

            if (currY > 0) {
                Square next = maze[currY-1][currX];
                if (next.isEnd()) {
                    next.setAdj(curr);
                    pathFound = true;
                    break;
                }
                if (next.isOpen() && seen.add(next)) {
                    next.setAdj(curr);
                    queue.add(next);
                }
            }

            if (currX < width-1) {
                Square next = maze[currY][currX+1];
                if (next.isEnd()) {
                    next.setAdj(curr);
                    pathFound = true;
                    break;
                }
                if (next.isOpen() && seen.add(next)) {
                    next.setAdj(curr);
                    queue.add(next);
                }
            }

            if (currY < height-1) {
                Square next = maze[currY+1][currX];
                if (next.isEnd()) {
                    next.setAdj(curr);
                    pathFound = true;
                    break;
                }
                if (next.isOpen() && seen.add(next)) {
                    next.setAdj(curr);
                    queue.add(next);
                }
            }
        }

        if (pathFound) {
            ArrayList<Square> path = findPath(end);
            // Nice way of printing path out, anything on the path is marked as "p" and anything else that is not the start or end is "_"
            for (Square[] row : maze) {
                for (Square item : row) {
                    if (item.isStart()) {
                        System.out.print("o");
                    } else if (item.isEnd()) {
                        System.out.print("*");
                    } else if (path.contains(item)) {
                        System.out.print("p");
                    } else {
                        System.out.print("_");
                    }
                }
                System.out.println();
            }
        } else {
            System.out.println("No path exists");
        }
    }

    public static ArrayList<Square> findPath(Square end) {
        // Backtracking from end: keep getting squares that led to current one until reach start
        ArrayList<Square> path = new ArrayList<>();
        path.add(end);
        Square adjSquare = end.getAdj();
        path.add(adjSquare);
        while (!adjSquare.isStart()) {
            adjSquare = adjSquare.getAdj();
            path.add(adjSquare);
        }

        return path;
    }
}
