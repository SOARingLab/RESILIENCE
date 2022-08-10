import {Edit} from "./edit";
import {CodeNode} from "./code-node";

export class Visitor {

  edit: Edit;

  constructor(
    edit: Edit
  ) {
    this.edit = edit;
  }

  visitCodeNode(codeNode: CodeNode) {
    let result = '';
    if (
      codeNode.type === 'inputs'
      || codeNode.type === 'input'
      || codeNode.type === 'conditions'
      || codeNode.type === 'actions'
    ) {
      for (let content of codeNode.contents) {
        let contentCodeNode = this.edit.code.getCodeNode(content);
        result += this.visitCodeNode(contentCodeNode);
      }
    } else if (
      codeNode.type === 'condition'
      || codeNode.type === 'action'
    ) {
      for (let i = 0; i < codeNode.contents.length; i++) {
        if (i > 0) {
          result += ' ';
        }
        let contentCodeNode = this.edit.code.getCodeNode(codeNode.contents[i]);
        result += this.visitCodeNode(contentCodeNode);
      }
      result += '\n';
    } else if (
      codeNode.type === 'IDENTIFIER'
      || codeNode.type === 'EXPRESSION'
      || codeNode.type === 'OPERATOR'
      || codeNode.type === 'SET'
      || codeNode.type === '='
    ) {
      return codeNode.content;
    } else if (
      codeNode.type === 'LOGICAL'
    ) {
      let fatherCodeNode = this.edit.code.getCodeNode(codeNode.fatherId);
      if (this.edit.getCodeNodePosition(fatherCodeNode) === 0) {
        return 'WHEN';
      } else {
        return codeNode.content;
      }
    }
    return result;
  }

  visit() {
    let rootCodeNode = this.edit.code.getRootCodeNode();
    return this.visitCodeNode(rootCodeNode);
  }
}
