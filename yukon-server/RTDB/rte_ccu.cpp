#include "precompiled.h"

#include "rte_ccu.h"

#include "dsm2.h"
#include "dev_ccu.h"
#include "porter.h"
#include "logger.h"
#include "numstr.h"

#include "prot_versacom.h"
#include "prot_emetcon.h"
#include "expresscom.h"

#include "cparms.h"

using namespace Cti::Protocols;
using std::string;
using std::endl;
using std::list;

#define MAX_EXPRESSCOM_IN_EMETCON_LENGTH 18

static bool NoQueingVersacom   = gConfigParms.isTrue("VERSACOM_CCU_NOQUEUE");
static bool NoQueingEmetcon    = gConfigParms.isTrue("EMETCON_CCU_NOQUEUE");
static bool NoQueingExpresscom = gConfigParms.isTrue("EXPRESSCOM_CCU_NOQUEUE");
static bool NoQueing = gConfigParms.isTrue("CCU_NOQUEUE");

INT CtiRouteCCU::ExecuteRequest(CtiRequestMsg            *pReq,
                                CtiCommandParser         &parse,
                                OUTMESS                 *&OutMessage,
                                list< CtiMessage* >      &vgList,
                                list< CtiMessage* >      &retList,
                                list< OUTMESS* >         &outList)
{
    INT      status = NORMAL;

    if(_transmitterDevice)      // This is the pointer which refers this rte to its transmitter device.
    {
        if((status = _transmitterDevice->checkForInhibitedDevice(retList, OutMessage)) != DEVICEINHIBITED)
        {
            // ALL Routes MUST do this, since they are the final gasp before the trxmitting device
            OutMessage->Request.CheckSum  = _transmitterDevice->getUniqueIdentifier();
            OutMessage->MessageFlags     |= MessageFlag_ApplyExclusionLogic;           // 051903 CGP.  Are all these OMs excludable (ie susceptible to crosstalk)??

            OutMessage->DeviceID = _transmitterDevice->getID();         // This is the route transmitter device, not the causal device.
            OutMessage->Port     = _transmitterDevice->getPortID();
            OutMessage->Remote   = _transmitterDevice->getAddress();    // This is the DLC address if the CCU.

            if( NoQueing || isForeignCcuPort(OutMessage->Port) || _transmitterDevice->hasExclusions() )
            {
                OutMessage->EventCode |=  DTRAN;
                OutMessage->EventCode &= ~QUEUED;
            }

            if(OutMessage->EventCode & VERSACOM)
            {
                status = assembleVersacomRequest(pReq, parse, OutMessage, vgList, retList, outList);
            }
            else if(OutMessage->EventCode & AWORD || OutMessage->EventCode & BWORD)
            {
                status = assembleDLCRequest(parse, OutMessage, vgList, retList, outList);
            }
            else if(parse.getiValue("type") == ProtocolExpresscomType)
            {
                status = assembleExpresscomRequest(pReq, parse, OutMessage, vgList, retList, outList);
            }
            else
            {
                status = NoExecuteRequestMethod;
            }
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " ERROR: Route " << getName() << " has no  associated transmitter device" << endl;
        status = -1;
    }

    return status;
}


INT CtiRouteCCU::assembleVersacomRequest(CtiRequestMsg            *pReq,
                                         CtiCommandParser         &parse,
                                         OUTMESS                  *OutMessage,
                                         list< CtiMessage* >      &vgList,
                                         list< CtiMessage* >      &retList,
                                         list< OUTMESS* >         &outList)
{
    INT            i, j;
    INT            status = NORMAL;
    string      resultString;
    string      byteString;
    BSTRUCT        BSt;
    VSTRUCT        VSt;

    unsigned cwordCount = 0;

    /*
     * Addressing variables SHALL have been assigned at an earlier level!
     */
    if(!OutMessage->Retry)     OutMessage->Retry = 2;

    if( parse.getiValue("type") == ProtocolVersacomType && parse.isKeyValid("noqueue") )
    {
        OutMessage->EventCode |= DTRAN;
    }

    /*
     * From this point on the OutMessage will be an EMETCON type.
     * Do not use the VSt struct from it
     */
    CtiProtocolVersacom  Versacom(_transmitterDevice->getType());

    Versacom.parseRequest(parse, OutMessage->Buffer.VSt);                       // Pick out the CommandType and parameters

    /* the transmitter is an EMETCON device so load up the Preamble, B, and C words */
    /* Versacom wrapped into Emetcon requires faking the word build routines into supporting the structure */

    ::memset(&BSt, 0, sizeof(BSTRUCT));

    unsigned amp = 0;

    const int transmitterType = _transmitterDevice->getType();

    if( transmitterType == TYPE_CCU700 ||
        transmitterType == TYPE_CCU710 ||
        transmitterType == TYPE_CCU711 )
    {
        CtiDeviceIDLC *idlc = static_cast<CtiDeviceIDLC *>(_transmitterDevice.get());

        amp = idlc->getIDLC().getAmp();
    }

    /* Load up the hunks of the B structure that we need */
    BSt.Port                = _transmitterDevice->getPortID();
    BSt.Remote              = _transmitterDevice->getAddress();
    BSt.DlcRoute.Amp        = amp;
    BSt.DlcRoute.Bus        = Carrier.getBus();
    BSt.DlcRoute.RepVar     = Carrier.getCCUVarBits();
    BSt.DlcRoute.RepFixed   = Carrier.getCCUFixBits();
    BSt.DlcRoute.Stages     = getStages();                // How many repeaters on this route?

    for(j = 0; j < Versacom.entries(); j++)
    {
        OUTMESS *NewOutMessage = CTIDBG_new OUTMESS( *OutMessage );  // Create and copy

        if(NewOutMessage != NULL)
        {
            /*
             *  Now we get a fully qualified VSTRUCT which has a valid message in it
             *  finish it off by performing the emetcon wrap.
             */
            VSt = Versacom.getVStruct(j);           // Copy in the structure

            // Generate a protocol string to return to the client application.
            for(i = 0; i < VSt.Nibbles / 2 ; i++)
            {
                byteString += CtiNumStr((int)VSt.Message[i]).hex().zpad(2)+ " ";
            }
            byteString += "\n";

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
                // This code does not appear to function correctly even though it seems to represent the documentation correctly.
                // By skipping this, we are assuming a 6 byte message!
                if(gConfigParms.isTrue("PORTER_EMETCON_VERSACOM_CONFORM"))
                {
                    BSt.Function |= ((VSt.Nibbles + 1) / 2) & 0x000f;
                }
            }

            /* Now build the IO function */
            if( VSt.Nibbles < 6 && gConfigParms.isTrue("PORTER_EMETCON_VERSACOM_CONFORM"))
                BSt.IO = 2;
            else
                BSt.IO = 0;

            /* If anything left place it in structure for C words */
            if( cwordCount )
            {
                /* see how much is left */
                BSt.Length = ((VSt.Nibbles - 6) + 1) / 2;

                /* clear the memory buffer */
                ::memset (BSt.Message, 0, sizeof (BSt.Message));
                /* copy it into a cleared out message buffer */
                ::memcpy (BSt.Message, VSt.Message + 3, BSt.Length);
            }
            else
            {
                /* clear the memory buffer */
                ::memset (BSt.Message, 0, sizeof (BSt.Message));
                BSt.Length = 0;
            }

            /* Things are now ready to go */
            switch( _transmitterDevice->getType() )
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
                        if(NoQueingVersacom)
                        {
                            NewOutMessage->EventCode &= ~QUEUED;
                            NewOutMessage->EventCode |= (DTRAN | BWORD);
                        }
                        else
                        {
                            NewOutMessage->EventCode |= BWORD;
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
                            NewOutMessage->OutLength  = PREAMLEN + (cwordCount + 1) * BWORDLEN + 3;
                            NewOutMessage->EventCode |= DTRAN & BWORD;
                            NewOutMessage->TimeOut    = TIMEOUT + BSt.DlcRoute.Stages * (cwordCount + 1);

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
                case TYPE_CCU721:
                    {
                        /* check if queing is allowed */
                        if(NoQueingVersacom)
                        {
                            NewOutMessage->EventCode &= ~QUEUED;
                            NewOutMessage->EventCode |= (DTRAN | BWORD);
                        }
                        else
                        {
                            NewOutMessage->EventCode |= BWORD;
                        }

                        /* Load up the B word stuff */
                        NewOutMessage->Buffer.BSt = BSt;

                        //  retries are handled internally by the CCU-721
                        NewOutMessage->Retry = 0;

                        //  message gets built up inside the CCU code directly instead of here in the route

                        break;
                    }
            }

            outList.push_back(NewOutMessage);
        }
    }

    bool xmore = true;          // Expect more tag.

    if(Versacom.entries() > 0)
    {
        resultString = CtiNumStr(Versacom.entries()) + " Versacom commands sent on route " + getName() + " \n" + byteString;
    }
    else
    {
        xmore = false;
        resultString = "Route " + getName() + " did not transmit Versacom commands";
    }

    CtiReturnMsg *retReturn = CTIDBG_new CtiReturnMsg(OutMessage->TargetID, string(OutMessage->Request.CommandStr), resultString, status, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.GrpMsgID, OutMessage->Request.UserID, OutMessage->Request.SOE, CtiMultiMsg_vec());

    if(retReturn)
    {
        if(parse.isTwoWay()) retReturn->setExpectMore(xmore);
        retList.push_back(retReturn);
    }
    else
    {
        delete retReturn;
    }

    return status;
}

