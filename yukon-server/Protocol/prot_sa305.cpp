/*-----------------------------------------------------------------------------*
*
* File:   prot_sa305
*
* Date:   3/8/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2005/02/17 23:35:45 $
*
* HISTORY      :
* $Log: prot_sa305.cpp,v $
* Revision 1.11  2005/02/17 23:35:45  cplender
* Modified the cycling selection to aim for control percentage, not period.
*
* Revision 1.10  2005/02/17 19:02:58  mfisher
* Removed space before CVS comment header, moved #include "yukon.h" after CVS header
*
* Revision 1.9  2005/02/10 23:23:57  alauinger
* Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build
*
* Revision 1.8  2005/01/04 22:16:03  cplender
* Completed the asString() method.
*
* Revision 1.7  2004/12/14 22:25:15  cplender
* Various to wring out config commands.  Should be pretty good.
*
* Revision 1.6  2004/11/24 17:11:16  cplender
* Working on the configuration of SA receivers.
*
* Revision 1.5  2004/11/17 23:42:38  cplender
* Complete 305 for RTC transmitter
*
* Revision 1.4  2004/11/08 14:40:39  cplender
* 305 Protocol should send controls on RTCs now.
*
* Revision 1.3  2004/11/05 17:25:58  cplender
*
* Getting 305s to work
*
* Revision 1.2  2004/04/29 19:58:49  cplender
* Initial sa protocol/load group support
*
* Revision 1.1  2004/03/18 19:46:43  cplender
* Added code to support the SA305 protocol and load group
*
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "cparms.h"
#include "devicetypes.h"
#include "logger.h"
#include "numstr.h"
#include "prot_sa305.h"


RWCString CtiProtocolSA305::_strategyStr[64] =
{
    RWCString("Strategy 00: n/a"),
    RWCString("Strategy 01: 7.5 / 7.5"),
    RWCString("Strategy 02: 7.5 / 15"),
    RWCString("Strategy 03: n/a"),
    RWCString("Strategy 04: 5 / 30"),
    RWCString("Strategy 05: 7.5 / 30"),
    RWCString("Strategy 06: 10 / 30"),
    RWCString("Strategy 07: 12 / 30"),
    RWCString("Strategy 08: 13.5 / 30"),
    RWCString("Strategy 09: 15 / 30"),
    RWCString("Strategy 10: 16.5 / 30"),
    RWCString("Strategy 11: 18 / 30"),
    RWCString("Strategy 12: 19.5 / 30"),
    RWCString("Strategy 13: 21 / 30"),
    RWCString("Strategy 14: 22.5 / 30"),
    RWCString("Strategy 15: 24 / 30"),
    RWCString("Strategy 16: 25.5 / 30"),
    RWCString("Strategy 17: 27 / 30"),
    RWCString("Strategy 18: 28.5 / 30"),
    RWCString("Strategy 19: 30 / 30"),
    RWCString("Strategy 20: n/a"),
    RWCString("Strategy 21: 5 / 20"),
    RWCString("Strategy 22: 6.6 / 20"),
    RWCString("Strategy 23: 8 / 20"),
    RWCString("Strategy 24: 9 / 20"),
    RWCString("Strategy 25: 10 / 20"),
    RWCString("Strategy 26: 11 / 20"),
    RWCString("Strategy 27: 12 / 20"),
    RWCString("Strategy 28: 13 / 20"),
    RWCString("Strategy 29: 14 / 20"),
    RWCString("Strategy 30: 15 / 20"),
    RWCString("Strategy 31: 16 / 20"),
    RWCString("Strategy 32: 17 / 20"),
    RWCString("Strategy 33: 18 / 20"),
    RWCString("Strategy 34: 19 / 20"),
    RWCString("Strategy 35: 20 / 20"),
    RWCString("Strategy 36: n/a"),
    RWCString("Strategy 37: 5 / 60"),
    RWCString("Strategy 38: 10 / 60"),
    RWCString("Strategy 39: 15 / 60"),
    RWCString("Strategy 40: 20 / 60"),
    RWCString("Strategy 41: 24 / 60"),
    RWCString("Strategy 42: 27 / 60"),
    RWCString("Strategy 43: 30 / 60"),
    RWCString("Strategy 44: 33 / 60"),
    RWCString("Strategy 45: 36 / 60"),
    RWCString("Strategy 46: 39 / 60"),
    RWCString("Strategy 47: 42 / 60"),
    RWCString("Strategy 48: 45 / 60"),
    RWCString("Strategy 49: 48 / 60"),
    RWCString("Strategy 50: 51 / 60"),
    RWCString("Strategy 51: 54 / 60"),
    RWCString("Strategy 52: 57 / 60"),
    RWCString("Strategy 53: 60 / 60"),
    RWCString("Strategy 54: Undefined"),
    RWCString("Strategy 55: Undefined"),
    RWCString("Strategy 56: Undefined"),
    RWCString("Strategy 57: Undefined"),
    RWCString("Strategy 58: Undefined"),
    RWCString("Strategy 59: Undefined"),
    RWCString("Strategy 60: Undefined"),
    RWCString("Strategy 61: Undefined"),
    RWCString("Strategy 62: Undefined"),
    RWCString("Strategy 63: Undefined")
};

CtiProtocolSA305::CtiProtocolSA305() :
_padBits(0),
_startBits(4),
_transmitterType(0),
_transmitterAddress(0),
_messageReady(false),
_addressUsage(0),
_serial(-1),
_group(-1),
_division(-1),
_substation(-1),
_utility(-1),
_rateFamily(0),
_rateMember(0),
_rateHierarchy(0),
_priority(0),
_strategy(0),
_functions(0),
_rtcResponse(0x00),
_repetitions(0)
{
}

CtiProtocolSA305::CtiProtocolSA305(const CtiProtocolSA305& aRef) :
_padBits(0),
_startBits(4),
_transmitterType(0),
_transmitterAddress(0),
_messageReady(false),
_addressUsage(0),
_serial(-1),
_group(-1),
_division(-1),
_substation(-1),
_utility(-1),
_rateFamily(0),
_rateMember(0),
_rateHierarchy(0),
_priority(0),
_strategy(0),
_functions(0),
_rtcResponse(0x00),
_repetitions(0)
{
    *this = aRef;
}

CtiProtocolSA305::CtiProtocolSA305(BYTE *bytestr, UINT bytelen) :
_padBits(0),
_startBits(4),
_transmitterType(0),
_transmitterAddress(0),
_messageReady(false),
_addressUsage(0),
_serial(-1),
_group(-1),
_division(-1),
_substation(-1),
_utility(-1),
_rateFamily(0),
_rateMember(0),
_rateHierarchy(0),
_priority(0),
_strategy(0),
_functions(0),
_rtcResponse(0x00),
_repetitions(0)
{
    for(int i = 0; i < bytelen; i++)
    {
        addBits(bytestr[i], 8);
    }
}

CtiProtocolSA305::~CtiProtocolSA305()
{
}

CtiProtocolSA305& CtiProtocolSA305::operator=(const CtiProtocolSA305& aRef)
{
    if(this != &aRef)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    return *this;
}

/*
 * This method used the input strategy or the period and percentage to produce a strategy value.
 * The produced value will be equal or larger than the requested parse.  If the parse is unsuccessful, the result will be -1.
 */
