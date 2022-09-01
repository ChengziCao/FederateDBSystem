package com.suda.federate;


import com.suda.federate.config.DbConfig;
import com.suda.federate.silo.FederateDBServer;
import com.suda.federate.utils.FederateUtils;
import com.suda.federate.utils.LogUtils;
import io.grpc.ServerBuilder;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;

public class MysqlServer extends FederateDBServer {
    private static boolean isLeader = false;

    public MysqlServer(DbConfig config, int port) throws IOException {
        super(ServerBuilder.forPort(port), port, new FederateMysqlService(config));
    }

    public static void main(String[] args) throws Exception {
        String configFile = "config.json";
        DbConfig config = FederateUtils.parseDbConfig(configFile);
        int grpcPort = config.getGrpcPort();
        MysqlServer server = new MysqlServer(config, grpcPort);
        LogUtils.debug("MysqlServer started...");
        server.start();
        server.blockUntilShutdown();
    }
}
