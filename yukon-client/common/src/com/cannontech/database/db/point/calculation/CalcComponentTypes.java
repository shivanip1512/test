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
	
	
	
	public static final String MIN_FUNCTION= "Min";
	public static final String MAX_FUNCTION = "Max";
	public static final String BASELINE_FUNCTION = "Baseline";
	
	public static final String DEMAND_AVG15_FUNCTION = "DemandAvg15";
	public static final String DEMAND_AVG30_FUNCTION = "DemandAvg30";
	public static final String DEMAND_AVG60_FUNCTION = "DemandAvg60";
	public static final String PFACTOR_KW_KVAR_FUNCTION = "P-Factor KW/KVar";
	public static final String PFACTOR_KW_KQ_FUNCTION = "P-Factor KW/KQ";
	public static final String PFACTOR_KW_KVA_FUNCTION = "P-Factor KW/KVa";
	
   
   public static final String LABEL_KVAR = "KVAR";
   
	//our operation function strings
	public static String[] CALC_FUNCTIONS =
	{
		MIN_FUNCTION,
		MAX_FUNCTION,
		BASELINE_FUNCTION,
		DEMAND_AVG15_FUNCTION,
		DEMAND_AVG30_FUNCTION,
		DEMAND_AVG60_FUNCTION,
		PFACTOR_KW_KVAR_FUNCTION,
      PFACTOR_KW_KQ_FUNCTION,
      PFACTOR_KW_KVA_FUNCTION
	};


	//our operation symbols
	public static String[] CALC_OPERATIONS =
	{
		ADDITION_OPERATION,
		SUBTRACTION_OPERATION,
		MULTIPLICATION_OPERATION,
		DIVISION_OPERATION
	};
	
}
