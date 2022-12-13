package edu.uiuc;

import edu.uiuc.utils.Timer;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.rio.RDFFormat;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class YAGO4Class {

    public static final String DIR_IN = YAGO4Directories.RAW_CLASS;
    public static final String DIR_OUT = YAGO4Directories.OUT_ROOT + "YAGO4-class.txt";

    public static final String REL_SUBCLASS = "http://www.w3.org/2000/01/rdf-schema#subClassOf";

    private static final int COUNT_INTERVAL = 1000000;


    public static void parseYagoClass() throws IOException {
        var timer = new Timer(COUNT_INTERVAL);
        // Input and output
        RDFFormat format = RDFFormat.NTRIPLES;
        FileInputStream inputStream = new FileInputStream(DIR_IN);
        BufferedWriter writer = new BufferedWriter(new FileWriter(DIR_OUT));
        // Start processing
        try (GraphQueryResult res = QueryResults.parseGraphBackground(
                inputStream, DIR_IN, format)) {
            while (res.hasNext()) {
                timer.tik();
                Statement st = res.next();
                // Filter by namespace
                var subject = st.getSubject();
                var rel = st.getPredicate();
                var object = st.getObject();
                if (!rel.toString().equals(REL_SUBCLASS)) {
                    continue;
                }
                // Write to file
                writer.write(subject.toString());
                writer.write('\t');
                writer.write(object.toString());
                writer.write('\n');
            }
            timer.printTime();
        } catch (RDF4JException e) {
            System.out.println("Error");
        } finally {
            inputStream.close();
            writer.close();
        }
    }
}
