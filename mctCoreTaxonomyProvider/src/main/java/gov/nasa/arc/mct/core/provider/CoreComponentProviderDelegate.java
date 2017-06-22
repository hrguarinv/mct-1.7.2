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
package gov.nasa.arc.mct.core.provider;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.core.components.MineTaxonomyComponent;
import gov.nasa.arc.mct.core.components.TelemetryUserDropBoxComponent;
import gov.nasa.arc.mct.platform.core.access.PlatformAccess;
import gov.nasa.arc.mct.platform.spi.PersistenceProvider;
import gov.nasa.arc.mct.services.component.AbstractProviderDelegate;
import gov.nasa.arc.mct.services.internal.component.ComponentInitializer;
import gov.nasa.arc.mct.services.internal.component.CoreComponentRegistry;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class CoreComponentProviderDelegate extends AbstractProviderDelegate {
    
    private static final String DELIM = ",";
    /*
     * Mapping
     * <userid, group> => <created component, set of parent components>
     */
    private Map<String, Map<AbstractComponent, Collection<AbstractComponent>>> map 
                    = new HashMap<String, Map<AbstractComponent,Collection<AbstractComponent>>>();

    @Override
    public void userAdded(String session, String userId, String group) {
        Map<AbstractComponent, Collection<AbstractComponent>> userMap = new HashMap<AbstractComponent, Collection<AbstractComponent>>();
        map.put(userId + DELIM + group, userMap);
        
        PersistenceProvider persistenceService = PlatformAccess.getPlatform().getPersistenceProvider();
        CoreComponentRegistry componentRegistry = PlatformAccess.getPlatform().getComponentRegistry();
                        
        AbstractComponent mySandbox = createMySandbox(persistenceService, componentRegistry, session, userMap, userId, group);
        createUserDropbox(persistenceService, session, userMap, userId, group, mySandbox);        
    }
    
    private AbstractComponent createMySandbox(PersistenceProvider persistenceService, CoreComponentRegistry componentRegistry, 
            String session, Map<AbstractComponent, Collection<AbstractComponent>> userMap, String userId, String group) {
        // Create My Sandbox, which goes under All
        AbstractComponent all = PlatformAccess.getPlatform().getRootComponent();
        AbstractComponent mySandbox = createComponent(MineTaxonomyComponent.class);        
        mySandbox.setDisplayName("My Sandbox");
        mySandbox.setOwner(userId);
        all.addDelegateComponent(mySandbox);
        
        userMap.put(mySandbox, Collections.singleton(all));
        return mySandbox;
    }    
        
    private void createUserDropbox(PersistenceProvider persistenceProvider, String session, 
            Map<AbstractComponent, Collection<AbstractComponent>> userMap, 
            String userId, String group, AbstractComponent mySandbox) {
        
        // Create DropBox under My Sandbox
        AbstractComponent userDropBox = createComponent(TelemetryUserDropBoxComponent.class);
        userDropBox.setOwner(userId);
        ComponentInitializer ci = userDropBox.getCapability(ComponentInitializer.class);
        ci.setCreator(userId);
        ci.setCreationDate(new Date());
        
        userDropBox.setDisplayName(userId + "'s Drop Box");
        mySandbox.addDelegateComponent(userDropBox);
        Collection<AbstractComponent> dropboxParents = new LinkedHashSet<AbstractComponent>();
        dropboxParents.add(mySandbox);
        
        userMap.put(userDropBox, dropboxParents);
        
        // Place user dropbox under the Discpline's Drop Boxes
        AbstractComponent dropboxContainer = null;//ownedByAdmin(persistenceProvider.findComponentByName(session, group + "\'s Drop Boxes"));
        
        assert dropboxContainer != null : "Cannot find " + group + "'s Drop Boxes component";
        dropboxContainer.addDelegateComponents(Collections.singleton(userDropBox));
        
        dropboxParents.add(dropboxContainer);
    }
    
    private AbstractComponent createComponent(Class<? extends AbstractComponent> clazz) {
        AbstractComponent newInstance = null;
        try {
            newInstance = clazz.newInstance();
            newInstance.getCapability(ComponentInitializer.class).initialize();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return newInstance;
    }
    
    /*
    private AbstractComponent ownedByAdmin(Collection<AbstractComponent> components) {
        for (AbstractComponent component : components) {
            if (component.getOwner().equals("admin"))
                return component;
        }
        return null;            
    } */
    
    @Override
    public void userAddedFailed(String userId, String group) {
        Map<AbstractComponent, Collection<AbstractComponent>> userMap = map.get(userId + DELIM + group);
        if (userMap == null)
            return;
        
        // Remove connections to parent components
        for (AbstractComponent child : userMap.keySet()) {
            for (AbstractComponent parent : userMap.get(child)) {
                parent.removeDelegateComponent(child);
            }
        }
        
        // Remove from this delegate's map
        map.remove(userId + DELIM + group);
    }
    
    @Override
    public void userAddedSuccessful(String userId, String group) {
        map.remove(userId + DELIM + group);        
    }

}
