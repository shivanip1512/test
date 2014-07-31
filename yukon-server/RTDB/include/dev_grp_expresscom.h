#pragma once

#include "dev_base.h"
#include "dev_grp.h"
#include "tbl_dv_expresscom.h"

class IM_EX_DEVDB CtiDeviceGroupExpresscom : public CtiDeviceGroupBase
{
private:

    typedef CtiDeviceGroupBase Inherited;
    CtiTableExpresscomLoadGroup     _expresscomGroup;
    
    typedef std::map< long, CtiDeviceGroupBaseWPtr > WPtrGroupMap;
    WPtrGroupMap _children;

protected:

    // This method makes a gripe if any addressing level or load is predefined when submitting a request to a group
    bool checkForEmptyParseAddressing( CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &retList );
    void reportChildControlStart(int isshed, int shedtime, int reductionratio, std::list< CtiMessage* >  &vgList, std::string cmd, int controlPriority );
    std::string getAddressingAsString();

    int extractGroupAddressing(CtiRequestMsg * &pReq, CtiCommandParser &parse, OUTMESS * &OutMessage, std::list<CtiMessage *> &vgList, std::list<CtiMessage *> &retList, std::string &resultString);
    void reportAndLogControlStart(CtiCommandParser &parse, std::list<CtiMessage *> &vgList, const std::string &commandString);

public:    
    CtiDeviceGroupExpresscom();
    virtual ~CtiDeviceGroupExpresscom();


    CtiTableExpresscomLoadGroup   getExpresscomGroup() const;
    CtiTableExpresscomLoadGroup&  getExpresscomGroup();
    CtiDeviceGroupExpresscom&     setExpresscomGroup(const CtiTableExpresscomLoadGroup& aRef);

    virtual std::string getSQLCoreStatement() const;

    virtual LONG getRouteID();
    virtual std::string getDescription(const CtiCommandParser & parse) const;
    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
    virtual std::string getPutConfigAssignment(UINT modifier = 0);
    virtual void reportControlStart(int isshed, int shedtime, int reductionratio, std::list< CtiMessage* >  &vgList, std::string cmd = std::string(""), int priority = 0 );

    virtual ADDRESSING_COMPARE_RESULT compareAddressing(CtiDeviceGroupBaseSPtr otherGroup);
    bool compareAddressValues(USHORT addressing, CtiDeviceGroupExpresscom *expGroup);
    virtual bool isAParent();
    virtual void addChild(CtiDeviceGroupBaseSPtr child);
    virtual void removeChild(long child);
    virtual void clearChildren();

};
