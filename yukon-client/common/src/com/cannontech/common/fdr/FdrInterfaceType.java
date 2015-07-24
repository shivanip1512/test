package com.cannontech.common.fdr;

import java.util.Comparator;
import java.util.List;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.Lists;

public enum FdrInterfaceType implements DatabaseRepresentationSource, DisplayableEnum {
    INET(1, new FdrInterfaceOption[] {
                FdrInterfaceOption.INET_DEVICE,
                FdrInterfaceOption.INET_POINT,
                FdrInterfaceOption.INET_DESTINATION_SOURCE
            },
            new FdrDirection[] {
                FdrDirection.SEND, 
                FdrDirection.SEND_FOR_CONTROL, 
                FdrDirection.RECEIVE, 
                FdrDirection.RECEIVE_FOR_CONTROL
            }),
    ACS(2, new FdrInterfaceOption[] {
                FdrInterfaceOption.ACS_CATEGORY,
                FdrInterfaceOption.ACS_REMOTE,
                FdrInterfaceOption.ACS_POINT
            },
            new FdrDirection[] {
                FdrDirection.SEND, 
                FdrDirection.SEND_FOR_CONTROL, 
                FdrDirection.RECEIVE, 
                FdrDirection.RECEIVE_FOR_CONTROL
            }),
    VALMET(3, new FdrInterfaceOption[] {
                FdrInterfaceOption.VALMET_POINT
            },
            new FdrDirection[] {
                FdrDirection.SEND, 
                FdrDirection.SEND_FOR_CONTROL, 
                FdrDirection.RECEIVE, 
                FdrDirection.RECEIVE_FOR_CONTROL, 
                FdrDirection.RECEIVE_FOR_ANALOG_OUTPUT
            }),
    CYGNET(4, new FdrInterfaceOption[] {
                FdrInterfaceOption.CYGNET_POINTID
            },
            new FdrDirection[] {
                FdrDirection.SEND, 
                FdrDirection.SEND_FOR_CONTROL, 
                FdrDirection.RECEIVE, 
                FdrDirection.RECEIVE_FOR_CONTROL
            }),
    STEC(5, new FdrInterfaceOption[] {
                FdrInterfaceOption.STEC_POINT
            },
            new FdrDirection[] {
                FdrDirection.RECEIVE
            }),
    RCCS(6, new FdrInterfaceOption[] {
                FdrInterfaceOption.RCCS_DEVICE,
                FdrInterfaceOption.RCCS_POINT,
                FdrInterfaceOption.RCCS_DESTINATION_SOURCE
            },
            new FdrDirection[] {
                FdrDirection.SEND, 
                FdrDirection.SEND_FOR_CONTROL, 
                FdrDirection.RECEIVE, 
                FdrDirection.RECEIVE_FOR_CONTROL
            }),
    TRISTATE(7, new FdrInterfaceOption[] {
                FdrInterfaceOption.TRISTATE_POINT
            },
            new FdrDirection[] {
                FdrDirection.RECEIVE
            }),
    RDEX(8, new FdrInterfaceOption[] {
                FdrInterfaceOption.RDEX_TRANSLATION,
                FdrInterfaceOption.RDEX_DESTINATION_SOURCE
            },
            new FdrDirection[] {
                FdrDirection.SEND, 
                FdrDirection.SEND_FOR_CONTROL, 
                FdrDirection.RECEIVE, 
                FdrDirection.RECEIVE_FOR_CONTROL
            }),
    SYSTEM(9, new FdrInterfaceOption[] {
                FdrInterfaceOption.SYSTEM_CLIENT
            },
            new FdrDirection[] {
                FdrDirection.LINK_STATUS
            }),
    DSM2IMPORT(10, new FdrInterfaceOption[] {
                FdrInterfaceOption.DSM2IMPORT_POINT
            },
            new FdrDirection[] {
                FdrDirection.RECEIVE, 
                FdrDirection.RECEIVE_FOR_CONTROL
            }),
    TELEGYR(11, new FdrInterfaceOption[] {
                FdrInterfaceOption.TELEGYR_POINT,
                FdrInterfaceOption.TELEGYR_INTERVAL_SEC
            },
            new FdrDirection[] {
                FdrDirection.RECEIVE, 
                FdrDirection.RECEIVE_FOR_CONTROL
            }),
    TEXTIMPORT(12, new FdrInterfaceOption[] {
                FdrInterfaceOption.TEXTIMPORT_POINT_ID,
                FdrInterfaceOption.TEXTIMPORT_DRIVEPATH,
                FdrInterfaceOption.TEXTIMPORT_FILENAME
            },
            new FdrDirection[] {
                FdrDirection.RECEIVE, 
                FdrDirection.RECEIVE_FOR_CONTROL, 
                FdrDirection.RECEIVE_FOR_ANALOG_OUTPUT
            }),
    TEXTEXPORT(13, new FdrInterfaceOption[] {
                FdrInterfaceOption.TEXTEXPORT_POINT_ID
            },
            new FdrDirection[] {
                FdrDirection.SEND
            }),       
    //ID 14 NOT USED
    //ID 15 NOT USED
    LODESTAR_STD(16, new FdrInterfaceOption[] {
                FdrInterfaceOption.LODESTAR_STD_CUSTOMER,
                FdrInterfaceOption.LODESTAR_STD_CHANNEL,
                FdrInterfaceOption.LODESTAR_STD_DRIVEPATH,
                FdrInterfaceOption.LODESTAR_STD_FILENAME
            },
            new FdrDirection[] {
                FdrDirection.RECEIVE
            }),
    LODESTAR_ENH(17, new FdrInterfaceOption[] {
                FdrInterfaceOption.LODESTAR_ENH_CUSTOMER,
                FdrInterfaceOption.LODESTAR_ENH_CHANNEL,
                FdrInterfaceOption.LODESTAR_ENH_DRIVEPATH,
                FdrInterfaceOption.LODESTAR_ENH_FILENAME
            },
            new FdrDirection[] {
                FdrDirection.RECEIVE
            }),
    DSM2FILEIN(18, new FdrInterfaceOption[] {
                FdrInterfaceOption.DSM2FILEIN_OPTION_NUMBER,
                FdrInterfaceOption.DSM2FILEIN_POINT_ID
            },
            new FdrDirection[] {
                FdrDirection.RECEIVE, 
                FdrDirection.RECEIVE_FOR_CONTROL
            }),
    XA21LM(19, new FdrInterfaceOption[] {
                FdrInterfaceOption.XA21LM_TRANSLATION
            },
            new FdrDirection[] {
                FdrDirection.RECEIVE, 
                FdrDirection.SEND
            }),
    BEPC(20, new FdrInterfaceOption[] {
                FdrInterfaceOption.BEPC_COOP_ID,
                FdrInterfaceOption.BEPC_FILENAME
            },
            new FdrDirection[] {
                FdrDirection.SEND
            }),
    PI(21, new FdrInterfaceOption[] {
                FdrInterfaceOption.PI_TAG_NAME,
                FdrInterfaceOption.PI_PERIOD_SEC
            },
            new FdrDirection[] {
                FdrDirection.RECEIVE
            }),
    LIVEDATA(22, new FdrInterfaceOption[] {
                FdrInterfaceOption.LIVEDATA_ADDRESS,
                FdrInterfaceOption.LIVEDATA_DATA_TYPE
            },
            new FdrDirection[] {
                FdrDirection.RECEIVE
            }),
    ACSMULTI(23, new FdrInterfaceOption[] {
                FdrInterfaceOption.ACSMULTI_CATEGORY,
                FdrInterfaceOption.ACSMULTI_REMOTE,
                FdrInterfaceOption.ACSMULTI_POINT,
                FdrInterfaceOption.ACSMULTI_DESTINATION_SOURCE
            },
            new FdrDirection[] {
                FdrDirection.SEND, 
                FdrDirection.SEND_FOR_CONTROL, 
                FdrDirection.RECEIVE, 
                FdrDirection.RECEIVE_FOR_CONTROL
            }),
    WABASH(24, new FdrInterfaceOption[] {
                FdrInterfaceOption.WABASH_SCHEDNAME,
                FdrInterfaceOption.WABASH_PATH,
                FdrInterfaceOption.WABASH_FILENAME
            },
            new FdrDirection[] {
                FdrDirection.SEND
            }),
    TRISTATESUB(25, new FdrInterfaceOption[] {
                FdrInterfaceOption.TRISTATESUB_POINT
            },
            new FdrDirection[] {
                FdrDirection.RECEIVE, 
                FdrDirection.SEND
            }),
    OPC(26, new FdrInterfaceOption[] {
                FdrInterfaceOption.OPC_SERVER_NAME,
                FdrInterfaceOption.OPC_OPC_GROUP,
                FdrInterfaceOption.OPC_OPC_ITEM
            },
            new FdrDirection[] {
                FdrDirection.RECEIVE, 
                FdrDirection.SEND
            }),
    MULTISPEAK_LM(27, new FdrInterfaceOption[] {
                FdrInterfaceOption.MULTISPEAK_LM_OBJECTID
            },
            new FdrDirection[] {
                FdrDirection.RECEIVE
            }),
    DNPSLAVE(28, new FdrInterfaceOption[] {
                FdrInterfaceOption.DNPSLAVE_MASTERID,
                FdrInterfaceOption.DNPSLAVE_SLAVEID,
                FdrInterfaceOption.DNPSLAVE_OFFSET,
                FdrInterfaceOption.DNPSLAVE_DESTINATION_SOURCE,
                FdrInterfaceOption.DNPSLAVE_MULTIPLIER
            },
            new FdrDirection[] {
                FdrDirection.SEND,
                FdrDirection.RECEIVE_FOR_CONTROL
            }),
    VALMETMULTI(29, new FdrInterfaceOption[] {
                FdrInterfaceOption.VALMETMULTI_POINT,
                FdrInterfaceOption.VALMETMULTI_PORT
            },
            new FdrDirection[] {
                FdrDirection.SEND, 
                FdrDirection.SEND_FOR_CONTROL, 
                FdrDirection.RECEIVE, 
                FdrDirection.RECEIVE_FOR_CONTROL, 
                FdrDirection.RECEIVE_FOR_ANALOG_OUTPUT
            });
    
