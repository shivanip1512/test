#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   Cvs
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:40 $
*  $Workfile: Cvs.h $

  Notes: Current Value Service Operations header file

 --------------------------------------------------------------------------
 Copyright Visual Systems Inc., All Rights Reserved.
 --------------------------------------------------------------------------

 See bottom of file for revision history.

* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __CVS_H
#define __CVS_H

#ifdef _MSC_VER
#pragma pack(push, __CVS__PACKING__)
#pragma pack(1)
#endif // _MSC_VER

#include "CygNetDefs.h"


//*****************************************************************************
// CygNet NAME FOR A REAL-TIME POINT
//*****************************************************************************

struct CVS_POINT
{
    char site[8];
    char service[8];
    char point[8];
};

// Extended version
struct CVS_POINT_EX
{
    char site[8];
    char service[8];
    char szLongId[32];
};

//*****************************************************************************
// SERVER DATA RECORD STRUCTURE
//*****************************************************************************

struct RTR
{
    unsigned short number;              // point number in point id order
    unsigned short status;              // status word
    long           time;                // update time
    char           id[8];               // point id string
    char           desc[24];            // point description string
    char           val[16];             // point value string
    char           units[8];            // point units string
};

// Extended version
#define CVS_VALUE_TYPE_STRING       1
#define CVS_VALUE_TYPE_DWORD        2
#define CVS_VALUE_TYPE_DOUBLE       3

#define CVS_TIMESTAMP_TYPE_OLEDATE  1
#define CVS_TIMESTAMP_TYPE_UCT      2

struct RTR_EX
{
    unsigned short  wNumber;            // point number in long id order
    unsigned short  wBaseStatus;        // Same as RTR.status
    unsigned long   dwUserStatus;       // User Application (not yet implemented - always 0)
    unsigned short  wTimeStampType;     // 1-OLEDATE, 2-UCT
    union
    {
        double      dOleDate;           // WIN32 OLE Variant DateTime ( (+-days) 0.0 == 30 December 1899, midnight)
        long        lUCTStamp[2];       // Univeral Cooridated Time ( seconds since 1/1/70 )
    };
    char            szShortId[9];       // null-terminated short id string
    char            szLongId[32];       // null-terminated long id string
    char            szDescription[25];  // null-terminated version of RTR.desc
    char            szUnits[9];         // null-terminated vserion of RTR.units
    unsigned short  wValueType;         // 1-string, 2-DWORD, 3-Double (string only implementation for now)
    union
    {
        char           szValue[17];     // null-terminated version of RTR.val
        unsigned long  dwValue[4];      // array of 4 DWORD's overlaying val
        double         dValue[2];       // array of 2 Doubles overlaying val
    };
};                                                               // Total size = 112 bytes

//*****************************************************************************
// SERVER SHORT DATA RECORD STRUCTURE
//*****************************************************************************

struct SRTR
{
    unsigned short number;             // point number in point id order
    unsigned short status;             // status word
    long           time;               // update time
    char           val[16];            // point value string
};

// Extended version
struct SRTR_EX
{
    unsigned short     wNumber;           // point number in long id order
    unsigned short     wBaseStatus;       // Same as RTR.status
    unsigned long      dwUserStatus;      // User Application (not yet implemented - always 0)
    unsigned short     wTimeStampType;    // 1-OLEDATE, 2-UCT
    union
    {
        double         dOleDate;          // NT OLE Variant DateTime ( (+-days) 0.0 == 30 December 1899, midnight)
        long           lUCTStamp[2];      // Univeral Cooridated Time ( seconds since 1/1/70 )
    };
    unsigned short     wValueType;        // 1-string, 2-DWORD, 3-Double (string only implementation for now)
    union
    {
        char           szValue[17];    // null-terminated version of RTR.val
        unsigned long  dwValue[4];     // array of 4 DWORD's overlaying val
        double         dValue[2];      // array of 2 Doubles overlaying val
    };
};                                     // Total size = 35


//*****************************************************************************
// DEVICE POINT RECORD STRUCTURE
//*****************************************************************************

struct CVS_DEVICE_POINT
{
    RTR_EX          rtrEx;
    char            strFacilityId[20];
    char            strUniformDataCode[10];
};


