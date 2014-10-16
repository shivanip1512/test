#include "precompiled.h"


#include "guard.h"
#include "logger.h"
#include "pointdefs.h"
#include "prot_ansi.h"
#include "utility.h"
#include "ctitime.h"
#include "ctidate.h"
#include "cparms.h"

using std::endl;
using std::string;
using std::list;

using namespace Cti::Protocols::Ansi;
const CHAR * CtiProtocolANSI::METER_TIME_TOLERANCE = "PORTER_SENTINEL_TIME_TOLERANCE";
//const CHAR * CtiProtocolANSI::ANSI_DEBUGLEVEL = "ANSI_DEBUGLEVEL";
//========================================================================================================================================
//=========================================================================================================================================

CtiProtocolANSI::CtiProtocolANSI()
{
   _index = 0;
   _bytesInGot = 0;

   _validFlag = false;
   _previewTable64 = false;
   _entireTableFlag = false;
   _clearMfgTables = false;
   _nbrLPDataBlksWanted = 0;
   _nbrLPDataBlkIntvlsWanted = 0;
   _nbrFirstLPDataBlkIntvlsWanted = 0;
   _nbrLastLPDataBlkIntvlsWanted = 0;
   _timeSinceLastLPTime = 0;

   _seqNbr = 0;

   _lpNbrLoadProfileChannels = 0;
   _lpNbrIntvlsLastBlock = 0;
   _lpNbrValidBlks = 0;
   _lpLastBlockIndex = 0;
   _lpNbrIntvlsPerBlock = 0;
   _lpNbrBlksSet = 0;
   _lpMaxIntervalTime = 0;
   _lpStartBlockIndex = 0;
   _lpBlockSize = 0;
   _lpOffset = 0;
   _lpNbrFullBlocks = 0;
   _lpLastBlockSize = 0;
   _ansiAbortOperation = FALSE;

   _stdTblsAvailable.push_back(0);

   _currentTableNotAvailableFlag = false;
   _requestingBatteryLifeFlag = false;
   _forceProcessDispatchMsg = false;

   _scanOperation = generalScan;
   _parseFlags = 0;

}

//=========================================================================================================================================
//FIXME - I'm very broken....
//=========================================================================================================================================

void CtiProtocolANSI::destroyMe( void )
{
    CTILOG_INFO(dout, "Ansi destroy started----");

    _header.reset();
    _tables.clear();

    _table00.reset();
    _table01.reset();
    _table08.reset();
    _table10.reset();
    _table11.reset();
    _table12.reset();
    _table13.reset();
    _table14.reset();
    _table15.reset();
    _table16.reset();
    _table21.reset();
    _table22.reset();
    _table23.reset();
    _table27.reset();
    _table28.reset();
    _table31.reset();
    _table32.reset();
    _table33.reset();
    _table51.reset();
    _table52.reset();
    _table61.reset();
    _table62.reset();
    _table63.reset();
    _table64.reset();
    _frozenRegTable.reset();

    _lpValues .clear();
    _lpTimes  .clear();
    _lpQuality.clear();

    CTILOG_INFO(dout, "----Ansi destroy finished");
}


//=========================================================================================================================================
//=========================================================================================================================================

CtiProtocolANSI::~CtiProtocolANSI()
{
   destroyMe();
}

//=========================================================================================================================================
//gets us back to our original state and cleans stuff up
//=========================================================================================================================================

void CtiProtocolANSI::reinitialize( void )
{
    CTILOG_INFO(dout, "Ansi reinit started----");

    try
    {
        getApplicationLayer().reinitialize();
        destroyManufacturerTables();
        destroyMe();

        _lpNbrLoadProfileChannels = 0;
        _lpNbrIntvlsLastBlock = 0;
        _lpNbrValidBlks = 0;
        _lpLastBlockIndex = 0;
        _lpNbrIntvlsPerBlock = 0;
        _lpNbrBlksSet = 0;
        _lpMaxIntervalTime = 0;
        _lpStartBlockIndex = 0;
        _lpBlockSize = 0;
        _lpOffset = 0;
        _lpNbrFullBlocks = 0;
        _lpLastBlockSize = 0;
        _currentTableNotAvailableFlag = false;
        _ansiAbortOperation = false;

        if (!_stdTblsAvailable.empty())
        {
            _stdTblsAvailable.clear();
            _stdTblsAvailable.push_back(0);
        }

        if (!_mfgTblsAvailable.empty())
        {
            _mfgTblsAvailable.clear();
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);

        autopsy(__FILE__, __LINE__);
    }

    CTILOG_INFO(dout, "Ansi reinit finished");
}

//=========================================================================================================================================
//porter side:
//we take the outmessage that we assembled above on the scanner side and start it on it's journey through the layers of the ansi protocol
//=========================================================================================================================================

int CtiProtocolANSI::recvOutbound( OUTMESS *OutMessage )
{
    try
    {
        // set our table index to zero
       _index = 0;
       buildWantedTableList (OutMessage->Buffer.OutMessage);

       // init the application layer
       if( _header->numTablesRequested )
       {
           // prime the application layer with the first table request
           getApplicationLayer().init();
           getApplicationLayer().initializeTableRequest (_tables[_index].tableID,
                                                _tables[_index].tableOffset,
                                                _tables[_index].bytesExpected,
                                                _tables[_index].type,
                                                _tables[_index].operation);
       }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);

        autopsy(__FILE__, __LINE__);
    }

    return( getScanOperation() == loopBack ? 1 : _header->numTablesRequested );   //just a val
}

// needed on both sides

void CtiProtocolANSI::buildWantedTableList( BYTE *aPtr )
{
    try
    {
        BYTE *bufptr = aPtr;
        int sizeOfPswd = 20;

        _header.reset(new WANTS_HEADER);

        memcpy( ( void *)_header.get(), bufptr, sizeof( WANTS_HEADER ) );
        bufptr += sizeof( WANTS_HEADER );

        getApplicationLayer().setPassword(bufptr);
        bufptr += sizeOfPswd;

        _tables.clear();
        _tables.resize(_header->numTablesRequested);

        for( int x = 0; x < _header->numTablesRequested; x++ )
        {
            memcpy( ( void *)&_tables[x], bufptr, sizeof( ANSI_TABLE_WANTS ));
            bufptr += sizeof( ANSI_TABLE_WANTS );
        }

        _scanOperation = ( CtiProtocolANSI::AnsiScanOperation ) *bufptr;
        bufptr += 1;

        memcpy ((void *)&_parseFlags, bufptr, sizeof(UINT));
        bufptr += sizeof(UINT);

        setAnsiDeviceType();
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);

        autopsy(__FILE__, __LINE__);
    }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiANSIApplication &CtiProtocolANSI::getApplicationLayer( void )
{
   return _appLayer;
}

//=========================================================================================================================================
//porter side:
//pass our xfer message to whomever can make it look ansi-like
//=========================================================================================================================================

bool CtiProtocolANSI::generate( CtiXfer &xfer )
{
    if (!_currentTableNotAvailableFlag)
    {
        return (getApplicationLayer().generate( xfer ));
    }
    else
        return false;
}

//=========================================================================================================================================
//here's where we'll decide what we got back from the port
//=========================================================================================================================================

bool CtiProtocolANSI::decode( CtiXfer &xfer, int status )
{
    try
    {

        bool  done = false;

        done = getApplicationLayer().decode( xfer, status );

        if( getApplicationLayer().isTableComplete() || getApplicationLayer().getPartialProcessLPDataFlag())
        {
            if ((_tables[_index].type == ANSI_TABLE_TYPE_STANDARD && isStdTableAvailableInMeter(_tables[_index].tableID)) ||
                (_tables[_index].type == ANSI_TABLE_TYPE_MANUFACTURER && isMfgTableAvailableInMeter(_tables[_index].tableID - 0x0800)))
            {
                if (_tables[_index].operation != ANSI_OPERATION_READ)
                {
                    if (handleWriteOperations())
                    {
                        return true;
                    }
                }
                else
                {
                    convertToTable();
                }

                if (createWriteOperations())
                {
                    return true;
                }


               try
               {
                   if (_tables[_index].tableID == LoadProfileStatus  && _table63)
                   {
                       if ( !setLoadProfileVariables() )
                           return true;
                   }
               }
               catch( ... )
               {
                   CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);

                   autopsy(__FILE__, __LINE__);

                   _ansiAbortOperation = TRUE;
                   _index = _header->numTablesRequested - 1;
                   getApplicationLayer().terminateSession();
                   return true;
               }
           }
           // anything else to do
           do
           {
               _index++;
               if (_index >= _header->numTablesRequested || _tables[_index].tableID == -1)
               {
                   if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )//DEBUGLEVEL_LUDICROUS )
                   {
                       printResults();
                   }
                    // done with tables, do the termination etc
                    getApplicationLayer().terminateSession();
                    return( done ); //just a val
               }
               updateBytesExpected ();
           } while( _currentTableNotAvailableFlag );
           prepareApplicationLayer();
        }

        return( done ); //just a val
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);

        autopsy(__FILE__, __LINE__);

        _ansiAbortOperation = TRUE;
        _index = _header->numTablesRequested - 1;
        getApplicationLayer().terminateSession();

    }
    return false;

}

void CtiProtocolANSI::prepareApplicationLayer()
{
    if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_ACTIVITY_INFO) )
    {
        CTILOG_DEBUG(dout, "Inside function");
    }

    if (_tables[_index].tableID == Constants ||
        _tables[_index].tableID == CurrentRegisterData ||
        _tables[_index].tableID == FrozenRegisterData)
    {
        _tables[_index].tableOffset = 0;
         getApplicationLayer().setCurrentTableSize(_tables[_index].bytesExpected);
    }
    else if (_tables[_index].tableID >= LoadProfileDataSet1 && _tables[_index].tableID <= LoadProfileDataSet4)
    {
        getApplicationLayer().setCurrentTableSize( _table61->getLPMemoryLength());
        _tables[_index].tableOffset = _lpOffset;

    }
    else
    {
        _tables[_index].tableOffset = 0;
        getApplicationLayer().setCurrentTableSize(1024);
    }

    if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_ACTIVITY_INFO) )
    {
        CTILOG_DEBUG(dout, "Initializing table request");
    }

    getApplicationLayer().initializeTableRequest (_tables[_index].tableID,
                                         _tables[_index].tableOffset,
                                         _tables[_index].bytesExpected,
                                         _tables[_index].type,
                                         _tables[_index].operation);
}

void CtiProtocolANSI::printResults()
{
    if( _table00 ) _table00->printResult(getAnsiDeviceName());
    if( _table01 ) _table01->printResult(getAnsiDeviceName());
    if( _table11 ) _table11->printResult(getAnsiDeviceName());
    if( _table12 ) _table12->printResult(getAnsiDeviceName());
    if( _table13 ) _table13->printResult(getAnsiDeviceName());
    if( _table14 ) _table14->printResult(getAnsiDeviceName());
    if( _table15 ) _table15->printResult(getAnsiDeviceName());
    if( _table16 ) _table16->printResult(getAnsiDeviceName());
    if( _table21 ) _table21->printResult(getAnsiDeviceName());
    if( _table22 ) _table22->printResult(getAnsiDeviceName());
    if( _table23 ) _table23->printResult(getAnsiDeviceName());
    if( _frozenRegTable ) _frozenRegTable->printResult(getAnsiDeviceName());
    if( _table27 ) _table27->printResult(getAnsiDeviceName());
    if( _table28 ) _table28->printResult(getAnsiDeviceName());
    if( _table31 ) _table31->printResult(getAnsiDeviceName());
    if( _table32 ) _table32->printResult(getAnsiDeviceName());
    if( _table33 ) _table33->printResult(getAnsiDeviceName());
    if( _table51 ) _table51->printResult(getAnsiDeviceName());
    if( _table52 ) _table52->printResult(getAnsiDeviceName());
    if( _table61 ) _table61->printResult(getAnsiDeviceName());
    if( _table62 ) _table62->printResult(getAnsiDeviceName());
    if( _table63 ) _table63->printResult(getAnsiDeviceName());
    if( _table64 ) _table64->printResult(getAnsiDeviceName());
    if( _table08 ) _table08->printResult(getAnsiDeviceName());

    return;
}

