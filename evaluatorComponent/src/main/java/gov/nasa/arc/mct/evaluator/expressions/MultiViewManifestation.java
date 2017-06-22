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
import gov.nasa.arc.mct.components.FeedProvider.RenderingInfo;
import gov.nasa.arc.mct.components.TimeConversion;
import gov.nasa.arc.mct.evaluator.api.Evaluator;
import gov.nasa.arc.mct.evaluator.component.MultiComponent;
import gov.nasa.arc.mct.evaluator.enums.MultiEvaluator;
import gov.nasa.arc.mct.evaluator.enums.MultiEvaluator.MultiExpression;
import gov.nasa.arc.mct.evaluator.enums.MultiEvaluator.MultiExpression.SetLogic;
import gov.nasa.arc.mct.gui.FeedView;
import gov.nasa.arc.mct.gui.SelectionProvider;
import gov.nasa.arc.mct.roles.events.AddChildEvent;
import gov.nasa.arc.mct.roles.events.RemoveChildEvent;
import gov.nasa.arc.mct.services.component.ViewInfo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a visible manifestation of the multi-input expression view role.
 */
@SuppressWarnings("serial")
public class MultiViewManifestation extends FeedView {
	private static final ResourceBundle bundle = ResourceBundle.getBundle("MultiComponent");
	private static final Logger logger = LoggerFactory.getLogger(MultiViewManifestation.class);
	/** Expression view role name. */
	public static final String VIEW_NAME = bundle.getString("MultiViewRoleName");

	private MultiRuleExpression selectedExpression;
	private MultiRuleExpressionList currentExpressions;
	private ArrayList<AbstractComponent> telemetryElements;
	private AbstractComponent selectedTelemetry;
	private final MultiComponent multiComponent;
	private MultiExpressionsFormattingControlsPanel controlPanel;
	private final AtomicReference<Collection<FeedProvider>> feedProvidersRef = new AtomicReference<Collection<FeedProvider>>(Collections.<FeedProvider>emptyList());
	private Map<String, TimeConversion> timeConversionMap = new HashMap<String, TimeConversion>();
	@SuppressWarnings("unused")
	private MultiRuleExpressionsTableModel expModel;
	/** The telemetry model. */
	public TelemetryModel telModel;
	private JTable expressionsTable;
	private JTable telemetryTable;
	private JPanel expressionsPanel;
	private JLabel resultOutput;
	private Border componentBorder = null;

	/**
	 * The multi-input expression view manifestation initialization.
	 * @param ac the component.
	 * @param vi the view info.
	 */
	public MultiViewManifestation(AbstractComponent ac, ViewInfo vi) { 
		super(ac,vi);
		this.multiComponent = getManifestedComponent().getCapability(MultiComponent.class);
		this.selectedExpression = new MultiRuleExpression();
		this.currentExpressions = new MultiRuleExpressionList(multiComponent.getData().getCode());
		this.telemetryElements = new ArrayList<AbstractComponent>();
		refreshTelemetry();
		updateFeedProviders();
		expressionsPanel = new JPanel();
		expressionsPanel.getAccessibleContext().setAccessibleName("Expressions");

		if (getColor("border") != null) {
			componentBorder = BorderFactory.createLineBorder(getColor("border"));
		}
		
		load();
		
		expressionsPanel.setAutoscrolls(true);
	}
	
	private Color getColor(String name) {
        return UIManager.getColor(name);        
    }
	
	/**
	 * Gets the multi-input evaluator component.
	 * @return the multi-input evaluator component.
	 */
	public MultiComponent getMulti() {
		return multiComponent;
	}
	
	/**
	 * Gets the multi-input expression list.
	 * @return the multi-input expression list.
	 */
	public MultiRuleExpressionList getExpressions(){
		return currentExpressions;
	}
	
	/**
	 * Gets the array list of telemetry components.
	 * @return array list of telemetry components.
	 */
	public ArrayList<AbstractComponent> getTelemetry(){
		return telemetryElements;
	}
	
	private void refreshTelemetry(){
		telemetryElements.clear();
		for (AbstractComponent component : getManifestedComponent().getComponents()){
			if (component.getCapability(FeedProvider.class) != null){
				telemetryElements.add(component);
			}
		}
	}
	
