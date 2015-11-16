package com.company;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
    static Node ast;
    static Integer nTok = 0;
    static List<Lexer.Token> tokens;
    //static Lexeme[] arrayOfTypes = {com.company.Lexeme.INT, com.company.Lexeme.FLOAT, com.company.Lexeme.STRING, com.company.Lexeme.CHAR, com.company.Lexeme.BOOL, com.company.Lexeme.VOID};
    List<Lexeme> arrayOfTypes = Arrays.asList(com.company.Lexeme.INT, com.company.Lexeme.FLOAT, com.company.Lexeme.STRING, com.company.Lexeme.CHAR, com.company.Lexeme.BOOL, com.company.Lexeme.VOID);
    //myList = {Lexeme.INT, Lexeme.FLOAT, Lexeme.STRING, Lexeme.CHAR, Lexeme.BOOL, Lexeme.VOID};
    public Parser(List<Lexer.Token> programsTokens) {
        tokens = programsTokens;
        for(Lexer.Token t : tokens) {
            System.out.println(t);
        }
        ast = programParse(getNextToken());
        ast.toXml(0);
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
        Lexer.Token rootToken = new Lexer.Token(Lexeme.PROGRAM, "The begining");
        Node node = new Node (rootToken);
        if (arrayOfTypes.contains(token.t)) {
            //node.addChildren(mainParse(getNextToken()));
            node.addChildren(new Node(token));
            return node;
        }
        else if (token.t == Lexeme.MAIN) {
            node.addChildren(new Node(new Lexer.Token(Lexeme.MAIN, "Programos mainas")));
            return node;
        }
        else{
            return node;
        }
    }
    public Node mainParse (Lexer.Token token){
        if (token.t == Lexeme.MAIN)

            return null;
        return null;
    }

}