bool CtiProtocolANSI::handleWriteOperations()
{

    if (_requestingBatteryLifeFlag)
    {
        _tables[_index].tableID = Sentinel_BatteryLifeResponse;
        _tables[_index].tableOffset = 0;
        _tables[_index].type = ANSI_TABLE_TYPE_MANUFACTURER;
        _tables[_index].operation = ANSI_OPERATION_READ;
        updateBytesExpected ();
        getApplicationLayer().initializeTableRequest (_tables[_index].tableID,
                                                      _tables[_index].tableOffset,
                                                      _tables[_index].bytesExpected,
                                                      _tables[_index].type,
                                                      _tables[_index].operation);
        _requestingBatteryLifeFlag = false;
        return true;

    }
    else if (_tables[_index].tableID == Focus_SetLpReadControl)
    {
        return false;
    }
    else
    {
        _tables[_index].tableID = ProcedureResponse;
        _tables[_index].tableOffset = 0;
        _tables[_index].type = ANSI_TABLE_TYPE_STANDARD;
        _tables[_index].operation = ANSI_OPERATION_READ;
        updateBytesExpected ();
        getApplicationLayer().initializeTableRequest (_tables[_index].tableID,
                                                      _tables[_index].tableOffset,
                                                      _tables[_index].bytesExpected,
                                                      _tables[_index].type,
                                                      _tables[_index].operation);
        return true;
    }
}

bool CtiProtocolANSI::createWriteOperations()
{

     if (_scanOperation == CtiProtocolANSI::demandReset && _tables[_index].tableID == GeneralManufacturerIdentification) //1 = demand reset
     {
          if (proc09RemoteReset(1))
          {
              return true;
          }
     }
     if (_tables[_index].tableID == DataSelection && snapshotData())
     {
         return true;
     }
     if (_tables[_index].tableID == PresentRegisterData && batteryLifeData())
     {
         _requestingBatteryLifeFlag = true;
         return true;
     }
     return false;
}

void CtiProtocolANSI::setLoadProfileFullBlocks(long fullBlocks)
{
    _lpNbrFullBlocks =  fullBlocks;
}

bool CtiProtocolANSI::setLoadProfileVariables()
{
    bool retVal = true;
   _lpNbrIntvlsLastBlock = _table63->getNbrValidIntvls(1);
   _lpNbrValidBlks = _table63->getNbrValidBlocks(1);
   _lpLastBlockIndex = _table63->getLastBlkElmt(1);
   _lpNbrIntvlsPerBlock = _table61->getNbrBlkIntsSet(1);
   _lpMaxIntervalTime = _table61->getMaxIntTimeSet(1);
   _lpNbrBlksSet = _table61->getNbrBlksSet(1);

   if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_ACTIVITY_INFO) )
   {
       CTILOG_DEBUG(dout, getApplicationLayer().getAnsiDeviceName() <<
               endl <<"  ** _lpNbrIntvlsLastBlock  "<< _lpNbrIntvlsLastBlock <<
               endl <<"  ** _lpLastBlockIndex  "<< _lpLastBlockIndex
               );
   }
   _lpBlockSize = getSizeOfLPDataBlock(1);
   _lpLastBlockSize =  getSizeOfLastLPDataBlock(1);
   getApplicationLayer().setLPBlockSize(_lpBlockSize);

   if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_ACTIVITY_INFO) )
   {
       CTILOG_DEBUG(dout, getApplicationLayer().getAnsiDeviceName() <<
               endl <<"  ** _lpBlockSize  "<< _lpBlockSize <<
               endl <<"  ** _lpLastBlockSize  "<< _lpLastBlockSize
               );
   }

   if ((_lpStartBlockIndex = calculateLPDataBlockStartIndex(_header->lastLoadProfileTime)) < 0)
   {
       retVal = false;
   }
   else
   {
       _lpNbrFullBlocks =  _lpLastBlockIndex - _lpStartBlockIndex;
       _lpOffset = _lpStartBlockIndex * _lpBlockSize;
       if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_ACTIVITY_INFO) )
       {
           CTILOG_DEBUG(dout, getApplicationLayer().getAnsiDeviceName() <<
                   endl <<"  ** Nbr of Full Blocks  "<< _lpNbrFullBlocks
                   );
       }
   }
   return retVal;
}


