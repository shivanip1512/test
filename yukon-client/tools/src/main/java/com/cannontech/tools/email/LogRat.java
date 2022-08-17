/*
 * Created on May 3, 2004
 */
package com.cannontech.tools.email;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * @author aaron
 */
public class LogRat implements Runnable {
	private String _smtpHost;
	private String _fromAddress;
	private String _toAddress;
	private String _watchDir;
	private String _logName;
	private long _interval;

	public LogRat(String smtpHost, String fromAddress, String toAddress, String watchDir, String logName, long interval) {
		_smtpHost = smtpHost;
		_fromAddress = fromAddress;
		_toAddress = toAddress;
		_watchDir = watchDir;
		_logName = logName;
		_interval = interval;
	}
		
	public void run() {
		System.out.println(new Date() + " - LogRat starting to sniff, dir=" + _watchDir + " name=" + _logName + " interval: " + _interval);
		
		long prevModified = Long.MIN_VALUE;
		boolean sentAlert = false;
		while(true) {
			String fileName = getTodaysFileName();
			File f = new File(_watchDir + "/" + fileName);
			
			long lastModified = f.lastModified();
			if(prevModified == lastModified) {
				if(!sentAlert) { 				
					//holy hell! let 'em know
					System.out.println(new Date() + " - " + fileName + " hasn't changed in the last " + _interval + " seconds!");
					String subject = "[LogRat] Alert - " + _logName;
					String body = _logName + "'s log file, " + fileName + " hasn't changed in the last " + _interval + " seconds";
					try{
						sendEmail(_smtpHost, _fromAddress, _toAddress, subject, body);
					}
					catch(Exception e) {
						e.printStackTrace(); 
					}
					sentAlert = true;
				}
				else {
					System.out.println(new Date() + " - " + fileName + " still hasn't changed");				
				}
			}	
			else {
				sentAlert = false;		
			}
			prevModified = lastModified;
			try {
				Thread.sleep(_interval*1000L);
			}
			catch(InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	}
	

	private String getTodaysFileName() {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(new java.util.Date());
		String dayStr = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
		if(dayStr.length() == 1) {
			dayStr = "0" + dayStr;
		}
		return _logName +  dayStr + ".log"; 
	}
	
	private void sendEmail(String smtpHost, String fromAddress, String toAddress, String subject, String body) throws MessagingException, AddressException {
		java.util.Properties systemProps = System.getProperties();
		systemProps.put("mail.smtp.host", smtpHost);
		Session session = Session.getInstance(systemProps);

		Message	message = new MimeMessage(session);
		message.setFrom(new InternetAddress(fromAddress));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
		message.setSubject(subject);
		Multipart multiPart = new MimeMultipart();
		MimeBodyPart bodyPart = new MimeBodyPart();
		bodyPart.setText(body);
		multiPart.addBodyPart(bodyPart);
		
		message.setContent(multiPart);
		message.setHeader("X-Mailer", "CannontechEmail");
		message.setSentDate(new java.util.Date());

		Transport.send(message);
	}
	
	public static LogRat[] makeRats(Properties props) {
		ArrayList ratList = new ArrayList();
		
		String smtpHost = props.getProperty("lograt.smtp.host");
		String watchDir = props.getProperty("lograt.watchdir", "c:/yukon/server/log");
		String emailTo = props.getProperty("lograt.email.to");
		String emailFrom = props.getProperty("lograt.email.from", "lograt@cannontech.com");
		
		Iterator keyIter = props.keySet().iterator();
		while(keyIter.hasNext()) {
			String prop = keyIter.next().toString();
			if(prop.startsWith("lograt.log") && prop.endsWith(".name")) {
				 int ratNum = Integer.parseInt(prop.substring("lograt.log".length(),prop.indexOf(".name")));
				 String logName = props.getProperty(prop);
				 long interval = Long.parseLong(props.getProperty("lograt.log" + ratNum + ".interval"));
				 LogRat rat = new LogRat(smtpHost, emailFrom, emailTo, watchDir, logName, interval);
				 ratList.add(rat);
			}
		}
		LogRat[] ratArr = new LogRat[ratList.size()];
		ratList.toArray(ratArr);
		return ratArr;
	}
	
	public static void main(String[] args) throws Exception {
		Properties ratProps = new Properties();
		ratProps.load(new FileInputStream(args[0]));
		LogRat[] lr = makeRats(ratProps);
		
		Thread[] thr = new Thread[lr.length];
		for(int i = 0; i < thr.length; i++) {
			thr[i] = new Thread(lr[i]);
			thr[i].start();
		}
	}
}
