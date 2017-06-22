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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.JLabel;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestPlotMarkerIcon {
	private static final Color BACKGROUND = Color.ORANGE;
	
	private JLabel label = new JLabel();
	
	
	@Test
	public void testSimpleInstantiation() {
		for (int i = 10; i < 20; i+=2) { // Try circles of some different sizes
			Icon icon = new PlotMarkerIcon(makeCircle(i));
			
			// Give a couple of pixels of tolerance for size
			Assert.assertTrue(icon.getIconWidth()  >= 2*i  );
			Assert.assertTrue(icon.getIconHeight() <= 2*i+2);
			
			// Should be drawn centered
			Assert.assertTrue (isFilledEllipse (drawCenteredIcon(icon), label.getForeground()) );
		}
	}
	
	@Test
	public void testCenteredCircle() {
		for (int i = 10; i < 20; i+=2) { // Try circles of some different sizes
			Icon icon = new PlotMarkerIcon(makeCircle(i), true);
			
			// Give a couple of pixels of tolerance
			Assert.assertTrue(icon.getIconWidth()  >= 2*i  );
			Assert.assertTrue(icon.getIconHeight() <= 2*i+2);
			
			// Should be drawn centered
			Assert.assertTrue(isFilledEllipse (drawCenteredIcon(icon), label.getForeground()) );
		}
	}
	
	@Test
	public void testNonCenteredCircle() {
		for (int i = 10; i < 20; i+=2) { // Try circles of some different sizes
			Icon icon = new PlotMarkerIcon(makeCircle(i), false);
			
			// Give a couple of pixels of tolerance for size
			Assert.assertTrue(icon.getIconWidth()  >= 2*i  );
			Assert.assertTrue(icon.getIconHeight() <= 2*i+2);
			
			// Should be drawn centered
			Assert.assertFalse (isFilledEllipse ( drawCenteredIcon(icon), label.getForeground()) );
			Assert.assertTrue  (isFilledEllipse (drawUnalignedIcon(icon), label.getForeground()) );
		}
	}
	
	@Test
	public void testSizedCircle() {
		boolean[] truths = { false, true };
		for (boolean center : truths) {
			for (int i = 10; i < 20; i+=2) { // Try circles of some different sizes
				Icon icon = new PlotMarkerIcon(makeCircle(120), center, i, i);

				// Give a couple of pixels of tolerance for size
				Assert.assertTrue(icon.getIconWidth()  == i  );
				Assert.assertTrue(icon.getIconHeight() == i  );

				// Should be drawn centered
				Assert.assertEquals(isFilledEllipse ( drawCenteredIcon(icon), label.getForeground()),  center );
				Assert.assertEquals(isFilledEllipse (drawUnalignedIcon(icon), label.getForeground()), !center );
			}
		}
	}
	
	@Test
	public void testColoredCircle() {
		Color testColor = Color.YELLOW;
		boolean[] truths = { false, true };
		for (boolean center : truths) {
			for (int i = 10; i < 20; i+=2) { // Try circles of some different sizes
				Icon icon = new PlotMarkerIcon(makeCircle(120), testColor, center, i, i);

				// Give a couple of pixels of tolerance for size
				Assert.assertTrue(icon.getIconWidth()  == i  );
				Assert.assertTrue(icon.getIconHeight() == i  );

				// Should be drawn centered
				Assert.assertEquals(isFilledEllipse ( drawCenteredIcon(icon), testColor),  center );
				Assert.assertEquals(isFilledEllipse (drawUnalignedIcon(icon), testColor), !center );
			}
		}
	}

	private boolean isFilledEllipse(BufferedImage image, Color color) {
		int w = image.getWidth();
		int h = image.getHeight();
		int x = w / 2; // Expected origin
		int y = h / 2;
		int r = (x+y) / 2; // Estimate expected radius
		
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				double d = Math.sqrt( (u-x)*(u-x) + (v-y)*(v-y) );
				// Note: Permit a region of uncertainty +- 2 pixels for border/rounding
				if (d > r + 2) {
					if (image.getRGB(u, v) == color.getRGB()) return false;
				} else if (d < r - 2) {
					if (image.getRGB(u, v) != color.getRGB()) return false;
				}
			}
		}
		
		return true;
	}
	
	private BufferedImage drawCenteredIcon(Icon i) {
		BufferedImage image = new BufferedImage(i.getIconWidth(), i.getIconHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics g = image.createGraphics();
		g.setColor(BACKGROUND);
		g.fillRect(0, 0, i.getIconWidth(), i.getIconHeight());
		i.paintIcon(label, image.createGraphics(), i.getIconWidth()/2, i.getIconHeight()/2);		
		return image;
	}

	private BufferedImage drawUnalignedIcon(Icon i) {
		BufferedImage image = new BufferedImage(i.getIconWidth(), i.getIconHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics g = image.createGraphics();
		g.setColor(BACKGROUND);
		g.fillRect(0, 0, i.getIconWidth(), i.getIconHeight());
		i.paintIcon(label, image.createGraphics(), 0, 0);		
		return image;
	}
	
	private Shape makeCircle (double radius) {
		return new Ellipse2D.Double(-radius, -radius, radius*2, radius*2);
	}
	
}