//=========================================================================================================================================
//we've gotten table data back from the device, so we'll paste it into the appropriate table structure
//we may decode it here, we may send it somewhere else....
//=========================================================================================================================================
void CtiProtocolANSI::convertToTable(  )
{
    try
    {

        // if its manufactured, send it to the child class
        if (_tables[_index].type == ANSI_TABLE_TYPE_MANUFACTURER)
        {
            convertToManufacturerTable (getApplicationLayer().getCurrentTable(),
                                        _tables[_index].bytesExpected,
                                        _tables[_index].tableID);

        }
        else
        {
            if (isStdTableAvailableInMeter(_tables[_index].tableID))
            {


                switch( _tables[_index].tableID )
                {
                case Configuration:
                   {
                      _table00.reset(new CtiAnsiTable00( getApplicationLayer().getCurrentTable() ));

                      if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )//DEBUGLEVEL_LUDICROUS )
                      {
                          _table00->printResult(getAnsiDeviceName());
                      }

                      setTablesAvailable(_table00->getStdTblsUsed(), _table00->getDimStdTblsUsed(),
                                         _table00->getMfgTblsUsed(), _table00->getDimMfgTblsUsed());
                   }
                   break;

                case GeneralManufacturerIdentification:
                   {
                      _table01.reset(new CtiAnsiTable01( getApplicationLayer().getCurrentTable(),
                                                               _table00->getRawMfgSerialNumberFlag(),
                                                               _table00->getRawIdFormat() ));
                      if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )//DEBUGLEVEL_LUDICROUS )
                      {
                          _table01->printResult(getAnsiDeviceName());
                      }

                      if (getApplicationLayer().getAnsiDeviceType() == CtiANSIApplication::sentinel) //sentinel
                      {
                          getApplicationLayer().setFWVersionNumber(_table01->getFWVersionNumber());
                      }
                   }
                   break;
               case ProcedureResponse:
                   {
                       _table08.reset(new CtiAnsiTable08( getApplicationLayer().getCurrentTable()));

                       if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )//DEBUGLEVEL_LUDICROUS )
                       {
                           _table08->printResult(getAnsiDeviceName());
                       }


                       if (_scanOperation == CtiProtocolANSI::demandReset)
                       {
                           getApplicationLayer().setWriteSeqNbr( 0 );
                       }
                       else
                       {
                           _lpStartBlockIndex = _table08->getLPOffset();
                           _lpNbrFullBlocks = _lpLastBlockIndex - _lpStartBlockIndex;

                           if( _table63 &&
                               _lpStartBlockIndex >= 255)
                           {
                               _lpStartBlockIndex = 0;
                               _lpOffset = 0;
                               if( _table63->getNbrValidIntvls(1) < _table63->getNbrValidBlocks(1) )
                               {
                                   _lpNbrFullBlocks = _table63->getNbrValidBlocks(1) -1;
                               }
                               else
                               {
                                   _lpNbrFullBlocks = _table63->getNbrValidBlocks(1);
                               }
                           }
                           else
                           {
                               _lpOffset = _lpStartBlockIndex * _lpBlockSize;
                           }
                       }
                   }
                   break;

                case DataSource:
                   {
                      _table10.reset(new CtiAnsiTable10( getApplicationLayer().getCurrentTable() ));
                   }
                   break;

                case ActualSourcesLimiting:
                   {
                       _table11.reset(new CtiAnsiTable11( getApplicationLayer().getCurrentTable() ));
                       if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                       {
                           _table11->printResult(getAnsiDeviceName());
                       }
                   }
                   break;

                case UnitOfMeasureEntry:
                   {
                       _table12.reset(new CtiAnsiTable12( getApplicationLayer().getCurrentTable(),
                                                             _table11->getNumberUOMEntries(),
                                                             _table00->getRawDataOrder() ));
                       if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                       {
                           _table12->printResult(getAnsiDeviceName());
                       }
                   }
                   break;

                case DemandControl:
                   {
                       _table13.reset();

                       if( _table11 )
                       {
                           _table13.reset(new CtiAnsiTable13( getApplicationLayer().getCurrentTable(),
                                                                  _table11->getNumberDemandControlEntries(),
                                                                  _table11->getRawPFExcludeFlag(),
                                                                  _table11->getRawSlidingDemandFlag(),
                                                                  _table11->getRawResetExcludeFlag(),
                                                                  _table00->getRawDataOrder() ));
                           if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                           {
                               _table13->printResult(getAnsiDeviceName());
                           }
                       }
                   }
                   break;

                case DataControl:
                   {
                       _table14.reset();

                       if( _table11 )
                       {
                           _table14.reset(new CtiAnsiTable14( getApplicationLayer().getCurrentTable(),
                                                               _table11->getDataControlLength(),
                                                               _table11->getNumberDataControlEntries()));
                           if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                           {
                               _table14->printResult(getAnsiDeviceName());
                           }
                       }
                   }
                   break;

                case Constants:
                   {
                       _table15.reset();

                       if( _table11 && _table00 )
                       {
                           _table15.reset(new CtiAnsiTable15( getApplicationLayer().getCurrentTable(),
                                                               _table11->getRawConstantsSelector(),
                                                               _table11->getNumberConstantsEntries(),
                                                               _table11->getRawNoOffsetFlag(),
                                                               _table11->getRawSetOnePresentFlag(),
                                                               _table11->getRawSetTwoPresentFlag(),
                                                               _table00->getRawNIFormat1(),
                                                               _table00->getRawNIFormat2(),
                                                               _table00->getRawDataOrder() ));

                           if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                           {
                               _table15->printResult(getAnsiDeviceName());
                           }
                       }
                   }
                   break;

                case SourceDefinition:
                   {
                       _table16.reset();

                       if( _table11 )
                       {
                           _table16.reset(new CtiAnsiTable16( getApplicationLayer().getCurrentTable(),
                                                             _table11->getNumberSources() ));
                           if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                           {
                               _table16->printResult(getAnsiDeviceName());
                           }
                       }
                   }
                   break;

                case ActualRegister:
                   {
                      _table21.reset(new CtiAnsiTable21( getApplicationLayer().getCurrentTable() ));

                      if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                      {
                          _table21->printResult(getAnsiDeviceName());
                      }
                   }
                   break;

                case DataSelection:
                   {
                       _table22.reset();

                       if( _table21 )
                       {
                            _table22.reset(new CtiAnsiTable22( getApplicationLayer().getCurrentTable(),
                                                             _table21->getNumberSummations(),
                                                             _table21->getNumberDemands(),
                                                             _table21->getCoinValues() ));
                            if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                            {
                                _table22->printResult(getAnsiDeviceName());
                            }
                       }
                   }
                   break;

                case CurrentRegisterData:
                   {
                       _table23.reset();

                       if( _table21 && _table00 )
                       {
                           _table23.reset(new CtiAnsiTable23( getApplicationLayer().getCurrentTable(),
                                                                 _table21->getOccur(),
                                                                 _table21->getNumberSummations(),
                                                                 _table21->getNumberDemands(),
                                                                 _table21->getCoinValues(),
                                                                 _table21->getTiers(),
                                                                 _table21->getDemandResetCtrFlag(),
                                                                 _table21->getTimeDateFieldFlag(),
                                                                 _table21->getCumDemandFlag(),
                                                                 _table21->getContCumDemandFlag(),
                                                                 _table00->getRawNIFormat1(),
                                                                 _table00->getRawNIFormat2(),
                                                                 _table00->getRawTimeFormat(),
                                                                  23,
                                                                _table00->getRawDataOrder()));

                           if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                           {
                               _table23->printResult(getAnsiDeviceName());
                           }
                       }
                   }
                   break;
                case FrozenRegisterData:
                    {
                        _frozenRegTable.reset();

                        if ( _table21 && _table00)
                        {
                            _frozenRegTable.reset(new CtiAnsiTable25 (  getApplicationLayer().getCurrentTable(),
                                                                          _table21->getOccur(),
                                                                          _table21->getNumberSummations(),
                                                                          _table21->getNumberDemands(),
                                                                          _table21->getCoinValues(),
                                                                          _table21->getTiers(),
                                                                          _table21->getDemandResetCtrFlag(),
                                                                          _table21->getTimeDateFieldFlag(),
                                                                          _table21->getCumDemandFlag(),
                                                                          _table21->getContCumDemandFlag(),
                                                                          _table00->getRawNIFormat1(),
                                                                          _table00->getRawNIFormat2(),
                                                                          _table00->getRawTimeFormat(),
                                                                          _table21->getSeasonInfoFieldFlag() ));
                            if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                            {
                                _frozenRegTable->printResult(getAnsiDeviceName());
                            }
                        }
                    }
                    break;
                case PresentRegisterSelection:
                   {
                       _table27.reset();

                       if ( _table21 )
                       {
                           _table27.reset(new CtiAnsiTable27( getApplicationLayer().getCurrentTable(),
                                                                 _table21->getNbrPresentDemands(),
                                                                 _table21->getNbrPresentValues()));
                           if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                           {
                               _table27->printResult(getAnsiDeviceName());
                           }
                       }
                   }
                   break;
                case PresentRegisterData:
                   {
                       _table28.reset();

                       if( _table21 && _table00 )
                       {
                           _table28.reset(new CtiAnsiTable28( getApplicationLayer().getCurrentTable(),
                                                                 _table21->getNbrPresentDemands(),
                                                                 _table21->getNbrPresentValues(),
                                                                 _table21->getTimeRemainingFlag(),
                                                                 _table00->getRawNIFormat1(),
                                                                 _table00->getRawNIFormat2(),
                                                                 _table00->getRawTimeFormat(),
                                                                 _table00->getRawDataOrder() ));

                           if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                           {
                               _table28->printResult(getAnsiDeviceName());
                           }
                       }
                   }
                   break;
                   case ActualDisplay:
                   {
                       _table31.reset(new CtiAnsiTable31( getApplicationLayer().getCurrentTable(),
                                                      _table00->getRawDataOrder()));

                       if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                       {
                           _table31->printResult(getAnsiDeviceName());
                       }
                   }
                   break;
                   case DisplaySource:
                   {
                       _table32.reset();

                       if( _table31 )
                       {
                           _table32.reset(new CtiAnsiTable32( getApplicationLayer().getCurrentTable(),
                                                                 _table31->getNbrDispSources(),
                                                                 _table31->getWidthDispSources() ));

                           if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                           {
                                _table32->printResult(getAnsiDeviceName());
                           }
                       }
                   }
                   break;
                   case PrimaryDisplayList:
                   {
                       _table33.reset();

                       if( _table31 )
                       {
                           _table33.reset(new CtiAnsiTable33( getApplicationLayer().getCurrentTable(),
                                                                 _table31->getNbrPriDispLists(),
                                                                 _table31->getNbrPriDispListItems(),
                                                                 _table00->getRawDataOrder() ));

                           if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                           {
                               _table33->printResult(getAnsiDeviceName());
                           }
                      }
                   }
                   break;
                case ActualTimeAndTOU:
                   {
                       _table51.reset(new CtiAnsiTable51( getApplicationLayer().getCurrentTable() ));

                       if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                       {
                           _table51->printResult(getAnsiDeviceName());
                       }
                   }
                   break;
                case Clock:
                   {
                       _table52.reset();

                       if( _table00 )
                       {
                           _table52.reset(new CtiAnsiTable52( getApplicationLayer().getCurrentTable(), _table00->getRawTimeFormat() ));

                           if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                           {
                               _table52->printResult(getAnsiDeviceName());
                           }
                       }
                   }
                   break;
                case ActualLoadProfile:
                   {
                       _table61.reset();

                       if( _table00 )
                       {
                           _table61.reset(new CtiAnsiTable61( getApplicationLayer().getCurrentTable(),
                                                          _table00->getStdTblsUsed(),
                                                          _table00->getDimStdTblsUsed(),
                                                          _table00->getRawDataOrder()  ));

                           if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                           {
                               _table61->printResult(getAnsiDeviceName());
                           }

                           _lpNbrLoadProfileChannels = _table61->getNbrChansSet(1);
                       }
                   }
                   break;
                case LoadProfileControl:
                   {
                      _table62.reset();

                      if( _table00 && _table61 )
                      {
                          _table62.reset(new CtiAnsiTable62( getApplicationLayer().getCurrentTable(), _table61->getLPDataSetUsedFlags(), _table61->getLPDataSetInfo(),
                                                             _table61->getLPScalarDivisorFlag(1), _table61->getLPScalarDivisorFlag(2), _table61->getLPScalarDivisorFlag(3),
                                                             _table61->getLPScalarDivisorFlag(4),  _table00->getRawStdRevisionNo(), _table00->getRawDataOrder() ));
                          if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                          {
                              _table62->printResult(getAnsiDeviceName());
                          }
                      }
                   }

                   break;
               case LoadProfileStatus:
                   {
                       _table63.reset();

                       if( _table61 )
                       {
                          _table63.reset(new CtiAnsiTable63( getApplicationLayer().getCurrentTable(), _table61->getLPDataSetUsedFlags(), _table00->getRawDataOrder()));
                          if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                          {
                              _table63->printResult(getAnsiDeviceName());
                          }
                      }
                   }
                   break;
                case LoadProfileDataSet1:
                {
                    _table64.reset();
                    UINT16 validIntvls = 0;
                    if( _table63 )
                    {

                        validIntvls = _table63->getNbrValidIntvls(1);
                        if (getApplicationLayer().getPartialProcessLPDataFlag())
                        {
                            _lpNbrFullBlocks = (getApplicationLayer().getLPByteCount() / _lpBlockSize) - 1;

                            Cti::FormattedList itemsList;
                            itemsList.add("LP Byte Count")      << getApplicationLayer().getLPByteCount();
                            itemsList.add("LP Block Size")      << _lpBlockSize;
                            itemsList.add("LP Nbr Full Blocks") << _lpNbrFullBlocks;

                            CTILOG_INFO(dout, itemsList);

                            _lpNbrIntvlsLastBlock = 0;//_table61->getNbrBlkIntsSet(1);
                            validIntvls = _lpNbrIntvlsLastBlock;
                            getApplicationLayer().setPartialProcessLPDataFlag(false);
                            if (_lpNbrFullBlocks > 0)
                                _forceProcessDispatchMsg = true;
                        }
                    }

                    int meterHour = 0;
                    bool timeZoneApplied = false;
                    if (_table52)
                    {
                        meterHour = _table52->getClkCldrHour();
                        timeZoneApplied = _table52->getTimeZoneAppliedFlag();
                    }

                    if( _table00 && _table62 && _table61 )
                    {
                        _table64.reset(new CtiAnsiTable64( getApplicationLayer().getCurrentTable(), _lpNbrFullBlocks + 1,
                                                               _table61->getNbrChansSet(1), _table61->getClosureStatusFlag(),
                                                               _table61->getSimpleIntStatusFlag(), _table61->getNbrBlkIntsSet(1),
                                                               _table61->getBlkEndReadFlag(), _table61->getBlkEndPulseFlag(),
                                                               _table61->getExtendedIntStatusFlag(), _table61->getMaxIntTimeSet(1),
                                                               _table62->getIntervalFmtCde(1), validIntvls,
                                                               _table00->getRawNIFormat1(), _table00->getRawNIFormat2(),
                                                               _table00->getRawTimeFormat(), meterHour, timeZoneApplied,
                                                               _table00->getRawDataOrder(), _table63->isDataBlockOrderDecreasing(1),
                                                               _table63->isIntervalOrderDecreasing(1)));

                    }
                }

                   break;
                default:
                    break;
                }
            }

        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);

        autopsy(__FILE__, __LINE__);

        _ansiAbortOperation = TRUE;
        _index = _header->numTablesRequested - 1;
        getApplicationLayer().terminateSession();
    }
    return;
}

