/*-----------------------------------------------------------------------------*
*
* File:   port_modem
*
* Date:   12/5/2002
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.13 $
* DATE         :  $Date: 2005/02/10 23:24:02 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "numstr.h"
#include "port_modem.h"


static int abortFunction( CtiPort *port )
{
    // Can add code here to let certain long lived modem functions abort out of things.
    return NORMAL;
}

static int PortKillTime( CtiPort *port, int millisleep )
{
    Sleep(millisleep);
    return NORMAL;
}

CtiHayesModem::CtiHayesModem() :
    _status(0),
    _delay_value(0),
    _match_string("OK"),
    _character_printer(NULL)
{
}

CtiHayesModem::~CtiHayesModem()
{
}


CtiHayesModem& CtiHayesModem::setPort( CtiPort *port )
{
    _port = port;

    return *this;
}

/*
 *  int hm3( CtiPort *port, char *s, int ctrl )
 *
 *  ARGUMENTS
 *
 *   CtiPort *port : The port the modem is attached to.
 *
 *   char *s    : A command string, typically something like "ATF".
 *
 *   int ctrl   : A numeric parameter.
 *
 *  DESCRIPTION
 *
 *   This is an internal utility function used by lots of the modem
 *   functions.  It is used to send a string to the modem, followed
 *   by a single numeric parameter.  It uses sendString() to output
 *   the string it builds up, meaning it might execute a fixed delay,
 *   or it might wait for an OK response.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   An error code.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int CtiHayesModem::hm3(  char *s, int ctrl )  /* Tag: Modem private */
{
    RWCString buf(s);
    int error_code;

    buf += CtiNumStr(ctrl);
    error_code = sendString( buf );

    if( error_code >= NORMAL )
        return( NORMAL );
    else
        return( error_code );
}

/*
 *  int reset(  )
 *
 *  ARGUMENTS
 *
 *    : The _port the modem is attached to.
 *
 *  DESCRIPTION
 *
 *   This function sends an ATZ to the modem, and performs whatever sort
 *   of wait is currently specified.  This has the effect of resetting
 *   the modem to its stored reset state.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   An error code.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int CtiHayesModem::reset(  )  /* Tag: Modem public */
{
    int error_code;

    error_code = sendString( "ATZ" );
    if( error_code >= NORMAL )
        return( NORMAL );
    else
        return( error_code );
}


/*
 *  int setRegister(  int num, int val )
 *
 *  ARGUMENTS
 *
 *    : The _port the modem is attached to.
 *
 *   int num    : The modem S register to set.
 *
 *   int val    : The new value to store in the S register.
 *
 *  DESCRIPTION
 *
 *   This function sends an ATSn=v string to the modem, which should
 *   set register n to the new value v.  It performs whatever sort
 *   of post-output delay has been setup for the modem.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   An error code.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int CtiHayesModem::setRegister(  int num, int val )  /* Tag: Modem public */
{
    RWCString str("ATS");
    int error_code;

    str += CtiNumStr(num) + "=" + CtiNumStr(val);
    error_code = sendString( str.data() );

    if( error_code >= NORMAL )
        return( NORMAL );
    else
        return( error_code );
}


/*
 *   int setAutoAnswerRingCount(  int num )
 *
 *  ARGUMENTS
 *
 *    : The _port the modem is attached to.
 *
 *   int num    : The new auto answer ring count
 *
 *  DESCRIPTION
 *
 *   This function sends an ATS0=vnstring to the modem, which should
 *   set the auto answer ring count in register 0 to a new value.
 *   Note that if the value is 0 the modem will not answer any incoming
 *   calls.  This function is lazy and actually relies on the set
 *   register function to do all the work.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   An error code.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int CtiHayesModem::setAutoAnswerRingCount(  int num )  /* Tag: Modem public */
{
    return( setRegister( 0, num ) );
}


/*
 *  int getIncomingRingCount(  )
 *
 *  ARGUMENTS
 *
 *    : The _port the modem is attached to.
 *
 *  DESCRIPTION
 *
 *   This function sends an ATS1? to the modem, which reads the value
 *   in register S1 back.  This function actually interprets the
 *   value that comes back so the application program will be able
 *   to read it directly.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   A Greenleaf status code if an error occured.  Otherwise it returns
 *   the numeric value in register 1.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int CtiHayesModem::getIncomingRingCount(  )  /* Tag: Modem public */
{
    return( getRegister( 1 ) );
}


/*
 *   int setEscapeCode(  char val )
 *
 *  ARGUMENTS
 *
 *    : The _port the modem is attached to.
 *
 *   char val   : The new escape code.
 *
 *  DESCRIPTION
 *
 *   This function sends an ATS2=v string to the modem, which should
 *   set the escape code to a new value.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   An error code.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int CtiHayesModem::setEscapeCode(  char val )  /* Tag: Modem public */
{
    return( setRegister( 2, val ) );
}


