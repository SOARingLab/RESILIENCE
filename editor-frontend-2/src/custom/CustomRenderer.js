import BaseRenderer from 'diagram-js/lib/draw/BaseRenderer';

import {append as svgAppend, attr as svgAttr, classes as svgClasses, create as svgCreate} from 'tiny-svg';

import {getFillColor, getRoundRectPath, getSemantic, getStrokeColor} from 'bpmn-js/lib/draw/BpmnRenderUtil';

import {getBusinessObject, is} from 'bpmn-js/lib/util/ModelUtil';

import {assign, isNil} from 'min-dash';

import Ids from 'ids';

import {
    query as domQuery
} from 'min-dom';

import $ from 'jquery';

const HIGH_PRIORITY = 1500,
    TASK_BORDER_RADIUS = 2,
    COLOR_RED = '#CC0000',
    COLOR_GREEN = '#008000',
    COLOR_BLUE = '#0066CC',
    COLOR_ORANGE = '#FFA500',
    COLOR_WHITE = '#FFFFFF',
    COLOR_BLACK = '#000000';

var RENDERER_IDS = new Ids();

var rendererId = RENDERER_IDS.next();

var markers = {};

var canvas = $('#js-canvas');

export default class CustomRenderer extends BaseRenderer {
    constructor(eventBus, bpmnRenderer) {
        super(eventBus, HIGH_PRIORITY);

        this.bpmnRenderer = bpmnRenderer;
    }

    canRender(element) {

        // ignore labels
        return !element.labelTarget;
    }

    drawShape(parentNode, element) {
        const shape = this.bpmnRenderer.drawShape(parentNode, element);

        // annotation

        const {type, businessObject, di} = element;

        const {color} = businessObject;

        if (type === 'bpmn:TextAnnotation') {
            if (color === 'green') {
                di.set('bioc:stroke', color);
            }
        }

        // constraint

        let width = 0, height = 0;

        if (shape.width && shape.height) {
            width = shape.width.baseVal.value;
            height = shape.height.baseVal.value;
        } else if (shape.r) {
            width = shape.r.baseVal.value * 2;
            height = shape.r.baseVal.value * 2;
        }

        const declarative = this.getDeclarative(element);
        const temporal = this.getTemporal(element);
        const declarativeColor = this.getDeclarativeColor(element);
        const temporalColor = this.getTemporalColor(element);

        if (!isNil(declarative)) {
            let stroke = COLOR_RED;

            if (!isNil(declarativeColor)) {
                stroke = declarativeColor;
            }

            const rect = drawRect(parentNode, 50, 20, TASK_BORDER_RADIUS, stroke);

            svgAttr(rect, {
                transform: 'translate(' + (width / 2 - 25) + ', ' + (-20) + ')'
            });

            const text = svgCreate('text');

            svgAttr(text, {
                fill: stroke,
                transform: 'translate(' + (width / 2 - 20) + ', ' + (-5) + ')'
            });

            svgClasses(text).add('djs-label');

            svgAppend(text, document.createTextNode(declarative));

            svgAppend(parentNode, text);
        }

        if (!isNil(temporal)) {
            let stroke = COLOR_GREEN;

            if (!isNil(temporalColor)) {
                stroke = temporalColor;
            }

            const rect = drawRect(parentNode, 50, 20, TASK_BORDER_RADIUS, stroke);

            svgAttr(rect, {
                transform: 'translate(' + (width / 2 - 25) + ', ' + (height) + ')'
            });

            const text = svgCreate('text');

            svgAttr(text, {
                fill: stroke,
                transform: 'translate(' + (width / 2 - 20) + ', ' + (height + 15) + ')'
            });

            svgClasses(text).add('djs-label');

            svgAppend(text, document.createTextNode(temporal));

            svgAppend(parentNode, text);
        }

        return shape;
    }

    drawConnection(parentNode, element) {
        // constraint

        const {di} = element;

        const declarative = this.getDeclarative(element);
        const temporal = this.getTemporal(element);
        const declarativeColor = this.getDeclarativeColor(element);
        const temporalColor = this.getTemporalColor(element);

        if (!isNil(declarative)) {
            let pathData = createPathFromConnection(element);

            let fill = COLOR_RED,
                stroke = COLOR_RED;

            if (!isNil(declarativeColor)) {
                fill = declarativeColor;
                stroke = declarativeColor;
            }

            let attrs = {
                strokeLinejoin: 'round',
                stroke: stroke
            };

            let path = drawPath(parentNode, pathData, attrs);

            if (declarative === 'response') {
                svgAttr(path, {
                    markerStart: marker('declarative-circle', fill, stroke),
                    markerEnd: marker('declarative-arrow-end', fill, stroke),
                });
            }

            if (declarative === 'precedence') {
                svgAttr(path, {
                    markerEnd: marker('declarative-circle-arrow-end', fill, stroke),
                });
            }

            return path;
        } else if (!isNil(temporal)) {
            let pathData = createPathFromConnection(element);

            let fill = COLOR_ORANGE,
                stroke = COLOR_ORANGE;

            if (!isNil(temporalColor)) {
                // fill = temporalColor;
                // stroke = temporalColor;
                di.set('bioc:stroke', temporalColor);
            }

            let attrs = {
                strokeLinejoin: 'round',
                markerEnd: marker('temporal-end', fill, stroke),
                stroke: stroke
            };

            let path = drawPath(parentNode, pathData, attrs);

            return path;
        }

        return this.bpmnRenderer.drawConnection(parentNode, element);
    }

