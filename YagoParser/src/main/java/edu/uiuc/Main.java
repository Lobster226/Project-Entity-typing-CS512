package edu.uiuc;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException {
        YAGO4Facts.filterYAGOFacts();
        YAGO4Types.filterYAGOTypes();
        YAGO4Relations.findRelations();
        YAGO4ET20.filterEntitiesByCount();
        YAGO4ET20.filterFactsByFreqEntities();
        YAGO4ET20.filterTypesByFreqEntities();
        YAGO4Class.parseYagoClass();
    }
}