INT CtiRouteCCU::assembleDLCRequest(CtiCommandParser     &parse,
                                    OUTMESS             *&OutMessage,
                                    list< CtiMessage* >  &vgList,
                                    list< CtiMessage* >  &retList,
                                    list< OUTMESS* >     &outList)
{
    INT           status = NORMAL;

    if(OutMessage->EventCode & BWORD)
    {
        BSTRUCT &BSt = OutMessage->Buffer.BSt;

        unsigned amp = 0;

        const int transmitterType = _transmitterDevice->getType();

        if( transmitterType == TYPE_CCU700 ||
            transmitterType == TYPE_CCU710 ||
            transmitterType == TYPE_CCU711 )
        {
            CtiDeviceIDLC *idlc = static_cast<CtiDeviceIDLC *>(_transmitterDevice.get());

            amp = idlc->getIDLC().getAmp();
        }

        /* Load up the hunks of the B structure that we know/need */
        BSt.Port                = _transmitterDevice->getPortID();
        BSt.Remote              = _transmitterDevice->getAddress();
        BSt.DlcRoute.Amp        = amp;
        BSt.DlcRoute.Bus        = Carrier.getBus();
        BSt.DlcRoute.RepVar     = Carrier.getCCUVarBits();
        BSt.DlcRoute.RepFixed   = Carrier.getCCUFixBits();
        BSt.DlcRoute.Stages     = getStages();                // How many repeaters on this route?

        const bool isOneWayRequest = (BSt.IO == EmetconProtocol::IO_Write) ||
                                     (BSt.IO == EmetconProtocol::IO_Function_Write);

        if( isOneWayRequest )
        {
            //  Adjust the stages-to-follow count if we have anything special to do,
            //    such as an MCT-410 disconnect or we want to delay one-way commands
            adjustOutboundStagesToFollow(BSt.DlcRoute.Stages,
                                         OutMessage->MessageFlags,
                                         _transmitterDevice->getType());
        }
    }
    else if(OutMessage->EventCode & AWORD)
    {
        /* Load up the hunks of the A structure that we know/need */
        OutMessage->Buffer.ASt.Port = _transmitterDevice->getPortID();
        OutMessage->Buffer.ASt.Remote = _transmitterDevice->getAddress();

        OutMessage->Buffer.ASt.DlcRoute.Bus        = Carrier.getBus();
        OutMessage->Buffer.ASt.DlcRoute.RepVar     = Carrier.getCCUVarBits();
        OutMessage->Buffer.ASt.DlcRoute.RepFixed   = Carrier.getCCUFixBits();
        OutMessage->Buffer.ASt.DlcRoute.Stages     = getStages();

        // Add these two items to the list for control accounting!
        parse.setValue("control_interval", EmetconProtocol::calculateControlInterval(parse.getiValue("shed", 0)));
        parse.setValue("control_reduction", 100 );
    }

    /* Things are now ready to go */
    switch( _transmitterDevice->getType() )
    {
        case TYPE_CCU700:
        case TYPE_CCU710:
        {
            OutMessage->EventCode &= ~QUEUED;
            OutMessage->EventCode |=  DTRAN;

            /***** FALL THROUGH ** FALL THROUGH *****/
        }
        case TYPE_CCU711:
        {
            if(NoQueingEmetcon)
            {
                OutMessage->EventCode &= ~QUEUED;
                OutMessage->EventCode |=  DTRAN;
            }

            if(OutMessage->EventCode & DTRAN)
            {
                if( OutMessage->EventCode & BWORD )
                {
                    EmetconProtocol::buildBWordMessage(OutMessage);
                }
                else if( OutMessage->EventCode & AWORD )
                {
                    EmetconProtocol::buildAWordMessage(OutMessage);
                }

                /* load the IDLC specific stuff for DTRAN */
                OutMessage->Source                = 0;
                OutMessage->Destination           = DEST_DLC;
                OutMessage->Command               = CMND_DTRAN;

                if(OutMessage->InLength <= 0)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << " 062101 CGP CHECK CHECK CHECK " << endl;
                    }

                    OutMessage->InLength = 2;
                }

                OutMessage->Buffer.OutMessage[6]  = (UCHAR)OutMessage->InLength;
                OutMessage->EventCode             &= ~RCONT;
            }

            break;
        }

        case TYPE_CCU721:
        {
            //  retries are handled internally by the CCU-721
            OutMessage->Retry = 0;

            //  message gets built up inside the CCU code directly instead of here in the route

            break;
        }
    }

    //  Only send a notice if the response from this request will be sent back to the client
    if( OutMessage->Request.Connection )
    {
        CtiReturnMsg *retReturn = new CtiReturnMsg(OutMessage->TargetID, string(OutMessage->Request.CommandStr), "Emetcon DLC command sent on route " + getName(), status, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.GrpMsgID, OutMessage->Request.UserID, OutMessage->Request.SOE, CtiMultiMsg_vec());

        if( (OutMessage->MessageFlags & MessageFlag_ExpectMore)
            || parse.isTwoWay()
            || parse.isDisconnect())
        {
            retReturn->setExpectMore(true);
        }

        retList.push_back(retReturn);
    }

    outList.push_back(OutMessage);
    OutMessage = 0;

    return status;
}