    getShapePath(shape) {
        if (is(shape, 'bpmn:Task')) {
            return getRoundRectPath(shape, TASK_BORDER_RADIUS);
        }

        return this.bpmnRenderer.getShapePath(shape);
    }

    getConnectionPath(connection) {


        return this.bpmnRenderer.getConnectionPath(connection);
    }

    getDeclarative(element) {
        const businessObject = getBusinessObject(element);

        const {declarative} = businessObject;

        return declarative;
    }

    getTemporal(element) {
        const businessObject = getBusinessObject(element);

        const {temporal} = businessObject;

        return temporal;
    }

    getDeclarativeColor(element) {
        const businessObject = getBusinessObject(element);

        const {declarativeColor} = businessObject;

        return declarativeColor;
    }

    getTemporalColor(element) {
        const businessObject = getBusinessObject(element);

        const {temporalColor} = businessObject;

        return temporalColor;
    }
}

CustomRenderer.$inject = ['eventBus', 'bpmnRenderer'];

// helpers //////////

// copied from https://github.com/bpmn-io/bpmn-js/blob/master/lib/draw/BpmnRenderer.js
function drawRect(parentNode, width, height, borderRadius, color) {
    const rect = svgCreate('rect');

    svgAttr(rect, {
        width: width,
        height: height,
        rx: borderRadius,
        ry: borderRadius,
        stroke: color,
        strokeWidth: 2,
        fill: COLOR_WHITE
    });

    svgAppend(parentNode, rect);

    return rect;
}

function drawPath(parentGfx, d, attrs) {

    var path = svgCreate('path');
    svgAttr(path, {d: d});
    svgAttr(path, attrs);
    svgAttr(path, {
        strokeWidth: 2
    });

    svgAppend(parentGfx, path);

    return path;
}

function createPathFromConnection(connection) {
    var waypoints = connection.waypoints;

    var pathData = 'm  ' + waypoints[0].x + ',' + waypoints[0].y;
    for (var i = 1; i < waypoints.length; i++) {
        pathData += 'L' + waypoints[i].x + ',' + waypoints[i].y + ' ';
    }
    return pathData;
}

function addMarker(id, options) {
    var attrs = assign({
        fill: 'black',
        strokeWidth: 1,
        strokeLinecap: 'round',
        strokeDasharray: 'none'
    }, options.attrs);

    var ref = options.ref || {x: 0, y: 0};

    var scale = options.scale || 1;

    // fix for safari / chrome / firefox bug not correctly
    // resetting stroke dash array
    if (attrs.strokeDasharray === 'none') {
        attrs.strokeDasharray = [10000, 1];
    }

    var marker = svgCreate('marker');

    svgAttr(options.element, attrs);

    svgAppend(marker, options.element);

    svgAttr(marker, {
        id: id,
        viewBox: '0 0 20 20',
        refX: ref.x,
        refY: ref.y,
        markerWidth: 20 * scale,
        markerHeight: 20 * scale,
        orient: 'auto'
    });

    var defs = domQuery('defs', canvas._svg);

    if (!defs) {
        defs = svgCreate('defs');

        svgAppend(canvas._svg, defs);
    }

    svgAppend(defs, marker);

    markers[id] = marker;
}

function colorEscape(str) {

    // only allow characters and numbers
    return str.replace(/[^0-9a-zA-z]+/g, '_');
}

function marker(type, fill, stroke) {
    var id = type + '-' + colorEscape(fill) + '-' + colorEscape(stroke) + '-' + rendererId;

    if (!markers[id]) {
        createMarker(id, type, fill, stroke);
    }

    return 'url(#' + id + ')';
}

function createMarker(id, type, fill, stroke) {

    if (type === 'declarative-circle') {
        let declarativeCircle = svgCreate('circle');
        svgAttr(declarativeCircle, {cx: 6, cy: 6, r: 5});

        addMarker(id, {
            element: declarativeCircle,
            ref: {x: 1, y: 6},
            scale: 0.5,
            attrs: {
                fill: fill,
                stroke: stroke
            }
        });
    }

    if (type === 'declarative-arrow-end') {
        let declarativeArrowEnd = svgCreate('path');
        svgAttr(declarativeArrowEnd, {d: 'M 1 5 L 11 10 L 1 15 Z'});

        addMarker(id, {
            element: declarativeArrowEnd,
            ref: {x: 11, y: 10},
            scale: 0.5,
            attrs: {
                fill: fill,
                stroke: stroke
            }
        });
    }

    if (type === 'declarative-circle-arrow-end') {
        let circleArrowEnd = svgCreate('g');

        let circle = svgCreate('circle');
        svgAttr(circle, {cx: 15, cy: 10, r: 5});

        let arrowEnd = svgCreate('path');
        svgAttr(arrowEnd, {d: 'M 1 5 L 11 10 L 1 15 Z'});

        svgAppend(circleArrowEnd, circle);
        svgAppend(circleArrowEnd, arrowEnd);

        addMarker(id, {
            element: circleArrowEnd,
            ref: {x: 20, y: 10},
            scale: 0.5,
            attrs: {
                fill: fill,
                stroke: stroke
            }
        });
    }

    if (type === 'temporal-end') {
        let temporalEnd = svgCreate('path');
        svgAttr(temporalEnd, {d: 'M 1 5 L 11 10 L 1 15 Z'});

        addMarker(id, {
            element: temporalEnd,
            ref: {x: 11, y: 10},
            scale: 0.5,
            attrs: {
                fill: fill,
                stroke: stroke
            }
        });
    }
}
