package com.bricefamily.alex.time_tracker;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseBooleanArray;

/**
 * Created by bricenangue on 02/02/16.
 */
public class SparceBooleanArrayParecelable extends SparseBooleanArray implements Parcelable {
    public static Parcelable.Creator<SparceBooleanArrayParecelable> CREATOR = new Parcelable.Creator<SparceBooleanArrayParecelable>() {
        @Override
        public SparceBooleanArrayParecelable createFromParcel(Parcel source) {
            SparceBooleanArrayParecelable read = new SparceBooleanArrayParecelable();
            int size = source.readInt();

            int[] keys = new int[size];
            boolean[] values = new boolean[size];

            source.readIntArray(keys);
            source.readBooleanArray(values);

            for (int i = 0; i < size; i++) {
                read.put(keys[i], values[i]);
            }

            return read;
        }

        @Override
        public SparceBooleanArrayParecelable[] newArray(int size) {
            return new SparceBooleanArrayParecelable[size];
        }
    };

    public SparceBooleanArrayParecelable() {

    }

    public SparceBooleanArrayParecelable(SparseBooleanArray sparseBooleanArray) {
        for (int i = 0; i < sparseBooleanArray.size(); i++) {
            this.put(sparseBooleanArray.keyAt(i), sparseBooleanArray.valueAt(i));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        int[] keys = new int[size()];
        boolean[] values = new boolean[size()];

        for (int i = 0; i < size(); i++) {
            keys[i] = keyAt(i);
            values[i] = valueAt(i);
        }

        dest.writeInt(size());
        dest.writeIntArray(keys);
        dest.writeBooleanArray(values);
    }
}