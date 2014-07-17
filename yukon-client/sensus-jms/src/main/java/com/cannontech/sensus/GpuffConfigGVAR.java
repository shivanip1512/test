package com.cannontech.sensus;

import com.cannontech.clientutils.YukonLogManager;
import org.apache.log4j.Logger;

public class GpuffConfigGVAR extends GpuffConfig {
	  private Logger log = YukonLogManager.getLogger(GpuffConfigGVAR.class);

	  int reportPeriod = 14;
	  int overCurrentLevel = 300;
	  int resetLevel = 20;

	  public void decodeRow(String[] row)
	  {
	    super.decodeRow(row);

	    String str = row[(this.columnNum++)];
	    setReportPeriod(Integer.parseInt(str));
	    str = row[(this.columnNum++)];
	    setOverCurrentLevel(Integer.parseInt(str));
	    str = row[(this.columnNum++)];
	    setResetLevel(Integer.parseInt(str));
	  }

	  public int getReportPeriod() {
	    return this.reportPeriod;
	  }
	  public void setReportPeriod(int reportPeriod) {
	    this.reportPeriod = reportPeriod;
	  }

	  public int getOverCurrentLevel() {
	    return this.overCurrentLevel;
	  }
	  public void setOverCurrentLevel(int overCurrentLevel) {
	    this.overCurrentLevel = overCurrentLevel;
	  }

	  public int getResetLevel() {
	    return this.resetLevel;
	  }
	  public void setResetLevel(int resetLevel) {
	    this.resetLevel = resetLevel;
	  }

	  public String toString()
	  {
	    return super.toString() + " Report Period (days): " + getReportPeriod() + " Over Current Threshold (amps): " + getOverCurrentLevel() + " Reset Threshold (amps): " + getResetLevel();
	  }

	  public int hashCode()
	  {
	    int prime = 31;
	    int result = super.hashCode();
	    result = 31 * result + this.overCurrentLevel;
	    result = 31 * result + this.reportPeriod;
	    result = 31 * result + this.resetLevel;
	    return result;
	  }

	  public boolean equals(Object obj)
	  {
	    if (this == obj)
	      return true;
	    if (!super.equals(obj))
	      return false;
	    if (getClass() != obj.getClass())
	      return false;
	    GpuffConfigGVAR other = (GpuffConfigGVAR)obj;
	    if (this.overCurrentLevel != other.overCurrentLevel)
	      return false;
	    if (this.reportPeriod != other.reportPeriod) {
	      return false;
	    }
	    return this.resetLevel == other.resetLevel;
	  }

}
