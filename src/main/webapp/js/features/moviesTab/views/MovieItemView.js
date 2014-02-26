define([
	'jquery',
	'marionette',
	'handlebars',
	'text!features/moviesTab/templates/movie-item.tpl',
	'utils/Utils',
	'components/search-result/views/SearchResultsCollectionView',
	'features/collections/UserTorrentCollection'
],
	function($, Marionette, Handlebars, template, Utils, SearchResultsCollectionView, UserTorrentCollection) {
		"use strict";

		var MAX_NOT_VIEWED_TORRENTS_TO_DISPLAY = 3;

		return Marionette.Layout.extend({
			template: Handlebars.compile(template),
			className: 'movie-item',

			ui: {
				scheduledImage: '.movie-item-scheduled-image',
				downloadedImage: '.movie-item-downloaded-image',
				searchingImage: '.movie-item-searching-image',
				movieTitle: '.movie-item-title',
				futureImage: '.movie-item-future-image',
				subTitle: '.movie-sub-title',
				scheduledOn: '.movie-scheduled-on',
				collapseLink: '.movie-item-torrents-collapse',
				showAllLink: '.movie-item-torrents-show-all',
				statusIconsContainer: '.movie-item-icon-wrapper',
				titleWrapper: '.movie-item-title-wrapper',
				newTorrentsLabel: '.movie-item-new-label'
			},

			events: {
				'click': 'onMovieClick',
				'click .future-movie-item-remove-image': 'onFutureMovieRemoveClick',
				'click .movie-item-torrents-show-all': '_onShowAllClick',
				'click .movie-item-torrents-collapse': '_onCollapseClick'
			},

			regions: {
				torrentsListRegion: '.movie-item-torrents'
			},

			constructor: function(options) {
				this.vent = options.vent;
				Marionette.Layout.prototype.constructor.apply(this, arguments);

				var that = this;
				this.model.on('change:downloadStatus', function() {
					that.updateDownloadStatus();
				});

				this.movieTorrentCollection = new UserTorrentCollection();
				this.movieTorrentCollectionView = new SearchResultsCollectionView({
					vent: this.vent,
					collection: this.movieTorrentCollection
				});

				// count how many torrents are in not-viewed state
				this.viewedTorrents = [];
				var viewedCounter = 0;
				this.notViewedTorrents = [];
				this.notViewedCounter = 0;
				var torrents = this.model.get('torrents');
				for (var i = 0; i < torrents.length; ++i) {
					if (!torrents[i].viewed) {
						this.notViewedTorrents[this.notViewedCounter++] = torrents[i];
					} else {
						this.viewedTorrents[viewedCounter++] = torrents[i];
					}
				}
				this._showNotViewedTorrents();
			},

			_showNotViewedTorrents: function() {
				if (this.notViewedCounter <= 6) {
					this.movieTorrentCollection.reset(this.notViewedTorrents);
				} else {
					this.movieTorrentCollection.reset(this.notViewedTorrents.slice(0, MAX_NOT_VIEWED_TORRENTS_TO_DISPLAY));
				}
			},

			onRender: function() {
				if (!this.model.get('viewed')) {
					this.$el.addClass('movie-item-not-viewed');
				}

				this.updateDownloadStatus();

				Utils.addTooltip([this.ui.scheduledImage, this.ui.downloadedImage, this.ui.movieTitle, this.ui.futureImage, this.ui.searchingImage]);

				var that = this;
				$('.movie-show-preview-' + this.model.get('id')).fancybox({
					'width': '800',
					'height': '75%',
					'autoScale': false,
					'transitionIn': 'none',
					'transitionOut': 'none',
					'type': 'iframe',
					'beforeLoad': function() {
						that.vent.trigger('movie-selected', that.model);
						return true;
					}
				});

				this.torrentsListRegion.show(this.movieTorrentCollectionView);

				if (this.model.get('torrents').length === 0) {
					this.ui.showAllLink.hide();
				}

				if (this.notViewedCounter > 0) {
					this.ui.statusIconsContainer.addClass('movie-item-icon-wrapper-with-new-label');
					this.ui.titleWrapper.addClass('movie-item-title-wrapper-with-new-label');
					this.ui.newTorrentsLabel.show();
				} else {
					this.ui.statusIconsContainer.removeClass('movie-item-icon-wrapper-with-new-label');
					this.ui.titleWrapper.removeClass('movie-item-title-wrapper-with-new-label');
				}
			},

			_onShowAllClick: function() {
				this.ui.collapseLink.show();
				this.ui.showAllLink.hide();
				var torrents = this.model.get('torrents');
				this.movieTorrentCollection.reset(torrents);
				this.movieTorrentCollectionView.$el.slideDown('slow');
			},

			_onCollapseClick: function() {
				this.ui.collapseLink.hide();
				this.ui.showAllLink.show();
				this._showNotViewedTorrents();
			},

			onMovieClick: function(event) {
				// if remove icon was clicked, then ignore selection
				if (event != null && $(event.target).hasClass('future-movie-item-remove-image')) {
					return;
				}

				this.vent.trigger('movie-selected', this.model);
			},

			updateDownloadStatus: function() {
				this.ui.scheduledImage.hide();
				this.ui.downloadedImage.hide();
				this.ui.futureImage.hide();
				this.ui.searchingImage.hide();

				if (this.model.get('downloadStatus') == 'SCHEDULED') {
					this.ui.scheduledImage.show();
				} else if (this.model.get('downloadStatus') == 'DOWNLOADED') {
					this.ui.downloadedImage.show();
				} else if (this.model.get('downloadStatus') == 'BEING_SEARCHED') {
					this.ui.searchingImage.show();
				} else if (this.model.get('downloadStatus') == 'FUTURE') {
					this.ui.futureImage.show();
					this.ui.scheduledOn.show();
					this.ui.subTitle.show();
				}
			},

			onFutureMovieRemoveClick: function() {
				this.vent.trigger('future-movie-remove', this.model);
			},

			_getTorrentsStatus: function() {
				if (this.model.get('torrents').length === 1) {
					return 'Total 1 torrent';
				} else if (this.model.get('torrents').length === 0) {
					if (this.model.get('downloadStatus') === 'OLD') {
						return 'No available torrents';
					} else {
						return 'No available torrents yet';
					}
				} else {
					return 'Total ' + this.model.get('torrents').length + ' torrents';
				}
			},

			templateHelpers: function() {
				return {
					'escapedTitle': Utils.fixForTooltip(this.model.get('title')),
					'torrentsLabel': this._getTorrentsStatus(),
					'notViewedTorrentsCounter': this.notViewedCounter
				};
			}
		});
	});

