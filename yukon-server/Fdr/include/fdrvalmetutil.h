#pragma once

#include "pointdefs.h"
#include "fdrpointlist.h"


namespace Fdr {
namespace Valmet {

// global defines
#define VALMET_PORTNUMBER       1666


/* Definitions and structures used to share data with VALMET */

/* Valmet link quality definition masks */
#define VALMET_NORMAL           0x0000
#define VALMET_PLUGGED          0x0001
#define VALMET_DATAINVALID      0x0002
#define VALMET_UNREASONABLE     0x0004
#define VALMET_MANUALENTRY      0x0008
#define VALMET_OUTOFSCAN        0x0010
#define VALMET_UNSOLICITED      0x0020

/*
NOTE:  All data limit violations will be handled by the receiving system
*/
#pragma pack(push, valmet_packing, 1)

template<unsigned int nameLength>
struct ValmetInterface
{
    USHORT Function;
    CHAR TimeStamp[18];
    union {
        /* Null Message     Function = 0 */

        /* Value Message   Function = 101 */
        struct {
            CHAR Name[nameLength];
            union {
                FLOAT Value;
                ULONG LongValue;
            };
            USHORT Quality;
        } Value;

        /* Status Message   Function = 102 */
        struct {
            CHAR Name[nameLength];
            union {
                USHORT Value;
                ULONG LongValue;
            };
            USHORT Quality;
        } Status;

        /* Control Message   Function = 103 */
        struct {
            CHAR Name[nameLength];
            union {
                USHORT Value;
                ULONG LongValue;
            };
        } Control;

        /* Force Scan Message  Function = 110 */
        struct {
            CHAR Name[nameLength];
        } ForceScan;

        /* TimeSync         Function = 401 */
    };

    /* Force long alignment */
    USHORT Spare;
};

#pragma pack(pop, valmet_packing)     // Restore the prior packing alignment..

enum {  Valmet_Invalid = 0,
        Valmet_Open = 1,
        Valmet_Closed=2,
        Valmet_Indeterminate=3};

typedef ValmetInterface<16> ValmetInterface_t;
typedef ValmetInterface<32> ValmetExtendedInterface_t;


USHORT        ForeignToYukonQuality (USHORT aQuality);
int           ForeignToYukonStatus (USHORT aStatus);
CtiTime       ForeignToYukonTime (PCHAR aTime, int timeStampReasonability, bool aTimeSyncFlag = false);
std::string   ForeignQualityToString(USHORT quality);

std::string   YukonToForeignTime (CtiTime aTimeStamp);
USHORT        YukonToForeignQuality (const CtiFDRPoint &p);
USHORT        YukonToForeignStatus (int aStatus);

}
}
