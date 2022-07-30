package com.suda.federate.security.sha;

import com.suda.federate.config.FedSpatialConfig;
import com.suda.federate.rpc.FederateCommon;
import com.suda.federate.security.dp.Laplace;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SiloCache {

    private Set<Pair<Double,Double>> localSet=new HashSet<>();
    private Set<Pair<Double,Double>> localRandomSet;

    private final double sample;

    public SiloCache(Collection<Pair<Double,Double>> points) {
        Laplace lp = new Laplace(FedSpatialConfig.EPS_DP, FedSpatialConfig.SD_DP);
        sample = lp.sample();
        localSet.addAll(points);
    }

    public void addSet(Collection<Pair<Double,Double>> newSet) {
        localSet.addAll(newSet);
    }

    public Set<Pair<Double,Double>> getLocalSet() {
        return localSet;
    }

    public Set<Pair<Double,Double>> getLocalRandomSet() {
        Set<Pair<Double,Double>> randomSet = new HashSet<>();
        if (localRandomSet == null) {
            localRandomSet = new HashSet<>();
        }

        for (Pair<Double,Double>  point : localSet) {
            Pair<Double,Double> randomPoint = Pair.of(point.getLeft()+sample,point.getRight() + sample);
            randomSet.add(randomPoint);
        }
        localRandomSet = randomSet;
        return localRandomSet;
    }

    public Set<Pair<Double,Double>> getObfSet() {
        Set<Pair<Double,Double>> obf = new HashSet<>();
        obf.addAll(this.getLocalSet());
        obf.addAll(this.getLocalRandomSet());
        return obf;
    }
}
