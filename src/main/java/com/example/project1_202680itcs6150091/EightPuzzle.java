package com.example.project1_202680itcs6150091;

import javafx.util.Pair;

import java.io.IOException;
import java.util.*;

public class EightPuzzle {
    private final HeuristicFunction heuristicFunction;

    public EightPuzzle(HeuristicFunction heuristicFunction) {
        this.heuristicFunction = heuristicFunction;
    }

    private int getTotalEstimatedH(PuzzleNumber[][] puzzle){
        int total = 0;
        for(PuzzleNumber[] row: puzzle){
            for(PuzzleNumber number : row) {
                if(number.getNumber()!=0) {
                    total += heuristicFunction.getHValue(number);
                }
            }
        }
        return total;
    }

    public PuzzleNumber[][] solvePuzzle(PuzzleNumber[][] initialState){
        System.out.println("Beginning puzzle: \n"+stringifyPuzzle(initialState));
        int initialHValue = getTotalEstimatedH(initialState);
        //Pair<Integer, Integer> is (gValue, hValue), AKA (distance travelled, heuristic estimate of distance remaining)
        PriorityQueue<Pair<Pair<Integer, Integer>, PuzzleNumber[][]>> frontier = new PriorityQueue<>(Comparator.comparingInt(pair -> (pair.getKey().getKey() + pair.getKey().getValue())));
        frontier.add(new Pair<>(new Pair<>(0, initialHValue), initialState));
        HashSet<String> visitedStates = new HashSet<>();
        Map<String, Integer> bestF = new HashMap<>();
        while(!frontier.isEmpty() ) {

            Pair<Pair<Integer, Integer>, PuzzleNumber[][]> state = frontier.poll();
            System.out.println("Considering state:\n"+stringifyPuzzle(state.getValue()));



            //If h is zero, we're there
            if(state.getKey().getValue() == 0){
                System.out.println("Goal achieved in "+state.getKey().getKey()+" steps");
                return state.getValue();
            }
            List<Pair<Pair<Integer, Integer>, PuzzleNumber[][]>> possibleMoves = getPossibleStates(state.getValue(), state.getKey().getKey());
            for(Pair<Pair<Integer, Integer>, PuzzleNumber[][]> pair : possibleMoves){
                String stringState = stringifyPuzzle(pair.getValue());
                int fValue = pair.getKey().getKey()+pair.getKey().getValue();

                if (!bestF.containsKey(stringState) || fValue < bestF.get(state)) {
                    bestF.put(stringState, fValue);
                    frontier.add(pair);
                }
            }
        }
        return initialState;
    }

    private List<Pair<Pair<Integer, Integer>, PuzzleNumber[][]>> getPossibleStates(PuzzleNumber[][] state, int distanceTravelled) {
        PuzzleNumber emptyPosition = findEmptyPosition(state);
        List<Pair<Pair<Integer, Integer>, PuzzleNumber[][]>> options = new ArrayList<>();

        if(emptyPosition.getX()-1>=0 ){
            PuzzleNumber[][] newState = moveTile(state, state[emptyPosition.getY()][emptyPosition.getX()-1], emptyPosition);
            options.add(new Pair<>(new Pair<>(distanceTravelled + 1, getTotalEstimatedH(newState)), newState));
        }
        if(emptyPosition.getX()+1<=2){
            PuzzleNumber[][] newState = moveTile(state, state[emptyPosition.getY()][emptyPosition.getX()+1], emptyPosition);
            options.add(new Pair<>(new Pair<>(distanceTravelled + 1, getTotalEstimatedH(newState)), newState));
        }
        if(emptyPosition.getY()-1>=0){
            PuzzleNumber[][] newState = moveTile(state, state[emptyPosition.getY()-1][emptyPosition.getX()], emptyPosition);
            options.add(new Pair<>(new Pair<>(distanceTravelled + 1, getTotalEstimatedH(newState)), newState));
        }
        if(emptyPosition.getY()+1<=2){
            PuzzleNumber[][] newState = moveTile(state, state[emptyPosition.getY()+1][emptyPosition.getX()], emptyPosition);
            options.add(new Pair<>(new Pair<>(distanceTravelled + 1, getTotalEstimatedH(newState)), newState));
        }

        return options;
    }

    private PuzzleNumber[][] moveTile(PuzzleNumber[][] state, PuzzleNumber number, PuzzleNumber emptyPosition) {
        PuzzleNumber[][] copy = deepCopyArray(state);
        copy[emptyPosition.getY()][emptyPosition.getX()] = new PuzzleNumber(emptyPosition.getX(), emptyPosition.getY(), number.getNumber());
        copy[number.getY()][number.getX()] = new PuzzleNumber(number.getX(), number.getY(), 0);
        return copy;
    }

    public static PuzzleNumber[][] deepCopyArray(PuzzleNumber[][] original) {
        if (original == null) return null;
        PuzzleNumber[][] copy = new PuzzleNumber[original.length][];
        for (int i = 0; i < original.length; i++) {
            if (original[i] != null) {
                copy[i] = new PuzzleNumber[original[i].length];
                for (int j = 0; j < original[i].length; j++) {
                    if (original[i][j] != null) {
                        copy[i][j] = new PuzzleNumber(original[i][j]);
                    }
                }
            }
        }
        return copy;
    }



    private PuzzleNumber findEmptyPosition(PuzzleNumber[][] puzzle) {
        for(PuzzleNumber[] row: puzzle){
            for(PuzzleNumber number : row) {
                if(number.getNumber()==0){
                    return number;
                }
            }
        }
        throw new IllegalArgumentException("No empty space located in puzzle");
    }

    public static void main(String... args) throws IOException {
        Map<Integer, PuzzleNumber> solutionMap = new HashMap<>();
        solutionMap.put(1, new PuzzleNumber(0,0,1));
        solutionMap.put(2, new PuzzleNumber(1,0,2));
        solutionMap.put(3, new PuzzleNumber(2,0,3));
        solutionMap.put(4, new PuzzleNumber(0,1,4));
        solutionMap.put(5, new PuzzleNumber(1,1,5));
        solutionMap.put(6, new PuzzleNumber(2,1,6));
        solutionMap.put(7, new PuzzleNumber(0,2,7));
        solutionMap.put(8, new PuzzleNumber(1,2,8));

        HeuristicFunction manhattanHeuristic = new ManhattanHeuristic(solutionMap);
        EightPuzzle manhattanEightPuzzle = new EightPuzzle(manhattanHeuristic);
        PuzzleNumber[][] puzzle = PuzzleParser.readMatrix("Test1.txt");
        manhattanEightPuzzle.solvePuzzle(puzzle);


    }

    public String stringifyPuzzle(PuzzleNumber[][] puzzle) {
        StringBuilder result = new StringBuilder();
        for (PuzzleNumber[] puzzleNumbers : puzzle) {
            for (PuzzleNumber number: puzzleNumbers) {
                result.append(number.getNumber());
                result.append(" ");
            }
            result.append("\n");
        }
        return result.toString();
    }

}
