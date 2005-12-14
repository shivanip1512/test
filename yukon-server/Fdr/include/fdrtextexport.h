#pragma warning( disable : 4786 )  
/*****************************************************************************
*
*    FILE NAME: fdrtextexport.cpp
*
*    DATE: 04/11/2003
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrtextexport.cpp-arc  $
*    REVISION     :  $Revision: 1.3 $
*    DATE         :  $Date: 2005/12/14 16:04:18 $
*
*
*    AUTHOR: David Sutton
*
*    PURPOSE: Generic text export Export
*
*    DESCRIPTION: 
*
*    ---------------------------------------------------
*    History: 
      $Log: fdrtextexport.h,v $
      Revision 1.3  2005/12/14 16:04:18  dsutton
      Added a file format that allows us to integrate to a Survalent SCADA system.
      Format specification is triggered via a CPARM which defaults to our standard output

      Revision 1.2  2003/04/22 20:44:46  dsutton
      Interfaces FDRTextExport and FDRTextImport and all the pieces needed
      to make them compile and work

****************************************************************************
*/

#ifndef __FDRTEXTEXPORT_H__
#define __FDRTEXTEXPORT_H__


#include <windows.h>    //  NOTE:  if porting this to non-WIN32, make sure to replace this

#include "dlldefs.h"
#include "fdrtextfilebase.h"

class IM_EX_FDRTEXTEXPORT CtiFDR_TextExport : public CtiFDRTextFileBase
{
    typedef CtiFDRTextFileBase Inherited;

public:
    // constructors and destructors
    CtiFDR_TextExport(); 

    enum {formatOne=1,
        survalent=100
    };

    virtual ~CtiFDR_TextExport();
    virtual BOOL    init( void );   
    virtual BOOL    run( void );
    virtual BOOL    stop( void );

    int readConfig( void );
    bool sendMessageToForeignSys ( CtiMessage *aMessage );

    RWCString YukonToForeignTime (RWTime aTime);
    CHAR YukonToForeignQuality (USHORT aQuality);
    CHAR YukonToForeignDST (bool aFlag);

    bool shouldAppendToFile() const;
    CtiFDR_TextExport &setAppendToFile (bool aFlag);

    void threadFunctionWriteToFile( void );
    virtual bool loadTranslationLists(void);
    void processPointToSurvalent (FILE* aFilePtr, CtiFDRPoint *aPoint, RWTime aTime);
    // ddefine these for each interface type
    static const CHAR * KEY_INTERVAL;
    static const CHAR * KEY_FILENAME;
    static const CHAR * KEY_DRIVE_AND_PATH;
    static const CHAR * KEY_DB_RELOAD_RATE;
    static const CHAR * KEY_QUEUE_FLUSH_RATE;
    static const CHAR * KEY_APPEND_FILE;
    static const CHAR * KEY_FORMAT;
private:
    RWThreadFunction    _threadWriteToFile;
    bool                _appendFlag;
    int                 _format;
};
#endif

