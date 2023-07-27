package hu.madak1.my8puzzle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hu.madak1.my8puzzle.data.Score

class AdapterRV(
    private val scoreList: List<Score>
): RecyclerView.Adapter<AdapterRV.MyViewHolder>() {

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val posTV: TextView = itemView.findViewById(R.id.row_position_tv)
        val nameTV: TextView = itemView.findViewById(R.id.row_name_tv)
        val scoreTV: TextView = itemView.findViewById(R.id.row_score_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.score_row, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return this.scoreList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, idx: Int) {
        val curScore = this.scoreList[idx]
        val position = if (idx < 9) "0${idx+1}" else "${idx+1}"
        val score = "${curScore.score} pts"
        holder.posTV.text = position
        holder.nameTV.text = curScore.username
        holder.scoreTV.text = score
    }
}