import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

public class ScannerTest {

    static int passed = 0;
    static int failed = 0;

    public static void main(String[] args) throws java.io.IOException {

        // ---------- Identifiers & reserved words ----------
        run("basic identifier", "x", "ID(x)");
        run("reserved word", "int", "INT");
        run("reserved word 2", "while", "WHILE");
        run("id starting with hex letter", "cab", "ID(cab)");
        run("id starting with x", "xValue", "ID(xValue)");
        run("id with digits", "counter1", "ID(counter1)");
        run("reserved is case sensitive", "Int", "ID(Int)"); // lowercase 'int' is reserved, 'Int' isn't

        // ---------- Integer literals ----------
        run("decimal literal", "123", "INTEGER_LITERAL(123)");
        run("single digit", "7", "INTEGER_LITERAL(7)");
        run("bare zero", "0", "INTEGER_LITERAL(0)");
        run("octal literal", "017", "INTEGER_LITERAL(017)");
        run("octal literal 2", "0123", "INTEGER_LITERAL(0123)");
        run("hex literal", "0x1A", "INTEGER_LITERAL(0x1A)");
        run("hex literal lowercase", "0xff", "INTEGER_LITERAL(0xff)");
        run("hex literal mixed", "0xdeadBEEF", "INTEGER_LITERAL(0xdeadBEEF)");
        run("invalid octal digit", "089", "Invalid character in octal number.");
        run("invalid hex - no digits", "0x", "Invalid character in hex number.");
        run("invalid hex - bad char", "0xg1", "Invalid character in hex number.");

        // ---------- Strings ----------
        run("simple string", "\"hi\"", "STRING_LITERAL(hi)");
        run("string with spaces", "\"hello world\"", "STRING_LITERAL(hello world)");
        run("empty string", "\"\"", "STRING_LITERAL()");
        run("unterminated string", "\"hi", "Illegal token.");

        // ---------- Single-char operators ----------
        run("plus", "+", "PLUS");
        run("minus", "-", "MINUS");
        run("star", "*", "STAR");
        run("divide", "/", "DIVIDE");
        run("lessthan", "<", "LESSTHAN");
        run("greaterthan", ">", "GREATERTHAN");
        run("bang", "!", "BANG");
        run("assign", "=", "ASSIGN");

        // ---------- Compound operators ----------
        run("plusequals", "+=", "PLUSEQUALS");
        run("minusequals", "-=", "MINUSEQUALS");
        run("starequals", "*=", "STAREQUALS");
        run("divideequals", "/=", "DIVIDEEQUALS");
        run("lessequal", "<=", "LESSEQUAL");
        run("greaterequal", ">=", "GREATEREQUAL");
        run("notequal", "!=", "NOTEQUAL");
        run("equal", "==", "EQUAL");
        run("and", "&&", "AND");
        run("or", "||", "OR");
        run("lone ampersand illegal", "&", "Illegal token.");
        run("lone pipe illegal", "|", "Illegal token.");

        // ---------- Punctuation ----------
        run("lparen", "(", "LPAREN");
        run("rparen", ")", "RPAREN");
        run("lbrace", "{", "LBRACE");
        run("rbrace", "}", "RBRACE");
        run("semicolon", ";", "SEMICOLON");
        run("comma", ",", "COMMA");

        // ---------- Whitespace handling ----------
        run("leading/trailing whitespace", "   x   ", "ID(x)");
        run("tabs and newlines", "\t\nx\n\t", "ID(x)");
        run("whitespace only", "   ", "EOF");
        run("empty input", "", "EOF");

        // ---------- Multi-token sequences ----------
        run("id then semicolon", "x;", "ID(x)", "SEMICOLON");
        run("declaration", "int x;", "INT", "ID(x)", "SEMICOLON");
        run("full assignment", "int x = x + 6;",
            "INT", "ID(x)", "ASSIGN", "ID(x)", "PLUS", "INTEGER_LITERAL(6)", "SEMICOLON");
        run("compound then single", "x += 1;",
            "ID(x)", "PLUSEQUALS", "INTEGER_LITERAL(1)", "SEMICOLON");
        run("equal vs assign", "x == 5",
            "ID(x)", "EQUAL", "INTEGER_LITERAL(5)");
        run("logical and", "a && b",
            "ID(a)", "AND", "ID(b)");
        run("no space between id and op", "x=5",
            "ID(x)", "ASSIGN", "INTEGER_LITERAL(5)");
        run("no space between id and semicolon", "return0;",
            "ID(return0)", "SEMICOLON"); // "return0" is one identifier, not "return"+"0"
        run("class skeleton", "class Foo{int x;}",
            "CLASS", "ID(Foo)", "LBRACE", "INT", "ID(x)", "SEMICOLON", "RBRACE");
        run("hex then semicolon no space", "0xFF;",
            "INTEGER_LITERAL(0xFF)", "SEMICOLON");
        run("zero then semicolon no space", "0;",
            "INTEGER_LITERAL(0)", "SEMICOLON");
        run("string then id", "\"hi\" x",
            "STRING_LITERAL(hi)", "ID(x)");
        run("not equal in condition", "if(x!=0){",
            "IF", "LPAREN", "ID(x)", "NOTEQUAL", "INTEGER_LITERAL(0)", "LBRACE");

        System.out.println();
        System.out.println("===================================");
        System.out.println("Passed: " + passed + "   Failed: " + failed);
    }

    private static void run(String name, String input, String... expectedTokens) throws java.io.IOException {
        Scanner scanner = new Scanner();
        StringReader reader = new StringReader(input);
        List<String> expected = Arrays.asList(expectedTokens);

        StringBuilder actualLog = new StringBuilder();
        boolean ok = true;

        for (int i = 0; i < expected.size(); i++) {
            String got = scanner.getCoin(reader);
            actualLog.append(got).append(" | ");
            if (!got.equals(expected.get(i))) {
                ok = false;
            }
        }

        // if all expected tokens matched, confirm EOF (or illegal-token stop) follows,
        // unless the last expected token was itself an error/EOF
        String lastExpected = expected.isEmpty() ? "" : expected.get(expected.size() - 1);
        if (ok && !lastExpected.equals("EOF") && !lastExpected.startsWith("Illegal")
                && !lastExpected.startsWith("Invalid")) {
            String trailing = scanner.getCoin(reader);
            actualLog.append(trailing);
            if (!trailing.equals("EOF")) {
                ok = false;
            }
        }

        if (ok) {
            passed++;
            System.out.println("PASS  [" + name + "]");
        } else {
            failed++;
            System.out.println("FAIL  [" + name + "]  input=\"" + input.replace("\n", "\\n") + "\"");
            System.out.println("      expected: " + expected);
            System.out.println("      actual:   " + actualLog);
        }
    }
}