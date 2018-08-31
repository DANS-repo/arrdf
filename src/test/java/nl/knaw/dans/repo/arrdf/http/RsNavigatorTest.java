package nl.knaw.dans.repo.arrdf.http;

import nl.knaw.dans.repo.arrdf.xml.RsLn;
import nl.knaw.dans.repo.arrdf.xml.UrlItem;
import nl.knaw.dans.repo.arrdf.xml.Urlset;
import org.junit.Test;

import java.net.URI;

import static org.junit.Assume.assumeTrue;

/**
 * Created on 2018-08-23 16:08.
 */
public class RsNavigatorTest {

    @Test
    public void navigate() throws Exception {
        assumeTrue("Live testing is off.", Testing.LIVE_TESTS);

        String url = "https://data.anansi.clariah.nl/.well-known/resourcesync";
        String dwcCL = "https://data.anansi.clariah.nl/v5/resourcesync/u74ccc032adf8422d7ea92df96cd4783f0543db3b/dwc/capabilitylist.xml";
        String nlgisCL = "https://data.anansi.clariah.nl/v5/resourcesync/u74ccc032adf8422d7ea92df96cd4783f0543db3b/nlgis/capabilitylist.xml";

        String someRresorceList = "https://data.anansi.clariah.nl/v5/resourcesync/u74ccc032adf8422d7ea92df96cd4783f0543db3b/tbi/resourcelist.xml";

        String noRs = "https://github.com/DANS-repo/arrdf";

        RsNavigator navigator = new RsNavigator(new CapabilityListener() {
            @Override
            public boolean accept(URI capabilityListURI) {
                System.out.println("accept " + capabilityListURI);
                return dwcCL.equals(capabilityListURI.toString());
            }

            @Override
            public void onCapabilityListItem(UrlItem item) {
                System.out.println("onCapabilityListItem " + item);
            }

            @Override
            public void onDescribedBy(URI capabilityListUrl, RsLn rsLn) {
                System.out.println("onDescribedBy " + rsLn.getHref());
            }

            @Override
            public void onResourceList(URI capabilityListUrl, Urlset urlset) {
                System.out.println("onResourceList " + urlset);
            }

            @Override
            public void onChangeList(URI capabilityListUrl, Urlset urlset) {
                System.out.println("onChangeList " + urlset);
            }
        });

        navigator.navigate(new URI(url));
        //navigator.navigate(dwcCL);
        //navigator.navigate(nlgisCL);
        //navigator.navigate(someResourceList); // WARN  n.k.dans.repo.arrdf.http.RsNavigator - Capability not implemented.

        //navigator.navigate(noRs); // throws javax.xml.bind.UnmarshalException
    }
}
