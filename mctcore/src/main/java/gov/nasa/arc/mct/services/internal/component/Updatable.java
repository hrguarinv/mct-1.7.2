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
package gov.nasa.arc.mct.services.internal.component;

import gov.nasa.arc.mct.components.AbstractComponent;

import java.util.List;

/**
 * Updatable interface.
 */
public interface Updatable {
    
    /**
     * Sets the id.
     * @param id - the unique identifier.
     */
    public void setId(String id);

    /**
     * Sets the base display name.
     * @param baseDisaplyedName - the base display name.
     */
    public void setBaseDisplayedName(String baseDisaplyedName);
    
    /**
     * Sets the component owner.
     * @param owner of the the component. 
     */
    public void setOwner(String owner);
    
    /**
     * Sets the version number.
     * @param version - number.
     */
    public void setVersion(int version);
    
    /**
     * Adds a list of abstract component references.
     * @param references - list of abstract component references.
     */
    public void addReferences(List<AbstractComponent> references);

    /**
     * Removes the list of abstract component references.
     * @param references - list of abstract component references.
     */
    public void removeReferences(List<AbstractComponent> references);
    
    /**
     * Notifies the component that the version running on the client is out of date.
     */
    public void notifyStale();

    /**
     * 
     * @param version version number to compare against
     */
    public void setStaleByVersion(int version);
}