//*****************************************************************************
// POINT STATUS FLAG BIT DEFINITIONS
//*****************************************************************************

// GENERAL BITS - SYSTEM SET
#define RTSTAT_INITED         0x0001  // has been initialized                     BIT 0
#define RTSTAT_UPDATED        0x0002  // has been updated                         BIT 1
#define RTSTAT_UNRELIABLE     0x0004  // data is bad                              BIT 2
#define RTSTAT_REPEAT         0x0800  // toggle this flag to "force" a VHS update BIT 11
#define RTSTAT_DIGITAL        0x1000  // digital/analog                           BIT 12
#define RTSTAT_OUT            0x2000  // in/out                                   BIT 13
#define RTSTAT_STRING         0x0400  // string or enumeration point              BIT 10


// ANALOG ALARM BITS - AI and AO points - OBSOLETE legacy use only
//#define RTSTAT_OOR            0x0008  // point is out of range - either hi or low BIT 3
#define RTSTAT_LL_ALARM       0x0010  // low alarm                                BIT 4
#define RTSTAT_L_ALARM        0x0020  // low warning                              BIT 5
#define RTSTAT_H_ALARM        0x0040  // high warning                             BIT 6
#define RTSTAT_HH_ALARM       0x0080  // high alarm                               BIT 7
#define RTSTAT_DEVIATION      0x0100  // high deviation from previous value       BIT 8
#define RTSTAT_WARNING        0x0080  // digital point is in warning (ALC use)    BIT 7

// ANALOG ALARM BITS - AI and AO points
#define RTSTAT_OOR            0x0008  // point is out of range - either hi or low BIT 3
#define RTSTAT_LOW_ALARM      0x0010  // low alarm                                BIT 4
#define RTSTAT_LOW_WARNING    0x0020  // low warning                              BIT 5
#define RTSTAT_HIGH_WARNING   0x0040  // high warning                             BIT 6
#define RTSTAT_HIGH_ALARM     0x0080  // high alarm                               BIT 7
#define RTSTAT_HIGH_DEVIATION 0x0100  // high deviation from previous value       BIT 8


// DIGITAL ALARM BITS - DI and DO points
// UNUSED                     0x0008                                              BIT 3
#define RTSTAT_CHATTERING     0x0010  // digital point is chattering              BIT 4
// UNUSED                     0x0020                                              BIT 5
#define RTSTAT_DIGITAL_ALARM  0x0040  // digital point is in alarm                BIT 6
#define RTSTAT_DIGITAL_WARNING 0x0080  // digital point is in warning (ALC use)    BIT 7
// UNUSED                     0x0100                                              BIT 8


// STRING ALARM BITS - SI and SO points
#define RTSTAT_STRING_STATE_1 0x0008  // state 1 alarm bit                        BIT 3
#define RTSTAT_STRING_STATE_2 0x0010  // state 2 alarm bit                        BIT 4
#define RTSTAT_STRING_STATE_3 0x0020  // state 3 alarm bit                        BIT 5
#define RTSTAT_STRING_STATE_4 0x0040  // state 4 alarm bit                        BIT 6
#define RTSTAT_STRING_STATE_5 0x0080  // state 5 alarm bit                        BIT 7
#define RTSTAT_STRING_STATE_6 0x0100  // state 6 alarm bit                        BIT 8


// ENUMERATION ALARM BITS - EN and EO points
#define RTSTAT_ENUM_STATE_1   0x0008  // state 1 alarm bit                        BIT 3
#define RTSTAT_ENUM_STATE_2   0x0010  // state 2 alarm bit                        BIT 4
#define RTSTAT_ENUM_STATE_3   0x0020  // state 3 alarm bit                        BIT 5
#define RTSTAT_ENUM_STATE_4   0x0040  // state 4 alarm bit                        BIT 6
#define RTSTAT_ENUM_STATE_5   0x0080  // state 5 alarm bit                        BIT 7
#define RTSTAT_ENUM_STATE_6   0x0100  // state 6 alarm bit                        BIT 8


// APPLICATION DEFINED BITS
#define RTSTAT_APPL_DEF_1     0x0200  // application defined                      BIT 9
#define RTSTAT_APPL_DEF_2     0x4000  // application defined                      BIT 14
#define RTSTAT_APPL_DEF_3     0x8000  // application defined                      BIT 15



