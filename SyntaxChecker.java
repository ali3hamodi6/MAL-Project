import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SyntaxChecker {
    Scanner firstScanner;
    String[] opCode = {"MOVE", "MOVEI", "MUL", "BR", "BGT", "BLT", "BEQ", "ADD", "SUB", "INC", "DEC", "DIV", "END"};

    public SyntaxChecker(Scanner fileScanner) {
        this.firstScanner = fileScanner;


        commentsAndBlankLinesStripper();
    }

    // #####################################################################
    private void commentsAndBlankLinesStripper() {

        StringBuilder builder = new StringBuilder();
        String temp = "";

        while (firstScanner.hasNextLine()) {
            String theCurrentLine = firstScanner.nextLine();

            if (theCurrentLine.startsWith(";")) {
                continue;

            } else if (theCurrentLine.trim().length() == 0) {
                continue;

            } else if (theCurrentLine.contains(";")) {
                temp = theCurrentLine.substring(0, theCurrentLine.indexOf(";"));
                builder.append(temp);
                builder.append("\n");
            } else {
                builder.append(theCurrentLine);
                builder.append("\n");
            }
        } //end of while

        System.out.println(builder.toString());
        malLogicalParser(builder.toString());
    }
    // #####################################################################

    enum Flag {INTIAL_STATE, HAS_LABEL, INSTRUCTION_ONLY}

    // #####################################################################
    private void malLogicalParser(String trimmed) {

        try {
            Scanner scan = new Scanner(trimmed);
            PrintWriter pw = new PrintWriter("output.txt");
            int counter = 1;
            Flag flag = Flag.INTIAL_STATE;


            while (scan.hasNextLine()) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(scan.nextLine());
                stringBuilder.append("\n");


                String currentLine = stringBuilder.toString();
                String remaining = "";
                String extractedOpCode = "";
                String extractedLabel = "";
                String OperandOne = "";
                String OperandTwo = "";
                String OperandThree = "";
                String illegalOperand = "";

                List<String> list = new ArrayList<>();

                pw.print("" + counter + " ");
                counter++;
                pw.print(currentLine);

                // handle the case when a label exist alone on one line
                if (currentLine.trim().endsWith(":")) {
                    boolean status = validLabel(currentLine.substring(0, currentLine.trim().indexOf(":")).trim());
                    if (status) {
                        continue;
                    } else {
                        pw.println("Ill-formed label, please make sure that the label contains only letters and " +
                                "doesn't exceed 5 letters ");
                        continue;
                    }
                } else if (currentLine.contains(":")) {
                    extractedLabel = currentLine.trim().substring(0, currentLine.trim().indexOf(":"));
                    boolean status = validLabel(extractedLabel);
                    if (status) {
                        System.out.print("");
                    } else {
                        pw.println("Ill-formed label, please make sure that the label contains only letters and " +
                                "doesn't exceed 5 letters ");
                        continue;
                    }

                }

                if (currentLine.trim().equals("END")) {
                    continue;
                }

                //Parsing opCode
                if (currentLine.contains(":")) {
                    remaining = currentLine.substring(currentLine.indexOf(":") + 1);
                    extractedOpCode = remaining.trim().substring(0, remaining.trim().indexOf(" "));

                    list.add(extractedOpCode);

                } else {
                    extractedOpCode = currentLine.trim().substring(0, currentLine.trim().indexOf(" ") + 1);
                    list.add(extractedOpCode);
                }

                // parsing Operands
                // if no comma exists after the operand
                if (!currentLine.contains(",")) {
                    OperandOne = currentLine.substring(currentLine.indexOf(extractedOpCode) + extractedOpCode.length(), currentLine.indexOf("\n")).trim();
                    list.add(OperandOne);

                    // if only one comma exists
                } else if (!currentLine.substring(currentLine.indexOf(",") + 1).contains(",")) {
                    OperandOne = currentLine.substring(currentLine.indexOf(extractedOpCode) + extractedOpCode.length(), currentLine.indexOf(",")).trim();
                    OperandTwo = currentLine.substring(currentLine.indexOf(",") + 1, currentLine.indexOf("\n")).trim();
                    list.add(OperandOne);
                    list.add(OperandTwo);

                    // two or more commas exist
                } else {
                    int firstComma = currentLine.indexOf(",");
                    int secondComma = currentLine.indexOf(",", firstComma + 1); // anything is more than 2 commas is not allowed in MAL
                    int thirdComma = currentLine.indexOf(",", secondComma + 1); // shouldn't exist in MAL

                    // only 3 operands exist
                    if (thirdComma == -1) { //index method return -1 when the value not found
                        OperandOne = currentLine.substring(currentLine.indexOf(extractedOpCode) + extractedOpCode.length(), firstComma).trim();
                        OperandTwo = currentLine.substring(firstComma + 1, secondComma).trim();
                        OperandThree = currentLine.substring(secondComma + 1).trim();

                        list.add(OperandOne);
                        list.add(OperandTwo);
                        list.add(OperandThree);

                        // more than 3 operands exist
                    } else {
                        OperandOne = currentLine.substring(currentLine.indexOf(extractedOpCode) + extractedOpCode.length(), firstComma).trim();
                        OperandTwo = currentLine.substring(firstComma + 1, secondComma).trim();
                        OperandThree = currentLine.substring(secondComma + 1, thirdComma).trim();
                        illegalOperand = currentLine.substring(thirdComma + 1).trim();

                        list.add(OperandOne);
                        list.add(OperandTwo);
                        list.add(OperandThree);
                        list.add(illegalOperand);
                    }
                }

                System.out.println(list.toString());

                // VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
                if (!validOpcode(list.get(0))) {
                    String suggestion = opCodeSuggestions(list.get(0));
                    pw.println("Invalid opCode, probably you meant " + suggestion);
                    continue;
                }
                // VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
                // this switch statement is to test for the number oof allowed operands for each opCode
                String str = "";
                switch (list.get(0).trim()) {
                    case "BLT":
                    case "BGT":
                    case "BEQ":
                    case "ADD":
                    case "SUB":
                    case "MUL":
                    case "DIV":

                        if (list.size() == 4) {
                            break;
                        } else if (list.size() > 4) {
                            str = "There are too many operands for  " + list.get(0) + ", expected 3 operands";
                        } else {
                            str = "There are too few operands for  " + list.get(0) + ", expected 3 operands";
                        }
                        break;
                    case "MOVE":
                    case "MOVEI":
                        if (list.size() == 3) {
                            break;
                        } else if (list.size() > 3) {
                            str = "There are too many operands for  " + list.get(0) + ", expected 2 operands";
                        } else {
                            str = "There are too few operands for  " + list.get(0) + ", expected 2 operands";
                        }
                        break;
                    case "BR":
                    case "INC":
                    case "DEC":
                        if (list.size() == 2) {
                            break;
                        } else if (list.size() > 2) {
                            str = "There are too many operands for  " + list.get(0) + ", expected 1 operands";
                        } else {
                            str = "There are too few operands for  " + list.get(0) + ", expected 1 operands";
                        }
                }// end of switch
                pw.println(str);


                String operandItem = "";
                for (int i = 1; i < list.size(); i++) {
                    String item = list.get(i);
                    boolean aNum = true;
                    boolean hasdigit = false;

                    for (char ch : item.toCharArray()) {
                        if (Character.isLetter(ch)) {
                            aNum = false;
                            break;
                        }
                    }

                    if (item.length() > 5 && !aNum) {
                        operandItem = "Ill-formed operand, the length of the operand can't exceed 5 letters";
                        continue;
                    }

                    if (aNum){
                        for (Character ch : item.toCharArray()) {
                            int x = Integer.parseInt(ch.toString());
                            if (x ==8 || x==9) {
                                operandItem = "Ill-formed operand, this is not octal.";
                            }
                        }
                    }
                    for (char ch : item.toCharArray()) {
                        if (Character.isDigit(ch)) {
                            hasdigit =  true;
                            break;
                        }
                    }
                    if (hasdigit && !aNum && !item.equals("R0") && !item.equals("R1")&& !item.equals("R2")
                            && !item.equals("R3") && !item.equals("R4") && !item.equals("R5") && !item.equals("R6")
                            && !item.equals("R7")) {
                        operandItem = "Ill-formed operand, only letters are allowed to be used.";
                        continue;
                    }


                }// end of for
                pw.println(operandItem);




            }// end of while
            pw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    // #####################################################################

    // #####################################################################

    private String opCodeSuggestions(String remaining) {

        if (remaining.trim().startsWith("B") && remaining.contains(","))
            return "BGT or BLT or BEQ";

        if (remaining.trim().startsWith("B") && !remaining.contains(","))
            return "BR";

        if (remaining.trim().startsWith("M"))
            return "MOVE or MOVEI or MUL";

        if (remaining.trim().startsWith("A"))
            return "ADD";

        if (remaining.trim().startsWith("S"))
            return "SUB";

        if (remaining.trim().startsWith("I"))
            return "ADD";

        if (remaining.trim().startsWith("D") && remaining.contains(","))
            return "DEV";

        if (remaining.trim().startsWith("D") && !remaining.contains(","))
            return "DIC";

        if (remaining.trim().startsWith("E"))
            return "END";

        return "something else";

    }

    // #####################################################################
    private boolean validLabel(String currentLine) {
        boolean state = true;

        // String label = currentLine.trim().substring(0, currentLine.trim().indexOf(":"));

        String label = currentLine;
        if (label.length() > 5) {
            return false;
        }

        for (char ch : label.toCharArray()) {
            if (!Character.isLetter(ch)) {
                state = false;
                break;
            }
        }// end of for
        return state;
    }

    // #####################################################################
    private boolean validOpcode(String str) {

        for (String singleOpCode : opCode) {

            if (str.contains(singleOpCode))
                return true;
        }// end of for

        return false;
    }

}