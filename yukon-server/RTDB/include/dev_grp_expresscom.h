#pragma once

#include "dev_base.h"
#include "dev_grp.h"
#include "tbl_dv_expresscom.h"

class IM_EX_DEVDB CtiDeviceGroupExpresscom : public CtiDeviceGroupBase
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiDeviceGroupExpresscom(const CtiDeviceGroupExpresscom&);
    CtiDeviceGroupExpresscom& operator=(const CtiDeviceGroupExpresscom&);

    typedef CtiDeviceGroupBase Inherited;
    CtiTableExpresscomLoadGroup     _expresscomGroup;

    typedef std::map< long, CtiDeviceGroupBaseWPtr > WPtrGroupMap;
    WPtrGroupMap _children;

protected:

    // This method makes a gripe if any addressing level or load is predefined when submitting a request to a group
    bool checkForEmptyParseAddressing( CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &retList );
    void reportChildControlStart(int isshed, int shedtime, int reductionratio, CtiMessageList  &vgList, std::string cmd, int controlPriority );
    std::string getAddressingAsString();

    int extractGroupAddressing(CtiRequestMsg * &pReq, CtiCommandParser &parse, OUTMESS * &OutMessage, CtiMessageList &vgList, CtiMessageList &retList, std::string &resultString);
    void reportAndLogControlStart(CtiCommandParser &parse, CtiMessageList &vgList, const std::string &commandString);

public:
    CtiDeviceGroupExpresscom();

    CtiTableExpresscomLoadGroup&       getExpresscomGroup();
    const CtiTableExpresscomLoadGroup& getExpresscomGroup() const;

    virtual std::string getSQLCoreStatement() const;

    virtual LONG getRouteID();
    virtual std::string getDescription(const CtiCommandParser & parse) const;
    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
    INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;
    virtual std::string getPutConfigAssignment(UINT modifier = 0);
    virtual void reportControlStart(int isshed, int shedtime, int reductionratio, CtiMessageList  &vgList, std::string cmd = std::string(""), int priority = 0 );

    virtual ADDRESSING_COMPARE_RESULT compareAddressing(CtiDeviceGroupBaseSPtr otherGroup);
    bool compareAddressValues(USHORT addressing, CtiDeviceGroupExpresscom *expGroup);
    virtual bool isAParent();
    virtual void addChild(CtiDeviceGroupBaseSPtr child);
    virtual void removeChild(long child);
    virtual void clearChildren();

};
