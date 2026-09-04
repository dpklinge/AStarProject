package com.example.project1_202680itcs6150091;

import java.util.Map;

public class AbsoluteDistanceHeuristic implements HeuristicFunction{
    private Map<Integer, PuzzleNumber> puzzleSolution;

    public AbsoluteDistanceHeuristic(Map<Integer, PuzzleNumber> puzzleSolution) {
        this.puzzleSolution = puzzleSolution;
    }

    @Override
    public int getHValue(PuzzleNumber number) {
        PuzzleNumber solutionNumber = puzzleSolution.get(number.getNumber());
        return (int) Math.sqrt(Math.abs(solutionNumber.getX() - number.getX())^2 + Math.abs(solutionNumber.getY()-number.getY())^2);
    }
}
