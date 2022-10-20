package com.suda.federate;

import com.suda.federate.config.DbConfig;
import com.suda.federate.silo.FederateDBServer;
import com.suda.federate.utils.FederateUtils;
import com.suda.federate.utils.LogUtils;
import io.grpc.ServerBuilder;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;

import static com.suda.federate.utils.FederateUtils.buildErrorMessage;

public class PostgresqlServer extends FederateDBServer {
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(PostgresqlServer.class);

    public PostgresqlServer(DbConfig config, int port) throws IOException {
        super(ServerBuilder.forPort(port), port, new FederatePostgresqlService(config));
    }


    public static void main(String[] args) {
        try {
            String configFile = "config.json";
            DbConfig config = FederateUtils.parseDbConfig(configFile);
            int grpcPort = config.getGrpcPort();
            PostgresqlServer server = new PostgresqlServer(config, grpcPort);
            LogUtils.debug("PostgresqlServer started...");
            server.start();
            server.blockUntilShutdown();
        } catch (Exception e) {
            LOGGER.error(buildErrorMessage(e));
        }
    }
}

