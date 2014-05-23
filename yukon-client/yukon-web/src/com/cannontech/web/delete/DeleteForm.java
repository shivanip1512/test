package com.cannontech.web.delete;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DBDeleteResult;
import com.cannontech.core.dao.DBDeletionDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.capcontrol.CapControlArea;
import com.cannontech.database.data.capcontrol.CapControlSpecialArea;
import com.cannontech.database.data.capcontrol.CapControlSubstation;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.capcontrol.streamable.Area;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.editor.CapControlForm;
import com.cannontech.web.editor.DBEditorForm;
import com.cannontech.web.navigation.CtiNavObject;
import com.cannontech.web.util.JSFParamUtil;

/**
 * @author ryan
 * 
 */
public abstract class DeleteForm extends DBEditorForm {
	private int[] itemIDs = new int[0];
	private Deleteable[] deletables = null; // new Deleteable[0];
	private CapControlCache capControlCache = (CapControlCache) YukonSpringHook
			.getBean("capControlCache");

	public DeleteForm() {
		super();
		initItem();
	}

	/**
	 * Returns the DB object for the given ID; this could be a Point, PAO,
	 * Customer, etc.
	 */
	abstract DBPersistent getDBObj(int itemID);

	/**
	 * Calls the delete() method for each item that we are able to delete
	 */
	public void update() {
		ExternalContext ex = FacesContext.getCurrentInstance()
				.getExternalContext();
		if (deletables != null) {
			for (int i = 0; i < deletables.length; i++) {

				// this message will be filled in by the super class
				FacesMessage facesMsg = new FacesMessage();
				try {
					Area area = null;

					Deleteable deleteable = deletables[i];
					boolean isSubstation = false;
					if (deleteable.getDbPersistent() instanceof CapControlSubstation) { // get
																						// these
																						// before
																						// the
																						// delete
						isSubstation = true;
						com.cannontech.database.db.capcontrol.CapControlSubstation subDB = ((CapControlSubstation) deleteable
								.getDbPersistent()).getCapControlSubstation();
						Integer subId = subDB.getSubstationID();
						Integer areaId = 0;
						try {
							SubStation sub = capControlCache
									.getSubstation(subId);
							areaId = sub.getParentID();
						} catch (NotFoundException nfe) {
							areaId = 0;
						}
						if (areaId > 0) {
							try {
								area = capControlCache.getArea(areaId);
							} catch (NotFoundException nfe) {
								area = null;
							}
						}
					}
					// be sure we can attempt to delete this item
					if (deleteable.isDeleteAllowed()
							&& deleteable.getChecked().booleanValue()) {
						deleteDBObject(deleteable.getDbPersistent(), facesMsg);
						deleteable.setWasDeleted(true);
						facesMsg.setDetail("...deleted");
						if (deleteable.getDbPersistent() instanceof PointBase) {
							CapControlForm capControlForm = (CapControlForm) JSFParamUtil
									.getJSFVar("capControlForm");

							capControlForm.getPointTreeForm().resetPointList();
						}
						HttpSession session = (HttpSession) ex
								.getSession(false);
						CtiNavObject navObject = (CtiNavObject) session
								.getAttribute("CtiNavObject");
						if (area != null) {
							String previousPage = navObject.getPreviousPage();
							if (previousPage.contains("feeders")) {
								// We came from the substation's page, and that
								// station doesn't exist anymore. Go to the
								// areas page instead.
								navObject.setModuleExitPage(ServletUtil
										.createSafeUrl((ServletRequest) ex
												.getRequest(),
												"/capcontrol/tier/areas"));
							} else {
								if (isSubstation
										&& area.getStations().length <= 1) {
									/*
									 * Sort of a hack. We can't trust that the
									 * cache updated immediately, so it's likely
									 * that if there was only one substation on
									 * the area, even though we just deleted it,
									 * the cache would still show that the area
									 * has one last station. If the length of
									 * the substations list in the area has a
									 * length of one or less and what we just
									 * deleted was a substation, redirect the
									 * user to the areas page.
									 * 
									 * Worst-case scenario, the cache was right
									 * and the user is redirected to the areas
									 * page instead of the page for this area
									 * with its one last substation. The
									 * alternative would be directing them back
									 * to an empty area page, which is much less
									 * user-friendly.
									 */
									navObject.setModuleExitPage(ServletUtil
											.createSafeUrl((ServletRequest) ex
													.getRequest(),
													"/capcontrol/tier/areas"));
								}
							}
						} else {
							// This is to set the return page to list page when
							// a area/special area is deleted.
							if (deleteable.getDbPersistent() instanceof CapControlArea) {
								navObject.setModuleExitPage(ServletUtil
										.createSafeUrl((ServletRequest) ex
												.getRequest(),
												"/capcontrol/tier/areas"));
							} else if (deleteable.getDbPersistent() instanceof CapControlSpecialArea) {
								navObject
										.setModuleExitPage(ServletUtil.createSafeUrl(
												(ServletRequest) ex
														.getRequest(),
												"/capcontrol/tier/areas?isSpecialArea=true"));
							}
						}
					} else {
						facesMsg.setDetail("Item not deleted");
					}
				} catch (TransactionException te) {
					// do nothing since the appropriate actions was taken in the
					// super
				} finally {
					deletables[i].setWarningMsg(facesMsg.getDetail());
					deletables[i]
							.setDeleteError(facesMsg.getSeverity() == FacesMessage.SEVERITY_ERROR);
				}
			}
		}

	}

