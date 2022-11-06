import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckingAccountTestFixture {
    public static Logger logger = LogManager.getLogger(CheckingAccountTestFixture.class);
    // TODO We should probably read the file from classpath instead of hardcoding the pathname
    static final String TEST_FILE = "src/test/resources/CheckingAccountTest.csv";

    record TestScenario(double initBalance,
                        List<Double> checks,
                        List<Double> withdrawals,
                        List<Double> deposits,
                        boolean runMonthEnd,
                        double endBalance
    ) { }

    private static List<TestScenario> testScenarios;

    @Test
    public void runTestScenarios() throws Exception {
        assertThat("testScenarios object must be populated, is this running from main()?",
                testScenarios, notNullValue());

        // iterate over all test scenarios
        for (int testNum = 0; testNum < testScenarios.size(); testNum++) {
            TestScenario scenario = testScenarios.get(testNum);
            logger.info("**** Running test for {}", scenario);

            // set up account with specified starting balance
            CheckingAccount ca = new CheckingAccount(
                    "test "+testNum, scenario.initBalance, new Owner("TEST_"+testNum));

            // now process checks, withdrawals, deposits
            for (double checkAmount : scenario.checks) {
                ca.writeCheck("CHECK", checkAmount);
            }
            for (double withdrawalAmount : scenario.withdrawals) {
                ca.withdraw(withdrawalAmount);
            }
            for (double depositAmount : scenario.deposits) {
                ca.deposit(depositAmount);
            }

            // run month-end if desired and output register
            if (scenario.runMonthEnd) {
                ca.monthEnd();
                for (Map.Entry<String, Double> entry : ca.getRegisterEntries()) {
                    logger.info("Register Entry -- {}: {}", entry.getKey(), entry.getValue());

                }
            }

            // make sure the balance is correct
            assertThat("Test #" + testNum + ":" + scenario, ca.getBalance(), is(scenario.endBalance));
        }
    }

    private static void runJunitTests() {
        JUnitCore jc = new JUnitCore();
        jc.addListener(new TextListener(System.out));
        Result r = jc.run(CheckingAccountTestFixture.class);
        System.out.printf("Tests run: %d Passed: %d Failed: %d\n",
                r.getRunCount(), r.getRunCount() - r.getFailureCount(), r.getFailureCount());
        System.out.println("Failures:");
        for (Failure f : r.getFailures()) {
            System.out.println("\t"+f);
        }
    }

    // TODO this could be added to TestScenario class
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

    // TODO this could be added to TestScenario class
    private static TestScenario parseScenarioString(String scenarioAsString) {
        String [] scenarioValues = scenarioAsString.split(",");
        // should probably validate length here
        double initialBalance = Double.parseDouble(scenarioValues[0]);
        List<Double> checks = parseListOfAmounts(scenarioValues[1]);
        List<Double> wds = parseListOfAmounts(scenarioValues[2]);
        List<Double> deps = parseListOfAmounts(scenarioValues[3]);
        double finalBalance = Double.parseDouble(scenarioValues[4]);
        TestScenario scenario = new TestScenario(
                initialBalance, checks, wds, deps, false, finalBalance
        );
        return scenario;
    }

    private static List<TestScenario> parseScenarioStrings(String ... scenarioStrings) {
        logger.info("Running test scenarios...");
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
        System.out.println("START");

        // We can:
        // ... manually populate the list of scenarios we want to test...
        System.out.println("\n\n****** FROM OBJECTS ******\n");
        testScenarios = List.of(
                new TestScenario(100, List.of(), List.of(), List.of(), false, 100),
                new TestScenario(100, List.of(10d), List.of(), List.of(), false, 90),
                new TestScenario(100, List.of(10.,20.), List.of(), List.of(10.), true, 80)
                );
        runJunitTests();

        // ...or create scenarios from a collection of strings...
        // Format for each line: BALANCE,check_amt|check_amt|...,withdraw_amt|...,deposit_amt|...,end_balance
        // note we left out runMonthEnd from our file format

        // Same scenarios as above plus one more to verify it's running these string scenarios
        System.out.println("\n\n****** FROM STRINGS ******\n");
        String [] scenarioStrings = {
                "0, , , 10|20, 30",
                "100, , , , 100",
                "100, 10, , , 90",
                "100, 10|20, , 10, 80"
        };
        List<TestScenario> parsedScenarios = parseScenarioStrings(scenarioStrings);
        testScenarios = parsedScenarios;
        runJunitTests();

        // ...or populate with scenarios from a CSV file...
        // now load these same scenarios from a file plus one more
        System.out.println("\n\n****** FROM FILE ******\n");
        // We could get the filename from the cmdline, e.g. "-f CheckingAccountScenarios.csv"
        List<String> scenarioStringsFromFile = Files.readAllLines(Paths.get(TEST_FILE));
        testScenarios = parseScenarioStrings(scenarioStringsFromFile.toArray(String[]::new));
        runJunitTests();

        // ...or, we could also specify a single scenario on the command line,
        // for example "-t '10, 20|20, , 40|10, 0'"
        // Note the single-quotes because of the embedded spaces and the pipe symbol
        System.out.println("Command-line arguments passed in: " + java.util.Arrays.asList(args));
        
        System.out.println("DONE");
    }
}
