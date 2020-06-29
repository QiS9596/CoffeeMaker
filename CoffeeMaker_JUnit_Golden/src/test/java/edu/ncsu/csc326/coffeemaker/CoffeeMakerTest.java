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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import edu.ncsu.csc326.coffeemaker.exceptions.InventoryException;
import edu.ncsu.csc326.coffeemaker.exceptions.RecipeException;
import edu.ncsu.csc326.coffeemaker.Inventory;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

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
    private Inventory inventory_test_obj;
    private Recipe recipe_test_obj;

    // Sample recipes to use in testing.
    private Recipe recipe1;
    private Recipe recipe2;
    private Recipe recipe3;
    private Recipe recipe4;

    // golden replicas of recipes, since edit recipe would change the object
    // for we cant access to the Recipe.java, we will have to handcraft copy here
    private Recipe recipe1_g;
    private Recipe recipe2_g;
    private Recipe recipe3_g;
    private Recipe recipe4_g;

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

        recipe1_g = new Recipe();
        recipe1_g.setName("Coffee");
        recipe1_g.setAmtChocolate("0");
        recipe1_g.setAmtCoffee("3");
        recipe1_g.setAmtMilk("1");
        recipe1_g.setAmtSugar("1");
        recipe1_g.setPrice("50");


        //Set up for r2
        recipe2 = new Recipe();
        recipe2.setName("Mocha");
        recipe2.setAmtChocolate("20");
        recipe2.setAmtCoffee("3");
        recipe2.setAmtMilk("1");
        recipe2.setAmtSugar("1");
        recipe2.setPrice("75");

        recipe2_g = new Recipe();
        recipe2_g.setName("Mocha");
        recipe2_g.setAmtChocolate("20");
        recipe2_g.setAmtCoffee("3");
        recipe2_g.setAmtMilk("1");
        recipe2_g.setAmtSugar("1");
        recipe2_g.setPrice("75");

        //Set up for r3
        recipe3 = new Recipe();
        recipe3.setName("Latte");
        recipe3.setAmtChocolate("0");
        recipe3.setAmtCoffee("3");
        recipe3.setAmtMilk("3");
        recipe3.setAmtSugar("1");
        recipe3.setPrice("100");

        recipe3_g = new Recipe();
        recipe3_g.setName("Latte");
        recipe3_g.setAmtChocolate("0");
        recipe3_g.setAmtCoffee("3");
        recipe3_g.setAmtMilk("3");
        recipe3_g.setAmtSugar("1");
        recipe3_g.setPrice("100");

        //Set up for r4
        recipe4 = new Recipe();
        recipe4.setName("Hot Chocolate");
        recipe4.setAmtChocolate("4");
        recipe4.setAmtCoffee("0");
        recipe4.setAmtMilk("1");
        recipe4.setAmtSugar("1");
        recipe4.setPrice("65");

        recipe4_g = new Recipe();
        recipe4_g.setName("Hot Chocolate");
        recipe4_g.setAmtChocolate("4");
        recipe4_g.setAmtCoffee("0");
        recipe4_g.setAmtMilk("1");
        recipe4_g.setAmtSugar("1");
        recipe4_g.setPrice("65");

        inventory_test_obj = new Inventory();

        recipe_test_obj = new Recipe();
        recipe_test_obj.setName("Hot Chocolate");
        recipe_test_obj.setAmtChocolate("4");
        recipe_test_obj.setAmtCoffee("0");
        recipe_test_obj.setAmtMilk("1");
        recipe_test_obj.setAmtSugar("1");
        recipe_test_obj.setPrice("65");

    }

    /**
     * golden version of add recipe
     */
    private synchronized boolean add_recipe_helper(CoffeeMaker cm, Recipe r) throws NoSuchFieldException, IllegalAccessException {
        Field recipeBook_field = CoffeeMaker.class.getDeclaredField("recipeBook");
        recipeBook_field.setAccessible(true);
        RecipeBook recipeBook = (RecipeBook) recipeBook_field.get(cm);

        Field recipeArray_field = RecipeBook.class.getDeclaredField("recipeArray");
        recipeArray_field.setAccessible(true);
        Recipe[] recipeArray = (Recipe[]) recipeArray_field.get(recipeBook);

        boolean exists = false;

        for (int i = 0; i < recipeArray.length; i++) {
            if (r.equals(recipeArray[i])) {
                exists = true;
            }
        }

        boolean added = false;
        //Check for first empty spot in array
        if (!exists) {
            for (int i = 0; i < recipeArray.length && !added; i++) {
                if (recipeArray[i] == null) {
                    recipeArray[i] = r;
                    added = true;
                }
            }
        }
        return added;

    }


    private synchronized Recipe[] getRecipes_helper(CoffeeMaker cm) throws NoSuchFieldException, IllegalAccessException {
        Field recipeBook_field = CoffeeMaker.class.getDeclaredField("recipeBook");
        recipeBook_field.setAccessible(true);
        RecipeBook recipeBook = (RecipeBook) recipeBook_field.get(cm);
        return recipeBook.getRecipes();
    }

    /**
     * Compare the ingredient of two recipe, they may have different name, but if all four ingredient attribute matches
     * return true
     */
    private boolean compareRecipeIngredient_helper(Recipe r1, Recipe r2) {
        return (r1.getAmtChocolate() == r2.getAmtChocolate()) &&
                (r1.getAmtCoffee() == r2.getAmtCoffee()) &&
                (r1.getAmtMilk() == r2.getAmtMilk()) &&
                (r1.getAmtSugar() == r2.getAmtSugar());
    }

    /**
     * Generate golden inventory String helper
     */
    private String inventoryToStringGolden_helper(Inventory inventory) {
        StringBuffer buf = new StringBuffer();
        buf.append("Coffee: ");
        buf.append(inventory.getCoffee());
        buf.append("\n");
        buf.append("Milk: ");
        buf.append(inventory.getMilk());
        buf.append("\n");
        buf.append("Sugar: ");
        buf.append(inventory.getSugar());
        buf.append("\n");
        buf.append("Chocolate: ");
        buf.append(inventory.getChocolate());
        buf.append("\n");
        return buf.toString();
    }

    /**
     * Generate golden hashCode for Recipe object
     */
    private int hashCodeGolden_helper(Recipe r) {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((r.getName() == null) ? 0 : r.getName().hashCode());
        return result;
    }
    /*--------------------------------------------------------------------------------------------------------------------*/
    //testing add inventory method

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

    // for coverage
    @Test(expected = InventoryException.class)
    public void testAddInventory_exception3() throws InventoryException {
        coffeeMaker.addInventory("-14", "-1", "asdf", "3");
    }

    @Test(expected = InventoryException.class)
    public void testAddInventory_exception4() throws InventoryException {
        coffeeMaker.addInventory("2", "123kl", "asdf", "3");
    }

    @Test(expected = InventoryException.class)
    public void testAddInventory_exception5() throws InventoryException {
        coffeeMaker.addInventory("2", "4", "asdf", "3");
    }

    @Test(expected = InventoryException.class)
    public void testAddInventory_exception6() throws InventoryException {
        coffeeMaker.addInventory("2", "4", "-33", "3");
    }

    @Test(expected = InventoryException.class)
    public void testAddInventory_exception7() throws InventoryException {
        coffeeMaker.addInventory("2", "4", "2", "-3");
    }

    @Test(expected = InventoryException.class)
    public void testAddInventory_exception8() throws InventoryException {
        coffeeMaker.addInventory("2", "4", "2", "tjoikl");
    }
    // end testing addInventory
    /*--------------------------------------------------------------------------------------------------------------------*/
    // start testing addRecipe

    /**
     * Safely add one receipe
     * check if the remaining recipe slots are set to null
     */
    @Test
    public void testAddRecipe_correct1() throws NoSuchFieldException, IllegalAccessException {
        boolean add = coffeeMaker.addRecipe(recipe1);
        assertTrue(add);
        Recipe[] recipes = getRecipes_helper(coffeeMaker);

        assertEquals(3, recipes.length);
        assertEquals(recipe1, recipes[0]);
        assertNull(recipes[1]);
        assertNull(recipes[2]);
    }

    /**
     * Correctly add multiple recipes
     * check if they are in order
     */
    @Test
    public void testAddReceipe_correct2() throws NoSuchFieldException, IllegalAccessException {
        boolean add4 = coffeeMaker.addRecipe(recipe4);
        boolean add2 = coffeeMaker.addRecipe(recipe2);

        assertTrue(add2);
        assertTrue(add4);

        Recipe[] recipes = getRecipes_helper(coffeeMaker);

        assertEquals(3, recipes.length);
        assertEquals(recipe2, recipes[1]);
        assertEquals(recipe4, recipes[0]);
        assertNull(recipes[2]);
    }

    /**
     * test addReceipe, add more than three recipes
     */
    @Test
    public void testAddReceipe_overflow1() throws NoSuchFieldException, IllegalAccessException {
        boolean add3 = coffeeMaker.addRecipe(recipe3);
        boolean add1 = coffeeMaker.addRecipe(recipe1);
        boolean add2 = coffeeMaker.addRecipe(recipe2);
        boolean add4 = coffeeMaker.addRecipe(recipe4);

        assertTrue(add3);
        assertTrue(add1);
        assertTrue(add2);
        assertFalse(add4);

        Recipe[] recipes = getRecipes_helper(coffeeMaker);

        assertEquals(3, recipes.length);
        assertEquals(recipe3, recipes[0]);
        assertEquals(recipe1, recipes[1]);
        assertEquals(recipe2, recipes[2]);
    }

    /**
     * test addRecipe, add duplicated recipes
     */
    @Test
    public void testAddRecipe_duplicated1() throws NoSuchFieldException, IllegalAccessException {
        boolean add1_1 = coffeeMaker.addRecipe(recipe1);
        boolean add1_2 = coffeeMaker.addRecipe(recipe1);

        assertTrue(add1_1);
        assertFalse(add1_2);

        Recipe[] recipes = getRecipes_helper(coffeeMaker);
        assertEquals(3, recipes.length);
        assertEquals(recipe1, recipes[0]);
        assertNull(recipes[1]);
        assertNull(recipes[2]);
    }
    // end testing addRecipe
    /*--------------------------------------------------------------------------------------------------------------------*/
    // start testing deleteRecipe
    // to make sure that the tests in this section is specialized in testing deleteRecipe, we will use reflection
    // to access internal private variable to add recipes instead of using addRecipe function, so in this case if
    // addRecipe is incorrectly implemented the test would not fail


    /**
     * test deleteRecipe method, for a simplest test case, just add one recipe and delete it.
     */
    @Test
    public void testDeleteRecipe_correct1() throws NoSuchFieldException, IllegalAccessException {
        add_recipe_helper(coffeeMaker, recipe1);

        Recipe[] recipes = getRecipes_helper(coffeeMaker);

        assertEquals(3, recipes.length);
        assertEquals(recipe1, recipes[0]);
        assertNull(recipes[1]);
        assertNull(recipes[2]);

        String deletedRecipeName = coffeeMaker.deleteRecipe(0);
        assertEquals(recipe1.getName(), deletedRecipeName);

        recipes = getRecipes_helper(coffeeMaker);

        assertEquals(3, recipes.length);
        assertNull(recipes[0]);
        assertNull(recipes[1]);
        assertNull(recipes[2]);
    }

    /**
     * add multiple recipes then delete one of them
     */
    @Test
    public void testDeleteRecipe_correct2() throws NoSuchFieldException, IllegalAccessException {
        add_recipe_helper(coffeeMaker, recipe1);
        add_recipe_helper(coffeeMaker, recipe4);

        Recipe[] recipes = getRecipes_helper(coffeeMaker);
        assertEquals(3, recipes.length);
        assertEquals(recipe1, recipes[0]);
        assertEquals(recipe4, recipes[1]);
        assertNull(recipes[2]);

        String deletedRecipeName = coffeeMaker.deleteRecipe(0);
        assertEquals(recipe1.getName(), deletedRecipeName);

        recipes = getRecipes_helper(coffeeMaker);

        assertEquals(3, recipes.length);
        assertNull(recipes[0]);
        assertEquals(recipe4, recipes[1]);
        assertNull(recipes[2]);
    }

    /**
     * delete a recipe multiple times
     */
    @Test
    public void testDeleteRecipe_redelete1() throws NoSuchFieldException, IllegalAccessException {
        add_recipe_helper(coffeeMaker, recipe1);
        add_recipe_helper(coffeeMaker, recipe4);

        Recipe[] recipes = getRecipes_helper(coffeeMaker);
        assertEquals(3, recipes.length);
        assertEquals(recipe1, recipes[0]);
        assertEquals(recipe4, recipes[1]);
        assertNull(recipes[2]);

        String deletedRecipeName = coffeeMaker.deleteRecipe(0);
        assertEquals(recipe1.getName(), deletedRecipeName);

        recipes = getRecipes_helper(coffeeMaker);

        assertEquals(3, recipes.length);
        assertNull(recipes[0]);
        assertEquals(recipe4, recipes[1]);
        assertNull(recipes[2]);

        deletedRecipeName = coffeeMaker.deleteRecipe(0);
        assertNull(deletedRecipeName);

        recipes = getRecipes_helper(coffeeMaker);

        assertEquals(3, recipes.length);
        assertNull(recipes[0]);
        assertEquals(recipe4, recipes[1]);
        assertNull(recipes[2]);
    }

    /**
     * delete a recipe multiple times
     */
    @Test
    public void testDeleteRecipe_deleteNull1() throws NoSuchFieldException, IllegalAccessException {
        add_recipe_helper(coffeeMaker, recipe1);
        add_recipe_helper(coffeeMaker, recipe4);

        Recipe[] recipes = getRecipes_helper(coffeeMaker);
        assertEquals(3, recipes.length);
        assertEquals(recipe1, recipes[0]);
        assertEquals(recipe4, recipes[1]);
        assertNull(recipes[2]);

        String deletedRecipeName = coffeeMaker.deleteRecipe(2);
        assertNull(deletedRecipeName);

        recipes = getRecipes_helper(coffeeMaker);

        assertEquals(3, recipes.length);
        assertEquals(recipe1, recipes[0]);
        assertEquals(recipe4, recipes[1]);
        assertNull(recipes[2]);

    }
    // end testing deleteRecipe
    /*--------------------------------------------------------------------------------------------------------------------*/
    // start testing editRecipe

    /**
     * A simplest test case for adding one recipe then edit it
     */
    @Test
    public void testEditRecipe_correct1() throws NoSuchFieldException, IllegalAccessException {
        add_recipe_helper(coffeeMaker, recipe1);
        Recipe[] recipes = getRecipes_helper(coffeeMaker);
        assertEquals(3, recipes.length);
        assertEquals(recipe1, recipes[0]);
        assertNull(recipes[1]);
        assertNull(recipes[2]);

        String newRecipeName = coffeeMaker.editRecipe(0, recipe2);
        // the method should return the name of the recipe, which should be the same with the old one
        assertEquals(recipe1_g.getName(), newRecipeName);

        recipes = getRecipes_helper(coffeeMaker);

        assertEquals(3, recipes.length);

        assertTrue(compareRecipeIngredient_helper(recipe2_g, recipes[0]));
        assertNull(recipes[1]);
        assertNull(recipes[2]);
    }

    /**
     * edit same recipe multiple time
     */
    @Test
    public void testEditRecipe_correct2() throws NoSuchFieldException, IllegalAccessException {
        add_recipe_helper(coffeeMaker, recipe1);
        Recipe[] recipes = getRecipes_helper(coffeeMaker);
        assertEquals(3, recipes.length);
        assertEquals(recipe1, recipes[0]);
        assertNull(recipes[1]);
        assertNull(recipes[2]);
        //edit first time
        String newRecipeName = coffeeMaker.editRecipe(0, recipe2);

        assertEquals(recipe1_g.getName(), newRecipeName);

        recipes = getRecipes_helper(coffeeMaker);

        assertEquals(3, recipes.length);
        assertTrue(compareRecipeIngredient_helper(recipe2_g, recipes[0]));
        assertNull(recipes[1]);
        assertNull(recipes[2]);
        //edit second time
        newRecipeName = coffeeMaker.editRecipe(0, recipe4);

        assertEquals(recipe1_g.getName(), newRecipeName);

        recipes = getRecipes_helper(coffeeMaker);

        assertEquals(3, recipes.length);
        assertTrue(compareRecipeIngredient_helper(recipe4_g, recipes[0]));
        assertNull(recipes[1]);
        assertNull(recipes[2]);
    }

    /**
     * edit a recipe to it's own replication
     */
    @Test
    public void testEditRecipe_correct3() throws NoSuchFieldException, IllegalAccessException {
        add_recipe_helper(coffeeMaker, recipe4);
        Recipe[] recipes = getRecipes_helper(coffeeMaker);
        assertEquals(3, recipes.length);
        assertEquals(recipe4, recipes[0]);
        assertNull(recipes[1]);
        assertNull(recipes[2]);
        //edit first time
        String newRecipeName = coffeeMaker.editRecipe(0, recipe4);

        assertEquals(recipe4_g.getName(), newRecipeName);

        recipes = getRecipes_helper(coffeeMaker);

        assertEquals(3, recipes.length);
        assertTrue(compareRecipeIngredient_helper(recipe4_g, recipes[0]));
        assertNull(recipes[1]);
        assertNull(recipes[2]);
    }

    /**
     * edit a recipe to null
     */
    @Test(expected = NullPointerException.class)
    public void testEditRecipe_editToNull1() throws NoSuchFieldException, IllegalAccessException {
        add_recipe_helper(coffeeMaker, recipe4);
        Recipe[] recipes = getRecipes_helper(coffeeMaker);
        assertEquals(3, recipes.length);
        assertEquals(recipe4, recipes[0]);
        assertNull(recipes[1]);
        assertNull(recipes[2]);
        String newRecipeName = coffeeMaker.editRecipe(0, null);
    }

    /**
     * edit an uninitialized recipe
     */
    @Test
    public void testEditRecipe_editUnInit1() throws NoSuchFieldException, IllegalAccessException {
        add_recipe_helper(coffeeMaker, recipe4);
        add_recipe_helper(coffeeMaker, recipe1);
        Recipe[] recipes = getRecipes_helper(coffeeMaker);
        assertEquals(3, recipes.length);
        assertEquals(recipe4, recipes[0]);
        assertEquals(recipe1, recipes[1]);
        assertNull(recipes[2]);

        String newRecipeName = coffeeMaker.editRecipe(2, recipe3);

        assertNull(newRecipeName);

        recipes = getRecipes_helper(coffeeMaker);

        assertEquals(3, recipes.length);
        assertEquals(recipe4, recipes[0]);
        assertEquals(recipe1, recipes[1]);
        assertNull(recipes[2]);
    }

    /**
     * edit an out of boundary recipe slot: <0
     */
    @Test
    public void testEditRecipe_editOutOfBoundary1() throws NoSuchFieldException, IllegalAccessException {
        add_recipe_helper(coffeeMaker, recipe4);
        add_recipe_helper(coffeeMaker, recipe1);
        Recipe[] recipes = getRecipes_helper(coffeeMaker);
        assertEquals(3, recipes.length);
        assertEquals(recipe4, recipes[0]);
        assertEquals(recipe1, recipes[1]);
        assertNull(recipes[2]);

        String newRecipeName = coffeeMaker.editRecipe(-1, recipe3);

        assertNull(newRecipeName);

        recipes = getRecipes_helper(coffeeMaker);

        assertEquals(3, recipes.length);
        assertEquals(recipe4, recipes[0]);
        assertEquals(recipe1, recipes[1]);
        assertNull(recipes[2]);
    }

    /**
     * edit an out of boundary recipe slot: greater than array length
     */
    @Test
    public void testEditRecipe_editOutOfBoundary2() throws NoSuchFieldException, IllegalAccessException {
        add_recipe_helper(coffeeMaker, recipe4);
        add_recipe_helper(coffeeMaker, recipe1);
        Recipe[] recipes = getRecipes_helper(coffeeMaker);
        assertEquals(3, recipes.length);
        assertEquals(recipe4, recipes[0]);
        assertEquals(recipe1, recipes[1]);
        assertNull(recipes[2]);

        String newRecipeName = coffeeMaker.editRecipe(3, recipe3);

        assertNull(newRecipeName);

        recipes = getRecipes_helper(coffeeMaker);

        assertEquals(3, recipes.length);
        assertEquals(recipe4, recipes[0]);
        assertEquals(recipe1, recipes[1]);
        assertNull(recipes[2]);
    }
    // end testing editRecipe
    /*--------------------------------------------------------------------------------------------------------------------*/
    //Start testing checkInventory

    /**
     * Simplest test: get default inventory report
     */
    @Test
    public void testCheckInventory_null1() throws NoSuchFieldException, IllegalAccessException {
        Field inventory_field = CoffeeMaker.class.getDeclaredField("inventory");

        inventory_field.setAccessible(true);
        Inventory inventory = (Inventory) inventory_field.get(coffeeMaker);
        // check init
        assertEquals(15, inventory.getCoffee());
        assertEquals(15, inventory.getMilk());
        assertEquals(15, inventory.getSugar());
        assertEquals(15, inventory.getChocolate());
        String str = coffeeMaker.checkInventory();
        assertEquals(inventoryToStringGolden_helper(inventory), str);
    }

    // end testing checkInventory
    /*--------------------------------------------------------------------------------------------------------------------*/
    // start testing makeCoffee

    /**
     * Given a coffee maker with one valid recipe
     * When we make coffee, selecting the valid recipe and paying more than
     * the coffee costs
     * Then we get the correct change back.
     */
    @Test
    public void testMakeCoffee_correct1() throws NoSuchFieldException, IllegalAccessException {
        coffeeMaker.addRecipe(recipe1);
        assertEquals(25, coffeeMaker.makeCoffee(0, 75));

        // check inventory was correctly used
        Field inventory_field = CoffeeMaker.class.getDeclaredField("inventory");
        inventory_field.setAccessible(true);
        Inventory inventory = (Inventory) inventory_field.get(coffeeMaker);
        assertEquals(15, inventory.getChocolate());
        assertEquals(12, inventory.getCoffee());
        assertEquals(14, inventory.getMilk());
        assertEquals(14, inventory.getSugar());
    }

    @Test
    public void testMakeCoffee_correct2() throws InventoryException, NoSuchFieldException, IllegalAccessException {
        coffeeMaker.addRecipe(recipe1);
        coffeeMaker.addRecipe(recipe2);
        coffeeMaker.addInventory("0", "0", "0", "5");
        assertEquals(0, coffeeMaker.makeCoffee(1, 75));

        Field inventory_field = CoffeeMaker.class.getDeclaredField("inventory");
        inventory_field.setAccessible(true);
        Inventory inventory = (Inventory) inventory_field.get(coffeeMaker);
        assertEquals(0, inventory.getChocolate());
        assertEquals(12, inventory.getCoffee());
        assertEquals(14, inventory.getMilk());
        assertEquals(14, inventory.getSugar());
    }


    @Test
    public void testMakeCoffee_correct3() throws InventoryException, NoSuchFieldException, IllegalAccessException {
        coffeeMaker.addRecipe(recipe1);
        coffeeMaker.addRecipe(recipe2);
        coffeeMaker.addInventory("0", "0", "0", "5");
        assertEquals(0, coffeeMaker.makeCoffee(1, 75));
        assertEquals(25, coffeeMaker.makeCoffee(0, 75));
        Field inventory_field = CoffeeMaker.class.getDeclaredField("inventory");
        inventory_field.setAccessible(true);
        Inventory inventory = (Inventory) inventory_field.get(coffeeMaker);
        assertEquals(0, inventory.getChocolate());
        assertEquals(9, inventory.getCoffee());
        assertEquals(13, inventory.getMilk());
        assertEquals(13, inventory.getSugar());
    }

    /**
     * Try to access uninitialized recipe
     */
    @Test
    public void testMakeCoffee_accessNull1() throws NoSuchFieldException, IllegalAccessException {
        coffeeMaker.addRecipe(recipe4);
        assertEquals(200, coffeeMaker.makeCoffee(1, 200));

        Field inventory_field = CoffeeMaker.class.getDeclaredField("inventory");
        inventory_field.setAccessible(true);
        Inventory inventory = (Inventory) inventory_field.get(coffeeMaker);
        assertEquals(15, inventory.getChocolate());
        assertEquals(15, inventory.getCoffee());
        assertEquals(15, inventory.getMilk());
        assertEquals(15, inventory.getSugar());
    }

    /**
     * Try to access outof index order
     */
    @Test
    public void testMakeCoffee_accessNull2() throws NoSuchFieldException, IllegalAccessException {
        coffeeMaker.addRecipe(recipe4);
        assertEquals(200, coffeeMaker.makeCoffee(3, 200));
        Field inventory_field = CoffeeMaker.class.getDeclaredField("inventory");
        inventory_field.setAccessible(true);
        Inventory inventory = (Inventory) inventory_field.get(coffeeMaker);
        assertEquals(15, inventory.getChocolate());
        assertEquals(15, inventory.getCoffee());
        assertEquals(15, inventory.getMilk());
        assertEquals(15, inventory.getSugar());
    }

    @Test
    public void testMakeCoffee_accessNull3() throws NoSuchFieldException, IllegalAccessException {
        coffeeMaker.addRecipe(recipe4);
        assertEquals(200, coffeeMaker.makeCoffee(-1, 200));
        Field inventory_field = CoffeeMaker.class.getDeclaredField("inventory");
        inventory_field.setAccessible(true);
        Inventory inventory = (Inventory) inventory_field.get(coffeeMaker);
        assertEquals(15, inventory.getChocolate());
        assertEquals(15, inventory.getCoffee());
        assertEquals(15, inventory.getMilk());
        assertEquals(15, inventory.getSugar());
    }

    /**
     * try to access with insufficient found
     */
    @Test
    public void testMakeCoffee_accessNull4() throws NoSuchFieldException, IllegalAccessException {
        coffeeMaker.addRecipe(recipe4);
        assertEquals(64, coffeeMaker.makeCoffee(0, 64));
        Field inventory_field = CoffeeMaker.class.getDeclaredField("inventory");
        inventory_field.setAccessible(true);
        Inventory inventory = (Inventory) inventory_field.get(coffeeMaker);
        assertEquals(15, inventory.getChocolate());
        assertEquals(15, inventory.getCoffee());
        assertEquals(15, inventory.getMilk());
        assertEquals(15, inventory.getSugar());
    }

    /**
     * Try to access to insufficient supply
     */
    @Test
    public void testMakeCoffee_accessNull5() throws NoSuchFieldException, IllegalAccessException {
        coffeeMaker.addRecipe(recipe1);
        coffeeMaker.addRecipe(recipe2);
        assertEquals(75, coffeeMaker.makeCoffee(1, 75));
        Field inventory_field = CoffeeMaker.class.getDeclaredField("inventory");
        inventory_field.setAccessible(true);
        Inventory inventory = (Inventory) inventory_field.get(coffeeMaker);
        assertEquals(15, inventory.getChocolate());
        assertEquals(15, inventory.getCoffee());
        assertEquals(15, inventory.getMilk());
        assertEquals(15, inventory.getSugar());
    }

    @Test
    public void testMakeCoffee_accessNull6() throws NoSuchFieldException, IllegalAccessException {
        Field inventory_field = CoffeeMaker.class.getDeclaredField("inventory");
        inventory_field.setAccessible(true);
        Inventory inventory = (Inventory) inventory_field.get(coffeeMaker);
        inventory.setCoffee(5);


        coffeeMaker.addRecipe(recipe1);
        coffeeMaker.addRecipe(recipe2);
        assertEquals(25, coffeeMaker.makeCoffee(0, 75));
        assertEquals(75, coffeeMaker.makeCoffee(1, 75));

        assertEquals(15, inventory.getChocolate());
        assertEquals(2, inventory.getCoffee());
        assertEquals(14, inventory.getMilk());
        assertEquals(14, inventory.getSugar());
    }

    @Test
    public void testMakeCoffee_accessNull7() throws NoSuchFieldException, IllegalAccessException {
        Field inventory_field = CoffeeMaker.class.getDeclaredField("inventory");
        inventory_field.setAccessible(true);
        Inventory inventory = (Inventory) inventory_field.get(coffeeMaker);
        inventory.setMilk(1);


        coffeeMaker.addRecipe(recipe1);
        coffeeMaker.addRecipe(recipe2);
        assertEquals(25, coffeeMaker.makeCoffee(0, 75));
        assertEquals(75, coffeeMaker.makeCoffee(1, 75));

        assertEquals(15, inventory.getChocolate());
        assertEquals(12, inventory.getCoffee());
        assertEquals(0, inventory.getMilk());
        assertEquals(14, inventory.getSugar());
    }


    @Test
    public void testMakeCoffee_accessNull8() throws NoSuchFieldException, IllegalAccessException {
        Field inventory_field = CoffeeMaker.class.getDeclaredField("inventory");
        inventory_field.setAccessible(true);
        Inventory inventory = (Inventory) inventory_field.get(coffeeMaker);
        inventory.setSugar(1);


        coffeeMaker.addRecipe(recipe1);
        coffeeMaker.addRecipe(recipe2);
        assertEquals(25, coffeeMaker.makeCoffee(0, 75));
        assertEquals(75, coffeeMaker.makeCoffee(1, 75));

        assertEquals(15, inventory.getChocolate());
        assertEquals(12, inventory.getCoffee());
        assertEquals(14, inventory.getMilk());
        assertEquals(0, inventory.getSugar());
    }

    //end makeCoffee test
    /*---------------------------------------------------------------------------------------------------------------------*/
    // end of testing coffeeMaker
    /*======================================================================================================================*/
    //To merge everything into one file for easier uploading, we will have friend class test here instead of an independent
    //file for each class
    // as during the CoffeeMaker test we have cover lots of the function of it's attribute classes, we will be focusing
    // on untested methods and codes under the guidance of Jacoco
    /*-----------------------------------------------------------------------------------------------------------------------*/
    //Test Inventory
    //Test set methods
    // setChocolate valid
    @Test
    public void testSetChocolate_valid1() {
        assertEquals(15, inventory_test_obj.getChocolate());
        inventory_test_obj.setChocolate(5);
        assertEquals(5, inventory_test_obj.getChocolate());
    }

    @Test
    public void testSetChocolate_valid2() {
        assertEquals(15, inventory_test_obj.getChocolate());
        inventory_test_obj.setChocolate(0);
        assertEquals(0, inventory_test_obj.getChocolate());
    }

    // test setChocolate with invalid value
    @Test
    public void testSetChocolate_invalid1() {
        assertEquals(15, inventory_test_obj.getChocolate());
        inventory_test_obj.setChocolate(-1);
        assertEquals(15, inventory_test_obj.getChocolate());
    }


    // setCoffee valid
    @Test
    public void testSetCoffee_valid1() {
        assertEquals(15, inventory_test_obj.getCoffee());
        inventory_test_obj.setCoffee(5);
        assertEquals(5, inventory_test_obj.getCoffee());
    }

    @Test
    public void testSetCoffee_valid2() {
        assertEquals(15, inventory_test_obj.getCoffee());
        inventory_test_obj.setCoffee(0);
        assertEquals(0, inventory_test_obj.getCoffee());
    }

    // test setCoffee with invalid value
    @Test
    public void testSetCoffee_invalid1() {
        assertEquals(15, inventory_test_obj.getCoffee());
        inventory_test_obj.setCoffee(-1);
        assertEquals(15, inventory_test_obj.getCoffee());
    }

    //setMilk valid
    @Test
    public void testSetMilk_valid1() {
        assertEquals(15, inventory_test_obj.getMilk());
        inventory_test_obj.setMilk(5);
        assertEquals(5, inventory_test_obj.getMilk());
    }

    @Test
    public void testSetMilk_valid2() {
        assertEquals(15, inventory_test_obj.getMilk());
        inventory_test_obj.setMilk(0);
        assertEquals(0, inventory_test_obj.getMilk());
    }

    //setMilk Invalid
    @Test
    public void testSetMilk_invalid1() {
        assertEquals(15, inventory_test_obj.getMilk());
        inventory_test_obj.setMilk(-1);
        assertEquals(15, inventory_test_obj.getMilk());
    }

    //setSugar valid
    @Test
    public void testSetSugar_valid1() {
        assertEquals(15, inventory_test_obj.getSugar());
        inventory_test_obj.setSugar(5);
        assertEquals(5, inventory_test_obj.getSugar());
    }

    @Test
    public void testSetSugar_valid2() {
        assertEquals(15, inventory_test_obj.getSugar());
        inventory_test_obj.setSugar(0);
        assertEquals(0, inventory_test_obj.getSugar());
    }

    @Test
    public void testSetSugar_invalid1() {
        assertEquals(15, inventory_test_obj.getSugar());
        inventory_test_obj.setSugar(-1);
        assertEquals(15, inventory_test_obj.getSugar());
    }

    // end testing set method for Inventory
    /*--------------------------------------------------------------------------------------------------------------------*/
    //start testing Recipe class
    //hashCode with valid name value
    @Test
    public void testHashCode_valid1() {
        assertEquals(hashCodeGolden_helper(recipe_test_obj), recipe_test_obj.hashCode());
    }

    //hashCode with null name value
    @Test
    public void testHashCode_invalid1() throws NoSuchFieldException, IllegalAccessException {
        Field name_filed = Recipe.class.getDeclaredField("name");
        name_filed.setAccessible(true);
        name_filed.set(recipe_test_obj, null);
        assertNull(recipe_test_obj.getName());
        assertEquals(hashCodeGolden_helper(recipe_test_obj), recipe_test_obj.hashCode());
    }

    // end testing hashCode method
    // start testing setters
    //valid setAmtChocolate
    @Test
    public void testSetAmtChocolate_valid1() throws RecipeException, NoSuchFieldException, IllegalAccessException {
        Field chocolateAmt_field = Recipe.class.getDeclaredField("amtChocolate");
        chocolateAmt_field.setAccessible(true);
        int chocolateAmt = chocolateAmt_field.getInt(recipe_test_obj);
        assertEquals(4, chocolateAmt);

        recipe_test_obj.setAmtChocolate("2");
        chocolateAmt = chocolateAmt_field.getInt(recipe_test_obj);
        assertEquals(2, chocolateAmt);
    }

    //invalid setAmtChocolate
    @Test
    public void testSetAmtChocolate_invalid1() throws RecipeException, NoSuchFieldException, IllegalAccessException {
        Field chocolateAmt_field = Recipe.class.getDeclaredField("amtChocolate");
        chocolateAmt_field.setAccessible(true);
        int chocolateAmt = chocolateAmt_field.getInt(recipe_test_obj);
        assertEquals(4, chocolateAmt);
        try {
            recipe_test_obj.setAmtChocolate("rwji123");
        } catch (RecipeException e) {
            assertEquals("Units of chocolate must be a positive integer", e.getMessage());
            chocolateAmt = chocolateAmt_field.getInt(recipe_test_obj);
            assertEquals(4, chocolateAmt);
        }
    }

    @Test
    public void testSetAmtChocolate_invalid2() throws NoSuchFieldException, IllegalAccessException{
        Field chocolateAmt_field = Recipe.class.getDeclaredField("amtChocolate");
        chocolateAmt_field.setAccessible(true);
        int chocolateAmt = chocolateAmt_field.getInt(recipe_test_obj);
        assertEquals(4, chocolateAmt);
        try {
            recipe_test_obj.setAmtChocolate("-3");
        } catch (RecipeException e) {
            assertEquals("Units of chocolate must be a positive integer", e.getMessage());
            chocolateAmt = chocolateAmt_field.getInt(recipe_test_obj);
            assertEquals(4, chocolateAmt);
        }
    }

    //coffee setter
    //valid setAmtCoffee
    @Test
    public void testSetAmtCoffee_valid1() throws RecipeException, NoSuchFieldException, IllegalAccessException {
        Field coffeeAmt_field = Recipe.class.getDeclaredField("amtCoffee");
        coffeeAmt_field.setAccessible(true);
        int coffeeAmt = coffeeAmt_field.getInt(recipe_test_obj);
        assertEquals(0, coffeeAmt);

        recipe_test_obj.setAmtCoffee("3");
        coffeeAmt = coffeeAmt_field.getInt(recipe_test_obj);
        assertEquals(3, coffeeAmt);
    }

    //invalid setAmtCoffee
    @Test
    public void testSetAmtCoffee_invalid1() throws RecipeException, NoSuchFieldException, IllegalAccessException {
        Field coffeeAmt_field = Recipe.class.getDeclaredField("amtCoffee");
        coffeeAmt_field.setAccessible(true);
        int coffeeAmt = coffeeAmt_field.getInt(recipe_test_obj);
        assertEquals(0, coffeeAmt);
        try {
            recipe_test_obj.setAmtCoffee("rwji123");
        } catch (RecipeException e) {
            assertEquals("Units of coffee must be a positive integer", e.getMessage());
            coffeeAmt = coffeeAmt_field.getInt(recipe_test_obj);
            assertEquals(0, coffeeAmt);
        }
    }

    @Test
    public void testSetAmtCoffee_invalid2() throws RecipeException, NoSuchFieldException, IllegalAccessException {
        Field coffeeAmt_field = Recipe.class.getDeclaredField("amtCoffee");
        coffeeAmt_field.setAccessible(true);
        int coffeeAmt = coffeeAmt_field.getInt(recipe_test_obj);
        assertEquals(0, coffeeAmt);
        try {
            recipe_test_obj.setAmtCoffee("-1");
        } catch (RecipeException e) {
            assertEquals("Units of coffee must be a positive integer", e.getMessage());
            coffeeAmt = coffeeAmt_field.getInt(recipe_test_obj);
            assertEquals(0, coffeeAmt);
        }
    }

    // milk setters
    @Test
    public void testSetAmtMilk_valid1() throws RecipeException, NoSuchFieldException, IllegalAccessException {
        Field amtMilk_field = Recipe.class.getDeclaredField("amtMilk");
        amtMilk_field.setAccessible(true);
        int milkAmt = amtMilk_field.getInt(recipe_test_obj);
        assertEquals(1, milkAmt);

        recipe_test_obj.setAmtMilk("3");
        milkAmt = amtMilk_field.getInt(recipe_test_obj);
        assertEquals(3, milkAmt);
    }
    @Test
    public void testSetAmtMilk_invalid1() throws NoSuchFieldException, IllegalAccessException {
        Field amtMilk_field = Recipe.class.getDeclaredField("amtMilk");
        amtMilk_field.setAccessible(true);
        int milkAmt = amtMilk_field.getInt(recipe_test_obj);
        assertEquals(1, milkAmt);
        try {
            recipe_test_obj.setAmtMilk("rwji123");
        } catch (RecipeException e) {
            assertEquals("Units of milk must be a positive integer", e.getMessage());
            milkAmt = amtMilk_field.getInt(recipe_test_obj);
            assertEquals(1, milkAmt);
        }
    }
    @Test
    public void testSetAmtMilk_invalid2() throws NoSuchFieldException, IllegalAccessException {
        Field amtMilk_field = Recipe.class.getDeclaredField("amtMilk");
        amtMilk_field.setAccessible(true);
        int milkAmt = amtMilk_field.getInt(recipe_test_obj);
        assertEquals(1, milkAmt);
        try {
            recipe_test_obj.setAmtMilk("-3");
        } catch (RecipeException e) {
            assertEquals("Units of milk must be a positive integer", e.getMessage());
            milkAmt = amtMilk_field.getInt(recipe_test_obj);
            assertEquals(1, milkAmt);
        }
    }

    //Suger setters
    @Test
    public void testSetAmtSugar_valid1() throws RecipeException, NoSuchFieldException, IllegalAccessException {
        Field amtSugar_field = Recipe.class.getDeclaredField("amtSugar");
        amtSugar_field.setAccessible(true);
        int sugarAmt = amtSugar_field.getInt(recipe_test_obj);
        assertEquals(1, sugarAmt);

        recipe_test_obj.setAmtSugar("3");
        sugarAmt = amtSugar_field.getInt(recipe_test_obj);
        assertEquals(3, sugarAmt);
    }
    @Test
    public void testSetAmtSugar_invalid1() throws NoSuchFieldException, IllegalAccessException {
        Field amtSugar_field = Recipe.class.getDeclaredField("amtSugar");
        amtSugar_field.setAccessible(true);
        int sugarAmt = amtSugar_field.getInt(recipe_test_obj);
        assertEquals(1, sugarAmt);
        try {
            recipe_test_obj.setAmtSugar("rwji123");
        } catch (RecipeException e) {
            assertEquals("Units of sugar must be a positive integer", e.getMessage());
            sugarAmt = amtSugar_field.getInt(recipe_test_obj);
            assertEquals(1, sugarAmt);
        }
    }
    @Test
    public void testSetAmtSugar_invalid2() throws NoSuchFieldException, IllegalAccessException {
        Field amtSugar_field = Recipe.class.getDeclaredField("amtSugar");
        amtSugar_field.setAccessible(true);
        int sugarAmt = amtSugar_field.getInt(recipe_test_obj);
        assertEquals(1, sugarAmt);
        try {
            recipe_test_obj.setAmtSugar("-3");
        } catch (RecipeException e) {
            assertEquals("Units of sugar must be a positive integer", e.getMessage());
            sugarAmt = amtSugar_field.getInt(recipe_test_obj);
            assertEquals(1, sugarAmt);
        }
    }
    //name setters
    @Test
    public void testSetName_valid1() throws NoSuchFieldException, IllegalAccessException {
        Field name_field = Recipe.class.getDeclaredField("name");
        name_field.setAccessible(true);
        String name = (String)name_field.get(recipe_test_obj);
        assertEquals("Hot Chocolate", name);

        recipe_test_obj.setName("aWhole NE-W 名字");
        name = (String) name_field.get(recipe_test_obj);
        assertEquals("aWhole NE-W 名字", name);
    }
    @Test
    public void testSetName_valid2() throws NoSuchFieldException, IllegalAccessException {
        Field name_field = Recipe.class.getDeclaredField("name");
        name_field.setAccessible(true);
        String name = (String)name_field.get(recipe_test_obj);
        assertEquals("Hot Chocolate", name);

        recipe_test_obj.setName("");
        name = (String) name_field.get(recipe_test_obj);
        assertEquals("", name);
    }
    @Test
    public void testSetName_invalid1() throws NoSuchFieldException, IllegalAccessException {
        Field name_field = Recipe.class.getDeclaredField("name");
        name_field.setAccessible(true);
        String name = (String)name_field.get(recipe_test_obj);
        assertEquals("Hot Chocolate", name);

        recipe_test_obj.setName(null);
        name = (String) name_field.get(recipe_test_obj);
        assertEquals("Hot Chocolate", name);
    }

    //Price setters
    @Test
    public void testSetPrice_valid1() throws RecipeException, NoSuchFieldException, IllegalAccessException {
        Field price_field = Recipe.class.getDeclaredField("price");
        price_field.setAccessible(true);
        int price = price_field.getInt(recipe_test_obj);
        assertEquals(65, price);

        recipe_test_obj.setPrice("45");
        price = price_field.getInt(recipe_test_obj);
        assertEquals(45, price);
    }

    @Test
    public void testSetPrice_invalid1() throws NoSuchFieldException, IllegalAccessException {
        Field price_field = Recipe.class.getDeclaredField("price");
        price_field.setAccessible(true);
        int price = price_field.getInt(recipe_test_obj);
        assertEquals(65, price);
        try {
            recipe_test_obj.setPrice("11zzk");
        } catch (RecipeException e) {
            assertEquals("Price must be a positive integer", e.getMessage());
            price = price_field.getInt(recipe_test_obj);
            assertEquals(65, price);
        }
    }

    @Test
    public void testSetPrice_invalid2() throws NoSuchFieldException, IllegalAccessException {
        Field price_field = Recipe.class.getDeclaredField("price");
        price_field.setAccessible(true);
        int price = price_field.getInt(recipe_test_obj);
        assertEquals(65, price);
        try {
            recipe_test_obj.setPrice("-3");
        } catch (RecipeException e) {
            assertEquals("Price must be a positive integer", e.getMessage());
            price = price_field.getInt(recipe_test_obj);
            assertEquals(65, price);
        }
    }
    // complete test setters
    //start test getters
    @Test
    public void testGetAmtChocolate() throws NoSuchFieldException, IllegalAccessException {
        Field chocolateAmt_field = Recipe.class.getDeclaredField("amtChocolate");
        chocolateAmt_field.setAccessible(true);
        int chocolateAmt = chocolateAmt_field.getInt(recipe_test_obj);

        assertEquals(chocolateAmt, recipe_test_obj.getAmtChocolate());
    }
    @Test
    public void testGetAmtCoffee() throws NoSuchFieldException, IllegalAccessException {
        Field coffeeAmt_field = Recipe.class.getDeclaredField("amtCoffee");
        coffeeAmt_field.setAccessible(true);
        int coffeeAmt = coffeeAmt_field.getInt(recipe_test_obj);

        assertEquals(coffeeAmt, recipe_test_obj.getAmtCoffee());
    }
    @Test
    public void testGetAmtMilk() throws NoSuchFieldException, IllegalAccessException {
        Field amtMilk_field = Recipe.class.getDeclaredField("amtMilk");
        amtMilk_field.setAccessible(true);
        int milkAmt = amtMilk_field.getInt(recipe_test_obj);

        assertEquals(milkAmt, recipe_test_obj.getAmtMilk());
    }
    @Test
    public void testGetAmtSugar() throws NoSuchFieldException, IllegalAccessException {
        Field amtSugar_field = Recipe.class.getDeclaredField("amtSugar");
        amtSugar_field.setAccessible(true);
        int sugarAmt = amtSugar_field.getInt(recipe_test_obj);

        assertEquals(sugarAmt, recipe_test_obj.getAmtSugar());
    }

    @Test
    public void testGetName() throws NoSuchFieldException, IllegalAccessException {
        Field name_field = Recipe.class.getDeclaredField("name");
        name_field.setAccessible(true);
        String name = (String)name_field.get(recipe_test_obj);

        assertEquals(name, recipe_test_obj.getName());
    }

    @Test
    public void testGetPrice() throws NoSuchFieldException, IllegalAccessException {
        Field price_field = Recipe.class.getDeclaredField("price");
        price_field.setAccessible(true);
        int price = price_field.getInt(recipe_test_obj);
        assertEquals(price, recipe_test_obj.getPrice());
    }
}
