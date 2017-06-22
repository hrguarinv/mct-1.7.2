/*******************************************************************************
 * Mission Control Technologies, Copyright (c) 2009-2012, United States Government
 * as represented by the Administrator of the National Aeronautics and Space 
 * Administration. All rights reserved.
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
package gov.nasa.arc.mct.dbpersistence.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.OptimisticLockException;
import javax.persistence.Persistence;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestComponentSpec {
	private EntityManagerFactory factory;
	private EntityManager em;
	
	@BeforeMethod
	protected void setup() {
		Map<String,String> properties = new HashMap<String,String>();
		properties.put("javax.persistence.jdbc.driver","org.apache.derby.jdbc.EmbeddedDriver");
		properties.put("javax.persistence.jdbc.url","jdbc:derby:memory:testdb;create=true");
		properties.put("hibernate.hbm2ddl.auto","create");
		properties.put("hibernate.show_sql" ,"false"); 
		properties.put("hibernate.format_sql", "false");
		
		factory = Persistence.createEntityManagerFactory("MissionControlTechnologies", properties);
		em = factory.createEntityManager();
	}
	

	private ComponentSpec createComponentSpec(String id, String userId, String name, String type, String owner, List<Tag> tags, Map<String,String> views) {
		ComponentSpec cs = new ComponentSpec();
		cs.setComponentId(id);
		cs.setCreatorUserId(userId);
		cs.setComponentName(name);
		cs.setComponentType(type);
		cs.setOwner(owner);
		cs.setReferencedComponents(new ArrayList<ComponentSpec>());
		List<ViewState> viewStates = new ArrayList<ViewState>();
		for (Entry<String,String> e:views.entrySet()) {
			ViewState vs = new ViewState();
			ViewStatePK viewStatePK = new ViewStatePK();
			viewStatePK.setComponentId(cs.getComponentId());
			viewStatePK.setViewType(e.getKey());
			vs.setViewStatePK(viewStatePK);
			vs.setViewInfo(e.getValue());
			viewStates.add(vs);
		}
		cs.setViewStateCollection(viewStates);
		
		cs.setTagAssociationCollection(new ArrayList<TagAssociation>());
		for (Tag aTag:tags) {
			TagAssociation association = new TagAssociation();
			TagAssociationPK tagPK = new TagAssociationPK();
			tagPK.setComponentId(cs.getComponentId());
			tagPK.setTagId(aTag.getTagId());
			association.setTagAssociationPK(tagPK);
			association.setTagProperty(aTag.getTagProperty());
			cs.getTagAssociationCollection().add(association);
		}
		
		return cs;
	}
	
	private void checkComponentSpec(ComponentSpec cs, String owner, String type, String creator, String name, Collection<Tag> tags, Map<String,String> views) {
		Assert.assertEquals(owner, cs.getOwner());
		Assert.assertEquals(type, cs.getComponentType());
		Assert.assertEquals(creator, cs.getCreatorUserId());
		Assert.assertEquals(name, cs.getComponentName());
		Assert.assertNotNull(cs.getDateCreated());
		Assert.assertNotNull(cs.getLastModified());
		Assert.assertEquals(cs.getViewStateCollection().size(), views.size());
		for (Entry<String,String> e: views.entrySet()) {
			Assert.assertTrue(findViewState(cs.getViewStateCollection(), e.getKey(), e.getValue()));
		}
		Assert.assertEquals(cs.getTagAssociationCollection().size(), tags.size());
		for (Tag t: tags) {
			Assert.assertTrue(findTags(cs.getTagAssociationCollection(), t));
		}
	}
	
	private boolean findTags(Collection<TagAssociation> tags, Tag expectedTag) {
		for (TagAssociation tag: tags) {
			if (tag.getTag().getTagId().equals(expectedTag.getTagId())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean findViewState(Collection<ViewState> states, String viewType, String viewValue) {
		for (ViewState state: states) {
			if (state.getViewStatePK().getViewType().equals(viewType) && state.getViewInfo().equals(viewValue)) {
				return true;
			}
		}
		return false;
	}
	
	@Test(expectedExceptions=OptimisticLockException.class)
	public void testOptimisticLockException() {
		ComponentSpec cs = createComponentSpec(Long.toString(1), "chris", "cs", "type1", "chris",Collections.<Tag>emptyList(), Collections.<String,String>emptyMap());
		em.getTransaction().begin();
		em.persist(cs);
		em.getTransaction().commit();
		em.clear();
		Assert.assertFalse(em.contains(cs));
		em.getTransaction().begin();
		ComponentSpec orig = em.find(ComponentSpec.class,Long.toString(1));
		orig.setComponentName("revisedName");
		em.getTransaction().commit();
		em.clear();
		ComponentSpec current = em.find(ComponentSpec.class,Long.toString(1));
		Assert.assertEquals(current.getObjVersion(),1);
		Assert.assertEquals(cs.getObjVersion(),0);
		em.getTransaction().begin();
		em.merge(cs);
		em.getTransaction().commit(); // optimistic lock exception should be thrown as there has been an intervening commit
	}
	
	@Test
	public void testComponentSpec() {
		em.getTransaction().begin();
		long time = System.currentTimeMillis();
		final Map<String,String> initialViews = Collections.singletonMap("v1", "v1value");
		Tag t1 = new Tag();
		t1.setTagId("tag1");
		Tag t2 = new Tag();
		t2.setTagId("tag2");
		
		final List<Tag> tags = Arrays.asList(t1, t2);
		for (Tag t:tags) {
			em.persist(t);
		}
		ComponentSpec cs = createComponentSpec(Long.toString(time), "chris", "cs", "type1", "chris",tags, initialViews);
		em.persist(cs);
		
		ComponentSpec cs1 = createComponentSpec(Long.toString(time+1), "chris", "cs1", "type2", "chris", Collections.<Tag>emptyList(), initialViews);
		em.persist(cs1);
		cs.getReferencedComponents().add(cs1);
		em.getTransaction().commit();
		em.clear();
		
		cs = em.find(ComponentSpec.class, Long.toString(time));
		checkComponentSpec(cs,"chris","type1", "chris", "cs", tags, initialViews);
		Assert.assertEquals(cs.getReferencedComponents().size(), 1);
		Assert.assertEquals(cs.getReferencedComponents().iterator().next().getComponentId(), Long.toString(time+1));
		checkComponentSpec(cs.getReferencedComponents().iterator().next(), "chris", "type2", "chris", "cs1",Collections.<Tag>emptyList(), initialViews);
		
		em.getTransaction().begin();
		ComponentSpec cs2 = createComponentSpec(Long.toString(time+2), "chris", "cs2", "type3", "chris", Collections.<Tag>emptyList(), Collections.<String,String>emptyMap());
		cs = em.find(ComponentSpec.class, Long.toString(time));
		cs.getReferencedComponents().add(0, cs2);
		em.getTransaction().commit();
		em.clear();
		
		// check cascade persist and relationship ordering
		Assert.assertNotNull(em.find(ComponentSpec.class, Long.toString(time+2)));
		cs = em.find(ComponentSpec.class, Long.toString(time));
		Assert.assertEquals(cs.getReferencedComponents().size(), 2);
		Iterator<ComponentSpec> it = cs.getReferencedComponents().iterator();
		Assert.assertEquals(it.next().getComponentId(), Long.toString(time+2));
		Assert.assertEquals(it.next().getComponentId(), Long.toString(time+1));

	}
	
	@AfterMethod
	protected void tearDown() {
		em.clear();
		factory.close();
	}
}
