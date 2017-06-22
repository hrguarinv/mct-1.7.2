package gov.nasa.arc.mct.fastplot.bridge;

import gov.nasa.arc.mct.fastplot.bridge.HighPrecisionLinearRegression;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestLinearRegression {

	@DataProvider(name = "regressionValues")
	public Object[][] regressionValues() {
		
		return new Object[][] {
				// x Values
			new Object[] { 
					new Double[]{1.47, 1.50, 1.52, 1.55, 1.57, 1.60, 
					1.63, 1.65, 1.68, 1.70, 1.73, 1.75, 1.78, 1.80, 1.83}, 
				// y Values
					new Double[]{52.21, 53.12, 54.48, 55.84, 57.20, 
					58.57, 59.93, 61.29, 63.11, 64.47, 66.28, 68.10, 69.92, 72.19, 74.46 }
			}
					
		};
	}
	
	@SuppressWarnings("unchecked")
	@Test(dataProvider="regressionValues")
	public void test(final Double[] xValues, final Double[] yValues) {
		double[] x = new double[xValues.length];
		double[] y = new double[yValues.length];
		for (int i = 0 ; i < xValues.length; i++) {
			x[i] = xValues[i].doubleValue();
			y[i] = yValues[i].doubleValue();
					
		}
		HighPrecisionLinearRegression lr= new HighPrecisionLinearRegression(x, y);
		Assert.assertEquals(lr.getSlope(), new Double("61.27218654211064078236753758575390454060").doubleValue());
		Assert.assertEquals(lr.getIntercept().doubleValue(), new Double("-39.06195591884396438476134870821777842837082406218070354692745584586191796818020").doubleValue());
//		System.out.println(lr.getModel());
	}
}
