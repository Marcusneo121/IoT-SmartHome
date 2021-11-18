package my.edu.tarc.iotassignmentg11

import android.content.ContentValues.TAG
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import my.edu.tarc.iotassignmentg11.databinding.FragmentAirconBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class Aircond : Fragment() {
    private var _binding: FragmentAirconBinding? = null
//    private var db: FirebaseDatabase =
//        FirebaseDatabase.getInstance("https://bait2123-202105-12-default-rtdb.asia-southeast1.firebasedatabase.app")

    private var db: FirebaseDatabase =
        FirebaseDatabase.getInstance("https://bait2123-202105-12-2-default-rtdb.asia-southeast1.firebasedatabase.app")

    private var reference: DatabaseReference = db.reference.child("PI_12__CONTROL")
    private lateinit var reference2: DatabaseReference
    private lateinit var fStore: FirebaseFirestore
    private lateinit var fAuth: FirebaseAuth

    private val binding get() = _binding!!
    private var aircondStatus: Boolean = false
    private var temperature: Int = 24
    private var enablePressBtn: Boolean = false
    private var userID: String? = null
    private var houseTemp: String? = "reading..."
    private var databaseListener: ValueEventListener? = null
    private var toCheck: Boolean = false
    private var userTemp: Int = 30
    private var checkFeature: Boolean = true

    //private var liveAcStatus: String = ""
    //private var condition = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        if (!condition) {
//            val options = FirebaseOptions.Builder()
//                .setApplicationId("1:155508844801:android:ef5127a80fe8595a94d300")
//                .setApiKey("AIzaSyCCmz4_im6v7LEnzj0liiKce5BQI4N67U0")
//                .setDatabaseUrl("https://iotassignment-1de1e-default-rtdb.asia-southeast1.firebasedatabase.app/")
//                .setProjectId("iotassignment-1de1e")
//                .build()
//            this.context?.let { FirebaseApp.initializeApp(it, options, "IoTAssignment") }
//            condition = true

//            reference.get().addOnSuccessListener {
//                if(it.exists()){
//                    val liveAcStatus = it.child("relay1").value
//                    if(liveAcStatus == "1"){
//
//                    } else {
//
//                    }
//                }
//            }
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAirconBinding.inflate(inflater, container, false)




        binding.tvHouseTemp.text = houseTemp.toString()
        binding.tvTempSet.text = temperature.toString()
        binding.tvTemperature.text = "${temperature.toString()}°C"
        enablePressBtn = false
//        binding.btnPlus.isClickable = true
//        binding.btnPlus.isEnabled = false
//        binding.btnMinus.isClickable = true
//        binding.btnMinus.isEnabled = false
        binding.btnPlus.setBackgroundColor(Color.parseColor("#676767"))
        binding.btnMinus.setBackgroundColor(Color.parseColor("#676767"))
        return binding.root
    }

    //    private fun readData(){
