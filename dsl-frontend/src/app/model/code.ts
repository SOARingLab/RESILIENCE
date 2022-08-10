import {CodeNode} from "./code-node";

export class Code {
  name = '';
  codeNodeList: CodeNode[] = [];
  codeNodeMap = new Map<string, CodeNode>();
  rootCodeNodeId = '';
  nextCodeNodeId = 0;

  static load(codeFile: string) {
    let code = JSON.parse(codeFile) as Code;
    let newCode = new Code();
    newCode.name = code.name;
    newCode.codeNodeList = code.codeNodeList;
    for (let codeNode of newCode.codeNodeList) {
      newCode.codeNodeMap.set(codeNode.id, codeNode);
    }
    newCode.rootCodeNodeId = code.rootCodeNodeId;
    newCode.nextCodeNodeId = code.nextCodeNodeId;
    return newCode;
  }

  save() {
    this.codeNodeList = [...this.codeNodeMap.values()];
    return JSON.stringify(this);
  }

  getCodeNode(id: string) {
    return this.codeNodeMap.get(id) as CodeNode;
  }

  getRootCodeNode() {
    return this.getCodeNode(this.rootCodeNodeId);
  }
}
