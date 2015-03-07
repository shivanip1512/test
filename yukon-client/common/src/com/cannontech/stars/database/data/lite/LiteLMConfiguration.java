package com.cannontech.stars.database.data.lite;

import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteTypes;

public class LiteLMConfiguration extends LiteBase {
    
    private String coldLoadPickup;
    private String tamperDetect;
    
    private SASimple saSimple;
    private SA205 sa205;
    private SA305 sa305;
    private ExpressCom expresscom;
    private VersaCom versacom;
    
    public LiteLMConfiguration() {
        super();
        setLiteType(LiteTypes.STARS_LMCONFIGURATION);
    }
    
    public LiteLMConfiguration(int configId) {
        super();
        setConfigurationID(configId);
        setLiteType(LiteTypes.STARS_LMCONFIGURATION);
    }
    
    public int getConfigurationID() {
        return getLiteID();
    }
    
    public void setConfigurationID(int configId) {
        setLiteID(configId);
    }
    
    public String getColdLoadPickup() {
        return coldLoadPickup;
    }
    
    public String getTamperDetect() {
        return tamperDetect;
    }
    
    public void setColdLoadPickup(String coldLoadPickup) {
        this.coldLoadPickup = coldLoadPickup;
    }
    
    public void setTamperDetect(String tamperDetect) {
        this.tamperDetect = tamperDetect;
    }
    
    public SA205 getSA205() {
        return sa205;
    }
    
    public void setSA205(SA205 sa205) {
        this.sa205 = sa205;
    }
    
    public SA305 getSA305() {
        return sa305;
    }
    
    public void setSA305(SA305 sa305) {
        this.sa305 = sa305;
    }
    
    public VersaCom getVersaCom() {
        return versacom;
    }
    
    public ExpressCom getExpressCom() {
        return expresscom;
    }
    
    public void setVersaCom(VersaCom versacom) {
        this.versacom = versacom;
    }
    
    public void setExpressCom(ExpressCom expresscom) {
        this.expresscom = expresscom;
    }
    
    public SASimple getSASimple() {
        return saSimple;
    }
    
    public void setSASimple(SASimple saSimple) {
        this.saSimple = saSimple;
    }
    
    public static class SA205 {
        
        private int slot1;
        private int slot2;
        private int slot3;
        private int slot4;
        private int slot5;
        private int slot6;
        
        public int getSlot1() {
            return slot1;
        }
        
        public void setSlot1(int slot1) {
            this.slot1 = slot1;
        }
        
        public int getSlot2() {
            return slot2;
        }
        
        public void setSlot2(int slot2) {
            this.slot2 = slot2;
        }
        
        public int getSlot3() {
            return slot3;
        }
        
        public void setSlot3(int slot3) {
            this.slot3 = slot3;
        }
        
        public int getSlot4() {
            return slot4;
        }
        
        public void setSlot4(int slot4) {
            this.slot4 = slot4;
        }
        
        public int getSlot5() {
            return slot5;
        }
        
        public void setSlot5(int slot5) {
            this.slot5 = slot5;
        }
        
        public int getSlot6() {
            return slot6;
        }
        
        public void setSlot6(int slot6) {
            this.slot6 = slot6;
        }
        
    }
    
    public static class SA305 {
        
        private int utility;
        private int group;
        private int division;
        private int substation;
        private int rateFamily;
        private int rateMember;
        private int rateHierarchy;
        
        public int getUtility() {
            return utility;
        }
        
        public void setUtility(int utility) {
            this.utility = utility;
        }
        
        public int getGroup() {
            return group;
        }
        
        public void setGroup(int group) {
            this.group = group;
        }
        
        public int getDivision() {
            return division;
        }
        
        public void setDivision(int division) {
            this.division = division;
        }
        
        public int getSubstation() {
            return substation;
        }
        
        public void setSubstation(int substation) {
            this.substation = substation;
        }
        
        public int getRateFamily() {
            return rateFamily;
        }
        
        public void setRateFamily(int rateFamily) {
            this.rateFamily = rateFamily;
        }
        
        public int getRateMember() {
            return rateMember;
        }
        
        public void setRateMember(int rateMember) {
            this.rateMember = rateMember;
        }
        
        public int getRateHierarchy() {
            return rateHierarchy;
        }
        
        public void setRateHierarchy(int rateHierarchy) {
            this.rateHierarchy = rateHierarchy;
        }
        
    }
    
    public static class ExpressCom {
        
        private int serviceProvider;
        private int geo;
        private int substation;
        private int feeder;
        private int zip;
        private int userAddress;
        private String program;
        private String splinter;
        
        public int getServiceProvider() {
            return serviceProvider;
        }
        
        public void setServiceProvider(int serviceProvider) {
            this.serviceProvider = serviceProvider;
        }
        
        public int getGEO() {
            return geo;
        }
        
        public void setGEO(int geo) {
            this.geo = geo;
        }
        
        public int getSubstation() {
            return substation;
        }
        
        public void setSubstation(int substation) {
            this.substation = substation;
        }
        
        public int getFeeder() {
            return feeder;
        }
        
        public void setFeeder(int feeder) {
            this.feeder = feeder;
        }
        
        public int getZip() {
            return zip;
        }
        
        public void setZip(int zip) {
            this.zip = zip;
        }
        
        public int getUserAddress() {
            return userAddress;
        }
        
        public void setUserAddress(int userAddress) {
            this.userAddress = userAddress;
        }
        
        public String getProgram() {
            return program;
        }
        
        public void setProgram(String program) {
            this.program = program;
        }
        
        public String getSplinter() {
            return splinter;
        }
        
        public void setSplinter(String splinter) {
            this.splinter = splinter;
        }
        
    }
    
    public static class VersaCom {
        
        private int utilityID;
        private int section;
        private int classAddress;
        private int divisionAddress;
        
        public int getUtilityID() {
            return utilityID;
        }
        
        public void setUtilityID(int utilityID) {
            this.utilityID = utilityID;
        }
        
        public int getSection() {
            return section;
        }
        
        public void setSection(int section) {
            this.section = section;
        }
        
        public int getClassAddress() {
            return classAddress;
        }
        
        public void setClassAddress(int classAddress) {
            this.classAddress = classAddress;
        }
        
        public int getDivisionAddress() {
            return divisionAddress;
        }
        
        public void setDivisionAddress(int divisionAddress) {
            this.divisionAddress = divisionAddress;
        }
        
    }
    
    public static class SASimple {
        
        private String operationalAddress;
        
        public String getOperationalAddress() {
            return operationalAddress;
        }
        
        public void setOperationalAddress(String operationalAddress) {
            this.operationalAddress = operationalAddress;
        }
        
    }
    
}