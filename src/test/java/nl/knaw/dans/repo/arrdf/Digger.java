package nl.knaw.dans.repo.arrdf;

import nl.knaw.dans.repo.arrdf.nqud.NQuadsUDAssembler;
import nl.knaw.dans.repo.arrdf.nqud.NQuadsUDParser;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import java.net.URL;

/**
 * Created on 2018-08-16 10:10.
 */
public class Digger {

    public static void main(String[] args) throws Exception {
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // // https://data.anansi.clariah.nl/.well-known/resourcesync

        // // gemeentegeschiedenisnl
        // String base = "https://data.anansi.clariah
        // .nl/v5/resourcesync/u74ccc032adf8422d7ea92df96cd4783f0543db3b/gemeentegeschiedenisnl";
        // int maxChangeNum = 2;
        // String expectedContext = "https://data.anansi.clariah
        // .nl/datasets/u74ccc032adf8422d7ea92df96cd4783f0543db3b/gemeentegeschiedenisnl/";

        // // dwc
        // String base = "https://data.anansi.clariah.nl/v5/resourcesync/u74ccc032adf8422d7ea92df96cd4783f0543db3b/dwc";
        // int maxChangeNum = 24;
        // String expectedContext = "https://data.anansi.clariah
        // .nl/datasets/u74ccc032adf8422d7ea92df96cd4783f0543db3b/dwc/";

        // // nlgis
        // String base = "https://data.anansi.clariah
        // .nl/v5/resourcesync/u74ccc032adf8422d7ea92df96cd4783f0543db3b/nlgis";
        // int maxChangeNum = 0;
        // String expectedContext = "https://data.anansi.clariah
        // .nl/datasets/u74ccc032adf8422d7ea92df96cd4783f0543db3b/nlgis/";

        // // gemeentegeschiedenis
        // String base = "https://data.anansi.clariah
        // .nl/v5/resourcesync/u74ccc032adf8422d7ea92df96cd4783f0543db3b/gemeentegeschiedenis";
        // int maxChangeNum = 0;
        // String expectedContext = "https://data.anansi.clariah
        // .nl/datasets/u74ccc032adf8422d7ea92df96cd4783f0543db3b/gemeentegeschiedenis/";

        // // bioport
        // String base = "https://data.anansi.clariah
        // .nl/v5/resourcesync/u74ccc032adf8422d7ea92df96cd4783f0543db3b/bioport";
        // int maxChangeNum = 2;
        // String expectedContext = "https://data.anansi.clariah
        // .nl/datasets/u74ccc032adf8422d7ea92df96cd4783f0543db3b/bioport/";

        // // tbi
        // // <rs:md capability="resourcelist" at="2018-07-10T13:48:23.648Z" completed="2018-08-16T12:46:51.459Z"/>
        // // <rs:md capability="changelist" from="2018-07-10T13:48:23.648Z"/>
        // // latest change file: <rs:md datetime="2018-07-10T13:48:23.145Z" change="updated"
        // type="application/n-quads"/>
        // // What is the final dataset?
        // // !!! 2 adds from : https://data.anansi.clariah
        // .nl/v5/resourcesync/u74ccc032adf8422d7ea92df96cd4783f0543db3b/tbi/changes/changes0.nqud
        // String base = "https://data.anansi.clariah.nl/v5/resourcesync/u74ccc032adf8422d7ea92df96cd4783f0543db3b/tbi";
        // int maxChangeNum = 105;
        // String expectedContext = "https://data.anansi.clariah
        // .nl/datasets/u74ccc032adf8422d7ea92df96cd4783f0543db3b/tbi/";

        // // personen
        // String base = "https://data.anansi.clariah
        // .nl/v5/resourcesync/u33707283d426f900d4d55b410a78996dc730b2f7/personen";
        // int maxChangeNum = 20;
        // String expectedContext = "https://data.anansi.clariah
        // .nl/datasets/u33707283d426f900d4d55b410a78996dc730b2f7/personen/";

        // // concepten
        // // 504 Gateway Time-out on https://data.anansi.clariah
        // .nl/v5/resourcesync/u33707283d426f900d4d55b410a78996dc730b2f7/concepten/dataset.nq
        // String base = "https://data.anansi.clariah
        // .nl/v5/resourcesync/u33707283d426f900d4d55b410a78996dc730b2f7/concepten";
        // int maxChangeNum = 1;
        // String expectedContext = "https://data.anansi.clariah
        // .nl/datasets/u33707283d426f900d4d55b410a78996dc730b2f7/concepten/";

        // // plaatsen
        // // gone from sourceDescription.xml
        // String base = "https://data.anansi.clariah
        // .nl/v5/resourcesync/u33707283d426f900d4d55b410a78996dc730b2f7/plaatsen";
        // int maxChangeNum = 1;
        // String expectedContext = "https://data.anansi.clariah
        // .nl/datasets/u33707283d426f900d4d55b410a78996dc730b2f7/plaatsen/";


        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // // https://repository.huygens.knaw.nl/v5/resourcesync/sourceDescription.xml

        // // dwc
        // String base = "https://repository.huygens.knaw
        // .nl/v5/resourcesync/u74ccc032adf8422d7ea92df96cd4783f0543db3b/dwc";
        // int maxChangeNum = 100;
        // String expectedContext = "https://data.huygens.knaw
        // .nl/rdf/datasets/u74ccc032adf8422d7ea92df96cd4783f0543db3b/dwc/";

        // // charterportaal
        // String base = "https://repository.huygens.knaw
        // .nl/v5/resourcesync/u74ccc032adf8422d7ea92df96cd4783f0543db3b/charterportaal";
        // int maxChangeNum = 57;
        // String expectedContext = "https://data.huygens.knaw
        // .nl/rdf/datasets/u74ccc032adf8422d7ea92df96cd4783f0543db3b/charterportaal/";

        // // bioport
        // String base = "https://repository.huygens.knaw.nl/v5/resourcesync/u74ccc032adf8422d7ea92df96cd4783f0543db3b/bioport";
        // int maxChangeNum = 27;
        // String expectedContext = "https://data.huygens.knaw
        // .nl/rdf/datasets/u74ccc032adf8422d7ea92df96cd4783f0543db3b/bioport/";

        // // vocopvarenden2
        // String base = "https://repository.huygens.knaw
        // .nl/v5/resourcesync/u0c0efe7fb8a246c7ebb7b0d710d7b19d5e60ce3d/vocopvarenden2";
        // int maxChangeNum = 2;
        // String expectedContext = "https://data.huygens.knaw
        // .nl/rdf/datasets/u0c0efe7fb8a246c7ebb7b0d710d7b19d5e60ce3d/vocopvarenden2/";

        // prizepapers
        // 504 Gateway Time-out
        String base =
          "https://repository.huygens.knaw.nl/v5/resourcesync/u0c0efe7fb8a246c7ebb7b0d710d7b19d5e60ce3d/prizepapers";
        int maxChangeNum = 5; // 1
        String expectedContext =
          "https://data.huygens.knaw.nl/rdf/datasets/u0c0efe7fb8a246c7ebb7b0d710d7b19d5e60ce3d/prizepapers/";


        digThat(base, maxChangeNum, expectedContext);
    }

