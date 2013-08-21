//	---------------------------------------------------------------------------
//	jWebSocket - ConnectorsManager (Community Edition, CE)
//	---------------------------------------------------------------------------
//	Copyright 2010-2013 Innotrade GmbH (jWebSocket.org)
//  Alexander Schulze, Germany (NRW)
//
//	Licensed under the Apache License, Version 2.0 (the "License");
//	you may not use this file except in compliance with the License.
//	You may obtain a copy of the License at
//
//	http://www.apache.org/licenses/LICENSE-2.0
//
//	Unless required by applicable law or agreed to in writing, software
//	distributed under the License is distributed on an "AS IS" BASIS,
//	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//	See the License for the specific language governing permissions and
//	limitations under the License.
//	---------------------------------------------------------------------------
package org.jwebsocket.jms.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.util.HashMap;
import java.util.Map;
import org.apache.activemq.command.ActiveMQTempQueue;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.jms.Attributes;
import org.jwebsocket.jms.BaseConnectorsManager;
import org.jwebsocket.jms.ConnectorStatus;
import org.jwebsocket.jms.JMSConnector;
import org.springframework.util.Assert;

/**
 * Database based connectors manager.
 *
 * @author kyberneees
 */
public class MongoDBConnectorsManager extends BaseConnectorsManager {

	private DBCollection mCollection;

	public MongoDBConnectorsManager() {
	}

	@Override
	public JMSConnector addConnector(String aSessionId, String aIpAddress, String aReplyDest) throws Exception {
		if (!sessionExists(aSessionId)) {
			mCollection.save(new BasicDBObject()
					.append(Attributes.IP_ADDRESS, aIpAddress)
					.append(Attributes.SESSION_ID, aSessionId)
					.append(Attributes.REPLY_DESTINATION, aReplyDest)
					.append(Attributes.STATUS, ConnectorStatus.ONLINE));
		} else {
			mCollection.update(new BasicDBObject().append(Attributes.SESSION_ID, aSessionId),
					new BasicDBObject()
					.append("$set", new BasicDBObject()
					.append(Attributes.REPLY_DESTINATION, aReplyDest)
					.append(Attributes.STATUS, ConnectorStatus.ONLINE)));
		}
		return getConnector(aSessionId);
	}

	@Override
	public boolean sessionExists(String aSessionId) throws Exception {
		return null != mCollection.findOne(new BasicDBObject()
				.append(Attributes.STATUS, ConnectorStatus.ONLINE)
				.append(Attributes.SESSION_ID, aSessionId));
	}

	private JMSConnector toConnector(DBObject aRecord) throws Exception {
		String lSessionId = (String) aRecord.get(Attributes.SESSION_ID);

		JMSConnector lConnector = new JMSConnector(getEngine(), getReplyProducer(),
				(String) aRecord.get(Attributes.IP_ADDRESS),
				(String) aRecord.get(Attributes.SESSION_ID),
				new ActiveMQTempQueue(
				(String) aRecord.get(Attributes.REPLY_DESTINATION)));

		lConnector.getSession().setSessionId(lSessionId);
		lConnector.getSession().setStorage(getSessionManager().getSession(lSessionId));

		return lConnector;
	}

	@Override
	public JMSConnector getConnector(String aSessionId) throws Exception {
		if (!sessionExists(aSessionId)) {
			return null;
		}

		// getting the connector entry on database
		DBObject lRecord = mCollection.findOne(new BasicDBObject().append(Attributes.SESSION_ID, aSessionId));

		// creating connector from database entry
		return toConnector(lRecord);
	}

	@Override
	public void removeConnector(String aSessionId) throws Exception {
		mCollection.remove(new BasicDBObject().append(Attributes.SESSION_ID, aSessionId));
		getSessionManager().getSession(aSessionId).clear();
	}

	public void setCollection(DBCollection aCollection) {
		mCollection = aCollection;
	}

	public DBCollection getCollection() {
		return mCollection;
	}

	@Override
	public void setStatus(String aSessionId, int aStatus) throws Exception {
		Assert.isTrue(sessionExists(aSessionId), "The given session was not found!");
		mCollection.update(new BasicDBObject().append(Attributes.SESSION_ID, aSessionId),
				new BasicDBObject()
				.append("$set", new BasicDBObject()
				.append(Attributes.STATUS, aStatus)));
	}

	@Override
	public Map<String, WebSocketConnector> getConnectors() throws Exception {
		DBCursor lCursor = mCollection.find(new BasicDBObject().append(Attributes.STATUS, ConnectorStatus.ONLINE));

		Map<String, WebSocketConnector> lConnectors = new HashMap<String, WebSocketConnector>();
		while (lCursor.hasNext()) {
			WebSocketConnector lConnector = toConnector(lCursor.next());
			lConnectors.put(lConnector.getId(), lConnector);
		}

		return lConnectors;
	}

	@Override
	public void initialize() throws Exception {
		super.initialize();

		mCollection.ensureIndex(new BasicDBObject().append(Attributes.SESSION_ID, 1),
				new BasicDBObject().append("unique", true));
	}
}