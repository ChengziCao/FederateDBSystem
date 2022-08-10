package com.suda.federate;

import com.suda.federate.config.DbConfig;
import com.suda.federate.silo.FederateDBServer;
import com.suda.federate.utils.FederateUtils;
import io.grpc.ServerBuilder;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;

import static com.suda.federate.utils.FederateUtils.buildErrorMessage;

public class PostgresqlServer extends FederateDBServer {
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(PostgresqlServer.class);
    private static boolean isLeader = false;


    public PostgresqlServer(DbConfig config, int port) throws IOException {
        super(ServerBuilder.forPort(port), port, new FederatePostgresqlService(config));
    }


    public static void main(String[] args) throws Exception {

        try {
            String configFile = "config.json";
            DbConfig config = FederateUtils.parseDbConfig(configFile).get(0);

            int grpcPort = 8887;
            System.out.println("666");
            PostgresqlServer server = new PostgresqlServer(config, grpcPort);

            server.start();
            server.blockUntilShutdown();
        } catch (Exception e) {
            LOGGER.error(buildErrorMessage(e));
        }
    }
}

