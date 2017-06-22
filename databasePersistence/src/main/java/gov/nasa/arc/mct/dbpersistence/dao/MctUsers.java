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
@Table(name = "mct_users")
@NamedQueries({
    @NamedQuery(name = "MctUsers.findAll", query = "SELECT m FROM MctUsers m"),
    @NamedQuery(name = "MctUsers.findByGroup", query = "SELECT m FROM MctUsers m where m.disciplineId.disciplineId = :group")})
public class MctUsers implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "user_id")
    private String userId;
    @Column(name = "firstname")
    private String firstname;
    @Column(name = "lastname")
    private String lastname;
    @Basic(optional = false)
    @Column(name = "obj_version")
    private int objVersion;
    @JoinColumn(name = "discipline_id", referencedColumnName = "discipline_id")
    @ManyToOne(optional = false)
    private Disciplines disciplineId;

    public MctUsers() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getObjVersion() {
        return objVersion;
    }

    public void setObjVersion(int objVersion) {
        this.objVersion = objVersion;
    }

    public Disciplines getDisciplineId() {
        return disciplineId;
    }

    public void setDisciplineId(Disciplines disciplineId) {
        this.disciplineId = disciplineId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userId != null ? userId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MctUsers)) {
            return false;
        }
        MctUsers other = (MctUsers) object;
        if ((this.userId == null && other.userId != null) || (this.userId != null && !this.userId.equals(other.userId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "test.MctUsers[ userId=" + userId + " ]";
    }
    
}
