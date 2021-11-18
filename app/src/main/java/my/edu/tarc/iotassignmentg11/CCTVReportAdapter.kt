package my.edu.tarc.iotassignmentg11

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import my.edu.tarc.iotassignmentg11.databinding.FragmentCameraBinding


class CCTVReportAdapter(private val context: Context?, private val cctvRecordList: ArrayList<CCTVReportArrayList>) :
    RecyclerView.Adapter<CCTVReportAdapter.ViewHolder>() {
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private lateinit var reference2: DatabaseReference
    private lateinit var fStore: FirebaseFirestore

    //private var listref: StorageReference = storage.getReference().child("PI_12__CONTROL/")


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CCTVReportAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_cctvrecord, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CCTVReportAdapter.ViewHolder, position: Int) {
        val cctvArrayListItem: CCTVReportArrayList = cctvRecordList[position]
        holder.time.text = cctvArrayListItem.time// wait me open discord sia okok
        val url: Uri? = Uri.parse(cctvArrayListItem.imgURL)
        //holder.imgURL.setImageURI(url)

        this.context?.let { Glide.with(it).load(url).into(holder.imgURL) }

        /*val listRef = fStore.getReference("CCTVRecord")


        listRef.listAll()
            .addOnSuccessListener { listResult ->
                for(allFile in listResult.items){
                    allFile.downloadUrl.addOnSuccessListener{
                        imageList.add(it.toString())
                        Log.d("item", it.toString())
                        //add this list of url to Firebase
                    }
                }
                Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Log.d("item", it.toString())
            }*/
    }


    override fun getItemCount(): Int {
        return cctvRecordList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val time: TextView = itemView.findViewById(R.id.dateRecord)
        val imgURL: ImageView = itemView.findViewById(R.id.cctvImg)

    }
}


