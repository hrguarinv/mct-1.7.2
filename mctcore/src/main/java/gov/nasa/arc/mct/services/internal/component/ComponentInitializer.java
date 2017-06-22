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
import gov.nasa.arc.mct.components.ExtendedProperties;

import java.util.Date;
import java.util.Map;

/**
 * Component initializer interface.
 *
 */
public interface ComponentInitializer {
    
    /**
     * Sets the id.
     * @param id - the identifier.
     */
    public void setId(String id);
    
    /**
     * Sets the delegate for this component.
     * @param delegate for work unit
     */
    public void setWorkUnitDelegate(AbstractComponent delegate);
    
    /**
     * Notifies the component that is has been persisted so the dirty state can be reset.
     */
    public void componentSaved();
    
    /**
     * Sets the owner.
     * @param owner of the component. 
     */
    public void setOwner(String owner);
    
    /**
     * Sets the creator.
     * @param creator of the component
     */
    public void setCreator(String creator);
    
    /**
     * Sets the creation date.
     * @param creationDate for the component.
     */
    public void setCreationDate(Date creationDate);
    
    /**
     * Sets the view role property as extended properties.
     * @param viewRoleType - view role type.
     * @param properties - extended properties.
     */
    public void setViewRoleProperty(String viewRoleType, ExtendedProperties properties);
    
    /**
     * Gets the view role extended properties.
     * @param viewType - view type classname
     * @return ExtendedProperties - extended properties.
     */
    public ExtendedProperties getViewRoleProperties (String viewType);
    
    /**
     * Gets all the view role extended properties.
     * @return map of extended properties.
     */
    public Map<String, ExtendedProperties> getAllViewRoleProperties();
    
    /**
     * Gets the view role properties that may have been mutated.
     * @return map of possibly mutated properties
     */
    public Map<String, ExtendedProperties> getMutatedViewRoleProperties();
 
    
    /**
     * Adds view role properties.
     * @param viewRoleType - the view role type.
     * @param properties - extended properties.
     */
    public void addViewRoleProperties(String viewRoleType, ExtendedProperties properties);
    
    /**
     * Initializes the component.
     */
    public void initialize();
    
    /**
     * Checks whether the component is initialized or not.
     * @return boolean - flag to check for whether it's been initialized or not.
     */
    public boolean isInitialized();
}
