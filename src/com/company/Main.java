package com.company;

import java.io.BufferedReader;
//import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class Main {

    public static class Token {
        public final Lexeme t;
        public final String c; // Papildoma informacija
        public Token(Lexeme t, String c) {
            this.t = t;
            this.c = c;
        }
        public String toString() {
            switch (t) {
                case LPAREN:
                    return "Lexeme: LPAREN, " + c;
                case RPAREN:
                    return "Lexeme: RPAREN, " + c;
                case LBRACKET:
                    return "Lexeme: LBRACKET, " + c;
                case RBRACKET:
                    return "Lexeme: RBRACKET, " + c;
                case ATOM:
                    return "Lexeme: ATOM, " + c;
                case SEMICOLON:
                    return "Lexeme: SEMICOLON, " + c;
                case PLUS:
                    return "Lexeme: PLUS, " + c;
                case MINUS:
                    return "Lexeme: MINUS, " + c;
                case MULTIPLICATION:
                    return "Lexeme: MULTIPLICATION, " + c;
                case DIVISION:
                    return "Lexeme: DIVISION, " + c;
                case MOD:
                    return "Lexeme: MOD, " + c;
                case NEGATIVE:
                    return "Lexeme: NEGATIVE, " + c;
                case INT:
                    return "Lexeme: INT, " + c;
                case FLOAT:
                    return "Lexeme: FLOAT, " + c;
                case STRING:
                    return "Lexeme: STRING, " + c;
                case CHAR:
                    return "Lexeme: CHAR, " + c;
                case BOOL:
                    return "Lexeme: BOOL, " + c;
                case VOID:
                    return "Lexeme: VOID, " + c;
                default:
                    return t.toString();


            }
        }
    }

    public static String getAtom(String s, int i) {
        int j = i;
        for( ; j < s.length(); ) {
            if(Character.isLetter(s.charAt(j))) {
                j++;
            } else {
                return s.substring(i, j);
            }
        }
        return s.substring(i, j);
    }

    public static List<Token> lex(String input) {
        List<Token> result = new ArrayList<Token>();
        for(int i = 0; i < input.length(); ) {
            switch(input.charAt(i)) {
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
                case '&':
                    if(input.charAt(i + 1) == '&') {
                        result.add(new Token(Lexeme.ANDOP, "&&"));
                        i += 2;
                        break;
                    } else{
                        result.add(new Token(Lexeme.EXEPTION, "Undefined value after &: " + input.charAt(i + 1)));
                        i++;
                        break;
                    }
                default:
                    if(Character.isWhitespace(input.charAt(i))) {
                        i++;
                    } else if (Character.isLetter(input.charAt(i))){
                        String atom = getAtom(input, i);
                        i += atom.length();
                        result.add(new Token(Lexeme.ATOM, atom));
                    } else {
                        i++;
                    }
                    break;
            }
        }
        return result;
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
                for(Token t : tokens) {
                    System.out.println(t);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                br.close();
            }

        }
    }
}
