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

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.components.JAXBModelStatePersistence;
import gov.nasa.arc.mct.components.ModelStatePersistence;
import gov.nasa.arc.mct.evaluator.api.Evaluator;
import gov.nasa.arc.mct.evaluator.api.Executor;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.LoggerFactory;

/**
 * The <code>MultiComponent</code> class defines an evaluator requiring multiple inputs. 
 * The evaluator stores code and references
 * other components (that are feed providers) to determine appropriate usage. 
 *
 */
public class MultiComponent extends AbstractComponent {
	private final AtomicReference<MultiModelRole> model = new AtomicReference<MultiModelRole>(new MultiModelRole());
	
	
	/** Get the model for the data associated with this component
	 * @return model a MultiModelRole
	 */
	public MultiModelRole getModel() {
		return model.get();
	}
	
	/** Get the data associated with this component
	 * @return a MultiData object
	 */
	public MultiData getData() {
		return getModel().getData();
	}
	
	/** Get the base display name for this component
	 * @return
	 */
	public String getDisplay(){
		return this.getDisplayName();
	}
	
	
	@Override
	protected <T> T handleGetCapability(Class<T> capability) {
		if (MultiComponent.class.isAssignableFrom(capability)) {
			return capability.cast(this);
		}
		if (Evaluator.class.isAssignableFrom(capability)) {
			Evaluator e = new Evaluator() {

				@Override
				public String getCode() {
					return getData().getCode();
				}

				@Override
				public String getDisplayName() {
					return getDisplay();
				}

				@Override
				public String getLanguage() {
					return getData().getLanguage();
				}

				public boolean requiresMultipleInputs() {
					return getExecutor() != null && getExecutor().requiresMultipleInputs();
				}

				private Executor getExecutor() {
					Executor e = executor.get();
					if (e == null) {
						executor.set(e = MultiProviderRegistry.getExecutor(MultiComponent.this));
					}
					
					return e;
				}
				
				private final AtomicReference<Executor> executor = new AtomicReference<Executor>();
				
				@Override
				public FeedProvider.RenderingInfo evaluate(
						Map<String, List<Map<String, String>>> data,
						List<FeedProvider> providers) {
					Executor e = getExecutor();
					
					FeedProvider.RenderingInfo ri = null;
					if (e != null) {
						if (getData().isPassThrough()) {
							for (FeedProvider fp : providers) {
								if (fp.getSubscriptionId().equals(getData().getPassThroughParameterId())) {
									List<Map<String,String>> values = data.get(getData().getPassThroughParameterId());
									if (values != null && !values.isEmpty()) {
										ri = fp.getRenderingInfo(values.get(values.size() - 1));
									}
								}
							}
						} else {
							ri = e.evaluate(data, providers);
							if (ri == null) {
								ri = new FeedProvider.RenderingInfo(getData().getFallThroughDisplayValue(), Color.red, "", Color.red, false);
							}
						}
					} else {
						LoggerFactory.getLogger(MultiComponent.class).error("no evaluator provider available for " + getLanguage());
						ri = new FeedProvider.RenderingInfo("", Color.red, "", Color.red,false);
					}
					
					return ri;
				}
			};
			
			return capability.cast(e);
		}
		
		if (ModelStatePersistence.class.isAssignableFrom(capability)) {
		    JAXBModelStatePersistence<MultiModelRole> persistence = new JAXBModelStatePersistence<MultiModelRole>() {

				@Override
				protected MultiModelRole getStateToPersist() {
					return model.get();
				}

				@Override
				protected void setPersistentState(MultiModelRole modelState) {
					model.set(modelState);
				}

				@Override
				protected Class<MultiModelRole> getJAXBClass() {
					return MultiModelRole.class;
				}
		        
			};
			
			return capability.cast(persistence);
		}
		
		return super.handleGetCapability(capability);
	}
}
