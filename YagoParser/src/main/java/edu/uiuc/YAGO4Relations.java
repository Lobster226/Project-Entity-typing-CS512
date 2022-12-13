package edu.uiuc;

import edu.uiuc.utils.StringCounter;
import edu.uiuc.utils.Timer;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.rio.RDFFormat;

import java.io.*;

public class YAGO4Relations {

    public static final String DIR_IN = "../dat/YAGO4-facts-concise.txt";

    public static final String DIR_RELATIONS = "../dat/YAGO4-relations-concise.txt";

    private static final int COUNT_INTERVAL = 1000000;

    public static void findRelations() throws IOException {
        var timer = new Timer(COUNT_INTERVAL);
        var stream = new FileInputStream(DIR_IN);
        var reader = new BufferedReader(new InputStreamReader(stream));
        var counter = new StringCounter();
        String line;
        while ((line = reader.readLine()) != null) {
            String[] lineItems = line.split("\t");
            counter.addOne(lineItems[1]);
            timer.tik();
        }
        var set = counter.filterByMinCount(0);
        BufferedWriter writer = new BufferedWriter(new FileWriter(DIR_RELATIONS));
        for (String relation : set) {
            writer.write(relation);
            writer.newLine();
        }
        writer.close();
    }

}
