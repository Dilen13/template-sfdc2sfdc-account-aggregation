package org.mule.kicks.transformers;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;

/**
 * This transformer will sort a list of map defining a weight for each map base
 * on the value of its keys.
 * 
 * @author damian.sima
 */
public class SortAccountsList extends AbstractMessageTransformer {
	private static final String IDENTITY_FIELD_KEY = "Name";

	@SuppressWarnings("unchecked")
	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {

		List<Map<String, String>> sortedUsersList = (List<Map<String, String>>) message.getPayload();

		Collections.sort(sortedUsersList, accountMapComparator);

		return sortedUsersList;
	}

	public static Comparator<Map<String, String>> accountMapComparator = new Comparator<Map<String, String>>() {

		public int compare(Map<String, String> user1, Map<String, String> user2) {

			String key1 = buildKey(user1);
			String key2 = buildKey(user2);

			return key1.compareTo(key2);
		}

		private String buildKey(Map<String, String> user) {
			StringBuilder key = new StringBuilder();

			if (StringUtils.isNotBlank(user.get("IDInA")) && StringUtils.isNotBlank(user.get("IDInB"))) {
				key.append("~~");
				key.append(user.get("IDInA"));
				key.append(user.get("IDInB"));
				key.append(user.get(IDENTITY_FIELD_KEY));
			}

			if (StringUtils.isNotBlank(user.get("IDInA")) && StringUtils.isBlank(user.get("IDInB"))) {
				key.append(user.get("IDInA"));
				key.append("~");
				key.append(user.get(IDENTITY_FIELD_KEY));
			}

			if (StringUtils.isBlank(user.get("IDInA")) && StringUtils.isNotBlank(user.get("IDInB"))) {
				key.append("~");
				key.append(user.get("IDInB"));
				key.append(user.get(IDENTITY_FIELD_KEY));
			}

			return key.toString();
		}

	};

}
