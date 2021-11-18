package my.edu.tarc.iotassignmentg11

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_intrusion_door.*
import my.edu.tarc.iotassignmentg11.databinding.FragmentIntrusionDoorBinding
import java.text.SimpleDateFormat
import java.util.*

class IntrusionDetectDoor: Fragment() {
    private var db: FirebaseDatabase =
        FirebaseDatabase.getInstance("https://bait2123-202105-12-2-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private var reference: DatabaseReference = db.reference.child("PI_12__CONTROL")
    private lateinit var reference2: DatabaseReference
    private lateinit var fStore: FirebaseFirestore
    private lateinit var fAuth: FirebaseAuth
    private var databaseListener: ValueEventListener? = null
    private var userID: String? = null


    private var _binding: FragmentIntrusionDoorBinding? = null
    private val binding get() = _binding!!

    private var doorDetect: String? = null
    private var doorFeature: Boolean = true
    private var toCheckDoor: Boolean = false
    private var toCheckClose: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentIntrusionDoorBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val secondApp: FirebaseApp = FirebaseApp.getInstance("IoTAssignment")
        fStore = FirebaseFirestore.getInstance(secondApp)
        fAuth = FirebaseAuth.getInstance(secondApp)
        userID = fAuth.currentUser?.uid

        binding.btnCheckDoorStatus.setOnClickListener() {
            doorFeature = true
            doorOpen()
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    toCheckDoor = false
                    doorFeature = false
                }
            },15000)
        }

    }

    private fun doorOpen(){
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

                val dformat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
                dformat.timeZone = TimeZone.getTimeZone("GMT+8")
                val strDate: String = dformat.format(currentDate).toString()

                val displayDateTime: String = dformat.format(currentDate).toString()

                binding.tvDStatus.visibility=View.VISIBLE
                binding.tvDoorDT.visibility=View.VISIBLE
                binding.tvDoorDateTime.visibility=View.VISIBLE
                binding.tvDoorIs.visibility=View.VISIBLE

                db.reference
                    .child("PI_12__$formattedDate")
                    .child(currentH)
                    .orderByKey()
                    .limitToLast(1)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            Log.d("RAND2", "Start Running")
                            for (h in snapshot.children) {

                                Log.d(
                                    "RAND2",
                                    h.child("rand2").getValue(String::class.java).toString()
                                )

                                //showImage = h.child("c").getValue(String::class.java).toString()
                                doorDetect =
                                    h.child("rand2").getValue(String::class.java).toString()
                                val doorValue = 400
                                if (doorFeature) {
                                    if (doorDetect !! <= doorValue.toString()) {
                                        if (toCheckDoor) {
                                            return
                                        } else {
                                            //Toast.makeText(context, "Door is opened!!", Toast.LENGTH_SHORT).show()
                                            binding.tvDStatus.text = "OPEN"
                                            binding.tvDoorDateTime.text = displayDateTime
                                            binding.ivCheckDoorStatus.visibility=View.INVISIBLE
                                            binding.imgViewDoorLock.visibility=View.INVISIBLE
                                            binding.imgViewDoorOpen.visibility=View.VISIBLE

                                            reference.child("relay2").setValue("1")
                                            reference.child("camera").setValue("1")
                                            reference.child("lcdbkR").setValue("5")
                                            reference.child("lcdbkG").setValue("15")
                                            reference.child("lcdbkB").setValue("5")
                                            reference.child("lcdtxt").setValue("*Door is opened*")

                                            val hashMapDoor = hashMapOf(
                                                "doorStatus" to "OPEN ",
                                                "time" to strDate,
                                            )

                                            fStore.collection("doorDetect")
                                                .document(userID!!)
                                                .collection("allDoorDetect")
                                                .add(hashMapDoor)
                                                .addOnSuccessListener {
                                                    Log.d(
                                                        ContentValues.TAG,
                                                        "Added Document with ID ${it.id}"
                                                    )
                                                }
                                                .addOnFailureListener {
                                                    Log.w(
                                                        ContentValues.TAG,
                                                        "Error adding document ${it.suppressedExceptions}"
                                                    )
                                                }
                                            toCheckDoor = true
                                        }
                                    } else {
                                        if (toCheckClose) {
                                            return
                                        } else {

                                            //Toast.makeText(context, "Door is closed.", Toast.LENGTH_SHORT).show()
                                            binding.tvDStatus.text = "CLOSE"
                                            binding.tvDoorDateTime.text = displayDateTime
                                            binding.ivCheckDoorStatus.visibility = View.INVISIBLE
                                            binding.imgViewDoorLock.visibility = View.VISIBLE
                                            binding.imgViewDoorOpen.visibility = View.INVISIBLE


                                            reference.child("relay2").setValue("0")
                                            reference.child("camera").setValue("0")
                                            reference.child("lcdbkR").setValue("15")
                                            reference.child("lcdbkG").setValue("5")
                                            reference.child("lcdbkB").setValue("5")
                                            reference.child("lcdtxt").setValue("*Door is closed*")

                                            val hashMapDoor = hashMapOf(
                                                "doorStatus" to "CLOSE",
                                                "time" to strDate,
                                            )

                                            fStore.collection("doorDetect")
                                                .document(userID!!)
                                                .collection("allDoorDetect")
                                                .add(hashMapDoor)
                                                .addOnSuccessListener {
                                                    Log.d(
                                                        ContentValues.TAG,
                                                        "Added Document with ID ${it.id}"
                                                    )
                                                }
                                                .addOnFailureListener {
                                                    Log.w(
                                                        ContentValues.TAG,
                                                        "Error adding document ${it.suppressedExceptions}"
                                                    )
                                                }

                                            toCheckClose = true
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


}