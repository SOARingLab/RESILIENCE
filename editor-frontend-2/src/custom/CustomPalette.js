export default class CustomPalette {
    constructor(bpmnFactory, create, elementFactory, palette, translate) {
        this.bpmnFactory = bpmnFactory;
        this.create = create;
        this.elementFactory = elementFactory;
        this.translate = translate;

        palette.registerProvider(this);
    }

    getPaletteEntries(element) {
        const {
            bpmnFactory,
            create,
            elementFactory,
            translate
        } = this;

        function createScriptAnnotation(color) {
            return function (event) {
                const businessObject = bpmnFactory.create('bpmn:TextAnnotation');

                businessObject.color = color;

                const shape = elementFactory.createShape({
                    type: 'bpmn:TextAnnotation',
                    businessObject: businessObject
                });

                create.start(event, shape);
            }
        }

        return {
            'create.script-annotation-green': {
                group: 'activity',
                className: 'bpmn-icon-text-annotation green',
                title: translate('Create Text Annotation'),
                action: {
                    dragstart: createScriptAnnotation('green'),
                    click: createScriptAnnotation('green')
                }
            }
        }
    }
}

CustomPalette.$inject = [
    'bpmnFactory',
    'create',
    'elementFactory',
    'palette',
    'translate'
];
