package ke.co.appslab.smarthome.ui.doorbell

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import ke.co.appslab.smarthome.models.DoorbellEntry
import kotlinx.android.synthetic.main.item_camera_feed_details.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class DoorbellAdapter(
    private val doorEntryList: List<DoorbellEntry>,
    private val itemClickListener: (DoorbellEntry) -> Unit
) : RecyclerView.Adapter<DoorbellAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View, itemClickListener: (DoorbellEntry) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val doorbellImg = itemView.doorbellImg
        private val timestampText = itemView.timestampText

        fun bindDoorBell(doorbellEntry: DoorbellEntry) {
            with(doorbellEntry) {
                timestamp?.let {
                    val timeDifference =
                        DateUtils.getRelativeTimeSpanString(it, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS)
                    timestampText.text = timeDifference
                }
                Glide.with(itemView.context).load(image).into(doorbellImg)
                itemView.setOnClickListener {
                    itemClickListener(this)
                }

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(ke.co.appslab.smarthome.R.layout.item_camera_feed_details, parent, false)
        return MyViewHolder(itemView, itemClickListener)
    }

    override fun getItemCount(): Int = doorEntryList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindDoorBell(doorEntryList[position])
    }

}