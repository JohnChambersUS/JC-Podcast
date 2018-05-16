package us.johnchambers.podcast.screens.fragments.options


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker

import us.johnchambers.podcast.R
import us.johnchambers.podcast.fragments.MyFragment
import us.johnchambers.podcast.misc.C
import us.johnchambers.podcast.objects.FragmentBackstackType
import us.johnchambers.podcast.objects.GlobalOptions
import android.widget.Toast




/**
 * A simple [Fragment] subclass.
 * Use the [GlobalOptionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class GlobalOptionsFragment : MyFragment() {

    lateinit var _view : View
    lateinit var _speedPicker : NumberPicker
    val _globalOptions : GlobalOptions by lazy { GlobalOptions() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _view = inflater.inflate(R.layout.fragment_global_options, container, false)
        initSpeedPicker()
        return _view
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                GlobalOptionsFragment().apply {
                    arguments = Bundle().apply {

                    }
                }
    }

    override fun getBackstackType(): FragmentBackstackType {
        return FragmentBackstackType.BRANCH
    }

    private fun initSpeedPicker() {
        _speedPicker = _view.findViewById(R.id.speed_picker) as NumberPicker
        _speedPicker.displayedValues = _globalOptions.getSpeeds()
        _speedPicker.minValue = 0
        _speedPicker.maxValue = _globalOptions.getMaxSpeed()
        _speedPicker.value = _globalOptions.getCurrentSpeedIndex()
        _speedPicker.wrapSelectorWheel = false
        _speedPicker.setOnValueChangedListener(object : NumberPicker.OnValueChangeListener {
            override fun onValueChange(numberPicker: NumberPicker, old: Int, new: Int) {
                _globalOptions.setCurrentSpeed(new)
            }
        })


    }
}
