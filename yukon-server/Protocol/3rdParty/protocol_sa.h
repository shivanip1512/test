
/*-----------------------------------------------------------------------------*
*
* File:   protocol_sa
*
* Class:
* Date:   7/21/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2004/11/08 14:40:39 $
*
*
* Notes:
*       Proposed format for protocol DLL - Win32 format.
*
* Copyright (c) 2003
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)
#ifndef __PROTOCOL_SA_H__
#define __PROTOCOL_SA_H__

typedef struct schedCode
{
   CHAR code[7];
   SHORT function;  /* The DCU function to be activated */
   USHORT type;     /* Type of DCU defined below */
   USHORT swTime;       /* desired (virtual) switch timeout in minutes */
   USHORT cycleTime;    /* cycle time  in minutes */
   SHORT repeats;       /* number repeats to effect virtual timeout */
                        /* or number cycleTimes in control period (205)*/
}SA_CODE;

/*--------------------------------------------------------------------------*/
/* The X205CMD (xmit 205 command) structure describes a particular 205 setup*/
/* command.                                 */
/*--------------------------------------------------------------------------*/

typedef struct xmit205Cmd   /* 48 bytes per cmd */
{
   char serialNum[33];
   USHORT type;
   char code[7];
   USHORT function;
   USHORT data;
   USHORT padding;
}
X205CMD;

/* This is the current DCU types in OASyS LM */
#define SA205 0
#define GOLAY 1
#define SADIG 2
#define SA105 3
#define SALAT 4
#define GOLAT 5

#define SA305 6

/* SA205 DCU management command */
#define DEF_ADDR 1
#define OVERRIDE 2
#define TAMP_DET 3
#define CLPU_ACT 4

#define DEF_ADDR_FUNC 0
#define OVERRIDE_FUNC 6
#define TAMP_DET_FUNC 8
#define CLPU_ACT_FUNC 12

/* MAX databuffer len */
#define MAX_XBUF    32

#define SHED 1
#define REST 0

/* TMS command type */
#define TMS_ONE 0
#define TMS_ALL 1
#define TMS_INIT 2
#define TMS_ACK 3

/* Message type from TMS */
#define TMS_EMPTY 0
#define TMS_SADIG 4
#define TMS_SA105 6
#define TMS_GOLAY 7
#define TMS_205CMD 12
#define TMS_CODE  20
#define TMS_UNKNOWN  21
#define TMS_LRC_ERROR 22

/* define code buffer sizes */
#define CODESZ 6
#define CONTROL_CODESZ 7
#define CONFIG_CODESZ 12
#define TMS_BUFFER_SIZE 8

// Success and non-fatal errors.
#define PROTSA_SUCCESS                          0
#define PROTSA_SUCCESS_MODIFIED_PARAM           1
#define PROTSA_FAIL_INVALID_SW_CYCLE_TIME       2

// .... and then some as needed...

// Fatal errors.
#define PROTSAERROR_BUFFER_TOO_SMALL            100
#define PROTSAERROR_PARAMETER_OUT_OF_RANGE      101
#define PROTSAERROR_BAD_PARAMETER               102
// .... and then some as needed...


/*----------------------------------------------------------------------------*
 * Function: controlSADigital
 *
 * buf      - output buffer. A fully formed control command is built and placed
 *              in this buffer on successful completion of the function.
 *
 * buflen   - input/output.  On entry buflen indicates the allowed size of buf.
 *              On successful exit, buflen indicates the length of the completed
 *              message stored in buf.
 *
 * code -  Input: valid input format is "xyz" or "xy-z".
    where x,y,z could be the same digit. "xy-z" will be converted to an
    integer value = 8*xy+z.
        In both cases, the maximum integer value of the code is 255.
 *
 * xmitter_addr - Input: Transmitter address
 *
 * markIndex - Input: Mark Index
 *
 * spareIndex - Input: Spare Index
 *
 * Returns:
 *          - A valid return code from above.
 *
 *----------------------------------------------------------------------------*/
INT controlSADigital( BYTE *buf, INT *buflen,  CHAR code[],
                      USHORT xmitter_addr,USHORT markIndex,
                      USHORT spareIndex );


