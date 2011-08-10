package com.cannontech.billing.format.dynamic;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

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

    private static final String UNITMEASURE = "unitMeasure";
    
    private static final String RATE = "rate";

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
			    
			    String valueString = field.getFormat();
			    if(field.getMaxLength() > 0) {
                    valueString = processValueString(field, valueString);
                }
			    writeToFile.append(valueString);

			} else {
				BillableField billableField = BillableField.valueOf(fName);

				// checking which method to use from the device passed
				try {

					// the field have reading format associated with them
					if (field.getName().endsWith(READING) == true) {

					    Double value = device.getValue(field.getChannel(), field.getReadingType(), billableField);
						if (value == null) {
							writeToFile.append( processValueString(field, "") );
						} else {
							String valueString = String.valueOf(value);
							String formatString = field.getFormat();

							DecimalFormat formatter;
							// if there are no format, default it
							if (formatString.equals("")) {
								formatString = "#####.00";
								formatter = new DecimalFormat(formatString);
							} else {
								if (StringUtils.isBlank(formatString)) {
									throw new RuntimeException(
											"WARNING: EMPTY STRING");
								} else {
									formatter = new DecimalFormat(formatString);
								}
							}
							
							formatter.setRoundingMode(field.getRoundingMode());
							
							valueString = formatter.format(value);
							valueString = processValueString(field, valueString);

							writeToFile.append(valueString);
						}

						// the field have timestamp format associated with them
					} else if (field.getName().endsWith(TIMESTAMP) == true) {
						Timestamp timestamp = device.getTimestamp(field.getChannel(), field.getReadingType(), billableField);
						if (timestamp == null) {
							writeToFile.append(processValueString(field, ""));
						} else {
							String formatString = field.getFormat();

							SimpleDateFormat dateFormatter;
							// if no format, default it
							if (formatString.equals("")) {
								dateFormatter = new SimpleDateFormat(
										"MM/dd/yyyy hh:mm:ss zZ");
							} else {
								if (StringUtils.isBlank(formatString)) {
									throw new RuntimeException(
											"WARNING: EMPTY STRING");
								} else {
									dateFormatter = new SimpleDateFormat(formatString);
								}
							}
							
							String valueString = dateFormatter.format(timestamp);
                            valueString = processValueString(field, valueString);
							
							writeToFile.append(valueString);
						}
					} else if (field.getName().endsWith(UNITMEASURE) == true) {
					    String unitMeasure = device.getUnitOfMeasure(field.getChannel(), field.getReadingType(), billableField);
                        if (unitMeasure == null) {
                            writeToFile.append(processValueString(field, ""));
                        } else {
                            String valueString = processValueString(field, unitMeasure);
                            writeToFile.append(valueString);
                        }

                    } else if (field.getName().endsWith(RATE) == true) {
                        String rate = device.getRate(field.getChannel(), field.getReadingType(), billableField);
                        if (rate== null) {
                            writeToFile.append(processValueString(field, ""));
                        } else {
                            String valueString = processValueString(field, rate);
                            writeToFile.append(valueString);
                        }

						// no timestamp, reading, uom, or rate...just data
					} else {
					    String data = device.getData(field.getChannel(), field.getReadingType(), billableField);
						if (data == null) {
							writeToFile.append(processValueString(field, ""));
						} else {
                            data = processValueString(field, data);
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
        writeToFile.append(System.getProperty("line.separator"));
		return writeToFile.toString();
	}

	// This method takes care of any modifying that needs to take place
	// on the valueString to output the wanted representation 
	private String processValueString(DynamicBillingField field, String valueString) {
	    // This if checks to see if we need more padding or 
	    // to truncate from the front of the value
	    if(field.getMaxLength() > 0) {

	        int neededPadSize = field.getMaxLength() - valueString.length();
	        if (neededPadSize > 0) {
	            if (!field.getPadChar().equalsIgnoreCase("")){
	                // This generates the padding string that will be added to
	                // the beginning/end of the string.
	                String paddedStr = "";
	                for (int i = 0; i < neededPadSize; i++){
	                    paddedStr += field.getPadChar();
	                }

	                // Put the padding on the left or right
	                if (field.getPadSide().equalsIgnoreCase("left")) {
	                    valueString = paddedStr + valueString;
	                }
	                if (field.getPadSide().equalsIgnoreCase("right")) {
	                    valueString += paddedStr;
	                }
	            }

	            // Too much padding truncating first part of the valueString
	        } else {
	            int desiredStartPos = - neededPadSize;
	            valueString = valueString.substring(desiredStartPos,valueString.length());
	        }
	    }
	    return valueString;
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

	@Override
	public String getBillingFileHeader() {
		if (dynamicFormat.getHeader().equals("")) {
			return "";
		} else {
            return dynamicFormat.getHeader() + System.getProperty("line.separator");
		}
	}

	@Override
	public String getBillingFileFooter() {
		if (dynamicFormat.getFooter().equals("")) {
			return "";
		} else {
            return dynamicFormat.getFooter() + System.getProperty("line.separator");
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
