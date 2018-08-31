package nl.knaw.dans.repo.arrdf.http;

import nl.knaw.dans.repo.arrdf.xml.Capability;
import nl.knaw.dans.repo.arrdf.xml.ResourceSyncContext;
import nl.knaw.dans.repo.arrdf.xml.RsLn;
import nl.knaw.dans.repo.arrdf.xml.Sitemapindex;
import nl.knaw.dans.repo.arrdf.xml.UrlItem;
import nl.knaw.dans.repo.arrdf.xml.Urlset;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * Created on 2018-08-23 11:12.
 */
public class RsNavigator {

    private static Logger logger = LoggerFactory.getLogger(RsNavigator.class);

    private final CapabilityListener capabilityListener;
    private CloseableHttpClient httpClient;
    private ResourceSyncContext rsContext;
    private RsDocumentReader docReader;

    public RsNavigator(CapabilityListener capabilityListener) {
        this.capabilityListener = capabilityListener;
    }


    public RsNavigator withHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
        return this;
    }

    public RsNavigator withRsContect(ResourceSyncContext rsContext) {
        this.rsContext = rsContext;
        return this;
    }

    private CloseableHttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = HttpClients.createDefault();
        }
        return httpClient;
    }

    private ResourceSyncContext getRsContext() {
        if (rsContext == null) {
            try {
                rsContext = new ResourceSyncContext();
            } catch (JAXBException e) {
                throw new RuntimeException(e);
            }
        }
        return rsContext;
    }

    private RsDocumentReader getDocReader() {
        if (docReader == null) {
            docReader = new RsDocumentReader(getHttpClient(), getRsContext());
        }
        return docReader;
    }

    public void navigate(URI uri) throws URISyntaxException {
        RsDocumentReader reader = getDocReader();
        reader.read(uri.toString());

        Optional<Sitemapindex> maybeIndex = reader.getLatestSitemapindex();
        if (maybeIndex.isPresent()) {
            logger.warn("Indexed lists not implemented.");
        }

        Optional<Urlset> maybeSet = reader.getLatestUrlset();
        if (maybeSet.isPresent()) {
            Urlset urlset = maybeSet.get();
            Capability setCapability = urlset.getCapability().orElse(null);

            if (Capability.DESCRIPTION.equals(setCapability)) {
                for (UrlItem item : urlset.getItemList()) {
                    URI capabilityListURI = new URI(item.getLoc());
                    if (capabilityListener.accept(capabilityListURI)) {
                        capabilityListener.onCapabilityListItem(item);
                        Optional<RsLn> maybeDescribedBy = item.getLink(RsLn.REL_DESCRIBEDBY);
                        maybeDescribedBy.ifPresent(rsLn -> capabilityListener.onDescribedBy(capabilityListURI, rsLn));
                        navigate(capabilityListURI);
                    }

                }
            }

            else if (Capability.CAPABILITYLIST.equals(setCapability)) {
                if (capabilityListener.accept(uri)) {
                    Optional<RsLn> maybeDescribedBy = urlset.getLink(RsLn.REL_DESCRIBEDBY);
                    maybeDescribedBy.ifPresent(rsLn -> capabilityListener.onDescribedBy(uri, rsLn));
                    for (UrlItem item : urlset.getItemList()) {
                        navigate(uri, item.getLoc());
                    }
                }
            }

            else {
                logger.warn("Capability not implemented. capability={}, URI={}", setCapability, uri);
            }
        }
    }

    private void navigate(URI capabilityListURI, String itemUrl) throws URISyntaxException {
        RsDocumentReader reader = getDocReader();
        reader.read(itemUrl);

        Optional<Urlset> maybeSet = reader.getLatestUrlset();
        if (maybeSet.isPresent()) {
            Urlset urlset = maybeSet.get();
            Capability setCapability = urlset.getCapability().orElse(null);

            if (Capability.RESOURCELIST.equals(setCapability)) {
                capabilityListener.onResourceList(capabilityListURI, urlset);
            } else if (Capability.CHANGELIST.equals(setCapability)) {
                capabilityListener.onChangeList(capabilityListURI, urlset);
            }

        }
    }


}
