package com.suda.federate.security.sha;

import com.suda.federate.config.FedSpatialConfig;
import com.suda.federate.rpc.FederateCommon;
import com.suda.federate.security.dp.Laplace;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SiloCache {

    private Set<FederateCommon.Point> localSet=new HashSet<>();
    private Set<FederateCommon.Point> localRandomSet;

    private final double sample;

    public SiloCache(Collection<FederateCommon.Point> localSet) {
        Laplace lp = new Laplace(FedSpatialConfig.EPS_DP, FedSpatialConfig.SD_DP);
        sample = lp.sample();
        localSet.addAll(localSet);
    }

    public void addSet(Collection<FederateCommon.Point> newSet) {
        localSet.addAll(newSet);
    }

    public Set<FederateCommon.Point> getLocalSet() {
        return localSet;
    }

    public Set<FederateCommon.Point> getLocalRandomSet() {
        Set<FederateCommon.Point> randomSet = new HashSet<>();
        if (localRandomSet == null) {
            localRandomSet = new HashSet<>();
        }

        for (FederateCommon.Point point : localSet) {
            FederateCommon.Point randomPoint = point.toBuilder().setLatitude(point.getLatitude() + sample)
                    .setLongitude(point.getLongitude() + sample).build();
            randomSet.add(randomPoint);
        }
        localRandomSet = randomSet;
        return localRandomSet;
    }

    public Set<FederateCommon.Point> getObfSet() {
        Set<FederateCommon.Point> obf = new HashSet<>();
        obf.addAll(this.getLocalSet());
        obf.addAll(this.getLocalRandomSet());
        return obf;
    }
}