/*
 *  int setEndOfLineCharacter(  char val )
 *
 *  ARGUMENTS
 *
 *    : The _port the modem is attached to.
 *
 *   char val   : The new end of line character
 *
 *  DESCRIPTION
 *
 *   This function sends an ATS3=v string to the modem, which should
 *   set the end of line character to a new value.  It isn't entirely
 *   clear why you would ever want to do this, but if you do, we have
 *   the function to take care of it.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   An error code.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int CtiHayesModem::setEndOfLineCharacter(  char val )  /* Tag: Modem public */
{
    return( setRegister( 3, val ) );
}


/*
 *  int setLineFeedCharacter(  char val )
 *
 *  ARGUMENTS
 *
 *    : The _port the modem is attached to.
 *
 *   char val   : The new line feed character.
 *
 *  DESCRIPTION
 *
 *   This function sends an ATS4=v string to the modem, which should
 *   set the line feed character to a new value.  It isn't entirely
 *   clear why you would ever want to do this, but if you do, we have
 *   the function to take care of it.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   An error code.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int  CtiHayesModem::setLineFeedCharacter(  char val )  /* Tag: Modem public */
{
    return( setRegister( 4, val ) );
}


/*
 *  int setBackspaceCharacter(  char val )
 *
 *  ARGUMENTS
 *
 *    : The _port the modem is attached to.
 *
 *   char val   : The backspace character.
 *
 *  DESCRIPTION
 *
 *   This function sends an ATS5=v string to the modem, which should
 *   set the backspace character to a new value.  It isn't entirely
 *   clear why you would ever want to do this, but if you do, we have
 *   the function to take care of it.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   An error code.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int  CtiHayesModem::setBackspaceCharacter(  char val )  /* Tag: Modem public */
{
    return( setRegister( 5, val ) );
}


/*
 *  int setWaitForDialToneTime(  int val )
 *
 *  ARGUMENTS
 *
 *    : The _port the modem is attached to.
 *
 *   int val    : The new wait for dial tone time.
 *
 *  DESCRIPTION
 *
 *   This function sends an ATS6=v string to the modem, which should
 *   set the wait for dial tone time.  The routine performs the normal
 *   delay function after setting the register.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   An error code.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int  CtiHayesModem::setWaitForDialToneTime(  int val )  /* Tag: Modem public */
{
    return( setRegister( 6, val ) );
}


/*
 *  int setWaitTimeForCarrier(  int val )
 *
 *  ARGUMENTS
 *
 *    : The _port the modem is attached to.
 *
 *   int val    : The new wait for carrier time.
 *
 *  DESCRIPTION
 *
 *   This function sends an ATS7=v string to the modem, which should
 *   set the wait time for carrier.  The routine performs the normal
 *   delay function after setting the register.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   An error code.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int  CtiHayesModem::setWaitTimeForCarrier(  int val )  /* Tag: Modem public */
{
    return( setRegister( 7, val ) );
}


/*
 *  int setPauseTimeForComma(  int val )
 *
 *  ARGUMENTS
 *
 *    : The _port the modem is attached to.
 *
 *   int val    : The new pause time for a comma.
 *
 *  DESCRIPTION
 *
 *   This function sends an ATS8=v string to the modem, which should
 *   set the pause time for a comma in a dialing string.  The routine
 *   performs the normal  delay function after setting the register.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   An error code.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int  CtiHayesModem::setPauseTimeForComma(  int val )  /* Tag: Modem public */
{
    return( setRegister( 8, val ) );
}


/*
 *  int setCDResponseTime(  int val )
 *
 *  ARGUMENTS
 *
 *    : The _port the modem is attached to.
 *
 *   int val    : The new Carrier Detect respons time.
 *
 *  DESCRIPTION
 *
 *   This function sends an ATS9=v string to the modem, which should
 *   set the wait for Carrier response time.  The function then
 *   performs the normal delay after setting the register.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   An error code.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int  CtiHayesModem::setCDResponseTime(  int val )  /* Tag: Modem public */
{
    return( setRegister( 9, val ) );
}


/*
 *  int setCarrierDisconnectTime(  int val )
 *
 *  ARGUMENTS
 *
 *    : The _port the modem is attached to.
 *
 *   int val    : The new carrier disconnect time.
 *
 *  DESCRIPTION
 *
 *   This function sends an ATS10=v string to the modem, which should
 *   set the Carrier Disconnect time.  The function then
 *   performs the normal delay after setting the register.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   An error code.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int  CtiHayesModem::setCarrierDisconnectTime(  int val )  /* Tag: Modem public */
{
    return( setRegister( 10, val ) );
}


