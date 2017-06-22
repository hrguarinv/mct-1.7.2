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

import gov.nasa.arc.mct.evaluator.enums.MultiEvaluator;

import java.util.ArrayList;
import java.util.regex.Matcher;

/** Class to collect all multi-input rules associated with a multi-input evaluator.
 * @author dcberrio
 *
 */
public class MultiRuleExpressionList {

	private ArrayList <MultiRuleExpression> expList;
	
	/**
	 * Expression list constructor.
	 * @param code the code.
	 */
	public MultiRuleExpressionList(String code) {
		expList = compile(code);
	}
	
	/**
	 * Compiles the expression.
	 * @param code the code.
	 * @return array list of expressions.
	 */
	public ArrayList<MultiRuleExpression> compile(String code){
		ArrayList <MultiRuleExpression> expressions = new ArrayList <MultiRuleExpression> ();
		Matcher m = MultiEvaluator.multiExpressionPattern.matcher(code);
		while (m.find()){
			assert m.groupCount() == 7 : "Seven matching groups should be discovered: found " + m.groupCount();
			String singleOrAll = m.group(1);
			String puis = m.group(2);
			String[] puiList = puis.split(MultiRuleExpression.parameterDelimiter, 0);
			String pui = m.group(3);
			String relation = m.group(4);
			String value = m.group(5);
			String display = m.group(6);
			String name = m.group(7);
			expressions.add(new MultiRuleExpression(Enum.valueOf(MultiRuleExpression.SetLogic.class, singleOrAll),
					puiList, pui, relation, value, display, name));
		}
		return expressions;
	}
	
	/**
	 * Gets the size of the expression list.
	 * @return the size number.
	 */
	public int size() {
		return expList.size();
	}
	
	/**
	 * Gets the index of the expression.
	 * @param exp expression.
	 * @return index number.
	 */
	public int indexOf (MultiRuleExpression exp) {
		return expList.indexOf(exp);
	}
	
	/**
	 * Gets the expression based on the index.
	 * @param index number.
	 * @return expression.
	 */
	public MultiRuleExpression getExp (int index) {
		return expList.get(index);
	}
	
	/**
	 * Adds the expression to the list.
	 * @param exp expression.
	 */
	public void addExp (MultiRuleExpression exp) {
		expList.add(exp);
	}
	
	/**
	 * Adds the expression to the list based on the index number and expression object.
	 * @param index number.
	 * @param exp expression object.
	 */
	public void addExp (int index, MultiRuleExpression exp) {
		expList.add(index, exp);
	}
	
	/**
	 * Updates the expression in the list based on the index number and expression object.
	 * @param index number.
	 * @param exp expression object.
	 */
	public void updateExp (int index, MultiRuleExpression exp) {
		expList.remove(index);
		expList.add(index, exp);
	}
	
	/**
	 * Deletes the expression.
	 * @param exp expression.
	 */
	public void deleteExp (MultiRuleExpression exp) {
		int index;
		if (expList != null && expList.contains(exp)) {
			index = expList.indexOf(exp);  
			expList.remove(index);
		}
	}
	
	/**
	 * Moves the expression.
	 * @param moveTo index.
	 * @param expSelected selected expression.
	 */
	public void move (int moveTo, MultiRuleExpression expSelected) {
		MultiRuleExpression temp = expSelected;
		int currentIndex;
		if (expList.contains(expSelected)){
			currentIndex = expList.indexOf(expSelected);
			//case for moving to bottom
			if (moveTo == expList.size()-1){
				expList.remove(currentIndex);
				expList.add(temp);
			}
			else {
				expList.remove(currentIndex);
				expList.add(moveTo, temp);
			}
		}
		
	}
	
	@Override
	public String toString(){
		String expressions = "";
		MultiRuleExpression temp;
		String exp;
		for (int i = 0; i < expList.size(); i++){
			temp = expList.get(i);
			exp = temp.getMultiSetLogic().toString() + 
					"\t" + temp.getPUIs() + 
					"\t" + temp.getSinglePui() +
					"\t" + temp.getOperator() + 
					"\t" + temp.getVal() +
					"\t" + temp.getDisplay() + 
					"\t" + temp.getName() + "|";
			expressions += exp;		
		}
		return expressions;
	}
}
