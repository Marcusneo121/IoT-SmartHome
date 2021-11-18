package my.edu.tarc.iotassignmentg11

import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import my.edu.tarc.iotassignmentg11.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private lateinit var fAuth : FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    // This property is only valid between onCreateView and
    // onDestroyView.
    private var userID: String? = null
    private var userName: String? = null
    private var userEmail: String? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        (requireActivity() as AppCompatActivity).supportActionBar?.show()

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {
                    Toast.makeText(context, "Press LOGOUT instead", Toast.LENGTH_SHORT).show()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val secondApp: FirebaseApp = FirebaseApp.getInstance("IoTAssignment")
        fStore = FirebaseFirestore.getInstance(secondApp)
        fAuth = FirebaseAuth.getInstance(secondApp)
        userID = fAuth.currentUser?.uid

        fStore.collection("users")
            .document(userID!!)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    userName = it.get("name").toString()
                    userEmail = it.get("email").toString()
                    binding.tvWelcomeMessage.text = "Welcome Home! $userName."
                }
            }

        binding.ivAircond.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_aircond)
        }

        binding.ivPet.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_petCare)
        }

        binding.ivCamera.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_camera)
        }

        binding.ivIntrusion.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_intrusionDetect)
        }

        binding.ivReport.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_report)
        }

        binding.btnLogout.setOnClickListener {

            fAuth.signOut()
            findNavController().navigate(R.id.action_HomeFragment_to_signIn)
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}