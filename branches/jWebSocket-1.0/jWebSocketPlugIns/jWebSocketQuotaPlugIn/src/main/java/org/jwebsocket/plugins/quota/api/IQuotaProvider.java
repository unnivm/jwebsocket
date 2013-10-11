/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jwebsocket.plugins.quota.api;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author osvaldo
 */
public interface IQuotaProvider {
    
    public IQuota getQuotaByType( String aType ) throws Exception;
    
     public Map<String, IQuota> getActiveQuotas();
    
}