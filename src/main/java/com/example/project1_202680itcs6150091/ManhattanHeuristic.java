package com.example.project1_202680itcs6150091;

import java.util.Map;

public class ManhattanHeuristic implements HeuristicFunction{
    private Map<Integer, PuzzleNumber> puzzleSolution;

    public ManhattanHeuristic(Map<Integer, PuzzleNumber> puzzleSolution) {
        this.puzzleSolution = puzzleSolution;
    }

    @Override
    public int getHValue(PuzzleNumber number) {
        PuzzleNumber solutionNumber = puzzleSolution.get(number.getNumber());
        return Math.abs(solutionNumber.getX() - number.getX()) + Math.abs(solutionNumber.getY()-number.getY());
    }
}
