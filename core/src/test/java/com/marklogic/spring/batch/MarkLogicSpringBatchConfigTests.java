package com.marklogic.spring.batch;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.admin.ServerConfigurationManager.UpdatePolicy;
import com.marklogic.client.helper.DatabaseClientProvider;

public class MarkLogicSpringBatchConfigTests extends AbstractSpringBatchTest {
	
	@Autowired
	DatabaseClientProvider databaseClientProvider;
	
	@Test
	public void isVersionOptimalUpdatePolicyTest() {
		
		DatabaseClient client = databaseClientProvider.getDatabaseClient();

		// create server config manager
		ServerConfigurationManager configMgr = client.newServerConfigManager();

		// read the server config from the database
		configMgr.readConfiguration();

		assertEquals(UpdatePolicy.VERSION_OPTIONAL, configMgr.getUpdatePolicy());

		// release the client
		//client.release();
	}

}
