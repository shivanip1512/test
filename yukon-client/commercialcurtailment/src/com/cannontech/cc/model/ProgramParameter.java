package com.cannontech.cc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "CCurtProgramParameter",
       uniqueConstraints=@UniqueConstraint(columnNames={"CCurtProgramId","parameterKey"}))
public class ProgramParameter {
    private Integer id;
    private Program program;
    private ProgramParameterKey parameterKey;
    private String parameterValue;
    
    
    @Id
    @GenericGenerator(name="yukon", strategy="com.cannontech.database.incrementer.HibernateIncrementer")
    @GeneratedValue(generator="yukon")
    @Column(name = "CCurtProgramParameterId")
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    @Column(nullable=false)
    public String getParameterValue() {
        return parameterValue;
    }
    
    public void setParameterValue(String value) {
        this.parameterValue = value;
    }

    @ManyToOne
    @JoinColumn(name="CCurtProgramId", nullable=false)
    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    public ProgramParameterKey getParameterKey() {
        return parameterKey;
    }

    public void setParameterKey(ProgramParameterKey parameterKey) {
        this.parameterKey = parameterKey;
    }

}
