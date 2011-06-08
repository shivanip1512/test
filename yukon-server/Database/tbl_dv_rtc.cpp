/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_rtc
*
* Date:   3/18/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.6.22.1 $
* DATE         :  $Date: 2008/11/13 17:23:49 $
*
* HISTORY      :
* $Log: tbl_dv_rtc.cpp,v $
* Revision 1.6.22.1  2008/11/13 17:23:49  jmarks
* YUK-5273 Upgrade Yukon tool chain to Visual Studio 2005/2008
*
* Responded to reviewer comments again.
*
* I eliminated excess references to windows.h .
*
* This still left over 100 references to it where "yukon.h" or "precompiled.h" was not obviously included.  Some other chaining of references could still be going on, and of course it is potentially possible that not all the files in the project that include windows.h actually need it - I didn't check for that.
*
* None-the-less, I than added the NOMINMAX define right before each place where windows.h is still included.
* Special note:  std::min<LONG>(TimeOut, 500); is still required for compilation.
*
* In this process I occasionally deleted a few empty lines, and when creating the define, also added some.
*
* This may not have affected every file in the project, but while mega-editing it certainly seemed like it did.
*
* Revision 1.6  2006/09/26 13:53:24  mfisher
* standardizing the code for the "Decoding" printout
*
* Revision 1.5  2005/12/20 17:16:06  tspar
* Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex
*
* Revision 1.4.2.4  2005/08/12 19:53:38  jliu
* Date Time Replaced
*
* Revision 1.4.2.3  2005/07/18 22:30:50  jliu
* rebuild_cppunit&correct_find
*
* Revision 1.4.2.2  2005/07/14 22:26:53  jliu
* RWCStringRemoved
*
* Revision 1.4.2.1  2005/07/12 21:08:32  jliu
* rpStringWithoutCmpParser
*
* Revision 1.4  2005/04/15 18:28:39  mfisher
* got rid of magic number debuglevel checks
*
* Revision 1.3  2005/02/17 19:02:57  mfisher
* Removed space before CVS comment header, moved #include "yukon.h" after CVS header
*
* Revision 1.2  2005/02/10 23:23:48  alauinger
* Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build
*
* Revision 1.1  2004/03/18 19:50:34  cplender
* Initial Checkin
* Builds, but not too complete.
*
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

////#include "row_reader.h"

#include "dbaccess.h"
#include "dlldefs.h"
#include "dllbase.h"
#include "logger.h"
#include "tbl_dv_rtc.h"

using std::string;
using std::endl;

CtiTableDeviceRTC::CtiTableDeviceRTC() :
    _deviceID(-1),
    _rtcAddress(0),
    _lbtMode(CtiTableDeviceRTC::NoLBT),
    _responseBit(true)
{}

CtiTableDeviceRTC::~CtiTableDeviceRTC()
{
}

LONG CtiTableDeviceRTC::getDeviceID() const
{
    return _deviceID;
}

int CtiTableDeviceRTC::getRTCAddress() const
{
    return _rtcAddress;
}

bool CtiTableDeviceRTC::getResponseBit() const
{
    return _responseBit;
}
int  CtiTableDeviceRTC::getLBTMode() const
{
    return _lbtMode;
}

void CtiTableDeviceRTC::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    string rwsTemp;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["deviceid"]      >> _deviceID;
    rdr["rtcaddress"] >> _rtcAddress;
    rdr["response"]  >> rwsTemp;

    if(rwsTemp.find("Y")!=string::npos||rwsTemp.find("y")!=string::npos) _responseBit = true;
    else _responseBit = false;

    rdr["lbtmode"]  >> _lbtMode;
}

string CtiTableDeviceRTC::getTableName()
{
    return "DeviceRTC";
}


