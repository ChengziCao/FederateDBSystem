@echo off
call mvn clean package -T 3 -Dmaven.test.skip=true
echo "package completed."