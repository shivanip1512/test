package com.cannontech.jobs.model;

import java.util.Map;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.jobs.support.YukonTask;

public class YukonJob {
    private Integer id;
    private String beanName;
    private boolean disabled;
    private LiteYukonUser runAsUser;
    private Map<String,String> jobProperties;
    private YukonJobDefinition<? extends YukonTask> jobDefinition;


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
    public LiteYukonUser getRunAsUser() {
        return runAsUser;
    }
    public void setRunAsUser(LiteYukonUser runAsUser) {
        this.runAsUser = runAsUser;
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
        tsc.append("runAsUser", runAsUser);
        tsc.append("jobProperties", jobProperties);
        return tsc.toString();
    }
}