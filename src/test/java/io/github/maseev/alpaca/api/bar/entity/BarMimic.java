package io.github.maseev.alpaca.api.bar.entity;

import java.io.Serializable;

public class BarMimic implements Serializable {

  private long t;
  private double o;
  private double h;
  private double l;
  private double c;
  private long v;

  public BarMimic() {
  }

  public BarMimic(Bar bar) {
    t = bar.time().getEpochSecond();
    o = bar.openPrice().doubleValue();
    h = bar.highPrice().doubleValue();
    l = bar.lowPrice().doubleValue();
    c = bar.closePrice().doubleValue();
    v = bar.volume();
  }

  public long getT() {
    return t;
  }

  public void setT(long t) {
    this.t = t;
  }

  public double getO() {
    return o;
  }

  public void setO(double o) {
    this.o = o;
  }

  public double getH() {
    return h;
  }

  public void setH(double h) {
    this.h = h;
  }

  public double getL() {
    return l;
  }

  public void setL(double l) {
    this.l = l;
  }

  public double getC() {
    return c;
  }

  public void setC(double c) {
    this.c = c;
  }

  public long getV() {
    return v;
  }

  public void setV(long v) {
    this.v = v;
  }

  @Override
  public String toString() {
    return "BarMimic{" +
      "t=" + t +
      ", o=" + o +
      ", h=" + h +
      ", l=" + l +
      ", c=" + c +
      ", v=" + v +
      '}';
  }
}
