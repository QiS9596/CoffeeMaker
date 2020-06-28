/*
 * Copyright (c) 2009,  Sarah Heckman, Laurie Williams, Dright Ho
 * All Rights Reserved.
 *
 * Permission has been explicitly granted to the University of Minnesota
 * Software Engineering Center to use and distribute this source for
 * educational purposes, including delivering online education through
 * Coursera or other entities.
 *
 * No warranty is given regarding this software, including warranties as
 * to the correctness or completeness of this software, including
 * fitness for purpose.
 *
 *
 * Modifications
 * 20171114 - Ian De Silva - Updated to comply with JUnit 4 and to adhere to
 * 							 coding standards.  Added test documentation.
 */
package edu.ncsu.csc326.coffeemaker;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import edu.ncsu.csc326.coffeemaker.exceptions.InventoryException;
import edu.ncsu.csc326.coffeemaker.exceptions.RecipeException;
import edu.ncsu.csc326.coffeemaker.Inventory;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Field;

/**
 * Unit tests for CoffeeMaker class.
 *
 * @author Sarah Heckman
 */
public class CoffeeMakerTest {

    /**
     * The object under test.
     */
    private CoffeeMaker coffeeMaker;

    // Sample recipes to use in testing.
    private Recipe recipe1;
    private Recipe recipe2;
    private Recipe recipe3;
    private Recipe recipe4;

    /**
     * Initializes some recipes to test with and the {@link CoffeeMaker}
     * object we wish to test.
     *
     * @throws RecipeException if there was an error parsing the ingredient
     *                         amount when setting up the recipe.
     */
    @Before
    public void setUp() throws RecipeException {
        coffeeMaker = new CoffeeMaker();

        //Set up for r1
        recipe1 = new Recipe();
        recipe1.setName("Coffee");
        recipe1.setAmtChocolate("0");
        recipe1.setAmtCoffee("3");
        recipe1.setAmtMilk("1");
        recipe1.setAmtSugar("1");
        recipe1.setPrice("50");

        //Set up for r2
        recipe2 = new Recipe();
        recipe2.setName("Mocha");
        recipe2.setAmtChocolate("20");
        recipe2.setAmtCoffee("3");
        recipe2.setAmtMilk("1");
        recipe2.setAmtSugar("1");
        recipe2.setPrice("75");

        //Set up for r3
        recipe3 = new Recipe();
        recipe3.setName("Latte");
        recipe3.setAmtChocolate("0");
        recipe3.setAmtCoffee("3");
        recipe3.setAmtMilk("3");
        recipe3.setAmtSugar("1");
        recipe3.setPrice("100");

        //Set up for r4
        recipe4 = new Recipe();
        recipe4.setName("Hot Chocolate");
        recipe4.setAmtChocolate("4");
        recipe4.setAmtCoffee("0");
        recipe4.setAmtMilk("1");
        recipe4.setAmtSugar("1");
        recipe4.setPrice("65");
    }


    /**
     * Given a coffee maker with the default inventory
     * When we add inventory with well-formed quantities
     * Then we do not get an exception trying to read the inventory quantities.
     * <p>
     * This test provide correct integer input
     *
     * @throws InventoryException if there was an error parsing the quanity
     *                            to a positive integer.
     */
    @Test
    public void testAddInventory_correct1() throws InventoryException, NoSuchFieldException, IllegalAccessException {
        coffeeMaker.addInventory("4", "7", "0", "9");
        Field inventory_field = CoffeeMaker.class.getDeclaredField("inventory");
        inventory_field.setAccessible(true);
        Inventory inventory = (Inventory) inventory_field.get(coffeeMaker);
        // the default about of all resources should be 15
        assertEquals(19, inventory.getCoffee()); // should be 15+4=19
        assertEquals(22, inventory.getMilk()); // should be 15+7=22
        assertEquals(15, inventory.getSugar()); // should be 15+0=15
        assertEquals(24, inventory.getChocolate()); // should be 15+9=24
    }

    /**
     * This test also provide the default integer input that should not yield any exception to CoffeMaker or it's
     * belonging objects
     */
    @Test
    public void testAddInventory_correct2() throws InventoryException, NoSuchFieldException, IllegalAccessException {
        coffeeMaker.addInventory("0", "0", "2", "11");
        Field inventory_field = CoffeeMaker.class.getDeclaredField("inventory");
        inventory_field.setAccessible(true);
        Inventory inventory = (Inventory) inventory_field.get(coffeeMaker);
        // the default about of all resources should be 15
        assertEquals(15, inventory.getCoffee());
        assertEquals(15, inventory.getMilk());
        assertEquals(17, inventory.getSugar());
        assertEquals(26, inventory.getChocolate());
    }

