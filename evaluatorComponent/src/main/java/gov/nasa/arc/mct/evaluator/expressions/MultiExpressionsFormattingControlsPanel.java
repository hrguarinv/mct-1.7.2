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

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.evaluator.expressions.MultiRuleExpression.SetLogic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Multi-input expression formating control panel class.
 */
@SuppressWarnings("serial")
public class MultiExpressionsFormattingControlsPanel extends JPanel {
	private static final ResourceBundle bundle = ResourceBundle.getBundle("MultiComponent");
	private static final Map<String,SetLogic> setLogicStrings = new HashMap<String,SetLogic>(); 
	private static final String[] setLogicFields = {"Single parameter", 
		"More than 1 parameter", "All parameters"};
	private static final String[] opStrings = { "\u2260","<",">","=" };
	
	private JPanel controlPanel;
	private JPanel ruleTablePanel;
	private JPanel bottomPanel;
	//Creation Panel
	private JButton addExpButton = null;
	private JButton deleteExpButton = null;
	private JButton applyButton = null;
	
	//Expression Order Editing Panel
	private JButton upOneButton = null;
	private JButton downOneButton = null;
	private JComboBox ifComboBox = null;
	private JComboBox parameterComboBox = null;
	private JComboBox opComboBox = null;
	
	private JTextField indeterminateTextField = null;
	private JTextField ruleNameTextField = null;
	private JTextField ruleResultTextField = null;
	private JFormattedTextField valueTextField = null;
	private JTextField displayTextField = null;
	
	private JCheckBox passThroughCheckBox;
	
	private boolean listenersEnabled = true;
	private boolean applyButtonListenersEnabled = true;
	
	private final MultiViewManifestation multiViewManifestation;
	private MultiExpressionsController controller;
	
	static {
		setLogicStrings.put("Single parameter", SetLogic.SINGLE_PARAMETER);
		setLogicStrings.put("More than 1 parameter", SetLogic.MORE_THAN_ONE_PARAMETER);
		setLogicStrings.put("All parameters",SetLogic.ALL_PARAMETERS);
	}
	
	/**
	 * Initialize the expressions format control panel.
	 * @param multiViewManifestation the view manifestation.
	 */
	MultiExpressionsFormattingControlsPanel (MultiViewManifestation multiView) {
		this.multiViewManifestation = multiView;
		controlPanel = createExpressionsFormattingControlsPanel();
		loadRuleButtonSettings();
		controller = new MultiExpressionsController(multiView);
		this.add(controlPanel);
		if (multiViewManifestation.getExpressions().size() == 0) {
			ruleTablePanel.setVisible(false);
			bottomPanel.setVisible(false);
		}
	}
	
	/**
	 * Load the states of the controls based on current rule set.
	 */
	public void loadRuleButtonSettings() {
		if (multiViewManifestation.getTelemetry().size() == 0 || 
				multiViewManifestation.getMulti().getData().isPassThrough()) {
			addExpButton.setEnabled(false);
			deleteExpButton.setEnabled(false);
			upOneButton.setEnabled(false);
			downOneButton.setEnabled(false);
		} else {
			addExpButton.setEnabled(true);
			if (multiViewManifestation.getExpressions().size() == 0) {
				deleteExpButton.setEnabled(false);
			} else {
				if (multiViewManifestation.getSelectedExpression() != null) {
					deleteExpButton.setEnabled(true);
				} else {
					deleteExpButton.setEnabled(false);
				}
			}
			if (multiViewManifestation.getExpressions().size() >= 2 && 
					multiViewManifestation.getSelectedExpression() != null) {
				upOneButton.setEnabled(true);
				downOneButton.setEnabled(true);
			} else {
				upOneButton.setEnabled(false);
				downOneButton.setEnabled(false);
			}
		}
	}
	
