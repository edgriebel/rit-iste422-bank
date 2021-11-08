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

public class CheckingAccountTestRunner {
    public static Logger logger = LogManager.getLogger(CheckingAccountTestRunner.class);
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
    public void testParams() throws Exception {
        assertThat("testScenarios object must be populated", testScenarios, notNullValue());

        // iterate over all test params
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

    private static void runTests() {
        JUnitCore jc = new JUnitCore();
        jc.addListener(new TextListener(System.out));
        Result r = jc.run(CheckingAccountTestRunner.class);
        System.out.printf("Tests run: %d Passed: %d Failed: %d\n",
                r.getRunCount(), r.getRunCount() - r.getFailureCount(), r.getFailureCount());
        System.out.println("Failures:");
        for (Failure f : r.getFailures()) {
            System.out.println("\t"+f);
        }
    }

    private static List<Double> parseAmountList(String amounts) {
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

    private static List<TestScenario> parseScenarioStrings(String [] scenarioStrings) {
        logger.info("Running test scenarios...");
        List<TestScenario> scenarios = new ArrayList<>();
        for (String scenarioAsString : scenarioStrings) {
            if (scenarioAsString.trim().isEmpty()) {
                continue;
            }
            String [] scenarioValues = scenarioAsString.split(",");
            // should probably validate length here
            double initialBalance = Double.parseDouble(scenarioValues[0]);
            List<Double> checks = parseAmountList(scenarioValues[1]);
            List<Double> wds = parseAmountList(scenarioValues[2]);
            List<Double> deps = parseAmountList(scenarioValues[3]);
            double finalBalance = Double.parseDouble(scenarioValues[4]);
            scenarios.add(new TestScenario(
                    initialBalance, checks, wds, deps, false, finalBalance
            ));
        }
        return scenarios;
    }

    public static void main(String [] args) throws IOException {
        System.out.println("START");

        // Manually populate the list of scenarios we want to test
        System.out.println("\n\n****** FROM OBJECTS ******\n");
        testScenarios = List.of(
                new TestScenario(100, List.of(), List.of(), List.of(), false, 100),
                new TestScenario(100, List.of(10d), List.of(), List.of(), false, 90),
                new TestScenario(100, List.of(10.,20.), List.of(), List.of(10.), true, 80)
                );
        runTests();

        // now populate with scenarios from a CSV file
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
        runTests();

        // now load these same scenarios from a file plus one more
        System.out.println("\n\n****** FROM FILE ******\n");
        List<String> lst = Files.readAllLines(Paths.get(TEST_FILE));
        testScenarios = parseScenarioStrings(lst.toArray(String[]::new));
        runTests();

        // We could also demonstrate reading a scenario from stdin via Scanner() or cmdline via args

        System.out.println("DONE");
    }
}