/*----------------------------------------------------------------------------*
 * Function: control105_205
 *
 * buf      - output buffer. A fully formed control command is built and placed
 *              in this buffer on successful completion of the function.
 *
 * buflen   - input/output.  On entry buflen indicates the allowed size of buf.
 *              On successful exit, buflen indicates the length of the completed
 *              message stored in buf.
 *
 * scode     - Input: SA105/205 operational information as defined in the SA_CODE structure
 *
 * xmitter_addr - Input: Transmitter address
 *
 * row    - Input: SA105/205 timeout matrix row index (cycletime)
 *
 * column - Input: SA105/205 timeout matrix column index (timeout)
 *
 *
 * Notes: There is no validation for swtime/ctime pairs. The calling function is responsible
 *        for that.

 * Returns:
 *          - A valid return code from above.
 *
 *----------------------------------------------------------------------------*/
INT control105_205( BYTE *buf, INT *buflen, SA_CODE *scode, USHORT xmitter_addr,
                    INT row, INT column);

/*----------------------------------------------------------------------------*
 * Function: controlGolay
 *
 * buf      - output buffer. A fully formed control command is built and placed
 *              in this buffer on successful completion of the function.
 *
 * buflen   - input/output.  On entry buflen indicates the allowed size of buf
 *              On successful exit, buflen indicates the length of the completed
 *              message stored in buf.
 *
 * code     - 6-digit operational code.
 *
 * function - DCU function.
 *
 * xmitter_addr - Transmitter address
 *
 * Notes:
 *
 * Returns:
 *          - A valid return code from above.
 *
 *----------------------------------------------------------------------------*/
INT controlGolay( BYTE *buf, INT *buflen, CHAR code[], SHORT function, USHORT xmitter_addr);


/*----------------------------------------------------------------------------*
 * Function: config205
 *
 * buf      - output buffer. A fully formed control command is built and placed
 *              in this buffer on successful completion of the function.
 *
 * buflen   - input/output.  On entry buflen indicates the allowed size of buf
 *              On successful exit, buflen indicates the length of the completed
 *              message stored in buf.
 *
 * serialNum - Input: SA205 DCU serial number.
 *
 * address_slot - Input: Which of the 6 operational addresses the code represents
 *
 * new_code - Input: New operational code(6-digit) to assign into the address slot.
 *
 * xmitter_addr - Input: Transmitter address
 *
 * Notes:
 *
 * Returns:
 *          - A valid return code from above.
 *
 *----------------------------------------------------------------------------*/
INT config205( BYTE *buf, INT *buflen, CHAR serialNum[],
           USHORT address_slot, CHAR new_code[], USHORT xmitter_addr);

/*----------------------------------------------------------------------------*
 * Function: tempOutOfService205
 *
 * buf      - output buffer. A fully formed control command is built and placed
 *              in this buffer on successful completion of the function.
 *
 * buflen   - input/output.  On entry buflen indicates the allowed size of buf
 *              On successful exit, buflen indicates the length of the completed
 *              message stored in buf.
 *
 * serialNum  - DCU serial number.
 *
 * hours_out    - Number of hours to remain out of service, 0-255.
 *
 * xmitter_addr - Transmitter address
 *
 *
 * Notes:
 *      Times too large to be supported should be assigned the maximum value and
 *      PROTSA_SUCCESS_MODIFIED_PARAM returned.
 *
 * Returns:
 *          - A valid return code from above.
 *
 *----------------------------------------------------------------------------*/
INT tempOutOfService205( BYTE *buf, INT *buflen, CHAR serialNum[], INT hours_out, USHORT xmitter_addr );

/*----------------------------------------------------------------------------*
 * Function:tamperDetect205
 *
 * buf      - output buffer. A fully formed control command is built and placed
 *              in this buffer on successful completion of the function.
 *
 * buflen   - input/output.  On entry buflen indicates the allowed size of buf
 *              On successful exit, buflen indicates the length of the completed
 *              message stored in buf.
 *
 * serialNum  - Input: SA205 DCU serial number.
 *
 * relay - Input: valid entries are 1 or 2 only. Invalid input will result in
 *         returning PROTSAERROR_BAD_PARAMETER.
 *
 * tdCount - Input: Tamper Detect Count, 0-255
 *
 * xmitter_addr - Transmitter address
 *
 *
 * Notes:
 *      If tdCount > 255, 255 will be used and PROTSA_SUCCESS_MODIFIED_PARAM
 *      returned.
 *      If tdCount < 0, 0 will be used and PROTSA_SUCCESS_MODIFIED_PARAM
 *      returned.
 *
 * Returns:
 *          - A valid return code from above.
 *
 *----------------------------------------------------------------------------*/
