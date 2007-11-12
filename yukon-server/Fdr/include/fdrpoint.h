#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*****************************************************************************
*
*    FILE NAME: fdrpoint.h
*
*    DATE: 08/25/2000
*
*    AUTHOR: Matt Fisher
*
*    PURPOSE: CtiFDRPoint class header
*
*    DESCRIPTION: class is a basically a memory duplicate of a
*                 row in the FDRTRANSLATION db table.
*
*
*
*    Copyright (C) 2000 Cannon Technologies, Inc.  All rights reserved.
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrpoint.h-arc  $
*    REVISION     :  $Revision: 1.6 $
*    DATE         :  $Date: 2007/11/12 16:46:55 $
*    History:
      $Log: fdrpoint.h,v $
      Revision 1.6  2007/11/12 16:46:55  mfisher
      Removed some Rogue Wave includes

      Revision 1.5  2005/12/20 17:17:16  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.4  2005/09/13 20:44:27  tmack
      In the process of working on the new ACS(MULTI) implementation, the following changes were made:

      - add a operator<< function to provide easy printing of this class
      - add a typedef for the destination list to replace the verbose vector<CtiFDRDestination>
      Revision 1.3.58.3  2005/08/12 19:53:47  jliu
      Date Time Replaced

      Revision 1.3.58.2  2005/07/14 22:26:56  jliu
      RWCStringRemoved

      Revision 1.3.58.1  2005/07/12 21:08:39  jliu
      rpStringWithoutCmpParser

      Revision 1.3  2002/04/16 15:58:44  softwarebuild
      20020416_1031_2_16

      Revision 1.2  2002/04/15 15:19:01  cplender

      This is an update due to the freezing of PVCS on 4/13/2002


****************************************************************************
*/

#ifndef __FDRPOINT_H__
#define __FDRPOINT_H__

/** include files **/
#include <windows.h>
#include <vector>
#include "dlldefs.h"
#include "pointtypes.h"
#include "fdrdestination.h"
#include "types.h"
#include "ctitime.h"

using std::vector;
using std::string;

class CtiTime;

class IM_EX_FDRBASE CtiFDRPoint
{

public:

    CtiFDRPoint( long pointID = 0);
    virtual ~CtiFDRPoint();

//    BOOL operator==( const CtiFDRPoint &other ) const;
    CtiFDRPoint& operator=( const CtiFDRPoint &other );

    typedef std::vector<CtiFDRDestination> DestinationList;
    DestinationList getDestinationList(void) const;
    DestinationList &getDestinationList(void);

    CtiFDRPoint &setDestinationList (CtiFDRPoint &aList);
    CtiFDRPoint &setDestinationList (DestinationList &aList);


    // getters and setters
    long                getPointID( void ) const;
    CtiFDRPoint &  setPointID(const long aPointID);

    double              getMultiplier( void ) const;
    CtiFDRPoint &  setMultiplier( const double aMultiplier );

    double              getValue( void ) const;
    CtiFDRPoint &  setValue( const double aValue );

    double              getOffset( void ) const;
    CtiFDRPoint &  setOffset( const double aOffset );

    unsigned              getQuality( void ) const;
    CtiFDRPoint &  setQuality( const unsigned aQuality );

    bool                isControllable( void ) const;
    CtiFDRPoint &  setControllable( const bool aFlag=true );

    CtiTime              getLastTimeStamp ( void ) const;
    CtiFDRPoint &  setLastTimeStamp ( const CtiTime & aTime );

    CtiPointType_t getPointType() const;
    CtiFDRPoint & setPointType(CtiPointType_t aType);

    string      getTranslateName(string &aDestination);
    string      getTranslateName(int aIndex);


private:

        // all points have these
        long                        iPointID;
        CtiPointType_t              iPointType;
        DestinationList             iDestinationList;
        bool                        iControllable;
        double                      iValue;
        unsigned                    iQuality;

        // maybe should be in own class someday
        double                      iMultiplier;
        double                      iOffset;
        CtiTime                      iLastTimeStamp;
};

IM_EX_FDRBASE std::ostream& operator<< (std::ostream& os, const CtiFDRPoint& point);

#endif  //  #ifndef __FDRPOINT_H__
