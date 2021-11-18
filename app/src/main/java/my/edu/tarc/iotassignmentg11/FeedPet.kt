package my.edu.tarc.iotassignmentg11

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import my.edu.tarc.iotassignmentg11.databinding.FragmentFeedPetBinding
import java.text.SimpleDateFormat
import java.util.*


class FeedPet : Fragment() {

    private var _binding: FragmentFeedPetBinding? = null
    private lateinit var database: DatabaseReference
    private val binding get() = _binding!!
    private lateinit var fStore: FirebaseFirestore
    private lateinit var fAuth: FirebaseAuth
    var feedRecord = ArrayDeque<String>()
    private var userID: String? = null
    private var timerBoolean:Boolean?= true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedPetBinding.inflate(inflater, container, false)
        _binding!!.editTextInterval.isEnabled = false
        _binding!!.btnTimePicker.isEnabled = false


        _binding!!.btnTimePicker.setOnClickListener {
            val builder= AlertDialog.Builder(this.context)
            builder.setTitle("Warning")
            builder.setMessage("Please select the time in advance, or it will immediately execute the action and continue normally following the feed interval.")
            builder.setPositiveButton("Ok"){dialogInterface, which ->
                val cal = Calendar.getInstance()
                val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, minute)
                    cal.set(Calendar.SECOND, 0)
                    val currentTime = cal.time
                    updateRecord("Time selected: " + SimpleDateFormat("HH:mm").format(currentTime) )
                    updateRecord("Feed Inverval: " + _binding!!.editTextInterval.text.toString() +"hour" )
                    schedule(currentTime)
                }
                TimePickerDialog(this.context, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()

            }
            val alertDialog:AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()



        }

        _binding!!.switchAutoFeed.setOnCheckedChangeListener{ buttonView, isChecked ->
            if (isChecked){
                _binding!!.editTextInterval.isEnabled= true
                _binding!!.btnTimePicker.isEnabled = true
                updateRecord("Auto Feed ON")
                Log.d("TAG","Auto Feed Checked")
                Log.d("TAG", feedRecord.toString())
                timerBoolean=true


            }else{
                _binding!!.editTextInterval.isEnabled = false
                _binding!!.btnTimePicker.isEnabled = false
                updateRecord("Auto Feed OFF")
                timerBoolean=false

            }
        }



        return binding.root
    }



    private fun updateRecord(s: String) {
        val timeNow = Calendar.getInstance().time
        val formattedTime = SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(timeNow)

        feedRecord.push(formattedTime + "  " + s + "\n"  )
        val formattedString: String = feedRecord.toString()
            .replace(",", "") //remove the commas
            .replace("[", " ") //remove the right bracket
            .replace("]", "") //remove the left bracket
            .trim() //remove trailing spaces from partially initialized arrays

        _binding!!.textViewRecord.text= formattedString

        val hashMapFeedRecord = hashMapOf(
            "details" to s,
            "time" to formattedTime,
            //"userID" to userID
        )

        fStore.collection("feedRecord")
            .document(userID!!)
            .collection("allFeedRecord")
            .add(hashMapFeedRecord)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "Added Document with ID ${it.id}")
            }
            .addOnFailureListener {
                Log.w(ContentValues.TAG, "Error adding document ${it.suppressedExceptions}")
            }

    }


    private fun schedule(currentTime: Date) {
        val timer = Timer()
        val feedInterval = _binding!!.editTextInterval.text.toString().toLong()

        val task: TimerTask = object : TimerTask() {
            override fun run(){
                if(timerBoolean == true) {
                    updateData()
                    Log.d("TAG", "UPDATED")
                }else{
                    timer.cancel()
                }
            }
        }

        timer.schedule(task,currentTime,1000*60*feedInterval)
    }

    private fun updateData() {
        database = FirebaseDatabase.getInstance().getReference()

        database.child("PI_12__CONTROL").child("buzzer").setValue("1").addOnSuccessListener {
            updateRecord("PET FEED SUCCESSFULLY")
            Log.d("UPDATE","SUCCESS!")
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    database.child("PI_12__CONTROL").child("buzzer").setValue("0")
                }
            }, 1000 * 5)
        }.addOnFailureListener{
            Log.d("UPDATE","FAIL")
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