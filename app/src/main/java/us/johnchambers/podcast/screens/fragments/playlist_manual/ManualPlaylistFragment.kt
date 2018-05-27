package us.johnchambers.podcast.screens.fragments.playlist_manual


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.greenrobot.eventbus.EventBus

import us.johnchambers.podcast.R
import us.johnchambers.podcast.fragments.MyFragment
import us.johnchambers.podcast.objects.DocketManualPlaylist
import us.johnchambers.podcast.objects.FragmentBackstackType
import us.johnchambers.podcast.playlists.Playlist
import us.johnchambers.podcast.playlists.PlaylistFactory

class ManualPlaylistFragment : MyFragment() {

    lateinit var _view : View;
    lateinit var _playlist : Playlist

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        EventBus.getDefault().register(this)
        _playlist = PlaylistFactory.getPlaylist(DocketManualPlaylist())

    }

    //todo fill out
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _view =  inflater.inflate(R.layout.fragment_manual_playlist, container, false)








        return _view;
    }


    companion object {
        @JvmStatic
        fun newInstance() =
                ManualPlaylistFragment().apply {
                    arguments = Bundle().apply {

                    }
                }
    }

    override fun getBackstackType() : FragmentBackstackType {
        return FragmentBackstackType.ROOT
    }
}
