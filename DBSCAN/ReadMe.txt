1. Adjust heap size to 10M
2. Upload input.txt to HDFS input folder
3. Start hadoop
4. Run hadoop job: final.jar
5. Download output files in HDFS

In result, every point is followed by its clusterID. Each clusterID is a string consisting of partitionIndex as well as local clusterID.

If users want to adjust radius and minPoint, 
1. Change the parameter in DBSCAN.java
2. Export DBSCAN.java, MapOutValue.java, Point.java to a new jar file
3. Repeat run hadoop job