INT tamperDetect205(BYTE *codeBuf, INT *codeIndex, CHAR serialNum[],
                      USHORT relay, INT tdCount, USHORT xmitter_addr);

/*----------------------------------------------------------------------------*
 * Function:coldLoadPickup205
 *
 * buf      - output buffer. A fully formed control command is built and placed
 *              in this buffer on successful completion of the function.
 *
 * buflen   - input/output.  On entry buflen indicates the allowed size of buf
 *              On successful exit, buflen indicates the length of the completed
 *              message stored in buf.
 *
 * serialNum  - Input: SA205 DCU serial number.
 *
 * relay - Input: valid entries are 1, 2, 3 or 4 only. Invalid input will
 *        result in returning PROTSAERROR_BAD_PARAMETER.
 *
 * clpCount - Input:Cold Load Pickup Count, 0-255, 1 count = 14.0616seconds
 *
 * xmitter_addr - Transmitter address
 *
 *
 * Notes:
 *      If clpCount > 255, 255 will be used and PROTSA_SUCCESS_MODIFIED_PARAM
 *      returned.
 *
 * Returns:
 *          - A valid return code from above.
 *
 *----------------------------------------------------------------------------*/
INT coldLoadPickup205(BYTE *codeBuf, INT *codeIndex, CHAR serialNum[],
                      USHORT relay, INT clpCount, USHORT xmitter_addr);

/*----------------------------------------------------------------------------*
 * Function:formatTMScmd
 *
 * abuf      - output buffer. A fully formed TMS command in ASCII is built and placed
 *              in this buffer on successful completion of the function.
 *
 * buflen   - input/output.  On entry buflen indicates the allowed size of buf.
 *              On successful exit, buflen indicates the length of the completed
 *              message stored in buf.
 *
 * TMS_cmd_type - Input: TMS command type as defined above.
 *
 * xmitter  - input: transmitter address
 *
 * Returns:
 *          - A valid return code.
 *----------------------------------------------------------------------------*/
INT formatTMScmd (UCHAR *abuf, INT *buflen, USHORT TMS_cmd_type, USHORT xmitter);

/*----------------------------------------------------------------------------*
 * Function:TMSlen
 *
 * abuf  - input: first 8 bytes of TMS response in ASCII.
 *
 * len   - output:  additional bytes to receive
 *
 * Returns:
 *          - A valid return code.
 *----------------------------------------------------------------------------*/
INT TMSlen (UCHAR *abuf, INT *len);

/*----------------------------------------------------------------------------*
 * Function:procTMSmsg
 *
 * abuf     - input ASCII buffer received from TMS.
 * len      - input: reference length when convert ASCII to binary
 * scode    - output if buf contains control code.
 * x205cmd  - output if buf contains SA205 config command.
 * Returns:
 *          - TMS_EMPTY,
 *            TMS_205CMD if abuf contains SA205 config command,
 *            TMS_CODE abuf contains control code,
 *            TMS_UNKNOWN.
 *----------------------------------------------------------------------------*/
INT procTMSmsg(UCHAR *abuf, INT len, SA_CODE *scode, X205CMD *x205cmd);

/*----------------------------------------------------------------------------*
 * Function: lastSAError
 *
 * buf      - output buffer. A fully formed null terminated c-style string which
 *              is indicates any textual error condition a user of this library
 *              may wish to know.
 *
 * buflen   - input/output.  On entry buflen indicates the allowed size of buf
 *              On successful exit, buflen indicates the length of the completed
 *              message stored in buf.
 *
 * Notes:
 *      Data is temporal and will be assumed to be non thread safe.
 *
 * Returns:
 *          - A valid return code from above.
 *
 *----------------------------------------------------------------------------*/
INT lastSAError( CHAR *buf, INT *buflen );

#endif // #ifndef __PROTOCOL_SA_H__

