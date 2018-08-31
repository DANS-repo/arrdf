package nl.knaw.dans.repo.arrdf.nqud;

import nl.knaw.dans.repo.arrdf.xml.Urlset;
import org.eclipse.rdf4j.rio.RDFFormat;

import java.net.URI;

/**
 * Created on 2018-08-28 10:56.
 */
public class DatasetManager {

    private final URI capabilityListURI;

    public DatasetManager(URI capabilityListURI) {
        this.capabilityListURI = capabilityListURI;
    }

    public URI getCapabilityListURI() {
        return capabilityListURI;
    }

    public void onDescribedBy(URI descriptionURI, RDFFormat rdfFormat) {

    }

    public void onResourceList(Urlset urlset) {

    }

    public void onChangeList(Urlset urlset) {
        // not implemented
    }
}
