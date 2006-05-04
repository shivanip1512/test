package com.cannontech.cc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="CCurtProgramTypeParameter")
public class ProgramTypeParameter {
    private Integer id;
    private ProgramType programType;
    private String parameterName;
    private String parameterDisplay;
    private String parameterValueDefault;
    
    
    @Id
    @GenericGenerator(name="yukon", strategy="com.cannontech.database.incrementer.HibernateIncrementer")
    @GeneratedValue(generator="yukon")
    @Column(name = "CCurtProgramTypeParameterId")
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    @Column(nullable=false)
    public String getParameterName() {
        return parameterName;
    }
    
    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }
    
    @Column(nullable=false)
    public String getParameterValueDefault() {
        return parameterValueDefault;
    }
    
    public void setParameterValueDefault(String parameterValueDefault) {
        this.parameterValueDefault = parameterValueDefault;
    }
    
    @ManyToOne
    @JoinColumn(name="CCurtProgramTypeId", nullable=false)
    public ProgramType getProgramType() {
        return programType;
    }
    
    public void setProgramType(ProgramType programType) {
        this.programType = programType;
    }

    @Column(nullable=false)
    public String getParameterDisplay() {
        return parameterDisplay;
    }

    public void setParameterDisplay(String parameterDisplay) {
        this.parameterDisplay = parameterDisplay;
    }

}
