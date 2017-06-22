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
package gov.nasa.arc.mct.evaluator.component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/** An object to hold all the data needed by an instance of this component
 * @author dcberrio
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MultiData {

	private String code;
	private String language;
	private String description;
	private boolean passThrough;
	private String passThroughParameterId;
	private String fallThroughDisplayValue;
	
	/** Set the encoded data associated with this component, including rules
	 * @param code
	 */
	public void setCode(String code) {
		this.code = code;
	}
	
	/** Get the encoded data associated with this component, including rules
	 * @return code encoded component data
	 */
	public String getCode() {
		return code;
	}
	
	/** Set the language used for rules used by this evaluator
	 * @param language
	 */
	public void setLanguage(String language) {
		this.language = language;
	}
	
	/** Get the language used for rules used by this evaluator
	 * @return language
	 */
	public String getLanguage() {
		return language;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}

	/** Return whether this component is passing through the value of an associated parameter
	 * @return passThrough
	 */
	public boolean isPassThrough() {
		return passThrough;
	}

	/** Set whether this component is passing through the value of an associated parameter
	 * @param passThrough
	 */
	public void setPassThrough(boolean passThrough) {
		this.passThrough = passThrough;
	}

	/** Get the parameter whose value is being passed through by this component
	 * @return passThroughParameterId
	 */
	public String getPassThroughParameterId() {
		return passThroughParameterId;
	}

	/** Set the parameter whose value is being passed through by this component
	 * @param passThroughParameterId
	 */
	public void setPassThroughParameterId(String passThroughParameterId) {
		this.passThroughParameterId = passThroughParameterId;
	}

	/** Get the value this component will display if no rules are firing
	 * @return fallThroughDisplayValue
	 */
	public String getFallThroughDisplayValue() {
		return fallThroughDisplayValue;
	}

	/** Set the value this component will display if no rules are firing
	 * @param fallThroughDisplayValue
	 */
	public void setFallThroughDisplayValue(String fallThroughDisplayValue) {
		this.fallThroughDisplayValue = fallThroughDisplayValue;
	}
}
