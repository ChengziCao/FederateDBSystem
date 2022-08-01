//package com.suda.federate.application;
//
//import com.suda.federate.sql.table.FederateTableInfo;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//public class FedSpatialClient {//被FederateSchema 调用
//        private static final Logger LOG = LogManager.getLogger(FedSpatialClient.class);
//
//        private final Map<String, FederateDBClient> dbClientMap;
//        private final Map<String, FederateTableInfo> tableMap;
//        private final ExecutorService executorService;
//        public static void main(String[] args) {
//            FedSpatialClient fedSpatialClient = new FedSpatialClient();
//
//        }
//        public FedSpatialClient() {
//            dbClientMap = new HashMap<>();
//            tableMap = new HashMap<>();
//            this.executorService = Executors.newFixedThreadPool(2);
//        }
//
//        public Map<String, FederateDBClient> getDBClientMap() {
//            return dbClientMap;
//        }
//
//        public Map<String, FederateTableInfo> getTableMap() {
//            return tableMap;
//        }
//
//        public ExecutorService getExecutorService() {
//            return executorService;
//        }
//
//        // for federateDB
////        public boolean addFederate(String endpoint) {
////            if (hasFederate(endpoint)) {
////                return false;
////            }
////            FederateDBClient newClient = new FederateDBClient(endpoint);
////            for (Map.Entry<String, FederateDBClient> entry : dbClientMap.entrySet()) {
////                entry.getValue().addClient(endpoint);
////                newClient.addClient(entry.getKey());
////            }
////            dbClientMap.put(endpoint, newClient);
////            return true;
////        }
//
//        public boolean hasFederate(String endpoint) {
//            return dbClientMap.containsKey(endpoint);
//        }
//
//        public FederateDBClient getDBClient(String endpoint) {
//            return dbClientMap.get(endpoint);
//        }
//
//        // for global table
//        public void addTable(String tableName, FederateTableInfo table) {
//            this.tableMap.put(tableName, table);
//        }
//
//        public void dropTable(String tableName) {
//            tableMap.remove(tableName);
//        }
//
//        public FederateTableInfo getTable(String tableName) {
//            return tableMap.get(tableName);
//        }
//
//        public boolean hasTable(String tableName) {
//            return tableMap.containsKey(tableName);
//        }
//
//        // for local table
//        public void addLocalTable(String globalTableName, FederateDBClient client, String localTableName) {
//            FederateTableInfo table = getTable(globalTableName);
//            if (table != null) {
//                table.addFed(client, localTableName);
//            }
//        }
//        public Map<FederateDBClient, String> getTableClients(String tableName) {
//            FederateTableInfo table = getTable(tableName);
//            return table != null ? table.getTableMap() : null;
//        }
//
////        public void clearCache(String uuid, Map<FederateDBClient, String> tableClients) {
////            for (Map.Entry<FederateDBClient, String> entry : tableClients.entrySet()) {
////                FederateDBClient client = entry.getKey();
////                client.clearCache(uuid);
////            }
////        }
//
//    }
