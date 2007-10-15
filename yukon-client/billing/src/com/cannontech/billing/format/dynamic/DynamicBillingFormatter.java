package com.cannontech.billing.format.dynamic;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.billing.device.base.BillableDevice;
import com.cannontech.billing.format.BillingFormatterBase;
import com.cannontech.common.dynamicBilling.dao.DynamicBillingFileDao;
import com.cannontech.common.dynamicBilling.model.BillableField;
import com.cannontech.common.dynamicBilling.model.DynamicBillingField;
import com.cannontech.common.dynamicBilling.model.DynamicFormat;

/**
 * Class to return the string representation based on dynamic format made by the
 * user may include header or footer
 * 
 */
public class DynamicBillingFormatter extends BillingFormatterBase {

	private static final String READING = "reading";

	private static final String TIMESTAMP = "timestamp";

	private DynamicFormat dynamicFormat;

	private DynamicBillingFileDao dynamicBillingFileDao = null;

	@Override
	public String dataToString(BillableDevice device) {
		StringBuffer writeToFile = new StringBuffer("");

		// for each field in our dynamic billing field,
		// look for pattern and get respective value/timestamp/data from them
		boolean isFirst = true;
		for (DynamicBillingField field : dynamicFormat.getFieldList()) {

			// fixing delimiter appearances
			if (isFirst) {
				isFirst = false;
			} else {
				writeToFile.append(dynamicFormat.getDelim());
			}

			// split it by the delimiter "-" to get the actual name
			String fName = field.getName();
			fName = fName.split("-")[0];

			// if there are no reading or timestamp, it will return the original
			// name, with whitespace omitted
			fName = fName.trim();

			// if it is plain text, just write the format into the file
			if (fName.equals("Plain Text")) {
				writeToFile.append(field.getFormat());
			} else {
				BillableField billableField = BillableField.valueOf(fName);

				// checking which method to use from the device passed
				try {

					// the field have reading format associated with them
					Format format = null;
					if (field.getName().endsWith(READING) == true) {

						if (device.getValue(billableField) == null) {
							writeToFile.append("");
						} else {
							double value = device.getValue(billableField);
							String valueString = String.valueOf(value);

							String formatString = field.getFormat();

							// if there are no format, default it
							if (formatString.equals("")) {
								formatString = "#####.00";
								format = new DecimalFormat(formatString);
							} else {
								if (StringUtils.isBlank(formatString)) {
									throw new RuntimeException(
											"WARNING: EMPTY STRING");
								} else {
									format = new DecimalFormat(formatString);
								}
							}

							// Convert to BigDecimal to truncate the decimal digits
							// to the correct length
							BigDecimal val = new BigDecimal(value);

							// if a decimal is put as first character, there is at least 1 integer
							if (formatString.startsWith(".")) {
								formatString = "0" + formatString;
							}
							int maxLength = field.getMaxLength();
							int decimals = getNoDec(formatString);

                            if(maxLength > 0) {
    							((DecimalFormat) format).setMaximumIntegerDigits(maxLength);
                            }
							val = val.setScale(decimals, BigDecimal.ROUND_DOWN);
							
							valueString = format.format(val);

							writeToFile.append(valueString);
						}

						// the field have timestamp format associated with them
					} else if (field.getName().endsWith(TIMESTAMP) == true) {
						Timestamp timestamp = device
								.getTimestamp(billableField);
						if (timestamp == null) {
							writeToFile.append("");
						} else {
							String formatString = field.getFormat();

							// if no format, default it
							if (formatString.equals("")) {
								format = new SimpleDateFormat(
										"MM/dd/yyyy hh:mm:ss zZ");
							} else {
								if (StringUtils.isBlank(formatString)) {
									throw new RuntimeException(
											"WARNING: EMPTY STRING");
								} else {
									format = new SimpleDateFormat(formatString);
								}
							}

							writeToFile.append(format.format(timestamp));
						}

						// no timestamp or reading, just data
					} else {
						if (device.getData(billableField) == null) {
							writeToFile.append("");
						} else {
							String data = device.getData(billableField);
							writeToFile.append(data);
						}
					}
				} catch (IllegalArgumentException e) {

					StringBuffer sb = new StringBuffer();
					sb.append("The pattern for the '" + field.getName()
							+ "' field is incorrect: " + e.getMessage());

					throw new RuntimeException(sb.toString());

				} catch (RuntimeException re) {
					StringBuffer sb = new StringBuffer();
					sb.append("The pattern for the '"
							+ field.getName()
							+ "' is empty. "
							+ "Please put at least 1 valid character or "
							+ "select no format to get the default format "
							+ re.getMessage());
					throw new RuntimeException(sb.toString());
				}
			}
		}

		// append new line and return the generated line
		writeToFile.append("\n");
		return writeToFile.toString();
	}

	/**
	 * This method is used to load a dynamic billing format from persistance.
	 * 
	 * @param formatId -Id of the format to load
	 */
	public void loadFormat(int formatId) {
		try {
			dynamicFormat = dynamicBillingFileDao.retrieve(formatId);
		} catch (EmptyResultDataAccessException er) {
			throw new RuntimeException("WARNING: Format is not found in database");
		}
	}

	/**
	 * Helper method to get the number of decimal part of the format
	 * 
	 * @param format - the string pattern
	 */
	private static int getNoDec(String format) {
		int digits = 0;
		StringTokenizer st = new StringTokenizer(format, ".");
		if (st.hasMoreTokens() == false) {
			return 0;
		} else {
			st.nextToken();
		}
		if (st.hasMoreTokens() == false) {
			return 0;
		} else {
			format = st.nextToken(); // get the decimal

			for (int i = 0; i < format.length(); i++) {
				if (format.charAt(i) == '#' || format.charAt(i) == '0') {
					digits++;
				}
			}

			return digits;
		}
	}

	@Override
	public String getBillingFileHeader() {
		if (dynamicFormat.getHeader().equals("")) {
			return "";
		} else {
			return dynamicFormat.getHeader() + "\n";
		}
	}

	@Override
	public String getBillingFileFooter() {
		if (dynamicFormat.getFooter().equals("")) {
			return "";
		} else {
			return dynamicFormat.getFooter() + "\n";
		}
	}

	public void setDynamicFormat(DynamicFormat dynamicFormat) {
		this.dynamicFormat = dynamicFormat;
	}

	public DynamicBillingFileDao getDynamicBillingFileDao() {
		return dynamicBillingFileDao;
	}

	public void setDynamicBillingFileDao(
			DynamicBillingFileDao dynamicBillingFileDao) {
		this.dynamicBillingFileDao = dynamicBillingFileDao;
	}
}
