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
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiDeviceMacro(const CtiDeviceMacro&);
    CtiDeviceMacro& operator=(const CtiDeviceMacro&);

    typedef CtiDeviceGroupBase Inherited;

    INT analyzeWhiteRabbits( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    bool coalesceRippleGroups( CtiRequestMsg *pReq, CtiCommandParser &parse, BYTE *RippleMessage );
    bool executeOnSubGroupRoute( const CtiDeviceSPtr &pBase, std::set< LONG > &executedRouteSet );

protected:

    typedef std::vector< CtiDeviceSPtr >           deviceVec_t;
    typedef std::vector< CtiDeviceSPtr >::iterator deviceIter_t;

    deviceVec_t _deviceList;

    CtiMutex _deviceListMux;

public:

    CtiDeviceMacro( );

    virtual std::string toString() const override;

    void clearDeviceList( void );
    CtiDeviceMacro &addDevice( CtiDeviceSPtr toAdd );

    virtual LONG getRouteID() { return 0L; }

    virtual std::string getSQLCoreStatement() const;

    void DecodeDatabaseReader(Cti::RowReader &rdr) override;
    YukonError_t ExecuteRequest( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) override;
    virtual INT processTrxID( int trx, CtiMessageList  &vgList );
    virtual INT initTrxID( int trx, CtiCommandParser &parse, CtiMessageList  &vgList );
    virtual std::string getDescription(const CtiCommandParser & parse) const;
};
