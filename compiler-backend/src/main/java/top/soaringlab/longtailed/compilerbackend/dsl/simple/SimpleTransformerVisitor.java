package top.soaringlab.longtailed.compilerbackend.dsl.simple;

import java.util.ArrayList;
import java.util.List;

public class SimpleTransformerVisitor extends SimpleBaseVisitor<List<String>> {

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
        // conditionBuilder.append("${");
        int i = 0;
        for (SimpleParser.ConditionContext conditionContext : ctx.condition()) {
            if (i++ > 0) {
                conditionBuilder.append(" ");
            }
            conditionBuilder.append(visit(conditionContext).get(0));
        }
        // conditionBuilder.append("}");
        if (i > 0) {
            result.add("condition");
            result.add(conditionBuilder.toString());
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
            stringBuilder.append("&& ");
        } else if (ctx.logical.getText().equals("OR")) {
            stringBuilder.append("|| ");
        }
        stringBuilder.append(ctx.operand1.getText()).append(" ").append(ctx.operator.getText()).append(" ").append(ctx.operand2.getText());
        result.add(stringBuilder.toString());
        return result;
    }

    @Override
    public List<String> visitSet(SimpleParser.SetContext ctx) {
        return null;
    }

    @Override
    public List<String> visitInsert(SimpleParser.InsertContext ctx) {
        List<String> result = new ArrayList<>();
        result.add("INSERT " + ctx.operator.getText());
        result.add(removeQuotation(ctx.operand.getText()));
        return result;
    }

    @Override
    public List<String> visitSkip(SimpleParser.SkipContext ctx) {
        List<String> result = new ArrayList<>();
        result.add("SKIP");
        result.add("");
        return result;
    }

    @Override
    public List<String> visitReplace(SimpleParser.ReplaceContext ctx) {
        List<String> result = new ArrayList<>();
        result.add("REPLACE");
        result.add(removeQuotation(ctx.operand.getText()));
        return result;
    }

    @Override
    public List<String> visitAbort(SimpleParser.AbortContext ctx) {
        List<String> result = new ArrayList<>();
        result.add("ABORT");
        result.add("");
        return result;
    }

    private String removeQuotation(String s) {
        return s.replace("\"", "");
    }
}