int CtiProtocolSA305::solveStrategy(CtiCommandParser &parse)
{
    int strategy = -1;

    // We only try to predict it if it has not already been fully identified for us.
    if(_strategy == 0)
    {
        if(parse.isKeyValid("sa_restore"))
        {
            strategy = 61;
            _repetitions = parse.getiValue("sa_reps", 0);
        }
        else
        {
            int shed_seconds = parse.getiValue("shed", 0);
            int cycle_percent = parse.getiValue("cycle",0);
            int cycle_period = parse.getiValue("cycle_period");
            int cycle_count = parse.getiValue("cycle_count", 0);

            if(shed_seconds)
            {
                int period_sec;
                _percentageOff = 100.0;
                if(shed_seconds < 6300)            // This is 450 seconds * 14 repeats!
                {
                    _period = 7.5;
                    period_sec = 450;
                }
                else if(shed_seconds < 16800)       // This is 900 seconds * 14 repeats
                {
                    _period = 20.0;
                    period_sec = 1200;
                }
                else if(shed_seconds < 25200)
                {
                    _period = 30.0;
                    period_sec = 1800;
                }
                else if(shed_seconds < 50400)
                {
                    _period = 60.0;
                    period_sec = 3600;
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " ALERT: That's a BIG Control!" << endl;
                    }
                    _period = 60.0;
                    period_sec = 3600;
                }

                _repetitions = shed_seconds / period_sec - ( shed_seconds % period_sec ? 1 : 0);
                if(_repetitions < 0) _repetitions = 0;
            }
            else if(cycle_percent > 0)
            {
                // It is a cycle command!

                if(cycle_period <= 8 && _percentageOff == 100.0)            // We can do 7.5 / 7.5
                {
                    _period = 7.5;
                }
                else if(cycle_period <= 15 && _percentageOff == 50.0)        // We can do 7.5 / 15.0
                {
                    _period = 15.0;
                }
                else if(cycle_period < 30)
                {
                    _period = 20.0;
                }
                else if(cycle_period < 60)
                {
                    _period = 30.0;
                }
                else
                {
                    _period = 60.0;
                }

                _percentageOff = (float)cycle_percent;
                _repetitions = cycle_count - 1;
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }

        if(_repetitions > 13) _repetitions = 13;

        if(_period <= 7.5)
        {
            // There is only one option here!
            strategy = 1;  // 000001b
        }
        else if(_period <= 15.0)
        {
            strategy = 2;  // 000010b   7.5/15
        }
        else if(_period <= 20.0)
        {
            if(_percentageOff <= 25.0)
            {
                strategy = 21;  // 05/20
            }
            else if(_percentageOff <= 33.3)
            {
                strategy = 22;  // 6.6/20
            }
            else if(_percentageOff <= 40.0)
            {
                strategy = 23;  // 8/20
            }
            else if(_percentageOff <= 45.0)
            {
                strategy = 24;  // 9/20
            }
            else if(_percentageOff <= 50.0)
            {
                strategy = 25;  // 10/20
            }
            else if(_percentageOff <= 55.0)
            {
                strategy = 26;  // 11/20
            }
            else if(_percentageOff <= 60.0)
            {
                strategy = 27;  // 12/20
            }
            else if(_percentageOff <= 65.0)
            {
                strategy = 28;  // 13/20
            }
            else if(_percentageOff <= 70.0)
            {
                strategy = 29;  // 14/20
            }
            else if(_percentageOff <= 75.0)
            {
                strategy = 30;  // 15/20
            }
            else if(_percentageOff <= 80.0)
            {
                strategy = 31;  // 16/20
            }
            else if(_percentageOff <= 85.0)
            {
                strategy = 32;  // 17/20
            }
            else if(_percentageOff <= 90.0)
            {
                strategy = 33;  // 18/20
            }
            else if(_percentageOff <= 95.0)
            {
                strategy = 34;  // 19/20
            }
            else if(_percentageOff <= 100)
            {
                strategy = 35;  // 20/20
            }
        }
        else if(_period <= 30.0)
        {
            if(_percentageOff <= 16.7)
            {
                strategy = 4; // 000100b 5/30
            }
            else if(_percentageOff <= 25)
            {
                strategy = 5; // 000101b 7.5/30
            }
            else if(_percentageOff <= 33.3)
            {
                strategy = 6; // 000110b 10/30
            }
            else if(_percentageOff <= 40)
            {
                strategy = 7; // 000111b 12/30
            }
            else if(_percentageOff <= 45)
            {
                strategy = 8; // 001000b 13.5/30
            }
            else if(_percentageOff <= 50)
            {
                strategy = 9; // 001001b 15/30
            }
            else if(_percentageOff <= 55)
            {
                strategy = 10; // 001010b 16.5/30
            }
            else if(_percentageOff <= 60)
            {
                strategy = 11; // 001011b 18/30
            }
            else if(_percentageOff <= 65)
            {
                strategy = 12; // 001100b 19.5/30
            }
            else if(_percentageOff <= 70)
            {
                strategy = 13; // 001101b 21/30
            }
            else if(_percentageOff <= 75)
            {
                strategy = 14; // 001110b 22.5/30
            }
            else if(_percentageOff <= 80)
            {
                strategy = 15; // 001111b 24/30
            }
            else if(_percentageOff <= 85)
            {
                strategy = 16; // 010000b 25.5/30
            }
            else if(_percentageOff <= 90)
            {
                strategy = 17; // 010001b 27/30
            }
            else if(_percentageOff <= 95)
            {
                strategy = 18; // 010010b 28.5/30
            }
            else if(_percentageOff <= 100)
            {
                strategy = 19; // 010011b 30/30
            }
        }
        else // ( We will assume it is 60 minutes then!)
        {
            if(_percentageOff <= 8.3)
            {
                strategy = 37;      // 5/60
            }
            else if(_percentageOff <= 16.7)
            {
                strategy = 38;      // 10/60
            }
            else if(_percentageOff <= 25.0)
            {
                strategy = 39;      // 15/60
            }
            else if(_percentageOff <= 33.3)
            {
                strategy = 40;      // 20/60
            }
            else if(_percentageOff <= 40.0)
            {
                strategy = 41;      // 24/60
            }
            else if(_percentageOff <= 45.0)
            {
                strategy = 42;      // 27/60
            }
            else if(_percentageOff <= 50.0)
            {
                strategy = 43;      // 30/60
            }
            else if(_percentageOff <= 55.0)
            {
                strategy = 44;      // 33/60
            }
            else if(_percentageOff <= 60.0)
            {
                strategy = 45;      // 36/60
            }
            else if(_percentageOff <= 65.0)
            {
                strategy = 46;      // 39/60
            }
            else if(_percentageOff <= 70.0)
            {
                strategy = 47;      // 42/60
            }
            else if(_percentageOff <= 75.0)
            {
                strategy = 48;      // 45/60
            }
            else if(_percentageOff <= 80.0)
            {
                strategy = 49;      // 48/60
            }
            else if(_percentageOff <= 85.0)
            {
                strategy = 50;      // 51/60
            }
            else if(_percentageOff <= 90.0)
            {
                strategy = 51;      // 54/60
            }
            else if(_percentageOff <= 95.0)
            {
                strategy = 52;      // 57/60
            }
            else if(_percentageOff <= 100.0)
            {
                strategy = 53;      // 60/60
            }
        }
    }

    if(gConfigParms.getValueAsInt("SA305_DEBUGLEVEL",0) & DEBUGLEVEL_LUDICROUS)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << " period        : " << _period << endl;
        dout << " % off         : " << _percentageOff << endl;
        dout << " repetitions   : " << _repetitions << endl;
        dout << " strategy      : " << strategy << endl;
        dout << " flags         : " << _flags << endl;
    }

    return strategy;
}

