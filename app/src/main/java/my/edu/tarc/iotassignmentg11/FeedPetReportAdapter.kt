package my.edu.tarc.iotassignmentg11

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FeedPetReportAdapter(private val feedRecordListList : ArrayList<FeedPetReportArrayList>) : RecyclerView.Adapter<FeedPetReportAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedPetReportAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_feedrecord, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FeedPetReportAdapter.ViewHolder, position: Int) {
        val feedPetArrayListItem: FeedPetReportArrayList = feedRecordListList[position]
        holder.details.text = feedPetArrayListItem.details
        holder.time.text = feedPetArrayListItem.time
    }

    override fun getItemCount(): Int {
        return feedRecordListList.size
    }

    public class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        val time : TextView = itemView.findViewById(R.id.dateRecord)
        val details : TextView = itemView.findViewById(R.id.detailsFeed)


    }

}