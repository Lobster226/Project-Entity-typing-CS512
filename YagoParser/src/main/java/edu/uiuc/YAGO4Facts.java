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

public class YAGO4Facts {
    public static final boolean DEBUG = false;

    public static final long NUM_FACTS = 22718091;
    public static final String YAGO = "http://yago-knowledge.org/resource/";
    public static final String SCHEMA = "http://schema.org/";

    public static final String DIR_IN = YAGO4Directories.RAW_FACTS;

    public static final String DIR_FILTERED_FACTS = "../dat/YAGO4-facts-concise.txt";

    public static final long MAX_ITER = DEBUG ? 100000 : NUM_FACTS * 2;

    private static final int COUNT_INTERVAL = 1000000;


    /**
     * This function simply reads all YAGO4 triples and filters out those whose:
     * - head and tail entities have YAGO namespace
     * - relation has Schema namespace
     * <p>
     * The filtered facts are saved to DIR_FILTERED_FACTS, where each line is:
     * "head rel tail\n"
     * where head, rel, and tail have no namespace and are separated by spaces
     * <p>
     * Runtime: it takes around 592 seconds to run all 22718091 facts on a laptop
     */
    public static void filterYAGOFacts() throws IOException {
        var timer = new Timer(COUNT_INTERVAL);
        // Input and output
        RDFFormat format = RDFFormat.NTRIPLES;
        FileInputStream inputStream = new FileInputStream(DIR_IN);
        BufferedWriter writer = new BufferedWriter(new FileWriter(DIR_FILTERED_FACTS));
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
                if (!rel.getNamespace().equals(SCHEMA)) {
                    continue;
                }
                if (!object.isIRI() || !((IRI) object).getNamespace().equals(YAGO)) {
                    continue;
                }
                // Write to file
                writer.write(((IRI) subject).getLocalName());
                writer.write('\t');
                writer.write(rel.getLocalName());
                writer.write('\t');
                writer.write(((IRI) object).getLocalName());
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
