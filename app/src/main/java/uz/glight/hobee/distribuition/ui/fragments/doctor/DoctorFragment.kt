package uz.glight.hobee.distribuition.ui.fragments.doctor

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.glight.hobeedistribuition.network.model.DoctorModel
import com.ulugbek.ibragimovhelpers.helpers.commons.toast
import uz.glight.hobee.distribuition.R
import uz.glight.hobee.distribuition.databinding.FragmentDoctorBinding


class DoctorFragment : Fragment(R.layout.fragment_doctor) {
    private var doctorBinding: FragmentDoctorBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentDoctorBinding.bind(view)
        doctorBinding = binding
        // Argument "doctor"
        val doctor = requireArguments().getSerializable("doctor") as DoctorModel
        binding.dcName.text = doctor.name ?: ""
        binding.dcPhone.text = doctor.phone ?: ""
        var specs = ""
        doctor.specialization?.map{
            specs += it.name
        }
        binding.docSpecialization.text = specs
        binding.docDescription.text = doctor.about ?: ""
        binding.docJobLocation.text = doctor.room
        binding.docBreak.text = doctor.coffeeBreak

        binding.btnCall.setOnClickListener {
            if (!doctor.phone.isNullOrEmpty()){
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${doctor.phone}")
                }
//                if (intent.resolveActivity(requireContext().packageManager) != null) {
                    startActivity(intent)
//                }
            } else {
                context?.toast("Номер телефона не указан!")
            }
        }
        binding.startDiscuss.setOnClickListener {
            findNavController().navigate(R.id.to_record_fragmet, bundleOf("doctor_id" to doctor.id))
        }

    }

    override fun onDestroyView() {
        doctorBinding = null
        super.onDestroyView()
    }
}