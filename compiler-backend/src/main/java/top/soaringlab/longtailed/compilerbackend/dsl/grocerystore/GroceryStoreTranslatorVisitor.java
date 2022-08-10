package top.soaringlab.longtailed.compilerbackend.dsl.grocerystore;

public class GroceryStoreTranslatorVisitor extends GroceryStoreBaseVisitor<String> {
    @Override
    public String visitInputs(GroceryStoreParser.InputsContext ctx) {
        StringBuilder stringBuilder = new StringBuilder();
        for (GroceryStoreParser.InputContext inputContext : ctx.input()) {
            stringBuilder.append(visit(inputContext));
        }
        return stringBuilder.toString();
    }

    @Override
    public String visitInput(GroceryStoreParser.InputContext ctx) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("if (\n");
        for (GroceryStoreParser.ConditionContext conditionContext : ctx.condition()) {
            stringBuilder.append(visit(conditionContext)).append("\n");
        }
        stringBuilder.append(") {\n");
        for (GroceryStoreParser.ActionContext actionContext : ctx.action()) {
            stringBuilder.append(visit(actionContext)).append("\n");
        }
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }

    @Override
    public String visitCondition(GroceryStoreParser.ConditionContext ctx) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("    ");
        if (ctx.operator.getText().equals("AND")) {
            stringBuilder.append("&& ");
        } else if (ctx.operator.getText().equals("OR")) {
            stringBuilder.append("|| ");
        }
        if (ctx.predicate.getText().equals("not in")) {
            stringBuilder.append("!");
        }
        stringBuilder.append("S.risk(execution, execution.getVariable(\"region\")).equals(\"");
        if (ctx.object.getText().equals("low-risk region")) {
            stringBuilder.append("low");
        } else if (ctx.object.getText().equals("medium-risk region")) {
            stringBuilder.append("medium");
        } else {
            stringBuilder.append("high");
        }
        stringBuilder.append("\")");
        return stringBuilder.toString();
    }

    @Override
    public String visitAction(GroceryStoreParser.ActionContext ctx) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("    execution.setVariable(\"delivery method\", \"");
        stringBuilder.append(ctx.object.getText());
        stringBuilder.append("\");");
        return stringBuilder.toString();
    }
}
