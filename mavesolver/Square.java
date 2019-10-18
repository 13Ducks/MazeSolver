package mazesolver;

/**
 Chose to make Square a class rather than an enum to be able to track various important variables
 Being able to track adj is very important for backtracking from end to start to find path
 Distance is important for A* and also unique object references for the HashSet
 **/

public class Square implements Comparable<Square> {
    private int x, y, distance, heuristic;
    private char mark;
    private Square adj;

    public Square (char mark, int x, int y) {
        this.mark = mark;
        this.x = x;
        this.y = y;
    }

    public boolean isOpen () {
        // You do not have to teleport if on a teleporter, you can pass through it
        return mark=='.' || mark == '@';
    }

    public boolean isStart () {
        return mark=='o';
    }

    public boolean isEnd () {
        return mark=='*';
    }

    public boolean isTeleport () {
        return mark=='@';
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Square getAdj() {
        return adj;
    }

    public void setAdj(Square adj) {
        this.adj = adj;
    }

    public void setHeuristic(int heuristic) {
        this.heuristic = heuristic;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public String toString() {
        // Override toString method so it prints the correct character rather than the object reference
        return Character.toString(mark);
    }

    public int compareTo(Square o) {
        // used in PriorityQueue to order elements, the lower the heuristic the higher the priority
        return this.heuristic - o.heuristic;
    }
}