// only used for standard tables possibly larger than one data packet size
void CtiProtocolANSI::updateBytesExpected( )
{
    try
    {
        // if its manufactured, send it to the child class
        if (_tables[_index].type == ANSI_TABLE_TYPE_MANUFACTURER)
        {
            if (isMfgTableAvailableInMeter((_tables[_index].tableID) - 0x0800))
            {
                _currentTableNotAvailableFlag = false;
                updateMfgBytesExpected();
            }
        }
        else
        {
            if(isStdTableAvailableInMeter(_tables[_index].tableID))
            {

                _currentTableNotAvailableFlag = false;

                switch( _tables[_index].tableID )
                {
                   case Configuration:
                   {
                       _tables[_index].bytesExpected = 30;
                   }
                   break;
                   case GeneralManufacturerIdentification:
                   {
                       _tables[_index].bytesExpected = 24;
                   }
                   break;
                   case ProcedureResponse:
                   {
                       _tables[_index].bytesExpected = 5;
                   }
                   break;
                   case ActualSourcesLimiting:
                   {
                       _tables[_index].bytesExpected = 8;
                   }
                   break;
                   case UnitOfMeasureEntry:
                   {
                       _tables[_index].bytesExpected = 4 * _table11->getNumberUOMEntries();
                   }
                   break;

                case DemandControl:
                   {
                       _tables[_index].bytesExpected = 0;
                       if (_table11->getRawResetExcludeFlag())
                       {
                           _tables[_index].bytesExpected += 1;
                       }
                       if (_table11->getRawPFExcludeFlag())
                       {
                           _tables[_index].bytesExpected += 3;
                       }
                       // add array values
                       _tables[_index].bytesExpected += 2 * _table11->getNumberDemandControlEntries();
                   }
                   break;

                case DataControl:
                    {
                       _tables[_index].bytesExpected = _table11->getDataControlLength() *
                                                       _table11->getNumberDataControlEntries();
                    }
                   break;

                case Constants:
                   {
                       // NOTE: worrying about electrical only
                       //MULTIPLIER
                       _tables[_index].bytesExpected = sizeOfNonIntegerFormat (_table00->getRawNIFormat1());
                       //OFFSET
                       if (!_table11->getRawNoOffsetFlag())
                       {
                           _tables[_index].bytesExpected += sizeOfNonIntegerFormat (_table00->getRawNIFormat1());
                       }
                       //SET1_CONSTANTS
                       if (_table11->getRawSetOnePresentFlag())
                       {
                            _tables[_index].bytesExpected += 1;
                            _tables[_index].bytesExpected += ( 2 * sizeOfNonIntegerFormat (_table00->getRawNIFormat1()));
                       }
                       //SET2_CONSTANTS
                       if (_table11->getRawSetTwoPresentFlag())
                       {
                            _tables[_index].bytesExpected += 1;
                            _tables[_index].bytesExpected += ( 2 * sizeOfNonIntegerFormat (_table00->getRawNIFormat1())) ;
                       }
                       _tables[_index].bytesExpected *= _table11->getNumberConstantsEntries();
                   }
                   break;

                case SourceDefinition:
                   {
                        _tables[_index].bytesExpected = _table11->getNumberSources();
                   }
                   break;
                case Register:
                case ActualRegister:
                   {
                       _tables[_index].bytesExpected = 10;
                   }
                   break;
                case DataSelection:
                   {
                       _tables[_index].bytesExpected = (_table21->getNumberSummations() +
                                                        _table21->getNumberDemands() +
                                                        (2 * _table21->getCoinValues()) +
                                                        ((int)_table21->getNumberDemands() + 7)/ 8);
                   }
                   break;

                case CurrentRegisterData:
                   {
                       // get the size of a demands record first
                       int demandsRecSize = 0;
                       if (_table21->getTimeDateFieldFlag())
                       {
                           demandsRecSize += (_table21->getOccur() * sizeOfSTimeDate());
                       }

                       if (_table21->getCumDemandFlag())
                       {
                           demandsRecSize += sizeOfNonIntegerFormat (_table00->getRawNIFormat1());
                       }

                       if (_table21->getContCumDemandFlag())
                       {
                           demandsRecSize += sizeOfNonIntegerFormat (_table00->getRawNIFormat1());
                       }

                       demandsRecSize += (_table21->getOccur()  *
                                          sizeOfNonIntegerFormat (_table00->getRawNIFormat2()));

                       int coinRecSize = (_table21->getOccur()  *
                                          sizeOfNonIntegerFormat (_table00->getRawNIFormat2()));

                       _tables[_index].bytesExpected = _table21->getNumberSummations() *
                                                       sizeOfNonIntegerFormat (_table00->getRawNIFormat1());
                       _tables[_index].bytesExpected += _table21->getNumberDemands() * demandsRecSize;
                       _tables[_index].bytesExpected += _table21->getCoinValues() * coinRecSize;


                       _tables[_index].bytesExpected += (_table21->getTiers() * _tables[_index].bytesExpected);


                        if (_table21->getDemandResetCtrFlag())
                        {
                             _tables[_index].bytesExpected += 1;
                        }
                   }
                   break;
                case FrozenRegisterData:
                    {
                        // get the size of a demands record first
                       int demandsRecSize = 0;
                       int registerInfoSize = 0;
                       if (_table21->getTimeDateFieldFlag())
                       {
                           demandsRecSize += (_table21->getOccur() * sizeOfSTimeDate());
                           registerInfoSize += sizeOfSTimeDate();
                       }
                       if (_table21->getSeasonInfoFieldFlag())
                       {
                           registerInfoSize += 1;
                       }
                       if (_table21->getCumDemandFlag())
                       {
                           demandsRecSize += sizeOfNonIntegerFormat (_table00->getRawNIFormat1());
                       }

                       if (_table21->getContCumDemandFlag())
                       {
                           demandsRecSize += sizeOfNonIntegerFormat (_table00->getRawNIFormat1());
                       }

                       demandsRecSize += (_table21->getOccur()  *
                                          sizeOfNonIntegerFormat (_table00->getRawNIFormat2()));

                       int coinRecSize = (_table21->getOccur()  *
                                          sizeOfNonIntegerFormat (_table00->getRawNIFormat2()));

                       _tables[_index].bytesExpected = _table21->getNumberSummations() *
                                                       sizeOfNonIntegerFormat (_table00->getRawNIFormat1());
                       _tables[_index].bytesExpected += _table21->getNumberDemands() * demandsRecSize;
                       _tables[_index].bytesExpected += _table21->getCoinValues() * coinRecSize;


                       _tables[_index].bytesExpected += (_table21->getTiers() * _tables[_index].bytesExpected);


                        if (_table21->getDemandResetCtrFlag())
                        {
                             _tables[_index].bytesExpected += 1;
                        }
                        _tables[_index].bytesExpected += registerInfoSize;

                    }
                    break;
                case PresentRegisterSelection:
                   {
                       _tables[_index].bytesExpected += (_table21->getNbrPresentDemands() +
                                                         _table21->getNbrPresentValues());
                   }
                   break;
                case PresentRegisterData:
                    {
                        if (_table27)
                        {
                            _tables[_index].bytesExpected += ( _table21->getNbrPresentDemands() * ( sizeOfNonIntegerFormat(_table00->getRawNIFormat2()) + 4 ))
                                                          + ( _table21->getNbrPresentValues() * sizeOfNonIntegerFormat (_table00->getRawNIFormat1()) );
                        }
                        else
                        {
                            _tables[_index].bytesExpected = 0;
                        }

                    }
                    break;
                case ActualDisplay:
                    {
                         _tables[_index].bytesExpected = 10;
                    }
                    break;
                case DisplaySource:
                    {
                         _tables[_index].bytesExpected += (_table31->getNbrDispSources() * ( _table31->getWidthDispSources() * 1));
                    }
                    break;
                case PrimaryDisplayList:
                    {
                        _tables[_index].bytesExpected += ((_table31->getNbrPriDispLists() * 3) + (_table31->getNbrPriDispListItems() * 2));
                    }
                    break;
                case SecondaryDisplayList:
                    {
                        _tables[_index].bytesExpected += ((_table31->getNbrSecDispLists() * 3) + (_table31->getNbrSecDispListItems() * 2));
                    }
                    break;
                case ActualTimeAndTOU:
                    {
                        _tables[_index].bytesExpected = 9;
                    }
                    break;
                case Clock:
                    {     _tables[_index].bytesExpected = 7;
                        // _tables[_index].bytesExpected = sizeof (LTIME_DATE) + 1; //LTIME_DATE + TIME_DATE_QUAL_BFLD
                    }
                    break;
                case ActualLoadProfile:
                    {
                       // if (useScanFlags())
                        {
                            _tables[_index].bytesExpected = 7; //LP_MEMORY_LEN + LP_FLAGS + LP_FMATS

                            int lpTbl[] = {64, 65, 66, 67};
                            unsigned char *stdTblsUsed = _table00->getStdTblsUsed();

                            if (_table00->getDimStdTblsUsed() > 8)
                            {
                                int x, y, yy;
                                x = 0;
                                while (x < 4)
                                {
                                    y = 1;
                                    for (yy = 0; yy < lpTbl[x]%8; yy++)
                                    {
                                        y = y*2;
                                    }
                                    if (stdTblsUsed[(lpTbl[x]/8)] & y)
                                    {
                                        _tables[_index].bytesExpected += 6;   //6 bytes per data set used
                                    }
                                    x++;
                                }
                            }
                        }
                       // else
                        {
                      //      _currentTableNotAvailableFlag = true;
                        }
                    }
                    break;
                case LoadProfileControl:
                    {
                       // if (useScanFlags())
                        {
                            bool * dataSetUsedFlags = _table61->getLPDataSetUsedFlags();
                            LP_DATA_SET *lp_data_set_info = _table61->getLPDataSetInfo();
                            _tables[_index].bytesExpected = 0;

                            for (int x = 0; x < 4; x++)
                            {
                                if (dataSetUsedFlags[x])
                                {
                                    _tables[_index].bytesExpected += (lp_data_set_info[x].nbr_chns_set * 3);
                                    _tables[_index].bytesExpected += 1;
                                    if (_table61->getLPScalarDivisorFlag(x+1))
                                    {
                                        //Scalers Set and Divisors Set
                                        _tables[_index].bytesExpected += 2 * (lp_data_set_info[x].nbr_chns_set * 2);
                                    }
                                }
                            }
                        }
                       // else
                        {
                       //     _currentTableNotAvailableFlag = true;
                        }
                    }
                    break;
                case LoadProfileStatus:
                    {
                        bool * dataSetUsedFlags = _table61->getLPDataSetUsedFlags();
                        _tables[_index].bytesExpected = 0;

                        for (int x = 0; x < 4; x++)
                        {
                            if (dataSetUsedFlags[x])
                            {
                                _tables[_index].bytesExpected += 13;
                            }
                        }

                    }
                    break;
                case LoadProfileDataSet1:
                    {
                        _tables[_index].bytesExpected = (_lpNbrFullBlocks * _lpBlockSize) + _lpLastBlockSize;
                        if(getApplicationLayer().getAnsiDeviceType() == CtiANSIApplication::focus)
                        {
                            _tables[_index].bytesExpected = (_lpNbrFullBlocks * _lpBlockSize) + _lpBlockSize;
                        }

                    }
                    break;
                 }
            }
            else
            {
                _currentTableNotAvailableFlag = true;

                CTILOG_INFO(dout, "Table "<< _tables[_index].tableID <<" NOT present in meter -- 0 bytes expected");
            }

        }
        if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_ACTIVITY_INFO) )
        {
            CTILOG_DEBUG(dout, "Table "<< _tables[_index].tableID <<" expected bytes "<< (int)_tables[_index].bytesExpected);
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);

        autopsy(__FILE__, __LINE__);

        _ansiAbortOperation = TRUE;
    }

}


int CtiProtocolANSI::sizeOfNonIntegerFormat( int aFormat )
{
    int retVal=0;

   switch( aFormat )
   {
       case ANSI_NI_FORMAT_FLOAT64:
           retVal = 8;
           break;
       case ANSI_NI_FORMAT_FLOAT32:
           retVal = 4;
            break;
       case ANSI_NI_FORMAT_ARRAY12_CHAR:
           retVal = 12;
           break;
       case ANSI_NI_FORMAT_ARRAY6_CHAR:
           retVal = 6;
           break;
       case ANSI_NI_FORMAT_INT32_IMPLIED:
           retVal = 4;
           break;
       case ANSI_NI_FORMAT_ARRAY6_BCD:
           retVal = 6;
           break;
       case ANSI_NI_FORMAT_ARRAY4_BCD:
           retVal = 4;
           break;
       case ANSI_NI_FORMAT_INT24:
           retVal = 3;
           break;
       case ANSI_NI_FORMAT_INT32:
           retVal = 4;
           break;
       case ANSI_NI_FORMAT_INT40:
           retVal = 5;
           break;
       case ANSI_NI_FORMAT_INT48:
           retVal = 6;
           break;
       case ANSI_NI_FORMAT_INT64:
           retVal = 8;
           break;
       case ANSI_NI_FORMAT_ARRAY8_BCD:
           retVal = 8;
           break;
       case ANSI_NI_FORMAT_ARRAY21_CHAR:
           retVal = 21;
           break;
   }
   return( retVal );
}

