package nl.knaw.dans.repo.arrdf.http;

import nl.knaw.dans.repo.arrdf.xml.RsLn;
import nl.knaw.dans.repo.arrdf.xml.UrlItem;
import nl.knaw.dans.repo.arrdf.xml.Urlset;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * Created on 2018-08-23 14:06.
 */
public interface CapabilityListener {

    boolean accept(URI capabilityListURI);

    void onCapabilityListItem(UrlItem item);

    void onDescribedBy(URI capabilityListURI, RsLn rsLn);

    void onResourceList(URI capabilityListURI, Urlset urlset);

    void onChangeList(URI capabilityListURI, Urlset urlset);
}
