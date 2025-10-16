package com.github.boybeak.fpspet.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.boybeak.fpspet.R
import com.github.boybeak.fpspet.vm.MainVM
import yuku.ambilwarna.AmbilWarnaDialog
import kotlin.getValue

class GeneralFragment : Fragment() {

    companion object {
        private const val TAG = "GeneralFragment"
    }

    private val mainVM: MainVM by activityViewModels()

    private val seekCorner by lazy {
        view?.findViewById<SeekBar>(R.id.seekCorner)
    }
    private val seekBorderWidth by lazy {
        view?.findViewById<SeekBar>(R.id.seekBorderWidth)
    }
    private val previewColor by lazy {
        view?.findViewById<View>(R.id.previewColor)
    }
    private val seekBorderAlpha by lazy {
        view?.findViewById<SeekBar>(R.id.seekBorderAlpha)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_general, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        seekCorner?.max = resources.getDimensionPixelSize(R.dimen.corner_radius_max)
        seekCorner?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (!fromUser) {
                    return
                }
                val progress = seekBar?.progress?.toFloat() ?: return
                mainVM.setCornerRadius(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val progress = seekBar?.progress?.toFloat() ?: return
                mainVM.setCornerRadius(progress)
            }
        })

        seekBorderWidth?.max = resources.getDimensionPixelSize(R.dimen.border_width_max)
        seekBorderWidth?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (!fromUser) {
                    return
                }
                val progress = seekBar?.progress?.toFloat() ?: return
                mainVM.setBorderWidth(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val progress = seekBar?.progress?.toFloat() ?: return
                mainVM.setBorderWidth(progress)
            }
        })
        previewColor?.setOnClickListener {
            val colorPicker = AmbilWarnaDialog(
                requireContext(),
                mainVM.borderColor.value ?: Color.BLUE,
                object : AmbilWarnaDialog.OnAmbilWarnaListener {
                    override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                        mainVM.setBorderColor(color)
                    }

                    override fun onCancel(dialog: AmbilWarnaDialog?) {}
                }
            )
            colorPicker.show()
        }
        seekBorderAlpha?.max = 255
        seekBorderAlpha?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (!fromUser) {
                    return
                }
                val progress = seekBar?.progress ?: return
                mainVM.setBorderAlpha(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val progress = seekBar?.progress ?: return
                mainVM.setBorderAlpha(progress)
            }
        })
    }

}