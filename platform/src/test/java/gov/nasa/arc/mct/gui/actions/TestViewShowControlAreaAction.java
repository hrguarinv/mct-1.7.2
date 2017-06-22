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
package gov.nasa.arc.mct.gui.actions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.context.GlobalContext;
import gov.nasa.arc.mct.gui.housing.MCTHousing;
import gov.nasa.arc.mct.gui.util.TestSetupUtilities;
import gov.nasa.arc.mct.platform.spi.Platform;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.services.internal.component.User;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestViewShowControlAreaAction {

    @SuppressWarnings("serial")
    class MyLocalViewShowControlAreaAction extends ViewShowControlAreaAction {
        private MCTHousing myHousing;

        @Override
        protected MCTHousing getHousing() {
            return myHousing;
        }
        
        protected void setTestHousing(MCTHousing inputHousing) {
            myHousing = inputHousing;
        }
    }

    private MyLocalViewShowControlAreaAction viewShow;
    private MCTHousing testHousing;
    @Mock private Platform mockPlatform;
    @Mock private AbstractComponent mockRoot;
    
    @AfterMethod
    protected void teardown() {
        (new PlatformAccess()).setPlatform(null);
    }
    
    @BeforeMethod
    protected void setup() {
        MockitoAnnotations.initMocks(this);
        GlobalContext.getGlobalContext().switchUser(new User() {

            @Override
            public String getUserId() {
                return "abc";
            }

            @Override
            public String getDisciplineId() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public User getValidUser(String userID) {
                // TODO Auto-generated method stub
                return null;
            }
            
        }, null);
        Mockito.when(mockPlatform.getRootComponent()).thenReturn(mockRoot);
        (new PlatformAccess()).setPlatform(mockPlatform);
        testHousing = TestSetupUtilities.setUpActiveHousing();
        viewShow = new MyLocalViewShowControlAreaAction();
        viewShow.setTestHousing(testHousing);
    }

    @Test
    public void testConstructor() {
        assertNotNull(viewShow);
        assertEquals(viewShow.getHousing(), testHousing);
    }
    

    @Test(dependsOnMethods = { "testConstructor" })
    public void testCanHandle() {
        assertEquals(viewShow.canHandle(null), true);
    }

    @Test(dependsOnMethods = { "testCanHandle" })
    public void testIsEnabled() {
        assertEquals(viewShow.isEnabled(), true);
    }

    @Test(dependsOnMethods = { "testCanHandle" })
    public void testActionPerformed() {
        assertFalse(testHousing.isControlAreaVisible());
        viewShow.actionPerformed(null);
        assertTrue(testHousing.isControlAreaVisible());
        viewShow.actionPerformed(null);
        assertFalse(testHousing.isControlAreaVisible());
    }
    
    @Test (dependsOnMethods = { "testActionPerformed" })
    public void testNullHousing() {
       viewShow.setTestHousing(null);
       viewShow.actionPerformed(null); // no exceptions raised.
    }  
    
}
