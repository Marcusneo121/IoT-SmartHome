package my.edu.tarc.iotassignmentg11

import android.content.ContentValues
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_camera.*
import my.edu.tarc.iotassignmentg11.databinding.FragmentCameraBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class Camera : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private var db: FirebaseDatabase = FirebaseDatabase.getInstance("https://bait2123-202105-12-2-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private var reference: DatabaseReference = db.reference.child("PI_12__CONTROL")
    private lateinit var reference2: DatabaseReference
    //retrieve data
    private lateinit var database: DatabaseReference
    //user login & report
    private lateinit var fStore: FirebaseFirestore
    private lateinit var fAuth: FirebaseAuth
    //private var storage: FirebaseStorage =
        //FirebaseStorage.getInstance("gs://bait2123-202105-12-2.appspot.com")

    private var databaseListener: ValueEventListener? = null
    private var userID: String? = null
    private var detectPerson: Boolean = false
    private var cameraCheck: Boolean = true
    private var cameraUltrasonic: String? = null
    var cctvRecord = ArrayDeque<String>()
    private lateinit var recyclerView: RecyclerView
    //private lateinit var cctvRecordList: ArrayList<CCTVReportArrayList>
//^ if only wan report at cctv report delete this array

    private var viewPressBtn: Boolean = false
    private var checking: Boolean = false

    private var imageList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding.ActivityMainBinding.inflate(layoutInflater)
        //val view = binding.root
        //setContentView(view)

//        binding.recyclerViewCCTVRecord.apply{
//            layoutManager = LinearLayoutManager(this@MainActivity)
//        }

        //fetchData()
        arguments?.let {
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        //_binding!!.editTextInterval.isEnabled = false
        //_binding!!.btnTimePicker.isEnabled = false
        binding.btnView.isClickable = true

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if (databaseListener != null) reference2.addValueEventListener(databaseListener!!)
    }

    override fun onStop() {
        super.onStop()
        if (databaseListener != null) reference2.removeEventListener(databaseListener!!)
    }

    private fun onCCTV(){
        imgOn.visibility = View.VISIBLE
        imgOff.visibility = View.INVISIBLE
        imgCCTV.visibility = View.VISIBLE

        btnCCTVOn.visibility = View.INVISIBLE
        btnCCTVOff.visibility = View.VISIBLE

        binding.cctvStatus.text = "ON"

        reference.child("relay1").setValue("1")
        reference.child("lcdbkR").setValue("5")
        reference.child("lcdbkG").setValue("15")
        reference.child("lcdbkB").setValue("5")
        reference.child("lcdtxt").setValue("CCTV switched ON")

        Timer().schedule(object : TimerTask() {
            override fun run() {
                Log.d("check","image captured")
                reference.child("camera").setValue("1")
            }
        }, 1000 * 10)

//        val handler = Handler(Looper.getMainLooper())
//        handler.postDelayed({
//            reference.child("camera").setValue("1")
//        }, 3000)

    }

    private fun offCCTV(){
        imgOn.visibility = View.INVISIBLE
        imgOff.visibility = View.VISIBLE
        imgCCTV.visibility = View.INVISIBLE

        btnCCTVOn.visibility = View.VISIBLE
        btnCCTVOff.visibility = View.INVISIBLE

        binding.cctvStatus.text = "OFF"


        reference.child("relay1").setValue("0")
        reference.child("lcdbkR").setValue("5")
        reference.child("lcdbkG").setValue("15")
        reference.child("lcdbkB").setValue("5")
        reference.child("lcdtxt").setValue("CCTV switched OFF")
        reference.child("ledlgt").setValue("0")
    }

    private fun displayPic(){
        //cam
        val storage = FirebaseStorage.getInstance("gs://bait2123-202105-12-2.appspot.com")
        val listRef = storage.getReference("PI_12__CONTROL")

        listRef.listAll()
            .addOnSuccessListener { listResult ->
                imageList.clear()
                for(allFile in listResult.items){
                    allFile.downloadUrl.addOnSuccessListener{
                        imageList.add(it.toString())
                        Log.d("item", it.toString())
                        //add this list of url to Firebase
                    }
                }
                Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Log.d("item", it.toString())
            }

        /*try{
            binding.btnView.isEnabled = false
            Toast.makeText(context, "Retrieving image, try again in 10 seconds...", Toast.LENGTH_SHORT).show()
        }catch (e: Exception){

        }*/

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            //binding.btnView.isEnabled = true

            val lastPosition = imageList.size - 1
            Log.d("URL", imageList[lastPosition])
            this.context?.let {
                Glide.with(it)
                    .load(imageList[lastPosition])
                    .into(binding.imgCCTV)
            }
            updateRecord(imageList[lastPosition].toString())
        }, 10000)

        reference.child("ledlgt").setValue("1")
        reference.child("lcdtxt").setValue("CCTV Image Captured!")
    }

    private fun readData(){
        reference2 = FirebaseDatabase.getInstance().getReference()
        databaseListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentDate = Calendar.getInstance().time
                val df = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                df.timeZone = TimeZone.getTimeZone("GMT+8")
                val formattedDate = df.format(currentDate)

                val hours = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                hours.timeZone = TimeZone.getTimeZone("GMT+8")
                val currentH = hours.format(currentDate).substring(0,2)

                val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                val date = Date()
                val strDate: String = dateFormat.format(date).toString()

                //display the formatted date in DB
                db.reference
                    .child("PI_12__$formattedDate")
                    .child(currentH)
                    .orderByKey()
                    .limitToLast(1)
                    .addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists())
                                Log.d("RAND1", "RUN HERE")
                            for (h in snapshot.children) {
                                Log.d(
                                    "RAND1",
                                    h.child("rand1").getValue(String::class.java).toString()
                                )

                                cameraUltrasonic =
                                    h.child("rand1").getValue(String::class.java).toString()
                                val ultrasonicCompare: Double = cameraUltrasonic!!.toDouble()
                                if (cameraCheck) {
                                    if (ultrasonicCompare <= 100) {
                                        if (checking) {
                                            return
                                        } else {
                                            detectPerson = true
                                            Log.d("CHECK", "PERSON DETECT")
                                            //reference.child("lcdtxt").setValue("Person detected.")
                                            checking = true
                                        }
                                    } else {
                                        detectPerson = false
                                        Log.d("CHECK", "PERSON NOT DETECT")
                                        //reference.child("lcdtxt").setValue("Ppl Not detected")
                                        checking = false
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

    private fun updateRecord(s: String){
        //val timeNow = Calendar.getInstance().getTime().toString()
        val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val date = Date()
        val strDate: String = dateFormat.format(date).toString()

        val hashMapCCTVRecord = hashMapOf(
            "imgURL" to s,
            "time" to strDate
        )

        //CCTV Report
        fStore.collection("CCTVRecord")
            .document(userID!!)
            .collection("CCTVRecord")
            .add(hashMapCCTVRecord)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "Added Document with ID ${it.id}")
            }
            .addOnFailureListener {
                Log.w(ContentValues.TAG,"Error adding document ${it.suppressedExceptions}")
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val secondApp: FirebaseApp = FirebaseApp.getInstance("IoTAssignment")
        fStore = FirebaseFirestore.getInstance(secondApp)
        fAuth = FirebaseAuth.getInstance(secondApp)
        userID = fAuth.currentUser?.uid

        //fetchData()

        binding.btnCCTVOn.setOnClickListener {
            viewPressBtn = true
            onCCTV()
            checking = true
            readData()
        }

        binding.btnCCTVOff.setOnClickListener {
            binding.btnView.isClickable = true
            offCCTV()
            checking = false
            viewPressBtn = false

        }

        binding.btnView.setOnClickListener {
            if (!viewPressBtn) {
                Toast.makeText(
                    this.context,
                    "Turn on CCTV to view",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("CHECK","FALSE")

            }else{
                Log.d("CHECK","TURNED ON")
                displayPic()
            }
        }
    }
}