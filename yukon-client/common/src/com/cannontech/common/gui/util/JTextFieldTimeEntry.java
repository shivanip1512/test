package com.cannontech.common.gui.util;

/**
 * Insert the type's description here.
 * Creation date: (4/23/2001 1:47:06 PM)
 * @author: 
 */
import com.cannontech.common.gui.unchanging.TimeStringDocumentValidator;

public class JTextFieldTimeEntry extends javax.swing.JTextField 
{
	private TimeStringDocumentValidator timeValidator = null;

/**
 * JTextFieldTimeEntry constructor comment.
 */
public JTextFieldTimeEntry() {
	super();
	initialize();
}
/**
 * JTextFieldTimeEntry constructor comment.
 * @param columns int
 */
public JTextFieldTimeEntry(int columns) {
	super(columns);
}
/**
 * JTextFieldTimeEntry constructor comment.
 * @param text java.lang.String
 */
public JTextFieldTimeEntry(String text) {
	super(text);
}
/**
 * JTextFieldTimeEntry constructor comment.
 * @param text java.lang.String
 * @param columns int
 */
public JTextFieldTimeEntry(String text, int columns) {
	super(text, columns);
}
/**
 * JTextFieldTimeEntry constructor comment.
 * @param doc javax.swing.text.Document
 * @param text java.lang.String
 * @param columns int
 */
public JTextFieldTimeEntry(javax.swing.text.Document doc, String text, int columns) {
	super(doc, text, columns);
}
/**
 * Insert the method's description here.
 * Creation date: (4/24/2001 9:32:04 AM)
 * @return java.lang.String
 */
public String getTimeText() 
{

	String text = getText();
	int hour = 0;
	int minute = 0;

	if( getTimeValidator().isValid() && text != null && text.length() > 0 )
	{

		try
		{
			int pos = -1;
			
			if( (pos = text.indexOf(":")) != -1  ) //found the :
			{
				hour = Integer.parseInt( text.substring(0, pos) );

				if( (pos+1) < text.length() )
					minute = Integer.parseInt( text.substring(pos+1, text.length()) );
			}
			else  //did not find :
			{
				switch( text.length() )
				{
					case 1: //9
						hour = Integer.parseInt( text );
						break;
						
					case 2: //23
						hour = Integer.parseInt( text );
						if( hour >= 24 )
						{
							hour = Integer.parseInt( text.substring(0, 1) );
							minute = Integer.parseInt( text.substring(1, 2) );
						}
						break;

					case 3: //930
						hour = Integer.parseInt( text.substring(0, 1) );
						minute = Integer.parseInt( text.substring(1, 3) );
						break;

					case 4://0930
						hour = Integer.parseInt( text.substring(0, 2) );
						minute = Integer.parseInt( text.substring(2, 4) );
						break;
					
					default:
						return null;
				}

			}
				
		}
		catch( Exception e )
		{
			return null;
		}	
		
	}

	String hourStr = String.valueOf(hour);
	String minuteStr = String.valueOf(minute);
	
	if( minute <= 9 )
		minuteStr = "0" + minute;

	if( hour <= 9 )
		hourStr = "0" + hour;
	
	return hourStr + ":" + minuteStr;
}
/**
 * Insert the method's description here.
 * Creation date: (4/24/2001 9:32:04 AM)
 * @return java.lang.String
 */
public Integer getTimeTotalSeconds() 
{

	String text = getText();
	int hour = 0;
	int minute = 0;

	if( getTimeValidator().isValid() && text != null && text.length() > 0 )
	{

		try
		{
			int pos = -1;
			
			if( (pos = text.indexOf(":")) != -1  ) //found the :
			{
				hour = Integer.parseInt( text.substring(0, pos) );

				if( (pos+1) < text.length() )
					minute = Integer.parseInt( text.substring(pos+1, text.length()) );
			}
			else  //did not find :
			{
				switch( text.length() )
				{
					case 1: //9
						hour = Integer.parseInt( text );
						break;
						
					case 2: //23
						hour = Integer.parseInt( text );
						if( hour >= 24 )
						{
							hour = Integer.parseInt( text.substring(0, 1) );
							minute = Integer.parseInt( text.substring(1, 2) );
						}
						break;

					case 3: //930
						hour = Integer.parseInt( text.substring(0, 1) );
						minute = Integer.parseInt( text.substring(1, 3) );
						break;

					case 4://0930
						hour = Integer.parseInt( text.substring(0, 2) );
						minute = Integer.parseInt( text.substring(2, 4) );
						break;
					
					default:
						return new Integer(0);
				}

			}
				
		}
		catch( Exception e )
		{
			return new Integer(0);
		}	
		
	}

		return new Integer( (hour * 3600) + (minute * 60) );
}

public static Integer getTimeTotalSeconds(String time) 
{

	String text = time;
	int hour = 0;
	int minute = 0;

	if( text != null && text.length() > 0 )
	{

		try
		{
			int pos = -1;
			
			if( (pos = text.indexOf(":")) != -1  ) //found the :
			{
				hour = Integer.parseInt( text.substring(0, pos) );

				if( (pos+1) < text.length() )
					minute = Integer.parseInt( text.substring(pos+1, text.length()) );
			}
			else  //did not find :
			{
				switch( text.length() )
				{
					case 1: //9
						hour = Integer.parseInt( text );
						break;
						
					case 2: //23
						hour = Integer.parseInt( text );
						if( hour >= 24 )
						{
							hour = Integer.parseInt( text.substring(0, 1) );
							minute = Integer.parseInt( text.substring(1, 2) );
						}
						break;

					case 3: //930
						hour = Integer.parseInt( text.substring(0, 1) );
						minute = Integer.parseInt( text.substring(1, 3) );
						break;

					case 4://0930
						hour = Integer.parseInt( text.substring(0, 2) );
						minute = Integer.parseInt( text.substring(2, 4) );
						break;
					
					default:
						return new Integer(0);
				}

			}
				
		}
		catch( Exception e )
		{
			return new Integer(0);
		}	
		
	}
	
	return new Integer( (hour * 3600) + (minute * 60) );
}
/**
 * Insert the method's description here.
 * Creation date: (4/24/2001 9:47:02 AM)
 * @return com.cannontech.common.gui.unchanging.TimeStringDocumentValidator
 */
private com.cannontech.common.gui.unchanging.TimeStringDocumentValidator getTimeValidator() 
{
	if( timeValidator == null )
		timeValidator = new TimeStringDocumentValidator();

	return timeValidator;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}