// Some useful bit masks
#define RTSTAT_UNUSED         0x0000  // not yet initialized
#define RTSTAT_TYPE_MASK      0xf000  // point type mask
#define RTSTAT_ANALOG_IN      0x0000  // analog point type
#define RTSTAT_DIGITAL_IN     0x1000  // digital point type
#define RTSTAT_ANALOG_OUT     0x2000  // analog point type
#define RTSTAT_DIGITAL_OUT    0x3000  // digital point type


//*****************************************************************************
// MESSAGE PARAMETER DEFINITIONS
//*****************************************************************************

// Alphabetical search defines

#define RT_FORWARD              0x0000  // forward  greater than or equal to search
#define RT_BACKWARD             0x0001  // backward less    than or equal to search

#define RT_FORWARD_STRICT       0x0002  // forward  strict greater than search
#define RT_BACKWARD_STRICT      0x0003  // backward strict less    than search

#define RT_DIRECTION            0x0001  // direction bit
#define RT_STRICT               0x0002  // strict inequality search bit

// Tag type defines

#define RT_TAG_TYPE_POINTID          1  // tag is a PointId
#define RT_TAG_TYPE_POINTIDLONG      2  // tag is a PointIdLong

//*****************************************************************************
// SERVER CONTROL REQUEST HEADER RECORD STRUCTURE
//*****************************************************************************

struct RTCH
{
    unsigned short user_id_flag;        // always 0xffff
    unsigned short ctrl_count;          // number of control recs to follow
    char           usr_id[60];          // user id string, only 60 bytes to align with RTR length
};

//*****************************************************************************
// ACKNOWLDGE RECORD DATA
//*****************************************************************************

struct RTAD
{
    char          user_id[64];          // user id string
    unsigned char point_id[8];          // point id string
};

//*****************************************************************************
// SERVER REFERENCE ENTRIES
//*****************************************************************************

struct RTID
{
    char    name[64];                   // name of service
    union
    {
        char    qualifier[8];       // additional qualifier (if needed)
        long    lQualifier[2];          // overlay makes 2 longs from char[8]
    };
};

struct RT_SYS_REFS
{
    RTID  points;           // Site/Service for points
    RTID  security;         // Security Service name and application
    RTID  alarm;            // Alarm service name
    RTID  history;          // History service name
    RTID  config;           // Point configuration manager service name
    RTID  config_table;     // Additional point config info if needed
    RTID  admin;            // System Admin service name
    RTID  sys_log;          // System Logging service name
    RTID  gns;              // General Notification Service Name
    RTID  aux2;             // Spare
};

//*****************************************************************************
// SERVER REQUEST TYPES (base 0)
//*****************************************************************************

#define RT_GET_REC                         CVS_MESSAGE_BASE +  1      // get records request
#define RT_GET_SHORT_REC                   CVS_MESSAGE_BASE +  2      // get short records request
#define RT_GET_NAMED_REC                   CVS_MESSAGE_BASE +  3      // get named records request
#define RT_GET_SHORT_NAMED_REC             CVS_MESSAGE_BASE +  4      // get short named records request
#define RT_SET_RECORD                      CVS_MESSAGE_BASE +  5      // set record values
#define RT_ACK_RECORD                      CVS_MESSAGE_BASE +  6      // acknowledge alarm - ALC use only
#define RT_GET_REFS                        CVS_MESSAGE_BASE +  7      // get server reference list
#define RT_MATCH_NAME                      CVS_MESSAGE_BASE +  8      // get first match for name pattern

// Get by description messages made obsolete 1-2-98
//#define RT_GET_DESC                      CVS_MESSAGE_BASE +  9      // get records by desc order request
//#define RT_GET_SHORT_DESC                CVS_MESSAGE_BASE + 10      // get short records by desc order request
//#define RT_MATCH_DESC                    CVS_MESSAGE_BASE + 11      // get first match for desc pattern

