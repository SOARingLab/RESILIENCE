import CustomRenderer from './CustomRenderer';
import CustomContextPad from "./CustomContextPad";
import CustomPalette from "./CustomPalette";

export default {
    __init__: ['customContextPad', 'customPalette', 'customRenderer'],
    customContextPad: ['type', CustomContextPad],
    customPalette: ['type', CustomPalette],
    customRenderer: ['type', CustomRenderer]
};
