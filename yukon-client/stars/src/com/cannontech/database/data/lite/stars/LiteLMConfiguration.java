/*
 * Created on Jun 23, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.data.lite.stars;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteTypes;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LiteLMConfiguration extends LiteBase {
	
	public static class SA205 {
		private int slot1 = 0;
		private int slot2 = 0;
		private int slot3 = 0;
		private int slot4 = 0;
		private int slot5 = 0;
		private int slot6 = 0;
		
		/**
		 * @return
		 */
		public int getSlot1() {
			return slot1;
		}

		/**
		 * @return
		 */
		public int getSlot2() {
			return slot2;
		}

		/**
		 * @return
		 */
		public int getSlot3() {
			return slot3;
		}

		/**
		 * @return
		 */
		public int getSlot4() {
			return slot4;
		}

		/**
		 * @return
		 */
		public int getSlot5() {
			return slot5;
		}

		/**
		 * @return
		 */
		public int getSlot6() {
			return slot6;
		}

		/**
		 * @param i
		 */
		public void setSlot1(int i) {
			slot1 = i;
		}

		/**
		 * @param i
		 */
		public void setSlot2(int i) {
			slot2 = i;
		}

		/**
		 * @param i
		 */
		public void setSlot3(int i) {
			slot3 = i;
		}

		/**
		 * @param i
		 */
		public void setSlot4(int i) {
			slot4 = i;
		}

		/**
		 * @param i
		 */
		public void setSlot5(int i) {
			slot5 = i;
		}

		/**
		 * @param i
		 */
		public void setSlot6(int i) {
			slot6 = i;
		}

	}
	
	public static class SA305 {
		private int utility = 0;
		private int group = 0;
		private int division = 0;
		private int substation = 0;
		private int rateFamily = 0;
		private int rateMember = 0;
		private int rateHierarchy = 0;
		
		/**
		 * @return
		 */
		public int getDivision() {
			return division;
		}

		/**
		 * @return
		 */
		public int getGroup() {
			return group;
		}

		/**
		 * @return
		 */
		public int getRateFamily() {
			return rateFamily;
		}

		/**
		 * @return
		 */
		public int getRateHierarchy() {
			return rateHierarchy;
		}

		/**
		 * @return
		 */
		public int getRateMember() {
			return rateMember;
		}

		/**
		 * @return
		 */
		public int getSubstation() {
			return substation;
		}

		/**
		 * @return
		 */
		public int getUtility() {
			return utility;
		}

		/**
		 * @param i
		 */
		public void setDivision(int i) {
			division = i;
		}

		/**
		 * @param i
		 */
		public void setGroup(int i) {
			group = i;
		}

		/**
		 * @param i
		 */
		public void setRateFamily(int i) {
			rateFamily = i;
		}

		/**
		 * @param i
		 */
		public void setRateHierarchy(int i) {
			rateHierarchy = i;
		}

		/**
		 * @param i
		 */
		public void setRateMember(int i) {
			rateMember = i;
		}

		/**
		 * @param i
		 */
		public void setSubstation(int i) {
			substation = i;
		}

		/**
		 * @param i
		 */
		public void setUtility(int i) {
			utility = i;
		}

	}
	
	private int configurationID = CtiUtilities.NONE_ID;
	private String coldLoadPickup = null;
	private String tamperDetect = null;
	
	private SA205 sa205_ = null;
	private SA305 sa305_ = null;
	
	public LiteLMConfiguration() {
		super();
		setLiteType( LiteTypes.STARS_LMCONFIGURATION );
	}
	
	public LiteLMConfiguration(int configID) {
		super();
		setConfigurationID( configID );
		setLiteType( LiteTypes.STARS_LMCONFIGURATION );
	}
	
	public int getConfigurationID() {
		return getLiteID();
	}
	
	public void setConfigurationID(int configID) {
		setLiteID( configID );
	}

	/**
	 * @return
	 */
	public String getColdLoadPickup() {
		return coldLoadPickup;
	}

	/**
	 * @return
	 */
	public String getTamperDetect() {
		return tamperDetect;
	}

	/**
	 * @param string
	 */
	public void setColdLoadPickup(String string) {
		coldLoadPickup = string;
	}

	/**
	 * @param string
	 */
	public void setTamperDetect(String string) {
		tamperDetect = string;
	}
	
	public SA205 getSA205() {
		return sa205_;
	}
	
	public void setSA205(SA205 sa205) {
		sa205_ = sa205;
	}
	
	public SA305 getSA305() {
		return sa305_;
	}
	
	public void setSA305(SA305 sa305) {
		sa305_ = sa305;
	}

}
