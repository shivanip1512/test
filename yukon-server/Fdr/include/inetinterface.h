#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
#ifndef __INETINTERFACE_H__
#define __INETINTERFACE_H__

#include <iostream>

using namespace std;

#include <rw/rwtime.h>

#include "fdriosocket.h"

#define STANDNAMLEN     20
#define DESTSIZE        10
#define MAXTYPES        30

#define INETPORT        11235

//  this interface has yet to be implemented, but here's the data structures needed in the meantime

typedef struct _DRPMESS 
        {
            USHORT Type;
            CHAR SourceName[DESTSIZE];
            union
            {
                struct
                {
                    CHAR DeviceName[STANDNAMLEN];
                    CHAR PointName[STANDNAMLEN];
                    USHORT Quality;
                    ULONG TimeStamp;
                    USHORT AlarmState;
                    FLOAT Value;
                } DRPValue;
                struct
                {
                    ULONG FinishTime;
                    USHORT Level[MAXTYPES];
                } DRPScram;
                struct
                {
                    ULONG StartTime;
                    ULONG FinishTime;
                    USHORT Level[MAXTYPES][24];
                } DRPStrategy;
                struct
                {
                    CHAR Message[80];
                    ULONG MSAlarmPoint;
                } DRPTextMessage;
                struct
                {
                    USHORT Mode;
                } DRPFBLCMode;
                struct
                {
                    struct timeb TimeB;
                } DRPTimeSync;
            } DRPUnion;
        } DRPMESS;



#endif  //  __INETINTERFACE_H__
