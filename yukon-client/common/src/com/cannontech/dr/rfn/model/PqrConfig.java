package com.cannontech.dr.rfn.model;

import java.util.Optional;

//TODO: Regular getters for UI, optionals for back-end?
public class PqrConfig {
    //PQR Enable/Disable
    private Boolean pqrEnable;
    
    //LOV Parameters
    private Double lovTrigger;
    private Double lovRestore;
    private Integer lovTriggerTime;
    private Integer lovRestoreTime;
    
    //LOV Event Duration
    private Integer lovMinEventDuration;
    private Integer lovMaxEventDuration;
    
    //LOV Delay Duration
    private Integer lovStartRandomTime;
    private Integer lovEndRandomTime;
    
    //LOF Parameters
    private Integer lofTrigger;
    private Integer lofRestore;
    private Integer lofTriggerTime;
    private Integer lofRestoreTime;
    
    //LOF Event Duration
    private Integer lofMinEventDuration;
    private Integer lofMaxEventDuration;
    
    //LOF Delay Duration
    private Integer lofStartRandomTime;
    private Integer lofEndRandomTime;
    
    //General Event Separation
    private Integer minimumEventSeparation;

    public Optional<Boolean> getPqrEnable() {
        return Optional.ofNullable(pqrEnable);
    }

    public void setPqrEnable(Boolean pqrEnable) {
        this.pqrEnable = pqrEnable;
    }

    public Optional<Double> getLovTrigger() {
        return Optional.ofNullable(lovTrigger);
    }

    public void setLovTrigger(Double lovTrigger) {
        this.lovTrigger = lovTrigger;
    }

    public Optional<Double> getLovRestore() {
        return Optional.ofNullable(lovRestore);
    }

    public void setLovRestore(Double lovRestore) {
        this.lovRestore = lovRestore;
    }

    public Optional<Integer> getLovTriggerTime() {
        return Optional.ofNullable(lovTriggerTime);
    }

    public void setLovTriggerTime(Integer lovTriggerTime) {
        this.lovTriggerTime = lovTriggerTime;
    }

    public Optional<Integer> getLovRestoreTime() {
        return Optional.ofNullable(lovRestoreTime);
    }

    public void setLovRestoreTime(Integer lovRestoreTime) {
        this.lovRestoreTime = lovRestoreTime;
    }

    public Optional<Integer> getLovMinEventDuration() {
        return Optional.ofNullable(lovMinEventDuration);
    }

    public void setLovMinEventDuration(Integer lovMinEventDuration) {
        this.lovMinEventDuration = lovMinEventDuration;
    }

    public Optional<Integer> getLovMaxEventDuration() {
        return Optional.ofNullable(lovMaxEventDuration);
    }

    public void setLovMaxEventDuration(Integer lovMaxEventDuration) {
        this.lovMaxEventDuration = lovMaxEventDuration;
    }

    public Optional<Integer> getLovStartRandomTime() {
        return Optional.ofNullable(lovStartRandomTime);
    }

    public void setLovStartRandomTime(Integer lovStartRandomTime) {
        this.lovStartRandomTime = lovStartRandomTime;
    }

    public Optional<Integer> getLovEndRandomTime() {
        return Optional.ofNullable(lovEndRandomTime);
    }

    public void setLovEndRandomTime(Integer lovEndRandomTime) {
        this.lovEndRandomTime = lovEndRandomTime;
    }

    public Optional<Integer> getLofTrigger() {
        return Optional.ofNullable(lofTrigger);
    }

    public void setLofTrigger(Integer lofTrigger) {
        this.lofTrigger = lofTrigger;
    }

    public Optional<Integer> getLofRestore() {
        return Optional.ofNullable(lofRestore);
    }

    public void setLofRestore(Integer lofRestore) {
        this.lofRestore = lofRestore;
    }

    public Optional<Integer> getLofTriggerTime() {
        return Optional.ofNullable(lofTriggerTime);
    }

    public void setLofTriggerTime(Integer lofTriggerTime) {
        this.lofTriggerTime = lofTriggerTime;
    }

    public Optional<Integer> getLofRestoreTime() {
        return Optional.ofNullable(lofRestoreTime);
    }

    public void setLofRestoreTime(Integer lofRestoreTime) {
        this.lofRestoreTime = lofRestoreTime;
    }

    public Optional<Integer> getLofMinEventDuration() {
        return Optional.ofNullable(lofMinEventDuration);
    }

    public void setLofMinEventDuration(Integer lofMinEventDuration) {
        this.lofMinEventDuration = lofMinEventDuration;
    }

    public Optional<Integer> getLofMaxEventDuration() {
        return Optional.ofNullable(lofMaxEventDuration);
    }

    public void setLofMaxEventDuration(Integer lofMaxEventDuration) {
        this.lofMaxEventDuration = lofMaxEventDuration;
    }

    public Optional<Integer> getLofStartRandomTime() {
        return Optional.ofNullable(lofStartRandomTime);
    }

    public void setLofStartRandomTime(Integer lofStartRandomTime) {
        this.lofStartRandomTime = lofStartRandomTime;
    }

    public Optional<Integer> getLofEndRandomTime() {
        return Optional.ofNullable(lofEndRandomTime);
    }

    public void setLofEndRandomTime(Integer lofEndRandomTime) {
        this.lofEndRandomTime = lofEndRandomTime;
    }

    public Optional<Integer> getMinimumEventSeparation() {
        return Optional.ofNullable(minimumEventSeparation);
    }

    public void setMinimumEventSeparation(Integer minimumEventSeparation) {
        this.minimumEventSeparation = minimumEventSeparation;
    }
    
    public boolean hasLovParameters() {
        return getLovTrigger().isPresent() &&
               getLovRestore().isPresent() &&
               getLovTriggerTime().isPresent() &&
               getLovRestoreTime().isPresent();
    }
    
    public boolean hasLovEventDuration() {
        return getLovMinEventDuration().isPresent() &&
               getLovMaxEventDuration().isPresent();
    }
    
    public boolean hasLovDelayDuration() {
        return getLovStartRandomTime().isPresent() &&
               getLovEndRandomTime().isPresent();
    }
    
    public boolean hasLofParameters() {
        return getLofTrigger().isPresent() &&
               getLofRestore().isPresent() &&
               getLofTriggerTime().isPresent() &&
               getLofRestoreTime().isPresent();
    }
    
    public boolean hasLofEventDuration() {
        return getLofMinEventDuration().isPresent() &&
               getLofMaxEventDuration().isPresent();
    }
    
    public boolean hasLofDelayDuration() {
        return getLofStartRandomTime().isPresent() &&
               getLofEndRandomTime().isPresent();
    }
}
