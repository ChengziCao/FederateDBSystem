package com.suda.federate.driver;

import com.suda.federate.application.FederateDBClient;
import com.suda.federate.config.DbConfig;
import com.suda.federate.driver.utils.SQLGenerator;
import com.suda.federate.rpc.FederateCommon;
import com.suda.federate.rpc.FederateGrpc;
import com.suda.federate.rpc.FederateService;
import com.suda.federate.security.sha.SiloCache;
import com.suda.federate.silo.FederateDBServer;
import com.suda.federate.silo.FederateDBService;
import com.suda.federate.sql.function.FD_Knn;
import com.suda.federate.sql.function.FD_RangeCount;
import com.suda.federate.sql.function.FD_RangeQuery;
import com.suda.federate.sql.type.FD_Point;
import com.suda.federate.sql.type.Point;
import com.suda.federate.utils.ENUM;
import com.suda.federate.utils.FederateUtils;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.suda.federate.security.sha.SecretSum.localClient;
import static com.suda.federate.security.sha.SecretSum.setSummation;

public class PostgresqlServer extends FederateDBServer {
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(PostgresqlServer.class);
    private static boolean isLeader=false;
    private static class FederatePostgresqlService<T> extends FederateDBService {
        private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(FederatePostgresqlService.class);
        private DatabaseMetaData metaData;
        private Connection conn;
        private ENUM.DATABASE databaseType;