int CtiProtocolANSI::sizeOfSTimeDate( void )
{
    int retVal=0;
   if (_table00)
   {
       switch (_table00->getRawTimeFormat())
       {
           case 1/*CASE1*/:
           {
               retVal = 3;
               break;
           }
           case 2/*CASE2*/:
           {
               retVal = 5;
               break;
           }
           case 3/*CASE3*/:
           case 4/*CASE4*/:
           {
               retVal = 4;
               break;
           }
           default:
               break;
       }
   }
   return retVal;
}
int CtiProtocolANSI::sizeOfLTimeDate( void )
{
    int retVal=0;
   if (_table00)
   {
       switch (_table00->getRawTimeFormat())
       {
           case 1/*CASE1*/:
           {
               retVal = 3;
               break;
           }
           case 2/*CASE2*/:
           {
               retVal = 6;
               break;
           }
           case 3/*CASE3*/:
           {
               retVal = 5;
               break;
           }
           case 4/*CASE4*/:
           {
               retVal = 4;
               break;
           }
           default:
               break;
       }
   }
   return retVal;
}


//=========================================================================================================================================
//=========================================================================================================================================

bool CtiProtocolANSI::isTransactionComplete( void ) const
{
    // trying to decide if we are done with our attempts
   return _appLayer.isReadComplete() || _appLayer.isReadFailed();
}

bool CtiProtocolANSI::isTransactionFailed( void )
{
    // trying to decide if we are done with our attempts
   return (getApplicationLayer().isReadFailed());
}


////////////////////////////////////////////////////////////////////////////////////
// Demand - KW, KVAR, KVA, etc...
////////////////////////////////////////////////////////////////////////////////////
bool CtiProtocolANSI::retrieveDemand( int offset, double *value, double *timestamp, bool frozen )
{
    try
    {

        bool success = false;
        if( _ansiAbortOperation )
        {
            return success;
        }
        if ((frozen && !_frozenRegTable) ||
            (!frozen && !_table23) ||
            !_table22 || !_table21 || !_table12 )
        {
            return false;
        }
        AnsiUnit ansiOffset = getUnitsOffsetMapping(offset);
        AnsiTOURate ansiTOURate = getRateOffsetMapping(offset);
        unsigned char *demandSelect = _table22->getDemandSelect();

        for (int x = 0; x < _table21->getNumberDemands(); x++)
        {
            if ((int) demandSelect[x] != 255)
            {
                if (_table12->getRawTimeBase(demandSelect[x]) == CtiAnsiTable12::timebase_block_average  &&
                    _table12->getRawIDCode(demandSelect[x]) == ansiOffset )
                {
                    success = true;
                    double multiplier = getElecMultiplier((demandSelect[x]%20));
                    multiplier = scaleMultiplier(multiplier, demandSelect[x]);

                    // will bring back value in KW/KVAR ...
                    if( frozen )
                    {
                        *value = _frozenRegTable->getDemandResetDataTable()->getDemandValue(x, ansiTOURate) * multiplier;
                        *timestamp = _frozenRegTable->getDemandResetDataTable()->getDemandEventTime( x, ansiTOURate );
                    }
                    else
                    {
                        *value = _table23->getDemandValue(x, ansiTOURate) * multiplier ;
                        *timestamp = _table23->getDemandEventTime( x, ansiTOURate );
                    }
                    if (_table52 )
                    {
                        *timestamp = _table52->adjustTimeZoneAndDST(*timestamp);
                    }
                    printDebugValue(*value, frozen);
                    break;
                }

            }
        }
        demandSelect = NULL;

        return success;
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);

        autopsy(__FILE__, __LINE__);
    }
    return false;
}
////////////////////////////////////////////////////////////////////////////////////
// Summations = Energy - KWH, KVARH, KVAH, etc...
////////////////////////////////////////////////////////////////////////////////////
bool CtiProtocolANSI::retrieveSummation( int offset, double *value,  double *timestamp, bool frozen )
{
    try
    {
        bool success = false;
        if( _ansiAbortOperation )
        {
            return success;
        }
        if ((frozen && !_frozenRegTable) ||
            (!frozen && ! _table23) ||
            ! _table22 || ! _table21 || ! _table12 )
        {
            return false;
        }
        /* Watts = 0, Vars = 1, VA = 2, etc */
        AnsiUnit ansiOffset = getUnitsOffsetMapping(offset);
        AnsiTOURate ansiTOURate = getRateOffsetMapping(offset);

       /* returns pointer to list of summation Selects */
        unsigned char* summationSelect = _table22->getSummationSelect();

        for (int x = 0; x < _table21->getNumberSummations(); x++)
        {
            if ((int) summationSelect[x] != 255)
            {
                if (_table12->getRawTimeBase(summationSelect[x]) == CtiAnsiTable12::timebase_dial_reading &&
                    _table12->getRawIDCode(summationSelect[x]) == ansiOffset)
                {
                    double multiplier = getElecMultiplier(summationSelect[x]);
                    multiplier = scaleMultiplier(multiplier, summationSelect[x]);

                    if( frozen )
                    {

                        *value = _frozenRegTable->getDemandResetDataTable()->getSummationsValue(x, ansiTOURate) * multiplier;
                        *timestamp = _frozenRegTable->getEndDateTime();
                    }
                    else
                    {
                        *value = _table23->getSummationsValue(x, ansiTOURate) * multiplier;
                        *timestamp = CtiTime().seconds();
                    }
                    success = true;
                    printDebugValue(*value, frozen);
                    break;
                }
            }
        }
        return success;
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);

        autopsy(__FILE__, __LINE__);
    }
    return false;
}

////////////////////////////////////////////////////////////////////////////////////
// Present Values - volts, current, pf, etc...
////////////////////////////////////////////////////////////////////////////////////
bool CtiProtocolANSI::retrievePresentValue( int offset, double *value )
{
    bool success = false;
    if( _ansiAbortOperation )
    {
        return success;
    }

    if (success = retrieveMfgPresentValue(offset, value)) //if 1, kv2 gets info from mfg tbl 110
    {
        printDebugValue(*value);
    }
    else
    {
        if( !_table27 || !_table21 || !_table12 || !_table28 )
        {
            return false;
        }
        /* Watts = 0, Vars = 1, VA = 2, Volts = 8, Current = 12, etc */
        AnsiUnit ansiOffset = getUnitsOffsetMapping(offset);

        try
        {
            /* returns pointer to list of present Value Selects */
            unsigned char* presentValueSelect = _table27->getValueSelect();

            if (presentValueSelect != NULL)
            {
                for (int x = 0; x < _table21->getNbrPresentValues(); x++)
                {
                    if ((int) presentValueSelect[x] != 255)
                    {

                        if (_table12->getRawTimeBase(presentValueSelect[x]) == CtiAnsiTable12::timebase_instantaneous &&
                            _table12->getSegmentation(presentValueSelect[x]) == getSegmentationOffsetMapping(offset) &&
                            _table12->getRawIDCode(presentValueSelect[x]) == ansiOffset)
                        {

                            *value = _table28->getPresentValue(x) * getElecMultiplier(presentValueSelect[x]);
                            success = true;
                            printDebugValue(*value);
                            break;
                        }
                    }
                }
            }

        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);

            autopsy(__FILE__, __LINE__);
        }
    }
    return success;
}

////////////////////////////////////////////////////////////////////////////////////
// Present Values - volts, current, pf, etc...
////////////////////////////////////////////////////////////////////////////////////
bool CtiProtocolANSI::retrievePresentDemand( int offset, double *value )
{
    bool success = false;
    if( _ansiAbortOperation )
    {
        return success;
    }
    if (getApplicationLayer().getAnsiDeviceType() == CtiANSIApplication::kv2) //if 1, kv2 gets info from mfg tbl 110
    {
        try
        {
            CTILOG_WARN(dout, "NOT IMPLEMENTED FOR KV2 YET");
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);

            autopsy(__FILE__, __LINE__);
        }
    }
    else
    {
        if( !_table27 || !_table21 || !_table12 || !_table28 )
        {
            return false;
        }

        /* Watts = 0, Vars = 1, VA = 2, Volts = 8, Current = 12, etc */
        AnsiUnit ansiOffset = getUnitsOffsetMapping(offset);
        int ansiQuadrant = getQuadrantOffsetMapping(offset);

        try
        {
            /* returns pointer to list of present Demand Selects */
            unsigned char* presentDemandSelect = _table27->getDemandSelect();

            if (presentDemandSelect != NULL)
            {
                for (int x = 0; x < _table21->getNbrPresentDemands(); x++)
                {
                    if ((int) presentDemandSelect[x] != 255)
                    {
                        if (_table12->getRawTimeBase(presentDemandSelect[x]) == CtiAnsiTable12::timebase_block_average &&
                            _table12->getRawIDCode(presentDemandSelect[x]) == ansiOffset &&
                            _table12->getQuadrantAccountabilityFlag(ansiQuadrant, presentDemandSelect[x]) )
                        {
                            *value = _table28->getPresentDemand(x) * getElecMultiplier(presentDemandSelect[x]);
                            success = true;
                            printDebugValue(*value);
                            break;
                        }
                    }
                }
            }
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);

            autopsy(__FILE__, __LINE__);
        }
    }
    return success;
}


////////////////////////////////////////////////////////////////////////////////////
// Battery Life - volts, current, pf, etc...
////////////////////////////////////////////////////////////////////////////////////
bool CtiProtocolANSI::retrieveBatteryLife( int offset, double *value )
{
    if ( getApplicationLayer().getAnsiDeviceType() != CtiANSIApplication::sentinel) //if 0,1, kv,kv2 not supported
    {
        return false;
    }
    if( _ansiAbortOperation )
    {
        return false;
    }

    try
    {
        switch (offset)
        {
            case 180:
            {
                *value = abs((UINT16)getGoodBatteryReading() - (UINT16)getCurrentBatteryReading());
                return true;
            }
            case 181:
            {
                *value = getDaysOnBatteryReading();
                return true;
            }
            default:
                break;
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);

        autopsy(__FILE__, __LINE__);
    }
    return false;
}
bool CtiProtocolANSI::retrieveMeterTimeDiffStatus( int offset, double *status )
{
    bool success = false;
    if( _ansiAbortOperation )
    {
        return success;
    }
    string   str;
    int tempDiff = 600;

    if( !(str = gConfigParms.getValueAsString(METER_TIME_TOLERANCE)).empty() )
    {
        tempDiff = atoi(str.c_str());
    }

    if (_table52)
    {
        try
        {
            ULONG value = _table52->getMeterServerTimeDifference();
            if (value > tempDiff)
            {
                *status = STATEONE; //BAD - ALARMING
                success = true;
            }
            else
            {
                *status = STATEZERO; //GOOD
                success = true;
            }
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);

            autopsy(__FILE__, __LINE__);
        }
    }

    return success;
}


