package com.penjin.android.widget.calendar;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by maotiancai on 2016/1/19.
 */
public class PenjinCalendarPoint implements Parcelable {
    public int x;
    public int y;


    public PenjinCalendarPoint() {
    }

    public PenjinCalendarPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public PenjinCalendarPoint(PenjinCalendarPoint src) {
        this.x = src.x;
        this.y = src.y;
    }

    /**
     * Set the point's x and y coordinates
     */
    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Negate the point's coordinates
     */
    public final void negate() {
        x = -x;
        y = -y;
    }

    /**
     * Offset the point's coordinates by dx, dy
     */
    public final void offset(int dx, int dy) {
        x += dx;
        y += dy;
    }

    /**
     * Returns true if the point's coordinates equal (x,y)
     */
    public final boolean equals(int x, int y) {
        return this.x == x && this.y == y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PenjinCalendarPoint point = (PenjinCalendarPoint) o;

        if (x != point.x) return false;
        if (y != point.y) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return "CalendarPoint(" + x + ", " + y + ")";
    }

    protected PenjinCalendarPoint(Parcel in) {
        x = in.readInt();
        y = in.readInt();
    }

    public static final Creator<PenjinCalendarPoint> CREATOR = new Creator<PenjinCalendarPoint>() {
        @Override
        public PenjinCalendarPoint createFromParcel(Parcel in) {
            return new PenjinCalendarPoint(in);
        }

        @Override
        public PenjinCalendarPoint[] newArray(int size) {
            return new PenjinCalendarPoint[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(x);
        dest.writeInt(y);
    }
}
