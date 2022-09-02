mvn clean package -Dmaven.test.skip=true
cp suda-core/target/core-jar-with-dependencies.jar release/core/FederateDB-core.jar
#cp suda-core/target/classes/model.json ./release/core
#cp suda-core/target/classes/query.json ./release/core
cp suda-postgresql-driver/target/postgresql-driver-jar-with-dependencies.jar ./release/silo1/postgis-driver.jar
cp suda-mysql-driver/target/mysql-driver-jar-with-dependencies.jar ./release/silo2/mysql-driver.jar
