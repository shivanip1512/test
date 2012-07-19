#pragma once

#include "tbl_pt_control.h"
#include "pointtypes.h"

#include <boost/optional.hpp>

class IM_EX_CTIYUKONDB CtiTablePointStatusControl : public CtiTablePointControl
{
    typedef CtiTablePointControl Inherited;

    CtiControlType_t  _controlType;
    int               _closeTime1;
    int               _closeTime2;
    std::string       _stateZeroControl;
    std::string       _stateOneControl;
    int               _commandTimeout;

public:

    CtiTablePointStatusControl();

    CtiControlType_t getControlType() const  {  return _controlType;  }
    int getCloseTime1()      const  {  return _closeTime1;  }
    int getCloseTime2()      const  {  return _closeTime2;  }
    const std::string& getStateZeroControl() const {  return _stateZeroControl;  }
    const std::string& getStateOneControl() const  {  return _stateOneControl;  }
    int getCommandTimeout()  const  {  return _commandTimeout;  }

    // -- only used for unit tests?  Fixy? --
    void setControlType(CtiControlType_t t);

    void DecodeDatabaseReader(Cti::RowReader &rdr);
};

enum
{
    DefaultControlExpirationTime = 300
};