	private JPanel createExpressionsFormattingControlsPanel() {
		JPanel controlPanel = new JPanel (new GridBagLayout());
		JPanel creationPanel = createCreationPanel(true);
		JPanel indeterminatePanel = createIndeterminatePanel();
		
		//Creation Panel
		GridBagConstraints creationPanelConstraints = new GridBagConstraints();
		creationPanelConstraints.fill = GridBagConstraints.NONE;
		creationPanelConstraints.anchor = GridBagConstraints.NORTHWEST;
		creationPanelConstraints.weighty = 0;
		creationPanelConstraints.weightx = 1;
		creationPanelConstraints.insets = new Insets(3,7,3,7);
		creationPanelConstraints.gridx = 0;
		creationPanelConstraints.gridy = 0;
		controlPanel.add(creationPanel, creationPanelConstraints);

		//Indeterminate Panel
		GridBagConstraints indeterminatePanelConstraints = new GridBagConstraints();
		indeterminatePanelConstraints.fill = GridBagConstraints.NONE;
		indeterminatePanelConstraints.anchor = GridBagConstraints.NORTHWEST;
		indeterminatePanelConstraints.weighty = 1;
		indeterminatePanelConstraints.weightx = 1;
		indeterminatePanelConstraints.insets = new Insets(3,7,3,7);
		indeterminatePanelConstraints.gridx = 0;
		indeterminatePanelConstraints.gridy = 2;
		controlPanel.add(indeterminatePanel, indeterminatePanelConstraints);
		
		controlPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
		
		this.setLayout(new GridLayout(1,0));
		return controlPanel;
		
	}
	
	
	private JPanel createRuleTablePanel() {
		ruleTablePanel = new JPanel();
		ruleTablePanel.setLayout(new GridBagLayout());
		JPanel ruleTableInnerPanel = new JPanel(new GridBagLayout());
		ruleTableInnerPanel.setBorder(BorderFactory.createEmptyBorder(5,2,2,2));
		GridBagConstraints ruleListInnerPanelConstraints = new GridBagConstraints();
		ruleListInnerPanelConstraints.fill = GridBagConstraints.NONE;
		ruleListInnerPanelConstraints.anchor = GridBagConstraints.LINE_START;
		ruleListInnerPanelConstraints.weighty = 1;
		ruleListInnerPanelConstraints.weightx = 0;
		ruleListInnerPanelConstraints.ipadx = 1;
		ruleListInnerPanelConstraints.gridx = 0;
		ruleListInnerPanelConstraints.gridy = 0;
		ruleTableInnerPanel.add(new JLabel(bundle.getString("RuleNameLabel")), ruleListInnerPanelConstraints);
		
		
		GridBagConstraints ruleListInnerPanelConstraints3 = new GridBagConstraints();
		ruleListInnerPanelConstraints3.fill = GridBagConstraints.NONE;
		ruleListInnerPanelConstraints3.anchor = GridBagConstraints.LINE_START;
		ruleListInnerPanelConstraints3.weighty = 1;
		ruleListInnerPanelConstraints3.weightx = 0;
		ruleListInnerPanelConstraints3.ipadx = 1;
		ruleListInnerPanelConstraints3.gridx = 1;
		ruleListInnerPanelConstraints3.gridy = 0;
		ruleNameTextField = new JTextField();
		ruleNameTextField.setPreferredSize(new Dimension(150,20));
		ruleNameTextField.setEditable(true);
		ruleNameTextField.getDocument().addDocumentListener(new ApplyButtonListener());
		ruleTableInnerPanel.add(ruleNameTextField, ruleListInnerPanelConstraints3);
		
		GridBagConstraints ruleListInnerPanelConstraints4 = new GridBagConstraints();
		ruleListInnerPanelConstraints4.fill = GridBagConstraints.NONE;
		ruleListInnerPanelConstraints4.anchor = GridBagConstraints.LINE_START;
		ruleListInnerPanelConstraints4.weighty = 1;
		ruleListInnerPanelConstraints4.weightx = 0;
		ruleListInnerPanelConstraints4.ipadx = 1;
		ruleListInnerPanelConstraints4.gridx = 0;
		ruleListInnerPanelConstraints4.gridy = 0;
		ruleTablePanel.add(ruleTableInnerPanel, ruleListInnerPanelConstraints4);
		
		final int borderWidth = 2;
		JPanel tablePanel = new JPanel(new GridLayout(0, 5));
		tablePanel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		JPanel ifPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		ifPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		ifPanel.add(new JLabel(bundle.getString("IfLabel")));
		JPanel parametersPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		parametersPanel.setBorder(BorderFactory.createMatteBorder(borderWidth, 
                0, 
                borderWidth, 
                borderWidth, 
                Color.BLACK));
		parametersPanel.add(new JLabel(bundle.getString("ParameterLabel")));
		JPanel opPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		opPanel.setBorder(BorderFactory.createMatteBorder(borderWidth, 
                0, 
                borderWidth, 
                borderWidth, 
                Color.BLACK));
		opPanel.add(new JLabel(bundle.getString("OpLabel")));
		JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		valuePanel.setBorder(BorderFactory.createMatteBorder(borderWidth, 
                0, 
                borderWidth, 
                borderWidth, 
                Color.BLACK));
		valuePanel.add(new JLabel(bundle.getString("ValueLabel")));
		JPanel displayPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		displayPanel.setBorder(BorderFactory.createMatteBorder(borderWidth, 
                0, 
                borderWidth, 
                borderWidth, 
                Color.BLACK));
		displayPanel.add(new JLabel(bundle.getString("DisplayLabel")));
		tablePanel.add(ifPanel, 0);
		tablePanel.add(parametersPanel, 1);
		tablePanel.add(opPanel, 2);
		tablePanel.add(valuePanel, 3);
		tablePanel.add(displayPanel, 4);
		
		// Add rule components
		JPanel ifChoicePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		ifChoicePanel.setBorder(BorderFactory.createMatteBorder(0, 
                borderWidth, 
                borderWidth, 
                borderWidth, 
                Color.BLACK));
		ifComboBox = new JComboBox(setLogicFields);
		ifComboBox.addActionListener(new ApplyActionListener());
		ifChoicePanel.add(ifComboBox);
		tablePanel.add(ifChoicePanel);
		JPanel paramChoicePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		paramChoicePanel.setBorder(BorderFactory.createMatteBorder(0, 
                0, 
                borderWidth, 
                borderWidth, 
                Color.BLACK));
		parameterComboBox = new JComboBox(getTelemetryIDs());
		parameterComboBox.addActionListener(new ApplyActionListener());
		paramChoicePanel.add(parameterComboBox);
		tablePanel.add(paramChoicePanel);
		JPanel opChoicePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		opChoicePanel.setBorder(BorderFactory.createMatteBorder(0, 
                0, 
                borderWidth, 
                borderWidth, 
                Color.BLACK));

		opComboBox = new JComboBox(opStrings);
		opComboBox.addActionListener(new ApplyActionListener());
		opChoicePanel.add(opComboBox);
		tablePanel.add(opChoicePanel);
		JPanel valueChoicePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		valueChoicePanel.setBorder(BorderFactory.createMatteBorder(0, 
                0, 
                borderWidth, 
                borderWidth, 
                Color.BLACK));

		NumberFormat generalNumberFormat = NumberFormat.getInstance();
		valueTextField = new JFormattedTextField(generalNumberFormat);
		valueTextField.setEditable(true);
		valueTextField.setName(bundle.getString("ValueControlName"));
		valueTextField.setPreferredSize(new Dimension(100,20));
		valueTextField.getDocument().addDocumentListener(new ApplyButtonListener());
		valueChoicePanel.add(valueTextField);
		tablePanel.add(valueChoicePanel);
		JPanel displayChoicePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		displayChoicePanel.setBorder(BorderFactory.createMatteBorder(0, 
                0, 
                borderWidth, 
                borderWidth, 
                Color.BLACK));
		displayTextField = new JTextField();
		displayTextField.setEditable(true);
		displayTextField.setName(bundle.getString("DisplayControlName"));
		displayTextField.setPreferredSize(new Dimension(100,20));
		displayTextField.getDocument().addDocumentListener(new ApplyButtonListener());
		displayChoicePanel.add(displayTextField);
		tablePanel.add(displayChoicePanel);
		GridBagConstraints ruleListInnerPanelConstraints2 = new GridBagConstraints();
		ruleListInnerPanelConstraints2.fill = GridBagConstraints.NONE;
		ruleListInnerPanelConstraints2.anchor = GridBagConstraints.LINE_START;
		ruleListInnerPanelConstraints2.weighty = 1;
		ruleListInnerPanelConstraints2.weightx = 0;
		ruleListInnerPanelConstraints2.ipadx = 1;
		ruleListInnerPanelConstraints2.gridx = 0;
		ruleListInnerPanelConstraints2.gridy = 1;
		ruleTablePanel.add(tablePanel, ruleListInnerPanelConstraints2);
		return ruleTablePanel;
	}
	