//        databaseListener = object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//
//                val currentDate = Calendar.getInstance().time
//                val df = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
//                df.timeZone = TimeZone.getTimeZone("GMT+8")
//                val formattedDate = df.format(currentDate)
//
//                val hours = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
//                hours.timeZone = TimeZone.getTimeZone("GMT+8")
//                val currentH = hours.format(currentDate).substring(0, 2)
//
//                reference2 = db.reference
//                    .child("PI_12_$formattedDate").child(currentH)
//
//                if(snapshot.exists()){
//                    Log.d("Rand1", "It run in here")
//                    for(h in snapshot.children){
//                        houseTemp = h.child("rand1").getValue(PiData::class.java).toString()
//                        Log.d("Rand1", "$houseTemp")
//                    }
//                }
////                reference2
////                    .orderByKey()
////                    .limitToLast(1)
////                    .addListenerForSingleValueEvent()
////                    .get().addOnSuccessListener {
////                        if (it.exists()) {
////                            val tempRandom = it.child("rand1").value
////                            houseTemp = tempRandom.toString()
////                            binding.tvHouseTemp.text = houseTemp
////                        } else {
////                            Toast.makeText(context, "Some error occur!", Toast.LENGTH_SHORT).show()
////                        }
////                    }
//            }
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        }
//        reference2.addValueEventListener(databaseListener as ValueEventListener)
//    }
//

    override fun onStart() {
        super.onStart()
        if (databaseListener != null) reference2.addValueEventListener(databaseListener!!)
    }

    override fun onStop() {
        super.onStop()
        if (databaseListener != null) reference2.removeEventListener(databaseListener!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (databaseListener != null) reference2.removeEventListener(databaseListener!!)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val secondApp: FirebaseApp = FirebaseApp.getInstance("IoTAssignment")
        fStore = FirebaseFirestore.getInstance(secondApp)
        fAuth = FirebaseAuth.getInstance(secondApp)
        userID = fAuth.currentUser?.uid

        val aircondAnimation: LottieAnimationView = view.findViewById(R.id.aircondLottie)

        super.onViewCreated(view, savedInstanceState)

        binding.btnApply.setOnClickListener {
            userTemp = binding.etSetAircondTemp.text.toString().toInt()
            binding.etSetAircondTemp.clearFocus()
        }

        binding.switchOpenFeature.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                checkFeature = true
                binding.etSetAircondTemp.isFocusableInTouchMode = true
                binding.etSetAircondTemp.isEnabled = true
                binding.etSetAircondTemp.isFocusable = true
                binding.btnApply.isClickable = true
                binding.btnApply.isEnabled = true
                binding.btnApply.setBackgroundColor(Color.parseColor("#ABE3FB"))
                Toast.makeText(context, "Auto Temp Detection On", Toast.LENGTH_SHORT).show()

            } else {
                checkFeature = false
                binding.etSetAircondTemp.isFocusableInTouchMode = false
                binding.etSetAircondTemp.isEnabled = false
                binding.etSetAircondTemp.isFocusable = false
                binding.btnApply.isClickable = false
                binding.btnApply.isEnabled = false
                binding.btnApply.setBackgroundColor(Color.parseColor("#676767"))
                Toast.makeText(context, "Auto Temp Detection Off", Toast.LENGTH_SHORT).show()
            }
        }


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
                                Log.d("Rand1", "It run in here")
                                for (h in snapshot.children) {
                                    houseTemp =
                                        h.child("rand1").getValue(String::class.java).toString()
                                    binding.tvHouseTemp.text = houseTemp.toString()
                                    Log.d("Tempe", "$houseTemp")

                                    val hashMapOn = hashMapOf(
                                        "airconStatus" to "On",
                                        "temp" to temperature,
                                        "time" to strDate,
                                        //"userID" to userID
                                    )

                                    val hashMapOff = hashMapOf(
                                        "airconStatus" to "Off",
                                        "temp" to temperature,
                                        "time" to strDate,
                                        //"userID" to userID
                                    )
                                    if (checkFeature) {
                                        if (houseTemp!! >= userTemp.toString()) {
                                            if (toCheck) {
                                                return
                                            } else {
                                                //this is for weird bug on string to int conversion
                                                if(houseTemp!! == "10.0" || houseTemp!! == "9.0" || houseTemp!! == "8.0"
                                                    || houseTemp!! == "7.0" || houseTemp!! == "6.0" || houseTemp!! == "5.0"
                                                    || houseTemp!! == "4.0" || houseTemp!! == "3.0" || houseTemp!! == "2.0"
                                                    || houseTemp!! == "1.0" || houseTemp!! == "0.0"){
                                                    return
                                                }
                                                binding.btnPlus.setBackgroundColor(
                                                    Color.parseColor(
                                                        "#ABE3FB"
                                                    )
                                                )
                                                binding.btnMinus.setBackgroundColor(
                                                    Color.parseColor(
                                                        "#ABE3FB"
                                                    )
                                                )
                                                reference.child("relay1").setValue("1")
                                                reference.child("camera").setValue("1")
                                                reference.child("lcdbkR").setValue("5")
                                                reference.child("lcdbkG").setValue("15")
                                                reference.child("lcdbkB").setValue("5")
                                                reference.child("lcdtxt")
                                                    .setValue("==AC temp: ${temperature.toString()}C==")

                                                fStore.collection("airconOnOff")
                                                    .document(userID!!)
                                                    .collection("allAirconOnOff")
                                                    .add(hashMapOn)
                                                    .addOnSuccessListener {
                                                        Log.d(
                                                            TAG,
                                                            "Added Document with ID ${it.id}"
                                                        )
                                                    }
                                                    .addOnFailureListener {
                                                        Log.w(
                                                            TAG,
                                                            "Error adding document ${it.suppressedExceptions}"
                                                        )
                                                    }

                                                aircondStatus = true
                                                enablePressBtn = true
                                                aircondAnimation.setMinAndMaxFrame(67, 157)
                                                binding.tvAirconStatus.text = "On"
                                                binding.btnToggle.text = "Turn Off Aircon"
                                                disableButtonAll()
                                                binding.tvTempSet.text = temperature.toString()
                                                toCheck = true
                                            }
                                        } else {

                                            if (aircondStatus == false) {
                                                return
                                            } else {
                                                reference.child("relay1").setValue("0")
                                                reference.child("camera").setValue("1")
                                                reference.child("lcdbkR").setValue("15")
                                                reference.child("lcdbkG").setValue("5")
                                                reference.child("lcdbkB").setValue("5")
                                                reference.child("lcdtxt")
                                                    .setValue("=Air-Con is Off=")

                                                fStore.collection("airconOnOff")
                                                    .document(userID!!)
                                                    .collection("allAirconOnOff")
                                                    .add(hashMapOff)
                                                    .addOnSuccessListener {
                                                        Log.d(
                                                            TAG,
                                                            "Added Document with ID ${it.id}"
                                                        )
                                                    }
                                                    .addOnFailureListener {
                                                        Log.w(
                                                            TAG,
                                                            "Error adding document ${it.suppressedExceptions}"
                                                        )
                                                    }

                                                aircondStatus = false
                                                enablePressBtn = false
                                                aircondAnimation.setMinAndMaxFrame(0, 0)
                                                binding.tvAirconStatus.text = "Off"
                                                binding.btnToggle.text = "Turn On Air-con"
                                                disableButtonAll()
                                                binding.tvTempSet.text = temperature.toString()
                                                toCheck = false
                                            }
                                        }
                                    } else {
                                        return
                                    }
                                    enableButtonAll()
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

        if (!aircondStatus) {
            aircondAnimation.setMinAndMaxFrame(0, 0)
        } else {
            aircondAnimation.setMinAndMaxFrame(0, 209)
        }

        binding.btnMinus.setOnClickListener {

            val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
            val date = Date()
            val strDate: String = dateFormat.format(date).toString()

            if (!enablePressBtn) {
                Toast.makeText(
                    this.context,
                    "Turn on AC before set temperature",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (temperature != 16 && temperature >= 16) {
                    temperature--;
                }
                reference.child("lcdtxt").setValue("==AC temp: ${temperature}C==")
                binding.tvTempSet.text = temperature.toString()
                binding.tvTemperature.text = "${temperature.toString()}°C"

                val hashMapTempChange = hashMapOf(
                    "temp" to temperature,
                    "time" to strDate,
                    //"userID" to userID
                )

                fStore.collection("airconTempChange")
                    .document(userID!!)
                    .collection("allTempChange")
                    .add(hashMapTempChange)
                    .addOnSuccessListener {
                        Log.d(TAG, "Added Document with ID ${it.id}")
                    }
                    .addOnFailureListener {
                        Log.w(TAG, "Error adding document ${it.suppressedExceptions}")
                    }

                reference.child("camera").setValue("1")
            }
        }

        binding.btnPlus.setOnClickListener {

            val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
            val date = Date()
            val strDate: String = dateFormat.format(date).toString()
            val plusTemp: Int = temperature

            if (!enablePressBtn) {
                Toast.makeText(
                    this.context,
                    "Turn on AC before set temperature",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (temperature != 30 && temperature <= 30) {
                    temperature++;
                }
                reference.child("lcdtxt").setValue("==AC temp: ${temperature}C==")
                binding.tvTempSet.text = temperature.toString()
                binding.tvTemperature.text = "${temperature.toString()}°C"

                val hashMapTempChange = hashMapOf(
                    "temp" to temperature,
                    "time" to strDate,
                    //"userID" to userID
                )

                fStore.collection("airconTempChange")
                    .document(userID!!)
                    .collection("allTempChange")
                    .add(hashMapTempChange)
                    .addOnSuccessListener {
                        Log.d(TAG, "Added Document with ID ${it.id}")
                    }
                    .addOnFailureListener {
                        Log.w(TAG, "Error adding document ${it.suppressedExceptions}")
                    }

                reference.child("camera").setValue("1")
            }
        }


        binding.btnToggle.setOnClickListener {

            val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
            val date = Date()
            val strDate: String = dateFormat.format(date).toString()
            //myRef.child("datetime").setValue(strDate)


            val hashMapOn = hashMapOf(
                "airconStatus" to "On",
                "temp" to temperature,
                "time" to strDate,
                //"userID" to userID
            )

            val hashMapOff = hashMapOf(
                "airconStatus" to "Off",
                "temp" to temperature,
                "time" to strDate,
                //"userID" to userID
            )

            if (binding.btnToggle.text == "Turn Off Aircon") {
                binding.btnPlus.isClickable = true
                binding.btnPlus.isEnabled = false
                binding.btnMinus.isClickable = true
                binding.btnMinus.isEnabled = false
                reference.child("relay1").setValue("0")
                reference.child("camera").setValue("1")
                reference.child("lcdbkR").setValue("15")
                reference.child("lcdbkG").setValue("5")
                reference.child("lcdbkB").setValue("5")
                reference.child("lcdtxt").setValue("=Air-Con is Off=")

                fStore.collection("airconOnOff")
                    .document(userID!!)
                    .collection("allAirconOnOff")
                    .add(hashMapOff)
                    .addOnSuccessListener {
                        Log.d(TAG, "Added Document with ID ${it.id}")
                    }
                    .addOnFailureListener {
                        Log.w(TAG, "Error adding document ${it.suppressedExceptions}")
                    }

                aircondStatus = false
                enablePressBtn = false
                aircondAnimation.setMinAndMaxFrame(0, 0)
                binding.tvAirconStatus.text = "Off"
                binding.btnToggle.text = "Turn On Air-con"
                disableButtonAll()
            } else {
                binding.btnPlus.isClickable = true
                binding.btnPlus.isEnabled = true
                binding.btnMinus.isClickable = true
                binding.btnMinus.isEnabled = true
                binding.btnPlus.setBackgroundColor(Color.parseColor("#ABE3FB"))
                binding.btnMinus.setBackgroundColor(Color.parseColor("#ABE3FB"))
                reference.child("relay1").setValue("1")
                reference.child("camera").setValue("1")
                reference.child("lcdbkR").setValue("5")
                reference.child("lcdbkG").setValue("15")
                reference.child("lcdbkB").setValue("5")
                reference.child("lcdtxt").setValue("==AC temp: ${temperature}C==")

//                fStore.collection("airconOn")
//                    .add(hashMapOn)
//                    .addOnSuccessListener {
//                        Log.d(TAG, "Added Document with ID ${it.id}")
//                    }
//                    .addOnFailureListener {
//                        Log.w(TAG, "Error adding document ${it.suppressedExceptions}")
//                    }

                fStore.collection("airconOnOff")
                    .document(userID!!)
                    .collection("allAirconOnOff")
                    .add(hashMapOn)
                    .addOnSuccessListener {
                        Log.d(TAG, "Added Document with ID ${it.id}")
                    }
                    .addOnFailureListener {
                        Log.w(TAG, "Error adding document ${it.suppressedExceptions}")
                    }

                aircondStatus = true
                enablePressBtn = true
                //aircondAnimation.setMinAndMaxFrame(0,209)
                aircondAnimation.setMinAndMaxFrame(67, 157)
                binding.tvAirconStatus.text = "On"
                binding.btnToggle.text = "Turn Off Aircon"
                disableButtonAll()
            }
            Toast.makeText(
                this.context,
                "Adjustment of AC only available after 15secs",
                Toast.LENGTH_LONG
            ).show()
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                enableButtonAll()
                if (aircondStatus == false) {
                    binding.btnMinus.setBackgroundColor(Color.parseColor("#676767"))
                    binding.btnPlus.setBackgroundColor(Color.parseColor("#676767"))
//                        binding.btnPlus.isEnabled = false
//                        binding.btnMinus.isEnabled = false
//                        binding.btnPlus.isClickable = true
//                        binding.btnMinus.isClickable = true
                } else {
                    binding.btnPlus.setBackgroundColor(Color.parseColor("#ABE3FB"))
                    binding.btnMinus.setBackgroundColor(Color.parseColor("#ABE3FB"))
                }
            }, 15000)
        }
    }

    private fun disableButtonAll() {
        binding.btnPlus.isClickable = false
        binding.btnPlus.isEnabled = false
        binding.btnMinus.isClickable = false
        binding.btnMinus.isEnabled = false
        binding.btnToggle.isClickable = false
        binding.btnToggle.isEnabled = false
        binding.btnToggle.setBackgroundColor(Color.parseColor("#676767"))
        binding.btnPlus.setBackgroundColor(Color.parseColor("#676767"))
        binding.btnMinus.setBackgroundColor(Color.parseColor("#676767"))
    }

    private fun enableButtonAll() {
        binding.btnPlus.isClickable = true
        binding.btnPlus.isEnabled = true
        binding.btnMinus.isClickable = true
        binding.btnMinus.isEnabled = true
        binding.btnToggle.isClickable = true
        binding.btnToggle.isEnabled = true
        binding.btnToggle.setBackgroundColor(Color.parseColor("#ABE3FB"))
        binding.btnPlus.setBackgroundColor(Color.parseColor("#ABE3FB"))
        binding.btnMinus.setBackgroundColor(Color.parseColor("#ABE3FB"))
    }





}





