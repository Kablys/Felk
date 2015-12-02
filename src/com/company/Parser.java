package com.company;

import java.util.Arrays;
import java.util.List;

public class Parser {
    String output = "";
    Integer numOfError = 0;
    static Node ast;
    static Integer nTok = 0;
    int numOfPairs = 0;
    static List<Lexer.Token> tokens;
    boolean changed = false;
    //static Lexeme[] arrayOfTypes = {com.company.Lexeme.INT, com.company.Lexeme.FLOAT, com.company.Lexeme.STRING, com.company.Lexeme.CHAR, com.company.Lexeme.BOOL, com.company.Lexeme.VOID};

    List<Lexeme> arrayOfTypes = Arrays.asList(com.company.Lexeme.INT, com.company.Lexeme.FLOAT, com.company.Lexeme.STRING,
            com.company.Lexeme.CHAR, com.company.Lexeme.BOOL, com.company.Lexeme.VOID);
    List<Lexeme> relatOp = Arrays.asList(com.company.Lexeme.EQUAL, com.company.Lexeme.LESS, com.company.Lexeme.MORE,
            com.company.Lexeme.NOTEQUAL, com.company.Lexeme.MOREEQUAL, com.company.Lexeme.LESSEQUAL);
    List<Lexeme> addOp = Arrays.asList(com.company.Lexeme.PLUS, com.company.Lexeme.MINUS, com.company.Lexeme.OROP);
    List<Lexeme> mulOp = Arrays.asList(com.company.Lexeme.MULTIPLY, com.company.Lexeme.DIVISION, com.company.Lexeme.MOD,
            com.company.Lexeme.ANDOP);

