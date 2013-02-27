Ext.define('IS.view.item.History', {
	extend: 'IS.view.base.Window',
	alias: 'widget.i_history',

	title: 'History viewer',
	autoShow: true,
	width: 500,
	height: 400,
	iconCls: 'history',
	modal: false,
	
	loadHistory: function( aCollection, aItemPK ){
		this.setTitle( 'History viewer: ' + aCollection + ' - ' + aItemPK);
		
		var lCollection = aCollection;
		var lItemPK = aItemPK;
		var lStore= Ext.create('Ext.data.Store', {
			model: 'IS.model.ItemLog',
			proxy: Ext.create('Ext.jws.data.Proxy', {
				type: 'jws',
				ns: jws.ItemStoragePlugIn.NS,
				api: {
					read: 'getLogs'
				},
				reader: {
					type: 'jws'
				},
				transform: function ( aRequest ){
					if ( 'getLogs' == aRequest.type ){
						aRequest.data.offset = aRequest.data.start;
						aRequest.data.length = aRequest.data.limit;
						aRequest.data.eType = 'item';
				
						// setting the collection name
						aRequest.data.collectionName = lCollection;
						aRequest.data.itemPK = lItemPK;

						delete aRequest.data.start;
						delete aRequest.data.limit;
						delete aRequest.data.page;
					}
				}
			})
		});
		
		this.removeAll();
		this.add({
			xtype: 'grid',
			autoScroll: true,
			sortableColumns: false,
			viewConfig: {
				loadMask: false
			},
			store: lStore,
			border: 0,
			columns: [{
				header: 'Action',  
				dataIndex: 'action',  
				flex: 1
			},{
				header: 'User',  
				dataIndex: 'user',  
				flex: 1,
				renderer: function( aUser ){
					return (Ext.jws.getConnection().getUsername() == aUser) ? 
					'<b>' + aUser + '</b>' : aUser;
				}
			},{
				header: 'Time',  
				dataIndex: 'time',  
				flex: 1,
				renderer: function ( aTime ){
					return new Date(aTime);
				},
				minWidth: 250
			}],
			dockedItems: [{
				xtype: 'pagingtoolbar',
				dock: 'bottom',
				store: lStore
			}]
		});
		
		this.down('pagingtoolbar').moveFirst();
		this.showAt({
			y: 100
		});
	},
	
	initComponent: function() {
	
		this.callParent(arguments);
	},
	close: function(){
		this.hide();
	}
});