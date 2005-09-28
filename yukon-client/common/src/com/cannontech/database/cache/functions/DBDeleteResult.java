package com.cannontech.database.cache.functions;


/**
 * Result of a deletion attemp for a given DBPersistent
 * @author ryan
 */
public class DBDeleteResult {

	private int itemID = -1; 
	private int delType = -1;
	private StringBuffer confirmMessage = new StringBuffer(16);
	private StringBuffer unableDelMsg = new StringBuffer(16);
	private StringBuffer descriptionMsg = new StringBuffer(16);

	private boolean deletable = false;

	/**
	 * 
	 */
	protected DBDeleteResult() {
		this( -1, -1 );
	}

	/**
	 *
	 */
	public DBDeleteResult( int delItemID, int deleteType ) {
		super();
		setItemID( delItemID );
		setDelType( deleteType );
	}

	/**
	 * @return
	 */
	public StringBuffer getConfirmMessage() {
		return confirmMessage;
	}

	/**
	 * @return
	 */
	public int getDelType() {
		return delType;
	}

	/**
	 * @return
	 */
	public StringBuffer getDescriptionMsg() {
		return descriptionMsg;
	}

	/**
	 * @return
	 */
	public int getItemID() {
		return itemID;
	}

	/**
	 * @return
	 */
	public StringBuffer getUnableDelMsg() {
		return unableDelMsg;
	}

	/**
	 * @param buffer
	 */
	public void setConfirmMessage(StringBuffer buffer) {
		confirmMessage = buffer;
	}

	/**
	 * @param i
	 */
	public void setDelType(int i) {
		delType = i;
	}

	/**
	 * @param buffer
	 */
	public void setDescriptionMsg(StringBuffer buffer) {
		descriptionMsg = buffer;
	}

	/**
	 * @param i
	 */
	public void setItemID(int i) {
		itemID = i;
	}

	/**
	 * @param buffer
	 */
	public void setUnableDelMsg(StringBuffer buffer) {
		unableDelMsg = buffer;
	}

	/**
	 * @return
	 */
	public boolean isDeletable() {
		return deletable;
	}

	/**
	 * @param b
	 */
	public void setDeletable(boolean b) {
		deletable = b;
	}

}
