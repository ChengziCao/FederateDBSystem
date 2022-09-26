package com.suda.federate.security.sha;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SecretSum {

    /**
     * @param x  参与多方计算的各方id
     * @param y  参与多方计算的各方混淆的和
     * @param x0 取0，还原真实的多方求和数值
     * @description 拉格朗日插值还原真实值
     */
    public static int lag(List<Integer> x, List<Integer> y, double x0) {
        int m = x.size();
        double y0;
        double j = 0;
        for (int ib = 0; ib < m; ib++) {
            double k = 1;
            for (int ic = 0; ic < m; ic++) {
                if (ib != ic) {
                    k = k * (x0 - x.get(ic)) / (x.get(ib) - x.get(ic));
                }
            }
            k = k * y.get(ib);
            j = j + k;
        }
        y0 = j;
        return (int) Math.round(y0);
    }

    /**
     * generate random array
     *
     * @param min min value (inclusive)
     * @param max max value (inclusive)
     * @param n   array size
     */
    public static int[] generateRandomNum(int min, int max, int n) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = ThreadLocalRandom.current().nextInt(min, max + 1);
        }
        return arr;
    }


    /**
     * f_{i}(x)=(\sum_{k=1}^{n-1} a_{i k} x^{k})+v_{i}
     *
     * @param t        silos
     * @param localSum vi the local counting result of silo F_i
     * @param publicKey   n different public parameters
     * @return [0, f(x1), f(x2), ..., f(xt)] 其中 f(x) = d + a1*x + a2*x^2 + ... +a_(t-1)*x^(t-1)
     */
    public static List<Integer> localEncrypt(int t, int localSum, List<Integer> publicKey) {
        int[] arr = generateRandomNum(0, 1000, t - 1);
        List<Integer> fakeLocalSum = new ArrayList<>(t);
        for (int x : publicKey) {
            int temp = localSum;
            for (int j = 1; j < t; j++) {
                temp += arr[j - 1] * (int) Math.pow(x, j);
            }
            fakeLocalSum.add(temp);
        }
        return fakeLocalSum;
    }

    /**
     * 输入一个t*(t+1)矩阵fakeLocalSumList 按列求和
     * <p>
     * [
     * [ f1(x1), f1(x2), ..., f1(xt)],
     * [ f2(x1), f2(x2), ..., f2(xt)],
     * ...,
     * [ ft(x1), ft(x2), ..., ft(xt)]
     * ],
     * 计算S(x)，其中:
     * S(x1) = f1(x1) + f2(x1) + ... + ft(x1),
     * ...,
     * S(x_t) = f1(x_t) + f2(x_t) + ... + ft(x_t)
     */
    public static List<Integer> computeS(List<List<Integer>> fakeLocalSumList) { //t*(t+1)
        int n = fakeLocalSumList.size();
        List<Integer> S = new ArrayList<>(Collections.nCopies(n, 0));
        for (List<Integer> ints : fakeLocalSumList) {
            for (int j = 0; j < n; j++) {
                S.set(j, S.get(j) + ints.get(j));
            }
        }
        return S;
    }

    public static void main(String[] args) {
        int n = 7;
        //各方的本地和
        int[] v = generateRandomNum(0, 100, n);
        int realSum = Arrays.stream(v).sum();
        //各方id
        List<Integer> idList = Arrays.asList(1, 2, 3, 4, 5, 6, 7);

        List<List<Integer>> encryptSumList = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            encryptSumList.add(localEncrypt(n, v[i], idList));
        }

        System.out.println(encryptSumList);
        List<Integer> S = computeS(encryptSumList);

        System.out.println(S);
        int secureSum = lag(idList, S, 0);
        System.out.println("realSum: " + realSum + " secureSum: " + secureSum);
        System.out.println(realSum == secureSum);
    }
}
