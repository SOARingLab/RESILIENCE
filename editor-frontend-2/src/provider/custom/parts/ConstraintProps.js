import {TextFieldEntry, isTextFieldEntryEdited, SelectEntry, isSelectEntryEdited} from '@bpmn-io/properties-panel';
import {useService} from 'bpmn-js-properties-panel';

export default function (element) {

    return [
        {
            id: 'declarative',
            element,
            component: Declarative,
            // isEdited: isTextFieldEntryEdited
            isEdited: isSelectEntryEdited
        },
        {
            id: 'temporal',
            element,
            component: Temporal,
            isEdited: isTextFieldEntryEdited
        },
        {
            id: 'declarativeColor',
            element,
            component: DeclarativeColor,
            isEdited: isTextFieldEntryEdited
        },
        {
            id: 'temporalColor',
            element,
            component: TemporalColor,
            isEdited: isTextFieldEntryEdited
        }
    ];
}

function Declarative(props) {
    const {element, id} = props;

    const modeling = useService('modeling');
    const translate = useService('translate');
    // const debounce = useService('debounceInput');

    const getValue = () => {
        return element.businessObject.declarative || '';
    }

    const setValue = value => {
        if (value === '') {
            value = null;
        }
        return modeling.updateProperties(element, {
            declarative: value
        });
    }

    const getOptions = () => {
        return [
            {label: '', value: '', disabled: false},
            {label: '●---', value: 'responded existence', disabled: false},
            {label: '●--●', value: 'co-existence', disabled: false},
            {label: '●--⯈', value: 'response', disabled: false},
            {label: '--⯈●', value: 'precedence', disabled: false},
            {label: '●-⯈●', value: 'succession', disabled: false}
        ];
    }

    // return <TextFieldEntry
    //     id={id}
    //     element={element}
    //     description={translate('Add declarative constraint')}
    //     label={translate('Declarative')}
    //     getValue={getValue}
    //     setValue={setValue}
    //     debounce={debounce}
    // />

    return <SelectEntry
        id={id}
        element={element}
        description={translate('Add declarative constraint')}
        label={translate('Declarative')}
        getValue={getValue}
        setValue={setValue}
        getOptions={getOptions}
        disabled={false}
    />
}

function Temporal(props) {
    const {element, id} = props;

    const modeling = useService('modeling');
    const translate = useService('translate');
    const debounce = useService('debounceInput');

    const getValue = () => {
        return element.businessObject.temporal || '';
    }

    const setValue = value => {
        return modeling.updateProperties(element, {
            temporal: value
        });
    }

    return <TextFieldEntry
        id={id}
        element={element}
        description={translate('Add temporal constraint')}
        label={translate('Temporal')}
        getValue={getValue}
        setValue={setValue}
        debounce={debounce}
    />
}

function DeclarativeColor(props) {
    const {element, id} = props;

    const modeling = useService('modeling');
    const translate = useService('translate');
    const debounce = useService('debounceInput');

    const getValue = () => {
        return element.businessObject.declarativeColor || '';
    }

    const setValue = value => {
        return modeling.updateProperties(element, {
            declarativeColor: value
        });
    }

    return <TextFieldEntry
        id={id}
        element={element}
        description={translate('Set declarative color')}
        label={translate('Declarative color')}
        getValue={getValue}
        setValue={setValue}
        debounce={debounce}
    />
}

function TemporalColor(props) {
    const {element, id} = props;

    const modeling = useService('modeling');
    const translate = useService('translate');
    const debounce = useService('debounceInput');

    const getValue = () => {
        return element.businessObject.temporalColor || '';
    }

    const setValue = value => {
        return modeling.updateProperties(element, {
            temporalColor: value
        });
    }

    return <TextFieldEntry
        id={id}
        element={element}
        description={translate('Set temporal color')}
        label={translate('Temporal color')}
        getValue={getValue}
        setValue={setValue}
        debounce={debounce}
    />
}
