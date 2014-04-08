package com.cannontech.stars.core.service;

import java.util.List;

import com.cannontech.core.dao.EnergyCompanyNotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.google.common.base.Function;

public interface YukonEnergyCompanyService {

    final int DEFAULT_ENERGY_COMPANY_ID = -1;

    Function<YukonEnergyCompany, Integer> TO_ID_FUNCTION = new Function<YukonEnergyCompany, Integer>() {
        @Override
        public Integer apply(YukonEnergyCompany energyCompany) {
            return energyCompany.getEnergyCompanyId();
        }
    };

    /**
     * Returns energy company associated with this operator user.
     * 
     * If this user is a residential customer, or no energy company is found throws EnergyCompanyNotFoundException
     */
    EnergyCompany getEnergyCompanyByOperator(LiteYukonUser operator);

    /**
     * Returns energy company associated with this user.
     * 
     * This user can be an operator or residential user.
     * If no energy company is found throws EnergyCompanyNotFoundException
     */
    EnergyCompany getEnergyCompanyByUser(LiteYukonUser user);

    /**
     * This method returns whether or not the operator provided is an Energy Company Operator
     */
    boolean isEnergyCompanyOperator(LiteYukonUser operator);
    
    /**
     * This method gets the yukon energy company that is associated with the supplied account.  
     * This method should be used as the primary option when you are working with an account. 
     */
    EnergyCompany getEnergyCompanyByAccountId(int accountId);

    /**
     * This method gets the yukon energy company that is associated with the supplied inventory.  
     * This method should be used as the primary option when you are working with an inventory. 
     */
    EnergyCompany getEnergyCompanyByInventoryId(int inventoryId);
    
    /**
     * Returns a list of all energy companies
     */
    List<EnergyCompany> getAllEnergyCompanies();

    boolean isDefaultEnergyCompany(YukonEnergyCompany energyCompany);

    /**
     * This method returns "true" if this is a primary operator.
     */
    boolean isPrimaryOperator(int operatorLoginId);
    
    EnergyCompany getEnergyCompany(int ecId);
    
    EnergyCompany findEnergyCompany(int ecId);

    /**
     * Returns an immutable list of all routeIds associated with the energy company
     */
    List<Integer> getRouteIds(int ecId);

    /**
     * This method gets the energy company id for the operator supplied.
     * 
     * @throws EnergyCompanyNotFoundException
     * @deprecated Use {@link #getEnergyCompanyByOperator()}.getEnergyCompanyId()
     */
    @Deprecated
    int getEnergyCompanyIdByOperator(LiteYukonUser operator);

    /**
     * This method returns all of the child energy companies underneath the supplied energy company 
     * no matter how many levels deep that may be.
     * 
     * @param energyCompanyId - The energyCompanyId supplied is not included in the resulting list.
     * @deprecated Use {@link #getEnergyCompany()}.getDescendants(false)
     */
    @Deprecated
    List<Integer> getChildEnergyCompanies(int energyCompanyId);

    /**
     * This method returns the direct child energy companies underneath the energy company.
     * It will not return any of the children of the child energy companies.  If you want that data you'll want to
     * use the getChildEnergyCompanies method above.
     * 
     * @param energyCompanyId - The energyCompanyId supplied is not included in the resulting list.
     * @deprecated Use {@link #getEnergyCompany()}.getChildren()
     */
    @Deprecated
    List<Integer> getDirectChildEnergyCompanies(int energyCompanyId);

    /**
     * This method returns the parent energy company id of the energy company id supplied.  This method will throw an exception
     * if you supply the default energy company or the main energy company.
     * @deprecated Use {@link #getEnergyCompany()}.getParent()
     */
    @Deprecated
    Integer getParentEnergyCompany(int energyCompanyId);

    /**
     * This method returns the parent energy company id of the energy company id supplied.  This will return null
     * if the energy company id is the default energy company or the main energy company.
     * @deprecated Use {@link #getEnergyCompany()}
     */
    @Deprecated
    Integer findParentEnergyCompany(int energyCompanyId);
}