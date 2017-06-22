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
package gov.nasa.arc.mct.evaluator.expressions;

import java.util.ArrayList;
import java.util.List;

/** Class to hold a visual representation of a multi-input expression (rule).
 * @author dcberrio
 *
 */
public class MultiRuleExpression extends Expression {

	/**
	 * Delimiter to use to separate parameter IDs. 
	 */
	public static final String parameterDelimiter = ";";
	private SetLogic multiSetLogic;
	private List<String> puiList = new ArrayList<String>();
	private String singlePui = "";
	private String name = "";
	
	/**
	 * Expression initialization based upon operation, value, and display.
	 * @param setLogic the logic for this rule
	 * @param puis an array of pui IDs for this rule
	 * @param singlePui a single pui to use for this rule
	 * @param op operation.
	 * @param val value.
	 * @param dis display name.
	 * @param name the name of the rule
	 */
	public MultiRuleExpression(SetLogic setLogic, String[] puis, String singlePui, String op, 
			String val, String dis, String name){
		super(op, val, dis);
		this.setMultiSetLogic(setLogic);
		this.name = name;
		this.singlePui = singlePui;
		for (String pui : puis) {
			puiList.add(pui);
		}
	}
	
	/** Get all the parameter IDs associated with this rule.
	 * @return a delimited list of parameter IDs
	 */
	public String getPUIs() {
		StringBuilder sb = new StringBuilder();
		for (String pui : puiList) {
			sb.append(pui+ parameterDelimiter);
		}
		return sb.toString();
	}
	
	/**
	 * Create a Multi rule expression.
	 */
	public MultiRuleExpression() {
		super();
		this.multiSetLogic = SetLogic.SINGLE_PARAMETER;
	}

	/** Return the logic used to combine values compared by this rule.
	 * @return multiSetLogic
	 */
	public SetLogic getMultiSetLogic() {
		return multiSetLogic;
	}

	/** Set the logic used to combine values compared by this rule.
	 * @param multiSetLogic the set logic used by this rule
	 */
	public void setMultiSetLogic(SetLogic multiSetLogic) {
		this.multiSetLogic = multiSetLogic;
	}

	/** Get the name of this rule.
	 * @return name the name of this rule
	 */
	public String getName() {
		return name;
	}

	/** Set the name of this rule.
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/** Get the single parameter ID when the set logic requires it. 
	 * @return singlePui
	 */
	public String getSinglePui() {
		return singlePui;
	}

	/** Set the single parameter ID when the set logic requires it.
	 * @param singlePui set the single parameter ID
	 */
	public void setSinglePui(String singlePui) {
		this.singlePui = singlePui;
	}

	/** Enumeration of rule set logic.
	 * @author dcberrio
	 *
	 */
	public enum SetLogic {
		/** Use a single parameter.
		 * @author dcberrio
		 *
		 */
		SINGLE_PARAMETER ("Single parameter") {
			
			/** Test whether a string matches the set logic.
			 * @param setLogic the set logic to match
			 * @return boolean matching or not
			 */
			public boolean matches(String setLogic) {
				return "SINGLE_PARAMETER".equals(setLogic);
			}
			
		},
		
		/** More than 1 parameter.
		 * @author dcberrio
		 *
		 */
		MORE_THAN_ONE_PARAMETER ("More than 1 parameter") {
			
			/** Test whether a string matches the set logic.
			 * @param setLogic the set logic to match
			 * @return boolean matching or not
			 */
			public boolean matches(String setLogic) {
				return "MORE_THAN_ONE_PARAMETER".equals(setLogic);
			}
			
		},
		
		/** All parameters.
		 * @author dcberrio
		 *
		 */
		ALL_PARAMETERS ("All parameters"){
			
			/** Test whether a string matches the set logic.
			 * @param setLogic the set logic to match
			 * @return boolean matching or not
			 */
			public boolean matches(String setLogic) {
				return "ALL_PARAMETERS".equals(setLogic);
			}
			
		};
		
		private String displayName;
		
		private SetLogic(String name) {
			this.setDisplayName(name);
		}

		/** Get the display name.
		 * @return displayName the name of this rule
		 */
		public String getDisplayName() {
			return displayName;
		}

		/** Set the display name.
		 * @param displayName the name of the rule
		 */
		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}

	}
	
}
