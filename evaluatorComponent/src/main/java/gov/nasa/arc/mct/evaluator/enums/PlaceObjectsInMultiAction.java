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

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.DetectGraphicsDevices;
import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.evaluator.component.MultiComponent;
import gov.nasa.arc.mct.evaluator.component.MultiCreationServiceImpl;
import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.ContextAwareAction;
import gov.nasa.arc.mct.gui.OptionBox;
import gov.nasa.arc.mct.gui.View;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This action allows users to select a group of components and
 * create a new multi component that the selected components will
 * be associated with.
 */
        
@SuppressWarnings("serial")
public class PlaceObjectsInMultiAction extends ContextAwareAction {
    
	private static final Logger logger = LoggerFactory.getLogger(PlaceObjectsInMultiAction.class);
	
	private Collection<View> selectedManifestations;
	private static ResourceBundle bundle = ResourceBundle.getBundle("MultiComponent");
    
    private String graphicsDeviceName;
	
	/**
	 * Place objects in multi action constructor.
	 */
    public PlaceObjectsInMultiAction() {
        super(bundle.getString("PlaceObjectsInMulti"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Set<AbstractComponent> sourceComponents = new LinkedHashSet<AbstractComponent>();
        for (View manifestation : selectedManifestations)
            sourceComponents.add(manifestation.getManifestedComponent());
        
        final String name = getNewMulti(sourceComponents);
        if (name.isEmpty())
            return;
        
        final AbstractComponent multi = createNewMulti(sourceComponents);
        if (multi == null) {
            showErrorInCreateMulti();
        } else {
        	multi.setDisplayName(name);
        	SwingUtilities.invokeLater(new Runnable() {
        		public void run() {
        			openNewMulti(name, multi);
        		}
        	});
        }
        
    }

    private String getActiveGraphicsDeviceName(Component component) {
    	String deviceName = null;
    	JFrame rootJFrame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, component);
 		 
 	 	if (rootJFrame != null) {	            
 	 		deviceName = rootJFrame.getGraphicsConfiguration().getDevice().getIDstring();
 	 	 	if (deviceName != null) {
 	 	 		deviceName = deviceName.replace("\\", "");
 	 	 	}
 	 	} else {
 	 	 		logger.warn("Cannot get root JFrame from SwingUtilities.getAncestorOfClass() for multi-monitor support.");
 	 	}
    	return deviceName;
    }
    
    @Override
    public boolean canHandle(ActionContext context) {
        
    	if (DetectGraphicsDevices.getInstance().getNumberGraphicsDevices() > DetectGraphicsDevices.MINIMUM_MONITOR_CHECK) {       
            
    		if (context.getWindowManifestation() != null) {
    			graphicsDeviceName = getActiveGraphicsDeviceName(context.getWindowManifestation());
    		} 
        }
    	
    	selectedManifestations = context.getSelectedManifestations();
        if (selectedManifestations.isEmpty()){
            //No objects selected to add to a new Multi
            return false;
        }
        return true;
    }

    @Override
    public boolean isEnabled() {
        if (selectedManifestations.isEmpty()){
            //No objects selected to add to a new Multi
            return false;
        }
        for (View m : selectedManifestations){
        	if (m.getManifestedComponent().getCapability(FeedProvider.class) == null){
        		return false;
        	}
        }
        return true;
    }
    
    //Returns an array of names of selected components
    private String[] getSelectedComponentNames(Collection<AbstractComponent> components) {
        List<String> names = new ArrayList<String>();
        for (AbstractComponent component : components) {
            names.add(component.getDisplayName());
        }
        return names.toArray(new String[names.size()]);        
    }
    
    /**
     * Gets the new multi.
     * @param sourceComponents collection of components.
     * @return new multi string.
     */
    String getNewMulti(Collection<AbstractComponent> sourceComponents) {
        Frame frame = null;
        for (Frame f: Frame.getFrames()) {
            if (f.isActive()) {
                frame = f;
            }
        }
        
        PlaceObjectsInMultiDialog dialog =
            new PlaceObjectsInMultiDialog(frame, getSelectedComponentNames(sourceComponents));
        return dialog.getConfirmedMultiName();
    }
    
    /**
     * Creates a new component based on the collection of source components.
     * @param sourceComponents the collection of components.
     * @return newly created abstract component.
     */
    AbstractComponent createNewMulti(Collection<AbstractComponent> sourceComponents) {
    	MultiCreationServiceImpl e = new MultiCreationServiceImpl();
    	AbstractComponent ac = e.createMulti(MultiEvaluator.LANGUAGE_STRING, "");
		((MultiComponent)ac).getData().setFallThroughDisplayValue("????");
		if (sourceComponents.size() == 1) {
			((MultiComponent)ac).getData().setPassThrough(true);
			FeedProvider fp = sourceComponents.iterator().next().getCapability(FeedProvider.class);
			((MultiComponent)ac).getData().setPassThroughParameterId(fp.getSubscriptionId());
		} else {
			((MultiComponent)ac).getData().setPassThrough(false);
		}
    	ac.addDelegateComponents(sourceComponents);
    	return ac;
    }
    
    /**
     * Shows the error dialog message.
     */
    void showErrorInCreateMulti() {
        OptionBox.showMessageDialog(selectedManifestations.iterator().next(), "Internal Error - Unable to place selected objects to a new collection.", "Error", OptionBox.ERROR_MESSAGE);    
    }
    
    /**
     * Opens the new multi.
     * @param name display name.
     * @param e component.
     */
    void openNewMulti(String name, AbstractComponent e) {
        if (DetectGraphicsDevices.getInstance().getNumberGraphicsDevices() > DetectGraphicsDevices.MINIMUM_MONITOR_CHECK) {
            GraphicsConfiguration graphicsConfig = DetectGraphicsDevices.getInstance().getSingleGraphicDeviceConfig(graphicsDeviceName);
            e.open(graphicsConfig);
        } else {
            e.open();
        }
    }

}
