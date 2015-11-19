package com.company;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
    String output = "";
    static Node ast;
    static Integer nTok = 0;
    static List<Lexer.Token> tokens;
    //static Lexeme[] arrayOfTypes = {com.company.Lexeme.INT, com.company.Lexeme.FLOAT, com.company.Lexeme.STRING, com.company.Lexeme.CHAR, com.company.Lexeme.BOOL, com.company.Lexeme.VOID};
    List<Lexeme> arrayOfTypes = Arrays.asList(com.company.Lexeme.INT, com.company.Lexeme.FLOAT, com.company.Lexeme.STRING,
            com.company.Lexeme.CHAR, com.company.Lexeme.BOOL, com.company.Lexeme.VOID);
    //myList = {Lexeme.INT, Lexeme.FLOAT, Lexeme.STRING, Lexeme.CHAR, Lexeme.BOOL, Lexeme.VOID};
    public Parser(List<Lexer.Token> programsTokens) {
        tokens = programsTokens;
        int i = 0;
        for(Lexer.Token t : tokens) {
            System.out.println(i++ +" "+t);
        }
        ast = programParse(33);
        output = ast.toXml(0);
        ast.toXml2(output);
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


    public Node programParse (Integer index){

        Lexer.Token rootToken = new Lexer.Token(Lexeme.PROGRAM, "<program>");
        Node node = new Node (rootToken);
        if (arrayOfTypes.contains(tokens.get(index).t)) {
            node.addChildren(new Node(tokens.get(index)));
            return node;
        }
        else if (tokens.get(index).t == Lexeme.MAIN) {
            node.addChildren(mainParse(index));
            return node;
        }
        else{
            return node;
        }
    }

    public Node functionParse (Lexer.Token token){
        Node node = new Node (new Lexer.Token(Lexeme.MAIN, "Mainas"));
        return node;
    }

    public Node mainParse (int index){
        Node node = new Node (tokens.get(index));
        node.addChildren(blockParse(index+2));
        return node;
    }

    public Node blockParse (int index){
        Node node = new Node (new Lexer.Token(Lexeme.BLOCK, "Block"));
        index= index + 2;
        System.out.println(index);
        if (arrayOfTypes.contains(tokens.get(index).t)){
            node.addChildren(typeParse(index));
        }
        else{}
        return node;
    }

    public Node typeParse (int index){
        Node node = new Node (tokens.get(index));
        index = index + 1;
        node.addChildren(new Node (tokens.get(index)));
        return node;
    }

}
