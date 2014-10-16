/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_08
*
* Date:   9/13/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_08.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2008/10/21 16:30:31 $
*    History:
      $Log: std_ansi_tbl_zero_eight.cpp,v $
      Revision 1.5  2008/10/21 16:30:31  mfisher
      YUK-6615 ANSI table class names and filenames are difficult to read
      Renamed classes and filenames

      Revision 1.4  2005/12/20 17:19:57  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.3  2005/12/12 20:34:30  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.2  2005/02/10 23:23:58  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.1  2004/10/01 17:54:26  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.  New files!

      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table

* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "std_ansi_tbl_08.h"
#include "logger.h"
using std::endl;
using std::string;
//=========================================================================================================================================
//We've gotten all the data back from the device and we're going to fill up our table
//Note: we have to use some of the pieces in this table to fill other pieces in this table..
//
//This will feel.... a little weird....
//=========================================================================================================================================
CtiAnsiTable08::CtiAnsiTable08( BYTE *dataBlob )
{
    int dummy = 0;

    dataBlob += toAnsiIntParser(dataBlob, &_proc_resp_tbl.proc, sizeof( unsigned short ));
    dataBlob += toAnsiIntParser(dataBlob, &_proc_resp_tbl.seq_nbr, sizeof( unsigned char ));
    dataBlob += toAnsiIntParser(dataBlob, &_proc_resp_tbl.result_code, sizeof( unsigned char ));
   
    populateRespDataRcd(dataBlob, &_proc_resp_tbl.resp_data, dummy);
    dataBlob += dummy;

}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable08::~CtiAnsiTable08()
{
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable08& CtiAnsiTable08::operator=(const CtiAnsiTable08& aRef)
{
  if(this != &aRef)
  {
  }
  return *this;
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable08::populateRespDataRcd( BYTE *dataBlob, RSP_DATA_RCD *data_rcd, int &offset )
{

    switch((int)_proc_resp_tbl.proc.tbl_proc_nbr)
    {
        case 0:
            {
                break;
            }
        case 1:
        {
            break;
        }
        case 2:
            {
                break;
            }
        case 3:
            {
                break;
            }
        case 4:
            {
                break;
            }
        case 5:
            {
                dataBlob += toAnsiIntParser(dataBlob, &data_rcd->u.p5.tbl_list, sizeof( unsigned char ));
                dataBlob += toAnsiIntParser(dataBlob, &data_rcd->u.p5.entries_read, sizeof( unsigned short ));
                offset = 3;
                break;
            }
        case 22:
        {
            dataBlob += toAnsiIntParser(dataBlob, &data_rcd->u.pm22.lpOffset, sizeof( unsigned char ));
            offset = 1;
            break;
        }
        default:
            break;
    }
    return;
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable08::printResult( const string& deviceName )
{
    Cti::FormattedList itemList;

    itemList.add("Procedure")        << getTblProcNbr();
    itemList.add("Std Vs. Mfg Flag") << getStdMfgFlg();
    itemList.add("Selector")         << getSelector();
    itemList.add("Sequence Nbr")     << getSeqNbr();
    itemList.add("Result Code")      << getResultCode();

    if (getTblProcNbr() == 22)
    {
        itemList.add("LP Offset") << _proc_resp_tbl.resp_data.u.pm22.lpOffset;
    }

    CTILOG_INFO(dout,
            endl << formatTableName(deviceName +" Std Table 8") <<
            endl <<"** Data Response Table **"<<
            itemList
            );
}

int CtiAnsiTable08::getTblProcNbr(void)
{
    return (int) _proc_resp_tbl.proc.tbl_proc_nbr;
}

bool CtiAnsiTable08::getStdMfgFlg(void)
{
    return (bool) _proc_resp_tbl.proc.std_vs_mfg_flag;
}

int CtiAnsiTable08::getSelector(void)
{
    return (int) _proc_resp_tbl.proc.selector;
}

int CtiAnsiTable08::getSeqNbr(void)
{
    return (int)_proc_resp_tbl.seq_nbr;
}
int CtiAnsiTable08::getResultCode(void)
{
    return (int)_proc_resp_tbl.result_code;
}
int CtiAnsiTable08::getLPOffset(void)
{
    if (getTblProcNbr() == 22)
    {
        return  (int)_proc_resp_tbl.resp_data.u.pm22.lpOffset;
    }
    else
        return 0;
}



