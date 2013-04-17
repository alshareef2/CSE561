package util;

public class PrintableSquareMatrix extends Matrix{

  private String[] names;

  public PrintableSquareMatrix(int rows, int cols){
    super(rows, cols);

    names = new String[]{};
  }

  public PrintableSquareMatrix(int rows, int cols, String[] names){
    super(rows, cols);
    this.names = names;
  }

  public void setNames(String[] names){
    this.names = names;
  }

  public String[] getNames(){
    return names;
  }

  public String toString(){
    //find the longest name
    int longestName = 0;
    StringBuffer sb = new StringBuffer();
    for(String s : names){
      longestName = Math.max(longestName, s.length());
    }
    longestName++;

    //print the top row
    sb.append(padRight("", longestName));
    for(String s : names){
      sb.append(padRight(s, longestName));
    }
    sb.append("\n");

    //print the matrix
    for(int row = 0; row < getRowDimension(); row++){
      sb.append(padRight(names[row], longestName));
      for(int col = 0; col < getColumnDimension(); col++){
        sb.append(padRight("" + matrix[row][col], longestName));
      }
      sb.append("\n");
    }

    return sb.toString();
  }

  public static String padRight(String s, int n) {
    return String.format("%1$-" + n + "s", s);  
  }
}