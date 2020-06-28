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
