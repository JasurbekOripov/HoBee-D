package uz.glight.hobee.distribuition.ui.fragments.doctor

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Chronometer
import androidx.core.content.ContextCompat
import com.glight.hobeedistribuition.utils.PermissionUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import uz.glight.hobee.distribuition.R
import uz.glight.hobee.distribuition.databinding.FragmentRecordBinding
import uz.glight.hobee.distribuition.services.record.*
import uz.glight.hobee.distribuition.ui.activities.BottomNavigationActivity
import uz.glight.hobee.ibrogimov.commons.getFragmentTag


class RecordFragment : Fragment(R.layout.fragment_record) {
    private var recordFragmentBinding: FragmentRecordBinding? = null
    private lateinit var permUtils: PermissionUtils
    private var bus: EventBus? = null
    var mStartRecording = true
    private var status = RECORDING_STOPPED
    private lateinit var args: Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bus = EventBus.getDefault()
        bus!!.register(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentRecordBinding.bind(view)
        recordFragmentBinding = binding

        Intent(context, RecordService::class.java).apply {
            action = GET_RECORDER_INFO
            try {
                context?.startService(this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        args = requireArguments()

        permUtils = PermissionUtils(requireContext(), (activity as BottomNavigationActivity))

        binding.startRecordBtn.setOnClickListener {

            toggleRecorder()
            Log.d(getFragmentTag(), "onViewCreated: $args")
        }
    }

    private fun toggleRecButton(){
        if (status == RECORDING_RUNNING){
            recordFragmentBinding?.startRecordBtn?.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red)
            recordFragmentBinding?.startRecordBtn?.text = "Закончить беседу"
        } else {
            recordFragmentBinding?.startRecordBtn?.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.blue)
            recordFragmentBinding?.startRecordBtn?.text = "Начать беседу"
        }
    }

    override fun onDestroyView() {
        recordFragmentBinding = null
        bus?.unregister(this)
        super.onDestroyView()
    }

    private fun toggleRecorder(){
        if (permUtils.checkUserPermission()){
            status = if (status == RECORDING_RUNNING || status == RECORDING_PAUSED) {
                RECORDING_STOPPED
            } else {
                RECORDING_RUNNING
            }

            if (status == RECORDING_RUNNING) {
                startRecording()
            } else {
                stopRecording()
            }
            toggleRecButton()
        } else {
            permUtils.requestPermission()
        }
    }

    private fun startRecording() {
        Intent(context, RecordService::class.java).apply {
            putExtra("doctor_id", args.getInt("doctor_id"))
            context?.startService(this)
        }
        onStartRecord()
    }

    private fun stopRecording() {
        Intent(context, RecordService::class.java).apply {
            putExtra("doctor_id", args.getInt("doctor_id"))
            context?.stopService(this)
        }
        onStopRecord()
    }

    private fun onStartRecord() {
        recordFragmentBinding?.chronometer?.base = SystemClock.elapsedRealtime()
        recordFragmentBinding?.chronometer?.start()
    }

    private fun onStopRecord() {
        recordFragmentBinding?.chronometer?.stop()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun gotStatusEvent(event: RecordEvents.RecordingStatus) {
        status = event.status
        toggleRecButton()
    }
}