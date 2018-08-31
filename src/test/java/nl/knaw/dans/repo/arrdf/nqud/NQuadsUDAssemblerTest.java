package nl.knaw.dans.repo.arrdf.nqud;

import nl.knaw.dans.repo.arrdf.http.Testing;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.StringWriter;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

/**
 * Created on 2018-08-09 16:12.
 */
public class NQuadsUDAssemblerTest {

    @Test
    public void assemble() throws Exception {
        Repository repo = new SailRepository(new MemoryStore());
        repo.initialize();

        NQuadsUDAssembler assembler = new NQuadsUDAssembler(repo);
        assembler.add(new File("src/test/resources/nqud/original.nq"), null, null);

        NQuadsUDParser parser = new NQuadsUDParser();
        parser.setRDFHandler(assembler);
        FileReader reader = new FileReader("src/test/resources/nqud/change.nqud");
        parser.parse(reader, null);

        StringWriter out = new StringWriter();
        RDFHandler writer = Rio.createWriter(RDFFormat.NQUADS, out);
        assembler.export(writer);

        String expected = "<http://timbuctoo.huygens.knaw.nl/datasets/clusius/Place_PL00000029> <http://timbuctoo" +
          ".huygens.knaw.nl/properties/country> \"The Netherland\" .\n" +
          "<http://timbuctoo.huygens.knaw.nl/datasets/clusius/Place_PL00000029> <http://timbuctoo.huygens.knaw" +
          ".nl/properties/longitude> \"436052\"^^<http://schema.org/longitude> .\n" +
          "<http://timbuctoo.huygens.knaw.nl/datasets/clusius/Place_PL00000029> <http://www" +
          ".w3.org/1999/02/22-rdf-syntax-ns#type> <http://timbuctoo.huygens.knaw.nl/datasets/clusius/Places> .\n" +
          "<http://timbuctoo.huygens.knaw.nl/datasets/clusius/Place_PL00000029> <http://timbuctoo.huygens.knaw" +
          ".nl/properties/latitude> \"5200951\"^^<http://schema.org/latitude> .\n" +
          "<http://timbuctoo.huygens.knaw.nl/datasets/clusius/Place_PL00000029> <http://timbuctoo.huygens.knaw" +
          ".nl/properties/original_id> \"PL00000029\" .\n";

        assertEquals(expected, out.toString());
    }


    @Test
    public void liveAssemble() throws Exception {
        assumeTrue("Live testing is off.", Testing.LIVE_TESTS);

        Repository repo = new SailRepository(new MemoryStore());
        repo.initialize();

        NQuadsUDAssembler assembler = new NQuadsUDAssembler(repo);
        assembler.add(
          new URL(
            "https://data.anansi.clariah.nl/v5/resourcesync/u74ccc032adf8422d7ea92df96cd4783f0543db3b/tbi/dataset.nq"),
          null, RDFFormat.NQUADS);

        long originalStatementCount;
        try (RepositoryConnection con = repo.getConnection()) {
            originalStatementCount = con.size();
        }

        NQuadsUDParser parser = new NQuadsUDParser();
        parser.setRDFHandler(assembler);

        String changeString =
          "https://data.anansi.clariah.nl/v5/resourcesync/u74ccc032adf8422d7ea92df96cd4783f0543db3b/tbi/changes/changes";

        System.out.print("Applying changes: ");
        for (int i = 0; i < 106; i++) {
            URL changeURL = new URL(changeString + i + ".nqud");
            System.out.print(".");
            System.out.flush();
            parser.parse(changeURL, null);
        }
        System.out.println();

        System.out.println(assembler.getPlusStatementCount());      // 207468
        System.out.println(assembler.getMinusStatementCount());     // 0
        System.out.println(assembler.getAddedStatementCount());     // 2
        System.out.println(assembler.getRemovedStatementCount());   // 0

        NQuadsWriter writer = new NQuadsWriter("target/test-output/assembler/tbi/tbi")
          .withMaxStatementsInFile(100000);

        assembler.export(writer);

        long totalStatements = originalStatementCount + assembler.getAddedStatementCount()
          - assembler.getRemovedStatementCount();

        System.out.println("total statements written: " + writer.getTotalStatements());

        assertEquals(totalStatements, writer.getTotalStatements());
    }
}
