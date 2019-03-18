package com.example.bucketlist;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "bucketlist")
public class BucketListItem implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "finished")
    private int finished;

    public BucketListItem(String title, String description, int finished) {
        this.title = title;
        this.description = description;
        this.finished = finished;
    }

    protected BucketListItem(Parcel in) {
        id = in.readInt();
        title = in.readString();
        description = in.readString();
        finished = in.readInt();
    }

    public static final Creator<BucketListItem> CREATOR = new Creator<BucketListItem>() {
        @Override
        public BucketListItem createFromParcel(Parcel in) {
            return new BucketListItem(in.readString(), in.readString(), in.readInt());
        }

        @Override
        public BucketListItem[] newArray(int size) {
            return new BucketListItem[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parc, int flags) {
        parc.writeString(this.title);
        parc.writeString(this.description);
        parc.writeInt(this.finished);
    }
}
