/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_control
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_pt_control.cpp-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2007/09/28 15:38:00 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_pt_control.h"
#include "logger.h"

using std::string;
using std::endl;

CtiTablePointControl& CtiTablePointControl::operator=(const CtiTablePointControl& aRef)
{
   if(this != &aRef)
   {
      _controlOffset    = aRef.getControlOffset();
      _closeTime1       = aRef.getCloseTime1();
      _closeTime2       = aRef.getCloseTime2();
      _stateZeroControl = aRef.getStateZeroControl();
      _stateOneControl  = aRef.getStateOneControl();
   }
   return *this;
}

INT  CtiTablePointControl::getControlOffset() const
{
   return _controlOffset;
}
INT  CtiTablePointControl::getCloseTime1() const
{
   return _closeTime1;
}
INT  CtiTablePointControl::getCloseTime2() const
{
   return _closeTime2;
}
const string& CtiTablePointControl::getStateZeroControl() const
{
   return _stateZeroControl;
}
const string& CtiTablePointControl::getStateOneControl() const
{
   return _stateOneControl;
}

CtiTablePointControl& CtiTablePointControl::setControlOffset(INT i)
{
   _controlOffset = i;
   return *this;
}
CtiTablePointControl& CtiTablePointControl::setCloseTime1(INT i)
{
   _closeTime1 = i;
   return *this;
}
CtiTablePointControl& CtiTablePointControl::setCloseTime2(INT i)
{
   _closeTime2 = i;
   return *this;
}
CtiTablePointControl& CtiTablePointControl::setStateZeroControl(const string& zero)
{
   _stateZeroControl = zero;
   return *this;
}
CtiTablePointControl& CtiTablePointControl::setStateOneControl(const string& one)
{
   _stateOneControl = one;
   return *this;
}


void CtiTablePointControl::dump() const
{
   CtiLockGuard<CtiLogger> doubt_guard(dout);
   dout << " Control Offset                           : " << _controlOffset << endl;
   dout << " Close Time #1                            : " << _closeTime1 << endl;
   dout << " Close Time #2                            : " << _closeTime2 << endl;
   dout << " State Zero Control String                : " << _stateZeroControl << endl;
   dout << " State One Control String                 : " << _stateOneControl << endl;
}

void CtiTablePointControl::DecodeDatabaseReader(Cti::RowReader &rdr)
{
   if(getDebugLevel() & DEBUGLEVEL_DATABASE) 
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }
   rdr >> _controlOffset;
   rdr >> _closeTime1;
   rdr >> _closeTime2;
   rdr >> _stateZeroControl;
   rdr >> _stateOneControl;
}

UINT CtiTablePointControl::getStaticTags() const
{
   UINT tag = 0;

   return tag;
}

CtiTablePointControl::CtiTablePointControl() :_controlOffset(0),_closeTime1(0),_closeTime2(0),
                                              _stateZeroControl(string("")),
                                              _stateOneControl(string("")){}

CtiTablePointControl::CtiTablePointControl(const CtiTablePointControl& aRef)
{
   *this = aRef;
}

CtiTablePointControl::~CtiTablePointControl() {}


