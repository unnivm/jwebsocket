//	---------------------------------------------------------------------------
//	jWebSocket - Grizzly Engine
//	Copyright (c) 2010 Alexander Schulze, Innotrade GmbH
//	---------------------------------------------------------------------------
//	This program is free software; you can redistribute it and/or modify it
//	under the terms of the GNU Lesser General Public License as published by the
//	Free Software Foundation; either version 3 of the License, or (at your
//	option) any later version.
//	This program is distributed in the hope that it will be useful, but WITHOUT
//	ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//	FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
//	more details.
//	You should have received a copy of the GNU Lesser General Public License along
//	with this program; if not, see <http://www.gnu.org/licenses/lgpl.html>.
//	---------------------------------------------------------------------------
package org.jwebsocket.grizzly;

import java.util.Date;
import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.grizzly.websockets.WebSocketAddOn;
import org.glassfish.grizzly.websockets.WebSocketApplication;
import org.glassfish.grizzly.websockets.WebSocketEngine;
import org.glassfish.grizzly.websockets.WebSocketServer;
import org.jwebsocket.api.EngineConfiguration;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.engines.BaseEngine;
import org.jwebsocket.kit.CloseReason;
import org.jwebsocket.kit.WebSocketException;
import org.jwebsocket.logging.Logging;

/**
 *
 * @author vbarzana, aschulze
 */
public class GrizzlyEngine extends BaseEngine {

	private static Logger mLog = Logging.getLogger();
	private static Integer mPort = 80;
	private static Integer mSSLPort = 443;
	private boolean mIsRunning = false;
	private HttpServer mServer = null;
	// private HttpServer mServer = null;
	private static final String mDemoRootDirectory = "/var/www/web";
	private static final String mDemoContext = "/jWebSocketGrizzlyDemos";
	private static final String mDemoServlet = "/jWebSocketGrizzlyServlet";

	public GrizzlyEngine(EngineConfiguration aConfiguration) {

		super(aConfiguration);
		try {
			// load the ports from the configuration
			mPort = aConfiguration.getPort();
			mSSLPort = aConfiguration.getSSLPort();

			String lEngineContext = aConfiguration.getContext();
			String lEngineApp = aConfiguration.getServlet();

			if (mSSLPort == 0) {
				mSSLPort = 443;
			}
			if (mPort == 0) {
				mPort = 8080;
			}

			if (lEngineContext == null) {
				lEngineContext = "/";
			}

			if (lEngineApp == null) {
				lEngineApp = "/*";
			}

			if (mLog.isDebugEnabled()) {
				mLog.debug("Instantiating Grizzly server '"
						+ "port " + mPort
						+ ", ssl-port " + mSSLPort
						+ ", context: '" + lEngineContext
						+ "', servlet: '" + lEngineApp + "'...");
			}

			// Creating Grizzly server
			// InetSocketAddress lISA = new InetSocketAddress(mPort);
			//mServer = WebSocketServer.createServer(mPort);
			// Registering grizzly jWebSocket Wrapper Application into grizzly WebSocketEngine
			//mServer.register("jWebSocketEngine", new GrizzlyWebSocketApplication(this));m

			
			mServer = HttpServer.createSimpleServer(lEngineApp, mPort);
			HttpHandler lHttpHandler = new	StaticHttpHandler(mDemoRootDirectory);
			mServer.getServerConfiguration().addHttpHandler(lHttpHandler, mDemoContext);
			
			mServer.getListener("grizzly").registerAddOn(new WebSocketAddOn());
			// lHttpServer.addListener(lListener);
			
			// The WebSocketApplication will control the incoming and
			//outgoing flow, connection, listeners, etc... final
			WebSocketApplication lApp = new
			GrizzlyWebSocketApplication(this);
			
			// Registering grizzly jWebSocket Wrapper Application into
			// grizzly WebSocketEngine
			 WebSocketEngine.getEngine().register(lApp);
			 

			//TODO: IMPLEMENT GRIZZLY WSS LISTENER
		   /*
			 * // -------------- SSL SECTION ----------------------
			 * SSLContextConfigurator lSSLContext = new
			 * SSLContextConfigurator(); String lWebSocketHome =
			 * System.getenv(JWebSocketServerConstants.JWEBSOCKET_HOME); String
			 * lKeyStore = lWebSocketHome + "/conf/jWebSocket.ks";
			 * lSSLContext.createSSLContext();
			 * lSSLContext.setKeyStoreFile(lKeyStore);
			 * lSSLContext.setKeyPass("jWebSocket");
			 * lSSLContext.setKeyStorePass("jWebSocket");
			 * lSSLContext.setKeyManagerFactoryAlgorithm("SunX509");
			 * SSLEngineConfigurator lSSLConfigurator = new
			 * SSLEngineConfigurator(lSSLContext); NetworkListener lNListener =
			 * new NetworkListener("grizzly-ssl",
			 * mServer.getListener("grizzly").getHost(), mSSLPort);
			 * lNListener.setSSLEngineConfig(lSSLConfigurator);
			 * lNListener.setSecure(true); lNListener.registerAddOn(new
			 * WebSocketAddOn());
			 *
			 * mServer.addListener(lNListener);
			 *
			 * if (mLog.isDebugEnabled()) { mLog.debug("Loading SSL cert from
			 * keystore '" + lKeyStore + "'..."); } //-------------------------
			 * SSL SECTION END ---------------------
			 */
		} catch (Exception lEx) {
			mLog.error(lEx.getMessage());
		}
	}

