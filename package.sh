mvn clean package -T 8 -Dmaven.test.skip=true
mkdir -p release
cp suda-core/target/core-jar-with-dependencies.jar ./release/core/FederateDB-core.jar
#cp suda-core/target/classes/model.json ./release/core
#cp suda-core/target/classes/query.json ./release/core
mkdir -p release/silo1
cp suda-postgresql-driver/target/postgresql-driver-jar-with-dependencies.jar ./release/silo1/postgis-driver.jar
mkdir -p release/silo2
cp suda-postgresql-driver/target/postgresql-driver-jar-with-dependencies.jar ./release/silo2/postgis-driver.jar
mkdir -p release/silo3
cp suda-postgresql-driver/target/postgresql-driver-jar-with-dependencies.jar ./release/silo3/postgis-driver.jar
#cp suda-mysql-driver/target/mysql-driver-jar-with-dependencies.jar ./release/silo2/mysql-driver.jar