// Interesting
INT CtiProtocolSA305::parseCommand(CtiCommandParser &parse, CtiOutMessage &OutMessage)
{
    INT status = NORMAL;

    // These elements of addressing must be defined.
    if(_utility <= 0) _utility = parse.getiValue("sa_utility");
    if(_utility <= 0) _utility = gConfigParms.getValueAsInt("PROTOCOL_305_UTILITY_ADDRESS",0);   // Back up plan.

    if(_rateFamily <= 0) _rateFamily = parse.getiValue("sa_ratefamily", 0);
    if(_rateMember <= 0) _rateMember = parse.getiValue("sa_ratemember", 0);

    if(_serial <= 0) _serial = parse.getiValue("serial",0);
    if(_group <= 0) _group = parse.getiValue("sa_group",0);
    if(_division <= 0) _division = parse.getiValue("sa_division",0);
    if(_substation <= 0) _substation = parse.getiValue("sa_substation",0);
    _rateHierarchy = parse.getiValue("sa_hierarchy",0);

    _addressUsage =  parse.getiValue("sa_addressusage", FALSE); // Assume a serially Addressed message (serial if FALSE)

    // Optional elements
    if(_priority <= 0) _priority = parse.getiValue("sa_priority", 0);
    if(_functions <= 0) _functions = parse.getiValue("sa_function",0);
    if(_dataType <= 0) _dataType = parse.getiValue("sa_datatype", 0);
    if(_data <= 0) _data = parse.getiValue("sa_dataval", 0);

    if(_strategy <= 0) _strategy = parse.getiValue("sa_strategy", 0);

    if( parse.getCommandStr().contains(" adapt", RWCString::ignoreCase) )   // Adaptive algorithm!
    {
        setStartBits(5);
    }
    // Now process the message components.
    resetMessage();
    addressMessage(parse.getiValue("sa_f1bit", CtiProtocolSA305::CommandTypeOperationFlag), parse.getiValue("sa_f0bit", CtiProtocolSA305::CommandDescription_DIMode) );

    // Assist in the asString() call in the future.
    OutMessage.Buffer.SASt._commandType = parse.getCommand();

    switch(parse.getCommand())
    {
    case ControlRequest:
        {
            assembleControl( parse, OutMessage );
            break;
        }
    case PutConfigRequest:
        {
            assemblePutConfig( parse, OutMessage );
            break;
        }
    default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Unsupported command on sa305 route. Command = " << parse.getCommand() << endl;
            }

            status = CtiInvalidRequest;

            break;
        }
    }

    if(_transmitterType != TYPE_RTC) appendCRCToMessage();
    _messageReady = true;
    // dumpBits();

    return status;
}


