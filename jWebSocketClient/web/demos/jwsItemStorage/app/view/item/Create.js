Ext.define('IS.view.item.Create', {
	extend: 'IS.view.base.Window',
	alias: 'widget.i_create',
	requires: ['IS.lib.Util'],
	iconCls: 'i_add',
	
	loadForEdit: function ( aCollectionName, aItemDefinition, aRecord ){
		this.loadForCreation(aCollectionName, aItemDefinition);
		
		this.setTitle('Edit item (' + aItemDefinition.type + ')');
		var lForm = this.down('form');
		
		lForm.getForm().getFields().each(function (aItem){
			aItem.setValue(aRecord.get(aItem.name));
		});
		if (aRecord.get('id')){
			this.recordId = aRecord.get('id');
		}
	},
	
	loadForCreation: function(aCollectionName, aItemDefinition){
		this.setTitle('Create item (' + aItemDefinition.type + ')');
		this.collectionName = aCollectionName;
		
		var lForm = this.down('form');
		
		if ('id' != aItemDefinition.pk_attr){
			lForm.add({
				xtype: 'textfield',
				name: aItemDefinition.pk_attr,
				fieldLabel: aItemDefinition.pk_attr,
				allowBlank: false
			});		
		}
		for (var lAttr in aItemDefinition.attr_types){
			if (lAttr != aItemDefinition.pk_attr){ // skip primary key attr
				lForm.add(IS.lib.Util.createFormField(lAttr, aItemDefinition.attr_types[lAttr]));
			}
		}
		
		this.showAt({
			y: 100
		});
	},
	
	initComponent: function() {
		this.items = [{
			xtype: 'form',
			bodyPadding: 10,
			border: 0,
			autoScroll: true,
			maxHeight: 300,
			width: 305,
			items: [] 
		}];

		this.buttons = [{
			text: 'Save',
			action: 'save'
		},{
			text: 'Cancel',
			scope: this,
			handler: this.close
		}];

		this.callParent(arguments);
	}
});
