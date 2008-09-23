#pragma warning( disable : 4786 )  
/*****************************************************************************
*
*    FILE NAME: fdrbepc.h
*
*    DATE: 04/11/2003
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrbepc.h-arc  $
*    REVISION     :  $Revision: 1.5 $
*    DATE         :  $Date: 2008/09/23 15:15:22 $
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
      Revision 1.5  2008/09/23 15:15:22  tspar
      YUK-5013 Full FDR reload should not happen with every point db change

      Review changes. Most notable is mgr_fdrpoint.cpp now encapsulates CtiSmartMap instead of extending from rtdb.

      Revision 1.4  2008/09/15 21:09:16  tspar
      YUK-5013 Full FDR reload should not happen with every point db change

      Changed interfaces to handle points on an individual basis so they can be added
      and removed by point id.

      Changed the fdr point manager to use smart pointers to help make this transition possible.

      Revision 1.3  2005/12/20 17:17:15  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.2.2.2  2005/08/12 19:53:47  jliu
      Date Time Replaced

      Revision 1.2.2.1  2005/07/27 19:27:58  alauinger
      merged from the head 20050720
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

    string YukonToForeignTime (CtiTime aTime);
    CHAR YukonToForeignQuality (USHORT aQuality);
    CHAR YukonToForeignDST (bool aFlag);

    bool shouldAppendToFile() const;
    CtiFDR_BEPC &setAppendToFile (bool aFlag);

    string & getCoopID();
    string  getCoopID() const;
    CtiFDR_BEPC &setCoopID (string aID);

    void threadFunctionWriteToFile( void );
    virtual bool loadTranslationLists(void);
    virtual bool translateSinglePoint(CtiFDRPointSPtr translationPoint, bool send=false);

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
    string           _coopid;
};

#endif


