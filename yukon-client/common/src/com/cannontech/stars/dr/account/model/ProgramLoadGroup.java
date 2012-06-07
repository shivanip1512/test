package com.cannontech.stars.dr.account.model;

public class ProgramLoadGroup {

    private int paobjectId;
    private int lmGroupId;
    private int programId;
    private String programName;
    
    public ProgramLoadGroup() {
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName());
        sb.append(" Program Id " );
        sb.append(this.programId);
        sb.append(" Program Name " );
        sb.append(" PAObject Id " );
        sb.append(this.paobjectId);
        sb.append(" LMGroup Id " );
        sb.append(this.lmGroupId);
        String toString = sb.toString();
        return toString;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + paobjectId;
        result = PRIME * result + programId;
        result = PRIME * result + lmGroupId;
        result = PRIME * result + ((programName == null) ? 0 : programName.hashCode());
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
        final ProgramLoadGroup other = (ProgramLoadGroup) obj;
        if (paobjectId != other.paobjectId)
            return false;
        if (programName == null) {
            if (other.programName != null)
                return false;
        }
        if (programId != other.programId)
            return false;
        if (lmGroupId != other.lmGroupId)
            return false;
        return true;
    }

    public int getLmGroupId() {
        return lmGroupId;
    }

    public void setLmGroupId(int lmGroupId) {
        this.lmGroupId = lmGroupId;
    }

    public int getPaobjectId() {
        return paobjectId;
    }

    public void setPaobjectId(int paobjectId) {
        this.paobjectId = paobjectId;
    }

    public int getProgramId() {
        return programId;
    }

    public void setProgramId(int programId) {
        this.programId = programId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }
    
}
