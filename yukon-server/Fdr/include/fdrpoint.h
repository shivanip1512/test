#pragma once

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>
#include <vector>
#include "dlldefs.h"
#include "pointtypes.h"
#include "fdrdestination.h"
#include "types.h"
#include "ctitime.h"

class CtiTime;

typedef boost::shared_ptr<CtiFDRPoint> CtiFDRPointSPtr;

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
    long           getPointID( void ) const;
    CtiFDRPoint &  setPointID(const long aPointID);

    double         getMultiplier( void ) const;
    CtiFDRPoint &  setMultiplier( const double aMultiplier );

    double         getValue( void ) const;
    CtiFDRPoint &  setValue( const double aValue );

    double         getOffset( void ) const;
    CtiFDRPoint &  setOffset( const double aOffset );

    unsigned       getQuality( void ) const;
    CtiFDRPoint &  setQuality( const unsigned aQuality );

    bool           isUnsolicited( void ) const;
    CtiFDRPoint &  setUnsolicited( const bool unsolicited );

    bool           isControllable( void ) const;
    CtiFDRPoint &  setControllable( const bool aFlag=true );

    CtiTime        getLastTimeStamp ( void ) const;
    CtiFDRPoint &  setLastTimeStamp ( const CtiTime & aTime );

    CtiPointType_t getPointType() const;
    CtiFDRPoint & setPointType(CtiPointType_t aType);

    long    getPaoID() const;
    void    setPaoID(long aPaoID);

    long    getPointOffset() const;
    void    setPointOffset(long aPointOffset);

    std::string      getTranslateName(std::string &aDestination);
    std::string      getTranslateName(int aIndex);

    bool isCommStatus();

private:

        // all points have these
        long                        iPointID;
        long                        iPaoID;
        long                        iPointOffset;
        CtiPointType_t              iPointType;
        DestinationList             iDestinationList;
        bool                        iControllable;
        bool                        iUnsolicited;
        double                      iValue;
        unsigned                    iQuality;

        // maybe should be in own class someday
        double                      iMultiplier;
        double                      iOffset;
        CtiTime                     iLastTimeStamp;
};

IM_EX_FDRBASE std::ostream& operator<< (std::ostream& os, const CtiFDRPoint& point);
