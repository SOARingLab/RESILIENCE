import {Component, OnInit} from '@angular/core';
import {SyntaxNode} from "../../model/syntax-node";
import {Edit} from "../../model/edit";
import {EditService} from "../../service/edit.service";
import {Converter} from "../../model/converter";
import {Visitor} from "../../model/visitor";

@Component({
  selector: 'app-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.css']
})
export class EditComponent implements OnInit {

  edit: Edit;
  syntaxFile = '';
  codeFile = '';

  constructor(
    private editService: EditService
  ) {
    this.edit = this.editService.edit;
  }

  ngOnInit(): void {
  }

  loadSyntax() {
    this.edit.loadSyntax(this.syntaxFile);
  }

  saveSyntax() {
    this.syntaxFile = this.edit.saveSyntax();
  }

  loadCode() {
    this.edit.loadCode(this.codeFile);
  }

  saveCode() {
    this.codeFile = this.edit.saveCode();
  }

  newCode() {
    this.edit.newCode();
  }

  getSyntaxNodeChildren() {
    let syntaxNode = this.edit.selectedEditNode.syntaxNode;
    return this.edit.getSyntaxNodeChildren(syntaxNode);
  }

  addCodeNode() {
    let codeNode = this.edit.selectedEditNode.codeNode;
    let contentSyntaxNode = this.edit.syntax.getSyntaxNode(this.edit.selectedEditNode.syntaxNode.contents[0]);
    this.edit.addCodeNode(codeNode, contentSyntaxNode);
  }

  deleteCodeNode() {
    let codeNode = this.edit.selectedEditNode.codeNode;
    this.edit.deleteCodeNode(codeNode);
  }

  moveCodeNode(direction: number) {
    let codeNode = this.edit.selectedEditNode.codeNode;
    this.edit.moveCodeNode(codeNode, direction);
  }

  setCodeNode(contentSyntaxNode: SyntaxNode) {
    let codeNode = this.edit.selectedEditNode.codeNode;
    this.edit.setCodeNode(codeNode, contentSyntaxNode);
  }

  canAddCodeNode() {
    let codeNode = this.edit.selectedEditNode.codeNode;
    return this.edit.canAddCodeNode(codeNode);
  }

  canDeleteCodeNode() {
    let codeNode = this.edit.selectedEditNode.codeNode;
    return this.edit.canDeleteCodeNode(codeNode);
  }

  canMoveCodeNode() {
    let codeNode = this.edit.selectedEditNode.codeNode;
    return this.edit.canMoveCodeNode(codeNode);
  }

  canSetCodeNode() {
    let codeNode = this.edit.selectedEditNode.codeNode;
    return this.edit.canSetCodeNode(codeNode);
  }

  convert() {
    let edit = new Edit();
    edit.loadSyntax(this.edit.saveSyntax());
    edit.loadCode(this.edit.saveCode());
    let converter = new Converter(edit);
    this.syntaxFile = converter.convert().save();
  }

  visit() {
    let visitor = new Visitor(this.edit);
    this.codeFile = visitor.visit();
  }

  test() {
    console.log(this.edit.selectedEditNode);
  }
}
