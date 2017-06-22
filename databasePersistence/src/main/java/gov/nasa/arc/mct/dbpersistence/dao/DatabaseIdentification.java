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

/**
 *
 * @author cbwebste
 */
@Entity
@Table(name = "database_identification")
@NamedQueries({
    @NamedQuery(name = "DatabaseIdentification.findAll", query = "SELECT d FROM DatabaseIdentification d"),
    @NamedQuery(name = "DatabaseIdentification.findSchemaId", query = "Select d FROM DatabaseIdentification d where d.name = 'schema_id'")})
public class DatabaseIdentification implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @Column(name = "value")
    private String value;
    @Basic(optional = false)
    @Column(name = "obj_version")
    private int objVersion;

    public DatabaseIdentification() {
    }

    public DatabaseIdentification(String name) {
        this.name = name;
    }

    public DatabaseIdentification(String name, String value, int objVersion) {
        this.name = name;
        this.value = value;
        this.objVersion = objVersion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getObjVersion() {
        return objVersion;
    }

    public void setObjVersion(int objVersion) {
        this.objVersion = objVersion;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (name != null ? name.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DatabaseIdentification)) {
            return false;
        }
        DatabaseIdentification other = (DatabaseIdentification) object;
        if ((this.name == null && other.name != null) || (this.name != null && !this.name.equals(other.name))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "test.DatabaseIdentification[ name=" + name + " ]";
    }
    
}
