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
import my.edu.tarc.iotassignmentg11.IntrusionDoorStatusAdapter
import my.edu.tarc.iotassignmentg11.IntrusionDoorStatusArrayList
import my.edu.tarc.iotassignmentg11.databinding.FragmentIntrusionReportDoorStatusBinding

class IntrusionReportDoorStatus : Fragment() {

    private var _binding: FragmentIntrusionReportDoorStatusBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var doorStatusArrayList: ArrayList<IntrusionDoorStatusArrayList>
    private lateinit var doorStatusAdapter: IntrusionDoorStatusAdapter
    private lateinit var fStore: FirebaseFirestore
    private lateinit var fAuth: FirebaseAuth
    private var userID: String? = null
    private lateinit var viewStubDoor: ViewStub



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    private fun EventChangeListener() {
        val secondApp: FirebaseApp = FirebaseApp.getInstance("IoTAssignment")
        fStore = FirebaseFirestore.getInstance(secondApp)
        fAuth = FirebaseAuth.getInstance(secondApp)
        userID = fAuth.currentUser?.uid

        fStore.collection("doorDetect")
            .document(userID!!)
            .collection("allDoorDetect")
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }

                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            doorStatusArrayList.add(dc.document.toObject(IntrusionDoorStatusArrayList::class.java))
                        }
                    }
                    doorStatusAdapter.notifyDataSetChanged()
                    if(doorStatusArrayList.isEmpty()){
                        viewStubDoor.visibility = View.VISIBLE
                    } else {
                        viewStubDoor.visibility = View.GONE
                    }
                }
            })
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentIntrusionReportDoorStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val secondApp: FirebaseApp = FirebaseApp.getInstance("IoTAssignment")
        fStore = FirebaseFirestore.getInstance(secondApp)
        fAuth = FirebaseAuth.getInstance(secondApp)
        userID = fAuth.currentUser?.uid
        viewStubDoor = binding.viewStubDoor

        recyclerView = binding.recyclerViewDoorStatus
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.setHasFixedSize(true)

        doorStatusArrayList = arrayListOf()
        doorStatusAdapter = IntrusionDoorStatusAdapter(doorStatusArrayList)

        recyclerView.adapter = doorStatusAdapter

        EventChangeListener()
    }
}