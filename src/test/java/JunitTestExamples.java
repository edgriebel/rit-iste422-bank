import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Every;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Collection;
import static org.hamcrest.CoreMatchers.*;

public class JunitTestExamples {

    /**
     * Example showing combining multiple conditions in a single test with anyOf, allOf
     */
    @Test
    public void testAllOfAnyOf() throws Exception {
        List<Integer> intList = List.of(1, 2, 3, 4, 5);
        List<String> stringList = Arrays.asList("ABCDEF".split(""));
        List<Integer> testValues = List.of(1, 2, 3, 4, 5);

        // Note that even though 3 is a scalar int and the list is of Integers, this still works
        // because [int 3] is auto-boxed to [Integer 3]:
        // https://www.geeksforgeeks.org/autoboxing-unboxing-java/
        assertThat("List has 3", intList, hasItem(3));
        assertThat("List has 5", intList, hasItem(5));
        // Three ways to consolidate this to one line:
        assertThat("List has 3 and 5", intList, hasItems(3, 5));
        assertThat("List has 3 and 5", intList, allOf(hasItem(3), hasItem(5)));
        assertThat("List has 3 and 5", intList, both(hasItem(3)).and(hasItem(5)));

        // We can also test for one of a series of items in a set. Here are two ways:
        assertThat("List has 3 or 99", intList, either(hasItem(3)).or(hasItem(99)));
        assertThat("List has 3 or 99", intList, anyOf(hasItem(3), hasItem(99)));

        // We can test using multiple and's and or's, although this can get convoluted
        assertThat(intList, either(both(hasItem(3)).and(hasItem(4))).or(hasItem(1)).or(hasItem(99)));

        //
        // Some examples with Strings
        //
        assertThat(stringList, hasItem("A"));
        assertThat(stringList, anyOf(hasItem("A"), hasItem("B"), hasItem("C")));
        assertThat(stringList, allOf(hasItem("A"), hasItem("B"), hasItem("C")));
        // we can have different types of tests in allOf()
        assertThat(stringList, allOf(hasItem("A"), hasItem("C")));

    }

    /**
     * Example of verifying elements in a collection
     */
    @Test
    public void testHasItem() throws Exception {
        List<String> helloList = List.of("Hello", "world", "goodbye");
        List<String> robotList = List.of("big hello robot", "big world robot");

        // Simple check for an item anywhere in a collection
        assertThat(helloList, hasItem("Hello"));
        assertThat(helloList, hasItem("goodbye"));

        // these 2 are equivalent
        assertThat(helloList, allOf(hasItem("Hello"), hasItem("world")));
        assertThat(helloList, hasItems("Hello", "world"));

        // Checking for something in every element
        assertThat(robotList, everyItem(startsWith("big")));
        assertThat(robotList, everyItem(endsWith("robot")));
        assertThat(robotList, everyItem(containsString(" ")));

        // Do a set of test for each item
        assertThat(robotList, everyItem(allOf(startsWith("big"), endsWith("robot"))));

    }

    @Test
    public void otherExamples() throws Exception {
        // floating point numbers don't have an exact representation so won't always compare correctly
        // We need to test floating points with an epsilon/delta

        // This test passes as expected
        assertThat("This test should pass", 6.0f, is(6.0f));
        // But, this test fails because the representation of 6.1 is different between float and double
        // uncomment to see this fail!
        // assertThat("This test should also pass because 6.1001==6.1001", 6.1 + 0.0001, is(6.1001));

        // Note do not use in() when using tests in org.junit.Assert.*
        assertEquals("this version passes with a delta", 6.1 + 0.0001, 6.1001, 0.0000001);

        // negating a test with not()
        assertThat(33, not(is(99)));
        assertThat(List.of(1, 2, 3), not(hasItem(99)));
    }

    // we expect this to fail because there's no data in the list,
    // so let JUnit look for that instead of wrapping it with a try/catch block
    // like in the next method
    @Test(expected=ArrayIndexOutOfBoundsException.class)
    public void testNoDataRaisesException() throws Exception {
        List<String> myData = List.of();
        System.out.println(myData.get(0));
    }
    @Test
    public void testNoDataRaisesException_undesirable() throws Exception {
        List<String> myData = List.of();
        try {
            System.out.println(myData.get(0));
            fail("No data should have thrown ArrayIndexOutOfBoundsException");
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            // note that an empty catch block is an anti-pattern in Java as well
        }
    }


    /*****************************************************
     * These 2 tests illustrate why you should use a more specific comparison if available.
     * See how assertTrue gives a very generic reason, only that it failed.
     * Compare with the assertEquals output, it displays both operands in the error message.
     * Comment out the two @Ignore statements to see this in action!
     *****************************************************/
    @Ignore("Comment these Ignore annotations to see the difference in error messages")
    @Test
    public void dontUseAssertTrueExceptBooleans_moreInfo() throws Exception {
        int badVal = 4;
        assertEquals("No need to look at the code to see why it failed!", badVal, 33);
    }

    @Ignore("Comment these Ignore annotations to see the difference in error messages")
    @Test
    public void dontUseAssertTrueExceptBooleans_lessInfo() throws Exception {
        int badVal = 4;
        assertTrue("You have to look at the code to see what comparison failed", badVal == 33);
    }
}
