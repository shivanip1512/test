package ivvcvoltageprofilegraph;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cannontech.common.util.GraphIntervalRounding;
import com.cannontech.web.capcontrol.ivvc.service.impl.VoltageFlatnessGraphServiceImpl;
import com.google.common.collect.Lists;


public class VoltageFlatnessGraphTest {
    
    @Test
    public void test_calculateBigDecimalScale_1() {
        Double num = .0012345;
        BigDecimal rounded = GraphIntervalRounding.roundUp(num, 2); //.002
        int scale = rounded.scale();
        Assert.assertEquals(3, scale);
    }
    @Test
    public void test_calculateBigDecimalScale_2() {
        Double num = .0000012345;
        BigDecimal rounded = GraphIntervalRounding.roundUp(num, 2); //.000002
        int scale = rounded.scale();
        Assert.assertEquals(6, scale);
    }
    @Test
    public void test_calculateBigDecimalScale_3() {
        Double num = 112.12345;
        BigDecimal rounded = GraphIntervalRounding.roundUp(num, 2); //200
        int scale = rounded.scale();
        
        // including this test to display what value scale is in a value greater than 1
        // I don't use this -2, however DecimalFormat.setMaximumFractionDigits(scale)
        // does not accept negative values and any negative values passed into this method are
        // treated as zero (which is what I want)
        Assert.assertEquals(-2, scale);
    }
    
    @Test
    public void test_calculateBucketSize_1() {
        Double num = 0.0;
        BigDecimal rounded = GraphIntervalRounding.roundUp(num, 5);
        Double result = rounded.doubleValue();
        Double expected = 0.0;
        Assert.assertEquals(expected, result);
    }
    @Test
    public void test_calculateBucketSize_2() {
        Double num = 0.0;
        BigDecimal rounded = GraphIntervalRounding.roundUp(num, 20);
        Double result = rounded.doubleValue();
        Double expected = 0.0;
        Assert.assertEquals(expected, result);
    }
    @Test
    public void test_calculateBucketSize_3() {
        Double num = .12345678901234;
        BigDecimal rounded = GraphIntervalRounding.roundUp(num, 2);
        Double result = rounded.doubleValue();
        Double expected = .2;
        Assert.assertEquals(expected, result);
    }
    @Test
    public void test_calculateBucketSize_4() {
        Double num = .12345678901234;
        BigDecimal rounded = GraphIntervalRounding.roundUp(num, 2.5);
        Double result = rounded.doubleValue();
        Double expected = .25;
        Assert.assertEquals(expected, result);
    }
    @Test
    public void test_calculateBucketSize_5() {
        Double num = .12345678901234;
        BigDecimal rounded = GraphIntervalRounding.roundUp(num, 5);
        Double result = rounded.doubleValue();
        Double expected = .5;
        Assert.assertEquals(expected, result);
    }
    @Test
    public void test_calculateBucketSize_6() {
        Double num = .12345678901234;
        BigDecimal rounded = GraphIntervalRounding.roundUp(num, 10);
        Double result = rounded.doubleValue();
        Double expected = 1.0;
        Assert.assertEquals(expected, result);
    }
    @Test
    public void test_calculateBucketSize_7() {
        Double num = .12345678901234;
        BigDecimal rounded = GraphIntervalRounding.roundUp(num, 20);
        Double result = rounded.doubleValue();
        Double expected = 2.0;
        Assert.assertEquals(expected, result);
    }

    @Test
    public void test_calculateBucketSizeNegativeNum_1() {
        Double num = -1.5;
        BigDecimal rounded = GraphIntervalRounding.roundUp(num, 2);
        Double result = rounded.doubleValue();
        Double expected = 0.0;
        Assert.assertEquals(expected, result);
    }
    @Test
    public void test_calculateBucketSizeNegativeNum_2() {
        Double num = -111.5;
        BigDecimal rounded = GraphIntervalRounding.roundUp(num, 2);
        Double result = rounded.doubleValue();
        Double expected = 0.0;
        Assert.assertEquals(expected, result);
    }

