/*-----------------------------------------------------------------------------*
*
* File:   prot_sa305
*
* Class:  CtiProtocolSA305
* Date:   3/8/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2005/03/17 05:15:34 $
* HISTORY      :
* $Log: prot_sa305.h,v $
* Revision 1.9  2005/03/17 05:15:34  mfisher
* 305 currently doesn't share any similarities with Protocol::Interface, so it's going to be orphaned for a while
*
* Revision 1.8  2005/03/10 19:22:50  mfisher
* changed CtiProtocolBase to Cti::Protocol::Interface
*
* Revision 1.7  2005/02/10 23:23:58  alauinger
* Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build
*
* Revision 1.6  2005/01/04 22:16:03  cplender
* Completed the asString() method.
*
* Revision 1.5  2004/12/14 22:25:16  cplender
* Various to wring out config commands.  Should be pretty good.
*
* Revision 1.4  2004/11/17 23:42:38  cplender
* Complete 305 for RTC transmitter
*
* Revision 1.3  2004/11/08 14:40:39  cplender
* 305 Protocol should send controls on RTCs now.
*
* Revision 1.2  2004/11/05 17:25:59  cplender
*
* Getting 305s to work
*
* Revision 1.1  2004/03/18 19:46:44  cplender
* Added code to support the SA305 protocol and load group
*
*
* Copyright (c) 2002, 2003, 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __PROT_SA305_H__
#define __PROT_SA305_H__

#include <vector>
using namespace std;

#include "cmdparse.h"
#include "dlldefs.h"
#include "dllbase.h"
#include "dsm2.h"
#include "pointtypes.h"


class IM_EX_PROT CtiProtocolSA305
{
protected:

    enum {
        AddressTypeGroupFlag = 0x20,
        GroupFlag = 0x10,
        DivisionFlag = 0x08,
        SubstationFlag = 0x04,
        CommandTypeOperationFlag = 0x00,
        CommandTypeAsignmentFlag = 0x02,
        CommandDescription_DIMode = 0x01,
        CommandDescription_DLCMode = 0x00,
        CommandDescription_29Bit = 0x01,
        CommandDescription_13Bit = 0x00
    };


private:

    int _padBits;            // For RTC trx this is 00b.
    int _startBits;         // May be 100 = "4" or 101 = "5" For adaptive algorithm.
    bool _messageReady;

    int _serial;            // 22-bit
    int _group;             // 6 bits. Optional group address
    int _division;          // 6-bits. Optional group address
    int _substation;        // 10-bits. Optional group address

    int _utility;           // 4-bits. Always sent.
    int _rateFamily;        // 3-bits. Always sent with a group addressed message.
    int _rateMember;        // 4-bits. Always sent with a group addressed message.
    int _rateHierarchy;     // 1-bits. Always sent with a group addressed message.

    int _addressUsage;      // Used to specify which address fields are sent no matter which are set.

    // The flags member is generated by other calls and cannot be directly edited.
    // Flags F5 - Address Type - if set, one or more group/optional addresses are included.
    // Flags F4 - Group - if set, the group address is included in the message.
    // Flags F3 - Division - if set, the division address is included in the message.
    // Flags F2 - Substation - if set, the substation address is included in the message.
    // Flags F1 - Command Type -
    //              If F1 is set a configuration message.
    //              If F1 clear an operational command is sent in the message.
    // Flags F0 - Command Description -
    //              F1=0: If F1 set, the command is a 29-bit assignement.
    //              F1=0: If F1 clear, the command is a 13-bit general purpose assignement.
    int _flags;              // 6-bits

    // The following data members are used if the F1 Flag is CLEAR.  This is an operational command and is 13-bits in length.
    int _strategy;          // 6-bits. Describes the timeouts and cycle times. Found in table Appendix A.
    int _functions;         // 4-bits. FN4-FN1.  All four or any combination may be represented in this way.
    int _repetitions;       // 4-bits. ATTN: 0 in this field means 1 repetition.  Max is 13 meaning 14 repitions.  Indicates the number of times the strategy is executed.
    int _priority;          // 2-bits. 0 is the lowest priority and 3 the highest.

    // The following data members are used if the F1 Flag is SET.  This is an assignment or configuration address.
    int _dataType;          // 5-bits. Describes the assignment function.
    int _data;              // 8-bits. Defines the data to download.


    // The data below is used to identify the strategy in a Versacomesque fashion.  This data is converted to the closest equal or higher control values.
    float _period;          // Cycle Time. 7.5, 15, 20, 30, & 60 are valid values.
    float _percentageOff;   // Percentage of the cycle that the loads are to be shed.


    int _transmitterType;       // ? Transmitter
    int _transmitterAddress;    // Used for RTC targeted 305 messages
    BYTE _rtcResponse;          // Should the RTC respond to commands?


    vector< BYTE > _messageBits;  // Store it as bits.  Not sure if this will cost me in the long run...
    int _messageCount;

    void addBits(unsigned int src, int num=32);
    void setBit(unsigned int offset, BYTE bit);
    UINT getBits(unsigned int &offset, int len) const;

    void resetMessage();
    void addressMessage(int command_type = CommandTypeOperationFlag, int command_description = CommandDescription_DIMode);
    void terminateMessage();
    void resolveAddressLevel();
    INT assembleControl(CtiCommandParser &parse, CtiOutMessage &OutMessage);
    INT assemblePutConfig(CtiCommandParser &parse, CtiOutMessage &OutMessage);

    INT timedLoadControl();                 // This is a shed!
    INT restoreLoadControl();
    INT cycleLoadControl();

    /*
     * This method used the input strategy or the period and percentage to produce a strategy value.
     * The produced value will be equal or larger than the requested parse.  If the parse is unsuccessful, the result will be -1.
     */
    int solveStrategy(CtiCommandParser &parse);

    static bool _noCRC;

    RWCString _bitStr;

    static RWCString _strategyStr[64];

