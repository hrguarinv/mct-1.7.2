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
package gov.nasa.arc.mct.evaluator.component;

import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.evaluator.api.Evaluator;
import gov.nasa.arc.mct.evaluator.api.Executor;
import gov.nasa.arc.mct.evaluator.spi.EvaluatorProvider;
import gov.nasa.arc.mct.evaluator.spi.MultiProvider;

import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestEvaluatorComponent {
	
	@BeforeMethod 
	public void setup() {
		
	}
	
	@Test
	public void testComponent() {
		EvaluatorComponent component = new EvaluatorComponent();
		Assert.assertFalse(component.isLeaf());
		EvaluatorData data = component.getData();
		Assert.assertNotNull(data);
		
		// test accessors
		Assert.assertNull(data.getCode());
		final String code = "code";
		data.setCode(code);
		Assert.assertEquals(data.getCode(), code);
		
		Assert.assertNull(data.getLanguage());
		final String language = "lang";
		data.setLanguage(language);
		Assert.assertEquals(data.getLanguage(), language);
		
		Assert.assertNull(data.getDescription());
		final String description = "desc";
		data.setDescription(description);
		Assert.assertEquals(data.getDescription(), description);
		
		MultiComponent multiComponent = new MultiComponent();
		Assert.assertFalse(multiComponent.isLeaf());
		MultiData data1 = multiComponent.getData();
		Assert.assertNotNull(data1);
		
		// test accessors
		Assert.assertNull(data1.getCode());
		final String code2 = "code";
		data1.setCode(code2);
		Assert.assertEquals(data1.getCode(), code2);
		
		Assert.assertNull(data1.getLanguage());
		final String language2 = "lang";
		data1.setLanguage(language2);
		Assert.assertEquals(data1.getLanguage(), language2);
		
		Assert.assertNull(data1.getDescription());
		final String description2 = "desc";
		data1.setDescription(description2);
		Assert.assertEquals(data1.getDescription(), description2);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testEvaluatorCapability() {
		EvaluatorComponent ec = new EvaluatorComponent();
		Evaluator e = ec.getCapability(Evaluator.class);
		EvaluatorProviderRegistry registry = new EvaluatorProviderRegistry();
		
		Assert.assertNotNull(e.evaluate(Collections.<String,List<Map<String,String>>>emptyMap(), Collections.<FeedProvider>emptyList()));
		
		ec = new EvaluatorComponent();
		final String language = "test/language";
		EvaluatorModelRole mcr = ec.getModel();
		mcr.getData().setLanguage(language);
		final FeedProvider.RenderingInfo expectedRenderingInfo = new FeedProvider.RenderingInfo("", Color.black, "", Color.black, false);
		
		EvaluatorProvider ep = Mockito.mock(EvaluatorProvider.class);
		e = ec.getCapability(Evaluator.class);
		registry.addProvider(ep);
		Executor executor = Mockito.mock(Executor.class);
		Mockito.when(executor.evaluate(Mockito.anyMap(), Mockito.anyList())).thenReturn(expectedRenderingInfo);
		Mockito.when(ep.getLanguage()).thenReturn(language);
		Mockito.when(ep.compile(Mockito.anyString())).thenReturn(executor);
		
		Assert.assertSame(expectedRenderingInfo, e.evaluate(Collections.<String,List<Map<String,String>>>emptyMap(), Collections.<FeedProvider>emptyList()));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testMultiCapability() {
		MultiComponent mc = new MultiComponent();
		Evaluator e = mc.getCapability(Evaluator.class);
		MultiProviderRegistry registry = new MultiProviderRegistry();
		
		Assert.assertNotNull(e.evaluate(Collections.<String,List<Map<String,String>>>emptyMap(), Collections.<FeedProvider>emptyList()));
		
		mc = new MultiComponent();
		final String language = "test/language";
		MultiModelRole mcr = mc.getModel();
		mcr.getData().setLanguage(language);
		final FeedProvider.RenderingInfo expectedRenderingInfo = new FeedProvider.RenderingInfo("", Color.white, "", Color.white, false);
		
		MultiProvider mp = Mockito.mock(MultiProvider.class);
		e = mc.getCapability(Evaluator.class);
		registry.addProvider(mp);
		Executor executor = Mockito.mock(Executor.class);
		Mockito.when(executor.evaluate(Mockito.anyMap(), Mockito.anyList())).thenReturn(expectedRenderingInfo);
		Mockito.when(mp.getLanguage()).thenReturn(language);
		Mockito.when(mp.compile(Mockito.anyString())).thenReturn(executor);
		
		Assert.assertSame(expectedRenderingInfo, e.evaluate(Collections.<String,List<Map<String,String>>>emptyMap(), Collections.<FeedProvider>emptyList()));
	}
}
