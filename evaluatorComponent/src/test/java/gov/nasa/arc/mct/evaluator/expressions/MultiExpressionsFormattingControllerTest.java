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
/*******************************************************************************
 * Mission Control Technologies is Copyright 2007-2012 NASA Ames Research Center
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use 
 * this file except in compliance with the License. See the MCT Open Source 
 * Licenses file distributed with this work for additional information regarding copyright 
 * ownership. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES 
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for 
 * the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package gov.nasa.arc.mct.evaluator.expressions;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.evaluator.expressions.MultiRuleExpression.SetLogic;

import java.util.ArrayList;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class MultiExpressionsFormattingControllerTest {
	private MultiRuleExpression e1, e2, e3;
	private MultiRuleExpressionList eList;
	private MultiExpressionsController controller;
	private ArrayList<AbstractComponent> tList;
	@Mock 
	private AbstractComponent t; 
	@Mock private MultiViewManifestation view; 
	
	@BeforeMethod
	public void setup(){
        MockitoAnnotations.initMocks(this);

		eList = new MultiRuleExpressionList("");
		e1 = new MultiRuleExpression(SetLogic.ALL_PARAMETERS, new String[] {"PUI1","PUI2"}, 
				"PUI1","=", "abc", "display", "rule1");
		e2 = new MultiRuleExpression(SetLogic.ALL_PARAMETERS, new String[] {"PUI1","PUI2"}, 
				"PUI1","=", "abc", "display", "rule2");
		e3 = new MultiRuleExpression(SetLogic.ALL_PARAMETERS, new String[] {"PUI1","PUI2"}, 
				"PUI1","=", "abc", "display", "rule3");
		tList = new ArrayList<AbstractComponent>();
		controller = new MultiExpressionsController(view);
	}
	
	@AfterMethod
	public void tearDown() {
		this.eList = new MultiRuleExpressionList("");
	}
	
	@Test
	public void notifyExpressionAddedTest(){
		controller.notifyExpressionAdded(e1, eList);
		Assert.assertEquals(eList.size(), 1);
		Assert.assertEquals(eList.getExp(0), e1);
	}
	
	@Test
	public void notifyExpressionDeletedTest(){
		eList.addExp(e1);
		eList.addExp(e2);
		controller.notifyExpressionDeleted(e2, eList);
		Assert.assertEquals(eList.size(), 1);
		Assert.assertEquals(eList.getExp(0), e1);
	}
	
	@Test
	public void notifyMovedUpOneTest(){
		eList.addExp(e1);
		eList.addExp(e2);
		controller.notifyMovedUpOne(e2, eList);
		Assert.assertEquals(eList.getExp(0), e2);
	}
	
	@Test
	public void notifyMovedDownOneTest(){
		eList.addExp(e1);
		eList.addExp(e2);
		controller.notifyMovedDownOne(e1, eList);
		Assert.assertEquals(eList.getExp(0), e2);
	}
	
}

