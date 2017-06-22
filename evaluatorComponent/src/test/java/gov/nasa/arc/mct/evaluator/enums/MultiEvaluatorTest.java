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
package gov.nasa.arc.mct.evaluator.enums;

import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.evaluator.api.Executor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author dcberrio
 *
 */
public class MultiEvaluatorTest {
	private MultiEvaluator evaluator;
	private List<FeedProvider> providers;
	private static final String INDICATOR_KEY = "indicator.key";

	
	@DataProvider(name = "expressions")
	public Object[][] expressionTests() {
		final String param1 = "isp:PARAMETER1";
		final String param2 = "isp:PARAMETER2";
		final String param3 = "isp:PARAMETER3";
		final String fullWithAllOnAndAllOff = 
				"ALL_PARAMETERS	isp:PARAMETER1;isp:PARAMETER2;isp:PARAMETER3;	isp:PARAMETER1	>	0.0	Invalid	TestR1|" + 
				"MORE_THAN_ONE_PARAMETER	isp:PARAMETER1;isp:PARAMETER2;isp:PARAMETER3;	isp:PARAMETER1	>	0.0	Invalid	TestR2|" +
				"SINGLE_PARAMETER	isp:PARAMETER1;isp:PARAMETER2;isp:PARAMETER3;	isp:PARAMETER1	>	0.0	ON	TestR3|" +
				"SINGLE_PARAMETER	isp:PARAMETER1;isp:PARAMETER2;isp:PARAMETER3;	isp:PARAMETER2	>	0.0	OFF	TestR4|" +
				"ALL_PARAMETERS	isp:PARAMETER1;isp:PARAMETER2;isp:PARAMETER3;	isp:PARAMETER1	=	0.0	error	TestR5|";
				//"PARAMETER1=ON|PARAMETER2=OFF|PARAMETER3=AUTO|ALLON=Invalid|ALLOFF=error";
		final String fullWithoutAllOnAndAllOff = "PARAMETER1=ON|PARAMETER2=OFF|PARAMETER3=AUTO";
		final String fullWithoutAllOnAndAllOffWithFormat = "PARAMETER1=%5.3f|PARAMETER2=OFF|PARAMETER3=AUTO|ALLON=Invalid|ALLOFF=error";

		
		return new Object[][] {
//			new Object[] {fullWithAllOnAndAllOff, Collections.emptyMap(), "", "", true},
//							// expression,      feedValues, expected output
			new Object[] {fullWithAllOnAndAllOff, 
					createStringValues(param1, "2", true, 25, "a",
									   param2, "0", true, 25, "a",
									   param3, "0", true, 25, "a"), "ON", "a", true},
			// verify that valid priority is ordered
		    new Object[] {fullWithAllOnAndAllOff, 
					createStringValues(param1, "2", true, 25, "a",
									   param2, "2", true, 24, "x",
									   param3, "2", true, 23, "d"), "Invalid", "d", true},
			// verify that valid priority all zeros is ordered
			 new Object[] {fullWithAllOnAndAllOff, 
						createStringValues(param1, "0", true, 25, "a",
										   param2, "0", true, 24, "x",
										   param3, "0", true, 23, "d"), "error", "d", true},
		    // verify that invalid priority is ordered
		    new Object[] {fullWithAllOnAndAllOff, 
					createStringValues(param1, "2", true, 25, "a",
									   param2, "2", false, 24, "x",
									   param3, "2", true, 23, "d"), "Invalid", "x", false},
			new Object[] {fullWithAllOnAndAllOff, 
					createStringValues(param1, "0", true, 25, "a",
									   param2, "2", true, 25, "a",
									   param3, "0", true, 25, "a"), "OFF", "a", true},
			new Object[] {fullWithAllOnAndAllOff, 
					createStringValues(param1, "0", true, 25, "a",
									   param2, "2", true, 25, "a",
									   param3, "2", true, 25, "a"), "Invalid", "a", true},
			new Object[] {fullWithAllOnAndAllOff, 
					createStringValues(param1, "0", true, 25, "a",
									   param2, "0", true, 25, "a",
									   param3, "0", true, 25, "a"), "error", "a", true},
		};
	}
	@DataProvider(name = "expressions2")
	public Object[][] expressionTests2() {
		final String param1 = "isp:PARAMETER1";
		final String param2 = "isp:PARAMETER2";
		final String param3 = "isp:PARAMETER3";
		final String fullWithAllOnAndAllOff = 
				"ALL_PARAMETERS	isp:PARAMETER1;isp:PARAMETER2;isp:PARAMETER3;	isp:PARAMETER1	>	0.0	Invalid	TestR1|" + 
				"MORE_THAN_ONE_PARAMETER	isp:PARAMETER1;isp:PARAMETER2;isp:PARAMETER3;	isp:PARAMETER1	>	0.0	Invalid	TestR2|" +
				"SINGLE_PARAMETER	isp:PARAMETER1;isp:PARAMETER2;isp:PARAMETER3;	isp:PARAMETER1	>	0.0	ON	TestR3|" +
				"SINGLE_PARAMETER	isp:PARAMETER1;isp:PARAMETER2;isp:PARAMETER3;	isp:PARAMETER2	>	0.0	OFF	TestR4|" +
				"ALL_PARAMETERS	isp:PARAMETER1;isp:PARAMETER2;isp:PARAMETER3;	isp:PARAMETER1	=	0.0	error	TestR5|";
				//"PARAMETER1=ON|PARAMETER2=OFF|PARAMETER3=AUTO|ALLON=Invalid|ALLOFF=error";
		final String fullWithoutAllOnAndAllOff = "PARAMETER1=ON|PARAMETER2=OFF|PARAMETER3=AUTO";
		final String fullWithoutAllOnAndAllOffWithFormat = "PARAMETER1=%5.3f|PARAMETER2=OFF|PARAMETER3=AUTO|ALLON=Invalid|ALLOFF=error";

		
		return new Object[][] {
		    new Object[] {fullWithAllOnAndAllOff, 
					createStringValues(param1, "-1", true, 25, "a",
									   param2, "-1", true, 25, "a",
									   param3, "-1", true, 25, "a"), null, "a", true},
		};
	}
	
