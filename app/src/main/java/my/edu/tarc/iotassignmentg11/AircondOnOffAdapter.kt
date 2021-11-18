package my.edu.tarc.iotassignmentg11

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AircondOnOffAdapter(private val onOffArrayListList : ArrayList<AircondOnOffArrayList>) : RecyclerView.Adapter<AircondOnOffAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AircondOnOffAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_aircond_onoff, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AircondOnOffAdapter.ViewHolder, position: Int) {
        val onOffArrayListItem: AircondOnOffArrayList = onOffArrayListList[position]
        holder.airconStatus.text = onOffArrayListItem.airconStatus
        holder.temp.text = onOffArrayListItem.temp.toString()
        holder.time.text = onOffArrayListItem.time
    }

    override fun getItemCount(): Int {
        return onOffArrayListList.size
    }

    public class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        val time : TextView = itemView.findViewById(R.id.date)
        val airconStatus : TextView = itemView.findViewById(R.id.status)
        val temp : TextView = itemView.findViewById(R.id.temperature)

    }

}