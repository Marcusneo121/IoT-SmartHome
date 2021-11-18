package my.edu.tarc.iotassignmentg11

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import my.edu.tarc.iotassignmentg11.databinding.FragmentAircondReportBinding


class AircondReport : Fragment() {

    private var _binding: FragmentAircondReportBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAircondReportBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivTempChange.setOnClickListener {
            findNavController().navigate(R.id.action_aircondReport_to_aircondReportTemp)
        }

        binding.ivOnOff.setOnClickListener {
            findNavController().navigate(R.id.action_aircondReport_to_aircondReportOnOff)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}