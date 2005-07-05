/*---------------------------------------------------------------------------
        Filename:  ccid.h
                        
        Programmer:  Josh Wolberg
        
        Description:    Header file containing id numbers for various
                        Cti classes that inherit from RWCollectable.

        Initial Date:  9/6/2000
        
        Revision Date: 8/29/2001
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/

/* Various message classes */
#define CTICCMESSAGE_ID             500
#define CTICCSUBSTATIONBUS_MSG_ID   501
#define CTICCSUBSTATIONBUS_ID       502
#define CTICCFEEDER_ID              503
#define CTICCCAPBANK_ID             504
#define CTICCCOMMAND_ID             505
#define CTICCSHUTDOWN_ID            506
#define CTICCSTATE_ID               507
#define CTICCCAPBANKSTATES_MSG_ID   508
#define CTICCGEOAREAS_MSG_ID        509
#define CTICCERROR_ID               510
#define CTICCDONE_ID                511
#define CTICCUPDATE_ID              512
#define CTICCCAPBANKMOVEMSG_ID      513
#define CTICCSUBVERIFICATIONMSG_ID  514
#define CTIPAOSCHEDULEMSG_ID        515


/*#define CTICCSTRATEGYLIST_ID     501
#define CTICCSTRATEGYLIST_MSG_ID     502
#define CTICCSTRATEGY_ID     503
#define CTICAPBANK_ID     504
#define CTICCCOMMAND_ID     505
#define CTICCSHUTDOWN_ID    506
#define CTICCSTATE_ID    507
#define CTICCSTATELIST_MSG_ID    508
#define CTICCAREALIST_MSG_ID    509
#define CTICCERROR_ID    511
#define CTICCDONE_ID    512
#define CTICCUPDATE_ID    513*/


// square root of 3 for power factor calculations
#define SQRT3               1.7320508075688772935274463415059


/* Various debug levels */
#define CC_DEBUG_NONE              0x00000000
#define CC_DEBUG_STANDARD          0x00000001
#define CC_DEBUG_POINT_DATA        0x00000002
#define CC_DEBUG_DATABASE          0x00000004
#define CC_DEBUG_CLIENT            0x00000008
#define CC_DEBUG_CONTROL_PARAMS    0x00000010
//#define CC_DEBUG_                  0x00000000
#define CC_DEBUG_EXTENDED          0x10000000


extern RWDBDateTime gInvalidRWDBDateTime;
extern ULONG gInvalidRWDBDateTimeSeconds;
