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
        private String factory;
        private Operand operand;
        private List<Tables> tables;

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

        public void setFactory(String factory) {
            this.factory = factory;
        }

        public String getFactory() {
            return this.factory;
        }

        public void setOperand(Operand operand) {
            this.operand = operand;
        }

        public Operand getOperand() {
            return this.operand;
        }

        public void setTables(List<Tables> tables) {
            this.tables = tables;
        }

        public List<Tables> getTables() {
            return this.tables;
        }
    }

    public static class Feds {

        private String endpoint;
        private String name;
        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }
        public String getEndpoint() {
            return endpoint;
        }

        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }

    }
    public static class Operand {

        private List<Feds> feds;
        public void setFeds(List<Feds> feds) {
            this.feds = feds;
        }
        public List<Feds> getFeds() {
            return feds;
        }

    }
    public static class Tables {

        private String name;
        private String factory;
        private Operand operand;
        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }

        public void setFactory(String factory) {
            this.factory = factory;
        }
        public String getFactory() {
            return factory;
        }

        public void setOperand(Operand operand) {
            this.operand = operand;
        }
        public Operand getOperand() {
            return operand;
        }

    }
    }

