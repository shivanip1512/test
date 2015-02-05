package com.cannontech.stars.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.service.StarsSearchService;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteAccountInfo;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.energyCompany.model.EnergyCompany;

/**
 * Used to handle requests for passwords and send the requests
 * to the email address of the Energy Company that owns this Contact,
 * YukonUser or Customer. If we can not determine which Energy Company
 * to use, we send the request to the MasterMail, supplied in:
 */
public class StarsRequestPword extends RequestPword {
    private String accNum = null;
    private StarsCustAccountInformationDao starsCustAccountInformationDao;
    private StarsSearchService starsSearchService;

    public StarsRequestPword(String userName, String email, String fName,
            String lName, String accNum,
            StarsCustAccountInformationDao starsCustAccountInformationDao,
            StarsSearchService starsSearchService) {
        super(userName, email, fName, lName);

        this.accNum = accNum;

        String[] tParams = new String[allParams.length + 1];
        tParams[tParams.length - 1] = accNum;

        System.arraycopy(allParams, 0, tParams, 0, allParams.length);
        allParams = tParams;

        this.starsCustAccountInformationDao = starsCustAccountInformationDao;
        this.starsSearchService = starsSearchService;
    }

    @Override
    public void doRequest() {
        try {
            // uses STARS functionality
            if (accNum != null) {
                // we may continue after this, remove all the stored data
                foundData.clear();

                List<LiteStarsEnergyCompany> engrComps = StarsDatabaseCache.getInstance().getAllEnergyCompanies();
                LiteStarsEnergyCompany eComp = null;
                ArrayList<LiteAccountInfo> allCustAccts = new ArrayList<LiteAccountInfo>(8);

                for (LiteStarsEnergyCompany lsec : engrComps) {

                    LiteAccountInfo lCustInfo = null;
                    List<Integer> accounts = starsSearchService.searchAccountByAccountNumber(lsec, accNum, false, true);

                    lCustInfo = searchForMatchingAccount(accounts, lsec);

                    if (lCustInfo != null) {
                        allCustAccts.add(lCustInfo);
                        eComp = lsec;
                    }
                }

                if (allCustAccts.size() == 1) {
                    // only 1 found, this is good
                    LiteAccountInfo lCustInf = (LiteAccountInfo) allCustAccts.get(0);

                    LiteContact lc = YukonSpringHook.getBean(ContactDao.class).getContact(lCustInf.getCustomer().getPrimaryContactID());
                    if (lc != null) {
                        LiteYukonUser user = YukonSpringHook.getBean(YukonUserDao.class).getLiteYukonUser(lc.getLoginID());

                        foundData.add(" Username: " + user.getUsername());
                        foundData.add(" Contact Name: " + lc.getContFirstName() + " " + lc.getContLastName());
                    }

                    // we must get the Yukon lite energy company for the stars lite energy company
                    EnergyCompany energyCompany = YukonSpringHook.getBean(EnergyCompanyDao.class).getEnergyCompany(eComp.getEnergyCompanyId());

                    processEnergyCompanies(Collections.singletonList(energyCompany));
                } else if (allCustAccts.size() < 1) {
                    setState(RET_FAILED, "NO_ACCOUNT");
                } else {
                    setState(RET_FAILED, "MULTIPLE_ACCOUNTS");
                    subject = "WebMaster: " + subject;
                    foundData.add(" " + getResultString());
                    foundData.add(" Number of Account Numbers for this Account: " + allCustAccts.size());

                    for (LiteAccountInfo lCstInfo : allCustAccts) {
                        foundData.add("   Account #: " + lCstInfo.getCustomerAccount().getAccountNumber());
                    }
                    sendEmails(new String[] { masterMail }, genBody());
                }
            }

            // try the parents functionality
            if (getState() != RET_SUCCESS) {
                super.doRequest();
            }
        }
        // TODO: This whole servlet should be overhauled to improve exception
        // handling. For now, will just change the text of the error message.
        catch (Exception e) {
            // send this request with all its data to CTI
            setState(RET_FAILED, "DOES_NOT_MATCH");
            subject = "WebMaster: " + subject;
            CTILogger.error(e.getMessage(), e);
        }
    }

    private LiteAccountInfo searchForMatchingAccount(List<Integer> accounts, LiteStarsEnergyCompany lsec) {
        LiteAccountInfo lCustInfo = null;
        for (Integer accountId : accounts) {
            lCustInfo = starsCustAccountInformationDao.getById(accountId, lsec.getEnergyCompanyId());
            return lCustInfo;
        }
        return null;
    }

    @Override
    protected List<EnergyCompany> processContact(LiteContact lCont_) {
        LiteCustomer liteCust = YukonSpringHook.getBean(ContactDao.class).getCustomer(lCont_.getContactID());

        if (liteCust.getEnergyCompanyID() != -1) {
            EnergyCompany energyCompany = YukonSpringHook.getBean(EnergyCompanyDao.class).getEnergyCompany(liteCust.getEnergyCompanyID());
            return Collections.singletonList(energyCompany);
        }

        // Try the parent's functionality
        return super.processContact(lCont_);
    }
}