		setDocument( getTimeValidator() );

		// user code end
		setName("JTextFieldTimeEntry");
		setSize(71, 20);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (4/24/2001 10:29:58 AM)
 * @return boolean
 */
public boolean isValidText() 
{
	return getTimeValidator().isValid();
}
/**
 * Insert the method's description here.
 * Creation date: (4/24/2001 9:32:04 AM)
 * @return java.lang.String
 */
public void setTimeText( Integer seconds )
{
	int hours = seconds.intValue() / 3600;
	int minutes = (seconds.intValue() % 3600) / 60;

	String hrStr = String.valueOf(hours);
	String minStr = String.valueOf(minutes);
	
	if( minutes <= 9 )
		minStr = "0" + minutes;

	if( hours <= 9 )
		hrStr = "0" + hours;

		
	//set the actual text below
	setText( hrStr + ":" + minStr );

/*	final String value = hrStr + ":" + minStr;
	javax.swing.SwingUtilities.invokeLater( new Runnable()
	{
		public void run()
		{
			JTextFieldTimeEntry.this.setText( value );
		}
		
	});
*/
}

public static String setTimeTextForField( Integer seconds )
{
	int hours = seconds.intValue() / 3600;
	int minutes = (seconds.intValue() % 3600) / 60;

	String hrStr = String.valueOf(hours);
	String minStr = String.valueOf(minutes);
	
	if( minutes <= 9 )
		minStr = "0" + minutes;

	if( hours <= 9 )
		hrStr = "0" + hours;

	return hrStr + ":" + minStr;
}		
/**
 * Insert the method's description here.
 * Creation date: (4/24/2001 9:32:04 AM)
 * @return java.lang.String
 */
public void setTimeText( java.util.Date date )
{
	String hourStr = "00";
	String minuteStr = "00";
	
	if( date != null )
	{
		java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
		cal.setTime( date );

		int hour = cal.get( cal.HOUR_OF_DAY );
		int minute = cal.get( cal.MINUTE );


		hourStr = String.valueOf(hour);
		minuteStr = String.valueOf(minute);
	
		if( minute <= 9 )
			minuteStr = "0" + minute;

		if( hour <= 9 )
			hourStr = "0" + hour;
	}

	//set the actual text below
	setText( hourStr + ":" + minuteStr );
		
/*	final String value = hourStr + ":" + minuteStr;
	javax.swing.SwingUtilities.invokeLater( new Runnable()
	{
		public void run()
		{
			JTextFieldTimeEntry.this.setText( value );
		}
		
	});
*/
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G9CF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135D7DDEC94D594EE8C92E2CCACC9939A63C3D550CAD02A02E98C09D8F9B042038F92C968C3B57E90638FA13C28BC5C5D7EA13FADD4028A315AA2BFB6C5AC4652945B6DF637FF5B5850DA1AF4E9B35D3D5BCEBBBB334C5C250BE2741C39774ECEE1D74460A6DF6E5CF3661EF36E395FB9F31754FDDDD0697156916229A46277330710AD8789599139FCCC4AF2E8C995F17D3E81541217BAAAE04C85DD35BD
	7FEBFC9F0C87G05A043F1A70CB5E45978D1A15FA464929EAFA12F45701DE7460F89D9AB4C07G390065B17E7EBA98BF33BD5F5B56AA5E8BAFADA0BBDFB9695D0FF6DCB6A50ECD73351DE22CA72BBD5B265157D6BBCEDC1302F991GB7DFCE4E9CBF87E37B505C74AF91A35E9CD1F45D50990D4CB4CFA75566A4D33536367E911AE2C115EA5191B5CE83BAB32F431A688C5AED886F366D905284736D36CC1BC0DF05703C9E30451137CC227C3CDAD29582C4D5393F0BA75BC57CE3FF2CA6EBDE574B8FE6595F8D15
	6FEB391A5EDFDE72136AAF6C33A877E14E4BC0D7B1DBD2F594508E8883EEG3E825DE13227738C0C3EA966B762F1C377A592DDDA549CB422D4036736B6508859A59AB14CA8A17C1C5F98EF4A904FBF012F7B680E57711C943648B3DFF569B1D27A6C0037BAC3CECA8BFF70664D72DC2C1ACD67047BBE954E66FB536CC2CEAE74FD1938FDAFDFEC14D7E6703DE673E9EF19303353653BF18E4F3A4C66C09DBCFB84BF387CBDDBBE6312F3FE1A369C67A950154EC9EE0C9D103104AA9EA4B567C2997310DF6273F6EA
	BC06B16D66D89E37F937C863BEBDCEACC536BC57A56731DC181435729068C83C240A73EB1F9F6BB58A739CC0BE208870AA60FDG51A54706BB4F7ECB0E8DA9A66BCA2ADA141A042F5D67BB82A3541B29746996D374889DD5CCD5195228C544BB1F7BB087D38B56FDE9F3BF82BEFA28CECD0529068E5615B8E5543478BB8D3EDAFC47243696EB999C4493C45A7D5EF798C6CD3158EF092842E87A8D2EEF716119DCD3C641A29800E71E178B5D182FFA58FFAB20574943D3363D81EA6A943AC848B0661209915C8949
	53B14F9DF66F8843B353E50EFF8A20EC8B4C37BBF65E769DF25969D6A3983E02CD08737C1D896C499506644C07E312B35D7BD710264D7D99B903E8B4B8D75A0D1BEBE73BED936362735444589D56F1B6D49837569E7731742E79A84FA06379FDB408E7950598F2G85064CF1FDD7BD67EC17E2D14952DFCDA0980D26B34E63FCF212B8B1BF67FA6E183CDB71E76F27E37F2B8F1E0BA3FE7C7E50EBD4E75DB4C175A8BCD8C2F75C0F9C937599B31CB58D36BC81756C0782329C79E9BF561C421829CEA5999D260C29
	7AF4DAFF4E76E5A5279269D6A0FD357A316F9A73D04F4E7B977DD88B91C35729FDA84E92216F30EB11EAB48EC17B0D244E38BC500DFC1E2357C5AF686B464B4DB5C5CBE2DF61F701A2E5AF6E41D463F054581A48A6836BF4B4DC674A915ED370CED36C545BFC032C37465F9FA66BA7CFF8B3FDAF72D7F4FB6B921C7B279269FA63713485B2475DE97783A137F81CEB0358D7B25DCAB847CBFB50D6AF788883D8C2F2FCDDG75320F88F27B8DCD6329F4F27CC430864F04DECDD2E83F5830861C3C95858F5D249B95
	B9647A62A066D0F375BAFECFFA4139A75D1F10F97BB3A86FBDF9F385E45D419F3DFBB3648D31C1646BADD75E1A1CFB4FF7FDE83B71EAC955792BF22FADBD6E5E6B33ACA3225AEDDF4479E60FBB2FDC76EE4F9E98E32AE931BEA0FDCA6E69039E0C973F8BC2ACEAF1C3567B8522776A2DFD0E479B6FC7BE05C11F8264828A81AFBB31A7FBDB48ED3E1F0194FC56F4C59BDED0B872BB56D74E39AE66E77BAAA557B1577637E72BFD9E5B40A7B19D3F7D596B7EC6FF663A3F27BFF35DAF692F65F230617E4ACBFDEEFF
	363E40B97E74BCF2A1869FF0908FA7E804941B98770E307CDF518873D64098A0854887F02E9D3D32DB7008D8700E05326095F78DE3FECE831A81DC1FDA202F3DC53FF2019E777C4082FD5B04DBDF69DA4F393E7104647A2E0D05C4DBFBA16BFF189CCB5C7145B8B45AC09A6F7B341AF36E78157FF61FFE31DA7ADF737CA324F871471EEC35D6AA7C6E926336118697F73BC2187FA686779C0086D884488794B149234601A64E5FC0D4E5BE4614480CDDE5123C23AA1DCF0B71F0396DA50318438DE0278CD011047F
	B000262434BBB370AD371B41E6A0BACD7D06FDE3E412E383216CFC3E943A7DBDF3BC047DE04AE04C0823FDA9D7C2F8964CC858C2AE0B05B04F26BABD43782BC22E05100B9A0D59E2417D416CF1AD9B3CFDDC8503F66F82FBA3579342660AC17C1E993118C51D7D2F9C2CF1E4FD5610E12958G7F86D0CB8788B050AC1F0B86GGG90GGD0CB818294G94G88G88G9CF954ACB050AC1F0B86GGG90GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81
	G81GBAGGG4586GGGG
**end of data**/
}
}
