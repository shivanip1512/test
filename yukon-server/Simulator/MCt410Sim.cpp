#include "yukon.h"
#include "Mct410Sim.h"
#include "SharedFunctions.h"
#include "logger.h"
#include "dev_mct410.h"
#include "EmetconWordDn.h"
#include "EmetconWordD1.h"

//Hack class do bypass protected function declaration in mct410 and mct4xx
//To be deleted when we move functions to a shared location.
class mct410Bypass : CtiDeviceMCT410
{
    public:
        int makeDynamicDemand_bp(double d){return makeDynamicDemand(d);};
        unsigned char crc8_bp(unsigned char *buf, unsigned int len){ return crc8(buf,len);};
};


Mct410Sim::Mct410Sim()
{
    //Plans to pull this from the database eventually. Porter stores it there.
    periodOfInterest = 0;

    mctAddress = 0;
    numberOfRepeaters = 0;
}

long Mct410Sim::getPeroidOfInterest()
{
    return periodOfInterest;
}

void Mct410Sim::setPeroidOfInterest(long n)
{
    periodOfInterest = n;
}

int Mct410Sim::getMctAddress()
{
    return mctAddress;
}

void Mct410Sim::setMctAddress(int addr)
{
    mctAddress = addr;
}

int Mct410Sim::getNumberOfRepeaters()
{
    return numberOfRepeaters;
}

void Mct410Sim::setNumberOfRepeaters(int num)
{
    numberOfRepeaters = num;
}

unsigned char* Mct410Sim::generateEmetconWordResponse(int& responseSize, int function, CtiTime time, unsigned char ack)
{
    //unsigned char ack = makeAck(mctAddress>>1);
    unsigned char* resp = NULL;
    responseSize = 0;

    if (function == Ping)//ping?
    {//For function 0  there is a chance I need to know how many Dwords I need to send back blank. Pass in the Bword?
        EmetconWordD1 word1;
        unsigned char buf[defaultWordSize];

        unsigned char ebuf[3];
        ebuf[0] = '\0';
        ebuf[1] = '\0';
        ebuf[2] = '\0';
        int ctr = 0;

        responseSize = defaultWordSize;
        resp = new unsigned char [responseSize];

        //Response is an empty Dword with Address
        word1.setMctAddress(mctAddress);
        word1.setNumberOfRepeaters(numberOfRepeaters);
        word1.insertData(ebuf,3);
        
        word1.getBytes(buf,defaultWordSize);
        for (int i = 0; i < defaultWordSize; i++)
        {
            resp[ctr++] = buf[i];
        }

    }
    else if (function == GetCurrentMeterReading)
    {
        //Function 0x90
        EmetconWordD1 word1;
        unsigned char buf[defaultWordSize];

        responseSize = 7;
        resp = new unsigned char [responseSize];
        
        int ctr = 0;    
        unsigned char * ptr = getKWHData(3/*bytes for kwh data*/);

        word1.setMctAddress(mctAddress);
        word1.setNumberOfRepeaters(numberOfRepeaters);

        word1.insertData(ptr,3);
        word1.getBytes(buf,defaultWordSize);
        for (int i = 0; i < defaultWordSize; i++)
        {
            resp[ctr++] = buf[i];
        }
        delete [] ptr;
    }
    else if (function >= LongLoadProfileTableMin && function <= LongLoadProfileTableMax)
    {
        EmetconWordD1 word1;
        EmetconWordDn word2,word3;

        int ctr = 0;
        unsigned char buf[defaultWordSize];

        responseSize = defaultWordSize*3+2;//3 is the number of words 
        resp = new unsigned char [responseSize];

        unsigned char* ptr = getLongLoadProfileData(function,13/*bytes for llp data*/);

        word1.setMctAddress(mctAddress);
        word1.setNumberOfRepeaters(numberOfRepeaters);

        word1.insertData(ptr,3);
        word2.insertData(ptr+3,5);
        word3.insertData(ptr+8,5);

        word1.getBytes(buf,defaultWordSize);
        for (int i = 0; i < defaultWordSize; i++)
        {
            resp[ctr++] = buf[i];
        }
        resp[ctr++] = ack;

        word2.getBytes(buf,defaultWordSize);
        for (i = 0; i < defaultWordSize; i++)
        {
            resp[ctr++] = buf[i];
        }
        resp[ctr++] = ack;

        word3.getBytes(buf,defaultWordSize);
        for (i = 0; i < defaultWordSize; i++)
        {
            resp[ctr++] = buf[i];
        }
    } 
    else if (function == GetCurrentPeakDemandReading)
    {
        EmetconWordD1 word1;
        EmetconWordDn word2,word3;

        responseSize = defaultWordSize*3+2;
        resp = new unsigned char [responseSize];
        //Response is an empty Dword  word 2 and 3 have KWH in them.

        unsigned char buf[defaultWordSize];
        for (int i = 0; i < defaultWordSize; i++)
        {
            buf[i] = 0x00;
        }

        int ctr = 0;
        word1.setMctAddress(mctAddress);
        word1.setNumberOfRepeaters(numberOfRepeaters);
        word1.insertData(buf,3);

        //This has a KWH section. Lets fill it.
        unsigned char * ptr = getKWHData(3/*bytes for kwh data*/);

        buf[3] = ptr[0];
        buf[4] = ptr[1];
        word2.insertData(buf,5);

        buf[0] = ptr[2];
        buf[3] = 0;
        buf[4] = 0;
        word3.insertData(buf,5);

        delete [] ptr;

        word1.getBytes(buf,defaultWordSize);
        for (i = 0; i < defaultWordSize; i++)
        {
            resp[ctr++] = buf[i];
        }
        resp[ctr++] = ack;

        word2.getBytes(buf,defaultWordSize);
        for (i = 0; i < defaultWordSize; i++)
        {
            resp[ctr++] = buf[i];
        }
        resp[ctr++] = ack;

        word3.getBytes(buf,defaultWordSize);
        for (i = 0; i < defaultWordSize; i++)
        {
            resp[ctr++] = buf[i];
        }
    }
    else if (function == GetRecentDemandReading)
    {//Response is an empty Dword
        EmetconWordD1 word1;
        EmetconWordDn word2;

        responseSize = defaultWordSize*2+1;
        resp = new unsigned char [responseSize];
        
        int ctr = 0;
        word1.setMctAddress(mctAddress);
        word1.setNumberOfRepeaters(numberOfRepeaters);

        unsigned char buf[defaultWordSize];
        for (int i = 0; i < defaultWordSize; i++)
        {
            buf[i] = 0x00;
        }

        word1.insertData(buf,3);
        word2.insertData(buf,5);

        word1.getBytes(buf,defaultWordSize);
        for (i = 0; i < defaultWordSize; i++)
        {
            resp[ctr++] = buf[i];
        }
        resp[ctr++] = ack;

        word2.getBytes(buf,defaultWordSize);
        for (i = 0; i < defaultWordSize; i++)
        {
            resp[ctr++] = buf[i];
        }
    }
    else // WritePointOfInterest / WriteIntervals
    {
        //Default response is
        //Response is an empty Dword with NO Address
        EmetconWordD1 word1;
        unsigned char buf[defaultWordSize];

        unsigned char ebuf[3];
        ebuf[0] = '\0';
        ebuf[1] = '\0';
        ebuf[2] = '\0';
        int ctr = 0;

        responseSize = defaultWordSize;
        resp = new unsigned char [responseSize];


        word1.insertData(ebuf,3);
        
        word1.getBytes(buf,defaultWordSize);
        for ( int i = 0; i < defaultWordSize; i++)
        {
            resp[ctr++] = buf[i];
        }
    }

    return resp;
}

