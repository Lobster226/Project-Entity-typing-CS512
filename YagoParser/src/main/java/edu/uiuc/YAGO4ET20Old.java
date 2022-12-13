package edu.uiuc;

import edu.uiuc.utils.Timer;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;

import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class YAGO4ET20Old {
    public static final boolean DEBUG = false;

    public static final boolean RUN_ONLY_IF_OUTPUT_FILE_NOT_EXISTS = true;

    /**
     * if true, then the program shall read fom file for valid entities
     */
    public static final boolean VALID_ENTITIES_WRITTEN_TO_FILE = true;

    public static final long NUM_FACTS = 22718091;
    public static final long MIN_ENTITY_COUNT = 20;
    public static final String YAGO = "http://yago-knowledge.org/resource/";
    public static final String SCHEMA = "http://schema.org/";

    public static final String DIR_IN = "../dat/yago-wd-facts.nt";
    public static final String DIR_OUT = "../dat/YAGO4ET20-facts.nt";
    public static final String DIR_FREQ_20_ENTITIES = "../dat/YAGO4ET20-entities.txt";
    public static final String DIR_FREQ_20_ENTITIES_CONCISE = "../dat/YAGO4ET20-entities-concise.txt";

    public static final String DIR_FILTERED_FACTS = "../dat/YAGO4-facts-concise.txt";

    public static final long MAX_ITER = DEBUG ? 100000 : NUM_FACTS * 2;

    private static final int COUNT_INTERVAL = 1000000;


//    //        // namespaces
//    String yago = "http://yago-knowledge.org/resource/";
//    //        String owl = "http://www.w3.org/2002/07/owl#";
//    //        String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
//    //        String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
//    //        String sh = "http://www.w3.org/ns/shacl#";
//    //        String skos = "http://www.w3.org/2004/02/skos/core#";
//    String schema = "http://schema.org/";
//    //        String wd = "http://www.wikidata.org/entity/";
//    //        String wdt = "http://www.wikidata.org/prop/direct/";
//    //        String wpq = "http://www.wikidata.org/prop/qualifier/";
//    //        String xsd = "http://www.w3.org/2001/XMLSchema#";
//    //        String ys = "http://yago-knowledge.org/schema#";




    /**
     * This function counts valid entities and return those that occur at least 20 times
     * Valid entities are entities in yago namespace
     * The valid entities shall be written into DIR_FREQ_20_ENTITIES
     * https://www.programcreek.com/2013/10/efficient-counter-in-java/
     */
    public static HashSet<String> filterEntitiesByCount() throws IOException {
        var timer = new Timer(COUNT_INTERVAL);

        HashSet<String> entitySet = new HashSet<>();
        // Read from file if results are already calculated
        if (RUN_ONLY_IF_OUTPUT_FILE_NOT_EXISTS && VALID_ENTITIES_WRITTEN_TO_FILE) {
            Scanner reader = new Scanner(new File(DIR_FREQ_20_ENTITIES));
            while (reader.hasNextLine()) {
                String entity = reader.nextLine();
                entitySet.add(entity);
            }
            return entitySet;
        }
        // Count entities
        HashMap<String, int[]> entityCounter = new HashMap<>();
        // Input and output
        RDFFormat format = RDFFormat.NTRIPLES;
        FileInputStream inputStream = new FileInputStream(DIR_IN);
        System.out.println((inputStream != null) ?
                "Input steam created" : "Cannot create input stream");

        // Start counting
        timer.start();
        try (GraphQueryResult res = QueryResults.parseGraphBackground(
                inputStream, DIR_IN, format)) {
            long startTime = System.currentTimeMillis();
            NumberFormat formatter = new DecimalFormat("#0.00000");
            long count = 0;

            while (res.hasNext() && timer.getCount() < MAX_ITER) {
                Statement st = res.next();

                // http://yago
                String subjectString = st.getSubject().toString();
                if (subjectString.length() >= 11 && "yago".equals(subjectString.substring(7, 11))) {
                    int[] valueWrapper = entityCounter.get(subjectString);
                    if (valueWrapper == null) {
                        entityCounter.put(subjectString, new int[]{1});
                    } else {
                        valueWrapper[0]++;
                    }
                }

                String objectString = st.getObject().toString();
                if (objectString.length() >= 11 && "yago".equals(objectString.substring(7, 11))) {
                    int[] valueWrapper = entityCounter.get(objectString);
                    if (valueWrapper == null) {
                        entityCounter.put(objectString, new int[]{1});
                    } else {
                        valueWrapper[0]++;
                    }
                }
                timer.tik();
            }
            double runtime = (System.currentTimeMillis() - startTime) / 1000d;
            System.out.println(
                    "Count: " + count + "; "
                            + formatter.format(runtime) + " seconds");
            System.out.println(
                    "Estimated run time: "
                            + formatter.format(runtime / count * NUM_FACTS)
                            + " seconds");
        } catch (RDF4JException e) {
            // handle unrecoverable error
            System.out.println("Error");
        } finally {
            inputStream.close();
        }
        // Filter and write to file
        BufferedWriter entityWriter = new BufferedWriter(new FileWriter(DIR_FREQ_20_ENTITIES));
        for (HashMap.Entry<String, int[]> entry : entityCounter.entrySet()) {
            if (entry.getValue()[0] >= MIN_ENTITY_COUNT) {
                entitySet.add(entry.getKey());
                entityWriter.write(entry.getKey());
                entityWriter.newLine();
            }
        }
        entityWriter.close();
        return entitySet;
    }


    public static void parseYagoFacts(HashSet<String> validEntities) throws IOException {

        // Input and output
        RDFFormat format = RDFFormat.NTRIPLES;
        FileInputStream inputStream = new FileInputStream(DIR_IN);
        System.out.println((inputStream != null) ?
                "Input steam created" : "Cannot create input stream");
        FileOutputStream outputStream = new FileOutputStream(DIR_OUT);
        RDFWriter writer = Rio.createWriter(format, outputStream);

        // Start processing
        try (GraphQueryResult res = QueryResults.parseGraphBackground(
                inputStream, DIR_IN, format)) {
            long startTime = System.currentTimeMillis();
            NumberFormat formatter = new DecimalFormat("#0.00000");
            long count = 0;

            writer.startRDF();
            while (res.hasNext()) {
                count++;
                if (count > MAX_ITER) break;
                Statement st = res.next();
                // http://yago
////                if (!subject.isIRI() || !((IRI)subject).getNamespace().equals(yago)) {
////                    continue;
////                }
//                if (!"yago".equals(subject.toString().substring(7, 11))){continue;}
//                Value object = st.getObject();
////                if (!object.isIRI() || !((IRI)object).getNamespace().equals(yago)) {
////                    continue;
////                }
//                if (!"yago".equals(object.toString().substring(7, 11))){continue;}


                String subjectString = st.getSubject().toString();
                if (subjectString.length() >= 11 && "yago".equals(subjectString.substring(7, 11))) {
                    String objectString = st.getObject().toString();
                    if (objectString.length() >= 11 && "yago".equals(objectString.substring(7, 11))
                            && (validEntities.contains(objectString) || validEntities.contains(subjectString))) {
                        writer.handleStatement(st);
                    }
                }

//                // http://yago
//                String subjectString = st.getSubject().toString();
//                if (subjectString.charAt(7) != 'y') {
//                    continue;
//                }
//                String objectString = st.getObject().toString();
//                if (objectString.charAt(7) != 'y') {
//                    continue;
//                }


                if (count % 1000000 == 0) {
                    System.out.println("Count: " + count + "; " + formatter.format(
                            (System.currentTimeMillis() - startTime) / 1000d) + " seconds");
                }
            }
            double runtime = (System.currentTimeMillis() - startTime) / 1000d;
            System.out.println(
                    "Count: " + count + "; "
                            + formatter.format(runtime) + " seconds");
            System.out.println(
                    "Estimated run time: "
                            + formatter.format(runtime / count * NUM_FACTS)
                            + " seconds");
            writer.endRDF();
        } catch (RDF4JException e) {
            // handle unrecoverable error
            System.out.println("Error");
        } finally {
            inputStream.close();
            outputStream.close();
        }
    }


    public static void trimEntitiesAndFacts() throws IOException {

    }
}
