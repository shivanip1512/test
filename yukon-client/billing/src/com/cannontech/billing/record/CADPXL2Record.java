package com.cannontech.billing.record;

/**
 * Insert the type's description here.
 * Creation date: (6/28/00 11:55:04 AM)
 * @author: 
 */
public class CADPXL2Record implements BillingRecordBase
{
	private String accountNumber = null;
	private String importType = null;
	private String serviceGroup = null;
	private String paymentSign = null;
	private Double payment = null;
	private String batchDate = null;
	private String batchNumber = null;
	private String readDate = null;
	private String whoReadMeter = null;
	private String meterNumber = null;
	private Integer meterPositionNumber = null;
	private java.util.Vector registerNumberVector = null;
	private java.util.Vector kwhReadingVector = null;
	private java.util.Vector kwReadingVector = null;
	private java.util.Vector kvarReadingVector = null;

	java.text.DecimalFormat decimalFormat7v2 = new java.text.DecimalFormat("0000000.00");
	java.text.DecimalFormat decimalFormat6v3 = new java.text.DecimalFormat("000000.000");
	java.text.DecimalFormat decimalFormat9v0 = new java.text.DecimalFormat("000000000");

	private static java.text.SimpleDateFormat DATE_FORMAT = new java.text.SimpleDateFormat("yyyyMMdd");
	public static java.util.Hashtable accountNumberHashTable;
	public static java.util.Hashtable countMetersPerAccount;
/**
 * CADPFormat constructor comment.
 */
public CADPXL2Record()
{
	super();
}
/**
 * CADPFormat constructor comment.
 */
public CADPXL2Record(String newAccountNumber,  String newMeterNumber, Integer newMeterPosition, java.sql.Timestamp newTimeStamp, java.util.Vector newRegNumVector, java.util.Vector newKwhVector, java.util.Vector newKwVector, java.util.Vector newKvarVector)
{
	super();
	setAccountNumber(newAccountNumber);
	setMeterNumber(newMeterNumber);
	setMeterPositionNumber(newMeterPosition);
	setReadDate(newTimeStamp);
	setRegisterNumberVector(newRegNumVector);
	setKwhReadingVector(newKwhVector);
	setKwReadingVector(newKwVector);
	setKvarReadingVector(newKvarVector);
}
/**
 * CADPFormat constructor comment.
 */
public CADPXL2Record(	String accountNumber, String importType, String serviceGroup, String paymentSign, Double payment, java.sql.Timestamp batchDate, String batchNumber, java.sql.Timestamp readDate, String whoReadMeter, String meterNumber, Integer meterPositionNumber, java.util.Vector registerNumberVector, java.util.Vector kwhReadingVector, java.util.Vector kwReadingVector, java.util.Vector kvarReadingVector )
{
	super();
	setAccountNumber(accountNumber);
	setImportType(importType);
	setServiceGroup(serviceGroup);
	setPaymentSign(paymentSign);
	setPayment(payment);
	setBatchDate(batchDate);
	setBatchNumber(batchNumber);
	setReadDate(readDate);
	setWhoReadMeter(whoReadMeter);
	setMeterNumber(meterNumber);
	setMeterPositionNumber(meterPositionNumber);
	setRegisterNumberVector(registerNumberVector);
	setKwhReadingVector(kwhReadingVector);
	setKwReadingVector(kwReadingVector);
	setKvarReadingVector(kvarReadingVector);
}
/**
 * dataToString method comment.
 */
public String dataToString()
{
	int pass = 0;
		
	StringBuffer writeToFile = new StringBuffer();

	String tempAcctNumber = getAccountNumber();
	if( tempAcctNumber.length() > 10)
		tempAcctNumber = tempAcctNumber.substring(0, 10);	//only keep 10 characters
	for(int i = 0; i < (10-tempAcctNumber.length()); i++)
	{
		writeToFile.append("0");	//add 0s to end of string if less than 10 chars
	}
	writeToFile.append(tempAcctNumber);

	writeToFile.append(getImportType());

	writeToFile.append(getServiceGroup());
	for(int i=0;i<(5-getServiceGroup().length());i++)
	{
		writeToFile.append(" ");	//add blanks to end of string if less than 5 chars
	}

	writeToFile.append(getPaymentSign());

	String tempPaymentString = decimalFormat7v2.format(getPayment().doubleValue());
	for(int i=0;i<tempPaymentString.length();i++)
	{
		if(tempPaymentString.charAt(i) != '.')
		{
			writeToFile.append(tempPaymentString.charAt(i));
		}
	}

	writeToFile.append(getBatchDate());

	writeToFile.append(getBatchNumber());

	writeToFile.append(getReadDate());

	writeToFile.append(getWhoReadMeter());

	writeToFile.append(getMeterNumber());
	for(int i=0;i<(15-getMeterNumber().length());i++)
	{
		writeToFile.append(" ");
	}

	
	
	for(int i=0;i<(2-getMeterPositionNumber().toString().length());i++)
	{
		writeToFile.append("0");
	}
	writeToFile.append(getMeterPositionNumber());


	if( getRegisterNumberVector().size() > 0 )
	{
		for(int i = 0; i < getRegisterNumberVector().size(); i++)
		{
			for(int j=0;j<(2-((Integer)getRegisterNumberVector().get(i)).toString().length());j++)
			{
				writeToFile.append("0");
			}
			writeToFile.append(getRegisterNumberVector().get(i).toString());

			if( getKwhReadingVector().size() > i )
			{
				pass++;
				String tempKwhReadingString = decimalFormat9v0.format(((Double)getKwhReadingVector().get(i)).doubleValue());
				for(int j=0;j<tempKwhReadingString.length();j++)
				{
					if(tempKwhReadingString.charAt(j) != '.')
					{
						writeToFile.append(tempKwhReadingString.charAt(j));
					}
				}
			}
			else
			{
				for(int j=0;j<9;j++)
				{
					writeToFile.append("0");
				}
			}

			if( getKwReadingVector().size() > i )
			{
				pass++;
				String tempKWReadingString = decimalFormat6v3.format(((Double)getKwReadingVector().get(i)).doubleValue());
				for(int j=0;j<tempKWReadingString.length();j++)
				{
					if(tempKWReadingString.charAt(j) != '.')
					{
						writeToFile.append(tempKWReadingString.charAt(j));
					}
				}
			}
			else
			{
				for(int j=0;j<9;j++)
				{
					writeToFile.append("0");
				}
			}

			if( getKvarReadingVector().size() > i )
			{
				pass++;
				
				String tempKvarReadingString = decimalFormat7v2.format(((Double)getKvarReadingVector().get(i)).doubleValue());
				for(int j=0;j<tempKvarReadingString.length();j++)
				{
					if(tempKvarReadingString.charAt(j) != '.')
					{
						writeToFile.append(tempKvarReadingString.charAt(j));
					}
				}
			}
			else
			{
				for(int j=0;j<9;j++)
				{
					writeToFile.append("0");
				}
			}
		}
	}
	else
	{
	}

	writeToFile.append("\r\n");
	if (pass > 0)
		return writeToFile.toString();
	else 
		return null;
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2001 4:30:45 PM)
 * @return java.lang.Integer
 */
public java.lang.String getAccountNumber()
{
	return accountNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (3/15/2002 11:34:00 AM)
 * @return java.util.Hashtable
 */
public java.util.Hashtable getAccountNumberHashTable()
{
	return accountNumberHashTable;
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2001 4:30:45 PM)
 * @return java.lang.String
 */
public String getBatchDate()
{
	if( batchDate == null)
	{
		java.util.Date today = new java.util.Date();
		batchDate = DATE_FORMAT.format(today);
	}
	return batchDate;
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2001 4:30:45 PM)
 * @return java.lang.String
 */
public java.lang.String getBatchNumber() 
{
	if(batchNumber == null)
		return "800";
	return batchNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2001 4:30:45 PM)
 * @return java.lang.String
 */
public java.lang.String getImportType() 
{
	if( importType == null)
		return "HH";
	return importType;
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2001 4:30:45 PM)
 * @return java.util.Vector
 */
public java.util.Vector getKvarReadingVector() 
{
	return kvarReadingVector;
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2001 4:30:45 PM)
 * @return java.util.Vector
 */
public java.util.Vector getKwhReadingVector()
{
	return kwhReadingVector;
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2001 4:30:45 PM)
 * @return java.util.Vector
 */
public java.util.Vector getKwReadingVector()
{
	return kwReadingVector;
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2001 4:30:45 PM)
 * @return java.lang.String
 */
public java.lang.String getMeterNumber()
{
	return meterNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2001 4:30:45 PM)
 * @return java.lang.Integer
 */
public Integer getMeterPositionNumber()
{
	return meterPositionNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2001 4:30:45 PM)
 * @return java.lang.Double
 */
public java.lang.Double getPayment() 
{
	if( payment == null)
		return new Double(0.0);
	return payment;
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2001 4:30:45 PM)
 * @return java.lang.String
 */
public java.lang.String getPaymentSign() 
{
	if( paymentSign == null)
		return " ";
	return paymentSign;
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2001 4:30:45 PM)
 * @return java.lang.String
 */
public String getReadDate()
{
	return readDate;
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2001 4:30:45 PM)
 * @return java.util.Vector
 */
public java.util.Vector getRegisterNumberVector() 
{
	return registerNumberVector;
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2001 4:30:45 PM)
 * @return java.lang.String
 */
public java.lang.String getServiceGroup()
{
	//Options are ALL or ELEC
	if( serviceGroup == null )
		return "ELEC";
	return serviceGroup;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2001 10:40:24 AM)
 * @return java.lang.String
 */
public java.lang.String getWhoReadMeter()
{
	if( whoReadMeter == null )
		return "   ";
	return whoReadMeter;
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2001 4:30:45 PM)
 * @param newAccountNumber java.lang.Integer
 */
public void setAccountNumber(java.lang.String newAccountNumber)
{
	accountNumber = newAccountNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2001 4:30:45 PM)
 * @param newBatchDate java.lang.String
 */
public void setBatchDate(java.sql.Timestamp timeStamp)
{
	java.util.Date d = new java.util.Date(timeStamp.getTime());
	batchDate = DATE_FORMAT.format(d);
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2001 4:30:45 PM)
 * @param newBatchNumber java.lang.String
 */
public void setBatchNumber(java.lang.String newBatchNumber) 
{
	batchNumber = newBatchNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2001 4:30:45 PM)
 * @param newImportType java.lang.String
 */
public void setImportType(java.lang.String newImportType)
{
	importType = newImportType;
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2001 4:30:45 PM)
 * @param newKvarReadingVector java.util.Vector
 */
public void setKvarReadingVector(java.util.Vector newKvarReadingVector)
{
	kvarReadingVector = newKvarReadingVector;
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2001 4:30:45 PM)
 * @param newKwhReadingVector java.util.Vector
 */
public void setKwhReadingVector(java.util.Vector newKwhReadingVector) 
{
	kwhReadingVector = newKwhReadingVector;
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2001 4:30:45 PM)
 * @param newKwReadingVector java.util.Vector
 */
public void setKwReadingVector(java.util.Vector newKwReadingVector)
{
	kwReadingVector = newKwReadingVector;
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2001 4:30:45 PM)
 * @param newMeterNumber java.lang.String
 */
public void setMeterNumber(java.lang.String newMeterNumber)
{
	meterNumber = newMeterNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2001 4:30:45 PM)
 * @param newMeterPositionNumber java.lang.Integer
 */
public void setMeterPositionNumber(Integer newMeterPositionNumber)
{
	meterPositionNumber = newMeterPositionNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2001 4:30:45 PM)
 * @param newPayment java.lang.Double
 */
public void setPayment(java.lang.Double newPayment) 
{
	payment = newPayment;
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2001 4:30:45 PM)
 * @param newPaymentSign java.lang.String
 */
public void setPaymentSign(java.lang.String newPaymentSign) 
{
	paymentSign = newPaymentSign;
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2001 4:30:45 PM)
 * @param newReadDate java.lang.String
 */
public void setReadDate(java.sql.Timestamp timeStamp)
{
	java.util.Date d = new java.util.Date(timeStamp.getTime());
	readDate = DATE_FORMAT.format(d);
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2001 4:30:45 PM)
 * @param newRegisterNumberVector java.util.Vector
 */
public void setRegisterNumberVector(java.util.Vector newRegisterNumberVector) 
{
	registerNumberVector = newRegisterNumberVector;
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2001 4:30:45 PM)
 * @param newServiceGroup java.lang.String
 */
public void setServiceGroup(java.lang.String newServiceGroup)
{
	serviceGroup = newServiceGroup;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/2001 10:40:24 AM)
 * @param newWhoReadMeter java.lang.String
 */
public void setWhoReadMeter(java.lang.String newWhoReadMeter)
{
	whoReadMeter = newWhoReadMeter;
}
}