	/**
	 * Clear the list of multi-input expressions from the expressions table.
	 */
	public void refreshRuleExpressions(){
		((MultiRuleExpressionsTableModel)expressionsTable.getModel()).clearModel();
	}
	
	
	private void load() {			
		buildGUI();
	}
	
	@SuppressWarnings("unused")
	private boolean containsChildComponent(AbstractComponent parentComponent, String childComponentId) {
		for (AbstractComponent childComponent : parentComponent.getComponents()) {
			if (childComponentId.equals(childComponent.getComponentId())){
				return true;
			}
		}
		return false;
	}
	
	private void buildGUI() {
		//show associated telemetry element, table of expressions, test value
		setLayout(new GridBagLayout());
		
		//Input area for test value at top of view
		JLabel result = new JLabel("Result: ");
		resultOutput = new JLabel();
		
		
		//Add table of expressions
		expressionsTable = new JTable(expModel = new MultiRuleExpressionsTableModel());
		expressionsTable.setAutoCreateColumnsFromModel(true);
		expressionsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		expressionsTable.setRowSelectionAllowed(true);
		expressionsTable.getColumnModel().getColumn(0).setWidth(20);
		expressionsTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		expressionsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				controlPanel.loadRule();
			}
			
		});
		JScrollPane expressionsTableScrollPane = new JScrollPane(expressionsTable, 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		expressionsTableScrollPane.setPreferredSize(new Dimension(300, 150));
				
		GridBagConstraints titleConstraints = getConstraints(0,1);
		titleConstraints.gridwidth = 3;
		titleConstraints.fill = GridBagConstraints.HORIZONTAL;
		titleConstraints.gridheight = 1;
		titleConstraints.insets = new Insets(1,9,9,9);
		titleConstraints.weightx = 1;
		titleConstraints.weighty = .7;
		add(new JLabel(multiComponent.getDisplayName() + " " + bundle.getString("RuleTableTitleLabel")), titleConstraints);
		
		GridBagConstraints eTableConstraints = getConstraints(0,2);
		eTableConstraints.gridwidth = 3;
		eTableConstraints.fill = GridBagConstraints.HORIZONTAL;
		eTableConstraints.gridheight = 1;
		eTableConstraints.insets = new Insets(1,9,9,9);
		eTableConstraints.weightx = 1;
		eTableConstraints.weighty = .1;
		add(expressionsTableScrollPane, eTableConstraints);
		
		GridBagConstraints paramTableTitleConstraints = getConstraints(0,3);
		paramTableTitleConstraints.gridwidth = 3;
		paramTableTitleConstraints.fill = GridBagConstraints.HORIZONTAL;
		paramTableTitleConstraints.gridheight = 1;
		paramTableTitleConstraints.insets = new Insets(1,9,9,9);
		paramTableTitleConstraints.weightx = 1;
		paramTableTitleConstraints.weighty = .1;
		add(new JLabel(multiComponent.getDisplayName() + " " + bundle.getString("ParamTableTitleLabel")), paramTableTitleConstraints);
		
		
		//Add table of telemetry elements
		telemetryTable = new JTable(telModel = new TelemetryModel());
		telemetryTable.setAutoCreateColumnsFromModel(true);
		telemetryTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		telemetryTable.setRowSelectionAllowed(true);
		telemetryTable.setAutoCreateRowSorter(true);
		DefaultTableCellRenderer renderer = new
				DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.CENTER);
		TableColumn tc = telemetryTable.getColumn(bundle.getString("currentValueLabel"));
		tc.setCellRenderer(renderer);
		JScrollPane telemetryTableScrollPane = new JScrollPane(telemetryTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		telemetryTableScrollPane.setPreferredSize(new Dimension(300, 150));
		
		if (componentBorder != null) {
			expressionsTableScrollPane.setBorder(componentBorder);
			telemetryTableScrollPane.setBorder(componentBorder);
		}
		
		GridBagConstraints tTableConstraints = getConstraints(0,7);
		tTableConstraints.gridwidth = 3;
		tTableConstraints.gridheight = 1;
		tTableConstraints.insets = new Insets(1,9,9,9);
		tTableConstraints.weightx = 1;
		tTableConstraints.weighty = .3;
		tTableConstraints.anchor = GridBagConstraints.WEST;
		tTableConstraints.fill = GridBagConstraints.HORIZONTAL;
		add(telemetryTableScrollPane, tTableConstraints);
	
		//Add results
		
		GridBagConstraints rConstraints = getConstraints(0,12);
		rConstraints.insets = new Insets(3,9,5,1);
		add(result, rConstraints);
		
		GridBagConstraints rOutputConstraints = getConstraints(1,12);
		rOutputConstraints.insets = new Insets (3,1,5,9);
		add(resultOutput, rOutputConstraints);
	}
	
	private GridBagConstraints getConstraints(int x, int y) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.weightx = x == 0 ? 0 : 1;
		gbc.weighty = 0;
		gbc.insets = new Insets(0,9,4,9);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		return gbc;
	}
	
	/**
	 * Evaluates the result.
	 * @param value the string value.
	 * @return the evaluated result.
	 */
	public String evaluate(String value){
		MultiEvaluator e = new MultiEvaluator();
		e.compile(multiComponent.getData().getCode());
		return null;
	}
	
	/**
	 * Expressions table model.
	 */
	@SuppressWarnings("unchecked")
	public class MultiRuleExpressionsTableModel extends AbstractTableModel {
		private MultiRuleExpressionList eList = currentExpressions;
		private List<String> numbers = new ArrayList<String>();
		private List<String> rulesNames = new ArrayList<String>();
		private List<String> displayedStrings = new ArrayList<String>();
		
		private List<List<String>> lists = Arrays.asList(numbers, rulesNames, displayedStrings);
		private final List<String> columnNames = new ArrayList<String>();
		
		/**
		 * Initializes the expression model.
		 */
		MultiRuleExpressionsTableModel(){
			columnNames.add(bundle.getString("NumberLabel"));
			columnNames.add(bundle.getString("RuleName"));
			columnNames.add(bundle.getString("DisplayedString"));
			loadExpressions();
		}
		
		@Override
		public int getColumnCount() {
			return columnNames.size();
		}
		
		@Override
		public int getRowCount() {
			return eList.size();
		}
		
		@Override
		public boolean isCellEditable(int row, int col) {
			return false;
		}
		
		@Override 
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}
		
		@Override
		public String getColumnName(int column) {
			return columnNames.get(column);
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex){
			List<String> list = getListForColumn(columnIndex);
			String value = list.get(rowIndex);
			return columnIndex == 3 ? Double.valueOf(value) : value;
		}
	
		private List<String> getListForColumn(int col){
			return lists.get(col);
		}
		
		@Override
		public void setValueAt(Object value, int row, int col) {
			if (col==1 && value != null){
				eList.getExp(row).setName(value.toString());
			}
			if (col==2 && value != null){
				eList.getExp(row).setDisplay(value.toString());
			}
			multiComponent.getData().setCode(eList.toString());
			fireTableCellUpdated(row, col);
			fireFocusPersist();
		}	
		
		private void loadExpressions(){
			MultiRuleExpression e;
			for (int i = 0; i < eList.size(); i++){
				e = eList.getExp(i);
				numbers.add(Integer.valueOf(i+1).toString());
				rulesNames.add(e.getName());
				displayedStrings.add(e.getDisplay());
			}
		}
		
		/**
		 * Clears the model.
		 */
		public void clearModel() {
			numbers.clear();
			rulesNames.clear();
			displayedStrings.clear();
			loadExpressions();
			fireTableDataChanged();
		}
	}
	
	/**
	 * Telemetry model.
	 */
	@SuppressWarnings("unchecked")
	class TelemetryModel extends AbstractTableModel{
		private List<String> pui = new ArrayList<String>();
		private List<String> baseDisplay = new ArrayList<String>();
		private List<String> currentValues = new ArrayList<String>();
		private List<List<String>> lists = Arrays.asList(pui, baseDisplay, currentValues);
		private final List<String> columnNames = new ArrayList<String>();
		
		/**
		 * Initializes the telemetry model.
		 */
		TelemetryModel(){
			columnNames.add(bundle.getString("PuiLabel"));
			columnNames.add(bundle.getString("BaseDisplayLabel"));
			columnNames.add(bundle.getString("currentValueLabel"));
			loadTelemetry();
		}
		
		@Override
		public int getColumnCount() {
			return columnNames.size();
		}
		
		@Override
		public int getRowCount() {
			return telemetryElements.size();
		}
		
		@Override
		public boolean isCellEditable(int row, int col) {
			return false;
		}
		
		@Override 
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}
		
		@Override
		public String getColumnName(int column) {
			return columnNames.get(column);
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex){
			List<String> list = getListForColumn(columnIndex);
			if (list.size() == 0) {
				return "";
			}
			return list.get(rowIndex);
		}
	
		private List<String> getListForColumn(int col){
			return lists.get(col);
		}
		
		/**
		 * Loads the telemetry.
		 */
		protected void loadTelemetry(){
			refreshTelemetry();
			for (AbstractComponent ac : telemetryElements){
				pui.add(ac.getExternalKey());
				baseDisplay.add(ac.getDisplayName());
				currentValues.add("");
			}
		}
		
		@Override
		public void setValueAt(Object value, int row, int col) {
			if (col==2 && value != null) {
				currentValues.set(row, value.toString());
			}
			fireTableCellUpdated(row, col);
		}
		
		/**
		 * Clears the telemetry model.
		 */
		public void clearModel() {
			pui.clear();
			baseDisplay.clear();
			currentValues.clear();
			loadTelemetry();
			controlPanel.updatePassThroughControl();
			fireTableDataChanged();
		}
		
	}
	
	@Override
	protected JComponent initializeControlManifestation() {
		//Set canvas control
		this.controlPanel = new MultiExpressionsFormattingControlsPanel(this);
		Dimension d = controlPanel.getMinimumSize();
		d.setSize(0,0);
		controlPanel.setMinimumSize(d);
		
		JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		pane.setOneTouchExpandable(true);
		pane.setBorder(BorderFactory.createEmptyBorder());
		JScrollPane controlScrollPane = new JScrollPane(controlPanel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		return controlScrollPane;
	}			
	
	/**
	 * Gets the selected multi-input expression.
	 * @return the multi-input expression.
	 */
	public MultiRuleExpression getSelectedExpression(){
		int rows = expressionsTable.getSelectedRowCount();
		if (rows == 1){
			selectedExpression = currentExpressions.getExp(expressionsTable.getSelectedRow());
		}
		else {
			selectedExpression = null;
		}
		return selectedExpression;
	}
	
	/**
	 * Sets the selected expression.
	 * @param exp the expression to set
	 */
	public void setSelectedExpression(MultiRuleExpression exp){
		int index = getExpressions().indexOf(exp);
		expressionsTable.getSelectionModel().setSelectionInterval(index, index);
		selectedExpression = exp;
	}
	
	/**
	 * Gets the selected telemetry.
	 * @return the component.
	 */
	public AbstractComponent getSelectedTelemetry(){
		int row = telemetryTable.getSelectedRowCount();
		if (row == 1){
			selectedTelemetry = telemetryElements.get(telemetryTable.getSelectedRow());
		}
		else {
			selectedTelemetry = null;
		}
		return selectedTelemetry;
	}
	
	/**
	 * Saves the manifested component.
	 */
	public void fireFocusPersist(){
		if (!isLocked()) {
			getManifestedComponent().save();
		}
	}
	
	/**
	 * Fires the selection property changes.
	 */
	public void fireManifestationChanged() {
		firePropertyChange(SelectionProvider.SELECTION_CHANGED_PROP, null, getSelectedExpression());
	}
	
	@Override
	public void updateMonitoredGUI(){
		((MultiRuleExpressionsTableModel)expressionsTable.getModel()).clearModel();
		((TelemetryModel)telemetryTable.getModel()).clearModel();
		controlPanel.loadRuleButtonSettings();
		updateFeedProviders();
	}
	
	@Override
	public void updateMonitoredGUI(AddChildEvent event) {
		((TelemetryModel)telemetryTable.getModel()).clearModel();
		controlPanel.loadRuleButtonSettings();
		updateFeedProviders();
	}
	
	@Override
	public void updateMonitoredGUI(RemoveChildEvent event) {
		((TelemetryModel)telemetryTable.getModel()).clearModel();
		controlPanel.loadRuleButtonSettings();
		updateFeedProviders();
	}
	
	private List<FeedProvider> getFeedProviders(AbstractComponent component) {

		List<FeedProvider> feedProviders = new ArrayList<FeedProvider>(
				component.getComponents().size());
		for (AbstractComponent referencedComponent : component.getComponents()) {
			FeedProvider fp = referencedComponent.getCapability(
					FeedProvider.class);
			if (fp != null) {
				feedProviders.add(fp);
			}
		}
		return feedProviders;
	}
	
	private void updateFeedProviders() {
		ArrayList<FeedProvider> feedProviders = new ArrayList<FeedProvider>();
		timeConversionMap.clear();
		for (AbstractComponent component : telemetryElements) {
			if (component != null) {
				FeedProvider fp = getFeedProvider(component);
				if (fp != null) {
					feedProviders.add(fp);
					TimeConversion tc = component.getCapability(TimeConversion.class);
					if (tc != null) {
						timeConversionMap.put(fp.getSubscriptionId(), tc);
					}							
				} else {
					if (component.getCapability(Evaluator.class) != null) {
						for (AbstractComponent referencedComponent : component.getComponents()) {
							fp = getFeedProvider(referencedComponent);
							if (fp != null) {
								feedProviders.add(fp);
							}
						}
					}
				}
			}
		}
		feedProviders.trimToSize();
		feedProvidersRef.set(feedProviders);
	}

	@Override
	public void updateFromFeed(Map<String, List<Map<String, String>>> data) {
		if (data != null) {
			Collection<FeedProvider> feeds = getVisibleFeedProviders();
			Map<String,Double> valuesMap = new HashMap<String,Double>();
			for (FeedProvider provider : feeds) {
				String feedId = provider.getSubscriptionId();
				List<Map<String, String>> dataForThisFeed = data
						.get(feedId);
				if (dataForThisFeed != null && !dataForThisFeed.isEmpty()) {
					// Process the first value for this feed.
					Map<String, String> entry = dataForThisFeed
							.get(dataForThisFeed.size() - 1);

					try {
						RenderingInfo ri = provider.getRenderingInfo(entry);
						Object value = ri.getValueText();
						valuesMap.put(feedId,Double.valueOf(ri.getValueText()));;
						for (AbstractComponent parameter : telemetryElements) {
							FeedProvider fp = parameter.getCapability(FeedProvider.class);
							if (fp != null && feedId.equals(fp.getSubscriptionId())) {
								telemetryTable.getModel().setValueAt(value, telemetryElements.indexOf(parameter), 2);
							}
						}
					} catch (ClassCastException ex) {
						logger.error("Feed data entry of unexpected type",
								ex);
					} catch (NumberFormatException ex) {
						logger.error(
								"Feed data entry does not contain parsable value",
								ex);
					}
				}
			}
			//Update Multi Result in Manifestation
			FeedProvider.RenderingInfo multiValueInfo = getMulti().
					getCapability(Evaluator.class).evaluate(data,
							getFeedProviders(getMulti()));
			if (multiValueInfo != null) {
				resultOutput.setText(multiValueInfo.getValueText() + multiValueInfo.getStatusText());
				resultOutput.setForeground(multiValueInfo.getValueColor());
			} else {
				resultOutput.setText("");
			}
			
			// Update SingleRule Result in control panel
			if (getSelectedExpression() != null) {
				MultiExpression expression = new MultiExpression(Enum.valueOf(SetLogic.class, 
						getSelectedExpression().getMultiSetLogic().name()), 
						getSelectedExpression().getPUIs().split(MultiRuleExpression.parameterDelimiter, 0), 
						getSelectedExpression().getSinglePui(), getSelectedExpression().getOperator(), 
						getSelectedExpression().getVal().toString(), getSelectedExpression().getDisplay());
				String ruleResult = expression.execute(valuesMap);
				if (ruleResult != null) {
					controlPanel.setRuleResultField(ruleResult);
				} else {
					controlPanel.setRuleResultField("");
				}
			}
		}
	}

	@Override
	public void synchronizeTime(Map<String, List<Map<String, String>>> data,
			long syncTime) {
		updateFromFeed(data);
	}

	@Override
	public Collection<FeedProvider> getVisibleFeedProviders() {
		return feedProvidersRef.get();
	}
}