unsigned char * Mct410Sim::getLongLoadProfileData(int function, int bytesToReturn)
{
    //Hardcoded interval. we will want this dynamic per meter eventually
    int demandIntervalMinutes = 60;

    unsigned char * ret = NULL;
    /* Long Load Profile Read*/ 

    //Hack class do bypass protected functions
    mct410Bypass mctFuncs;

    //Channel 1 assumed default.
    int channel = 0;

    //Bytes to return should be 13.
    if (bytesToReturn != 13)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "\n" << CtiTime() << " Error. 13 bytes not to be returned. Canceling return message generation." << bytesToReturn << endl;
        return NULL;
    }

    /* Example of the crc for byte 0 of LLP
        unsigned char interest[5];
        unsigned long tmptime = _llpInterest.time;

        interest[0] = (tmptime >> 24) & 0x000000ff;
        interest[1] = (tmptime >> 16) & 0x000000ff;
        interest[2] = (tmptime >>  8) & 0x000000ff;
        interest[3] = (tmptime)       & 0x000000ff;
        interest[4] = _llpInterest.channel + 1;

        if( crc8(interest, 5) == DSt->Message[0] )
    */
    //CRC calculation
    unsigned char interest[5];
    unsigned long tmptime = getPeroidOfInterest();

    interest[0] = (tmptime >> 24) & 0x000000ff;
    interest[1] = (tmptime >> 16) & 0x000000ff;
    interest[2] = (tmptime >>  8) & 0x000000ff;
    interest[3] = (tmptime)       & 0x000000ff;
    interest[4] = channel + 1;
    unsigned char crc = mctFuncs.crc8_bp(interest,5);

    CtiTime lpTime = CtiTime(getPeroidOfInterest());
    lpTime.addMinutes(demandIntervalMinutes*(6*(function-64)));
    //Get the rest of the data


    ret = new unsigned char [bytesToReturn] ;

    //Fill the 13 bytes with CRC and data
    ret[0] = crc;

    for (int i = 1; i < bytesToReturn;)
    {
        double value = mctGetLoadProfile(mctAddress,lpTime);
        int dynamicDemand = mctFuncs.makeDynamicDemand_bp(value);
        ret[i++] = (dynamicDemand >> 8) & 0x000000ff;
        ret[i++] =  dynamicDemand & 0x000000ff;
    
        lpTime.addMinutes(60);
    }

    /* End Long Load Profile */

    return ret;
}

