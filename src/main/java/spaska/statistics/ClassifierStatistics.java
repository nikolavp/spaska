package spaska.statistics;

import spaska.data.Attribute.ValueType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Lazar Chifudov
 */
public final class ClassifierStatistics extends Statistics {

    private int[][] confusionMatrix;
    private String[] classNames;
    private Map<String, Integer> namesMap; // for easy access to class numbers
    private boolean isNumeric; // if class value is numeric, not nominal
    private List<Double> residuals; // for storing numeric residuals

    /**
     * This constructor creates an empty statistics of type numeric or nominal
     * based on the supplied value type.
     * 
     * @param type
     *            the parameter type from which to create the classifier
     *            statistics
     */
    public ClassifierStatistics(ValueType type) {
        switch (type) {

        case Numeric:
            residuals = new ArrayList<Double>();
            isNumeric = true;
            break;
        case Nominal:
            isNumeric = false;
            break;
        default:
            throw new UnsupportedOperationException("Cannot create"
                    + " statistics for unknown class types!");
        }
    }

    /* set the class names for a numeric type statistic */
    public void setClassNames(String[] classNames) {
        if (classNames == null) {
            throw new IllegalArgumentException("classNames = null!");
        }
        if (this.classNames != null) {
            throw new IllegalStateException("Class names already set!");
        }
        if (isNumeric) {
            throw new IllegalStateException(
                    "Cannot set class names on numeric type statistic!");
        }
        confusionMatrix = new int[classNames.length][classNames.length];
        namesMap = new HashMap<String, Integer>();
        this.classNames = new String[classNames.length];
        for (int i = 0; i < this.classNames.length; i++) {
            this.classNames[i] = classNames[i];
            namesMap.put(this.classNames[i], i);
        }
        modified = true;
    }

    /*
     * add a pair (real class, classified class) by class index in confusion
     * matrix
     */
    private void addInfo(int realClass, int classifiedClass) {
        if (realClass < confusionMatrix.length && realClass >= 0
                && classifiedClass < confusionMatrix.length
                && classifiedClass >= 0) {
            confusionMatrix[realClass][classifiedClass]++;
            instances++;
            modified = true;
        } else {
            throw new IllegalArgumentException();
        }
    }

    /* add a pair (real class, classified class) for nominal type statistics */
    public void addNominalInfo(String realClass, String classifiedClass) {
        if (isNumeric) {
            throw new IllegalStateException(
                    "Cannot add info by class name on numeric statistics");
        }
        if (classNames == null) {
            throw new IllegalStateException("Class names not set!");
        }
        int realClassNumber = getClassNumber(realClass);
        int classifiedClassNumber = getClassNumber(classifiedClass);
        addInfo(realClassNumber, classifiedClassNumber);
    }

    /* add a pair (real class, classified class) for numeric type statistics */
    public void addNumericInfo(double realClass, double classifiedClass) {
        if (!isNumeric) {
            throw new IllegalStateException("Cannot add numeric info on "
                    + "nominal class statistics.");
        }
        residuals.add(realClass - classifiedClass); // store residual
        instances++;
        modified = true;
    }

    /* convert class name to class number */
    private int getClassNumber(String className) {
        if (namesMap != null) {
            Integer classNum = namesMap.get(className);
            if (classNum != null) {
                return classNum;
            }
        }
        return -1;
    }

    /* convert class number to class name */
    private String getClassName(int classNumber) {
        if (classNames != null) {
            return classNames[classNumber];
        }
        return classNumber + "";
    }

    /* get the mean squared error for a numeric statistic */
    private double getNumericError() {
        double sum = 0.0;
        for (double err : residuals) {
            sum += err * err;
        }
        return (sum / residuals.size());
    }

    /* get precision value by class number */
    private double getPrecision(int classNum) {
        int returned = 0;
        for (int i = 0; i < confusionMatrix.length; ++i) {
            returned += confusionMatrix[i][classNum];
        }
        if (returned == 0) {
            return 0;
        }
        return confusionMatrix[classNum][classNum] / (double) returned;
    }

    /* get precision value by class name */
    public double getPrecision(String className) {
        if (isNumeric) {
            throw new UnsupportedOperationException("Precision not available"
                    + "for numeric statistics.");
        }
        int classNum = getClassNumber(className);
        return getPrecision(classNum);
    }

    /* get recall value by class number */
    private double getRecall(int classNum) {
        int correct = 0;
        for (int i = 0; i < confusionMatrix.length; ++i) {
            correct += confusionMatrix[classNum][i];
        }
        if (correct == 0) {
            return 0;
        }
        return confusionMatrix[classNum][classNum] / (double) correct;
    }

