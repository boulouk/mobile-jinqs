package tools ;

public class EmptyListException extends RuntimeException {
   public EmptyListException( String name )
   {
      super( "The " + name + " is empty" );
   }
}

