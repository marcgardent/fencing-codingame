import { GraphicEntityModule } from './entity-module/GraphicEntityModule.js';
import { TooltipModule } from './tooltip-module/TooltipModule.js';
import { ToggleModule } from './toggle-module/ToggleModule.js'

export const playerColors = [
  '#49cc35', // green
  '#ff0000' // red
];

export const modules = [
	GraphicEntityModule,
	TooltipModule,
	ToggleModule
];

// The list of toggles displayed in the options of the viewer
export const options = [
  ToggleModule.defineToggle({
    // The name of the toggle
    // replace "myToggle" by the name of the toggle you want to use
    toggle: 'debugInfo',
    // The text displayed over the toggle
    title: 'Debug Info',
    // The labels for the on/off states of your toggle
    values: {
      'TOGGLED ON': true,
      'TOGGLED OFF': false
    },
    // Default value of your toggle
    default: false
  })
]