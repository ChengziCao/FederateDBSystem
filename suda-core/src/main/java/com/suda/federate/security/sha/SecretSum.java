package com.suda.federate.security.sha;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
public class SecretSum {

        /**
         *
         * @param x 参与多方计算的各方id
         * @param y 参与多方计算的各方混淆的和
         * @param x0 取0，还原真实的多方求和数值
         * @description  拉格朗日插值还原真实值
         */
        private static int lag(int[] x, int[] y, double x0){
            int m=x.length;
            double y0;
            double j=0;
            for(int ib=0;ib<m;ib++) {
                double k=1;
                for(int ic=0;ic<m;ic++) {
                    if(ib!=ic){
                        k=k*(x0-x[ic])/(x[ib]-x[ic]);
                    }
                }
                k=k*y[ib];
                j=j+k;
            }
            y0=j;
            return (int) y0;
        }

        /**
         *
         * @descricption 生成f(x) = d + a1*x + a2*x^2 + ... +a_(t-1)*x^(t-1)的系数 ai
         */
        public static int[] generateRandomNum(int min, int max, int t) {
            int[] arr = new int[t];
            for (int i=0; i<t-1; i++) {
                // nextInt is normally exclusive of the top value,
                // so add 1 to make it inclusive
                arr[i] = ThreadLocalRandom.current().nextInt(min, max + 1);
            }
            return arr;
        }

        /**
         *
         * @return [0, f(x1), f(x2), ..., f(xt)] 其中 f(x) = d + a1*x + a2*x^2 + ... +a_(t-1)*x^(t-1)
         */
        public static int[] localClient(int t, int localSum, int[] idList) {
            int[] arr = generateRandomNum(0, 1000, t-1);
            int[] fakeLocalSum = new int[t+1];
            fakeLocalSum[0] = 0;
            for(int x : idList) {
                fakeLocalSum[x] = localSum;
                for (int j = 1; j < t; j++) {
                    fakeLocalSum[x] += arr[j - 1] * (int) Math.pow(x, j);
                }
            }
            return fakeLocalSum;
        }

        /**
         *
         * @description 输入一个t*(t+1)矩阵，即：
         * [
         * [0, f1(x1), f1(x2), ..., f1(xt)],
         * [0, f2(x1), f2(x2), ..., f2(xt)],
         * ...,
         * [0, ft(x1), ft(x2), ..., ft(xt)]
         * ],
         * 计算S(x),其中: S(x1) = f1(x1) + f2(x1) + ... + ft(x1), ..., S(x_t) = f1(x_t) + f2(x_t) + ... + ft(x_t)
         *
         * @return S
         */
        public static int[] computeS(int[][] fakeLocalSumList) { //t*(t+1)
            int t = fakeLocalSumList.length;
            int[] S = new int[t+1];
            for(int i = 0; i <= t; i++) {
                S[i] = 0;
            }

            for (int[] ints : fakeLocalSumList) {
                for (int j = 1; j <= t; j++) {
                    S[j] += ints[j];
                }
            }
            return S;
        }



        public static void main(String[] args) {
            //各方id：id = i + 1
            int[] idLst = {1, 2, 3};
            //各方的本地和
            int[] localSumLst = {100, 150, 200};
            //阈值t，解密的至少人数
            int t = 3;


            int[][] fakeLocalSumList = new int[t][t+1];

            for(int id : idLst) {
                fakeLocalSumList[id-1] = localClient(t, localSumLst[id-1], idLst);
            }
            int[] tempS = computeS(fakeLocalSumList);
            int[] s = Arrays.copyOfRange(tempS, 1, t+1);
            int secureSum = lag(idLst, s, 0);
            System.out.println(secureSum);
        }
    }
