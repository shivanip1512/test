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
*    REVISION     :  $Revision: 1.2 $
*    DATE         :  $Date: 2002/04/15 15:19:01 $
*    History: 
      $Log: fdrpoint.h,v $
      Revision 1.2  2002/04/15 15:19:01  cplender

      This is an update due to the freezing of PVCS on 4/13/2002


****************************************************************************
*/

#ifndef __FDRPOINT_H__
#define __FDRPOINT_H__

/** include files **/
#include <windows.h>
#include <vector>
#include <rw/tpslist.h>
#include <rw/cstring.h>
#include "dlldefs.h"
#include "pointtypes.h"
#include "fdrdestination.h"
#include "types.h"

class RWTime;

class IM_EX_FDRBASE CtiFDRPoint
{

public:

    CtiFDRPoint( long pointID = 0);
    virtual ~CtiFDRPoint();

//    BOOL operator==( const CtiFDRPoint &other ) const;
    CtiFDRPoint& operator=( const CtiFDRPoint &other );

    vector< CtiFDRDestination > getDestinationList(void) const;
    vector< CtiFDRDestination > &getDestinationList(void);

    CtiFDRPoint &setDestinationList (CtiFDRPoint &aList);
    CtiFDRPoint &setDestinationList (vector< CtiFDRDestination > &aList);


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

    RWTime              getLastTimeStamp ( void ) const;
    CtiFDRPoint &  setLastTimeStamp ( const RWTime & aTime );
    
    CtiPointType_t getPointType() const;        
    CtiFDRPoint & setPointType(CtiPointType_t aType);

    RWCString      getTranslateName(RWCString &aDestination);
    RWCString      getTranslateName(int aIndex);


private:

        // all points have these
        long                        iPointID;
        CtiPointType_t              iPointType;
        vector< CtiFDRDestination > iDestinationList;
        bool                        iControllable;
        double                      iValue;
        unsigned                    iQuality;

        // maybe should be in own class someday
        double                      iMultiplier;
        double                      iOffset;
        RWTime                      iLastTimeStamp;
};

#endif  //  #ifndef __FDRPOINT_H__
