import {SyntaxNode} from "./syntax-node";
import {Syntax} from "./syntax";
import {CodeNode} from "./code-node";
import {Edit} from "./edit";

export class Converter {

  edit: Edit;
  nameCodeNodeMap = new Map<string, CodeNode>();
  newSyntax = new Syntax();

  constructor(
    edit: Edit
  ) {
    this.edit = edit;
  }

  getCodeNodeByName(name: string) {
    return this.nameCodeNodeMap.get(name) as CodeNode;
  }

  convertSyntax() {
    let codeNodeList = [...this.edit.code.codeNodeMap.values()];
    for (let codeNode of codeNodeList) {
      let syntaxNode = this.edit.syntax.getSyntaxNode(codeNode.type);
      if (syntaxNode.name === 'syntax') {
        for (let content of codeNode.contents) {
          let contentCodeNode = this.edit.code.getCodeNode(content);
          this.nameCodeNodeMap.set(contentCodeNode.name, contentCodeNode);
        }
        this.edit.code.rootCodeNodeId = codeNode.contents[0];
        this.edit.code.codeNodeMap.delete(codeNode.id);
      }
    }
  }

  convertExpression() {
    let codeNodeList = [...this.edit.code.codeNodeMap.values()];
    for (let codeNode of codeNodeList) {
      let syntaxNode = this.edit.syntax.getSyntaxNode(codeNode.type);
      if (syntaxNode.name === 'expression') {
        let contentCodeNode = this.edit.code.getCodeNode(codeNode.contents[0]);
        codeNode.type = contentCodeNode.type;
        codeNode.content = contentCodeNode.content;
        codeNode.contents = contentCodeNode.contents;
        this.edit.code.codeNodeMap.delete(contentCodeNode.id);
      }
    }
  }

  convertIdentifier() {
    let codeNodeList = [...this.edit.code.codeNodeMap.values()];
    for (let codeNode of codeNodeList) {
      for (let i = 0; i < codeNode.contents.length; i++) {
        let contentCodeNode = this.edit.code.getCodeNode(codeNode.contents[i]);
        let contentSyntaxNode = this.edit.syntax.getSyntaxNode(contentCodeNode.type)
        if (contentSyntaxNode.name === 'identifier') {
          if (contentCodeNode.name === '') {
            codeNode.contents[i] = this.getCodeNodeByName(contentCodeNode.content).id;
            this.edit.code.codeNodeMap.delete(contentCodeNode.id);
          } else {
            contentCodeNode.type = 'concatenation';
            contentCodeNode.contents = [this.getCodeNodeByName(contentCodeNode.content).id];
            contentCodeNode.content = '';
          }
        }
      }
    }
  }

  generateSyntaxNode() {
    let codeNodeList = [...this.edit.code.codeNodeMap.values()];
    for (let codeNode of codeNodeList) {
      let syntaxNode = this.edit.syntax.getSyntaxNode(codeNode.type);
      let newSyntaxNode = new SyntaxNode();
      newSyntaxNode.id = codeNode.name;
      newSyntaxNode.name = codeNode.name;
      newSyntaxNode.type = syntaxNode.name;
      newSyntaxNode.content = codeNode.content;
      for (let content of codeNode.contents) {
        let contentCodeNode = this.edit.code.getCodeNode(content);
        newSyntaxNode.contents.push(contentCodeNode.name);
      }
      newSyntaxNode.comment = codeNode.comment;
      this.newSyntax.syntaxNodeMap.set(newSyntaxNode.id, newSyntaxNode);
    }
    this.newSyntax.name = this.edit.code.name;
    this.newSyntax.rootSyntaxNodeId = this.edit.code.getCodeNode(this.edit.code.rootCodeNodeId).name;
  }

  convert() {
    this.convertSyntax();
    this.convertExpression();
    this.convertIdentifier();
    this.generateSyntaxNode();
    return this.newSyntax;
  }
}
