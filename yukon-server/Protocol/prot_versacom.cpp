/*-----------------------------------------------------------------------------*
*
* File:   prot_versacom
*
* Date:   6/28/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.24 $
* DATE         :  $Date: 2005/06/24 16:13:51 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <windows.h>
#include <iomanip>
#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore


#include <rw/re.h>
#undef mask_

#include "ctidbgmem.h" // defines CTIDBG_new
#include "cparms.h"
#include "cmdparse.h"
#include "prot_versacom.h"
#include "master.h"
#include "msg_pcrequest.h"
#include "dllbase.h"
#include "devicetypes.h"
#include "logger.h"
#include "utility.h"



/* Routine to set a nibble in a message */
INT CtiProtocolVersacom::setNibble (INT iNibble, INT iValue)
{
    USHORT Nibble = (USHORT)iNibble;
    USHORT Value  = (USHORT)iValue;

    if((Nibble & 0x0001))
        _vst[_last]->Message[Nibble / 2] |= Value & 0x000f;
    else
        _vst[_last]->Message[Nibble / 2] |= (Value << 4) & 0x00f0;

    return(NORMAL);
}


INT CtiProtocolVersacom::initVersacomMessage()
{
    _vst[_last]->Nibbles = 2;

    memset (_vst[_last]->Message, 0, MAX_VERSACOM_MESSAGE);

    _addressMode = 0;

    if(!(_vst[_last]->Address))
    {
        /* Check if we are to use utility addressing */
        if(_vst[_last]->UtilityID)
            _addressMode |= 0x0008;

        if(_vst[_last]->Section)
            _addressMode |= 0x0004;

        if(_vst[_last]->Class)
            _addressMode |= 0x0002;

        if(_vst[_last]->Division)
            _addressMode |= 0x0001;
    }

    setNibble (1, _addressMode);

    return NORMAL;
}

