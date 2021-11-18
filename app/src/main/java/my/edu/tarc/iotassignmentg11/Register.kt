package my.edu.tarc.iotassignmentg11

import android.content.ContentValues
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.progress_bar_register.*
import my.edu.tarc.iotassignmentg11.databinding.FragmentRegisterBinding

class Register : Fragment() {

    private var _binding: FragmentRegisterBinding? = null

    private val binding get() = _binding!!
    private var condition = false
    private var fAuth: FirebaseAuth? = null
    private lateinit var fStore: FirebaseFirestore
    private var userID: String? = null
    private val secondApp: FirebaseApp = FirebaseApp.getInstance("IoTAssignment")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (!condition) {
////            val options = FirebaseOptions.Builder()
////                .setApplicationId("1:155508844801:android:ef5127a80fe8595a94d300")
////                .setApiKey("AIzaSyCCmz4_im6v7LEnzj0liiKce5BQI4N67U0")
////                .setDatabaseUrl("https://iotassignment-1de1e-default-rtdb.asia-southeast1.firebasedatabase.app/")
////                .setProjectId("iotassignment-1de1e")
////                .build()
////            this.context?.let { FirebaseApp.initializeApp(it, options, "IoTAssignment") }
////            condition = true
////        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        (requireActivity() as AppCompatActivity).supportActionBar?.show()

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
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

        //val secondApp: FirebaseApp = FirebaseApp.getInstance("IoTAssignment")
        fAuth = FirebaseAuth.getInstance(secondApp)
        fStore = FirebaseFirestore.getInstance(secondApp)

        val progressBarReg = ProgressBarRegister(this)
        val progressBarEmailSent = ProgressBarEmailSend(this)

        binding.btnReg.setOnClickListener {
            val name = binding.etRegName
            val password = binding.etRegPass
            val conPassword = binding.etRegConPass
            val email = binding.etRegEmail

            if(name.text.isEmpty()){
                name.error = "Please enter full name."
                return@setOnClickListener
            }

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
                return@setOnClickListener
            }

            if(!TextUtils.equals(password.text.toString(), conPassword.text.toString())){
                conPassword.error = "Confirm password is not same as password."
                return@setOnClickListener
            }

            progressBarReg.startLoading()

            fAuth!!.createUserWithEmailAndPassword(email.text.toString(), password.text.toString()).addOnCompleteListener {
                if(it.isSuccessful){
                    //send email verification

                    val userEmailVerif: FirebaseUser? = fAuth!!.currentUser
                    userEmailVerif?.sendEmailVerification()?.addOnSuccessListener {
                        Toast.makeText(this.context, "Verification Email has been sent", Toast.LENGTH_SHORT).show()
                    }?.addOnFailureListener {
                        Log.d("ExceptionMsg", "onFailure: Email not sent")
                    }


                    Toast.makeText(this.context, "User Created", Toast.LENGTH_SHORT).show()
                    userID = fAuth!!.currentUser?.uid
                    //val docRef: DocumentReference? = userID?.let { it1 -> fStore.collection("users").document(it1)}

                    val hashMapUser = hashMapOf(
                        "name" to name.text.toString(),
                        "email" to email.text.toString(),
                        //"userID" to userID,
                    )

                    fStore.collection("users").document(userID!!)
//                        .collection("userData")
                        .set(hashMapUser)
                        .addOnSuccessListener {
                            Log.d("TAG", "onSuccess: User profile is created for $userID")
                            progressBarReg.isDismiss()
                            progressBarEmailSent.startLoading()
                            val handler = Handler()
                            handler.postDelayed(object: Runnable{
                                override fun run() {
                                    progressBarEmailSent.isDismiss()
                                    findNavController().navigate(R.id.action_register_to_signIn)
                                }
                            }, 6000)
                        }
                        .addOnFailureListener {
                            Log.w(ContentValues.TAG, "Error adding document ${it.suppressedExceptions}")
                        }

//                    docRef?.set(hashMapUser)?.addOnSuccessListener {
//
//
//                    }

                } else {
                    Toast.makeText(this.context, "Something went wrong. Try Again.", Toast.LENGTH_SHORT).show()
                    progressBarReg.isDismiss()
                }
            }.addOnFailureListener { e->
                Toast.makeText(this.context, "$e", Toast.LENGTH_SHORT).show()
            }

        }

        binding.btnLoginScreen.setOnClickListener {
            findNavController().navigate(R.id.action_register_to_signIn)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        //secondApp.delete()

    }
}