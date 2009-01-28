#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef __FDRPOINTIDMAP_H__
#define __FDRPOINTIDMAP_H__

/*******************************************************************
*
*    FILE NAME: fdrpointidmap.h
*
*    DATE: 10/23/2000
*
*    AUTHOR: Ben Wallace
*
*    PURPOSE:  Class CtiFDRPointIdMap
*
*
*    DESCRIPTION: This class models a generic FDR Translation record.
*                 It uses a single String for an Id on the other system
*                 and include the points multiplier and offset.
*
********************************************************************
*/


/** include files **/

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>
#include "ctitime.h"
#include "pointtypes.h"
#include "dlldefs.h"
#include "fdr.h"


class IM_EX_FDRBASE CtiFDRPointIdMap
{
    public:    
        CtiFDRPointIdMap( long pointID = 0, string & translateName = string(), string & destinationName = string(), FDRDbReloadReason aReason=NotReloaded);
        ~CtiFDRPointIdMap();

        BOOL operator==( const CtiFDRPointIdMap &other ) const;
        
        CtiFDRPointIdMap& operator=( const CtiFDRPointIdMap &other );
    
        // getters and setters
        long                getPointID( void ) const;
        CtiFDRPointIdMap &  setPointID(const long aPointID);

        string           getTranslateName() const;
        CtiFDRPointIdMap &  setTranslateName(const string & aName);

        string           getDestinationName() const;
        CtiFDRPointIdMap &  setDestinationName(const string & aName);

        double              getMultiplier( void ) const;
        CtiFDRPointIdMap &  setMultiplier( const double aMultiplier );

        double              getOffset( void ) const;
        CtiFDRPointIdMap &  setOffset( const double aOffset );

        bool                isControllable( void ) const;
        CtiFDRPointIdMap &  setControllable( const bool aFlag=true );

        CtiTime              getLastTimeStamp ( void ) const;
        CtiFDRPointIdMap &  setLastTimeStamp ( const CtiTime & aTime );
        
        FDRDbReloadReason   getReasonForReload( void ) const;
        CtiFDRPointIdMap &  setReasonForReload(FDRDbReloadReason aReason=DbChange);

        CtiPointType_t getPointType() const;        
        CtiFDRPointIdMap & setPointType(CtiPointType_t aType);

    protected:    
    private:
        // private data
        long            iPointID;
        CtiPointType_t  iPointType;
        string       iTranslateName;
        double          iMultiplier;
        double          iOffset;
        CtiTime          iLastTimeStamp;

        // this should be in class of its own but time constraints call DLS
        string       iDestinationName;
        FDRDbReloadReason  iReasonForReload;
        bool            iControllable;
};

#endif  //  #ifndef __FDRPOINTIDMAP_H__