void CtiProtocolSA305::resetMessage()
{
    _messageReady = false;
    _messageBits.clear();
    _bitStr = RWCString();
    return;
}

void CtiProtocolSA305::addressMessage(int command_type, int command_description)
{
    _flags = (command_type?2:0) | (command_description?1:0);     // These represent Bit F1, and F0 from the flag.

    if(_addressUsage == FALSE) // This is a serially addressed message!
    {
    }
    else
    {
        _flags |= AddressTypeGroupFlag;

        if(_group > 0)
        {
            _flags |= GroupFlag;
        }
        if(_division > 0)
        {
            _flags |= DivisionFlag;
        }
        if(_substation > 0)
        {
            _flags |= SubstationFlag;
        }
    }

    // And now build out the bits...
    if(_transmitterType == TYPE_RTC)
    {
        addBits(_padBits, 2);            // The RTC seems to want this!
    }

    addBits(_startBits, 3);          // This appears to be the start bits.
    addBits(_flags, 6);

    if(_flags & AddressTypeGroupFlag)
    {
        // this is a serially addressed message.
        addBits(_utility, 4);

        if(_flags & GroupFlag) addBits(_group, 6);
        if(_flags & DivisionFlag) addBits(_division, 6);
        if(_flags & SubstationFlag) addBits(_substation, 10);

        addBits(_rateFamily, 3);
        addBits(_rateMember, 4);
        addBits(_rateHierarchy, 1);

    }
    else
    {
        // this is a serially addressed message.
        addBits(_utility, 4);
        addBits(_serial, 22);
    }

    return;
}

void CtiProtocolSA305::terminateMessage()
{
    return;
}

void CtiProtocolSA305::resolveAddressLevel()
{
    return;
}

INT CtiProtocolSA305::assembleControl(CtiCommandParser &parse, CtiOutMessage &OutMessage)
{
    INT  i;
    INT  status = NORMAL;
    UINT CtlReq = CMD_FLAG_CTL_ALIASMASK & parse.getFlags();

    _strategy = solveStrategy(parse);

    if(CtlReq == CMD_FLAG_CTL_SHED)
    {
        int shed_seconds = parse.getiValue("shed");

        if(shed_seconds >= 0)
        {
            // Add these two items to the list for control accounting!
            parse.setValue("control_interval", parse.getiValue("shed"));
            parse.setValue("control_reduction", 100 );

            timedLoadControl();
        }
    }
    else if(CtlReq == CMD_FLAG_CTL_CYCLE)
    {
        INT period     = parse.getiValue("cycle_period", 30);
        INT repeat     = parse.getiValue("cycle_count", 8);

        // Add these two items to the list for control accounting!
        parse.setValue("control_reduction", parse.getiValue("cycle", 0) );
        parse.setValue("control_interval", 60 * period * repeat);

        cycleLoadControl();
    }
    else if(CtlReq == CMD_FLAG_CTL_RESTORE)
    {
        restoreLoadControl();
    }
    else if(CtlReq == CMD_FLAG_CTL_TERMINATE)
    {
        INT delay = parse.getiValue("delaytime_sec", 0) / 60;
        cycleLoadControl();
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Unsupported sa305 command.  Command = " << parse.getCommand() << endl;
    }

    return status;
}

