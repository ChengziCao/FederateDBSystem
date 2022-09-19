package com.suda.federate.function;

import com.suda.federate.config.ModelConfig;
import com.suda.federate.rpc.FederateCommon;
import com.suda.federate.rpc.FederateService;
import com.suda.federate.silo.FederateDBClient;
import com.suda.federate.utils.FederateUtils;
import com.suda.federate.utils.LogUtils;
import com.suda.federate.utils.StreamingIterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * TODO: table name --> silo table name
 */
public abstract class FederateQuery {
    public static List<FederateDBClient> federateDBClients = new ArrayList<>();
    public static List<String> endpoints = new ArrayList<>();
    // public static Map<String, FederateDBClient> federateClientMap = new HashMap<>();
    public static ExecutorService executorService;

    /**
     * public query
     *
     * @param expression
     * @throws InterruptedException
     * @throws ExecutionException
     */
    protected static List<FederateService.SQLReply> fedSpatialPublicQuery(FederateService.SQLExpression expression) throws Exception {
        StreamingIterator<FederateService.SQLReply> iterator = new StreamingIterator<>(federateDBClients.size());
        List<Callable<Boolean>> tasks = new ArrayList<>();
        for (FederateDBClient federateDBClient : federateDBClients) {
            tasks.add(() -> {
                try {
                    FederateService.SQLReply reply = federateDBClient.fedSpatialQuery(expression);
                    iterator.add(reply);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    iterator.finish();
                }
            });
        }
        // TODO 如果某个 data silos 阻塞，全局都会阻塞
        List<Future<Boolean>> statusList = executorService.invokeAll(tasks);

        checkStatus(statusList, Future.class);

        List<FederateService.SQLReply> replyList = new ArrayList<>();
        while (iterator.hasNext()) {
            replyList.add(iterator.next());
        }

        return replyList;
    }

    protected static Integer publicSummation(List<FederateService.SQLReply> replyList) {
        return replyList.stream().mapToInt(x -> (int) x.getIntegerNumber()).sum();
    }

    protected static List<FederateCommon.Point> publicUnion(List<FederateService.SQLReply> replyList) {
        return replyList.stream()
                .map(x -> x.getPointList())
                .flatMap(x -> x.stream())
                .collect(Collectors.toList());
    }

    protected static Integer privacySummation(FederateService.SQLExpression expression) {

        FederateService.SummationRequest.Builder requestBuilder = FederateService.SummationRequest.newBuilder();
        List<Integer> idList = federateDBClients.stream().map(x -> x.getId()).collect(Collectors.toList());
        int m = federateDBClients.size();
        int leaderIndex = 0;

        requestBuilder.setIndex(leaderIndex)
                .addAllIdList(idList)
                .setSiloSize(m)
                .setUuid(expression.getUuid())
                .addAllEndpoints(endpoints)
                .setResponse(FederateService.SummationResponse.newBuilder());

        FederateDBClient leaderFederateDBClient = federateDBClients.get(leaderIndex);

        FederateService.SummationResponse summationResponse = leaderFederateDBClient.privacySummation(requestBuilder.build());

        //清理各silo垃圾
        clearCache(expression.getUuid());
        return summationResponse.getCount();
    }

    protected static List<FederateCommon.Point> privacyUnion(FederateService.SQLExpression expression) {
        FederateService.UnionRequest.Builder unionRequest = FederateService.UnionRequest.newBuilder();

        int leaderIndex = 0;

        unionRequest.setLoop(1).setIndex(leaderIndex - 1).setUuid(expression.getUuid()).addAllEndpoints(endpoints);
        FederateDBClient leaderFederateDBClient = federateDBClients.get(leaderIndex);
        FederateService.UnionResponse unionResponse = leaderFederateDBClient.privacyUnion(unionRequest.build());
//        System.out.println(unionResponse);
        //清理各silo垃圾
        clearCache(expression.getUuid());
        return unionResponse.getPointList();
    }

    protected static void checkStatus(List<?> statusList, Class clazz) {
        boolean status = true;
        //if(statusList.get(0))
        if (clazz == Boolean.class) {
            status = statusList.stream().allMatch(x -> (Boolean) x);
        } else if (clazz == Future.class) {
            status = statusList.stream().allMatch(x -> {
                try {
                    return ((Future<Boolean>) x).get();
                } catch (Exception e) {
                    return false;
                }
            });
        }
        if (!status)
            throw new RuntimeException("error in fedSpatial Query");
    }

    protected static void clearCache(String uuid) {
        for (FederateDBClient client : federateDBClients) {
            client.clearCache(uuid);
        }
    }
}
