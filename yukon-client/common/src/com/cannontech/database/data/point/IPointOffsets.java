package com.cannontech.database.data.point;

import com.cannontech.database.data.pao.PAOGroups;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public interface IPointOffsets 
{

   public static final int PT_OFFSET_TOTAL_KWH				= 1;
   
   public static final int PT_OFFSET_KW_DEMAND				= 1;
   public static final int PT_OFFSET_VOLTAGE_DEMAND			= 4;
   public static final int PT_OFFSET_PEAK_KW_DEMAND			= 11;
   public static final int PT_OFFSET_MAX_VOLT_DEMAND 		= 14;
   public static final int PT_OFFSET_MIN_VOLT_DEMAND 		= 15;
   public static final int PT_OFFSET_TOTAL_KVARH			= 21;
   public static final int PT_OFFSET_LP_KW_DEMAND			= 15;
   public static final int PT_OFFSET_KVAR_DEMAND			= 35;
	
   public static final int PT_OFFSET_LPROFILE_KW_DEMAND   = 101;
   public static final int PT_OFFSET_LPROFILE_VOLTAGE_DEMAND = 104;
		
   public static final int PT_OFFSET_TRANS_STATUS			= 2000;

   public static final int PT_OFFSET_DAILY_HISTORY			= 2500;
   public static final int PT_OFFSET_MONTHLY_HISTORY		= 2501;
   public static final int PT_OFFSET_SEASONAL_HISTORY		= 2502;
   public static final int PT_OFFSET_ANNUAL_HISTORY		= 2503;
   
   

   public static final int PT_OFFSET_BILLING_BASELINE		= 6000;	//Billing Point, curtailment settlement (CSVBilling)
   
   
   
   
   public static final PointOffset[] ALL_POINT_OFFSETS =
   {
		//Offsets for DEVICES
		new PointOffset( PAOGroups.MCT410IL, PointTypes.PULSE_ACCUMULATOR_POINT,
					new int[]{1,20},
					new String[] {"kWh","Power Fail Count"} ),
		new PointOffset( PAOGroups.MCT410IL, PointTypes.DEMAND_ACCUMULATOR_POINT, 
					new int[]{1,4,11,14,15,101,104},
					new String[] {"kW","Peak kW","Voltage","Max Volts","Min Volts",
								"Load profile kW demand","Load profile voltage"} ),

		new PointOffset( PAOGroups.MCT370, PointTypes.STATUS_POINT, 
					new int[]{5,6,10,11,12,2000},
					new String[] {"Status of A relay","Status of B relay","Power Fail Flag",
						"Short Power Fail Flag","Over Flow Flag","Communication Status (CVD)"} ),
		new PointOffset( PAOGroups.MCT370, PointTypes.ANALOG_POINT,
					new int[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30},
					new String[] {"Total KWH","Peak KW (Rate A)","Rate A KWH","Rate B KW","Rate B KWH","Rate C KW","Rate C KWH","Rate D KW",
						"Rate D KWH","Last Interval KW","Total KVARH","Peak KVAR (Rate A)","Rate A KVARH","Rate B KVAR","Rate B KVARH","Rate C KVAR",
						"Rate C KVARH","Rate D KVAR","Rate D KVAR","Last Interval KVARH","Total KVAH","Peak KVA (Rate A)","Rate A KVAH","Rate B KVA",
						"Rate B KVAH","Rate C KVA","Rate C KVAH","Rate D KVA","Rate D KVAH","Last Interval KVA"} ),
		new PointOffset( PAOGroups.MCT370, PointTypes.PULSE_ACCUMULATOR_POINT,
					new int[]{1,2,3,4,5,6,20},
					new String[] {"PI #1 (Current)","PI #2 (Current)","PI #3 (Current)","PI #1 (Frozen)",
						"PI #2 (Frozen)","PI #3 (Frozen)","Power Fail Count"} ),
		new PointOffset( PAOGroups.MCT370, PointTypes.DEMAND_ACCUMULATOR_POINT,
					new int[]{1,2,3,10,11,12,13,16,17},
					new String[] {"PI #1-Demand","PI #2-Demand","PI #3-Demand","PI input 1 Max demand (MNMX mode) or PI input 1 off peak demand (LMPK mode)",
						"PI input 1 Min demand (MNMX mode) or PI input 1 on peak demand (LMPK mode)",
						"PI input 2 Max demand (MNMX mode) or PI input 2 off peak demand (LMPK mode)",
						"PI input 2 Min demand (MNMX mode) or PI input 2 on peak demand (LMPK mode)",
						"PI input 3 Max demand (MNMX mode) or PI input 3 off peak demand (LMPK mode)",
						"PI input 3 Min demand (MNMX mode) or PI input 3 on peak demand (LMPK mode)"} ),
		new PointOffset( PAOGroups.MCT370, PointTypes.CONTROLTYPE_NORMAL,
					new int[]{1},
					new String[] {"Control relays A/B"} ),

		new PointOffset( PAOGroups.MCT360, PointTypes.STATUS_POINT, 
					new int[]{5,6,10,11,12,2000},
					new String[] {"Status of A relay","Status of B relay","Power Fail Flag",
						"Short Power Fail Flag","Over Flow Flag","Communication Status (CVD)"} ),
		new PointOffset( PAOGroups.MCT360, PointTypes.ANALOG_POINT,
					new int[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30},
					new String[] {"Total KWH","Peak KW (Rate A)","Rate A KWH","Rate B KW","Rate B KWH","Rate C KW","Rate C KWH","Rate D KW",
						"Rate D KWH","Last Interval KW","Total KVARH","Peak KVAR (Rate A)","Rate A KVARH","Rate B KVAR","Rate B KVARH","Rate C KVAR",
						"Rate C KVARH","Rate D KVAR","Rate D KVAR","Last Interval KVARH","Total KVAH","Peak KVA (Rate A)","Rate A KVAH","Rate B KVA",
						"Rate B KVAH","Rate C KVA","Rate C KVAH","Rate D KVA","Rate D KVAH","Last Interval KVA"} ),
			
		new PointOffset( PAOGroups.MCT318, PointTypes.STATUS_POINT, 
					new int[]{5,6,10,11,12,2000},
					new String[] {"Status of A relay","Status of B relay","Power Fail Flag",
						"Short Power Fail Flag","Over Flow Flag","Communication Status (CVD)"} ),
		new PointOffset( PAOGroups.MCT318, PointTypes.PULSE_ACCUMULATOR_POINT,
					new int[]{1,2,3,4,5,6,20},
					new String[] {"PI #1 (Current)","PI #2 (Current)","PI #3 (Current)","PI #1 (Frozen)",
						"PI #2 (Frozen)","PI #3 (Frozen)","Power Fail Count"} ),
		new PointOffset( PAOGroups.MCT318, PointTypes.DEMAND_ACCUMULATOR_POINT,
					new int[]{1,2,3,10,11,12,13,14,15},
					new String[] {"PI #1-Demand","PI #2-Demand","PI #3-Demand","PI input 1 Max demand (MNMX mode) or PI input 1 off peak demand (LMPK mode)",
						"PI input 1 Min demand (MNMX mode) or PI input 1 on peak demand (LMPK mode)",
						"PI input 2 Max demand (MNMX mode) or PI input 2 off peak demand (LMPK mode)",
						"PI input 2 Min demand (MNMX mode) or PI input 2 on peak demand (LMPK mode)",
						"PI input 3 Max demand (MNMX mode) or PI input 3 off peak demand (LMPK mode)",
						"PI input 3 Min demand (MNMX mode) or PI input 3 on peak demand (LMPK mode)"} ),
		new PointOffset( PAOGroups.MCT318, PointTypes.CONTROLTYPE_NORMAL,
					new int[]{1},
					new String[] {"Control relays A/B"} ),

		new PointOffset( PAOGroups.MCT310IL, PointTypes.DEMAND_ACCUMULATOR_POINT, 
					new int[]{101},
					new String[] {"Load profile kW demand"} ),
		new PointOffset( PAOGroups.MCT310IL, PointTypes.STATUS_POINT,
					new int[]{1},
					new String[] {"Disconnect status"} ),

		new PointOffset( PAOGroups.MCT310IDL, PointTypes.DEMAND_ACCUMULATOR_POINT, 
					new int[]{101},
					new String[] {"Load profile kW demand"} ),
		new PointOffset( PAOGroups.MCT310IDL, PointTypes.STATUS_POINT,
					new int[]{1},
					new String[] {"Disconnect status"} ),

		new PointOffset( PAOGroups.MCT310, PointTypes.STATUS_POINT, 
					new int[]{5,6,10,11,12,2000},
					new String[] {"Status of A relay","Status of B relay","Power Fail Flag",
						"Short Power Fail Flag","Over Flow Flag","Communication Status (CVD)"} ),
		new PointOffset( PAOGroups.MCT310, PointTypes.PULSE_ACCUMULATOR_POINT,
					new int[]{1,2,3,4,5,6,20},
					new String[] {"PI #1 (Current)","PI #2 (Current)","PI #3 (Current)","PI #1 (Frozen)",
						"PI #2 (Frozen)","PI #3 (Frozen)","Power Fail Count"} ),
		new PointOffset( PAOGroups.MCT310, PointTypes.DEMAND_ACCUMULATOR_POINT,
					new int[]{1,2,3},
					new String[] {"PI #1-Demand","PI #2-Demand","PI #3-Demand"} ),

		new PointOffset( PAOGroups.MCT250, PointTypes.STATUS_POINT, 
					new int[]{1,2,3,4,9,10,11,12,13,14,2000},
					new String[] {"Status input 1","Status input 2","Status input 3","Status input 4","Time Sync Flag","Power Fail Flag",
						"Short Power Fail Flag","Over Flow Flag","Load Survey Active Flag","Tamper Flag","Communication Status (CVD)"} ),
		new PointOffset( PAOGroups.MCT250, PointTypes.PULSE_ACCUMULATOR_POINT,
					new int[]{1,4,7,20},
					new String[] {"PI #1 (Most Recent Register)","PI #1 (Previous Register)","PI #1 (Resettable PI Register)","Power Fail Count"}),
		new PointOffset( PAOGroups.MCT250, PointTypes.DEMAND_ACCUMULATOR_POINT,
					new int[]{1},
					new String[] {"PI #1-Demand"} ),

		new PointOffset( PAOGroups.MCT248, PointTypes.STATUS_POINT, 
					new int[]{1,9,10,11,12,13,14,2000},
					new String[] {"Control Point","Time Sync Flag","Power Fail Flag",
						"Short Power Fail Flag","Over Flow Flag","Load Survey Active Flag","Tamper Flag","Communication Status (CVD)"} ),
		new PointOffset( PAOGroups.MCT248, PointTypes.PULSE_ACCUMULATOR_POINT,
					new int[]{1,4,20},
					new String[] {"PI #1 (Most Recent Register)","PI #1 (Previous Register)","Power Fail Count"} ),
		new PointOffset( PAOGroups.MCT248, PointTypes.DEMAND_ACCUMULATOR_POINT,
					new int[]{1},
					new String[] {"PI #1-Demand"} ),
		new PointOffset( PAOGroups.MCT248, PointTypes.CONTROLTYPE_NORMAL,
					new int[]{1},
					new String[] {"Control relays A/B"} ),

		new PointOffset( PAOGroups.MCT240, PointTypes.STATUS_POINT, 
					new int[]{9,10,11,12,13,14,2000},
					new String[] {"Time Sync Flag","Power Fail Flag",
						"Short Power Fail Flag","Over Flow Flag","Load Survey Active Flag","Tamper Flag","Communication Status (CVD)"} ),
		new PointOffset( PAOGroups.MCT240, PointTypes.PULSE_ACCUMULATOR_POINT,
					new int[]{1,4,20},
					new String[] {"PI #1 (Most Recent Register)","PI #1 (Previous Register)","Power Fail Count"} ),
		new PointOffset( PAOGroups.MCT240, PointTypes.DEMAND_ACCUMULATOR_POINT,
					new int[]{1},
					new String[] {"PI #1-Demand"} ),

		new PointOffset( PAOGroups.MCT213, PointTypes.STATUS_POINT, 
					new int[]{1,10,12,14,2000},
					new String[] {"Disconnect Status (3 State: Open, Close, Transition)","Power Fail Flag",
						"Over Flow Flag","Tamper Flag","Communication Status (CVD)"} ),
		new PointOffset( PAOGroups.MCT213, PointTypes.PULSE_ACCUMULATOR_POINT,
					new int[]{1,4,20},
					new String[] {"PI #1 (Most Recent Register)","PI #1 (Previous Register)","Power Fail Count"} ),
		new PointOffset( PAOGroups.MCT213, PointTypes.DEMAND_ACCUMULATOR_POINT,
					new int[]{1},
					new String[] {"PI #1-Demand"} ),
		new PointOffset( PAOGroups.MCT213, PointTypes.CONTROLTYPE_NORMAL,
					new int[]{1},
					new String[] {"Connect/Disconnect"} ),
			
		new PointOffset( PAOGroups.MCT210, PointTypes.STATUS_POINT, 
					new int[]{10,12,14},
					new String[] {"Power Fail Flag","Over Flow Flag","Tamper Flag"} ),
		new PointOffset( PAOGroups.MCT210, PointTypes.PULSE_ACCUMULATOR_POINT,
					new int[]{1,4,20},
					new String[] {"PI #1 (Most Recent Register)","PI #1 (Previous Register)","Power Fail Count"} ),
		new PointOffset( PAOGroups.MCT210, PointTypes.DEMAND_ACCUMULATOR_POINT,
					new int[]{1},
					new String[] {"PI #1-Demand"} ),

		new PointOffset( PAOGroups.RTUILEX, PointTypes.STATUS_POINT, 
					new int[]{1,2,3,4,5,6,7,8},
					new String[] {"Status Input #1","Status Input #2","Status Input #3","Status Input #4",
						"Status Input #5","Status Input #6","Status Input #7","Status Input #8"} ),
		new PointOffset( PAOGroups.RTUILEX, PointTypes.PULSE_ACCUMULATOR_POINT,
					new int[]{1,2,3,4,5,6,7,8},
					new String[] {"PI input #1 (Form A)","PI input #2 (Form A)","PI input #3 (Form A)","PI input #4 (Form A)",
						"PI input #5 (Form A)","PI input #6 (Form A)","PI input #7 (Form A)","PI input #8 (Form A)"} ),
		new PointOffset( PAOGroups.RTUILEX, PointTypes.DEMAND_ACCUMULATOR_POINT,
					new int[]{1,2,3,4,5,6,7,8},
					new String[] {"PI input #1 (Form A)","PI input #2 (Form A)","PI input #3 (Form A)","PI input #4(Form A)",
						"PI input #5 (Form A)","PI input #6 (Form A)","PI input #7 (Form A)","PI input #8 (Form A)"} ),
		new PointOffset( PAOGroups.RTUILEX, PointTypes.ANALOG_POINT,
					new int[]{1,2,3,4,5,6,7,8,9},
					new String[] {"Analog #1","Analog #2","Analog #3","Analog #4","Analog #5","Analog #6","Minus 90% reference",
							"Plus 90% reference","Zero reference"} ),
		new PointOffset( PAOGroups.RTUILEX, PointTypes.CONTROLTYPE_NORMAL,
					new int[]{1,2},
					new String[] {"Relay 1 and 2","Relay 3 and 4"} ),

		new PointOffset( PAOGroups.TCU5000, PointTypes.STATUS_POINT, 
					new int[]{17,18,19,20,21,23,24,25,26,29},
					new String[] {"Local Operation","Alarms","Queue Full (overflowed when set)","COP Reset","Message Verification",
						"Transmitter alarm","Power restored since last status","Clock unlock","Clock fail","Paging terminal interface full"} ),

		new PointOffset( PAOGroups.TCU5500, PointTypes.STATUS_POINT, 
					new int[]{17,18,19,20,21,23,24,25,26,27,28,29},
					new String[] {"Local Operation","Alarms","Queue Full (overflowed when set)","COP Reset","Message Verification",
						"Transmitter alarm","Power restored since last status","Clock unlock","Clock fail",
						"VHF Channel Bust","VHF Tx inhibit","Paging terminal interface full"} ),

		new PointOffset( PAOGroups.CCU710A, PointTypes.STATUS_POINT, 
					new int[]{2000},
					new String[] {"Communication Status (CVD)"} ),

		new PointOffset( PAOGroups.CCU711, PointTypes.STATUS_POINT, 
					new int[]{2000},
					new String[] {"Communication Status (CVD)"} ),

		new PointOffset( PAOGroups.REPEATER, PointTypes.STATUS_POINT, 
					new int[]{2000},
					new String[] {"Communication Status (CVD)"} ),

		new PointOffset( PAOGroups.DCT_501, PointTypes.STATUS_POINT, 
					new int[]{1,2,9,10,11,12,13,14,16,2000},
					new String[] {"Status Input 1","Status Input 2","Time Sync Flag","Power Fail Flag",
						"Short Power Fail Flag","Over Flow","Remote/Local Status","TOS - Status","TOS Mode","Communication Status (CVD)"} ),
		new PointOffset( PAOGroups.DCT_501, PointTypes.ANALOG_POINT,
					new int[]{1,2,3,4},
					new String[] {"Analog #1","Analog #2","Analog #3","Analog #4"} ),
		new PointOffset( PAOGroups.DCT_501, PointTypes.CONTROLTYPE_NORMAL,
					new int[]{1},
					new String[] {"C-Relay/D-Relay"} ),

		new PointOffset( PAOGroups.LMT_2, PointTypes.STATUS_POINT, 
					new int[]{1,2,9,10,11,12,13,14,15,16,2000},
					new String[] {"Status Point #1","Status Point #2","Time Sync Flag","Power Fail Flag",
						"Short Power Fail Flag","Over Flow Flag","Load Survey Active Flag","Customer Over Ride Flag",
						"Tou Active Flag","Tou Rate Status","Communication Status (CVD)"} ),
		new PointOffset( PAOGroups.LMT_2, PointTypes.PULSE_ACCUMULATOR_POINT,
					new int[]{1,2,3,4,7,8,9},
					new String[] {"PI #1 (Most Recent Register)","PI #2","PI #3","PI #1 (Previous Register)",
						"Off-Peak TOU Cons.","Mid-Peak TOU Cons.","Peak TOU Cons."} ),
		new PointOffset( PAOGroups.LMT_2, PointTypes.DEMAND_ACCUMULATOR_POINT,
					new int[]{1,4,5,6},
					new String[] {"PI #1-Demand","PI #1 - Off-Peak Demand (TOU)",
						"PI #1 - Mid-Peak Demand (TOU)","PI #1 - Peak Demand (TOU)"} ),
		new PointOffset( PAOGroups.LMT_2, PointTypes.CONTROLTYPE_NORMAL,
					new int[]{1},
					new String[] {"C-Relay/D-Relay"} ),

		new PointOffset( PAOGroups.LCU415, PointTypes.STATUS_POINT, 
					new int[]{1,2,3,4,5,6,7,8,17,18,19,24,26,27,2000},
					new String[] {"Status Input 1","Status Input 2","Status Input 3","Status Input 4","Status Input 5","Status Input 6",
						"Status Input 7","Status Input 8","Local Operation","Injector Alarm","Busy Transmitting",
						"Power Fail","Output disabled","Injector staged","Communication Status (CVD)"} ),
		new PointOffset( PAOGroups.LCU415, PointTypes.DEMAND_ACCUMULATOR_POINT,
					new int[]{1},
					new String[] {"PI #1"} ),
		new PointOffset( PAOGroups.LCU415, PointTypes.ANALOG_POINT,
					new int[]{1,2,3,4,5,6,7,8},
					new String[] {"Analog #1","Analog #2","Analog #3","Analog #4","Analog #5","Analog #6","Analog #7","Analog #8"} ),


		//Offsets for CAP_CONTROL
		new PointOffset( "CAP CONTROL SUB BUS", PAOGroups.CAP_CONTROL_SUBBUS, PointTypes.ANALOG_POINT, 
					new int[]{1,2,3,4},
					new String[] {"Estimated Var Load","Daily Operations","Power Factor","Estimated Power Factor"} ),

		new PointOffset( "CAP CONTROL FEEDER", PAOGroups.CAP_CONTROL_FEEDER, PointTypes.ANALOG_POINT, 
					new int[]{1,2,3,4},
					new String[] {"Estimated Var Load","Daily Operations","Power Factor","Estimated Power Factor"} ),   	

		new PointOffset( PAOGroups.CAPBANK, PointTypes.ANALOG_POINT, 
					new int[]{1},
					new String[] {"Total Operations"} ),   	
		new PointOffset( PAOGroups.CAPBANK, PointTypes.STATUS_POINT, 
					new int[]{1},
					new String[] {"Eight State Capbank Status"} ),


		//Offsets for LOAD_CONTROL
		new PointOffset( PAOGroups.LM_CONTROL_AREA, PointTypes.STATUS_POINT, 
					new int[]{1},
					new String[] {"Control Area Control Status"} ),

		new PointOffset( PAOGroups.LM_DIRECT_PROGRAM, PointTypes.STATUS_POINT, 
					new int[]{1},
					new String[] {"Program Control Status"} ),   	

		new PointOffset( "LM GROUPS", PAOGroups.LM_GROUP_VERSACOM, PointTypes.ANALOG_POINT, 
					new int[]{2500,2501,2502,2503},
					new String[] {"Daily Control Hours (in seconds)","Monthly Control Hours (in seconds)",
						"Seasonal Control Hours (in seconds)","Annual Control Hours (in seconds)"} ),   	
		new PointOffset( "LM GROUPS", PAOGroups.LM_GROUP_VERSACOM, PointTypes.STATUS_POINT,
					new int[]{1},
					new String[] {"Controlable status point for latching gears"} ),




		//Offset for remaining YUKON points
		new PointOffset( "BILLING", PAOGroups.INVALID, PointTypes.ANALOG_POINT, 
					new int[]{6000},
					new String[] {"Base line, curtailment settlement (CSVBilling)"} ),

		new PointOffset( "TRANSMITTERS", PAOGroups.INVALID, PointTypes.STATUS_POINT,
					new int[]{2000},
					new String[] {"Comm. status point for transmitters"} ),

   };

}
