package us.johnchambers.podcast.screens.fragments.playlist_manual


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import us.johnchambers.podcast.R
import us.johnchambers.podcast.fragments.MyFragment
import us.johnchambers.podcast.objects.FragmentBackstackType

class ManualPlaylistFragment : MyFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_manual_playlist, container, false)
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
