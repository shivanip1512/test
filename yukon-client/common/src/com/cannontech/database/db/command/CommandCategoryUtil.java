package com.cannontech.database.db.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class CommandCategoryUtil {

    private static final Set<CommandCategory> ALL_CATEGORIES = ImmutableSet.of(
        CommandCategory.ALPHA_BASE,
        CommandCategory.CBC_BASE,
        CommandCategory.CCU_BASE,
        CommandCategory.DISCONNECT_BASE,
        CommandCategory.IED_BASE,
        CommandCategory.ION_BASE,
        CommandCategory.LCU_BASE,
        CommandCategory.LOAD_GROUP_BASE,
        CommandCategory.LP_BASE,
        CommandCategory.MCT_BASE,
        CommandCategory.RFN_BASE,
        CommandCategory.RTU_BASE,
        CommandCategory.REPEATER_BASE,
        CommandCategory.TCU_BASE,
        CommandCategory.STATUSINPUT_BASE,
        CommandCategory.PING_BASE,
        CommandCategory.MCT_4XX_SERIES_BASE,
        CommandCategory.TWO_WAY_LCR_BASE
    );

    private static List<PaoType> CAT_ALPHA_BASE_DEVTYPES;
    private static List<PaoType> CAT_CBC_BASE_DEVTYPES;
    private static List<PaoType> CAT_CCU_BASE_DEVTYPES;
    private static List<PaoType> CAT_DISCONNECT_BASE_DEVTYPES;
    private static List<PaoType> CAT_IED_BASE_DEVTYPES;
    private static List<PaoType> CAT_ION_BASE_DEVTYPES;
    private static List<PaoType> CAT_LCU_BASE_DEVTYPES;
    private static List<PaoType> CAT_LOAD_GROUP_BASE_DEVTYPES;
    private static List<PaoType> CAT_LP_BASE_DEVTYPES;
    private static List<PaoType> CAT_MCT_BASE_DEVTYPES;
    private static List<PaoType> CAT_RTU_BASE_DEVTYPES;
    private static List<PaoType> CAT_REPEATER_BASE_DEVTYPES;
    private static List<PaoType> CAT_TCU_BASE_DEVTYPES;
    private static List<PaoType> CAT_STATUSINPUT_BASE_DEVTYPES;
    private static List<PaoType> CAT_PING_BASE_DEVTYPES;
    private static List<PaoType> CAT_MCT_4XX_SERIES_DEVTYPES;
    private static List<PaoType> CAT_TWO_WAY_LCR_DEVTYPES;
    private static List<PaoType> CAT_RFN_BASE_DEVTYPES;
    
    public final static Set<CommandCategory> getAllCategories() {
        return ALL_CATEGORIES;
    }
    
    /**
     * Return true if category is one from ALL_CATEGORIES,
     * false if not. (more than likely it is just a YukonPaobject.paoType value)
     */
    public static boolean isCommandCategory(String category) {
        
        for (CommandCategory commandCategory :  getAllCategories()) {
            if (commandCategory.getDbString().equalsIgnoreCase(category)) return true;
        }
        
        return false;
    }
    
    /**
     * Return true if category is either ExpressCom or VersaCom
     * false if not.
     */
    public static boolean isExpressComOrVersaCom(String category) {
        return CommandCategory.EXPRESSCOM_SERIAL.getDbString().equals(category) || CommandCategory.VERSACOM_SERIAL.getDbString().equals(category);
    }
    
    public static List<PaoType> getAllTypesForCategory(CommandCategory category) {
        
        if (category == CommandCategory.ALPHA_BASE) {
            return getAllAlphaBaseDevTypes();
        } else if (category == CommandCategory.CBC_BASE) {
            return getAllCBCDevTypes();
        } else if (category == CommandCategory.CCU_BASE) {
            return getAllCCUDevTypes();
        } else if (category == CommandCategory.DISCONNECT_BASE) {
            return getAllDisconnectDevTypes();
        } else if (category == CommandCategory.IED_BASE) {
            return getAllIEDDevTypes();
        } else if (category == CommandCategory.ION_BASE) {
            return getAllIONDevTypes();
        } else if (category == CommandCategory.LCU_BASE) {
            return getAllLCUDevTypes();
        } else if (category == CommandCategory.LOAD_GROUP_BASE) {
            return getAllLoadGroupDevTypes();
        } else if (category == CommandCategory.LP_BASE) {
            return getAllLPDevTypes();
        } else if (category == CommandCategory.MCT_BASE) {
            return getALLMCTDevTypes();
        } else if (category == CommandCategory.RTU_BASE) {
            return getAllRTUDevTypes();
        } else if (category == CommandCategory.REPEATER_BASE) {
            return getAllRepeaterDevTypes();
        } else if (category == CommandCategory.TCU_BASE) {
            return getAllTCUDevTypes();
        } else if (category == CommandCategory.STATUSINPUT_BASE) {
            return getAllStatusInputDevTypes();
        } else if (category == CommandCategory.PING_BASE) {
            return getAllPingableDevTypes();
        } else if (category == CommandCategory.MCT_4XX_SERIES_BASE) {
            return getAllMCT4XXSeriesDevTypes();
        } else if (category == CommandCategory.TWO_WAY_LCR_BASE) {
            return getAllTwoWayLCRDevTypes();
        } else if (category == CommandCategory.RFN_BASE) {
            return getALLRFNDevTypes();
        }
        return null;
    }
    
    private static List<PaoType> getAllStatusInputDevTypes() {
        
        if (CAT_STATUSINPUT_BASE_DEVTYPES == null) {
            CAT_STATUSINPUT_BASE_DEVTYPES = new ArrayList<>();
            for (PaoType paoType : PaoType.values()) {
                if (paoType == PaoType.MCT248 ||
                        paoType == PaoType.MCT250 ||
                        paoType == PaoType.MCT318 ||
                        paoType == PaoType.MCT318L ||
                        paoType == PaoType.MCT360 ||
                        paoType == PaoType.MCT370) {
                    CAT_STATUSINPUT_BASE_DEVTYPES.add(paoType);
                }
            }
        }
        
        return CAT_STATUSINPUT_BASE_DEVTYPES;
    }
    
    private static List<PaoType> getAllPingableDevTypes() {
        
        if (CAT_PING_BASE_DEVTYPES == null) {
            CAT_PING_BASE_DEVTYPES = new ArrayList<>();
            for (PaoType paoType : PaoType.values()) {
                if (paoType.isCcu() ||
                        paoType.isMct() ||
                        paoType.isRepeater() ||
                        paoType == PaoType.TCU5000 ||
                        paoType == PaoType.TCU5500 ||
                        paoType == PaoType.LCU415 ||
                        paoType == PaoType.LCU_ER ||
                        paoType == PaoType.LCU_T3026 ||
                        paoType == PaoType.LCULG ||
                        paoType == PaoType.DAVISWEATHER || 
                        paoType == PaoType.RTUILEX || 
                        paoType == PaoType.RTUWELCO ) {
                    CAT_PING_BASE_DEVTYPES.add(paoType);
                }
            }
        }
        
        return CAT_PING_BASE_DEVTYPES;
    }
    
    private static List<PaoType> getAllTCUDevTypes() {
        
        if (CAT_TCU_BASE_DEVTYPES == null) {
            CAT_TCU_BASE_DEVTYPES = new ArrayList<>();
            for (PaoType paoType : PaoType.values()) {
                if (paoType.isTcu()) {
                    CAT_TCU_BASE_DEVTYPES.add(paoType);
                }
            }
        }
        
        return CAT_TCU_BASE_DEVTYPES;
    }
    
    private static List<PaoType> getAllRepeaterDevTypes() {
        
        if (CAT_REPEATER_BASE_DEVTYPES == null) {
            CAT_REPEATER_BASE_DEVTYPES = Lists.newArrayList(PaoType.getRepeaterTypes());
        }
        
        return CAT_REPEATER_BASE_DEVTYPES;
    }

    private static List<PaoType> getAllRTUDevTypes() {
        
        if (CAT_RTU_BASE_DEVTYPES == null) {
            SetView<PaoType> rtuAndIons = Sets.union(PaoType.getRtuTypes(), PaoType.getIonTypes());
            CAT_RTU_BASE_DEVTYPES = Lists.newArrayList(rtuAndIons);
        }
        
        return CAT_RTU_BASE_DEVTYPES;
    }
    
    private static List<PaoType> getALLMCTDevTypes() {
        
        if (CAT_MCT_BASE_DEVTYPES == null) {
            CAT_MCT_BASE_DEVTYPES = Lists.newArrayList(PaoType.getMctTypes());
        }
        
        return CAT_MCT_BASE_DEVTYPES;
    }

    private static List<PaoType> getALLRFNDevTypes() {
        
        if (CAT_RFN_BASE_DEVTYPES == null) {
            CAT_RFN_BASE_DEVTYPES = Lists.newArrayList(PaoType.getRfMeterTypes());
        }
        
        return CAT_RFN_BASE_DEVTYPES;
    }

    private static List<PaoType> getAllLPDevTypes() {
        
        if (CAT_LP_BASE_DEVTYPES == null) {
            CAT_LP_BASE_DEVTYPES = new ArrayList<>();
            for (PaoType paoType : PaoType.values()) {
                if (DeviceTypesFuncs.isLoadProfile1Channel(paoType) ||
                    DeviceTypesFuncs.isLoadProfile3Channel(paoType) ||
                    DeviceTypesFuncs.isLoadProfile4Channel(paoType))
                    CAT_LP_BASE_DEVTYPES.add(paoType);
            }
        }
        
        return CAT_LP_BASE_DEVTYPES;
    }
    
    private static List<PaoType> getAllLoadGroupDevTypes() {
        
        if (CAT_LOAD_GROUP_BASE_DEVTYPES == null) {
            CAT_LOAD_GROUP_BASE_DEVTYPES = new ArrayList<>();
            for (PaoType paoType : PaoType.values()) {
                if (paoType.isLoadGroup()) {
                    CAT_LOAD_GROUP_BASE_DEVTYPES.add(paoType);
                }
            }
        }
        
        return CAT_LOAD_GROUP_BASE_DEVTYPES;
    }
    
    private static List<PaoType> getAllLCUDevTypes() {
        
        if (CAT_LCU_BASE_DEVTYPES == null) {
            CAT_LCU_BASE_DEVTYPES = new ArrayList<>();
            for (PaoType paoType : PaoType.values()) {
                if (paoType.isLcu()) {
                    CAT_LCU_BASE_DEVTYPES.add(paoType);
                }
            }
        }
        
        return CAT_LCU_BASE_DEVTYPES;
    }
    
    private static List<PaoType> getAllIEDDevTypes() {
        
        if (CAT_IED_BASE_DEVTYPES == null) {
            CAT_IED_BASE_DEVTYPES = new ArrayList<>();
            for (PaoType paoType : PaoType.values()) {
                if (paoType == PaoType.MCT360 || paoType == PaoType.MCT370) {
                    CAT_IED_BASE_DEVTYPES.add(paoType);
                }
            }
        }
        
        return CAT_IED_BASE_DEVTYPES;
    }
    
    public final static List<PaoType> getAllAlphaBaseDevTypes() {
        
        if(CAT_ALPHA_BASE_DEVTYPES == null) {
            CAT_ALPHA_BASE_DEVTYPES = new ArrayList<>();
            for (PaoType paoType : PaoType.values()) {
                if (PaoType.ALPHA_A1 == paoType ||
                        PaoType.ALPHA_A3 == paoType ||
                        PaoType.ALPHA_PPLUS == paoType) {
                    CAT_ALPHA_BASE_DEVTYPES.add(paoType);
                }
            }
        }
        
        return CAT_ALPHA_BASE_DEVTYPES;
    }
    
    public final static List<PaoType> getAllCBCDevTypes() {
        
        if (CAT_CBC_BASE_DEVTYPES == null) {
            CAT_CBC_BASE_DEVTYPES = new ArrayList<>();
            for (PaoType paoType : PaoType.values()) {
                if (paoType.isCbc()) {
                    CAT_CBC_BASE_DEVTYPES.add(paoType);
                }
            }
            CAT_CBC_BASE_DEVTYPES.add(PaoType.CAPBANK);
        }
        
        return CAT_CBC_BASE_DEVTYPES;
    }
    
    public final static List<PaoType> getAllCCUDevTypes() {
        
        if (CAT_CCU_BASE_DEVTYPES == null) {
            CAT_CCU_BASE_DEVTYPES = Lists.newArrayList(PaoType.getCcuTypes());
        }
        
        return CAT_CCU_BASE_DEVTYPES;
    }
    
    public final static List<PaoType> getAllDisconnectDevTypes() {
        
        if (CAT_DISCONNECT_BASE_DEVTYPES == null) {
            CAT_DISCONNECT_BASE_DEVTYPES = new ArrayList<>();
            for (PaoType paoType : PaoType.values()) {
                if (DeviceTypesFuncs.isDisconnectMCT(paoType)) {
                    CAT_DISCONNECT_BASE_DEVTYPES.add(paoType);
                }
            }
        }
        
        return CAT_DISCONNECT_BASE_DEVTYPES;
    }
    
    public final static List<PaoType> getAllMCT4XXSeriesDevTypes() {
        
        if (CAT_MCT_4XX_SERIES_DEVTYPES == null) {
            CAT_MCT_4XX_SERIES_DEVTYPES = new ArrayList<>();
            for (PaoType paoType : PaoType.values()) {
                if (DeviceTypesFuncs.isMCT4XX(paoType)) {
                    CAT_MCT_4XX_SERIES_DEVTYPES.add(paoType);
                }
            }
        }
        
        return CAT_MCT_4XX_SERIES_DEVTYPES;
    }
    
    public final static List<PaoType> getAllTwoWayLCRDevTypes() {
        
        if (CAT_TWO_WAY_LCR_DEVTYPES == null) {
            CAT_TWO_WAY_LCR_DEVTYPES = new ArrayList<>();
            for (PaoType paoType : PaoType.values()) {
                if (paoType.isTwoWayLcr()) {
                    CAT_TWO_WAY_LCR_DEVTYPES.add(paoType);
                }
            }
        }
        
        return CAT_TWO_WAY_LCR_DEVTYPES;
    }
    
    public final static List<PaoType> getAllIONDevTypes() {
        
        if (CAT_ION_BASE_DEVTYPES == null) {
            CAT_ION_BASE_DEVTYPES = Lists.newArrayList(PaoType.getIonTypes());
        }
        
        return CAT_ION_BASE_DEVTYPES;
    }
}
