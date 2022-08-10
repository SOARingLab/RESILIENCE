import {SyntaxNode} from "./syntax-node";
import {Syntax} from "./syntax";
import {CodeNode} from "./code-node";
import {Code} from "./code";
import {EditNode} from "./edit-node";

export class Edit {
  syntax = new Syntax();
  code = new Code();
  selectedEditNode = new EditNode();

  loadSyntax(syntaxFile: string) {
    this.syntax = Syntax.load(syntaxFile);
  }

  saveSyntax() {
    return this.syntax.save();
  }

  loadCode(codeFile: string) {
    this.code = Code.load(codeFile);
  }

  saveCode() {
    return this.code.save();
  }

  newCode() {
    this.code = new Code();
    this.selectedEditNode = new EditNode();
    let rootSyntaxNode = this.syntax.getRootSyntaxNode();
    let rootCodeNode = this.addCodeNode(new CodeNode(), rootSyntaxNode);
    this.code.rootCodeNodeId = rootCodeNode.id;
  }

  getSyntaxNodeChildren(syntaxNode: SyntaxNode) {
    let syntaxNodeChildren: SyntaxNode[] = [];
    for (let content of syntaxNode.contents) {
      syntaxNodeChildren.push(this.syntax.getSyntaxNode(content));
    }
    return syntaxNodeChildren;
  }

  getCodeNodePosition(codeNode: CodeNode) {
    let fatherCodeNode = this.code.getCodeNode(codeNode.fatherId);
    let index;
    for (index = 0; index < fatherCodeNode.contents.length; index++) {
      if (fatherCodeNode.contents[index] === codeNode.id) {
        break;
      }
    }
    return index;
  }

  addCodeNode(codeNode: CodeNode, contentSyntaxNode: SyntaxNode) {
    let contentCodeNodeId = String(this.code.nextCodeNodeId++);
    let contentCodeNode = new CodeNode();
    contentCodeNode.id = contentCodeNodeId;
    this.code.codeNodeMap.set(contentCodeNode.id, contentCodeNode);
    contentCodeNode.fatherId = codeNode.id;
    codeNode.contents.push(contentCodeNode.id);

    contentCodeNode.type = contentSyntaxNode.id;
    if (contentSyntaxNode.type === 'lexical') {
    } else if (contentSyntaxNode.type === 'terminal') {
      contentCodeNode.content = contentSyntaxNode.content;
    } else if (contentSyntaxNode.type === 'question') {
    } else if (contentSyntaxNode.type === 'plus') {
    } else if (contentSyntaxNode.type === 'asterisk') {
    } else if (contentSyntaxNode.type === 'concatenation') {
      for (let contentContent of contentSyntaxNode.contents) {
        let contentContentSyntaxNode = this.syntax.getSyntaxNode(contentContent);
        this.addCodeNode(contentCodeNode, contentContentSyntaxNode);
      }
    } else if (contentSyntaxNode.type === 'alternation') {
    }

    return contentCodeNode;
  }

  deleteCodeNode(codeNode: CodeNode) {
    let fatherCodeNode = this.code.getCodeNode(codeNode.fatherId);
    let index = this.getCodeNodePosition(codeNode);
    fatherCodeNode.contents.splice(index, 1);

    let contents = codeNode.contents.concat();
    for (let content of contents) {
      this.deleteCodeNode(this.code.getCodeNode(content));
    }

    this.code.codeNodeMap.delete(codeNode.id);
  }

  deleteCodeNodeContents(codeNode: CodeNode) {
    let contents = codeNode.contents.concat();
    for (let content of contents) {
      this.deleteCodeNode(this.code.getCodeNode(content));
    }
  }

  moveCodeNode(codeNode: CodeNode, direction: number) {
    let fatherCodeNode = this.code.getCodeNode(codeNode.fatherId);
    let index = this.getCodeNodePosition(codeNode);
    if (index + direction >= 0 && index + direction < fatherCodeNode.contents.length) {
      [fatherCodeNode.contents[index], fatherCodeNode.contents[index + direction]] = [fatherCodeNode.contents[index + direction], fatherCodeNode.contents[index]]
    }
  }

  setCodeNode(codeNode: CodeNode, contentSyntaxNode: SyntaxNode) {
    if (codeNode.contents.length !== 0) {
      this.deleteCodeNodeContents(codeNode);
    }
    this.addCodeNode(codeNode, contentSyntaxNode);
  }

  canAddCodeNode(codeNode: CodeNode) {
    let syntaxNode = this.syntax.getSyntaxNode(codeNode.type);
    return (syntaxNode.type === 'question' && syntaxNode.contents.length === 0)
      || syntaxNode.type === 'plus'
      || syntaxNode.type === 'asterisk';
  }

  canDeleteCodeNode(codeNode: CodeNode) {
    if (!codeNode.fatherId) {
      return false;
    }
    let fatherCodeNode = this.code.getCodeNode(codeNode.fatherId);
    let fatherSyntaxNode = this.syntax.getSyntaxNode(fatherCodeNode.type);
    return fatherSyntaxNode.type === 'question'
      || fatherSyntaxNode.type === 'plus'
      || fatherSyntaxNode.type === 'asterisk'
      || fatherSyntaxNode.type === 'alternation';
  }

  canMoveCodeNode(codeNode: CodeNode) {
    if (!codeNode.fatherId) {
      return false;
    }
    let fatherCodeNode = this.code.getCodeNode(codeNode.fatherId);
    let fatherSyntaxNode = this.syntax.getSyntaxNode(fatherCodeNode.type);
    return fatherSyntaxNode.type === 'question'
      || fatherSyntaxNode.type === 'plus'
      || fatherSyntaxNode.type === 'asterisk'
  }

  canSetCodeNode(codeNode: CodeNode) {
    let syntaxNode = this.syntax.getSyntaxNode(codeNode.type);
    return syntaxNode.type === 'alternation';
  }

  getEditNode(codeNodeId: string) {
    let codeNode = this.code.getCodeNode(codeNodeId);
    let syntaxNode = this.syntax.getSyntaxNode(codeNode.type);
    let editNode = new EditNode();
    editNode.syntaxNode = syntaxNode;
    editNode.codeNode = codeNode;
    return editNode;
  }

  getRootEditNode() {
    return this.getEditNode(this.code.rootCodeNodeId);
  }
}
