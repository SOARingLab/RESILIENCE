import $ from 'jquery';
import BpmnModeler from 'bpmn-js/lib/Modeler';

import {debounce} from 'min-dash';

import {BpmnPropertiesPanelModule, BpmnPropertiesProviderModule} from 'bpmn-js-properties-panel';

import diagramXML from '../resources/newDiagram.bpmn';

import {getBusinessObject} from 'bpmn-js/lib/util/ModelUtil';

import customModule from './custom';

import customPropertiesProviderModule from './provider/custom';

import constraintExtension from '../resources/constraint.json';

const HIGH_PRIORITY = 1500;

var container = $('#js-drop-zone');

var canvas = $('#js-canvas');

var bpmnModeler = new BpmnModeler({
    container: canvas,
    propertiesPanel: {
        parent: '#js-properties-panel'
    },
    additionalModules: [
        BpmnPropertiesPanelModule,
        BpmnPropertiesProviderModule,
        customModule,
        customPropertiesProviderModule
    ],
    moddleExtensions: {
        constraint: constraintExtension
    }
});
container.removeClass('with-diagram');

function createNewDiagram() {
    openDiagram(diagramXML);
}

async function openDiagram(xml) {

    try {

        await bpmnModeler.importXML(xml);

        container
            .removeClass('with-error')
            .addClass('with-diagram');
    } catch (err) {

        container
            .removeClass('with-diagram')
            .addClass('with-error');

        container.find('.error pre').text(err.message);

        console.error(err);
    }
}

function registerFileDrop(container, callback) {

    function handleFileSelect(e) {
        e.stopPropagation();
        e.preventDefault();

        var files = e.dataTransfer.files;

        var file = files[0];

        var reader = new FileReader();

        reader.onload = function (e) {

            var xml = e.target.result;

            callback(xml);
        };

        reader.readAsText(file);
    }

    function handleDragOver(e) {
        e.stopPropagation();
        e.preventDefault();

        e.dataTransfer.dropEffect = 'copy'; // Explicitly show this is a copy.
    }

    container.get(0).addEventListener('dragover', handleDragOver, false);
    container.get(0).addEventListener('drop', handleFileSelect, false);
}


////// file drag / drop ///////////////////////

// check file api availability
if (!window.FileList || !window.FileReader) {
    window.alert(
        'Looks like you use an older browser that does not support drag and drop. ' +
        'Try using Chrome, Firefox or the Internet Explorer > 10.');
} else {
    registerFileDrop(container, openDiagram);
}

// bootstrap diagram functions

$(function () {

    $('#js-create-diagram').click(function (e) {
        e.stopPropagation();
        e.preventDefault();

        createNewDiagram();
    });

    var downloadLink = $('#js-download-diagram');
    var downloadSvgLink = $('#js-download-svg');

    $('.buttons a').click(function (e) {
        if (!$(this).is('.active')) {
            e.preventDefault();
            e.stopPropagation();
        }
    });

    function setEncoded(link, name, data) {
        var encodedData = encodeURIComponent(data);

        if (data) {
            link.addClass('active').attr({
                'href': 'data:application/bpmn20-xml;charset=UTF-8,' + encodedData,
                'download': name
            });
        } else {
            link.removeClass('active');
        }
    }

    var exportArtifacts = debounce(async function () {

        try {

            const {svg} = await bpmnModeler.saveSVG();

            setEncoded(downloadSvgLink, 'diagram.svg', svg);
        } catch (err) {

            console.error('Error happened saving SVG: ', err);

            setEncoded(downloadSvgLink, 'diagram.svg', null);
        }

        try {

            const {xml} = await bpmnModeler.saveXML({format: true});

            setEncoded(downloadLink, 'diagram.bpmn', xml);
        } catch (err) {

            console.log('Error happened saving XML: ', err);

            setEncoded(downloadLink, 'diagram.bpmn', null);
        }
    }, 500);

    bpmnModeler.on('commandStack.changed', exportArtifacts);

    bpmnModeler.on('directEditing.activate', async (event) => {

        let {active} = event;

        let {element} = active;

        let {id, type, businessObject} = element;

        let {color} = businessObject

        if (type === 'bpmn:TextAnnotation') {
            if (color === 'green') {
                const {xml} = await bpmnModeler.saveXML({format: true});
                localStorage.setItem('file', xml);
                localStorage.setItem('id', id);
                window.location.href = '/editor';
            }
        }
    });

    let file = localStorage.getItem('file');
    if (file) {
        const id = localStorage.getItem("id");
        const text = localStorage.getItem("text");

        const stringBpmn = "<bpmn:textAnnotation id=\"" + id + "\" />";
        const stringBpmn2 = "<bpmn2:textAnnotation id=\"" + id + "\" />";

        const replaceBpmn = "<bpmn:textAnnotation id=\"" + id + "\">\n\t<bpmn:text>" + text + "</bpmn:text>\n</bpmn:textAnnotation>";
        const replaceBpmn2 = "<bpmn2:textAnnotation id=\"" + id + "\">\n\t<bpmn2:text>" + text + "</bpmn2:text>\n</bpmn2:textAnnotation>";

        file = file.replace(stringBpmn, replaceBpmn);
        file = file.replace(stringBpmn2, replaceBpmn2);

        openDiagram(file);
    } else {
        localStorage.setItem('filename', 'diagram.bpmn');
    }
});

$("#js-open-diagram").click(() => {
    const file = $("#open-diagram")[0].files[0];
    const reader = new FileReader();
    reader.readAsText(file, 'utf-8');
    reader.onload = () => {
        openDiagram(reader.result);
        localStorage.setItem('filename', file.name);
    };
});

$("#js-deploy-diagram").click(async () => {
    const {xml} = await bpmnModeler.saveXML({format: true});
    localStorage.setItem('file', xml);
    localStorage.setItem('withAnnotations', 'no');
    window.location.href = '/deploy';
});

$("#js-deploy-with-annotations").click(async () => {
    const {xml} = await bpmnModeler.saveXML({format: true});
    localStorage.setItem('file', xml);
    localStorage.setItem('withAnnotations', 'yes');
    window.location.href = '/deploy';
});

$("#js-verify").click(async () => {
    const {xml} = await bpmnModeler.saveXML({format: true});
    const startNodeName = $("#start-node-name").val();
    const SLIs = $("#sli-list").val();
    localStorage.setItem('file', xml);
    localStorage.setItem('start', startNodeName);
    localStorage.setItem('SLIs', SLIs);
    window.location.href = '/verify';
});

$("#js-verify-clear").click(async () => {
    localStorage.removeItem('resultFunctionalDetail');
    localStorage.removeItem('resultNonFunctionalDetail');
    const {xml} = await bpmnModeler.saveXML({format: true});
    localStorage.setItem('file', xml);
    location.reload();
});
