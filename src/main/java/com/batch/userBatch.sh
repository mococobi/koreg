BATCH_CLASS_PATH=.:/aphome/tomcat/webapps/BisPortal/WEB-INF/lib/*:/aphome/tomcat/webapps/BisPortal/WEB-INF/classes
BATCH_NAME=SyncUser
BATCH_CLASS_NAME=com.custom.batch.SyncUser

/usr/java/jdk-11.0.10/bin/java -DbatchTag=$BATCH_NAME -cp $BATCH_CLASS_PATH $BATCH_CLASS_NAME

exit 0