	public void initItem() {

		String[] ids = JSFParamUtil.getReqParamsVar("value");
		if (ids == null)
			return;

		itemIDs = new int[ids.length];
		for (int i = 0; i < ids.length; i++) {
			itemIDs[i] = Integer.parseInt(ids[i]);
			CTILogger.debug("  DeleteFrom inited for item id = " + itemIDs[i]);
		}
		reset();
	}

	/**
	 * Determine if we can delete this item and set any message as to why we may
	 * or may not be able to delete it.
	 */
	protected void setDeleteMsgs(Deleteable delItem) {

		DBDeleteResult delRes = null;

		try {
			// get the info about this possible deletion candidate
			delRes = YukonSpringHook.getBean(DBDeletionDao.class)
					.getDeleteInfo(delItem.getDbPersistent(),
							delItem.getDbPersistent().toString());

			int res = YukonSpringHook.getBean(DBDeletionDao.class)
					.deletionAttempted(delRes);
			delItem.setDeleteAllowed(DBDeletionDao.STATUS_DISALLOW != res);

			delItem.setWarningMsg(delItem.isDeleteAllowed() ? delRes
					.getConfirmMessage().toString() : delRes.getUnableDelMsg()
					.append(delRes.getDescriptionMsg()).toString());

		} catch (Exception e) {
			CTILogger.error(e.getMessage(), e);
			delItem.setWarningMsg("(db exception occurred)");
		}

	}

	public void reset() {
		setDeletables(null);
	}

	/**
	 * Retrieves the items that are to be deleted
	 */
	public Deleteable[] getDeleteItems() {
		if (getDeletables() == null) {
			setDeletables(new Deleteable[getItemIDs().length]);
			for (int i = 0; i < getItemIDs().length; i++) {

				getDeletables()[i] = new Deleteable();
				try {
					DBPersistent dbObj = getDBObj(getItemIDs()[i]);
					if (dbObj == null) {
						CTILogger
								.warn("Unable to find item ID = "
										+ getItemIDs()[i]
										+ " in cache, ignoring entry");
						continue;
					}
					// when we delete points we have a check box that confirms
					// the deletion,
					// on the paos we don't
					if (!(dbObj instanceof PointBase)) {
						getDeletables()[i].setChecked(true);
					}
					getDeletables()[i].setDbPersistent(dbObj);
				} catch (NotFoundException nfe) {
					CTILogger.warn("Unable to find item ID = "
							+ getItemIDs()[i] + " in cache, ignoring entry");
					continue;
				}

				try {
					Transaction.createTransaction(Transaction.RETRIEVE,
							getDeletables()[i].getDbPersistent()).execute();
					// if we retrieve the item, find out if we can delete it
					setDeleteMsgs(getDeletables()[i]);
				} catch (Exception e) {
					CTILogger.warn("Unable to Retrieve item with id: "
							+ getDeletables()[i].getDbPersistent().toString(),
							e);
				}
			}
		}

		return getDeletables();
	}

	/**
	 * @return
	 */
	protected Deleteable[] getDeletables() {
		return deletables;
	}

	/**
	 * @return
	 */
	protected int[] getItemIDs() {
		return itemIDs;
	}

	/**
	 * @param deleteables
	 */
	protected void setDeletables(Deleteable[] deleteables) {
		deletables = deleteables;
	}

	/**
	 * @param is
	 */
	protected void setItemIDs(int[] is) {
		itemIDs = is;
	}

}