package com.suda.federate.utils;

import com.suda.federate.config.FedSpatialConfig;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentBuffer {
  private final Map<String, Object> bufferMap;
  private final Lock[] locks;
  private final int RETRY;

  public ConcurrentBuffer() {
    bufferMap = new ConcurrentHashMap<>();
    locks = new Lock[FedSpatialConfig.LOCK_NUMBER];
    for (int i = 0; i < FedSpatialConfig.LOCK_NUMBER; ++i) {
      locks[i] = new ReentrantLock();
    }
    RETRY = FedSpatialConfig.RETRY;
  }

  public void set(String uuid, Object buffer) {
    int hash = quickHash(uuid.charAt(0), uuid.charAt(uuid.length() - 1));
    synchronized (locks[hash]) {
      bufferMap.put(uuid, buffer);
      locks[hash].notifyAll();
    }
  }

  public boolean contains(String uuid) {
    return bufferMap.containsKey(uuid);
  }

  public Object get(String uuid) {
    int count = 0;
    int hash = quickHash(uuid.charAt(0), uuid.charAt(uuid.length() - 1));
    synchronized (locks[hash]) {
      while (!bufferMap.containsKey(uuid) && count < RETRY) {
        try {
          locks[hash].wait(FedSpatialConfig.TIME_OUT);
        } catch (InterruptedException e) {
          e.printStackTrace();
          return null;
        }
      count++;
      }
    }
    return bufferMap.get(uuid);
  }


  public void remove(String uuid) {
    bufferMap.remove(uuid);
  }

  private int quickHash(char b, char e) {
    return ((int) b * (int) e + (int) e) % locks.length;
  }
}
