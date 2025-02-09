package cdioil.backoffice.console.presentation;

import cdioil.backoffice.application.LoadAnswersController;
import cdioil.console.Console;
import cdioil.files.InvalidFileFormattingException;

/**
 * UI Class
 *
 * Stress Tests the application by loading hundreds of Survey Reviews on runtime
 */
public class LoadAnswersUI {
    /**
     * Separator used for clarity
     */
    private static final String SEPARATOR = "================================";
    /**
     * Controller field
     */
    private LoadAnswersController controller;

    /**
     * Instantiates the controller and starts
     */
    public LoadAnswersUI() {
        controller = new LoadAnswersController();
        start();
    }

    /**
     * Starts the stress test
     */
    private void start() {
        System.out.println(SEPARATOR);
        System.out.println("Loading Application with Answers");
        System.out.println(SEPARATOR);

        String filename = Console.readLine("File:");

        int numAnswers = Console.readInteger("Number of answers:");

        try {
            long timeTook = controller.performStressTest(filename, numAnswers);
            // Number of elements that were loaded to the application
            System.out.println("Finished Loading Answers");
            System.out.println(SEPARATOR);
            System.out.println(String.format("Took %s ms", timeTook));
            System.out.println("Finished.");
            System.out.println(numAnswers + " Reviews added");
            System.out.println(SEPARATOR);
        } catch (InvalidFileFormattingException|InterruptedException e) {
            System.out.println("File does not exist or has invalid formatting.");
        }
    }
}
