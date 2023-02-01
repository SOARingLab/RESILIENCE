package top.soaringlab.longtailed.compilerbackend.dsl.simple;

import java.util.ArrayList;
import java.util.List;

public class SimpleVerifierVisitor extends SimpleBaseVisitor<List<String>> {

    @Override
    public List<String> visitInputs(SimpleParser.InputsContext ctx) {
        List<String> result = new ArrayList<>();
        for (SimpleParser.InputContext inputContext : ctx.input()) {
            result.add("input");
            result.addAll(visit(inputContext));
        }
        return result;
    }

    @Override
    public List<String> visitInput(SimpleParser.InputContext ctx) {
        List<String> result = new ArrayList<>();
        StringBuilder conditionBuilder = new StringBuilder();
        int i = 0;
        for (SimpleParser.ConditionContext conditionContext : ctx.condition()) {
            if (i++ > 0) {
                conditionBuilder.append(" ");
            }
            conditionBuilder.append(visit(conditionContext).get(0));
        }
        if (i > 0) {
            result.add("condition");
            result.add(removeQuotation(conditionBuilder.toString()));
        }
        for (SimpleParser.ActionContext actionContext : ctx.action()) {
            List<String> action = visit(actionContext);
            if (action != null) {
                result.add("action");
                result.addAll(action);
            }
        }
        return result;
    }

    @Override
    public List<String> visitCondition(SimpleParser.ConditionContext ctx) {
        List<String> result = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        if (ctx.logical.getText().equals("AND")) {
            stringBuilder.append("& ");
        } else if (ctx.logical.getText().equals("OR")) {
            stringBuilder.append("| ");
        }
        String operator = ctx.operator.getText();
        if (operator.equals("==")) {
            operator = "=";
        }
        stringBuilder.append(ctx.operand1.getText()).append(" ").append(operator).append(" ").append(ctx.operand2.getText());
        result.add(stringBuilder.toString());
        return result;
    }

    @Override
    public List<String> visitSet(SimpleParser.SetContext ctx) {
        List<String> result = new ArrayList<>();
        result.add(ctx.operand1.getText());
        result.add(removeQuotation(ctx.operand2.getText()));
        return result;
    }

    @Override
    public List<String> visitInsert(SimpleParser.InsertContext ctx) {
        return null;
    }

    @Override
    public List<String> visitSkip(SimpleParser.SkipContext ctx) {
        return null;
    }

    @Override
    public List<String> visitReplace(SimpleParser.ReplaceContext ctx) {
        return null;
    }

    @Override
    public List<String> visitAbort(SimpleParser.AbortContext ctx) {
        return null;
    }

    private String removeQuotation(String s) {
        return s.replace("\"", "_");
    }
}
