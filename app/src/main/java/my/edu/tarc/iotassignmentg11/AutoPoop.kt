package my.edu.tarc.iotassignmentg11

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.NonCancellable.cancel
import my.edu.tarc.iotassignmentg11.databinding.FragmentAutoPoopingPadBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Handler

class AutoPoop: Fragment() {
    private var _binding: FragmentAutoPoopingPadBinding? = null
    private val binding get() = _binding!!
    private var db: FirebaseDatabase =
        FirebaseDatabase.getInstance("https://bait2123-202105-12-2-default-rtdb.asia-southeast1.firebasedatabase.app")
    private var reference: DatabaseReference = db.reference.child("PI_12__CONTROL")
    private lateinit var reference2: DatabaseReference
    var autoPoopRecord = ArrayDeque<String>()
    private lateinit var fStore: FirebaseFirestore
    private lateinit var fAuth: FirebaseAuth
    private var databaseListener: ValueEventListener? = null
    private var poopCheck: Boolean = false
    private var dogCheck: Boolean = false
    private var userID: String? = null

    private var checkBoo:Boolean=true
    private var checkData:Boolean = false
    private var poopRecord:String?=null
    private var poopPresence:String?= null
    private var dogPresence:String? = null
    private var ultrasonicValue: String? = null
    private var potentioValue: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onStart() {
        super.onStart()
        if (databaseListener != null) reference2.addValueEventListener(databaseListener!!)
    }

    override fun onStop() {
        super.onStop()
        if (databaseListener != null) reference2.removeEventListener(databaseListener!!)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAutoPoopingPadBinding.inflate(inflater, container, false)
        poopPresence = binding.textViewPresencePoop.text.toString()
        dogPresence=binding.textViewDogPresence.text.toString()







        _binding!!.switchAutoPoop.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                readData()
                checkData=false
                checkBoo=true

            }else{
                checkBoo=false
            }


        }


        return binding.root
    }






    private fun readData() {
        reference2 = FirebaseDatabase.getInstance().getReference()
        databaseListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentDate = Calendar.getInstance().time
                val df = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                df.timeZone = TimeZone.getTimeZone("GMT+8")
                val formattedDate = df.format(currentDate)

                val hours = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                hours.timeZone = TimeZone.getTimeZone("GMT+8")
                val currentH = hours.format(currentDate).substring(0, 2)

                val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                val date = Date()
                val strDate: String = dateFormat.format(date).toString()

                db.reference
                    .child("PI_12__$formattedDate")
                    .child(currentH)
                    .orderByKey()
                    .limitToLast(1)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                Log.d("RAND1", "RUN HERE")
                                for (h in snapshot.children) {
                                    Log.d(
                                        "RAND1",
                                        h.child("rand1").getValue(String::class.java).toString()
                                    )
                                    Log.d(
                                        "RAND2",
                                        h.child("rand2").getValue(String::class.java).toString()
                                    )

                                    ultrasonicValue = h.child("rand1").getValue().toString()
                                    potentioValue = h.child("rand2").getValue(String::class.java)

                                    if (checkData) {
                                        return
                                    } else {

                                        try {
                                            var ultrasonicInt: Double = ultrasonicValue!!.toDouble()
                                            var potentioValueDouble: Double =
                                                potentioValue!!.toDouble()

                                            if (potentioValueDouble <= 1000) {
                                                dogCheck = true
                                            }
                                            if (ultrasonicInt <= 80) {
                                                poopCheck = true

                                            }
                                        } catch (ex: Exception) {
                                            Log.d("ERROR", ex.toString())
                                        }

                                        if (poopCheck) {
                                            binding.textViewPresencePoop.text = "DETECTED"
                                            //updateRecord("POOP DETECTED")

                                        } else {
                                            binding.textViewPresencePoop.text = "NOT DETECTED"
                                        }

                                        if (dogCheck) {
                                            binding.textViewDogPresence.text = "DETECTED"
                                            //updateRecord("DOG DETECTED")

                                        } else {
                                            binding.textViewDogPresence.text = "NOT DETECTED"
                                        }

                                        if (dogCheck == true && poopCheck == true) {
                                            reference.child("relay1").setValue("1")
                                            reference.child("lcdtxt").setValue("==CLEANING POOP=")
                                            reference.child("lcdbkR").setValue("5")
                                            reference.child("lcdbkG").setValue("15")
                                            reference.child("lcdbkB").setValue("5")
                                                updateRecord("Poop Detected ! Cleaning Poop")
                                                Log.d("CHECK","CLEANING")
                                            checkData= true



                                            Timer().schedule(object : TimerTask() {
                                                override fun run() {
                                                    if(checkBoo == true){
                                                   reference.child("relay1").setValue("0")
                                                    reference.child("lcdtxt").setValue("NO POOP DETECTED")
                                                    reference.child("lcdbkR").setValue("0")
                                                    reference.child("lcdbkG").setValue("10")
                                                    reference.child("lcdbkB").setValue("0")
                                                    Log.d("CHECK","CLEANING DONE, START FUNCTION")

                                                    poopCheck = false
                                                    dogCheck = false
                                                    checkData=false
                                                    }else{
                                                        checkData=true
                                                    }

                                                }
                                            }, 1000 * 5)

                                        }


                                    }
                                }


                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }

        reference2.addValueEventListener(databaseListener as ValueEventListener)


    }


    private fun updateRecord(s: String) {

        val timeNow = Calendar.getInstance().time
        val formattedTime = SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(timeNow)


        autoPoopRecord.push(formattedTime + "  " + s + "\n")
        val formattedString: String = autoPoopRecord.toString()
            .replace(",", "") //remove the commas
            .replace("[", "") //remove the right bracket
            .replace("]", "") //remove the left bracket
            .trim() //remove trailing spaces from partially initialized arrays

        binding.textViewAutoPoopRecord.text = formattedString

        val hashMapFeedRecord = hashMapOf(
            "details" to s,
            "time" to formattedTime,
            //"userID" to userID
        )

        fStore.collection("autoPoopRecord")
            .document(userID!!)
            .collection("allAutoPoopRecord")
            .add(hashMapFeedRecord)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "Added Document with ID ${it.id}")
            }
            .addOnFailureListener {
                Log.w(ContentValues.TAG, "Error adding document ${it.suppressedExceptions}")
            }

    }




        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            val secondApp: FirebaseApp = FirebaseApp.getInstance("IoTAssignment")
            fStore = FirebaseFirestore.getInstance(secondApp)
            fAuth = FirebaseAuth.getInstance(secondApp)
            userID = fAuth.currentUser?.uid

        }
    }
