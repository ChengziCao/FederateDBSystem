package com.suda.federate.silo;

import com.suda.federate.rpc.FederateCommon;
import com.suda.federate.rpc.FederateGrpc;
import com.suda.federate.utils.ENUM.DATABASE;
import com.suda.federate.utils.FederateUtils;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FederateDBServer {
    private static final Logger LOG = LogManager.getLogger(FederateDBServer.class);
    protected final int port;
    protected final Server server;

    public FederateDBServer(ServerBuilder<?> serverBuilder, int port, FederateGrpc.FederateImplBase service)
            throws IOException {
        this.port = port;
        server = serverBuilder.addService(service).build();
    }

    public FederateDBServer(int port, FederateGrpc.FederateImplBase service)
            throws IOException {
        this.port = port;
        server = ServerBuilder.forPort(port)
                .addService(service)
                .build();
    }

    public void start() throws IOException {
        server.start();
        LOG.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown
                // hook.
                LOG.info("*** shutting down gRPC server since JVM is shutting down");
                try {
                    FederateDBServer.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                LOG.info("*** server shut down");
            }
        });
    }

    /**
     * Stop serving requests and shutdown resources.
     */
    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon
     * threads.
     */
    protected void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}