package nl.knaw.dans.repo.arrdf.http;


import nl.knaw.dans.repo.arrdf.util.LambdaUtil;
import nl.knaw.dans.repo.arrdf.xml.ResourceSyncContext;
import nl.knaw.dans.repo.arrdf.xml.RsBuilder;
import nl.knaw.dans.repo.arrdf.xml.RsRoot;
import nl.knaw.dans.repo.arrdf.xml.Sitemapindex;
import nl.knaw.dans.repo.arrdf.xml.Urlset;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class RsDocumentReader extends AbstractUriReader {

    private final ResourceSyncContext rsContext;
    private final RsBuilder rsBuilder;
    private LambdaUtil.BiFunction_WithExceptions<URI, HttpResponse, RsRoot, Exception> rsConverter =
      (uri, response) -> {
          InputStream inStream = null;
          if (response.getEntity() != null) {
              inStream = response.getEntity().getContent();
          }
          return getRsBuilder().setInputStream(inStream).build().orElse(null);
      };

    public RsDocumentReader(CloseableHttpClient httpClient, ResourceSyncContext rsContext) {
        super(httpClient);
        this.rsContext = rsContext;
        rsBuilder = new RsBuilder(rsContext);
    }

    public Result<RsRoot> read(String url) throws URISyntaxException {
        URI uri = new URI(url);
        return read(uri);
    }

    public Result<RsRoot> read(URI uri) {
        return execute(uri, rsConverter);
    }

    public Optional<Urlset> getLatestUrlset() {
        return rsBuilder.getUrlset();
    }

    public Optional<Sitemapindex> getLatestSitemapindex() {
        return rsBuilder.getSitemapindex();
    }

    private ResourceSyncContext getRsContext() {
        return rsContext;
    }

    private RsBuilder getRsBuilder() {
        return rsBuilder;
    }

}
