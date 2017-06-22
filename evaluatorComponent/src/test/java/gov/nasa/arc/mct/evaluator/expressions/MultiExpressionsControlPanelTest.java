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
package gov.nasa.arc.mct.evaluator.expressions;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.evaluator.component.MultiComponent;
import gov.nasa.arc.mct.evaluator.component.MultiData;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

import javax.swing.JFrame;

import org.fest.swing.core.BasicRobot;
import org.fest.swing.core.Robot;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.fixture.FrameFixture;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class MultiExpressionsControlPanelTest {
	private Robot robot;
	@Mock 
	private MultiViewManifestation mockExpManifestation;
	@Mock 
	private MultiComponent mc;
	@Mock 
	private MultiData multiData;
	private ArrayList<AbstractComponent> tList;
	@Mock
	private AbstractComponent ac;
	@Mock
	private MultiRuleExpressionList ruleList;
	@Mock 
	private MultiRuleExpression expression;
	private MultiExpressionsFormattingControlsPanel controlPanel;
	private static final String TITLE = "Test Frame";
	private static final ResourceBundle bundle = ResourceBundle.getBundle("MultiComponent");

	@BeforeMethod
	public void setUp(){
		MockitoAnnotations.initMocks(this);
		
		robot = BasicRobot.robotWithCurrentAwtHierarchy();
		tList = new ArrayList<AbstractComponent>();
		tList.add(ac);
		
		FeedProvider mockfp = Mockito.mock(FeedProvider.class);
		Mockito.when(mockExpManifestation.getExpressions()).thenReturn(ruleList);
		Mockito.when(ruleList.size()).thenReturn(2);
		Mockito.when(mockExpManifestation.getMulti()).thenReturn(mc);
		Mockito.when(mc.getData()).thenReturn(multiData);
		Mockito.when(ac.getExternalKey()).thenReturn("isp:PARAMETER1");
		Mockito.when(ac.getCapability(FeedProvider.class)).thenReturn(mockfp);
		Mockito.when(mockfp.getSubscriptionId()).thenReturn("isp:PARAMETER1");
		Mockito.when(multiData.isPassThrough()).thenReturn(false);
		Mockito.when(mc.getComponents()).thenReturn(Collections.<AbstractComponent>emptyList());
		
		Mockito.when(mockExpManifestation.getSelectedTelemetry()).thenReturn(ac);
		Mockito.when(mockExpManifestation.getTelemetry()).thenReturn(tList);
		
		GuiActionRunner.execute(new GuiTask(){

			@Override
			protected void executeInEDT() throws Throwable {
				controlPanel = new MultiExpressionsFormattingControlsPanel(mockExpManifestation);
				JFrame frame = new JFrame(TITLE);
				frame.setPreferredSize(new Dimension(950,600));
				frame.setName(TITLE);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				controlPanel.setOpaque(true);
				frame.setContentPane(controlPanel);
				frame.pack();
				frame.setVisible(true);
			}
		});
	}
	
	@AfterMethod
	public void tearDown() {
		robot.cleanUp();
	}
	
	@Test
	public void testPersistenceIsCalledDuringActions() {
		int persistentCount = 0;
		FrameFixture window = WindowFinder.findFrame(TITLE).using(robot);
		window.button(bundle.getString("AddExpressionTitle")).click();
		Mockito.verify(mockExpManifestation, Mockito.times(++persistentCount)).fireFocusPersist();
		window.textBox(bundle.getString("ValueControlName")).setText("1.0");
		window.textBox(bundle.getString("DisplayControlName")).setText("test display");
		Mockito.when(mockExpManifestation.getSelectedExpression()).thenReturn(expression);
		Mockito.when(expression.getPUIs()).thenReturn("isp:PARAMETER1");
		window.button(bundle.getString("ApplyTitle")).click();
		Mockito.verify(mockExpManifestation, Mockito.times(++persistentCount)).fireFocusPersist();
		
		window.textBox(bundle.getString("ValueControlName")).setText("test value2");
		window.textBox(bundle.getString("DisplayControlName")).setText("test display2");
		window.button(bundle.getString("ApplyTitle")).click();
		Mockito.verify(mockExpManifestation, Mockito.times(++persistentCount)).fireFocusPersist();
		window.button(bundle.getString("AddExpressionTitle")).click();
		Mockito.verify(mockExpManifestation, Mockito.times(++persistentCount)).fireFocusPersist();
		window.textBox(bundle.getString("ValueControlName")).setText("1.0");
		window.textBox(bundle.getString("DisplayControlName")).setText("test display");
		Mockito.when(mockExpManifestation.getSelectedExpression()).thenReturn(expression);
		Mockito.when(expression.getPUIs()).thenReturn("isp:PARAMETER1");
		window.button(bundle.getString("ApplyTitle")).click();
		Mockito.verify(mockExpManifestation, Mockito.times(++persistentCount)).fireFocusPersist();
		window.button(bundle.getString("MoveUpOneTitle")).click();
		Mockito.verify(mockExpManifestation, Mockito.times(++persistentCount)).fireFocusPersist();

		window.button(bundle.getString("MoveDownOneTitle")).click();
		Mockito.verify(mockExpManifestation, Mockito.times(++persistentCount)).fireFocusPersist();

		window.button(bundle.getString("DeleteExpressionTitle")).click();
		Mockito.verify(mockExpManifestation, Mockito.times(++persistentCount)).fireFocusPersist();
		
		window.checkBox(bundle.getString("PassThroughControl")).click();
		Mockito.verify(mockExpManifestation, Mockito.times(++persistentCount)).fireFocusPersist();
		
	}
}