        FederatePostgresqlService(DbConfig config) {
            try {
                init(config);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void init(DbConfig config) throws ClassNotFoundException, SQLException {
            databaseType = ENUM.DATABASE.POSTGRESQL;//TODO
            Class.forName(config.getDriver());
            conn = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());

        }


        @Override
        public void rangeCount(FederateService.SQLExpression request, StreamObserver<FederateService.SQLReply> responseObserver) {
            System.out.println("收到的信息：" + request.getFunction());
            Integer result=0;
            try {
                result = localRangeCount(request.getPoint(),request.getTable(), request.getLiteral());
                FederateService.SQLReply reply = setSummation(request,result);
                responseObserver.onNext(reply);
                responseObserver.onCompleted();
            } catch (SQLException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                System.out.println("eror"+e.getMessage());
            }

        }
        @Override
        public void privacyUnion(FederateService.UnionRequest request, StreamObserver<FederateService.UnionResponse> responseObserver) {
            Integer currIndex= request.getIndex();
            if(federateClientMap==null || federateClientMap.isEmpty()){
                initClients(request.getEndpointsList());//索引endpoint都初始化，一劳永逸（如果loop顺序不变，建议只初始化nextnexFederateDBClient）
            }
            if (currIndex==-1){
                isLeader=true;
                currIndex+=1;
            }

            if (isLeader){

                List<FederateCommon.Point> points = new ArrayList<>();
                String nextEndpoint = request.getEndpoints(currIndex);
                FederateDBClient nextFederateDBClient = getClient(nextEndpoint);
                SiloCache siloCache = (SiloCache) buffer.get(request.getUuid());

                FederateService.UnionRequest nexRequest = request.toBuilder()
                        .setIndex(currIndex)
                        .addAllPoint(siloCache.getObfSet()).build();
                List<Callable<FederateService.UnionResponse>> task1 = new ArrayList<>();
                task1.add(() -> {
                    FederateService.UnionResponse unionResponse= nextFederateDBClient.privacyUnion(nexRequest);
                    return unionResponse;
                });
                FederateService.UnionResponse recUnionResponse = null;
                try {
                    List<Future<FederateService.UnionResponse>> alphaList = executorService.invokeAll(task1);
                    for (Future<FederateService.UnionResponse> falpha : alphaList) {
                        recUnionResponse =falpha.get();
                        break;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                assert recUnionResponse != null;
                Integer loop = recUnionResponse.getLoop();
                Integer index =recUnionResponse.getIndex();
                if (loop)
//            FederateDBClient federateDBClient = getClient(nextId);
//            federateDBClient.privacyUnion()
        }}

        @Override
        public void privacyRangeQuery(FederateService.SQLExpression request, StreamObserver<FederateService.Status> responseObserver) {
            System.out.println("收到的信息：" + request.getFunction());
            FederateService.Status status;
            try {
                List<FederateCommon.Point> res =localRangeQuery(request.getPoint(), request.getLiteral(),FederateCommon.Point.class);
                SiloCache siloCache = new SiloCache(res);
                buffer.set(request.getUuid(),siloCache);
                status=FederateService.Status.newBuilder().build();
                responseObserver.onNext(status);// 表示查成功了，不返回具体结果
                responseObserver.onCompleted();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void rangeQuery(FederateService.SQLExpression request, StreamObserver<FederateService.SQLReplyList> responseObserver) {
            System.out.println("收到的信息：" + request.getFunction());
            FederateService.SQLReplyList.Builder replyList = null;
            try {
                List<String> res =localRangeQuery(request.getPoint(), request.getLiteral(),String.class);
                replyList = FederateService.SQLReplyList.newBuilder()
                        .addAllMessage(res);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //构造返回
            assert replyList != null;
            responseObserver.onNext(replyList.build());
            responseObserver.onCompleted();
        }

        @Override
        public void knnRadiusQuery(FederateService.SQLExpression request, StreamObserver<FederateService.SQLReply> responseObserver) {
            System.out.println("收到的信息：" + request.getFunction());
            Double result=0.0;
            try {
                Double k =request.getLiteral();
                int kk = k.intValue();
                result = localKnnRadiusQuery(request.getPoint(),request.getTable(),kk);
            } catch (SQLException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            //构造返回
            FederateService.SQLReply reply = FederateService.SQLReply.newBuilder().setMessage(result).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        /**
         * query: select RangeCounting (P, radius) from table_name;
         * result: Integer，The number of points whose distance from P < radius in table_name.
         *
         * @param point  query location
         * @param radius range count radius
         */
        public Integer localRangeCount(FederateCommon.Point point,String tableName, Double radius) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

            // 读取参数
            // TODO: plaintext query

            // 生成目标 SQL
            String sql = SQLGenerator.generateRangeCountingSQL(point,tableName,radius, databaseType);
            LOGGER.info(String.format("\n%s Target SQL: ", "postgresql") + sql);
            // 执行 SQL
            Integer ans = executeSql(sql, Integer.class, false);

            LOGGER.info(String.format("\n%s RangeCount Result: ", "postgresql") + ans);

            // TODO: secure summation
            return ans;
        }

        public Double localKnnRadiusQuery(FederateCommon.Point point,String tableName, Integer k) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {//knn主函数
            Double minRadius = Double.MAX_VALUE;
            // 初始化查询半径
            String sql = SQLGenerator.generateKnnRadiusQuerySQL(point,tableName, k, databaseType);
            Double ans = executeSql(sql, Double.class, false);
            return ans;
        }


        /**
         * query: select RangeQuery (P, radius) from table_name;
         * result: List<point>，points whose distance from P < radius in table_name.
         *
         * @param point  query location
         * @param radius range count radius
         */
        private <T> List<T>  localRangeQuery(FederateCommon.Point point, Double radius,Class<T> resultClass) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {//select * 获取knn结果
            // 生成 SQL
            String sql = SQLGenerator.generateRangeQuerySQL(point, radius, databaseType);
            LOGGER.info(String.format("\n%s Target SQL: ", "postgresql") + sql);
            // 执行 SQL
            List<T> pointList = executeSql(sql, resultClass);
            LOGGER.info(String.format("\n%s RangeQuery Result:", "postgresql") + pointList.toString());

            return pointList;
        }


        private boolean setCompare(List<Float> list, Float k) {
            return true;
        }


        public void localQuery(FederateService.SQLExpression expression) throws Exception {
//        if (expression.getFunction() == ENUM.FUNCTION.RANGE_COUNT.name()) {
//            FD_RangeCount rangeCounting = new FD_RangeCount(expression);
//            Integer result = localRangeCount(rangeCounting.point, rangeCounting.radius);
//            LOGGER.info("\nAggregation Result:" + result);
//        } else if (expression.function == ENUM.FUNCTION.RANGE_QUERY) {
//            FD_RangeQuery rangeQuery = new FD_RangeQuery(expression);
//            List<FD_Point> pointList = localRangeQuery(rangeQuery.point, rangeQuery.radius);
//            LOGGER.info("\nAggregation Result: " + pointList.toString());
//        } else if (expression.function == ENUM.FUNCTION.KNN) {
//            FD_Knn knnQuery = new FD_Knn(expression);
//            List<FD_Point> pointList = localKnnQuery(knnQuery.point, knnQuery.k);
//            assert pointList != null;
//            LOGGER.info("\nAggregation Result: " + pointList.toString());
//        } else {
//            throw new Exception("type not support.");
//        }
        }
        public <T> List<T> executeSql(String sql) throws SQLException {
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery(sql);
            List<T> resultList = new ArrayList<>();
//            while (resultSet.next()) {
//                T t = resultSet2Object(resultSet, clazz);
//                resultList.add(t);
//            }
            return null;
        }
        public <T> List<T> executeSql(String sql, Class<T> resultClass) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery(sql);
            return resultSet2List(resultSet, resultClass);
        }

        public <T> T executeSql(String sql, Class<T> resultClass, Boolean listFlag) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
            if (listFlag) {
                // to do something
                return null;
            } else {
                Statement stmt = conn.createStatement();
                ResultSet resultSet = stmt.executeQuery(sql);
                return resultSet2Object(resultSet, resultClass);
            }
        }
    }

    public PostgresqlServer(DbConfig config, int port) throws IOException {
        super(ServerBuilder.forPort(port), port, new FederatePostgresqlService(config));
    }


    public static void main(String[] args) throws Exception {
        String configFile = "config.json";
        DbConfig config = FederateUtils.configInitialization(configFile).get(0);

        int grpcPort = 8887;
        System.out.println("666");
        PostgresqlServer server = new PostgresqlServer(config, grpcPort);

        server.start();
        server.blockUntilShutdown();
    }



    public static String getStackTraceString(Throwable ex) {//(Exception ex) {
        StackTraceElement[] traceElements = ex.getStackTrace();

        StringBuilder traceBuilder = new StringBuilder();

        if (traceElements != null && traceElements.length > 0) {
            for (StackTraceElement traceElement : traceElements) {
                traceBuilder.append("\t").append(traceElement.toString());
                traceBuilder.append("\n");
            }
        }
        return traceBuilder.toString();
    }

    //构造异常堆栈信息
    public static String buildErrorMessage(Exception ex) {

        String result;
        String stackTrace = getStackTraceString(ex);
        String exceptionType = ex.toString();
        String exceptionMessage = ex.getMessage();

        result = String.format("%s : %s \r\n %s", exceptionType, exceptionMessage, stackTrace);
        return result;
    }
}
