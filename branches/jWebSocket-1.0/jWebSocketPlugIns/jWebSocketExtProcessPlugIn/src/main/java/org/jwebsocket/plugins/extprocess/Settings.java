//	---------------------------------------------------------------------------
//	jWebSocket - Settings for External Process Plug-in (Community Edition, CE)
//	---------------------------------------------------------------------------
//	Copyright 2010-2014 Innotrade GmbH (jWebSocket.org)
//	Alexander Schulze, Germany (NRW)
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
package org.jwebsocket.plugins.extprocess;

import java.util.Map;
import javolution.util.FastMap;

/**
 *
 * @author aschulze
 */
public class Settings {

	private Map<String, String> mAllowedProgs = new FastMap<String, String>();

	/**
	 * @return the aliases
	 */
	public Map<String, String> getAllowedProgs() {
		return mAllowedProgs;
	}

	/**
	 * @param aAllowedProgs the aliases to set
	 */
	public void setAllowedProgs(Map aAllowedProgs) {
		mAllowedProgs = aAllowedProgs;
	}
}
