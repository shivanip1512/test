package com.cannontech.dr.rfn.model;

import java.util.Optional;

/**
 * Object describing parameters for a Power Quality Response configuration.
 */
public class PqrConfig {
    //PQR Enable/Disable
    private Boolean pqrEnable;
    
    //LOV Parameters
    private Double lovTrigger; //volts
    private Double lovRestore; //volts
    private Short lovTriggerTime; //millis
    private Short lovRestoreTime; //millis
    
    //LOV Event Duration
    private Short lovMinEventDuration; //seconds
    private Short lovMaxEventDuration; //seconds
    
    //LOV Delay Duration
    private Short lovStartRandomTime; //millis
    private Short lovEndRandomTime; //millis
    
    //LOF Parameters
    private Short lofTrigger; //millis
    private Short lofRestore; //millis
    private Short lofTriggerTime; //millis
    private Short lofRestoreTime; //millis
    
    //LOF Event Duration
    private Short lofMinEventDuration; //seconds
    private Short lofMaxEventDuration; //seconds
    
    //LOF Delay Duration
    private Short lofStartRandomTime; //millis
    private Short lofEndRandomTime; //millis
    
    //General Event Separation
    private Short minimumEventSeparation; //seconds
    
    public Boolean getPqrEnable() {
        return pqrEnable;
    }
    
    public Optional<Boolean> getPqrEnableOptional() {
        return Optional.ofNullable(pqrEnable);
    }

    public void setPqrEnable(Boolean pqrEnable) {
        this.pqrEnable = pqrEnable;
    }

    public Double getLovTrigger() {
        return lovTrigger;
    }

    public Optional<Double> getLovTriggerOptional() {
        return Optional.ofNullable(lovTrigger);
    }

    public void setLovTrigger(Double lovTrigger) {
        this.lovTrigger = lovTrigger;
    }

    public Double getLovRestore() {
        return lovRestore;
    }

    public Optional<Double> getLovRestoreOptional() {
        return Optional.ofNullable(lovRestore);
    }

    public void setLovRestore(Double lovRestore) {
        this.lovRestore = lovRestore;
    }

    public Short getLovTriggerTime() {
        return lovTriggerTime;
    }

    public Optional<Short> getLovTriggerTimeOptional() {
        return Optional.ofNullable(lovTriggerTime);
    }

    public void setLovTriggerTime(Short lovTriggerTime) {
        this.lovTriggerTime = lovTriggerTime;
    }

    public Short getLovRestoreTime() {
        return lovRestoreTime;
    }

    public Optional<Short> getLovRestoreTimeOptional() {
        return Optional.ofNullable(lovRestoreTime);
    }

    public void setLovRestoreTime(Short lovRestoreTime) {
        this.lovRestoreTime = lovRestoreTime;
    }

    public Optional<Short> getLovMinEventDurationOptional() {
        return Optional.ofNullable(lovMinEventDuration);
    }
    
    public Short getLovMinEventDuration() {
        return lovMinEventDuration;
    }

    public void setLovMinEventDuration(Short lovMinEventDuration) {
        this.lovMinEventDuration = lovMinEventDuration;
    }

    public Optional<Short> getLovMaxEventDurationOptional() {
        return Optional.ofNullable(lovMaxEventDuration);
    }
    
    public Short getLovMaxEventDuration() {
        return lovMaxEventDuration;
    }

    public void setLovMaxEventDuration(Short lovMaxEventDuration) {
        this.lovMaxEventDuration = lovMaxEventDuration;
    }

    public Optional<Short> getLovStartRandomTimeOptional() {
        return Optional.ofNullable(lovStartRandomTime);
    }
    
    public Short getLovStartRandomTime() {
        return lovStartRandomTime;
    }

    public void setLovStartRandomTime(Short lovStartRandomTime) {
        this.lovStartRandomTime = lovStartRandomTime;
    }

    public Optional<Short> getLovEndRandomTimeOptional() {
        return Optional.ofNullable(lovEndRandomTime);
    }
    
