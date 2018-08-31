package nl.knaw.dans.repo.arrdf.nqud;

import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created on 2018-08-28 12:10.
 */
public class TimbuctooListenerTest {

    @Test
    public void getManager() throws Exception {
        TimbuctooListener tl = new TimbuctooListener(null);
        DatasetManager manager = tl.getManager(new URI("http://example.com"));
        assertNotNull(manager);
        assertEquals(new URI("http://example.com"), manager.getCapabilityListURI());
    }


}
