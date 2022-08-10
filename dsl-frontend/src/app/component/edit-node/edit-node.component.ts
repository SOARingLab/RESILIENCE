import {Component, Input, OnInit} from '@angular/core';
import {EditNode} from "../../model/edit-node";
import {Edit} from "../../model/edit";
import {EditService} from "../../service/edit.service";
import {CodeNode} from "../../model/code-node";

@Component({
  selector: 'app-edit-node',
  templateUrl: './edit-node.component.html',
  styleUrls: ['./edit-node.component.css']
})
export class EditNodeComponent implements OnInit {

  @Input()
  editNode = new EditNode();
  edit: Edit;

  constructor(
    private editService: EditService
  ) {
    this.edit = this.editService.edit;
  }

  ngOnInit(): void {
  }

  select() {
    this.edit.selectedEditNode = this.editNode;
  }

  addCodeNode(codeNode: CodeNode) {
    let syntaxNode = this.edit.syntax.getSyntaxNode(codeNode.type);
    let contentSyntaxNode = this.edit.syntax.getSyntaxNode(syntaxNode.contents[0]);
    this.edit.addCodeNode(codeNode, contentSyntaxNode);
  }

  addCodeNodeToFather(codeNode: CodeNode) {
    let syntaxNode = this.edit.syntax.getSyntaxNode(codeNode.type);
    let fatherCodeNode = this.edit.code.getCodeNode(codeNode.fatherId);
    console.log(syntaxNode);
    console.log(fatherCodeNode);
    this.edit.addCodeNode(fatherCodeNode, syntaxNode);
  }

  deleteCodeNode(codeNode: CodeNode) {
    this.edit.deleteCodeNode(codeNode);
    this.edit.selectedEditNode = new EditNode();
  }

  // Simple

  getConditionPosition(codeNode: CodeNode) {
    let fatherCodeNode = this.edit.code.getCodeNode(codeNode.fatherId);
    return this.edit.getCodeNodePosition(fatherCodeNode);
  }
}
