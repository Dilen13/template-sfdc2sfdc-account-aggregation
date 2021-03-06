/**
 * Mule Anypoint Template
 * Copyright (c) MuleSoft, Inc.
 * All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates.transformers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mule.api.MuleContext;
import org.mule.api.transformer.TransformerException;

@RunWith(MockitoJUnitRunner.class)
public class SFDCAccountsMergeTest {
	
	@Mock
	private MuleContext muleContext;

	@Test
	public void testMerge() throws TransformerException {
		List<Map<String, String>> accountsA = createAccountLists("A", 0, 1);
		List<Map<String, String>> accountsB = createAccountLists("B", 1, 2);

		SFDCAccountsMerge sfdcAccountMerge = new SFDCAccountsMerge();
		List<Map<String, String>> mergedList = sfdcAccountMerge.mergeList(accountsA, accountsB);

		Assert.assertEquals("The merged list obtained is not as expected",
				createExpectedList(), mergedList);
	}

	static List<Map<String, String>> createExpectedList() {

		Map<String, String> account0 = createEmptyMergedRecord(0);
		account0.put("IDInA", "0");
		account0.put("IndustryInA", "Goverment");
		account0.put("NumberOfEmployeesInA", "550.0");

		Map<String, String> account1 = createEmptyMergedRecord(1);
		account1.put("IDInA", "1");
		account1.put("IndustryInA", "Goverment");
		account1.put("NumberOfEmployeesInA", "550.0");
		account1.put("IDInB", "1");
		account1.put("IndustryInB", "Goverment");
		account1.put("NumberOfEmployeesInB", "550.0");

		Map<String, String> account2 = createEmptyMergedRecord(2);
		account2.put("IDInB", "2");
		account2.put("IndustryInB", "Goverment");
		account2.put("NumberOfEmployeesInB", "550.0");

		List<Map<String, String>> accountList = new ArrayList<Map<String, String>>();
		accountList.add(account0);
		accountList.add(account1);
		accountList.add(account2);

		return accountList;
	}

	static Map<String, String> createEmptyMergedRecord(Integer secuense) {
		Map<String, String> account = new HashMap<String, String>();
		account.put("Name", "SomeName_" + secuense);
		account.put("IDInA", "");
		account.put("IndustryInA", "");
		account.put("NumberOfEmployeesInA", "");
		account.put("IDInB", "");
		account.put("IndustryInB", "");
		account.put("NumberOfEmployeesInB", "");
		return account;
	}

	static List<Map<String, String>> createAccountLists(String orgId,
			int start, int end) {
		List<Map<String, String>> accountList = new ArrayList<Map<String, String>>();
		for (int i = start; i <= end; i++) {
			accountList.add(createAccount(orgId, i));
		}
		return accountList;
	}

	static Map<String, String> createAccount(String orgId, int sequence) {
		Map<String, String> account = new HashMap<String, String>();

		account.put("Id", new Integer(sequence).toString());
		account.put("Name", "SomeName_" + sequence);
		account.put("Industry", "Goverment");
		account.put("NumberOfEmployees", "550.0");

		return account;
	}
}
