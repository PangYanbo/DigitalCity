package digitalcity.sim0;

import java.util.HashMap;
import java.util.Map;

public class Test {

    // @org.junit.Test
    public void testPearson() {
        Map<String, Double> mx = new HashMap<>();
        Map<String, Double> my = new HashMap<>();
        mx.put("52301412" , 1.0);
        mx.put("52301413" , 2.0);
        mx.put("52301414" , 3.0);
        mx.put("52301415" , 4.0);
        my.put("52301412" , 1.0);
        my.put("52301413" , 2.0);
        my.put("52301414" , 3.0);
        my.put("52301415" , 4.0);
        System.out.println(String.format("The Pearson Correlation Coefficient of two groups is: %f", Evaluation.calculatePearson(mx, my)));
        System.out.println(String.format("The likelihood of two groups is: %f", Evaluation.calcLikelihood(mx, my)));
    }
}
