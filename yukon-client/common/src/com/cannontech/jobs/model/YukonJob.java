package com.cannontech.jobs.model;

import java.util.Map;

import org.apache.commons.lang3.builder.StandardToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;

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
    private Integer jobGroupId;

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

    /**
     * Another workaround until a true system user is created. Calling this method with a
     * null value indicates that the job is a system job, and sets the <code>isSystemUser</code>
     * variable to true. This method should never be called using {@link YukonUserContext#system} 
     * as a parameter, because it will be possible to incorrectly set the state of the 
     * <code>isSystemUser</code> variable in the job. Because of all this, a job may have a user
     * context with a userId equal to the yukon userid for two reasons; either the job is actually
     * owned by the yukon user (in which case the <code>isSystemUser</code> variable should be false)
     * or the job is a system job and the <code>isSystemUser</code> variable should be set to true.
     */
    public void setUserContext(YukonUserContext userContext) {
        this.userContext = userContext;

        if (userContext == null) {
            isSystemUser = true;
            this.userContext = YukonUserContext.system;
        }
    }

    public YukonUserContext getUserContext() {
        return userContext;
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
    
    public Integer getJobGroupId() {
        return jobGroupId;
    }
    
    public void setJobGroupId(Integer jobGroupId) {
        this.jobGroupId = jobGroupId;
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
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final YukonJob other = (YukonJob) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (!jobGroupId.equals(other.jobGroupId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StandardToStringStyle style = new StandardToStringStyle();
        style.setFieldSeparator(", ");
        style.setUseShortClassName(true);
        ToStringBuilder tsc = new ToStringBuilder(this, style);
        tsc.append("jobId", id);
        tsc.append("jobGroupId", jobGroupId);
        tsc.append("beanName", beanName);
        tsc.append("disabled", disabled);
        tsc.append("user", userContext.getYukonUser().getUsername());
        tsc.append("jobProperties", jobProperties);
        tsc.append("isSystemUser", isSystemUser);
        return tsc.toString();
    }
}