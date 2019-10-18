package mazesolver;

/**
 Chose to make Square a class rather than an enum to be able to track various important variables
 Being able to track adj is very important for backtracking from end to start to find path
 Also having unique object references is very useful for working in sets and comparison
**/

public class Square {
    private int x, y;
    private char mark;
    private Square adj;

    public Square (char mark, int x, int y) {
        this.mark = mark;
        this.x = x;
        this.y = y;
    }

    public boolean isOpen () {
        return mark=='.';
    }

    public boolean isStart () {
        return mark=='o';
    }

    public boolean isEnd () {
        return mark=='*';
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }


    public String toString() {
        // Override toString method so it prints the correct character rather than the object reference
        return Character.toString(mark);
    }

    public Square getAdj() {
        return adj;
    }

    public void setAdj(Square adj) {
        this.adj = adj;
    }
}
