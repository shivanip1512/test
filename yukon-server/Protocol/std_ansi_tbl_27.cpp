#include "precompiled.h"

#include "logger.h"
#include "std_ansi_tbl_27.h"

using std::endl;
using std::string;
//=========================================================================================================================================
//=========================================================================================================================================
CtiAnsiTable27::CtiAnsiTable27( BYTE *dataBlob, UINT8 nbrPresentDemands, UINT8 nbrPresentValues )
{
    _nbrPresentDemands = nbrPresentDemands;
    _nbrPresentValues = nbrPresentValues;

    _presentDemandSelect = new unsigned char[_nbrPresentDemands];
    for (int i = 0; i < _nbrPresentDemands; i++)
    {
        dataBlob += toAnsiIntParser(dataBlob, &_presentDemandSelect[i], sizeof( unsigned char ));
    }
    _presentValueSelect = new unsigned char[_nbrPresentValues];
    for (int i = 0; i < _nbrPresentValues; i++)
    {
        dataBlob += toAnsiIntParser(dataBlob, &_presentValueSelect[i], sizeof( unsigned char ));
    }

}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable27::~CtiAnsiTable27()
{
     if (_presentDemandSelect != NULL)
     {
         delete []_presentDemandSelect;
         _presentDemandSelect = NULL;
     }
     if (_presentValueSelect != NULL)
     {
         delete []_presentValueSelect;
         _presentValueSelect = NULL;
     }

}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable27& CtiAnsiTable27::operator=(const CtiAnsiTable27& aRef)
{
   if(this != &aRef)
   {
   }

   return *this;
}

//=========================================================================================================================================
//=========================================================================================================================================

unsigned char* CtiAnsiTable27::getDemandSelect( )
{
    if (_presentDemandSelect != NULL)
    {
        return _presentDemandSelect;
    }
    else
        return NULL;
}

unsigned char* CtiAnsiTable27::getValueSelect(  )
{
    if (_presentDemandSelect != NULL)
    {
        return _presentValueSelect;
    }
    else
        return NULL;
}


//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable27::printResult( const string& deviceName )
{
    Cti::FormattedList itemList;

    Cti::StreamBufferSink& demands = itemList.add("Demand Src Indices");
    for( int index = 0; index < _nbrPresentDemands; index++ )
    {
        demands << _presentDemandSelect[index] <<"  ";
    }

    Cti::StreamBufferSink& values = itemList.add("Value Src Indices");
    for( int index = 0; index < _nbrPresentValues; index++ )
    {
        values << _presentValueSelect[index] <<"  ";
    }

    CTILOG_INFO(dout,
            endl << formatTableName(deviceName +" Std Table 27") <<
            endl <<"** Present Register Select **"<<
            itemList
            );
}
