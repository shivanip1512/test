<%@ include file="oper_header.jsp" %>
<%@ include file="oper_trendingheader.jsp" %>

<%@ page import="com.cannontech.servlet.LCConnectionServlet" %>
<%@ page import="com.cannontech.loadcontrol.data.LMProgramEnergyExchange" %>
<%@ page import="com.cannontech.loadcontrol.data.LMEnergyExchangeOffer" %>
<%@ page import="com.cannontech.loadcontrol.data.LMEnergyExchangeOfferRevision" %>
<%@ page import="com.cannontech.loadcontrol.data.LMEnergyExchangeHourlyOffer" %>
<%@ page import="com.cannontech.loadcontrol.data.LMEnergyExchangeCustomer" %>
<%@ page import="com.cannontech.loadcontrol.data.LMEnergyExchangeCustomerReply" %>
<%@ page import="com.cannontech.loadcontrol.data.LMEnergyExchangeHourlyCustomer" %>
<%@ taglib uri="/WEB-INF/struts.tld" prefix="struts" %>
<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>
<jsp:useBean id="checker" scope="session" class="com.cannontech.validate.PageBean"/>

<%  
    //previous decl of tab in oper_treandingheader.jsp :P
    //String tab = request.getParameter("tab");
    tab = request.getParameter("tab");

    if( tab == null )
        tab = "current";

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
	java.text.DecimalFormat numberFormat = new java.text.DecimalFormat("#,###.00");

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
				checker.set("program", programIds[0]);
				for (int i = 0; i < 24; i++)
				{
					priceStrs[i] = "0";
					amountStrs[i] = "0";
				}
				checker.set("prices", priceStrs);
				checker.set("amount", amountStrs);