INT CtiProtocolVersacom::assembleCommandToMessage()
{
    ULONG    i;
    USHORT   Mask;
    USHORT   Flag;

    ULONG    Divisor;
    ULONG    ComputeTime;


    switch(_vst[_last]->CommandType)
    {
    case VCONTROL:
        {
            setNibble(0, VCONTROL);

            /* Leave room for the count */
            _vst[_last]->Nibbles++;
            /* build up the control actions */
            for(i = 0; i < 3; i++)
            {
                Mask = 0;
                if(_vst[_last]->Load[i].Relay[0])
                    Mask |= 0x0004;
                if(_vst[_last]->Load[i].Relay[1])
                    Mask |= 0x0002;
                if(_vst[_last]->Load[i].Relay[2])
                    Mask |= 0x0001;
                if(Mask)
                {
                    if(_vst[_last]->Load[i].ControlType)
                        Mask |= 0x0008;
                    setNibble(_vst[_last]->Nibbles++, Mask);
                    setNibble(_vst[_last]->Nibbles++, _vst[_last]->Load[i].TimeCode);
                }
                else
                {
                    break;
                }
            }

            /* see if we have anything to send */
            if(!(i) && !(Mask))
                return(NORMAL);
            else
                setNibble ( 2, (USHORT)i);

            break;

        }
    case VINITIATOR:
        {
            setNibble ( 0, VINITIATOR);
            /* see if this is ripple */
            if(isLCU(_transmitterType))
                setNibble ( 1, _vst[_last]->Initiator);
            else
                setNibble ( _vst[_last]->Nibbles++, _vst[_last]->Initiator);

            break;
        }
    case VCONFIG:
        {
            setNibble ( 0, VCONFIG);

            /* set the configuration type */
            setNibble ( _vst[_last]->Nibbles++, _vst[_last]->VConfig.ConfigType >> 4);
            setNibble ( _vst[_last]->Nibbles++, _vst[_last]->VConfig.ConfigType);

            /* Now load the data */
            for(i = 0; i < 10; i++)
            {
                if((i & 0x0001))
                {
                    setNibble ( _vst[_last]->Nibbles++, _vst[_last]->VConfig.Data[i / 2]);
                }
                else
                {
                    setNibble ( _vst[_last]->Nibbles++, _vst[_last]->VConfig.Data[i / 2] >> 4);
                }
            }
            break;
        }
    case VFULLADDRESS:
        {
            setNibble ( 0, VFULLADDRESS);

            /* Now load the bytes up to the service byte.  i represents the number of nibbles in this message!*/
            for(i = 0; i < 15; i++)
            {
                if((i & 0x0001))
                {
                    setNibble ( _vst[_last]->Nibbles++, _vst[_last]->VData.Data[i / 2]);
                }
                else
                {
                    #if 0
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << hex << setw(2) << (int)_vst[_last]->VData.Data[i / 2] << endl;
                    }
                    #endif
                    setNibble ( _vst[_last]->Nibbles++, _vst[_last]->VData.Data[i / 2] >> 4);
                }
            }

            break;
        }
    case VCOUNTRESET:
        setNibble ( 0, VCOUNTRESET);

        setNibble ( _vst[_last]->Nibbles++, _vst[_last]->CountReset >> 4);
        setNibble (  _vst[_last]->Nibbles++, _vst[_last]->CountReset);
        break;


    case VSERVICE:
        setNibble ( 0, VSERVICE);
        setNibble ( _vst[_last]->Nibbles++, _vst[_last]->Service);
        break;

    case VEX_SERVICE:
        {
            USHORT flag = (_vst[_last]->ExService.Cancel ? 0x0004 : 0x0000) | (_vst[_last]->ExService.LED_Off ? 0x0002 : 0x0000);

            setNibble ( 0, VADDITIONAL);
            setNibble ( _vst[_last]->Nibbles++, VSERVICE);  // Service is a 9 as-is EX_SERVICE.. Pay attention.
            setNibble ( _vst[_last]->Nibbles++, flag);

            if(!(_vst[_last]->ExService.Cancel))
            {
                USHORT offtime = _vst[_last]->ExService.TOOS_Time;
                setNibble ( _vst[_last]->Nibbles++, offtime >> 12);
                setNibble ( _vst[_last]->Nibbles++, offtime >> 8);
                setNibble ( _vst[_last]->Nibbles++, offtime >> 4);
                setNibble ( _vst[_last]->Nibbles++, offtime);
            }

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            break;
        }
    case VPROPOGATION:
        setNibble ( 0, VPROPOGATION);
        setNibble ( _vst[_last]->Nibbles++, _vst[_last]->PropDIT);
        break;

    case VDATA:
        {
            setNibble ( 0, VDATA);

            if(_vst[_last]->VData.DataType)
                _vst[_last]->VData.Data[0] |= 0x80;
            else
                _vst[_last]->VData.Data[0] &= 0x7f;

            for(i = 0; i < 6; i++)
            {
                _vst[_last]->Message[i+1] = _vst[_last]->VData.Data[i];
            }

            _vst[_last]->Nibbles += 12;

            break;
        }
    case EXDATA:
        {
            setNibble ( 0, EXDATA);
            // Nibble 1 has been set in init as addressing mode!
            setNibble ( 2, (_vst[_last]->VData.DataLength >> 1) & 0x000f);
            setNibble ( 3, ((_vst[_last]->VData.DataLength << 3) & 0x0008) | (_vst[_last]->VData.DataType & 0x0007));


            if(_vst[_last]->VData.DataLength < 1)
            {
                _vst[_last]->VData.DataLength = 1;
            }
            else if(_vst[_last]->VData.DataLength > 31)
            {
                _vst[_last]->VData.DataLength = 31;
            }

            for(i = 0; i < _vst[_last]->VData.DataLength; i++)
            {
                _vst[_last]->Message[i + 2] = _vst[_last]->VData.Data[i];
            }

            _vst[_last]->Nibbles += 2 + (_vst[_last]->VData.DataLength * 2);

            // dumpMessageBuffer();

            break;
        }
    case VECONTROL:
        {
            setNibble ( 0, VECONTROL );

            /* Calculate the delay time */
            if(_vst[_last]->ELoad.DelayUntil <= LongTime ())
                _vst[_last]->ELoad.DelayUntil = 0;
            else
                _vst[_last]->ELoad.DelayUntil -= LongTime ();

            /* figure out the relays */
            Mask = _vst[_last]->ELoad.Relay;

            if(_vst[_last]->ELoad.ControlType)
                Mask |= 0x0008;

            /* xRRR - (bit 3 == 1) discrete command, (bits 2,1,0) relays */
            setNibble ( _vst[_last]->Nibbles++, Mask);

            if(_vst[_last]->ELoad.ControlType)
            {
                /* figure out what order of magnitude out control is */
                if(_vst[_last]->ELoad.ControlTime <= 3825L)
                {
                    Flag = 0;
                    Divisor = 15L;
                    if(_vst[_last]->ELoad.ControlTime % 15L)
                        _vst[_last]->ELoad.ControlTime += 15L;
                }
                else if(_vst[_last]->ELoad.ControlTime <= 15300L)
                {
                    Flag = 1;
                    Divisor = 60L;
                    if(_vst[_last]->ELoad.ControlTime % 60L)
                        _vst[_last]->ELoad.ControlTime += 60L;
                }
                else if(_vst[_last]->ELoad.ControlTime <= 76500L)
                {
                    Flag = 2;
                    Divisor = 300L;
                    if(_vst[_last]->ELoad.ControlTime % 300L)
                        _vst[_last]->ELoad.ControlTime += 300L;
                }
                else if(_vst[_last]->ELoad.ControlTime <= 229500L)
                {
                    Flag = 3;
                    Divisor = 900L;
                    if(_vst[_last]->ELoad.ControlTime % 900L)
                        _vst[_last]->ELoad.ControlTime += 900L;
                }
                else
                {
                    Flag = 3;
                    Divisor = 900L;
                    _vst[_last]->ELoad.ControlTime = 229500L;
                }

                Mask = Flag << 2;

                if(_vst[_last]->ELoad.RandomTime)
                    Mask |= 0x0002;
                if(_vst[_last]->ELoad.DelayUntil)
                    Mask |= 0x0001;

                /* FLAG */
                setNibble ( _vst[_last]->Nibbles++, Mask);

                /* CONTROL - set the control time */
                setNibble ( _vst[_last]->Nibbles++, (USHORT) (_vst[_last]->ELoad.ControlTime / Divisor) >> 4);

                setNibble ( _vst[_last]->Nibbles++, (USHORT) (_vst[_last]->ELoad.ControlTime / Divisor));

                /* see if we need randomization */
                if(_vst[_last]->ELoad.RandomTime)
                {
                    if(_vst[_last]->ELoad.RandomTime % Divisor)
                        _vst[_last]->ELoad.RandomTime += Divisor;
                    Mask = (USHORT) (_vst[_last]->ELoad.RandomTime / Divisor);
                    if(Mask > 15)
                        Mask = 15;

                    /* RAND */
                    setNibble ( _vst[_last]->Nibbles++, Mask);
                }
            }
            else
            {
                ComputeTime = _vst[_last]->ELoad.Window;

                /* get the divisor and the flag*/
                if(ComputeTime < 945L)
                {
                    Flag = 0;
                    Divisor = 15L;
                    if(ComputeTime % 15L)
                        ComputeTime += 15L;
                }
                else if(ComputeTime == 945L)
                {
                    Flag = 0;
                    Divisor = 15L;
                }
                else if(ComputeTime < 3780L)
                {
                    Flag = 1;
                    Divisor = 60L;
                    if(ComputeTime % 60L)
                        ComputeTime += 60L;
                }
                else if(ComputeTime == 3780L)
                {
                    Flag = 1;
                    Divisor = 60L;
                }
                else if(ComputeTime < 18900L)
                {
                    Flag = 2;
                    Divisor = 300L;
                    if(ComputeTime % 300L)
                        ComputeTime += 300L;
                }
                else if(ComputeTime == 18900L)
                {
                    Flag = 2;
                    Divisor = 300L;
                }
                else if(ComputeTime < 56700L)
                {
                    Flag = 3;
                    Divisor = 900L;
                    if(ComputeTime % 900L)
                        ComputeTime += 900L;
                }
                else if(ComputeTime == 56700L)
                {
                    Flag = 3;
                    Divisor = 900L;
                }
                else
                {
                    Flag = 3;
                    Divisor = 900L;
                    ComputeTime = 56700L;
                }

                Mask = Flag << 2;
                if(ComputeTime || _vst[_last]->ELoad.Count)
                    Mask |= 0x0002;
                if(_vst[_last]->ELoad.DelayUntil)
                    Mask |= 0x0001;

                /* FLAG */
                setNibble ( _vst[_last]->Nibbles++, Mask);

                /* figure out if this is a bump or cycling */
                if(_vst[_last]->ELoad.CycleType)
                {
                    /* Bump */
                    Mask = 0x0080;
                    if(_vst[_last]->ELoad.Percent < 0)
                    {
                        _vst[_last]->ELoad.Percent = 0 - _vst[_last]->ELoad.Percent;
                    }
                    else
                        Mask |= 0x0040;

                    if(_vst[_last]->ELoad.Percent > 50)
                        _vst[_last]->ELoad.Percent = 50;

                    Mask |= _vst[_last]->ELoad.Percent & 0x003f;

                }
                else
                {
                    /* Cycling */
                    Mask = 0;
                    if(_vst[_last]->ELoad.Percent > 100)
                        _vst[_last]->ELoad.Percent = 100;
                    if(_vst[_last]->ELoad.Percent < 0)
                        _vst[_last]->ELoad.Percent = 0;

                    Mask |= _vst[_last]->ELoad.Percent & 0x007f;

                }

                /* PERCENT High byte */
                setNibble ( _vst[_last]->Nibbles++, Mask >> 4);

                /* PERCENT Low byte */
                setNibble ( _vst[_last]->Nibbles++, Mask);

                if(ComputeTime || _vst[_last]->ELoad.Count)
                {
                    /* change the times into what will be sent */
                    if(_vst[_last]->ELoad.Window > 56700L)
                        _vst[_last]->ELoad.Window = 56700L;

                    if(_vst[_last]->ELoad.Window % Divisor)
                        _vst[_last]->ELoad.Window += (USHORT)Divisor;

                    _vst[_last]->ELoad.Window /= (USHORT)Divisor;

                    /* Put them in the message */

                    /* WINDOW */
                    setNibble ( _vst[_last]->Nibbles++, _vst[_last]->ELoad.Window >> 2);

                    /* WINDOW / COUNT */
                    setNibble (
                              _vst[_last]->Nibbles++,
                              (_vst[_last]->ELoad.Window << 2) |
                              ((_vst[_last]->ELoad.Count >> 4) & 0x0003));

                    /* COUNT */
                    setNibble ( _vst[_last]->Nibbles++, _vst[_last]->ELoad.Count);

                }
            }

            /* see if we need delay */
            if(_vst[_last]->ELoad.DelayUntil)
            {
                if(_vst[_last]->ELoad.DelayUntil < 945L)
                {
                    Flag = 0;
                    Divisor = 15L;
                    if(_vst[_last]->ELoad.DelayUntil % 15L)
                        _vst[_last]->ELoad.DelayUntil += 15L;
                }
                else if(_vst[_last]->ELoad.DelayUntil == 945L)
                {
                    Flag = 0;
                    Divisor = 15L;
                }
                else if(_vst[_last]->ELoad.DelayUntil < 3780L)
                {
                    Flag = 1;
                    Divisor = 60L;
                    if(_vst[_last]->ELoad.DelayUntil % 60L)
                        _vst[_last]->ELoad.DelayUntil += 60L;
                }
                else if(_vst[_last]->ELoad.DelayUntil == 3780L)
                {
                    Flag = 1;
                    Divisor = 60L;
                }
                else if(_vst[_last]->ELoad.DelayUntil < 18900L)
                {
                    Flag = 2;
                    Divisor = 300L;
                    if(_vst[_last]->ELoad.DelayUntil % 300L)
                        _vst[_last]->ELoad.DelayUntil += 300L;
                }
                else if(_vst[_last]->ELoad.DelayUntil == 18900L)
                {
                    Flag = 2;
                    Divisor = 300L;
                }
                else if(_vst[_last]->ELoad.DelayUntil < 56700L)
                {
                    Flag = 3;
                    Divisor = 900L;
                    if(_vst[_last]->ELoad.DelayUntil % 900L)
                        _vst[_last]->ELoad.DelayUntil += 900L;
                }
                else if(_vst[_last]->ELoad.DelayUntil == 56700L)
                {
                    Flag = 3;
                    Divisor = 900L;
                }
                else
                {
                    Flag = 3;
                    Divisor = 900L;
                    _vst[_last]->ELoad.DelayUntil = 56700L;
                }

                Mask = Flag << 6;
                Mask |= ((USHORT) (_vst[_last]->ELoad.DelayUntil / Divisor)) & 0x003f;

                /* DELAY High byte */
                setNibble ( _vst[_last]->Nibbles++, Mask >> 4);

                /* DELAY Low byte */
                setNibble ( _vst[_last]->Nibbles++, Mask);

            }

            break;
        }
    case VSCRAM:
        {
            setNibble ( 0, VSCRAM);
            break;
        }
    case VFILLER:
        {
            setNibble ( 0, VFILLER);
            break;
        }
    }
    return NORMAL;
}

