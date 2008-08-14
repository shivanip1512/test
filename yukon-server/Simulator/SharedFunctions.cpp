#include "yukon.h"

#include "SharedFunctions.h"

#include "logger.h"

int uchar_parity( unsigned char x ) {
  x = x ^ (x >> 4);
  x = x ^ (x >> 2);
  x = x ^ (x >> 1);

  return x & 1;
} 

unsigned char makeAck(int ccuAddress)
{
    unsigned char ack = 0x40;
    unsigned char adr = 0x03 & ccuAddress;
    ack = adr | ack;
    if (uchar_parity(ack)) {
        ack = 0x80 | ack;
    }
    return ack;
}

unsigned int mctGetValue(int mctAddress, CtiTime time)
{
    //Increasing. Based off the number and time.
    //debug print value returned

    //1 year loop.
    unsigned int value = time.seconds();
    //Dropping off the change within a minute. Multiple reads should return the same value.
    value = value % 31556926;
    value = value - value%60;

    int modMeter = mctAddress / 100;

    value = value/600 + modMeter;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "DEBUG: MCTValue Generated: " << value  << endl;
    }

    return value;
}

double mctGetLoadProfile(int mctNumber, CtiTime time)
{
    double ret = 0.0;
    const float PI = 3.14159265;

    const float ampYear = 2000;//fluctuate 1
    const float ampDay = 1000;
    const float baseMin = 500;
    // y(t) = A * sin(wt+O)

    // Sine wave.  Alway positive.
    // Use the time to control the wave form. Make two waves. The Year and Day.

    //Day Calculations
    long seconds = time.seconds();
    seconds += mctNumber;
    seconds = seconds%86400;

    double num = (double)seconds / 86400.0;
    double computeValue = 2*PI*num;

    //Day value
    double value = ampDay * sin(computeValue);
    ret = fabs(value);
    ret += baseMin;

    //Year Calculations
    seconds = time.seconds();
    seconds += mctNumber;
    seconds = seconds%31556926;
    
    num = (double)seconds / 31556926.0;
    computeValue = 2*PI*num;
    
    value = ampYear * sin(computeValue);
    ret += fabs(value);

    return ret;
}
