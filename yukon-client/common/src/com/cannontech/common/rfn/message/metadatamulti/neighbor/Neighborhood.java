package com.cannontech.common.rfn.message.metadatamulti.neighbor;

import java.io.Serializable;
import java.util.Set;

import com.cannontech.common.rfn.message.RfnIdentifier;

public class Neighborhood implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private RfnIdentifier myRfnIdentifier;
    
    private Set<Neighbor> neighbors;

    public RfnIdentifier getMyRfnIdentifier() {
        return myRfnIdentifier;
    }

    public void setMyRfnIdentifier(RfnIdentifier myRfnIdentifier) {
        this.myRfnIdentifier = myRfnIdentifier;
    }

    public Set<Neighbor> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(Set<Neighbor> neighbors) {
        this.neighbors = neighbors;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((myRfnIdentifier == null) ? 0 : myRfnIdentifier.hashCode());
        result = prime * result + ((neighbors == null) ? 0 : neighbors.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Neighborhood other = (Neighborhood) obj;
        if (myRfnIdentifier == null) {
            if (other.myRfnIdentifier != null)
                return false;
        } else if (!myRfnIdentifier.equals(other.myRfnIdentifier))
            return false;
        if (neighbors == null) {
            if (other.neighbors != null)
                return false;
        } else if (!neighbors.equals(other.neighbors))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String
            .format("Neighborhood [myRfnIdentifier=%s, neighbors=%s]", myRfnIdentifier, neighbors);
    }
}
