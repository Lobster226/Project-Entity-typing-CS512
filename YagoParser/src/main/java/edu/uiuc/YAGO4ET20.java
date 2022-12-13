package edu.uiuc;

import edu.uiuc.utils.StringCounter;
import edu.uiuc.utils.Timer;

import java.io.*;
import java.util.HashSet;

import static edu.uiuc.YAGO4Directories.OUT_ROOT;

public class YAGO4ET20 {
    public static final int NUM_FACTS = 12584259;

    public static final boolean DEBUG = false;

    public static final String DIR_FILTERED_FACTS = OUT_ROOT + "YAGO4-facts-concise.txt";
    public static final String DIR_FILTERED_TYPES = OUT_ROOT + "YAGO4-types-concise.txt";

    public static final String DIR_FREQ_20_ENTITIES = OUT_ROOT + "YAGO4ET20-freq-entities.txt";
    public static final String DIR_FREQUENT_FACTS_ONE_END = OUT_ROOT + "YAGO4-facts-freq20-one-end.txt";
    public static final String DIR_FREQUENT_FACTS_BOTH_ENDS = OUT_ROOT + "YAGO4-facts-freq20-both-ends.txt";
    public static final String DIR_FREQUENT_TYPES = OUT_ROOT + "YAGO4-types-freq20.txt";

    public static final int COUNT_INTERVAL = 1000000;

    /**
     * We read in filtered facts and pick out entities that show up at least 20 times
     */
    public static void filterEntitiesByCount() throws IOException {
        var timer = new Timer(COUNT_INTERVAL);
        var stream = new FileInputStream(DIR_FILTERED_FACTS);
        var reader = new BufferedReader(new InputStreamReader(stream));
        var counter = new StringCounter();
        String line;
        while ((line = reader.readLine()) != null) {
            String[] lineItems = line.split("\t");
            counter.addOne(lineItems[0]);
            counter.addOne(lineItems[2]);
            timer.tik();
        }
        var entitySet = counter.filterByMinCount(20);
        BufferedWriter writer = new BufferedWriter(new FileWriter(DIR_FREQ_20_ENTITIES));
        for (String entity : entitySet) {
            writer.write(entity);
            writer.newLine();
        }
        writer.close();
    }

    public static void filterFactsByFreqEntities() throws IOException {
        var timer = new Timer(COUNT_INTERVAL);


        var stream = new FileInputStream(DIR_FREQ_20_ENTITIES);
        var reader = new BufferedReader(new InputStreamReader(stream));
        var entitySet = new HashSet<String>();

        String line;
        while ((line = reader.readLine()) != null) {
            entitySet.add(line);
        }
        reader.close();

        stream = new FileInputStream(DIR_FILTERED_FACTS);
        reader = new BufferedReader(new InputStreamReader(stream));

        BufferedWriter writer = new BufferedWriter(new FileWriter(DIR_FREQUENT_FACTS_BOTH_ENDS));

        while ((line = reader.readLine()) != null) {
            String[] lineItems = line.split("\t");
            if (entitySet.contains(lineItems[0]) && entitySet.contains(lineItems[2])) {
                writer.write(line);
                writer.write('\n');
            }
            timer.tik();
        }
        reader.close();
        writer.close();
    }


    public static void filterTypesByFreqEntities() throws IOException {
        var timer = new Timer(COUNT_INTERVAL);


        var stream = new FileInputStream(DIR_FREQ_20_ENTITIES);
        var reader = new BufferedReader(new InputStreamReader(stream));
        var entitySet = new HashSet<String>();

        String line;
        while ((line = reader.readLine()) != null) {
            entitySet.add(line);
        }
        reader.close();

        stream = new FileInputStream(DIR_FILTERED_TYPES);
        reader = new BufferedReader(new InputStreamReader(stream));

        BufferedWriter writer = new BufferedWriter(new FileWriter(DIR_FREQUENT_TYPES));

        while ((line = reader.readLine()) != null) {
            String[] lineItems = line.split("\t");
            if (entitySet.contains(lineItems[0])) {
                writer.write(line);
                writer.write('\n');
            }
            timer.tik();
        }
        reader.close();
        writer.close();
    }

}
