package com.cannontech.web.stars.dr.operator.hardware.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.util.LazyList;
import com.cannontech.common.util.SimpleSupplier;
import com.cannontech.stars.dr.hardware.model.HardwareType;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.xml.serialize.ExpressCom;
import com.cannontech.stars.xml.serialize.SA205;
import com.cannontech.stars.xml.serialize.SA305;
import com.cannontech.stars.xml.serialize.SASimple;
import com.cannontech.stars.xml.serialize.StarsLMConfiguration;
import com.cannontech.stars.xml.serialize.VersaCom;

public class HardwareConfigurationDto {
    private int accountId;
    private int inventoryId;
    private String action;
    private List<ProgramEnrollmentDto> programEnrollments =
        new LazyList<ProgramEnrollmentDto>(new ArrayList<ProgramEnrollmentDto>(),
                new SimpleSupplier<ProgramEnrollmentDto>(ProgramEnrollmentDto.class));

    // hardware addressing fields (used only when TRACK_HARDWARE_ADDRESSING
    // role property is true)
    // HardwareConfigType.EXPRESSCOM
    private Integer serviceProvider = 0;
    private Integer geo = null;
    private Integer substation = null;  // also used by SA305
    private boolean[] feederBits = new boolean[16];
    private Integer zip = null;
    private Integer userAddress = null;

    // HardwareConfigType.VERSACOM
    private Integer utility = 0; // also used by SA305
    private Integer section = null;
    private boolean[] classAddressBits = new boolean[16];
    private boolean[] divisionBits = new boolean[16];

    // HardwareConfigType.SA205
    private Integer slots[] = new Integer[6];

    // HardwareConfigType.SA305
    // sharing utility with VERSACOM
    private Integer group = null;
    private Integer division = null;
    // sharing substation with EXPRESSCOM
    private Integer rate = null;
    private Integer rateFamily = null;
    private Integer rateMember = null;

    // HardwareConfigType.SA_SIMPLE
    private String operationalAddress = "";

    // Relay section
    private String[] coldLoadPickup = new String[8];
    private String[] tamperDetect = new String[2];
    private Integer[] program = new Integer[8];
    private Integer[] splinter = new Integer[8];