#define RT_ALPHA_GET                       CVS_MESSAGE_BASE + 12      // get RTR     records by PointId alphabetically
#define RT_GET_RTREX                       CVS_MESSAGE_BASE + 13      // get RTR_EX  records by row index list
#define RT_GET_SRTREX                      CVS_MESSAGE_BASE + 14      // get SRTR_EX records by row index list
#define RT_GET_NAMED_RTREX                 CVS_MESSAGE_BASE + 15      // get RTR_EX  records by PointIdLong list
#define RT_GET_NAMED_SRTREX                CVS_MESSAGE_BASE + 16      // get SRTR_EX records by PointIdLong list
#define RT_SET_RTREX                       CVS_MESSAGE_BASE + 17      // set RTR_EX  records by PointIdLong list
#define RT_MATCH_NAMED_RTREX               CVS_MESSAGE_BASE + 18      // get RTR_EX  record  by PointIdLong pattern
#define RT_GET_SNAMED_RTREX                CVS_MESSAGE_BASE + 20      // get RTR_EX  records by PointId list

#define RT_ALPHA_GET_RTREX                 CVS_MESSAGE_BASE + 19      // use RT_ALPHA_GET_RTREX_BY_POINTIDLONG
#define RT_ALPHA_GET_RTREX_BY_POINTIDLONG  CVS_MESSAGE_BASE + 19      // get RTR_EX  records by PointIdLong alphabetically
#define RT_ALPHA_GET_RTREX_BY_POINTID      CVS_MESSAGE_BASE + 21      // get RTR_EX  records by PointId alphabetically

#define RT_GET_RTREX_BY_TAG                CVS_MESSAGE_BASE + 22      // get RTR_EX  by Tag (PointId or PointIdLong) list
#define RT_GET_SRTREX_BY_TAG               CVS_MESSAGE_BASE + 23      // get SRTR_EX by Tag (PointId or PointIdLong) list

//*****************************************************************************
// CVS Message Error Codes (base 11000)
//*****************************************************************************

#define RT_SET_ERROR                       CVS_ERROR_BASE + 0 // general error placed on the resp header
#define RT_SET_QUEUE_FULL                  CVS_ERROR_BASE + 1 // In the resp error array and/or on the header
#define RT_SET_ACCESS_DENIED               CVS_ERROR_BASE + 2 // In the resp error array and/or on the header
#define RT_SET_POINT_NOT_FOUND             CVS_ERROR_BASE + 3 // In the resp error array
#define RT_MATCH_POINT_NOT_FOUND           CVS_ERROR_BASE + 4 // general error placed on the resp header


//*****************************************************************************
// SERVER REQUEST HEADER STRUCTURE
//*****************************************************************************


struct RTRH
{
    unsigned short type;                // request type
    unsigned short count;               // number of items
    unsigned short err;                 // error code (filled on return)
    unsigned short p4;                  // (not used)
};

//extended version
struct RTRH_EX
{
    unsigned short    wType;                      // request type
    unsigned short    wCount;                     // number of items
    unsigned short    wError;                     // error code (filled on return)
    unsigned short    wTotalPoints;               // total number of points in the CVS
    unsigned long     dwRequestId;                // used for DCLICall support
};

//*****************************************************************************
// GET RECORDS NETWORK REQUEST AND RESPONSE STRUCTURES
//*****************************************************************************

struct RT_GET_REC_REQ
{
    enum            { eMAX_COUNT = 31 };
    RTRH            header;
    unsigned short  recs[eMAX_COUNT];
};

struct RT_GET_REC_RESP
{
    enum            { eMAX_COUNT = RT_GET_REC_REQ::eMAX_COUNT };
    RTRH            header;
    RTR             recs[eMAX_COUNT];
};

//*****************************************************************************
// GET SHORT RECORDS NETWORK REQUEST AND RESPONSE STRUCTURES
//*****************************************************************************

struct RT_GET_SHORT_REC_REQ
{
    enum            { eMAX_COUNT = 84 };
    RTRH            header;
    unsigned short  recs[eMAX_COUNT];
};

struct RT_GET_SHORT_REC_RESP
{
    enum            { eMAX_COUNT = RT_GET_SHORT_REC_REQ::eMAX_COUNT };
    RTRH            header;
    SRTR            recs[eMAX_COUNT];
};

//*****************************************************************************
// GET NAMED RECORDS NETWORK REQUEST AND RESPONSE STRUCTURES
//*****************************************************************************

