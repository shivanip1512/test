
/******************************************************************************
        Filename: $RCSfile: apiclipadq.h,v $
        Revision: $Revision: 1.1 $
        Author:   $Author: eschmit $
        Date:     $Date: 2002/07/15 14:04:35 $
*******************************************************************************
$Log: apiclipadq.h,v $
Revision 1.1  2002/07/15 14:04:35  eschmit
Telegyr stuff added

Revision 1.2  2000/07/04 08:50:33  imhoff
changes for new API functions (TG8000_SMR_FI_00038.doc)

*******************************************************************************/

/* -------------------------------------------------------------------------- */
/* Include file for routines api_rts_write_xxx to define the allowed          */
/* ANALOG, STATUS and ACCUM data qualities and point attributes.              */
/* -------------------------------------------------------------------------- */

#ifndef	APICLIPADQ_H
#define	APICLIPADQ_H

/* ***************** Masks for fields to be altered ******************* */
/*      These must match the definitions in /elinc/eldacsset.h 		*/
/* ******************************************************************** */

/* ******************************************************************** */
#define	SET_STATE_APIC	  0x00000001  /* Alter state field		*/
#define	SET_INVERSN_APIC  0x00000002  /* Alter inverse video field 	*/
#define	SET_BLANK_APIC	  0x00000004  /* Alter blank field		*/
#define	SET_OUT_SCAN_APIC 0x00000008  /* Alter out of scan field 	*/
#define	SET_TELEMF_APIC	  0x00000010  /* Alter telemetry failure field 	*/
#define	SET_MAN_ENT_APIC  0x00000020  /* Alter manual entry field 	*/
#define	SET_QMAN_ENT_APIC 0x00000040  /* Alter propag. man entry field  */
#define	SET_BACKUP_APIC	  0x00000080  /* Alter backup field 		*/
#define	SET_INH_ALRM_APIC 0x00000100  /* Alter alarm inhibit flags 	*/
#define	SET_QUESTION_APIC 0x00000200  /* Alter questionable value flag 	*/
#define	SET_REASON_F_APIC 0x00000400  /* Alter reason. failure flags 	*/
#define	SET_FLOAT_EX_APIC 0x00000800  /* Alter float. point excpt flag 	*/
#define	SET_INH_WARN_APIC 0x00001000  /* Alter warning inhibit flags 	*/
#define	SET_VALUE_APIC	  0x00002000  /* Alter value field 		*/
#define	SET_TEST_APIC	  0x00004000  /* Alter test field 		*/
#define	SET_SE_BAD_APIC	  0x00008000  /* Alter state estimate bad flag 	*/
#define	SET_SE_UPDAT_APIC 0x00010000  /* Alter state estimate repl flag */
#define	SET_UN_INIT_APIC  0x00020000  /* Alter uninitialized flag 	*/
#define	SET_DRIFT_APIC	  0x00040000  /* Alter drift flag 		*/
#define	SET_MISSING_APIC  0x00080000  /* Alter missing flag 		*/
#define	SET_ALARM_APIC	  0x00100000  /* Alarm code argument valid 	*/
#define	SET_LOCAL_APIC	  0x00200000  /* Alter local control flag 	*/
/* ******************************************************************** */

/* Quality bit definitions matches the ones in /lg/d_qual_macros.h */
/* --------------------------------------------------------------- */
#define 	API_QUAL_OUT_SCAN_BIT	 0		 /* bit 0  */
#define 	API_QUAL_TELEM_F_BIT 	 1		 /* bit 1  */
#define 	API_QUAL_MAN_BIT	 2		 /* bit 2  */
#define 	API_QUAL_Q_MAN_BIT	 3		 /* bit 3  */
#define 	API_QUAL_TEST_BIT	 4		 /* bit 4  */
#define 	API_QUAL_BACKUP_BIT	 5		 /* bit 5  */
#define 	API_QUAL_IN_CONT_BIT	 6		 /* bit 6  */
#define 	API_QUAL_TAG_BIT	 7		 /* bit 7  */
#define 	API_QUAL_INH_ALM_BIT	 8		 /* bit 8  */
#define 	API_QUAL_SE_BAD_BIT	 9		 /* bit 9  */
#define 	API_QUAL_SE_UPDATE_BIT	 10		 /* bit 10 */
#define 	API_QUAL_NET_TOP_BIT	 11		 /* bit 11 */
#define 	API_QUAL_RESV1_BIT	 12		 /* bit 12 */
#define 	API_QUAL_RESV2_BIT	 13		 /* bit 13 */
#define 	API_QUAL_UN_INIT_BIT	 14		 /* bit 14 */
#define 	API_QUAL_DBVU_FMT_BIT	 15		 /* bit 15 */
#define 	API_QUAL_NOTELEMF_BIT	 16		 /* bit 16 */
/* not defined */                                        /* bit 17 */
#define 	API_QUAL_QUESTION_BIT	 18		 /* bit 18 */
#define 	API_QUAL_MISSING_BIT	 19		 /* bit 19 */
#define 	API_QUAL_DEV_CMNT_BIT	 20		 /* bit 20 */
#define 	API_QUAL_CMD_FAIL_BIT	 21		 /* bit 21 */ 

/* ANALOG TABLE specific data qualities */
/* ------------------------------------ */
#define 	API_QUAL_LOCAL_BIT	 22		 /* bit 22 */ 
#define 	API_QUAL_HI_WARNING_BIT	 23		 /* bit 23 */
#define 	API_QUAL_LOW_WARNING_BIT 24		 /* bit 24 */
#define 	API_QUAL_DRIFT_BIT 	 25		 /* bit 25 */
#define 	API_QUAL_REASON_F_BIT 	 26		 /* bit 26 */
#define 	API_QUAL_FLOAT_EX_BIT	 27		 /* bit 27 */
#define 	API_QUAL_OVERRIDE_BIT	 28		 /* bit 28 */
#define 	API_QUAL_HI_ALARM_BIT	 29		 /* bit 29 */
#define 	API_QUAL_LOW_ALARM_BIT	 30		 /* bit 30 */
#define 	API_QUAL_INH_WARN_BIT	 31		 /* bit 31 */

/* STATUS TABLE specific data qualities */
/* ------------------------------------ */
#define 	API_QUAL_LOCAL_BIT	 22		 /* bit 22 */
#define 	API_QUAL_INH_SOE_BIT	 31		 /* bit 31 */


#define 	API_QUAL_MIN_BIT	 0		/* bit 0  */
#define 	API_QUAL_MAX_BIT	 31		/* bit 31 */
#define         API_QUAL_ALL_BIT_MASK    0xffffffff

/* -------------------------------------------------------------------------- */

#endif /* APICLIPADQ_H */
