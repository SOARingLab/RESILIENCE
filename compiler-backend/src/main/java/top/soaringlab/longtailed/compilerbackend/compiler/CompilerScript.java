package top.soaringlab.longtailed.compilerbackend.compiler;

public class CompilerScript {

    static class Char {
        char ch;
        int row;
        int col;
    }

    static class Word {
        String str;
        String type;
        int row;
        int col;

        void pushBack(Char ch) {
            if (str == null) {
                str = "";
                row = ch.row;
                col = ch.col;
            }
            str = str + ch.ch;
        }
    }

    private String text;

    private int pos;

    private int row;

    private int col;

    private Word lookahead;

    public String compileScript(String text) throws Exception {
        this.text = text + "#";
        pos = 0;
        row = 1;
        col = 1;
        lookahead = getWord();

        String ans = statements();
        if (lookahead.type.equals("eof")) {
            return ans;
        } else {
            error(lookahead, "unknown");
            return null;
        }
    }

    private Char getChar() {
        Char ch = new Char();
        ch.ch = text.charAt(pos++);
        ch.row = row;
        ch.col = col;
        if (ch.ch == '\n') {
            row++;
            col = 1;
        } else {
            col++;
        }
        return ch;
    }

    private void unGetChar(Char ch) {
        row = ch.row;
        col = ch.col;
        pos--;
    }

    private Word getWord() {
        Char ch;
        Word word = new Word();
        int state = 0;

        while (true) {
            ch = getChar();

            if (state == 0) {
                if (ch.ch == ' ' || ch.ch == '\t' || ch.ch == '\r' || ch.ch == '\n') {
                    continue;
                } else if (Character.isLetter(ch.ch) || ch.ch == '_') {
                    word.pushBack(ch);
                    state = 100;
                } else if (Character.isDigit(ch.ch)) {
                    word.pushBack(ch);
                    state = 200;
                } else if (ch.ch == '(') {
                    word.pushBack(ch);
                    word.type = "(";
                    return word;
                } else if (ch.ch == ')') {
                    word.pushBack(ch);
                    word.type = ")";
                    return word;
                } else if (ch.ch == ',') {
                    word.pushBack(ch);
                    word.type = ",";
                    return word;
                } else if (ch.ch == ';') {
                    word.pushBack(ch);
                    word.type = ";";
                    return word;
                } else if (ch.ch == '<') {
                    word.pushBack(ch);
                    state = 500;
                } else if (ch.ch == '>') {
                    word.pushBack(ch);
                    state = 510;
                } else if (ch.ch == '=') {
                    word.pushBack(ch);
                    state = 520;
                } else if (ch.ch == '+') {
                    word.pushBack(ch);
                    word.type = "+";
                    return word;
                } else if (ch.ch == '-') {
                    word.pushBack(ch);
                    word.type = "-";
                    return word;
                } else if (ch.ch == '*') {
                    word.pushBack(ch);
                    word.type = "*";
                    return word;
                } else if (ch.ch == '/') {
                    word.pushBack(ch);
                    state = 540;
                } else if (ch.ch == '!') {
                    word.pushBack(ch);
                    state = 700;
                } else if (ch.ch == '"') {
                    word.pushBack(ch);
                    state = 800;
                } else if (ch.ch == '#') {
                    word.pushBack(ch);
                    word.type = "eof";
                    return word;
                } else {
                    word.pushBack(ch);
                    word.type = "error";
                    return word;
                }
            } else if (state == 100) {
                if (Character.isLetter(ch.ch) || Character.isDigit(ch.ch) || ch.ch == '_') {
                    word.pushBack(ch);
                } else {
                    unGetChar(ch);
                    if (word.str.equals("if")) {
                        word.type = "if";
                    } else if (word.str.equals("then")) {
                        word.type = "then";
                    } else if (word.str.equals("not")) {
                        word.type = "not";
                    } else if (word.str.equals("and")) {
                        word.type = "and";
                    } else if (word.str.equals("or")) {
                        word.type = "or";
                    } else {
                        word.type = "id";
                    }
                    return word;
                }
            } else if (state == 200) {
                if (Character.isDigit(ch.ch)) {
                    word.pushBack(ch);
                } else if (ch.ch == '.') {
                    word.pushBack(ch);
                    state = 300;
                } else {
                    unGetChar(ch);
                    word.type = "number";
                    return word;
                }
            } else if (state == 300) {
                if (Character.isDigit(ch.ch)) {
                    word.pushBack(ch);
                } else {
                    unGetChar(ch);
                    word.type = "number";
                    return word;
                }
            } else if (state == 500) {
                if (ch.ch == '=') {
                    word.pushBack(ch);
                    word.type = "equal";
                    return word;
                } else {
                    unGetChar(ch);
                    word.type = "equal";
                    return word;
                }
            } else if (state == 510) {
                if (ch.ch == '=') {
                    word.pushBack(ch);
                    word.type = "equal";
                    return word;
                } else {
                    unGetChar(ch);
                    word.type = "equal";
                    return word;
                }
            } else if (state == 520) {
                if (ch.ch == '=') {
                    word.pushBack(ch);
                    word.type = "equal";
                    return word;
                } else {
                    unGetChar(ch);
                    word.type = "=";
                    return word;
                }
            } else if (state == 540) {
                if (ch.ch == '/') {
                    word.pushBack(ch);
                    state = 600;
                } else if (ch.ch == '*') {
                    word.pushBack(ch);
                    state = 610;
                } else {
                    unGetChar(ch);
                    word.type = "/";
                    return word;
                }
            } else if (state == 600) {
                if (ch.ch == '\r' || ch.ch == '\n' || ch.ch == '#') {
                    unGetChar(ch);
                    word = new Word();
                    state = 0;
                } else {
                    word.pushBack(ch);
                }
            } else if (state == 610) {
                if (ch.ch == '*') {
                    word.pushBack(ch);
                    state = 611;
                } else {
                    word.pushBack(ch);
                }
            } else if (state == 611) {
                if (ch.ch == '*') {
                    word.pushBack(ch);
                } else if (ch.ch == '/') {
                    word.pushBack(ch);
                    word = new Word();
                    state = 0;
                } else {
                    word.pushBack(ch);
                    state = 610;
                }
            } else if (state == 700) {
                if (ch.ch == '=') {
                    word.pushBack(ch);
                    word.type = "equal";
                    return word;
                } else {
                    unGetChar(ch);
                    word.type = "error";
                    return word;
                }
            } else if (state == 800) {
                if (ch.ch == '"') {
                    word.pushBack(ch);
                    word.type = "number";
                    return word;
                } else if (ch.ch == '\r' || ch.ch == '\n' || ch.ch == '#') {
                    unGetChar(ch);
                    word.type = "error";
                    return word;
                } else {
                    word.pushBack(ch);
                }
            }
        }
    }

