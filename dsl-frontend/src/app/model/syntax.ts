import {SyntaxNode} from "./syntax-node";

export class Syntax {
  name = '';
  syntaxNodeList: SyntaxNode[] = [];
  syntaxNodeMap = new Map<string, SyntaxNode>();
  rootSyntaxNodeId = '';

  static load(syntaxFile: string) {
    let syntax = JSON.parse(syntaxFile) as Syntax;
    let newSyntax = new Syntax();
    newSyntax.name = syntax.name;
    newSyntax.syntaxNodeList = syntax.syntaxNodeList;
    for (let syntaxNode of newSyntax.syntaxNodeList) {
      newSyntax.syntaxNodeMap.set(syntaxNode.id, syntaxNode);
    }
    newSyntax.rootSyntaxNodeId = syntax.rootSyntaxNodeId;
    return newSyntax;
  }

  save() {
    this.syntaxNodeList = [...this.syntaxNodeMap.values()];
    return JSON.stringify(this);
  }

  getSyntaxNode(id: string) {
    return this.syntaxNodeMap.get(id) as SyntaxNode;
  }

  getRootSyntaxNode() {
    return this.getSyntaxNode(this.rootSyntaxNodeId);
  }
}
