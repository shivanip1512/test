<%@ include file="include/oper_header.jsp" %>

<%@ page import="com.cannontech.servlet.LCConnectionServlet" %>
<%@ page import="com.cannontech.loadcontrol.data.LMProgramEnergyExchange" %>
<%@ page import="com.cannontech.loadcontrol.data.LMEnergyExchangeOffer" %>
<%@ page import="com.cannontech.loadcontrol.data.LMEnergyExchangeOfferRevision" %>
<%@ page import="com.cannontech.loadcontrol.data.LMEnergyExchangeHourlyOffer" %>
<%@ page import="com.cannontech.loadcontrol.data.LMEnergyExchangeCustomer" %>
<%@ page import="com.cannontech.loadcontrol.data.LMEnergyExchangeCustomerReply" %>
<%@ page import="com.cannontech.loadcontrol.data.LMEnergyExchangeHourlyCustomer" %>
 
<%@ taglib uri="/WEB-INF/struts.tld" prefix="struts" %>
  
<cti:checkRole roleid="<%=EnergyBuybackRole.ROLEID%>">
<jsp:useBean id="checker" scope="session" class="com.cannontech.validate.PageBean"/>

<%   
    String tab = request.getParameter("tab");
    tab = request.getParameter("tab");
 
    if( tab == null )
        tab = "current";

	String referrer = (String) session.getValue("referrer");
    if( referrer == null )
      referrer = request.getRequestURI() + "?" + request.getQueryString();
      
	String[] programNames = null;
	String[] programIds = null;

	String totAmountStr = "";
	String programStr = "";
	String dateStr = "";
	String notifyDateStr = "";
	String notifyTimeStr = "";
	String expireDateStr = "";
	String expireTimeStr = "";

	String progIdStr = "";    
	String offerIdStr = "";
	String revNumStr = "";

	String[] priceStrs = new String[24]; 
	String[] amountStrs = new String[24];
	String[] newPriceStrs = new String[24];
	String[] newAmountStrs = new String[24];
	
	java.text.SimpleDateFormat eeDateFormat = new java.text.SimpleDateFormat("MM/dd/yy");
	java.text.SimpleDateFormat eeTimeFormat = new java.text.SimpleDateFormat("HH:mm");
	java.text.SimpleDateFormat eeDateTimeFormat = new java.text.SimpleDateFormat("MM/dd/yy HH:mm");
	java.text.DecimalFormat priceFormat = new java.text.DecimalFormat("$#,##0.00");
	java.text.DecimalFormat priceFormat_NoDollar = new java.text.DecimalFormat("#,##0.00");
	java.text.DecimalFormat numberFormat = new java.text.DecimalFormat("#,###");	//Value can only be in whole KW, therefore decimal places are not needed	

	eeDateFormat.setTimeZone(tz);
	eeTimeFormat.setTimeZone(tz);
	eeDateTimeFormat.setTimeZone(tz);

	session.putValue("referrer", request.getRequestURI() + "?" + request.getQueryString());
	
	if (tab.equalsIgnoreCase("current"))
	{
		String pending = request.getParameter("pending");

		if (pending != null)
			out.println("<META HTTP-EQUIV=\"refresh\" CONTENT=\"5;URL=oper_ee.jsp\">");
		else
			out.println("<META HTTP-EQUIV=\"refresh\" CONTENT=\"600\">");
	}
	if (tab.equalsIgnoreCase("new"))
	{
		LMProgramEnergyExchange[] programs = cache.getEnergyCompanyEnergyExchangePrograms(energyCompanyID);
        programNames = new String[programs.length];
        programIds = new String[programs.length];
		for (int i = 0; i < programs.length; i++) {
			programNames[i] = programs[i].getYukonName();
			programIds[i] = programs[i].getYukonID().toString();
		}
	
		if (request.getParameter("error") == null) {
			if (request.getParameter("submitted") == null) {
				checker.clear();
				if( programIds.length > 0)
					checker.set("program", programIds[0]);
				else
					checker.set("program", "-1");//give it some invalid value, gets us past exceptions being tossed
				for (int i = 0; i < 24; i++)
				{
					priceStrs[i] = "0";
					amountStrs[i] = "0";
				}
				checker.set("prices", priceStrs);
				checker.set("amount", amountStrs);

				checker.set("date", eeDateFormat.format(com.cannontech.util.ServletUtil.getToday(tz)));
				checker.set("notifydate", eeDateFormat.format(com.cannontech.util.ServletUtil.getToday(tz)));
				checker.set("notifytime", eeTimeFormat.format(new java.util.Date()));
				checker.set("expiredate", eeDateFormat.format(com.cannontech.util.ServletUtil.getToday(tz)));
				checker.set("expiretime", "18:00");
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
					response.sendRedirect("oper_ee.jsp?tab=newconfirm");
					return;
				}
				else{
					priceStrs = (String[]) checker.getObject("prices");
					amountStrs = (String[]) checker.getObject("amount");
				}
			}
		}
		else {
			priceStrs = (String[]) checker.getObject("prices");
			amountStrs = (String[]) checker.getObject("amount");
		}
	}
	else if (tab.equalsIgnoreCase("newconfirm"))
	{ 
		if (request.getParameter("confirmed") == null) {
			programStr = checker.get("programname");
			dateStr = checker.get("date");
			notifyDateStr = checker.get("notifydate");
			notifyTimeStr = checker.get("notifytime");
			expireDateStr = checker.get("expiredate");
			expireTimeStr = checker.get("expiretime");

			newAmountStrs = (String[]) checker.getObject("amount");
			newPriceStrs = (String[]) checker.getObject("prices");
			amountStrs = (String[]) newAmountStrs.clone();
			priceStrs = (String[]) newPriceStrs.clone();

			boolean isTooSmall = false;
			double totAmount = 0;
			int startOfferHour = -1;
			
			for (int i = 0; i < 24; i++)
			{
				try {
					if( newAmountStrs[i].length() == 0)
						newAmountStrs[i] = "0";
					if( newPriceStrs[i].length() == 0)
						newPriceStrs[i] = "0";
					double amountVal = numberFormat.parse(newAmountStrs[i]).doubleValue();
					double priceVal = 0;
					if( !newPriceStrs[i].equalsIgnoreCase("0"))
					{
						try{
						  priceVal = priceFormat_NoDollar.parse(newPriceStrs[i]).doubleValue();
						}
						catch (java.text.ParseException e) {
						  priceVal = priceFormat.parse(newPriceStrs[i]).doubleValue();
						}
					}
					
					totAmount += amountVal;

					if (amountVal == 0)
						amountStrs[i] = "----";
					else {
						if( amountVal < 500 ){
							isTooSmall = true;
							checker.setError("amounterror" + String.valueOf(i), "");
						}
						amountStrs[i] = numberFormat.format(amountVal);
					}
					if (priceVal == 0)
						priceStrs[i] = "----";
					else
					{
						priceStrs[i] = priceFormat.format(priceVal);
											
						//We have a price, lets save this start offer hour
						if( startOfferHour < 0)
							startOfferHour = i;
					}
				}
				catch (NumberFormatException ne) {
					checker.setError("formaterror", "Some of the values below have invalid format");
					response.sendRedirect("oper_ee.jsp?tab=new&error=true");
					return;
				}
				catch (java.text.ParseException pe) {
					checker.setError("formaterror", "Some of the values below have invalid format or characters");
					response.sendRedirect("oper_ee.jsp?tab=new&error=true");
					return;
				}
			}

			totAmountStr = String.valueOf(totAmount);

			if( isTooSmall){
				checker.setError("amounterror", "Offer amount(s) must be equal to or greater than 500kW");
				response.sendRedirect("oper_ee.jsp?tab=new&error=true");
				return;
			}
			if( startOfferHour < 0){
				checker.setError("nooffer", "No hourly prices greater than $0.00 were found");
				response.sendRedirect("oper_ee.jsp?tab=new&error=true");
				return;
			}			

			java.util.Date offerDate = ServletUtil.parseDateStringLiberally(dateStr + " 00:00");
			java.util.Date notifyDateTime = eeDateTimeFormat.parse(notifyDateStr +" " + notifyTimeStr);
			java.util.Date expireDateTime = eeDateTimeFormat.parse(expireDateStr +" " + expireTimeStr);
			
			java.util.Calendar offerCal = java.util.Calendar.getInstance();
			offerCal.setTime(offerDate);
			offerCal.set(java.util.Calendar.HOUR_OF_DAY, startOfferHour);
			java.util.Date startOfferDateTime = offerCal.getTime();
			
			offerCal = java.util.Calendar.getInstance();
			offerCal.setTime(offerDate);
			offerCal.add(java.util.Calendar.DATE, 1);
			java.util.Date endOfOfferDate = offerCal.getTime();

System.out.println("sending- Offer Date  : " + offerDate);
System.out.println("sending- Notify Date : " + notifyDateTime);
System.out.println("sending- Expire Date : " + expireDateTime);
System.out.println("sending- Start Offer Date : " + startOfferDateTime);
System.out.println("sending- EndOf Offer Date : " + endOfOfferDate);

			if (!notifyDateTime.before(expireDateTime))
			{
				if (checker == null) {
					response.sendRedirect("oper_ee.jsp");
					return;
				}
				checker.setError("notifytime", "Notification time must be earlier than expiration time");
				response.sendRedirect("oper_ee.jsp?tab=new&error=true");
				return;
			}
			else if (!expireDateTime.before(endOfOfferDate))
			{
				if (checker == null) {
					response.sendRedirect("oper_ee.jsp");
                    return;
                }
				checker.setError("expiretime", "Expiration time must be earlier than the end of offer day");
				response.sendRedirect("oper_ee.jsp?tab=new&error=true");
				return;
			}
			else if (!notifyDateTime.before(startOfferDateTime))
			{
				if (checker == null) {
					response.sendRedirect("oper_ee.jsp");
                    return;
                }
				checker.setError("offertooearly", "Warning: First hourly offer is earlier than the notification time");
			}			
		}
		else {
			com.cannontech.loadcontrol.messages.LMEnergyExchangeControlMsg msg =
				new com.cannontech.loadcontrol.messages.LMEnergyExchangeControlMsg();
            msg.setCommand(new Integer( com.cannontech.loadcontrol.messages.LMEnergyExchangeControlMsg.NEW_OFFER ));
            msg.setYukonID(new Integer( request.getParameter("program") ));
			msg.setAdditionalInfo("(none)");

			java.util.Date offerDate = ServletUtil.parseDateStringLiberally(request.getParameter("date") + " 00:00");
			java.util.Date notifyDateTime = eeDateTimeFormat.parse(request.getParameter("notifydate") + " " + request.getParameter("notifytime"));
			java.util.Date expireDateTime = eeDateTimeFormat.parse(request.getParameter("expiredate") + " " + request.getParameter("expiretime"));
			
System.out.println("sending- Offer Date  : " + offerDate);
System.out.println("sending- Notify Date : " + notifyDateTime);
System.out.println("sending- Expire Date : " + expireDateTime);

            msg.setOfferID(new Integer(0) ); //irrelevant
			msg.setOfferDate( offerDate );
			msg.setNotificationDateTime( notifyDateTime );
			msg.setExpirationDateTime( expireDateTime );

			amountStrs = request.getParameterValues("amount");
			priceStrs = request.getParameterValues("prices");

			Double[] amount = new Double[24];
			Integer[] prices = new Integer[24];
			for (int i = 0; i < 24; i++)
			{
				try{
					amount[i] = new Double(numberFormat.parse(amountStrs[i]).doubleValue());
					int centVal = 0;
					if( !priceStrs[i].equalsIgnoreCase("0"))
					{
						try{
						  centVal = (int) (priceFormat.parse(priceStrs[i]).doubleValue() * 100);
						}
						catch (java.text.ParseException e) {
						  centVal = (int) (priceFormat_NoDollar.parse(priceStrs[i]).doubleValue() * 100);
						}
					}
					prices[i] = new Integer(centVal);
				}
				catch (NumberFormatException ne) {
					checker.setError("formaterror", "Some of the values below have an invalid format or invalid characters.");
					response.sendRedirect("oper_ee.jsp?tab=new&error=true");
					return;
				}
				catch (java.text.ParseException pe) {
					checker.setError("formaterror", "Some of the values below have invalid format or characters");
					response.sendRedirect("oper_ee.jsp?tab=new&error=true");
					return;
				}
			}

			msg.setAmountRequested(amount);
			msg.setPricesOffered(prices);

			com.cannontech.loadcontrol.LoadControlClientConnection conn = cs.getConnection();
			conn.write(msg);

			if (checker != null)
				checker.clear();
			response.sendRedirect("oper_ee.jsp?pending=new");
				return;
		}
	}
	else if (tab.equalsIgnoreCase("revise"))
	{
        if (request.getParameter("error") == null)
		{
			if (request.getParameter("submitted") == null)
			{                
				checker.clear();
             
				checker.set("prog", request.getParameter("prog"));
				checker.set("offer", request.getParameter("offer"));
				checker.set("rev", request.getParameter("rev"));

				checker.set("notifydate", eeDateFormat.format(com.cannontech.util.ServletUtil.getToday(tz) ));
				checker.set("notifytime", eeTimeFormat.format( new java.util.Date() ));
				checker.set("expiredate", eeDateFormat.format( com.cannontech.util.ServletUtil.getToday(tz) ));
				checker.set("expiretime", "18:00");
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
					response.sendRedirect("oper_ee.jsp?tab=reviseconfirm");
					return;
				}
				else{
					priceStrs = (String[]) checker.getObject("prices");
					amountStrs = (String[]) checker.getObject("amount");
				}
			}
		} 
	}
	else if (tab.equalsIgnoreCase("reviseconfirm"))
	{        
		if (request.getParameter("confirmed") == null) {
			programStr = checker.get("progname");           
			dateStr = checker.get("date");
            
			progIdStr = checker.get("prog");
			offerIdStr = checker.get("offer");
			revNumStr = checker.get("rev");
			notifyDateStr = checker.get("notifydate");
			notifyTimeStr = checker.get("notifytime");
			expireDateStr = checker.get("expiredate");
			expireTimeStr = checker.get("expiretime");

			newAmountStrs = (String[]) checker.getObject("amount");
			newPriceStrs = (String[]) checker.getObject("prices");
			amountStrs = (String[]) newAmountStrs.clone();
			priceStrs = (String[]) newPriceStrs.clone();

			double totAmount = 0;
			boolean isTooSmall = false;
			int startOfferHour = -1;
			
			for (int i = 0; i < 24; i++)
			{
				try {
					if( newAmountStrs[i].length() == 0)
						newAmountStrs[i] = "0";
					if( newPriceStrs[i].length() == 0)
						newPriceStrs[i] = "0";
					double amountVal = numberFormat.parse(newAmountStrs[i]).doubleValue();
					double priceVal = 0;
					if( !newPriceStrs[i].equalsIgnoreCase("0"))
					{
						try{
						  priceVal = priceFormat.parse(newPriceStrs[i]).doubleValue();
						}
						catch (java.text.ParseException e) {
						  priceVal = priceFormat_NoDollar.parse(newPriceStrs[i]).doubleValue();
						}
					}

					totAmount += amountVal;

					if (amountVal == 0)
						amountStrs[i] = "----";
					else
						if( amountVal < 500){
							isTooSmall = true;
							checker.setError("amounterror" + String.valueOf(i), "");
						}
						amountStrs[i] = numberFormat.format(amountVal);
					if (priceVal == 0)
						priceStrs[i] = "----";
					else{
						priceStrs[i] = priceFormat.format(priceVal);

						//We have a price, lets save this start offer hour
						if( startOfferHour < 0)
							startOfferHour = i;
					}
				}
				catch (NumberFormatException ne) {
					checker.setError("formaterror", "Some of the values below have invalid format");
					response.sendRedirect("oper_ee.jsp?tab=revise&prog=" + checker.get("prog") + "&offer=" + checker.get("offer") + "&rev=" + checker.get("rev") + "&error=true");
					return;
				}
				catch (java.text.ParseException pe) {
					checker.setError("formaterror", "Some of the values below have invalid format or characters");
					response.sendRedirect("oper_ee.jsp?tab=revise&prog=" + checker.get("prog") + "&offer=" + checker.get("offer") + "&rev=" + checker.get("rev") + "&error=true");
					return;
				}

			}
			
			totAmountStr = String.valueOf(totAmount);
			if( isTooSmall){
				checker.setError("amounterror", "Offer amount(s) must be equal to or greater than 500kW");
				response.sendRedirect("oper_ee.jsp?tab=revise&prog=" + checker.get("prog") + "&offer=" + checker.get("offer") + "&rev=" + checker.get("rev") + "&error=true");
				return;
			}
			if( startOfferHour < 0){
				checker.setError("nooffer", "No hourly prices greater than $0.00 were found");
				response.sendRedirect("oper_ee.jsp?tab=revise&prog=" + checker.get("prog") + "&offer=" + checker.get("offer") + "&rev=" + checker.get("rev") + "&error=true");
				return;
			}			
			java.util.Date offerDate = ServletUtil.parseDateStringLiberally(dateStr + " 00:00");
			java.util.Date notifyDateTime = eeDateTimeFormat.parse(notifyDateStr +" " + notifyTimeStr);
			java.util.Date expireDateTime = eeDateTimeFormat.parse(expireDateStr +" " + expireTimeStr);
			
			java.util.Calendar offerCal = java.util.Calendar.getInstance();
			offerCal.setTime(offerDate);
			offerCal.set(java.util.Calendar.HOUR_OF_DAY, startOfferHour);
			java.util.Date startOfferDateTime = offerCal.getTime();
			
			offerCal = java.util.Calendar.getInstance();
			offerCal.setTime(offerDate);
			offerCal.add(java.util.Calendar.DATE, 1);
			java.util.Date endOfOfferDate = offerCal.getTime();

System.out.println("sending- Offer Date  : " + offerDate);
System.out.println("sending- Notify Date : " + notifyDateTime);
System.out.println("sending- Expire Date : " + expireDateTime);
System.out.println("sending- Start Offer Date : " + startOfferDateTime);
System.out.println("sending- EndOf Offer Date : " + endOfOfferDate);

			if (!notifyDateTime.before(expireDateTime))
			{
				if (checker == null) {
					response.sendRedirect("oper_ee.jsp");
					return;
				}
				checker.setError("notifytime", "Notification time must be earlier than expiration time");
				response.sendRedirect("oper_ee.jsp?tab=revise&prog=" + checker.get("prog") + "&offer=" + checker.get("offer") + "&rev=" + checker.get("rev") + "&error=true");
				return;
			}
			else if (!expireDateTime.before(endOfOfferDate))
			{
				if (checker == null) {
					response.sendRedirect("oper_ee.jsp");
                    return;
                }
				checker.setError("expiretime", "Expiration time must be earlier than the end of offer day");
				response.sendRedirect("oper_ee.jsp?tab=new&error=true");
				return;
			}
			else if (!notifyDateTime.before(startOfferDateTime))
			{
				if (checker == null) {
					response.sendRedirect("oper_ee.jsp");
                    return;
                }
				checker.setError("offertooearly", "Warning: First hourly offer is earlier than the notification time");
			}			
		}
		else {
			com.cannontech.loadcontrol.messages.LMEnergyExchangeControlMsg msg =
				new com.cannontech.loadcontrol.messages.LMEnergyExchangeControlMsg();
            msg.setCommand(new Integer( com.cannontech.loadcontrol.messages.LMEnergyExchangeControlMsg.OFFER_REVISION ));
            msg.setYukonID(new Integer( request.getParameter("prog") ));            
			msg.setAdditionalInfo("(none)");

			java.util.Date offerDate = eeDateTimeFormat.parse(request.getParameter("date") + " 00:00");
			java.util.Date notifyDateTime = eeDateTimeFormat.parse(request.getParameter("notifydate") + " " + request.getParameter("notifytime"));
			java.util.Date expireDateTime = eeDateTimeFormat.parse(request.getParameter("expiredate") + " " + request.getParameter("expiretime"));
			
            msg.setOfferID(new Integer(checker.getObject("offer").toString()));
			msg.setOfferDate( offerDate );
			msg.setNotificationDateTime( notifyDateTime );
			msg.setExpirationDateTime( expireDateTime );

			amountStrs = request.getParameterValues("amount");
			priceStrs = request.getParameterValues("prices");

			Double[] amount = new Double[24];
			Integer[] prices = new Integer[24];
			for (int i = 0; i < 24; i++)
			{
				try{
					amount[i] = new Double(numberFormat.parse(amountStrs[i]).doubleValue());
					int centVal = 0;
					if( !priceStrs[i].equalsIgnoreCase("0"))
					{
						try{
						  centVal = (int) (priceFormat.parse(priceStrs[i]).doubleValue() * 100);						
						}
						catch (java.text.ParseException e) {
						  centVal = (int) (priceFormat_NoDollar.parse(priceStrs[i]).doubleValue() * 100);
						}
					}
					prices[i] = new Integer(centVal);
				}
				catch (NumberFormatException ne) {
					checker.setError("formaterror", "Some of the values below have an invalid format or invalid characters.");
					response.sendRedirect("oper_ee.jsp?tab=new&error=true");
					return;
				}
				catch (java.text.ParseException pe) {
					checker.setError("formaterror", "Some of the values below have invalid format or characters");
					response.sendRedirect("oper_ee.jsp?tab=new&error=true");
					return;
				}
			}

			msg.setAmountRequested(amount);
			msg.setPricesOffered(prices);

			com.cannontech.loadcontrol.LoadControlClientConnection conn = cs.getConnection();
			conn.write(msg);
               
			if (checker != null)
				checker.clear();
			response.sendRedirect("oper_ee.jsp?pending=revise");
			return;
		}
	}
	else if (tab.equalsIgnoreCase("close"))
	{
		Integer offerID = new Integer( request.getParameter("offer") );
		LMEnergyExchangeOffer offer = null;
	
		LMProgramEnergyExchange[] programs = cache.getEnergyCompanyEnergyExchangePrograms(energyCompanyID);
		boolean foundOffer = false;

		if( programs != null )
		{
			for( int i = 0; i < programs.length; i++ )
			{
				java.util.Vector offers = programs[i].getEnergyExchangeOffers();

				for( int j = 0; j < offers.size(); j++ )
				{
					LMEnergyExchangeOffer o = (LMEnergyExchangeOffer) offers.elementAt(j);

					if( o.getOfferID().intValue() == offerID.intValue() )
					{
						offer = o;
						foundOffer = true;
						break;
					}
				}

				if ( foundOffer )
					break;
			}
		}
		// should found offer

		com.cannontech.loadcontrol.messages.LMEnergyExchangeControlMsg msg =
			new com.cannontech.loadcontrol.messages.LMEnergyExchangeControlMsg();
		msg.setCommand(new Integer( com.cannontech.loadcontrol.messages.LMEnergyExchangeControlMsg.CLOSE_OFFER ));
		msg.setYukonID(new Integer( request.getParameter("prog") ));
        msg.setOfferID(offerID);
		msg.setOfferDate( offer.getOfferDate() );        
		msg.setAdditionalInfo( "(none)" );
        msg.setNotificationDateTime( new java.util.Date() );
        msg.setExpirationDateTime( new java.util.Date() );

        Double[] amount = new Double[24];
        Integer[] prices = new Integer[24];
        for (int i = 0; i < 24; i++)
        {
            amount[i] = new Double(0.0);
            prices[i] = new Integer(0);
        }

        msg.setAmountRequested(amount);
        msg.setPricesOffered(prices);

		com.cannontech.loadcontrol.LoadControlClientConnection conn = cs.getConnection();
		conn.write(msg);

		response.sendRedirect("oper_ee.jsp?pending=close");
		return;
	}
