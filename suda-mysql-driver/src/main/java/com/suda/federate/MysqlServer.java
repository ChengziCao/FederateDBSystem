package com.suda.federate;


import com.suda.federate.config.DbConfig;
import com.suda.federate.silo.FederateDBServer;
import com.suda.federate.utils.FederateUtils;
import io.grpc.ServerBuilder;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;

public class MysqlServer extends FederateDBServer {
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(MysqlServer.class);
    private static boolean isLeader = false;

    public MysqlServer(DbConfig config, int port) throws IOException {
        super(ServerBuilder.forPort(port), port, new FederateMysqlService(config));
    }

    public static void main(String[] args) throws Exception {
        String configFile = "config.json";
        DbConfig config = FederateUtils.configInitialization(configFile).get(0);
        int grpcPort = 8886;
        System.out.println("666");
        MysqlServer server = new MysqlServer(config, grpcPort);
        server.start();
        server.blockUntilShutdown();
    }
}