    /**
     * This test targets on testing addInventory method for CoffeeMaker class, and this test specifically concentrates
     * on providing medium to large input to the addInventory method
     */
    @Test
    public void testAddInventory_correct3() throws InventoryException, NoSuchFieldException, IllegalAccessException {
        coffeeMaker.addInventory("354", "1088", "9258", "69557");
        Field inventory_field = CoffeeMaker.class.getDeclaredField("inventory");
        inventory_field.setAccessible(true);
        Inventory inventory = (Inventory) inventory_field.get(coffeeMaker);
        // the default about of all resources should be 15
        assertEquals(369, inventory.getCoffee());
        assertEquals(1103, inventory.getMilk());
        assertEquals(9273, inventory.getSugar());
        assertEquals(69572, inventory.getChocolate());
    }

    /**
     * This test targets on testing addInventory method for CoffeeMaker class,
     * this test challenges the addInventory with extreme large integers that will cause integer overflow
     * Though integer overflow will cause un interpretable value to fields in inventory object, yet the specification
     * of the program does not say the program should reject this, so this test will expect the overflowed value to be negative
     */
    @Test
    public void testAddInventory_boundaryIntOverflow1() throws InventoryException, NoSuchFieldException, IllegalAccessException {
        coffeeMaker.addInventory("2147483647", "2147483632", "2147483632", "2147483632");
        //-2147483633
        Field inventory_field = CoffeeMaker.class.getDeclaredField("inventory");
        inventory_field.setAccessible(true);
        Inventory inventory = (Inventory) inventory_field.get(coffeeMaker);
        // the default about of all resources should be 15
        assertEquals(-2147483634, inventory.getCoffee());
        assertEquals(2147483647, inventory.getMilk());
        assertEquals(2147483647, inventory.getSugar());
        assertEquals(2147483647, inventory.getChocolate());
    }


    /**
     * Given a coffee maker with the default inventory
     * When we add inventory with malformed quantities (i.e., a negative
     * quantity and a non-numeric string)
     * Then we get an inventory exception
     * <p>
     * In this case, we expect the addInventory method to abort after adding coffee, which is correct value,
     * then abort at add milk, which has incorrect value, thus does not adding value to milk, sugar or chocolate,
     * and ignores the incorrect format for adding surger
     *
     * @throws InventoryException if there was an error parsing the quanity
     *                            to a positive integer.
     */

    @Test
    public void testAddInventory_exception1() throws InventoryException, NoSuchFieldException, IllegalAccessException {


        Field inventory_field = CoffeeMaker.class.getDeclaredField("inventory");
        inventory_field.setAccessible(true);
        Inventory inventory = (Inventory) inventory_field.get(coffeeMaker);
        // the default about of all resources should be 15

        try {
            coffeeMaker.addInventory("4", "-1", "asdf", "3");
            Assert.fail("Does not catch incorrect input format for addInventory");
        } catch (InventoryException e) {
            assertEquals("Units of milk must be a positive integer", e.getMessage());
            assertEquals(19, inventory.getCoffee());
            assertEquals(15, inventory.getMilk());
            assertEquals(15, inventory.getSugar());
            assertEquals(15, inventory.getChocolate());
        }

    }

    /**
     * Given a coffee maker with the default inventory
     * When we add inventory with malformed quantities (i.e., a negative
     * quantity and a non-numeric string)
     * Then we get an inventory exception
     * <p>
     * In this case, we expect the addInventory method to abort after adding coffee, which is correct value,
     * then abort at add milk, which has incorrect value, thus does not adding value to milk, sugar or chocolate,
     * and ignores the incorrect format for adding surger
     *
     * @throws InventoryException if there was an error parsing the quanity
     *                            to a positive integer.
     */
    @Test
    public void testAddInventory_exception2() throws InventoryException, NoSuchFieldException, IllegalAccessException {

        Field inventory_field = CoffeeMaker.class.getDeclaredField("inventory");
        inventory_field.setAccessible(true);
        Inventory inventory = (Inventory) inventory_field.get(coffeeMaker);
        // the default about of all resources should be 15

        try {
            coffeeMaker.addInventory("*(", "-1", "15", "3");
            Assert.fail("Does not catch incorrect input format for addInventory");
        } catch (InventoryException e) {
            assertEquals("Units of coffee must be a positive integer", e.getMessage());
            assertEquals(15, inventory.getCoffee());
            assertEquals(15, inventory.getMilk());
            assertEquals(15, inventory.getSugar());
            assertEquals(15, inventory.getChocolate());
        }

    }

    /**
     * Given a coffee maker with one valid recipe
     * When we make coffee, selecting the valid recipe and paying more than
     * the coffee costs
     * Then we get the correct change back.
     */
    @Test
    public void testMakeCoffee() {
        coffeeMaker.addRecipe(recipe1);
        assertEquals(25, coffeeMaker.makeCoffee(0, 75));
    }

}
