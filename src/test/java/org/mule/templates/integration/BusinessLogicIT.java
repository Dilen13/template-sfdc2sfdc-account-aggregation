/**
 * Mule Anypoint Template
 * Copyright (c) MuleSoft, Inc.
 * All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates.integration;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleEvent;
import org.mule.processor.chain.SubflowInterceptingChainLifecycleWrapper;
import org.mule.tck.junit4.rule.DynamicPort;
import org.mule.templates.builders.SfdcObjectBuilder;

import com.sforce.soap.partner.SaveResult;

/**
 * The objective of this class is to validate the correct behavior of the flows
 * for this Mule Template that make calls to external systems.
 * 
 */
public class BusinessLogicIT extends AbstractTemplateTestCase {
	protected static final String TEMPLATE_NAME = "account-aggregation";
	
	private List<Map<String, Object>> createdAccountsInA = new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> createdAccountsInB = new ArrayList<Map<String, Object>>();

	@Rule
	public DynamicPort port = new DynamicPort("http.port");

	@Before
	public void setUp() throws Exception {

		createAccounts();
	}

	@SuppressWarnings("unchecked")
	private void createAccounts() throws Exception {
		SubflowInterceptingChainLifecycleWrapper flow = getSubFlow("createAccountInAFlow");
		flow.initialise();

		Map<String, Object> account = createAccount("A", 0);
		createdAccountsInA.add(account);

		MuleEvent event = flow.process(getTestEvent(createdAccountsInA,
				MessageExchangePattern.REQUEST_RESPONSE));
		List<SaveResult> results = (List<SaveResult>) event.getMessage()
				.getPayload();
		for (int i = 0; i < results.size(); i++) {
			createdAccountsInA.get(i).put("Id", results.get(i).getId());
		}

		flow = getSubFlow("createAccountInBFlow");
		flow.initialise();

		account = createAccount("B", 0);
		createdAccountsInB.add(account);

		event = flow.process(getTestEvent(createdAccountsInB,
				MessageExchangePattern.REQUEST_RESPONSE));
		results = (List<SaveResult>) event.getMessage().getPayload();

		for (int i = 0; i < results.size(); i++) {
			createdAccountsInB.get(i).put("Id", results.get(i).getId());
		}
	}

	protected Map<String, Object> createAccount(String orgId, int sequence) {
		return SfdcObjectBuilder
				.anAccount()
				.with("Name",
						buildUniqueName(TEMPLATE_NAME, "_Name_" + sequence
								+ "_")).with("Industry", "Education")
				.with("Description", "Some account description").build();
	}

	protected String buildUniqueName(String templateName, String name) {
		String timeStamp = new Long(new Date().getTime()).toString();

		StringBuilder builder = new StringBuilder();
		builder.append(name);
		builder.append(templateName);
		builder.append(timeStamp);

		return builder.toString();
	}

	@After
	public void tearDown() throws Exception {

		deleteTestAccountFromSandBox(createdAccountsInA,
				"deleteAccountFromAFlow");
		deleteTestAccountFromSandBox(createdAccountsInB,
				"deleteAccountFromBFlow");

	}

	protected void deleteTestAccountFromSandBox(
			List<Map<String, Object>> createdAccounts, String deleteFlow)
			throws Exception {
		List<String> idList = new ArrayList<String>();

		// Delete the created accounts in A
		SubflowInterceptingChainLifecycleWrapper flow = getSubFlow(deleteFlow);
		flow.initialise();
		for (Map<String, Object> c : createdAccounts) {
			idList.add((String) c.get("Id"));
		}
		flow.process(getTestEvent(idList,
				MessageExchangePattern.REQUEST_RESPONSE));
		idList.clear();

	}
	
	@Test
	public void testMainFlow() throws Exception {
		MuleEvent event = runFlow("mainFlow");
		Assert.assertTrue("The payload should not be null.", "Please find attached your Accounts Report".equals(event.getMessage().getPayload()));
	}
	
	@Test
	public void testGatherDataFlow() throws Exception {
		MuleEvent event = runFlow("gatherDataFlow");
		Iterator<Map<String, String>> mergedList = (Iterator<Map<String, String>>)event.getMessage().getPayload();
		Assert.assertTrue("There should be contacts from source A or source B.", mergedList.hasNext());
	}

}
