package my.edu.tarc.iotassignmentg11

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import my.edu.tarc.iotassignmentg11.databinding.FragmentReportBinding

class report : Fragment() {

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivAircondReport.setOnClickListener {
            findNavController().navigate(R.id.action_report_to_aircondReport)
        }



        binding.ivPetCare.setOnClickListener{
            findNavController().navigate(R.id.action_report_to_petCareReport)
        }

        binding.ivCameraReport.setOnClickListener {
            findNavController().navigate(R.id.action_report_to_cctvRecord)

        }

        binding.ivIntrusionReport.setOnClickListener {
            findNavController().navigate(R.id.action_report_to_intrusionReport)

        }
    }
}