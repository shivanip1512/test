<%                	     

// Grab their graphdefinitions
   Class[] types = { Integer.class,String.class };
   Object[][] opGraphs = com.cannontech.util.ServletUtil.executeSQL( dbAlias, "select graphdefinition.graphdefinitionid,graphdefinition.name from graphdefinition,OperatorLoginGraphList where graphdefinition.graphdefinitionid=OperatorLoginGraphList.graphdefinitionid and OperatorLoginGraphList.OperatorLoginID=" + user.getUserID() + " order by graphdefinition.graphdefinitionid", types );
                    
   //out.println("select graphdefinition.graphdefinitionid,graphdefinition.name from graphdefinition,GraphCustomerList where graphdefinition.graphdefinitionid=GraphCustomerList.graphdefinitionid and GraphCustomerList.LMCustomerDeviceID=" + user.getCustomerId() + " order by GraphCustomerList.CustomerOrder");
   String referrer;
   int graphDefinitionId = -1;
   java.util.Date start = null;
   java.util.Date saveStart = null;
   java.util.Date stop = null;
   String period = null;
   String selectedPeriod = null;
   String page_;
   String tab;
   int modelType = 0;

   int numDays;
                
   referrer = (String) session.getValue("referrer");
   if( referrer == null )
      referrer = request.getRequestURI() + "?" + request.getQueryString();

   tab = request.getParameter("tab");
   if( tab == null )
      tab = "graph";

   page_ = request.getParameter("page");
   if( page_ == null )
       page_ = "1";
   
   String modelTypeStr = request.getParameter("model");
   if (modelTypeStr != null)
   {
       modelType = Integer.parseInt(modelTypeStr);
	   //out.println("model Type = "+ modelType + " string "+ modelTypeStr);
   }

   String gDefIDStr = request.getParameter("gdefid");
   if( gDefIDStr != null )                           
      graphDefinitionId = Integer.parseInt(gDefIDStr);
   else
   if( opGraphs != null && opGraphs.length > 0 && opGraphs[0] != null &&
       opGraphs[0].length > 0 && opGraphs[0][0] != null )
   {   
      graphDefinitionId = Integer.parseInt(opGraphs[0][0].toString());
   }

   String startStr = request.getParameter("start");
   String stopStr = request.getParameter("end");
   
   if( startStr != null )  
       start = com.cannontech.util.ServletUtil.parseDateStringLiberally(request.getParameter("start"));       
   else
       start = com.cannontech.util.ServletUtil.getToday();
   //Hold on to the original start date that the trending application is looking for.
   saveStart = start;
   
   if( stopStr != null )
      stop = com.cannontech.util.ServletUtil.parseDateStringLiberally(request.getParameter("end"));
		
   //Accept period if end isn't a parameter we can parse
   if( stop == null )
   {
      String periodStr = request.getParameter("period");      

      if( periodStr == null )
      {
        period = com.cannontech.util.ServletUtil.historicalPeriods[0];
        saveStart = start;
        stop = com.cannontech.util.ServletUtil.getEndingDateOfInterval( start, period); 
      }
      else
      {
        period = java.net.URLDecoder.decode(periodStr.trim());
        stop = com.cannontech.util.ServletUtil.getEndingDateOfInterval( start, period); 
        saveStart = start;
        start = com.cannontech.util.ServletUtil.getStartingDateOfInterval(start, period);
      }
   }
   
   if( period != null )
   {
     for( int k = 0; k < com.cannontech.util.ServletUtil.historicalPeriods.length; k++)
     {
       if( com.cannontech.util.ServletUtil.historicalPeriods[k].equalsIgnoreCase( period ) )
       {
         selectedPeriod = period;
         break;
       }
     }
   }
               
   if( selectedPeriod == null )
      period = selectedPeriod = com.cannontech.util.ServletUtil.getPeriodFromDates(start, stop);
   
   numDays = com.cannontech.common.util.TimeUtil.differenceInDays( start, stop );

   session.putValue("referrer", request.getRequestURI() + "?" + request.getQueryString());
  %>               
