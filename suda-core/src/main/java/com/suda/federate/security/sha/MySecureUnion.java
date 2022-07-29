package com.suda.federate.security.sha;

import com.suda.federate.config.FedSpatialConfig;
import com.suda.federate.rpc.FederateCommon;
import com.suda.federate.security.dp.Laplace;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


class SiloTest {
    private final String siloID;
    private  Set<FederateCommon.Point> localSet;
    private  Set<FederateCommon.Point> localRandomSet;

    private final double sample;

    SiloTest(String sID) {
        siloID = sID;
        Laplace lp = new Laplace(FedSpatialConfig.EPS_DP, FedSpatialConfig.SD_DP);
        this.sample=lp.sample();
    }

    SiloTest(String sID, Set<FederateCommon.Point> localSet) {
        siloID = sID;
        Laplace lp = new Laplace(FedSpatialConfig.EPS_DP, FedSpatialConfig.SD_DP);
        sample=lp.sample();
        this.localSet = localSet;
    }

    public void addPoint(FederateCommon.Point point) {
        if (localSet == null) {
            localSet = new HashSet<>();
        }
        localSet.add(point);
    }

    public void addSet(Set<FederateCommon.Point> newSet) {
        localSet.addAll(newSet);
    }

    public String getSiloID(){
        return siloID;
    }

    public Set<FederateCommon.Point> getLocalSet() {
        return localSet;
    }

    public  Set<FederateCommon.Point> getLocalRandomSet() {
        Set<FederateCommon.Point> randomSet = new HashSet<>();
        if(localRandomSet == null) {
            localRandomSet = new HashSet<>();
        }

        for(FederateCommon.Point point : localSet) {
            FederateCommon.Point randomPoint = point.toBuilder().setLatitude(point.getLatitude()+sample)
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

/**
 * @author NULL
 */
public class MySecureUnion {
    public static void initSiloFromCsv(String csvFile, SiloTest[] siloTestLst) {
        // String csvFile = "src/main/java/SecretShare/osm_part";
        String line = "";
        String cvsSplitBy = ",";

        int cnt = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            line = br.readLine();
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] osm = line.split(cvsSplitBy);
                siloTestLst[cnt++ / 10].addPoint(FederateCommon.Point.newBuilder()
                                .setLatitude(new BigDecimal(osm[1]).doubleValue())
                                .setLongitude(new BigDecimal(osm[2]).doubleValue()).build());

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Set<FederateCommon.Point> randomShares(int[] IDLst, int num, HashMap<Integer, SiloTest> silos) {
        Set<FederateCommon.Point> unionResult = new HashSet<>();
        for(int id : IDLst) {//loop 1
            unionResult.addAll(silos.get(id).getObfSet());//加入 fake random Set<FederateCommon.Point>
            if (id % num == 0) {
                break;
            }
        }

        for(int id : IDLst) {//loop 2
            unionResult.removeAll(silos.get(id).getObfSet());//移除 fake random Set<FederateCommon.Point>
            unionResult.addAll(silos.get(id).getLocalSet());//加入 真实数据
        }
        return unionResult;

    }

    public static Set<FederateCommon.Point> plaintextUnion(HashMap<Integer, SiloTest> silos) {
        Set<FederateCommon.Point> unionResult = new HashSet<>();
        for(int k : silos.keySet()) {
            unionResult.addAll(silos.get(k).getLocalSet());
        }
        return unionResult;
    }

    public  static void main(String[] args) {
        int num = 3;
        int[] iDLst = {1,2,3};
        SiloTest[] SiloLst = new SiloTest[num];
        SiloLst[0] = new SiloTest("C1");
        SiloLst[1] = new SiloTest("C2");
        SiloLst[2] = new SiloTest("C3");

        initSiloFromCsv("suda-core/src/main/java/com/suda/federate/security/sha/osm_part", SiloLst);
        HashMap<Integer, SiloTest> Silos = new HashMap<>(num);
        Silos.put(1, SiloLst[0]);
        Silos.put(2, SiloLst[1]);
        Silos.put(3, SiloLst[2]);

        Set<FederateCommon.Point> plaintextUnionResult = plaintextUnion(Silos);
        System.out.println("---------------------明文集合求并---------------------------");
        System.out.println(plaintextUnionResult.size());
        for (FederateCommon.Point p : plaintextUnionResult) {
            System.out.println(p);
        }
        System.out.println("----------------------------------------------------------");

        Set<FederateCommon.Point> unionResult = randomShares(iDLst, num, Silos);
        System.out.println("---------------------集合安全求并---------------------------");
        System.out.println(unionResult.size());
        for(FederateCommon.Point p : unionResult) {
            System.out.println(p);
        }
        System.out.println("----------------------------------------------------------");

        System.out.println("明文求并与集合安全求并是否一致: " + plaintextUnionResult.equals(unionResult));
    }
}
