package com.suda.federate.driver;

import com.suda.federate.config.DbConfig;
import com.suda.federate.driver.utils.SQLGenerator;
import com.suda.federate.rpc.FederateCommon;
import com.suda.federate.rpc.FederateService;
import com.suda.federate.security.sha.SiloCache;
import com.suda.federate.silo.FederateDBServer;
import com.suda.federate.silo.FederateDBService;
import com.suda.federate.utils.FederateUtils;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.suda.federate.security.sha.SecretSum.setSummation;
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
            DbConfig config = FederateUtils.configInitialization(configFile).get(0);

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

