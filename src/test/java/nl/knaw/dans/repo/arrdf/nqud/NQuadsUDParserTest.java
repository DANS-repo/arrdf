package nl.knaw.dans.repo.arrdf.nqud;

import nl.knaw.dans.repo.arrdf.http.Testing;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import static org.junit.Assume.assumeTrue;

/**
 * Created on 2018-08-09 14:12.
 */
public class NQuadsUDParserTest {

    @BeforeClass
    public static void initialize() throws Exception {
        assumeTrue("Live testing is off.",  Testing.LIVE_TESTS);
    }

    @Test
    public void testParse() throws Exception {
        NQuadsUDParser parser = new NQuadsUDParser();
        parser.setRDFHandler(new RDFHandler() {

            private int plusStCount;
            private int minusStCount;
            private int nsCount;
            private int cmCount;

            @Override
            public void startRDF() throws RDFHandlerException {
                System.out.println("startRDF");
            }

            @Override
            public void endRDF() throws RDFHandlerException {
                System.out.println("endRDF");
                System.out.println("+ statements: " + plusStCount);
                System.out.println("- statements: " + minusStCount);
                System.out.println("namespaces:   " + nsCount);
                System.out.println("comments:     " + cmCount);
            }

            @Override
            public void handleNamespace(String prefix, String uri) throws RDFHandlerException {
                //System.out.println("handleNamespace " + prefix + " " + uri);
                nsCount++;
            }

            @Override
            public void handleStatement(Statement st) throws RDFHandlerException {
                //String action = parser.isAssertion() ? "+" : "-";
                //System.out.println("handleStatement " + action + st);
                if (parser.isAssertion()) {
                    plusStCount++;
                } else {
                    minusStCount++;
                }
            }

            @Override
            public void handleComment(String comment) throws RDFHandlerException {
                //System.out.println("handleComment " + comment);
                cmCount++;
            }
        });

        // FileReader reader = new FileReader("src/test/resources/nqud/change.nqud");
        URL url = new URL(
          "https://data.anansi.clariah.nl/v5/resourcesync/u74ccc032adf8422d7ea92df96cd4783f0543db3b" +
            "/gemeentegeschiedenisnl/changes/changes0.nqud");
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        parser.parse(reader, null);

    }
}
