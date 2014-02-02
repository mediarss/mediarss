/*global define*/
define([
	'marionette',
	'handlebars',
	'text!features/homeTab/templates/home-tab.tpl',
	'components/section/views/SectionView',
	'zeroClipboard',
	'jqplugin',
	'chosen',
	'MessageBox',
	'utils/Utils',
	'HttpUtils'
],
	function(Marionette, Handlebars, template, SectionView, ZeroClipboard, jqPlugin, Chosen, MessageBox, Utils, HttpUtils) {
		"use strict";

		var SUBTITLES_NONE = 'None';
		return Marionette.Layout.extend({
			template: Handlebars.compile(template),
			className: 'home-tab',

			ui: {
				tvShowsCopyLinkNotification: '.tvshows-feed-copy-link-notification',
				moviesCopyLinkNotification: '.movies-feed-copy-link-notification'
			},

			regions: {
				rssFeedsSectionRegion: '.rss-feeds-section'
			},

			constructor: function(options) {
				Marionette.Layout.prototype.constructor.apply(this, arguments);

				this.rssFeedsSection = new SectionView({
					title: 'Your personalized RSS feeds',
					description: 'Those are your personalized rss feeds. You can add them to your torrent client (such as uTorrent)'
				});
				this.subtitlesSection = new SectionView({
					title: 'Subtitles (in construction)',
					description: 'Subtitles could be added to your rss feeds (both movies and tv shows)'
				});
			},

			onRender: function() {
				var that = this;
				HttpUtils.get('rest/user/initial-data', function(res) {
					that.tabData = res;
					that.rssFeedsSectionRegion.show(that.rssFeedsSection);

					// register copy to clipboard
					that.setCopyToClipboard();
				}, false);
			},

			setCopyToClipboard: function() {
				this.$el.find('#tvshows-feed-copy-link').attr('data-clipboard-text', this.tabData.tvShowsRssFeed);
				this.$el.find('#movies-feed-copy-link').attr('data-clipboard-text', this.tabData.moviesRssFeed);

				if ($.browser.flash == true) {
//					console.log('YES FLASH');
					var clip = new ZeroClipboard([this.$el.find('#tvshows-feed-copy-link')[0], this.$el.find('#movies-feed-copy-link')[0]], {
						moviePath: "ZeroClipboard.swf"
					});
					clip.on('complete', function(client, args) {
						console.log("Copied text to clipboard: " + args.text + ' ' + this.id);
						var notification;
						if (this.id.indexOf('movies') > -1) {
							notification = $('.movies-feed-copy-link-notification');
						} else {
							notification = $('.tvshows-feed-copy-link-notification');
						}
						notification.fadeIn('slow', function() {
							setTimeout(function() {
								notification.fadeOut('slow');
							}, 2000);
						});
					});
				} else {
//					console.log('no FLASH');
					this.$el.find('#tvshows-feed-copy-link')
						.attr('target', '_blank')
						.attr('href', this.tabData.tvShowsRssFeed);
					this.$el.find('#movies-feed-copy-link')
						.attr('target', '_blank')
						.attr('href', this.tabData.moviesRssFeed);
				}
			},

			createChosen: function(selector) {
				$(selector).chosen();

				if (this.tabData.userSubtitles) {
					$(selector).val(this.tabData.userSubtitles);
					$(selector).trigger("liszt:updated");
				}
			}
		});
	});
