package com.company;

import java.util.List;

public class Parser {
    static Node ast;
    static Integer nTok = 0;
    static List<Lexer.Token> tokens;
    public Parser(List<Lexer.Token> ltokens) {
        tokens = ltokens;
        for(Lexer.Token t : tokens) {
            System.out.println(t);
        }
        ast = programParse(getNextToken());
    }

    public static Lexer.Token getNextToken () {

        if (nTok < tokens.size()) {
            Lexer.Token t = tokens.get(nTok);
            nTok++;
            return t;
        } else {
            nTok = -1;
            return tokens.get(tokens.size() - 1);
        }
    }


    public Node programParse (Lexer.Token token){
        Node node;
        if (token.t == Lexeme.MAIN)
            mainParse(getNextToken());
        return null;
    }
    public Node mainParse (Lexer.Token token){
        if (token.t == Lexeme.MAIN)

            return null;
        return null;
    }

}
