package my.edu.tarc.iotassignmentg11

import my.edu.tarc.iotassignmentg11.databinding.FragmentFeedRecordBinding


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
import com.google.firebase.firestore.*
import my.edu.tarc.iotassignmentg11.AircondOnOffAdapter
import my.edu.tarc.iotassignmentg11.AircondOnOffArrayList
import my.edu.tarc.iotassignmentg11.databinding.FragmentAircondReportOnOffBinding

class feedRecord : Fragment() {

    private var _binding: FragmentFeedRecordBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var feedRecordList: ArrayList<FeedPetReportArrayList>
    private lateinit var onOffAdapter: FeedPetReportAdapter
    private lateinit var fStore: FirebaseFirestore
    private lateinit var fAuth: FirebaseAuth
    private var userID: String? = null
    private lateinit var viewStub: ViewStub

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private fun EventChangeListener() {
        val secondApp: FirebaseApp = FirebaseApp.getInstance("IoTAssignment")
        fStore = FirebaseFirestore.getInstance(secondApp)
        fAuth = FirebaseAuth.getInstance(secondApp)
        userID = fAuth.currentUser?.uid

        fStore.collection("feedRecord")
            .document(userID!!)
            .collection("allFeedRecord")
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }

                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            feedRecordList.add(dc.document.toObject(FeedPetReportArrayList::class.java))
                        }
                    }
                    onOffAdapter.notifyDataSetChanged()
                    if(feedRecordList.isEmpty()){
                        viewStub.visibility = View.VISIBLE
                    } else {
                        viewStub.visibility = View.GONE
                    }
                }
            })
    }

    private fun showViewStub() {
        recyclerView.visibility = View.GONE
        viewStub.visibility = View.VISIBLE
    }

    private fun hideViewStub() {
        viewStub.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFeedRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val secondApp: FirebaseApp = FirebaseApp.getInstance("IoTAssignment")
        fStore = FirebaseFirestore.getInstance(secondApp)
        fAuth = FirebaseAuth.getInstance(secondApp)
        userID = fAuth.currentUser?.uid
        viewStub = binding.viewStub2

        recyclerView = binding.recyclerViewFeedRecord
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.setHasFixedSize(true)

        feedRecordList = arrayListOf()
        onOffAdapter = FeedPetReportAdapter(feedRecordList)

        recyclerView.adapter = onOffAdapter

        EventChangeListener()
    }
}