INT CtiProtocolSA305::assemblePutConfig(CtiCommandParser &parse, CtiOutMessage &OutMessage)
{
    INT status = NORMAL;

    if( _strategy != 0 )
    {
        // These are the "operational" putconfigs...
        int val;

        addBits(_strategy, 6);

        if( 0 <= (val = parse.getiValue("sa_override",-1)) )
        {
            addBits(val, 8);
        }
        else if( 0 <= (val = parse.getiValue("sa_setpriority",-1)) )
        {
            addBits((val & 0x00000003), 4);
            addBits(0, 4);
        }
        else if( 0 <= (val = parse.getiValue("sa_setled",-1)) )
        {
            addBits(0, 4);
            addBits(val, 4);
        }
        else if( 0 <= (val = parse.getiValue("sa_flashled",-1)) )
        {
            addBits(1, 4);
            addBits(val, 4);
        }
        else if( 0 <= (val = parse.getiValue("sa_flashrate",-1)) )
        {
            addBits(4, 4);
            addBits(0, 4);
        }

        addBits(_priority, 2);
    }
    else if(parse.getiValue("sa_f0bit", 0))
    {
        // This is a 29bit config!
        addBits(parse.getiValue("sa_group",0), 6);
        addBits(parse.getiValue("sa_division",0), 6);
        addBits(parse.getiValue("sa_substation",0), 10);

        if(0 <= parse.getiValue("sa_package",-1))
        {
            addBits(parse.getiValue("sa_package",0), 7);
        }
        else
        {
            addBits(parse.getiValue("sa_family",0), 3);
            addBits(parse.getiValue("sa_member",0), 4);
        }
    }
    else
    {
        // This is a 13bit config!
        int val;

        if( 0 <= (val = parse.getiValue("sa_clpf1",-1)) )
        {
            val = (int)((float)val / 14.0616);
            if(val >= 255) val = 255;
            addBits(0, 5);
            addBits(val, 8);
        }
        else if( 0 <= (val = parse.getiValue("sa_clpf2",-1)) )
        {
            val = (int)(((float)val + 7.0307) / 14.0616);
            if(val >= 255) val = 255;
            addBits(1, 5);
            addBits(val, 8);
        }
        else if( 0 <= (val = parse.getiValue("sa_clpf3",-1)) )
        {
            val = (int)((float)val / 14.0616);
            if(val >= 255) val = 255;
            addBits(2, 5);
            addBits(val, 8);
        }
        else if( 0 <= (val = parse.getiValue("sa_clpf4",-1)) )
        {
            val = (int)((float)val / 14.0616);
            if(val >= 255) val = 255;
            addBits(3, 5);
            addBits(val, 8);
        }
        else if( 0 <= (val = parse.getiValue("sa_clpall",-1)) )
        {
            val = (int)((float)val / 14.0616);
            if(val >= 255) val = 255;
            addBits(4, 5);
            addBits(val, 8);
        }
        else if( 0 <= (val = parse.getiValue("sa_lorm0",-1)) )
        {
            addBits(8, 5);
            addBits(val, 8);
        }
        else if( 0 <= (val = parse.getiValue("sa_horm0",-1)) )
        {
            addBits(9, 5);
            addBits(val, 8);
        }
        else if( 0 <= (val = parse.getiValue("sa_lorm1",-1)) )
        {
            addBits(10, 5);
            addBits(val, 8);
        }
        else if( 0 <= (val = parse.getiValue("sa_horm1",-1)) )
        {
            addBits(11, 5);
            addBits(val, 8);
        }
        else if( 0 <= (val = parse.getiValue("sa_userelaymap0",-1)) )
        {
            addBits(12, 5);
            addBits(0, 8);
        }
        else if( 0 <= (val = parse.getiValue("sa_userelaymap1",-1)) )
        {
            addBits(12, 5);
            addBits(1, 8);
        }
        else if( 0 <= (val = parse.getiValue("sa_clearlc",-1)) )
        {
            addBits(12, 5);
            addBits(2, 8);
        }
        else if( 0 <= (val = parse.getiValue("sa_clearhc",-1)) )
        {
            addBits(12, 5);
            addBits(3, 8);
        }
        else if( 0 <= (val = parse.getiValue("sa_clearpcd",-1)) )
        {
            addBits(12, 5);
            addBits(6, 8);
        }
        else if( 0 <= (val = parse.getiValue("sa_freezepcd",-1)) )
        {
            addBits(12, 5);
            addBits(5, 8);
        }
        else if( 0 <= (val = parse.getiValue("sa_datatype",-1)) )
        {
            addBits(val, 5);
            addBits(parse.getiValue("sa_dataval",0), 8);
        }
    }
    return status;
}


INT CtiProtocolSA305::timedLoadControl( )
{
    INT status = NoError;

    // Ok, the flags and addressing are all stuffed on the message, we also believe that the stratey, function etc are ready for us to use!

    addBits(_strategy,6);
    addBits(_functions,4);
    addBits(_repetitions,4);
    addBits(_priority,2);

    return status;
}


INT CtiProtocolSA305::restoreLoadControl( )
{
    INT status = NoError;
    return status;
}


INT CtiProtocolSA305::cycleLoadControl()
{
    INT status = NoError;

    // Ok, the flags and addressing are all stuffed on the message, we also believe that the stratey, function etc are ready for us to use!

    addBits(_strategy,6);
    addBits(_functions,4);
    addBits(_repetitions,4);
    addBits(_priority,2);

    return status;
}

void CtiProtocolSA305::addBits(unsigned int src, int num)
{
    BYTE bit;
    int i;

    for(i = 0; i < num; i++)
    {
        bit = (src & (0x80000000 >> (32 - num + i)) ? 1 : 0);
        _messageBits.push_back(bit);
        _bitStr += CtiNumStr(bit);
    }

    _bitStr += " ";
}

void CtiProtocolSA305::setBit(unsigned int offset, BYTE bit)
{
    if(_messageBits.size() >= offset)
    {
        _messageBits[offset] = (bit ? 1 : 0);
    }
}

UINT CtiProtocolSA305::getBits(unsigned int &offset, int len) const
{
    UINT val = 0;
    if(_messageBits.size() >= offset+len)
    {
        for(int i = 0; i < len; i++)
        {
            val <<= 1;
            val |= (_messageBits[offset+i] ? 1 : 0);
        }

        #if 0
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << " Getting " << len << " bits of data from " << offset << " as " << val << endl;
        }
        #endif

        offset += len;
    }

    return val;
}

