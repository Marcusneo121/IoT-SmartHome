package my.edu.tarc.iotassignmentg11

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import my.edu.tarc.iotassignmentg11.databinding.FragmentSplashscreenBinding
 
class SplashScreenFragment : Fragment() {

    private var _binding: FragmentSplashscreenBinding? = null
    private var condition = false

    private lateinit var fApp : FirebaseApp
    private lateinit var fAuth :FirebaseAuth
    private lateinit var fStore: FirebaseFirestore


    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (!condition) {
//            val options = FirebaseOptions.Builder()
//                .setApplicationId("1:155508844801:android:ef5127a80fe8595a94d300")
//                .setApiKey("AIzaSyCCmz4_im6v7LEnzj0liiKce5BQI4N67U0")
//                .setDatabaseUrl("https://iotassignment-1de  1e-default-rtdb.asia-southeast1.firebasedatabase.app/")
//                .setProjectId("iotassignment-1de1e")
//                .build()
//
//                context?.let { FirebaseApp.initializeApp(it, options, "IoTAssignment") }
//
//            condition = true
//        }
//        fApp = FirebaseApp.getInstance("IoTAssignment")
//        fAuth = FirebaseAuth.getInstance(fApp)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        (requireActivity() as AppCompatActivity).supportActionBar?.show()

        _binding = FragmentSplashscreenBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val introAnimation: LottieAnimationView = view.findViewById(R.id.introLottie)
        introAnimation.speed = 0.6F
        val handler = Handler()
        handler.postDelayed(object: Runnable{
            override fun run() {
                findNavController().navigate(R.id.action_SplashScreen_to_signIn)
            }
        }, 5000)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        //val secondApp: FirebaseApp = FirebaseApp.getInstance("IoTAssignment")
//        fApp = FirebaseApp.getInstance("IoTAssignment")
//        fAuth = FirebaseAuth.getInstance(fApp)
//
//        val progressBar = ProgressBar(this)
//        val password = binding.etPassword
//        val email = binding.etEmail
//
//
//        binding.btnRegisterScreen.setOnClickListener {
//            findNavController().navigate(R.id.action_SignInFragment_to_register)
//        }
//
//        binding.btnLogin.setOnClickListener {
//
//            if(email.text.isEmpty()){
//                email.error = "Please enter email."
//                return@setOnClickListener
//            }
//
//            if(password.text.isEmpty()){
//                password.error = "Please enter password."
//                return@setOnClickListener
//            }
//
//            if(password.text.toString().length < 6){
//                password.error = "Password must more than 5 characters."
//            }
//
//            progressBar.startLoading()
//
//            fAuth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
//                .addOnCompleteListener {
//                    if(it.isSuccessful){
//                        if(fAuth.currentUser?.isEmailVerified == true){
//                            Toast.makeText(this.context, "Logged in Successfully", Toast.LENGTH_SHORT).show()
//                            findNavController().navigate(R.id.action_SignInFragment_to_HomeFragment)
//                            progressBar.isDismiss()
//                        } else {
//                            Toast.makeText(this.context, "Please verified your Email", Toast.LENGTH_SHORT).show()
//                            progressBar.isDismiss()
//                        }
//                    } else {
//                        Toast.makeText(this.context, "Something went wrong, Please Try Again.", Toast.LENGTH_SHORT).show()
//                        progressBar.isDismiss()
//                    }
//                }.addOnFailureListener { e->
//                    Toast.makeText(this.context, "$e", Toast.LENGTH_SHORT).show()
//                }
//
//
////            val handler = Handler()
////            handler.postDelayed(object: Runnable{
////                override fun run() {
////                    findNavController().navigate(R.id.action_SignInFragment_to_HomeFragment)
////                    progressBar.isDismiss()
////                }
////            }, 5000)
//        }
//
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//
//        //fApp.delete()
//
//    }
}