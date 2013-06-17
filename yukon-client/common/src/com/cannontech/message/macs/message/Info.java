package com.cannontech.message.macs.message;

/**
 * Insert the type's description here.
 * Creation date: (2/19/2001 1:09:03 PM)
 * @author: 
 */
public class Info extends com.cannontech.message.util.Message {
	private long id;
	private String info;
/**
 * Info constructor comment.
 */
public Info() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (2/19/2001 1:10:25 PM)
 * @return long
 */
public long getId() {
	return id;
}
/**
 * Insert the method's description here.
 * Creation date: (2/19/2001 1:09:59 PM)
 * @return java.lang.String
 */
public java.lang.String getInfo() {
	return info;
}
/**
 * Insert the method's description here.
 * Creation date: (2/19/2001 1:10:25 PM)
 * @param newId long
 */
public void setId(long newId) {
	id = newId;
}
/**
 * Insert the method's description here.
 * Creation date: (2/19/2001 1:09:59 PM)
 * @param newInfo java.lang.String
 */
public void setInfo(java.lang.String newInfo) {
	info = newInfo;
}
}
