

#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   rte_ccu
*
* Date:   2/20/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/rte_ccu.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2002/06/05 17:42:02 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <iostream>
#include <iomanip>
using namespace std;

#include "dsm2.h"
#include "rte_xcu.h"
#include "master.h"
#include "cmdparse.h"
#include "ctibase.h"
#include "dev_remote.h"
#include "dev_ccu.h"
#include "rte_ccu.h"
#include "msg_pcrequest.h"
#include "porter.h"
#include "logger.h"
#include "numstr.h"

#include "prot_versacom.h"
#include "prot_emetcon.h"

static INT NoQueing = FALSE;

INT CtiRouteCCU::ExecuteRequest(CtiRequestMsg                  *pReq,
                                CtiCommandParser               &parse,
                                OUTMESS                        *&OutMessage,
                                RWTPtrSlist< CtiMessage >      &vgList,
                                RWTPtrSlist< CtiMessage >      &retList,
                                RWTPtrSlist< OUTMESS >         &outList)
{
    INT      status = NORMAL;
    CHAR     temp[80];

    BASEDLL_IMPORT extern CTINEXUS PorterNexus;

    if(Device != NULL)      // This is the pointer which refers this rte to its transmitter device.
    {
        // ALL Routes MUST do this, since they are the final gasp before the trxmitting device
        OutMessage->Request.CheckSum = Device->getUniqueIdentifier();

        if(OutMessage->EventCode & VERSACOM)
        {
            status = assembleVersacomRequest(pReq, parse, OutMessage, vgList, retList, outList);
        }
        else if(OutMessage->EventCode & AWORD || OutMessage->EventCode & BWORD)
        {
            status = assembleDLCRequest(pReq, parse, OutMessage, vgList, retList, outList);
        }
        else
        {
            cout << "Finish some code here " << __FILE__ << " (" << __LINE__ << ")"  << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " ERROR: Route " << getName() << " has no  associated transmitter device" << endl;
        status = -1;
    }

    if(OutMessage != NULL)
    {
        delete OutMessage;       // Get rid of the memory which got us here.
        OutMessage = NULL;
    }

    return status;
}


INT CtiRouteCCU::assembleVersacomRequest(CtiRequestMsg                  *pReq,
                                         CtiCommandParser               &parse,
                                         OUTMESS                        *OutMessage,
                                         RWTPtrSlist< CtiMessage >      &vgList,
                                         RWTPtrSlist< CtiMessage >      &retList,
                                         RWTPtrSlist< OUTMESS >         &outList)
{
    INT            i, j;
    INT            status = NORMAL;
    RWCString      resultString;
    BSTRUCT        BSt;
    VSTRUCT        VSt;

    INT cwordCount = 0;

    /*
     * Addressing variables SHALL have been assigned at an earlier level!
     */
    OutMessage->DeviceID       = Device->getID();
    OutMessage->Port           = Device->getPortID();
    OutMessage->Remote         = Device->getAddress();
    if(!OutMessage->Retry)     OutMessage->Retry = 2;

    /*
     * From this point on the OutMessage will be an EMETCON type.
     * Do not use the VSt struct from it
     */
    CtiProtocolVersacom  Versacom(Device->getType());

    Versacom.parseRequest(parse, OutMessage->Buffer.VSt);                       // Pick out the CommandType and parameters

    /* the transmitter is an EMETCON device so load up the Preamble, B, and C words */
    /* Versacom wrapped into Emetcon requires faking the word build routines into supporting the structure */

    memset(&BSt, 0, sizeof(BSTRUCT));

    /* Load up the hunks of the B structure that we need */
    BSt.Port                = Device->getPortID();
    BSt.Remote              = Device->getAddress();
    BSt.DlcRoute.Amp        = 0; // Carrier.getAmpUseType();
    BSt.DlcRoute.Feeder     = Carrier.getBus();
    BSt.DlcRoute.RepVar     = Carrier.getCCUVarBits();
    BSt.DlcRoute.RepFixed   = Carrier.getCCUFixBits();
    BSt.DlcRoute.Stages     = getStages();                // How many repeaters on this route?

    for(j = 0; j < Versacom.entries(); j++)
    {
        OUTMESS *NewOutMessage = new OUTMESS( *OutMessage );  // Create and copy

        if(NewOutMessage != NULL)
        {
            /*
             *  Now we get a fully qualified VSTRUCT which has a valid message in it
             *  finish it off by performing the emetcon wrap.
             */
            VSt = Versacom.getVStruct(j);           // Copy in the structure

            /* Calcultate the number of words that will be involved */
            if(VSt.Nibbles <= 6)
                cwordCount = 0;
            else if(VSt.Nibbles <= 16)
                cwordCount = 1;
            else if(VSt.Nibbles <= 26)
                cwordCount = 2;
            else
                cwordCount = 3;

            /* Now build the address */
            BSt.Address = 0x3e0000 | ((ULONG)VSt.Message[0] << 8) | (ULONG)VSt.Message[1];

            /* Now build the function */
            BSt.Function = VSt.Message[2];
            if( VSt.Nibbles < 5 )
            {
                BSt.Function |= ((VSt.Nibbles + 1) / 2) & 0x000f;
            }

            /* Now build the IO function */
            if( VSt.Nibbles < 5 )
                BSt.IO = 2;
            else
                BSt.IO = 0;

            /* If anything left place it in structure for C words */
            if( cwordCount )
            {
                /* see how much is left */
                BSt.Length = ((VSt.Nibbles - 6) + 1) / 2;

                /* clear the memory buffer */
                memset (BSt.Message, 0, sizeof (BSt.Message));
                /* copy it into a cleared out message buffer */
                memcpy (BSt.Message, VSt.Message + 3, BSt.Length);
            }
            else
                BSt.Length = 0;

            /* Things are now ready to go */
            switch( Device->getType() )
            {
            case TYPE_CCU700:
            case TYPE_CCU710:
                {
                    NewOutMessage->EventCode &= ~QUEUED;
                    NewOutMessage->EventCode |= (DTRAN | BWORD);

                    /***** FALL THROUGH ** FALL THROUGH *****/
                }
            case TYPE_CCU711:
                {
                    /* check if queing is allowed */
                    if(NoQueing)
                    {
                        NewOutMessage->EventCode &= ~QUEUED;
                        NewOutMessage->EventCode |= (DTRAN | BWORD);
                    }
                    else
                    {
                        // ACH FIX FIX FIX 051001 CGP: Must allow for queueing!

#if 0
                        NewOutMessage->EventCode |= BWORD;
#else
                        NewOutMessage->EventCode &= ~QUEUED;
                        NewOutMessage->EventCode |= (DTRAN | BWORD);
#endif
                    }

                    if(NewOutMessage->EventCode & DTRAN)
                    {
                        /* Load the B word */
                        B_Word (NewOutMessage->Buffer.OutMessage+PREIDLEN+PREAMLEN, BSt, cwordCount);

                        /* Load the C words if neccessary */
                        if(cwordCount)
                            C_Words (NewOutMessage->Buffer.OutMessage+PREIDLEN+PREAMLEN+BWORDLEN, BSt.Message, BSt.Length, &cwordCount);

                        /* Now do the preamble */
                        BPreamble (NewOutMessage->Buffer.OutMessage+PREIDLEN, BSt, cwordCount);

                        /* Calculate the length of the message */
                        NewOutMessage->OutLength = PREAMLEN + (cwordCount + 1) * BWORDLEN + 3;
                        NewOutMessage->EventCode |= DTRAN & BWORD;
                        NewOutMessage->TimeOut   = TIMEOUT + BSt.DlcRoute.Stages * (cwordCount + 1);

                        /* load the IDLC specific stuff for DTRAN */
                        NewOutMessage->Source                = 0;
                        NewOutMessage->Destination           = DEST_DLC;
                        NewOutMessage->Command               = CMND_DTRAN;
                        NewOutMessage->InLength              = 2;
                        NewOutMessage->Buffer.OutMessage[6]  = (UCHAR)OutMessage->InLength;
                        NewOutMessage->EventCode             &= ~RCONT;
                    }
                    else
                    {
                        /* Load up the B word stuff */
                        NewOutMessage->Buffer.BSt = BSt;
                    }
                    break;
                }
            }

            outList.insert(NewOutMessage);
        }
    }

    bool xmore = true;          // Expect more tag.

    if(Versacom.entries() > 0)
    {
        resultString = CtiNumStr(Versacom.entries()) + " Versacom commands sent on route " + getName();
    }
    else
    {
        xmore = false;
        resultString = "Route " + getName() + " did not transmit Versacom commands";
    }

    CtiReturnMsg *retReturn = new CtiReturnMsg(OutMessage->TargetID, RWCString(OutMessage->Request.CommandStr), resultString, status, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.TrxID, OutMessage->Request.UserID, OutMessage->Request.SOE, RWOrdered());

    if(retReturn)
    {
        if(parse.isTwoWay()) retReturn->setExpectMore(xmore);
        retList.insert(retReturn);
    }
    else
    {
        delete retReturn;
    }

    return status;
}

INT CtiRouteCCU::assembleDLCRequest(CtiRequestMsg                  *pReq,
                                    CtiCommandParser               &parse,
                                    OUTMESS                        *OutMessage,
                                    RWTPtrSlist< CtiMessage >      &vgList,
                                    RWTPtrSlist< CtiMessage >      &retList,
                                    RWTPtrSlist< OUTMESS >         &outList)
{
    INT            i, j;
    INT            status = NORMAL;
    bool           xmore = true;
    RWCString      resultString;
    CtiDeviceCCU  *trxDev;


    OutMessage->DeviceID = Device->getID();         // This is the route transmitter device, not the causal device.
    OutMessage->Port     = Device->getPortID();
    OutMessage->Remote   = Device->getAddress();    // This is the DLC address if the CCU.

    CtiProtocolEmetcon Emetcon(OutMessage->Buffer.BSt.DeviceType, Device->getType());

    if(OutMessage->EventCode & BWORD)
    {
        Emetcon.setSSpec(OutMessage->Buffer.BSt.SSpec);

        /* Load up the hunks of the B structure that we know/need */
        OutMessage->Buffer.BSt.Port                = Device->getPortID();
        OutMessage->Buffer.BSt.Remote              = Device->getAddress();
        if( Device != NULL )
        {
            switch( ((CtiDeviceIDLC *)Device)->getIDLC().getCCUAmpUseType() )
            {
                default:
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                case RouteAmpAlternating:
                case RouteAmpAltFail:
                case RouteAmpDefault1Fail2:
                case RouteAmp1:
                    OutMessage->Buffer.BSt.DlcRoute.Amp = 0;
                    break;

                case RouteAmpDefault2Fail1:
                case RouteAmp2:
                    OutMessage->Buffer.BSt.DlcRoute.Amp = 1;
                    break;

            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "Device == NULL, defaulting to amp card 1" << endl;
            }

            OutMessage->Buffer.BSt.DlcRoute.Amp        = 0; // Carrier.getAmpUseType();
        }

        OutMessage->Buffer.BSt.DlcRoute.Feeder     = Carrier.getBus();
        OutMessage->Buffer.BSt.DlcRoute.RepVar     = Carrier.getCCUVarBits();
        OutMessage->Buffer.BSt.DlcRoute.RepFixed   = Carrier.getCCUFixBits();
        OutMessage->Buffer.BSt.DlcRoute.Stages     = getStages();                // How many repeaters on this route?

        status = Emetcon.parseRequest(parse, *OutMessage);                     // Determin the stuff be need based upon the command.
    }
    else if(OutMessage->EventCode & AWORD)
    {
        /* Load up the hunks of the A structure that we know/need */
        // 3/17/02 MSKF // OutMessage->EventCode |= DTRAN;                                         // Make sure not queued!
        OutMessage->Buffer.ASt.Port = Device->getPortID();
        OutMessage->Buffer.ASt.Remote = Device->getAddress();

        OutMessage->Buffer.ASt.DlcRoute.Feeder     = Carrier.getBus();
        OutMessage->Buffer.ASt.DlcRoute.RepVar     = Carrier.getCCUVarBits();
        OutMessage->Buffer.ASt.DlcRoute.RepFixed   = Carrier.getCCUFixBits();

        status = Emetcon.parseRequest(parse, *OutMessage);                     // Determine the stuff be need based upon the command.
    }


    if( Emetcon.entries() > 0 )
    {
        resultString = CtiNumStr(Emetcon.entries()) + " Emetcon DLC commands sent on route " + getName();
    }
    else
    {
        xmore = false;
        resultString = "Emetcon DLC commands failed with error " + CtiNumStr(status) + " on route " + getName();
    }

    while( Emetcon.entries() > 0 )
    {
        OUTMESS *NewOutMessage = Emetcon.popOutMessage();

        if(NewOutMessage != NULL)
        {
            /* Things are now ready to go */
            switch( Device->getType() )
            {
            case TYPE_CCU700:
            case TYPE_CCU710:
                {
                    NewOutMessage->EventCode &= ~QUEUED;
                    NewOutMessage->EventCode |= (DTRAN | BWORD);

                    /***** FALL THROUGH ** FALL THROUGH *****/
                }
            case TYPE_CCU711:
                {
                    /* check if queing is allowed */
                    if(NoQueing)
                    {
                        NewOutMessage->EventCode &= ~QUEUED;
                        NewOutMessage->EventCode |= (DTRAN  | BWORD);
                    }
                    else
                    {
                        // 3/17/02 MSKF //NewOutMessage->EventCode |= BWORD;
                    }

                    if(NewOutMessage->EventCode & DTRAN)
                    {
                        /* load the IDLC specific stuff for DTRAN */
                        NewOutMessage->Source                = 0;
                        NewOutMessage->Destination           = DEST_DLC;
                        NewOutMessage->Command               = CMND_DTRAN;

                        if(NewOutMessage->InLength <= 0)
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                dout << " 062101 CGP CHECK CHECK CHECK " << endl;
                            }

                            NewOutMessage->InLength              = 2;
                        }

                        NewOutMessage->Buffer.OutMessage[6]  = (UCHAR)NewOutMessage->InLength;
                        NewOutMessage->EventCode             &= ~RCONT;
                    }
                    else
                    {
                        /* Load up the B word stuff */
                        NewOutMessage->Buffer.BSt = OutMessage->Buffer.BSt;
                    }
                    break;
                }
            }

            outList.insert(NewOutMessage);
        }
    }

    CtiReturnMsg *retReturn = new CtiReturnMsg(OutMessage->TargetID, RWCString(OutMessage->Request.CommandStr), resultString, status, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.TrxID, OutMessage->Request.UserID, OutMessage->Request.SOE, RWOrdered());

    if(retReturn)
    {
        if(parse.isTwoWay()) retReturn->setExpectMore(xmore);
        retList.insert(retReturn);
    }
    else
    {
        delete retReturn;
    }

    return status;
}