unsigned char * Mct410Sim::getLoadProfileData(int function, int bytesToReturn)
{
    //Hardcoded interval. we will want this dynamic per meter eventually
    int demandIntervalMinutes = 60;

    //Hack class do bypass protected functions
    mct410Bypass mctFuncs;

    unsigned char * ret = NULL;

    //Bytes to return should be 13.
    if (bytesToReturn != 13)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "\n" << CtiTime() << " Error. 13 bytes not to be returned. Canceling return message generation." << bytesToReturn << endl;
        return NULL;
    }

    /* Load Profile Read*/ 
    /*
    Function Reads 50h-5Fh return values for channel 1
    Function Reads 60h-6Fh return values for channel 2
    Function Reads 70h-7Fh return values for channel 3
    Function Reads 80h-8Fh return values for channel 4 (voltage)
    */
    int channel = -1;
    if(function >= LoadProfileTableMinCh1 && function <= LoadProfileTableMaxCh1)
    {
        channel = 0;
    }
    else if (function >= LoadProfileTableMinCh2 && function <= LoadProfileTableMaxCh2)
    {
        channel = 1;//channel 2
    }
    else if (function >= LoadProfileTableMinCh3 && function <= LoadProfileTableMaxCh3)
    {
        channel = 2;//channel 3
    }
    else if (function >= LoadProfileTableMinCh4 && function <= LoadProfileTableMaxCh4)
    {
        channel = 3;//channel 4
    }

    if (channel == -1)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "\n" << CtiTime() << " Error. Unhandled function value. Can't determing the channel" << function << endl;
        return NULL;      
    }

    CtiTime currentTime;
    ret = new unsigned char [bytesToReturn];

    //Fill the 13 bytes with CRC and data
    ret[0] = (unsigned char)((currentTime.seconds()/(demandIntervalMinutes*60))%96)+1;

    if (channel == 0)//Only doing Channel 1 for now.
    {
        long seconds = currentTime.seconds();
        seconds = seconds%(demandIntervalMinutes*60);
        CtiTime timeOfInterest(seconds-(demandIntervalMinutes*60*2));//subtracting 2 periods off (per documentation)
    
        //move to interested Period
        timeOfInterest.addMinutes(-1*demandIntervalMinutes*(6*(function-80)));
        //Get the rest of the data
    
        for (int i = 1; i < bytesToReturn;)
        {
            double value = mctGetLoadProfile(mctAddress,timeOfInterest);
            int dynamicDemand = mctFuncs.makeDynamicDemand_bp(value);
            ret[i++] = (dynamicDemand >> 8) & 0x000000ff;
            ret[i++] =  dynamicDemand & 0x000000ff;
        
            timeOfInterest.addMinutes(-1*demandIntervalMinutes);
        }
    }
    else //Send back Zero's for all other channels right now.
    {
        ret = new unsigned char [bytesToReturn];
        for (int i = 1; i < bytesToReturn;)
        {
            ret[i++] = 0;
            ret[i++] = 0;
        }
    }

    /* End Load Profile */

    return ret;
}

unsigned char * Mct410Sim::getKWHData(int bytesToReturn)
{
    unsigned char * ret = NULL;

    /*function  0x90*/
    /******* This is for getValueKWH Only */
    unsigned int tempValue = (unsigned int)mctGetValue(mctAddress,CtiTime());

    ret = new unsigned char [bytesToReturn] ;

    unsigned char * ptr = (unsigned char*)&tempValue;

    for( int i = 0; i < bytesToReturn; i++)
    {
        // Only the first 3 bytes are important. in reverse order.
        ret[i] = ptr[2-i];
    }
    /* end getValueKWH */

    return ret;
}

