/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jwebsocket.plugins.stockticker.event;

import org.jwebsocket.plugins.stockticker.api.IUser;
import org.jwebsocket.eventmodel.annotation.ImportFromToken;
import org.jwebsocket.eventmodel.event.C2SEvent;

/**
 *
 * @author Roy
 */
public class CreateUser extends C2SEvent implements IUser {

	private String mUser;
	private String mPass;

	/**
	 *
	 * @param aUser
	 */
	@Override
	@ImportFromToken
	public void setUser(String aUser) {
		mUser = aUser;
	}

	/**
	 *
	 * @param aPass
	 */
	@Override
	@ImportFromToken
	public void setPass(String aPass) {
		mPass = aPass;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String getUser() {
		return mUser;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String getPass() {
		return mPass;
	}
}
