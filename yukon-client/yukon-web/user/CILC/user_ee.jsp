<%@ page import="com.cannontech.util.ServletUtil" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.GregorianCalendar" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="com.cannontech.loadcontrol.data.LMProgramEnergyExchange" %>
<%@ page import="com.cannontech.loadcontrol.data.LMEnergyExchangeOffer" %>
<%@ page import="com.cannontech.loadcontrol.data.LMEnergyExchangeOfferRevision" %>
<%@ page import="com.cannontech.loadcontrol.data.LMEnergyExchangeHourlyOffer" %>
<%@ page import="com.cannontech.loadcontrol.data.LMEnergyExchangeCustomer" %>
<%@ page import="com.cannontech.loadcontrol.data.LMEnergyExchangeCustomerReply" %> 
<%@ page import="com.cannontech.loadcontrol.data.LMEnergyExchangeHourlyCustomer" %>
<%@ page import="com.cannontech.servlet.LCConnectionServlet" %>
<%@ page import="com.cannontech.web.loadcontrol.LoadcontrolCache" %>

<%@ include file="../include/user_header.jsp" %> 
 
<%@ taglib uri="/WEB-INF/struts.tld" prefix="struts" %>
<jsp:useBean id="checker" scope="session" class="com.cannontech.validate.PageBean"/>
<%    
//    java.util.Enumeration enum1 = request.getParameterNames();
//    while (enum1.hasMoreElements()) {
//        out.println(" --" + enum1.nextElement());
//    }


    LCConnectionServlet cs = (LCConnectionServlet) application.getAttribute(LCConnectionServlet.SERVLET_CONTEXT_ID);
    LoadcontrolCache cache = cs.getCache();
    
	String tab = request.getParameter("tab"); 
	if (tab == null)
		tab = "";  

    java.text.NumberFormat hourFormat = new java.text.DecimalFormat();
    hourFormat.setMinimumIntegerDigits(2);
  	java.text.DecimalFormat priceFormat = new java.text.DecimalFormat("$#,##0.00");
	java.text.DecimalFormat numberFormat = new java.text.DecimalFormat("#,###");	//Value can only be in whole KW, therefore decimal places are not needed
                   
    // has this customer accepted any current revisions?
	LMEnergyExchangeOffer offer = null;
	LMEnergyExchangeOfferRevision revision = null;
	LMEnergyExchangeCustomerReply reply = null;

	com.cannontech.web.history.HEnergyExchangeProgramOffer offerHistory = null;
	com.cannontech.web.history.HEnergyExchangeOfferRevision revisionHistory = null;
	com.cannontech.web.history.HEnergyExchangeCustomerReply replyHistory = null;

    java.util.ArrayList offerList = new java.util.ArrayList();
	java.util.ArrayList replyList = new java.util.ArrayList();
    java.util.ArrayList revisionList = new java.util.ArrayList();
    java.util.ArrayList newOfferList = new java.util.ArrayList();

	int numNewOffers = 0;
	int numAcceptedOffers = 0;

	String[] priceStrs = new String[24];
	String[] amountStrs = new String[24];
	String[] newAmountStrs = new String[24];
    
	// Check for any offers to be displayed
	LMProgramEnergyExchange[] programs = cache.getCustomerEnergyExchangePrograms(customerID);
 
 
	for( int i = 0; i < programs.length; i++ )  //iterate through all the programs for this customer
	{
		LMProgramEnergyExchange program = programs[i];

		java.util.Vector offers = program.getEnergyExchangeOffers();

		if( offers != null )
		{        
			for( int j = 0; j < offers.size(); j++ )  //iterate through all the offers for this program
			{   
				offer = (LMEnergyExchangeOffer) offers.elementAt(j);

				//it must be for today or tomorrow
				GregorianCalendar offerCal = new GregorianCalendar();
				GregorianCalendar nowCal = new GregorianCalendar();

				offerCal.setTime( offer.getOfferDate() );
				nowCal.setTime( ServletUtil.getToday() );

//				if(   nowCal.get( Calendar.YEAR ) == offerCal.get(Calendar.YEAR) &&
//					( nowCal.get( Calendar.DAY_OF_YEAR ) == offerCal.get(Calendar.DAY_OF_YEAR) ||
//					  nowCal.get( Calendar.DAY_OF_YEAR )+1 == offerCal.get(Calendar.DAY_OF_YEAR) ) )
                      
				{                   
					offerList.add(offer);
					boolean foundResponse = false;

					java.util.Vector customers = program.getEnergyExchangeCustomers();

					for( int k = 0; k < customers.size(); k++ )
					{
						LMEnergyExchangeCustomer customer = (LMEnergyExchangeCustomer) customers.elementAt(k);

						if( customer.getCustomerID().longValue() == customerID )
						{                                
                            java.util.Vector revisions = offer.getEnergyExchangeOfferRevisions();
							java.util.Vector replies = customer.getEnergyExchangeCustomerReplies();

							if( replies != null )
							{                                
								for( int m = 0; m < replies.size(); m++ )
								{    
									reply = (LMEnergyExchangeCustomerReply) replies.elementAt(m);										
									if( reply.getOfferID().equals( offer.getOfferID() ) &&											
										!reply.getAcceptStatus().equalsIgnoreCase("noresponse"))
									{
                                        LMEnergyExchangeOfferRevision rev = null;

										if( reply.getAcceptStatus().equalsIgnoreCase("declined")
         									&&( reply.getRevisionNumber().intValue() == ((LMEnergyExchangeOfferRevision)revisions.lastElement()).getRevisionNumber().intValue()))
										{    
											// i think we found a reponse in that mess
	                                        rev = (LMEnergyExchangeOfferRevision) revisions.lastElement();
										}
										if( reply.getAcceptStatus().equalsIgnoreCase("accepted"))	//must be an accepted offer.
										{
											// i think we found a reponse in that mess									
    	                                    rev = (LMEnergyExchangeOfferRevision) revisions.elementAt( reply.getRevisionNumber().intValue() );
   	                                    }
										
										if( rev != null)
										{
                                        // Make sure the offer is notified before showing it
                                        if( rev.getNotificationDateTime().before( new java.util.Date() ) )
                                        {                                       
										    replyList.add(reply);
										    revisionList.add(rev);
                                      
										    numAcceptedOffers++;
										    newOfferList.add(Boolean.FALSE);

										    foundResponse = true;
										    break;
                                        }
                                        }
									}
								}
							
								if (!foundResponse) {                                    
									// no response, so use the most recent revision
									revision = (LMEnergyExchangeOfferRevision) revisions.elementAt( revisions.size()-1 );

                                    if( revision.getNotificationDateTime().before( new java.util.Date() ) )
                                    {                                                        
									    replyList.add(null);
									    revisionList.add(revision);
                                                     
									    if( revision.getOfferExpirationDateTime().compareTo(new java.util.Date()) >= 0 )
                                        {
										    numNewOffers++;
										    newOfferList.add(Boolean.TRUE);
                                        }
                                        else
                                        {
                                            newOfferList.add(Boolean.FALSE);
                                        }
									}
									else {
										newOfferList.add(Boolean.FALSE );
									}
								}
							}
						}

						if ( foundResponse )
							break;
					}
				}
			}
		}
	}
  
	String pending = request.getParameter("pending");

	if (tab.equalsIgnoreCase(""))
	{
		if (pending != null)
			out.println("<META HTTP-EQUIV=\"refresh\" CONTENT=\"5;URL=user_ee.jsp\">");
		else
			out.println("<META HTTP-EQUIV=\"refresh\" CONTENT=\"600;URL=user_ee.jsp\">");
	}
	else if (tab.equalsIgnoreCase("accept"))
	{
		int offerId = 0;
		int revisionNumber = 0;

		String offerIdStr = request.getParameter("offer");
		if( offerIdStr == null)
			offerIdStr = checker.get("offer");
		if (offerIdStr != null)
			offerId = Integer.parseInt(offerIdStr);
		
		String revisionNumberStr = request.getParameter("rev");
		if( revisionNumberStr == null)
			revisionNumberStr = checker.get("rev");
		if (revisionNumberStr != null)
			revisionNumber = Integer.parseInt(revisionNumberStr);

		// find offer and revision in the list
		for (int i = 0; i < revisionList.size(); i++)
		{
			revision = (LMEnergyExchangeOfferRevision) revisionList.get(i);
			if (revision.getOfferID().intValue() == offerId && revision.getRevisionNumber().intValue() == revisionNumber) {
				offer = (LMEnergyExchangeOffer) offerList.get(i);
				break;
			}
		}
		if (request.getParameter("error") == null)
		{
			if (request.getParameter("submitted") == null)
			{   
			  // NEED TO FIGURE OUT HOW TO MAKE SURE SOMEONE LOGGING IN AS A NEW PERSON DOESN'T SEE THE LAST STUFF!!!
			  //Attempt to load amount and prices from checker object, otherwise we will init them below (used with 'Cancel' option)
			  amountStrs = (String[]) checker.getObject("amount");
  			  priceStrs = (String[]) checker.getObject("prices");
		 	  checker.clear();

			  if( amountStrs == null  || priceStrs == null)  // assuming one or the other, they both get loaded together so this is lazily checking I guess. SN
			  {
				priceStrs = new String[24];	// re-init
				amountStrs = new String[24]; //re-init

				//Look up the customer curtailable amount, use this as the default amount
				Object[][] gData2 = com.cannontech.util.ServletUtil.executeSQL( session, "select curtailamount from cicustomerbase where customerid=" +  customerID);  
				String curtailAmount = gData2[0][0].toString();
			  
				for (int i = 0; i < 24; i++)
				{
					double price = ((LMEnergyExchangeHourlyOffer) revision.getEnergyExchangeHourlyOffers().elementAt(i)).getPrice().doubleValue() / 100;
					priceStrs[i] = priceFormat.format(price);

					double amount = ((LMEnergyExchangeHourlyOffer) revision.getEnergyExchangeHourlyOffers().elementAt(i)).getAmountRequested().doubleValue();
					amountStrs[i] = numberFormat.format(amount);
					if (!amountStrs[i].equals("0"))
					{
						//If offer exists, the value will not be '0', default to default curtail value.
						amountStrs[i] = curtailAmount;
					}
					else
					{
						//'0' is a defined curtail amount so we must use something else.
						amountStrs[i] = "----";
					}
				}
			  }
			  checker.set("prices", priceStrs);
			  checker.set("amount", amountStrs);
			  checker.set("offer", offerIdStr);
			  checker.set("rev", revisionNumberStr);
			}
			else {
				java.util.Enumeration enum = request.getParameterNames();
				while (enum.hasMoreElements()) {
					String name = (String)enum.nextElement();
					String[] value = request.getParameterValues(name);
					if (value.length == 1)
						checker.set(name, value[0]);
					else
						checker.set(name, value);
				}
			    if (checker.validate()) {
					response.sendRedirect("user_ee.jsp?tab=confirm");
					tab = "";
				}
			}
		}
		priceStrs = (String[]) checker.getObject("prices");
		newAmountStrs = (String[]) checker.getObject("amount");
		amountStrs = (String[]) newAmountStrs.clone();

		for (int i = 0; i < 24; i++)
		{
			try {
				if( newAmountStrs[i].trim().length() == 0)
				{	//The editable value field was left empty/blank, give it a legit value.
					newAmountStrs[i] = "0";
				}
				if ( !newAmountStrs[i].equalsIgnoreCase("----"))	//'----' is our non-available offer default value
				{
					//format the actual value for display
					double amountVal = numberFormat.parse(newAmountStrs[i]).doubleValue();
					amountStrs[i] = numberFormat.format(amountVal);
				}
			}
			catch (NumberFormatException ne){
			//the "confirm" tab will handle the errors
			}
			catch (java.text.ParseException pe) {
			//the "confirm" tab will handle the errors
			}
		}
	}
	else if (tab.equalsIgnoreCase("confirm")) {
		if (request.getParameter("confirmed") == null)
		{
			if (checker.get("submitted").equalsIgnoreCase("true")) {	// accept the offer
				priceStrs = (String[]) checker.getObject("prices");
				newAmountStrs = (String[]) checker.getObject("amount");
				amountStrs = (String[]) newAmountStrs.clone();

				boolean isTooSmall = false;
				try {
					for (int i = 0; i < 24; i++)
					{
						double amountVal = 0;
						if( !newAmountStrs[i].equalsIgnoreCase("----"))
						{
							amountVal = numberFormat.parse(newAmountStrs[i]).doubleValue();
							if( amountVal < 500 && amountVal > 0)
							{
								isTooSmall = true;
								checker.setError("amounterror" + String.valueOf(i), "");
							}
                        	amountStrs[i] = numberFormat.format(amountVal);
                       	}
   					}
				}
				catch (NumberFormatException ne) {
					checker.setError("formaterror", "Some of the values below have invalid format");
					response.sendRedirect("user_ee.jsp?tab=accept&error=true");
					tab = "";
				}
				catch (java.text.ParseException pe) {
					checker.setError("formaterror", "Some of the values below have invalid format or characters");
					response.sendRedirect("user_ee.jsp?tab=accept&error=true");
					return;
				}
				if( isTooSmall) {
					checker.setError("amounterror", "Offer amount(s) must be equal to or greater than 500 kW");
					checker.setError("flag","");
					response.sendRedirect("user_ee.jsp?tab=accept&error=true");
					tab = "";
				}
				else if (checker.get("initials").equals("")) {
					checker.setError("initials", "Initials can not be empty");
					response.sendRedirect("user_ee.jsp?tab=accept&error=true");
					tab = "";
				}
			}                                          
			else {	// decline the offer
				if (checker.get("initials").equals("")) {
					checker.setError("initials", "Initials cannot be empty");
					response.sendRedirect("user_ee.jsp?tab=accept&error=true");
				    tab = "";
				}
				else {
					System.out.println("Sending DECLINE message");
					com.cannontech.loadcontrol.LoadControlClientConnection conn = cs.getConnection();

					com.cannontech.loadcontrol.messages.LMEnergyExchangeAcceptMsg msg =
						new com.cannontech.loadcontrol.messages.LMEnergyExchangeAcceptMsg();

					msg.setYukonID( new Integer((int) customerID ));
					msg.setOfferID( new Integer(checker.get("offer")) );
					msg.setRevisionNumber( new Integer(checker.get("rev")) );
					msg.setAcceptStatus(com.cannontech.loadcontrol.messages.LMEnergyExchangeAcceptMsg.DECLINED_ACCEPT_STATUS);
					msg.setIpAddressOfCustomer( request.getRemoteAddr() );
					msg.setUserIDName( liteYukonUser.getUsername());
					msg.setNameOfAcceptPerson( checker.get("initials") );
					msg.setEnergyExchangeNotes(checker.get("comments") );
					conn.write(msg);

					if (checker != null)
						checker.clear();
					response.sendRedirect("user_ee.jsp?pending=decline");
					tab = "";
				}
			}
		}
		else {	// confirmed the offer     
			System.out.println("Sending CONFIRM message");
			com.cannontech.loadcontrol.LoadControlClientConnection conn = cs.getConnection();

			com.cannontech.loadcontrol.messages.LMEnergyExchangeAcceptMsg msg =
				new com.cannontech.loadcontrol.messages.LMEnergyExchangeAcceptMsg();

			msg.setYukonID( new Integer((int)customerID) );
			msg.setOfferID( new Integer(request.getParameter("offer")) );
			msg.setRevisionNumber( new Integer(request.getParameter("rev")) );
			msg.setAcceptStatus(com.cannontech.loadcontrol.messages.LMEnergyExchangeAcceptMsg.ACCEPTED_ACCEPT_STATUS);
			msg.setIpAddressOfCustomer( request.getRemoteAddr() );
			msg.setUserIDName( liteYukonUser.getUsername());
			msg.setNameOfAcceptPerson( request.getParameter("initials") );
			msg.setEnergyExchangeNotes(checker.get("comments") );

			amountStrs = request.getParameterValues("amount");
			Double[] amount = new Double[24];
			for( int i = 0; i < 24; i++ )
			{
				try{
					amount[i] = new Double( numberFormat.parse(amountStrs[i]).doubleValue());
				}
				catch(java.text.ParseException pe){
					//This is where the '----' non-offered values get changed to '0' for the database!!!
					amount[i] = new Double(0);
				}
			}

			msg.setAmountCommitted(amount);
			conn.write(msg);

			if (checker != null)
				checker.clear();
			response.sendRedirect("user_ee.jsp?pending=confirm");
			tab = "";

		}   
	}

	if (tab.equalsIgnoreCase(""))
	{
		%>
			<%@ include file="include/user_ee_current.jsp" %>
		<%
	}
	else if (tab.equalsIgnoreCase("accept"))
	{
		%>
			<%@ include file="include/user_ee_accept.jsp" %>
		<%
	}
	else if (tab.equalsIgnoreCase("confirm"))
	{
		%>
			<%@ include file="include/user_ee_confirm.jsp" %>
		<%
	}
	else if (tab.equalsIgnoreCase("offer"))
	{
		%>
			<%@ include file="include/user_ee_offer.jsp" %>
		<%
	}
%>

</body>
</html>

