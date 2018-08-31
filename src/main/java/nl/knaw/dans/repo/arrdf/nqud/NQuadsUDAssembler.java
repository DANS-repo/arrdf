package nl.knaw.dans.repo.arrdf.nqud;

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.function.Supplier;

/**
 * An {@link RDFHandler} backed by a {@link Repository} that can assemble modifications based on the unified-diff
 * format (<code>application/vnd.timbuctoo-rdf.nquads_unified_diff</code>).
 *
 */
public class NQuadsUDAssembler implements RDFHandler {

    private static final int ADD = '+';
    private static Logger logger = LoggerFactory.getLogger(NQuadsUDAssembler.class);
    private final Repository repository;
    private Supplier<Integer> actionSupplier;

    private long plusStatementCount;
    private long minusStatementCount;
    private long addedStatementCount;
    private long removedStatementCount;

    public NQuadsUDAssembler(Repository repository) {
        this.repository = repository;
    }

    public void registerActionSupplier(Supplier<Integer> actionSupplier) {
        this.actionSupplier = actionSupplier;
    }

    public void add(URL url, String baseURI, RDFFormat dataFormat, Resource... contexts) throws IOException {
        try (RepositoryConnection con = repository.getConnection()) {
            con.add(url, baseURI, dataFormat, contexts);
        }
    }

    public void add(File file, String baseURI, RDFFormat dataFormat, Resource... contexts) throws IOException {
        try (RepositoryConnection con = repository.getConnection()) {
            con.add(file, baseURI, dataFormat, contexts);
        }
    }

    public void export(RDFHandler handler) {
        try (RepositoryConnection con = repository.getConnection()) {
            con.export(handler);
        }
    }

    @Override
    public void startRDF() throws RDFHandlerException {

    }

    @Override
    public void endRDF() throws RDFHandlerException {

    }

    @Override
    public void handleNamespace(String prefix, String uri) throws RDFHandlerException {
        try (RepositoryConnection con = repository.getConnection()) {
            con.setNamespace(prefix, uri);
        }
    }

    @Override
    public void handleStatement(Statement st) throws RDFHandlerException {
        try (RepositoryConnection con = repository.getConnection()) {
            if (isAssertion()) {
                plusStatementCount++;
                if (!con.hasStatement(st, false)) {
                    addedStatementCount++;
                    // logger.debug("Added: {}", st);
                }
                con.add(st);
            } else {
                minusStatementCount++;
                if (con.hasStatement(st, false)) {
                    removedStatementCount++;
                    // logger.debug("Removed: {}", st);
                }
                con.remove(st);
            }
        }
    }

    @Override
    public void handleComment(String comment) throws RDFHandlerException {

    }

    public long getPlusStatementCount() {
        return plusStatementCount;
    }

    public long getMinusStatementCount() {
        return minusStatementCount;
    }

    public long getAddedStatementCount() {
        return addedStatementCount;
    }

    public long getRemovedStatementCount() {
        return removedStatementCount;
    }

    public void reset() {
        plusStatementCount = 0;
        minusStatementCount = 0;
        addedStatementCount = 0;
        removedStatementCount = 0;
    }

    protected boolean isAssertion() {
        return actionSupplier == null || actionSupplier.get() == ADD;
    }
}