	@Override
	public void startEngine()
			throws WebSocketException {
		if (mLog.isDebugEnabled()) {
			mLog.debug("Starting Grizzly engine '" + getId() + "...");
		}

		super.startEngine();
		try {
			mServer.start();
		} catch (Exception lEx) {
			mLog.error(lEx.getClass().getSimpleName() + " instantiating Embedded Grizzly Server: " + lEx.getMessage());
		}

		if (mLog.isDebugEnabled()) {
			mLog.debug("Grizzly Server '"
					+ "' sucessfully instantiated at port "
					+ mPort + ", SSL port " + mSSLPort + "...");
		}

		mIsRunning = true;

		if (mLog.isInfoEnabled()) {
			mLog.info("Grizzly engine '" + getId() + "' started.");
		}

		// fire the engine start event
		engineStarted();
	}

	@Override
	public void stopEngine(CloseReason aCloseReason)
			throws WebSocketException {
		if (mLog.isDebugEnabled()) {
			mLog.debug("Stopping Grizzly ' engine '"
					+ getId() + "...");
		}

		// resetting "isRunning" causes engine listener to terminate
		mIsRunning = false;
		// inherited method stops all connectors

		long lStarted = new Date().getTime();
		int lNumConns = getConnectors().size();
		super.stopEngine(aCloseReason);

		try {
			if (mServer != null) {
				mServer.stop();
				if (mLog.isDebugEnabled()) {
					mLog.debug("Grizzly successfully stopped.");
				}
			} else {
				if (mLog.isDebugEnabled()) {
					mLog.debug("Grizzly not yet started, properly terminated.");
				}
				return;
			}
		} catch (Exception lEx) {
			mLog.error(lEx.getClass().getSimpleName()
					+ " stopping Grizzly Server: "
					+ lEx.getMessage());
		}

		// now wait until all connectors have been closed properly
		// or timeout exceeds...
		try {
			while (getConnectors().size() > 0 && new Date().getTime() - lStarted < 10000) {
				Thread.sleep(250);
			}
		} catch (Exception lEx) {
			mLog.error(lEx.getClass().getSimpleName() + ": " + lEx.getMessage());
		}
		if (mLog.isDebugEnabled()) {
			long lDuration = new Date().getTime() - lStarted;
			int lRemConns = getConnectors().size();
			if (lRemConns > 0) {
				mLog.warn(lRemConns + " of " + lNumConns
						+ " Grizzly connectors '" + getId()
						+ "' did not stop after " + lDuration + "ms.");
			} else {
				mLog.debug(lNumConns
						+ " Grizzly connectors '" + getId()
						+ "' stopped after " + lDuration + "ms.");
			}
		}
		// fire the engine stopped event
		engineStopped();
	}

	@Override
	public void connectorStarted(WebSocketConnector aConnector) {
		if (mLog.isDebugEnabled()) {
			mLog.debug("Detected new Grizzly connector at port "
					+ aConnector.getRemotePort() + ".");
		}
		getConnectors().put(aConnector.getId(), aConnector);
		super.connectorStarted(aConnector);
	}

	@Override
	public void connectorStopped(WebSocketConnector aConnector, CloseReason aCloseReason) {
		if (mLog.isDebugEnabled()) {
			mLog.debug("Detected stopped Grizzly connector at port "
					+ aConnector.getRemotePort() + ".");
		}
		//The BaseEngine removes the connector from the list
		super.connectorStopped(aConnector, aCloseReason);
	}

	/*
	 * Returns {@code true} if Grizzly engine is running or {@code false}
	 * otherwise. The alive status represents the state of the Grizzly engine
	 * listener thread.
	 */
	@Override
	public boolean isAlive() {
		return mIsRunning;
	}
}
