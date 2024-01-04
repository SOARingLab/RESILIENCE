package top.soaringlab.longtailed.compilerbackend.dsl.simple;

public class SimpleTranslatorVisitor extends SimpleBaseVisitor<String> {
    @Override
    public String visitInputs(SimpleParser.InputsContext ctx) {
        StringBuilder stringBuilder = new StringBuilder();
        for (SimpleParser.InputContext inputContext : ctx.input()) {
            stringBuilder.append(visit(inputContext));
        }
        return stringBuilder.toString();
    }

    @Override
    public String visitInput(SimpleParser.InputContext ctx) {
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder conditionBuilder = new StringBuilder();
        int i = 0;
        for (SimpleParser.ConditionContext conditionContext : ctx.condition()) {
            i++;
            conditionBuilder.append("    ").append(visit(conditionContext)).append("\n");
        }
        if (i > 0) {
            stringBuilder.append("if (\n");
            stringBuilder.append(conditionBuilder);
            stringBuilder.append(") ");
        }
        stringBuilder.append("{\n");
        for (SimpleParser.ActionContext actionContext : ctx.action()) {
            String action = visit(actionContext);
            if (action != null) {
                stringBuilder.append("    ").append(action).append("\n");
            }
        }
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }

    @Override
    public String visitCondition(SimpleParser.ConditionContext ctx) {
        StringBuilder stringBuilder = new StringBuilder();
        if (ctx.logical.getText().equals("AND")) {
            stringBuilder.append("&& ");
        } else if (ctx.logical.getText().equals("OR")) {
            stringBuilder.append("|| ");
        }
        stringBuilder.append(ctx.operand1.getText()).append(" ").append(ctx.operator.getText()).append(" ").append(ctx.operand2.getText());
        return stringBuilder.toString();
    }

    @Override
    public String visitSet(SimpleParser.SetContext ctx) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("S.info(execution, \"Annotation (SET ").append(ctx.operand1.getText()).append(" = ").append(removeQuotation(ctx.operand2.getText())).append(")\");\n");
        stringBuilder.append("execution.setVariable(\"").append(ctx.operand1.getText()).append("\", ").append(ctx.operand2.getText()).append(");");
        return stringBuilder.toString();
    }

    @Override
    public String visitInsert(SimpleParser.InsertContext ctx) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("S.info(execution, \"Annotation (INSERT ").append(removeQuotation(ctx.operand.getText())).append(" ").append(ctx.operator.getText()).append(")\");");
        return stringBuilder.toString();
    }

    @Override
    public String visitSkip(SimpleParser.SkipContext ctx) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("S.info(execution, \"Annotation (SKIP)\");");
        return stringBuilder.toString();
    }

    @Override
    public String visitReplace(SimpleParser.ReplaceContext ctx) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("S.info(execution, \"Annotation (REPLACE ").append(removeQuotation(ctx.operand.getText())).append(")\");");
        return stringBuilder.toString();
    }

    @Override
    public String visitAbort(SimpleParser.AbortContext ctx) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("S.info(execution, \"Annotation (ABORT)\");");
        return stringBuilder.toString();
    }

    private String removeQuotation(String s) {
        return s.replace("\"", "");
    }
}