	private class ApplyActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (displayTextField.getText().isEmpty() || valueTextField.getText().isEmpty()) {
				if (applyButtonListenersEnabled) {
					applyButton.setEnabled(false);
				}
				applyButton.setToolTipText("Rule value or display fields do not contain valid values");
				ruleNameTextField.setToolTipText(ruleNameTextField.getText());
				
			} else {
				if (applyButtonListenersEnabled) {
					applyButton.setEnabled(true);
				}
				applyButton.setToolTipText("Apply changes to this rule");
			}
			displayTextField.setToolTipText(displayTextField.getText());
			valueTextField.setToolTipText(valueTextField.getText());
		}
		
	}
	
	private class ApplyButtonListener implements DocumentListener {
		public void insertUpdate(DocumentEvent event) {
			if (displayTextField.getText().isEmpty() || valueTextField.getText().isEmpty()) {
				if (applyButtonListenersEnabled) {
					applyButton.setEnabled(false);
				}
				applyButton.setToolTipText("Rule value or display fields do not contain valid values");
				ruleNameTextField.setToolTipText(ruleNameTextField.getText());
				
			} else {
				if (applyButtonListenersEnabled) {
					applyButton.setEnabled(true);
				}
				applyButton.setToolTipText("Apply changes to this rule");
			}
			displayTextField.setToolTipText(displayTextField.getText());
			valueTextField.setToolTipText(valueTextField.getText());
		}
		public void removeUpdate(DocumentEvent event) {
			if (displayTextField.getText().isEmpty() || valueTextField.getText().isEmpty()) {
				if (applyButtonListenersEnabled) {
					applyButton.setEnabled(false);
				}
				applyButton.setToolTipText("Rule value or display fields do not contain valid values");
			} else {
				if (applyButtonListenersEnabled) {
					applyButton.setEnabled(true);
				}
				applyButton.setToolTipText("Apply changes to this rule");
			}
			displayTextField.setToolTipText(displayTextField.getText());
			valueTextField.setToolTipText(valueTextField.getText());
		}
		@Override
		public void changedUpdate(DocumentEvent e) {
		}
	}
	
	private String[] addDataFeedPrefixes(String[] puis) {
		if (puis == null) return null;
		String[] returnStrings = new String[puis.length];
		for (int i=0 ; i < puis.length ; i++) {
			if (!puis[i].contains(":") && bundle.getString("DataFeedPrefix").length() > 0){
				returnStrings[i] = bundle.getString("DataFeedPrefix") + puis[i];
			} else {
				returnStrings[i] = puis[i];
			}
		}
		return returnStrings;
	}
	
	private String[] getTelemetryIDs() {
		List<String> ids = new ArrayList<String>();
		for (AbstractComponent ac : multiViewManifestation.getTelemetry()) {
			ids.add(ac.getCapability(FeedProvider.class).getSubscriptionId());
		}
		return ids.toArray(new String[0]);
	}
	
	
	/**
	 * Set the pass through function according to the component state.
	 */
	public void updatePassThroughControl() {
		if (multiViewManifestation.getTelemetry().size() != 1) {
			if (passThroughCheckBox.isSelected()) {
				passThroughCheckBox.setSelected(false);
				multiViewManifestation.getMulti().getData().setPassThrough(false);
			}
			passThroughCheckBox.setEnabled(false);
		} else {
			passThroughCheckBox.setEnabled(true);
		}
	}
	
	private JPanel createCreationPanel(boolean includeRuleTable) {
		JPanel creationPanel = new JPanel();
		creationPanel.setLayout(new GridBagLayout());
		
		//Add title
		creationPanel.setBorder(BorderFactory.createTitledBorder(bundle.getString("CreateDeleteTitle")));
		
		JPanel creationInnerPanel = new JPanel();
		GridBagConstraints innerPanelConstraints = new GridBagConstraints();
		innerPanelConstraints.fill = GridBagConstraints.NONE;
		innerPanelConstraints.anchor = GridBagConstraints.LINE_START;
		innerPanelConstraints.weighty = 1;
		innerPanelConstraints.weightx = 0;
		innerPanelConstraints.ipadx = 1;
		innerPanelConstraints.gridx = 0;
		innerPanelConstraints.gridy = 1;
		creationPanel.add(creationInnerPanel, innerPanelConstraints);
		
		JPanel checkBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		passThroughCheckBox = new JCheckBox();
		passThroughCheckBox.setName(bundle.getString("PassThroughControl"));
		if (multiViewManifestation.getTelemetry().size() != 1) {
			passThroughCheckBox.setEnabled(false);
		}
		if (multiViewManifestation.getMulti().getData().isPassThrough()) {
			passThroughCheckBox.setSelected(true);
		}
		passThroughCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
		        AbstractButton abstractButton = (AbstractButton) e.getSource();
		        if (abstractButton.getModel().isSelected()) {
		        	multiViewManifestation.getMulti().getData().setPassThrough(true);
					multiViewManifestation.getMulti().getData().setPassThroughParameterId(multiViewManifestation.getTelemetry().get(0).getCapability(FeedProvider.class).getSubscriptionId());
		        	addExpButton.setEnabled(false);
		        	deleteExpButton.setEnabled(false);
		        	upOneButton.setEnabled(false);
		        	downOneButton.setEnabled(false);
		        } else {
		        	multiViewManifestation.getMulti().getData().setPassThrough(false);
		        	multiViewManifestation.getMulti().getData().setPassThroughParameterId(null);
		        	addExpButton.setEnabled(true);
		        	deleteExpButton.setEnabled(true);
		        	upOneButton.setEnabled(true);
		        	downOneButton.setEnabled(true);
		        }
		        multiViewManifestation.fireFocusPersist();
			}
			
		});
		checkBoxPanel.add(passThroughCheckBox);
		checkBoxPanel.add(new JLabel(bundle.getString("PassThroughLabel")));

		
		
		creationInnerPanel.setLayout(new GridBagLayout());
		
		//Creation controls
		addExpButton = new JButton(bundle.getString("AddExpressionTitle"));
		deleteExpButton = new JButton(bundle.getString("DeleteExpressionTitle"));
		upOneButton = new JButton(bundle.getString("MoveUpOneTitle"));
		downOneButton = new JButton(bundle.getString("MoveDownOneTitle"));
		applyButton = new JButton(bundle.getString("ApplyTitle"));
		
		addExpButton.setName(bundle.getString("AddExpressionTitle"));
		deleteExpButton.setName(bundle.getString("DeleteExpressionTitle"));
		upOneButton.setName(bundle.getString("MoveUpOneTitle"));
		downOneButton.setName(bundle.getString("MoveDownOneTitle"));
		applyButton.setName(bundle.getString("ApplyTitle"));
		
		addExpButton.setToolTipText(bundle.getString("AddExpressionToolTip"));
		deleteExpButton.setToolTipText(bundle.getString("DeleteExpressionToolTip"));
		upOneButton.setToolTipText(bundle.getString("MoveUpOneToolTip"));
		downOneButton.setToolTipText(bundle.getString("MoveDownOneToolTip"));
		applyButton.setToolTipText(bundle.getString("ApplyTitle"));
		
		addExpButton.setOpaque(false);
		deleteExpButton.setOpaque(false);
		upOneButton.setOpaque(false);
		downOneButton.setOpaque(false);
		applyButton.setOpaque(false);
		
		addExpButton.setFocusPainted(false);
		deleteExpButton.setFocusPainted(false);
		upOneButton.setFocusPainted(false);
		downOneButton.setFocusPainted(false);
		applyButton.setFocusPainted(false);
		
		addExpButton.setSize(200,200);
		addExpButton.setContentAreaFilled(true);
		deleteExpButton.setSize(200,200);
		deleteExpButton.setContentAreaFilled(true);
		upOneButton.setSize(200,10);
		upOneButton.setContentAreaFilled(true);
		downOneButton.setSize(200,10);
		downOneButton.setContentAreaFilled(true);
		applyButton.setSize(200,10);
		applyButton.setContentAreaFilled(true);

		applyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!valueTextField.getText().isEmpty() && !displayTextField.getText().isEmpty() && 
						multiViewManifestation.getSelectedExpression() != null) {
					MultiRuleExpression newExp = new MultiRuleExpression(setLogicStrings.get(ifComboBox.getSelectedItem().toString()),
							multiViewManifestation.getSelectedExpression().getPUIs().split(MultiRuleExpression.parameterDelimiter,0),
							parameterComboBox.getSelectedItem().toString().contains(":") ? parameterComboBox.getSelectedItem().toString() :
								bundle.getString("DataFeedPrefix")+parameterComboBox.getSelectedItem().toString(),
							opComboBox.getSelectedItem().toString(),
							valueTextField.getText(),
							displayTextField.getText(),
							ruleNameTextField.getText());
					controller.notifyRuleUpdated(multiViewManifestation.getExpressions().indexOf(multiViewManifestation.getSelectedExpression()), 
							multiViewManifestation.getExpressions(), 
							newExp);
					multiViewManifestation.getMulti().getData().setCode(multiViewManifestation.getExpressions().toString());
					multiViewManifestation.fireFocusPersist();
					applyButton.setEnabled(false);
					multiViewManifestation.setSelectedExpression(newExp);
				}
			}
		});
		applyButton.setEnabled(false);
		
		addExpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (listenersEnabled){
					ruleTablePanel.setVisible(true);
					bottomPanel.setVisible(true);
					String valueText = valueTextField.getText().isEmpty() ? bundle.getString("DefaultValue") : valueTextField.getText();
					String displayText = displayTextField.getText().isEmpty() ? bundle.getString("DefaultDisplayValue") : displayTextField.getText();
					MultiRuleExpression newExp = new MultiRuleExpression(setLogicStrings.get(ifComboBox.getSelectedItem().toString()),
							addDataFeedPrefixes(getTelemetryIDs()),
							parameterComboBox.getSelectedItem().toString().contains(":") ? parameterComboBox.getSelectedItem().toString() :
								bundle.getString("DataFeedPrefix")+parameterComboBox.getSelectedItem().toString(),
							opComboBox.getSelectedItem().toString(),
							valueText,
							displayText,
							ruleNameTextField.getText());
					controller.notifyExpressionAdded(newExp, 
							multiViewManifestation.getExpressions());
					multiViewManifestation.getMulti().getData().setCode(multiViewManifestation.getExpressions().toString());
					multiViewManifestation.fireFocusPersist();
					multiViewManifestation.setSelectedExpression(newExp);
					loadRuleButtonSettings();
				}
			}
		});
		
		deleteExpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MultiRuleExpression selectedExpression = multiViewManifestation.getSelectedExpression();
				if (listenersEnabled){
					controller.notifyExpressionDeleted(selectedExpression, multiViewManifestation.getExpressions());
					if (multiViewManifestation.getExpressions().size() == 0) {
						ruleTablePanel.setVisible(false);
						bottomPanel.setVisible(false);
					}
					multiViewManifestation.getMulti().getData().setCode(multiViewManifestation.getExpressions().toString());
					multiViewManifestation.fireFocusPersist();
					loadRuleButtonSettings();
				}
			}
		});
		
		upOneButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MultiRuleExpression selectedExpression = multiViewManifestation.getSelectedExpression();
				if (listenersEnabled){
					controller.notifyMovedUpOne(selectedExpression, multiViewManifestation.getExpressions());
					multiViewManifestation.getMulti().getData().setCode(multiViewManifestation.getExpressions().toString());
					multiViewManifestation.fireFocusPersist();
					multiViewManifestation.setSelectedExpression(selectedExpression);
				}
			}
		});
		
		downOneButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MultiRuleExpression selectedExpression = multiViewManifestation.getSelectedExpression();
				if (listenersEnabled){
					controller.notifyMovedDownOne(selectedExpression, multiViewManifestation.getExpressions());
					multiViewManifestation.getMulti().getData().setCode(multiViewManifestation.getExpressions().toString());
					multiViewManifestation.fireFocusPersist();
					multiViewManifestation.setSelectedExpression(selectedExpression);
				}
			}
		});
		
		ruleResultTextField = new JTextField();
		ruleResultTextField.setEditable(false);
		ruleResultTextField.setBackground(Color.WHITE);
		ruleResultTextField.setPreferredSize(new Dimension(100,20));
		
		GridBagConstraints addExpButtonConstraints = new GridBagConstraints();
		addExpButtonConstraints.fill = GridBagConstraints.NONE;
		addExpButtonConstraints.anchor = GridBagConstraints.LINE_START;
		addExpButtonConstraints.weighty = 1;
		addExpButtonConstraints.weightx = 0;
		addExpButtonConstraints.ipadx = 1;
		addExpButtonConstraints.gridx = 0;
		addExpButtonConstraints.gridy = 0;
		creationInnerPanel.add(addExpButton, addExpButtonConstraints);
		
		GridBagConstraints deleteExpButtonConstraints = new GridBagConstraints();
		deleteExpButtonConstraints.fill = GridBagConstraints.NONE;
		deleteExpButtonConstraints.anchor = GridBagConstraints.LINE_START;
		deleteExpButtonConstraints.weighty = 1;
		deleteExpButtonConstraints.weightx = 0;
		deleteExpButtonConstraints.ipadx = 1;
		deleteExpButtonConstraints.gridx = 1;
		deleteExpButtonConstraints.gridy = 0;
		creationInnerPanel.add(deleteExpButton, deleteExpButtonConstraints);
		
		GridBagConstraints addAboveButtonConstraints = new GridBagConstraints();
		addAboveButtonConstraints.fill = GridBagConstraints.NONE;
		addAboveButtonConstraints.anchor = GridBagConstraints.LINE_START;
		addAboveButtonConstraints.weighty = 1;
		addAboveButtonConstraints.weightx = 0;
		addAboveButtonConstraints.ipadx = 1;
		addAboveButtonConstraints.gridx = 2;
		addAboveButtonConstraints.gridy = 0;
		creationInnerPanel.add(upOneButton, addAboveButtonConstraints);
		
		GridBagConstraints addBelowButtonConstraints = new GridBagConstraints();
		addBelowButtonConstraints.fill = GridBagConstraints.NONE;
		addBelowButtonConstraints.anchor = GridBagConstraints.LINE_START;
		addBelowButtonConstraints.weighty = 1;
		addBelowButtonConstraints.weightx = 1;
		addBelowButtonConstraints.ipadx = 1;
		addBelowButtonConstraints.gridx = 3;
		addBelowButtonConstraints.gridy = 0;
		creationInnerPanel.add(downOneButton, addBelowButtonConstraints);
		
		GridBagConstraints checkBoxPanelConstraints = new GridBagConstraints();
		checkBoxPanelConstraints.fill = GridBagConstraints.NONE;
		checkBoxPanelConstraints.anchor = GridBagConstraints.LINE_START;
		checkBoxPanelConstraints.weighty = 1;
		checkBoxPanelConstraints.weightx = 1;
		checkBoxPanelConstraints.ipadx = 1;
		checkBoxPanelConstraints.gridx = 5;
		checkBoxPanelConstraints.gridy = 0;
		creationInnerPanel.add(checkBoxPanel, checkBoxPanelConstraints);
		
		if (includeRuleTable) {
			GridBagConstraints rulePanelConstraints = new GridBagConstraints();
			rulePanelConstraints.fill = GridBagConstraints.NONE;
			rulePanelConstraints.anchor = GridBagConstraints.LINE_START;
			rulePanelConstraints.weighty = 1;
			rulePanelConstraints.weightx = 1;
			rulePanelConstraints.ipadx = 1;
			rulePanelConstraints.gridx = 0;
			rulePanelConstraints.gridy = 2;
			creationPanel.add(createRuleTablePanel(), rulePanelConstraints);
			
			bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			bottomPanel.add(new JLabel(bundle.getString("RuleResultTitle")));
			bottomPanel.add(ruleResultTextField);
			bottomPanel.add(applyButton );
			bottomPanel.setPreferredSize(new Dimension((int) ruleTablePanel.getPreferredSize().getWidth(),50));
			GridBagConstraints rulePanelConstraints2 = new GridBagConstraints();
			rulePanelConstraints2.fill = GridBagConstraints.NONE;
			rulePanelConstraints2.anchor = GridBagConstraints.LINE_START;
			rulePanelConstraints2.weighty = 1;
			rulePanelConstraints2.weightx = 1;
			rulePanelConstraints2.ipadx = 1;
			rulePanelConstraints2.gridx = 0;
			rulePanelConstraints2.gridy = 3;
			creationPanel.add(bottomPanel, rulePanelConstraints2);
		}
		
		return creationPanel;
	}
	
	/**
	 * Load the currently selected rule into the controls.
	 */
	public void loadRule() {
		if (multiViewManifestation.getSelectedExpression() != null) {
			applyButtonListenersEnabled = false;
			ifComboBox.setSelectedItem(multiViewManifestation.getSelectedExpression().getMultiSetLogic().getDisplayName());
			parameterComboBox.removeAllItems();
			for (String parameter : multiViewManifestation.getSelectedExpression().getPUIs().split(MultiRuleExpression.parameterDelimiter)) {
				if (parameter.startsWith(bundle.getString("DataFeedPrefix"))){
					parameterComboBox.addItem(parameter.substring(bundle.getString("DataFeedPrefix").length()));
				} else {
					parameterComboBox.addItem(parameter);
				}
			}
			if (multiViewManifestation.getSelectedExpression().getMultiSetLogic().equals(SetLogic.SINGLE_PARAMETER)) {
				if (multiViewManifestation.getSelectedExpression().getSinglePui().startsWith(bundle.getString("DataFeedPrefix"))){
					parameterComboBox.setSelectedItem(multiViewManifestation.getSelectedExpression().getSinglePui().substring(bundle.getString("DataFeedPrefix").length()));
				} else {
					parameterComboBox.setSelectedItem(multiViewManifestation.getSelectedExpression().getSinglePui());
				}
			}
			opComboBox.setSelectedItem(multiViewManifestation.getSelectedExpression().getOperator());
			valueTextField.setText(multiViewManifestation.getSelectedExpression().getVal().toString());
			displayTextField.setText(multiViewManifestation.getSelectedExpression().getDisplay());
			displayTextField.setToolTipText(displayTextField.getText());
			valueTextField.setToolTipText(valueTextField.getText());
			ruleNameTextField.setText(multiViewManifestation.getSelectedExpression().getName());
			ruleNameTextField.setToolTipText(ruleNameTextField.getText());
			applyButtonListenersEnabled = true;
			applyButton.setEnabled(false);
			loadRuleButtonSettings();
		}
	}
	
	
	private JPanel createIndeterminatePanel(){
		JPanel indeterminatePanel = new JPanel();
		indeterminatePanel.setLayout(new BorderLayout());
		
		//Add title
		indeterminatePanel.setBorder(BorderFactory.createTitledBorder(bundle.getString("Indeterminate")));
		
		JPanel indeterminateInnerPanel = new JPanel();
		
		indeterminateInnerPanel.setLayout(new GridBagLayout());
		
		indeterminateTextField = new JTextField();
		indeterminateTextField.setPreferredSize(new Dimension(150,20));
		indeterminateTextField.setEditable(true);
		indeterminateTextField.setText(multiViewManifestation.getMulti().getData().getFallThroughDisplayValue());
		indeterminateTextField.setName(bundle.getString("Indeterminate"));
		indeterminateTextField.getDocument().addDocumentListener(new IndeterminateValueListener());
		indeterminateTextField.setToolTipText(indeterminateTextField.getText());
		JLabel indeterminateLabel = new JLabel(bundle.getString("IndeterminateLabel"));
		
		GridBagConstraints indeterminateFieldConstraints = new GridBagConstraints();
		indeterminateFieldConstraints.fill = GridBagConstraints.NONE;
		indeterminateFieldConstraints.anchor = GridBagConstraints.LINE_START;
		indeterminateFieldConstraints.weighty = .25;
		indeterminateFieldConstraints.ipadx = 1;
		indeterminateFieldConstraints.gridx = 0;
		indeterminateFieldConstraints.gridy = 0;
		indeterminateInnerPanel.add(indeterminateLabel, indeterminateFieldConstraints);
		GridBagConstraints indeterminateFieldConstraints2 = new GridBagConstraints();
		indeterminateFieldConstraints2.fill = GridBagConstraints.NONE;
		indeterminateFieldConstraints2.anchor = GridBagConstraints.LINE_START;
		indeterminateFieldConstraints2.weighty = .25;
		indeterminateFieldConstraints2.ipadx = 1;
		indeterminateFieldConstraints2.gridx = 1;
		indeterminateFieldConstraints2.gridy = 0;
		indeterminateInnerPanel.add(indeterminateTextField, indeterminateFieldConstraints2);
		
		indeterminatePanel.add(indeterminateInnerPanel, BorderLayout.WEST);
		return indeterminatePanel;
	}
	
	/** Set the value of the rule result text field.
	 * @param result result of the rule execution
	 */
	public void setRuleResultField(String result) {
		ruleResultTextField.setText(result);
		ruleResultTextField.setToolTipText(ruleResultTextField.getText());
	}

	private class IndeterminateValueListener implements DocumentListener {
		public void insertUpdate(DocumentEvent e) {
			indeterminateTextField.setToolTipText(indeterminateTextField.getText());
			multiViewManifestation.getMulti().getData().setFallThroughDisplayValue(indeterminateTextField.getText());
			multiViewManifestation.fireFocusPersist();
		}

		public void removeUpdate(DocumentEvent e) {
			indeterminateTextField.setToolTipText(indeterminateTextField.getText());
			multiViewManifestation.getMulti().getData().setFallThroughDisplayValue(indeterminateTextField.getText());
			multiViewManifestation.fireFocusPersist();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
		}

	}

}
