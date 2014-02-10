package com.cannontech.jobs.model;

import java.util.Map;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.jobs.support.YukonTask;
import com.cannontech.user.YukonUserContext;

public class YukonJob {
    private Integer id;
    private String beanName;
    private boolean disabled;
    private boolean deleted;
    private Map<String, String> jobProperties;
    private YukonJobDefinition<? extends YukonTask> jobDefinition;
    private YukonUserContext userContext;

    /*
     * This is unfortunate. A byproduct of the PacifiCorp changes. We currently have no way
     * to determine whether a job read in from the database is assigned a "system" user other
     * than by knowing the job had null data in the user context columns of the job table.
     * This variable is meant to signify that state, and should be removed upon the completion
     * of YUK-13009, which will create a system user to be used for user-less jobs in Yukon.
     */
    private boolean isSystemUser;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void setUserContext(YukonUserContext userContext) {
        this.userContext = userContext;
    }

    public YukonUserContext getUserContext() {
        return userContext;
    }
    
    public void setSystemUser(boolean isSystemUser) {
        this.isSystemUser = isSystemUser;
    }
    
    public boolean isSystemUser() {
        return isSystemUser;
    }

    public Map<String, String> getJobProperties() {
        return jobProperties;
    }

    public void setJobProperties(Map<String, String> jobProperties) {
        this.jobProperties = jobProperties;
    }

    public YukonJobDefinition<? extends YukonTask> getJobDefinition() {
        return jobDefinition;
    }

    public void setJobDefinition(YukonJobDefinition<? extends YukonTask> jobDefinition) {
        this.jobDefinition = jobDefinition;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((id == null) ? 0 : id.hashCode());
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
        final YukonJob other = (YukonJob) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("id", id);
        tsc.append("beanName", beanName);
        tsc.append("disabled", disabled);
        tsc.append("userContext", userContext);
        tsc.append("jobProperties", jobProperties);
        return tsc.toString();
    }
}