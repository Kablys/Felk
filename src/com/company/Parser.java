package com.company;

import java.util.List;

/**
 * Created by domas on 15.11.8.
 */
public class Parser {
    public Parser(List<Lexer.Token> tokens) {
        for(Lexer.Token t : tokens) {
            System.out.println(t);
        }
    }

}
