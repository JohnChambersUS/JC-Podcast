package us.johnchambers.podcast.playlists

import us.johnchambers.podcast.database.EpisodeTable
import us.johnchambers.podcast.database.PodcastDatabaseHelper
import us.johnchambers.podcast.misc.C

@Suppress("UNUSED_PARAMETER")
class TagAllPlaylist(useExisting : Boolean, tag: String) : Playlist(C.playlist.GENERIC_PLAYLIST) {

    var _tag = tag

    init {
        _playistId = _tag // + C.playlist.TAG_PLAYLIST_SUFFIX
        _episodes = mutableListOf()
        loadCurrentEpisodeList() //non destructive load
        if (_episodes.isEmpty()) {
            refreshEpisodeList() //reindex if there was nothing in index
        }
    }

    override fun updatePlaylistInfo() {

    }

    override fun getNextEpisode(): EpisodeTable {

        var episodeFinder = NextEpisodePodcast()
        _episodeIndex = episodeFinder.getNextEpisode(_episodes, _episodeIndex)
        if (_episodeIndex < 0) return EpisodeTable()
        return PodcastDatabaseHelper.getInstance().getEpisodeTableRowByEpisodeId(_episodes.get(_episodeIndex).eid)

    }

    override fun setCurrentEpisode(eid : String) {
        if (_episodes.size < 1) {
            _episodeIndex = -1
            return
        }

        _episodeIndex = _episodes.size - 1
        do {
            if (_episodes.get(_episodeIndex).eid.equals(eid))
                break
            else
                _episodeIndex--

        } while (_episodeIndex > -1)
    }

    override fun alignWithNowPlayingInfo() { //todo

    }

    override fun getEpisodes() : MutableList<EpisodeTable> {
        refreshEpisodeList()
        _episodeIndex = -1
        return _episodes
    }

    override fun getCurrentEpisodes() : MutableList<EpisodeTable>  {
        loadCurrentEpisodeList()
        _episodeIndex = -1
        return _episodes
    }

    //*** wipes table and reloads ***
    private fun refreshEpisodeList() {
        _episodes.clear()
        if (_newOnly) {
            _episodes = (PodcastDatabaseHelper.getInstance().getRefreshedListOfTagPlaylistEpisodesTop(_tag))
        }
        else {
            _episodes = (PodcastDatabaseHelper.getInstance().getRefreshedListOfTagPlaylistEpisodes(_tag))
        }
        //delete all tag related from playlist-tag
        PodcastDatabaseHelper.getInstance().removePlaylistFromPlaylistTable(getPlaylistId())
        //insert all episodes into playlist-tag
        for (episode in _episodes) {
            PodcastDatabaseHelper.getInstance().upsertPlaylistRow(getPlaylistId(), episode.eid)
        }
    }
    //*** does not wipe tables ***
    private fun loadCurrentEpisodeList() {
        _episodes.clear()
        var playlistEntries = PodcastDatabaseHelper.getInstance().getCurrentTagPlaylistEpisodes(_tag)
        for (entry in playlistEntries) {
            _episodes.add(PodcastDatabaseHelper.getInstance().getEpisodeTableRowByEpisodeId(entry.eid))
        }
    }

    override fun getPlaylistId() : String {
        return _playistId
    }

    override fun removeItem(index: Int) {
        if ((index > -1) && (index < _episodes.size)) {
            PodcastDatabaseHelper.getInstance().removeItemFromPlaylistTable(_playistId, _episodes.get(index).eid)
        }
        loadCurrentEpisodeList()
        if (index == _episodeIndex) {
            _episodeIndex = -1
        }
    }

    override fun moveItem(source: Int, target: Int) {
        var element = _episodes.get(source)
        _episodes.removeAt(source)
        _episodes.add(target, element)
        updateDatabase()
    }

    private fun updateDatabase() {
        //remove all items for playlist
        PodcastDatabaseHelper.getInstance().removePlaylistFromPlaylistTable(_playistId)
        //run loop to add items in _episode list to db
        for (episode in _episodes) {
            PodcastDatabaseHelper.getInstance().upsertPlaylistRow(_playistId, episode.eid)
        }
    }




}