    //myList = {Lexeme.INT, Lexeme.FLOAT, Lexeme.STRING, Lexeme.CHAR, Lexeme.BOOL, Lexeme.VOID};
    public Parser(List<Lexer.Token> programsTokens) {
        tokens = programsTokens;
        int i = 0;
        for(Lexer.Token t : tokens) {
            System.out.println(i++ +" "+t);
        }
        try {
            ast = programParse(0);
            System.out.println("Baigiau Parseri");
            output = ast.toXml(0);
            ast.toXml2(output);
        }catch (UnexpectedLexem e){
            System.out.println("Bloga lexema: " + e.getToken());
            e.printStackTrace();
        }
        System.out.println("Number of errrors:" + numOfError.toString());

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

    public Node programParse (Integer index) throws UnexpectedLexem {
        Lexer.Token rootToken = new Lexer.Token(Lexeme.PROGRAM, "<program>");
        Node node = new Node (rootToken);
        while (arrayOfTypes.contains(tokens.get(index).t)){
            node.addChildren(functionParse(index + 1));
            index = nTok + 1;

        }
       if ((tokens.get(index).t) == Lexeme.MAIN) {
            System.out.println("Gavom main");
            node.addChildren(mainParse(index));
        } else {
           System.out.println("negavom main");
           throw new UnexpectedLexem(Lexeme.MAIN, index, tokens.get(index));
        }
        return node;
    }

    public Node functionParse (Integer index) throws UnexpectedLexem {
        Node node = new Node (new Lexer.Token(Lexeme.FUNCTION, "<function>"));
        node.addChildren(new Node (tokens.get(index - 1)));
        if (tokens.get(index).t == Lexeme.IDENTIFIER){
            node.addChildren(new Node (tokens.get(index)));
            node.addChildren(paramParse(index + 1));
            index = nTok + 1;
            node.addChildren(blockParse(index));
            index = nTok;
        } else {
            throw new UnexpectedLexem(Lexeme.IDENTIFIER, index, tokens.get(index));
        }
        nTok = index;
        return node;
    }

    public Node paramParse (Integer index) throws UnexpectedLexem {
        Node node = new Node (new Lexer.Token(Lexeme.PARAMETER, "test"));
        if (tokens.get(index).t == Lexeme.LPAREN) {
            index++;
            while (tokens.get(index).t != Lexeme.RPAREN) {
                node.addChildren(typeParse(index, numOfPairs));
                index= index+2;
                if(tokens.get(index).t == Lexeme.COMMA){
                    index++;
                }
            }
        } else {
            throw new UnexpectedLexem(Lexeme.LPAREN, index, tokens.get(index));
        }
        nTok= index;
        return node;
    }

    public Node mainParse (int index) throws UnexpectedLexem {
        Node node = new Node (tokens.get(index));
        node.addChildren(paramParse(index + 1));
        index = nTok;
        node.addChildren(blockParse(index));
        return node;
    }

    public Node blockParse (int index) throws UnexpectedLexem {
        Node node = new Node (new Lexer.Token(Lexeme.BLOCK, "Block"));
        index= index + 1;
        while(tokens.get(index).t != Lexeme.RBRACKET) {
            if (arrayOfTypes.contains(tokens.get(index).t)) {
                node.addChildren(typeParse(index,numOfPairs));
                index = nTok;
            }
            else if (tokens.get(index).t == Lexeme.FOR){
                node.addChildren(forBlock(index));
                index=nTok-1;
            }else if(tokens.get(index+1).t == Lexeme.ASSIG){
                Node assignNode = new Node(tokens.get(index+1));
                node.addChildren(assignNode);
                assignNode.addChildren(new Node(tokens.get(index)));
                assignNode.addChildren(expression(index+2,numOfPairs));
            }else if (tokens.get(index).t == Lexeme.WHILE){
                Node whileNode = new Node(tokens.get(index));
                node.addChildren(whileNode);
                whileNode.addChildren(expression(index+1,numOfPairs));
                index = nTok;
                whileNode.addChildren(blockParse(index));
                index = nTok;
            }else if(tokens.get(index).t == Lexeme.IF) {
                Node ifNode = new Node(tokens.get(index));
                node.addChildren(ifNode);
                ifNode.addChildren(expression(index + 1,numOfPairs));
                index = nTok;
                ifNode.addChildren(blockParse(index));
                index = nTok;
                if(tokens.get(index+1).t == Lexeme.ELSE){
                    //System.out.println("else");
                    Node elseNode = new Node(tokens.get(index+1));
                    ifNode.addChildren(elseNode);
                    elseNode.addChildren(blockParse(index+2));
                    index = nTok;
                }
            }else if(tokens.get(index).t == Lexeme.RETURN){
                Node reNode = new Node(tokens.get(index));
                node.addChildren(reNode);
                reNode.addChildren(expression(index + 1,numOfPairs));
                index = nTok;
            }else if(tokens.get(index).t == Lexeme.SYSTEMIN){
                Node systemInNode = new Node(tokens.get(index));
                node.addChildren(systemInNode);
                systemInNode.addChildren(expression(index+1,numOfPairs));
                index = nTok;
            }else if (tokens.get(index).t == Lexeme.SYSTEMOUT){
                Node systemOut = new Node(tokens.get(index));
                node.addChildren(systemOut);
                systemOut.addChildren(expression(index+1,numOfPairs));
                index = nTok+1;
            }else{
                numOfError++;
                System.out.print("Wrong: "); System.out.print(index); System.out.println(" " + tokens.get(index).toString());
                //throw new UnexpectedLexem(null, index, tokens.get(index));
            }
            index++;
        }
        nTok = index;
        //else{}
        return node;
    }

    /*public Node ifBlock(int index) throws UnexpectedLexem {
        Node node = new Node(tokens.get(index));
        if(tokens.get(index+1).t == Lexeme.LPAREN){
            Node expressionNode = new Node(tokens.get(index+1));
            node.addChildren(expressionNode);
                expressionNode.addChildren(expression(index));
            index = nTok;
            if(tokens.get(index).t == Lexeme.LPAREN){
                node.addChildren(blockParse(index));
            }

        }
        return node;
    }*/

    public Node forToNode(int index){
        Node node = new Node(tokens.get(index+1));
        index++;
        node.addChildren(new Node(tokens.get(index-1)));
        node.addChildren(new Node(tokens.get(index+1)));
        nTok = index+1;
        return node;
    }


    public Node forBlock(int index) throws UnexpectedLexem {
        Node node = new Node(tokens.get(index));
        if(tokens.get(index+2).t == Lexeme.ASSIG){
            index = index+2;
            Node forAssigNode = new Node(tokens.get(index));
            node.addChildren(forAssigNode);
            forAssigNode.addChildren(new Node(tokens.get(index-1)));
            forAssigNode.addChildren(forToNode(index+1));
            index = nTok+1;
            node.addChildren(blockParse(index));
            index = nTok+1;
        }
        nTok = index;
        return node;
    }



    public Node typeParse(int index, int elemnts) throws UnexpectedLexem {
        numOfPairs = elemnts;
        if (tokens.get(index+2).t == Lexeme.ASSIG){
            index = index+2;
            Node node = new Node(tokens.get(index)); // :=\
            Node nodeInt; // type
            if (arrayOfTypes.contains(tokens.get(index - 2).t)) {
                nodeInt = new Node(tokens.get(index - 2));// type
            }else {
                throw new UnexpectedLexem(Lexeme.TYPE, index, tokens.get(index));
            }
            node.addChildren(nodeInt);
            if (tokens.get(index - 1).t == Lexeme.IDENTIFIER) {
                nodeInt.addChildren(new Node(tokens.get(index - 1)));//dentifier
            }else {
                throw new UnexpectedLexem(Lexeme.TYPE, index, tokens.get(index));
            }
            node.addChildren(expression(index+1,numOfPairs));
            return node;
        }
        else{
            Node node;
            if (arrayOfTypes.contains(tokens.get(index).t)) {
                node = new Node(tokens.get(index));
            }else {
                throw new UnexpectedLexem(Lexeme.TYPE, index, tokens.get(index));
            }
            index = index + 1;
            if (tokens.get(index).t == Lexeme.IDENTIFIER) {
                node.addChildren(new Node(tokens.get(index)));
            }else {
                throw new UnexpectedLexem(Lexeme.IDENTIFIER, index, tokens.get(index));
            }
            nTok = index;
            return node;
        }
    }


    public Node expression (int index, int element) throws UnexpectedLexem {
        numOfPairs = element;
        Node node = new Node(new Lexer.Token(Lexeme.EXPRESSION, "Expression"));
        while((tokens.get(index).t != Lexeme.SEMICOLON)){
            if(tokens.get(index).t == Lexeme.LPAREN){
                numOfPairs++;
                index++;
                continue; //Compileris sako kad nebutinas
            }
            else if(tokens.get(index).t == Lexeme.RPAREN){
                numOfPairs--;
                if(numOfPairs < 0){
                    throw new UnexpectedLexem(null, index,tokens.get(index));
                }
                index++;
            }
            //int search= 0;
            else if(relatOp.contains(tokens.get(index+1).t)){
                node = new Node(tokens.get(index+1));
                node.addChildren(new Node(tokens.get(index)));
                node.addChildren((SimpleExpression1(index+2,numOfPairs)));
                break;
            }else if(relatOp.contains(tokens.get(index).t)){
                node = new Node(tokens.get(index));
                node.addChildren(SimpleExpression1(index+1, numOfPairs));
                break;
            }else if((tokens.get(index).t == Lexeme.NUMBER || tokens.get(index).t == Lexeme.FLOAT ||
                    tokens.get(index).t == Lexeme.STRING || tokens.get(index).t == Lexeme.IDENTIFIER )&&
                    tokens.get(index+1).t == Lexeme.SEMICOLON){
                node.addChildren(new Node(tokens.get(index)));
                index++;
                nTok = index;
                break;
            }else if((tokens.get(index).t == Lexeme.NUMBER || tokens.get(index).t == Lexeme.FLOAT ||
                    tokens.get(index).t == Lexeme.STRING || tokens.get(index).t == Lexeme.IDENTIFIER )&&
                    tokens.get(index+2).t == Lexeme.SEMICOLON){
                node.addChildren(new Node(tokens.get(index)));
                index++;
                nTok = index;
                break;
            }

            else{
                node.addChildren(SimpleExpression1(index, numOfPairs));
                break;
            }

        }
        return node;
    }

    public Node SimpleExpression1(int index, int element) throws UnexpectedLexem {
        numOfPairs = element;
//        Node node = new Node(new Lexer.Token(Lexeme.EXPRESSION, "Expression"));
        Node node = new Node(tokens.get(index));
//        while(tokens.get(index+1).t != Lexeme.SEMICOLON){
            if (relatOp.contains(tokens.get(index).t)){
                index++;
                node.addChildren(new Node(tokens.get(index)));
            }else if(relatOp.contains(tokens.get(index+1).t)) {
                index++;
                node.addChildren(expression(index, numOfPairs));
            }else if (addOp.contains(tokens.get(index + 1).t)) {
                if(tokens.get(index).t == Lexeme.RPAREN){
                    node = new Node(tokens.get(index));
                    Node newNode = new Node(tokens.get(index+1));
                    node.addChildren(newNode);
                    newNode.addChildren(addingNode(index+2, numOfPairs));
                }else {
                    node = new Node(tokens.get(index + 1));
                    node.addChildren(new Node(tokens.get(index)));
                    node.addChildren(addingNode(index + 2,numOfPairs));
//                break;
                }
            } else if (addOp.contains(tokens.get(index).t)) {
                node = new Node(tokens.get(index));
                node.addChildren(addingNode(index + 1, numOfPairs));
//                break;
            }else if(mulOp.contains(tokens.get(index+1).t)){
                if(tokens.get(index).t == Lexeme.RPAREN){
                    node = new Node(tokens.get(index));
                    Node newNode = new Node(tokens.get(index+1));
                    node.addChildren(newNode);
                    newNode.addChildren(addingNode(index+2, numOfPairs));
                }else {
                    index++;
                    node = new Node(tokens.get(index));
                    node.addChildren(addingNode(index - 1, numOfPairs));
                }
            }
            else if (tokens.get(index+1).t == Lexeme.LBRACKET){
                //node.addChildren(new Node(tokens.get(index)));
                nTok = index;
            }
            else {
                node.addChildren(addingNode(index, numOfPairs));
//                break;
            }
//        }
        return node;
    }

    public Node addingNode(int index, int elements) throws UnexpectedLexem {
        numOfPairs = elements;
        Node node = new Node(tokens.get(index));
        if(mulOp.contains(tokens.get(index+1).t)){
            if(tokens.get(index).t == Lexeme.RPAREN) {
                node = new Node(tokens.get(index));
                Node newNode = new Node(tokens.get(index + 1));
                node.addChildren(newNode);
                newNode.addChildren(mulNode(index + 2,numOfPairs));
            }else {
                index++;
                node = new Node(tokens.get(index));
                node.addChildren(new Node(tokens.get(index - 1)));
                node.addChildren(mulNode(index + 1, numOfPairs));
            }
        }else if(mulOp.contains(tokens.get(index).t)){
            node = new Node(tokens.get(index));
            node.addChildren(mulNode(index+1, numOfPairs));
        }
        else if(addOp.contains(tokens.get(index).t)){
            index++;
            node.addChildren(new Node(tokens.get(index)));
        }
        else if(addOp.contains(tokens.get(index+1).t)){
            index++;
            node.addChildren(expression(index,numOfPairs));
//            node.addChildren(SimpleExpression1(index));
        }
        else if (tokens.get(index+1).t == Lexeme.LBRACKET){
            //node.addChildren(new Node(tokens.get(index)));
            nTok = index;
        }
        else if(tokens.get(index+1).t != Lexeme.SEMICOLON){
            node.addChildren(expression(index+1,numOfPairs));
//            node.addChildren(SimpleExpression1(index+1));
            nTok = index;
        }
        else if(tokens.get(index+1).t == Lexeme.SEMICOLON){
            nTok = index;
        }
        else{
            nTok = index+1;
        }
        return node;
    }

    public Node mulNode(int index, int elements) throws UnexpectedLexem {
        numOfPairs = elements;
        Node node = new Node(tokens.get(index));
        if(tokens.get(index+1).t != Lexeme.SEMICOLON){
            node.addChildren(expression(index+1,numOfPairs));
//            node.addChildren(SimpleExpression1(index+1));
        }
        nTok = index;
        return node;
    }

    public Node functionCall(int index){
        Node node = new Node(tokens.get(index));

        return node;
    }


}