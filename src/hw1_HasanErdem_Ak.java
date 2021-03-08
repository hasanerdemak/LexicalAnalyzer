/*
    Simple Lexical Analyzer program for a case insensitive, Pascal-like and fictional language, called EtuLang
    Written by Hasan Erdem AK
 */

import java.io.*;
import java.util.*;

public class hw1_HasanErdem_Ak {
    static Hashtable<String, String> table;
    static File inFile, outFile;
    static PrintWriter pw;

    public static void main(String[] args) {

        //Create a hash table to hold token and lexeme values
        table = new Hashtable<>();
        table.put("(", "LPARANT");
        table.put(")", "RPARANT");
        table.put(":=", "ASSIGNM");
        table.put("+", "ADD");
        table.put("-", "SUBT");
        table.put("/", "DIV");
        table.put("*", "MULT");
        table.put(";", "SEMICOLON");
        table.put("<>", "NOTEQ");
        table.put(">", "GREATER");
        table.put("<", "LESS");
        table.put(">=", "GRE_EQ");
        table.put("<=", "LESS_EQ");
        table.put(",", "COMMA");
        table.put(":", "COLON");
        table.put("begin", "RES_WORD");
        table.put("end", "RES_WORD");
        table.put("if", "RES_WORD");
        table.put("then", "RES_WORD");
        table.put("else", "RES_WORD");
        table.put("while", "RES_WORD");
        table.put("program", "RES_WORD");
        table.put("integer", "RES_WORD");
        table.put("var", "RES_WORD");
        table.put("end of file", "EOF");

        try {
            if (args[0].isEmpty() || args[1].isEmpty()) // Check if arguments are provided
                System.out.println("Gecersiz arguman");
            else
                parser(args[0], args[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void parser(String in, String out) throws IOException { // Takes input and output files names.
        // Parses into lexems
        String str = "";
        int a;
        inFile = new File(in); //"sample1.ETU"
        FileReader fr = new FileReader(inFile);
        BufferedReader br = new BufferedReader(fr);

        outFile = new File(out); //"output1.txt"
        pw = new PrintWriter(new FileWriter(out));

        while ((a = br.read()) != -1) { // read to the end of the file
            if (a == '%') { //Ignoring comments
                while ((char) br.read() != '%'); //read to the next '%'
            } else if (table.containsKey("" + (char) a)) { // If the character is in table, then control if it is in the table with the next character
                if (!str.isEmpty()) // before do it, check if str has any character
                    analyzer(str);
                str = (a > 32) ? "" + (char) a : ""; // add "a" to str if "a" is not a space or an escape character
                a = br.read();
                if (a == '%') //Ignoring comments
                    while ((char) br.read() != '%'); //read to the next '%'
                else {
                    if (table.containsKey("" + (char) a) || a == '=') { // Control equality operator separately because it is not included in the table alone
                        if (!table.containsKey(str + (char) a)) {
                            analyzer(str);
                            str = (a > 32) ? "" + (char) a : "";
                        } else {
                            str = (a > 32) ? str + (char) a : str;
                            analyzer(str);
                            str = "";
                        }
                    } else {
                        analyzer(str);
                        str = (a > 32) ? "" + (char) a : "";
                    }
                }
            } else if (!(isAlphaNum((char) a) || isUnknownToken((char) a))) { // Check if lexeme is over. (The "if"s above also check this)
                if (!str.isEmpty()) {
                    analyzer(str);
                    str = "";
                }
            } else str = (a > 32) ? str + (char) a : str; // Else, add "a" to str
        }
        analyzer(str);
        analyzer("end of file");

        fr.close();
        br.close();
        pw.close();
    }

    public static void analyzer(String str) { // Checks which token the Lexemes belong to
        if (!str.isEmpty()) {
            if (table.containsKey(str.toLowerCase()))
                printer(table.get(str.toLowerCase()), str);
            else if (isIdentifier(str))
                printer("identifier", str);
            else if (isIntegerConstant(str))
                printer("INT_LIT", str);
            else
                printer("UNKNOWN", str);
        }
    }

    public static boolean isAlphaNum(char a) { // Checks if character is alphanum
        return (a >= 'a' && a <= 'z') || (a >= 'A' && a <= 'Z') || (a >= '0' && a <= '9');
    }

    public static boolean isUnknownToken(char a) { // Checks if character is unknown token which is not in table
        return !table.containsKey("" + a) && a > 32 && !isAlphaNum(a);
    }

    public static boolean isIntegerConstant(String intConst) { // Checks if word is integer constant
        for (int i = 0; i < intConst.length(); i++) {
            char num = intConst.charAt(i);
            if (!(num >= '0' && num <= '9'))
                return false;
        }
        return true;
    }

    public static boolean isIdentifier(String idntfr) { // Checks if word is Identifier
        if (idntfr.length()>15)
            return false;

        char first = idntfr.charAt(0);
        if (!(first >= '0' && first <= '9')) { //Cannot begin with a number
            for (int i = 0; i < idntfr.length(); i++) {
                if (!isAlphaNum(idntfr.charAt(i)))
                    return false;
            }
            return true;
        }
        return false;
    }

    public static void printer(String token, String lexeme) { // Writes the analyzes to the output file
        String tokenStr = "Next token is " + token;
        String lexemeStr = "Next lexeme is " + lexeme + "\n";

        char[] Str= new char[30];
        for (int i = 0; i < 30; i++) //put space character for alignment
            Str[i] = ' ';
        for (int i = 0; i < tokenStr.length(); i++)
            Str[i] = tokenStr.charAt(i);

        pw.write(Str);
        pw.write(lexemeStr);
    }

}