    private String match(String type) throws Exception {
        if (lookahead.type.equals(type)) {
            String str = lookahead.str;
            lookahead = getWord();
            return str;
        } else {
            error(lookahead, "expect " + type);
            return null;
        }
    }

    private void error(Word word, String msg) throws Exception {
        throw new Exception(word.row + ": " + word.col + ": " + word.str + ": " + word.type + ": " + msg);
    }

    private String statements() throws Exception {
        String res = "";
        while (lookahead.type.equals("id") || lookahead.type.equals("if")) {
            res = res + statement() + "\n";
        }
        return res;
    }

    private String statement() throws Exception {
        if (lookahead.type.equals("id")) {
            return assignmentStatement();
        } else if (lookahead.type.equals("if")) {
            return conditionalStatement();
        } else {
            error(lookahead, "expect statement");
            return null;
        }
    }

    private String assignmentStatement() throws Exception {
        String id = match("id");
        if (id.equals("constraint")) {
            String condition = condition();
            String res = "if (" + condition + ") {\nS.info(execution, \"constraint " + condition + " passed\")\n} else {\nS.warn(execution, \"constraint " + condition + " failed\")\n}";
            return res;
        } else if (lookahead.type.equals("=")) {
            String res = "execution.setVariable(\"" + id + "\", ";
            match("=");
            res = res + expression() + ")";
            return res;
        } else {
            String res = "S." + id + argumentList();
            return res;
        }
    }

    private String conditionalStatement() throws Exception {
        String res = match("if") + " (" + condition() + ") {\n";
        match("then");
        res = res + statements() + "}";
        match(";");
        return res;
    }

    private String expression() throws Exception {
        String res = term();
        while (lookahead.type.equals("+") || lookahead.type.equals("-")) {
            res = res + " " + match(lookahead.type) + " " + term();
        }
        return res;
    }

    private String term() throws Exception {
        String res = factor();
        while (lookahead.type.equals("*") || lookahead.type.equals("/")) {
            res = res + " " + match(lookahead.type) + " " + factor();
        }
        return res;
    }

    private String factor() throws Exception {
        String res = "";
        if (lookahead.type.equals("-")) {
            res = match(lookahead.type);
        }
        if (lookahead.type.equals("number")) {
            res = res + match(lookahead.type);
            return res;
        } else if (lookahead.type.equals("id")) {
            String id = match(lookahead.type);
            if (lookahead.type.equals("(")) {
                res = res + "S." + id + argumentList();
            } else {
                res = res + id;
            }
            return res;
        } else if (lookahead.type.equals("(")) {
            res = res + match("(") + expression() + match(")");
            return res;
        } else {
            error(lookahead, "expect factor");
            return null;
        }
    }

    private String condition() throws Exception {
        String res = disjunct();
        while (lookahead.type.equals("or")) {
            match(lookahead.type);
            res = res + " || " + disjunct();
        }
        return res;
    }

    private String disjunct() throws Exception {
        String res = conjunct();
        while (lookahead.type.equals("and")) {
            match(lookahead.type);
            res = res + " && " + conjunct();
        }
        return res;
    }

    private String conjunct() throws Exception {
        String res = "";
        if (lookahead.type.equals("not")) {
            match(lookahead.type);
            res = "!";
        }
        if (lookahead.str.equals("true") || lookahead.str.equals("false")) {
            res = res + match(lookahead.type);
            return res;
        } else if (lookahead.type.equals("-") || lookahead.type.equals("number") || lookahead.type.equals("id") || lookahead.type.equals("(")) {
            res = res + expression() + " " + match("equal") + " " + expression();
            return res;
        } else {
            error(lookahead, "expect conjunct");
            return null;
        }
    }

    private String argumentList() throws Exception {
        String res = match("(") + "execution";
        if (lookahead.type.equals("-") || lookahead.type.equals("number") || lookahead.type.equals("id") || lookahead.type.equals("(")) {
            res = res + ", " + expression();
            while (lookahead.type.equals(",")) {
                res = res + match(lookahead.type) + " " + expression();
            }
        }
        res = res + match(")");
        return res;
    }
}
