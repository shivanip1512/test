package com.cannontech.esub.web.servlet;

import java.awt.Rectangle;
import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.esub.element.CurrentAlarmsTable;
import com.cannontech.esub.model.PointAlarmTableModel;

/**
 * Writes out a svg representation of all the alarms for
 * a given device id.
 * 
 * Parameters:
 * 
 * deviceid 
 * x
 * y
 * width
 * height
 * 
 * @author alauinger
 */
public class AlarmsTableGenerator extends HttpServlet {
        
        private static final String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;     
        
        private static final String PARAM_DEVICE_ID = "deviceid";
        private static final String PARAM_X = "x";
        private static final String PARAM_Y = "y";
        private static final String PARAM_WIDTH = "width";
        private static final String PARAM_HEIGHT = "height";

        /**
         * @see javax.servlet.http.HttpServlet#service(HttpServletRequest, HttpServletResponse)
         */
        protected void service(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {

                String deviceIDStr = req.getParameter(PARAM_DEVICE_ID);
                String xStr = req.getParameter(PARAM_X);
                String yStr = req.getParameter(PARAM_Y);
                String widthStr = req.getParameter(PARAM_WIDTH);
                String heightStr = req.getParameter(PARAM_HEIGHT);

                try {
                        int deviceID = Integer.parseInt(deviceIDStr);
                        int x = Integer.parseInt(xStr);
                        int y = Integer.parseInt(yStr);
                        int width = Integer.parseInt(widthStr);
                        int height = Integer.parseInt(heightStr);
                        int ackX = width - 120;
                        int ackY = 18;
                
                        CurrentAlarmsTable cat = new CurrentAlarmsTable();
                        cat.setDeviceID(deviceID);
                        ((PointAlarmTableModel)cat.getTable().getModel()).refresh();
                        try {
                                // Try to defeat caching
                                /*resp.setHeader("Cache-Control", "no-store"); //HTTP 1.1
                                  resp.setHeader("Pragma", "no-cache"); //HTTP 1.0*/
                                resp.setDateHeader("Expires", 0);
                                //prevents caching at the proxy server
                                
                                org.w3c.dom.DOMImplementation domImpl =
                                        org.apache.batik.dom.GenericDOMImplementation.getDOMImplementation();
                
                                // Create an instance of org.w3c.dom.Document
                                org.w3c.dom.Document document = domImpl.createDocument(null, "svg", null);
                        
                                Writer out = resp.getWriter();
                                SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
                                cat.getTable().draw(svgGenerator, new Rectangle(width, height));
                                Element retElement = svgGenerator.getRoot();
                                
                                retElement.setAttributeNS(null, "x", Integer.toString(x));
                                retElement.setAttributeNS(null, "y", Integer.toString(y));
                                retElement.setAttributeNS(null, "width", Integer.toString(width));
                                retElement.setAttributeNS(null, "height", Integer.toString(height));                    
                                retElement.setAttributeNS(null, "object", "table");
                                retElement.setAttributeNS(null, "elementID", "alarmsTable");
                                retElement.setAttributeNS(null, "devicename", PAOFuncs.getYukonPAOName(deviceID));
                                retElement.setAttributeNS(null, "deviceid", Integer.toString(deviceID)); 
                
                                Element text = document.createElementNS(svgNS,"text");
                                text.setAttributeNS(null, "fill","rgb(0,125,122)");
                                text.setAttributeNS(null, "x", Integer.toString(ackX));
                                text.setAttributeNS(null, "y", Integer.toString(ackY));
                                text.setAttributeNS(null, "devicename", PAOFuncs.getYukonPAOName(deviceID));
                                text.setAttributeNS(null, "deviceid", Integer.toString(deviceID)); 
                                text.setAttributeNS(null, "onclick", "acknowledgeAlarm(evt)");
                                Text theText = document.createTextNode("Acknowledge Alarms");
                                text.insertBefore(theText,null);
                                retElement.appendChild(text);

                                svgGenerator.stream(retElement, out, true);                             
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
