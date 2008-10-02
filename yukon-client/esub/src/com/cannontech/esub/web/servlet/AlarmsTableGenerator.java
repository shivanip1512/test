package com.cannontech.esub.web.servlet;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import com.cannontech.common.util.StringUtils;
import com.cannontech.esub.element.CurrentAlarmsTable;
import com.cannontech.esub.model.PointAlarmTableModel;
import com.cannontech.esub.svg.ESubSVGGenerator;
import com.cannontech.esub.svg.SVGOptions;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;

/**
 * Writes out a svg representation of all the alarms for
 * the given device ids, point ids, and or alarmcategory ids.
 * 
 * Parameters:
 * 
 * deviceid (comma seperated list)
 * pointid  (comma seperated list)
 * alarmcategoryid (comma seperated list)
 * x
 * y
 * width
 * height
 * display 	Name of the display the table is on.
 * 
 * @author alauinger
 */
public class AlarmsTableGenerator extends HttpServlet {
        
        private static final String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;     
        
        private static final String PARAM_DEVICE_ID = "deviceid";
        private static final String PARAM_POINT_ID = "pointid";
        private static final String PARAM_ALARMCATEGORY_ID = "alarmcategoryid";
        private static final String PARAM_X = "x";
        private static final String PARAM_Y = "y";
        private static final String PARAM_WIDTH = "width";
        private static final String PARAM_HEIGHT = "height";
        private static final String PARAM_ACKED = "acked";
        private static final String PARAM_EVENT = "events";
        private static final String PARAM_INACTIVE = "inactive";

        /**
         * @see javax.servlet.http.HttpServlet#service(HttpServletRequest, HttpServletResponse)
         */
        protected void service(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {

                String deviceIdStr = req.getParameter(PARAM_DEVICE_ID);
                String pointIdStr = req.getParameter(PARAM_POINT_ID);
                String alarmCategoryIdStr = req.getParameter(PARAM_ALARMCATEGORY_ID);
                String xStr = req.getParameter(PARAM_X);
                String yStr = req.getParameter(PARAM_Y);
                String widthStr = req.getParameter(PARAM_WIDTH);
                String heightStr = req.getParameter(PARAM_HEIGHT);  
                String ackStr = req.getParameter(PARAM_ACKED);                
                String eventStr = req.getParameter(PARAM_EVENT);                
                String inactStr = req.getParameter(PARAM_INACTIVE);
                
                YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(req);
                
                try {
                        int[] deviceIds = StringUtils.parseIntString(deviceIdStr);
                        int[] pointIds = StringUtils.parseIntString(pointIdStr);
                        int[] alarmCategoryIds = StringUtils.parseIntString(alarmCategoryIdStr);
                                        
                        Boolean ack = new Boolean(ackStr);
                        Boolean evnt= new Boolean(eventStr);
                        Boolean inact = new Boolean(inactStr);
                        
                        int x = Integer.parseInt(xStr);
                        int y = Integer.parseInt(yStr);
                        int width = Integer.parseInt(widthStr);
                        int height = Integer.parseInt(heightStr);
                        // is audio even present?
                        boolean audioEnabled = true; // TODO: check roleproperty
                        
                        CurrentAlarmsTable cat = new CurrentAlarmsTable();
                        cat.setDeviceIds(deviceIds);
                        cat.setPointIds(pointIds);
                        cat.setAlarmCategoryIds(alarmCategoryIds);
                        cat.setX(x);
                        cat.setY(y);
                        cat.setWidth(width);
                        cat.setHeight(height);
                        cat.setHideAcknowledged(ack);
                        cat.setHideEvents(evnt);
                        cat.setHideInactive(inact);
                        cat.setUserContextOnTable(userContext);
                        
                        PointAlarmTableModel tableModel = (PointAlarmTableModel) cat.getTable().getModel();
                        tableModel.setHideInactive(cat.isHideInactive());
                        tableModel.setHideEvents(cat.isHideEvents());
                        tableModel.setHideAcknowledged(cat.isHideAcknowledged());
                        // Fill it up with signals/alarms
                        tableModel.refresh();

                        try {
                                // Try to defeat caching
                                resp.setDateHeader("Expires", 0);
                                
                                DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
                        	 	SVGDocument document = (SVGDocument) impl.createDocument(svgNS, "svg", null);
                        	 	
                                SVGOptions options = new SVGOptions();
                                options.setEditEnabled(true);
                                options.setAudioEnabled(audioEnabled);
                                
                                ESubSVGGenerator svgGen = new ESubSVGGenerator(options);

                                Element alarmTableElement = svgGen.createAlarmsTable(document, cat);
                                alarmTableElement.setAttributeNS(null,"elementID", cat.getElementID());
            					alarmTableElement.setAttributeNS(null,"classid",cat.getClass().getName());
                                
                                Writer out = resp.getWriter();
                                
                                OutputFormat format  = new OutputFormat( document, "ISO-8859-1", true );  
                                XMLSerializer    serial = new XMLSerializer(out, format);
                                serial.asDOMSerializer();                            
                                serial.serialize( alarmTableElement );

                                out.flush();                                                 
                        } catch (java.io.IOException ioe) {
                                ioe.printStackTrace();
                        }
                } catch (NumberFormatException nfe) {
                        resp.getWriter().write("error");
                        return;
                }
        }
}
