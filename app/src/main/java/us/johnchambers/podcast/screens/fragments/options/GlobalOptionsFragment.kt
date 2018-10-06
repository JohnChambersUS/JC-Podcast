package us.johnchambers.podcast.screens.fragments.options


import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
    lateinit var _rewindBox : EditText
    lateinit var _forwardBox : EditText

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
        initRewind()
        initForward()
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

    private fun initRewind() {
        _rewindBox = _view.findViewById(R.id.rewindInputBox) as EditText
        var currMins = _globalOptions.getRewindMinutesAsString()
        var e = Editable.Factory.getInstance().newEditable(currMins)
        _rewindBox.text = e
        _rewindBox.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                _globalOptions.setRewindMinutes(p0.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        });
    }

    private fun initForward() {
        _forwardBox = _view.findViewById(R.id.forwardInputBox) as EditText
        var currMins = _globalOptions.getForwardMinutesAsString()
        var e = Editable.Factory.getInstance().newEditable(currMins)
        _forwardBox.text = e
        _forwardBox.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                _globalOptions.setForwardMinutes(p0.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        });



    }
}