    @Test
    public void test_calculateAllBuckets_0() {
        Double num = 0.0;
        BigDecimal bucketSize = GraphIntervalRounding.roundUp(num, 2);
        List<Double> bucketValues = getBucketList(bucketSize, 1.8);
        List<Double> expectedList = Lists.newArrayList(0.0);
        Assert.assertEquals(expectedList, bucketValues);
    }
    @Test
    public void test_calculateAllBuckets_1() {
        Double num = .12345678901234;
        BigDecimal bucketSize = GraphIntervalRounding.roundUp(num, 2);
        List<Double> bucketValues = getBucketList(bucketSize, 1.8);
        List<Double> expectedList = Lists.newArrayList(0.0, 0.2, 0.4, 0.6, 0.8, 1.0, 1.2, 1.4, 1.6, 1.8);
        Assert.assertEquals(expectedList, bucketValues);
    }
    @Test
    public void test_calculateAllBuckets_2() {
        Double num = 5.12345678901234;
        BigDecimal bucketSize = GraphIntervalRounding.roundUp(num, 2);
        List<Double> bucketValues = getBucketList(bucketSize, 54.0);
        List<Double> expectedList = Lists.newArrayList(0.0, 6.0, 12.0, 18.0, 24.0, 30.0, 36.0, 42.0, 48.0, 54.0);
        Assert.assertEquals(expectedList, bucketValues);
    }
    @Test
    public void test_calculateAllBuckets_3() {
        Double num = .12345678901234;
        BigDecimal bucketSize = GraphIntervalRounding.roundUp(num, 2.5);
        List<Double> bucketValues = getBucketList(bucketSize, 2.25);
        List<Double> expectedList = Lists.newArrayList(0.0, 0.25, 0.5, 0.75, 1.0, 1.25, 1.5, 1.75, 2.0, 2.25);
        Assert.assertEquals(expectedList, bucketValues);
    }
    @Test
    public void test_calculateAllBuckets_4() {
        Double num = 5.12345678901234;
        BigDecimal bucketSize = GraphIntervalRounding.roundUp(num, 2.5);
        List<Double> bucketValues = getBucketList(bucketSize, 67.5);
        List<Double> expectedList = Lists.newArrayList(0.0, 7.5, 15.0, 22.5, 30.0, 37.5, 45.0, 52.5, 60.0, 67.5);
        Assert.assertEquals(expectedList, bucketValues);
    }
    @Test
    public void test_calculateAllBuckets_5() {
        Double num = .12345678901234;
        BigDecimal bucketSize = GraphIntervalRounding.roundUp(num, 5);
        List<Double> bucketValues = getBucketList(bucketSize, 4.5);
        List<Double> expectedList = Lists.newArrayList(0.0, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5);
        Assert.assertEquals(expectedList, bucketValues);
    }
    @Test
    public void test_calculateAllBuckets_6() {
        Double num = 5.12345678901234;
        BigDecimal bucketSize = GraphIntervalRounding.roundUp(num, 5);
        List<Double> bucketValues = getBucketList(bucketSize, 90.0);
        List<Double> expectedList = Lists.newArrayList(0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0);
        Assert.assertEquals(expectedList, bucketValues);
    }
    
    private List<Double> getBucketList(BigDecimal bucketSize, double bucketEndValue) {
        List<Double> result = VoltageFlatnessGraphServiceImpl.getBucketValues(bucketSize, bucketEndValue);
        return result;
    }
    
    /**
     * This doesn't work. Produces a list like this:
     * [0.0, 0.2, 0.4, 0.6000000000000001, 0.8, 1.0, 1.2000000000000002, 1.4000000000000001, 1.6, 1.8]
     * @param bucketSize
     * @return
     */
    private List<Double> getBucketListViaBigDecimalScaling(BigDecimal bucketSize, double bucketEndValue) {
        List<Double> bucketValues = Lists.newArrayList();
        double bucketSizeAsDouble = bucketSize.doubleValue();
        Integer iterationNum = 0;
        Double currentVal = 0.0;
        int precision = bucketSize.scale();
        
        while(currentVal <= bucketEndValue) {
            bucketValues.add(currentVal);
            iterationNum++;
            currentVal = iterationNum * bucketSizeAsDouble;
            BigDecimal bd = new BigDecimal(currentVal);
            bd.setScale(precision, RoundingMode.DOWN);
            currentVal = bd.doubleValue();
        }
        return bucketValues;
    }
}
