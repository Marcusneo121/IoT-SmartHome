package my.edu.tarc.iotassignmentg11

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import my.edu.tarc.iotassignmentg11.databinding.FragmentAircondReportBinding
import my.edu.tarc.iotassignmentg11.databinding.FragmentPetCareBinding

class PetCare : Fragment() {

    private var _binding: FragmentPetCareBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPetCareBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivAutoFeed.setOnClickListener {
            findNavController().navigate(R.id.action_petCare_to_feedPet)
        }

        binding.ivAutoPoopingPad.setOnClickListener {
            findNavController().navigate(R.id.action_petCare_to_autopoop)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}