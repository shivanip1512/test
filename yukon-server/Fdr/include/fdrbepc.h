#pragma warning( disable : 4786 )  
/*****************************************************************************
*
*    FILE NAME: fdrbepc.h
*
*    DATE: 04/11/2003
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrbepc.h-arc  $
*    REVISION     :  $Revision: 1.2 $
*    DATE         :  $Date: 2005/06/24 20:08:00 $
*
*
*    AUTHOR: David Sutton
*
*    PURPOSE: Text file export for Basin Electric Public Coop members
*
*    DESCRIPTION: 
*
*    ---------------------------------------------------
*    History: 
      $Log: fdrbepc.h,v $
      Revision 1.2  2005/06/24 20:08:00  dsutton
      New interface for KEM electric to send information to their
      power supplier Basin Electric


****************************************************************************
*/

#ifndef __FDRBEPC_H__
#define __FDRBEPC_H__


#include <windows.h>    //  NOTE:  if porting this to non-WIN32, make sure to replace this

#include "dlldefs.h"
#include "fdrtextfilebase.h"

class IM_EX_FDRBEPC CtiFDR_BEPC : public CtiFDRTextFileBase
{
    typedef CtiFDRTextFileBase Inherited;

public:
    // constructors and destructors
    CtiFDR_BEPC(); 

    virtual ~CtiFDR_BEPC();
    virtual BOOL    init( void );   
    virtual BOOL    run( void );
    virtual BOOL    stop( void );

    int readConfig( void );
    bool sendMessageToForeignSys ( CtiMessage *aMessage );

    RWCString YukonToForeignTime (RWTime aTime);
    CHAR YukonToForeignQuality (USHORT aQuality);
    CHAR YukonToForeignDST (bool aFlag);

    bool shouldAppendToFile() const;
    CtiFDR_BEPC &setAppendToFile (bool aFlag);

    RWCString & getCoopID();
    RWCString  getCoopID() const;
    CtiFDR_BEPC &setCoopID (RWCString aID);

    void threadFunctionWriteToFile( void );
    virtual bool loadTranslationLists(void);
    // ddefine these for each interface type
    static const CHAR * KEY_INTERVAL;
    static const CHAR * KEY_FILENAME;
    static const CHAR * KEY_DRIVE_AND_PATH;
    static const CHAR * KEY_DB_RELOAD_RATE;
    static const CHAR * KEY_QUEUE_FLUSH_RATE;
    static const CHAR * KEY_APPEND_FILE;
    static const CHAR * KEY_COOP_ID;

    static const CHAR * KEY_TOTAL_LOAD_KW;


private:
    RWThreadFunction    _threadWriteToFile;
    bool                _appendFlag;
    RWCString           _coopid;
};

#endif


