#pragma warning( disable : 4786 )  
/*****************************************************************************
*
*    FILE NAME: fdrtextimport.cpp
*
*    DATE: 03/11/2004
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrdsm2fileint.cpp-arc  $
*    REVISION     :  $Revision: 1.2 $
*    DATE         :  $Date: 2004/03/24 22:38:51 $
*
*
*    AUTHOR: David Sutton
*
*    PURPOSE: Generic text import ascii import
*
*    DESCRIPTION: 
*
*    ---------------------------------------------------
*    History: 
      $Log: fdrdsm2filein.h,v $
      Revision 1.2  2004/03/24 22:38:51  dsutton
      Added a text file interface to FDR to allow Yukon users to import data formated
      for DSM2's filein format.  Revision 1.0

      Revision 1.1.2.1  2004/03/23 22:11:43  dsutton
      Added a text file interface to FDR to allow Yukon users to import data formated
      for DSM2's filein format.  Revision 1.0


****************************************************************************
*/

#ifndef __FDRDSM2FILEIN_H__
#define __FDRDSM2FILEIN_H__


#include <windows.h>    //  NOTE:  if porting this to non-WIN32, make sure to replace this

#include "dlldefs.h"
#include "fdrtextfilebase.h"

class IM_EX_FDRDSM2FILEIN CtiFDR_Dsm2Filein : public CtiFDRTextFileBase
{
    typedef CtiFDRTextFileBase Inherited;

public:
    // constructors and destructors
    CtiFDR_Dsm2Filein(); 

    virtual ~CtiFDR_Dsm2Filein();
    virtual BOOL    init( void );   
    virtual BOOL    run( void );
    virtual BOOL    stop( void );

    int readConfig( void );
    bool buildAndAddPoint (CtiFDRPoint &aPoint, 
                           DOUBLE aValue, 
                           RWTime aTimestamp, 
                           int aQuality,
                           RWCString aTranslationName,
                           CtiMessage **aRetMsg);
    USHORT ForeignToYukonQuality (RWCString aQuality);
    RWTime ForeignToYukonTime (RWCString aTime);

    bool processFunctionOne (RWCString &aLine, CtiMessage **aRetMsg);

    bool shouldDeleteFileAfterImport() const;
    CtiFDR_Dsm2Filein &setDeleteFileAfterImport (bool aFlag);

    bool useSystemTime() const;
    CtiFDR_Dsm2Filein &setUseSystemTime (bool aFlag);

    bool validateAndDecodeLine( RWCString &input, CtiMessage **aRetMsg);

    void threadFunctionReadFromFile( void );
    virtual bool loadTranslationLists(void);
    // ddefine these for each interface type
    static const CHAR * KEY_INTERVAL;
    static const CHAR * KEY_FILENAME;
    static const CHAR * KEY_DRIVE_AND_PATH;
    static const CHAR * KEY_DB_RELOAD_RATE;
    static const CHAR * KEY_QUEUE_FLUSH_RATE;
    static const CHAR * KEY_DELETE_FILE;
    static const CHAR * KEY_USE_SYSTEM_TIME;

private:
    RWThreadFunction    _threadReadFromFile;
    bool                _deleteFileAfterImportFlag;
    bool                _useSystemTime;
};



#endif  //  #ifndef __FDR_STEC_H__

