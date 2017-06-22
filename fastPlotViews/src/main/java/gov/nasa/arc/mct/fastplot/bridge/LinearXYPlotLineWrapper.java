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

import java.awt.BasicStroke;
import java.awt.Stroke;

import javax.swing.Icon;

import plotter.xy.LinearXYPlotLine;
import plotter.xy.XYAxis;
import plotter.xy.XYDimension;

/**
 * Wraps some behavior of the standard LinearXYPlotLine to ensure that repaint requests 
 * are large enough to encompass thickened lines and plot markers, without making changes to 
 * the code optimized for the common case of one pixel thick unadorned lines. 
 */
public class LinearXYPlotLineWrapper extends LinearXYPlotLine {
	private static final long serialVersionUID = -656948949864598815L;

	private int     xPadding = 0;
	private int     yPadding = 0;
	
	public LinearXYPlotLineWrapper(XYAxis xAxis, XYAxis yAxis,
			XYDimension independentDimension) {
		super(xAxis, yAxis, independentDimension);
	}
	

	@Override
	public void setStroke(Stroke stroke) {
		super.setStroke(stroke);
		calculatePadding();
	}
	
	@Override
	public void setPointIcon(Icon icon) {
		super.setPointIcon(icon);
		calculatePadding();
	}


	@Override
	public void repaint(long tm, int x, int y, int width, int height) {
		super.repaint(tm, x - xPadding, y - yPadding, width + xPadding * 2, height + yPadding * 2);
	}

	@Override
	public void repaint(int x, int y, int width, int height) {
		super.repaint(x - xPadding, y - yPadding, width + xPadding * 2, height + yPadding * 2);
	}

	private void calculatePadding() {
		int x = 0;
		int y = 0;
		
		Stroke s = getStroke();
		if (s != null && s instanceof BasicStroke) {
			x += ((BasicStroke) s).getLineWidth() / 2;
			y += ((BasicStroke) s).getLineWidth() / 2;
		}
		
		Icon i = getPointIcon();
		if (i != null) {
			x += i.getIconWidth()  / 2;
			y += i.getIconHeight() / 2;
		}
		
		xPadding = x;
		yPadding = y;
	}
}