    public HardwareConfigurationDto() {
        Arrays.fill(coldLoadPickup, "");
        Arrays.fill(tamperDetect, "");
        Arrays.fill(slots, InventoryUtils.SA205_UNUSED_ADDR);

    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<ProgramEnrollmentDto> getProgramEnrollments() {
        return programEnrollments;
    }

    public void setProgramEnrollments(List<ProgramEnrollmentDto> programEnrollments) {
        this.programEnrollments = programEnrollments;
    }

    public Integer[] getSlots() {
        return slots;
    }

    public void setSlots(Integer[] slots) {
        this.slots = slots;
    }

    public Integer getGroup() {
        return group;
    }

    public void setGroup(Integer group) {
        this.group = group;
    }

    public Integer getDivision() {
        return division;
    }

    public void setDivision(Integer division) {
        this.division = division;
    }

    public Integer getRateFamily() {
        return rateFamily;
    }

    public void setRateFamily(Integer rateFamily) {
        this.rateFamily = rateFamily;
    }

    public Integer getRateMember() {
        return rateMember;
    }

    public void setRateMember(Integer rateMember) {
        this.rateMember = rateMember;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public Integer getUtility() {
        return utility;
    }

    public void setUtility(Integer utility) {
        this.utility = utility;
    }

    public Integer getSection() {
        return section;
    }

    public void setSection(Integer section) {
        this.section = section;
    }

    public boolean[] getClassAddressBits() {
        return classAddressBits;
    }

    public void setClassAddressBits(boolean[] classAddressBits) {
        this.classAddressBits = classAddressBits;
    }

    public boolean[] getDivisionBits() {
        return divisionBits;
    }

    public void setDivisionBits(boolean[] divisionBits) {
        this.divisionBits = divisionBits;
    }

    public Integer getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(Integer serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public Integer getGeo() {
        return geo;
    }

    public void setGeo(Integer geo) {
        this.geo = geo;
    }

    public Integer getSubstation() {
        return substation;
    }

    public void setSubstation(Integer substation) {
        this.substation = substation;
    }

    public boolean[] getFeederBits() {
        return feederBits;
    }

    public void setFeederBits(boolean[] feederBits) {
        this.feederBits = feederBits;
    }

    public Integer getZip() {
        return zip;
    }

    public void setZip(Integer zip) {
        this.zip = zip;
    }

    public Integer getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(Integer userAddress) {
        this.userAddress = userAddress;
    }

    public String getOperationalAddress() {
        return operationalAddress;
    }

    public void setOperationalAddress(String operationalAddress) {
        this.operationalAddress = operationalAddress;
    }

    public String[] getColdLoadPickup() {
        return coldLoadPickup;
    }

    public void setColdLoadPickup(String[] coldLoadPickup) {
        this.coldLoadPickup = coldLoadPickup;
    }

    public String[] getTamperDetect() {
        return tamperDetect;
    }

    public void setTamperDetect(String[] tamperDetect) {
        this.tamperDetect = tamperDetect;
    }

    public Integer[] getProgram() {
        return program;
    }

    public void setProgram(Integer[] Program) {
        this.program = Program;
    }

    public Integer[] getSplinter() {
        return splinter;
    }

    public void setExpressComSplinter(Integer[] expressComSplinter) {
        this.splinter = expressComSplinter;
    }

    private static void splitBits(int bitField, boolean[] destination) {
        for (int bitPosition = 0; bitPosition < 16; bitPosition++) {
            destination[bitPosition] = (bitField & (1 << bitPosition)) > 0;
        }
    }

    private Integer nullIfZero(int intIn) {
        return intIn == 0 ? null : intIn;
    }

    private Integer[] stringToIntegerArray(String string) {
        String[] stringArray = string.split(",", -1);
        Integer[] retVal = new Integer[stringArray.length];
        for (int index = 0; index < retVal.length; index++) {
            if (StringUtils.isNotBlank(stringArray[index])) {
                retVal[index] = Integer.parseInt(stringArray[index]);
            }
        }
        return retVal;
    }

    public void setStarsLMConfiguration(StarsLMConfiguration config,
            HardwareType hardwareType) {
        if (config == null) {
            return;
        }

        if (config.getExpressCom() != null) {
            ExpressCom expressCom = config.getExpressCom();

            serviceProvider = expressCom.getServiceProvider();
            geo = nullIfZero(expressCom.getGEO());
            substation = nullIfZero(expressCom.getSubstation());

            splitBits(expressCom.getFeeder(), feederBits);

            zip = nullIfZero(expressCom.getZip());
            userAddress = nullIfZero(expressCom.getUserAddress());

            program = stringToIntegerArray(config.getExpressCom().getProgram());
            splinter = stringToIntegerArray(config.getExpressCom().getSplinter());
        }

        if (config.getVersaCom() != null) {
            VersaCom versaCom = config.getVersaCom();
            utility = versaCom.getUtility();
            section = nullIfZero(versaCom.getSection());
            splitBits(versaCom.getClassAddress(), classAddressBits);
            splitBits(versaCom.getDivision(), divisionBits);
        }

        if (config.getSA205() != null) {
            SA205 sa205 = config.getSA205();
            slots[0] = nullIfZero(sa205.getSlot1());
            slots[1] = nullIfZero(sa205.getSlot2());
            slots[2] = nullIfZero(sa205.getSlot3());
            slots[3] = nullIfZero(sa205.getSlot4());
            slots[4] = nullIfZero(sa205.getSlot5());
            slots[5] = nullIfZero(sa205.getSlot6());
        }

        if (config.getSA305() != null) {
            SA305 sa305 = config.getSA305();
            utility = sa305.getUtility();
            group = nullIfZero(sa305.getGroup());
            division = nullIfZero(sa305.getDivision());
            substation = nullIfZero(sa305.getSubstation());
            rate = nullIfZero(sa305.getRateRate());
            rateFamily = nullIfZero(sa305.getRateFamily());
            rateMember = nullIfZero(sa305.getRateMember());
        }

        if (config.getSASimple() != null) {
            operationalAddress = config.getSASimple().getOperationalAddress();
        }

        if (config.getColdLoadPickup() != null) {
            String[] clp = config.getColdLoadPickup().split(",");
            System.arraycopy(clp, 0, coldLoadPickup, 0, clp.length);
        }

        if (config.getTamperDetect() != null) {
            String[] td = config.getTamperDetect().split(",");
            System.arraycopy(td, 0, tamperDetect, 0, td.length);
        }
    }

    private static int joinBits(boolean[] bits) {
        int retVal = 0;
        for (int bitPosition = 0; bitPosition < bits.length; bitPosition++) {
            if (bits[bitPosition]) {
                retVal |= (1 << bitPosition);
            }
        }
        return retVal;
    }

    public StarsLMConfiguration getStarsLMConfiguration(HardwareType hardwareType) {
        StarsLMConfiguration config = new StarsLMConfiguration();

        config.setColdLoadPickup(StringUtils.join(coldLoadPickup, ','));

        if (hardwareType.isHasTamperDetect()) {
            config.setTamperDetect(StringUtils.join(tamperDetect, ','));
        }

        switch (hardwareType.getHardwareConfigType()) {
        case EXPRESSCOM:
            ExpressCom expressCom = new ExpressCom();
            expressCom.setServiceProvider(serviceProvider);
            expressCom.setGEO(geo == null ? 0 : geo);
            expressCom.setSubstation(substation == null ? 0 : substation);
            expressCom.setFeeder(joinBits(feederBits));
            expressCom.setZip(zip == null ? 0 : zip);
            expressCom.setUserAddress(userAddress == null ? 0 : userAddress);

            if (hardwareType.isHasProgramSplinter()) {
                expressCom.setProgram(StringUtils.join(program, ','));
                expressCom.setSplinter(StringUtils.join(splinter, ','));
            }
            config.setExpressCom(expressCom);
            break;
        case VERSACOM:
            VersaCom versaCom = new VersaCom();
            versaCom.setUtility(utility);
            versaCom.setSection(section == null ? 0 : section);
            versaCom.setClassAddress(joinBits(classAddressBits));
            versaCom.setDivision(joinBits(divisionBits));
            config.setVersaCom(versaCom);
            break;
        case SA205:
            SA205 sa205 = new SA205();
            sa205.setSlot1(slots[0] == null ? 0 : slots[0]);
            sa205.setSlot2(slots[1] == null ? 0 : slots[1]);
            sa205.setSlot3(slots[2] == null ? 0 : slots[2]);
            sa205.setSlot4(slots[3] == null ? 0 : slots[3]);
            sa205.setSlot5(slots[4] == null ? 0 : slots[4]);
            sa205.setSlot6(slots[5] == null ? 0 : slots[5]);
            config.setSA205(sa205);
            break;
        case SA305:
            SA305 sa305 = new SA305();
            sa305.setUtility(utility == null ? 0 : utility);
            sa305.setGroup(group == null ? 0 : group);
            sa305.setDivision(division == null ? 0 : division);
            sa305.setSubstation(substation == null ? 0 : substation);
            sa305.setRateRate(rate == null ? 0 : rate);

            // Rate Hierarchy should not be on the configuration page; it is
            // part of the control command only
            sa305.setRateHierarchy(0);

            config.setSA305(sa305);
            break;
        case SA_SIMPLE:
            SASimple saSimple = new SASimple();
            saSimple.setOperationalAddress(operationalAddress);
            config.setSASimple(saSimple);
            break;
        }

        return config;
    }
}
