package my.edu.tarc.iotassignmentg11

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import my.edu.tarc.iotassignmentg11.databinding.FragmentIntrusionReportBinding

class IntrusionReport : Fragment() {
    private var _binding: FragmentIntrusionReportBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentIntrusionReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivIntrusion.setOnClickListener {
            findNavController().navigate(R.id.action_intrusionReport_to_intrusionReportDetection)
        }

        binding.ivAlarmStatus.setOnClickListener {
            findNavController().navigate(R.id.action_intrusionReport_to_intrusionReportAlarmStatus)
        }
        binding.ivDoorStatus.setOnClickListener {
            findNavController().navigate(R.id.action_intrusionReport_to_intrusionReportDoorStatus)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}