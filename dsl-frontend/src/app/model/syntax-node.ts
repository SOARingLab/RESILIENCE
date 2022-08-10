export class SyntaxNode {
  id = '';
  name = '';
  type = ''; // lexical, terminal, question, plus, asterisk, concatenation, alternation
  content = '';
  contents: string[] = [];
  comment = '';
}
