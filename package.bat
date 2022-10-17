@echo off
call mvn clean package -T 3 -Dmaven.test.skip=true
echo "package completed."

copy suda-core\target\core-jar-with-dependencies.jar release\core\FederateDB-core.jar /Y
::copy suda-core/target/classes/model.json ./release/core
::copy suda-core/target/classes/query.json ./release/core

for /l %%I in (0, 1, 4) do (
IF NOT EXIST "release\silo%%I" MD "release\silo%%I"
copy suda-postgresql-driver\target\postgresql-driver-jar-with-dependencies.jar release\silo%%I\postgis-driver.jar /Y
)
echo "copy completed."