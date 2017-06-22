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

/**
 *
 */
@Entity
@Table(name = "disciplines")
@NamedQueries({
    @NamedQuery(name = "Disciplines.findAll", query = "SELECT d FROM Disciplines d")})
public class Disciplines implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "discipline_id")
    private String disciplineId;
    @Column(name = "description")
    private String description;
    @Column(name = "program")
    private String program;
    @Basic(optional = false)
    @Column(name = "obj_version")
    private int objVersion;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "disciplineId")
    private Collection<MctUsers> mctUsersCollection;

    public Disciplines() {
    }

    public String getDisciplineId() {
        return disciplineId;
    }

    public void setDisciplineId(String disciplineId) {
        this.disciplineId = disciplineId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public int getObjVersion() {
        return objVersion;
    }

    public void setObjVersion(int objVersion) {
        this.objVersion = objVersion;
    }

    public Collection<MctUsers> getMctUsersCollection() {
        return mctUsersCollection;
    }

    public void setMctUsersCollection(Collection<MctUsers> mctUsersCollection) {
        this.mctUsersCollection = mctUsersCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (disciplineId != null ? disciplineId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Disciplines)) {
            return false;
        }
        Disciplines other = (Disciplines) object;
        if ((this.disciplineId == null && other.disciplineId != null) || (this.disciplineId != null && !this.disciplineId.equals(other.disciplineId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "test.Disciplines[ disciplineId=" + disciplineId + " ]";
    }
    
}
