package com.suda.federate.sql.function;

import com.esri.core.geometry.GeometryEngine;
import com.suda.federate.sql.type.Point;
import org.apache.calcite.runtime.Geometries;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpatialFunctions {
  private SpatialFunctions() {
  }

  public static Double Distance(Geometries.Geom p1, Geometries.Geom p2) {
    return GeometryEngine.distance(p1.g(), p2.g(), p1.sr());
  }

  public static Geometries.Geom MakePoint(double lon, double lat) {
    return Point(lon, lat);
  }

  public static Geometries.Geom MakePoint(BigDecimal lon, BigDecimal lat) {
    return Point(lon.doubleValue(), lat.doubleValue());
  }

  public static Geometries.Geom Point(double lon, double lat) {
    return new Point(lon, lat);
  }

  public static Geometries.Geom Point(BigDecimal lon, BigDecimal lat) {
    return new Point(lon.doubleValue(), lat.doubleValue());
  }

  public static String AsText(Geometries.Geom p) {
    return p.toString();
  }

  public static Geometries.Geom GeomFromText(String s) {
    return Point.parsePoint(s);
  }
  public static Geometries.Geom GeomFromTextWithoutBracket(String s){
    String ss[] = s.split(" ");
    return new Point(Double.parseDouble(ss[0]),Double.parseDouble(ss[1]));
  }
  public static List<String> PolygonFromTextWithoutBracket(String s){
    String ss[] = s.split(",");
    return Arrays.asList(ss);
  }


  public static Boolean DWithin(Geometries.Geom p1, Geometries.Geom p2, double distance) {
    return Distance(p1, p2) <= distance;
  }

  public static Boolean DWithin(Geometries.Geom p1, Geometries.Geom p2, BigDecimal distance) {
    return Distance(p1, p2) <= distance.doubleValue();
  }

  public static Double getX(Geometries.Geom p) {
    return p.g() instanceof com.esri.core.geometry.Point ? ((com.esri.core.geometry.Point) p.g()).getX() : null;
  }

  public static Double getY(Geometries.Geom p) {
    return p.g() instanceof com.esri.core.geometry.Point ? ((com.esri.core.geometry.Point) p.g()).getY() : null;
  }

  public static Boolean KNN(Geometries.Geom p1, Geometries.Geom p2, BigDecimal k) {
    return false;
  }
}