/*
 *  int setTouchToneDuration(  int val )
 *
 *  ARGUMENTS
 *
 *    : The _port the modem is attached to.
 *
 *   int val    : The new touch tone duration.
 *
 *  DESCRIPTION
 *
 *   This function sends an ATS11=v string to the modem, which should
 *   set the touch tone time.  The function then performs the normal
 *   delay after setting the register.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   An error code.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int  CtiHayesModem::setTouchToneDuration(  int val )  /* Tag: Modem public */
{
    return( setRegister( 11, val ) );
}


/*
 *  int setEscapeCodeGuardTime(  int val )
 *
 *  ARGUMENTS
 *
 *    : The _port the modem is attached to.
 *
 *   int val    : The new escape code guard time.
 *
 *  DESCRIPTION
 *
 *   This function sends an ATS12=v string to the modem, which should
 *   set the escape code guard time.  The function then performs the normal
 *   delay after setting the register.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   An error code.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int  CtiHayesModem::setEscapeCodeGuardTime(  int val )  /* Tag: Modem public */
{
    return( setRegister( 12, val ) );
}

/*
 *   int getUARTStatus(  )
 *
 *  ARGUMENTS
 *
 *    : The _port the modem is attached to.
 *
 *  DESCRIPTION
 *
 *   This function sends an ATS13? string to the modem, which should
 *   request the UART status from register 13.  The function then reads
 *   the numeric value back from the modem, if possible.  This value
 *   is hardware specific and not _portable.  I don't know of any good reason
 *   to use it, but maybe there is one.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   The UART status, if no error occurred.  If an error occurred when
 *   trying to read the value back, a Greenleaf status code less than
 *   0 will be returned.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int  CtiHayesModem::getUARTStatus(  )  /* Tag: Modem public */
{
    return( getRegister( 13 ) );
}

/*
 *   int getOptionRegister(  )
 *
 *  ARGUMENTS
 *
 *    : The _port the modem is attached to.
 *
 *  DESCRIPTION
 *
 *   This function sends an ATS14? string to the modem, which should
 *   request the option register setting from register 14.  The function
 *   then reads the numeric value back from the modem, if possible.  This
 *   value is manufacturer specific and not _portable.  I don't know of any
 *   good reason to use it, but maybe there is one.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   The modem option register, if no error occurred.  If an error occurred
 *   when trying to read the value back, a Greenleaf status code less than
 *   0 will be returned.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int  CtiHayesModem::getOptionRegister(  )  /* Tag: Modem public */
{
    return( getRegister( 14 ) );
}

/*
 *   int getFlagRegister(  )
 *
 *  ARGUMENTS
 *
 *    : The _port the modem is attached to.
 *
 *  DESCRIPTION
 *
 *   This function sends an ATS15? string to the modem, which should
 *   request the flag register setting from register 15.  The function
 *   then reads the numeric value back from the modem, if possible.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   The modem flag register, if no error occurred.  If an error occurred
 *   when trying to read the value back, a Greenleaf status code less than
 *   0 will be returned.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int  CtiHayesModem::getFlagRegister(  )  /* Tag: Modem public */
{
    return( getRegister( 15 ) );
}


/*
 *   int goOnline(  )
 *
 *  ARGUMENTS
 *
 *    : The _port the modem is attached to.
 *
 *  DESCRIPTION
 *
 *   This is the command sent to the modem to take it out of command
 *   mode and put it back into online mode.  This is one of the commands
 *   where we don't expect to get back an OK message from the modem, so
 *   instead of using sendString(), we have to use sendStringNoWait(),
 *   which returns immediately regardless of a response from the modem.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   An error code.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int  CtiHayesModem::goOnline(  )  /* Tag: Modem public */
{
    int error_code;

    error_code = sendStringNoWait( "ATO", '\r' ) ;
    if( error_code >= NORMAL )
        return( NORMAL );
    else
        return( error_code );
}

#if 0
/*
 *  int setDialingMethod(  int ctrl )
 *
 *  ARGUMENTS
 *
 *    : The _port the modem is attached to.
 *
 *   int ctrl   : The new dialing method.  The three options to be
 *        used here are the constants TOUCH_TONE, PULSE, and
 *        DEFAULT_DIALING_METHOD.  These three options determine
 *        which letter will follow the "ATD" part of the dialing
 *        string.  The letter will either be "T", "P", or nothing.
 *        The dialing method gets stored in the _port structure
 *        so that it is assigned on an individual basis for each
 *        _port.
 *
 *  DESCRIPTION
 *
 *   This command sets the dialing method for a given _port.  See the
 *   description of the ctrl parameter above for an explanation of how
 *   it works.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   An error code.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int  CtiHayesModem::setDialingMethod(  int ctrl )  /* Tag: Modem public */
{
    if( _port == NULL )
        return( ASINVPAR );
    if( ctrl < TOUCH_TONE || ctrl > DEFAULT_DIALING_METHOD )
        return( _status = ASINVPAR );
    _port->dialing_method = ctrl;
    return( NORMAL );
}
#endif

