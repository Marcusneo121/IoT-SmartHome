package my.edu.tarc.iotassignmentg11


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class IntrusionDoorStatusAdapter(private val doorStatusArrayListList : ArrayList<IntrusionDoorStatusArrayList>) : RecyclerView.Adapter<IntrusionDoorStatusAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntrusionDoorStatusAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_intrusion_doorstatus, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: IntrusionDoorStatusAdapter.ViewHolder, position: Int) {
        val doorStatusArrayListItem: IntrusionDoorStatusArrayList = doorStatusArrayListList[position]
        holder.doorStatus.text = doorStatusArrayListItem.doorStatus
        holder.time.text = doorStatusArrayListItem.time
    }

    override fun getItemCount(): Int {
        return doorStatusArrayListList.size
    }

    public class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        val time : TextView = itemView.findViewById(R.id.displayDateTime)
        val doorStatus : TextView = itemView.findViewById(R.id.displayDoorStatus)
    }

}