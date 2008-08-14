#ifndef  __MCT410SIM_H__
#define  __MCT410SIM_H__

#include "ctitime.h"
#include "cti_asmc.h"

/*
    Move all MCT functions into here eventually. Phase 2 Make a MctBaseSim class containing shared functions.
*/
class Mct410Sim
{
    public:
        Mct410Sim();

        long getPeroidOfInterest();
        void setPeroidOfInterest(long n);

        int getMctAddress();
        void setMctAddress(int addr);

        int getNumberOfRepeaters();
        void setNumberOfRepeaters(int num);

        unsigned char* generateEmetconWordResponse(int& responseSize, int function, CtiTime time);
        unsigned char * getLongLoadProfileData(int function, int bytesToReturn);
        unsigned char * getLoadProfileData(int function, int bytesToReturn);
        unsigned char * getKWHData(int bytesToReturn);
    private:
        int mctAddress;
        //Time in seconds UTC Start time
        long periodOfInterest;
        int numberOfRepeaters;
};

#endif

