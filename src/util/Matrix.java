package util;

public class Matrix{

  protected double[][] matrix;

  public Matrix(int rows, int cols){
    matrix = new double[rows][cols];
  }

  public void set(int row, int col, double v){
    matrix[row][col] = v;
  }

  public double get(int row, int col){
    return matrix[row][col];
  }

  public int getRowDimension(){
    return matrix.length;
  }

  public int getColumnDimension(){
    if(getRowDimension() == 0){
      return 0;
    }
    else{
      return matrix[0].length;
    }
  }

  public String toString(){
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < matrix.length; i++){
      for(int j = 0; j < matrix[i].length; j++){
        sb.append(matrix[i][j]);
        sb.append(" ");
      }
      sb.append("\n");
    }
    return sb.toString();
  }

}