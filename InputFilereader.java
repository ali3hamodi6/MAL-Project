//Ali Hamodi

import java.io.File;
import java.util.Scanner;

public class InputFilereader {


    public InputFilereader(){

        readingMalFile();
    }

     public void readingMalFile() {

         try {
             File file = new File("malprogram.txt");
             Scanner scan = new Scanner(file);
             SyntaxChecker syntaxChecker = new SyntaxChecker(scan);

         } catch (Exception e) {

             System.out.println(" Input file was not found ");
             e.printStackTrace();
         }
     }
}
