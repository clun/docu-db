package fr.clunven.docu.utils;

public class BatchUtils {
    
    /**
     * Simple way to check if a value is a integer.
     *
     * @param str
     *      current string
     * @return
     */
    public static boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    /**
     * Format execution time for log.
     *
     * @param executionTime
     *          current execution time in millis
     * @return
     */
    public static String formatExecutionTime(long executionTime) {
        long min = executionTime / (1000 * 60L);
        executionTime = executionTime - min * 1000 * 60L;
        long sec = executionTime / 1000L;
        long millis = executionTime - sec * 1000L;
        StringBuilder sb = new StringBuilder();
        sb.append(min + " min. ");
        sb.append(sec + " sec. ");
        sb.append(millis + " millis");
        return sb.toString();
    }
    
}
