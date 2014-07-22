package com.cannontech.tools.email;/**
 * Insert the type's description here.
 * Creation date: (11/14/2001 10:33:37 PM)
 * @author: 
 */
class MailAccount 
{
	private String server = ""; // name of the (pop,imap) server
	private String protocol = "pop"; // the protocol for receiving mail (pop,imap)
	private int port = 110; // port of the (pop,imap) server
	private String login = ""; // login name to the (pop,imap) server
	private String pass = ""; // pass to the (pop,imap) server
	private boolean expunge = false; // expunge the messages from the server after downloading them
	private boolean ssl = false; // use a ssl connection
	
	private String outServer = ""; // name of the (smtp) server
	private String outProtocol = "smtp"; // the protocol for sending mail (smtp)
	private int outPort = 25; // port of the (smtp) server
	private String outLogin = ""; // login name to the (smtp) server
	private String outPass = ""; // pass to the (smtp) server

	private String accountName = ""; 
	private String from = ""; // email used as senderaddress for this (smtp) server
	private String replyTo = ""; // replyto addresses for this (smtp) server
	private String name = ""; // real nameof the user
	private String organization = "Cannon Technologies"; 
	private String signature = "";
	private boolean alwaysSign = false;

	private String folderName = ""; // name of the rootfolder on the local store
/**
 * MailAccount constructor comment.
 */
public MailAccount() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @return java.lang.String
 */
public java.lang.String getAccountName() {
	return accountName;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @return java.lang.String
 */
public java.lang.String getFolderName() {
	return folderName;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @return java.lang.String
 */
public java.lang.String getFrom() {
	return from;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @return java.lang.String
 */
public java.lang.String getLogin() {
	return login;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @return java.lang.String
 */
public java.lang.String getOrganization() {
	return organization;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @return java.lang.String
 */
public java.lang.String getOutLogin() {
	return outLogin;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @return java.lang.String
 */
public java.lang.String getOutPass() {
	return outPass;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 11:43:01 PM)
 * @return int
 */
public int getOutPort() {
	return outPort;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:29 PM)
 * @return java.lang.String
 */
public java.lang.String getOutProtocol() {
	return outProtocol;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @return java.lang.String
 */
public java.lang.String getOutServer() {
	return outServer;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @return java.lang.String
 */
public java.lang.String getPass() {
	return pass;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @return int
 */
public int getPort() {
	return port;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @return java.lang.String
 */
public java.lang.String getProtocol() {
	return protocol;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @return java.lang.String
 */
public java.lang.String getReplyTo() {
	return replyTo;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @return java.lang.String
 */
public java.lang.String getServer() {
	return server;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @return java.lang.String
 */
public java.lang.String getSignature() {
	return signature;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @return boolean
 */
public boolean isAlwaysSign() {
	return alwaysSign;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @return boolean
 */
public boolean isExpunge() {
	return expunge;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @return boolean
 */
public boolean isSsl() {
	return ssl;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @param newAccountName java.lang.String
 */
public void setAccountName(java.lang.String newAccountName) {
	accountName = newAccountName;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @param newAlwaysSign boolean
 */
public void setAlwaysSign(boolean newAlwaysSign) {
	alwaysSign = newAlwaysSign;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @param newExpunge boolean
 */
public void setExpunge(boolean newExpunge) {
	expunge = newExpunge;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @param newFolderName java.lang.String
 */
public void setFolderName(java.lang.String newFolderName) {
	folderName = newFolderName;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @param newFrom java.lang.String
 */
public void setFrom(java.lang.String newFrom) {
	from = newFrom;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @param newLogin java.lang.String
 */
public void setLogin(java.lang.String newLogin) {
	login = newLogin;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @param newName java.lang.String
 */
public void setName(java.lang.String newName) {
	name = newName;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @param newOrganization java.lang.String
 */
public void setOrganization(java.lang.String newOrganization) {
	organization = newOrganization;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @param newOutLogin java.lang.String
 */
public void setOutLogin(java.lang.String newOutLogin) {
	outLogin = newOutLogin;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @param newOutPass java.lang.String
 */
public void setOutPass(java.lang.String newOutPass) {
	outPass = newOutPass;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 11:43:01 PM)
 * @param newOutPort int
 */
public void setOutPort(int newOutPort) {
	outPort = newOutPort;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:29 PM)
 * @param newOutProtocol java.lang.String
 */
public void setOutProtocol(java.lang.String newOutProtocol) {
	outProtocol = newOutProtocol;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @param newOutServer java.lang.String
 */
public void setOutServer(java.lang.String newOutServer) {
	outServer = newOutServer;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @param newPass java.lang.String
 */
public void setPass(java.lang.String newPass) {
	pass = newPass;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @param newPort int
 */
public void setPort(int newPort) {
	port = newPort;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @param newProtocol java.lang.String
 */
public void setProtocol(java.lang.String newProtocol) {
	protocol = newProtocol;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @param newReplyTo java.lang.String
 */
public void setReplyTo(java.lang.String newReplyTo) {
	replyTo = newReplyTo;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @param newServer java.lang.String
 */
public void setServer(java.lang.String newServer) {
	server = newServer;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @param newSignature java.lang.String
 */
public void setSignature(java.lang.String newSignature) {
	signature = newSignature;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 10:37:03 PM)
 * @param newSsl boolean
 */
public void setSsl(boolean newSsl) {
	ssl = newSsl;
}
}