package com.example.project1_202680itcs6150091;

import java.io.*;
import java.util.*;
public class PuzzleParser {
    public static PuzzleNumber[][] readMatrix(String filename) throws IOException {
        List<PuzzleNumber[]> rows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int column = 0;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                String[] values = line.split("\\s+");
                PuzzleNumber[] row = new PuzzleNumber[values.length];

                for (int i = 0; i < values.length; i++) {
                    row[i] = new PuzzleNumber(column, i, Integer.parseInt(values[i]));
                }

                rows.add(row);
            }
        }

        return rows.toArray(new PuzzleNumber[0][]);
    }
}