void CtiProtocolSA305::dumpBits() const
{
    vector< BYTE >::const_iterator itr;

    {
        int cnt = 0;
        CtiLockGuard<CtiLogger> doubt_guard(dout);

        for( itr = _messageBits.begin(); itr != _messageBits.end(); itr++ )
        {
            if(*itr)
                dout << "1";
            else
                dout << "0";

            if(++cnt >= 3)
            {
                dout << " ";
                cnt = 0;
            }
        }

        dout << endl;


        int bc = 0;
        int val;

        cnt = 0;

        for( itr = _messageBits.begin(); itr != _messageBits.end(); itr++ )
        {
            bc <<= 1;
            bc |= (*itr ? 1 : 0);

            if(++cnt >= 3)
            {
                dout << " " << bc << "  ";
                cnt = 0;
                val = 0;
                bc = 0;
            }
        }

        dout << endl;
    }
}


int CtiProtocolSA305::getSerial() const
{
    return _serial;
}

CtiProtocolSA305& CtiProtocolSA305::setSerial(int val)
{
    _serial = val;
    return *this;
}

int CtiProtocolSA305::getGroup() const
{
    return _group;
}

CtiProtocolSA305& CtiProtocolSA305::setGroup(int val)
{
    _group = val;
    return *this;
}

int CtiProtocolSA305::getDivision() const
{
    return _division;
}
CtiProtocolSA305& CtiProtocolSA305::setDivision(int val)
{
    _division = val;
    return *this;
}

int CtiProtocolSA305::getSubstation() const
{
    return _substation;
}
CtiProtocolSA305& CtiProtocolSA305::setSubstation(int val)
{
    _substation = val;
    return *this;
}

int CtiProtocolSA305::getUtility() const
{
    return _utility;
}
CtiProtocolSA305& CtiProtocolSA305::setUtility(int val)
{
    _utility = val;
    return *this;
}

CtiProtocolSA305& CtiProtocolSA305::setRatePackage(int val)
{
    _rateMember = (val & 0x0000000f);
    _rateFamily = (val & 0x00000070) >> 4;

    return *this;
}

int CtiProtocolSA305::getRateFamily() const
{
    return _rateFamily;
}
CtiProtocolSA305& CtiProtocolSA305::setRateFamily(int val)
{
    _rateFamily = val;
    return *this;
}

int CtiProtocolSA305::getRateMember() const
{
    return _rateMember;
}
CtiProtocolSA305& CtiProtocolSA305::setRateMember(int val)
{
    _rateMember = val;
    return *this;
}

int CtiProtocolSA305::getRateHierarchy() const
{
    return _rateHierarchy;
}
CtiProtocolSA305& CtiProtocolSA305::setRateHierarchy(int val)
{
    _rateHierarchy = val;
    return *this;
}

int CtiProtocolSA305::getStrategy() const
{
    return _strategy;
}
CtiProtocolSA305& CtiProtocolSA305::setStrategy(int val)
{
    _strategy = val;
    return *this;
}

int CtiProtocolSA305::getFunctions() const
{
    return _functions;
}
CtiProtocolSA305& CtiProtocolSA305::setFunctions(int val)
{
    _functions = val;
    return *this;
}

int CtiProtocolSA305::getPriority() const
{
    return _priority;
}
CtiProtocolSA305& CtiProtocolSA305::setPriority(int val)
{
    _priority = val;
    return *this;
}

int CtiProtocolSA305::getDataType() const
{
    return _dataType;
}
CtiProtocolSA305& CtiProtocolSA305::setDataType(int val)
{
    _dataType = val;
    return *this;
}

int CtiProtocolSA305::getData() const
{
    return _data;
}
CtiProtocolSA305& CtiProtocolSA305::setData(int val)
{
    _data = val;
    return *this;
}

float CtiProtocolSA305::getPeriod() const
{
    return _period;
}
CtiProtocolSA305& CtiProtocolSA305::setPeriod(float val)
{
    _period = val;
    return *this;
}

float CtiProtocolSA305::getPercentageOff() const
{
    return _percentageOff;
}
CtiProtocolSA305& CtiProtocolSA305::setPercentageOff(float val)
{
    _percentageOff = val;
    return *this;
}


unsigned char CtiProtocolSA305::addBitToCRC(unsigned char crc, unsigned char bit) // bit is 0 or 1
{
    unsigned char msb = ((crc&0x80)?1:0);
    bit = msb ^ bit;
    crc<<=1;
    crc|=bit;
    if (bit)
        crc^=0x48;

    return(crc);
}

unsigned char CtiProtocolSA305::addOctalCharToCRC(unsigned char crc, unsigned char ch) // octal char
{
    int i=0;
    ch-='0';
    for (i=0; i<3; i++)
    {
        crc = addBitToCRC(crc, ch&0x04?1:0);
        ch<<=1;
    }
    return(crc);
}

void CtiProtocolSA305::testCRC(char* testData)
{
    unsigned i;
    unsigned char crc=0;
    for (i=0; i<strlen(testData)-3; i++)
        crc = addOctalCharToCRC(crc,testData[i]);
    // shift in one false 0
    crc = addBitToCRC(crc, 0);
    printf("%o\r\n",crc);

    return;
}

void CtiProtocolSA305::appendCRCToMessage()
{
    vector< BYTE >::const_iterator itr;
    unsigned i;
    unsigned char crc=0;

    for( itr = _messageBits.begin(); itr != _messageBits.end(); itr++ )
    {
        crc = addBitToCRC(crc, ((*itr) ? 1 : 0));
    }

    addBits(crc, 8);
}

bool CtiProtocolSA305::messageReady() const
{
    return _messageReady;
}

