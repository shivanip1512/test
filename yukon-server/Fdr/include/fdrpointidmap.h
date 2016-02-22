#pragma once

#include <windows.h>
#include "ctitime.h"
#include "pointtypes.h"
#include "dlldefs.h"
#include "fdr.h"
#include "utility.h"


class IM_EX_FDRBASE CtiFDRPointIdMap
{
    public:
        DEBUG_INSTRUMENTATION

        CtiFDRPointIdMap( long pointID = 0, std::string & translateName = std::string(), std::string & destinationName = std::string(), FDRDbReloadReason aReason=NotReloaded);
        ~CtiFDRPointIdMap();

        BOOL operator==( const CtiFDRPointIdMap &other ) const;
        
        CtiFDRPointIdMap& operator=( const CtiFDRPointIdMap &other );
    
        // getters and setters
        long                getPointID( void ) const;
        CtiFDRPointIdMap &  setPointID(const long aPointID);

        std::string         getTranslateName() const;
        CtiFDRPointIdMap &  setTranslateName(const std::string & aName);

        std::string         getDestinationName() const;
        CtiFDRPointIdMap &  setDestinationName(const std::string & aName);

        double              getMultiplier( void ) const;
        CtiFDRPointIdMap &  setMultiplier( const double aMultiplier );

        double              getOffset( void ) const;
        CtiFDRPointIdMap &  setOffset( const double aOffset );

        bool                isControllable( void ) const;
        CtiFDRPointIdMap &  setControllable( const bool aFlag=true );

        CtiTime             getLastTimeStamp ( void ) const;
        CtiFDRPointIdMap &  setLastTimeStamp ( const CtiTime & aTime );
        
        FDRDbReloadReason   getReasonForReload( void ) const;
        CtiFDRPointIdMap &  setReasonForReload(FDRDbReloadReason aReason=DbChange);

        CtiPointType_t     getPointType() const;        
        CtiFDRPointIdMap & setPointType(CtiPointType_t aType);

    protected:    
    private:
        // private data
        long            iPointID;
        CtiPointType_t  iPointType;
        std::string     iTranslateName;
        double          iMultiplier;
        double          iOffset;
        CtiTime         iLastTimeStamp;

        // this should be in class of its own but time constraints call DLS
        std::string        iDestinationName;
        FDRDbReloadReason  iReasonForReload;
        bool               iControllable;
};
