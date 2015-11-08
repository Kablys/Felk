package com.company;

import java.io.BufferedReader;
//import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Lexer {

    private static String [] reservedKeyWords = {"int", "float", "string", "char", "bool", "void", "while", "for",
                                                "if", "else", "main", "systemOut", "systemIn", "true", "false",
                                                "return", "to", "downto"};


    public static class Token {
        public final Lexeme t;
        public final String c; // Papildoma informacija
        public Token(Lexeme t, String c) {
            this.t = t;
            this.c = c;
        }
        public String toString() {
            return String.format("%s %-10s %s" , "Lexeme", t, c );
        }
        public String toString2() {
            return String.format(c);
        }
    }

    public static String getNum(String s, int i) {
        int j = i;
        for( ; j < s.length(); ) {
            if(Character.isDigit(s.charAt(j))) {
                j++;
            } else if (s.charAt(j) == '.') {
                j++;
                for( ; j < s.length(); ) {
                    if (Character.isDigit(s.charAt(j))) {
                        j++;
                    }else{
                        return s.substring(i,j);
                    }
                }
            } else {
                return s.substring(i, j);
            }
        }
        return s.substring(i, j);
    }

    public static String getAtom(String s, int i) {
        int j = i;
        for( ; j < s.length(); ) {
            if(Character.isLetterOrDigit(s.charAt(j))) {
                j++;
            } else {
                return s.substring(i, j);
            }
        }
        return s.substring(i, j);
    }

    public static String getComment(String s, int i, Boolean multiline) {
        int j = i;
        if (multiline){
            for( ; j < s.length(); ) {
                if(s.charAt(j) == '*') {
                    if (s.charAt(j+1) == '/'){
                        return s.substring(i, j+2);
                    }
                    j++;
                } else {
                    j++;
                }
            }
            return s.substring(i, j);
        } else {
            for( ; j < s.length(); ) {
                if (s.charAt(j+1) == '\n'){
                    return s.substring(i, j+1);
                } else {
                    j++;
                }
            }
            return s.substring(i, j);
        }
    }
    public static String getString(String s, int i) {
        int j = i;
            for( ; j < s.length(); ) {
                //System.out.println("ENTERED STRING");
                if(s.charAt(j) == '"') {
                    return s.substring(i, j+1);
                } else if (s.charAt(j) == '\\') {
                    if (s.charAt(j + 1) == '"') {
                        System.out.println("SECOND ENTER STRING");
                        j += 2;
                    } else{
                        j++;
                    }
                }else{
                    j++;
                }
            }
            return s.substring(i, j);
    }

    public static List<Token> lex(String input) {
        List<Token> result = new ArrayList<Token>();
        for(int i = 0; i < input.length(); ) {
            switch(input.charAt(i)) {

                //                Double Symbols
                case '/':
                    if(input.charAt(i + 1) == '/') {
                        String comment = getComment(input, i, false);  //false = ne multiline
                        //result.add(new Token(Lexeme.SCOMMENT, comment));
                        i += comment.length();
                        break;
                    } else if(input.charAt(i + 1) == '*'){
                        String comment = getComment(input, i, true);  //true = multiline
                        if (comment.substring(comment.length() - 2).equals("*/")) {
                            //result.add(new Token(Lexeme.MCOMMENT, comment));
                            i += comment.length() + 1;
                            break;
                        }
                        result.add(new Token(Lexeme.EXCEPTION, "Comment is not closed"));
                        i += comment.length() + 1;
                        break;

                    } else {
                        result.add(new Token(Lexeme.DIVISION, "/"));
                        i++;
                        break;
                    }
                case '&':
                    if(input.charAt(i + 1) == '&') {
                        result.add(new Token(Lexeme.ANDOP, "&&"));
                        i += 2;
                        break;
                    } else{
                        if (Character.isWhitespace(input.charAt(i+1))){
                            result.add(new Token(Lexeme.EXCEPTION, "Undefined lexeme & "));
                            i++;
                            break;
                        } else{
                            result.add(new Token(Lexeme.EXCEPTION, "Undefined value after & " + '\'' + input.charAt(i + 1) + '\''));
                            i++;
                            break;
                        }
                    }
                case '|':
                    if(input.charAt(i + 1) == '|') {
                        result.add(new Token(Lexeme.OROP, "||"));
                        i += 2;
                        break;
                    } else{
                        if (Character.isWhitespace(input.charAt(i+1))){
                            result.add(new Token(Lexeme.EXCEPTION, "Undefined lexeme | "));
                            i++;
                            break;
                        } else{
                            result.add(new Token(Lexeme.EXCEPTION, "Undefined value after | " + '\'' + input.charAt(i + 1) + '\''));
                            i++;
                            break;
                        }
                    }
                case ':':
                    if(input.charAt(i + 1) == '=') {
                        result.add(new Token(Lexeme.ASSIG, ":="));
                        i += 2;
                        break;
                    } else{
                        if (Character.isWhitespace(input.charAt(i+1))){
                            result.add(new Token(Lexeme.EXCEPTION, "Undefined lexeme : "));
                            i++;
                            break;
                        } else{
                            result.add(new Token(Lexeme.EXCEPTION, "Undefined value after : " + '\'' + input.charAt(i + 1) + '\''));
                            i++;
                            break;
                        }
                    }
                case '=':
                    if(input.charAt(i + 1) == '=') {
                        result.add(new Token(Lexeme.EQUAL, "=="));
                        i += 2;
                        break;
                    } else {
                        if (Character.isWhitespace(input.charAt(i+1))){
                            result.add(new Token(Lexeme.EXCEPTION, "Undefined lexeme = "));
                            i++;
                            break;
                        } else{
                            result.add(new Token(Lexeme.EXCEPTION, "Undefined value after = " + '\'' + input.charAt(i + 1) + '\''));
                            i++;
                            break;
                        }
                    }
                case '!':
                    if(input.charAt(i + 1) == '=') {
                        result.add(new Token(Lexeme.NOTEQUAL, "!="));
                        i += 2;
                        break;
                    } else{
                        if (Character.isWhitespace(input.charAt(i+1))){
                            result.add(new Token(Lexeme.EXCEPTION, "Undefined lexeme ! "));
                            i++;
                            break;
                        } else{
                            result.add(new Token(Lexeme.EXCEPTION, "Undefined value after ! " + '\'' + input.charAt(i + 1) + '\''));
                            i++;
                            break;
                        }
                    }
                case '>':
                    if(input.charAt(i + 1) == '=') {
                        result.add(new Token(Lexeme.MOREEQUAL, ">="));
                        i += 2;
                        break;
                    } else{
                        result.add(new Token(Lexeme.MORE, ">"));
                        i++;
                        break;
                    }
                case '<':
                    if(input.charAt(i + 1) == '=') {
                        result.add(new Token(Lexeme.LESSEQUAL, "<="));
                        i += 2;
                        break;
                    } else{
                        result.add(new Token(Lexeme.LESS, "<"));
                        i++;
                        break;
                    }

//                Single Symbol

                case '"':
                    String string = getString(input, i+1);

                    if (string.substring(string.length() - 1).equals("\"")) {
                        result.add(new Token(Lexeme.STRING, '"' + string));
                        i += string.length() + 1;
                        break;
                    }
                    result.add(new Token(Lexeme.EXCEPTION, "String is not closed"));
                    i += string.length() + 1;
                    break;
                case '(':
                    result.add(new Token(Lexeme.LPAREN, "("));
                    i++;
                    break;
                case ')':
                    result.add(new Token(Lexeme.RPAREN, ")"));
                    i++;
                    break;
                case ';':
                    result.add(new Token(Lexeme.SEMICOLON, ";"));
                    i++;
                    break;
                case '[':
                    result.add(new Token(Lexeme.LBRACKET, "["));
                    i++;
                    break;
                case ']':
                    result.add(new Token(Lexeme.RBRACKET, "]"));
                    i++;
                    break;
                case '+':
                    result.add(new Token(Lexeme.PLUS, "+"));
                    i++;
                    break;
                case '-':
                    result.add(new Token(Lexeme.MINUS, "-"));
                    i++;
                    break;
                case '*':
                    result.add(new Token(Lexeme.MULTIPLY, "*"));
                    i++;
                    break;

                case '%':
                    result.add(new Token(Lexeme.MOD, "%"));
                    i++;
                    break;
                case ',':
                    result.add(new Token(Lexeme.COMMA, ","));
                    i++;
                    break;

//                    Text

                default:
                    boolean TYPO = false;
                    if(Character.isWhitespace(input.charAt(i))) {
                        i++;
                    } else if (Character.isLetter(input.charAt(i))){
                        String atom = getAtom(input, i);
                        for (String reservedKeyWord : reservedKeyWords) {
                            if (atom.equals(reservedKeyWord)) {
                                result.add(new Token(Lexeme.valueOf(reservedKeyWord.toUpperCase()), atom));
                                i += atom.length();
                                TYPO = true;
                            }
                        }
                        if(!TYPO) {
                            i += atom.length();
                            result.add(new Token(Lexeme.IDENTIFIER, atom));
                        }
                    } else if (Character.isDigit(input.charAt(i))){
                        String number = getNum(input, i);
                        if (number.indexOf('.') > -1){
                            result.add(new Token(Lexeme.FLOAT, number));
                        } else
                            result.add(new Token(Lexeme.NUMBER, number.replaceFirst("^0+(?!$)", "")));

                        i += number.length();
                    } else {
                        result.add(new Token(Lexeme.EXCEPTION, "Unknown lexem" + input.charAt(i)));

                        i++;
                    }
                    break;
            }
        }
        return result;
    }
    static Integer nTok = 0;
    public static Token getNextToken (List<Token> tokens){

        if (nTok < tokens.size()) {
            Token t = tokens.get(nTok);
            nTok++;
            return t;
        } else {
            nTok = -1;
            return tokens.get(tokens.size() - 1);
        }

    }
    public static void main(String[] args) throws IOException {
        if (args.length == 1){
            //System.out.println(args[0]);
            BufferedReader br = new BufferedReader(new FileReader(args[0]));
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                String everything = sb.toString();
                List<Token> tokens = lex(everything);
//                for(Token t : tokens) {
//                    System.out.println(t);
//                }
                //lexer print
//                Token tok = getNextToken(tokens);
//                while (nTok >= 0){
//                    System.out.println(tok);
//                    tok = getNextToken(tokens);
//                }
                //Parser parser = new Parser(tokens);
                Token rootToken = new Token(Lexeme.PROGRAM, "Root of the program");
                Node node = new Node (rootToken);

                Token rootToken2 = new Token(Lexeme.PROGRAM, "Root of the program2");
                Node node2 = new Node (rootToken2);
                Token rootToken3 = new Token(Lexeme.PROGRAM, "Root of the program3");
                Node node3 = new Node (rootToken3);
                Token rootToken4 = new Token(Lexeme.PROGRAM, "CHILED");
                Node node4 = new Node (rootToken4);

                node.addChildren(node2);
                node.addChildren(node3);
                node2.addChildren(node4);
                node.toXml(0);


            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                br.close();
            }

        }
    }
}