    private static void digThat(String base, int changeNum, String expectedContext) throws Exception {
        System.out.println("Inspecting :" + base);
        System.out.println("Number of change files: " + (changeNum + 1));
        // original
        Repository repo = new SailRepository(new MemoryStore());
        repo.initialize();

        ValueFactory f = repo.getValueFactory();
        Resource defaultContext = f.createIRI(expectedContext);

        NQuadsUDAssembler assembler = new NQuadsUDAssembler(repo);
        URL resource = new URL(base + "/dataset.nq");
        assembler.add(resource, null, RDFFormat.NQUADS);

        try (RepositoryConnection con = repo.getConnection()) {
            System.out.println("\n== Original dataset ==");
            System.out.println("size of repo: " + con.size());
            RepositoryResult<Resource> result = con.getContextIDs();
            while (result.hasNext()) {
                Resource context = result.next();
                if (context.equals(defaultContext)) {
                    System.out.println("expected context: " + context);
                } else {
                    System.out.println("new context:      " + context);
                }
            }
        }

        // changes
        NQuadsUDParser parser = new NQuadsUDParser();
        parser.setRDFHandler(assembler);
        long adds = 0;
        long removes = 0;
        for (int chNum = 0; chNum < changeNum + 1; chNum++) {
            URL change = new URL(base + "/changes/changes" + chNum + ".nqud");
            parser.parse(change, null);
            if (adds != assembler.getAddedStatementCount()) {
                adds = assembler.getAddedStatementCount();
                System.out.println("add from : " + change);
            }
            if (removes != assembler.getRemovedStatementCount()) {
                removes = assembler.getRemovedStatementCount();
                System.out.println("remove from: " + change);
            }
        }

        System.out.println("\n== Changes ==");
        System.out.println("plus statements:  " + parser.getPlusStatementCount());
        System.out.println("minus statements: " + parser.getMinusStatementCount());

        System.out.println("\n== After change ==");
        System.out.println("added statements:   " + assembler.getAddedStatementCount());
        System.out.println("removed statements: " + assembler.getRemovedStatementCount());

        try (RepositoryConnection con = repo.getConnection()) {
            System.out.println("\n== Changed dataset ==");
            System.out.println("size of repo: " + con.size());

            RepositoryResult<Resource> result = con.getContextIDs();
            while (result.hasNext()) {
                Resource context = result.next();
                if (context.equals(defaultContext)) {
                    System.out.println("expected context: " + context);
                } else {
                    System.out.println("new context:      " + context);
                }
            }

            // Resource r = f.createIRI("BlankNode:37ac980d-5bb1-4754-a6b7-a50c27d64538-combined_nt/bcongres1010");
            // RepositoryResult<Statement> stResult = con.getStatements(r, null, null);
            // while (stResult.hasNext()) {
            //     System.out.println(stResult.next());
            // }


        }

    }

}
