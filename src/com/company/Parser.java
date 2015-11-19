package com.company;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
    String output = "";
    static Node ast;
    static Integer nTok = 0;
    int skip = 0;
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
        ast = programParse(36);
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
            node.addChildren(functionParse(index+1));
            //return node;
        }
        else if (tokens.get(index).t == Lexeme.MAIN) {
            node.addChildren(mainParse(index));
        }
        else{
            return node;
        }
        return node;
    }

    public Node functionParse (Integer index){
        Node node = new Node (new Lexer.Token(Lexeme.FUNCTION, "<function>"));
        node.addChildren(new Node (tokens.get(index - 1)));
        if (tokens.get(index).t == Lexeme.IDENTIFIER){
            node.addChildren(new Node (tokens.get(index)));
            node.addChildren(paramParse(index + 1));
        }
        return node;
    }

    public Node paramParse (Integer index){
        Node node = new Node (new Lexer.Token(Lexeme.PARAMETER, "test"));
        if (tokens.get(index).t == Lexeme.LPAREN) {
            index++;
            while (tokens.get(index).t != Lexeme.RPAREN) {
                node.addChildren(typeParse(index));
                index++;
            }
        }
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
        while(tokens.get(index).t != Lexeme.RBRACKET) {
            if (arrayOfTypes.contains(tokens.get(index).t)) {
                node.addChildren(typeParse(index));
            }
            index++;
            if (tokens.get(index).t == Lexeme.COMMA){
                //System.out.println("Found comma at " + index);
                index++;
            }

        }
        //else{}
        return node;
    }

    public Node typeParse (int index){
        if (tokens.get(index+2).t == Lexeme.ASSIG){
            index = index+2;
            Node node = new Node(tokens.get(index));
            Node nodeInt = new Node(tokens.get(index-2));
            node.addChildren(nodeInt);
            nodeInt.addChildren(new Node(tokens.get(index-1)));
            node.addChildren(new Node(new Lexer.Token(Lexeme.EXPRESION,"Expresion")));
            return node;
        }else{
            Node node = new Node(tokens.get(index));
            index = index + 1;
            node.addChildren(new Node(tokens.get(index)));
            return node;
        }

    }

}
