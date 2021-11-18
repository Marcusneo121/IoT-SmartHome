package my.edu.tarc.iotassignmentg11

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import my.edu.tarc.iotassignmentg11.databinding.FragmentAutoPoopReportBinding


class AutoPoopReport: Fragment() {
    private var _binding: FragmentAutoPoopReportBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var autoPoopReportList: ArrayList<AutoPoopReportArrayList>
    private lateinit var autoPoopReportAdapter: AutoPoopReportAdapter
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

        fStore.collection("autoPoopRecord")
            .document(userID!!)
            .collection("allAutoPoopRecord")
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }

                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            autoPoopReportList.add(dc.document.toObject(AutoPoopReportArrayList::class.java))
                        }
                    }
                    autoPoopReportAdapter.notifyDataSetChanged()
                    if(autoPoopReportList.isEmpty()){
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
        _binding = FragmentAutoPoopReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val secondApp: FirebaseApp = FirebaseApp.getInstance("IoTAssignment")
        fStore = FirebaseFirestore.getInstance(secondApp)
        fAuth = FirebaseAuth.getInstance(secondApp)
        userID = fAuth.currentUser?.uid
        viewStub = binding.viewStub3

        recyclerView = binding.rvAutoPoopReport
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.setHasFixedSize(true)

        autoPoopReportList = arrayListOf()
        autoPoopReportAdapter = AutoPoopReportAdapter(autoPoopReportList)

        recyclerView.adapter = autoPoopReportAdapter

        EventChangeListener()
    }
}