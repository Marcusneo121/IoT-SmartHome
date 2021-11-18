package my.edu.tarc.iotassignmentg11

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import my.edu.tarc.iotassignmentg11.databinding.FragmentSignInBinding


class SignIn : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private var condition = false

    //private lateinit var fApp : FirebaseApp
    private lateinit var fAuth :FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private val secondApp: FirebaseApp = FirebaseApp.getInstance("IoTAssignment")

    private var backPressedTime:Long = 0
    lateinit var backToast:Toast

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
//            context?.let { FirebaseApp.initializeApp(it, options, "IoTAssignment") }
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

        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root

    }

    private fun finish(context: Context?) {
        finish(context)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {

                    Toast.makeText(context, "Press Again to Exit", Toast.LENGTH_SHORT).show()
                    val handler = Handler()
                    handler.postDelayed(object: Runnable{
                        override fun run() {
                            finish(context)
                        }
                    }, 1000)

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
        //val errorAnimation: LottieAnimationView = view.findViewById(R.id.errorLottie)
        //errorAnimation.speed = 1.2F
        //val secondApp: FirebaseApp = FirebaseApp.getInstance("IoTAssignment")
        //fApp = FirebaseApp.getInstance("IoTAssignment")
        //fAuth = FirebaseAuth.getInstance(fApp)
        fAuth = FirebaseAuth.getInstance(secondApp)


        val progressBar = ProgressBarSignIn(this)
        val progressBarError = ProgressBarErrorSignIn(this)
        val progressBarEmail = ProgressBarEmail(this)

        val password = binding.etPassword
        val email = binding.etEmail

        binding.btnRegisterScreen.setOnClickListener {
            findNavController().navigate(R.id.action_signIn_to_register)
        }

        binding.btnLogin.setOnClickListener {

            if(email.text.isEmpty()){
                email.error = "Please enter email."
                return@setOnClickListener
            }

            if(password.text.isEmpty()){
                password.error = "Please enter password."
                return@setOnClickListener
            }

            if(password.text.toString().length < 6){
                password.error = "Password must more than 5 characters."
            }

            progressBar.startLoading()

            fAuth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        if(fAuth.currentUser?.isEmailVerified == true){
                            Toast.makeText(this.context, "Logged in Successfully", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_signIn_to_HomeFragment)
                            progressBar.isDismiss()
                        } else {
                            //Toast.makeText(this.context, "Please verified your Email", Toast.LENGTH_SHORT).show()
                            progressBar.isDismiss()
                            progressBarEmail.startLoading()

                            val handler = Handler()
                            handler.postDelayed(object: Runnable{
                                override fun run() {
                                    progressBarEmail.isDismiss()
                                }
                            }, 7000)
                        }
                    } else {
                        //Toast.makeText(this.context, "Something went wrong, Please Try Again.", Toast.LENGTH_SHORT).show()
                        progressBar.isDismiss()
                        progressBarError.startLoading()

                        val handler = Handler()
                        handler.postDelayed(object: Runnable{
                            override fun run() {
                                progressBarError.isDismiss()
                            }
                        }, 7000)
                    }
                }.addOnFailureListener { e->
                    //Toast.makeText(this.context, "$e", Toast.LENGTH_LONG).show()
                }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        //fApp.delete()
    }
}