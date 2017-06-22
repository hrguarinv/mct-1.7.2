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
package gov.nasa.arc.mct.exception;

import gov.nasa.arc.mct.gui.OptionBox;
import gov.nasa.arc.mct.util.logging.MCTLogger;

import java.lang.Thread.UncaughtExceptionHandler;

public class DefaultExceptionHandler implements UncaughtExceptionHandler {

    private MCTLogger logger = MCTLogger.getLogger(DefaultExceptionHandler.class);
    private static final MCTLogger ADVISORY_SERVICE_LOGGER = MCTLogger.getLogger("gov.nasa.jsc.advisory.service");
    private final boolean enableDialogs;

    /**
     * Instantiates a handler to report MCT exceptions
     */
    public DefaultExceptionHandler() {
        this(true);
    }
    
    /**
     * Instantiates a handler to report MCT exceptions. 
     * Allows disabling GUI elements, useful for unit testing.
     * 
     * @param enableDialogs whether GUI dialogs should be also displayed. 
     */
    public DefaultExceptionHandler(boolean enableDialogs) {
        this.enableDialogs = enableDialogs;
        logger.debug("Started the default exception handler.");
    }
    
   /**
    * Dispatches to class specific handlers that add specialized information.
    * Method invoked when the given thread terminates due to the given uncaught exception.
    * 
    * @param thread the thread
    * @param t the exception
    */
    @Override
    public void uncaughtException(Thread thread, Throwable t) {
        String str;
        if (t.getCause() != null) {           
            str = t.getMessage() + " Caused by: "+ t.getCause().getMessage();
        } else {     
            str = t.getMessage();
        }
        ADVISORY_SERVICE_LOGGER.error("MCT has detected an exception: " + str);

        try {
            throw t;
        } catch (Throwable e) {
            if (enableDialogs) {
               generalizedHandler(e);
            } else {
                logger.error("MCT has detected an exception: ", e);
            }
        }
    }

    /**
     * Default reporter.    
     *  
     * @param throwable the throwable
     */
    private void generalizedHandler(Throwable throwable) {
        String str;        
        if (throwable.getCause() != null) {
             str = throwable.getMessage() + "\nCaused by: "+ throwable.getCause().getMessage();
        } else {
             str = throwable.getMessage();
        }
        logger.error(str, throwable);
        if (enableDialogs) {
            OptionBox.showMessageDialog(
                            null,                    
                            str + "\n"
                            + "\n"
                            + "See MCT log for more information.",
                            "Fatal Startup Error",
                            OptionBox.ERROR_MESSAGE
            );
        }
    }

}