	private Map<String, List<Map<String,String>>> createStringValues(
			String feedId, String value, boolean valid, int priority, String status,
			String feedId2, String value2, boolean valid2, int priority2, String status2,
			String feedId3, String value3, boolean valid3, int priority3, String status3) {
		
		Map<String, List<Map<String,String>>> cycleData = new HashMap<String, List<Map<String,String>>>();
		
		if (feedId != null) {
			cycleData.put(feedId, Collections.singletonList(createMap(value, valid, priority, status)));
		}
		if (feedId2 != null) {
			cycleData.put(feedId2, Collections.singletonList(createMap(value2, valid2, priority2, status2)));
		}
		if (feedId3 != null) {
			cycleData.put(feedId3, Collections.singletonList(createMap(value3, valid3, priority3, status3)));
		}
		
		return cycleData;
	}
	
	private Map<String, String> createMap(String value, boolean valid, int statusClass, String statusText) {
		Map<String,String> mapping = new HashMap<String,String>();
		mapping.put(FeedProvider.NORMALIZED_VALUE_KEY, value);
		mapping.put(FeedProvider.NORMALIZED_IS_VALID_KEY, Boolean.toString(valid));
		mapping.put(FeedProvider.NORMALIZED_TELEMETRY_STATUS_CLASS_KEY, Integer.toString(statusClass));
		mapping.put(INDICATOR_KEY, statusText);
		return mapping;
	}
	
	@BeforeMethod
	public void setup() {
		MockitoAnnotations.initMocks(this);
		evaluator = new MultiEvaluator();
		providers = new ArrayList<FeedProvider>();
	}
	
	@Test
	public void checkLanguage() {
		Assert.assertEquals(evaluator.getLanguage(), MultiEvaluator.LANGUAGE_STRING);
	}
	
	@SuppressWarnings("unchecked")
	@Test(dataProvider="expressions")
	public void test(final String expression, Map<String, List<Map<String,String>>> data, final String expectedValue, final String expectedStatus, final boolean isValid) {
		String[] ids = new String[] {"PARAMETER1","PARAMETER2","PARAMETER3"};
		for (String id:ids) {
			FeedProvider provider = Mockito.mock(FeedProvider.class);
			Mockito.when(provider.getSubscriptionId()).thenReturn("isp:"+id);
			Mockito.when(provider.getRenderingInfo(Mockito.anyMap())).thenAnswer(new Answer<FeedProvider.RenderingInfo>() {
				public FeedProvider.RenderingInfo answer(InvocationOnMock invocation) {
					Object[] args = invocation.getArguments();
					Map<String,String> dataValues = Map.class.cast(args[0]);
					String statusText = dataValues.get(INDICATOR_KEY);
					boolean isValid = Boolean.parseBoolean(dataValues.get(FeedProvider.NORMALIZED_IS_VALID_KEY));
					FeedProvider.RenderingInfo ri = new FeedProvider.RenderingInfo(dataValues.get(FeedProvider.NORMALIZED_VALUE_KEY), Color.GREEN, statusText, Color.red, isValid);
					return ri;
				}
			});

			providers.add(provider);
		}
		Executor executor = evaluator.compile(expression);
		FeedProvider.RenderingInfo ri = executor.evaluate(data, providers);
		Assert.assertEquals(ri.getValueText(), expectedValue);
//		Assert.assertEquals(ri.getStatusText(), expectedStatus);
//		Assert.assertEquals(ri.isValid(), isValid);
	}
	
	@SuppressWarnings("unchecked")
	@Test(dataProvider="expressions2")
	public void test2(final String expression, Map<String, List<Map<String,String>>> data, final String expectedValue, final String expectedStatus, final boolean isValid) {
		String[] ids = new String[] {"PARAMETER1","PARAMETER2","PARAMETER3"};
		for (String id:ids) {
			FeedProvider provider = Mockito.mock(FeedProvider.class);
			Mockito.when(provider.getSubscriptionId()).thenReturn("isp:"+id);
			Mockito.when(provider.getRenderingInfo(Mockito.anyMap())).thenAnswer(new Answer<FeedProvider.RenderingInfo>() {
				public FeedProvider.RenderingInfo answer(InvocationOnMock invocation) {
					Object[] args = invocation.getArguments();
					Map<String,String> dataValues = Map.class.cast(args[0]);
					String statusText = dataValues.get(INDICATOR_KEY);
					boolean isValid = Boolean.parseBoolean(dataValues.get(FeedProvider.NORMALIZED_IS_VALID_KEY));
					FeedProvider.RenderingInfo ri = new FeedProvider.RenderingInfo(dataValues.get(FeedProvider.NORMALIZED_VALUE_KEY), Color.GREEN, statusText, Color.red, isValid);
					return ri;
				}
			});

			providers.add(provider);
		}
		Executor executor = evaluator.compile(expression);
		FeedProvider.RenderingInfo ri = executor.evaluate(data, providers);
		Assert.assertEquals(ri, expectedValue);
//		Assert.assertEquals(ri.getStatusText(), expectedStatus);
//		Assert.assertEquals(ri.isValid(), isValid);
	}
}
