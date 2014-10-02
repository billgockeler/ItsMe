package com.itsme.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageSet implements Parcelable {

    public String largeImageUrl;
    public String smallImageUrlOne;
    public String smallImageUrlTwo;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.largeImageUrl);
        dest.writeString(this.smallImageUrlOne);
        dest.writeString(this.smallImageUrlTwo);
    }

    public ImageSet() {
    }

    private ImageSet(Parcel in) {
        this.largeImageUrl = in.readString();
        this.smallImageUrlOne = in.readString();
        this.smallImageUrlTwo = in.readString();
    }

    public static final Parcelable.Creator<ImageSet> CREATOR = new Parcelable.Creator<ImageSet>() {
        public ImageSet createFromParcel(Parcel source) {
            return new ImageSet(source);
        }

        public ImageSet[] newArray(int size) {
            return new ImageSet[size];
        }
    };
}
