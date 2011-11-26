package spaska.statistics;

/**
 *
 * @author Lazar Chifudov
 */

/* abstract class containing common methods and data
 * for different types of statistics
 */
public abstract class Statistics {

    protected int instances; //total instances
    protected long testTime; //running time - milliseconds
    protected String info, additionalInfo; //output string
    protected String algorithmName; //algorithm name
    protected boolean modified; //used when generating output

    /* set algorithm name if available */
    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
        modified = true;
    }

    /* set algorithm running time */
    public void setTestTime(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException();
        }
        this.testTime = millis;
        modified = true;
    }

    /* add to the current algorithm running time */
    public void addTestTime(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException();
        }
        this.testTime += millis;
        modified = true;
    }

    /* return test time */
    public long getTestTime() {
        return testTime;
    }

    /* generate String from the time */
    protected static String timeToString(long testTime) {
        /*if(testTime == 0) {
        return "N/A";
        } */
        long tempTime = testTime;
        int hours = (int) (tempTime / 3600000);
        tempTime %= 3600000;
        int minutes = (int) (tempTime / 60000);
        tempTime %= 60000;
        int seconds = (int) (tempTime / 1000);
        tempTime %= 1000;

        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, tempTime);
    }

    /* set something else to display after the statistics */
    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    /*generate the statistics output string */
    protected abstract void generateInfo();

    /* return the generated output string*/
    @Override
    public String toString() {
        if (modified) { //generate again only if modified
            generateInfo();
        }
        if(additionalInfo == null)
            additionalInfo = "";
        return info + "\n" + additionalInfo;
    }
}
