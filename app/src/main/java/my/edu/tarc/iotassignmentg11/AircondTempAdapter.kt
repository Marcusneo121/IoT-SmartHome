package my.edu.tarc.iotassignmentg11

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AircondTempAdapter(private val tempChangeList : ArrayList<AircondTempArrayList>) : RecyclerView.Adapter<AircondTempAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AircondTempAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_aircond_temp, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AircondTempAdapter.ViewHolder, position: Int) {
        val tempArrayListItem: AircondTempArrayList = tempChangeList[position]
        holder.temp.text = tempArrayListItem.temp.toString()
        holder.time.text = tempArrayListItem.time
    }

    override fun getItemCount(): Int {
        return tempChangeList.size
    }

    public class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        val time : TextView = itemView.findViewById(R.id.dateRecord)
        val temp : TextView = itemView.findViewById(R.id.cctvImg)

    }
}