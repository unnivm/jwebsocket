package org.jwebsocket.jms.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.jwebsocket.jms.Attributes;
import org.jwebsocket.jms.NodeStatus;
import org.jwebsocket.jms.api.INodesManager;
import org.springframework.util.Assert;

/**
 *
 * @author kyberneees
 */
public class MongoDBNodesManager implements INodesManager {

	private DBCollection mCollection;
	private String mNodeDescription;

	public DBCollection getCollection() {
		return mCollection;
	}

	public void setCollection(DBCollection aCollection) {
		mCollection = aCollection;
	}

	@Override
	public void register(String aSessionId, String aNodeId, String aDescription,
			String aIpAddress, double aCpuUsage) throws Exception {
		if (exists(aNodeId)) {
			mCollection.update(new BasicDBObject().append(Attributes.NODE, aNodeId),
					new BasicDBObject()
					.append("$set", new BasicDBObject()
					.append(Attributes.SESSION_ID, aSessionId)
					.append(Attributes.DESCRIPTION, aDescription)
					.append(Attributes.IP_ADDRESS, aIpAddress)
					.append(Attributes.STATUS, NodeStatus.ONLINE)
					.append(Attributes.CPU, aCpuUsage)));
		} else {
			mCollection.save(new BasicDBObject()
					.append(Attributes.NODE, aNodeId)
					.append(Attributes.SESSION_ID, aSessionId)
					.append(Attributes.DESCRIPTION, aDescription)
					.append(Attributes.IP_ADDRESS, aIpAddress)
					.append(Attributes.STATUS, NodeStatus.ONLINE)
					.append(Attributes.CPU, aCpuUsage));
		}
	}

	@Override
	public boolean exists(String aNodeId) throws Exception {
		return null != mCollection.findOne(new BasicDBObject().append(Attributes.NODE, aNodeId));
	}

	@Override
	public void updateCPU(String aNodeId, double aCpuUsage) throws Exception {
		Assert.isTrue(exists(aNodeId), "The target node does not exists!");

		mCollection.update(new BasicDBObject().append(Attributes.NODE, aNodeId), new BasicDBObject()
				.append("$set", new BasicDBObject()
				.append(Attributes.CPU, aCpuUsage)));
	}

	@Override
	public void setStatus(String aNodeId, int aStatus) throws Exception {
		Assert.isTrue(exists(aNodeId), "The target node does not exists!");

		mCollection.update(new BasicDBObject().append(Attributes.NODE, aNodeId), new BasicDBObject()
				.append("$set", new BasicDBObject()
				.append(Attributes.STATUS, aStatus)));
	}

	@Override
	public String getOptimumNode() throws Exception {
		DBCursor lCursor = mCollection.find(new BasicDBObject().append(Attributes.STATUS, NodeStatus.ONLINE))
				.sort(new BasicDBObject().append(Attributes.CPU, 1)
				.append(Attributes.REQUESTS, 1)).limit(1);

		String lNodeId = null;
		if (lCursor.hasNext()) {
			lNodeId = (String) lCursor.next().get(Attributes.NODE);
		}

		return lNodeId;
	}

	@Override
	public void increaseRequests(String aNodeId) throws Exception {
		Assert.isTrue(exists(aNodeId), "The target node does not exists!");

		mCollection.update(new BasicDBObject().append(Attributes.NODE, aNodeId), new BasicDBObject()
				.append("$inc", new BasicDBObject()
				.append(Attributes.REQUESTS, 1)));
	}

	@Override
	public String getNodeId(String aSessionId) throws Exception {
		DBObject lRecord = mCollection.findOne(new BasicDBObject().append(Attributes.SESSION_ID, aSessionId));
		if (null != lRecord) {
			return (String) lRecord.get(Attributes.NODE);
		}

		return null;
	}

	@Override
	public void initialize() throws Exception {
		// creating index for CPU and REQUESTS fields for sorting
		mCollection.ensureIndex(new BasicDBObject().append(Attributes.CPU, 1).append(Attributes.REQUESTS, 1));

		// setting SESSION_ID as primary key
		mCollection.ensureIndex(new BasicDBObject().append(Attributes.SESSION_ID, 1),
				new BasicDBObject().append("unique", true));

		// setting NODE id as primary key
		mCollection.ensureIndex(new BasicDBObject().append(Attributes.NODE, 1),
				new BasicDBObject().append("unique", true));
	}

	@Override
	public void setNodeDescription(String aNodeDescription) {
		mNodeDescription = aNodeDescription;
	}

	@Override
	public String getNodeDescription() {
		return mNodeDescription;
	}

	@Override
	public void shutdown() throws Exception {
	}

	@Override
	public long count() {
		return mCollection.count(new BasicDBObject().append(Attributes.STATUS, NodeStatus.ONLINE));
	}
}