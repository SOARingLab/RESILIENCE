import {Component, OnInit} from '@angular/core';
import {Grammar} from "../../model/grammar";

@Component({
  selector: 'app-generator',
  templateUrl: './generator.component.html',
  styleUrls: ['./generator.component.css']
})
export class GeneratorComponent implements OnInit {

  grammarConditions = [
    [['risk_level'], ['==', '!='], ['"low"', '"medium"', '"high"']],
    [['delivery_method'], ['==', '!='], ['"home_delivery"', '"contactless_locker"', '"at_store"']],
  ];
  grammarActions = [
    [['order_status'], ['='], ['"running"', '"finished"', '"canceled"']],
    [['delivery_method'], ['='], ['"home_delivery"', '"contactless_locker"', '"at_store"']],
    [['amount'], ['='], ['']],
  ];

  constructor() {
  }

  ngOnInit(): void {
    let grammarString = localStorage.getItem("grammar");
    if (grammarString) {
      let grammar = JSON.parse(grammarString);
      this.grammarConditions = grammar.grammarConditions;
      this.grammarActions = grammar.grammarActions;
    }
  }

  range(num: number) {
    let arr = [];
    for (let i = 0; i < num; i++) {
      arr.push(i);
    }
    return arr;
  }

  addGrammarCondition(index: number) {
    this.grammarConditions.splice(index + 1, 0, [[''], [''], ['']]);
  }

  deleteGrammarCondition(index: number) {
    this.grammarConditions.splice(index, 1);
  }

  addGrammarAction(index: number) {
    this.grammarActions.splice(index + 1, 0, [[''], [''], ['']]);
  }

  deleteGrammarAction(index: number) {
    this.grammarActions.splice(index, 1);
  }

  addElement(array: string[], index: number) {
    array.splice(index + 1, 0, '');
  }

  deleteElement(array: string[], index: number) {
    array.splice(index, 1);
  }

  saveGrammar() {
    let grammar = new Grammar();
    grammar.grammarConditions = this.grammarConditions;
    grammar.grammarActions = this.grammarActions;
    let grammarString = JSON.stringify(grammar);
    localStorage.setItem("grammar", grammarString);
  }
}
