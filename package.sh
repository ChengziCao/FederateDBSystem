mvn clean package -Dmaven.test.skip=true
cp target/FederateDB-jar-with-dependencies.jar release/FederateDb.jar
cp target/classes/config.json ./release
cp target/classes/query.json ./release