package com.cannontech.analysis.data.statistic;


/**
 * Created on Dec 15, 2003
 * StatisticData interface TableModel object 
 * All statistical models MUST implement this interface!
 * @author snebben
 */
public class StatisticData
{	
	/** Valid statType from DynamicPaoStatistics.statisticalType value critera, null results in all?*/
	public static final String DAILY_STAT_TYPE = "Daily";
	public static final String YESTERDAY_STAT_TYPE = "Yesterday";	
	public static final String MONTHLY_STAT_TYPE = "Monthly";
	public static final String LAST_MONTH_STAT_TYPE = "LastMonth";
	
	
	private String paoName = null;
	private Integer attempts = null;
	private Integer commErrors = null;
	private Integer systemErrors = null;
	private Integer protocolErrs = null;
	private Integer completions = null;
	private Integer requests = null;
	
	/** (attempts - commErrors) / attempts */
	private Double portPercent = null;
	
	/** completions / requests */
	private Double successPercent = null;
	
	/** systemErrors + protocolErrors + commErrors */
	private Integer totalErrs = null;
	
	/** totalErrors / attempts */
	private Double commErrPercent = null;
	
	/** attempts - commErrors - systemErrors */
	private Integer dlcAttempts = null;
	
	/** (attempts - protocolErrors) / attempts */
	private Double dlcPercent = null;
	
	/**
	 * @param paoName_
	 * @param attempts_
	 * @param commErrors_
	 * @param systemErrors_
	 * @param protocolErrs_
	 * @param completions_
	 * @param requests_
	 */
	public StatisticData(String paoName_, Integer attempts_, Integer commErrors_, Integer systemErrors_, Integer protocolErrs_, Integer completions_, Integer requests_)
	{	
		setPAOName( paoName_);
		setAttempts( attempts_);
		setCommErrors( commErrors_);
		setSystemErrors( systemErrors_);
		setProtocolErrs( protocolErrs_);
		setCompletions( completions_);
		setRequests( requests_);
	}			

	/**
	 * Default constructor.
	 */
	public StatisticData()
	{
		super();
	}

	/**
	 * @return attempts
	 */
	public Integer getAttempts()
	{
		return attempts;
	}

	/**
	 * @return
	 */
	public Integer getCommErrors()
	{
		return commErrors;
	}

	/**
	 * @return
	 */
	public Integer getCompletions()
	{
		return completions;
	}

	/**
	 * @return
	 */
	public String getPAOName()
	{
		return paoName;
	}

	/**
	 * @return
	 */
	public Integer getProtocolErrs()
	{
		return protocolErrs;
	}

	/**
	 * @return
	 */
	public Integer getRequests()
	{
		return requests;
	}

	/**
	 * @return
	 */
	public Integer getSystemErrors()
	{
		return systemErrors;
	}

	/**
	 * @param integer
	 */
	public void setAttempts(Integer integer)
	{
		attempts = integer;
	}

	/**
	 * @param integer
	 */
	public void setCommErrors(Integer integer)
	{
		commErrors = integer;
	}

	/**
	 * @param integer
	 */
	public void setCompletions(Integer integer)
	{
		completions = integer;
	}

	/**
	 * @param string
	 */
	public void setPAOName(String string)
	{
		paoName = string;
	}

	/**
	 * @param integer
	 */
	public void setProtocolErrs(Integer integer)
	{
		protocolErrs = integer;
	}

	/**
	 * @param integer
	 */
	public void setRequests(Integer integer)
	{
		requests = integer;
	}

	/**
	 * @param integer
	 */
	public void setSystemErrors(Integer integer)
	{
		systemErrors = integer;
	}
	
	/**
	 * @return
	 */
	public Double getPortPercent()
	{
		if (portPercent == null)
			portPercent =new Double((getAttempts().doubleValue() - getCommErrors().doubleValue()) / getAttempts().doubleValue());
		return portPercent;
	}

	/**
	 * @return
	 */
	public Double getSuccessPercent()
	{
		if( successPercent == null)
			successPercent = new Double((getCompletions().doubleValue()/ getRequests().doubleValue()));
		return successPercent;
	}

	/**
	 * @return
	 */
	public Double getCommErrPercent()
	{
		if( commErrPercent == null)
			commErrPercent = new Double (getTotalErrs().doubleValue() / getAttempts().doubleValue());
		return commErrPercent;
	}

	/**
	 * @return
	 */
	public Integer getTotalErrs()
	{
		if (totalErrs == null)
			totalErrs = new Integer( getSystemErrors().intValue() + getProtocolErrs().intValue() + getCommErrors().intValue());
		return totalErrs;
	}
	/**
	 * @return
	 */
	public Integer getDlcAttempts()
	{
		if (dlcAttempts == null )
			dlcAttempts = new Integer( getAttempts().intValue() - getCommErrors().intValue() - getSystemErrors().intValue());
		
		return dlcAttempts;
	}

	/**
	 * @return
	 */
	public Double getDlcPercent()
	{
		if( dlcPercent == null )
			dlcPercent = new Double( (getAttempts().doubleValue() - getProtocolErrs().doubleValue()) / getAttempts().doubleValue());
		return dlcPercent;
	}	
}