struct RT_GET_NAMED_REC_REQ
{
    enum            { eMAX_COUNT = 31 };
    RTRH            header;
    char            names[eMAX_COUNT][8];
};

struct RT_GET_NAMED_REC_RESP
{
    enum            { eMAX_COUNT = RT_GET_NAMED_REC_REQ::eMAX_COUNT };
    RTRH            header;
    RTR             recs[eMAX_COUNT];
};

//*****************************************************************************
// GET SHORT NAMED RECORDS NETWORK REQUEST AND RESPONSE STRUCTURES
//*****************************************************************************

struct RT_GET_SHORT_NAMED_REC_REQ
{
    enum            { eMAX_COUNT = 84 };
    RTRH            header;
    char            names[eMAX_COUNT][8];
};

struct RT_GET_SHORT_NAMED_REC_RESP
{
    enum            { eMAX_COUNT = RT_GET_SHORT_NAMED_REC_REQ::eMAX_COUNT };
    RTRH            header;
    SRTR            recs[eMAX_COUNT];
};

//*****************************************************************************
// SET RECORDS NETWORK REQUEST AND RESPONSE STRUCTURES
//*****************************************************************************

struct RT_SET_REC_REQ
{
    enum            { eMAX_COUNT = 30 };
    RTRH            header;
    union
    {
        char        msg[eMAX_COUNT+1][64];

        struct RT_SET_MSG
        {
            RTCH    RTControlHeader;
            RTR     Rtr[eMAX_COUNT];
        } RTSetMsg;
    };
};

struct RT_SET_REC_RESP
{
    enum    { eMAX_COUNT = RT_SET_REC_REQ::eMAX_COUNT };
    RTRH    header;
    unsigned short    wPointError[eMAX_COUNT];
};

//*****************************************************************************
// ACKNOWLEDGE RECORD NETWORK REQUEST AND RESPONSE STRUCTURES
//*****************************************************************************

struct RT_ACK_REC_REQ
{
    RTRH  header;
    RTAD  ack_data;
};

struct RT_ACK_REC_RESP
{
    RTRH          header;
};

//*****************************************************************************
// GET REFS NETWORK REQUEST AND RESPONSE STRUCTURES
//*****************************************************************************

struct RT_GET_REFS_REQ
{
    RTRH  header;
};

struct RT_GET_REFS_RESP
{
    RTRH        header;
    RT_SYS_REFS ref;
};

//*****************************************************************************
// MATCH NAME NETWORK REQUEST AND RESPONSE STRUCTURES
//*****************************************************************************

struct RT_MATCH_NAME_REQ
{
    RTRH header;
    char id[8];
};

struct RT_MATCH_NAME_RESP
{
    RTRH header;
    RTR  rec;
};

//*****************************************************************************
//  ALPHA INDEXED GET RECORDS NETWORK REQUEST AND RESPONSE STRUCTURES
//*****************************************************************************

struct RT_ALPHA_GET_REQ
{
    enum            { eMAX_COUNT = 31 } ;
    RTRH            header;
    unsigned long   req_id;
    unsigned short  direction;   // RT_FORWARD, RT_BACKWARD
    char            id[8];
};

struct RT_ALPHA_GET_RESP
{
    enum            { eMAX_COUNT = RT_ALPHA_GET_REQ::eMAX_COUNT };
    RTRH            header;
    unsigned long   req_id;
    unsigned short  direction;   // RT_FORWARD, RT_BACKWARD
    RTR             rec[eMAX_COUNT];
};


//*****************************************************************************
//  LONG ID AND EXT RTR NETWORK REQUEST AND RESPONSE STRUCTURES
//*****************************************************************************

struct RT_GET_RTREX_REQ
{
    enum                    { eMAX_COUNT = 36 };
    RTRH_EX                 Header;
    unsigned short          wTimeStampType;
    unsigned short          wNumber[eMAX_COUNT];
};

struct RT_GET_RTREX_RESP
{
    enum                    { eMAX_COUNT = RT_GET_RTREX_REQ::eMAX_COUNT };
    RTRH_EX                 Header;
    RTR_EX                  RtrEx[eMAX_COUNT];
};

