package com.cannontech.database.data.lite.stars;

import com.cannontech.database.Transaction;
import com.cannontech.database.data.customer.Contact;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteTypes;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteCustomerContact extends LiteBase {
	
	public static class ContactNotification {
		private boolean enabled = false;
		private String notification = null;
		
		protected ContactNotification() {}
		
		public static ContactNotification newInstance(boolean enabled, String notification) {
			ContactNotification instance = new ContactNotification();
			instance.setEnabled( enabled );
			instance.setNotification( notification );
			return instance;
		}

		public boolean isEnabled() {
			return enabled;
		}

		public String getNotification() {
			return notification;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public void setNotification(String notification) {
			this.notification = notification;
		}

	}

	private String lastName = null;
	private String firstName = null;
	private String homePhone = null;
	private String workPhone = null;
	private ContactNotification email = null;
	
	public LiteCustomerContact() {
		super();
		setLiteType( LiteTypes.STARS_CUSTOMER_CONTACT );
	}
	
	public LiteCustomerContact(int contactID) {
		super();
		setContactID( contactID );
		setLiteType( LiteTypes.STARS_CUSTOMER_CONTACT );
	}
	
	public int getContactID() {
		return getLiteID();
	}
	
	public void setContactID(int contactID) {
		setLiteID( contactID );
	}
	
	public void retrieve() {
		Contact contact = new Contact();
		contact.setContactID( new Integer(getContactID()) );
		try {
			contact = (Contact) Transaction.createTransaction(Transaction.RETRIEVE, contact).execute();
			StarsLiteFactory.setLiteCustomerContact( this, contact );
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the firstName.
	 * @return String
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Returns the homePhone.
	 * @return String
	 */
	public String getHomePhone() {
		return homePhone;
	}

	/**
	 * Returns the lastName.
	 * @return String
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Returns the workPhone.
	 * @return String
	 */
	public String getWorkPhone() {
		return workPhone;
	}

	/**
	 * Sets the firstName.
	 * @param firstName The firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Sets the homePhone.
	 * @param homePhone The homePhone to set
	 */
	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	/**
	 * Sets the lastName.
	 * @param lastName The lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Sets the workPhone.
	 * @param workPhone The workPhone to set
	 */
	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

	/**
	 * Returns the email.
	 * @return ContactNotification
	 */
	public ContactNotification getEmail() {
		return email;
	}

	/**
	 * Sets the email.
	 * @param email The email to set
	 */
	public void setEmail(ContactNotification email) {
		this.email = email;
	}

}
