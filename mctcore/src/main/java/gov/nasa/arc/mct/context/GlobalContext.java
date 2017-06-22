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
/**
 * GlobalContext.java Sep 23, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.context;

import gov.nasa.arc.mct.identitymgr.IIdentityManager;
import gov.nasa.arc.mct.profile.UserProfile;
import gov.nasa.arc.mct.services.internal.component.User;
import gov.nasa.arc.mct.util.logging.MCTLogger;

import javax.swing.SwingUtilities;

/**
 * Implements a singleton that holds references to MCT subsystems.
 * 
 */
public class GlobalContext {
    private static final MCTLogger ADVISORY_SERVICE_LOGGER = MCTLogger.getLogger("gov.nasa.jsc.advisory.service");
    private static final GlobalContext instance = new GlobalContext();

    private IIdentityManager idManager;

    private final UserProfile userProfile = new UserProfile();

    /**
     * Gets the singleton instance of the global context.
     * 
     * @return the global context
     */
    public static GlobalContext getGlobalContext() {
        return instance;
    }

    /**
     * Creates a global context. This should be protected or private to enforce
     * the singleton pattern.
     */
    private GlobalContext() {
        //
    }

    /**
     * Gets the current user. The user is set by a JVM property that is passed
     * in by the DNAV interface or a command-line parameter.
     * 
     * @return the current user
     */
    public User getUser() {
        return userProfile.getUser();
    }

    /**
     * Change the current user. This is called by the shift change detection
     * mechanism. 
     * 
     * @param user
     *            the new user
     * @param taskRunnable
     *            the task to run after the user is switched
     */
    public void switchUser(final User user, final Runnable taskRunnable) {
        ADVISORY_SERVICE_LOGGER.info("Switching user to: " + (user == null ? "null" : user.getUserId()));
        if (taskRunnable != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    userProfile.setUser(user);
                    taskRunnable.run();
                }
            });
        } else {
            userProfile.setUser(user);
        }
    }
    
    /**
     * Gets the identity manager instance.
     * @return IIdentityManager.
     */
    public IIdentityManager getIdManager() {
        return idManager;
    }

    /**
     * Sets the identity manager instance.
     * @param idManager - Sets the local identity manager to the instance variable.
     */
    public void setIdManager(IIdentityManager idManager) {
        this.idManager = idManager;
    }
   
}
