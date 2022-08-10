export default class CustomContextPad {
    constructor(bpmnFactory, config, contextPad, create, elementFactory, injector, translate) {
        this.bpmnFactory = bpmnFactory;
        this.create = create;
        this.elementFactory = elementFactory;
        this.translate = translate;

        if (config.autoPlace !== false) {
            this.autoPlace = injector.get('autoPlace', false);
        }

        contextPad.registerProvider(this);
    }

    getContextPadEntries(element) {
        const {
            autoPlace,
            bpmnFactory,
            create,
            elementFactory,
            translate
        } = this;

        function appendScriptAnnotation(color) {
            return function (event, element) {
                if (autoPlace) {
                    const businessObject = bpmnFactory.create('bpmn:TextAnnotation');

                    businessObject.color = color;

                    const shape = elementFactory.createShape({
                        type: 'bpmn:TextAnnotation',
                        businessObject: businessObject
                    });

                    autoPlace.append(element, shape);
                } else {
                    appendScriptAnnotationStart(event, element);
                }
            }
        }

        function appendScriptAnnotationStart(color) {
            return function (event) {
                const businessObject = bpmnFactory.create('bpmn:TextAnnotation');

                businessObject.color = color;

                const shape = elementFactory.createShape({
                    type: 'bpmn:TextAnnotation',
                    businessObject: businessObject
                });

                create.start(event, shape, element);
            }
        }

        return {
            'append.script-annotation-green': {
                group: 'model',
                className: 'bpmn-icon-text-annotation green',
                title: translate('Add Text Annotation with DSL'),
                action: {
                    click: appendScriptAnnotation('green'),
                    dragstart: appendScriptAnnotationStart('green')
                }
            }
        };
    }
}

CustomContextPad.$inject = [
    'bpmnFactory',
    'config',
    'contextPad',
    'create',
    'elementFactory',
    'injector',
    'translate'
];
