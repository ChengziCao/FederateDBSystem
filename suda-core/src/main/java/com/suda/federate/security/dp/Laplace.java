package com.suda.federate.security.dp;

import org.apache.commons.math3.distribution.LaplaceDistribution;

public class Laplace {
  private final LaplaceDistribution ld;
  private final double sd;

  public Laplace(final double epsilon, final double sd) {
    this.ld = new LaplaceDistribution(0, 1 / epsilon);
    this.sd = sd;
  }

  public double sample() {
    double sample = this.ld.sample();//噪声
    while (Math.abs(sample) >= this.sd) {//噪声限制。噪声别太大了，太大查询结果就不准了
      sample = this.ld.sample();
    }
    return sample;
  }

  public double getSD() {
    return sd;
  }
}