/*
 *  int dial(  char *s )
 *
 *  ARGUMENTS
 *
 *    : The _port the modem is attached to.
 *
 *   char *s    : A string that holds the number to be dialed.
 *
 *  DESCRIPTION
 *
 *   This command is used to dial a number via the modem.  One of the
 *   three dialing methods is selected, and the number is sent out using
 *   the "ATD" string.  Since we can't expect to get an "OK" message back,
 *   the number is sent using sendStringNoWait(), meaning we return
 *   right away without any delay.  Note that *all* interpretation of
 *   special characters in the dialing string is going to be done by
 *   the modem, not by any functions in CommLib.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   An error code.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int  CtiHayesModem::dial(  char *s)  /* Tag: Modem public */
{
    RWCString str("ATDT");
    int error_code;

    str += RWCString( s );

    error_code = sendStringNoWait( str.data(), '\r' );

    if( error_code >= NORMAL )
        return( NORMAL );
    else
        return( error_code );
}


/*
 *  int repeatLastCommand(  )
 *
 *  ARGUMENTS
 *
 *    : The _port the modem is attached to.
 *
 *  DESCRIPTION
 *
 *   This function sends the "A/" string, which instructs the modem
 *   to repeat the last command.  Since we have no idea what the last
 *   command does, this string goes out with sendStringNoWait(),
 *   which means we don't execute any delay or wait for an "OK"
 *   response.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   An error code.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int  CtiHayesModem::repeatLastCommand(  )  /* Tag: Modem public */
{
    int error_code;

    error_code = sendStringNoWait( "A/", -1 );
    if( error_code >= NORMAL )
        return( NORMAL );
    else
        return( error_code );
}

/*
 *  int dialInAnswerMode(  char *s )
 *
 *  ARGUMENTS
 *
 *    : The _port the modem is attached to.
 *
 *   char *s    : A string that holds the number to be dialed.
 *
 *  DESCRIPTION
 *
 *   This function is used to cause the modem to dial in answer mode.
 *   This is accomplished by simply appending an "R" to the dial string.
 *   Other than that, it operates identically to dial().
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   An error code.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int  CtiHayesModem::dialInAnswerMode(  char *s )  /* Tag: Modem public */
{
    RWCString str( "ATDT" );
    int error_code;

    str += RWCString( s ) + RWCString( "R" );
    error_code = sendStringNoWait( str, '\r' );

    if( error_code >= NORMAL )
        return( NORMAL );
    else
        return( error_code );
}

/*
 *  int dialAndReturnToCommandMode(  char *s )
 *
 *  ARGUMENTS
 *
 *    : The _port the modem is attached to.
 *
 *   char *s    : A string that holds the number to be dialed.
 *
 *  DESCRIPTION
 *
 *   This function is used to cause the modem to dial and return to
 *   command mode.  This is accomplished by appending a semicolon to
 *   the dial string.  Instead of waiting for a connection, the modem
 *   will dial the number and return to command mode.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   An error code.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int  CtiHayesModem::dialAndReturnToCommandMode(  char *s )  /* Tag: Modem public */
{
    RWCString str( "ATDT" );
    int error_code;

    str += RWCString( s ) + RWCString( ";" );
    error_code = sendStringNoWait( str, '\r' );

    if( error_code >= NORMAL )
        return( NORMAL );
    else
        return( error_code );
}


/*
 *  int answer(  )
 *
 *  ARGUMENTS
 *
 *    : The _port the modem is attached to.
 *
 *  DESCRIPTION
 *
 *   This function sends the "ATA" command to the modem, which causes
 *   it to go off hook and begin trying to make a connection.  Since
 *   the modem won't return an "OK" message in response to this, this
 *   command goes out with sendStringNoWait(), and no delay of any
 *   sort is executed after the string is sent.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   An error code.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int  CtiHayesModem::answer(  )  /* Tag: Modem public */
{
    int error_code;

    error_code = sendStringNoWait( "ATA", '\r' );
    if( error_code >= NORMAL )
        return( NORMAL );
    else
        return( error_code );
}


/*
 *  int setCarrier(  int ctrl )
 *
 *  ARGUMENTS
 *
 *    : The _port the modem is attached to.
 *
 *   int ctrl   : A numeric parameter, use a 0 to turn carrier off, a
 *        1 to turn carrier on.
 *
 *  DESCRIPTION
 *
 *   This function issues an "ATCx" command to the modem.  Don't ever
 *   use this, it has no good reason for existing.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   An error code.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int  CtiHayesModem::setCarrier(  int ctrl )  /* Tag: Modem public */
{
    if( ctrl < 0 || ctrl > 1 )
        return( ASINVPAR );
    else
        return( hm3( "ATC", ctrl ) );
}

