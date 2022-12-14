package com.cannontech.database.db.point.calculation;

/**
 * This type was created in VisualAge.
 */

public class CalcComponentTypes
{
	public static final String OPERATION_COMP_TYPE = "Operation";
	public static final String CONSTANT_COMP_TYPE = "Constant";
	public static final String FUNCTION_COMP_TYPE = "Function";

	public static final String ADDITION_OPERATION = "+";
	public static final String SUBTRACTION_OPERATION = "-";
	public static final String MULTIPLICATION_OPERATION = "*";
	public static final String DIVISION_OPERATION = "/";
	public static final String PUSH_OPERATION = "PUSH";
	
	/*** START OF CALC FUNCTIONS  ***/
	/*
	 * These calc functions are now added to the YukonListEntry table under ListID = 100
	 * They were moved there for the ease of Corey since he loves adding new ones 
	 * whenever it tickles his fancy.
	 * If there is special functionality with a selected entry, than they will still
	 * need to be added as a constant to this class for comparison purposes.
	 */ 
	public static final int CALC_FUNCTION_LIST_ID = 100; 
	
	public static final String ADDITION_FUNCTION = "Addition";
	public static final String SUBTRACTION_FUNCTION = "Subtraction";
	public static final String MULTIPLICATION_FUNCTION = "Multiplication";
	public static final String DIVISION_FUNCTION = "Division";
	public static final String AND_FUNCTION = "Logical AND";
	public static final String OR_FUNCTION = "Logical OR";
	public static final String NOT_FUNCTION = "Logical NOT";
	public static final String XOR_FUNCTION = "Logical XOR";
	
	public static final String GREATER_THAN_FUNCTION = "Greater than";
	public static final String GREATER_THAN_EQUAL_TO_FUNCTION = "Geq than";
	public static final String LESS_THAN_FUNCTION = "Less than";
	public static final String LESS_THAN_EQUAL_TO_FUNCTION = "Leq than";
	
	public static final String MIN_FUNCTION= "Min";
	public static final String MAX_FUNCTION = "Max";
	public static final String BASELINE_FUNCTION = "Baseline";
	public static final String BASELINE_PERCENT_FUNCTION = "Baseline Percent";
	
	public static final String DEMAND_AVG15_FUNCTION = "DemandAvg15";
	public static final String DEMAND_AVG30_FUNCTION = "DemandAvg30";
	public static final String DEMAND_AVG60_FUNCTION = "DemandAvg60";
	public static final String PFACTOR_KW_KVAR_FUNCTION = "P-Factor kW/kVAr";
	public static final String PFACTOR_KW_KQ_FUNCTION = "P-Factor kW/kQ";
	public static final String PFACTOR_KW_KVA_FUNCTION = "P-Factor kW/kVA";
	
	public static final String KVAR_FROM_KWKQ_FUNCTION = "kVAr from kW/kQ";
	public static final String KVA_FROM_KWKVAR_FUNCTION = "kVA from kW/kVAr";
	public static final String KVA_FROM_KWKQ_FUNCTION = "kVA from kW/kQ";
	public static final String SQUARED_FUNCTION = "Squared";
	public static final String SQUARE_ROOT_FUNCTION = "Square Root";
	public static final String ARCTAN_FUNCTION = "ArcTan";
	public static final String MAX_DIFFERENCE = "Max Difference";
	public static final String ABS_VALUE = "Absolute Value";
   
   	public static final String KW_FROM_KVAKVAR_FUNCTION = "kW from kVA/kVAr";
   	public static final String MODULO_DIVIDE = "Modulo Divide";
   	public static final String STATE_TIMER_FUNCTION = "State Timer";

   
	//our operation function strings
	public static String[] CALC_FUNCTIONS =
	{
		ABS_VALUE,
		ADDITION_FUNCTION,
		SUBTRACTION_FUNCTION,
		MULTIPLICATION_FUNCTION,
		DIVISION_FUNCTION,
		MIN_FUNCTION,
		MAX_FUNCTION,
		MAX_DIFFERENCE,
		MODULO_DIVIDE,
		GREATER_THAN_FUNCTION,
		GREATER_THAN_EQUAL_TO_FUNCTION,
		LESS_THAN_FUNCTION,
		LESS_THAN_EQUAL_TO_FUNCTION,
		AND_FUNCTION,
		OR_FUNCTION,
		NOT_FUNCTION,
		XOR_FUNCTION,
		ARCTAN_FUNCTION,
		BASELINE_FUNCTION,
		BASELINE_PERCENT_FUNCTION,
		DEMAND_AVG15_FUNCTION,
		DEMAND_AVG30_FUNCTION,
		DEMAND_AVG60_FUNCTION,

		KVAR_FROM_KWKQ_FUNCTION,
		KVA_FROM_KWKVAR_FUNCTION,
		KVA_FROM_KWKQ_FUNCTION,
		KW_FROM_KVAKVAR_FUNCTION,
		PFACTOR_KW_KVAR_FUNCTION,
      PFACTOR_KW_KQ_FUNCTION,
      PFACTOR_KW_KVA_FUNCTION,
      SQUARED_FUNCTION,
      SQUARE_ROOT_FUNCTION,
      STATE_TIMER_FUNCTION
	};


	//our operation symbols
	public static String[] CALC_OPERATIONS =
	{
		ADDITION_OPERATION,
		SUBTRACTION_OPERATION,
		MULTIPLICATION_OPERATION,
		DIVISION_OPERATION,
		PUSH_OPERATION
	};
	
	public static String[] CALC_TYPES = 
	{
	    OPERATION_COMP_TYPE,
	    CONSTANT_COMP_TYPE,
	    FUNCTION_COMP_TYPE
	};
	
}
