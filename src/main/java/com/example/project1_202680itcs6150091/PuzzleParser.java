package com.example.project1_202680itcs6150091;

import java.io.*;
import java.util.*;
public class PuzzleParser {
    public static Integer[][] readMatrix(String filename) throws IOException {
        List<Integer[]> rows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int column = 0;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                String[] values = line.split("\\s+");
                Integer[] row = new Integer[values.length];

                for (int i = 0; i < values.length; i++) {
                    row[i] = Integer.parseInt(values[i]);
                }

                rows.add(row);
            }
        }

        return rows.toArray(new Integer[0][]);
    }
}
