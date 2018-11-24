package us.johnchambers.podcast.playlists

import com.crashlytics.android.Crashlytics
import us.johnchambers.podcast.database.EpisodeTable
import us.johnchambers.podcast.database.PlaylistTable
import us.johnchambers.podcast.database.PodcastDatabaseHelper
import us.johnchambers.podcast.database.PodcastTagTable
import us.johnchambers.podcast.misc.C

@Suppress("UNUSED_PARAMETER")
class TagAllPlaylist(useExisting : Boolean, tag: String) : Playlist(C.playlist.GENERIC_PLAYLIST) {

    var _tag = tag

    init {
        _playistId = _tag + C.playlist.TAG_PLAYLIST_SUFFIX
        _episodes = mutableListOf()
        loadCurrentEpisodeList() //non destructive load
        if (_episodes.isEmpty()) {
            refreshEpisodeList() //reindex if there was nothing in index
        }
    }

    override fun updatePlaylistInfo() {

    }

    override fun getNextEpisode(): EpisodeTable {
        if (_episodes.size == 0) {
            return EpisodeTable()
        }
        if (_episodeIndex == -1) {
            setCurrentEpisodeIndex(_episodes.size - 1)
        }
        var attempts = _episodes.size
        var foundIt = false
        do {
            var currEpisode = PodcastDatabaseHelper.getInstance().getEpisodeTableRowByEpisodeId(_episodes.get(_episodeIndex).eid)
            if (currEpisode == null) continue
            _episodes.set(_episodeIndex, currEpisode)
            if (currEpisode.playPointAsLong < currEpisode.lengthAsLong) {
                foundIt = true
            }
            else {
                _episodeIndex--
                if (_episodeIndex < 0) {
                    _episodeIndex = _episodes.size - 1
                }
            }
        } while(!foundIt && (--attempts > 0))
        if (foundIt)
            return _episodes.get(_episodeIndex)
        else
            return EpisodeTable()
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

    override fun alignWithNowPlayingInfo() {

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
        _episodes = (PodcastDatabaseHelper.getInstance().getRefreshedListOfTagPlaylistEpisodes(_tag))
                //.sortedWith(compareBy({it.pubDateAsDate})) as MutableList<EpisodeTable>
        //delete all tag related from playlist-tag
        PodcastDatabaseHelper.getInstance().removePlaylistFromPlaylistTable(getPlaylistId())
        //insert all episodes into playlist-tag
        for (episode in _episodes) {
            PodcastDatabaseHelper.getInstance().upsertPlaylistRow(_playistId, episode.eid)
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
            PodcastDatabaseHelper.getInstance().removeEpisodeFromLatestTable(_episodes.get(index).eid)
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
        //remove all items for latest playlist
        PodcastDatabaseHelper.getInstance()
                .deleteAllFromLatestTable()
        //run loop to add items in _episode list to db
        for (episode in _episodes) {
            PodcastDatabaseHelper.getInstance().addToLatestTable(episode)
        }
    }




}