
#pragma warning( disable : 4786)
#ifndef __PORT_MODEM_H__
#define __PORT_MODEM_H__

/*-----------------------------------------------------------------------------*
*
* File:   port_modem
*
* Class:  CtiHayesModem
* Date:   12/5/2002
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2002/12/11 23:37:32 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "port_base.h"

class CtiHayesModem
{
public:

    typedef void (*CHAR_PRINTER)( char c );

protected:

    int _status;        // Modem status.
    int _delay_value;
    RWCString _match_string;
    CHAR_PRINTER _character_printer;

private:

    int hm3(  char *s, int ctrl );

    CtiPort *_port;                     // Not ours to touch or delete or own!

public:

    CtiHayesModem();
    virtual ~CtiHayesModem();

    CtiHayesModem& setPort( CtiPort *port );


    int reset(  );
    int getRegister(  int modem_register );
    int setRegister(  int modem_register, int value );
    int setAutoAnswerRingCount(  int count );
    int getIncomingRingCount(  );
    int setEscapeCode(  char escape_char );
    int setEndOfLineCharacter(  char end_of_line_char );
    int setLineFeedCharacter(  char line_feed_char );
    int setBackspaceCharacter(  char backspace_char );
    int setWaitForDialToneTime(  int wait_for_dt_time);
    int setWaitTimeForCarrier(  int wait_for_cd_time );
    int setPauseTimeForComma(  int pause_time_for_comma );
    int setCDResponseTime(  int carrier_detect_response );
    int setCarrierDisconnectTime(  int disc_timer );
    int setTouchToneDuration(  int dial_speed );
    int setEscapeCodeGuardTime(  int escape_guard_time );
    int getUARTStatus(  );
    int getOptionRegister(  );
    int getFlagRegister(  );
    int goOnline(  );
    int setDialingMethod(  int method );
    int dial(  char  *digit_string );
    int repeatLastCommand(  );
    int dialInAnswerMode(  char  *digits );
    int dialAndReturnToCommandMode(  char  *digit_string );
    int answer(  );
    int setCarrier(  int on_off );
    int setEchoMode(  int on_off );
    int setFullDuplexMode(  int full_or_half );
    int setHookSwitch(  int on_or_off_hook );
    int setSpeaker(  int speaker_control );
    int returnNoResultCodes(  int on_or_off );
    int selectExtendedResultCodes(  int extended_codes );
    int setVerboseMode(  int on_of_off );
    int sendString(  const char  *string );
    int sendStringNoWait(  const char  *string, int termination_sequence );
    void setUpEchoRoutine( CHAR_PRINTER character_printer );
    void setUpAbortKey( unsigned int key );
    void waitForOK( long milliseconds, char  *match_string );
    void fixedDelay( long milliseconds );
    long inputLine(  long milliseconds, char  *buffer, int length );
    int writeString(  const char *string, char termination_sequence );

};
#endif // #ifndef __PORT_MODEM_H__
