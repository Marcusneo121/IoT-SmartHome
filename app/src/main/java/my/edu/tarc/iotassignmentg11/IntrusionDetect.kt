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
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import io.grpc.Context
import kotlinx.android.synthetic.main.fragment_intrusion_detect.*
import my.edu.tarc.iotassignmentg11.databinding.FragmentIntrusionDetectBinding
import java.text.SimpleDateFormat
import java.util.*


class IntrusionDetect : Fragment() {

    private var db: FirebaseDatabase =
        FirebaseDatabase.getInstance("https://bait2123-202105-12-2-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private var reference: DatabaseReference = db.reference.child("PI_12__CONTROL")
    private lateinit var reference2: DatabaseReference
    private lateinit var fStore: FirebaseFirestore
    private lateinit var fAuth: FirebaseAuth
    private var databaseListener: ValueEventListener? = null
    private var storageListener: ValueEventListener? = null
    private var userID: String? = null

    private var storage: FirebaseStorage =
        FirebaseStorage.getInstance("gs://bait2123-202105-12-2.appspot.com")
    private var listref: StorageReference = storage.getReference().child("PI_12__CONTROL/")

    private var _binding: FragmentIntrusionDetectBinding? = null
    private val binding get() = _binding!!

    private var distanceDetect: String? = null
    private var doorDetect: String? = null
    private var showImage: String? = null
    private var detectFeature: Boolean = true
    private var toCheck: Boolean = false
    private var doorFeature: Boolean = true
    private var toCheckDoor: Boolean = false


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
        _binding = FragmentIntrusionDetectBinding.inflate(inflater, container, false)
        return binding.root

        binding.txtViewDetect.text = distanceDetect

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val secondApp: FirebaseApp = FirebaseApp.getInstance("IoTAssignment")
        fStore = FirebaseFirestore.getInstance(secondApp)
        fAuth = FirebaseAuth.getInstance(secondApp)
        userID = fAuth.currentUser?.uid

        binding.btnSound.isClickable = false
        binding.btnSound.isEnabled = false
        binding.btnLight.isClickable = false
        binding.btnLight.isEnabled = false

        binding.ivDoorBtn.setOnClickListener {
            findNavController().navigate(R.id.action_intrusionDetect_to_intrusionDetectDoor)
        }


        //val switch: Switch =_binding!!.switchBtn
        binding.switchBtn.setOnCheckedChangeListener() { _, isChecked ->
            if (isChecked) {
                switchOn()
                detectFeature = true
                setData()

            } else {
                switchOff()
                detectFeature = false

            }
        }

        binding.btnShow.setOnClickListener() {
            val frame = binding.frameContent
            val show = frame.isVisible
            if (show) {
                binding.frameContent.visibility = View.GONE
            } else {
                binding.frameContent.visibility = View.VISIBLE
            }
        }



        binding.btnDisplayImg.setOnClickListener(){
            /*val imageList = ArrayList<String>()
            val lastPosition = imageList.size - 1
            val listImg = sreference.listAll().toString()
            imageList.add(listImg)

            val imageList = ArrayList<String>()
            sreference.listAll().addOnSuccessListener{ listResult ->

                imageList.clear()
                for (fileRef in listResult.items) {
                    fileRef.downloadUrl.addOnSuccessListener { uri ->
                        imageList.add(uri.toString())
                        Log.d("item", uri.toString())
                    }
                }
            }
            val lastPosition = imageList.size - 1
            //val lastImg =imageList.get(lastPosition)
            //binding.txtViewTest.text= lastImg*/
            val listRef = storage.getReference("PI_12__CONTROL/")

            val imageList = ArrayList<String>()
            listRef.listAll()
                .addOnSuccessListener { listResult ->
                    imageList.clear()
                    for (allFile in listResult.items) {
                        allFile.downloadUrl.addOnSuccessListener {
                            imageList.add(it.toString())
                            Log.d("item", it.toString())
                        }
                    }
                    Toast.makeText(context, "Retrieving...", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Log.d("item", it.toString())
                }

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                val lastPosition = imageList.size - 1
                //binding.txtViewTest.text = imageList[lastPosition]
                Log.d("URL", imageList[lastPosition])
                //Picasso.with(this.context).load(imageList[lastPosition]).into(binding.imgSnap)
                Glide.with(this /* context */)
                    .load(imageList[lastPosition])
                    .into(imgSnap)
            }, 15000)


            }
        binding.btnSound.setOnClickListener() {
        val btnSoundText = binding.btnSound.text.toString()

            if (btnSoundText == "ON") {
                binding.btnSound.setText("OFF")
                binding.btnSound.setBackgroundColor(Color.parseColor("#E65555"))
                imgAudio.visibility = View.INVISIBLE
                imgMute.visibility = View.VISIBLE
                reference.child("buzzer").setValue("0")


            } else if (btnSoundText == "OFF") {
                binding.btnSound.setText("ON")
                binding.btnSound.setBackgroundColor(Color.parseColor("#03A9F4"))
                imgAudio.visibility = View.VISIBLE
                imgMute.visibility = View.INVISIBLE
                reference.child("buzzer").setValue("1")

            }
        }

