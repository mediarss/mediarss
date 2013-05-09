define([
	'marionette',
	'handlebars',
	'text!features/moviesTab/templates/movie-torrent-empty-list.tpl'
],
	function(Marionette, Handlebars, template) {
		"use strict";

		return Marionette.ItemView.extend({
			template: Handlebars.compile(template),
			className: 'movies-torrent-list-empty-label-container'
		});
	});