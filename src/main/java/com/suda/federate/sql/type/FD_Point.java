package com.suda.federate.sql.type;

import com.suda.federate.utils.ENUM;

public class FD_Point extends FD_Variable<FD_Point.Location> {

    public FD_Point(String name, Float x, Float y) {
        super(ENUM.DATA_TYPE.POINT, name, new Location(x, y));
    }



    @Override
    public String toString() {
        return "Point" + value;
    }

    public static class Location {
        public Float x, y;

        public Location(Float x, Float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }
}
