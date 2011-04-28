package com.cannontech.amr.phaseDetect.data;

import java.util.Map;

import com.cannontech.enums.Phase;
import com.google.common.collect.Maps;

public class PhaseDetectState {

    private Map<Phase, Boolean> phaseDetectSentMap = Maps.newHashMap();
    private Map<Phase, Boolean> phaseDetectReadMap = Maps.newHashMap();
    private String testStep = "send";
    private boolean readCanceled = false;
    private Phase currentPhaseBeingRead = Phase.A;
    
    public PhaseDetectState(){
        phaseDetectSentMap.put(Phase.A, false);
        phaseDetectSentMap.put(Phase.B, false);
        phaseDetectSentMap.put(Phase.C, false);
        phaseDetectReadMap.put(Phase.A, false);
        phaseDetectReadMap.put(Phase.B, false);
        phaseDetectReadMap.put(Phase.C, false);
    }
    
    public void setPhaseDetectSent(Phase phase) {
        phaseDetectSentMap.put(phase, true);
    }
    
    public void setPhaseDetectRead(Phase phase) {
        phaseDetectReadMap.put(phase, true);
        setCurrentPhaseBeingRead(phase);
    }
    
    public boolean isPhaseDetectComplete() {
        return phaseDetectSentMap.get(Phase.A) && phaseDetectSentMap.get(Phase.B) && phaseDetectSentMap.get(Phase.C);
    }

    public boolean isPhaseReadComplete() {
        return phaseDetectReadMap.get(Phase.A) && phaseDetectReadMap.get(Phase.B) && phaseDetectReadMap.get(Phase.C);
    }
    
    public boolean isPhaseADetectSent(){
        return phaseDetectSentMap.get(Phase.A);
    }

    public boolean isPhaseARead(){
        return phaseDetectReadMap.get(Phase.A);
    }

    public boolean isPhaseBDetectSent(){
        return phaseDetectSentMap.get(Phase.B);
    }

    public boolean isPhaseBRead(){
        return phaseDetectReadMap.get(Phase.B);
    }
    
    public boolean isPhaseCDetectSent(){
        return phaseDetectSentMap.get(Phase.C);
    }
    
    public boolean isPhaseCRead(){
        return phaseDetectReadMap.get(Phase.C);
    }

    public String getTestStep() {
        return testStep;
    }

    public void setTestStep(String testStep) {
        this.testStep = testStep;
    }

    public boolean isReadCanceled() {
        return readCanceled;
    }

    public void setReadCanceled(boolean readCanceled) {
        this.readCanceled = readCanceled;
        if(readCanceled){
            phaseDetectReadMap.put(currentPhaseBeingRead, false);
        }
    }

    public Phase getCurrentPhaseBeingRead() {
        return currentPhaseBeingRead;
    }

    public void setCurrentPhaseBeingRead(Phase currentPhaseBeingRead) {
        this.currentPhaseBeingRead = currentPhaseBeingRead;
    }

}