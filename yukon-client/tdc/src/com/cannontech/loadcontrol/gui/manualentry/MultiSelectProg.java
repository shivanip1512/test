package com.cannontech.loadcontrol.gui.manualentry;

import com.cannontech.loadcontrol.data.IGearProgram;
import com.cannontech.loadcontrol.data.LMProgramBase;

/**
 * @author rneuharth
 *
 * A wrapper class used to allow changes to the underlying LMProgramBase
 * instance.
 */
public class MultiSelectProg 
{
	private LMProgramBase baseProgram = null;
	private boolean hasDirectGears = false;
	
	
	//any changeable fields should go below since we can not change the program bases
	//values
	
	//will stay null if we deal with a program that does not have gears
	private Integer gearNum = null;
	private Integer startDelay = null;
	private Integer stopOffset = null;
	

	/**
	 * Constructor for MultiSelectProg.
	 */
	private MultiSelectProg() {
		super();
	}

	/**
	 * Constructor for MultiSelectProg.
	 */
	public MultiSelectProg( LMProgramBase prgBase_ ) 
	{
		this();

		setBaseProgram( prgBase_ );

		//may have to change if more program types are added that have gears!
		setHasDirectGears( prgBase_ instanceof IGearProgram );
		
		
		if( hasDirectGears() )
		{
			setGearNum( 
				((IGearProgram)prgBase_).getCurrentGearNumber() );
		}
		
	}

	/**
	 * Returns the gearNum.
	 * @return Integer
	 */
	public Integer getGearNum() {
		return gearNum;
	}

	/**
	 * Sets the gearNum. Allows the gear number to change while not changing the
	 * wrapped programs current gear number.
	 * @param gearNum The gearNum to set
	 */
	public void setGearNum(Integer gearNumIndx_ ) 
	{
		//we only care about this if we are a direct program
		if( !hasDirectGears() || gearNumIndx_ == null )
			return;


		//should work all the time
		IGearProgram dirPrg = (IGearProgram)getBaseProgram();
		
		if( gearNumIndx_.intValue() <= 0 
			 || gearNumIndx_.intValue() > dirPrg.getDirectGearVector().size() )
		{
			//use zero for the index since this program does not have the index requested 
			this.gearNum = new Integer(1);
		}
		else
			this.gearNum = gearNumIndx_;
	}

	/**
	 * Returns the isDirectGear.
	 * @return boolean
	 */
	public boolean hasDirectGears()
	{
		return hasDirectGears;
	}

	/**
	 * Sets the isDirectGear.
	 * @param isDirectGear The isDirectGear to set
	 */
	private void setHasDirectGears(boolean hasDirectGears)
	{
		this.hasDirectGears = hasDirectGears;
	}

	/**
	 * Returns the baseProgram.
	 * @return LMProgramBase
	 */
	public LMProgramBase getBaseProgram()
	{
		return baseProgram;
	}

	/**
	 * Sets the baseProgram.
	 * @param baseProgram The baseProgram to set
	 */
	private void setBaseProgram(LMProgramBase baseProgram)
	{
		this.baseProgram = baseProgram;
	}

	/**
	 * @return
	 */
	public Integer getStopOffset()
	{
		return stopOffset;
	}

	/**
	 * @return
	 */
	public Integer getStartDelay()
	{
		return startDelay;
	}

	/**
	 * @param integer
	 */
	public void setStopOffset(Integer integer)
	{
		stopOffset = integer;
	}

	/**
	 * @param integer
	 */
	public void setStartDelay(Integer integer)
	{
		startDelay = integer;
	}

}
