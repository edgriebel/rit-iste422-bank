import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/* TODO: Add these lines to build.gradle to add the runSavingsFixture target:
task runSavingsFixture(type: JavaExec) {
    group = "Execution"
    description = "Run SavingsAccountTestFixture class"
    classpath = sourceSets.test.runtimeClasspath
    mainClass = "SavingsAccountTestFixture"
}
 */

public class SavingsAccountTestFixture {
    public static Logger logger = LogManager.getLogger(SavingsAccountTestFixture.class);
    // Note that we could also load the file from the classpath instead of hardcoding the pathname
    static final String TEST_FILE = "src/test/resources/SavingsAccountTest.csv".replace('/', File.separatorChar);

    record TestScenario(double initBalance,
                        double interestRate,
                        List<Double> withdrawals,
                        List<Double> deposits,
                        int runMonthEndNTimes,
                        double endBalance
    ) { }

    private static List<TestScenario> testScenarios;

    @Test
    public void runTestScenarios() throws Exception {
        if (testScenarios == null) {
            System.err.println("\n\n");
            System.err.println("************************************");
            System.err.println("************************************");
            System.err.println();
            System.err.println("Note: NOT running any Test Scenarios");
            System.err.println("Run main() method to run scenarios!!");
            System.err.println();
            System.err.println("************************************");
            System.err.println("************************************");
            System.err.println("\n\n");
            return;
        }

        // iterate over all test scenarios
        for (int testNum = 0; testNum < testScenarios.size(); testNum++) {
            TestScenario scenario = testScenarios.get(testNum);
            logger.info("**** Running test for {}", scenario);

            // set up account with specified starting balance and interest rate
            // TODO: Add code to create account....

            // now process withdrawals, deposits
            // TODO: Add code to process withdrawals....

            // TODO: Add code to process deposits

            // run month-end if desired and output register
            if (scenario.runMonthEndNTimes > 0) {
                // TODO: Add code to run month-end....
            }

            // make sure the balance is correct
            // TODO: add code to verify balance

        }
    }

    private static void runJunitTests() {
        JUnitCore jc = new JUnitCore();
        jc.addListener(new TextListener(System.out));
        Result r = jc.run(SavingsAccountTestFixture.class);
        System.out.printf("Tests run: %d Passed: %d Failed: %d\n",
                r.getRunCount(), r.getRunCount() - r.getFailureCount(), r.getFailureCount());
        System.out.println("Failures:");
        for (Failure f : r.getFailures()) {
            System.out.println("\t"+f);
        }
    }

    // NOTE: this could be added to TestScenario class
    private static List<Double> parseListOfAmounts(String amounts) {
        if (amounts.trim().isEmpty()) {
            return List.of();
        }
        List<Double> ret = new ArrayList<>();
        logger.debug("Amounts to split: {}", amounts);
        for (String amtStr : amounts.trim().split("\\|")) {
            logger.debug("An Amount: {}", amtStr);
            ret.add(Double.parseDouble(amtStr));
        }
        return ret;
    }

    // NOTE: this could be added to TestScenario class
    private static TestScenario parseScenarioString(String scenarioAsString) {
        String [] scenarioValues = scenarioAsString.split(",");
        // should probably validate length here
        double initialBalance = Double.parseDouble(scenarioValues[0]);
        // TODO: parse the rest of your fields
        List<Double> wds = parseListOfAmounts(scenarioValues[2]);
        // TODO: Replace these dummy values with _your_ field values to populate TestScenario object
        TestScenario scenario = new TestScenario(
                initialBalance, 0.0, null, null, 0, 0.0
        );
        return scenario;
    }

    private static List<TestScenario> parseScenarioStrings(List<String> scenarioStrings) {
        logger.info("Parsing test scenarios...");
        List<TestScenario> scenarios = new ArrayList<>();
        for (String scenarioAsString : scenarioStrings) {
            if (scenarioAsString.trim().isEmpty()) {
                continue;
            }
            TestScenario scenario = parseScenarioString(scenarioAsString);
            scenarios.add(scenario);
        }
        return scenarios;
    }

    public static void main(String [] args) throws IOException {
        System.out.println("START TESTING");

        // TODO: Instead of hardcoded "false", determine if tests are coming from file or cmdline
        // Note: testsFromFile is just a suggestion, you don't have to use testsFromFile or even an if/then statement!
        boolean testsFromFile = false;

        // Note: this is just a suggestion, you don't have to use testsFromFile or even an if/then statement!
        if (testsFromFile) {
            // if populating with scenarios from a CSV file...
            // TODO: We could get the filename from the cmdline, e.g. "-f CheckingAccountScenarios.csv"
            System.out.println("\n\n****** FROM FILE ******\n");
            // TODO: get filename from cmdline and use instead of TEST_FILE constant
            List<String> scenarioStringsFromFile = Files.readAllLines(Paths.get(TEST_FILE));
            // Note: toArray converts from a List to an array
            testScenarios = parseScenarioStrings(scenarioStringsFromFile);
            runJunitTests();
        }
        else {
            // if specifying a scenario on the command line,
            // for example "-t '10, 20|20, , 40|10, 0'"
            // Note the single-quotes above ^^^ because of the embedded spaces and the pipe symbol
            System.out.println("Command-line arguments passed in: " + java.util.Arrays.asList(args));
            // TODO: write the code to "parse" scenario into a suitable string
            // TODO: get TestScenario object from above string and store to testScenarios static var
            runJunitTests();
        }
        System.out.println("DONE");
    }
}