    /* get recall value by class name */
    public double getRecall(String className) {
        if (isNumeric) {
            throw new UnsupportedOperationException("Recall not available"
                    + "for numeric statistics.");
        }
        int classNum = getClassNumber(className);
        return getRecall(classNum);
    }

    /* return an array of all precisions */
    public double[] getPrecisions() {
        if (isNumeric) {
            throw new UnsupportedOperationException("f-measure not available"
                    + "for numeric statistics.");
        }
        double[] result = new double[confusionMatrix.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = getPrecision(i);
        }
        return result;
    }

    /* return an array of all recalls */
    public double[] getRecalls() {
        if (isNumeric) {
            throw new UnsupportedOperationException("f-measure not available"
                    + "for numeric statistics.");
        }
        double[] result = new double[confusionMatrix.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = getRecall(i);
        }
        return result;
    }

    /* return an f-measure by a class name */
    public double getFMeasure(String className) {
        if (isNumeric) {
            throw new UnsupportedOperationException("f-measure not available"
                    + "for numeric statistics.");
        }
        int classNum = getClassNumber(className);
        double precision = getPrecision(classNum);
        double recall = getRecall(classNum);
        if (precision + recall == 0) {
            return 0;
        }
        return (2 * precision * recall) / (precision + recall);
    }

    /* return an array of all f-measures */
    public double[] getFMeasures() {
        if (isNumeric) {
            throw new UnsupportedOperationException("f-measure not available"
                    + "for numeric statistics.");
        }
        double[] result = new double[confusionMatrix.length];
        for (int i = 0; i < result.length; i++) {
            double p = getPrecision(i);
            double r = getRecall(i);
            if (p + r == 0) {
                result[i] = 0;
            } else {
                result[i] = (2 * p * r) / (p + r);
            }
        }
        return result;
    }

    public double getGeneralPrecision() {
        int correct = 0;
        for (int i = 0; i < confusionMatrix.length; i++) {
            correct += confusionMatrix[i][i];
        }
        return correct / (double) instances;
    }

    /* generate the output string and assign it to the info field */
    protected void generateInfo() {

        StringBuilder result = new StringBuilder();
        result.append("================================================\n");
        if (algorithmName != null) {
            result.append("Classifier: " + algorithmName + "\n");
        }
        String classType = isNumeric ? "NUMERIC" : "NOMINAL";
        result.append("Class type is: " + classType + "\n");
        result.append("Total instances: " + instances + "\n");

        if (isNumeric) {
            result.append("- - - - - - - - - - - - - - -\n");
            result.append(String.format("Mean squared error: %.6f\n",
                    +this.getNumericError()));
            result.append("- - - - - - - - - - - - - - -\n");
            result.append("Test time (HH:MM:SS.MS): " + timeToString(testTime));
            result.append("\n================================================\n");
            info = result.toString();
            return;
        }

        int correctlyClassified = 0;
        for (int i = 0; i < confusionMatrix.length; ++i) {
            correctlyClassified += confusionMatrix[i][i];
        }
        result.append("Correctly classified: " + correctlyClassified);
        double percent = (instances == 0) ? 100 : (correctlyClassified * 100)
                / (double) instances;
        result.append(String.format(" (%.2f%%)\n", percent));
        result.append("Incorrectly classified: "
                + (instances - correctlyClassified));
        result.append(String.format(" (%.2f%%)\n", 100.0 - percent));
        result.append("Test time (HH:MM:SS.MS): " + timeToString(testTime));
        result.append("\n================================================");
        result.append("\nDETAILS BY CLASSES:\n");
        result.append("------------------------------------------------");

        for (int i = 0; i < confusionMatrix.length; i++) {
            result.append("\nClass: " + getClassName(i));
            int total = 0;
            for (int j = 0; j < confusionMatrix.length; j++) {
                total += confusionMatrix[i][j];
            }
            result.append("\nTotal instances: " + total);
            result.append("\nCorrectly classified: " + confusionMatrix[i][i]
                    + "\n");
            percent = (total == 0) ? 100 : (confusionMatrix[i][i] * 100)
                    / (double) total;
            result.append("Incorrectly classified: "
                    + (total - confusionMatrix[i][i]) + "\n");
            double precision = getPrecision(i);
            double recall = getRecall(i);
            result.append(String.format("Precision:  %.3f\n", precision));
            result.append(String.format("Recall:     %.3f\n", recall));
            double fMeasure;
            if (precision + recall == 0) {
                fMeasure = 0;
            } else {
                fMeasure = (2 * precision * recall) / (precision + recall);
            }
            result.append(String.format("F-Measure:  %.3f\n", fMeasure));
            result.append("------------------------------------------------");
        }
        result.append("\n================================================");
        info = result.toString();
        modified = false;
    }
}