System.out.println("bar: " + eeDateFormat.format(com.cannontech.util.ServletUtil.getToday()));
				checker.set("date", eeDateFormat.format(com.cannontech.util.ServletUtil.getToday()));
				checker.set("notifydate", eeDateFormat.format(com.cannontech.util.ServletUtil.getToday()));
				checker.set("notifytime", eeTimeFormat.format(new java.util.Date()));
				checker.set("expiredate", eeDateFormat.format(com.cannontech.util.ServletUtil.getToday()));
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
                System.out.println("foobar: " + com.cannontech.validate.PageBean.parseDate(checker.get("date")));
				if (checker.validate()) {
					response.sendRedirect("oper_ee.jsp?tab=newconfirm");
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
            System.out.println("foobar2: " + com.cannontech.validate.PageBean.parseDate(checker.get("date")));
			programStr = checker.get("programname");
			dateStr = eeDateFormat.format( com.cannontech.validate.PageBean.parseDate(checker.get("date")) );
			notifyDateStr = eeDateFormat.format( com.cannontech.validate.PageBean.parseDate(checker.get("notifydate")) );
			notifyTimeStr = eeTimeFormat.format( com.cannontech.validate.PageBean.parseTime(checker.get("notifytime")) );
			expireDateStr = eeDateFormat.format( com.cannontech.validate.PageBean.parseDate(checker.get("expiredate")) );
			expireTimeStr = eeTimeFormat.format( com.cannontech.validate.PageBean.parseTime(checker.get("expiretime")) );

			newAmountStrs = (String[]) checker.getObject("amount");
			newPriceStrs = (String[]) checker.getObject("prices");
			amountStrs = (String[]) newAmountStrs.clone();
			priceStrs = (String[]) newPriceStrs.clone();

			double totAmount = 0;
			for (int i = 0; i < 24; i++)
			{
				try {
					double amountVal = Double.parseDouble(newAmountStrs[i]);
					double priceVal = Double.parseDouble(newPriceStrs[i]);
					totAmount += amountVal;

					if (amountVal == 0)
						amountStrs[i] = "----";
					else
						amountStrs[i] = numberFormat.format(amountVal);
					if (priceVal == 0)
						priceStrs[i] = "----";
					else
						priceStrs[i] = numberFormat.format(priceVal);
				}
				catch (NumberFormatException ne) {
					checker.setError("formaterror", "Some of the values below have invalid format");
					response.sendRedirect("oper_ee.jsp?tab=new&error=true");
				}
			}
			totAmountStr = String.valueOf(totAmount);
		}
		else {
			com.cannontech.loadcontrol.messages.LMEnergyExchangeControlMsg msg =
				new com.cannontech.loadcontrol.messages.LMEnergyExchangeControlMsg();
            msg.setCommand(new Integer( com.cannontech.loadcontrol.messages.LMEnergyExchangeControlMsg.NEW_OFFER ));
            msg.setYukonID(new Integer( request.getParameter("program") ));
			msg.setAdditionalInfo("(none)");

			String offerDateStr = request.getParameter("date") + " 00:00";
			//String endOfOfferDateStr = request.getParameter("date") + " 23:59";
			String notifyDateTimeStr = request.getParameter("notifydate") + " " + request.getParameter("notifytime");
			String expireDateTimeStr = request.getParameter("expiredate") + " " + request.getParameter("expiretime");
			
			java.util.Date offerDate = eeDateTimeFormat.parse(offerDateStr);
			//java.util.Date endOfOfferDate = new java.util.Date(endOfOfferDateStr);			
			java.util.Date notifyDateTime = eeDateTimeFormat.parse(notifyDateTimeStr);
			java.util.Date expireDateTime = eeDateTimeFormat.parse(expireDateTimeStr);
			
			java.util.Calendar offerCal = java.util.Calendar.getInstance();
			offerCal.setTime(offerDate);
			offerCal.add(java.util.Calendar.DATE, 1);
			java.util.Date endOfOfferDate = offerCal.getTime();

			if (!notifyDateTime.before(expireDateTime))
			{
				if (checker == null)
					response.sendRedirect("oper_ee.jsp");
				checker.setError("notifytime", "Notification time must be earlier than expiration time");
				response.sendRedirect("oper_ee.jsp?tab=new&error=true");
			}
			else if (!expireDateTime.before(endOfOfferDate))
			{
				if (checker == null)
					response.sendRedirect("oper_ee.jsp");
				checker.setError("expiretime", "Expiration time must be earlier than the end of offer day");
				response.sendRedirect("oper_ee.jsp?tab=new&error=true");
			}
			else {
                
                msg.setOfferID(new Integer(0) ); //irrelevant
                System.out.println("setting msg offer id: " + msg.getOfferID() );
				msg.setOfferDate( offerDate );
				msg.setNotificationDateTime( notifyDateTime );
				msg.setExpirationDateTime( expireDateTime );

				amountStrs = request.getParameterValues("amount");
				priceStrs = request.getParameterValues("prices");

				Double[] amount = new Double[24];
				Integer[] prices = new Integer[24];
				for (int i = 0; i < 24; i++)
				{
					amount[i] = new Double(amountStrs[i]);
					int centVal = (int) (Double.parseDouble(priceStrs[i]) * 100);
					prices[i] = new Integer(centVal);
				}

				msg.setAmountRequested(amount);
				msg.setPricesOffered(prices);

				com.cannontech.loadcontrol.LoadControlClientConnection conn = cs.getConnection();
				conn.write(msg);

				if (checker != null)
					checker.clear();
				response.sendRedirect("oper_ee.jsp?pending=new");
			}
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

				checker.set("notifydate", eeDateFormat.format(com.cannontech.util.ServletUtil.getToday() ));
				checker.set("notifytime", eeTimeFormat.format( new java.util.Date() ));
				checker.set("expiredate", eeDateFormat.format( com.cannontech.util.ServletUtil.getToday() ));
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
				}
			}
		}
	}
	else if (tab.equalsIgnoreCase("reviseconfirm"))
	{        
		if (request.getParameter("confirmed") == null) {
			programStr = checker.get("progname");           
			dateStr = datePart.format( com.cannontech.validate.PageBean.parseDate(checker.get("date")) );
            
			progIdStr = checker.get("prog");
			offerIdStr = checker.get("offer");
			revNumStr = checker.get("rev");
			notifyDateStr = eeDateFormat.format( com.cannontech.validate.PageBean.parseDate(checker.get("notifydate")) );
			notifyTimeStr = eeTimeFormat.format( com.cannontech.validate.PageBean.parseTime(checker.get("notifytime")) );
			expireDateStr = eeDateFormat.format( com.cannontech.validate.PageBean.parseDate(checker.get("expiredate")) );
			expireTimeStr = eeTimeFormat.format( com.cannontech.validate.PageBean.parseTime(checker.get("expiretime")) );

			newAmountStrs = (String[]) checker.getObject("amount");
			newPriceStrs = (String[]) checker.getObject("prices");
			amountStrs = (String[]) newAmountStrs.clone();
			priceStrs = (String[]) newPriceStrs.clone();

			double totAmount = 0;
			for (int i = 0; i < 24; i++)
			{
				try {
					double amountVal = Double.parseDouble(newAmountStrs[i]);
					double priceVal = Double.parseDouble(newPriceStrs[i]);
					totAmount += amountVal;

					if (amountVal == 0)
						amountStrs[i] = "----";
					else
						amountStrs[i] = numberFormat.format(amountVal);
					if (priceVal == 0)
						priceStrs[i] = "----";
					else
						priceStrs[i] = numberFormat.format(priceVal);
				}
				catch (NumberFormatException ne) {
					checker.setError("formaterror", "Some of the values below have invalid format");
					response.sendRedirect("oper_ee.jsp?tab=revise&prog=" + checker.get("prog") + "&offer=" + checker.get("offer") + "&rev=" + checker.get("rev") + "&error=true");
				}
			}
			totAmountStr = String.valueOf(totAmount);
		}
		else {
			com.cannontech.loadcontrol.messages.LMEnergyExchangeControlMsg msg =
				new com.cannontech.loadcontrol.messages.LMEnergyExchangeControlMsg();
            msg.setCommand(new Integer( com.cannontech.loadcontrol.messages.LMEnergyExchangeControlMsg.OFFER_REVISION ));
            msg.setYukonID(new Integer( request.getParameter("prog") ));            
			msg.setAdditionalInfo("(none)");

			String offerDateStr = request.getParameter("date") + " 00:00";
			//String endOfOfferDateStr = request.getParameter("date") + " 23:59";
			String notifyDateTimeStr = request.getParameter("notifydate") + " " + request.getParameter("notifytime");
			String expireDateTimeStr = request.getParameter("expiredate") + " " + request.getParameter("expiretime");
			
			java.util.Date offerDate = eeDateTimeFormat.parse(offerDateStr);
			//java.util.Date endOfOfferDate = new java.util.Date(endOfOfferDateStr);			
			java.util.Date notifyDateTime = eeDateTimeFormat.parse(notifyDateTimeStr);
			java.util.Date expireDateTime = eeDateTimeFormat.parse(expireDateTimeStr);
			
			java.util.Calendar offerCal = java.util.Calendar.getInstance();
			offerCal.setTime(offerDate);
			offerCal.add(java.util.Calendar.DATE, 1);
			java.util.Date endOfOfferDate = offerCal.getTime();

			if (!notifyDateTime.before(expireDateTime))
			{
				if (checker == null)
					response.sendRedirect("oper_ee.jsp");
				checker.setError("notifytime", "Notification time must be earlier than expiration time");
				response.sendRedirect("oper_ee.jsp?tab=revise&prog=" + checker.get("prog") + "&offer=" + checker.get("offer") + "&rev=" + checker.get("rev") + "&error=true");
			}
			else if (!expireDateTime.before(endOfOfferDate))
			{
				if (checker == null)
					response.sendRedirect("oper_ee.jsp");
				checker.setError("expiretime", "Expiration time must be earlier than the end of offer day");
				response.sendRedirect("oper_ee.jsp?tab=new&error=true");
			}
			else {
                //System.out.println("##" + checker.getObject("offer").toString() + "##");
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
					amount[i] = new Double(amountStrs[i]);
					int centVal = (int) (Double.parseDouble(priceStrs[i]) * 100);
					prices[i] = new Integer(centVal);
				}

				msg.setAmountRequested(amount);
				msg.setPricesOffered(prices);

				com.cannontech.loadcontrol.LoadControlClientConnection conn = cs.getConnection();
				conn.write(msg);
               
				if (checker != null)
					checker.clear();
				response.sendRedirect("oper_ee.jsp?pending=revise");
			}
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
	}