/*
 *  int setEchoMode(  int ctrl )
 *
 *  ARGUMENTS
 *
 *    : The _port the modem is attached to.
 *
 *   int ctrl   : A numeric parameter, use a 0 to turn echo off, a
 *        1 to turn carrier on.
 *
 *  DESCRIPTION
 *
 *   This function issues an "ATEx" command to the modem.  This turns
 *   the echo function on or off while in command mode.  A parameter
 *   of 0 is used to turn echo off, a 1 is used to turn echo on.  Most
 *   modems will default to having echo on.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   An error code.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int  CtiHayesModem::setEchoMode(  int ctrl )  /* Tag: Modem public */
{
    if( ctrl < 0 || ctrl > 1 )
        return( ASINVPAR );
    else
        return( hm3( "ATE", ctrl ) );
}

/*
 *  int setFullDuplexMode(  int ctrl )
 *
 *  ARGUMENTS
 *
 *    : The _port the modem is attached to.
 *
 *   int ctrl   : A numeric parameter, use a 0 to set the modem to
 *        half duplex mode.
 *
 *  DESCRIPTION
 *
 *   This function issues an "ATFx" command to the modem.  This turns
 *   "full duplex" mode on or off.  This command doesn't really change
 *   the modem to half or full duplex mode.  What it does change is
 *   whether or not the modem will echo input characters while in connect
 *   mode.  Most modems start off in full duplex mode, meaning they will
 *   not echo characters when in connect mode.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   An error code.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int  CtiHayesModem::setFullDuplexMode(  int ctrl )  /* Tag: Modem public */
{
    if( ctrl < 0 || ctrl > 1 )
        return( ASINVPAR );
    else
        return( hm3( "ATF", ctrl ) );
}

/*
 *  int setHookSwitch(  int ctrl )
 *
 *  ARGUMENTS
 *
 *    : The _port the modem is attached to.
 *
 *   int ctrl   : A numeric parameter, use a 0 to go on hook, and
 *        a 1 to go off hook.
 *
 *  DESCRIPTION
 *
 *   This function issues an "ATHx" command to the modem.  Normally this
 *   command is only used to cause the modem to go on hook when in command
 *   mode.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   An error code.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int  CtiHayesModem::setHookSwitch(  int ctrl )  /* Tag: Modem public */
{
    if( ctrl < 0 || ctrl > 2 )
        return( ASINVPAR );
    else
        return( hm3( "ATH", ctrl ) );
}

/*
 *  int setSpeaker(  int ctrl )
 *
 *  ARGUMENTS
 *
 *    : The _port the modem is attached to.
 *
 *   int ctrl   : A numeric parameter, use a 0 for speaker always off,
 *        a 1 to leave the speaker on until CD.
 *
 *  DESCRIPTION
 *
 *   This function issues an "ATMx" command to the modem.  This is used
 *   to control the mode of operation of the speaker.  A 0 value leaves the
 *   speaker off always.  A 1 means the speaker stays on after dialing
 *   until CD goes high.  A 2 means the speaker is always on when the
 *   modem is off hook.  Other manufacturers will frequently sup_port
 *   additional numeric parameters, check your manual for details.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   An error code.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int  CtiHayesModem::setSpeaker(  int ctrl )  /* Tag: Modem public */
{
    if( ctrl < 0 || ctrl > 2 )
        return( ASINVPAR );
    else
        return( hm3( "ATM", ctrl ) );
}

/*
 *  int returnNoResultCodes(  int ctrl )
 *
 *  ARGUMENTS
 *
 *    : The _port the modem is attached to.
 *
 *   int ctrl   : A numeric parameter, use a 0 for full result codes,
 *        a 1 set "quiet" mode.
 *
 *  DESCRIPTION
 *
 *   This function issues an "ATQx" command to the modem.  This is used
 *   to control "quiet" mode.  A parameter of 1 places the modem in
 *   quiet mode, which means it won't issue result code responses.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   An error code.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int  CtiHayesModem::returnNoResultCodes(  int ctrl )  /* Tag: Modem public */
{
    if( ctrl < 0 || ctrl > 1 )
        return( ASINVPAR );
    else
        return( hm3( "ATQ", ctrl ) );
}

/*
 *  int selectExtendedResultCodes(  int ctrl )
 *
 *  ARGUMENTS
 *
 *    : The _port the modem is attached to.
 *
 *   int ctrl   : A numeric parameter.
 *
 *  DESCRIPTION
 *
 *   This function issues an "ATXx" command to the modem.  The exact
 *   meaning of the single numeric parameter will vary quite a bit
 *   between different modems, so check your manual for details.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   An error code.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int  CtiHayesModem::selectExtendedResultCodes(  int ctrl )  /* Tag: Modem public */
{
    if( ctrl < 0 || ctrl > 1 )
        return( ASINVPAR );
    else
        return( hm3( "ATX", ctrl ) );
}

