package com.suda.federate.silo;

import com.suda.federate.application.FederateDBClient;
import com.suda.federate.config.FedSpatialConfig;
import com.suda.federate.rpc.FederateGrpc;
import com.suda.federate.security.dp.Laplace;
import com.suda.federate.utils.ConcurrentBuffer;
import io.grpc.ManagedChannelBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// 定义一个实现服务接口的类
public abstract class FederateDBService extends FederateGrpc.FederateImplBase {
    private static final Logger LOG = LogManager.getLogger(FederateDBService.class);
    public Map<String, FederateDBClient> federateClientMap = null;
    public  List<String> endpoints = null;
    public int THREAD_POOL_SIZE = 0;
    public ExecutorService executorService = null;
    public Laplace lp = null;
    public ConcurrentBuffer buffer = new ConcurrentBuffer();;

    public void initClients(List<String> endpoints) {
        endpoints = endpoints;
        federateClientMap = new TreeMap<>();
        for (String endpoint : endpoints) {
            federateClientMap.put(endpoint, new FederateDBClient(endpoint));
        }
        THREAD_POOL_SIZE = endpoints.size();
        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        lp = new Laplace(FedSpatialConfig.EPS_DP, FedSpatialConfig.SD_DP);
    }
    public FederateDBClient getClient(String endpoint,boolean used){//没啥用
        if (federateClientMap.containsKey(endpoint)){
            return federateClientMap.get(endpoint);
        }
        return null;
    }

    public FederateDBClient getClient(String endpoint) {
        return new FederateDBClient(endpoint);

    }
    public FederateDBClient getClient(Integer endpointId){
        String endpoint = endpoints.get(endpointId);
        if (federateClientMap.containsKey(endpoint)){
            return federateClientMap.get(endpoint);
        }
        return null;
    }

}
