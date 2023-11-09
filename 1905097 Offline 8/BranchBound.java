import java.io.*;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Scanner;

class Matrix{

    int[][] matrixArray;
    int lowerBound;
    int level;
    int order;
    int fixedRow;
    int fixedColumn;
    int size;


    public Matrix(int n){
        matrixArray = new int[n][n];
        size = n;
        for(int i = 0; i < n; i++){
            matrixArray[i] = new int[n];
        }
        lowerBound = -1;
        level = -1;
        order = -1;
        fixedRow = -1;
        fixedColumn = -1;
    }


    public Matrix(int[][] arr, int size, int fixedColumn, int fixedRow, int level, int order){

        matrixArray = new int[size][size];
        this.size = size;
        for(int i = 0; i < size; i++){
            matrixArray[i] = new int[size];
            for(int j = 0; j < size; j++){
                matrixArray[i][j] = arr[i][j];
            }
        }
        this.level = level;
        this.order = order;
        this.fixedRow = fixedRow;
        this.fixedColumn = fixedColumn;
        this.lowerBound = getLowerBound();
    }

    public int getBandWidth(){
        int bw = 0;

        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                if(matrixArray[i][j] == 1){
                    bw = Math.max(bw, Math.abs(i - j) + 1);
                }
            }
        }

        for(int j = 0; j < size; j++){
            for(int i = 0; i < size; i++){
                if(matrixArray[i][j] == 1){
                    bw = Math.max(bw, Math.abs(i - j) + 1);
                }
            }
        }

        return bw;
    }

    public void columnSwap(int col1, int col2){

        for(int i = col2; i > col1; i--){
            for(int j = 0; j < size; j++){
                int tmp = matrixArray[j][i];
                matrixArray[j][i] = matrixArray[j][i-1];
                matrixArray[j][i-1] = tmp;
            }
        }

    }

    public void rowSwap(int row1, int row2){

        for(int i = row2; i > row1; i--){
            for(int j = 0; j < size; j++){
                int tmp = matrixArray[i][j];
                matrixArray[i][j] = matrixArray[i-1][j];
                matrixArray[i-1][j] = tmp;
            }
        }

    }

    public int unfixedLowerBound(){
        int cnt = 0;

        for(int i = fixedRow; i < size; i++){
            int tmp = 0;
            for(int j = fixedColumn; j < size; j++){
                tmp += matrixArray[i][j];
            }
            if(tmp > cnt)
              cnt = tmp;
        }

        for(int j = fixedColumn; j < size; j++){
            int tmp = 0;
            for(int i = fixedRow; i < size; i++){
                tmp += matrixArray[i][j];
            }
            if(tmp > cnt)
               cnt = tmp;
        }

        cnt = (cnt + 1) / 2;
        return cnt;
    }

    public int fixedLowerBound(){
        int cnt = 0;

        for(int i = 0; i < fixedColumn; i++){
            int tmp1 = 0;
            int tmp2 = fixedRow;

            for(int j = 0; j < size; j++){
                if(j < fixedRow && matrixArray[j][i] == 1){
                   tmp1 = Math.max(tmp1, Math.abs(i - j) + 1);
                }
                else if(matrixArray[j][i] == 1){
                    tmp2++;
                }
            }

            if(tmp2 == fixedRow){
                cnt = Math.max(cnt, tmp1);
            }

            else{
                cnt = Math.max(cnt, Math.abs(tmp2 - i - 1) + 1);
            }

        }

        for(int i = 0; i < fixedRow; i++){
            int tmp1 = 0;
            int tmp2 = fixedColumn;

            for(int j = 0; j < size; j++){
                if(j < fixedColumn && matrixArray[i][j] == 1){
                    tmp1 = Math.max(tmp1, Math.abs(i - j) + 1);
                }
                else if(matrixArray[i][j] == 1){
                    tmp2++;
                }
            }

            if(tmp2 == fixedColumn){
                cnt = Math.max(cnt, tmp1);
            }

            else{
                cnt = Math.max(cnt, Math.abs(tmp2 - i - 1) + 1);
            }
        }

        return cnt;
    }

    public int getLowerBound(){

        int tmp1 = fixedLowerBound();
        int tmp2 = unfixedLowerBound();
        int tmp3 = Math.max(tmp1, tmp2);
        return tmp3;
    }



}

