package my.edu.tarc.iotassignmentg11

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class IntrusionAlarmStatusAdapter(private val alarmStatusArrayListList : ArrayList<IntrusionAlarmStatusArrayList>) : RecyclerView.Adapter<IntrusionAlarmStatusAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntrusionAlarmStatusAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_intrusion_alarmstatus, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: IntrusionAlarmStatusAdapter.ViewHolder, position: Int) {
        val alarmStatusArrayListItem: IntrusionAlarmStatusArrayList = alarmStatusArrayListList[position]
        holder.alarmStatus.text = alarmStatusArrayListItem.alarmStatus
        holder.time.text = alarmStatusArrayListItem.time
    }

    override fun getItemCount(): Int {
        return alarmStatusArrayListList.size
    }

    public class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        val time : TextView = itemView.findViewById(R.id.displayDate)
        val alarmStatus : TextView = itemView.findViewById(R.id.displayStatus)
    }

}