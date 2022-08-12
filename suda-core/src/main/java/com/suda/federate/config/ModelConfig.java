package com.suda.federate.config;

import java.util.List;

public class ModelConfig {
    private String version;
    private String defaultSchema;
    private List<Schemas> schemas;

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return this.version;
    }

    public void setDefaultSchema(String defaultSchema) {
        this.defaultSchema = defaultSchema;
    }

    public String getDefaultSchema() {
        return this.defaultSchema;
    }

    public void setSchemas(List<Schemas> schemas) {
        this.schemas = schemas;
    }

    public List<Schemas> getSchemas() {
        return this.schemas;
    }

    public static class Schemas {
        private String name;
        private String type;

        //private String factory;

        private List<String> endpoints;

        private List<Tables> tables;

        public List<String> getEndpoints() {
            return endpoints;
        }

        public void setEndpoints(List<String> endpoints) {
            this.endpoints = endpoints;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getType() {
            return this.type;
        }

        //public void setFactory(String factory) {
        //    this.factory = factory;
        //}
        //
        //public String getFactory() {
        //    return this.factory;
        //}


        public void setTables(List<Tables> tables) {
            this.tables = tables;
        }

        public List<Tables> getTables() {
            return this.tables;
        }
    }

    public static class Feds {

        private String endpoint;
        private String ip;
        private Integer port;
        private String siloTableName;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }


        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
            String[] es = endpoint.split(":");
            setIp(es[0]);
            setPort(Integer.parseInt(es[1]));
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setSiloTableName(String siloTableName) {
            this.siloTableName = siloTableName;
        }

        public String getSiloTableName() {
            return siloTableName;
        }

    }


    public static class Tables {

        private String name;
        private List<Feds> feds;

        //private String factory;
        public List<Feds> getFeds() {
            return feds;
        }

        public void setFeds(List<Feds> feds) {
            this.feds = feds;
        }


        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        //public void setFactory(String factory) {
        //    this.factory = factory;
        //}
        //public String getFactory() {
        //    return factory;
        //}


    }

}

