//  ---------------------------------------------------------------------------
//  jWebSocket - JMS Gateway Demo Client (Community Edition, CE)
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
package tld.yourname.jms.demo;

import java.util.Properties;
import java.util.UUID;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jwebsocket.jms.client.JMSClient;

/**
 * JMS Gateway Demo Client
 *
 * @author Alexander Schulze
 */
public class JMSDemoServer {

	static final Logger mLog = Logger.getLogger(JMSDemoServer.class);

	/**
	 *
	 * @param aArgs
	 */
	public static void main(String[] aArgs) {

		// set up log4j logging
		// later this should be read from a shared log4j properties or xml file!
		Properties lProps = new Properties();
		lProps.setProperty("log4j.rootLogger", "INFO, console");
		lProps.setProperty("log4j.logger.org.apache.activemq.spring", "WARN");
		lProps.setProperty("log4j.logger.org.apache.activemq.web.handler", "WARN");
		lProps.setProperty("log4j.logger.org.springframework", "WARN");
		lProps.setProperty("log4j.logger.org.apache.xbean", "WARN");
		lProps.setProperty("log4j.logger.org.apache.camel", "INFO");
		lProps.setProperty("log4j.logger.org.eclipse.jetty", "WARN");
		lProps.setProperty("log4j.appender.console", "org.apache.log4j.ConsoleAppender");
		lProps.setProperty("log4j.appender.console.layout", "org.apache.log4j.PatternLayout");
		lProps.setProperty("log4j.appender.console.layout.ConversionPattern", "%p: %m%n");
		lProps.setProperty("log4j.appender.console.threshold", "INFO");
		PropertyConfigurator.configure(lProps);

		String lBrokerURL = "failover:(tcp://0.0.0.0:61616,tcp://127.0.0.1:61616)?initialReconnectDelay=100&randomize=false";
		// the name of the JMD Gateway topic
		String lGatewayTopic = "org.jwebsocket.jms.gateway";
		String lEndPointId = UUID.randomUUID().toString();

		// tcp://172.20.116.68:61616 org.jwebsocket.jws2jms org.jwebsocket.jms2jws aschulze-dt
		// failover:(tcp://0.0.0.0:61616,tcp://127.0.0.1:61616)?initialReconnectDelay=100&randomize=false org.jwebsocket.jws2jms org.jwebsocket.jms2jws aschulze-dt
		mLog.info("jWebSocket JMS Gateway Server Endpoint");

		if (null != aArgs && aArgs.length >= 3) {
			lBrokerURL = aArgs[0];
			lGatewayTopic = aArgs[1];
			if (aArgs.length >= 3) {
				lEndPointId = aArgs[2];
			}
			mLog.info("Using: "
					+ lBrokerURL + ", "
					+ lGatewayTopic + ", "
					+ lEndPointId);
		} else {
			mLog.info("Usage: java -jar jWebSocketActiveMQClientBundle-<ver>.jar URL gateway-topic [node-id]");
			mLog.info("Example: java -jar jWebSocketActiveMQClientBundle-1.0.jar tcp://172.20.116.68:61616 " + lGatewayTopic + " [your node id]");
			System.exit(1);
		}

		// instantiate a new jWebSocket JMS Gateway Client
		JMSClient lJMSClient = new JMSClient(
				lBrokerURL,
				lGatewayTopic, // gateway topic
				lEndPointId, // unique node id
				5, // thread pool size, messages being processed concurrently
				JMSClient.DURABLE // durable (for servers) or temporary (for clients)
				);

		// add a listener to listen in coming messages
		lJMSClient.addListener(new JMSDemoMessageListener(lJMSClient));

		// this is a console app demo
		// so wait in a thread loop until the client get shut down
		try {
			while (!lJMSClient.isShutdown()) {
				Thread.sleep(1000);
			}
		} catch (InterruptedException lEx) {
			// ignore a potential exception here
		}

		// check if JMS client has already been shutdown by logic
		if (!lJMSClient.isShutdown()) {
			// if not yet done...
			mLog.info("Shutting down JMS Server Endpoint...");
			// shut the client properly down
			lJMSClient.shutdown();
		}
		// and show final status message in the console
		mLog.info("JMS Server Endpoint properly shutdown.");
	}
}