INT CtiProtocolVersacom::assembleAddressing()
{
    INT status = NORMAL;
    ULONG IAddress = 0;

    /* Now go ahead and figure out the addressing */
    switch(_vst[_last]->CommandType)
    {
    /* Handle the special cases */
    case VSCRAM:
        if(isLCU(_transmitterType))
        {
            if(!(_addressMode) && !(_vst[_last]->Address))
                break;
        }
    case VINITIATOR:
        if(isLCU(_transmitterType))
            break;

    default:
        {
            if(_addressMode == 0 && _vst[_last]->Address == 0)    // We have no addressing information
            {
                /*
                 *  OK, if the user types serial and doesn't specify an address, what should happen???
                 *  Let's make sure we don't do anything
                 */
                RWMutexLock::LockGuard  guard(coutMux);
                cout << "**** ADDRESSING ERROR **** " << __FILE__ << " (" << __LINE__ << ")" << endl;

                status = ADDRESSERROR;
            }

            /* Now build up the addressing */
            if(_addressMode)
            {
                if(_vst[_last]->UtilityID)
                {
                    setNibble ( _vst[_last]->Nibbles++, _vst[_last]->UtilityID >> 4);
                    setNibble ( _vst[_last]->Nibbles++, _vst[_last]->UtilityID);
                }

                if(_vst[_last]->Section)
                {
                    setNibble ( _vst[_last]->Nibbles++, _vst[_last]->Section >>4);

                    setNibble ( _vst[_last]->Nibbles++, _vst[_last]->Section);
                }

                /* Now we will build a long word with class and division in it as appropriate */
                /* This will end up being treated just like a unique address */
                if(_vst[_last]->Class)
                {
                    if(_vst[_last]->Division)
                    {
                        _vst[_last]->Address =  (ULONG) _vst[_last]->Class & 0x0000000f;
                        _vst[_last]->Address |= (((ULONG) _vst[_last]->Division) << 4) & 0x000000f0;
                        _vst[_last]->Address |= (((ULONG) _vst[_last]->Class) << 4) & 0x00000f00;
                        _vst[_last]->Address |= (((ULONG) _vst[_last]->Division) << 8) & 0x0000f000;
                        _vst[_last]->Address |= (((ULONG) _vst[_last]->Class) << 8) & 0x000f0000;
                        _vst[_last]->Address |= (((ULONG) _vst[_last]->Division) << 12) & 0x00f00000;
                        _vst[_last]->Address |= (((ULONG) _vst[_last]->Class) << 12) & 0x0f000000;
                        _vst[_last]->Address |= (((ULONG) _vst[_last]->Division) << 16) & 0xf0000000;
                    }
                    else
                        _vst[_last]->Address = _vst[_last]->Class;
                }
                else
                {
                    if(_vst[_last]->Division)
                        _vst[_last]->Address = _vst[_last]->Division;
                    else
                        _vst[_last]->Address = 0L;
                }
            }

            if(_vst[_last]->Address)
            {
                ULONG i;
                ULONG Nibbles;

                /* Invert the beast */
                IAddress = 0L;
                for(i = 0; i < 32; i++)
                    IAddress |= ((_vst[_last]->Address >> i) & 0x00000001) << (31 - i);

                /* find out how many nibbles we need to load */
                for(i = 0; i < 8; i++)
                {
                    if(((IAddress >> (4 * i)) & 0x0f))
                        break;
                }

                Nibbles = 8 - i;

                /* Load it into the message */
                for(i = 0; i < Nibbles; i++)
                {
                    setNibble ( _vst[_last]->Nibbles++, (USHORT) (IAddress >> ((7 - i) * 4)));
                }
            }

            break;
        }
    }

    return status;
}

INT CtiProtocolVersacom::updateVersacomMessage()
{
    INT status = NORMAL;

    status = initVersacomMessage();     // Prime the message buffer and the constants
    if(!status) status = assembleCommandToMessage();
    if(!status) status = assembleAddressing();

    // dumpMessageBuffer();

    return status;
}

VSTRUCT CtiProtocolVersacom::getVStruct(INT pos) const
{
    return *_vst[pos];
}
VSTRUCT& CtiProtocolVersacom::getVStruct(INT pos)
{
    return *_vst[pos];
}
CtiProtocolVersacom& CtiProtocolVersacom::setVStruct(const VSTRUCT &aVst)
{
    *_vst[_last] = aVst;

    return *this;
}

INT CtiProtocolVersacom::getTransmitterType() const
{
    return _transmitterType;
}
CtiProtocolVersacom& CtiProtocolVersacom::setTransmitterType(INT type)
{
    _transmitterType = type;
    return *this;
}

/*-------------------------------------------------------------------------*
 * Group addressing is COMPLETELY ignored if Serial Number (Serial) is non
 * zero!
 *-------------------------------------------------------------------------*/
INT CtiProtocolVersacom::adjustVersacomAddress(VSTRUCT &vTemp, ULONG Serial, UINT Uid, UINT Section, UINT Class, UINT Division)
{
    INT status = NORMAL;

    if(!(Serial))
    {
        /* Check if we are to use utility addressing */
        if(Uid)         vTemp.UtilityID = Uid;
        if(Section)     vTemp.Section = Section;
        if(Class)       vTemp.Class = Class;
        if(Division)    vTemp.Division = Division;
    }
    else
    {
        vTemp.Address = Serial;
    }

    return status;
}

INT    CtiProtocolVersacom::VersacomShedCommand(UINT controltime, UINT relaymask)
{
    INT i, mask;

    _vst[_last]->CommandType           = VCONTROL;
    _vst[_last]->Load[0].ControlType   = TRUE;
    _vst[_last]->Load[0].TimeCode      = VersacomControlDuration(_vst[_last]->Load[0].ControlType, controltime);

    if(_vst[_last]->RelayMask != 0)
    {
        /*
         *  This means we set this in a higher level of context and the actual relaymask
         *  passed in should be ignored...
         */
        relaymask = _vst[_last]->RelayMask;
    }

    for(i = 0, mask = 0x01; i < 3; i++, mask <<= 1)
    {
        _vst[_last]->Load[0].Relay[i] = ( (relaymask & mask) ? TRUE : FALSE);
    }

    /* OK this is all set-up for the builder to manhandle now! */
    return updateVersacomMessage();
}

INT    CtiProtocolVersacom::VersacomShedCommandEx(UINT controltime,   // = 0,
                                                  UINT relay,         // = 0,
                                                  UINT rand,          // = 120,
                                                  UINT delay)         // = 0)
{
    _vst[_last]->CommandType           = VECONTROL;
    _vst[_last]->ELoad.ControlType     = 1;           // Discrete (shed) control
    _vst[_last]->ELoad.CycleType       = 0;

    if(_vst[_last]->RelayMask != 0 && relay == 0)
    {
        /*
         *  This means we set this in a higher level of context and the actual relaymask
         *  passed in should be ignored...
         */
        relay = _vst[_last]->RelayMask;
    }


    _vst[_last]->ELoad.Relay           = relay;       // relay number or zero for ALL relays!
    _vst[_last]->ELoad.ControlTime     = controltime; // VersacomControlDurationEx(_vst[_last]->ELoad.ControlType, controltime);
    _vst[_last]->ELoad.RandomTime      = rand;
    _vst[_last]->ELoad.DelayUntil      = delay;

    /* OK this is all set-up for the builder to manhandle now! */
    return updateVersacomMessage();
}

INT    CtiProtocolVersacom::VersacomCycleCommandEx(UINT percent,      // = 0,
                                                   UINT relay,        // = 0,
                                                   UINT period,       // = 30,
                                                   UINT repeat,       // = 8,
                                                   UINT delay)        // = 0)
{
    _vst[_last]->CommandType           = VECONTROL;

    _vst[_last]->ELoad.ControlType     = 0;           // cycle control
    _vst[_last]->ELoad.CycleType       = 0;           // Cycle (not Bump) command

    if(_vst[_last]->RelayMask != 0 && relay == 0)
    {
        /*
         *  This means we set this in a higher level of context and the actual relaymask
         *  passed in should be ignored...
         */

        relay = _vst[_last]->RelayMask;
    }

    _vst[_last]->ELoad.Relay           = relay;       // relay number or zero for ALL relays!
    _vst[_last]->ELoad.Percent         = percent;     // 0 - 100, 127 means terminate!

    _vst[_last]->ELoad.Window          = (((period < 1) ? 30 : period) * 60); // VersacomControlDurationEx(_vst[_last]->ELoad.ControlType, period);
    _vst[_last]->ELoad.Count           = repeat;
    _vst[_last]->ELoad.DelayUntil      = delay;

    /* OK this is all set-up for the builder to manhandle now! */
    return updateVersacomMessage();
}

