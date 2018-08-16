package nl.knaw.dans.repo.arrdf.nqud;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.junit.Test;

import java.io.FileReader;

/**
 * Created on 2018-08-09 14:12.
 */
public class NQuadsUDParserTest {

    @Test
    public void testParse() throws Exception {
        NQuadsUDParser parser = new NQuadsUDParser();
        parser.setRDFHandler(new RDFHandler() {
            @Override
            public void startRDF() throws RDFHandlerException {
                System.out.println("startRDF");
            }

            @Override
            public void endRDF() throws RDFHandlerException {
                System.out.println("endRDF");
            }

            @Override
            public void handleNamespace(String prefix, String uri) throws RDFHandlerException {
                System.out.println("handleNamespace " + prefix + " " + uri);
            }

            @Override
            public void handleStatement(Statement st) throws RDFHandlerException {
                String action = parser.isAssertion() ? "+" : "-";
                System.out.println("handleStatement " + action + st);
            }

            @Override
            public void handleComment(String comment) throws RDFHandlerException {
                System.out.println("handleComment " + comment);
            }
        });

        FileReader reader = new FileReader("src/test/resources/nqud/change.nqud");
        parser.parse(reader, null);

    }
}