    private static final String baseKey = "yukon.common.fdr.interface.";
    
    private final int position;
    private final FdrInterfaceOption[] options;
    private final FdrDirection[] supportedDirections;
    
    /**
     * This comparator is designed to let us sort FdrInterfaceTypes by name, rather than 
     * natural enum order.
     */
    public static final Comparator<FdrInterfaceType> alphabeticalComparator = new Comparator<FdrInterfaceType>() {
        @Override
        public int compare(FdrInterfaceType typeOne, FdrInterfaceType typeTwo) {
            return typeOne.name().compareTo(typeTwo.name());
        }
    };
    
    FdrInterfaceType(int position, FdrInterfaceOption[] options, FdrDirection[] supportedDirections) {
        this.position = position;
        this.options = options;
        this.supportedDirections = supportedDirections;
    }
    
    /**
     * @return the integer id representing this interface.
     */
    public int getValue() {
        return position;
    }
    
    /**
     * @return the options used by this interface.
     */
    public FdrInterfaceOption[] getInterfaceOptions() {
        return options;
    }
    
    /**
     * @return the options used by this interface, as a List
     */
    public List<FdrInterfaceOption> getInterfaceOptionsList() {
        return Lists.newArrayList(options);
    }
    
    /**
     * This or the lack of this indicates a special case where the Destination field is utilized 
     * instead of defaulted to the interface name.
     * @return true if this interface uses the special case where the Destination value is contained 
     * in the Options.
     */
    public boolean isDestinationInOptions() {
        return getDestinationOption() != null;
    }
    
    /**
     * @return the destination option if this interface has one, otherwise null.
     */
    public FdrInterfaceOption getDestinationOption() {
        for(FdrInterfaceOption option : options) {
            if(option.isDestinationOption()) {
                return option;
            }
        }
        return null;
    }
    
    /**
     * @return an array containing all FdrDirections supported by this interface.
     */
    public FdrDirection[] getSupportedDirections() {
        return supportedDirections;
    }
    
    /**
     * @return a list containing all FdrDirections supported by this interface.
     */
    public List<FdrDirection> getSupportedDirectionsList() {
        return Lists.newArrayList(getSupportedDirections());
    }
    
    /**
     * @return the interface with the specified integer ID, or null if the ID is invalid
     */
    public static FdrInterfaceType getById(int id) {
        for (FdrInterfaceType interfaceType : FdrInterfaceType.values()) {
            if (id == interfaceType.getValue()) {
                return interfaceType;
            }
        }
        return null;
    }
    
    public static List<FdrInterfaceType> valuesList() {
        return Lists.newArrayList(FdrInterfaceType.values());
    }
    
    @Override
    public Object getDatabaseRepresentation() {
        return toString();
    }

    @Override
    public String getFormatKey() {
        return baseKey + name();
    }
}
