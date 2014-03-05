//	---------------------------------------------------------------------------
//	jWebSocket ChunkableZipInputStream (Community Edition, CE)
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
package org.jwebsocket.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import org.jwebsocket.token.Token;
import org.jwebsocket.token.TokenFactory;

/**
 * The class implements the IChunkable interface to support the transmission of
 * data from a InputStream zipped to the client.
 *
 * @author kyberneees
 */
public class ChunkableZipInputStream extends BaseChunkable {

	private InputStream mIS;

	/**
	 *
	 * @param aNS
	 * @param aType
	 * @param aIS
	 */
	public ChunkableZipInputStream(String aNS, String aType, InputStream aIS) {
		super(aNS, aType);

		try {
			byte[] lData = new byte[aIS.available()];
			aIS.read(lData, 0, aIS.available());
			mIS = new ByteArrayInputStream(Tools.zip(lData, Tools.ENC_PLAIN));
		} catch (Exception lEx) {
			throw new RuntimeException(lEx.getCause());
		}
	}

	/**
	 *
	 * @param aNS
	 * @param aType
	 * @param aData
	 */
	public ChunkableZipInputStream(String aNS, String aType, byte[] aData) {
		super(aNS, aType);

		try {
			byte[] lData = Tools.zip(aData, Tools.ENC_BASE64);
			mIS = new ByteArrayInputStream(lData);
		} catch (Exception lEx) {
			throw new RuntimeException(lEx.getCause());
		}
	}

	/**
	 *
	 * @param aNS
	 * @param aType
	 * @param aData
	 */
	public ChunkableZipInputStream(String aNS, String aType, String aData) {
		super(aNS, aType);

		try {
			byte[] lData = aData.getBytes("UTF-8");
			mIS = new ByteArrayInputStream(Tools.zip(lData, Tools.ENC_PLAIN));
		} catch (Exception lEx) {
			throw new RuntimeException(lEx.getCause());
		}
	}

	/**
	 *
	 * @return
	 */
	public InputStream getIS() {
		return mIS;
	}

	@Override
	public Iterator<Token> getChunksIterator() {

		return new Iterator<Token>() {
			@Override
			public boolean hasNext() {
				try {
					return mIS.available() > 0;
				} catch (IOException lEx) {
					return false;
				}
			}

			@Override
			public Token next() {
				try {
					int lLength = (mIS.available() > getFragmentSize()) ? getFragmentSize() : mIS.available();
					Token lChunk = TokenFactory.createToken();
					lChunk.setChunkType("stream" + getUniqueChunkId());

					byte[] lBA = new byte[lLength];
					mIS.read(lBA, 0, lLength);

					String lData = new String(lBA);

					lChunk.setMap("enc", new MapAppender().append("data", "base64").getMap());
					lChunk.setString("data", lData);

					return lChunk;
				} catch (Exception lEx) {
					return null;
				}
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("Not supported on InputStream objects!");
			}
		};
	}
}