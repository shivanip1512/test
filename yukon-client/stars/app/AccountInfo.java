/*
 * Created on Apr 14, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.stars.util.StarsUtils;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class AccountInfo {
	
	private static final String OUTPUT_FILE = "acctinfo.csv";
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Usage: " + AccountInfo.class.getName() + " energy_company_id [output_file]");
			return;
		}
		
		int energyCompanyID = -1;
		try {
			energyCompanyID = Integer.parseInt( args[0] );
		}
		catch (NumberFormatException e) {
			System.err.println("Invalid number format for energy company ID: " + args[0]);
			return;
		}
		
		File outputFile = (args.length > 1)? new File(args[1]) : new File(OUTPUT_FILE);
		
		ArrayList acctInfo = new ArrayList();
		acctInfo.add( "Last Name,First Name,Account Number,Stat Serial Number" );
		String sql = null;
		
		try {
			sql = "select AccountID from ECToAccountMapping where energyCompanyID = " + energyCompanyID;
			SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
			stmt.execute();
			
			for (int i = 0; i < stmt.getRowCount(); i++) {
				int accountID = ((java.math.BigDecimal)stmt.getRow(i)[0]).intValue();
				
				sql = "select cont.ContLastName, cont.ContFirstName, acct.AccountNumber " +
						"from Contact cont, Customer cust, CustomerAccount acct " +
						"where cont.ContactID = cust.PrimaryContactID and " +
						"cust.CustomerID = acct.CustomerID and acct.AccountID = " + accountID;
				SqlStatement stmt2 = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
				stmt2.execute();
				
				String lastName = (String) stmt2.getRow(0)[0];
				String firstName = (String) stmt2.getRow(0)[1];
				String accountNo = (String) stmt2.getRow(0)[2];
				
				sql = "select hw.ManufacturerSerialNumber " +
						"from LMHardwareBase hw, InventoryBase inv " +
						"where hw.InventoryID = inv.InventoryID and inv.AccountID = " + accountID;
				stmt2.setSQLString( sql );
				stmt2.execute();
				
				for (int j = 0; j < stmt2.getRowCount(); j++) {
					String serialNo = (String) stmt2.getRow(j)[0];
					String line = lastName + "," + firstName + "," + accountNo + "," + serialNo;
					acctInfo.add( line );
				}
			}
			
			String[] lines = new String[ acctInfo.size() ];
			acctInfo.toArray( lines );
			StarsUtils.writeFile( outputFile, lines );
		}
		catch (CommandExecutionException e) {
			System.err.println("Failed to execute SQL statement:" + sql);
		}
		catch (IOException e) {
			System.err.println("Failed to write output file: " + outputFile.getPath());
		}
	}

}
