package com.company;

import java.io.BufferedReader;
//import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static enum Lexem {
        LPAREN, RPAREN, ATOM, SOMETHINGELSE
    }
    public static class Token {
        public final Lexem t;
        public final String c; // Papildoma informacija
        public Token(Lexem t, String c) {
            this.t = t;
            this.c = c;
        }
        public String toString() {
            switch (t) {
                case LPAREN:
                    return "Lexem: LPAREN, " + c;
                case RPAREN:
                    return "Lexem: RPAREN, " + c;
                case ATOM:
                    return "Lexem: ATOM, " + c;
                case SOMETHINGELSE:
                    return "Lexem: SOMETHINGELSE, " + c;
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
                    result.add(new Token(Lexem.LPAREN, "("));
                    i++;
                    break;
                case ')':
                    result.add(new Token(Lexem.RPAREN, ")"));
                    i++;
                    break;
                default:
                    if(Character.isWhitespace(input.charAt(i))) {
                        i++;
                    } else if (Character.isLetter(input.charAt(i))){
                        String atom = getAtom(input, i);
                        i += atom.length();
                        result.add(new Token(Lexem.ATOM, atom));
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
