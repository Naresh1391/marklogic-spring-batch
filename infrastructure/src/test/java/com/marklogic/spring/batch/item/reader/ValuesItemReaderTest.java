package com.marklogic.spring.batch.item.reader;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.helper.DatabaseClientConfig;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.CountedDistinctValue;
import com.marklogic.junit.ClientTestHelper;
import com.marklogic.junit.spring.AbstractSpringTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = { com.marklogic.spring.batch.config.MarkLogicApplicationContext.class })
public class ValuesItemReaderTest extends AbstractSpringTest {

    private ValuesItemReader reader;
    ClientTestHelper helper;
    DatabaseClient client;

    @Autowired
    DatabaseClientConfig databaseClientConfig;

    @Before
    public void setup() {
        DatabaseClientFactory.SecurityContext securityContext = new DatabaseClientFactory.DigestAuthContext(databaseClientConfig.getUsername(), databaseClientConfig.getPassword());
        client = DatabaseClientFactory.newClient(databaseClientConfig.getHost(), databaseClientConfig.getPort(), securityContext);
        helper = new ClientTestHelper();
        helper.setDatabaseClientProvider(getClientProvider());
        XMLDocumentManager docMgr = client.newXMLDocumentManager();
        StringHandle xml1 = new StringHandle("<hello />");
        DocumentMetadataHandle metadata = new DocumentMetadataHandle();
        metadata.withCollections("a");
    
        DocumentMetadataHandle metadata2 = new DocumentMetadataHandle();
        metadata2.withCollections("sourceXML");
        
        for (int i = 0; i < 10; i++) {
            DocumentMetadataHandle h = (i % 2 == 0) ? metadata : metadata2;
            docMgr.write("hello" + i + ".xml", h, xml1);
        }
        helper.assertCollectionSize("", "a", 5);
        helper.assertCollectionSize("", "sourceXML", 5);
    }
    
    @Test
    public void getValuesFromItemReaderTest() throws Exception {
        reader = new ValuesItemReader(client);
        reader.open(null);
        assertEquals("Expecting size of 5", reader.getLength(), 5);
        CountedDistinctValue val = reader.read();
        String uri = val.get("xs:string", String.class);
        logger.info(uri);
        assertTrue(uri.equals("hello1.xml"));
        val = reader.read();
        uri = val.get("xs:string", String.class);
        assertTrue(uri.equals("hello3.xml"));
    }
}
