//  ---------------------------------------------------------------------------
//  jWebSocket - JDBCStorage
//  Copyright (c) 2011 Innotrade GmbH, jWebSocket.org
//  ---------------------------------------------------------------------------
//  This program is free software; you can redistribute it and/or modify it
//  under the terms of the GNU Lesser General Public License as published by the
//  Free Software Foundation; either version 3 of the License, or (at your
//  option) any later version.
//  This program is distributed in the hope that it will be useful, but WITHOUT
//  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
//  more details.
//  You should have received a copy of the GNU Lesser General Public License along
//  with this program; if not, see <http://www.gnu.org/licenses/lgpl.html>.
//  ---------------------------------------------------------------------------
package org.jwebsocket.storage.mongodb;

import org.jwebsocket.api.IBasicStorage;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javolution.util.FastMap;
import javolution.util.FastSet;

/**
 * This class uses MongoDB servers to persist the information. 
 * <br>
 * All the cache storages entries are located in the same database collection. 
 * 
 * @author rsantamaria
 */
public class MongoDBStorageV2<K, V> implements IBasicStorage<K, V> {
	
	private String name;
	private DBCollection myCollection;

	/**
	 * Create a new MongoDBStorage instance
	 *
	 * @param name The name of the storage container
	 * @param collection The MongoDB database collection instance
	 */
	public MongoDBStorageV2(String name, DBCollection collection) {
		this.name = name;
		myCollection = collection;
	}

	/**
	 * {@inheritDoc
	 */
	@Override
	public void initialize() throws Exception {
		myCollection.ensureIndex(new BasicDBObject().append("ns", 1).append("k", 1),
				new BasicDBObject().append("unique", true));
	}

	/**
	 * {@inheritDoc
	 */
	@Override
	public void shutdown() throws Exception {
	}

	/**
	 * {@inheritDoc
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * {@inheritDoc
	 */
	@Override
	public void setName(String newName) throws Exception {
		myCollection.update(new BasicDBObject().append("ns", name),
				new BasicDBObject().append("$set", new BasicDBObject().append("ns", newName)));

		name = newName;
	}

	/**
	 * {@inheritDoc
	 */
	@Override
	public Map<K, V> getAll(Collection<K> keys) {
		FastMap<K, V> map = new FastMap<K, V>();
		for (K key : keys) {
			map.put((K) key, get(key));
		}
		return map;
	}

	/**
	 * {@inheritDoc
	 */
	@Override
	public int size() {
		return keySet().size();
	}

	/**
	 * {@inheritDoc
	 */
	@Override
	public boolean isEmpty() {
		return keySet().isEmpty();
	}

	/**
	 * {@inheritDoc
	 */
	@Override
	public boolean containsKey(Object key) {
		DBObject obj = myCollection.findOne(new BasicDBObject().append("ns", name).append("k", (String) key));
		if (obj != null) {
			return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc
	 */
	@Override
	public boolean containsValue(Object value) {
		DBObject obj = myCollection.findOne(new BasicDBObject().append("ns", name).append("v", value));
		if (obj != null) {
			return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc
	 */
	@Override
	public V get(Object key) {
		return (V) myCollection.findOne(new BasicDBObject().append("ns", name).append("k", key)).get("v");
	}

	@Override
	public V put(K key, V value) {
		BasicDBObject obj = new BasicDBObject();
		obj.append("ns", name);
		obj.append("k", key);
		
		DBCursor cur = myCollection.find(obj);
		if (!cur.hasNext()) {
			obj.append("v", value);
			myCollection.insert(obj);
		} else {
			DBObject upd = cur.next();
			upd.put("v", value);
			myCollection.save(upd);
		}
		return value;
	}

	/**
	 * {@inheritDoc
	 */
	@Override
	public V remove(Object key) {
		if (containsKey(key)) {
			V val = get(key);
			myCollection.remove(new BasicDBObject().append("ns", name).append("k", key));
			return val;
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	/**
	 * {@inheritDoc
	 */
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (K key : m.keySet()) {
			put(key, m.get(key));
		}
	}

	/**
	 * {@inheritDoc
	 */
	@Override
	public void clear() {
		myCollection.remove(new BasicDBObject().append("ns", name));
	}

	/**
	 * {@inheritDoc
	 */
	@Override
	public Set<K> keySet() {
		Set<K> s = new FastSet<K>();
		DBCursor cur = myCollection.find(new BasicDBObject().append("ns", name));
		while (cur.hasNext()) {
			s.add((K) cur.next().get("k"));
		}
		return s;
	}

	/**
	 * {@inheritDoc
	 */
	@Override
	public Collection<V> values() {
		List<V> l = new ArrayList<V>();
		DBCursor cur = myCollection.find(new BasicDBObject().append("ns", name));
		while (cur.hasNext()) {
			l.add((V) cur.next().get("v"));
		}
		return l;
	}

	/**
	 * {@inheritDoc
	 */
	@Override
	public Set<Entry<K, V>> entrySet() {
		return getAll(keySet()).entrySet();
	}
}
