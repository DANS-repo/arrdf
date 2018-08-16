package nl.knaw.dans.repo.arrdf.nqud;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

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
}
