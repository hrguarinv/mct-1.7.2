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
package gov.nasa.arc.mct.core.policy;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.core.components.MineTaxonomyComponent;
import gov.nasa.arc.mct.policy.ExecutionResult;
import gov.nasa.arc.mct.policy.Policy;
import gov.nasa.arc.mct.policy.PolicyContext;

import java.util.Collection;

public class CannotDragMySandbox implements Policy {

    @Override
    public ExecutionResult execute(PolicyContext context) {
        @SuppressWarnings("unchecked")
        Collection<AbstractComponent> sourceComponents = context.getProperty(PolicyContext.PropertyName.SOURCE_COMPONENTS.getName(), Collection.class);
        assert sourceComponents != null : "source components must be used for this policy";
        
        for (AbstractComponent component : sourceComponents) {
            if (MineTaxonomyComponent.class.isAssignableFrom(component.getClass())) {
                return new ExecutionResult(context, false, "cannot contain my sandbox");
            }
        }
        
        return new ExecutionResult(context, true, "");
    }
    
}
