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
@Cacheable
@Table(name = "view_state")
public class ViewState implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ViewStatePK viewStatePK;
    @Basic(optional = false)
    @Lob
    @Column(name = "view_info",length=Integer.MAX_VALUE)
    private String viewInfo;
    @JoinColumn(name = "component_id", referencedColumnName = "component_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private ComponentSpec componentSpec;

    public ViewState() {
    }

    public ViewStatePK getViewStatePK() {
        return viewStatePK;
    }

    public void setViewStatePK(ViewStatePK viewStatePK) {
        this.viewStatePK = viewStatePK;
    }

    public String getViewInfo() {
        return viewInfo;
    }

    public void setViewInfo(String viewInfo) {
        this.viewInfo = viewInfo;
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
        hash += (viewStatePK != null ? viewStatePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ViewState)) {
            return false;
        }
        ViewState other = (ViewState) object;
        if ((this.viewStatePK == null && other.viewStatePK != null) || (this.viewStatePK != null && !this.viewStatePK.equals(other.viewStatePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ViewState[ viewStatePK=" + viewStatePK + " "+ "viewInfo " + viewInfo + "]";
    }
    
}
