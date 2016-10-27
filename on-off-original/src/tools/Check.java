package tools ;

public class Check {
  public static void check ( boolean check, String message ) {
    if ( !check ) {
      System.out.println( "\nERROR: " + message ) ;
      System.exit(0) ;
    }
  }
}
