SET BATCH_CLASS_PATH1=.;
SET BATCH_CLASS_PATH2=C:\eclipse-workspace-gcgf\koreg\build\classes;
SET BATCH_PROPERTIES_PATH=C:\eclipse-workspace-gcgf\koreg\src\main\deploy\local\classes;
SET BATCH_LIB_PATH=C:\eclipse-workspace-gcgf\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\koreg\WEB-INF\lib\*;
SET BATCH_RESOURCE_PATH=C:\eclipse-workspace-gcgf\koreg\src\main\resources;
SET BATCH_TOMCAT_LIB_PATH=C:\eclipse-jee-2023-03-R-win32-x86_64\apache-tomcat-9.0.75\lib\tomcat-util.jar;
SET BATCH_CLASS_PATH=%BATCH_CLASS_PATH1%%BATCH_CLASS_PATH2%%BATCH_PROPERTIES_PATH%%BATCH_LIB_PATH%%BATCH_RESOURCE_PATH%%BATCH_TOMCAT_LIB_PATH%

SET BATCH_CLASS_NAME=com.batch.util
SET BATCH_NAME=SyncUser

SET JAVA_PATH=C:\eclipse-jee-2023-03-R-win32-x86_64\plugins\org.eclipse.justj.openjdk.hotspot.jre.full.win32.x86_64_17.0.6.v20230204-1729\jre\bin\java.exe

@echo cd C:\eclipse-workspace-gcgf\koreg\src\main\java
@echo userBatch.bat

%JAVA_PATH% -classpath "%BATCH_CLASS_PATH%" %BATCH_CLASS_NAME%.%BATCH_NAME%

