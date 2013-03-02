//	---------------------------------------------------------------------------
//	jWebSocket SMS Plug-In (Community Edition, CE)
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
package org.jwebsocket.plugins.sms;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import javolution.util.FastList;
import org.apache.log4j.Logger;
import org.jwebsocket.api.PluginConfiguration;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.config.JWebSocketCommonConstants;
import org.jwebsocket.config.JWebSocketServerConstants;
import org.jwebsocket.kit.PlugInResponse;
import org.jwebsocket.logging.Logging;
import org.jwebsocket.plugins.TokenPlugIn;
import org.jwebsocket.token.Token;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author mayra
 */
public class SMSPlugIn extends TokenPlugIn {

	private static Logger mLog = Logging.getLogger();
	private static final String NS_SMS = JWebSocketServerConstants.NS_BASE + ".plugins.sms";
	private final static String VERSION = "1.0.0";
	private final static String VENDOR = JWebSocketCommonConstants.VENDOR_CE;
	private final static String LABEL = "jWebSocket SMSPlugIn";
	private final static String COPYRIGHT = JWebSocketCommonConstants.COPYRIGHT_CE;
	private final static String LICENSE = JWebSocketCommonConstants.LICENSE_CE;
	private final static String DESCRIPTION = "jWebSocket SMSPlugIn - Community Edition";
	private static Collection<WebSocketConnector> mClients = new FastList<WebSocketConnector>().shared();
	private static ApplicationContext mBeanFactory;
	private static Settings mSettings;
	private ISMSProvider mProvider;

	/**
	 *
	 * @param aConfiguration
	 */
	public SMSPlugIn(PluginConfiguration aConfiguration) {
		super(aConfiguration);
		if (mLog.isDebugEnabled()) {
			mLog.debug("Instantiating SMS plug-in...");
		}
		this.setNamespace(NS_SMS);

		try {
			mBeanFactory = getConfigBeanFactory();
			if (null == mBeanFactory) {
				mLog.error("No or invalid spring configuration for SMS plug-in, some features may not be available.");
			} else {
				mBeanFactory = getConfigBeanFactory();
				mSettings = (Settings) mBeanFactory.getBean("org.jwebsocket.plugins.sms.settings");
				if (mLog.isInfoEnabled()) {
					mLog.info("SMS plug-in successfully instantiated.");
				}
			}
		} catch (Exception lEx) {
			mLog.error(Logging.getSimpleExceptionMessage(lEx, "instantiating SMS plug-in"));
		}

		if (null != mSettings) {
			// just for developers convenience
			mProvider = mSettings.getProvider();
		}
	}

	@Override
	public String getVersion() {
		return VERSION;
	}

	@Override
	public String getLabel() {
		return LABEL;
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getVendor() {
		return VENDOR;
	}

	@Override
	public String getCopyright() {
		return COPYRIGHT;
	}

	@Override
	public String getLicense() {
		return LICENSE;
	}

	@Override
	public void processToken(PlugInResponse aResponse, WebSocketConnector aConnector, Token aToken) {
		if (aToken.getNS().equals(getNamespace())) {
			if (aToken.getType().equals("sms")) {
				try {
					sendSms(aConnector, aToken);
				} catch (Exception ex) {
					mLog.error(ex.getClass().getSimpleName() + ": " + ex.getMessage());
				}
			}

//			if (aToken.getType().equals("smsList")) {
//				mClients.remove(aConnector);
//			}
		}
	}

	/**
	 *
	 * @param aConnector
	 * @param aToken
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public void sendSms(WebSocketConnector aConnector, Token aToken) throws MalformedURLException, IOException {
		Token lRes = mProvider.sendSms(aToken);

		mLog.info("Provider returned: " + lRes.toString());

		getServer().setResponseFields(aToken, lRes);
		sendToken(aConnector, aConnector, lRes);
	}
}
