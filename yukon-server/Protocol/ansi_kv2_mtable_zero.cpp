#pragma warning( disable : 4786)
/*---------------------------------------------------------------------------------*
*
* File:   ansi_kv2_mtable_zero.cpp
*
* Class:
* Date:   2/20/2003
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/ansi_kv2_mtable_zero.cpp-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2003/04/25 14:54:54 $
*    History: 
      $Log: ansi_kv2_mtable_zero.cpp,v $
      Revision 1.1  2003/04/25 14:54:54  dsutton
      Ansi protocol tables specific to the implementation of the KV2


*----------------------------------------------------------------------------------*/

#include "ansi_kv2_mtable_zero.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiKV2ManufacturerTableZero::CtiAnsiKV2ManufacturerTableZero( BYTE *dataBlob )
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

CtiAnsiKV2ManufacturerTableZero::~CtiAnsiKV2ManufacturerTableZero()
{
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiKV2ManufacturerTableZero::MeterMode_e CtiAnsiKV2ManufacturerTableZero::getMeterMode()
{
    BYTEUSHORT mode;
    MeterMode_e retMode;

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


CtiAnsiKV2ManufacturerTableZero::MeterType_e CtiAnsiKV2ManufacturerTableZero::getMeterType()
{
    BYTEUSHORT type;
    MeterType_e retType;

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

CtiAnsiKV2ManufacturerTableZero::RegisterFunction_e CtiAnsiKV2ManufacturerTableZero::getRegisterFunction()
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



