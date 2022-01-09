package com.example.employeeapp2

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item.*

class EmployeeAdapter(private val listener: (Boolean, Long) -> Unit):

    ListAdapter<Employee, EmployeeAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        init {
            itemView.setOnClickListener {
                listener.invoke(true, getItem(adapterPosition).id)
                //itemView.background = ContextCompat.getDrawable(it.context, R.color.black)
            }
            edit_employee.setOnClickListener {
                listener.invoke(false, getItem(adapterPosition).id)
            }
        }

        fun bind(employee: Employee) {
            employee_name.text = employee.name
            employee_role.text = employee.role
            employee_age.text = employee.age.toString() + " years"
            employee_gender.text = Gender.values()[employee.gender].name
            with(employee.photo){
                if(isNotEmpty()){
                    employee_photo.setImageURI(Uri.parse(this))
                    employee_photo.tag = this

                } else{
                    employee_photo.setImageResource(R.drawable.user)
                    employee_photo.tag = ""
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeAdapter.ViewHolder {
        val itemLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)

        return ViewHolder(itemLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class DiffCallback: DiffUtil.ItemCallback<Employee>() {
    override fun areItemsTheSame(oldItem: Employee, newItem: Employee): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Employee, newItem: Employee): Boolean {
        return oldItem == newItem
    }
}