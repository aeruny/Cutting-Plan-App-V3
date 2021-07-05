package Main.Algorithm;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;

/**
 * @author Mingeon Sung
 * date: 1/6/2021
 * last updated: 7/5/2021
 */

public class CuttingPlan {

    private final String materialName;
    private double materialLength;
    private double bladeThickness;
    private Object[][] inputs;

    private double[] lengths;
    private int[] quantities;

    private String[][] plan;
    private Object[][] tableContents;
    private String[] columnNames;

    private int total;
    private double totalRemainder;
    private double[] remainders;
    private int[] cutCounts;

    public CuttingPlan(String materialName, double materialLength, double bladeThickness, Object[][] inputs) {
        // Store Inputs
        this.materialName = materialName;
        this.materialLength = materialLength;
        this.bladeThickness = bladeThickness;
        this.inputs = inputs;
        setInputs(inputs);

        // Process Cutting Plan
        double[][] cuttingPlan = calculateCuttingPlan(materialLength, bladeThickness, lengths, quantities);
        cuttingPlan = optimizedPlan(cuttingPlan);
        plan = toStringArray(cuttingPlan);

        // Conversion to Output
        setTotal(cuttingPlan);
        setExcessLengths(materialLength, cuttingPlan);

        setTableContents(cuttingPlan);
        setColumnNames(cuttingPlan);
    }

    // This methods outputs the cutting plan from 4 inputs
    private double[][] calculateCuttingPlan(double materialLength, double bladeThickness, double[] lengths, int[] quantities) {
        int a = 0;                                      // index/pointer of quantities[]
        int b = 0;                                      // index/pointer of lengths[]
        int c = 0;                                      // index of plan ArrayList (row)
        int d = 0;                                      // index of plan ArrayList[] (col)
        double temp = materialLength;                   // temporary value of target length for calculation
        boolean flag = true;                            // flag to check for an empty row
        List<double[]> cuttingPlan = new ArrayList<>(); // empty cutting plan arraylist
        int planLength = (int) (materialLength / lengths[lengths.length-1]);

        System.out.println(materialLength + "   " + lengths[lengths.length-1]);
        cuttingPlan.add(new double[planLength]);
        while(a < lengths.length) {
            // When the quantity at index a is 0, the corresponding length has been exhausted
            // Increment the indices a and b
            if(quantities[a] == 0) {
                a++;
                b = a;
            }
            else {
                // If the current value of temp is subtractable by the pointed length,
                // Subtract temp by the length and record the length to cuttingPlan
                // Else, move the pointer b to the next length
                if(temp >= lengths[b] && quantities[b] > 0) {
                    //System.out.println("a=" + a + " b= " + b + "      " + quantities[b]);
                    flag = false;
                    temp -= lengths[b];
                    temp -= bladeThickness;
                    quantities[b]--;
                    cuttingPlan.get(c)[d] = lengths[b];
                    d++;
                } else {
                    b++;
                }
            }

            // If the current value of temp is not subtractable at all,
            // Reset the temp value to the target value and pointer b to pointer a
            // To record the next row of plan ArrayList[], add a new element
            // And set pointer c to the new row, and set pointer d to 0;
            if(a < lengths.length && temp < getMin(a, lengths, quantities)) {
                cuttingPlan.add(new double[planLength]);
                flag = true;
                c++;
                d = 0;
                temp = materialLength;
                b = a;
            }
        }
        if(flag)
            cuttingPlan.remove(cuttingPlan.size() - 1);
        return toArray(cuttingPlan);
    }

    // Finds the smallest requested Length with an available Quantity
    private static double getMin(int index, double[] lengths, int[] quantities) {
        double min = lengths[index];

        for(int i = index; i < lengths.length; i++) {
            if(quantities[i] > 0 && lengths[i] < min) {
                min = lengths[i];
            }
        }
        return min;
    }

    private double[][] optimizedPlan(double[][] cuttingPlan) {
        // Find the column where the plan is empty
        int flag = 0;
        for(double[] row: cuttingPlan) {
            for (int c = flag; c < row.length; c++) {
                if (row[c] != 0) {
                    flag = c;
                }
            }
        }

        // Return a downscaled plan
        if(flag < cuttingPlan[0].length - 1) {
            double[][] resizedPlan = new double[cuttingPlan.length][];
            for(int row = 0; row < cuttingPlan.length; row++) {
                resizedPlan[row] = Arrays.copyOf(cuttingPlan[row], flag + 1);
            }
            return resizedPlan;
        }
        // Else return the original plan
        return cuttingPlan;
    }

    private String[][] toStringArray(double[][] input) {
        String[][] array = new String[input.length][input[0].length];
        for(int r = 0; r < array.length; r++) {
            for(int c = 0; c < array[r].length; c++) {
                array[r][c] = Double.toString(input[r][c]);
            }
        }
        return array;
    }


