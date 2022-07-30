package com.suda.federate.security.sha;

import com.suda.federate.config.FedSpatialConfig;
import com.suda.federate.rpc.FederateCommon;
import com.suda.federate.security.dp.Laplace;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SecretUnion {



    public static Set<Pair<Double,Double>> randomShares(int[] IDLst, int num, HashMap<Integer, SiloCache> silos) {
        Set<Pair<Double,Double>> unionResult = new HashSet<>();
        for (int id : IDLst) {//loop 1
            unionResult.addAll(silos.get(id).getObfSet());//加入 fake random Set<FederateCommon.Point>
            if (id % num == 0) {
                break;
            }
        }

        for (int id : IDLst) {//loop 2
            unionResult.removeAll(silos.get(id).getObfSet());//移除 fake random Set<FederateCommon.Point>
            unionResult.addAll(silos.get(id).getLocalSet());//加入 真实数据
        }
        return unionResult;

    }

    public static Set<Pair<Double,Double>> plaintextUnion(HashMap<Integer, SiloCache> silos) {
        Set<Pair<Double,Double>> unionResult = new HashSet<>();
        for (int k : silos.keySet()) {
            unionResult.addAll(silos.get(k).getLocalSet());
        }
        return unionResult;
    }
}



