
#pragma warning( disable : 4786)
#ifndef __STD_ANSI_TBL_ZERO_EIGHT_H__
#define __STD_ANSI_TBL_ZERO_EIGHT_H__

/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_zero_eight
*
* Class:  CtiAnsiTableZeroEight
* Date:   9/13/2002
*
* Author: Eric Schmit
*

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include "dlldefs.h"
#include "dsm2.h"
#include "ctitypes.h"
#include "types.h"
#include "std_ansi_tbl_base.h"

#pragma pack( push, 1)


struct RSP_DATA_RCD
{
    union PARMS
    {
      struct PARM0
      {  
          //no parms
      }p0;
      struct PARM1
      {
          //no parms
      }p1;
      struct PARM2
      {
          //no parms
      }p2;
      struct PARM3
      {
          //no parms
      }p3;
      struct PARM4
      {
          unsigned char tbl_list;
      }p4;
      struct PARM5
      {
          unsigned char tbl_list;
          unsigned short entries_read;
      }p5;
      struct PARM6
      {
         // ED_MODE_BFLD ed_mode;
      }p6;
      struct PARM7
      {
        //  ED_STD_STATUS1_BFLD ed_std_status_1;
         // ED_STD_STATUS2_BFLD ed_std_status_2;
      }p7;
      struct PARM8
      {
          //ED_MFG_STATUS_RCD ed_mfg_status;
      }p8;
      struct PARM9
      {
         // ACTION_FLAG_BFLD action_flag;
      }p9;
      struct PARM_M22
      {
         unsigned char lpOffset;
      }pm22;

    }u;
};



#pragma pack( pop )

class IM_EX_PROT CtiAnsiTableZeroEight : public CtiAnsiTableBase
{
protected:

   
private:
    struct TBL_IDB_BFLD
    {
       unsigned short   tbl_proc_nbr:11;
       unsigned short   std_vs_mfg_flag:1;
       unsigned short   selector:4;
    }; 

    struct PROC_RESP_RCD
    {
       TBL_IDB_BFLD          proc;
       unsigned char         seq_nbr;
       unsigned char         result_code;
       RSP_DATA_RCD          resp_data;
    };
public:
    PROC_RESP_RCD _proc_resp_tbl;

    void generateResultPiece( BYTE **dataBlob );
    void decodeResultPiece( BYTE **dataBlob );
    void printResult( );

    CtiAnsiTableZeroEight( );
    CtiAnsiTableZeroEight( BYTE *dataBlob );

    virtual ~CtiAnsiTableZeroEight();

    CtiAnsiTableZeroEight& operator=(const CtiAnsiTableZeroEight& aRef);


    void populateRespDataRcd( BYTE *dataBlob, RSP_DATA_RCD *data_rcd, int &offset );
    int getTblProcNbr(void);
    bool getStdMfgFlg(void);
    int getSelector(void);
    int getSeqNbr(void);
    int getResultCode(void);
    int getLPOffset(void);

};
#endif // #ifndef __STD_ANSI_TBL_ZERO_EIGHT_H__

