package my.edu.tarc.iotassignmentg11

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AutoPoopReportAdapter(private val autoPoopRecordListList : ArrayList<AutoPoopReportArrayList>) : RecyclerView.Adapter<AutoPoopReportAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AutoPoopReportAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_autopooprecord, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AutoPoopReportAdapter.ViewHolder, position: Int) {
        val autoPoopReportArrayListItem: AutoPoopReportArrayList = autoPoopRecordListList[position]
        holder.details.text = autoPoopReportArrayListItem.details
        holder.time.text = autoPoopReportArrayListItem.time
    }

    override fun getItemCount(): Int {
        return autoPoopRecordListList.size
    }

    public class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        val time : TextView = itemView.findViewById(R.id.dateRecord)
        val details : TextView = itemView.findViewById(R.id.detailsAutoPoop)


    }

}