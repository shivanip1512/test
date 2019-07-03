#pragma once

#include <windows.h>
#include "dlldefs.h"
#include "fdrinterface.h"
#include "string.h"

class __declspec(dllexport) FDRWabash : public CtiFDRInterface
{
        typedef CtiFDRInterface Inherited;
    public:
        DEBUG_INSTRUMENTATION;

        // constructors and destructors
        FDRWabash();
        virtual ~FDRWabash();

        //Initializers
        BOOL init();
        bool readConfig() override;
        bool loadTranslationLists();
        bool translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList = false);
        void resetForInitialLoad();

        //overloads
        /* gets called from fdr anytime a point data msg comes in. */
        void sendMessageToForeignSys( CtiMessage *aMessage );
        int processMessageFromForeignSystem( char* );//should never be called.

        //getter's and setter's
        std::string getFilename();
        std::string getPath();
        void setFilename( std::string );
        void setPath( std::string );

        static const char * KEY_DB_RELOAD;
        static const char * KEY_INITIAL_LOAD;

    private:
        bool writeDataToFile( std::string cmd );

        std::string _fileName;
        std::string _path;
        bool   _writeInitialLoad;

};
