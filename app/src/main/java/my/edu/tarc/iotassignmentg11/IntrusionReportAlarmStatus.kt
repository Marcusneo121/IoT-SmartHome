package my.edu.tarc.iotassignmentg11
import android.os.Bundle
import android.text.TextUtils.isEmpty
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.util.CollectionUtils.isEmpty
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.*
import my.edu.tarc.iotassignmentg11.IntrusionAlarmStatusAdapter
import my.edu.tarc.iotassignmentg11.IntrusionAlarmStatusArrayList
import my.edu.tarc.iotassignmentg11.databinding.FragmentIntrusionReportAlarmStatusBinding

class IntrusionReportAlarmStatus : Fragment() {

    private var _binding: FragmentIntrusionReportAlarmStatusBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var alarmStatusArrayList: ArrayList<IntrusionAlarmStatusArrayList>
    private lateinit var alarmStatusAdapter: IntrusionAlarmStatusAdapter
    private lateinit var fStore: FirebaseFirestore
    private lateinit var fAuth: FirebaseAuth
    private var userID: String? = null
    private lateinit var viewStubAlarm: ViewStub



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    private fun EventChangeListener() {
        val secondApp: FirebaseApp = FirebaseApp.getInstance("IoTAssignment")
        fStore = FirebaseFirestore.getInstance(secondApp)
        fAuth = FirebaseAuth.getInstance(secondApp)
        userID = fAuth.currentUser?.uid

        fStore.collection("alarmOnOff")
            .document(userID!!)
            .collection("allAlarmOnOff")
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }

                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            alarmStatusArrayList.add(dc.document.toObject(IntrusionAlarmStatusArrayList::class.java))
                        }
                    }
                    alarmStatusAdapter.notifyDataSetChanged()
                    if(alarmStatusArrayList.isEmpty()){
                        viewStubAlarm.visibility = View.VISIBLE
                    } else {
                        viewStubAlarm.visibility = View.GONE
                    }
                }
            })
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentIntrusionReportAlarmStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val secondApp: FirebaseApp = FirebaseApp.getInstance("IoTAssignment")
        fStore = FirebaseFirestore.getInstance(secondApp)
        fAuth = FirebaseAuth.getInstance(secondApp)
        userID = fAuth.currentUser?.uid
        viewStubAlarm = binding.viewStubAlarm

        recyclerView = binding.recyclerViewAlarmStatus
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.setHasFixedSize(true)

        alarmStatusArrayList = arrayListOf()
        alarmStatusAdapter = IntrusionAlarmStatusAdapter(alarmStatusArrayList)

        recyclerView.adapter = alarmStatusAdapter

        EventChangeListener()
    }
}