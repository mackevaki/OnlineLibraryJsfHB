package entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
public class Groups implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "GROUPID", nullable = false, length = 20)
    private String groupid;

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Groups groups = (Groups) o;

        if (groupid != null ? !groupid.equals(groups.groupid) : groups.groupid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return groupid != null ? groupid.hashCode() : 0;
    }
}
