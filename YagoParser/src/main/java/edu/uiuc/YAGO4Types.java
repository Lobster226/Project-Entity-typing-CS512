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

public class YAGO4Types {
    public static final boolean DEBUG = false;

    public static final long NUM_LINES = 5394049;
    public static final String YAGO = "http://yago-knowledge.org/resource/";
    public static final String TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

    public static final String DIR_IN = YAGO4Directories.RAW_TYPES;

    public static final String DIR_FILTERED_TYPES = "../dat/YAGO4-types-concise.txt";

    public static final long MAX_ITER = DEBUG ? 1000 : NUM_LINES * 2;

    private static final int COUNT_INTERVAL = 100000;


    /**
     * This function simply reads all YAGO4 type triples and filters out those whose:
     * - head entity has YAGO namespace
     * - relation has Schema namespace
     * <p>
     * The filtered mappings are saved to DIR_FILTERED_TYPES, where each line is:
     * "head type\n"
     * where head has no namespace, and type is full type
     * <p>
     */
    public static void filterYAGOTypes() throws IOException {
        var timer = new Timer(COUNT_INTERVAL);
        // Input and output
        RDFFormat format = RDFFormat.NTRIPLES;
        FileInputStream inputStream = new FileInputStream(DIR_IN);
        BufferedWriter writer = new BufferedWriter(new FileWriter(DIR_FILTERED_TYPES));
        // Start processing
        try (GraphQueryResult res = QueryResults.parseGraphBackground(
                inputStream, DIR_IN, format)) {
            while (res.hasNext() && timer.getCount() < MAX_ITER) {
                timer.tik();
                Statement st = res.next();
                // Filter by namespace
                var subject = st.getSubject();
                var rel = st.getPredicate();
                var object = st.getObject();
                if (!subject.isIRI() || !((IRI) subject).getNamespace().equals(YAGO)) {
                    continue;
                }
                if (!rel.toString().equals(TYPE)) {
                    continue;
                }
                // Write to file
                writer.write(((IRI) subject).getLocalName());
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
