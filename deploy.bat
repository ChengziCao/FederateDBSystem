@echo off
call package.bat

copy suda-core\target\core-jar-with-dependencies.jar release\core\FederateDB-core.jar /Y
::copy suda-core/target/classes/model.json ./release/core
::copy suda-core/target/classes/query.json ./release/core


for /l %%I in (0, 1, 4) do (
echo %%I
IF NOT EXIST "release\silo%%I" MD "release\silo%%I"
copy suda-postgresql-driver\target\postgresql-driver-jar-with-dependencies.jar release\silo%%I\postgis-driver.jar /Y
)

echo "copy completed."

::被复制的源文件夹
set sourceDir="D:\OneDrive - stu.suda.edu.cn\repository\repository-project\FederateDBSystem\release"
::目标文件夹
set tarDir="D:\Docker-Container\Spatial-FDBS-Container"
::复制并覆盖文件及文件夹
xcopy %sourceDir% %tarDir% /s/y
