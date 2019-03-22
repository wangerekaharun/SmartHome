package ke.co.appslab.smarthome.ui.motion

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.storage.FirebaseStorage
import ke.co.appslab.smarthome.models.MotionImageLog
import kotlinx.android.synthetic.main.item_camera_feed_details.view.*

class MotionAdapter(
    private val motionLogList: List<MotionImageLog>,
    private val itemClickListener: (MotionImageLog) -> Unit
) : RecyclerView.Adapter<MotionAdapter.MotionLogViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MotionLogViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(ke.co.appslab.smarthome.R.layout.item_camera_feed_details, parent, false)
        return MotionLogViewHolder(itemView, itemClickListener)
    }

    override fun getItemCount(): Int = motionLogList.size

    override fun onBindViewHolder(holder: MotionLogViewHolder, position: Int) {
        holder.bindMotionLog(motionLogList[position])
    }

    class MotionLogViewHolder(itemView: View, itemClickListener: (MotionImageLog) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val doorbellImg = itemView.doorbellImg
        private val timestampText = itemView.timestampText
        private val activityLabelText = itemView.activityLabelText
        private val answerImg = itemView.answerImg

        fun bindMotionLog(motionImageLog: MotionImageLog) {
            with(motionImageLog) {
                timestamp?.let {
                    val timeDifference =
                        DateUtils.getRelativeTimeSpanString(it, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS)
                    timestampText.text = timeDifference
                }
                activityLabelText.text = activityLabel
                imageRef?.let {
                    Glide.with(itemView.context).load(it).into(doorbellImg)
                    answerImg.visibility = View.GONE

                }

            }
        }

    }

}