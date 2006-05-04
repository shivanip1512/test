package com.cannontech.cc.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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

import com.cannontech.database.data.notification.NotifMap;

@Entity
@Table(name = "CCurtEEParticipant",
       uniqueConstraints=@UniqueConstraint(columnNames={"CCurtEconomicEventId","CustomerId"}))
public class EconomicEventParticipant {
    private CICustomerStub customer;
    private EconomicEvent event;
    private Integer id;
    private NotifMap notifMap = new NotifMap();
    private List<EconomicEventParticipantSelection> selections =
        new ArrayList<EconomicEventParticipantSelection>();

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EconomicEventParticipant == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        EconomicEventParticipant rhs = (EconomicEventParticipant) obj;
        return new EqualsBuilder().append(id, rhs.id).isEquals();
    }

    @ManyToOne
    @JoinColumn(name="CustomerId", nullable=false)
    public CICustomerStub getCustomer() {
        return customer;
    }

    @ManyToOne
    @JoinColumn(name="CCurtEconomicEventId", nullable=false)
    public EconomicEvent getEvent() {
        return event;
    }
    
    @Id
    @GenericGenerator(name="yukon", strategy="com.cannontech.database.incrementer.HibernateIncrementer")
    @GeneratedValue(generator="yukon")
    @Column(name = "CCurtEEParticipantId")
    public Integer getId() {
        return id;
    }

    @Column(nullable=false)
    public String getNotifAttribs() {
        return notifMap.getAttribs();
    }

    @Transient
    public NotifMap getNotifMap() {
        return notifMap;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).toHashCode();
    }

    public void setCustomer(CICustomerStub customer) {
        this.customer = customer;
    }
    
    public void setEvent(EconomicEvent event) {
        this.event = event;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public void setNotifAttribs(String attribs) {
        notifMap.setAttribs(attribs);
    }
    
    @Override
    public String toString() {
        return "EconomicEventParticipant [" + id + "]@" + Integer.toHexString(System.identityHashCode(this));
    }
    
    @OneToMany(mappedBy="participant", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    public List<EconomicEventParticipantSelection> getSelections() {
        return selections;
    }
    
    public void setSelections(List<EconomicEventParticipantSelection> selections) {
        this.selections = selections;
    }
    
    @Transient
    public EconomicEventParticipantSelection getSelection(EconomicEventPricing revision) {
        for (EconomicEventParticipantSelection selection : selections) {
            if (selection.getPricingRevision().equals(revision)) {
                return selection;
            }
        }
        throw new IllegalArgumentException(this + " does not have data for revision " + revision);
    }

    public void addSelection(EconomicEventParticipantSelection nextSelection) {
        nextSelection.setParticipant(this);
        selections.add(nextSelection);
    }

}
