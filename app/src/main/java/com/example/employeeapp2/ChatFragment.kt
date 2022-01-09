package com.example.employeeapp2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_employee_detail.*
import kotlinx.android.synthetic.main.message_item.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

const val MESSSAGE_BASE_PATH = "messages"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: FirebaseDatabase
    private lateinit var adapter: FirebaseRecyclerAdapter<UserMessage, ViewHolder?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolbar_chat
            .setupWithNavController(navController, appBarConfiguration)

        send_message.setOnClickListener {
            val messageText = message_text.text.toString()
            if(messageText.isBlank()) {
                Toast.makeText(requireContext(), "Please enter a message to send"
                    , Toast.LENGTH_SHORT).show()
            } else {
                val ref = dbRef.getReference(MESSSAGE_BASE_PATH).push()
                val userMessage =  UserMessage(auth.currentUser?.email ?: "Unknown", messageText)
                ref.setValue(userMessage).addOnSuccessListener {
                        message_text.setText("")
                        Log.d(TAG, "hello")
                    }
                    .addOnFailureListener { ex: Exception ->
                        Toast.makeText(requireContext(),
                            "Failed to send message ${ex.toString()}",
                            Toast.LENGTH_SHORT).show()

                    }
            }
        }
        messages.layoutManager = LinearLayoutManager(requireActivity())
        setUpMessageList()
    }

    private fun setUpMessageList() {
        val query: Query = dbRef.reference.child(MESSSAGE_BASE_PATH)

        val options: FirebaseRecyclerOptions<UserMessage> =
            FirebaseRecyclerOptions.Builder<UserMessage>()
                .setQuery(query) {
                    UserMessage(it.child("email").value.toString(), it.child("message").value.toString())
                }
                .build()

        adapter = object: FirebaseRecyclerAdapter<UserMessage, ViewHolder?>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val view: View = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
                return ViewHolder(view)
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int, model: UserMessage) {
                holder.bind(model)
            }
        }
        messages.adapter = adapter
    }

    class ViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(userMessage: UserMessage) {
            with(userMessage) {
                user_email.text = email
                user_message.text = message
            }
        }
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChatFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}