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
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.Icon;

/**
 * An icon which presents an outlined shape, which may be colored to match the 
 * foreground of whatever component it is displayed upon. Intended for use both 
 * on plot lines and with plot legends.
 */
public class PlotMarkerIcon implements Icon {
	private static final Color TRANSPARENT = new Color(0,0,0,0);
	
	private BufferedImage image;
	private Shape         shape;
	private float         xOffset;
	private float         yOffset;
	private Color         drawnColor;
	private boolean       center;
	private boolean       deriveColor = false;

	/**
	 * Create an icon using the specified shape. The shape's specific dimensions 
	 * will be used, and paintIcon requests will be presumed to refer to the 
	 * shape's origin (which may not be the top-left corner, as generally expected 
	 * of icons.) This is to facilitate drawing on plot lines.
	 * @param shape the shape to be displayed in this icon 
	 */
	public PlotMarkerIcon(Shape shape) {
		this(shape, true);
	}

	/**
	 * Create an icon using the specified shape. The shape's specific dimensions 
	 * will be used. If the "center" argument is true, paintIcon requests will be presumed 
	 * to refer to the shape's origin; otherwise, this will be used as the top-left corner 
	 * of the drawn shape.
	 * @param shape the shape to be displayed in this icon
	 * @param center whether or not paint requests should be centered on the shape's origin 
	 */
	public PlotMarkerIcon(Shape shape, boolean center) {
		this(shape, center, shape.getBounds().width+1, shape.getBounds().height+1);
	}
	
	/**
	 * Create an icon using the specified shape, constrained to the specified width and 
	 * height by scaling down if necessary. If the "center" argument is true, paintIcon requests 
	 * will be presumed to refer to the shape's origin; otherwise, this will be used as the 
	 * top-left corner of the drawn shape.
	 * @param shape the shape to be displayed in this icon
	 * @param center whether or not paint requests should be centered on the shape's origin
	 * @param width the desired width of the icon
	 * @param height the desired height of the icon 
	 */
	public PlotMarkerIcon(Shape shape, boolean center, int width, int height) {
		this(shape, Color.GRAY, center, width, height);
		deriveColor = true;
	}

	/**
	 * Create an icon using the specified shape. The shape's specific dimensions 
	 * will be used. If the "center" argument is true, paintIcon requests will be presumed 
	 * to refer to the shape's origin; otherwise, this will be used as the top-left corner 
	 * of the drawn shape. The marker will additionally be drawn with the specified color, 
	 * instead of adapting to the foreground color of its component.
	 * @param shape the shape to be displayed in this icon
	 * @param color the color to use to draw this icon
	 * @param center whether or not paint requests should be centered on the shape's origin
	 */
	public PlotMarkerIcon(Shape shape, Color color, boolean center) {
		this(shape, color, center, shape.getBounds().width+1, shape.getBounds().height+1);
	}
	
	
	/**
	 * Create an icon using the specified shape, constrained to the specified width and 
	 * height by scaling down if necessary. If the "center" argument is true, paintIcon requests 
	 * will be presumed to refer to the shape's origin; otherwise, this will be used as the 
	 * top-left corner of the drawn shape. The marker will additionally be drawn with the specified color, 
	 * instead of adapting to the foreground color of its component.
	 * @param shape the shape to be displayed in this icon
	 * @param color the color to use to draw this icon
	 * @param center whether or not paint requests should be centered on the shape's origin
	 * @param width the desired width of the icon
	 * @param height the desired height of the icon 
	 */	
	public PlotMarkerIcon(Shape shape, Color color, boolean center, int width, int height) {
		this.shape  = shape;
		this.center = center;
		
		Rectangle r = shape.getBounds();		
		if (r.width > width || r.height > height) {
			float scale = Math.min((float) (width  - 1) / (float) (r.width ), 
					               (float) (height - 1) / (float) (r.height));
			this.shape = AffineTransform.getScaleInstance(scale, scale)
			                            .createTransformedShape(shape);
			r = this.shape.getBounds();
		}
		
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		xOffset = r.x;
		yOffset = r.y;
		render(color);
	}
	
	private void render(Color c) {
		drawnColor = c;
		Graphics2D g = image.createGraphics();
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				image.setRGB(x, y, TRANSPARENT.getRGB());
			}
		}
		// We render rarely (mostly we just reuse old images) so we can afford to antialias
		Object oldHint = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(c);
		g.translate(-xOffset, -yOffset);//image.getWidth()/2, image.getHeight()/2);
		g.fill(shape);
		g.setColor(c.darker().darker());
		g.draw(shape);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldHint);
	}
	
	@Override
	public int getIconHeight() {
		return image.getHeight();
	}

	@Override
	public int getIconWidth() {
		return image.getWidth();
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		if (deriveColor) {
			Color fg = c.getForeground();
			if (fg.getRGB() != drawnColor.getRGB()) { // Presumably this doesn't happen often
				render(fg);
			}
		}
		if (center) {
			g.drawImage(image, x + (int) xOffset, y + (int) yOffset, c);
		} else {
			g.drawImage(image, x, y, c);
		}
	}
	

}
