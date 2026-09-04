package com.example.project1_202680itcs6150091;

import java.util.Map;

/*
Manhattan distance - number of left/right up/down movements to get to the target
 */
public class ManhattanHeuristic implements HeuristicFunction{
    private Map<Integer, Coordinate> puzzleSolution;

    public ManhattanHeuristic(Map<Integer, Coordinate> puzzleSolution) {
        this.puzzleSolution = puzzleSolution;
    }

    @Override
    public int getHValue(Coordinate coordinate, Integer value) {
        Coordinate solutionNumber = puzzleSolution.get(value);
        return Math.abs(solutionNumber.getX() - coordinate.getX()) + Math.abs(solutionNumber.getY()-coordinate.getY());
    }
}
