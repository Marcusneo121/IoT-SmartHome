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
import my.edu.tarc.iotassignmentg11.AircondOnOffAdapter
import my.edu.tarc.iotassignmentg11.AircondOnOffArrayList
import my.edu.tarc.iotassignmentg11.databinding.FragmentAircondReportOnOffBinding

class AircondReportOnOff : Fragment() {

    private var _binding: FragmentAircondReportOnOffBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var onOffArrayList: ArrayList<AircondOnOffArrayList>
    private lateinit var onOffAdapter: AircondOnOffAdapter
    private lateinit var fStore: FirebaseFirestore
    private lateinit var fAuth: FirebaseAuth
    private var userID: String? = null
    private lateinit var viewStub: ViewStub


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAircondReportOnOffBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun EventChangeListener() {
        val secondApp: FirebaseApp = FirebaseApp.getInstance("IoTAssignment")
        fStore = FirebaseFirestore.getInstance(secondApp)
        fAuth = FirebaseAuth.getInstance(secondApp)
        userID = fAuth.currentUser?.uid

        fStore.collection("airconOnOff")
            .document(userID!!)
            .collection("allAirconOnOff")
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }

                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            onOffArrayList.add(dc.document.toObject(AircondOnOffArrayList::class.java))
                        }
                    }
                    onOffAdapter.notifyDataSetChanged()
                    if(onOffArrayList.isEmpty()){
                        viewStub.visibility = View.VISIBLE
                    } else {
                        viewStub.visibility = View.GONE
                    }
                }
            })
    }

//    private fun showViewStub() {
//        recyclerView.visibility = View.GONE
//        viewStub.visibility = View.VISIBLE
//    }
//
//    private fun hideViewStub() {
//        viewStub.visibility = View.GONE
//        recyclerView.visibility = View.VISIBLE
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val secondApp: FirebaseApp = FirebaseApp.getInstance("IoTAssignment")
        fStore = FirebaseFirestore.getInstance(secondApp)
        fAuth = FirebaseAuth.getInstance(secondApp)
        userID = fAuth.currentUser?.uid
        viewStub = binding.viewStub

        recyclerView = binding.recyclerViewOnOff
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.setHasFixedSize(true)

        onOffArrayList = arrayListOf()
        onOffAdapter = AircondOnOffAdapter(onOffArrayList)

        recyclerView.adapter = onOffAdapter

        EventChangeListener()
    }
}