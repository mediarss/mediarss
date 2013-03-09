/*global define*/
define([
	'marionette',
	'handlebars',
	'text!components/header/templates/header.tpl'
],
	function(Marionette, Handlebars, template) {
		"use strict";

		return Marionette.Layout.extend({
			template: Handlebars.compile(template),
			className: 'header-container',

			regions: {
				descriptionRegion: '.header-description'
			},

			constructor: function(options) {
				Marionette.Layout.prototype.constructor.apply(this, arguments);
				this.descriptionViewDef = options.descriptionViewDef;
				this.descriptionViewOptions = options.descriptionViewOptions;
			},

			onRender: function() {
				this.descriptionRegion.show(new this.descriptionViewDef(this.descriptionViewOptions));
			}
		});
	});