class Priority implements Comparator<Matrix>{
    @Override
    public int compare(Matrix matrix1, Matrix matrix2){
        if(matrix1.lowerBound < matrix2.lowerBound){
            return -1;
        }
        else if(matrix1.lowerBound == matrix2.lowerBound && matrix1.level > matrix2.level){
            return -1;
        }
        else if(matrix1.lowerBound == matrix2.lowerBound && matrix1.level == matrix2.level && matrix1.order > matrix2.order){
            return -1;
        }
        return 1;
    }
}

public class BranchBound {
    public static void main(String[] args) {
        File inputFile = new File("Input.txt");
        File outputFile = new File("Output.txt");
        Scanner scn = null;
        BufferedWriter bw = null;

        try {
            scn = new Scanner(inputFile);
            bw = new BufferedWriter(new FileWriter(outputFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String str = scn.nextLine();
        int n = Integer.parseInt(str);
        int[][] givenMatrix = new int[n][n];

        for(int i = 0; i < n; i++) {
            str = scn.nextLine();

            String[] str1 = str.split(" ");
            for (int j = 0; j < n; j++) {

                if (str1[j].equals("0")) {
                    givenMatrix[i][j] = 0;
                } else {
                    givenMatrix[i][j] = 1;
                }
            }

        }
//        for(int i = 0; i < n; i++){
//            for(int j = 0; j < n; j++){
//                System.out.print(givenMatrix[i][j] + " ");
//            }
//            System.out.println();
//        }

        Matrix matrix = new Matrix(givenMatrix, n, 0, 0, 0, 0);
        int minBand = matrix.getBandWidth();
        PriorityQueue<Matrix> pq = new PriorityQueue<Matrix>(new Priority());
        int[][] ansMatrix = new int[n][n];

        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                ansMatrix[i][j] = matrix.matrixArray[i][j];
            }
        }
        pq.add(matrix);

        while(!pq.isEmpty()){
            Matrix mat1 = pq.remove();

//            System.out.println("Parent Matrix: ");
//            System.out.println("For level: " + mat1.level + " and for order: " + mat1.order);
//            for(int i = 0; i < n; i++){
//                for(int j = 0; j < n; j++){
//                    if(mat1.matrixArray[i][j] == 1){
//                        System.out.print("X ");
//                    }
//                    else{
//                        System.out.print("0 ");
//                    }
//                }
//                System.out.println();
//            }

            if(mat1.lowerBound >= minBand){
                break;
            }

            if(mat1.fixedRow == n-1 && mat1.lowerBound < minBand){
                for(int i = 0; i < n; i++){
                    for(int j = 0; j < n; j++){
                        ansMatrix[i][j] = mat1.matrixArray[i][j];
                    }
                }
                minBand = mat1.getBandWidth();
            }

            else if(mat1.fixedColumn == mat1.fixedRow){
                int minLowerBound = n;
                int fc = mat1.fixedColumn;

                for(int k = fc; k < n; k++){
                    int[][] tmpArr = new int[n][n];
                    for(int i = 0; i < n; i++){
                        for(int j = 0; j < n; j++){
                            tmpArr[i][j] = mat1.matrixArray[i][j];
                        }
                    }

                    Matrix tmpMat = new Matrix(tmpArr, n, mat1.fixedColumn + 1, mat1.fixedRow, mat1.level + 1, k);
                    tmpMat.columnSwap(fc, k);
                    tmpMat.lowerBound = tmpMat.getLowerBound();
                    minLowerBound = Math.min(minLowerBound, tmpMat.lowerBound);
                }

                for(int k = fc; k < n; k++){
                    int[][] tmpArr = new int[n][n];
                    for(int i = 0; i < n; i++){
                        for(int j = 0; j < n; j++){
                            tmpArr[i][j] = mat1.matrixArray[i][j];
                        }
                    }

                    Matrix tmpMat = new Matrix(tmpArr, n, mat1.fixedColumn + 1, mat1.fixedRow, mat1.level + 1, k);
                    tmpMat.columnSwap(fc, k);
                    tmpMat.lowerBound = tmpMat.getLowerBound();
                    if(minLowerBound == tmpMat.lowerBound){
//                        System.out.println("For level: " + tmpMat.level + " and for lower bound: " + tmpMat.lowerBound + " and for order: " + tmpMat.order);
//                        System.out.println("Fixed Column: " + fc + " and swapped Column: " + k);
//                        for(int i = 0; i < n; i++){
//                            for(int j = 0; j < n; j++){
//                                if(tmpMat.matrixArray[i][j] == 1){
//                                    System.out.print("X ");
//                                }
//                                else{
//                                    System.out.print("0 ");
//                                }
//                            }
//                            System.out.println();
//                        }
                        pq.add(tmpMat);
                    }
                }
            }

            else{
                int minLowerBound = n;
                int fr = mat1.fixedRow;

                for(int k = fr; k < n; k++){
                    int[][] tmpArr = new int[n][n];
                    for(int i = 0; i < n; i++){
                        for(int j = 0; j < n; j++){
                            tmpArr[i][j] = mat1.matrixArray[i][j];
                        }
                    }

                    Matrix tmpMat = new Matrix(tmpArr, n, mat1.fixedColumn, mat1.fixedRow + 1, mat1.level + 1, k);
                    tmpMat.rowSwap(fr, k);
                    tmpMat.lowerBound = tmpMat.getLowerBound();
                    minLowerBound = Math.min(minLowerBound, tmpMat.lowerBound);
                }

                for(int k = fr; k < n; k++){
                    int[][] tmpArr = new int[n][n];
                    for(int i = 0; i < n; i++){
                        for(int j = 0; j < n; j++){
                            tmpArr[i][j] = mat1.matrixArray[i][j];
                        }
                    }

                    Matrix tmpMat = new Matrix(tmpArr, n, mat1.fixedColumn, mat1.fixedRow + 1, mat1.level + 1, k);
                    tmpMat.rowSwap(fr, k);
                    tmpMat.lowerBound = tmpMat.getLowerBound();

                    if(minLowerBound == tmpMat.lowerBound){
//                        System.out.println("For level: " + tmpMat.level + " and for lower bound: " + tmpMat.lowerBound + " and for order: " + tmpMat.order);
//                        System.out.println("Fixed row: " + fr + " and swapped row: " + k);
//                        for(int i = 0; i < n; i++){
//                            for(int j = 0; j < n; j++){
//                                if(tmpMat.matrixArray[i][j] == 1){
//                                    System.out.print("X ");
//                                }
//                                else{
//                                    System.out.print("0 ");
//                                }
//                            }
//                            System.out.println();
//                        }
                        pq.add(tmpMat);
                    }
                }
            }
        }

        System.out.println(minBand);
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                if(ansMatrix[i][j] == 1){
                    System.out.print("X ");
                }
                else{
                    System.out.print("0 ");
                }
            }
            System.out.println();
        }

        try {
            bw.write(minBand + "\n");
            for(int i = 0; i < n; i++){
                for(int j = 0; j < n; j++){
                    if(ansMatrix[i][j] == 1){
                       bw.write("X ");
                    }
                    else{
                       bw.write("0 ");
                    }
                }
                bw.write("\n");
            }
            bw.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