CtiRouteCCU::CtiRouteCCU() {}

CtiRouteCCU::CtiRouteCCU(const CtiRouteCCU& aRef)
{
    *this = aRef;
}

CtiRouteCCU::~CtiRouteCCU() {}

CtiRouteCCU& CtiRouteCCU::operator=(const CtiRouteCCU& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
        Carrier = aRef.getCarrier();
        RepeaterList = aRef.getRepeaterList();
    }
    return *this;
}

void CtiRouteCCU::DumpData()
{
    Inherited::DumpData();

    Carrier.DumpData();

    for(int i = 0; i < RepeaterList.length(); i++)
        RepeaterList[i].DumpData();
}

CtiRouteCCU::CtiRepeaterList_t&     CtiRouteCCU::getRepeaterList()          { return RepeaterList;}
CtiRouteCCU::CtiRepeaterList_t      CtiRouteCCU::getRepeaterList() const    { return RepeaterList;}

INT    CtiRouteCCU::getStages() const          { return RepeaterList.entries();}
INT    CtiRouteCCU::getBus() const             { return Carrier.getBus();}
INT    CtiRouteCCU::getCCUFixBits() const      { return Carrier.getCCUFixBits();}
INT    CtiRouteCCU::getCCUVarBits() const      { return Carrier.getCCUVarBits();}

CtiTableCarrierRoute    CtiRouteCCU::getCarrier() const         { return Carrier;}
CtiTableCarrierRoute&   CtiRouteCCU::getCarrier()               { return Carrier;}
CtiRouteCCU&            CtiRouteCCU::setCarrier( const CtiTableCarrierRoute& aCarrier)
{
    Carrier = aCarrier;
    return *this;
}

void CtiRouteCCU::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);
    CtiTableCarrierRoute::getSQL(db, keyTable, selector);
}

void CtiRouteCCU::DecodeDatabaseReader(RWDBReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if(getDebugLevel() & 0x0800)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    Carrier.DecodeDatabaseReader(rdr);
}

void CtiRouteCCU::DecodeRepeaterDatabaseReader(RWDBReader &rdr)
{
    CtiTableRepeaterRoute   Rpt;

    if(getType() == CCURouteType)   // Just make darn sure.  (used to be RepeaterRouteType)
    {
        if(getDebugLevel() & 0x0800)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        Rpt.DecodeDatabaseReader(rdr);
        RepeaterList.insert(Rpt);
    }
}

