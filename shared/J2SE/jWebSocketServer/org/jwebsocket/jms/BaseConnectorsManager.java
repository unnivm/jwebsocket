package org.jwebsocket.jms;

import javax.jms.MessageProducer;
import org.jwebsocket.api.ISessionManager;
import org.jwebsocket.factory.JWebSocketFactory;
import org.jwebsocket.jms.api.IConnectorsManager;
import org.jwebsocket.plugins.system.SystemPlugIn;
import org.springframework.util.Assert;

/**
 *
 * @author kyberneees
 */
public abstract class BaseConnectorsManager implements IConnectorsManager {

	private ISessionManager mSessionManager;
	private JMSEngine mEngine;
	private MessageProducer mReplyProducer;

	public JMSEngine getEngine() {
		return mEngine;
	}

	public ISessionManager getSessionManager() {
		return mSessionManager;
	}

	@Override
	public void setEngine(JMSEngine aEngine) {
		mEngine = aEngine;
	}

	public MessageProducer getReplyProducer() {
		return mReplyProducer;
	}

	@Override
	public void setReplyProducer(MessageProducer aReplyProducer) {
		mReplyProducer = aReplyProducer;
	}

	@Override
	public void initialize() throws Exception {
		SystemPlugIn lPlugIn = (SystemPlugIn) JWebSocketFactory.getTokenServer()
				.getPlugInById("jws.system");
		mSessionManager = lPlugIn.getSessionManager();

		Assert.notNull(mSessionManager, "The system plug-in 'sessionManager' is not properly configured!");
		Assert.notNull(mEngine, "The 'engine' reference cannot be null!");
		Assert.notNull(mReplyProducer, "The 'reply-producer' reference cannot be null!");
	}

	@Override
	public void shutdown() throws Exception {
		getReplyProducer().close();
	}
}