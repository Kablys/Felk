package com.company;

import java.util.Arrays;
import java.util.List;

public class Parser {
    String output = "";
    Integer numOfError = 0;
    static Node ast;
    static Integer nTok = 0;
    int numOfPairs = 0;
    int numOfPairsBracket = 0;
    int BracketCount[] = new int[15];
    int counter = 1;
    static List<Lexer.Token> tokens;
    boolean changed = false;
    boolean functionCheck = false;
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
//            System.out.println("Baigiau Parseri");
            output = ast.toXml(0);
            ast.toXml2(output);
        }catch (UnexpectedLexem e){
            System.out.println("Bloga lexema: " + e.getToken());
            e.printStackTrace();
        }
        System.out.println("Number of errors:" + numOfError.toString());

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
        BracketCount[0] = 0;
        Lexer.Token rootToken = new Lexer.Token(Lexeme.PROGRAM, "<program>");
        Node node = new Node (rootToken);
        while (arrayOfTypes.contains(tokens.get(index).t)){
            node.addChildren(functionParse(index + 1));
            index = nTok + 1;
        }
        if ((tokens.get(index).t) == Lexeme.MAIN) {
            System.out.println("Gavom main");
            node.addChildren(mainParse(index));
        }else {
           System.out.println("negavom main");
           throw new UnexpectedLexem(Lexeme.MAIN, index, tokens.get(index));
        }

        index = nTok;
        if (index+1 == tokens.size()){
            return node;

        }else {
            System.out.println("Klaida: Po main bloku dar yra kodo (" + (tokens.size() - (index+1)) + " lexemos)");
            return node;

        }
    }

    public Node functionParse (Integer index) throws UnexpectedLexem {
        Node node = new Node (new Lexer.Token(Lexeme.FUNCTION, "<function>"));
        node.addChildren(new Node (tokens.get(index - 1)));
        if (tokens.get(index).t == Lexeme.IDENTIFIER){
            node.addChildren(new Node (tokens.get(index)));
            node.addChildren(paramParse(index + 1));
            index = nTok;
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
//            if (arrayOfTypes.contains(tokens.get(index).t)) {
//                node.addChildren(typeParse(index, numOfPairs));
//                index += 2;
//                if (tokens.get(index).t == Lexeme.RPAREN) {
//                    index++;
//                } else {
//                    throw new UnexpectedLexem(Lexeme.RPAREN, index, tokens.get(index));
//                }
//            }else if (tokens.get(index).t == Lexeme.RPAREN) {
//                index++;
//            }
//            else{
//                throw new UnexpectedLexem(Lexeme.TYPE, index, tokens.get(index));
//            }


//            senas kodas
            while (tokens.get(index).t != Lexeme.RPAREN) {
                node.addChildren(typeParse(index, numOfPairs));
                index= index+2;
                if(tokens.get(index).t == Lexeme.COMMA) {
                    index++;
                    //System.out.println(tokens.get(index).t);
                    if(!arrayOfTypes.contains(tokens.get(index).t)){
                        System.out.println(tokens.get(index).t);
                        numOfError++;
                        throw new UnexpectedLexem(Lexeme.TYPE, index, tokens.get(index));
                    }
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
            if(tokens.get(index).t == Lexeme.LBRACKET){
                numOfPairsBracket++;
                BracketCount[counter] = index;
                index++;
                counter++;
                continue;
            }else if (arrayOfTypes.contains(tokens.get(index).t)) {
                node.addChildren(typeParse(index,numOfPairs));
                index = nTok;
            } else if (tokens.get(index).t == Lexeme.FOR){
                node.addChildren(forBlock(index));
                index=nTok-1;
            }else if(tokens.get(index+1).t == Lexeme.ASSIG){
                Node assignNode = new Node(tokens.get(index+1));
                node.addChildren(assignNode);
                if (tokens.get(index).t == Lexeme.IDENTIFIER) {
                    assignNode.addChildren(new Node(tokens.get(index)));
                    assignNode.addChildren(expression(index + 2, numOfPairs));
                    index = nTok;
                } else {
                    throw new UnexpectedLexem(Lexeme.IDENTIFIER, index, tokens.get(index));
                }
            }

            else if (tokens.get(index).t == Lexeme.IDENTIFIER){
                if(tokens.get(index+1).t == Lexeme.LPAREN){
                    node.addChildren(functionCall(index,0));
                    index = nTok;
                }else
                    throw new UnexpectedLexem(Lexeme.LPAREN, index+1, tokens.get(index+1));
            }

            else if (tokens.get(index).t == Lexeme.WHILE){
                Node whileNode = new Node(tokens.get(index));
                node.addChildren(whileNode);
                whileNode.addChildren(expression(index+1,numOfPairs));
                index = nTok+1;
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
                index++;
                if (tokens.get(index).t == Lexeme.LPAREN) {
                    index++;
                    if (tokens.get(index).t == Lexeme.IDENTIFIER) {
                        systemInNode.addChildren(new Node(tokens.get(index)));
                        index++;
                        if (tokens.get(index).t == Lexeme.RPAREN) {
                            index++;
                        } else {
                            throw new UnexpectedLexem(Lexeme.RPAREN, index, tokens.get(index));
                        }
                    } else {
                        throw new UnexpectedLexem(Lexeme.IDENTIFIER, index, tokens.get(index));
                    }
                } else {
                    throw new UnexpectedLexem(Lexeme.LPAREN, index, tokens.get(index));
                }
                //senas kodas systemInNode.addChildren(expression(index+1,numOfPairs));
                //index = nTok;
            }else if (tokens.get(index).t == Lexeme.SYSTEMOUT){
                Node systemOut = new Node(tokens.get(index));
                node.addChildren(systemOut);
                systemOut.addChildren(expression(index+1,numOfPairs));
                index = nTok;//buvo +1
            }
            else if(tokens.get(index).t == Lexeme.SEMICOLON){ // nezinau kam sito reikai jei gale idejom index++
                //index++;
            }
            else{
                numOfError++;
                System.out.print("Wrong: "); System.out.print(index); System.out.println(" " + tokens.get(index).toString());
                //throw new UnexpectedLexem(null, index, tokens.get(index));
            }
            index++; // atsakingas uz ;
        }
        if(tokens.get(index).t == Lexeme.RBRACKET){
            if(numOfPairsBracket >= 0) {
                numOfPairsBracket--;
            }
            if(counter > 0) {
                counter--;
            }
        }
        if(numOfPairsBracket > 0){
            numOfError++;
            throw new UnexpectedLexem(null,BracketCount[counter],tokens.get(BracketCount[counter]));
        }

        /*if(numOfPairsBracket < 0 ){
                System.out.println("okay");
                throw new UnexpectedLexem(BracketCount[counter], tokens.get(BracketCount[counter]));

        }*/
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

    public Node forToNode(int index) throws UnexpectedLexem {
        index++;

        if(tokens.get(index).t == Lexeme.TO) {
            Node node = new Node(tokens.get(index));
            if(tokens.get(index-1).t == Lexeme.IDENTIFIER) {
                node.addChildren(new Node(tokens.get(index - 1)));
                if(tokens.get(index-1).t == Lexeme.IDENTIFIER) {
                    node.addChildren(new Node(tokens.get(index + 1)));
                    nTok = index + 1;
                    return node;
                }else {
                    throw new UnexpectedLexem(Lexeme.IDENTIFIER, index, tokens.get(index-1));
                }
            }else{
                throw new UnexpectedLexem(Lexeme.IDENTIFIER, index, tokens.get(index-1));
            }
        }else{
            throw new UnexpectedLexem(Lexeme.TO, index, tokens.get(index));
        }
    }


    public Node forBlock(int index) throws UnexpectedLexem {
        Node node = new Node(tokens.get(index));
        if(tokens.get(index+2).t == Lexeme.ASSIG){
            index = index+2;
            Node forAssigNode = new Node(tokens.get(index));
            node.addChildren(forAssigNode);
            if(tokens.get(index-1).t == Lexeme.IDENTIFIER){
                forAssigNode.addChildren(new Node(tokens.get(index-1)));
                if(tokens.get(index+1).t == Lexeme.IDENTIFIER) {
                    forAssigNode.addChildren(forToNode(index + 1));
                    index = nTok + 1;
                    node.addChildren(blockParse(index));
                    index = nTok + 1;
                }else {
                    throw new UnexpectedLexem(Lexeme.IDENTIFIER, index+1, tokens.get(index+1));
                }
            }else {
                throw new UnexpectedLexem(Lexeme.IDENTIFIER, index-1, tokens.get(index-1));

            }

        }else{
            throw new UnexpectedLexem(Lexeme.ASSIG, index+2, tokens.get(index+2));
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
                numOfError++;
                throw new UnexpectedLexem(Lexeme.TYPE, index, tokens.get(index));
            }
            node.addChildren(expression(index+1,numOfPairs));//maybe needs change 2
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
            //nTok = index;
            if(tokens.get(index+1).t == Lexeme.SEMICOLON){
                index = index+1;
            }
            nTok = index;
            return node;
        }
    }


    public Node expression (int index, int element) throws UnexpectedLexem {
        numOfPairs = element;
        if((tokens.get(index).t != Lexeme.NUMBER) && (tokens.get(index).t != Lexeme.STRING) &&  (tokens.get(index).t != Lexeme.FLOAT)
                && (tokens.get(index).t != Lexeme.IDENTIFIER) && (tokens.get(index).t != Lexeme.LPAREN) &&
                (tokens.get(index).t != Lexeme.RPAREN) && (tokens.get(index).t != Lexeme.SEMICOLON) && !addOp.contains(tokens.get(index).t)
                && !mulOp.contains(tokens.get(index).t) && !relatOp.contains((tokens.get(index).t))&& (tokens.get(index).t != Lexeme.COMMA)){
            throw new UnexpectedLexem(null, index,tokens.get(index));
        }
        Node node = new Node(new Lexer.Token(Lexeme.EXPRESSION, "Expression"));
        while(true){
            if(tokens.get(index).t == Lexeme.IDENTIFIER){
                if (tokens.get(index+1).t == Lexeme.LPAREN){
                    functionCheck = true;
                    node.addChildren(functionCall(index,numOfPairs));
                    functionCheck = false;
                    index = nTok;
                    return node;

                }
            }
            if(tokens.get(index).t == Lexeme.LPAREN){
                numOfPairs++;
                index++;
                continue;
            }
            else if(tokens.get(index).t == Lexeme.RPAREN){

                numOfPairs--;
                index++;
                if(functionCheck){
                    nTok = index;
                    return node;
                }
            }
            else if(relatOp.contains(tokens.get(index+1).t)){
                node = new Node(tokens.get(index+1));
                node.addChildren(new Node(tokens.get(index)));
                node.addChildren((SimpleExpression1(index + 2, numOfPairs)));
                index = nTok;
                break;
            }else if(relatOp.contains(tokens.get(index).t)){
                node = new Node(tokens.get(index));
                if((tokens.get(index+1).t != Lexeme.NUMBER) && (tokens.get(index+1).t != Lexeme.STRING) &&  (tokens.get(index+1).t != Lexeme.FLOAT)
                        && (tokens.get(index+1).t != Lexeme.IDENTIFIER) && (tokens.get(index+1).t != Lexeme.LPAREN) &&
                        (tokens.get(index+1).t != Lexeme.RPAREN) && (tokens.get(index+1).t != Lexeme.SEMICOLON) && !addOp.contains(tokens.get(index+1).t)
                        && !mulOp.contains(tokens.get(index+1).t) && !relatOp.contains((tokens.get(index+1).t))){
                    throw new UnexpectedLexem(null, index+1,tokens.get(index+1));
                }
                node.addChildren(SimpleExpression1(index+1, numOfPairs));
                break;
            }
            else if(tokens.get(index).t == Lexeme.SEMICOLON){
                //index++;
                nTok = index;
                index = nTok;
                break;
            }
            else if(tokens.get(index).t == Lexeme.STRING){
                node.addChildren(mulNode(index,numOfPairs));
                index = nTok;
            }
            else if(arrayOfTypes.contains(tokens.get(index).t)){
                numOfError++;
                throw new UnexpectedLexem(null, index, tokens.get(index));
            }else if(tokens.get(index+1).t == Lexeme.COMMA){
                node.addChildren(new Node(tokens.get(index)));
                nTok = index+1;
                return node;
                //break;
            }
            else{
                node.addChildren(SimpleExpression1(index, numOfPairs));
                index = nTok;
                if(!functionCheck) {
                    if (addOp.contains(tokens.get(index+1).t)) {//buvo be +1
                        node.addChildren(SimpleExpression1(index+1, numOfPairs));
                    }
                    if (mulOp.contains(tokens.get(index+1).t)) {//buvo be +1
                        node.addChildren(addingNode(index+1, numOfPairs));
                    }
                }
                if(tokens.get(index+1).t == Lexeme.RPAREN){
                    if(functionCheck) {
                        while (numOfPairs != 0) {
                            expression(index + 1, numOfPairs);
                        }
                    }
                    index++;
                    index++;
                    nTok = index;
                }
                break;
            }

        }if(!functionCheck) {
            if (numOfPairs != 0) {
                throw new UnexpectedLexem(null, index, tokens.get(index));
            }
        }
        return node;
    }




    public Node SimpleExpression1(int index, int element) throws UnexpectedLexem {
        numOfPairs = element;
        if((tokens.get(index).t != Lexeme.NUMBER) && (tokens.get(index).t != Lexeme.STRING) &&  (tokens.get(index).t != Lexeme.FLOAT)
                && (tokens.get(index).t != Lexeme.IDENTIFIER) && (tokens.get(index).t != Lexeme.LPAREN) &&
                (tokens.get(index).t != Lexeme.RPAREN) && (tokens.get(index).t != Lexeme.SEMICOLON) && !addOp.contains(tokens.get(index).t)
                && !mulOp.contains(tokens.get(index).t) && !relatOp.contains((tokens.get(index).t))&&(tokens.get(index).t != Lexeme.COMMA)){
            throw new UnexpectedLexem(null, index,tokens.get(index));
        }
        Node node = new Node(tokens.get(index));
        if(tokens.get(index).t == Lexeme.LPAREN){
            numOfPairs++;
            index++;
        }
        else if(tokens.get(index).t == Lexeme.RPAREN){
            numOfPairs--;
            index++;
            if(functionCheck){
                nTok = index-1;
                return node;
            }
        }

        if(tokens.get(index).t == Lexeme.COMMA){
            index++;
        }
        if(arrayOfTypes.contains(tokens.get(index).t)){
            numOfError++;
            throw new UnexpectedLexem(null, index, tokens.get(index));
        }else if (relatOp.contains(tokens.get(index).t)){
            index++;
            node.addChildren(new Node(tokens.get(index)));
        }else if(relatOp.contains(tokens.get(index+1).t)) {
            index++;
            node.addChildren(expression(index+1, numOfPairs));
        }
        else if (addOp.contains(tokens.get(index + 1).t)) {
            if(tokens.get(index+2).t == Lexeme.SEMICOLON){
                throw new UnexpectedLexem(Lexeme.IDENTIFIER, index,tokens.get(index));
            }else {
                node = new Node(tokens.get(index + 1));
                node.addChildren(new Node(tokens.get(index)));
                node.addChildren(addingNode(index + 2, numOfPairs));
            }
        } else if (addOp.contains(tokens.get(index).t)) {
            if(tokens.get(index+1).t == Lexeme.SEMICOLON){
                throw new UnexpectedLexem(Lexeme.IDENTIFIER, index+1,tokens.get(index+1));
            }else {
                node = new Node(tokens.get(index));
                node.addChildren(addingNode(index + 1, numOfPairs));
            }
        }else if (tokens.get(index+1).t == Lexeme.RPAREN){

            if(addOp.contains(tokens.get(index+2).t) || mulOp.contains(tokens.get(index+2).t)){
                if(tokens.get(index+3).t == Lexeme.SEMICOLON){
                    throw new UnexpectedLexem(Lexeme.IDENTIFIER, index+3,tokens.get(index+3));
                }else {
                    index++;
                    numOfPairs--;
                    if(functionCheck){
                        nTok = index;
                        return node;
                    }
                    Node adding = new Node(tokens.get(index + 1));
                    adding.addChildren(expression(index + 2, numOfPairs));
                    node.addChildren(adding);
                    index = nTok;
                }
            }else{
                //numOfPairs--;
                nTok = index;
                return node;
            }
        }
        else if(mulOp.contains(tokens.get(index+1).t)){
            if(tokens.get(index).t == Lexeme.RPAREN){
                node = new Node(tokens.get(index));
                Node newNode = new Node(tokens.get(index+1));
                node.addChildren(newNode);
                newNode.addChildren(addingNode(index+2, numOfPairs));
            }else {
                Node multiNode = new Node(tokens.get(index+1));
                index++;
                node = (multiNode);
                multiNode.addChildren(new Node(tokens.get(index - 1)));
                    multiNode.addChildren(addingNode(index + 1, numOfPairs));
            }
        }
        else if (tokens.get(index+1).t == Lexeme.LBRACKET){
            nTok = index;
        }
        else if (tokens.get(index+1).t == Lexeme.SEMICOLON){
            if(tokens.get(index).t == Lexeme.STRING || tokens.get(index).t == Lexeme.IDENTIFIER || tokens.get(index).t == Lexeme.NUMBER || tokens.get(index).t == Lexeme.FLOAT){
                node = new Node(tokens.get(index));
                nTok = index ;
            }else {
                if(mulOp.contains(tokens.get(index).t) || addOp.contains(tokens.get(index).t)){
                    throw new UnexpectedLexem(null, index, tokens.get(index));
                }
                index = index + 1;
                nTok = index;
            }
        }
        else {
            node.addChildren(addingNode(index, numOfPairs));
            index = nTok;
//                break;
        }
//        }
        return node;
    }

    public Node addingNode(int index, int elements) throws UnexpectedLexem {
        numOfPairs = elements;
        if((tokens.get(index).t != Lexeme.NUMBER) && (tokens.get(index).t != Lexeme.STRING) &&  (tokens.get(index).t != Lexeme.FLOAT)
                && (tokens.get(index).t != Lexeme.IDENTIFIER) && (tokens.get(index).t != Lexeme.LPAREN) &&
                (tokens.get(index).t != Lexeme.RPAREN) && (tokens.get(index).t != Lexeme.SEMICOLON) && !addOp.contains(tokens.get(index).t)
                && !mulOp.contains(tokens.get(index).t) && !relatOp.contains((tokens.get(index).t))&&(tokens.get(index).t!=Lexeme.COMMA)){
            throw new UnexpectedLexem(null, index,tokens.get(index));
        }
        Node node = new Node(tokens.get(index));
        if(tokens.get(index).t == Lexeme.COMMA){
            index++;
        }
        if(tokens.get(index).t == Lexeme.IDENTIFIER){
            if (tokens.get(index+1).t == Lexeme.LPAREN){
                functionCheck = true;
                node = functionCall(index,numOfPairs);
                //functionNode.addChildren(expression(index+1,numOfPairs));
                //numOfPairs--;
                //nTok = nTok-1;
                index = nTok;
                if(tokens.get(index).t == Lexeme.RPAREN){
                    if(numOfPairs == 0){
                        return node;
                    }
                    numOfPairs--;
                    return node;
                }else
                if(tokens.get(index+1).t != Lexeme.SEMICOLON){
                    index++;
                    nTok = index;
                    return node;
                }
            }
        }
        if(tokens.get(index+1).t == Lexeme.LPAREN){
            numOfPairs++;
            index++;
        }else if(tokens.get(index+1).t == Lexeme.RPAREN){
            numOfPairs--;
            if(tokens.get(index+2).t == Lexeme.RPAREN){
                index++;
            }
            index++;
            if(functionCheck){
                nTok = index;
                return node;
            }
//            ----------------------------------------------------------------------------------
//            sitoje vietoje reikia uzdaryti jei kartais yra taip
//            ----------------------------------------------------------------------------------
        }
        else if(tokens.get(index).t == Lexeme.LPAREN){
            numOfPairs++;
            index++;
        }else if(tokens.get(index).t == Lexeme.RPAREN){
            numOfPairs--;
            index++;
        }else if(arrayOfTypes.contains(tokens.get(index).t)){
            numOfError++;
            throw new UnexpectedLexem(null, index, tokens.get(index));
        }
        if (relatOp.contains(tokens.get(index+1).t)){
            if(tokens.get(index).t == Lexeme.SEMICOLON){
                throw new UnexpectedLexem(Lexeme.IDENTIFIER, index,tokens.get(index));
            }else {
                index++;
                //Node relation = new Node(tokens.get(index));
                node.addChildren(expression(index,numOfPairs));
            }
        }else if(mulOp.contains(tokens.get(index+1).t)){
            index++;
            if(tokens.get(index+1).t == Lexeme.SEMICOLON){
                throw new UnexpectedLexem(null, index+1,tokens.get(index+1));
            }else{
                if(tokens.get(index-1).t == Lexeme.RPAREN){
                    Node mult = new Node(tokens.get(index));
                    node.addChildren(mult);
                    mult.addChildren(mulNode(index+1,numOfPairs));
                }else {
                    node = new Node(tokens.get(index));
                    node.addChildren(new Node(tokens.get(index - 1)));
                    node.addChildren(mulNode(index + 1, numOfPairs));//maybe needs index change
                }
            }
        }else if(mulOp.contains(tokens.get(index).t)){
            //index++;
            if(tokens.get(index+1).t == Lexeme.SEMICOLON){
                throw new UnexpectedLexem(null, index+1, tokens.get(index+1));
            }else {
                node = new Node(tokens.get(index));
                node.addChildren(mulNode(index + 1, numOfPairs));
                index = nTok;
            }
        }
        else if(addOp.contains(tokens.get(index).t)){
            if(tokens.get(index).t == Lexeme.SEMICOLON){
                throw new UnexpectedLexem(Lexeme.IDENTIFIER, index,tokens.get(index));
            }else {
                if(tokens.get(index-1).t == Lexeme.RPAREN){
                    Node adding = new Node(tokens.get(index));
                    node.addChildren(adding);
                    adding.addChildren(new Node(tokens.get(index+1)));
                }
                index++;
                Node add = new Node(tokens.get(index));
                node.addChildren(add);
                //add.addChildren(expression(index+1, numOfPairs));
            }
        }
        else if(addOp.contains(tokens.get(index+1).t)){
            if(tokens.get(index+1).t == Lexeme.SEMICOLON) {
                throw new UnexpectedLexem(Lexeme.IDENTIFIER, index + 1, tokens.get(index + 1));
            }else {
                index++;
                node.addChildren(expression(index, numOfPairs));
//            node.addChildren(SimpleExpression1(index));
            }
        }
        else if (tokens.get(index+1).t == Lexeme.LBRACKET){
            //node.addChildren(new Node(tokens.get(index)));
            nTok = index;
        }
        else if(tokens.get(index).t == Lexeme.STRING){// || tokens.get(index).t == Lexeme.IDENTIFIER){
            node.addChildren(mulNode(index, numOfPairs));
            index = nTok;
        }
        else if(tokens.get(index+1).t == Lexeme.SEMICOLON){
            nTok = index+1;
        }
        else if(tokens.get(index+1).t == Lexeme.COMMA){
            nTok = index;
        }
        else if(tokens.get(index+1).t != Lexeme.SEMICOLON){
            node.addChildren(expression(index,numOfPairs));
//            node.addChildren(SimpleExpression1(index+1));
            index = nTok;
        }

        else{

           throw new UnexpectedLexem(null,index,tokens.get(index));
            //nTok = index+1;
        }
            return node;
    }

    public Node mulNode(int index, int elements) throws UnexpectedLexem {
        numOfPairs = elements;
        if((tokens.get(index).t != Lexeme.NUMBER) && (tokens.get(index).t != Lexeme.STRING) &&  (tokens.get(index).t != Lexeme.FLOAT)
                && (tokens.get(index).t != Lexeme.IDENTIFIER) && (tokens.get(index).t != Lexeme.LPAREN) &&
                (tokens.get(index).t != Lexeme.RPAREN) && (tokens.get(index).t != Lexeme.SEMICOLON) && !addOp.contains(tokens.get(index).t)
                && !mulOp.contains(tokens.get(index).t) && !relatOp.contains((tokens.get(index).t)) && (tokens.get(index).t != Lexeme.COMMA)){
            throw new UnexpectedLexem(null, index,tokens.get(index));
        }
        Node node = new Node(tokens.get(index));

        if(tokens.get(index).t == Lexeme.STRING){
            node = new Node(tokens.get(index));
            index++;
            if(tokens.get(index).t != Lexeme.RPAREN){
               numOfError++;
               throw new UnexpectedLexem(Lexeme.RPAREN,index,tokens.get(index));
            }
        }
        if(tokens.get(index).t == Lexeme.COMMA){
            index++;
        }
        if(tokens.get(index).t == Lexeme.LPAREN){
            numOfPairs++;
            index++;
        }
        else if(tokens.get(index).t == Lexeme.RPAREN){
            numOfPairs--;
            index++;
        }
        else if(tokens.get(index+1).t == Lexeme.LPAREN){
            numOfPairs++;
            index++;
        }
        else if(tokens.get(index+1).t == Lexeme.RPAREN){
            numOfPairs--;
            index++;
        }
        if(tokens.get(index).t == Lexeme.SEMICOLON){
            //index++;
            nTok = index;
        }else
        if(tokens.get(index+1).t != Lexeme.SEMICOLON){
            if((tokens.get(index).t != Lexeme.NUMBER) && (tokens.get(index).t != Lexeme.STRING) &&  (tokens.get(index).t != Lexeme.FLOAT)
                    && (tokens.get(index).t != Lexeme.IDENTIFIER) && (tokens.get(index).t != Lexeme.LPAREN) &&
                    (tokens.get(index).t != Lexeme.RPAREN) && (tokens.get(index).t != Lexeme.SEMICOLON) && addOp.contains(tokens.get(index).t)
                    && mulOp.contains(tokens.get(index).t) && relatOp.contains((tokens.get(index).t))){
                throw new UnexpectedLexem(null, index,tokens.get(index));
            }
                node.addChildren(expression(index+1, numOfPairs));
                index = nTok;
//            }
//  node.addChildren(SimpleExpression1(index+1));
        }
        else if(tokens.get(index+1).t == Lexeme.SEMICOLON){
            index = index + 1;
            nTok=index;
            return node;
        }
        nTok = index;
        return node;
    }

    public Node functionCall(int index, int got) throws UnexpectedLexem {
        Node funNode = new Node(new Lexer.Token(Lexeme.FUNCAL, "Function call"));
        funNode.addChildren(new Node(tokens.get(index)));
        int functionPairs = got + 1 ;
        index++;
        funNode.addChildren(expression(index + 1, functionPairs));
        index = nTok;//buvo +1
        while(true) {
            //functionCheck = false;
            if (tokens.get(index).t == Lexeme.COMMA) {
                funNode.addChildren(expression(index+1, functionPairs));
                index = nTok;
//                functionCheck = false;
            }if(tokens.get(index+1).t == Lexeme.COMMA) {
                index++;
            }
            else{
                //numOfPairs--;
                functionCheck = false;
                break;
            }
//            functionCheck = false;
        }
        return funNode;
    }

}