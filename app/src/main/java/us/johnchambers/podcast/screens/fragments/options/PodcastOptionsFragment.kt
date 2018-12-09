package us.johnchambers.podcast.screens.fragments.options


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker

import us.johnchambers.podcast.R
import us.johnchambers.podcast.fragments.MyFragment
import us.johnchambers.podcast.objects.FragmentBackstackType
import us.johnchambers.podcast.objects.PodcastOptions


class PodcastOptionsFragment : MyFragment() {

    lateinit var _view : View
    lateinit var _speedPicker : NumberPicker
    //val _globalOptions : GlobalOptions by lazy { GlobalOptions() }
    lateinit var _podcastId : String
    val _podcastlOptions : PodcastOptions by lazy { PodcastOptions(_podcastId) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        _view = inflater.inflate(R.layout.fragment_podcast_options, container, false)
        initSpeedPicker()
        return _view
    }

    companion object {
        @JvmStatic
        fun newInstance(podcastId: String) =
            PodcastOptionsFragment().apply {
                arguments = Bundle().apply {
                    _podcastId = podcastId
                }
            }
    }

    override fun getBackstackType(): FragmentBackstackType {
        return FragmentBackstackType.BRANCH
    }

    private fun initSpeedPicker() {
        _speedPicker = _view.findViewById(R.id.speed_picker) as NumberPicker
        _speedPicker.displayedValues = _podcastlOptions.getSpeeds()
        _speedPicker.minValue = 0
        _speedPicker.maxValue = _podcastlOptions.getMaxSpeed()
        _speedPicker.value = _podcastlOptions.getCurrentSpeedIndex()
        _speedPicker.wrapSelectorWheel = false
        _speedPicker.setOnValueChangedListener(object : NumberPicker.OnValueChangeListener {
            override fun onValueChange(numberPicker: NumberPicker, old: Int, new: Int) {
                _podcastlOptions.setCurrentSpeed(new)
            }
        })
    }

} //end of class