INT    CtiProtocolVersacom::VersacomCycleCommand(UINT controltime, UINT relaymask)
{
    INT i, mask;

    _vst[_last]->CommandType           = VCONTROL;
    _vst[_last]->Load[0].ControlType   = FALSE;
    _vst[_last]->Load[0].TimeCode      = VersacomControlDuration(_vst[_last]->Load[0].ControlType, controltime);

    if(_vst[_last]->RelayMask != 0 && relaymask == 0)
    {
        /*
         *  This means we set this in a higher level of context and the actual relaymask
         *  passed in should be ignored...
         */
        relaymask = _vst[_last]->RelayMask;
    }

    for(i = 0, mask = 0x01; i < 3; i++, mask <<= 1)
    {
        _vst[_last]->Load[0].Relay[i] = ( (relaymask & mask) ? TRUE : FALSE);
    }

    /* OK this is all set-up for the builder to manhandle now! */
    return updateVersacomMessage();
}

UINT CtiProtocolVersacom::VersacomControlDuration(UINT type, UINT controltime)
{
    UINT result = 0;

    if(type == 0)     // This is a cyclic percentage!
    {
        if(0 < controltime && controltime <= 5)
        {
            result = 1;     // 5%
        }
        else if(5 < controltime && controltime <= 10)
        {
            result = 2;     // 10%
        }
        else if(10 < controltime && controltime <= 20)
        {
            result = 3;     // 20%
        }
        else if(20 < controltime && controltime <= 25)
        {
            result = 4;     // 25%
        }
        else if(25 < controltime && controltime <= 30)
        {
            result = 5;     // 30%
        }
        else if(30 < controltime && controltime <= 33)
        {
            result = 6;     // 33%
        }
        else if(33 < controltime && controltime <= 40)
        {
            result = 7;     // 40%
        }
        else if(40 < controltime && controltime <= 50)
        {
            result = 8;     // 50%
        }
        else if(50 < controltime && controltime <= 60)
        {
            result = 9;     // 60%
        }
        else if(60 < controltime && controltime <= 67)
        {
            result = 10;     // 67%
        }
        else if(67 < controltime && controltime <= 70)
        {
            result = 11;     // 70%
        }
        else if(70 < controltime && controltime <= 75)
        {
            result = 12;     // 75%
        }
        else if(75 < controltime && controltime <= 80)
        {
            result = 13;     // 80%
        }
        else if(80 < controltime && controltime <= 90)
        {
            result = 14;     // 90%
        }
        else if(90 < controltime && controltime <= 95)
        {
            result = 15;     // 95%
        }
        else if(95 < controltime && controltime <= 100)
        {
            cout << "Versacom Load Control cycles at 95% maximum!.. " << endl;
            // cout << " I'll pretend that is what you meant... " << endl;
            result = 15;     // 95%
        }
    }
    else
    {
        if(0 < controltime && controltime <= 1)
        {
            result = 1;     // 1 second shed
        }
        else if(1 < controltime && controltime <= 60)
        {
            result = 2;     // 1 minute shed
        }
        else if(60 < controltime && controltime <= 300)
        {
            result = 3;     // 5 minute shed
        }
        else if(300 < controltime && controltime <= 450)
        {
            result = 4;     // 7.5 minute shed
        }
        else if(450 < controltime && controltime <= 600)
        {
            result = 5;     // 10 minute shed
        }
        else if(600 < controltime && controltime <= 900)
        {
            result = 6;     // 15 minute shed
        }
        else if(900 < controltime && controltime <= 1200)
        {
            result = 7;     // 20 minute shed
        }
        else if(1200 < controltime && controltime <= 1800)
        {
            result = 8;     // 30 minute shed
        }
        else if(1800 < controltime && controltime <= 2400)
        {
            result = 9;     // 40 minutes
        }
        else if(2400 < controltime && controltime <= 2700)
        {
            result = 10;     // 45 minutes
        }
        else if(2700 < controltime && controltime <= 3600)
        {
            result = 11;     // 1 hour shed
        }
        else if(3600 < controltime && controltime <= 7200)
        {
            result = 12;     // 2 hour shed
        }
        else if(7200 < controltime && controltime <= 10800)
        {
            result = 13;     // 3 hour shed
        }
        else if(10800 < controltime && controltime <= 14400)
        {
            result = 14;     // 4 hour shed
        }
        else if(14400 < controltime && controltime <= 28800)
        {
            result = 15;     // 8 hour shed
        }
        else if(28800 < controltime)
        {
            cout << "Versacom Load Control sheds up to 8 hours maximum!.. " << endl;
            // cout << " I'll pretend that is what you meant... " << endl;
            result = 15;     // 8 hours
        }
    }

    return result;
}

UINT CtiProtocolVersacom::VersacomControlDurationEx(UINT type, UINT controltime)
{
    UINT result = 0;

    if(type == 0)     // This is a cycle command
    {
        if(controltime < 1)
        {
            controltime = 30;       // Use 30 minutes if they don't know what they are doing!
        }


        result = controltime * 60;
    }
    else
    {
    }

    return result;
}

INT    CtiProtocolVersacom::VersacomRestoreCommand(UINT relaymask)
{
    INT i, mask;

    _vst[_last]->CommandType           = VCONTROL;
    _vst[_last]->Load[0].ControlType   = TRUE;
    _vst[_last]->Load[0].TimeCode      = 0;           // Restore from shed!

    if(_vst[_last]->RelayMask != 0)
    {
        /*
         *  This means we set this in a higher level of context and the actual relaymask
         *  passed in should be ignored...
         */
        relaymask = _vst[_last]->RelayMask;
    }

    for(i = 0, mask = 0x01; i < 3; i++, mask <<= 1)
    {
        _vst[_last]->Load[0].Relay[i] = ( (relaymask & mask) ? TRUE : FALSE);
    }

    /* OK this is all set-up for the builder to manhandle now! */
    return updateVersacomMessage();
}

INT    CtiProtocolVersacom::VersacomTerminateCommand(UINT relaymask)
{
    INT i, mask;

    _vst[_last]->CommandType           = VCONTROL;
    _vst[_last]->Load[0].ControlType   = FALSE;
    _vst[_last]->Load[0].TimeCode      = 0;           // Terminate the cycle command!

    if(_vst[_last]->RelayMask != 0)
    {
        /*
         *  This means we set this in a higher level of context and the actual relaymask
         *  passed in should be ignored...
         */
        relaymask = _vst[_last]->RelayMask;
    }

    for(i = 0, mask = 0x01; i < 3; i++, mask <<= 1)
    {
        _vst[_last]->Load[0].Relay[i] = ( (relaymask & mask) ? TRUE : FALSE);
    }

    /* OK this is all set-up for the builder to manhandle now! */
    return updateVersacomMessage();
}

INT    CtiProtocolVersacom::VersacomCapacitorControlCommand(BOOL Trip)
{
    /*
     *  This is a TRIP opreation if TripClose != 0.
     */

    _vst[_last]->CommandType           = VDATA;

    _vst[_last]->VData.DataType        = 0;        // ASCII, not TRL.
    _vst[_last]->VData.Data[0]         = 0x71;
    _vst[_last]->VData.Data[1]         = ( (Trip == 0) ? 0x80 : 0x40);   // close == 0x80, trip == 0x40
    _vst[_last]->VData.Data[2]         = 0x00;
    _vst[_last]->VData.Data[3]         = 0x00;
    _vst[_last]->VData.Data[4]         = 0x00;
    _vst[_last]->VData.Data[5]         = 0x00;


    /* OK this is all set-up for the builder to manhandle now! */
    return updateVersacomMessage();
}

INT    CtiProtocolVersacom::VersacomVoltageControlCommand(BOOL Enable)
{
    /*
     *  This is a ENABLE opreation if Enable != 0.
     */

    _vst[_last]->CommandType           = VDATA;

    _vst[_last]->VData.DataType        = 0;        // ASCII, not TRL.
    _vst[_last]->VData.Data[0]         = 0x71;
    _vst[_last]->VData.Data[1]         = ( (Enable == 0) ? 0xD0 : 0xE0);
    _vst[_last]->VData.Data[2]         = 0x00;
    _vst[_last]->VData.Data[3]         = 0x00;
    _vst[_last]->VData.Data[4]         = 0x00;
    _vst[_last]->VData.Data[5]         = 0x00;


    /* OK this is all set-up for the builder to manhandle now! */
    return updateVersacomMessage();
}

