package com.mstendo.rtp.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TimedExpiringMap<K, V> {
  private final Map<K, ExpiringValue<V>> map;
  private final TimeUnit timeUnit;
  
  public TimedExpiringMap(TimeUnit timeUnit) {
    this.map = new HashMap<K, ExpiringValue<V>>();
    this.timeUnit = timeUnit;
  }
  
  public void put(K key, V value, long expiryDuration) {
    long expiryTime = System.currentTimeMillis() + timeUnit.toMillis(expiryDuration);
    map.put(key, new ExpiringValue<V>(value, expiryTime));
  }
  
  public V get(K key) {
    ExpiringValue<V> expiringValue = map.get(key);
    if (expiringValue == null) {
      return null;
    }
    if (System.currentTimeMillis() > expiringValue.getExpiryTime()) {
      map.remove(key);
      return null;
    }
    return expiringValue.getValue();
  }
  
  public boolean containsKey(K key) {
    ExpiringValue<V> expiringValue = map.get(key);
    if (expiringValue == null) {
      return false;
    }
    if (System.currentTimeMillis() > expiringValue.getExpiryTime()) {
      map.remove(key);
      return false;
    }
    return true;
  }
  
  public boolean isEmpty() {
    cleanup();
    return map.isEmpty();
  }
  
  public void remove(K key) {
    map.remove(key);
  }
  
  private void cleanup() {
    long currentTime = System.currentTimeMillis();
    Iterator<Map.Entry<K, ExpiringValue<V>>> iterator = map.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<K, ExpiringValue<V>> entry = iterator.next();
      if (currentTime > entry.getValue().getExpiryTime()) {
        iterator.remove();
      }
    }
  }
  
  public static final class ExpiringValue<V> {
    private final V value;
    private final long expiryTime;
    
    public ExpiringValue(V value, long expiryTime) {
      this.value = value;
      this.expiryTime = expiryTime;
    }
    
    public V getValue() {
      return value;
    }
    
    public long getExpiryTime() {
      return expiryTime;
    }
  }
}