int CtiProtocolSA305::getMessageLength(int mode) const      // Returns the length in characters of this message.
{
    int length = 0;

    if(messageReady())
    {
        switch(mode)
        {
        case ModeUnspecified:
            {
                break;
            }
        case ModeNumericPage:
            {
                int cnt = _messageBits.size();
                length = cnt/3 + ((cnt%3)?1:0);
                if(_transmitterType == TYPE_RTC)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** ERROR Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << " RTCs should not execute this code!" << endl;
                    }
                }
                break;
            }
        case ModeHex:
            {
                int cnt = _messageBits.size();
                length = cnt/8 + ((cnt%8)?1:0);
                if(_transmitterType == TYPE_RTC)
                {
                    length += 5;    // RTC overhead
                }
                break;
            }
        case ModeSerial:
        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                break;
            }
        }
    }

    return length;
}

int CtiProtocolSA305::buildMessage(int mode, CHAR *buffer) const      // Returns the length in characters of this message.
{
    int bufpos = 0;

    if(messageReady())
    {
        switch(mode)
        {
        case ModeUnspecified:
            {
                break;
            }
        case ModeNumericPage:
            {
                unsigned pos = 0;
                int ich = 0;
                CHAR ch;

                vector< BYTE >::const_iterator itr;

                if(_transmitterType == TYPE_RTC)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** ERROR ACH Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "  RTCs should never execute this code" << endl;
                    }
                }

                for( itr = _messageBits.begin(); itr != _messageBits.end(); itr++ )
                {
                    ich<<=1;
                    ich|=(*itr?1:0);
                    if(++pos >= 3)  // ch is ready!
                    {
                        pos = 0;
                        buffer[bufpos++] = '0' + ich;
                        ich = 0;
                    }
                }

                // Pad out any remaining bits!
                while(pos != 0)
                {
                    ich<<=1;
                    if(++pos >= 3)  // ch is ready!
                    {
                        pos = 0;
                        buffer[bufpos++] = '0' + ich;
                        ich = 0;
                        break;
                    }
                }

                break;
            }
        case ModeHex:
            {
                int packagedBits = _messageBits.size();
                unsigned rtc_crc = 0;
                unsigned pos = 0;
                int ich = 0;
                CHAR ch;

                if(_transmitterType == TYPE_RTC)
                {
                    int ltf = _messageBits.size() / 8 + (_messageBits.size() % 8 ? 1 : 0);

                    buffer[bufpos++] = (CHAR)(0xa0 | (0x0f & _transmitterAddress));         // byte 1 of the RTC preamble
                    rtc_crc ^= buffer[bufpos-1];                                                // Update the rtc_crc.
                    buffer[bufpos++] = (CHAR)(0x29 | (0x10 & _rtcResponse));                // byte 2 of the RTC preamble
                    rtc_crc ^= buffer[bufpos-1];                                                // Update the rtc_crc.
                    buffer[bufpos++] = (CHAR)(3);                                           // 305 protocol is "3" per rtc spec
                    rtc_crc ^= buffer[bufpos-1];                                                // Update the rtc_crc.
                    buffer[bufpos++] = (CHAR)(ltf);                                         // Length to follow not including the CRC
                    rtc_crc ^= buffer[bufpos-1];                                                // Update the rtc_crc.
                }

                vector< BYTE >::const_iterator itr;

                int i;
                for( i = 0, itr = _messageBits.begin();
                     i < packagedBits && itr != _messageBits.end();
                     itr++, i++ )
                {
                    ich<<=1;
                    ich|=(*itr ? 0x01 : 0x00);
                    if(++pos >= 8)  // ch is ready!
                    {
                        pos = 0;
                        buffer[bufpos++] = ich;
                        rtc_crc ^= buffer[bufpos-1];                                                // Update the rtc_crc.
                        ich = 0;
                    }
                }

                // Pad out any remaining bits!
                while(pos != 0)
                {
                    ich<<=1;
                    if(++pos >= 8)  // ch is ready!
                    {
                        pos = 0;
                        buffer[bufpos++] = ich;
                        rtc_crc ^= buffer[bufpos-1];                                                // Update the rtc_crc.
                        ich = 0;
                        break;
                    }
                }

                if(_transmitterType == TYPE_RTC)
                {
                    buffer[bufpos++] = (CHAR)(rtc_crc);                                         // Record the CRC
                }

                break;
            }
        case ModeSerial:
        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                break;
            }
        }
    }

    return bufpos;
}

CtiProtocolSA305& CtiProtocolSA305::setTransmitterType( int trans )
{
    _transmitterType = trans;
    return *this;
}
CtiProtocolSA305& CtiProtocolSA305::setRTCResponse( bool bv )
{
    if(bv)
        _rtcResponse = 0x10;
    else
        _rtcResponse = 0x00;
    return *this;
}

CtiProtocolSA305& CtiProtocolSA305::setTransmitterAddress( int val )
{
    _transmitterAddress = val;
    return *this;
}

int CtiProtocolSA305::getStartBits() const
{
    return _startBits;
}
CtiProtocolSA305& CtiProtocolSA305::setStartBits(int val)
{
    _startBits = val & 0x00000007;  // Three bits
    return *this;
}

int CtiProtocolSA305::getPadBits() const
{
    return _startBits;
}
CtiProtocolSA305& CtiProtocolSA305::setPadBits(int val)
{
    _startBits = val & 0x00000003;  // Two bits.
    return *this;
}

RWCString CtiProtocolSA305::getBitString() const
{
    return _bitStr;
}