INT    CtiProtocolVersacom::VersacomInitiatorCommand(UINT Initiator)
{
    /*
     *  This is a ENABLE opreation if EnableDiasable != 0.
     */

    _vst[_last]->CommandType           = VINITIATOR;
    _vst[_last]->Initiator             = (USHORT) Initiator;

    /* OK this is all set-up for the builder to manhandle now! */
    return updateVersacomMessage();
}

INT    CtiProtocolVersacom::VersacomServiceCommand(UINT serviceflag)
{
    /*-------------------------------------------------------------------------*
     * Builds a message to remove or restore service to a versacom switch.
     *  ONE or more of the following may be defined, so long as they are not
     *  mutually exclusive.
     *  serviceflag == VC_SERVICE_T_OUT == 1 is Temporary OUT of service
     *  serviceflag == VC_SERVICE_T_IN  == 2 is Temporary IN service
     *  serviceflag == VC_SERVICE_C_OUT == 4 is Contractual OUT of service
     *  serviceflag == VC_SERVICE_C_IN  == 8 is Contractual IN service
     *  serviceflag == VC_SERVICE_MASK  == 0x0f is a mask
     *-------------------------------------------------------------------------*/

    _vst[_last]->CommandType           = VSERVICE;

    if( serviceflag & VC_SERVICE_T_IN && serviceflag & VC_SERVICE_T_OUT )
    {
        cout << RWTime() << " both in and out of service bits are set" << endl;
        cout << RWTime() << "  assuming temporary IN service" << endl;

        serviceflag &=  ~VC_SERVICE_T_OUT;     // Get rid of this!
    }

    if( serviceflag & VC_SERVICE_C_IN && serviceflag & VC_SERVICE_C_OUT )
    {
        cout << RWTime() << " both in and out of service bits are set" << endl;
        cout << RWTime() << "  assuming contractual IN service" << endl;

        serviceflag &=  ~VC_SERVICE_C_OUT;     // Get rid of this!
    }

    _vst[_last]->Service               = (USHORT)(serviceflag & VC_SERVICE_MASK);

    /* OK this is all set-up for the builder to manhandle now! */
    return updateVersacomMessage();
}

INT CtiProtocolVersacom::VersacomExtendedServiceCommand(bool cancel, bool led_off, int offtimeinhours )
{

    _vst[_last]->CommandType = VEX_SERVICE;

    _vst[_last]->ExService.Cancel = (cancel ? TRUE : FALSE);
    _vst[_last]->ExService.LED_Off = (led_off ? TRUE : FALSE);
    _vst[_last]->ExService.TOOS_Time = (USHORT)offtimeinhours;

    /* OK this is all set-up for the builder to manhandle now! */
    return updateVersacomMessage();
}

INT    CtiProtocolVersacom::VersacomConfigLEDCommand(BYTE leds)
{
    /*
     * leds is a mask of the following!
     * VC_REPORT_LED_ENABLED
     * VC_STATUS_LED_ENABLED
     * VC_LOAD_LED_ENABLED
     */

    _vst[_last]->CommandType           = VCONFIG;
    _vst[_last]->VConfig.ConfigType    = (USHORT)VCT_LEDConfig;
    _vst[_last]->VConfig.Data[0]       = (BYTE)(leds & 0xE0 );
    _vst[_last]->VConfig.Data[1]       = (BYTE)0x00;
    _vst[_last]->VConfig.Data[2]       = (BYTE)0x00;
    _vst[_last]->VConfig.Data[3]       = (BYTE)0x00;
    _vst[_last]->VConfig.Data[4]       = (BYTE)0x00;

    /* OK this is all set-up for the builder to manhandle now! */
    return updateVersacomMessage();
}

INT CtiProtocolVersacom::VersacomDataCommand(BYTE *message, INT len)
{
    int i;
    _vst[_last]->CommandType           = ((len > 6) ? EXDATA : VDATA);

    for(i = 0; i < len && i < 30; i++)
    {
        _vst[_last]->VData.Data[i]      = (BYTE)message[i];
    }

    _vst[_last]->VData.DataLength      = (USHORT)i;
    _vst[_last]->VData.DataType        = 0;                 // Make it BE ASCII.

    return updateVersacomMessage();
}

INT CtiProtocolVersacom::VersacomConfigCommand(UINT configtype, BYTE *cfg)
{
    int i;

    _vst[_last]->CommandType           = VCONFIG;
    _vst[_last]->VConfig.ConfigType    = (USHORT)configtype;

    for(i = 0; i < 5; i++)
    {
        _vst[_last]->VConfig.Data[i]      = (BYTE)cfg[i];
    }

    return updateVersacomMessage();
}

INT CtiProtocolVersacom::VersacomFillerCommand(BYTE uid)
{
    int i;

    _vst[_last]->CommandType = VFILLER;
    _vst[_last]->Address = 0;
    _vst[_last]->UtilityID = uid;

    return updateVersacomMessage();
}

INT CtiProtocolVersacom::VersacomRawConfigCommand(const BYTE *cfg)
{
    int i;

    _vst[_last]->CommandType           = VCONFIG;
    _vst[_last]->VConfig.ConfigType    = (USHORT)cfg[0];

    for(i = 0; i < 5; i++)
    {
        _vst[_last]->VConfig.Data[i]      = (BYTE)cfg[i + 1];
    }

    return updateVersacomMessage();
}

INT CtiProtocolVersacom::VersacomPropagationCommand(BYTE propmask)
{
    int i;

    /*
     * VC_PROPTERMINATE
     * VC_PROPINCREMENT
     * VC_PROPDISPLAY
     */

    _vst[_last]->CommandType           = VPROPOGATION;
    _vst[_last]->PropDIT               = (USHORT)(propmask & 0x07);

    return updateVersacomMessage();
}

INT CtiProtocolVersacom::VersacomConfigPropagationTimeCommand(BYTE duration)
{
    if(duration > 9)
    {
        duration = 9;
    }
    _vst[_last]->CommandType           = VCONFIG;
    _vst[_last]->VConfig.ConfigType    = (USHORT)VCT_PropDisplayTime;
    _vst[_last]->VConfig.Data[0]       = (BYTE)duration;
    _vst[_last]->VConfig.Data[1]       = (BYTE)0x00;
    _vst[_last]->VConfig.Data[2]       = (BYTE)0x00;
    _vst[_last]->VConfig.Data[3]       = (BYTE)0x00;
    _vst[_last]->VConfig.Data[4]       = (BYTE)0x00;

    /* OK this is all set-up for the builder to manhandle now! */
    return updateVersacomMessage();
}

INT CtiProtocolVersacom::VersacomCountResetCommand(UINT resetmask)
{
    int i;

    /*
     * VC_RESETR4COUNT
     * VC_RESETRTCCOUNT
     * VC_RESETLUFCOUNT
     * VC_RESETRELAY1COUNT
     *
     * VC_RESETRELAY2COUNT
     * VC_RESETRELAY3COUNT
     * VC_RESETCOMMLOSSCOUNT
     * VC_RESETPROPAGATIONCOUNT
     */

    _vst[_last]->CommandType           = VCOUNTRESET;
    _vst[_last]->CountReset            = (USHORT)(resetmask);

    return updateVersacomMessage();
}

INT CtiProtocolVersacom::primeAndAppend(const VSTRUCT &vTemp)
{
    INT status = NORMAL;
    VSTRUCT *newvst = CTIDBG_new VSTRUCT;

    if(newvst)
    {
        *newvst = vTemp;
        _vst.insert(newvst);
        _last = _vst.entries() - 1;      // This is the one we are working on?
    }
    else
        status = MEMORY;

    return status;
}

void CtiProtocolVersacom::removeLastVStruct()
{
    if(_vst.entries())
    {
        VSTRUCT *vst = _vst.removeLast();
        delete vst;
    }

    return;
}

INT CtiProtocolVersacom::parseRequest(CtiCommandParser  &parse, const VSTRUCT &aVst)
{
    INT            status = NORMAL;

    switch(parse.getCommand())
    {
    case ControlRequest:
        {
            assembleControl(parse, aVst);
            break;
        }
    case PutConfigRequest:
        {
            assemblePutConfig(parse, aVst);
            break;
        }
    case PutStatusRequest:
        {
            assemblePutStatus(parse, aVst);
            break;
        }
    default:
        {

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Unsupported command on versacom route Command = " << parse.getCommand() << endl;
            }

            status = CtiInvalidRequest;

            break;
        }
    }

    return status;
}


void CtiProtocolVersacom::dumpMessageBuffer()
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);

    for(int i = 0; i < (_vst[_last]->Nibbles * 2) && i < MAX_VERSACOM_MESSAGE; i++ )
    {
        if(i && !(i % 8))
        {
            dout << endl;
        }
        dout << hex << setw(2) << (INT)(_vst[_last]->Message[i]) << " ";
    }
    dout << dec << endl;
}


