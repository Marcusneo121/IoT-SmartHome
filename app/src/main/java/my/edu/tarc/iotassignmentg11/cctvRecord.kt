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
import my.edu.tarc.iotassignmentg11.databinding.FragmentCameraRecordBinding

class cctvRecord: Fragment() {

    private var _binding: FragmentCameraRecordBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var cctvRecordList: ArrayList<CCTVReportArrayList>
    private lateinit var cctvAdapter: CCTVReportAdapter
    private lateinit var fStore: FirebaseFirestore
    private lateinit var fAuth: FirebaseAuth
    private var userID: String? = null
    private lateinit var viewStub: ViewStub
    //private var imageList = java.util.ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let{

        }
    }

    private fun EventChangeListener(){
        val secondApp: FirebaseApp = FirebaseApp.getInstance("IoTAssignment")
        fStore = FirebaseFirestore.getInstance(secondApp)
        fAuth = FirebaseAuth.getInstance(secondApp)
        userID = fAuth.currentUser?.uid

        fStore.collection("CCTVRecord")
            .document(userID!!)
            .collection("CCTVRecord")
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if(error != null){
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }

                    for(dc: DocumentChange in value?.documentChanges!!){
                        if(dc.type == DocumentChange.Type.ADDED){
                            cctvRecordList.add(dc.document.toObject(CCTVReportArrayList::class.java))
                        }
                    }
                    cctvAdapter.notifyDataSetChanged()
                    if(cctvRecordList.isEmpty()){
                        viewStub.visibility = View.VISIBLE
                    } else {
                        viewStub.visibility = View.GONE
                    }
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCameraRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val secondApp: FirebaseApp = FirebaseApp.getInstance("IoTAssignment")
        fStore = FirebaseFirestore.getInstance(secondApp)
        fAuth = FirebaseAuth.getInstance(secondApp)
        userID = fAuth.currentUser?.uid
        viewStub = binding.viewStub2

        recyclerView = binding.recyclerViewCCTVRecord
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.setHasFixedSize(true)

        cctvRecordList = arrayListOf()
        cctvAdapter = CCTVReportAdapter(this.context, cctvRecordList)

        recyclerView.adapter = cctvAdapter

        EventChangeListener()


    }
}