////////////////////////////////////////////////////////////////////////////////////
// LP Demands - KW, KVAR, KVA, etc...
////////////////////////////////////////////////////////////////////////////////////
bool CtiProtocolANSI::retrieveLPDemand( int offset, int dataSet )
{
    bool success = false;
    if( _ansiAbortOperation )
    {
        return success;
    }
    if( !_table62 || !_table61 || !_table12 || !_table64 )
    {
        return false;
    }

    UINT8* lpDemandSelect = NULL;

    /* Watts = 0, Vars = 1, VA = 2, etc */
    AnsiUnit ansiOffset = getUnitsOffsetMapping(offset);
    AnsiSegmentation segmentation = getSegmentationOffsetMapping(offset);

    try
    {
        /* returns pointer to list of LP Demand Selects from either dataSet 1,2,3,or 4*/
        lpDemandSelect = _table62->getLPDemandSelect(dataSet);
        for (int x = 0; x < _table61->getNbrChansSet(dataSet); x++)
        {
            if ((int) lpDemandSelect[x] != 255)
            {

                if ((_table12->getRawTimeBase(lpDemandSelect[x]) == CtiAnsiTable12::timebase_relative_dial_reading ||
                     _table12->getRawTimeBase(lpDemandSelect[x]) == CtiAnsiTable12::timebase_dial_reading) &&
                     doesIdCodeMatch(lpDemandSelect[x], ansiOffset) &&
                     doesSegmentationMatch(lpDemandSelect[x], segmentation) )
                {

                    if (_lpNbrFullBlocks > 0 || _lpNbrIntvlsLastBlock > 0)
                    {
                        success = true;

                        switch (dataSet)
                        {
                            case 1:
                                {
                                    int intvlsPerBlk = _table61->getNbrBlkIntsSet(dataSet);
                                    int blkIndex = 0;
                                    int totalIntvls = intvlsPerBlk * _lpNbrFullBlocks + _lpNbrIntvlsLastBlock;
                                    _nbrLPDataBlkIntvlsWanted = totalIntvls;

                                    resetLoadProfilePointers(totalIntvls); //reallocate memory for _lpValues, _lpTimes, _lpQaulity

                                    int intvlIndex = isDataBlockOrderDecreasing() ? intvlsPerBlk - _lpNbrIntvlsLastBlock : 0;
                                    for (int y = 0; y < totalIntvls; y++)
                                    {
                                        double constant = 1.0;
                                        if (_table62->getNoMultiplierFlag(dataSet) && _table15) //no_multiplier_flag == true (constants need to be applied)
                                        {
                                            constant = _table15->getElecMultiplier((lpDemandSelect[x]%20));
                                        }
                                        _lpValues[y] =  (_table64->getLPDemandValue ( x, blkIndex, intvlIndex ) * constant) /
                                                        (_table12->getResolvedMultiplier(lpDemandSelect[x]) * 1000) *
                                                        (60 / _table61->getMaxIntTimeSet(dataSet)) *
                                                        getMfgConstants();
                                        _lpTimes[y] = _table64->getLPDemandTime (blkIndex, intvlIndex);
                                        if (_table52)
                                        {
                                            _lpTimes[y] = _table52->adjustTimeZoneAndDST(_lpTimes[y]);

                                        }
                                        if (_table64->getPowerFailFlag(blkIndex, intvlIndex))
                                            _lpQuality[y] = PowerfailQuality; //powerFailQuality
                                        else
                                            _lpQuality[y] = translateAnsiQualityToYukon(_table64->getExtendedIntervalStatus(x, blkIndex, intvlIndex));

                                        if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_ACTIVITY_INFO) )//DEBUGLEVEL_LUDICROUS )
                                        {
                                            CTILOG_DEBUG(dout, "lpTime:  "<< CtiTime(_lpTimes[y]) <<"  lpValue: "<<_lpValues[y]<<"  lpQuality: "<<(int)_lpQuality[y]);
                                        }

                                        if (intvlIndex + 1 == intvlsPerBlk)
                                        {
                                            blkIndex++;
                                        }
                                        intvlIndex = (intvlIndex + 1) % intvlsPerBlk;
                                    }
                                }
                                break;
                            /*
                            case 2:
                                {
                                    value[y] = _table65->getLPDemandValue ( x, 1, 1 );
                                }
                                break;
                            case 3:
                                {
                                    value[y] = _table66->getLPDemandValue ( x, 1, 1 );
                                }
                                break;
                            case 4:
                                {
                                    value[y] = _table67->getLPDemandValue ( x, 1, 1 );
                                }

                                break;
                            */
                            default:
                                break;
                           }
                    }
                }
            }
            if (success)
                break;
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);

        autopsy(__FILE__, __LINE__);
    }
    if (lpDemandSelect != NULL)
    {
        delete []lpDemandSelect;
        lpDemandSelect = NULL;
    }
    return success;
}
////////////////////////////////////////////////////////////////////////////////////
// LP Demands - KW, KVAR, KVA, etc...
////////////////////////////////////////////////////////////////////////////////////
double CtiProtocolANSI::getLPValue( int index)
{
    if( index < 0 || index >= _lpValues.size() )
    {
        return 0;
    }

    return _lpValues[index];
}
////////////////////////////////////////////////////////////////////////////////////
// LP Demands - KW, KVAR, KVA, etc...
////////////////////////////////////////////////////////////////////////////////////
ULONG CtiProtocolANSI::getLPTime( int index )
{
    if( index < 0 || index >= _lpTimes.size() )
    {
        return 0;
    }

    return _lpTimes[index];
}

////////////////////////////////////////////////////////////////////////////////////
// LP Qualities
////////////////////////////////////////////////////////////////////////////////////
UINT8 CtiProtocolANSI::getLPQuality( int index )
{
    if( index < 0 || index >= _lpQuality.size() )
    {
        return 0;
    }

    return _lpQuality[index];
}


int CtiProtocolANSI::getQuadrantOffsetMapping(int offset)
{
    int retVal = 300;
    switch (offset)
    {
        case OFFSET_QUADRANT1_TOTAL_KVARH:
        case OFFSET_QUADRANT1_LAST_INTERVAL_KVAR:
        {
            retVal = 1;
            break;
        }
        case OFFSET_QUADRANT2_TOTAL_KVARH:
        case OFFSET_QUADRANT2_LAST_INTERVAL_KVAR:
        {
            retVal = 2;
            break;
        }
        case OFFSET_QUADRANT3_TOTAL_KVARH:
        case OFFSET_QUADRANT3_LAST_INTERVAL_KVAR:
        {
            retVal = 3;
            break;
        }
        case OFFSET_QUADRANT4_TOTAL_KVARH:
        case OFFSET_QUADRANT4_LAST_INTERVAL_KVAR:
        {
            retVal = 4;
            break;
        }
    default:
        break;
    }
    return retVal;



}


AnsiUnit CtiProtocolANSI::getUnitsOffsetMapping(int offset)
{
    AnsiUnit retVal = UndefinedUnit;
    switch (offset)
    {
            case OFFSET_TOTAL_KWH:
            case OFFSET_PEAK_KW_OR_RATE_A_KW:
            case OFFSET_RATE_A_KWH:
            case OFFSET_RATE_B_KW:
            case OFFSET_RATE_B_KWH:
            case OFFSET_RATE_C_KW:
            case OFFSET_RATE_C_KWH:
            case OFFSET_RATE_D_KW:
            case OFFSET_RATE_D_KWH:
            case OFFSET_RATE_E_KW:
            case OFFSET_RATE_E_KWH:
            case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KW:
            case OFFSET_LOADPROFILE_KW:
            {
                retVal = KW;
                break;
            }
            case OFFSET_TOTAL_KVARH:
            case OFFSET_PEAK_KVAR_OR_RATE_A_KVAR:
            case OFFSET_RATE_A_KVARH:
            case OFFSET_RATE_B_KVAR:
            case OFFSET_RATE_B_KVARH:
            case OFFSET_RATE_C_KVAR:
            case OFFSET_RATE_C_KVARH:
            case OFFSET_RATE_D_KVAR:
            case OFFSET_RATE_D_KVARH:
            case OFFSET_RATE_E_KVAR:
            case OFFSET_RATE_E_KVARH:
            case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVAR:
            case OFFSET_LOADPROFILE_KVAR:
            case OFFSET_LOADPROFILE_QUADRANT1_KVAR:
            case OFFSET_LOADPROFILE_QUADRANT2_KVAR:
            case OFFSET_LOADPROFILE_QUADRANT3_KVAR:
            case OFFSET_LOADPROFILE_QUADRANT4_KVAR:
            case OFFSET_QUADRANT1_TOTAL_KVARH:
            case OFFSET_QUADRANT1_LAST_INTERVAL_KVAR:
            case OFFSET_QUADRANT2_TOTAL_KVARH:
            case OFFSET_QUADRANT2_LAST_INTERVAL_KVAR:
            case OFFSET_QUADRANT3_TOTAL_KVARH:
            case OFFSET_QUADRANT3_LAST_INTERVAL_KVAR:
            case OFFSET_QUADRANT4_TOTAL_KVARH:
            case OFFSET_QUADRANT4_LAST_INTERVAL_KVAR:
            {
                retVal = KVar;
                break;
            }
            case OFFSET_TOTAL_KVAH:
            case OFFSET_PEAK_KVA_OR_RATE_A_KVA:
            case OFFSET_RATE_A_KVAH:
            case OFFSET_RATE_B_KVA:
            case OFFSET_RATE_B_KVAH:
            case OFFSET_RATE_C_KVA:
            case OFFSET_RATE_C_KVAH:
            case OFFSET_RATE_D_KVA:
            case OFFSET_RATE_D_KVAH:
            case OFFSET_RATE_E_KVA:
            case OFFSET_RATE_E_KVAH:
            case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVA:
            case OFFSET_LOADPROFILE_KVA:
            case OFFSET_LOADPROFILE_QUADRANT1_KVA:
            case OFFSET_LOADPROFILE_QUADRANT2_KVA:
            case OFFSET_LOADPROFILE_QUADRANT3_KVA:
            case OFFSET_LOADPROFILE_QUADRANT4_KVA:
            {
                retVal = KVA;
                break;
            }
            case OFFSET_INSTANTANEOUS_PHASE_A_VOLTAGE:
            case OFFSET_LOADPROFILE_PHASE_A_VOLTAGE:
            case OFFSET_INSTANTANEOUS_PHASE_B_VOLTAGE:
            case OFFSET_LOADPROFILE_PHASE_B_VOLTAGE:
            case OFFSET_INSTANTANEOUS_PHASE_C_VOLTAGE:
            case OFFSET_LOADPROFILE_PHASE_C_VOLTAGE:
            {
                retVal = Voltage;
                break;
            }
            case OFFSET_INSTANTANEOUS_PHASE_A_CURRENT:
            case OFFSET_LOADPROFILE_PHASE_A_CURRENT:
            case OFFSET_INSTANTANEOUS_PHASE_B_CURRENT:
            case OFFSET_LOADPROFILE_PHASE_B_CURRENT:
            case OFFSET_INSTANTANEOUS_PHASE_C_CURRENT:
            case OFFSET_LOADPROFILE_PHASE_C_CURRENT:
            case OFFSET_INSTANTANEOUS_NEUTRAL_CURRENT:
            case OFFSET_LOADPROFILE_NEUTRAL_CURRENT:
            {
                retVal = Current;
                break;
            }
            case OFFSET_POWER_FACTOR:
            {
                retVal = PowerFactor;
            }
        default:
            break;
    }
    return retVal;
}

