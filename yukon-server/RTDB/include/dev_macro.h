#pragma once

#include "dev_base.h"
#include "dev_grp.h"
#include "cmdparse.h"
#include "mutex.h"
#include "yukon.h"
#include <set>
#include <vector>

class IM_EX_DEVDB CtiDeviceMacro : public CtiDeviceGroupBase // 2004/1/4 CGP // : public CtiDeviceBase
{
private:

    typedef CtiDeviceGroupBase Inherited;

    INT analyzeWhiteRabbits( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList );
    bool coalesceRippleGroups( CtiRequestMsg *pReq, CtiCommandParser &parse, BYTE *RippleMessage );
    bool executeOnSubGroupRoute( const CtiDeviceSPtr &pBase, std::set< LONG > &executedRouteSet );

protected:

    typedef std::vector< CtiDeviceSPtr >           deviceVec_t;
    typedef std::vector< CtiDeviceSPtr >::iterator deviceIter_t;

    deviceVec_t _deviceList;

    CtiMutex _deviceListMux;

public:

    CtiDeviceMacro( );
    CtiDeviceMacro( const CtiDeviceMacro &aRef );
    ~CtiDeviceMacro( );

    CtiDeviceMacro &operator=( const CtiDeviceMacro &aRef );
    virtual void DumpData( void );

    void clearDeviceList( void );
    CtiDeviceMacro &addDevice( CtiDeviceSPtr toAdd );

    virtual LONG getRouteID() { return 0L; }

    virtual std::string getSQLCoreStatement() const;

    void DecodeDatabaseReader(Cti::RowReader &rdr);
    virtual INT ExecuteRequest( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList );
    virtual INT processTrxID( int trx, std::list< CtiMessage* >  &vgList );
    virtual INT initTrxID( int trx, CtiCommandParser &parse, std::list< CtiMessage* >  &vgList );
    virtual std::string getDescription(const CtiCommandParser & parse) const;
};