INT CtiProtocolVersacom::assemblePutConfig(CtiCommandParser  &parse, const VSTRUCT &aVst)
{
    INT   i, IAddress, iNum;
    INT   status = NORMAL;
    BYTE  config[6];
    bool isGroupConfig = false;

    LONG sn  = parse.getiValue("serial",0);

    // Use these ONLY for the _CONFIGURATION_ of addressing!
    LONG uid = parse.getiValue("utility",0);
    LONG aux = parse.getiValue("aux",0);
    LONG sec = parse.getiValue("section",0);
    LONG cls = parse.getiValue("class",0);
    LONG div = parse.getiValue("division",0);

    // Use these ONLY for group addressing (targeting already configured switches)!
    LONG fuid = parse.getiValue("fromutility",0);
    LONG fsec = parse.getiValue("fromsection",0);
    LONG fcls = parse.getiValue("fromclass",0);
    LONG fdiv = parse.getiValue("fromdivision",0);

    /*
     *  This code decides how we are getting sent.  If the serial number or FROM addresses are specified, we
     *  will assume that any other addressing is to be _set_ in the device!  Otherwise,
     *  we assume that the device is being group configured
     */
    if(sn != 0 || fuid != 0)
    {
        isGroupConfig = true;
    }

    /*
     * This is the original.  IT will have its addressing block stomped on per the specified sn and from addressing.
     */
    VSTRUCT VStTemplate = aVst;
    adjustVersacomAddress(VStTemplate, sn, fuid, fsec, fcls, fdiv);

    if(parse.getiValue("vcfiller", 0) != 0)
    {
        primeAndAppend(VStTemplate);    // Get a new one in the list that looks like the original in terms of addressing

        if(VersacomFillerCommand( uid ))
            removeLastVStruct();
    }

    if((iNum = parse.getiValue("led")) != INT_MIN)
    {
        primeAndAppend(VStTemplate);    // Get a new one in the list that looks like the original in terms of addressing

        if(VersacomConfigLEDCommand( iNum ))
            removeLastVStruct();
    }

    if((iNum = parse.getiValue("coldload_r1")) != INT_MIN)
    {
        primeAndAppend(VStTemplate);    // Get a new one in the list that looks like the original in terms of addressing

        if(VersacomConfigColdLoadCommand( 1, iNum ))
            removeLastVStruct();
    }

    if((iNum = parse.getiValue("coldload_r2")) != INT_MIN)
    {
        primeAndAppend(VStTemplate);    // Get a new one in the list that looks like the original in terms of addressing

        if(VersacomConfigColdLoadCommand( 2, iNum ))
            removeLastVStruct();
    }

    if((iNum = parse.getiValue("coldload_r3")) != INT_MIN)
    {
        primeAndAppend(VStTemplate);    // Get a new one in the list that looks like the original in terms of addressing

        if(VersacomConfigColdLoadCommand( 3, iNum ))
            removeLastVStruct();
    }

    if((iNum = parse.getiValue("scram_r1")) != INT_MIN)
    {
        primeAndAppend(VStTemplate);    // Get a new one in the list that looks like the original in terms of addressing

        if(VersacomConfigScramTimeCommand( 1, iNum ))
            removeLastVStruct();
    }
    if((iNum = parse.getiValue("scram_r2")) != INT_MIN)
    {
        primeAndAppend(VStTemplate);    // Get a new one in the list that looks like the original in terms of addressing

        if(VersacomConfigScramTimeCommand( 2, iNum ))
            removeLastVStruct();
    }
    if((iNum = parse.getiValue("scram_r3")) != INT_MIN)
    {
        primeAndAppend(VStTemplate);    // Get a new one in the list that looks like the original in terms of addressing

        if(VersacomConfigScramTimeCommand( 3, iNum ))
            removeLastVStruct();
    }

    if((iNum = parse.getiValue("cycle_r1")) != INT_MIN)
    {
        primeAndAppend(VStTemplate);    // Get a new one in the list that looks like the original in terms of addressing

        if(VersacomConfigCycleRepeatsCommand( 1, iNum ))
            removeLastVStruct();
    }
    if((iNum = parse.getiValue("cycle_r2")) != INT_MIN)
    {
        primeAndAppend(VStTemplate);    // Get a new one in the list that looks like the original in terms of addressing

        if(VersacomConfigCycleRepeatsCommand( 2, iNum ))
            removeLastVStruct();
    }
    if((iNum = parse.getiValue("cycle_r3")) != INT_MIN)
    {
        primeAndAppend(VStTemplate);    // Get a new one in the list that looks like the original in terms of addressing

        if(VersacomConfigCycleRepeatsCommand( 3, iNum ))
            removeLastVStruct();
    }


    if((iNum = parse.getiValue("proptime")) != INT_MIN)
    {
        primeAndAppend(VStTemplate);    // Get a new one in the list that looks like the original in terms of addressing

        if(VersacomConfigPropagationTimeCommand( iNum ))
            removeLastVStruct();
    }

    if((iNum = parse.getiValue("reset")) != INT_MIN)
    {
        primeAndAppend(VStTemplate);    // Get a new one in the list that looks like the original in terms of addressing

        if(VersacomCountResetCommand( iNum ))
            removeLastVStruct();
    }

    if( parse.isKeyValid("vdata") )
    {
        primeAndAppend(VStTemplate);    // Get a new one in the list that looks like the original in terms of addressing

        if(VersacomDataCommand( (BYTE *)parse.getsValue("raw").data() ), parse.getsValue("raw").length())
            removeLastVStruct();
    }

    if( parse.isKeyValid("raw") )
    {
        primeAndAppend(VStTemplate);    // Get a new one in the list that looks like the original in terms of addressing

        if(VersacomRawConfigCommand( (const BYTE*)parse.getsValue("raw").data() ))
            removeLastVStruct();
    }

    if( parse.isKeyValid("lcrmode") )
    {
        primeAndAppend(VStTemplate);    // Get a new one in the list that looks like the original in terms of addressing

        if( parse.getsValue("lcrmode").compareTo("v") == 0 )
        {
           config[0] = 0x01;

           if(VersacomConfigCommand(VCT_LCRMode, config))
              removeLastVStruct();
        }
        else if( parse.getsValue("lcrmode").compareTo("e") == 0 )
        {
           config[0] = 0x00;

           if(VersacomConfigCommand(VCT_LCRMode, config))
              removeLastVStruct();
        }
        else
        {
           {
              CtiLockGuard<CtiLogger> doubt_guard(dout);
              dout << RWTime() << " **** Checkpoint - invalid value \"" << parse.getsValue("lcrmode") << "\" for \"putconfig lcrmode\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
           }
        }
    }

    if( parse.isKeyValid("eclp") )
    {
        primeAndAppend(VStTemplate);    // Get a new one in the list that looks like the original in terms of addressing

        if( parse.getsValue("eclp").contains("en") )
        {
           config[0] = 0x01;

           if(VersacomConfigCommand(VCT_EmetconColdLoad, config))
              removeLastVStruct();
        }
        else if( parse.getsValue("eclp").contains("dis") )
        {
           config[0] = 0x00;

           if(VersacomConfigCommand(VCT_EmetconColdLoad, config))
              removeLastVStruct();
        }
        else
        {
           {
              CtiLockGuard<CtiLogger> doubt_guard(dout);
              dout << RWTime() << " **** Checkpoint - invalid value \"" << parse.getsValue("eclp") << "\" for \"putconfig eclp\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
           }
        }
    }

    if( parse.isKeyValid("gold") )
    {
        primeAndAppend(VStTemplate);    // Get a new one in the list that looks like the original in terms of addressing

        iNum = parse.getiValue("gold");

        if( iNum > 0 && iNum <= 4 )
        {
           config[0] = iNum - 1;

           if(VersacomConfigCommand(VCT_EmetconGoldAddress, config))
              removeLastVStruct();
        }
        else
        {
           {
              CtiLockGuard<CtiLogger> doubt_guard(dout);
              dout << RWTime() << " **** Checkpoint - invalid value \"" << parse.getsValue("gold") << "\" for \"putconfig gold\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
           }
        }
    }

    if( parse.isKeyValid("silver") )
    {
        primeAndAppend(VStTemplate);    // Get a new one in the list that looks like the original in terms of addressing

        iNum = parse.getiValue("silver");

        if( iNum > 0 && iNum <= 60 )
        {
           config[0] = iNum - 1;

           if(VersacomConfigCommand(VCT_EmetconSilverAddress, config))
              removeLastVStruct();
        }
        else
        {
           {
              CtiLockGuard<CtiLogger> doubt_guard(dout);
              dout << RWTime() << " **** Checkpoint - invalid value \"" << parse.getsValue("silver") << "\" for \"putconfig silver\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
           }
        }
    }

    if( parse.isKeyValid("vcassign") && isConfigFullAddressValid(sn) && (uid || aux || sec || cls || div) )
    {
        primeAndAppend(VStTemplate);    // Get a new one in the list that looks like the original in terms of addressing

        if(VersacomFullAddressCommand(uid, aux, sec, cls, div, parse.getiValue("assignedservice", 0x00)))
            removeLastVStruct();
    }
    else
    {
        if((iNum = parse.getiValue("vctexservice")) != INT_MIN)
        {
            primeAndAppend(VStTemplate);    // Get a new one in the list that looks like the original in terms of addressing

            bool cancel = (parse.getiValue("vctservicetime", 0) == 0 ? true : false );
            bool led_off = (parse.getiValue("vctservicedisableled", 0) != 0 ? true : false );   // Currently not parsable!
            int halfseconds = parse.getiValue("vctservicetime", 0);

            if(VersacomExtendedServiceCommand(cancel, led_off, halfseconds))
                removeLastVStruct();
        }
        else if((iNum = parse.getiValue("service")) != INT_MIN)
        {
            primeAndAppend(VStTemplate);    // Get a new one in the list that looks like the original in terms of addressing

            if(VersacomServiceCommand(iNum & VC_SERVICE_MASK))
                removeLastVStruct();
        }

        if( isGroupConfig && uid )
        {  // We have a utility id to configure
            primeAndAppend(VStTemplate);    // Get a new one in the list that looks like the original in terms of addressing

            memset(config, 0, 6);      // Blank the bytes
            config[0] = (BYTE)uid;
            if(VersacomConfigCommand( VCONFIG_UTILID, config ))
                removeLastVStruct();
        }

        if( isGroupConfig && aux )
        {  // We have a utility id to configure
            primeAndAppend(VStTemplate);    // Get a new one in the list that looks like the original in terms of addressing

            memset(config, 0, 6);      // Blank the bytes
            config[0] = (BYTE)aux;
            if(VersacomConfigCommand( VCONFIG_AUXID, config ))
                removeLastVStruct();
        }

        if( isConfig63Valid(sn) && (sec || cls || div) )
        {
            primeAndAppend(VStTemplate);    // Get a new one in the list that looks like the original in terms of addressing

            memset(config, 0, 6);      // Blank the bytes

            config[0] = (BYTE)sec;

            IAddress = convertHumanFormAddressToVersacom(cls);

            config[1] = (BYTE)( (IAddress >> 8) & 0x00FF );
            config[2] = (BYTE)( (IAddress) & 0x00FF );

            IAddress = convertHumanFormAddressToVersacom(div);
            config[3] = (BYTE)( (IAddress >> 8) & 0x00FF );
            config[4] = (BYTE)( (IAddress) & 0x00FF );

            if(VersacomConfigCommand( VCONFIG_SCD, config ))
                removeLastVStruct();
        }
        else
        {
            if( isGroupConfig && ((iNum = parse.getiValue("section")) != INT_MIN) )
            {  // We have a utility id to configure
                primeAndAppend(VStTemplate);    // Get a new one in the list that looks like the original in terms of addressing

                memset(config, 0, 6);      // Blank the bytes
                config[0] = (BYTE)iNum;
                if(VersacomConfigCommand( VCONFIG_SECTION, config ))
                    removeLastVStruct();
            }

            if( isGroupConfig && ((iNum = parse.getiValue("class")) != INT_MIN) )
            {  // We have a utility id to configure
                primeAndAppend(VStTemplate);    // Get a new one in the list that looks like the original in terms of addressing

                memset(config, 0, 6);      // Blank the bytes

                IAddress = convertHumanFormAddressToVersacom(iNum);              /* Invert The Address */

                config[0] = HIBYTE (IAddress);
                config[1] = LOBYTE (IAddress);

                if(VersacomConfigCommand( VCONFIG_CLASS, config ))
                    removeLastVStruct();
            }

            if( isGroupConfig && ((iNum = parse.getiValue("division")) != INT_MIN) )
            {  // We have a utility id to configure
                primeAndAppend(VStTemplate);    // Get a new one in the list that looks like the original in terms of addressing

                memset(config, 0, 6);      // Blank the bytes
                IAddress = convertHumanFormAddressToVersacom(iNum);              /* Invert The Address */

                config[0] = HIBYTE (IAddress);
                config[1] = LOBYTE (IAddress);

                if(VersacomConfigCommand( VCONFIG_DIVISION, config ))
                    removeLastVStruct();
            }
        }
    }

    return status;
}

