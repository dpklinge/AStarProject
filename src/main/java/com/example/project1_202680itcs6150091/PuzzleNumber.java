package com.example.project1_202680itcs6150091;

public class PuzzleNumber {
    private final int x;
    private final int y;
    private final int number;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getNumber() {
        return number;
    }


    public PuzzleNumber(int x, int y, int number) {
        this.x = x;
        this.y = y;
        this.number = number;
    }


    public PuzzleNumber(PuzzleNumber puzzleNumber){
        this.x =puzzleNumber.x;
        this.y = puzzleNumber.y;
        this.number = puzzleNumber.number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PuzzleNumber that = (PuzzleNumber) o;

        if (x != that.x) return false;
        if (y != that.y) return false;
        return number == that.number;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + number;
        return result;
    }
}
