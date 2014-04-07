package com.cannontech.stars.energyCompany.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.energyCompany.model.EnergyCompany.Builder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class EnergyCompanyTest {
    private final static Object[][] sampleEnergyCompaniesInfo = new Object[][] {
        // ecId, name, user, contactId, parentEcId
        {1, "Middle Earth Power & Light", "mop", 100, null},
        {2, "Moria Gas & Electric", "mgop", 101, 1},
        {3, "Shire Power", "spop", 102, 1},
        {4, "Western Regions Electric", "wreop", 103, 1},
        {5, "North Shire Loop", "nsop", 104, 3},
        {6, "Western Territories", "wtop", 105, 3},
        {7, "Durin Took Gardens", "top", 106, 3},
        {8, "Some Random Gas & Electric", "srgeop", 200, null},
        {9, "Another Random Power Co.", "arpop", 200, null},
        {100, "We Pretend Power & Light", "wpop", 300, null},
        {101, "Don't You Dare Pizza!", "pizza", 301, 100},

        {200, "Bobs grandson's grandson", "",   301, 201},
        {201, "Bobs grandson", "",              301, 202},
        {202, "Bobs child", "",                 301, 203},
        {203, "Bob", "",                        301, null},
    };

    Map<Integer, EnergyCompany> energyCompanies;

    private void basicTests(EnergyCompany ec, String name, int numberOfChildrenExpected, EnergyCompany parent) {
        assertEquals("Wrong name for ecId " + ec.getId() + ".", name, ec.getName());
        assertEquals("Incorrect number of children of " + name + " EnergyCompany.",
            numberOfChildrenExpected, ec.getChildren().size());

        // The parent should be the exact same object as the one in the map (not just equivalent).
        assertTrue(parent == ec.getParent());
    }

    private void verifyCorrectChildren(EnergyCompany ec, EnergyCompany... children) {
        assertTrue(ec.getChildren().containsAll(ImmutableList.copyOf(children)));
    }

    private void verifyCorrectDescendants(EnergyCompany ec, EnergyCompany... descendants) {
        List<EnergyCompany> descendantsList = Lists.newArrayList(descendants);
        List<EnergyCompany> desc = ec.getDescendants(false);
        assertTrue(ec.getDescendants(false).containsAll(descendantsList));
        descendantsList.add(ec);
        assertTrue(ec.getDescendants(true).containsAll(descendantsList));
    }

    @Before
    public void setupEnergyCompanies() {
        Builder builder = new EnergyCompany.Builder();
        int userId = 7000;
        for (Object[] sampleEcInfo : sampleEnergyCompaniesInfo) {
            int ecId = (Integer) sampleEcInfo[0];
            String name = (String) sampleEcInfo[1];
            String userName = (String) sampleEcInfo[2];
            int contactId = (Integer) sampleEcInfo[3];
            Integer parentEcId = (Integer) sampleEcInfo[4];

            builder.addEnergyCompany(ecId, name, new LiteYukonUser(++userId, userName), contactId, parentEcId);
        }
        energyCompanies = builder.build();

        assertEquals("Incorrect number of EnergyCompany objects in map.",
            sampleEnergyCompaniesInfo.length, energyCompanies.size());
    }

    @Test
    public void basicTests() {
        EnergyCompany mepl = energyCompanies.get(1);
        basicTests(mepl, "Middle Earth Power & Light", 3, null);

        EnergyCompany moria = energyCompanies.get(2);
        basicTests(moria, "Moria Gas & Electric", 0, mepl);

        EnergyCompany shire = energyCompanies.get(3);
        basicTests(shire, "Shire Power", 3, mepl);

        EnergyCompany westernRegionsElectric = energyCompanies.get(4);
        basicTests(westernRegionsElectric, "Western Regions Electric", 0, mepl);

        EnergyCompany northShireLoop = energyCompanies.get(5);
        basicTests(northShireLoop, "North Shire Loop", 0, shire);

        EnergyCompany westernTerritoriesOfShire = energyCompanies.get(6);
        basicTests(westernTerritoriesOfShire, "Western Territories", 0, shire);

        EnergyCompany durinTookGardens = energyCompanies.get(7);
        basicTests(durinTookGardens, "Durin Took Gardens", 0, shire);

        EnergyCompany someRandom = energyCompanies.get(8);
        basicTests(someRandom, "Some Random Gas & Electric", 0, null);

        EnergyCompany anotherRandom = energyCompanies.get(9);
        basicTests(anotherRandom, "Another Random Power Co.", 0, null);

        EnergyCompany wePretend = energyCompanies.get(100);
        basicTests(wePretend, "We Pretend Power & Light", 1, null);

        EnergyCompany pizza = energyCompanies.get(101);
        basicTests(pizza, "Don't You Dare Pizza!", 0, wePretend);

        verifyCorrectChildren(mepl, moria, shire, westernRegionsElectric);
        verifyCorrectChildren(shire, northShireLoop, westernTerritoriesOfShire, durinTookGardens);
        verifyCorrectChildren(wePretend, pizza);

        try {
            mepl.getChildren().add(pizza);
            fail("should throw exception");
        } catch (UnsupportedOperationException e) {
            // Cool beans...we aren't allowed to mess with the children list after the fact.
        }
    }

    @Test
    public void test_descendants() {
        EnergyCompany bob = energyCompanies.get(203);
        EnergyCompany bobII = energyCompanies.get(202);
        EnergyCompany bobIII = energyCompanies.get(201);
        EnergyCompany bobIV = energyCompanies.get(200);

        verifyCorrectDescendants(bob, bobII, bobIII, bobIV);
        verifyCorrectDescendants(bobII, bobIII, bobIV);
        verifyCorrectDescendants(bobIII, bobIV);
        verifyCorrectDescendants(bobIV);

        EnergyCompany mepl = energyCompanies.get(1);
        EnergyCompany moria = energyCompanies.get(2);
        EnergyCompany shire = energyCompanies.get(3);
        EnergyCompany westernRegionsElectric = energyCompanies.get(4);
        EnergyCompany northShireLoop = energyCompanies.get(5);
        EnergyCompany westernTerritoriesOfShire = energyCompanies.get(6);
        EnergyCompany durinTookGardens = energyCompanies.get(7);
        EnergyCompany wePretend = energyCompanies.get(100);
        EnergyCompany pizza = energyCompanies.get(101);

        verifyCorrectDescendants(mepl,
            moria, shire, westernRegionsElectric, northShireLoop, westernTerritoriesOfShire, durinTookGardens);
        verifyCorrectDescendants(shire, northShireLoop, westernTerritoriesOfShire, durinTookGardens);
        verifyCorrectDescendants(wePretend, pizza);

        try {
            mepl.getDescendants(false).add(pizza);
            fail("should throw exception");
        } catch (UnsupportedOperationException e) {
            // Awesome...we can't mess with the descendant list after the fact.
        }
    }
}
