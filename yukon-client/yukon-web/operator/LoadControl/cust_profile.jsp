              
              <tr>
                <td>
                  <br>
                  <!--<CLIP>-->
                  <p align="center" class="Main"><%= cust.getCiCustomerBase().getCompanyName() %><br clear="ALL">
                    <%= cust.getAddress().getLocationAddress1() %><br clear="ALL">
<!--                    <%= cust.getAddress().getLocationAddress2() %><br clear="ALL">-->
                    <%= cust.getAddress().getCityName() %>, <%=cust.getAddress().getStateCode()%><br clear="ALL">
                  </p>
                </td>
              </tr>
              <%
                com.cannontech.database.data.lite.LiteContact liteContact = null;
                Integer primContID = cust.getCustomer().getPrimaryContactID();
                if( primContID != null)
                  liteContact = com.cannontech.database.cache.functions.ContactFuncs.getContact(primContID.intValue());
                if( liteContact != null){
              %>
              <tr>
                <td width="640">
                  <br>
                  <p align="center" class="Main"><b>Primary Contact</b><br clear="ALL">
                  <%= liteContact.getContFirstName() + " " + liteContact.getContLastName() %><br clear="ALL">
                  <%
                    java.util.Vector notifications = liteContact.getLiteContactNotifications();
                    for (int j = 0; j < notifications.size(); j++){
                      com.cannontech.database.data.lite.LiteContactNotification liteNot = (com.cannontech.database.data.lite.LiteContactNotification)notifications.get(j);
                  %>
                  <%=com.cannontech.common.constants.YukonListFuncs.getYukonListEntry(liteNot.getNotificationCategoryID()) + ": " + liteNot.getNotification() %><br>
                  <%}%>
                  </p>
                </td>
              </tr>
                <%
                  int [] contactids = cust.getCustomerContactIDs();
                  if( contactids.length > 0){
                %>
              <tr>
                <td width="640">
                  <br>
                  <p align="center" class="Main"><b>Additional Contacts</b><br clear="ALL">
                    <%
                      for (int i = 0; i < contactids.length; i++){
                        liteContact = com.cannontech.database.cache.functions.ContactFuncs.getContact(contactids[i]);
                    %>
		            <%= liteContact.getContFirstName() + " " + liteContact.getContLastName() %><br clear="ALL">
                        <%
			            notifications = liteContact.getLiteContactNotifications();
                        for (int j = 0; j < notifications.size(); j++){
                          com.cannontech.database.data.lite.LiteContactNotification liteNot = (com.cannontech.database.data.lite.LiteContactNotification)notifications.get(j);
                          %>
                          <%= com.cannontech.common.constants.YukonListFuncs.getYukonListEntry(liteNot.getNotificationCategoryID()) + ": " + liteNot.getNotification() %><br>
		    	      <%}%>
		    	  <br>
		            <%}
		          }%>
		        </td>
		      </tr>
              <%}%>
