package com.cannontech.cc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "CCurtEEParticipantSelection",
       uniqueConstraints=@UniqueConstraint(columnNames={"CCurtEEParticipantId","CCurtEEPricingId"}))
public class EconomicEventParticipantSelection {
    public enum SelectionState {DEFAULT, MANUAL};
    private String connectionAudit;
    private Integer id;
    private EconomicEventParticipant participant;
    private EconomicEventPricing pricingRevision;
    private Date submitTime;
    private SelectionState state;
    private Set<EconomicEventParticipantSelectionWindow> selectionWindows = 
        new HashSet<EconomicEventParticipantSelectionWindow>();

    public EconomicEventParticipantSelection() {
    }
    
    @Column(nullable=false)
    public String getConnectionAudit() {
        return connectionAudit;
    }
    
    @Id
    @GenericGenerator(name="yukon", strategy="com.cannontech.database.incrementer.HibernateIncrementer")
    @GeneratedValue(generator="yukon")
    @Column(name = "CCurtEEParticipantSelectionId")
    public Integer getId() {
        return id;
    }

    @ManyToOne
    @JoinColumn(name="CCurtEEParticipantId", nullable=false)
    public EconomicEventParticipant getParticipant() {
        return participant;
    }

    @ManyToOne
    @JoinColumn(name="CCurtEEPricingId", nullable=false)
    public EconomicEventPricing getPricingRevision() {
        return pricingRevision;
    }

    @Column(nullable=false)
    public Date getSubmitTime() {
        return submitTime;
    }
    
    @OneToMany(mappedBy="selection", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    public Set<EconomicEventParticipantSelectionWindow> getSelectionWindows() {
        return selectionWindows;
    }
    
    @Transient
    public List<EconomicEventParticipantSelectionWindow> getSortedSelectionWindows() {
        ArrayList<EconomicEventParticipantSelectionWindow> temp = 
            new ArrayList<EconomicEventParticipantSelectionWindow>(selectionWindows);
        Collections.sort(temp); // there aren't many and they'll usually be sorted
        return temp;
    }
    
    public void setSelectionWindows(Set<EconomicEventParticipantSelectionWindow> selectionWindows) {
        this.selectionWindows = selectionWindows;
    }
    
    @Transient
    public EconomicEventParticipantSelectionWindow 
    getSelectionWindow(EconomicEventPricingWindow pricingWindow) {
        for (EconomicEventParticipantSelectionWindow selectionWindow : selectionWindows) {
            if (selectionWindow.getWindow().equals(pricingWindow)) {
                return selectionWindow;
            }
        }
        throw new IllegalArgumentException(this + " does not have data for revision " + pricingWindow);
    }

    public void setConnectionAudit(String connectionAudit) {
        this.connectionAudit = connectionAudit;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setParticipant(EconomicEventParticipant participant) {
        this.participant = participant;
    }

    public void setPricingRevision(EconomicEventPricing pricingRevision) {
        this.pricingRevision = pricingRevision;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public void addWindow(EconomicEventParticipantSelectionWindow nextSelectionWindow) {
        nextSelectionWindow.setSelection(this);
        selectionWindows.add(nextSelectionWindow);
    }

    @Enumerated(EnumType.STRING)
    public SelectionState getState() {
        return state;
    }

    public void setState(SelectionState state) {
        this.state = state;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EconomicEventParticipantSelection == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        EconomicEventParticipantSelection rhs = (EconomicEventParticipantSelection) obj;
        return new EqualsBuilder().append(pricingRevision, rhs.pricingRevision).append(participant, rhs.participant).isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(pricingRevision).append(participant).toHashCode();
    }


}
