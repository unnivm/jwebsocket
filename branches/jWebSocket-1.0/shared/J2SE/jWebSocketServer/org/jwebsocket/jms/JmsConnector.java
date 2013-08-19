//	---------------------------------------------------------------------------
//	jWebSocket - JMS Connector (Community Edition, CE)
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
package org.jwebsocket.jms;

import java.net.InetAddress;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.log4j.Logger;
import org.jwebsocket.api.IPacketDeliveryListener;
import org.jwebsocket.api.WebSocketEngine;
import org.jwebsocket.api.WebSocketPacket;
import org.jwebsocket.connectors.BaseConnector;
import org.jwebsocket.kit.RequestHeader;
import org.jwebsocket.logging.Logging;

/**
 * JMS Connector
 *
 * @author Alexander Schulze
 * @author kyberneees
 */
public class JMSConnector extends BaseConnector {

	private final String mSessionId;
	private final String mRemoteHost;
	private final MessageProducer mReplyProducer;
	private final String mReplyDestination;
	private Logger mLog = Logging.getLogger();

	/**
	 *
	 * @param aEngine
	 * @param aJMSSender
	 * @param aRemoteHost
	 * @param aSessionId
	 */
	public JMSConnector(WebSocketEngine aEngine, MessageProducer aReplyProducer, String aReplyDestination,
			String aRemoteHost, String aSessionId) {
		super(aEngine);

		RequestHeader lHeader = new RequestHeader();
		lHeader.put(RequestHeader.WS_PROTOCOL, "org.jwebsocket.json");
		setHeader(lHeader);

		mReplyProducer = aReplyProducer;
		mRemoteHost = aRemoteHost;
		mSessionId = aSessionId;
		mReplyDestination = aReplyDestination;
	}

	@Override
	public String getId() {
		return mSessionId;
	}

	@Override
	public InetAddress getRemoteHost() {
		try {
			return InetAddress.getByName(mRemoteHost);
		} catch (Exception lEx) {
			return null;
		}
	}

	@Override
	public void sendPacket(final WebSocketPacket aDataPacket) {
		try {
			TextMessage lMessage = new ActiveMQTextMessage();
			lMessage.setText(aDataPacket.getString());

			mReplyProducer.send(ActiveMQDestination.createDestination(mReplyDestination,
					ActiveMQDestination.TEMP_QUEUE_TYPE),
					lMessage);
		} catch (Exception lEx) {
			mLog.error(Logging.getSimpleExceptionMessage(lEx, "sending packet to '" + getId() + "' connector"));
		}
	}

	@Override
	public void sendPacketInTransaction(WebSocketPacket aDataPacket, IPacketDeliveryListener aListener) {
		throw new UnsupportedOperationException("Not supported operation on JMS connectors!"); 
	}

	@Override
	public void sendPacketInTransaction(WebSocketPacket aDataPacket, Integer aFragmentSize, IPacketDeliveryListener aListener) {
		throw new UnsupportedOperationException("Not supported operation on JMS connectors!");
	}
}