AnsiSegmentation CtiProtocolANSI::getSegmentationOffsetMapping(int offset)
{

    AnsiSegmentation retVal = NotPhaseRelated;
    switch (offset)
    {
        case OFFSET_POWER_FACTOR:
        {
            retVal = NotPhaseRelated;
            break;
        }
        case OFFSET_INSTANTANEOUS_NEUTRAL_CURRENT:
        case OFFSET_LOADPROFILE_NEUTRAL_CURRENT:
        {
            retVal = Neutral;
            break;
        }
        case OFFSET_INSTANTANEOUS_PHASE_A_VOLTAGE:
        case OFFSET_LOADPROFILE_PHASE_A_VOLTAGE:
        case OFFSET_INSTANTANEOUS_PHASE_A_CURRENT:
        case OFFSET_LOADPROFILE_PHASE_A_CURRENT:
        {
            retVal = PhaseA;
            break;
        }
        case OFFSET_INSTANTANEOUS_PHASE_B_VOLTAGE:
        case OFFSET_LOADPROFILE_PHASE_B_VOLTAGE:
        case OFFSET_INSTANTANEOUS_PHASE_B_CURRENT:
        case OFFSET_LOADPROFILE_PHASE_B_CURRENT:
        {
            retVal = PhaseB;
            break;
        }
        case OFFSET_INSTANTANEOUS_PHASE_C_VOLTAGE:
        case OFFSET_LOADPROFILE_PHASE_C_VOLTAGE:
        case OFFSET_INSTANTANEOUS_PHASE_C_CURRENT:
        case OFFSET_LOADPROFILE_PHASE_C_CURRENT:
        {
            retVal = PhaseC;
            break;
        }
        default:
            break;
    }
    return retVal;
}


AnsiTOURate CtiProtocolANSI::getRateOffsetMapping(int offset)
{
    AnsiTOURate retVal = UndefinedRate;
    switch (offset)
    {
        case OFFSET_TOTAL_KWH:
        case OFFSET_TOTAL_KVARH:
        case OFFSET_TOTAL_KVAH:
        {
            retVal = Total;
            break;
        }
        case OFFSET_PEAK_KW_OR_RATE_A_KW:
        case OFFSET_PEAK_KVAR_OR_RATE_A_KVAR:
        case OFFSET_PEAK_KVA_OR_RATE_A_KVA:
        case OFFSET_RATE_A_KWH:
        case OFFSET_RATE_A_KVARH:
        case OFFSET_RATE_A_KVAH:
        {
            retVal = Tier1;
            break;
        }

        case OFFSET_RATE_B_KW:
        case OFFSET_RATE_B_KWH:
        case OFFSET_RATE_B_KVAR:
        case OFFSET_RATE_B_KVARH:
        case OFFSET_RATE_B_KVA:
        case OFFSET_RATE_B_KVAH:
        {
            retVal = Tier2;
            break;
        }

        case OFFSET_RATE_C_KW:
        case OFFSET_RATE_C_KWH:
        case OFFSET_RATE_C_KVAR:
        case OFFSET_RATE_C_KVARH:
        case OFFSET_RATE_C_KVA:
        case OFFSET_RATE_C_KVAH:
        {
            retVal = Tier3;
            break;
        }

        case OFFSET_RATE_D_KW:
        case OFFSET_RATE_D_KWH:
        case OFFSET_RATE_D_KVAR:
        case OFFSET_RATE_D_KVARH:
        case OFFSET_RATE_D_KVA:
        case OFFSET_RATE_D_KVAH:
        {
            retVal = Tier4;
            break;
        }

        case OFFSET_RATE_E_KW:
        case OFFSET_RATE_E_KWH:
        case OFFSET_RATE_E_KVAR:
        case OFFSET_RATE_E_KVARH:
        case OFFSET_RATE_E_KVA:
        case OFFSET_RATE_E_KVAH:
        {
            retVal = Tier5;
            break;
        }

        case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KW:
        case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVAR:
        case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVA:

        case OFFSET_LOADPROFILE_KW:
        case OFFSET_LOADPROFILE_KVAR:
        case OFFSET_LOADPROFILE_KVA:

        case OFFSET_LOADPROFILE_QUADRANT1_KVAR:
        case OFFSET_LOADPROFILE_QUADRANT1_KVA:
        case OFFSET_LOADPROFILE_QUADRANT2_KVAR:
        case OFFSET_LOADPROFILE_QUADRANT2_KVA:
        case OFFSET_LOADPROFILE_QUADRANT3_KVAR:
        case OFFSET_LOADPROFILE_QUADRANT3_KVA:
        case OFFSET_LOADPROFILE_QUADRANT4_KVAR:
        case OFFSET_LOADPROFILE_QUADRANT4_KVA:

        case OFFSET_INSTANTANEOUS_PHASE_A_VOLTAGE:
        case OFFSET_INSTANTANEOUS_PHASE_A_CURRENT:
        case OFFSET_LOADPROFILE_PHASE_A_VOLTAGE:
        case OFFSET_LOADPROFILE_PHASE_A_CURRENT:

        case OFFSET_INSTANTANEOUS_PHASE_B_VOLTAGE:
        case OFFSET_INSTANTANEOUS_PHASE_B_CURRENT:
        case OFFSET_LOADPROFILE_PHASE_B_VOLTAGE:
        case OFFSET_LOADPROFILE_PHASE_B_CURRENT:

        case OFFSET_INSTANTANEOUS_PHASE_C_VOLTAGE:
        case OFFSET_INSTANTANEOUS_PHASE_C_CURRENT:
        case OFFSET_LOADPROFILE_PHASE_C_VOLTAGE:
        case OFFSET_LOADPROFILE_PHASE_C_CURRENT:

        case OFFSET_INSTANTANEOUS_NEUTRAL_CURRENT:
        case OFFSET_LOADPROFILE_NEUTRAL_CURRENT:
        default:
            break;
    }
    return retVal;
}

int CtiProtocolANSI::translateAnsiQualityToYukon(int ansiQuality )
{
    int retVal = NormalQuality;
    switch (ansiQuality)
    {
        case 0:
        case 5:
        {
            retVal = NormalQuality;
            break;
        }
        case 1:
        {
            retVal = OverflowQuality;
            break;
        }
        case 2:
        {
            retVal = PartialIntervalQuality;
            break;
        }
        case 3: //long interval
        case 4:  //skipped
        {
            retVal = UnknownQuality;
            break;
        }
        default:
        {
            retVal = NormalQuality;
            break;
        }
    }
    return retVal;

}


int CtiProtocolANSI::getSizeOfLastLPDataBlock(int dataSetNbr)
{
    try
    {

        int sizeOfLpBlkDatRcd = 0;
        int nbrChnsSet = _table61->getNbrChansSet(dataSetNbr);
        int nbrBlkIntsSet = _table63->getNbrValidIntvls(dataSetNbr);


        sizeOfLpBlkDatRcd += sizeOfSTimeDate() +
                          (nbrChnsSet * getSizeOfLPReadingsRcd());

        if (_table61->getClosureStatusFlag())
        {
            sizeOfLpBlkDatRcd += (nbrChnsSet * 2); //uint16 closure_status_bfld
        }
        if (_table61->getSimpleIntStatusFlag())
        {
            sizeOfLpBlkDatRcd += (nbrBlkIntsSet + 7) / 8;
        }
        sizeOfLpBlkDatRcd += nbrBlkIntsSet * getSizeOfLPIntSetRcd(dataSetNbr);

        return sizeOfLpBlkDatRcd;
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);

        autopsy(__FILE__, __LINE__);
    }
    return 0;
}


int CtiProtocolANSI::getSizeOfLPDataBlock(int dataSetNbr)
{
    try
    {

        int sizeOfLpBlkDatRcd = 0;
        int nbrChnsSet = _table61->getNbrChansSet(dataSetNbr);
        int nbrBlkIntsSet = _table61->getNbrBlkIntsSet(dataSetNbr);


        sizeOfLpBlkDatRcd += sizeOfSTimeDate() +
                          (nbrChnsSet * getSizeOfLPReadingsRcd());

        if (_table61->getClosureStatusFlag())
        {
            sizeOfLpBlkDatRcd += (nbrChnsSet * 2); //uint16 closure_status_bfld
        }
        if (_table61->getSimpleIntStatusFlag())
        {
            sizeOfLpBlkDatRcd += (nbrBlkIntsSet + 7) / 8;
        }
        sizeOfLpBlkDatRcd += nbrBlkIntsSet * getSizeOfLPIntSetRcd(dataSetNbr);

        return sizeOfLpBlkDatRcd;
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);

        autopsy(__FILE__, __LINE__);
    }
    return 0;
}

int CtiProtocolANSI::getSizeOfLPReadingsRcd()
{
    try
    {
        int sizeOfReadingsRcd = 0;

        if (_table61->getBlkEndReadFlag())
        {
            sizeOfReadingsRcd += sizeOfNonIntegerFormat (_table00->getRawNIFormat1());
        }
        if (_table61->getBlkEndPulseFlag())
        {
            sizeOfReadingsRcd += 4;
        }
        return sizeOfReadingsRcd;
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);

        autopsy(__FILE__, __LINE__);
    }
    return 0;
}

int CtiProtocolANSI::getSizeOfLPIntSetRcd(int dataSetNbr)
{
    try
    {
        int sizeOfIntSetRcd = 0;
        int nbrChnsSet = _table61->getNbrChansSet(dataSetNbr);

        if (_table61->getExtendedIntStatusFlag())
        {
            sizeOfIntSetRcd += (nbrChnsSet/2) + 1;

        }
        sizeOfIntSetRcd += nbrChnsSet * getSizeOfLPIntFmtRcd(dataSetNbr);

        return sizeOfIntSetRcd;
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);

        autopsy(__FILE__, __LINE__);
    }
    return 0;
}
int CtiProtocolANSI::getSizeOfLPIntFmtRcd(int dataSetNbr)
{
    try
    {
        int sizeOfIntFmtRcd = 0;
        switch (_table62->getIntervalFmtCde(dataSetNbr))
        {
            case 1:
            case 8:
            {
                sizeOfIntFmtRcd += 1;
                break;
            }
            case 2:
            case 16:
            {
                sizeOfIntFmtRcd += 2;
                break;
            }
            case 4:
            case 32:
            {
                sizeOfIntFmtRcd += 4;
                break;
            }
            case 64:
            {
                sizeOfIntFmtRcd += sizeOfNonIntegerFormat (_table00->getRawNIFormat1());
                break;
            }
            case 128:
            {
                sizeOfIntFmtRcd += sizeOfNonIntegerFormat (_table00->getRawNIFormat2());

                break;
            }
        }
        return sizeOfIntFmtRcd;
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);

        autopsy(__FILE__, __LINE__);
    }
    return 0;
}
unsigned short CtiProtocolANSI::getTotalWantedLPBlockInts()
{
    return _nbrLPDataBlkIntvlsWanted;
}

int CtiProtocolANSI::proc09RemoteReset(UINT8 actionFlag)
{
    try
    {

        _tables[_index].tableID = ProcedureInitiate;
        _tables[_index].tableOffset = 0;
        _tables[_index].bytesExpected = 1;
        _tables[_index].type = ANSI_TABLE_TYPE_STANDARD;
        _tables[_index].operation = ANSI_OPERATION_WRITE;

        getApplicationLayer().initializeTableRequest (_tables[_index].tableID,
                                                      _tables[_index].tableOffset,
                                                      _tables[_index].bytesExpected,
                                                      _tables[_index].type,
                                                      _tables[_index].operation);

        REQ_DATA_RCD reqData;
        reqData.proc.tbl_proc_nbr = 9;
        reqData.proc.std_vs_mfg_flag = 0;
        reqData.proc.selector = 3;
        BYTE *superTemp;

        superTemp = (BYTE *)reqData.proc.tbl_proc_nbr;

        CTILOG_INFO(dout,
                endl <<"tbl_proc_nbr = "<<    (int)reqData.proc.tbl_proc_nbr <<
                endl <<"std_vs_mfg_flag = "<< (int)reqData.proc.std_vs_mfg_flag <<
                endl <<"selector = "<<        (int)reqData.proc.selector
                );

        getApplicationLayer().setProcBfld( reqData.proc );
        _seqNbr++;
        reqData.seq_nbr = _seqNbr;
        getApplicationLayer().setWriteSeqNbr( reqData.seq_nbr );


        reqData.u.p9.action_flag = actionFlag;
        /*BYTE *newPtr;
        newPtr = 0;
        *newPtr = reqData.u.p9.action_flag;*/
        getApplicationLayer().populateParmPtr((BYTE *)&reqData.u.p9.action_flag, 1) ;


        getApplicationLayer().setProcDataSize( sizeof(TBL_IDB_BFLD) + sizeof(reqData.seq_nbr) + 1 );

    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);

        autopsy(__FILE__, __LINE__);
    }

    return 1;
}


