package nl.knaw.dans.repo.arrdf.nqud;

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.function.Supplier;

/**
 * Created on 2018-08-09 09:48.
 */
public class NQuadsUDAssembler implements RDFHandler {

    private static final int ADD = '+';

    private final Repository repository;
    private Supplier<Integer> actionSupplier;

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
                con.add(st);
            } else {
                con.remove(st);
            }
        }
    }

    @Override
    public void handleComment(String comment) throws RDFHandlerException {

    }

    private boolean isAssertion() {
        return actionSupplier == null || actionSupplier.get() == ADD;
    }
}
