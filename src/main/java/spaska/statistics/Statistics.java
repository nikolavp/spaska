package spaska.statistics;

/**
 * Abstract class containing common methods and data for different types of
 * statistics.
 * 
 * @author Lazar Chifudov
 */

public abstract class Statistics {

    private static final int SECONDS = 1000;
    private static final int MINUTES_IN_MILLISECONDS = 60000;
    private static final int HOURS_IN_MILLISECONDS = 3600000;
    private int instances; // total instances
    private long testTime; // running time - milliseconds
    private String info;
    private String additionalInfo; // output string
    private String algorithmName; // algorithm name
    private boolean modified; // used when generating output

    /**
     * Set algorithm name if available.
     * 
     * @param algorithmName
     *            the new algorithm name
     */
    public final void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
        setModified(true);
    }

    /**
     * Set algorithm running time. Set the ammount of time in milliseconds to
     * complete the algorithm for this statistics.
     * 
     * @param millis
     *            the ammount of time in milliseconds
     */
    public final void setTestTime(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException();
        }
        this.testTime = millis;
        setModified(true);
    }

    /**
     * Add to the current algorithm running time.
     * 
     * @param millis
     *            the time it was taken in milliseconds
     */
    public final void addTestTime(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException();
        }
        this.setTestTime(this.getTestTime() + millis);
        setModified(true);
    }

    /**
     * Get the ammount of time for testing.
     * 
     * @return the ammount of time for testing in milliseconds
     */
    public final long getTestTime() {
        return testTime;
    }

    /* generate String from the time */
    protected static String timeToString(long testTime) {
        /*
         * if(testTime == 0) { return "N/A"; }
         */
        long tempTime = testTime;
        int hours = (int) (tempTime / HOURS_IN_MILLISECONDS);
        tempTime %= HOURS_IN_MILLISECONDS;
        int minutes = (int) (tempTime / MINUTES_IN_MILLISECONDS);
        tempTime %= MINUTES_IN_MILLISECONDS;
        int seconds = (int) (tempTime / SECONDS);
        tempTime %= SECONDS;

        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds,
                tempTime);
    }

    /**
     * Set something else to display after the statistics.
     * 
     * @param additionalInfo
     *            additional information to display after displaying the
     *            statistcs.
     */
    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    /**
     * Generate the statistics output string. This should be called by
     * subclasses to generate meaningful statistics information.
     */
    protected abstract void generateInfo();

    @Override
    public String toString() {
        if (isModified()) { // generate again only if modified
            generateInfo();
        }
        if (getAdditionalInfo() == null) {
            setAdditionalInfo("");
        }
        return getInfo() + "\n" + getAdditionalInfo();
    }

    protected int getInstances() {
        return instances;
    }

    protected void setInstances(int instances) {
        this.instances = instances;
    }

    protected String getInfo() {
        return info;
    }

    protected void setInfo(String info) {
        this.info = info;
    }

    protected String getAdditionalInfo() {
        return additionalInfo;
    }

    protected String getAlgorithmName() {
        return algorithmName;
    }

    protected boolean isModified() {
        return modified;
    }

    protected void setModified(boolean modified) {
        this.modified = modified;
    }
}