public:

    enum
    {
        ModeUnspecified = 0,
        ModeOctal,              // Every three bits are converted into an octal value.
        ModeHex,                // Every eight bits are converted into a hex value.
        ModeSerial,             // Data is churned out the port in a serial fashion.
        ModeNumericPage         // Every three bits are converted into an octal value and added to char '0' to be sent as a page.
    };

    CtiProtocolSA305();
    CtiProtocolSA305(BYTE *bytestr, UINT bytelen);
    CtiProtocolSA305(const CtiProtocolSA305& aRef);

    virtual ~CtiProtocolSA305();
    CtiProtocolSA305& operator=(const CtiProtocolSA305& aRef);

    int parseCommand(CtiCommandParser &parse, CtiOutMessage &OutMessage);
    void dumpBits() const;

    int getStartBits() const;
    CtiProtocolSA305& setStartBits(int val);

    int getPadBits() const;                 // Two bit prequel used for rtcTargets.
    CtiProtocolSA305& setPadBits(int val);

    int getSerial() const;
    CtiProtocolSA305& setSerial(int val);

    int getGroup() const;
    CtiProtocolSA305& setGroup(int val);

    int getDivision() const;
    CtiProtocolSA305& setDivision(int val);

    int getSubstation() const;
    CtiProtocolSA305& setSubstation(int val);

    int getUtility() const;
    CtiProtocolSA305& setUtility(int val);

    // This is a convienence function and sets both the family and member as a 7-bit quantity.
    CtiProtocolSA305& setRatePackage(int val);

    int getRateFamily() const;
    CtiProtocolSA305& setRateFamily(int val);

    int getRateMember() const;
    CtiProtocolSA305& setRateMember(int val);

    int getRateHierarchy() const;
    CtiProtocolSA305& setRateHierarchy(int val);

    int getStrategy() const;
    CtiProtocolSA305& setStrategy(int val);

    int getFunctions() const;
    CtiProtocolSA305& setFunctions(int val);

    int getPriority() const;
    CtiProtocolSA305& setPriority(int val);

    int getDataType() const;
    CtiProtocolSA305& setDataType(int val);

    int getData() const;
    CtiProtocolSA305& setData(int val);

    float getPeriod() const;
    CtiProtocolSA305& setPeriod(float val);

    float getPercentageOff() const;
    CtiProtocolSA305& setPercentageOff(float val);

    unsigned char addBitToCRC(unsigned char crc, unsigned char bit); // bit is 0 or 1
    unsigned char addOctalCharToCRC(unsigned char crc, unsigned char ch); // octal char
    void testCRC(char* testData);
    void appendCRCToMessage();

    bool messageReady() const;
    int getMessageLength(int mode) const;      // Returns the length in characters of this message.
    int buildMessage(int mode, CHAR *buffer) const;      // Returns the length in characters of this message.

    CtiProtocolSA305& setTransmitterAddress( int val );

    CtiProtocolSA305& setTransmitterType( int trans );
    CtiProtocolSA305& setRTCResponse( bool bv = true ); // Should the RTC respond to commands.

    RWCString getBitString() const;
    RWCString  asString() const;

};
#endif // #ifndef __PROT_SA305_H__
