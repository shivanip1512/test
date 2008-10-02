#pragma warning( disable : 4786 )  
/*****************************************************************************
*
*    FILE NAME: fdrtextimport.cpp
*
*    DATE: 03/11/2004
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrdsm2fileint.cpp-arc  $
*    REVISION     :  $Revision: 1.7 $
*    DATE         :  $Date: 2008/10/02 23:57:15 $
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
      Revision 1.7  2008/10/02 23:57:15  tspar
      YUK-5013 Full FDR reload should not happen with every point

      YUKRV-325  review changes

      Revision 1.6  2008/09/23 15:15:22  tspar
      YUK-5013 Full FDR reload should not happen with every point db change

      Review changes. Most notable is mgr_fdrpoint.cpp now encapsulates CtiSmartMap instead of extending from rtdb.

      Revision 1.5  2008/09/15 21:09:16  tspar
      YUK-5013 Full FDR reload should not happen with every point db change

      Changed interfaces to handle points on an individual basis so they can be added
      and removed by point id.

      Changed the fdr point manager to use smart pointers to help make this transition possible.

      Revision 1.4  2005/12/20 17:17:15  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.2.4.3  2005/08/12 19:53:47  jliu
      Date Time Replaced

      Revision 1.2.4.2  2005/07/27 19:27:58  alauinger
      merged from the head 20050720


      Revision 1.2.4.1  2005/07/12 21:08:38  jliu
      rpStringWithoutCmpParser
      Revision 1.3  2005/06/24 20:08:47  dsutton
      Added support for DSM2's function 2

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
                           CtiTime aTimestamp, 
                           int aQuality,
                           string aTranslationName,
                           CtiMessage **aRetMsg);
    USHORT ForeignToYukonQuality (string aQuality);
    CtiTime ForeignToYukonTime (string aTime);

    bool processFunctionOne (string &aLine, CtiMessage **aRetMsg);
    bool processFunctionTwo (string &aLine, CtiMessage **aRetMsg);

    bool shouldDeleteFileAfterImport() const;
    CtiFDR_Dsm2Filein &setDeleteFileAfterImport (bool aFlag);

    bool useSystemTime() const;
    CtiFDR_Dsm2Filein &setUseSystemTime (bool aFlag);

    bool validateAndDecodeLine( string &input, CtiMessage **aRetMsg);

    void threadFunctionReadFromFile( void );
    virtual bool loadTranslationLists(void);
    virtual bool translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool send=false);

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

