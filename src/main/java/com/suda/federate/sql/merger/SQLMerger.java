package com.suda.federate.sql.merger;

import com.suda.federate.sql.type.FD_Double;
import com.suda.federate.sql.type.FD_Int;
import com.suda.federate.sql.type.FD_Variable;

import java.util.List;

public class SQLMerger {


    public <T extends FD_Variable> T union(List<T> results) {
        return null;
    }

    /**
     * calc the summation of results
     *
     * @param results results of every data silos
     * @param clazz   result type
     * @return summation
     */
    public FD_Variable sum(List<FD_Variable> results, Class<?> clazz) {
        if (clazz == FD_Int.class) {
            FD_Int ans = new FD_Int("ans", 0);
            results.forEach(x -> {
                ans.value += ((FD_Int) x).value;
            });
            return ans;
        } else if (clazz == FD_Double.class) {
            FD_Double ans = new FD_Double("ans", 0.0);
            results.forEach(x -> {
                ans.value += ((FD_Double) x).value;
            });
            return ans;
        }
        return null;
    }

}