/*---------------------------------------------------------------------------*/

struct RT_GET_SRTREX_REQ
{
    enum                    { eMAX_COUNT = 116 };
    RTRH_EX                 Header;
    unsigned short          wTimeStampType;
    unsigned short          wNumber[eMAX_COUNT];
};

struct RT_GET_SRTREX_RESP
{
    enum                    { eMAX_COUNT = RT_GET_SRTREX_REQ::eMAX_COUNT };
    RTRH_EX                 Header;
    SRTR_EX                 SRtrEx[eMAX_COUNT];
};

/*---------------------------------------------------------------------------*/

struct RT_GET_NAMED_RTREX_REQ
{
    enum                    { eMAX_COUNT = 36 };
    RTRH_EX                 Header;
    unsigned short          wTimeStampType;
    char                    szLongId[eMAX_COUNT][32];
};

struct RT_GET_NAMED_RTREX_RESP
{
    enum                    { eMAX_COUNT = RT_GET_NAMED_RTREX_REQ::eMAX_COUNT };
    RTRH_EX                 Header;
    RTR_EX                  RtrEx[eMAX_COUNT];
};

/*---------------------------------------------------------------------------*/

struct RT_GET_NAMED_SRTREX_REQ
{
    enum                    { eMAX_COUNT = 116 };
    RTRH_EX                 Header;
    unsigned short          wTimeStampType;
    char                    szLongId[eMAX_COUNT][32];
};

struct RT_GET_NAMED_SRTREX_RESP
{
    enum                    { eMAX_COUNT = RT_GET_NAMED_SRTREX_REQ::eMAX_COUNT };
    RTRH_EX                 Header;
    SRTR_EX                 SRtrEx[eMAX_COUNT];
};

/*---------------------------------------------------------------------------*/

struct RT_SET_RTREX_REQ
{
    enum                    { eMAX_COUNT = 35 };
    RTRH_EX                 Header;
    RTCH                    RTControlHeader;
    unsigned short          wTimeStampType;
    RTR_EX                  RtrEx[eMAX_COUNT];
};

struct RT_SET_RTREX_RESP
{
    enum                    { eMAX_COUNT = RT_SET_RTREX_REQ::eMAX_COUNT };
    RTRH_EX                 Header;
    unsigned short          wPointError[eMAX_COUNT];
};

/*---------------------------------------------------------------------------*/

struct RT_MATCH_NAMED_RTREX_REQ
{
    RTRH_EX                 Header;
    unsigned short          wTimeStampType;
    char                    szLongId[32];
};

struct RT_MATCH_NAMED_RTREX_RESP
{
    RTRH_EX                 Header;
    RTR_EX                  RtrEx;
};

/*---------------------------------------------------------------------------*/

struct RT_ALPHA_GET_RTREX_BY_POINTIDLONG_REQ
{
    enum                    { eMAX_COUNT = 36 };
    RTRH_EX                 Header;
    unsigned short          wDirection;
    unsigned short          wTimeStampType;
    union
    {
        char                szLongId[32];
        char                szPointIdLong[32];
    };
} ;

typedef RT_ALPHA_GET_RTREX_BY_POINTIDLONG_REQ RT_ALPHA_GET_RTREX_REQ;

struct RT_ALPHA_GET_RTREX_BY_POINTIDLONG_RESP
{
    enum                    { eMAX_COUNT = RT_ALPHA_GET_RTREX_BY_POINTIDLONG_REQ::eMAX_COUNT };
    RTRH_EX                 Header;
    RTR_EX                  RtrEx[eMAX_COUNT];
};

typedef RT_ALPHA_GET_RTREX_BY_POINTIDLONG_RESP RT_ALPHA_GET_RTREX_RESP;

/*---------------------------------------------------------------------------*/

struct RT_GET_SNAMED_RTREX_REQ
{
    enum                    { eMAX_COUNT = 36 };
    RTRH_EX                 Header;
    unsigned short          wTimeStampType;
    char                    szShortId[eMAX_COUNT][9];
};

struct RT_GET_SNAMED_RTREX_RESP
{
    enum                    { eMAX_COUNT = RT_GET_SNAMED_RTREX_REQ::eMAX_COUNT };
    RTRH_EX                 Header;
    RTR_EX                  RtrEx[eMAX_COUNT];
};

