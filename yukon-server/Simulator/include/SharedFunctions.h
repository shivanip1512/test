#ifndef  __SHAREDFUNCTIONS_H__
#define  __SHAREDFUNCTIONS_H__

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

#endif