%>


<link id="StyleSheet" rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link id="StyleSheet" rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
</head>

<body text="#000000" link="#000000" vlink="#000000" alink="#000000"> -->
<%
            // Determine which tab was selected and include the appropriate file
            if( tab.equalsIgnoreCase("new") )
            {
            %>
<%@ include file="oper_ee_new.jsp" %>
<%
            }
            else
            if( tab.equalsIgnoreCase("newconfirm") )
            {
            %>
<%@ include file="oper_ee_new_confirm.jsp" %>
<%
            }
            else
            if( tab.equalsIgnoreCase("programs") )
            {
            %>
<%@ include file="oper_ee_programs.jsp" %>
<%
            }
            else
            if( tab.equalsIgnoreCase("profile") )
            {
            %>
<%@ include file="customer_ee_profile.jsp" %>
<%
            }
            else
            if( tab.equalsIgnoreCase("history") )
            {
            %>
<%@ include file="oper_ee_history.jsp" %>
<%
			}
			else
			if( tab.equalsIgnoreCase("historydetail" ) )
			{
			%>
<%@ include file="oper_ee_history_detail.jsp" %>
<%
			}
			else
			if( tab.equalsIgnoreCase("historyresponse" ) )
			{
			%>
<%@ include file="oper_ee_history_response.jsp" %>
<%
            }
            else
            if( tab.equalsIgnoreCase("current" ) )
            {
            %>
<%@ include file="oper_ee_current.jsp" %>
<%
            }
            else
            if( tab.equalsIgnoreCase("revise") )
            {
            %>
<%@ include file="oper_ee_revise.jsp" %>
<%
            }
            else
            if( tab.equalsIgnoreCase("reviseconfirm") )
            {
            %>
<%@ include file="oper_ee_revise_confirm.jsp" %>
<%
            }
            else
            {
           
            
                
            }
       %>
       
    
</body>
</html>

