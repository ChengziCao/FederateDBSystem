package com.suda.federate;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.geometry.Point;
import com.suda.federate.config.DbConfig;


import com.suda.federate.rpc.FederateCommon;
import com.suda.federate.rpc.FederateService;
import com.suda.federate.security.sha.SiloCache;
import com.suda.federate.silo.FederateDBService;

import com.github.davidmoten.rtree.RTree;
import com.suda.federate.utils.FederateUtils;
import io.grpc.stub.StreamObserver;

import java.io.*;

import java.util.*;

import com.github.davidmoten.rtree.geometry.Geometries;
import org.apache.commons.lang3.tuple.Pair;

import static com.github.davidmoten.rtree.geometry.Geometries.rectangle;
import static com.suda.federate.RKNNUtils.*;

public class FederateCSVService extends FederateDBService {
    private Map<String, RTree<String, Point>> conn;// key: tableName; value: RTree of tableName

    FederateCSVService(DbConfig config) {
        try {
            init(config);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Debug start
        FederateCommon.Point queryPoint = FederateCommon.Point.newBuilder().setLatitude(121.4).setLongitude(31.24).build();
        int c= localRangeCount(queryPoint,"osm_sh",12.0);
        System.out.println(c);
        localKnnRadiusQuery(queryPoint,"osm_sh",4);

        RTree<String, Point> rTree = conn.get("osm_sh");
        List<Point> CH = fPoint2DavidList(localRangeQuery(queryPoint,"osm_sh",666.0));//凸包
        List<Point> candidateSet = pointInRegion(search(rTree, polygon2MinRect(CH)) ,CH);

        List<FederateCommon.Point> rknn = david2FPointList(legalPoint(candidateSet, fPoint2David(queryPoint), rTree, CH.toArray().length));
        System.out.println(rknn);

        //Debug end
    }

    public void init(DbConfig config) throws UnsupportedEncodingException {
        String dbPath = FederateUtils.getRealPath(config.getUrl());
        File file = new File(dbPath);//需要读取的文件夹路径

        File[] tableList = file.listFiles();
        conn = new HashMap<>(tableList.length);
        for (int i = 0; i < tableList.length; i++) {
            String fileName = tableList[i].getName();//这里如果改成getAbsolutePath()可以得到文件的路径
            String tableName = fileName.substring(0, fileName.indexOf(".csv"));
            System.out.println(fileName);
            conn.put(tableName, InitRTreeFromCSV(tableList[i]));
        }
    }

    private static RTree<String, Point> InitRTreeFromCSV(File csvFile) {
        RTree<String, Point> tree = RTree.minChildren(3).maxChildren(6).create();//TODO 参数设置

        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {

            br = new BufferedReader(new FileReader(csvFile));
            line = br.readLine();
            while ((line = br.readLine()) != null) {

                // use comma as separator
                // id, lat, lon
                String[] country = line.split(cvsSplitBy);
                String id = country[0];
                double lat = Double.parseDouble(country[1]);
                double lon = Double.parseDouble(country[2]);
                tree = tree.add(id, Geometries.point(lon, lat));
                // System.out.println(id+", "+point(lon, lat));
                //System.out.println("Country [code= " + country[4] + " , name=" + country[5] + "]");

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return tree;
    }


    @Override
    public void publicRangeCount(FederateService.SQLExpression request, StreamObserver<FederateService.SQLReply> responseObserver) {
        System.out.println("收到的信息：" + request.getFunction());
        Integer result = 0;

        result = localRangeCount(request.getPoint(), request.getTable(), request.getLiteral());
        //构造返回
        FederateService.SQLReply reply = FederateService.SQLReply.newBuilder().setNum(result).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();

    }
    @Override
    public void publicRangeQuery(FederateService.SQLExpression request, StreamObserver<FederateService.SQLReply> responseObserver) {
        System.out.println("收到的信息：" + request.getFunction());
        FederateService.SQLReply.Builder replyList = null;
        try {
            List<FederateCommon.Point> res = localRangeQuery(request.getPoint(),request.getTable(), request.getLiteral());
            replyList = FederateService.SQLReply.newBuilder()
                    .setNum(res.size()).addAllPoint(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //构造返回
        assert replyList != null;
        responseObserver.onNext(replyList.build());
        responseObserver.onCompleted();
    }

    @Override
    public void knnRadiusQuery(FederateService.SQLExpression request, StreamObserver<FederateService.KnnRadiusQueryResponse> responseObserver) {
        System.out.println("收到的信息：" + request.getFunction());
        Double result = 0.0;

        Double k = request.getLiteral();
        int kk = k.intValue();
        result = localKnnRadiusQuery(request.getPoint(), request.getTable(), kk);

        //构造返回
        FederateService.KnnRadiusQueryResponse reply = FederateService.KnnRadiusQueryResponse.newBuilder().setRadius(result).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
    //RKNN 的request 必填参数(grpc)：point即queryPoint, polygon即凸包, literal即RKNN的K
    //PS: RTree太辣了，必须经过legalPoint等操作
    @Override
    public void privacyPolygonRangeQuery(FederateService.SQLExpression request, StreamObserver<FederateService.Status> responseObserver) {
        System.out.println("收到的信息：" + request.getFunction());
        FederateService.Status status;
        try {
            int K = (new Double(request.getLiteral())).intValue();
            List<FederateCommon.Point> res = localPolygonRangeQuery(request.getPoint(),request.getPolygon(),K,request.getTable());
            List<org.apache.commons.lang3.tuple.Pair<Double, Double>> resPairs = new ArrayList<>();
            for (FederateCommon.Point point : res) {
                resPairs.add(Pair.of(point.getLongitude(), point.getLatitude()));
            }
            SiloCache siloCache = new SiloCache(resPairs);
            buffer.set(request.getUuid(), siloCache);
            status = FederateService.Status.newBuilder().setCode(FederateService.Code.kOk).setMsg("ok").build();
            responseObserver.onNext(status);// 表示查成功了，不返回具体结果
            responseObserver.onCompleted();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //注意： Integer K 用于在RKNN中筛选结果。
    private List<FederateCommon.Point> localPolygonRangeQuery(FederateCommon.Point queryPoint,FederateCommon.Polygon polygon,Integer K,String table) {
        RTree<String, Point> rTree = conn.get(table);
        List<Point> CH = fPoint2DavidList(polygon.getPointList());//凸包
        List<Point> candidateSet = pointInRegion(search(rTree, polygon2MinRect(CH)) ,CH);

        return david2FPointList(legalPoint(candidateSet, fPoint2David(queryPoint), rTree, K));
    }


    private Double localKnnRadiusQuery(FederateCommon.Point fPoint, String table, int kk) {
        Point p = fPoint2David(fPoint);
        RTree<String, Point> rTree = conn.get(table);
        double radius = rTree.nearest(p,Double.MAX_VALUE,kk).last().toBlocking().single().geometry().distance(p);//TODO 参数设置 美化Double.MAX_VALUE
//        Iterable<Entry<String, Point>> knnEntries = rTree.nearest(p,Double.MAX_VALUE,kk).toBlocking().toIterable();
//        for(Entry<String, Point> entry : knnEntries) {
//            double r= entry.geometry().distance(p);
//            System.out.println(r);
//        }
        return radius;
    }


    private List<FederateCommon.Point> localRangeQuery(FederateCommon.Point fPoint, String table,  double literal) {
        RTree<String, Point> rTree = conn.get(table);
        //纬度---lat---x轴 经度---lon---y轴
        List< FederateCommon.Point> result = new ArrayList<>();

        Iterable<Entry<String, Point>> knnEntries =rTree.search(fPoint2David(fPoint), literal).toBlocking().toIterable();
        for (Entry<String, Point> entry : knnEntries) {
            result.add(david2FPoint(entry.geometry()));
        }
        return  result;
    }


    private Integer localRangeCount(FederateCommon.Point fPoint, String table, double literal) {
        RTree<String, Point> rTree = conn.get(table);
        //纬度---lat---x轴 经度---lon---y轴
        return rTree.search(fPoint2David(fPoint), literal).count().toBlocking().single();
    }

}