        binding.btnLight.setOnClickListener() {
        val btnLightText = binding.btnLight.text.toString()

            if (btnLightText == "ON") {
                binding.btnLight.setText("OFF")
                binding.btnLight.setBackgroundColor(Color.parseColor("#E65555"))
                imgLight.visibility = View.INVISIBLE
                imgDim.visibility = View.VISIBLE

                reference.child("ledlgt").setValue("0")

            } else {
                binding.btnLight.setText("ON")
                binding.btnLight.setBackgroundColor(Color.parseColor("#3DDDCE"))
                imgLight.visibility = View.VISIBLE
                imgDim.visibility = View.INVISIBLE

                reference.child("ledlgt").setValue("1")
            }
        }

        val seek = binding.seekBarDistance
        seek?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

        override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
            txtViewDistance.text = seek.progress.toString()

        }

        override fun onStartTrackingTouch(seek: SeekBar) {
        }

        override fun onStopTrackingTouch(seek: SeekBar) {

        }

        })

        binding.btnContact.setOnClickListener() {
        var index = 0
        if (index == 0) {
            val phone_intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel: 999")
            }
            startActivity(phone_intent)
        }
        }

        }

    private fun switchOn() {
        Toast.makeText(this.context, "Intrusion alarm is ON", Toast.LENGTH_SHORT).show()
        imgViewInsecure.visibility = View.INVISIBLE
        imgViewSecure.visibility = View.VISIBLE

        imgAudio.visibility = View.VISIBLE
        imgMute.visibility = View.INVISIBLE

        imgLight.visibility = View.VISIBLE
        imgDim.visibility = View.INVISIBLE

        imgSnap.visibility = View.VISIBLE
        tv8.visibility = View.VISIBLE
        txtDate.visibility = View.VISIBLE
        tv9.visibility = View.VISIBLE
        txtTime.visibility = View.VISIBLE
        tv3.visibility = View.VISIBLE
        txtViewDetect.visibility = View.VISIBLE

        binding.btnSound.isClickable = true
        binding.btnSound.isEnabled = true
        binding.btnLight.isClickable = true
        binding.btnLight.isEnabled = true
        binding.btnDisplayImg.visibility=View.VISIBLE


        binding.btnSound.setText("ON")
        binding.btnSound.setBackgroundColor(Color.parseColor("#03A9F4"))
        binding.btnLight.setText("ON")
        binding.btnLight.setBackgroundColor(Color.parseColor("#3DDDCE"))

        val distanceVal: Int = 50
        binding.seekBarDistance.progress = distanceVal

        reference.child("relay1").setValue("1")
        reference.child("lcdbkR").setValue("5")
        reference.child("lcdbkG").setValue("15")
        reference.child("lcdbkB").setValue("5")
        reference.child("lcdtxt").setValue("*Turn on alarm* ")

        val currentDate = Calendar.getInstance().time

        val dformat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        dformat.timeZone = TimeZone.getTimeZone("GMT+8")
        val strDate: String = dformat.format(currentDate).toString()

        val hashMapAlarmOn = hashMapOf(
        "alarmStatus" to "Intrusion alarm is ON ",
        "time" to strDate,
        )

        fStore.collection("alarmOnOff")
        .document(userID!!)
        .collection("allAlarmOnOff")
        .add(hashMapAlarmOn)
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

        }

    private fun switchOff() {
        Toast.makeText(this.context, "Intrusion alarm is OFF", Toast.LENGTH_SHORT).show()

        imgViewInsecure.visibility = View.VISIBLE
        imgViewSecure.visibility = View.INVISIBLE

        imgAudio.visibility = View.INVISIBLE
        imgMute.visibility = View.INVISIBLE

        imgLight.visibility = View.INVISIBLE
        imgDim.visibility = View.INVISIBLE

        imgSnap.visibility = View.INVISIBLE
        tv8.visibility = View.INVISIBLE
        txtDate.visibility = View.INVISIBLE
        tv9.visibility = View.INVISIBLE
        txtTime.visibility = View.INVISIBLE
        tv3.visibility = View.INVISIBLE
        txtViewDetect.visibility = View.INVISIBLE
        frameContent.visibility = View.GONE
        binding.btnSound.isClickable = false
        binding.btnSound.isEnabled = false
        binding.btnLight.isClickable = false
        binding.btnLight.isEnabled = false
        binding.btnDisplayImg.visibility=View.INVISIBLE


        reference.child("relay1").setValue("0")
        reference.child("buzzer").setValue("0")
        reference.child("ledlgt").setValue("0")
        reference.child("camera").setValue("1")
        reference.child("lcdbkR").setValue("15")
        reference.child("lcdbkG").setValue("5")
        reference.child("lcdbkB").setValue("5")
        reference.child("lcdtxt").setValue("*Turn off alarm*")

        val currentDate = Calendar.getInstance().time

        val dformat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        dformat.timeZone = TimeZone.getTimeZone("GMT+8")
        val strDate: String = dformat.format(currentDate).toString()

        val hashMapAlarmOff = hashMapOf(
        "alarmStatus" to "Intrusion alarm is OFF ",
        "time" to strDate,
        )

        fStore.collection("alarmOnOff")
        .document(userID!!)
        .collection("allAlarmOnOff")
        .add(hashMapAlarmOff)
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


        }


    private fun setData() {
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

        val displayDate: String = df.format(currentDate).toString()

        val displayTime: String = hours.format(currentDate).toString()


        db.reference
            .child("PI_12__$formattedDate")
            .child(currentH)
            .orderByKey()
            .limitToLast(1)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("Rand1", "Start Running")
                    for (h in snapshot.children) {

                        Log.d(
                            "RAND1",
                            h.child("rand1").getValue(String::class.java).toString()
                        )

                        showImage = h.child("c").getValue(String::class.java).toString()
                        distanceDetect =
                            h.child("rand1").getValue(String::class.java).toString()
                        val setDistance = binding.txtViewDistance.text.toString().toInt()

                        val hashMapDetect = hashMapOf(
                            "detection" to "Intrusion is detected. ",
                            "distance" to distanceDetect,
                            "time" to strDate,
                        )

                        if (detectFeature) {
                            if (distanceDetect!! <= setDistance.toString()) {
                                if (toCheck) {
                                    return
                                } else {
                                    //Toast.makeText(context, "Intrusion is detected", Toast.LENGTH_SHORT).show()

                                    binding.txtViewDetect.text = distanceDetect + "CM"
                                    binding.txtDate.text = displayDate
                                    binding.txtTime.text = displayTime


                                    reference.child("relay1").setValue("1")
                                    reference.child("buzzer").setValue("1")
                                    reference.child("ledlgt").setValue("1")
                                    reference.child("camera").setValue("1")
                                    reference.child("lcdbkR").setValue("5")
                                    reference.child("lcdbkG").setValue("15")
                                    reference.child("lcdbkB").setValue("5")
                                    reference.child("lcdtxt").setValue("Intrusion detect")

                                    fStore.collection("intrusionDetect")
                                        .document(userID!!)
                                        .collection("allIntrusionDetect")
                                        .add(hashMapDetect)
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
                                    displayImg()
                                    toCheck = true
                                }
                            } else if (distanceDetect!! >= setDistance.toString()) {
                                //Toast.makeText(context, "No intrusion is detected", Toast.LENGTH_SHORT).show()
                                binding.txtViewDetect.text = ""
                                binding.txtDate.text = ""
                                binding.txtTime.text = ""
                                binding.imgSnap.setImageResource(R.drawable.cctv123)

                                reference.child("relay1").setValue("1")
                                reference.child("ledlgt").setValue("0")
                                reference.child("buzzer").setValue("0")
                                reference.child("camera").setValue("0")
                                reference.child("lcdbkR").setValue("15")
                                reference.child("lcdbkG").setValue("5")
                                reference.child("lcdbkB").setValue("5")
                                reference.child("lcdtxt").setValue("==No intrusion==")

                                toCheck = false
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


    private fun displayImg(){
        val listRef = storage.getReference("PI_12__CONTROL/")

        val imageList = ArrayList<String>()
        listRef.listAll()
            .addOnSuccessListener { listResult ->
                imageList.clear()
                for (allFile in listResult.items) {
                    allFile.downloadUrl.addOnSuccessListener {
                        imageList.add(it.toString())
                        Log.d("item", it.toString())
                    }
                }
                Toast.makeText(context, "Retrieving...", Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
                Log.d("item", it.toString())
            }

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            val lastPosition = imageList.size - 1
            //binding.txtViewTest.text = imageList[lastPosition]
            Log.d("URL", imageList[lastPosition])
            //Picasso.with(this.context).load(imageList[lastPosition]).into(binding.imgSnap)
            Glide.with(this /* context */)
                .load(imageList[lastPosition])
                .into(imgSnap)
        }, 15000)
    }

}