INT CtiProtocolVersacom::assembleControl(CtiCommandParser  &parse, const VSTRUCT &aVst)
{
    INT  i;
    INT  status = NORMAL;
    UINT CtlReq = CMD_FLAG_CTL_ALIASMASK & parse.getFlags();
    INT  relay  = parse.getiValue("relaymask", 0);

    if(CtlReq == CMD_FLAG_CTL_SHED)
    {
        UINT hasrand  = parse.isKeyValid("shed_rand");
        UINT hasdelay = parse.isKeyValid("delaytime_sec");

        // Add these two items to the list for control accounting!
        parse.Map()["control_interval"]  = CtiParseValue( parse.getiValue("shed") );
        parse.Map()["control_reduction"] = CtiParseValue( 100 );

        if( useVersacomTypeFourControl  || getTransmitterType() == TYPE_TCU5000 || parse.getiValue("shed") == 1 )     // Positional relays only one thru three can go out type four (in one message).
        {
            // Assume the VSTRUCT RelayMask is set, otherwise use default relay 0
            primeAndAppend(aVst);  // Get a new one in the system
            VersacomShedCommand(parse.getiValue("shed"));
        }
        else
        {
            // If not specified, uses the last sent data.  Acts as a modification.
            INT rand  = parse.getiValue("shed_rand", 120);      // If not specified, it will continue the command in progress, modifying the other parameters
            INT delay = parse.getiValue("delaytime_sec", 0);    // If not specified, it will continue the command in progress, modifying the other parameters

            if(relay != 0)
            {
                for( i = 0; i < 7; i++ )
                {
                    if( relay & (0x01 << i) )
                    {
                        primeAndAppend(aVst);  // Get a new one in the system
                        VersacomShedCommandEx(parse.getiValue("shed"), (i+1), rand, delay);
                    }
                }
            }
            else
            {
                relay = aVst.RelayMask;         // Someone set this up at a higher level of context.

                for( i = 0; i < 7; i++ )
                {
                    if( relay & (0x01 << i) )
                    {
                        primeAndAppend(aVst);  // Get a new one in the system
                        VersacomShedCommandEx(parse.getiValue("shed"), (i+1), rand, delay);
                    }
                }
            }
        }
    }
    else if(CtlReq == CMD_FLAG_CTL_CYCLE)
    {
        // Add these two items to the list for control accounting!
        parse.Map()["control_reduction"] = CtiParseValue( parse.getiValue("cycle") );

        // Control percentage is in the parsers iValue!
        // Assume the VSTRUCT RelayMask is set, otherwise use default relay 0
        if(useVersacomTypeFourControl || getTransmitterType() == TYPE_TCU5000)
        {
            parse.Map()["control_interval"]  = CtiParseValue( 60 * 30 * 8 );    // Assume a bit here!

            primeAndAppend(aVst);  // Get a new one in the system
            VersacomCycleCommand(parse.getiValue("cycle"));
        }
        else
        {
            INT period     = parse.getiValue("cycle_period", 30);
            INT repeat     = parse.getiValue("cycle_count", 8);
            INT delay      = parse.getiValue("cycle_delay", 0);

            parse.Map()["control_interval"]  = CtiParseValue( 60 * period * repeat );

            if(relay != 0)
            {
                for( i = 0; i < 7; i++ )
                {
                    if( relay & (0x01 << i) )
                    {
                        primeAndAppend(aVst);  // Get a new one in the system
                        VersacomCycleCommandEx(parse.getiValue("cycle"), (i+1), period, repeat, delay);
                    }
                }
            }
            else
            {
                relay = aVst.RelayMask;         // Someone set this up at a higher level of context.

                for( i = 0; i < 7; i++ )
                {
                    if( relay & (0x01 << i) )
                    {
                        primeAndAppend(aVst);  // Get a new one in the system
                        VersacomCycleCommandEx(parse.getiValue("cycle"), (i+1), period, repeat, delay);
                    }
                }
            }
        }
    }
    else if(CtlReq == CMD_FLAG_CTL_RESTORE)
    {
        primeAndAppend(aVst);  // Get a new one in the system
        VersacomRestoreCommand();
    }
    else if(CtlReq == CMD_FLAG_CTL_TERMINATE)
    {
        primeAndAppend(aVst);  // Get a new one in the system
        VersacomTerminateCommand();
    }
    else if(CtlReq == CMD_FLAG_CTL_OPEN)
    {
        parse.Map()["control_reduction"] = CtiParseValue( 100 );

        primeAndAppend(aVst);  // Get a new one in the system
        VersacomCapacitorControlCommand(TRUE);
    }
    else if(CtlReq == CMD_FLAG_CTL_CLOSE)
    {
        primeAndAppend(aVst);  // Get a new one in the system
        VersacomCapacitorControlCommand(FALSE);
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Unsupported command on versacom route Command = " << parse.getCommand() << endl;
    }

    return status;
}