/*
 *  int setVerboseMode(  int ctrl )
 *
 *  ARGUMENTS
 *
 *    : The _port the modem is attached to.
 *
 *   int ctrl   : A numeric parameter.  A value of 1 indicates verbose
 *        mode, 0 uses numeric responses.
 *  DESCRIPTION
 *
 *   This function issues an "ATVx" command to the modem.  A value of
 *   0 will cause the modem to use numeric responses, which are
 *   usually not very interesting.  A value of 1 engages verbose response
 *   codes, which tend to be more meaningful to human beings.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   An error code.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int  CtiHayesModem::setVerboseMode(  int ctrl )  /* Tag: Modem public */
{
    if( ctrl < 0 || ctrl > 1 )
        return( ASINVPAR );
    else
        return( hm3( "ATV", ctrl ) );
}

/*
 *  void waitForOK( long milliseconds, char *match_string )
 *
 *  ARGUMENTS
 *
 *   long milliseconds  : The amount of time to wait after a command
 *            is sent to the modem for a good response.
 *
 *   char *match_string : The string to watch for.
 *
 *  DESCRIPTION
 *
 * This routine is used to set up the semi-intelligent Hayems modem delay.
 * After every command, the sendString() routine will sit in a loop for the number
 * amount of time specified here.  It will read input characters until if
 * finds one that looks like the match string.  As it reads in the chars,
 * they are printed out using the character printer routine.  If the routine
 * is NULL, they are not printed.  If the match string is null, the default
 * string of "OK" is used.
 *
 *  SIDE EFFECTS
 *
 *   This causes the wait for ok algorithm to be set up globally, not on
 *   a _port by _port basis.
 *
 *  RETURNS
 *
 *   Nothing.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

void  CtiHayesModem::waitForOK( long milliseconds, char *match_string )  /* Tag: Modem public */
{
    if( match_string != NULL )
        _match_string = match_string;
    _delay_value = -milliseconds;
}

/*
 *  void setUpEchoRoutine( void ( *character_printer )( char c ) )
 *
 *  ARGUMENTS
 *
 *   character_printer : A pointer to a function that will be used to
 *           print out characters.
 *
 *  DESCRIPTION
 *
 * When characters are being read in in inputLine, the character printer
 * routine can be used to echo them out.  This is how the character printer
 * routine is set up.
 *
 *  SIDE EFFECTS
 *
 *   This causes the wait for ok algorithm to be set up globally, not on
 *   a _port by _port basis.
 *
 *  RETURNS
 *
 *   Nothing.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

void  CtiHayesModem::setUpEchoRoutine( void (  *character_printer )( char c ) )  /* Tag: Modem public */
{
    _character_printer = character_printer;
}

#if 0
/*
 *  void setUpAbortKey( unsigned int key )
 *
 *  ARGUMENTS
 *
 *   unsigned int key :  The new abort key for inputLine().
 *
 *  DESCRIPTION
 *
 * When characters are being read in in inputLine, the user can hit an abort
 * key and cause an immediate return, with a return value of ASUSERABORT.
 * This is the routine where the abort key can be set up.
 *
 *  SIDE EFFECTS
 *
 *   This causes the abort key to be set up globally, not on
 *   a _port by _port basis.
 *
 *  RETURNS
 *
 *   Nothing.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

void  CtiHayesModem::setUpAbortKey( unsigned int key )  /* Tag: Modem public */
{
    _hm_abort_key = key;
}

#endif

/*
 *  void fixedDelay( long milliseconds )
 *
 *  ARGUMENTS
 *
 *   long milliseconds : The number of milliseconds to delay after any
 *           sendString command.
 *
 *  DESCRIPTION
 *
 *  This routine is called once to establish a fixed delay invoked
 *  after every call to sendString().  The user can set this up
 *  once with a call, and then does not have to bother with a timer
 *  instruction after every hm*() call.  This is one of the two
 *  methods used to make sure that the modem buffer does not
 *  overflow because commands are sent too rapidly.
 *
 *  SIDE EFFECTS
 *
 *   This causes the delay to be set up globally, not on
 *   a _port by _port basis.
 *
 *  RETURNS
 *
 *   Nothing.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */


void  CtiHayesModem::fixedDelay( long milliseconds )  /* Tag: Modem public */
{
    _delay_value = milliseconds;
}

