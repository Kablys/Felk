package com.company;


public class UnexpectedLexem extends Exception {
    Integer wrongIndex;
    Lexer.Token wrongToken;
    public UnexpectedLexem(Integer index, Lexer.Token token){
        wrongToken = token;
        wrongIndex = index;
//        node.addChildren(new Node (new Lexer.Token(Lexeme.ERROR, "Error occurred here")));
    }

    public String getToken(){
        return wrongToken.toString() + " at: " + wrongIndex.toString();
    }
}