import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

/**
 * @author  Wei on 5/08/17.
 */
public class DBSCAN {
    public static class Map extends Mapper<LongWritable, Text, Point, MapOutValue> {

        public void map(LongWritable partitionIndex, Text pointsString, Context context) throws IOException, InterruptedException {
            final double radius;
            final int minPoints;

            radius = 0.5;
            minPoints = 15;      //This number doesn't include current point itself

            String line = pointsString.toString();
            StringTokenizer tokenizer = new StringTokenizer(line);

            Set<Point> points = new HashSet<Point>();

            //Read line and construct Points
            while(tokenizer.hasMoreTokens()) {
                Point point = new Point();

                String token1 = tokenizer.nextToken(",");
                String token2 = tokenizer.nextToken(",");

                double currentX = Double.parseDouble(token1);
                double currentY = Double.parseDouble(token2);
                point.setX(currentX);
                point.setY(currentY);
                point.setIsBoundary(isBoundary(radius, minPoints, 1, 1,1, 1,1));
                point.setPartitionIndex(partitionIndex.get());

                points.add(point);
            }

            //Do DBSCAN, set clusterId in Points
            java.util.Map<Point, MapOutValue> result = doDBSCAN(points, radius, minPoints, partitionIndex.get());

            for(final Point point : result.keySet()) {
                context.write(point, result.get(point));
            }

        }

        private java.util.Map<Point, MapOutValue> doDBSCAN(final Set<Point> points,
                                                           final double radius,
                                                           final int minPoints,
                                                           final long partitionIndex) {
            java.util.Map<Point, MapOutValue> result = new HashMap<Point, MapOutValue>();
            java.util.Map<Point, java.util.Map<Point, Double>> distances = getDistances(points);
            java.util.Map<Point, Set<Point>> neighbors = getNeighbors(distances, radius);

            int currClusterId = 0;
            //Mark point that has been drawn circle around.(Attempted to put into BFS queue)
            Set<Point> visited = new HashSet<Point>();

            for(Point point : points) {
                if(visited.contains(point)) {
                    continue;
                }

                //If current point is not a core point, mark it as noise for now
                if(neighbors.get(point).size() < minPoints) {
                    point.setClusterId(-1);
                    continue;
                }

                //If current point is core point, do BFS to build the whole cluster
                Queue<Point> queue = new LinkedList<Point>();
                queue.offer(point);
                point.setClusterId(currClusterId);

                while(!queue.isEmpty()) {
                    Point frontPoint = queue.poll();
                    visited.add(frontPoint);
                    Set<Point> currNeighbors = neighbors.get(frontPoint);

                    //If frontPoint is core, set all neighbors' clusterId = currClusterId, put core neighbors into queue
                    if(currNeighbors.size() >= minPoints) {

                        for(Point neighbor : currNeighbors) {
                            neighbor.setClusterId(currClusterId);

                            //If current neighbor point is not visited before && is core point, put it into queue for further BFS
                            if(!visited.contains(neighbor) && neighbors.get(neighbor).size() >= minPoints) {
                                //if(!visited.contains(neighbor)) {
                                queue.offer(neighbor);
                            }

                            //Mark as visited anyway
                            visited.add(neighbor);
                        }

                    }
                }

                currClusterId++;
            }

            for(Point p : points) {
                result.put(p, new MapOutValue(1, true, partitionIndex));
            }

            return result;
        }

        private java.util.Map<Point, java.util.Map<Point, Double>> getDistances(Set<Point> points) {
            java.util.Map<Point, java.util.Map<Point, Double>> results = new HashMap<Point, java.util.Map<Point, Double>>();

            for(Point point : points) {
                java.util.Map<Point,Double> distances = new HashMap<Point, Double>();

                for(Point otherPoint : points) {

                    if(otherPoint.equals(point)) {
                        continue;
                    }

                    Double distance = getDistance(point.getY(), point.getX(), otherPoint.getY(), otherPoint.getX());
                    //Double distance = Math.sqrt(Math.pow(point.getX() - otherPoint.getX(), 2) + Math.pow(point.getY() - otherPoint.getY(), 2));
                    distances.put(otherPoint, distance);
                }

                results.put(point, distances);
            }

            return results;
        }

        private java.util.Map<Point, Set<Point>> getNeighbors(java.util.Map<Point, java.util.Map<Point, Double>> distances, double radius) {
            java.util.Map<Point, Set<Point>> results = new HashMap<Point, Set<Point>>();

            for(Point point : distances.keySet()) {
                java.util.Map<Point, Double> distancesOfPoint = distances.get(point);
                Set<Point> neighbors = new HashSet<Point>();

                for(Point otherPoint : distancesOfPoint.keySet()) {
                    if(distancesOfPoint.get(otherPoint) <= radius) {
                        neighbors.add(otherPoint);
                    }
                }

                results.put(point, neighbors);
            }

            return results;
        }

        private boolean isBoundary(final double radius,
                                   final int minPoints,
                                   final int baseX,
                                   final int baseY,
                                   final int pointX,
                                   final int pointY,
                                   final int blockSize) {
            //TODO implement

            return true;
        }

        private static double EARTH_RADIUS = 6378.137;
        private static double rad(double d) {
            return d * Math.PI / 180.0;
        }

        //supposed to return kilometer
        private double getDistance(double lat1, double lng1, double lat2, double lng2) {
            double radLat1 = rad(lat1);
            double radLat2 = rad(lat2);
            double a = radLat1 - radLat2;
            double b = rad(lng1) - rad(lng2);
            double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2),2)));
            s = s * EARTH_RADIUS;
            return s;
        }
    }

    public static class Reduce extends Reducer<Point, MapOutValue, Text, IntWritable> {

        public void reduce(Point point, Iterable<MapOutValue> values, Context context)
                throws IOException, InterruptedException {
            Text outputText = new Text(Double.toString(point.getX())
                    + ","
                    + Double.toString(point.getY())
                    + " "
                    + Double.toString(point.getPartitionIndex()) + ","
                    + Integer.toString(point.getClusterId()));

            context.write(outputText, new IntWritable(0));
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = new Job(conf, "dbscan");

        job.setJarByClass(DBSCAN.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        job.setMapOutputKeyClass(Point.class);
        job.setMapOutputValueClass(MapOutValue.class);

        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.waitForCompletion(true);
    }

}
