package com.example.project1_202680itcs6150091;

import javafx.util.Pair;

import java.io.IOException;
import java.util.*;

public class EightPuzzle {
    private final HeuristicFunction heuristicFunction;

    public EightPuzzle(HeuristicFunction heuristicFunction) {
        this.heuristicFunction = heuristicFunction;
    }

    private int getTotalEstimatedH(Integer[][] puzzle){
        int total = 0;
        for(int i =0; i<3;i++){
            for(int j=0;j<3;j++) {
                if(puzzle[i][j]!=0) {
                    total += heuristicFunction.getHValue(new Coordinate(j,i), puzzle[i][j]);
                }
            }
        }
        return total;
    }

    public Integer[][] solvePuzzle(Integer[][] initialState){
        System.out.println("Beginning puzzle: \n"+stringifyPuzzle(initialState));
        int initialHValue = getTotalEstimatedH(initialState);
        //Pair<Integer, Integer> is (gValue, hValue), AKA (distance travelled, heuristic estimate of distance remaining)
        PriorityQueue<Pair<Pair<Integer, Integer>, Integer[][]>> frontier = new PriorityQueue<>(Comparator.comparingInt(pair -> (pair.getKey().getKey() + pair.getKey().getValue())));
        frontier.add(new Pair<>(new Pair<>(0, initialHValue), initialState));
        HashSet<String> visitedStates = new HashSet<>();
        Map<String, Integer> bestF = new HashMap<>();
        while(!frontier.isEmpty() ) {

            Pair<Pair<Integer, Integer>, Integer[][]> state = frontier.poll();
            System.out.println("Considering state:\n"+stringifyPuzzle(state.getValue()));



            //If h is zero, we're there
            if(state.getKey().getValue() == 0){
                System.out.println("Goal achieved in "+state.getKey().getKey()+" steps");
                return state.getValue();
            }
            List<Pair<Pair<Integer, Integer>, Integer[][]>> possibleMoves = getPossibleStates(state.getValue(), state.getKey().getKey());
            for(Pair<Pair<Integer, Integer>, Integer[][]> pair : possibleMoves){
                String stringState = stringifyPuzzle(pair.getValue());
                int fValue = pair.getKey().getKey()+pair.getKey().getValue();

                if (!bestF.containsKey(stringState) || fValue < bestF.get(stringState)) {
                    bestF.put(stringState, fValue);
                    frontier.add(pair);
                }
            }
        }
        return initialState;
    }

    private List<Pair<Pair<Integer, Integer>, Integer[][]>> getPossibleStates(Integer[][] state, int distanceTravelled) {
        Coordinate emptyPosition = findEmptyPosition(state);
        List<Pair<Pair<Integer, Integer>, Integer[][]>> options = new ArrayList<>();

        if(emptyPosition.getX()-1>=0 ){
            Integer[][] newState = moveTile(state, new Coordinate(emptyPosition.getX()-1,emptyPosition.getY()), emptyPosition);
            options.add(new Pair<>(new Pair<>(distanceTravelled + 1, getTotalEstimatedH(newState)), newState));
        }
        if(emptyPosition.getX()+1<=2){
            Integer[][] newState = moveTile(state, new Coordinate(emptyPosition.getX()+1,emptyPosition.getY()), emptyPosition);
            options.add(new Pair<>(new Pair<>(distanceTravelled + 1, getTotalEstimatedH(newState)), newState));
        }
        if(emptyPosition.getY()-1>=0){
            Integer[][] newState = moveTile(state, new Coordinate(emptyPosition.getX(),emptyPosition.getY()-1), emptyPosition);
            options.add(new Pair<>(new Pair<>(distanceTravelled + 1, getTotalEstimatedH(newState)), newState));
        }
        if(emptyPosition.getY()+1<=2){
            Integer[][] newState = moveTile(state, new Coordinate(emptyPosition.getX(),emptyPosition.getY()+1), emptyPosition);
            options.add(new Pair<>(new Pair<>(distanceTravelled + 1, getTotalEstimatedH(newState)), newState));
        }

        return options;
    }

    private Integer[][] moveTile(Integer[][] state, Coordinate number, Coordinate emptyPosition) {
        Integer[][] copy = deepCopyArray(state);
        copy[emptyPosition.getY()][emptyPosition.getX()] = state[number.getY()][number.getX()];
        copy[number.getY()][number.getX()] = 0;
        return copy;
    }

    public static Integer[][] deepCopyArray(Integer[][] original) {
        if (original == null) return null;
        Integer[][] copy = new Integer[original.length][];
        for (int i = 0; i < original.length; i++) {
            if (original[i] != null) {
                copy[i] = new Integer[original[i].length];
                for (int j = 0; j < original[i].length; j++) {
                    if (original[i][j] != null) {
                        copy[i][j] = original[i][j];
                    }
                }
            }
        }
        return copy;
    }

    private Coordinate findEmptyPosition(Integer[][] puzzle) {
        for(int i =0; i<3;i++){
            for(int j=0;j<3;j++) {
                if(puzzle[i][j]==0) {
                   return new Coordinate(j, i);
                }
            }
        }
        throw new IllegalArgumentException("No empty space located in puzzle");
    }

    public static void main(String... args) throws IOException {
        Map<Integer, Coordinate> solutionMap = new HashMap<>();
        solutionMap.put(1, new Coordinate(0,0));
        solutionMap.put(2, new Coordinate(1,0));
        solutionMap.put(3, new Coordinate(2,0));
        solutionMap.put(4, new Coordinate(0,1));
        solutionMap.put(5, new Coordinate(1,1));
        solutionMap.put(6, new Coordinate(2,1));
        solutionMap.put(7, new Coordinate(0,2));
        solutionMap.put(8, new Coordinate(1,2));

        HeuristicFunction manhattanHeuristic = new ManhattanHeuristic(solutionMap);
        EightPuzzle manhattanEightPuzzle = new EightPuzzle(manhattanHeuristic);
        Integer[][] puzzle = PuzzleParser.readMatrix("Test1.txt");
        manhattanEightPuzzle.solvePuzzle(puzzle);


    }

    public String stringifyPuzzle(Integer[][] puzzle) {
        StringBuilder result = new StringBuilder();
        for (Integer[] puzzleNumbers : puzzle) {
            for (Integer number: puzzleNumbers) {
                result.append(number);
                result.append(" ");
            }
            result.append("\n");
        }
        return result.toString();
    }

}