/**
 * adjustOutboundStagesToFollow() adjusts the "stages to follow"
 * count based on the route's repeater count and any additional
 * silence that may be required for a command.
 *
 * @param stagesToFollow
 *               Base stages-to-follow count from the route
 * @param messageFlags
 *               MessageFlags from the OutMessage
 * @param type
 *               Transmitter type from _transmitterDevice
 */
void CtiRouteCCU::adjustOutboundStagesToFollow(unsigned short &stagesToFollow, unsigned &messageFlags, const int type)
{
    if( type == TYPE_CCU711 ||
        type == TYPE_CCU721 )
    {
        //  add on silence for the MCT-410 to broadcast to its disconnect collar
        if( messageFlags & MessageFlag_AddMctDisconnectSilence )
        {
            stagesToFollow += 2;
        }
    }

    if( type == TYPE_CCU711 )
    {
        //  make sure we have at least 1 stages-to-follow delay for the CCU-711 to cool down
        if( stagesToFollow == 0 )
        {
            stagesToFollow++;

            messageFlags |= MessageFlag_AddCcu711CooldownSilence;
        }
    }

    stagesToFollow = std::min<unsigned short>(stagesToFollow, MaxStagesToFollow);
}



INT CtiRouteCCU::assembleExpresscomRequest(CtiRequestMsg          *pReq,
                                         CtiCommandParser         &parse,
                                         OUTMESS                  *OutMessage,
                                         list< CtiMessage* >      &vgList,
                                         list< CtiMessage* >      &retList,
                                         list< OUTMESS* >         &outList)
{
    INT            status = NORMAL;
    string      resultString;
    string      byteString;
    BSTRUCT        BSt;

    unsigned cwordCount = 0;

    /*
     * Addressing variables SHALL have been assigned at an earlier level!
     */
    if(!OutMessage->Retry)     OutMessage->Retry = 2;

    if( parse.getiValue("type") == ProtocolExpresscomType && parse.isKeyValid("noqueue") )
    {
        OutMessage->EventCode |= DTRAN;
    }

    /*
     * From this point on the OutMessage will be an EMETCON type.
     */
    CtiProtocolExpresscom  xcom;
    xcom.parseAddressing(parse);                    // The parse holds all the addressing for the group.
    status = xcom.parseRequest(parse);

    // The CCU never uses a CRC for expresscom.
    xcom.setUseCRC(false);

    /* the transmitter is an EMETCON device so load up the Preamble, B, and C words */

    memset(&BSt, 0, sizeof(BSTRUCT));

    unsigned amp = 0;

    const int transmitterType = _transmitterDevice->getType();

    if( transmitterType == TYPE_CCU700 ||
        transmitterType == TYPE_CCU710 ||
        transmitterType == TYPE_CCU711 )
    {
        CtiDeviceIDLC *idlc = static_cast<CtiDeviceIDLC *>(_transmitterDevice.get());

        amp = idlc->getIDLC().getAmp();
    }

    /* Load up the hunks of the B structure that we need */
    BSt.Port                = _transmitterDevice->getPortID();
    BSt.Remote              = _transmitterDevice->getAddress();
    BSt.DlcRoute.Amp        = amp;
    BSt.DlcRoute.Bus        = Carrier.getBus();
    BSt.DlcRoute.RepVar     = Carrier.getCCUVarBits();
    BSt.DlcRoute.RepFixed   = Carrier.getCCUFixBits();
    BSt.DlcRoute.Stages     = getStages();                // How many repeaters on this route?

    if(!status && xcom.entries() > 0)
    {
        OUTMESS *NewOutMessage = CTIDBG_new OUTMESS( *OutMessage );  // Create and copy
        int size = xcom.messageSize();
        int maxsize = 0;

        for(int x = 1;x <= xcom.entries();x++)//find out the largest size of any divided up message possible.
        {
            if(xcom.messageSize(x)>maxsize)
               maxsize = xcom.messageSize(x);
        }

        if(NewOutMessage != NULL && size > 0 && size <= MAX_EXPRESSCOM_IN_EMETCON_LENGTH)
        {
            for(int j=0;j<size;j++)
                byteString += CtiNumStr(xcom.getByte(j)).hex().zpad(2);

            if(size>=2)
            {
                BSt.Address = 0x3D0000 | (xcom.getByte(0)<<8) | xcom.getByte(1);
            }
            else if(size == 1)
            {
                BSt.Address = 0x3D0000 | (xcom.getByte(0)<<8);//I dont think this is technically possible
            }

            if(size >=3)
            {
                BSt.Function = xcom.getByte(2);
            }
            else
            {
                BSt.Function = 0x00; //According to doc if its not used it must be 0
            }
            BSt.IO = 0x00;//defined in expresscom doc

            /* Calcultate the number of words that will be involved */
            // We can fit 3 bytes in B word, and 5 in each C word, up to 3 C words
            if(size <= 3)//whole thing fit into B word
                cwordCount = 0;
            else if(size <= 8)
                cwordCount = 1;
            else if(size <= 13)
                cwordCount = 2;
            else if(size <= 18)
                cwordCount = 3;


            /* If anything left place it in structure for C words */
            if( cwordCount )
            {
                /* see how much is left */
                BSt.Length = size-3;//simple, total minus number in B word

                /* clear the memory buffer */
                memset (BSt.Message, 0, sizeof (BSt.Message));
                /* copy it into a cleared out message buffer */
                for(int j=0;j<(size-3);j++)
                    BSt.Message[j] = xcom.getByte(j+3);
            }
            else
            {
                BSt.Length = 0;
            }

            /* Things are now ready to go */
            switch( _transmitterDevice->getType() )//This should probably be made into a function, but for now ill add to bloat
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
                        if(NoQueingExpresscom)
                        {
                            NewOutMessage->EventCode &= ~QUEUED;
                            NewOutMessage->EventCode |= (DTRAN | BWORD);
                        }
                        else
                        {
                            NewOutMessage->EventCode |= BWORD;
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
                            NewOutMessage->OutLength  = PREAMLEN + (cwordCount + 1) * BWORDLEN + 3;
                            NewOutMessage->EventCode |= DTRAN & BWORD;
                            NewOutMessage->TimeOut    = TIMEOUT + BSt.DlcRoute.Stages * (cwordCount + 1);

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
                case TYPE_CCU721:
                    {
                        /* check if queing is allowed */
                        if(NoQueingExpresscom)
                        {
                            NewOutMessage->EventCode &= ~QUEUED;
                            NewOutMessage->EventCode |= (DTRAN | BWORD);
                        }
                        else
                        {
                            NewOutMessage->EventCode |= BWORD;
                        }

                        /* Load up the B word stuff */
                        NewOutMessage->Buffer.BSt = BSt;

                        //  retries are handled internally by the CCU-721
                        NewOutMessage->Retry = 0;

                        //  message gets built up inside the CCU code directly instead of here in the route

                        break;
                    }
            }
            outList.push_back(CTIDBG_new OUTMESS(*NewOutMessage));
        }
        else if(xcom.entries()>1 && maxsize<=MAX_EXPRESSCOM_IN_EMETCON_LENGTH)//we can divide this up and still send every message!
        {
            for(int i = 1;i <= xcom.entries(); i++)
            {
                size = xcom.messageSize(i);

                for(int j=0;j<size;j++)
                    byteString += CtiNumStr(xcom.getByte(j,i)).hex().zpad(2);

                byteString += "\n";

                if(size>=2)
                {
                    BSt.Address = 0x3D0000 | (xcom.getByte(0,i)<<8) | xcom.getByte(1,i);
                }
                else if(size == 1)
                {
                    BSt.Address = 0x3D0000 | (xcom.getByte(0,i)<<8);//I dont think this is technically possible
                }

                if(size >=3)
                {
                    BSt.Function = xcom.getByte(2,i);
                }
                else
                {
                    BSt.Function = 0x00; //According to doc if its not used it must be 0
                }
                BSt.IO = 0x00;//defined in expresscom doc

                /* Calcultate the number of words that will be involved */
                // We can fit 3 bytes in B word, and 5 in each C word, up to 3 C words
                if(size <= 3)//whole thing fit into B word
                    cwordCount = 0;
                else if(size <= 8)
                    cwordCount = 1;
                else if(size <= 13)
                    cwordCount = 2;
                else if(size <= 18)
                    cwordCount = 3;


                /* If anything left place it in structure for C words */
                if( cwordCount )
                {
                    /* see how much is left */
                    BSt.Length = size-3;//simple, total minus number in B word

                    /* clear the memory buffer */
                    memset (BSt.Message, 0, sizeof (BSt.Message));
                    /* copy it into a cleared out message buffer */
                    for(int j=0;j<(size-3);j++)
                        BSt.Message[j] = xcom.getByte(j+3,i);
                }
                else
                {
                    BSt.Length = 0;
                }

                /* Things are now ready to go */
                switch( _transmitterDevice->getType() )//This should be put in a function
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
                            if(NoQueingExpresscom)
                            {
                                NewOutMessage->EventCode &= ~QUEUED;
                                NewOutMessage->EventCode |= (DTRAN | BWORD);
                            }
                            else
                            {
                                NewOutMessage->EventCode |= BWORD;
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
                                NewOutMessage->OutLength  = PREAMLEN + (cwordCount + 1) * BWORDLEN + 3;
                                NewOutMessage->EventCode |= DTRAN & BWORD;
                                NewOutMessage->TimeOut    = TIMEOUT + BSt.DlcRoute.Stages * (cwordCount + 1);

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
                    case TYPE_CCU721:
                        {
                            /* check if queing is allowed */
                            if(NoQueingExpresscom)
                            {
                                NewOutMessage->EventCode &= ~QUEUED;
                                NewOutMessage->EventCode |= (DTRAN | BWORD);
                            }
                            else
                            {
                                NewOutMessage->EventCode |= BWORD;
                            }

                            /* Load up the B word stuff */
                            NewOutMessage->Buffer.BSt = BSt;

                            //  retries are handled internally by the CCU-721
                            NewOutMessage->Retry = 0;

                            //  message gets built up inside the CCU code directly instead of here in the route

                            break;
                        }
                }
                outList.push_back(CTIDBG_new OUTMESS(*NewOutMessage));
            }
        }
        else
        {
            //really not sure what to do here
            status = BADRANGE;
            resultString = "Message length was too large for expresscom in emetcon message. Length: " + CtiNumStr(size) + " Maximum: " + CtiNumStr(MAX_EXPRESSCOM_IN_EMETCON_LENGTH);
            CtiReturnMsg *retReturn = CTIDBG_new CtiReturnMsg(OutMessage->TargetID, string(OutMessage->Request.CommandStr), resultString, status, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.GrpMsgID, OutMessage->Request.UserID, OutMessage->Request.SOE, CtiMultiMsg_vec());

            if(retReturn)
            {
                retList.push_back(retReturn);
            }
        }

        if(NewOutMessage)
        {
            delete NewOutMessage;
            NewOutMessage = NULL;
        }
    }
    else
    {
        status = BADPARAM;
        resultString = "Bad parameter in Expresscom command: \"" + parse.getCommandStr()+ "\" failed. Error " + CtiNumStr(status) + " - " + FormatError(status);

        CtiReturnMsg *retReturn = CTIDBG_new CtiReturnMsg(OutMessage->TargetID, string(OutMessage->Request.CommandStr), resultString, status, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.GrpMsgID, OutMessage->Request.UserID, OutMessage->Request.SOE, CtiMultiMsg_vec());

        if(retReturn)
        {
            retList.push_back(retReturn);
        }
    }

    bool xmore = true;          // Expect more tag.

    if(xcom.entries() > 0 && !status)
    {
        resultString = CtiNumStr(xcom.entries()) + " Expresscom commands sent on route " + getName();

        if( !gConfigParms.isTrue("HIDE_PROTOCOL") )
        {
            resultString += " \n" + byteString;
        }
    }
    else
    {
        xmore = false;
        resultString = "Route " + getName() + " did not transmit Expresscom commands";
    }

    CtiReturnMsg *retReturn = CTIDBG_new CtiReturnMsg(OutMessage->TargetID, string(OutMessage->Request.CommandStr), resultString, status, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.GrpMsgID, OutMessage->Request.UserID, OutMessage->Request.SOE, CtiMultiMsg_vec());

    if(retReturn)
    {
        if(parse.isTwoWay()) retReturn->setExpectMore(xmore);
        retList.push_back(retReturn);
    }
    else
    {
        delete retReturn;
    }

    return status;
}


