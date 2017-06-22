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

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "tag_association")
@NamedQueries({
    @NamedQuery(name = "TagAssociation.findAll", query = "SELECT t FROM TagAssociation t"),
    @NamedQuery(name = "TagAssociation.getComponentsByTag", query="SELECT t FROM TagAssociation t WHERE t.tag.tagId = :tagId")})
public class TagAssociation implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TagAssociationPK tagAssociationPK;
    @Column(name = "tag_property",length=200)
    private String tagProperty;
    @JoinColumn(name = "tag_id", referencedColumnName = "tag_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Tag tag;
    @JoinColumn(name = "component_id", referencedColumnName = "component_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private ComponentSpec componentSpec;

    public TagAssociation() {
    }

    public TagAssociationPK getTagAssociationPK() {
        return tagAssociationPK;
    }

    public void setTagAssociationPK(TagAssociationPK tagAssociationPK) {
        this.tagAssociationPK = tagAssociationPK;
    }

    public String getTagProperty() {
        return tagProperty;
    }

    public void setTagProperty(String tagProperty) {
        this.tagProperty = tagProperty;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public ComponentSpec getComponentSpec() {
        return componentSpec;
    }

    public void setComponentSpec(ComponentSpec componentSpec) {
        this.componentSpec = componentSpec;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (tagAssociationPK != null ? tagAssociationPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TagAssociation)) {
            return false;
        }
        TagAssociation other = (TagAssociation) object;
        if ((this.tagAssociationPK == null && other.tagAssociationPK != null) || (this.tagAssociationPK != null && !this.tagAssociationPK.equals(other.tagAssociationPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "test.TagAssociation[ tagAssociationPK=" + tagAssociationPK + " ]";
    }
    
}
