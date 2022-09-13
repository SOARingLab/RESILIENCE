package top.soaringlab.longtailed.compilerbackend.dsl.nusmvresult;

import java.util.ArrayList;
import java.util.List;

public class NusmvResultReaderVisitor extends NusmvResultBaseVisitor<List<String>> {
    @Override
    public List<String> visitStates(NusmvResultParser.StatesContext ctx) {
        List<String> result = new ArrayList<>();
        for (NusmvResultParser.StateContext stateContext : ctx.state()) {
            result.addAll(visit(stateContext));
        }
        return result;
    }

    @Override
    public List<String> visitState(NusmvResultParser.StateContext ctx) {
        List<String> result = new ArrayList<>();
        result.add("STATE");
        for (NusmvResultParser.AssignmentContext assignmentContext : ctx.assignment()) {
            result.addAll(visit(assignmentContext));
        }
        return result;
    }

    @Override
    public List<String> visitAssignment(NusmvResultParser.AssignmentContext ctx) {
        List<String> result = new ArrayList<>();
        result.add(ctx.variable.getText());
        result.add(ctx.value.getText());
        return result;
    }
}
