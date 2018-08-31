package nl.knaw.dans.repo.arrdf.nqud;

import nl.knaw.dans.repo.arrdf.http.CapabilityListener;
import nl.knaw.dans.repo.arrdf.xml.RsLn;
import nl.knaw.dans.repo.arrdf.xml.UrlItem;
import nl.knaw.dans.repo.arrdf.xml.Urlset;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created on 2018-08-27 10:02.
 */
public class TimbuctooListener implements CapabilityListener {

    private static Logger logger = LoggerFactory.getLogger(TimbuctooListener.class);

    private final List<String> acceptedURLs;

    private final Map<URI, DatasetManager> datasetManagerMap = new HashMap<>();

    private DatasetManager currentManager;

    public TimbuctooListener(List<String> acceptedURLs) {
        this.acceptedURLs = acceptedURLs;
    }

    @Override
    public boolean accept(URI capabilityListURI) {
        boolean accepted = acceptedURLs.contains(capabilityListURI.toString());
        if (accepted) {
            currentManager = getManager(capabilityListURI);
        }
        return accepted;
    }

    @Override
    public void onCapabilityListItem(UrlItem item) {

    }

    @Override
    public void onDescribedBy(URI capabilityListURI, RsLn rsLn) {
        if (isNotCurrent(capabilityListURI)) {
            logger.warn("Not the current uri: {}", capabilityListURI);
            return;
        }
        URI descriptionURI = URI.create(rsLn.getHref());
        String filename = FilenameUtils.getName(descriptionURI.getPath());

        RDFFormat rdfFormat = null;
        Optional<String> maybeType = rsLn.getType();
        if (maybeType.isPresent()) {
            rdfFormat = Rio.getParserFormatForMIMEType(maybeType.get()).orElse(null);
        }
        if (rdfFormat == null) {
            rdfFormat = Rio.getParserFormatForFileName(filename).orElse(null);
        }
        if (rdfFormat == null) {
            rdfFormat = RDFFormat.RDFXML;
        }
        currentManager.onDescribedBy(descriptionURI, rdfFormat);
    }

    @Override
    public void onResourceList(URI capabilityListURI, Urlset urlset) {
        if (isNotCurrent(capabilityListURI)) {
            logger.warn("Not the current uri: {}", capabilityListURI);
            return;
        }
        currentManager.onResourceList(urlset);
    }

    @Override
    public void onChangeList(URI capabilityListURI, Urlset urlset) {
        if (isNotCurrent(capabilityListURI)) {
            logger.warn("Not the current uri: {}", capabilityListURI);
            return;
        }
        currentManager.onChangeList(urlset);
    }

    protected DatasetManager getManager(URI uri) {
        return datasetManagerMap.computeIfAbsent(uri, dm -> new DatasetManager(uri));
    }

    private boolean isNotCurrent(URI uri) {
        return currentManager == null || !uri.equals(currentManager.getCapabilityListURI());
    }
}
