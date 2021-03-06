/*
 * File: app/view/winLoadDiagram.js
 *
 * This file was generated by Sencha Architect version 3.1.0.
 * http://www.sencha.com/products/architect/
 *
 * This file requires use of the Ext JS 5.0.x library, under independent license.
 * License of Sencha Architect does not include license for Ext JS 5.0.x. For more
 * details see http://www.sencha.com/license or contact license@sencha.com.
 *
 * This file will be auto-generated each and everytime you save your project.
 *
 * Do NOT hand edit this file.
 */

Ext.define('BPMNEditor.view.winLoadDiagram', {
    extend: 'Ext.window.Window',
    alias: 'widget.winloaddiagram',

    requires: [
        'BPMNEditor.view.winLoadDiagramViewModel',
        'Ext.form.Panel',
        'Ext.form.field.File',
        'Ext.toolbar.Toolbar',
        'Ext.button.Button'
    ],

    viewModel: {
        type: 'winloaddiagram'
    },
    height: 134,
    width: 360,
    layout: 'border',
    title: 'Load diagram',
    modal: true,

    items: [
        {
            xtype: 'form',
            region: 'center',
            bodyPadding: 10,
            title: '',
            items: [
                {
                    xtype: 'filefield',
                    anchor: '100%',
                    fieldLabel: 'File'
                }
            ],
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    items: [
                        {
                            xtype: 'button',
							action: 'uploadDiagram',
                            text: 'OK'
                        }
                    ]
                }
            ]
        }
    ]

});