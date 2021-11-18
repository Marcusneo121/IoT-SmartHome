package my.edu.tarc.iotassignmentg11

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import my.edu.tarc.iotassignmentg11.AircondOnOffAdapter
import my.edu.tarc.iotassignmentg11.AircondOnOffArrayList
import my.edu.tarc.iotassignmentg11.AircondTempAdapter
import my.edu.tarc.iotassignmentg11.AircondTempArrayList
import my.edu.tarc.iotassignmentg11.databinding.FragmentAircondReportTempBinding

class AircondReportTemp : Fragment() {

    private var _binding: FragmentAircondReportTempBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var tempArrayList: ArrayList<AircondTempArrayList>
    private lateinit var tempAdapter: AircondTempAdapter
    private lateinit var fStore: FirebaseFirestore
    private lateinit var fAuth: FirebaseAuth
    private var userID: String? = null
    private lateinit var viewStub: ViewStub

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
        _binding = FragmentAircondReportTempBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun EventChangeListener(){
        val secondApp: FirebaseApp = FirebaseApp.getInstance("IoTAssignment")
        fStore = FirebaseFirestore.getInstance(secondApp)
        fAuth = FirebaseAuth.getInstance(secondApp)
        userID = fAuth.currentUser?.uid

        fStore.collection("airconTempChange")
            .document(userID!!)
            .collection("allTempChange")
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if(error != null){
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }

                    for(dc: DocumentChange in value?.documentChanges!!){
                        if(dc.type == DocumentChange.Type.ADDED){
                            tempArrayList.add(dc.document.toObject(AircondTempArrayList::class.java))
                        }
                    }
                    tempAdapter.notifyDataSetChanged()
                    if(tempArrayList.isEmpty()){
                        viewStub.visibility = View.VISIBLE
                    } else {
                        viewStub.visibility = View.GONE
                    }
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val secondApp: FirebaseApp = FirebaseApp.getInstance("IoTAssignment")
        fStore = FirebaseFirestore.getInstance(secondApp)
        fAuth = FirebaseAuth.getInstance(secondApp)
        userID = fAuth.currentUser?.uid
        viewStub = binding.viewStub

        recyclerView = binding.recyclerViewTemp
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.setHasFixedSize(true)

        tempArrayList = arrayListOf()
        tempAdapter = AircondTempAdapter(tempArrayList)

        recyclerView.adapter = tempAdapter

        EventChangeListener()

    }
}