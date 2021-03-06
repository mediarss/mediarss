/*global define*/
define([
	'jquery',
	'backbone',
	'marionette',
	'handlebars',
	'text!features/moviesTab/templates/movies-tab.tpl',
	'features/moviesTab/views/MovieCollectionView',
	'features/moviesTab/views/MoviesSearchView',
	'features/moviesTab/collections/MoviesCollection',
	'utils/HttpUtils',
	'components/section/views/SectionView',
	'utils/MessageBox',
	'moment',
	'utils/StringUtils',
	'routers/RoutingPaths',
	'utils/Utils'
],
	function($, Backbone, Marionette, Handlebars, template, MovieCollectionView, MoviesSearchView, MoviesCollection, HttpUtils, SectionView, MessageBox, Moment, StringUtils, RoutingPaths, Utils) {
		"use strict";

		var availableMovies = null;
		var userMovies = null;
		var availableMoviesCount = null;
		var userMoviesCount = null;

		return Marionette.Layout.extend({
			template: Handlebars.compile(template),
			className: 'movies-tab',

			ui: {
				availableMoviesCounter: '.movies-counter',
				userMoviesCounter: '.future-movies-counter',
				userMoviesFilter: '.future-movies-filter',
				availableMoviesFilter: '.movies-filter'
			},

			events: {
				'click .future-movies-filter': 'onFutureMoviesFilterClick',
				'click .movies-filter': 'onMoviesFilterClick'
			},

			regions: {
				moviesSearchRegion: '.movies-search-section',
				moviesListRegion: '.movies-list-container',
				moviesSectionRegion: '.movies-section'
			},

			constructor: function(options) {
				this.vent = new Backbone.Wreqr.EventAggregator();
				Marionette.Layout.prototype.constructor.apply(this, arguments);
				this.tabData = options.tabData;

				this.availalbleMoviesCollection = new MoviesCollection();
				this.userMoviesCollection = new MoviesCollection();
				this.availableMoviesCollectionView = new MovieCollectionView({
					vent: this.vent,
					collection: this.availalbleMoviesCollection,
					isAdmin: this.tabData.isAdmin
				});
				this.userMoviesCollectionView = new MovieCollectionView({
					vent: this.vent,
					collection: this.userMoviesCollection,
					isAdmin: this.tabData.isAdmin
				});

				this.moviesSection = new SectionView({
					title: 'Latest Movies',
					description: 'Updated on: <span class=\'movies-updated-on\'></span>' +
						'<br/>Select movies to download. Here you can find newly available movies. You can use IMDB preview'
				});

				this.moviesSearchView = new MoviesSearchView({vent: this.vent});

				this.vent.on('future-movie-remove', this.onUserMovieRemove, this);
				this.vent.on('movie-redownload', this.onMovieRedownload, this);
				this.vent.on('movie-torrent-download', this.onMovieTorrentDownload, this);
				this.vent.on('movie-search-add', this.onFutureMovieAddButtonClick, this);
			},

			onRender: function() {
				this.moviesSectionRegion.show(this.moviesSection);
				this.moviesSearchRegion.show(this.moviesSearchView);
			},

			onShow: function() {
				var that = this;
				setTimeout(function() {
					var isAvailableMovies = window.location.href.indexOf('userMovies') === -1;
					if (isAvailableMovies && availableMovies != null) {
						that._switchToAvailableMovies(availableMovies);
						if (userMovies != null) {
							that.ui.userMoviesCounter.html(userMovies.length);
						} else {
							that.ui.userMoviesCounter.html(userMoviesCount);
						}
					} else if (!isAvailableMovies && userMovies != null) {
						that._switchToUserMovies(userMovies);
						if (availableMovies != null) {
							that.ui.availableMoviesCounter.html(availableMovies.length);
						} else {
							that.ui.availableMoviesCounter.html(availableMoviesCount);
						}
					} else {
						HttpUtils.get('rest/movies/initial-data/' + (isAvailableMovies ? 'availableMovies' : 'userMovies'), function(res) {
							if (isAvailableMovies) {
								availableMovies = res.availableMovies;
								userMoviesCount = res.userMoviesCount;
								that._switchToAvailableMovies(res.availableMovies);
								that.ui.userMoviesCounter.html(res.userMoviesCount);
							} else {
								userMovies = res.userMovies;
								availableMoviesCount = res.availableMoviesCount;
								that._switchToUserMovies(res.userMovies);
								that.ui.availableMoviesCounter.html(res.availableMoviesCount);
							}
							$('.movies-updated-on').html(Moment(new Date(res.moviesLastUpdated)).format('DD/MM/YYYY HH:mm '));
						}, false); // no need loading here
					}
				}, 50);
			},

			onMovieTorrentDownload: function(userTorrent, movieId) {
				function scrollToElement(element, time, verticalOffset) {
					time = typeof(time) != 'undefined' ? time : 1000;
					verticalOffset = typeof(verticalOffset) != 'undefined' ? verticalOffset : 0;
					var offset = element.offset();
					var offsetTop = offset.top + verticalOffset;
					$('html, body').animate({
						scrollTop: offsetTop
					}, time);
				}

				var isUserMovies = this._isUserMoviesSelected();
				var that = this;
				HttpUtils.post('rest/movies/download', {
					torrentId: userTorrent.get('torrentId'),
					movieId: movieId,
					isUserMovies: isUserMovies
				}, function(res) {
					// available movies - order doesn't change, only update status of torrent, user movies cache and counter
					// user movies - move movie model and movie view to top and scroll to it, keeping collapsed/expanded state
					var movieModel, movieView;
					if (isUserMovies) {
						movieModel = that.userMoviesCollection.get(movieId);
						movieView = that.userMoviesCollectionView.children.findByModel(movieModel);
						movieView.setTorrentStatus(userTorrent.get('torrentId'), 'SCHEDULED');

						that.userMoviesCollection.remove(movieModel);
						that.userMoviesCollection.add(movieModel, {at: 0});
						var func = function() {
							// userMovies are sorted from latest downloaded to oldest, so need to scroll to top just in case
							scrollToElement(that.moviesListRegion.$el);
							that.userMoviesCollectionView.off('render', func);
						};
						that.userMoviesCollectionView.on('render', func);
						that.userMoviesCollectionView.render();
					} else {
						movieModel = that.availalbleMoviesCollection.get(movieId);
						movieView = that.availableMoviesCollectionView.children.findByModel(movieModel);
						movieView.setTorrentStatus(userTorrent.get('torrentId'), 'SCHEDULED');

						that.ui.userMoviesCounter.html(res.userMoviesCount);

						// update caches
						userMoviesCount = res.userMoviesCount;
						availableMovies = that.availalbleMoviesCollection.toArray();
						availableMoviesCount = availableMovies.length;
					}
				});
			},

			onFutureMovieAddButtonClick: function(res) {
				userMovies = res.movies;
				this._switchToUserMovies(res.movies);
			},

			onMovieRedownload: function(movieModel) {
				var isUserMovies = this._isUserMoviesSelected();
				var that = this;
				HttpUtils.post("rest/movies/redownload", {
					movieId: movieModel.get('id'),
					isUserMovies: isUserMovies
				}, function(res) {
					var movieId = movieModel.get('id');
					if (isUserMovies) {
						movieModel = that.userMoviesCollection.get(movieId);
					} else {
						movieModel = that.availalbleMoviesCollection.get(movieId);
					}
					movieModel.set('downloadStatus', 'BEING_SEARCHED');
					that._startPollingThread([movieId]);
				});
			},

			onUserMovieRemove: function(movieModel) {
				var that = this;
				HttpUtils.post("rest/movies/future/remove", {
					movieId: movieModel.get('id')
				}, function(res) {
					MessageBox.info(res.message);

					that.userMoviesCollection.remove(movieModel);
					that.ui.userMoviesCounter.html(that.userMoviesCollection.size());
					that.userMoviesCollectionView.render();

					userMovies = that.userMoviesCollection.toArray();
					userMoviesCount = userMovies.length;
				});
			},

			_isUserMoviesSelected: function() {
				return this.ui.userMoviesFilter.hasClass('filter-selected');
			},

			_updateUserMovies: function(movies) {
				this.ui.userMoviesCounter.html(movies.length);
				this.userMoviesCollection.reset(movies);
				this.userMoviesCollectionView.render();

				var moviesBeingSearched = this._getMoviesBeingSearched(this.userMoviesCollection);
				if (moviesBeingSearched.length > 0) {
					this._startPollingThread(moviesBeingSearched);
				}
			},

			_getMoviesBeingSearched: function(collection) {
				var moviesBeingSearched = [];
				collection.forEach(function(movieModel) {
					if (movieModel.get('downloadStatus') === 'BEING_SEARCHED') {
						moviesBeingSearched.push(movieModel.get('id'));
					}
				});
				return moviesBeingSearched;
			},

			_updateAvailableMovies: function(movies) {
				this.ui.availableMoviesCounter.html(movies.length);
				this.availalbleMoviesCollection.reset(movies);
				this.availableMoviesCollectionView.render();

				var moviesBeingSearched = this._getMoviesBeingSearched(this.availalbleMoviesCollection);
				if (moviesBeingSearched.length > 0) {
					this._startPollingThread(moviesBeingSearched);
				}
			},

			_switchToUserMovies: function(movies) {
				Backbone.history.navigate(StringUtils.formatRoute(RoutingPaths.MOVIES, 'userMovies'), {trigger: false});

				this.ui.availableMoviesFilter.removeClass('filter-selected');
				this.ui.userMoviesFilter.addClass('filter-selected');
				this._updateUserMovies(movies);
				this.moviesListRegion.show(this.userMoviesCollectionView);
				this.moviesListRegion.$el.addClass('future-movies-list');
			},

			_switchToAvailableMovies: function(movies) {
				Backbone.history.navigate(StringUtils.formatRoute(RoutingPaths.MOVIES, 'availableMovies'), {trigger: false});

				this.ui.userMoviesFilter.removeClass('filter-selected');
				this.ui.availableMoviesFilter.addClass('filter-selected');
				this._updateAvailableMovies(movies);
				this.moviesListRegion.show(this.availableMoviesCollectionView);
				this.moviesListRegion.$el.removeClass('future-movies-list');
			},

			onFutureMoviesFilterClick: function() {
				if (this._isUserMoviesSelected()) {
					return;
				}

				var that = this;
				if (userMovies != null) {
					Utils.withLoading(function() {
						that._switchToUserMovies(userMovies);
						that.moviesListRegion.show(that.userMoviesCollectionView);
					});
				} else {
					HttpUtils.get('rest/movies/user-movies', function(res) {
						userMovies = res.movies;
						that._switchToUserMovies(res.movies);
						that.moviesListRegion.show(that.userMoviesCollectionView);
					});
				}
			},

			onMoviesFilterClick: function() {
				if (!this._isUserMoviesSelected()) {
					return;
				}

				this._showAvailableMovies();
			},

			_showAvailableMovies: function() {
				var that = this;
				if (availableMovies != null) {
					Utils.withLoading(function() {
						that._switchToAvailableMovies(availableMovies);
						that.moviesListRegion.show(that.availableMoviesCollectionView);
					});
				} else {
					HttpUtils.get('rest/movies/available-movies', function(res) {
						availableMovies = res.movies;
						that._switchToAvailableMovies(res.movies);
						that.moviesListRegion.show(that.availableMoviesCollectionView);
					});
				}
			},

			onClose: function() {
				// when leaving the view stop polling the server for job updates
				this._stopPollingThread();
			},

			_startPollingThread: function(moviesBeingSearched) {
				var that = this;
				var f = function() {
					if (!that.timer) {
						return;
					}

					$.post("rest/movies/check-movies-being-searched", {
						ids: moviesBeingSearched
					}).success(function(res) {
						if (that.timer !== null) {
							var isUserMovies = that._isUserMoviesSelected();

							var updateMovieModel = function(movieModel, el) {
								if (movieModel.get('downloadStatus') !== el.downloadStatus) {
									movieModel.set('notViewedTorrents', el.notViewedTorrents);
									movieModel.set('notViewedTorrentsCount', el.notViewedTorrentsCount);
									movieModel.set('viewedTorrents', el.viewedTorrents);
									movieModel.set('viewedTorrentsCount', el.viewedTorrentsCount);
									movieModel.set('downloadStatus', el.downloadStatus);

									// render only if visible
									var movieView;
									if (isUserMovies) {
										movieView = that.userMoviesCollectionView.children.findByModel(movieModel);
									} else {
										movieView = that.availableMoviesCollectionView.children.findByModel(movieModel);
									}
									movieView.render();
								}
							};

							// search in both tabs - user movies and available movies
							for (var i = 0; i < res.completed.length; ++i) {
								var el = res.completed[i];
								var movieModel = that.userMoviesCollection.get(el.id);
								if (movieModel) {
									updateMovieModel(movieModel, el);
								} else {
									movieModel = that.availalbleMoviesCollection.get(el.id);
									updateMovieModel(movieModel, el);
								}
							}

							// update caches
							userMovies = that.userMoviesCollection.toArray();
							userMoviesCount = userMovies.length;
							availableMovies = that.availalbleMoviesCollection.toArray();
							availableMoviesCount = availableMovies.length;

							if (res.completed.length === moviesBeingSearched.length) {
								that._stopPollingThread();
							}
						}
					}).error(function(res) {
						console.log('error. data: ' + res);
						that._stopPollingThread();
					});

					that.timer = setTimeout(f, 5000);
				};
				that.timer = setTimeout(f, 5000);
				// don't execute the method immidiately! let movies collection to render first
				//f();
			},

			_stopPollingThread: function() {
				clearTimeout(this.timer);
				this.timer = null;
			}
		});
	});
