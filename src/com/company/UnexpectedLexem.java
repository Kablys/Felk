package com.company;


public class UnexpectedLexem extends Exception {
    Integer wrongIndex;
    Lexer.Token wrongToken;
    Lexeme expectedLexem = null;
    public UnexpectedLexem(Lexeme expected, Integer index, Lexer.Token token){
        wrongToken = token;
        wrongIndex = index;
        expectedLexem = expected;

//        node.addChildren(new Node (new Lexer.Token(Lexeme.ERROR, "Error occurred here")));
    }

    public String getToken(){
        if (expectedLexem == null){
            return "at index: " + wrongIndex.toString() + " got " + wrongToken.toString();
        } else{
            return "at index: " + wrongIndex.toString() + " expected: " + expectedLexem.toString() + " got " + wrongToken.toString();
        }
    }
}