package top.soaringlab.longtailed.compilerbackend.dsl.expressionlanguage;

public class ExpressionLanguageTranslatorVisitor extends ExpressionLanguageBaseVisitor<String> {
    @Override
    public String visitInput(ExpressionLanguageParser.InputContext ctx) {
        return visit(ctx.condition());
    }

    @Override
    public String visitNegation(ExpressionLanguageParser.NegationContext ctx) {
        return "!" + visit(ctx.operand1);
    }

    @Override
    public String visitComparison(ExpressionLanguageParser.ComparisonContext ctx) {
        String operand1 = removeQuotation(ctx.operand1.getText());
        String operand2 = removeQuotation(ctx.operand2.getText());
        String operator = ctx.operator.getText();
        if (operator.equals("==")) {
            operator = "=";
        }
        return operand1 + " " + operator + " " + operand2;
    }

    @Override
    public String visitConjunction(ExpressionLanguageParser.ConjunctionContext ctx) {
        return visit(ctx.operand1) + " & " + visit(ctx.operand2);
    }

    @Override
    public String visitDisjunction(ExpressionLanguageParser.DisjunctionContext ctx) {
        return visit(ctx.operand1) + " | " + visit(ctx.operand2);
    }

    private String removeQuotation(String s) {
        return s.replace("\"", "_");
    }
}
