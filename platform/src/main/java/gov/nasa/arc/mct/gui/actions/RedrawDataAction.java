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

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.ActionContextImpl;
import gov.nasa.arc.mct.gui.ContextAwareAction;
import gov.nasa.arc.mct.gui.housing.MCTAbstractHousing;
import gov.nasa.arc.mct.gui.housing.registry.UserEnvironmentRegistry;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;

public class RedrawDataAction extends ContextAwareAction {

    private static final long serialVersionUID = 123457890L;
    private static final Logger logger = LoggerFactory.getLogger(RedrawDataAction.class);
    
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("RedrawDataMenuResource");
    private static String TEXT = BUNDLE.getString("RedrawDataMenu");
    private ActionContextImpl actionContext = null;
    private Collection<MCTAbstractHousing> housings = null;
    private MCTAbstractHousing currentHousing = null;
    
    public RedrawDataAction() {
        super(TEXT);
    }

    @Override
    public boolean canHandle(ActionContext context) {
        actionContext = (ActionContextImpl) context;
        if (actionContext.getTargetHousing() == null) {
            return false;
        }
        
        housings = getAllHousings();
        logger.debug("MCT housing count: {}", housings.size());
        if (housings == null || housings.isEmpty()) {
            return false;
        }
        
        currentHousing = (MCTAbstractHousing) (actionContext).getTargetHousing();
        if (currentHousing == null) {
            return false;
        }

        return true;
    }

    @Override
    public boolean isEnabled() {
        return ((housings != null) && (housings.size() > 0));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame parentFrame = actionContext.getTargetHousing().getHostedFrame();
        int confirmationResult = JOptionPane.showConfirmDialog(parentFrame,
                String.format(BUNDLE.getString("RedrawDataConfirmDialogMsg")),
                BUNDLE.getString("RedrawDataConfirmDialogTitle"),
                JOptionPane.YES_NO_OPTION);
                
        if (confirmationResult == 0) {           
            PlatformAccess.getPlatform().refreshAllMCTHousedContent();
        }
    }

    private Collection<MCTAbstractHousing> getAllHousings() {
        return UserEnvironmentRegistry.getAllHousings();
    }
}
