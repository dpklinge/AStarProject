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
        Map<String, Integer> bestG = new HashMap<>();
        Pair<Pair<Integer, Integer>, Integer[][]> latestState=null;
        while(!frontier.isEmpty() ) {
            latestState = frontier.poll();

            //If h is zero, we're there
            if(latestState.getKey().getValue() == 0){
                System.out.println("Final state: \n"+ stringifyPuzzle(latestState.getValue()));
                System.out.println("Goal achieved in "+latestState.getKey().getKey()+" steps\n");
                return latestState.getValue();
            }
            List<Pair<Pair<Integer, Integer>, Integer[][]>> possibleMoves = getPossibleStates(latestState.getValue(), latestState.getKey().getKey());
            for(Pair<Pair<Integer, Integer>, Integer[][]> pair : possibleMoves){
                String stringState = stringifyPuzzle(pair.getValue());
                int gValue = pair.getKey().getKey();

                if (!bestG.containsKey(stringState) || gValue < bestG.get(stringState)) {
                    bestG.put(stringState, gValue);
                    frontier.add(pair);
                }
            }
        }
        System.out.println("Goal unachievable- stopped at state:\n"+stringifyPuzzle(latestState.getValue()));
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
        Map<Integer, Coordinate> solutionMap = PuzzleParser.generateSolutionMap("TestFiles/DefaultSolution.txt");

        HeuristicFunction manhattanHeuristic = new ManhattanHeuristic(solutionMap);
        EightPuzzle manhattanEightPuzzle = new EightPuzzle(manhattanHeuristic);

        HeuristicFunction absoluteDistance = new AbsoluteDistanceHeuristic(solutionMap);
        EightPuzzle absoluteDistanceEightPuzzle = new EightPuzzle(absoluteDistance);

        Integer[][] test1 = PuzzleParser.readFile("TestFiles/Test1.txt");
        Integer[][] test2 = PuzzleParser.readFile("TestFiles/Test2.txt");
        Integer[][] test3 = PuzzleParser.readFile("TestFiles/Test3.txt");
        Integer[][] test4 = PuzzleParser.readFile("TestFiles/Test4.txt");
        List<Integer[][]> tests = new ArrayList<>();
        tests.add(test1);
        tests.add(test2);
        tests.add(test3);
        tests.add(test4);


        System.out.println("Running manhattan heuristic");
        for(Integer[][]test: tests){
            manhattanEightPuzzle.solvePuzzle(test);
        }

        System.out.println("Running absolute distance heuristic");
        for(Integer[][]test: tests){
            absoluteDistanceEightPuzzle.solvePuzzle(test);
        }


    }

    public static String stringifyPuzzle(Integer[][] puzzle) {
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