    public Short getLovEndRandomTime() {
        return lovEndRandomTime;
    }

    public void setLovEndRandomTime(Short lovEndRandomTime) {
        this.lovEndRandomTime = lovEndRandomTime;
    }

    public Optional<Short> getLofTriggerOptional() {
        return Optional.ofNullable(lofTrigger);
    }
    
    public Short getLofTrigger() {
        return lofTrigger;
    }

    public void setLofTrigger(Short lofTrigger) {
        this.lofTrigger = lofTrigger;
    }

    public Optional<Short> getLofRestoreOptional() {
        return Optional.ofNullable(lofRestore);
    }
    
    public Short getLofRestore() {
        return lofRestore;
    }

    public void setLofRestore(Short lofRestore) {
        this.lofRestore = lofRestore;
    }

    public Optional<Short> getLofTriggerTimeOptional() {
        return Optional.ofNullable(lofTriggerTime);
    }

    public Short getLofTriggerTime() {
        return lofTriggerTime;
    }
    
    public void setLofTriggerTime(Short lofTriggerTime) {
        this.lofTriggerTime = lofTriggerTime;
    }

    public Optional<Short> getLofRestoreTimeOptional() {
        return Optional.ofNullable(lofRestoreTime);
    }

    public Short getLofRestoreTime() {
        return lofRestoreTime;
    }
    
    public void setLofRestoreTime(Short lofRestoreTime) {
        this.lofRestoreTime = lofRestoreTime;
    }

    public Optional<Short> getLofMinEventDurationOptional() {
        return Optional.ofNullable(lofMinEventDuration);
    }
    
    public Short getLofMinEventDuration() {
        return lofMinEventDuration;
    }

    public void setLofMinEventDuration(Short lofMinEventDuration) {
        this.lofMinEventDuration = lofMinEventDuration;
    }

    public Optional<Short> getLofMaxEventDurationOptional() {
        return Optional.ofNullable(lofMaxEventDuration);
    }
    
    public Short getLofMaxEventDuration() {
        return lofMaxEventDuration;
    }

    public void setLofMaxEventDuration(Short lofMaxEventDuration) {
        this.lofMaxEventDuration = lofMaxEventDuration;
    }

    public Optional<Short> getLofStartRandomTimeOptional() {
        return Optional.ofNullable(lofStartRandomTime);
    }
    
    public Short getLofStartRandomTime() {
        return lofStartRandomTime;
    }

    public void setLofStartRandomTime(Short lofStartRandomTime) {
        this.lofStartRandomTime = lofStartRandomTime;
    }

    public Optional<Short> getLofEndRandomTimeOptional() {
        return Optional.ofNullable(lofEndRandomTime);
    }
    
    public Short getLofEndRandomTime() {
        return lofEndRandomTime;
    }

    public void setLofEndRandomTime(Short lofEndRandomTime) {
        this.lofEndRandomTime = lofEndRandomTime;
    }

    public Optional<Short> getMinimumEventSeparationOptional() {
        return Optional.ofNullable(minimumEventSeparation);
    }

    public Short getMinimumEventSeparation() {
        return minimumEventSeparation;
    }
    
    public void setMinimumEventSeparation(Short minimumEventSeparation) {
        this.minimumEventSeparation = minimumEventSeparation;
    }
    
    public boolean hasLovParams() {
        return getLovTriggerOptional().isPresent() &&
               getLovRestoreOptional().isPresent() &&
               getLovTriggerTimeOptional().isPresent() &&
               getLovRestoreTimeOptional().isPresent();
    }
    
    public boolean hasLovEventDurations() {
        return getLovMinEventDurationOptional().isPresent() &&
               getLovMaxEventDurationOptional().isPresent();
    }
    
    public boolean hasLovDelayDurations() {
        return getLovStartRandomTimeOptional().isPresent() &&
               getLovEndRandomTimeOptional().isPresent();
    }
    
