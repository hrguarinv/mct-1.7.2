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
package gov.nasa.arc.mct.api.persistence;

import gov.nasa.arc.mct.components.AbstractComponent;

/**
 * This interface defines the interface to the persistence provider in the platform. Instances of this 
 * interface can be obtained using the OSGI service lookup framework. 
 *
 */
public interface PersistenceService {
    /**
     * This method is invoked when starting a set of related persistence operations in the current thread. If the underlying
     * persistence implementation is a database this will likely start a transaction. This method will 
     * generally only be used from code that operates outside the scope of an action, for example an action 
     * that does some processing in the background. 
     */
    void startRelatedOperations();
    
    /**
     * This method is invoked when completing a set of related persistence operations. This method must
     * be invoked following {@link #startRelatedOperations()} and only a single time. T
     * @param save if the operation should be saved, false if the operation should not be saved. 
     */
    void completeRelatedOperations(boolean save);
    
    /**
     * Checks if <code>tagId</code> is used on at least one component in the database.
     * @param tagId tag ID
     * @return true there are components tagged with <code>tagId</code>; false, otherwise. 
     */
    boolean hasComponentsTaggedBy(String tagId);
    
    /**
     * Returns the component with the specified external key and component type.
     * @param externalKey to use for search criteria
     * @param componentType to use with external key
     * @param <T> type of component 
     * @return instance of component with the given type or null if the component cannot be found.
     */
    <T extends AbstractComponent> T getComponent(String externalKey, Class<T> componentType);

    /**
     * Returns the component with the specified external key and component type.
     * @param externalKey to use for search criteria
     * @param componentType to use with external key
     * @return instance of component with the given type or null if the component cannot
     * be found.
     */
    AbstractComponent getComponent(String externalKey, String componentType);

}
