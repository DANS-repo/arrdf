package nl.knaw.dans.repo.arrdf.nqud;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created on 2018-08-07 12:12.
 */
public class NQuadsWriter implements RDFHandler {

    private String outputFile;
    private FileOutputStream out;
    private RDFWriter writer;

    private int maxStatementsInFile = -1;
    private int statementCount = 0;
    private int totalStatements = 0;
    private int fileNumber = 0;

    private List<String> states = new ArrayList<>();

    public NQuadsWriter(String outtputFile) throws FileNotFoundException {
        this.outputFile = outtputFile;
        startWriter();
    }

    private void startWriter() throws FileNotFoundException {
        out = new FileOutputStream(newFileName());
        writer = Rio.createWriter(RDFFormat.NQUADS, out);
    }

    public NQuadsWriter maxStatementsInFile(int maxStatements) {
        maxStatementsInFile = maxStatements;
        return this;
    }

    @Override
    public void startRDF() throws RDFHandlerException {
        setState("startRDF");
        writer.startRDF();
    }

    @Override
    public void endRDF() throws RDFHandlerException {
        setState("endRDF");
        writer.endRDF();
        try {
            out.close();
        } catch (IOException e) {
            throw new RDFHandlerException(e);
        }
    }

    @Override
    public void handleNamespace(String prefix, String uri) throws RDFHandlerException {
        // Namespaces and prefixes are not part of the N-Quad specification.
        setState("handleNamespace");
        writer.handleNamespace(prefix, uri);
    }

    @Override
    public void handleStatement(Statement st) throws RDFHandlerException {
        setState("handleStatement");
        statementCount++;
        totalStatements++;
        if (maxStatementsInFile > 0 && statementCount > maxStatementsInFile) {
            statementCount = 0;
            writer.endRDF();
            try {
                out.close();
            } catch (IOException e) {
                throw new RDFHandlerException(e);
            }
            try {
                startWriter();
            } catch (FileNotFoundException e) {
                throw new RDFHandlerException(e);
            }
            writer.startRDF();
        }
        writer.handleStatement(st);
    }

    private String newFileName() {
        String filename = String.format(outputFile + "%03d.nq", fileNumber++);
        return filename;
    }

    @Override
    public void handleComment(String comment) throws RDFHandlerException {
        // N-Quad comments (starting with #) are ignored when adding a file or url to a RepositoryConnection,
        // so you want find them here when writing out a repository.
        setState("handleComment");
        writer.handleComment(comment);
    }

    private void setState(String state) {
        int len = states.size();
        if (len == 0 || !states.get(len - 1).equals(state)) {
            states.add(state);
        }
    }

    public int getTotalStatements() {
        return totalStatements;
    }

    public List<String> getStates() {
        return states;
    }

    public boolean foundExpectedHandlingOrder() {
        return states.equals(Stream.of("startRDF", "handleStatement", "endRDF").collect(Collectors.toList()));
    }
}