    public boolean hasLofParams() {
        return getLofTriggerOptional().isPresent() &&
               getLofRestoreOptional().isPresent() &&
               getLofTriggerTimeOptional().isPresent() &&
               getLofRestoreTimeOptional().isPresent();
    }
    
    public boolean hasLofEventDurations() {
        return getLofMinEventDurationOptional().isPresent() &&
               getLofMaxEventDurationOptional().isPresent();
    }
    
    public boolean hasLofDelayDurations() {
        return getLofStartRandomTimeOptional().isPresent() &&
               getLofEndRandomTimeOptional().isPresent();
    }

    public boolean isEmpty() {
        return pqrEnable == null &&
               lovTrigger == null &&
               lovRestore == null &&
               lovTriggerTime == null &&
               lovRestoreTime == null &&
               lovMinEventDuration == null &&
               lovMaxEventDuration == null &&
               lovStartRandomTime == null &&
               lovEndRandomTime == null &&
               lofTrigger == null &&
               lofRestore == null &&
               lofTriggerTime == null &&
               lofRestoreTime == null &&
               lofMinEventDuration == null &&
               lofMaxEventDuration == null &&
               lofStartRandomTime == null &&
               lofEndRandomTime == null &&
               minimumEventSeparation == null;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((lofEndRandomTime == null) ? 0 : lofEndRandomTime.hashCode());
        result = prime * result + ((lofMaxEventDuration == null) ? 0 : lofMaxEventDuration.hashCode());
        result = prime * result + ((lofMinEventDuration == null) ? 0 : lofMinEventDuration.hashCode());
        result = prime * result + ((lofRestore == null) ? 0 : lofRestore.hashCode());
        result = prime * result + ((lofRestoreTime == null) ? 0 : lofRestoreTime.hashCode());
        result = prime * result + ((lofStartRandomTime == null) ? 0 : lofStartRandomTime.hashCode());
        result = prime * result + ((lofTrigger == null) ? 0 : lofTrigger.hashCode());
        result = prime * result + ((lofTriggerTime == null) ? 0 : lofTriggerTime.hashCode());
        result = prime * result + ((lovEndRandomTime == null) ? 0 : lovEndRandomTime.hashCode());
        result = prime * result + ((lovMaxEventDuration == null) ? 0 : lovMaxEventDuration.hashCode());
        result = prime * result + ((lovMinEventDuration == null) ? 0 : lovMinEventDuration.hashCode());
        result = prime * result + ((lovRestore == null) ? 0 : lovRestore.hashCode());
        result = prime * result + ((lovRestoreTime == null) ? 0 : lovRestoreTime.hashCode());
        result = prime * result + ((lovStartRandomTime == null) ? 0 : lovStartRandomTime.hashCode());
        result = prime * result + ((lovTrigger == null) ? 0 : lovTrigger.hashCode());
        result = prime * result + ((lovTriggerTime == null) ? 0 : lovTriggerTime.hashCode());
        result = prime * result + ((minimumEventSeparation == null) ? 0 : minimumEventSeparation.hashCode());
        result = prime * result + ((pqrEnable == null) ? 0 : pqrEnable.hashCode());
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
        PqrConfig other = (PqrConfig) obj;
        if (lofEndRandomTime == null) {
            if (other.lofEndRandomTime != null) {
                return false;
            }
        } else if (!lofEndRandomTime.equals(other.lofEndRandomTime)) {
            return false;
        }
        if (lofMaxEventDuration == null) {
            if (other.lofMaxEventDuration != null) {
                return false;
            }
        } else if (!lofMaxEventDuration.equals(other.lofMaxEventDuration)) {
            return false;
        }
        if (lofMinEventDuration == null) {
            if (other.lofMinEventDuration != null) {
                return false;
            }
        } else if (!lofMinEventDuration.equals(other.lofMinEventDuration)) {
            return false;
        }
        if (lofRestore == null) {
            if (other.lofRestore != null) {
                return false;
            }
        } else if (!lofRestore.equals(other.lofRestore)) {
            return false;
        }
        if (lofRestoreTime == null) {
            if (other.lofRestoreTime != null) {
                return false;
            }
        } else if (!lofRestoreTime.equals(other.lofRestoreTime)) {
            return false;
        }
        if (lofStartRandomTime == null) {
            if (other.lofStartRandomTime != null) {
                return false;
            }
        } else if (!lofStartRandomTime.equals(other.lofStartRandomTime)) {
            return false;
        }
        if (lofTrigger == null) {
            if (other.lofTrigger != null) {
                return false;
            }
        } else if (!lofTrigger.equals(other.lofTrigger)) {
            return false;
        }
        if (lofTriggerTime == null) {
            if (other.lofTriggerTime != null) {
                return false;
            }
        } else if (!lofTriggerTime.equals(other.lofTriggerTime)) {
            return false;
        }
        if (lovEndRandomTime == null) {
            if (other.lovEndRandomTime != null) {
                return false;
            }
        } else if (!lovEndRandomTime.equals(other.lovEndRandomTime)) {
            return false;
        }
        if (lovMaxEventDuration == null) {
            if (other.lovMaxEventDuration != null) {
                return false;
            }
        } else if (!lovMaxEventDuration.equals(other.lovMaxEventDuration)) {
            return false;
        }
        if (lovMinEventDuration == null) {
            if (other.lovMinEventDuration != null) {
                return false;
            }
        } else if (!lovMinEventDuration.equals(other.lovMinEventDuration)) {
            return false;
        }
        if (lovRestore == null) {
            if (other.lovRestore != null) {
                return false;
            }
        } else if (!lovRestore.equals(other.lovRestore)) {
            return false;
        }
        if (lovRestoreTime == null) {
            if (other.lovRestoreTime != null) {
                return false;
            }
        } else if (!lovRestoreTime.equals(other.lovRestoreTime)) {
            return false;
        }
        if (lovStartRandomTime == null) {
            if (other.lovStartRandomTime != null) {
                return false;
            }
        } else if (!lovStartRandomTime.equals(other.lovStartRandomTime)) {
            return false;
        }
        if (lovTrigger == null) {
            if (other.lovTrigger != null) {
                return false;
            }
        } else if (!lovTrigger.equals(other.lovTrigger)) {
            return false;
        }
        if (lovTriggerTime == null) {
            if (other.lovTriggerTime != null) {
                return false;
            }
        } else if (!lovTriggerTime.equals(other.lovTriggerTime)) {
            return false;
        }
        if (minimumEventSeparation == null) {
            if (other.minimumEventSeparation != null) {
                return false;
            }
        } else if (!minimumEventSeparation.equals(other.minimumEventSeparation)) {
            return false;
        }
        if (pqrEnable == null) {
            if (other.pqrEnable != null) {
                return false;
            }
        } else if (!pqrEnable.equals(other.pqrEnable)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PqrConfig [pqrEnable=" + pqrEnable + ", lovTrigger=" + lovTrigger + ", lovRestore=" + lovRestore
               + ", lovTriggerTime=" + lovTriggerTime + ", lovRestoreTime=" + lovRestoreTime + ", lovMinEventDuration="
               + lovMinEventDuration + ", lovMaxEventDuration=" + lovMaxEventDuration + ", lovStartRandomTime="
               + lovStartRandomTime + ", lovEndRandomTime=" + lovEndRandomTime + ", lofTrigger=" + lofTrigger
               + ", lofRestore=" + lofRestore + ", lofTriggerTime=" + lofTriggerTime + ", lofRestoreTime="
               + lofRestoreTime + ", lofMinEventDuration=" + lofMinEventDuration + ", lofMaxEventDuration="
               + lofMaxEventDuration + ", lofStartRandomTime=" + lofStartRandomTime + ", lofEndRandomTime="
               + lofEndRandomTime + ", minimumEventSeparation=" + minimumEventSeparation + "]";
    }
}