/*
 *  int sendString(  char *string )
 *
 *  ARGUMENTS
 *
 *      :  The _port the modem is attached to.
 *
 *   char *string :  The string to send out to the modem.
 *
 *  DESCRIPTION
 *
 * This function is used internally to send strings to the modem.
 * This routine has two optional delay methods it can use after sending
 * a string to the modem.  If the global delay parameter is set to 0, no
 * delay is performed.  This is compatible with previous versions of the
 * library, and essentially leaves the responsibility for delay up to
 * the programmer.  If the delay parameter is positive, a fixed delay is
 * executed after the command is sent.  If the delay is negative, it is
 * considered to be a timeout value, and while the timeout is not up
 * the program waits for an "OK" string, or a specified user defined
 * string.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   An error code.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int  CtiHayesModem::sendString( const char *string )  /* Tag: Modem public */
{
    long current_time;
    int retvalue;
    char buffer[40];

    retvalue = sendStringNoWait( string, '\r' );
    if( retvalue < NORMAL )
    {
        return( retvalue );
    }
/*
 * If _delay_value is 0, we are going to let the calling program be
 * responsible for modem pacing.
 */
    if( _delay_value == 0 )
    {
        return( retvalue );
    }
/*
 * If _delay_value is greater than 0, then modem pacings consists of
 * a fixed number of milliseconds to be killed after each modem command.
 */
    if( _delay_value > 0 )
    {
        retvalue = PortKillTime( _port, _delay_value );
        return( retvalue );
    }
/*
 * If we fall through to here, it means the modem pacing is going to be
 * the pseudo-intelligent method of waiting for an appropriate answer
 * string.  Note that I still execute a short delay after getting an "OK".
 * Most of the modems we talk to are somewhat brain-damaged, and need
 * lots of extra time to think things over after saying OK.
 */
    current_time = -_delay_value;
    if( _match_string.isNull() )
    {
        return( retvalue );
    }
    for( ; ; )
    {
        current_time = inputLine( current_time, buffer, 40 );
        if( current_time < 0 )
        {
            return( (int) current_time );
        }
        if( current_time == 0 )
        {
            return( ASNOHAYESOK );
        }
        if(_match_string.compareTo(buffer) == 0)
        {
            retvalue = PortKillTime( _port, 500 );
            return( retvalue );
        }
    }
}

/*
 *  int sendStringNoWait(  char *string )
 *
 *  ARGUMENTS
 *
 *      :  The _port the modem is attached to.
 *
 *   char *string :  The string to send out to the modem.
 *
 *  DESCRIPTION
 *
 * Unlike its intelligent sibling, this function doesn't check for
 * an OK messasge after sending a command.  It will also blow off
 * the fixed delay routine if that is what has been set up.  It is
 * the` appropriate function to use to send commands that don't generate
 * OK messages, such as dialing commands.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   An error code.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */


int  CtiHayesModem::sendStringNoWait(  const char *string, int termination_sequence )
{
    int length;
    int saved_status;

    length = writeString( string, termination_sequence );
    if( _status < NORMAL )
        return( _status );

/*
 * Unfortunately, I cannot count on this function always being
 * available.  Drivers that don't let me check for an empty
 * transmit buffer aren't going to be sure when the entire Hayes
 * Modem command string has been sent.  If I call a non-sup_ported
 * function, I run the risk of setting the _status flag to
 * an error condition.  Since I know at this point that the status
 * flag should be NORMAL, I make sure it stays that way, even
 * if the driver doesn't sup_port this handy function.
 */
#if 0
    saved_status = _status;
    while( SpaceUsedInTXBuffer( _port ) > NORMAL )
        ;
    if( _status == ASNOTSUPPORTED )
        _status = saved_status;
#endif

    return( length );
}

/*
 *  int getRegister(  int num )
 *
 *  ARGUMENTS
 *
 *      :  The _port the modem is attached to.
 *
 *   int num      :  The register number to read out of the modem.
 *
 *  DESCRIPTION
 *
 *   This function is used to read the contents of one of the modem's
 *   S registers.  It sends the appropriate command ("ATSxxx?" ) to the
 *   modem, then reads the response back.  The ASCII response gets
 *   converted to a number, and is then returned to the callling
 *   routine.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   A Greenleaf status code, if an error occured.  Otherwise this function
 *   will return the status register value that came back from the modem.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

int  CtiHayesModem::getRegister(  int num )  /* Tag: Modem public */
{
    RWCString buffer( "ATS" );
    int status;
    unsigned char return_value;
    long time_left;
    int i;

    char inbuffer[40];


    buffer += CtiNumStr( num ) + RWCString("?");
    status = sendStringNoWait( buffer.data(), '\r' );

    if( status < 0)
    {
        return( status );
    }
    time_left = 1500L;
    for( ; ; )
    {
        time_left = inputLine( time_left, inbuffer, 40 );
        if( time_left < 0 )
        {  /* An error of some sort */
            return( (int) time_left );
        }
        if( time_left == 0 )
        {
            return( ASNOHAYESRESPONSE );
        }
        return_value = 0;
        for( i = 0 ; i < (int) strlen( inbuffer ) ; i++ )
        {
            if( !isdigit( inbuffer[ i ] ) )
                break;
            return_value *= (char)10;
            return_value = (unsigned char)( return_value + inbuffer[ i ] - (char)'0' );
        }
        if( i > 0 && i == (int) strlen( inbuffer ) ) break;
    }
    status = sendString( "" );

    if( status < 0 )
        return( status );
    return( return_value );
}

