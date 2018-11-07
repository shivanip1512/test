package com.cannontech.stars.core.dao;

import java.util.Collection;
import java.util.List;

import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.google.common.base.Function;

public interface EnergyCompanyDao {

    final int DEFAULT_ENERGY_COMPANY_ID = -1;

    Function<YukonEnergyCompany, Integer> TO_ID_FUNCTION = new Function<YukonEnergyCompany, Integer>() {
        @Override
        public Integer apply(YukonEnergyCompany energyCompany) {
            return energyCompany.getEnergyCompanyId();
        }
    };

    /**
     * Get energy company associated with this operator user.
     *
     * If this user is a residential customer, or no energy company is found throws
     * EnergyCompanyNotFoundException
     */
    EnergyCompany getEnergyCompanyByOperator(LiteYukonUser operator);

    /**
     * Get energy company associated with this user.
     *
     * This user can be an operator or residential user.
     * If no energy company is found returns default energy company
     */
    EnergyCompany getEnergyCompany(LiteYukonUser user);

    /**
     * Determine whether or not the operator provided is an Energy Company Operator.
     */
    boolean isEnergyCompanyOperator(LiteYukonUser operator);

    /**
     * Get the energy company that is associated with the supplied account.
     * This method should be used as the primary option when you are working with an account.
     */
    EnergyCompany getEnergyCompanyByAccountId(int accountId);

    /**
     * Get the energy company that is associated with the supplied inventory.
     * This method should be used as the primary option when you are working with inventory.
     */
    EnergyCompany getEnergyCompanyByInventoryId(int inventoryId);

    /**
     * Get a list of all energy companies
     */
    Collection<EnergyCompany> getAllEnergyCompanies();

    boolean isDefaultEnergyCompany(YukonEnergyCompany energyCompany);

    boolean isPrimaryOperator(int operatorLoginId);

    EnergyCompany getEnergyCompany(int ecId);

    EnergyCompany findEnergyCompany(int ecId);

    EnergyCompany getEnergyCompany(String energyCompanyName);

    EnergyCompany findEnergyCompany(String energyCompanyName);

    /**
     * Get an immutable list of all routeIds associated with the energy company
     */
    List<Integer> getRouteIds(int ecId);

    /**
     * Get all routes assigned to this energy company (or all routes in Yukon if it is a single energy company
     * system), ordered alphabetically.
     */
    List<LiteYukonPAObject> getAllRoutes(EnergyCompany energyCompany);

    void addCiCustomer(int customerId, EnergyCompany energyCompany);

    List<Integer> getCiCustomerIds(EnergyCompany energyCompany);

    /**
     * Only gets energy companies for CI customers. NOT residential customers
     */
    List<EnergyCompany> getEnergyCompaniesByCiCustomer(int customerId);

    /**
     * Returns the energy company Id of the newly created energy company
     */
    int createEnergyCompany(String name, int contactId, LiteYukonUser user);

    void updateCompanyName(String name, int ecId);

    List<Integer> getOperatorUserIds(EnergyCompany energyCompany);

    /**
     * Get the direct child energy companies underneath the energy company.
     *
     * It will not return any of the children of the child energy companies.  If you want that data you'll want
     * to use the getChildEnergyCompanies method above.
     *
     * @param energyCompanyId - The energyCompanyId supplied is not included in the resulting list.
     * @deprecated Use {@link #getEnergyCompany()}.getChildren()
     */
    @Deprecated
    List<Integer> getDirectChildEnergyCompanies(int energyCompanyId);

    /**
     * Get the parent energy company id of the energy company id supplied.  This method will
     * throw an exception
     * if you supply the default energy company or the main energy company.
     * @deprecated Use {@link #getEnergyCompany()}.getParent()
     */
    @Deprecated
    Integer getParentEnergyCompany(int energyCompanyId);

    /**
     * Get  the parent energy company id of the energy company id supplied.  This will return null
     * if the energy company id is the default energy company or the main energy company.
     * @deprecated Use {@link #getEnergyCompany()}
     */
    @Deprecated
    Integer findParentEnergyCompany(int energyCompanyId);
    
    /**
     * 
     *Attempts to find an energy company that should be used for third party integrations or automatic/system usage. 
     *If the master.cfg entry for RFN_ENERGY_COMPANY_NAME exists, then this will be the energy company returned. 
     *Note: RFN_ENERGY_COMPANY_NAME is poorly named and not just limiting us to "RFN". 
     *If no master.cfg entry for RFN_ENERGY_COMPANY_NAME exists, then attempt to find a reasonable default to use. 
     *A reasonable default would be if there exists exactly one energy company that is not the Default Energy Company (ie ecId > -1). 
     *If a reasonable default cannot be found, throws EnergyCompanyNotFoundException.     
     */
    
    EnergyCompany getDefaultEnergyCompanyForThirdPartyApiOrSystemUsage();
}
