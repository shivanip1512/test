package com.cannontech.loadcontrol.data;

/**
 * @author rneuharth
 */
public class LMProgramThermostatGear extends LMProgramDirectGear 
{
	private String settings = null;
	private Integer minValue = new Integer(0);
	private Integer maxValue = new Integer(0);
	private Integer valueB = new Integer(0);
	private Integer valueD = new Integer(0);
	private Integer valueF = new Integer(0);
	private Integer random = new Integer(0);
	private Integer valueTa = new Integer(0);
	private Integer valueTb = new Integer(0);
	private Integer valueTc = new Integer(0);
	private Integer valueTd = new Integer(0);     
	private Integer valueTe = new Integer(0);
	private Integer valueTf = new Integer(0);
	
	public LMProgramThermostatGear()
	{
		super();
	}

	/**
	 * Returns the maxValue.
	 * @return Integer
	 */
	public Integer getMaxValue() {
		return maxValue;
	}

	/**
	 * Returns the minValue.
	 * @return Integer
	 */
	public Integer getMinValue() {
		return minValue;
	}

	/**
	 * Returns the random.
	 * @return Integer
	 */
	public Integer getRandom() {
		return random;
	}

	/**
	 * Returns the settings.
	 * @return StringBuffer
	 */
	public String getSettings() {
		return settings;
	}

	/**
	 * Returns the valueB.
	 * @return Integer
	 */
	public Integer getValueB() {
		return valueB;
	}

	/**
	 * Returns the valueD.
	 * @return Integer
	 */
	public Integer getValueD() {
		return valueD;
	}

	/**
	 * Returns the valueF.
	 * @return Integer
	 */
	public Integer getValueF() {
		return valueF;
	}

	/**
	 * Returns the valueTa.
	 * @return Integer
	 */
	public Integer getValueTa() {
		return valueTa;
	}

	/**
	 * Returns the valueTb.
	 * @return Integer
	 */
	public Integer getValueTb() {
		return valueTb;
	}

	/**
	 * Returns the valueTc.
	 * @return Integer
	 */
	public Integer getValueTc() {
		return valueTc;
	}

	/**
	 * Returns the valueTd.
	 * @return Integer
	 */
	public Integer getValueTd() {
		return valueTd;
	}

	/**
	 * Returns the valueTe.
	 * @return Integer
	 */
	public Integer getValueTe() {
		return valueTe;
	}

	/**
	 * Returns the valueTf.
	 * @return Integer
	 */
	public Integer getValueTf() {
		return valueTf;
	}

	/**
	 * Sets the maxValue.
	 * @param maxValue The maxValue to set
	 */
	public void setMaxValue(Integer maxValue) {
		this.maxValue = maxValue;
	}

	/**
	 * Sets the minValue.
	 * @param minValue The minValue to set
	 */
	public void setMinValue(Integer minValue) {
		this.minValue = minValue;
	}

	/**
	 * Sets the random.
	 * @param random The random to set
	 */
	public void setRandom(Integer random) {
		this.random = random;
	}

	/**
	 * Sets the settings.
	 * @param settings The settings to set
	 */
	public void setSettings(String settings) {
		this.settings = settings;
	}

	/**
	 * Sets the valueB.
	 * @param valueB The valueB to set
	 */
	public void setValueB(Integer valueB) {
		this.valueB = valueB;
	}

	/**
	 * Sets the valueD.
	 * @param valueD The valueD to set
	 */
	public void setValueD(Integer valueD) {
		this.valueD = valueD;
	}

	/**
	 * Sets the valueF.
	 * @param valueF The valueF to set
	 */
	public void setValueF(Integer valueF) {
		this.valueF = valueF;
	}

	/**
	 * Sets the valueTa.
	 * @param valueTa The valueTa to set
	 */
	public void setValueTa(Integer valueTa) {
		this.valueTa = valueTa;
	}

	/**
	 * Sets the valueTb.
	 * @param valueTb The valueTb to set
	 */
	public void setValueTb(Integer valueTb) {
		this.valueTb = valueTb;
	}

	/**
	 * Sets the valueTc.
	 * @param valueTc The valueTc to set
	 */
	public void setValueTc(Integer valueTc) {
		this.valueTc = valueTc;
	}

	/**
	 * Sets the valueTd.
	 * @param valueTd The valueTd to set
	 */
	public void setValueTd(Integer valueTd) {
		this.valueTd = valueTd;
	}

	/**
	 * Sets the valueTe.
	 * @param valueTe The valueTe to set
	 */
	public void setValueTe(Integer valueTe) {
		this.valueTe = valueTe;
	}

	/**
	 * Sets the valueTf.
	 * @param valueTf The valueTf to set
	 */
	public void setValueTf(Integer valueTf) {
		this.valueTf = valueTf;
	}

}
