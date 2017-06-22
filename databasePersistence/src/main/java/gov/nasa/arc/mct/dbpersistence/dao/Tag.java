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
import java.util.Collection;
import javax.persistence.*;

@Entity
@Table(name = "tag")
@NamedQueries({
    @NamedQuery(name = "Tag.findAll", query = "SELECT t FROM Tag t")})
public class Tag implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "tag_id",length=100)
    private String tagId;
    @Column(name = "tag_property",length=200)
    private String tagProperty;
    @Basic(optional = false)
    @Column(name = "obj_version")
    private int objVersion;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tag")
    private Collection<TagAssociation> tagAssociationCollection;

    public Tag() {
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getTagProperty() {
        return tagProperty;
    }

    public void setTagProperty(String tagProperty) {
        this.tagProperty = tagProperty;
    }

    public int getObjVersion() {
        return objVersion;
    }

    public void setObjVersion(int objVersion) {
        this.objVersion = objVersion;
    }

    public Collection<TagAssociation> getTagAssociationCollection() {
        return tagAssociationCollection;
    }

    public void setTagAssociationCollection(Collection<TagAssociation> tagAssociationCollection) {
        this.tagAssociationCollection = tagAssociationCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (tagId != null ? tagId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tag)) {
            return false;
        }
        Tag other = (Tag) object;
        if ((this.tagId == null && other.tagId != null) || (this.tagId != null && !this.tagId.equals(other.tagId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "test.Tag[ tagId=" + tagId + " ]";
    }
    
}
