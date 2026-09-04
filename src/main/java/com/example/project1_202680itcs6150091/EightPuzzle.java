package com.example.project1_202680itcs6150091;

import javafx.util.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class EightPuzzle {

    private final HeuristicFunction heuristicFunction;

    public EightPuzzle(HeuristicFunction heuristicFunction) {
        this.heuristicFunction = heuristicFunction;
    }

    public static class RunResults {
        private final List<Integer[][]> solutionPath;
        private final int nodesGenerated;
        private final int nodesExpanded;

        public RunResults(
                List<Integer[][]> path,
                int nodesGenerated,
                int nodesExpanded) {

            this.solutionPath = path;
            this.nodesGenerated = nodesGenerated;
            this.nodesExpanded = nodesExpanded;
        }

        public List<Integer[][]> getPath() {
            return solutionPath;
        }

        public int getNodesGenerated() {
            return nodesGenerated;
        }

        public int getNodesExpanded() {
            return nodesExpanded;
        }

        @Override
        public String toString() {
            return "RunResults{" +
                    "solutionPath=" + solutionPath +
                    ", nodesGenerated=" + nodesGenerated +
                    ", nodesExpanded=" + nodesExpanded +
                    '}';
        }
    }

    private int getTotalEstimatedH(Integer[][] puzzle) {
        int total = 0;

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                if (puzzle[y][x] != 0) {
                    total += heuristicFunction.getHValue(
                            new Coordinate(x, y),
                            puzzle[y][x]
                    );
                }
            }
        }

        return total;
    }

    public RunResults solvePuzzle(Integer[][] initialState) {
        System.out.println("Beginning puzzle:\n"+stringifyPuzzle(initialState));
        /*
         *   key = (g, h)
         *   value = puzzle state
         *   Priority is f (g+h)
         */
        PriorityQueue<Pair<Pair<Integer, Integer>, Integer[][]>> frontier =
                new PriorityQueue<>(
                        Comparator.comparingInt(pair -> pair.getKey().getKey()
                                        + pair.getKey().getValue()));

        int initialHValue = getTotalEstimatedH(initialState);

        frontier.add(new Pair<>(new Pair<>(0, initialHValue), deepCopyArray(initialState)));

        String initialKey = stringifyPuzzle(initialState);

        Map<String, Integer> bestG = new HashMap<>();
        bestG.put(initialKey, 0);

        Map<String, String> stateParent = new HashMap<>();
        stateParent.put(initialKey, null);

        Map<String, Integer[][]> visitedStates = new HashMap<>();
        visitedStates.put(initialKey, deepCopyArray(initialState));

        int nodesGenerated = 1;
        int nodesExpanded = 0;

        while (!frontier.isEmpty()) {
            Pair<Pair<Integer, Integer>, Integer[][]> current = frontier.poll();

            Integer[][] currentState = current.getValue();
            int currentG = current.getKey().getKey();
            String currentKey = stringifyPuzzle(currentState);

            /*
             * If state already exists with shorter path, skip this loop
             */
            if (currentG > bestG.get(currentKey)) {
                continue;
            }

            if (getTotalEstimatedH(currentState)==0) {
                List<Integer[][]> solutionPath = reconstructPath(currentKey, stateParent, visitedStates);
                System.out.println("Final state: \n"+stringifyPuzzle(currentState));
                System.out.println("Goal achieved in " + currentG + " steps");
                System.out.println("Nodes generated: " + nodesGenerated);
                System.out.println("Nodes expanded: " + nodesExpanded);
                System.out.println("Solution path:");

                for (Integer[][] integers : solutionPath) {
                    System.out.println(stringifyPuzzle(integers));
                }
                return new RunResults(solutionPath, nodesGenerated, nodesExpanded);
            }

            nodesExpanded++;
            List<Pair<Pair<Integer, Integer>, Integer[][]>> possibleMoves = getPossibleStates(currentState, currentG);
            nodesGenerated += possibleMoves.size();

            for (Pair<Pair<Integer, Integer>, Integer[][]> pair : possibleMoves) {
                Integer[][] successor = pair.getValue();
                int successorG = pair.getKey().getKey();
                String successorKey = stringifyPuzzle(successor);
                /*
                 * Make sure this is the best path to this state in the queue
                 */
                if (!bestG.containsKey(successorKey) || successorG < bestG.get(successorKey)) {
                    bestG.put(successorKey, successorG);
                    stateParent.put(successorKey, currentKey);
                    visitedStates.put(successorKey, deepCopyArray(successor));
                    frontier.add(pair);
                }
            }
        }
        System.out.println("Goal is unreachable.");
        System.out.println("Nodes generated: " + nodesGenerated);
        System.out.println("Nodes expanded: " + nodesExpanded);
        return new RunResults(Collections.emptyList(), nodesGenerated, nodesExpanded);
    }

    private List<Integer[][]> reconstructPath(String goal, Map<String, String> parent, Map<String, Integer[][]> states) {
        String currentKey = goal;
        List<Integer[][]> path = new ArrayList<>();

        while (currentKey != null) {
            path.add(deepCopyArray(states.get(currentKey)));
            currentKey = parent.get(currentKey);
        }

        Collections.reverse(path);
        return path;
    }

    private List<Pair<Pair<Integer, Integer>, Integer[][]>> getPossibleStates(Integer[][] state, int distanceTravelled) {
        Coordinate emptyPosition = findEmptyPosition(state);
        List<Pair<Pair<Integer, Integer>, Integer[][]>> options = new ArrayList<>();

        if (emptyPosition.getX() - 1 >= 0) {
            Integer[][] newState = moveTile(state, new Coordinate(emptyPosition.getX() - 1, emptyPosition.getY()), emptyPosition);
            options.add(new Pair<>(new Pair<>(distanceTravelled + 1, getTotalEstimatedH(newState)), newState));
        }
        if (emptyPosition.getX() + 1 <= 2) {
            Integer[][] newState = moveTile(state, new Coordinate(emptyPosition.getX() + 1, emptyPosition.getY()), emptyPosition);
            options.add(new Pair<>(new Pair<>(distanceTravelled + 1, getTotalEstimatedH(newState)), newState));
        }
        if (emptyPosition.getY() - 1 >= 0) {
            Integer[][] newState = moveTile(state, new Coordinate(emptyPosition.getX(), emptyPosition.getY() - 1), emptyPosition);
            options.add(new Pair<>(new Pair<>(distanceTravelled + 1, getTotalEstimatedH(newState)), newState));
        }
        if (emptyPosition.getY() + 1 <= 2) {
            Integer[][] newState = moveTile(state, new Coordinate(emptyPosition.getX(), emptyPosition.getY() + 1), emptyPosition);
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
        if (original == null) {
            return null;
        }
        Integer[][] copy = new Integer[original.length][];
        for (int i = 0; i < original.length; i++) {
            if (original[i] != null) {
                copy[i] = new Integer[original[i].length];
                System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
            }
        }
        return copy;
    }

    private Coordinate findEmptyPosition(Integer[][] puzzle) {
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                if (puzzle[y][x] == 0) {
                    return new Coordinate(x, y);
                }
            }
        }
        throw new IllegalArgumentException("No empty space located in puzzle");
    }

    public static String stringifyPuzzle(Integer[][] puzzle) {
        StringBuilder result = new StringBuilder();
        for (Integer[] puzzleNumbers : puzzle) {
            for (Integer number : puzzleNumbers) {
                result.append(number);
                result.append(" ");
            }
            result.append("\n");
        }
        return result.toString();
    }

    public static void main(String... args) throws IOException {
        Map<Integer, Coordinate> solutionMap = PuzzleParser.generateSolutionMap("DefaultSolution.txt");
        HeuristicFunction manhattanHeuristic = new ManhattanHeuristic(solutionMap);
        EightPuzzle manhattanEightPuzzle = new EightPuzzle(manhattanHeuristic);
        HeuristicFunction euclideanDistance = new EuclideanDistanceHeuristic(solutionMap);
        EightPuzzle euclideanDistanceEightPuzzle = new EightPuzzle(euclideanDistance);

        Path testFilesPath = Paths.get("TestFiles");
        List<Integer[][]> tests = new ArrayList<>();
        try (Stream<Path> stream = Files.list(testFilesPath)) {
            stream.filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            tests.add(PuzzleParser.readFile(String.valueOf(file.toAbsolutePath())));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Running manhattan heuristic");
        for(Integer[][]test: tests){
            manhattanEightPuzzle.solvePuzzle(test);
        }
        System.out.println("Running absolute distance heuristic");
        for(Integer[][]test: tests){
            euclideanDistanceEightPuzzle.solvePuzzle(test);
        }
    }
}