    private void setInputs(Object[][] inputs) {
        double[] lengths = toLengths(inputs);
        int[] quantities = toQuantities(inputs);
        System.out.println(lengths.length);
        double max;
        int temp;
        int index;
        for(int i = 0; i < inputs.length; i++) {
            max = lengths[i];
            index = i;
            for(int j = i; j < inputs.length; j++) {
                if(lengths[j] > max) {
                    index = j;
                    max = lengths[index];
                }
            }
            lengths[index] = lengths[i];
            lengths[i] = max;
            temp = quantities[index];
            quantities[index] = quantities[i];
            quantities[i] = temp;
        }
        this.lengths = lengths;
        this.quantities = quantities;
    }

    public void setMaterialLength(double materialLength) {
        this.materialLength = materialLength;
    }

    public void setBladeThickness(double bladeThickness) {
        this.bladeThickness = bladeThickness;
    }

    private void setTotal(double[][] cuttingPlan) {
        this.total = cuttingPlan.length;
    }

    private void setExcessLengths(double target, double[][] cuttingPlan) {
        int numCuts;
        cutCounts = new int[cuttingPlan.length];
        double lengthSum;
        double totalSum = 0;
        remainders = new double[cuttingPlan.length];
        for (int i = 0; i < cuttingPlan.length; i++) {
            lengthSum = 0;
            numCuts = 0;
            for (int j = 0; j < cuttingPlan[i].length; j++) {
                lengthSum += cuttingPlan[i][j];
                if(cuttingPlan[i][j] > 0)
                    numCuts++;
            }
            cutCounts[i] = numCuts;
            remainders[i] = target - lengthSum;
            totalSum += lengthSum;
        }
        this.totalRemainder = target * cuttingPlan.length - totalSum;
    }


    private void setTableContents(double[][] cuttingPlan) {
        System.out.println("CuttingPlan: Setting Table Contents");
        Object[][] table = new Object[cuttingPlan.length][cuttingPlan[0].length + 3];
        for(int i = 0; i < table.length; i++) {
            System.out.println(Arrays.toString(cuttingPlan[i]) + " " + cuttingPlan[i].length);
            for(int j = 0; j < table[i].length; j++) {
                System.out.print("row: "+ i + "  col: " + j + "  ");
                String cellStr = "";
                if(j == 0)
                    cellStr = Integer.toString(i + 1);
                else if(j <= cuttingPlan[i].length)
                    cellStr = Double.toString(cuttingPlan[i][j - 1]);
                else if(j == cuttingPlan[i].length + 1) // Blade Thickness
                    cellStr = Integer.toString(cutCounts[i]);
                else if(j == cuttingPlan[i].length + 2) // Excess
                    cellStr = Double.toString(remainders[i]);
                System.out.println(cellStr);
                table[i][j] = cellStr;
            }
        }
        this.tableContents = table;
    }

    // This method adds the column names for each column,
    // adds the order (ex. #1, #2, #3) at the first column of each row,
    // adds the remainders at the last column of each row.
    public void setColumnNames(double[][] cuttingPlan) {
        columnNames = new String[cuttingPlan[0].length + 3];
        columnNames[0] = "#";
        for(int i = 1; i < columnNames.length; i++) {
            columnNames[i] = Integer.toString(i);
        }
        columnNames[columnNames.length - 2] = "# of Cuts";
        columnNames[columnNames.length - 1] = "Remainder";
    }


    public static double[] toLengths(Object[][] inputs) {
        double[] lengths = new double[inputs.length];
        for(int i = 0; i < inputs.length; i++) {
            String str = inputs[i][0].toString();
            lengths[i] = Double.parseDouble(str);
        }
        return lengths;
    }
    public static int[] toQuantities(Object[][] inputs) {
        int[] quantities = new int[inputs.length];
        for(int i = 0; i < inputs.length; i++) {
            String str = inputs[i][1].toString();
            quantities[i] = Integer.parseInt(str);
        }
        return quantities;
    }

    private static double[][] toArray(List<double[]> original) {
        double[][] array = new double[original.size()][];
        for(int i = 0;  i < original.size(); i++) {
            array[i] = original.get(i);
        }
        return array;
    }

    public String getMaterialName() {
        return materialName;
    }
    public double getMaterialLength() {
        return materialLength;
    }
    public double getBladeThickness() {
        return bladeThickness;
    }
    public Object[][] getInputs() {
        return inputs;
    }
    public int getTotal() {
        return total;
    }
    public double getTotalRemainder() {
        return totalRemainder;
    }
    public Object[][] getTableContents() {
        return tableContents;
    }
    public String[] getColumnNames() {
        return columnNames;
    }

}
