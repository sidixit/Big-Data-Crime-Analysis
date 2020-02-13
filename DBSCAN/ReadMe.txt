1. adjust heap size to 10M
2. Uplode input.txt to HDFS input folder
3. start hadoop
4. run hadoop job: final.jar
5. download output files in HDFS
In result, every point is followed by its clusterID. Each clusterID is a string consists of partitionIndex as well as local clusterID.

If user wants to adjuest radius and minPoint, 
1. change the parameter in DBSCAN.java
2. export DBSCAN.java, MapOutValue.java, Point.java to a new jar file
3. repeat run hadoop job




