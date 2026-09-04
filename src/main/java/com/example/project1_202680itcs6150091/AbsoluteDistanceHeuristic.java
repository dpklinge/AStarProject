package com.example.project1_202680itcs6150091;

import java.util.Map;

public class AbsoluteDistanceHeuristic implements HeuristicFunction{
    private Map<Integer, Coordinate> puzzleSolution;

    public AbsoluteDistanceHeuristic(Map<Integer, Coordinate> puzzleSolution) {
        this.puzzleSolution = puzzleSolution;
    }

    @Override
    public int getHValue(Coordinate location, Integer value) {
        Coordinate solutionLocation = puzzleSolution.get(value);
        return (int) Math.sqrt(Math.pow(solutionLocation.getX() - location.getX(), 2) + Math.pow(solutionLocation.getY()-location.getY(),2));
    }
}