/*---------------------------------------------------------------------------*/

struct RT_ALPHA_GET_RTREX_BY_POINTID_REQ
{
    enum                    { eMAX_COUNT = 36 };
    RTRH_EX                 Header;
    unsigned short          wDirection;
    unsigned short          wTimeStampType;
    char                    szPointId[9];
};

struct RT_ALPHA_GET_RTREX_BY_POINTID_RESP
{
    enum                    { eMAX_COUNT = RT_ALPHA_GET_RTREX_BY_POINTID_REQ::eMAX_COUNT };
    RTRH_EX                 Header;
    RTR_EX                  RtrEx[eMAX_COUNT];
};

/*---------------------------------------------------------------------------*/

struct RT_GET_RTREX_BY_TAG_REQ
{
    enum                    { eMAX_COUNT = 36 };
    RTRH_EX                 Header;
    unsigned short          wTimeStampType;
    struct _tag
    {
        unsigned short  wTagType;
        union
        {
            char szPointId[9];
            char szPointIdLong[32];
        };
    } tags[eMAX_COUNT];
};

struct RT_GET_RTREX_BY_TAG_RESP
{
    enum                    { eMAX_COUNT = RT_GET_RTREX_BY_TAG_REQ::eMAX_COUNT };
    RTRH_EX                 Header;
    RTR_EX                  RtrEx[eMAX_COUNT];
};

/*---------------------------------------------------------------------------*/

struct RT_GET_SRTREX_BY_TAG_REQ
{
    enum                    { eMAX_COUNT = 116 };
    RTRH_EX                 Header;
    unsigned short          wTimeStampType;
    struct _tag
    {
        unsigned short  wTagType;
        union
        {
            char szPointId[9];
            char szPointIdLong[32];
        };
    } tags[eMAX_COUNT];
};

struct RT_GET_SRTREX_BY_TAG_RESP
{
    enum                    { eMAX_COUNT = RT_GET_SRTREX_BY_TAG_REQ::eMAX_COUNT };
    RTRH_EX                 Header;
    SRTR_EX                 SRtrEx[eMAX_COUNT];
};

/*---------------------------------------------------------------------------*/

#ifdef _MSC_VER
#pragma pack(pop, __CVS__PACKING__)
#endif  // _MSC_VER


/*************************************************************************\
 $History: Cvs.h $
 *
 * *****************  Version 7  *****************
 * User: Rhg1         Date: 6/05/00    Time: 12:10p
 * Updated in $/CygNet/Source/Support/CygNetCore
 * VSI TRACKER #320 - modified commentary
 *
 * *****************  Version 6  *****************
 * User: Jgt1         Date: 4/08/00    Time: 4:34p
 * Updated in $/CygNet/Source/Support/CygNetCore
 * moved CCvsDevicePoint from CCVS to CygNetCore
 *
 * *****************  Version 5  *****************
 * User: Rhg1         Date: 10/27/99   Time: 6:25p
 * Updated in $/CygNet/Source/Support/CygNetCore
 * corrected minor problem due to VSS check-in anomaly
 *
 * *****************  Version 4  *****************
 * User: Rhg1         Date: 10/21/99   Time: 2:15p
 * Updated in $/CygNet/Source/Support/CygNetCore
 * added new messages
 *
 * *****************  Version 3  *****************
 * User: Rhg1         Date: 10/20/99   Time: 2:59p
 * Updated in $/CygNet/Source/Support/CygNetCore
 * untabified the file
 *
 * *****************  Version 2  *****************
 * User: Rps1         Date: 10/06/99   Time: 10:12p
 * Updated in $/CygNet/Source/Support/CygNetCore
 * Modified message ids to use common service id base value.
 *
 * *****************  Version 1  *****************
 * User: Rps1         Date: 9/17/99    Time: 3:56p
 * Created in $/CygNet/Source/Support/CygNetCore
 * New core struct and message files.

\*************************************************************************/

#endif // __CVS_H

/////////////////////////////////////////////////////////////////////////////
//  Copyright 1998 Visual Systems, Inc., All Rights Reserved
/////////////////////////////////////////////////////////////////////////////