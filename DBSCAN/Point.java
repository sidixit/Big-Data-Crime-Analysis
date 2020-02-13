import org.apache.hadoop.io.*;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author Wei on 5/8/17.
 */
public class Point implements WritableComparable<Point>{
    //-5 stands for initialized by default, -1 stands for noise point
    private int clusterId;

    //-1 stands for initialized by default
    private double x, y;

    //8888888.8888888 stands for initialized by default
    private double partitionIndex;

    //If this point is considered to be boundary point
    private boolean isBoundary;

    //If this point is a core point. Definition based on radius and minimum number of points within this radius
    private boolean isCore;

    public void write(DataOutput out) throws IOException {
        out.writeInt(clusterId);
        out.writeDouble(x);
        out.writeDouble(y);
        out.writeDouble(partitionIndex);
        out.writeBoolean(isBoundary);
        out.writeBoolean(isCore);
    }

    public void readFields(DataInput in) throws IOException {
        clusterId = in.readInt();
        x = in.readDouble();
        y = in.readDouble();
        partitionIndex = in.readDouble();
        isBoundary = in.readBoolean();
        isCore = in.readBoolean();
    }

    public boolean equals(Point point) {
        return this.x == point.getX() && this.y == point.getY();
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public int compareTo(Point point) {

        if(x == point.x && y == point.y) {
            return 0;
        } else if(x == point.x) {
            if(y - point.y > 0) {
                return 1;
            } else {
                return -1;
            }
        } else {
            if(x - point.x > 0) {
                return 1;
            } else {
                return -1;
            }
        }

    }

    @Override
    public String toString() {
        return "Point{" +
                "clusterId=" + clusterId +
                ", x=" + x +
                ", y=" + y +
                ", partitionIndex=" + partitionIndex +
                ", isBoundary=" + isBoundary +
                ", isCore=" + isCore +
                '}';
    }

    public Point() {
        setClusterId(-5);
        setIsBoundary(false);
        setIsCore(false);
        setPartitionIndex(8888888.8888888);
        setX(-1);
        setY(-1);
    }

    public Point(int clusterId, double x, double y, double partitionIndex, boolean isBoundary, boolean isCore) {
        this.clusterId = clusterId;
        this.x = x;
        this.y = y;
        this.partitionIndex = partitionIndex;
        this.isBoundary = isBoundary;
        this.isCore = isCore;
    }

    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getPartitionIndex() {
        return partitionIndex;
    }

    public void setPartitionIndex(double partitionIndex) {
        this.partitionIndex = partitionIndex;
    }

    public boolean isBoundary() {
        return isBoundary;
    }

    public void setIsBoundary(boolean boundary) {
        isBoundary = boundary;
    }

    public boolean isCore() {
        return isCore;
    }

    public void setIsCore(boolean core) {
        isCore = core;
    }

}