INT CtiProtocolVersacom::assemblePutStatus(CtiCommandParser  &parse, const VSTRUCT &aVst)
{
    INT   i, iNum;
    INT   status = NORMAL;
    BYTE  config[6];


    LONG sn  = parse.getiValue("serial", 0);

    // Use these ONLY for the addressing!
    LONG uid = parse.getiValue("utility", 0);
    LONG aux = parse.getiValue("aux", 0);
    LONG sec = parse.getiValue("section", 0);
    LONG cls = parse.getiValue("class", 0);
    LONG div = parse.getiValue("division", 0);

    /*
     * This is the original.  IT will have its addressing block stomped on per the specified sn and from addressing.
     */
    VSTRUCT VStTemplate = aVst;
    adjustVersacomAddress(VStTemplate, sn, uid, sec, cls, div);

    if((iNum = parse.getiValue("proptest")) != INT_MIN)
    {
        primeAndAppend(VStTemplate);    // Get a new one in the list that looks like the original in terms of addressing
        if(VersacomPropagationCommand(iNum & 0x07))
            removeLastVStruct();
    }

    if((iNum = parse.getiValue("ovuv")) != INT_MIN)
    {
        primeAndAppend(VStTemplate);    // Get a new one in the list that looks like the original in terms of addressing
        if(VersacomVoltageControlCommand((BOOL)iNum))
            removeLastVStruct();
    }

    return status;
}

INT    CtiProtocolVersacom::VersacomConfigColdLoadCommand(INT relay, INT seconds)
{
    USHORT   halfs = seconds * 2;

    if(halfs > 0xFE00)
    {
        halfs = 0xFE00;
    }

    _vst[_last]->CommandType           = VCONFIG;
    _vst[_last]->VConfig.ConfigType    = (USHORT)(VCT_ColdLoadPickupR1 + relay - 1);
    _vst[_last]->VConfig.Data[0]       = HIBYTE(halfs);
    _vst[_last]->VConfig.Data[1]       = LOBYTE(halfs);
    _vst[_last]->VConfig.Data[2]       = (BYTE)0x00;
    _vst[_last]->VConfig.Data[3]       = (BYTE)0x00;
    _vst[_last]->VConfig.Data[4]       = (BYTE)0x00;

    /* OK this is all set-up for the builder to manhandle now! */
    return updateVersacomMessage();
}

INT    CtiProtocolVersacom::VersacomConfigScramTimeCommand(INT relay, INT seconds)
{
    USHORT   halfs = seconds * 2;

    if(halfs > 0xFE00)
    {
        halfs = 0xFE00;
    }

    _vst[_last]->CommandType           = VCONFIG;
    _vst[_last]->VConfig.ConfigType    = (USHORT)(VCT_ScramTimeR1 + relay - 1);
    _vst[_last]->VConfig.Data[0]       = HIBYTE(halfs);
    _vst[_last]->VConfig.Data[1]       = LOBYTE(halfs);
    _vst[_last]->VConfig.Data[2]       = (BYTE)0x00;
    _vst[_last]->VConfig.Data[3]       = (BYTE)0x00;
    _vst[_last]->VConfig.Data[4]       = (BYTE)0x00;

    /* OK this is all set-up for the builder to manhandle now! */
    return updateVersacomMessage();
}


INT    CtiProtocolVersacom::VersacomConfigCycleRepeatsCommand(INT relay, USHORT repeats)
{
    if(repeats > 255)
    {
        repeats = 255;
    }
    _vst[_last]->CommandType           = VCONFIG;
    _vst[_last]->VConfig.ConfigType    = (USHORT)(VCT_CycleRepeatsR1 + relay - 1);
    _vst[_last]->VConfig.Data[0]       = (BYTE)repeats;
    _vst[_last]->VConfig.Data[1]       = (BYTE)0x00;
    _vst[_last]->VConfig.Data[2]       = (BYTE)0x00;
    _vst[_last]->VConfig.Data[3]       = (BYTE)0x00;
    _vst[_last]->VConfig.Data[4]       = (BYTE)0x00;

    /* OK this is all set-up for the builder to manhandle now! */
    return updateVersacomMessage();
}

bool CtiProtocolVersacom::isConfig63Valid(LONG sn) const
{
    bool bstatus = false;

    if(getTransmitterType() != TYPE_TCU5000 && sn > gConfigParms.getValueAsULong("VERSACOM_CONFIG63_BASE", 400000000L ))
    {
        bstatus = true;
    }

    return bstatus;
}

bool CtiProtocolVersacom::isConfigFullAddressValid(LONG sn) const
{
    bool bstatus = false;

    if( getTransmitterType() != TYPE_TCU5000 && sn != 0 )
    {
        RWCString vcrangestr = gConfigParms.getValueAsString("VERSACOM_FULL_ADDRESS_SERIAL_RANGES");

        if(!vcrangestr.isNull())
        {
            while(!vcrangestr.isNull())
            {
                RWCString rstr = vcrangestr.match("[0-9]*-[0-9]*,?");

                if(!rstr.isNull())
                {
                    char *chptr;
                    RWCString startstr = rstr.match("[0-9]*");
                    RWCString stopstr = rstr.match(" *- *[0-9]* *,? *");
                    stopstr = stopstr.strip(RWCString::both, ' ');
                    stopstr = stopstr.strip(RWCString::leading, '-');
                    stopstr = stopstr.strip(RWCString::trailing, ',');
                    stopstr = stopstr.strip(RWCString::both, ' ');

                    UINT startaddr = strtoul( startstr.data(), &chptr, 10 );
                    UINT stopaddr = strtoul( stopstr.data(), &chptr, 10 );

                    if(startaddr <= sn && sn <= stopaddr)
                    {
                        // This is a supported versacom switch and we can continue!
                        bstatus = true;
                        break;
                    }
                }

                vcrangestr.replace("[0-9]*-[0-9]*,?", "");
            }
        }
    }

    if(bstatus != true && sn > gConfigParms.getValueAsULong("VERSACOM_FULL_ADDRESS_SERIAL_BASE", 600000000))
    {
        bstatus = true;
    }


    return bstatus;
}

INT CtiProtocolVersacom::VersacomFullAddressCommand(BYTE uid, BYTE aux, BYTE sec, USHORT clsmask, USHORT divmask, BYTE svc)
{
    int i = 0;
    int pos = 0;
    USHORT mask = 0;

    _vst[_last]->CommandType  = VFULLADDRESS;

    _vst[_last]->VData.Data[i++] = uid;
    _vst[_last]->VData.Data[i++] = aux;
    _vst[_last]->VData.Data[i++] = sec;

    /*
     *  Nice.  The bits must be end-for-ended to match the parsed class and division!.
     *  Parser MSBit = Class 16, LSBit = Class 1.  FullAddressCommand Message MSBit = Class 1, LSBit = Class 16.
     */
    for(pos = 0; pos < 16; pos++)
    {
        if(clsmask & (0x0001 << pos))
        {
            mask |= (0x8000 >> pos);
        }
    }

    _vst[_last]->VData.Data[i++] = HIBYTE( mask );
    _vst[_last]->VData.Data[i++] = LOBYTE( mask );


    /*
     *  Nice.  The bits must be end-for-ended to match the parsed class and division!.
     *  Parser MSBit = Division 16, LSBit = Division 1.  FullAddressCommand Message MSBit = Division 1, LSBit = Division 16.
     */

    mask = 0;
    for(pos = 0; pos < 16; pos++)
    {
        if(divmask & (0x0001 << pos))
        {
            mask |= (0x8000 >> pos);
        }
    }

    _vst[_last]->VData.Data[i++] = HIBYTE( mask );
    _vst[_last]->VData.Data[i++] = LOBYTE( mask );

    _vst[_last]->VData.Data[i++] = (svc << 4);   // Put it in the hinibble so the unwind can be general...  This is only a bit awkward.

    return updateVersacomMessage();
}


