//	---------------------------------------------------------------------------
//	jWebSocket - JWebSocketJarClassLoader (Community Edition, CE)
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
package org.jwebsocket.factory;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * ClassLoader that loads the classes from the jars. Engine, Servers, Plugins
 * all configured via jWebSocket.xml file is loaded using this class.
 *
 * @author puran
 * @author kyberneees
 * @author Marcos Antonio González Huerta (markos0886, UCI)
 */
public class JWebSocketJarClassLoader {

	private URLClassLoader lCL = (URLClassLoader) ClassLoader.getSystemClassLoader();

	/**
	 *
	 * @return
	 */
	public URLClassLoader getClassLoader() {
		return lCL;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param aPath
	 * @throws Exception
	 */
	public void addFile(String aPath) throws Exception {
		Method lMethod = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
		lMethod.setAccessible(true);
		URL lURL = new File(aPath).toURI().toURL();
		lMethod.invoke(lCL, new Object[]{lURL});
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param aClassName
	 * @return Class<?>
	 * @throws ClassNotFoundException
	 */
	public Class<?> loadClass(String aClassName) throws ClassNotFoundException {
		return lCL.loadClass(aClassName);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param aClassName
	 * @param aPath
	 * @return Class<?>
	 * @throws ClassNotFoundException
	 */
	public Class<?> reloadClass(String aClassName, String aPath) throws ClassNotFoundException {
		LocalLoader lLocalLoader = new LocalLoader(lCL);
		lLocalLoader.loadJar(aPath);
		return lLocalLoader.loadClass(aClassName, true);
	}
}
//public class JWebSocketJarClassLoader extends JarClassLoader {
//
//	public JWebSocketJarClassLoader() {
//		// init with empty list of URLs
//		super();
//	}
//
//	public void addFile(String aPath) throws MalformedURLException {
//		File lFile = new File(aPath);
//		URL lURL = lFile.toURI().toURL();
//		add(lURL);
//	}
//
//	/**
//	 * {@inheritDoc}
//	 *
//	 * @param aURL 
//	 * @throws MalformedURLException
//	 */
//	public void addURL(URL aURL) throws MalformedURLException {
//		add(aURL);
//	}
//
//	public Class reloadClass(String className) {
//		return getLocalLoader().loadClass(className, true);
//	}
//}
