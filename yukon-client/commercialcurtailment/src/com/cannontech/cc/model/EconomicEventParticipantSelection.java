package com.cannontech.cc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "CCurtEEParticipantSelection",
       uniqueConstraints=@UniqueConstraint(columnNames={"CCurtEconomicEventParticipantId","CCurtEEPricingId"}))
public class EconomicEventParticipantSelection {
    public enum SelectionState {DEFAULT, MANUAL};
    private String connectionAudit;
    private Integer id;
    private EconomicEventParticipant participant;
    private EconomicEventPricing pricingRevision;
    private Date submitTime;
    private SelectionState state;
    private List<EconomicEventParticipantSelectionWindow> selectionWindows = 
        new ArrayList<EconomicEventParticipantSelectionWindow>();

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
    @JoinColumn(name="CCurtEconomicEventParticipantId", nullable=false)
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
    public List<EconomicEventParticipantSelectionWindow> getSelectionWindows() {
        Collections.sort(selectionWindows); // there aren't many and they'll usually be sorted
        return selectionWindows;
    }
    
    public void setSelectionWindows(List<EconomicEventParticipantSelectionWindow> selectionWindows) {
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

}
