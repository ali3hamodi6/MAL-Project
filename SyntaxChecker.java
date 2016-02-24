import java.util.Scanner;

public class SyntaxChecker {
    Scanner scanner;

    public SyntaxChecker(Scanner scanner) {
        this.scanner = scanner;
        lineByLineFileReader();
    }

    private void lineByLineFileReader() {

        StringBuilder builder = new StringBuilder();
        String temp =" ";

        while (scanner.hasNextLine()) {
            String theCurrentLine = scanner.nextLine();

            if (theCurrentLine.startsWith(";")) {
                continue;

            } else if (theCurrentLine.trim().length() == 0) {
                continue;

            } else if (theCurrentLine.contains(";")){
                temp = theCurrentLine.substring(0,theCurrentLine.indexOf(";"));
                builder.append(temp);
                builder.append("\n");
            }
        } //end of while

    System.out.println(builder.toString());

    }


}
