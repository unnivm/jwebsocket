//  ---------------------------------------------------------------------------
//  jWebSocket - PurgeCancelledTimeoutsTask (Community Edition, CE)
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
package org.jwebsocket.rrpc;

import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author kyberneees
 */
public class PurgeCancelledTimeoutsTask extends TimerTask {

	private Timer t;
	private static Log logger = LogFactory.getLog(PurgeCancelledTimeoutsTask.class);

	/**
	 *
	 * @param t
	 */
	public PurgeCancelledTimeoutsTask(Timer t) {
		this.t = t;
	}

	@Override
	public void run() {
		if (logger.isDebugEnabled()) {
			logger.debug("Purging the timeout callbacks queue...");
		}
		t.purge();
	}
}
