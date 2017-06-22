/*******************************************************************************
 * Mission Control Technologies, Copyright (c) 2009-2012, United States Government
 * as represented by the Administrator of the National Aeronautics and Space 
 * Administration. All rights reserved.
 *
 * The MCT platform is licensed under the Apache License, Version 2.0 (the 
 * "License"); you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations under 
 * the License.
 *
 * MCT includes source code licensed under additional open source licenses. See 
 * the MCT Open Source Licenses file included with this distribution or the About 
 * MCT Licenses dialog available at runtime from the MCT Help menu for additional 
 * information. 
 *******************************************************************************/
package gov.nasa.arc.mct.fastplot.bridge;

import java.text.NumberFormat;

/**For calculating a simple linear regression.
 * Adapted from http://www-stat.stanford.edu/~naras/java/course/lec2/
 * Balasubramanian Narasimhan, Stanford
 * */
public class LinearRegression {
	
	/**An example.*/
	public static void main(String[] args) {
		double[] x = {95, 85, 80, 70, 60};
		double[] y = {85, 95, 70, 65, 70};
		LinearRegression lr = new LinearRegression(x, y);
		System.out.println(lr.getRoundedModel());
//		System.out.println("calculate y given an x of 38 "+lr.calculateY(38));
//		System.out.println("calculate x given a y of 41 "+lr.calculateX(41));
	}
	
	//fields
	private double[] x;
	private double[] y;
	private double meanX;
	private double meanY;
	private double slope;
	private double intercept;
	private double stndDevX;
	private double stndDevY;
	
	//constructor
	public LinearRegression(double[] x, double[] y) {
		this.x = x;
		this.y = y;
		compute();
	}
	
	//methods
	/**Performs linear regression*/
	private void compute() {
		double n = x.length;
		double sumy = 0.0,
		sumx = 0.0,
		sumx2 = 0.0,
		sumy2 = 0.0,
		sumxy = 0.0;
		for (int i = 0; i < n; i++) {
			sumx += x[i];
			sumx2 += x[i] * x[i];
			sumy += y[i];
			sumy2 += y[i] * y[i];
			sumxy += x[i] * y[i];
		}
		meanX = sumx / n;
		meanY = sumy / n;
		slope = (sumxy - sumx * meanY) / (sumx2 - sumx * meanX);
		intercept = meanY - slope * meanX;
		stndDevX = Math.sqrt((sumx2 - sumx * meanX) / (n - 1));
		stndDevY = Math.sqrt((sumy2 - sumy * meanY) / (n - 1));
	}
		
	/**Return approximated Y value, good for a single interpolation, multiple calls are inefficient!*/
	public static double interpolateY(double x1, double y1, double x2, double y2, double fixedX ){
		double[] x = {x1, x2};
		double[] y = {y1, y2};
		LinearRegression lr = new LinearRegression(x,y);
		return lr.calculateY(fixedX);
	}
	
	/**Return approximated X value, good for a single interpolation, multiple calls are inefficient!*/
	public static double interpolateX(double x1, double y1, double x2, double y2, double fixedY ){
		double[] x = {x1, x2};
		double[] y = {y1, y2};
		LinearRegression lr = new LinearRegression(x,y);
		return lr.calculateX(fixedY);
	}
	
	//getters
	public double getSlope() {
		return slope;
	}
	public double getIntercept() {
		return intercept;
	}
	public double getRSquared() {
		double r = slope * stndDevX / stndDevY;
		return r * r;
	}
	public double[] getX() {
		return x;
	}
	/**Returns Y=mX+b with full precision, no rounding of numbers.*/
	public String getModel(){
		return "Y= "+slope+"X + "+intercept+" RSqrd="+getRSquared();
	}
	/**Returns Y=mX+b */
	public String getRoundedModel(){
		return "Y= "+formatNumber(slope,3)+"X + "+formatNumber(intercept,3)+" RSqrd="+ formatNumber(getRSquared(),3);
	}
	/**Calculate Y given X.*/
	public double calculateY (double x){
		return slope*x+intercept;
	}
	/**Calculate X given Y.*/
	public double calculateX (double y){
		return (y-intercept)/slope;
	}
	/**Nulls the x and y arrays.  Good to call before saving.*/
	public void nullArrays(){
		x = null;
		y = null;
	}
	
	/**Converts a double ddd.dddddddd to a user determined number of decimal places right of the .  */
	public static String formatNumber(double num, int numberOfDecimalPlaces){
		NumberFormat f = NumberFormat.getNumberInstance();
		f.setMaximumFractionDigits(numberOfDecimalPlaces);
		f.setMinimumFractionDigits(numberOfDecimalPlaces);
		return f.format(num);
	}
} 


