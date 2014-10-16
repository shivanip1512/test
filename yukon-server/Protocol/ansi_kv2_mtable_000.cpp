/*---------------------------------------------------------------------------------*
*
* File:   ansi_kv2_mtable_000.cpp
*
* Class:
* Date:   2/20/2003
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/ansi_kv2_mtable_000.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2008/10/21 16:30:30 $
*    History:
      $Log: ansi_kv2_mtable_zero.cpp,v $
      Revision 1.5  2008/10/21 16:30:30  mfisher
      YUK-6615 ANSI table class names and filenames are difficult to read
      Renamed classes and filenames

      Revision 1.4  2008/10/07 18:16:45  mfisher
      YUK-6504 Server-side point management is naive
      cleaned up a few dsm2.h dependencies

      Revision 1.3  2005/02/10 23:23:56  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.2  2004/09/30 21:37:17  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.1  2003/04/25 14:54:54  dsutton
      Ansi protocol tables specific to the implementation of the KV2


*----------------------------------------------------------------------------------*/
#include "precompiled.h"
#include "dsm2.h"
#include "ansi_kv2_mtable_000.h"
#include "logger.h"

using std::endl;

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiKV2ManufacturerTable000::CtiAnsiKV2ManufacturerTable000( BYTE *dataBlob )
{
    BYTE *ptr=dataBlob;

    // maybe not the best way to do this but
    _mfgVersionNumber       =   *ptr++;
    _mfgRevionsNumber       =   *ptr++;
    _gePartNumber[0]        =   *ptr++;
    _gePartNumber[1]        =   *ptr++;
    _gePartNumber[2]        =   *ptr++;
    _gePartNumber[3]        =   *ptr++;
    _gePartNumber[4]        =   *ptr++;
    _firmwareVersion        =   *ptr++;
    _firmwareRevision       =   *ptr++;
    _firmwareBuild          =   *ptr++;
    _mfgTestVector[0]       =   *ptr++;
    _mfgTestVector[1]       =   *ptr++;
    _mfgTestVector[2]       =   *ptr++;
    _mfgTestVector[3]       =   *ptr++;

    _meterType              =   *ptr++;
    _meterMode              =   *ptr++;
    _registerFunction       =   *ptr++;

    _installedOption1       =   *ptr++;
    _installedOption2       =   *ptr++;
    _installedOption3       =   *ptr++;

    _installedOption4       =   *ptr++;
    _installedOption5       =   *ptr++;
    _installedOption6       =   *ptr++;

    memcpy ((void*)&_upgrades, ptr, 2);
    ptr += 2;

    _reservered[0]          =   *ptr++;
    _reservered[1]          =   *ptr++;
    _reservered[2]          =   *ptr++;
    _reservered[3]          =   *ptr++;

    _flashConstantsPartNumber[0]            =   *ptr++;
    _flashConstantsPartNumber[1]            =   *ptr++;
    _flashConstantsPartNumber[2]            =   *ptr++;
    _flashConstantsPartNumber[3]            =   *ptr++;
    _flashConstantsPartNumber[4]            =   *ptr++;
    _flashConstantsRomVersion               =   *ptr++;
    _flashConstantsRomRevision              =   *ptr++;
    _flashConstantsRomBuild                 =   *ptr++;
    _flashConstantsChecksum[0]              =   *ptr++;
    _flashConstantsChecksum[1]              =   *ptr++;
    _flashConstantPatchFlags[0]             =   *ptr++;
    _flashConstantPatchFlags[1]             =   *ptr++;

    _userCalcPartNumber[0]                  =   *ptr++;
    _userCalcPartNumber[1]                  =   *ptr++;
    _userCalcPartNumber[2]                  =   *ptr++;
    _userCalcPartNumber[3]                  =   *ptr++;
    _userCalcPartNumber[4]                  =   *ptr++;
    _userCalcRomVersion                     =   *ptr++;
    _userCalcRomRevision                    =   *ptr++;
    _userCalcRomBuild                       =   *ptr++;
    _userCalcUserVersion                    =   *ptr++;
    _userCalcUserRevision                   =   *ptr++;
    _userCalcUserBuild                      =   *ptr++;
    _userCalcRomRevision                    =   *ptr++;
    _userCalcChecksum[0]                    =   *ptr++;
    _userCalcChecksum[1]                    =   *ptr++;
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiKV2ManufacturerTable000::~CtiAnsiKV2ManufacturerTable000()
{
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiKV2ManufacturerTable000::MeterMode_e CtiAnsiKV2ManufacturerTable000::getMeterMode()
{
    BYTEUSHORT mode;
    MeterMode_e retMode = demandOnlyMode;

    mode.ch[0] = _meterMode;
    switch (mode.sh)
    {
        case 0:
            retMode = demandOnlyMode;
            break;
        case 1:
            retMode = demandLPMode;
            break;
        case 2:
            retMode = timeOfUseMode;
            break;
    }
    return retMode;
}


CtiAnsiKV2ManufacturerTable000::MeterType_e CtiAnsiKV2ManufacturerTable000::getMeterType()
{
    BYTEUSHORT type;
    MeterType_e retType = CM21P;

    type.ch[0] = _meterType;
    switch (type.sh)
    {
        case 0:
            retType = CM21P;
            break;
        case 1:
            retType = kV;
            break;
        case 2:
            retType = kVModem;
            break;
        case 3:
            retType = kV2;
            break;
    }
    return retType;
}

CtiAnsiKV2ManufacturerTable000::RegisterFunction_e CtiAnsiKV2ManufacturerTable000::getRegisterFunction()
{
    BYTEUSHORT rf;
    RegisterFunction_e ret=Filler1;

    rf.ch[0] = _registerFunction;
    switch (rf.sh)
    {
        case 4:
            ret = demandOnly;
            break;
        case 5:
            ret = demandLP;
            break;
        case 6:
            ret = timeOfUse;
            break;
    }
    return ret;
}
void CtiAnsiKV2ManufacturerTable000::printResult(  )
{
    Cti::FormattedList itemList;

    itemList.add("MFG Version Number")  << _mfgVersionNumber;
    itemList.add("MFG Revision Number") << _mfgRevionsNumber;
    itemList.add("Meter Type")          << _meterType;
    itemList.add("Meter Mode")          << _meterMode;
    itemList.add("Register Function")   << _registerFunction;

    CTILOG_INFO(dout,
            endl << formatTableName("kV2 MFG Table 0") <<
            itemList
            );
}