%>


<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
</head>

<body text="#000000" link="#000000" vlink="#000000" alink="#000000"> 
<%
            // Determine which tab was selected and include the appropriate file
            if( tab.equalsIgnoreCase("new") )
            {
            %>
<%@ include file="include/oper_ee_new.jsp" %>
<%
            }
            else
            if( tab.equalsIgnoreCase("newconfirm") )
            {
            %>
<%@ include file="include/oper_ee_new_confirm.jsp" %>
<%
            }
            else
            if( tab.equalsIgnoreCase("programs") )
            {
            %>
<%@ include file="include/oper_ee_programs.jsp" %>
<%
            }
            else
            if( tab.equalsIgnoreCase("profile") )
            {
            %>
<%@ include file="include/customer_ee_profile.jsp" %>
<%
            }
            else
            if( tab.equalsIgnoreCase("history") )
            {
            %>
<%@ include file="include/oper_ee_history.jsp" %>
<% 
			} 
			else
			if( tab.equalsIgnoreCase("historydetail" ) )
			{
			%>
<%@ include file="include/oper_ee_history_detail.jsp" %>
<%
			}
			else
			if( tab.equalsIgnoreCase("historyresponse" ) )
			{
			%>
<%@ include file="include/oper_ee_history_response.jsp" %>
<%
            }
            else
            if( tab.equalsIgnoreCase("current" ) )
            {
            %>
<%@ include file="include/oper_ee_current.jsp" %>
<%
            }
            else
            if( tab.equalsIgnoreCase("revise") )
            {
            %>
<%@ include file="include/oper_ee_revise.jsp" %>
<%
            }
            else
            if( tab.equalsIgnoreCase("reviseconfirm") )
            {
            %>
<%@ include file="include/oper_ee_revise_confirm.jsp" %>
<%
            }
            else
            {
           
            
                
            }
       %>
       
    
</body>
</html>

</cti:checkRole>