void CtiProtocolANSI::setCurrentAnsiWantsTableValues(short tableID,int tableOffset, unsigned int bytesExpected,
                                                     BYTE  type, BYTE operation)
{
   _tables[_index].tableID = tableID;
   _tables[_index].tableOffset = tableOffset;
   _tables[_index].bytesExpected = bytesExpected;
   _tables[_index].type = type;
   _tables[_index].operation = operation;

   return;
}

short CtiProtocolANSI::getCurrentTableId()
{
    return _tables[_index].tableID;
}

int CtiProtocolANSI::getWriteSequenceNbr(void)
{
    return _seqNbr++;
}

unsigned long CtiProtocolANSI::getlastLoadProfileTime(void)
{
    return _header->lastLoadProfileTime;
}


int CtiProtocolANSI::getNbrValidIntvls()
{
    return _lpNbrIntvlsLastBlock;
}
int CtiProtocolANSI::getNbrValidBlks()
{
    return _lpNbrValidBlks;
}

int CtiProtocolANSI::getMaxIntervalTime()
{
    return _lpMaxIntervalTime;
}
int CtiProtocolANSI::getNbrBlksSet()
{
    return _lpNbrBlksSet;
}
int CtiProtocolANSI::getNbrIntervalsPerBlock()
{
    return _lpNbrIntvlsPerBlock;
}
int CtiProtocolANSI::getLastBlockIndex()
{
    return _lpLastBlockIndex;
}
bool CtiProtocolANSI::isDataBlockOrderDecreasing()
{
    return _table63 && _table63->isDataBlockOrderDecreasing(1);
}


void CtiProtocolANSI::setTablesAvailable(unsigned char * stdTblsUsed, int dimStdTblsUsed, unsigned char * mfgTblsUsed, int dimMfgTblsUsed)
{
    try
    {
        if (!_stdTblsAvailable.empty())
        {
            _stdTblsAvailable.clear();
        }
        if (_mfgTblsAvailable.empty())
        {
            _mfgTblsAvailable.clear();
        }

        int i;
        for (i = 0; i < dimStdTblsUsed; i++)
        {
            if (stdTblsUsed[i] & 0x01)
            {
                _stdTblsAvailable.push_back((i*8)+0);
            }
            if (stdTblsUsed[i] & 0x02)
            {
                _stdTblsAvailable.push_back((i*8)+1);
            }
            if (stdTblsUsed[i] & 0x04)
            {
                _stdTblsAvailable.push_back((i*8)+2);
            }
            if (stdTblsUsed[i] & 0x08)
            {
                _stdTblsAvailable.push_back((i*8)+3);
            }
            if (stdTblsUsed[i] & 0x10)
            {
                _stdTblsAvailable.push_back((i*8)+4);
            }
            if (stdTblsUsed[i] & 0x20)
            {
                _stdTblsAvailable.push_back((i*8)+5);
            }
            if (stdTblsUsed[i] & 0x40)
            {
                _stdTblsAvailable.push_back((i*8)+6);
            }
            if (stdTblsUsed[i] & 0x80)
            {
                _stdTblsAvailable.push_back((i*8)+7);
            }
        }

        for (i = 0; i < dimMfgTblsUsed; i++)
        {
            if ((int)mfgTblsUsed[i] & 0x01)
            {
                _mfgTblsAvailable.push_back((i*8)+0);
            }
            if (mfgTblsUsed[i] & 0x02)
            {
                _mfgTblsAvailable.push_back((i*8)+1);
            }
            if (mfgTblsUsed[i] & 0x04)
            {
                _mfgTblsAvailable.push_back((i*8)+2);
            }
            if (mfgTblsUsed[i] & 0x08)
            {
                _mfgTblsAvailable.push_back((i*8)+3);
            }
            if (mfgTblsUsed[i] & 0x10)
            {
                _mfgTblsAvailable.push_back((i*8)+4);
            }
            if (mfgTblsUsed[i] & 0x20)
            {
                _mfgTblsAvailable.push_back((i*8)+5);
            }
            if (mfgTblsUsed[i] & 0x40)
            {
                _mfgTblsAvailable.push_back((i*8)+6);
            }
            if (mfgTblsUsed[i] & 0x80)
            {
                _mfgTblsAvailable.push_back((i*8)+7);
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);

        autopsy(__FILE__, __LINE__);
    }

    return;
}

list < short > CtiProtocolANSI::getStdTblsAvailable(void)
{
    return _stdTblsAvailable;
}

list < short > CtiProtocolANSI::getMfgTblsAvailable(void)
{
    return _mfgTblsAvailable;
}

bool CtiProtocolANSI::isStdTableAvailableInMeter(short tableNbr)
{
    std::list<short>::iterator ii = _stdTblsAvailable.begin();

    do
    {
        if (*ii == tableNbr)
        {
            return true;
        }
        else
            ii++;

    }while(ii != _stdTblsAvailable.end());

    return false;
}

bool CtiProtocolANSI::isMfgTableAvailableInMeter(short tableNbr)
{
    std::list<short>::iterator ii = _mfgTblsAvailable.begin();

    do
    {
        if (*ii == tableNbr)
        {
            return true;
        }
        else
            ii++;

    }while(ii != _mfgTblsAvailable.end());

    return false;
}


CtiProtocolANSI::AnsiScanOperation CtiProtocolANSI::getScanOperation(void)
{
    return _scanOperation;
}


UINT CtiProtocolANSI::getParseFlags(void)
{
    return _parseFlags;
}

const string& CtiProtocolANSI::getAnsiDeviceName() const
{
    return _ansiDevName;
}
void CtiProtocolANSI::setAnsiDeviceName(const string& devName)
{
    getApplicationLayer().setAnsiDeviceName(devName);
    _ansiDevName = devName;
    return;
}


void CtiProtocolANSI::setLastLoadProfileTime(LONG lastLPTime)
{
    _header->lastLoadProfileTime = lastLPTime;
    return;
}

bool CtiProtocolANSI::forceProcessDispatchMsg()
{
    if (_table64)
        return _forceProcessDispatchMsg;
    else if (_table23 || _table52)
        return true;
    else
        return false;
}

bool CtiProtocolANSI::isTimeUninitialized(double timestamp)
{
    CtiTime t1 = CtiTime(timestamp);
    CtiTime t2 = CtiTime(CtiDate(1,1,2000));
    if ( t1.seconds() == t2.seconds() )
        return true;
    else
        return false;
}



 void CtiProtocolANSI::destroyManufacturerTables( void )
 {
     return;
 }
 void CtiProtocolANSI::convertToManufacturerTable( BYTE *data, BYTE numBytes, short aTableID )
 {
     return;
 }

bool CtiProtocolANSI::snapshotData()
{
    return false;
}

bool CtiProtocolANSI::batteryLifeData()
{
    return false;
}

int CtiProtocolANSI::getGoodBatteryReading()
{
    return 0;
}
int CtiProtocolANSI::getCurrentBatteryReading()
{
    return 0;
}
int CtiProtocolANSI::getDaysOnBatteryReading()
{
   return 0;
}
bool CtiProtocolANSI::retrieveMfgPresentValue( int offset, double *value )
{
    return false;
}

float CtiProtocolANSI::getMfgConstants( )
{
    return 1.0;
}


int CtiProtocolANSI::calculateLPDataBlockStartIndex(ULONG lastLPTime)
{
    int nbrIntervals = 0;
    int nbrValidInts = getNbrValidIntvls();
    int nbrIntsPerBlock = getNbrIntervalsPerBlock();
    int nbrBlksSet = getNbrValidBlks();
    CtiTime currentTime;


    currentTime.now();
    nbrIntervals =  (int)(abs((long)(currentTime.seconds() - lastLPTime))/60) / getMaxIntervalTime();
    if (nbrIntervals > (((nbrBlksSet-1) * nbrIntsPerBlock) + nbrValidInts))
    {
        nbrIntervals = (((nbrBlksSet-1) * nbrIntsPerBlock) + nbrValidInts);
    }
    if (isDataBlockOrderDecreasing())
    {
        return 0;
    }
    if (nbrIntervals <= nbrValidInts)
    {
        // lastBlockIndex;
        return getLastBlockIndex();
    }
    else if ((nbrIntervals - nbrValidInts) > nbrIntsPerBlock)
    {
        return getLastBlockIndex() - ((nbrIntervals - nbrValidInts) / nbrIntsPerBlock);
    }
    else //(nbrIntervals - nbrValidInts) <= nbrIntsPerBlock
    {
        // lastBlockIndex -  1;
        return getLastBlockIndex() - 1;
    }

}

int  CtiProtocolANSI::getFirmwareVersion()
{
    return _table01 ? _table01->getFWVersionNumber() : 0;
}
int  CtiProtocolANSI::getFirmwareRevision()
{
    return _table01 ? _table01->getFWRevisionNumber() : 0;
}
DataOrder  CtiProtocolANSI::getDataOrder()
{
    return _table00 ? _table00->getRawDataOrder() : LSB;
}

double CtiProtocolANSI::getElecMultiplier(int index)
{
    double multiplier = 1.0;
    if( ! _table16 || ! _table15 )
    {
        return multiplier;
    }
    if (_table16->getConstantsFlag(index) &&
        !_table16->getConstToBeAppliedFlag(index))
    {
        multiplier *= _table15->getElecMultiplier(index); /*/ 1000000000*/
    }

    return multiplier;
}

double CtiProtocolANSI::scaleMultiplier(double multiplier, int index)
{
    BYTE ansiDeviceType = getApplicationLayer().getAnsiDeviceType();
    if(ansiDeviceType == CtiANSIApplication::sentinel || ansiDeviceType == CtiANSIApplication::focus)
    {
        if( _table12 )
        {
             multiplier *= (_table12->getResolvedMultiplier(index)/1000);
        }
    }
    else
    {
        multiplier /= 1000000000;
    }
    return multiplier;
}

void CtiProtocolANSI::printDebugValue(double value, bool frozen)
{
    if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_ACTIVITY_INFO) )//DEBUGLEVEL_LUDICROUS )
    {
        CTILOG_DEBUG(dout, (frozen ? " frozen value =   " : " value =   ")<<value);
    }
}


void CtiProtocolANSI::resetLoadProfilePointers( int totalIntvls )
{
    _lpValues .assign(totalIntvls + 1, 0.0f);
    _lpTimes  .assign(totalIntvls + 1, 0UL);
    _lpQuality.assign(totalIntvls + 1, 0);
}

bool CtiProtocolANSI::doesIdCodeMatch(int index, AnsiUnit ansiOffset)
{
    return _table12 && (_table12->getRawIDCode(index) == ansiOffset);
}
bool CtiProtocolANSI::doesSegmentationMatch(int index, AnsiSegmentation  segmentation)
{
    return _table12 && (_table12->getSegmentation(index) == segmentation);
}
