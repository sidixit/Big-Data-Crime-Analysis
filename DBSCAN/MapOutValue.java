import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author  by Wei on 5/10/17.
 */
public class MapOutValue implements Writable{
    private int clusterId;

    /**
     * If the point is core point, set this field to true.
     * Because core points can merge with clusters from other partitions
     */

    private boolean merge;

    private long partitionId;

    public MapOutValue() {
    }
    
    public void write(DataOutput out) throws IOException {
        out.writeBoolean(merge);
        out.writeInt(clusterId);
        out.writeLong(partitionId);
    }

    public void readFields(DataInput in) throws IOException {
        merge = in.readBoolean();
        clusterId = in.readInt();
        partitionId = in.readLong();
    }

    public MapOutValue(int clusterId, boolean merge, long partitionId) {
        this.clusterId = clusterId;
        this.merge = merge;
        this.partitionId = partitionId;
    }

    public long getPartitionId() {
        return partitionId;
    }

    public void setPartitionId(long partitionId) {
        this.partitionId = partitionId;
    }

    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    public boolean isMerge() {
        return merge;
    }

    public void setMerge(boolean merge) {
        this.merge = merge;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapOutValue that = (MapOutValue) o;

        if (clusterId != that.clusterId) return false;
        if (partitionId != that.partitionId) return false;
        return merge == that.merge;
    }

    @Override
    public int hashCode() {
        int result = clusterId;
        result = 31 * result + (merge ? 1 : 0);
        result = 31 * result + (int) (partitionId ^ (partitionId >>> 32));
        return result;
    }
}