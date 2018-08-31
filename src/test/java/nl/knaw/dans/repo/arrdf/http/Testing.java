package nl.knaw.dans.repo.arrdf.http;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import java.net.URL;

/**
 * Created on 2017-04-12 17:27.
 */
public class Testing {

    public static boolean LIVE_TESTS = false
      ;

    @Test
    public void tt() throws Exception {
        System.out.println(FilenameUtils.getName(new URL("http://google.com").getPath()).equals(""));
        System.out.println(FilenameUtils.getName("https://data.anansi.clariah.nl/v5/resourcesync/u74ccc032adf8422d7ea92df96cd4783f0543db3b/gemeentegeschiedenisnl/description.xml"));
    }


}
