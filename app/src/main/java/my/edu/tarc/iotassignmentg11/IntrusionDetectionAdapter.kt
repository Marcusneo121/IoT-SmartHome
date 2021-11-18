package my.edu.tarc.iotassignmentg11

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class IntrusionDetectionAdapter(private val detectionArrayListList : ArrayList<IntrusionDetectionArrayList>) : RecyclerView.Adapter<IntrusionDetectionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntrusionDetectionAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_intrusion_detection, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: IntrusionDetectionAdapter.ViewHolder, position: Int) {
        val detectionArrayListItem: IntrusionDetectionArrayList = detectionArrayListList[position]
        holder.detection.text = detectionArrayListItem.detection
        holder.distance.text = detectionArrayListItem.distance
        holder.time.text = detectionArrayListItem.time
    }

    override fun getItemCount(): Int {
        return detectionArrayListList.size
    }

    public class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        val time : TextView = itemView.findViewById(R.id.displayTime)
        val detection : TextView = itemView.findViewById(R.id.detection)
        val distance : TextView = itemView.findViewById(R.id.distance)

    }


}