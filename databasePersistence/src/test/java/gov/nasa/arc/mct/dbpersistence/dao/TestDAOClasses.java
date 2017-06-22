/*******************************************************************************
 * Mission Control Technologies, Copyright (c) 2009-2012, United States Government
 * as represented by the Administrator of the National Aeronautics and Space 
 * Administration. All rights reserved.
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
package gov.nasa.arc.mct.dbpersistence.dao;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestDAOClasses {
	private final List<Class<?>> daoClasses = Arrays.<Class<?>>asList(ComponentSpec.class, DatabaseIdentification.class, Disciplines.class, MctUsers.class, Tag.class, TagAssociation.class, ViewState.class);
	
	@BeforeMethod
	public void setup() {
	}
	
	@Test
	public void testGetAndSetMethods() throws Exception {
		for (Class<?> c:daoClasses) {
			Object o = c.newInstance();
			for (Method m:c.getMethods()) {
				if (m.getName().startsWith("get")) {
					Object setValue = null;
					if (m.getReturnType().equals(String.class)) {
						setValue = "1";
					}
					
					if (m.getReturnType().equals(Integer.TYPE)) {
						setValue = 1;
					}
					
					if (m.getReturnType().equals(Boolean.TYPE)) {
						setValue = true;
					}
					
					if (m.getReturnType().equals(Date.class)) {
						setValue = new Date();
					}
					
					if (setValue == null) {
						continue;
					}

					try {
						Method setMethod = c.getMethod(m.getName().replaceAll("get", "set"), m.getReturnType());
						setMethod.invoke(o, setValue);
						Assert.assertEquals(m.invoke(o, new Object[0]), setValue);
					} catch (NoSuchMethodException nsme) {
						
					}
				}
			}
			Assert.assertEquals(o, o);
			Assert.assertEquals(o.hashCode(), o.hashCode());
			Assert.assertFalse(o.equals(Integer.valueOf(1)));
			o.toString();
			
		}
	}
	
}
