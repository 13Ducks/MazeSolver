package mazesolver;

import java.util.*;

public class Maze {
    private Square[][] maze;
    private Square start, end;
    private int width, height;
    private ArrayList<Square> teleporters;

    public Maze (Square[][] maze, Square start, Square end, int width, int height, ArrayList<Square> teleporters) {
        this.maze = maze;
        this.start = start;
        this.end = end;
        this.width = width;
        this.height = height;
        this.teleporters = teleporters;
    }

    public void validMaze () {
        // Make sure maze has both a start and end
        if (start == null || end == null) throw new IllegalArgumentException("Valid mazes must have a start and an end!");
        // There can only be 0 or 2 teleporters
        if (!(teleporters.size() == 0 || teleporters.size() == 2)) throw new IllegalArgumentException("Valid mazes only have 0 or 2 teleporters!");
    }

    public void displayMaze () {
        for (Square[] row : maze) {
            for (Square item : row) {
                // toString defined for Square in Square class so no explicit calls needed
                System.out.print(item);
            }
            System.out.println();
        }
    }

    private int chooseHeuristic(Square next, char mode) {
        /*
        mode = 'e': Euclidean distance from current tile to end
        mode = 'm': Manhattan distance from current tile to end
        mode = 'p': Proximity sensor -> Manhattan distance if lower than 8, 8 if higher
        anything else: 0
         */
        int diffX = Math.abs(next.getX() - end.getX());
        int diffY = Math.abs(next.getY() - end.getY());
        if (mode == 'e') {
            return (int) Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));
        } else if (mode == 'm') {
            return diffX + diffY;
        } else if (mode == 'p') {
            return Math.min(diffX + diffY, 8);
        } else {
            return 0;
        }
    }

    public void solveMaze () {
        // PriorityQueue allows use of heuristics to guess which tiles will lead to the end, and therefore should be explored earlier

        PriorityQueue<Square> queue = new PriorityQueue<>();
        queue.add(start);
        start.setDistance(0);

        // HashSet since inserting into it is O(1) rather than ArrayList.contains() being O(n)
        HashSet<Square> seen = new HashSet<>();
        seen.add(start);

        boolean pathFound = false;

        // Chooses mode that will be used in calls to chooseHeuristic()
        Scanner scsolve = new Scanner(System.in);
        System.out.println("Choose a heuristic mode");
        System.out.println("'e': Euclidean, 'm': Manhattan, 'p': Proximity sensor, anything else: 0");
        char hMode = scsolve.nextLine().charAt(0);
        scsolve.close();

        /*
        If a heuristic mode is selected, this may not try all values leading to potentially not finding the shortest path
        Example:
        .............
        .@.o.....*.@.
        .............
        with any heuristic mode selected it will go across rather than using the teleporter
        */

        if (!teleporters.isEmpty() && new HashSet<>(Arrays.asList('e', 'm', 'p')).contains(hMode)) {
            System.out.println("Attention! Your maze has teleporters and you have selected a heuristic. Keep in mind this may result in the shortest path not being found");
        }

        int count = 0;
        // If queue is empty and path is not found, that means there is no possible path to end

        while (!queue.isEmpty()) {
            // Get first element of queue and remove it
            Square curr = queue.poll();
            int currX = curr.getX();
            int currY = curr.getY();

            // Count has no functional use except to see if the heuristics are working better than having none
            count++;

            /*
            Checks to make sure that will not cause ArrayIndexOutOfBounds exception, then get cells on one side of current one
            Make sure never have seen cell before to stop infinite loops and then add to queue and add to set of seen squares
            Set each squares adj to be which square led to it
            A* Algorithm: Set the squares heuristic value to be the distance from start plus the estimated distance to end
            */

            //TODO: Add tie breaker for heuristics

            // LEFT
            if (currX > 0) {
                Square next = maze[currY][currX-1];
                if (next.isEnd()) {
                    next.setAdj(curr);
                    pathFound = true;
                    break;
                }
                if (next.isOpen() && seen.add(next)) {
                    next.setAdj(curr);
                    next.setDistance(curr.getDistance()+1);
                    next.setHeuristic(chooseHeuristic(next, hMode) + next.getDistance());
                    queue.add(next);
                }
            }

            // UP
            if (currY > 0) {
                Square next = maze[currY-1][currX];
                if (next.isEnd()) {
                    next.setAdj(curr);
                    pathFound = true;
                    break;
                }
                if (next.isOpen() && seen.add(next)) {
                    next.setAdj(curr);
                    next.setDistance(curr.getDistance()+1);
                    next.setHeuristic(chooseHeuristic(next, hMode) + next.getDistance());
                    queue.add(next);
                }
            }

            // RIGHT
            if (currX < width-1) {
                Square next = maze[currY][currX+1];
                if (next.isEnd()) {
                    next.setAdj(curr);
                    pathFound = true;
                    break;
                }
                if (next.isOpen() && seen.add(next)) {
                    next.setAdj(curr);
                    next.setDistance(curr.getDistance()+1);
                    next.setHeuristic(chooseHeuristic(next, hMode) + next.getDistance());
                    queue.add(next);
                }
            }

            // DOWN
            if (currY < height-1) {
                Square next = maze[currY+1][currX];
                if (next.isEnd()) {
                    next.setAdj(curr);
                    pathFound = true;
                    break;
                }
                if (next.isOpen() && seen.add(next)) {
                    next.setAdj(curr);
                    next.setDistance(curr.getDistance()+1);
                    next.setHeuristic(chooseHeuristic(next, hMode) + next.getDistance());
                    queue.add(next);
                }
            }

            // TELEPORTER
            // Make sure that the teleporter list has a size of 2, meaning no teleporter has been seen
            if (curr.isTeleport() && teleporters.size() > 1) {
                // Remove seen teleporter so the tiles cannot keep jumping back to each other and making an infinite loop
                teleporters.remove(curr);
                // Teleporter goes directly to other teleporter
                Square next = teleporters.get(0);
                if (seen.add(next)) {
                    next.setAdj(curr);
                    next.setHeuristic(chooseHeuristic(next, hMode));
                    queue.add(next);
                }
            }
        }

        if (pathFound) {
            ArrayList<Square> path = findPath();
            // path includes the start so subtract 1 to not include that
            System.out.println("The solution was " + (path.size()-1) + " tiles long! (includes end)");
            // Nice way of printing path out, anything on the path is marked as "p" and anything else that is not the start, end or teleporter is "_"
            for (Square[] row : maze) {
                for (Square item : row) {
                    if (item.isStart()) {
                        System.out.print("o");
                    } else if (item.isEnd()) {
                        System.out.print("*");
                    } else if (item.isTeleport()) {
                        System.out.print("@");
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

        System.out.println("There were " + count + " tiles searched");
    }

    private ArrayList<Square> findPath() {
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