RWCString  CtiProtocolSA305::asString() const
{
    RWCString rstr;
    UINT bit = 0;       // This is just past the Uaddr.

    rstr += "SA 305 - code. ";

    if(_transmitterType == TYPE_RTC)  getBits(bit,37);  // RTC crud.

    UINT flag = getBits(bit,6);   // 6 bits of flag.

    bool addresstype = flag & 0x20;
    bool group = flag & 0x10;
    bool div = flag & 0x08;
    bool sub = flag & 0x04;
    bool f1 = flag & 0x02;
    bool f0 = flag & 0x01;

    UINT uaddr = getBits(bit,4);
    UINT serial = 0;
    UINT gaddr = 0;
    UINT daddr = 0;
    UINT saddr = 0;
    UINT raddr = 0;
    UINT faddr = 0;
    UINT maddr = 0;
    UINT haddr = 0;

    rstr += "Utility " + CtiNumStr(uaddr) + " ";

    if(!addresstype)
    { // Individual Addressed switch
        serial = getBits(bit, 22);
        rstr += ".  Serial Number " + CtiNumStr(serial) + " ";
    }
    else
    {
        if(group)
        {
            gaddr = getBits(bit, 6);
            rstr += ". Group " + CtiNumStr(gaddr) + " ";
        }
        if(div)
        {
            daddr = getBits(bit, 6);
            rstr += ". Division " + CtiNumStr(daddr) + " ";
        }
        if(sub)
        {
            saddr = getBits(bit, 10);
            rstr += ". Substation " + CtiNumStr(saddr) + " ";
        }

        faddr = getBits(bit, 3);
        maddr = getBits(bit, 4);
        haddr = getBits(bit, 1);

        raddr = faddr*16 + maddr;
    }

    if(!f1)
    {
        rstr += ".  Operational Command";
        UINT strategy = getBits(bit, 6);
        UINT func4 = getBits(bit, 1);
        UINT func3 = getBits(bit, 1);
        UINT func2 = getBits(bit, 1);
        UINT func1 = getBits(bit, 1);
        UINT reps = getBits(bit, 4);
        UINT priority = getBits(bit, 2);

        rstr += ". " + _strategyStr[strategy] + " Function";

        if(func1)
        {
            rstr += " F1";
        }
        if(func2)
        {
            rstr += " F2";
        }
        if(func3)
        {
            rstr += " F3";
        }
        if(func4)
        {
            rstr += " F4";
        }

        rstr += ", Repetitions " + CtiNumStr(reps);
        rstr += ", Priority " + CtiNumStr(priority);
    }
    else
    {
        rstr += ".  Assignment Command";

        if(f0)  // Full addressing command (29-bit)
        {
            gaddr = getBits(bit, 6);
            daddr = getBits(bit, 6);
            saddr = getBits(bit, 10);
            faddr = getBits(bit, 3);
            maddr = getBits(bit, 4);
            raddr = faddr*16 + maddr;

            rstr += ". Group " + CtiNumStr(gaddr);
            rstr += ", Division " + CtiNumStr(daddr);
            rstr += ", Substation " + CtiNumStr(saddr);
            rstr += ", Substation " + CtiNumStr(saddr);
            rstr += ", Family " + CtiNumStr(faddr);
            rstr += ", Member " + CtiNumStr(maddr) + " (Rate = " + CtiNumStr(raddr) + ")";
        }
        else    // 13 byte address command.
        {
            UINT datatype = getBits(bit, 5);
            UINT dataval = getBits(bit, 8);

            switch(datatype)
            {
            case 0:
                {
                    rstr += ". Cold Load F1 set to " + CtiNumStr(dataval * 14.0616) + " seconds";
                    break;
                }
            case 1:
                {
                    rstr += ". Cold Load F2 set to " + CtiNumStr(dataval * 14.0616) + " seconds";
                    break;
                }
            case 2:
                {
                    rstr += ". Cold Load F3 set to " + CtiNumStr(dataval * 14.0616) + " seconds";
                    break;
                }
            case 3:
                {
                    rstr += ". Cold Load F4 set to " + CtiNumStr(dataval * 14.0616) + " seconds";
                    break;
                }
            case 4:
                {
                    rstr += ". Cold Load F1-F4 set to " + CtiNumStr(dataval * 14.0616) + " seconds";
                    break;
                }
            case 5:
                {
                    switch(dataval)
                    {
                    case 0:
                        {
                            rstr += ". Use Relay Map 0";
                            break;
                        }
                    case 1:
                        {
                            rstr += ". Use Relay Map 1";
                            break;
                        }
                    case 2:
                        {
                            rstr += ". Clear LC Tamper Regs";
                            break;
                        }
                    case 3:
                        {
                            rstr += ". Clear HC Tamper Regs";
                            break;
                        }
                    case 4:
                        {
                            rstr += ". Transmit Manchester PCD on LED";
                            break;
                        }
                    case 5:
                        {
                            rstr += ". Freeze PCD regs";
                            break;
                        }
                    case 6:
                        {
                            rstr += ". Clear PCD regs";
                            break;
                        }
                    case 7:
                        {
                            rstr += ". Transmit NRZ PCD on LED";
                            break;
                        }
                    case 8:
                        {
                            rstr += ". Immed. HW Reset";
                            break;
                        }
                    case 9:
                        {
                            rstr += ". Save mem. and HW Reset";
                            break;
                        }
                    default:
                        {
                            rstr += ". Unknown Type 5 command.  Data is " + CtiNumStr(dataval);
                            break;
                        }
                    }
                    break;
                }
            default:
                {
                    rstr += ". Unknown Command Type " + CtiNumStr(datatype) + "  Data " + CtiNumStr(dataval);
                    break;
                }
            }

        }
    }


    return rstr;
}