CtiRouteCCU::CtiRouteCCU()
{
}

CtiRouteCCU::CtiRouteCCU(const CtiRouteCCU& aRef)
{
    *this = aRef;
}

CtiRouteCCU::~CtiRouteCCU()
{
}

CtiRouteCCU& CtiRouteCCU::operator=(const CtiRouteCCU& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
        Carrier = aRef.Carrier;
        RepeaterList = aRef.RepeaterList;
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

CtiRouteCCU::CtiRepeaterList_t&  CtiRouteCCU::getRepeaterList()
{
    return RepeaterList;
}

INT CtiRouteCCU::getStages()     const  {   return RepeaterList.entries();      }
INT CtiRouteCCU::getBus()        const  {   return Carrier.getBus();            }
INT CtiRouteCCU::getCCUFixBits() const  {   return Carrier.getCCUFixBits();     }
INT CtiRouteCCU::getCCUVarBits() const  {   return Carrier.getCCUVarBits();     }

string CtiRouteCCU::getSQLCoreStatement()
{
    static const string sql =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, YP.disableflag, "
                                   "RT.routeid, RT.deviceid, RT.defaultroute, CRT.busnumber, CRT.ccufixbits, "
                                   "CRT.ccuvariablebits, CRT.userlocked, CRT.resetrptsettings "
                               "FROM YukonPAObject YP LEFT OUTER JOIN Route RT ON YP.paobjectid = RT.routeid "
                                   "LEFT OUTER JOIN CarrierRoute CRT ON YP.paobjectid = CRT.routeid";

    return sql;
}

void CtiRouteCCU::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    Carrier.DecodeDatabaseReader(rdr);
}

void CtiRouteCCU::addRepeater(const CtiTableRepeaterRoute &Rpt)
{
    RepeaterList.insert(Rpt);
}

