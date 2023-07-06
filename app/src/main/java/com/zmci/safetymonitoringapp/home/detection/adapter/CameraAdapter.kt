package com.zmci.safetymonitoringapp.home.detection.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.zmci.safetymonitoringapp.R
import com.zmci.safetymonitoringapp.home.HomeFragment
import com.zmci.safetymonitoringapp.home.detection.model.CameraData
import com.zmci.safetymonitoringapp.home.detection.utils.CAMERA_NAME_KEY

class CameraAdapter(val c: Context, val cameraList:MutableList<CameraData>): RecyclerView.Adapter<CameraAdapter.CameraViewHolder>() {
    inner class CameraViewHolder(val v: View, listener: onItemClickListener):RecyclerView.ViewHolder(v){
        var name: TextView = v.findViewById(R.id.mTitle)
        var mTopic: TextView = v.findViewById(R.id.mTopic)
        var mMenus: ImageView = v.findViewById(R.id.mMenus)

        init {
            mMenus.setOnClickListener{ popupMenus(it) }
            v.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }

        private fun popupMenus(v: View) {
            val position = cameraList[adapterPosition]
            val popupMenus = PopupMenu(c,v)
            popupMenus.inflate(R.menu.show_menu)
            popupMenus.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.viewLogs->{
                        val newList = cameraList[adapterPosition]
                        position.cameraName = newList.cameraName
                        position.MQTT_TOPIC = newList.MQTT_TOPIC
                        val cameraName = newList.cameraName
                        val cameraNameBundle = bundleOf(
                            CAMERA_NAME_KEY to cameraName
                        )
                        v.findNavController().navigate(R.id.action_navigation_home_to_fragment_camera_logs, cameraNameBundle)
                        true
                    }
                    R.id.edit->{
                        val v = LayoutInflater.from(c).inflate(R.layout.fragment_update_camera,null)

                        val newList = cameraList[adapterPosition]
                        position.cameraName = newList.cameraName
                        position.MQTT_TOPIC = newList.MQTT_TOPIC
                        val updateDeviceName : EditText = v.findViewById(R.id.updateDeviceName)
                        val updateUniqueID : EditText = v.findViewById(R.id.updateUniqueID)
                        updateDeviceName.hint = newList.cameraName
                        updateUniqueID.hint = newList.MQTT_TOPIC


                        AlertDialog.Builder(c)
                            .setView(v)
                            .setPositiveButton("Update"){
                                    dialog,_->
                                if (updateDeviceName.text.toString().isEmpty() || updateUniqueID.text.toString().isEmpty()) {
                                    Toast.makeText(c, "Fill out empty fields", Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    val isUpdate = HomeFragment.databaseHelper.updateCamera(
                                        newList.id.toString(),
                                        updateDeviceName.text.toString(),
                                        updateUniqueID.text.toString()
                                    )
                                    if (isUpdate) {
                                        cameraList[adapterPosition].cameraName =
                                            updateDeviceName.text.toString()
                                        cameraList[adapterPosition].MQTT_TOPIC =
                                            updateUniqueID.text.toString()
                                        notifyDataSetChanged()
                                        Toast.makeText(
                                            c,
                                            "Updated Successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(c, "Error Updating", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }
                                dialog.dismiss()
                            }
                            .setNegativeButton("Cancel"){
                                    dialog,_->
                                dialog.dismiss()
                            }
                            .create()
                            .show()
                        true
                    }
                    R.id.delete->{
                        val newList = cameraList[adapterPosition]
                        position.cameraName = newList.cameraName
                        position.MQTT_TOPIC = newList.MQTT_TOPIC
                        val cameraName = newList.cameraName

                        AlertDialog.Builder(c)
                            .setTitle("Warning")
                            .setMessage("Are you sure you want to delete : $cameraName ?")
                            .setPositiveButton("Yes", DialogInterface.OnClickListener{ dialog, which ->
                                if (HomeFragment.databaseHelper.deleteCamera(newList.id)) {
                                    cameraList.removeAt(adapterPosition)
                                    notifyItemRemoved(adapterPosition)
                                    notifyItemRangeChanged(adapterPosition,cameraList.size)
                                    Toast.makeText(c, "Device $cameraName Deleted", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(c, "Error Deleting", Toast.LENGTH_SHORT).show()
                                }
                            })
                            .setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->  })
                            .setIcon(R.drawable.ic_warning)
                            .show()
                        true
                    }
                    else-> true
                }
            }
            popupMenus.show()
            val popup = PopupMenu::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val menu = popup.get(popupMenus)
            menu.javaClass.getDeclaredMethod("setForceShowIcon",Boolean::class.java)
                .invoke(menu,true)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CameraViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.fragment_list_camera,parent,false)
        return CameraViewHolder(v,mListener)
    }

    override fun onBindViewHolder(holder: CameraViewHolder, position: Int) {
        val newList = cameraList[position]
        holder.name.text = newList.cameraName
        holder.mTopic.text = newList.MQTT_TOPIC
    }

    override fun getItemCount(): Int {
        return cameraList.size
    }

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

}