/*
 *  long inputLine(  long milliseconds, char *buffer, int length )
 *
 *  ARGUMENTS
 *
 *           :  The _port the modem is attached to.
 *
 *   long milliseconds :  The length of time to wait for a full input line.
 *
 *   char *buffer      :  A pointer to the buffer that will receive the
 *            input line.
 *
 *   int length        :  The maximum number of characters that can be
 *            read into the buffer.
 *
 *  DESCRIPTION
 *
 *   This function is used to read a full line of input back from the
 *   modem.  It expects a line to be terminated by a "\r\n" sequence.
 *   This function is used internally to read lines of input looking
 *   for an OK message.
 *
 *  SIDE EFFECTS
 *
 *   None.
 *
 *  RETURNS
 *
 *   A status code, if an error occured.  Otherwise this function
 *   will return the number of milliseconds left in the timeout.
 *
 *  AUTHOR
 *
 *
 *
 *  MODIFICATIONS
 *
 */

long  CtiHayesModem::inputLine(  long milliseconds, char *buffer, int length )  /* Tag: Modem public */
{
    int i;
    int c;
    long timeout;
    int return_status;
    long return_time;

    timeout = GetTickCount() + milliseconds;
    i = 0;
    length--;
    if( length <= 0 )
        return( ASINVPAR );
    for( ; ; )
    {
        return_status = abortFunction( _port );

        if( return_status < NORMAL )
            break;
        ULONG bytesRead = 0;
        c = 0;
        _port->readPort(&c, 1, 1, &bytesRead);

        if( c >= 0 )
        {
            if( _character_printer != NULL )
                _character_printer( ( char ) c );
            if( c == '\r' )
                break;
            if( c == '\n' )
                continue;
            buffer[ i++ ] = ( char ) c;
            length--;
            if( length <= 0 )
                break;
        }
        else if( bytesRead == 0 )
        {
            Sleep(250);             // FIX FIX FIX
            if( return_status < NORMAL )
                break;
            if( timeout <= GetTickCount() )
                break;
        }
        else
        {
            return_status = c;
            break;
        }
    }
    buffer[ i ] = '\0';

    if( return_status < NORMAL )
        return return_status;
    return_time = timeout - GetTickCount();
    if( return_time < 0 )
        return_time = 0;
    return return_time;
}



/*
 * int writeString( PORT *_port, char *string, int termination_sequence )
 * ARGUMENTS
 *
 * PORT *_port         : A pointer to a generic _port structure.
 *
 * char *string       : A pointer to a null terminated string which will
 *                      be sent out of the _port.
 *
 * int termination_sequence : if >= 0, this is the character to be sent
 *                            out after all of the string characters
 *                            are sent.  If == -2, a CR/LF is to be sent.
 *
 * DESCRIPTION
 *
 * This Level 2 function is called to write a null terminated string
 * out to a _port.  The choice of characters to be used as a terminator
 * during output is up to the caller.
 *
 * SIDE EFFECTS
 *
 *
 * RETURNS
 *
 * A status code, with ASSUCCESS meaning that all characters
 * were transferred.  Note that an error return will be copied into
 * _port->status.  Note also that the count of characters sent, EXCLUDING
 * the terminator(s) will be copied into _port->count.
 *
 * AUTHOR
 *
 *  SM                   March 15, 1991
 *
 * MODIFICATIONS
 *
 */

int CtiHayesModem::writeString(  const char *string, char termination_sequence )
{
    ULONG bcount = 0;

    if( termination_sequence < -2 || termination_sequence > 255)
        return( _status = ASINVPAR );

    _port->writePort((void *)string,  strlen( string ), INFINITE, &bcount);

    if( _status < NORMAL )
        return( _status );

    if( termination_sequence >= 0 )
    {
        _port->writePort( (void*)&termination_sequence, 1, 1, &bcount );
    }
    else if( termination_sequence == -2 )
    {
        _port->writePort( (void*)"\r\n", 2, 1, &bcount );
    }
    return( _status );
}


// known valid modem returns -- Note 0 is the same as OK when not in verbal mode
/* returns TRUE if a valid modem return is found */
bool CtiHayesModem::validModemResponse (PCHAR Response)
{
    int count;
    bool isValid = false;

    static PCHAR responseText[] = {
        "OK",
        "ERROR",
        "BUSY",
        "NO CARRIER",
        "NO DIALTONE",
        "NO ANSWER",
        "NO DIAL TONE",
        NULL};


   for(count = 0; responseText[count] != NULL; count++)
   {
      if( strstr(Response, responseText[count]) != NULL )
      {
         // Valid modem return
         strcpy(Response, responseText[count]);
         isValid = true;
         break;
      }
   }

   if( isValid != true )
   {
      if(!strcmp(Response, "0"))      // Zero is a special case of OK
      {
         // Valid modem return
         strcpy(Response, "OK");
         isValid = true;
      }
   }

   return(isValid);
}
