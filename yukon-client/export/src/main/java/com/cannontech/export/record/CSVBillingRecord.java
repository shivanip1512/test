package com.cannontech.export.record;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CSVBillingRecord implements RecordBase {
    private SimpleDateFormat CURTAIL_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
    private DecimalFormat DOLLAR_FORMAT = new DecimalFormat("$#########0.00");

    private String curtailOffer;
    private String meterLocation;
    private GregorianCalendar curtailDate;
    private Double curtailRate;
    private Double rlp; // referencd load profile (baseline value)
    private Double clr; // committed load response (amount committed for offer)
    private Double clr_lowerLimit; // load reduction lower limit ((clr * .5) || 500)
    private Double clr_upperLimit; // load reduction upper limit (clr * 1.2)
    private Double adl; // actual meter reading (rph)
    private Double pdl; // predefined demand level (from customer data)
    private Double rlp_adl; // rlp - adl
    private String compliant; // rlp_adl_greaterthan_clr + rlp_adl_equal_clr = 1 (1, 0)
    private Double finalQualifyingkWh;
    private Double curtailEnergyCredits; // curtail_level * curtail_rate/4

    private final double LOWEST_LIMIT = 500;
    private static Character delimiter = new Character('|');

    private static String[] columnHeadings = { "Curtail. Offer#", "Curtail. Date", "Curtail.  Rate($/kwh)", "RLP(kw)",
        "Adjusted RLP(kw)", "ADL(kw)", "Load Reduction(kw)", "CLR(kw)", "Lower Limit", "Upper Limit", "Compliant",
        "Final Qualifying kWh", "Curtail. Energy Credits($)" };

    @Override
    public String dataToString() {
        String dataString = new String("ID: " + getCurtailOffer() + getDelimiter().toString());

        dataString += CURTAIL_DATE_FORMAT.format(getCurtailDate().getTime());
        dataString += " " + new Integer(getCurtailDate().get(Calendar.HOUR_OF_DAY) + 1).toString();
        dataString += ":00" + getDelimiter().toString();

        dataString += DOLLAR_FORMAT.format(getCurtailRate().doubleValue()) + getDelimiter().toString();

        dataString += getRLP() + getDelimiter().toString();
        dataString += getAdjustedRLP() + getDelimiter().toString();

        dataString += getADL() + getDelimiter().toString();

        dataString += getRLP_ADL() + getDelimiter().toString();

        dataString += getCLR() + getDelimiter().toString();
        dataString += getCLR_lowerLimit() + getDelimiter().toString();
        dataString += getCLR_upperLimit() + getDelimiter().toString();

        dataString += getCompliant() + getDelimiter().toString();

        dataString += getFinalQualifyingkWh() + getDelimiter().toString();

        dataString += DOLLAR_FORMAT.format(getCurtailEnergyCredits()) + getDelimiter().toString() + "\r\n";

        return dataString;
    }

    public Double getAdjustedRLP() {
        // 0 represents a non participating PDL value so we must use the RLP.
        return (getRLP().doubleValue() <= getPDL().doubleValue() || getPDL().doubleValue() == 0 ? getRLP() : getPDL());
    }

    public Double getADL() {
        if (adl == null) {
            return new Double(0.0);
        }
        return adl;
    }

    /**
     * Return columnHeadings as a comma separated String.
     */
    public static String getColumnHeadingsString() {
        if (columnHeadings.length > 0) {
            String dataString = new String(columnHeadings[0]);

            for (int i = 1; i < columnHeadings.length; i++) {
                dataString += ',' + columnHeadings[i];
            }

            dataString += "\r\n";
            return dataString;
        }
        return null;
    }

    /**
     * If compliant is null, set compliant using rlp_adl >= clr.
     */
    public String getCompliant() {
        if (compliant == null) {
            if (getRLP_ADL().doubleValue() < getCLR_lowerLimit().doubleValue()) {
                compliant = new String("Too Low");
            } else if (getRLP_ADL().doubleValue() > getCLR_upperLimit().doubleValue()) {
                compliant = new String("Upper Limit");
            } else {
                compliant = new String("OK");
            }
        }
        return compliant;
    }

    public GregorianCalendar getCurtailDate() {
        return curtailDate;
    }

    /**
     * Returns the curtailEnergyCredits.
     * If curtailEnergyCredits is null, set using curtailLevel * curtailRate / 4
     */
    public Double getCurtailEnergyCredits() {
        if (curtailEnergyCredits == null) {
            curtailEnergyCredits = new Double(getFinalQualifyingkWh().doubleValue() * getCurtailRate().doubleValue());
        }
        return curtailEnergyCredits;
    }

    public String getCurtailOffer() {
        return curtailOffer;
    }

    public Double getCurtailRate() {
        return curtailRate;
    }

    private static Character getDelimiter() {
        return delimiter;
    }

    public Double getRLP() {
        if (rlp == null) {
            return new Double(0.0);
        }
        return rlp;
    }

    public Double getRLP_ADL() {
        if (rlp_adl == null) {
            rlp_adl = new Double(getAdjustedRLP().doubleValue() - getADL().doubleValue());
        }
        return rlp_adl;
    }

    public String getMeterLocation() {
        return meterLocation;
    }

    public Double getCLR() {
        if (clr == null) {
            return new Double(0.0);
        }
        return clr;
    }

    public void setADL(Double newADL) {
        adl = newADL;
    }

    /**
     * Sets the curtailDate.
     * Formats newCurtailDate using CURTAIL_DATE_FORMAT.
     */
    public void setCurtailDate(GregorianCalendar newCurtailDate) {
        curtailDate = newCurtailDate;
    }

    /**
     * Sets the curtailOffer.
     */
    public void setCurtailOffer(String newCurtailOffer) {
        curtailOffer = newCurtailOffer;
    }

    /**
     * Sets the curtailRate as (newCurtailRate * .01)
     * CurtailRate is stored in Cents and needs to be displayed as $/kwh
     */
    public void setCurtailRate(Double newCurtailRate) {
        curtailRate = new Double(newCurtailRate.doubleValue() * .01);
    }

    public void setDelimiter(Character newDelimiter) {
        delimiter = newDelimiter;
    }

    public void setRLP(Double newRLP) {
        rlp = newRLP;
    }

    public void setMeterLocation(String newMeterLocation) {
        meterLocation = newMeterLocation;
    }

    public void setCLR(Double newCLR) {
        clr = newCLR;
    }

    public Double getCLR_lowerLimit() {// lower reduction limit (clr * .5) or 500, which ever is higher
        if (clr_lowerLimit == null) {
            double lowerLimit = getCLR().doubleValue() * 0.5d;
            clr_lowerLimit = (lowerLimit >= LOWEST_LIMIT ? new Double(lowerLimit) : new Double(LOWEST_LIMIT));
        }
        return clr_lowerLimit;
    }

    public Double getCLR_upperLimit() {// upper reduction limit (clr * 1.2)
        if (clr_upperLimit == null) {
            clr_upperLimit = new Double(getCLR().doubleValue() * 1.2d);
        }
        return clr_upperLimit;
    }

    public Double getPDL() {
        if (pdl == null) {
            return new Double(0.0);
        }

        return pdl;
    }

    public void setPDL(Double double1) {
        pdl = double1;
    }

    public Double getFinalQualifyingkWh() {
        if (finalQualifyingkWh == null) {

            if (getRLP_ADL().doubleValue() < getCLR_lowerLimit().doubleValue()) {
                finalQualifyingkWh = new Double(0.0);
            } else if (getRLP_ADL().doubleValue() > getCLR_upperLimit().doubleValue()) {
                finalQualifyingkWh = new Double(getCLR_upperLimit().doubleValue());
            } else {
                finalQualifyingkWh = new Double(getRLP_ADL().doubleValue());
            }
        }
        return finalQualifyingkWh;
    }
}
