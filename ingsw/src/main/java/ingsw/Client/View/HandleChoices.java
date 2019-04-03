package ingsw.Client.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This class is responsible for handling User Choices form CLI
 */
public class HandleChoices {

    /**
     * Overloaded function that handle the selection between choices with no excluded values
     */
    public static synchronized int handleChoices(int minValue, int maxValue) {
        return handleChoices(minValue, maxValue, new ArrayList());
    }

    /**
     * Helper function to retrieve a int choice from a user. The choice shall be between
     * @param minValue minimum value permitted, included
     * @param maxValue maximum value permitted, excluded
     * @param except ArrayList of values we don't allow the user to select
     * @return the user choice
     */
    public static synchronized int handleChoices(int minValue, int maxValue, List except) {

        Scanner scanner = new Scanner(System.in);
        int val = -1;
        do {
            System.out.print("Pick a choice from " +
                    minValue + " to " + maxValue +
                    ((!except.isEmpty()) ? " avoid " + except : "") + "\n"); // Print the bounds of the input and eventually the limits
            try {
                val = scanner.nextInt();
            } catch (Exception e) {
                scanner.next();
                val = -1;
                continue;
            }
        } while (val < minValue || val > maxValue || except.contains(val));
        return val;
    }
}
