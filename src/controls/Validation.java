package controls;

import dataClasses.Movement;
import dataClasses.Range;

/**
 * provides a few methods for validating user input of numbers Created by Edward on 5/29/2015.
 */
public abstract class Validation {

    /**
     * checks several strings to see if they are doubles
     *
     * @param input an array of strings to be checked
     * @return a boolean of whether or not all the strings are doubles
     */
    public static boolean validateDouble(String... input) {
        if (input == null || input.length == 0 || (input.length == 1 && input[0].isEmpty())) {
            return false;
        } else {
            boolean result = true;
            for (int i = 0; i < input.length && result; i++) {
                try {
                    Double.parseDouble(input[i]);
                } catch (NumberFormatException e) {
                    result = false;
                }
            }
            return result;
        }
    }

    /**
     * checks several strings to see if they are integers
     *
     * @param input an array of strings to be checked
     * @return a boolean of whether or not all the strings are doubles
     */
    public static boolean validateInteger(String... input) {
        if (input == null || input.length == 0 || (input.length == 1 && input[0].isEmpty())) {
            return false;
        } else {
            boolean result = true;
            for (int i = 0; i < input.length && result; i++) {
                try {
                    Integer.parseInt(input[i]);
                } catch (NumberFormatException e) {
                    result = false;
                }
            }
            return result;
        }
    }

    /**
     * checks several numeric values against a given range
     *
     * @param range the range that all the values should be within
     * @param test  a variable number of doubles to be checked against the range
     * @return a boolean of whether or not all the numbers are in the given range
     */
    public static boolean validateRange(Range range, double... test) {
        if (test == null || test.length == 0) {
            return false;
        } else {
            boolean result = true;
            for (int i = 0; i < test.length && result; i++) {
                if (Double.isNaN(test[i]) || !range.contains(test[i])) {
                    result = false;
                }
            }
            return result;
        }
    }

    /**
     * checks for conflicting movements in a ring-barrier diagram
     *
     * @param tl the Movement object in the top left corner of the ring-barrier
     * @param tr the Movement object in the top right corner of the ring-barrier
     * @param bl the Movement object in the bottom left corner of the ring-barrier
     * @param br the Movement object in the bottom right corner of the ring-barrier
     * @return a boolean of whether or not any of the movements conflict
     */
    public static boolean validateMovement(Object tl, Object tr, Object bl, Object br) {
        if (tl instanceof Movement && tr instanceof Movement && bl instanceof Movement &&
            br instanceof Movement) {
            Movement topLeft = (Movement) tl;
            Movement topRight = (Movement) tr;
            Movement botLeft = (Movement) bl;
            Movement botRight = (Movement) br;
            //check if there is one and only one left turn movement for each direction
            if ((topLeft.getTurning().equals(Movement.Turning.LEFT) ^
                 topRight.getTurning().equals(Movement.Turning.LEFT)) &&
                botLeft.getTurning().equals(Movement.Turning.LEFT) ^
                botRight.getTurning().equals(Movement.Turning.LEFT)) {
                //check if the directions conflict
                if (!topLeft.getDirection().equals(topRight.getDirection()) &&
                    !botLeft.getDirection().equals(botRight.getDirection()) &&
                    ((topLeft.getDirection().equals(botLeft.getDirection()) &&
                      !topLeft.getTurning().equals(botLeft.getTurning())) ||
                     !topLeft.getDirection().equals(botLeft.getDirection()) &&
                     topLeft.getTurning().equals(botLeft.getTurning()))) {
                    return true;
                }
            }
        }
        return false;
    }
}
